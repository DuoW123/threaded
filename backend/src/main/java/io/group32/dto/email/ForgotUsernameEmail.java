package io.group32.dto.email;

import lombok.Data;

@Data
public class ForgotUsernameEmail {
    private String username;
    private String email;
}
