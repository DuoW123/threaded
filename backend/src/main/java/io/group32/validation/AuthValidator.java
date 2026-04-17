package io.group32.validation;

import org.springframework.stereotype.Component;

@Component
public class AuthValidator {
    public void validateUsername(String username) {
        //Blank username
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        //Username too short
        if (username.length() < 4) {
            throw new IllegalArgumentException("Username must be at least 4 characters long");
        }

        //Username that contains spaces
        if (username.contains(" ")) {
            throw new IllegalArgumentException("Username cannot contain space characters");
        }
    }

    public void validateEmail(String email) {
        //Blank email
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        //Email that contains spaces
        if (email.contains(" ")) {
            throw new IllegalArgumentException("Email cannot contain space characters");
        }

        //Invalid format (no @ or multiple @)
        if (!email.contains("@") || email.indexOf('@') != email.lastIndexOf('@')) {
            throw new IllegalArgumentException("Email must contain exactly one '@'");
        }

        //Email cant end or start with '@'
        if (email.startsWith("@") || email.endsWith("@")) {
            throw new IllegalArgumentException("Email cannot start or end with a '@'");
        }

        int atIndex = email.indexOf('@');
        String domain = email.substring(atIndex + 1);
        int firstDotIndex = domain.indexOf('.');

        //Valid emails contain at least one dot
        if (firstDotIndex <= 0) {
            throw new IllegalArgumentException("Invalid domain");
        }

        //Email cant end or start with '.'
        if (email.startsWith(".") || email.endsWith(".")) {
            throw new IllegalArgumentException("Email cannot start or end with a '.'");
        }
    }

    public void validatePassword(String password) {
        //Blank password
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        //Password too short
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        //Password cant contain spaces
        if (password.contains(" ")) {
            throw new IllegalArgumentException("Password cannot contain space characters");
        }

        //Password doesn't contain capital letter
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }

        //Password doesn't contain lowercase letter
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }

        //Password doesn't contain digit
        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }

        //Password doesn't contain special character
        if (!password.matches(".*[!@$%^&*+#].*")) {
            throw new IllegalArgumentException("Password must contain at least one special character");
        }
    }
}
