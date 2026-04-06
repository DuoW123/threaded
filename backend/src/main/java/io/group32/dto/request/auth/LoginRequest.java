package io.group32.dto.request.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}