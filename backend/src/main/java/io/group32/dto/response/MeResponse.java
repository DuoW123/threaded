package io.group32.dto.response.auth;

public class MeResponse {
    private String username;
    private String email;
    private boolean twoFactorEnabled;

    public MeResponse(String username, String email, boolean twoFactorEnabled) {
        this.username = username;
        this.email = email;
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public boolean isTwoFactorEnabled() { return twoFactorEnabled; }
}
