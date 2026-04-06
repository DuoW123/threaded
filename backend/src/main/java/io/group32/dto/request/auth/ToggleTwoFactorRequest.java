package io.group32.dto.request.auth;

import lombok.Data;

@Data
public class ToggleTwoFactorRequest {
    private boolean enabled;
}
