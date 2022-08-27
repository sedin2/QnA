package com.sedin.qna.exception;

/**
 * 패스워드가 틀린 경우에 던집니다.
 */
public class PasswordNotCorrectException extends RuntimeException {

    public PasswordNotCorrectException(String message) {
        super(message);
    }
}
