package io.group32.service;

import io.group32.dto.request.auth.*;
import io.group32.model.TokenType;
import io.group32.model.VerificationToken;
import io.group32.model.User;
import io.group32.repository.VerificationTokenRepository;
import io.group32.repository.UserRepository;
import io.group32.service.result.AuthResult;
import io.group32.validation.AuthValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import io.group32.dto.response.auth.MeResponse;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final SessionService sessionService;
    private final AuthValidator authValidator;

    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository,
                       EmailService emailService, VerificationTokenRepository verificationTokenRepository,
                       SessionService sessionService, AuthValidator authValidator) {

        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
        this.sessionService = sessionService;
        this.authValidator = authValidator;
    }

    @Transactional
    public AuthResult<Boolean> handleRegister(RegisterRequest request) {
        try {
            authValidator.validateUsername(request.getUsername());
            authValidator.validateEmail(request.getEmail());
            authValidator.validatePassword(request.getPassword());
        } catch (IllegalArgumentException exception) {
            return AuthResult.failure(exception.getMessage(), false);
        }

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            if (user.isEmailVerified()) {
                return AuthResult.failure("Email is taken", false);
            }

            if (user.getUsername().equals(request.getUsername())) {
                startEmailVerification(user);
                return AuthResult.success("A new verification email has been sent", true);
            }

            return AuthResult.failure("Email is taken", false);
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return AuthResult.failure("Username is taken", false);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail((request.getEmail()));
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmailVerified(false);

        userRepository.save(user);

        startEmailVerification(user);

        return AuthResult.success("Registration successful. Check your email to verify your email address.", true);
    }

    public AuthResult<Boolean> handleLogin(LoginRequest request, HttpServletRequest httpRequest) {
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());
        if (optionalUser.isEmpty()) {
            return AuthResult.failure("User not found", false);
        }

        User user = optionalUser.get();

        if (!user.isEmailVerified()) {
            return AuthResult.failure("Please verify your email address before logging in", false);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return AuthResult.failure("Incorrect password", false);
        }

        if (!user.isTwoFactorEnabled()) {
            sessionService.createSession(user, httpRequest);
            return AuthResult.success("Login complete", false);
        }

        startTwoFactorAuthentication(user);

        return AuthResult.success("2FA Required", true);
    }

    public AuthResult<Void> handleForgotUsername(ForgotUsernameRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        optionalUser.ifPresent(user -> emailService.sendForgotUsernameEmail(user.getEmail(), user.getUsername()));

        return AuthResult.success("If an account is linked to this address, an email has been sent.");
    }

    @Transactional
    public AuthResult<Void> handleForgotPassword(ForgotPasswordRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        optionalUser.ifPresent(user -> {
            if (!user.isEmailVerified()) {
                return;
            }

            invalidateOldTokens(user, TokenType.PASSWORD_RESET);
            String token = createAndSaveVerificationToken(user, TokenType.PASSWORD_RESET, 6 * 60);
            emailService.sendForgotPasswordEmail(user.getEmail(), user.getUsername(), token);
        });

        return AuthResult.success("If an account is linked to this address, a password reset link has been sent.");
    }

    @Transactional
    public AuthResult<Void> handleResetPassword(ResetPasswordRequest request) {
        try {
            authValidator.validatePassword(request.getNewPassword());
        } catch (IllegalArgumentException exception) {
            return AuthResult.failure(exception.getMessage());
        }
        Optional<VerificationToken> optionalToken = getValidToken(request.getToken(), TokenType.PASSWORD_RESET);

        if (optionalToken.isEmpty()) {
            return AuthResult.failure("The reset link has expired or is invalid.");
        }

        VerificationToken validToken = optionalToken.get();
        User user = validToken.getUser();

        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            return AuthResult.failure("The password must be different from the old one.");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        validToken.setUsed(true);
        verificationTokenRepository.save(validToken);

        emailService.sendPasswordChangedEmail(user.getEmail(), user.getUsername());

        return AuthResult.success("Password reset successfully.");
    }

    public void startEmailVerification(User user) {
        invalidateOldTokens(user, TokenType.EMAIL_VERIFICATION);

        String token = createAndSaveVerificationToken(user, TokenType.EMAIL_VERIFICATION, 24 * 60);

        emailService.sendEmailVerificationEmail(user.getEmail(), user.getUsername(), token);
    }

    @Transactional
    public AuthResult<Void> handleVerifyEmail(VerifyEmailRequest request) {
        Optional<VerificationToken> optionalToken = getValidToken(request.getToken(), TokenType.EMAIL_VERIFICATION);

        if (optionalToken.isEmpty()) {
            return AuthResult.failure("The reset link has expired or is invalid.");
        }

        VerificationToken validToken = optionalToken.get();
        User user = validToken.getUser();

        if (!user.isEmailVerified()) {
            user.setEmailVerified(true);
            userRepository.save(user);

            emailService.sendWelcomeEmail(user.getEmail());
        }

        validToken.setUsed(true);
        verificationTokenRepository.save(validToken);

        return AuthResult.success("Email verified successfully");
    }

    public void startTwoFactorAuthentication(User user) {
        invalidateOldTokens(user, TokenType.OTP_CODE);

        String code = createAndSaveVerificationToken(user, TokenType.OTP_CODE, 5);

        emailService.sendTwoFactorAuthenticationCode(user.getEmail(), code);
    }

    @Transactional
    public AuthResult<Void> handleTwoFactorAuthentication(TwoFactorRequest request, HttpServletRequest httpRequest) {
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());

        if (optionalUser.isEmpty()) {
            return AuthResult.failure("Invalid or expired code");
        }

        Optional<VerificationToken> optionalToken = getValidToken(request.getCode(), TokenType.OTP_CODE, optionalUser.get());

        if (optionalToken.isEmpty()) {
            return AuthResult.failure("Invalid or expired code");
        }

        VerificationToken validToken = optionalToken.get();

        validToken.setUsed(true);
        verificationTokenRepository.save(validToken);

        sessionService.createSession(optionalUser.get(), httpRequest);

        return AuthResult.success("Authentication successful");
    }

    @Transactional
    public AuthResult<Void> toggleTwoFactorAuthentication(ToggleTwoFactorRequest request, HttpServletRequest httpRequest) {
        User tempUser = sessionService.getUser(httpRequest);

        if (tempUser == null) {
            return AuthResult.failure("Not logged in");
        }

        Optional<User> optionalUser = userRepository.findByUsername(tempUser.getUsername());

        if (optionalUser.isEmpty()) {
            return AuthResult.failure("Not logged in");
        }

        User user = optionalUser.get();
        user.setTwoFactorEnabled(request.isEnabled());

        userRepository.save(user);

        return AuthResult.success("Two factor authentication is now " + (request.isEnabled()? "enabled" : "disabled"));
    }

    public String createAndSaveVerificationToken(User user, TokenType tokenType, int tokenLifetimeMinutes) {
        String token = (tokenType == TokenType.OTP_CODE) ? String.valueOf(getSixDigitCode()) : UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUser(user);
        verificationToken.setTokenHash(sha256Hash(token));
        verificationToken.setTokenType(tokenType);
        verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(tokenLifetimeMinutes));

        verificationTokenRepository.save(verificationToken);

        return token;
    }

    private Optional<VerificationToken> getValidToken(String unhashedToken, TokenType tokenType) {
        String lookupHash = sha256Hash(unhashedToken);

        return verificationTokenRepository.findByTokenHashAndTokenTypeAndUsedFalseAndExpiryDateAfter(lookupHash, tokenType, LocalDateTime.now());
    }

    private Optional<VerificationToken> getValidToken(String unhashedToken, TokenType tokenType, User user) {
        String lookupHash = sha256Hash(unhashedToken);

        return verificationTokenRepository.findByTokenHashAndTokenTypeAndUserAndUsedFalseAndExpiryDateAfter(lookupHash, tokenType, user, LocalDateTime.now());
    }

    public void invalidateOldTokens(User user, TokenType tokenType) {
        List<VerificationToken> tokens = verificationTokenRepository.findByUserAndTokenType(user, tokenType);

        tokens.forEach(token -> {
            if (!token.isUsed()) {
                token.setUsed(true);
            }
        });

        verificationTokenRepository.saveAll(tokens);
    }

    private int getSixDigitCode() {
        SecureRandom secureRandom = new SecureRandom();

        return (secureRandom.nextInt(900000) + 100000);
    }

    private String sha256Hash(String string) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashedString = messageDigest.digest(string.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hashedString);
        } catch (NoSuchAlgorithmException exception) {
            throw new RuntimeException("Error while hashing", exception);
        }
    }

    public AuthResult<MeResponse> handleMe(HttpServletRequest request) {
        User user = sessionService.getUser(request);

        if (user == null) {
            return AuthResult.failure("Not authenticated", null);
        }

        MeResponse me = new MeResponse(
                user.getUsername(),
                user.getEmail(),
                user.isTwoFactorEnabled()
        );

         return AuthResult.success("User loaded", me);
    }

    public AuthResult<Void> resendTwoFactor(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return AuthResult.failure("User not found");
        }
        startTwoFactorAuthentication(optionalUser.get());
        return AuthResult.success("Code resent");
    }

}
