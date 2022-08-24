package com.sedin.qna.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String token) {
        super("Invalid token: " + token);
    }
}
