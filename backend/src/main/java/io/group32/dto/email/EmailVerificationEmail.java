package io.group32.dto.email;

import lombok.Data;

@Data
public class EmailVerificationEmail {
    private String username;
    private String token;
}
