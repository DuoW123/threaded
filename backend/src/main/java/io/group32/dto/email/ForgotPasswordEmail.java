package io.group32.dto.email;

import lombok.Data;

@Data
public class ForgotPasswordEmail {
    private String username;
    private String token;
}
