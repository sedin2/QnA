package com.sedin.qna.exception;

/**
 * 패스워드가 틀린 경우에 던집니다.
 */
public class PasswordIncorrectException extends RuntimeException {

    public PasswordIncorrectException(String message) {
        super(message);
    }
}
