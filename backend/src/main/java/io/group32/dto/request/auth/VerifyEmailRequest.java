package io.group32.dto.request.auth;

import lombok.Data;

@Data
public class VerifyEmailRequest {
    private String token;
}
