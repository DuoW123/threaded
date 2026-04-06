package io.group32.service.result;

import lombok.Data;

@Data
public class AuthResult<T> {
    private boolean success;
    private String message;
    private T data;

    public AuthResult(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
    }

    public AuthResult(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> AuthResult<T> success(String message) {
        return new AuthResult<T>(true, message);
    }

    public static <T> AuthResult<T> success(String message, T data) {
        return new AuthResult<T>(true, message, data);
    }

    public static <T> AuthResult<T> failure(String message) {
        return new AuthResult<T>(false, message);
    }

    public static <T> AuthResult<T> failure(String message, T data) {
        return new AuthResult<T>(false, message, data);
    }
}