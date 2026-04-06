package io.group32.dto.request.auth;

import lombok.Data;

@Data
public class ForgotUsernameRequest {
    private String email;
}