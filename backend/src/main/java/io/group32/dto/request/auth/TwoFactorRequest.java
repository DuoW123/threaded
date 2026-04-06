package io.group32.dto.request.auth;

import lombok.Data;

@Data
public class TwoFactorRequest {
    private String username;
    private String code;
}