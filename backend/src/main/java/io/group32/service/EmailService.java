package io.group32.service;

import io.group32.dto.email.*;
import io.group32.model.User;
import io.group32.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;

    @Value("classpath:templates/")
    private Resource templateFolder;

    public EmailService(JavaMailSender javaMailSender, UserRepository userRepository) {
        this.javaMailSender = javaMailSender;
        this.userRepository = userRepository;
    }

    private String getTemplate(String templateName) throws IOException {
        Resource template = templateFolder.createRelative(templateName);
        return new String(template.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    private String processTemplate(String templateName, Object dtoObject) throws IOException, IllegalAccessException {
        String template = getTemplate(templateName);

        for (Field field : dtoObject.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            Object value = field.get(dtoObject);
            String placeholder = "[[" + field.getName().toUpperCase() + "]]";

            template = template.replace(placeholder, value != null ? value.toString() : "");
        }

        return template;
    }

    public void sendEmailWithTemplate(String destinationEmail, String subject, String text) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setFrom("threadedmailbox@gmail.com");
        helper.setTo(destinationEmail);
        helper.setSubject(subject);
        helper.setText(text, true);

        javaMailSender.send(mimeMessage);
    }


    public void sendWelcomeEmail(String destinationEmail) {
        try {
            User user = userRepository.findByEmail(destinationEmail).orElseThrow(() -> new RuntimeException("User not found."));

            WelcomeEmail email = new WelcomeEmail();
            email.setUsername(user.getUsername());

            String text = processTemplate("welcome-email.html", email);

            sendEmailWithTemplate(destinationEmail, "Welcome to Threaded", text);
        } catch (MessagingException | IOException | IllegalAccessException exception) {
            throw new RuntimeException("Failed to send welcome email.");
        }
    }

    public void sendForgotUsernameEmail(String destinationEmail, String username) {
        try {
            ForgotUsernameEmail email =  new ForgotUsernameEmail();
            email.setUsername(username);
            email.setEmail(destinationEmail);

            String text = processTemplate("forgot-username-email.html", email);

            sendEmailWithTemplate(destinationEmail, "Forgot Username Request", text);
        } catch (MessagingException | IOException | IllegalAccessException exception) {
            throw new RuntimeException("Failed to send forgot username email");
        }
    }

    public void sendForgotPasswordEmail(String destinationEmail, String username, String token) {
        try {
            ForgotPasswordEmail email =  new ForgotPasswordEmail();
            email.setUsername(username);
            email.setToken(token);

            String text = processTemplate("forgot-password-email.html", email);

            sendEmailWithTemplate(destinationEmail, "Password Reset Request", text);
        } catch (MessagingException | IOException | IllegalAccessException exception) {
            throw new RuntimeException("Failed to send forgot password email");
        }
    }

    public void sendPasswordChangedEmail(String destinationEmail, String username) {
        try {
            PasswordChangedEmail email =  new PasswordChangedEmail();
            email.setUsername(username);

            String text = processTemplate("password-changed-email.html", email);

            sendEmailWithTemplate(destinationEmail, "Password Changed", text);
        } catch (MessagingException | IOException | IllegalAccessException exception) {
            throw new RuntimeException("Failed to send password changed email");
        }
    }

    public void sendEmailVerificationEmail(String destinationEmail, String username, String token) {
        try {
            EmailVerificationEmail email =  new EmailVerificationEmail();
            email.setUsername(username);
            email.setToken(token);

            String text = processTemplate("email-verification-email.html", email);

            sendEmailWithTemplate(destinationEmail, "Email Verification", text);
        } catch (MessagingException | IOException | IllegalAccessException exception) {
            throw new RuntimeException("Failed to send email verification link");
        }
    }

    public void sendTwoFactorAuthenticationCode(String destinationEmail, String code) {
        try {
            TwoFactorEmail email =  new TwoFactorEmail();
            email.setCode(code);

            String text = processTemplate("two-factor-email.html", email);

            sendEmailWithTemplate(destinationEmail, "Two Factor Authentication", text);
        } catch (MessagingException | IOException | IllegalAccessException exception) {
            throw new RuntimeException("Failed to send 2FA code");
        }
    }
}
