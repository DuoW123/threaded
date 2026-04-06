package io.group32.dto.request.auth;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String newPassword;
    private String token;
}