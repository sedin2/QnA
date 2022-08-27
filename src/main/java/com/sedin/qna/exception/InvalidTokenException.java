package com.sedin.qna.exception;

/**
 * 토큰이 유효하지 않은 경우에 던집니다.
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String token) {
        super("Invalid token: " + token);
    }
}
