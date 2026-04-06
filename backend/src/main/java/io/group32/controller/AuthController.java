package io.group32.controller;

import io.group32.dto.request.auth.*;
import io.group32.dto.response.ApiResponse;
import io.group32.service.AuthService;
import io.group32.service.result.AuthResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Boolean>> register(@RequestBody RegisterRequest request) {
        AuthResult<Boolean> result = authService.handleRegister(request);
        ApiResponse<Boolean> response = new ApiResponse<>(result.isSuccess(), result.getMessage(), result.getData());

        if (result.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Boolean>> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        AuthResult<Boolean> result = authService.handleLogin(request, httpRequest);
        ApiResponse<Boolean> response = new ApiResponse<>(result.isSuccess(), result.getMessage(), result.getData());

        if (result.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/forgot-username")
    public ResponseEntity<ApiResponse<Void>> forgotUsername(@RequestBody ForgotUsernameRequest request) {
        AuthResult<Void> result = authService.handleForgotUsername(request);
        ApiResponse<Void> response = new ApiResponse<>(result.isSuccess(), result.getMessage(), result.getData());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        AuthResult<Void> result = authService.handleForgotPassword(request);
        ApiResponse<Void> response = new ApiResponse<>(result.isSuccess(), result.getMessage(), result.getData());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPasswordRequest request) {
        AuthResult<Void> result = authService.handleResetPassword(request);
        ApiResponse<Void> response = new ApiResponse<>(result.isSuccess(), result.getMessage(), result.getData());

        if (result.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestBody VerifyEmailRequest request) {
        AuthResult<Void> result = authService.handleVerifyEmail(request);
        ApiResponse<Void> response = new ApiResponse<>(result.isSuccess(), result.getMessage(), result.getData());

        if (result.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/verify-two-factor")
    public ResponseEntity<ApiResponse<Void>> verifyTwoFactor(@RequestBody TwoFactorRequest request, HttpServletRequest httpRequest) {
        AuthResult<Void> result = authService.handleTwoFactorAuthentication(request, httpRequest);
        ApiResponse<Void> response = new ApiResponse<>(result.isSuccess(), result.getMessage(), result.getData());

        if (result.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/toggle-two-factor")
    public ResponseEntity<ApiResponse<Void>> toggleTwoFactor(@RequestBody ToggleTwoFactorRequest request, HttpServletRequest httpRequest) {
        AuthResult<Void> result = authService.toggleTwoFactorAuthentication(request, httpRequest);
        ApiResponse<Void> response = new ApiResponse<>(result.isSuccess(), result.getMessage(), result.getData());

        if (result.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}