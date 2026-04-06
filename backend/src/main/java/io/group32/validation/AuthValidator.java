package io.group32.validation;

import org.springframework.stereotype.Component;

@Component
public class AuthValidator {
    public void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (username.length() < 4) {
            throw new IllegalArgumentException("Username must be at least 4 characters long");
        }

        if (username.contains(" ")) {
            throw new IllegalArgumentException("Username cannot contain space characters");
        }
    }

    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (email.contains(" ")) {
            throw new IllegalArgumentException("Email cannot contain space characters");
        }

        if (!email.contains("@") || email.indexOf('@') != email.lastIndexOf('@')) {
            throw new IllegalArgumentException("Email must contain exactly one '@'");
        }

        if (email.startsWith("@") || email.endsWith("@")) {
            throw new IllegalArgumentException("Email cannot start or end with a '@'");
        }

        int atIndex = email.indexOf('@');
        String domain = email.substring(atIndex + 1);
        int firstDotIndex = domain.indexOf('.');

        if (firstDotIndex <= 0) {
            throw new IllegalArgumentException("Invalid domain");
        }

        if (email.startsWith(".") || email.endsWith(".")) {
            throw new IllegalArgumentException("Email cannot start or end with a '.'");
        }
    }

    public void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        if (password.contains(" ")) {
            throw new IllegalArgumentException("Password cannot contain space characters");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }

        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }

        if (!password.matches(".*[!@$%^&*+#].*")) {
            throw new IllegalArgumentException("Password must contain at least one special character");
        }
    }
}
