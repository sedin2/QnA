package com.sedin.qna.common.exception;

/**
 * 패스워드가 틀린 경우에 던집니다.
 */
public class PasswordIncorrectException extends RuntimeException {

    private static final String INCORRECT_PASSWORD = "Incorrect Password";

    public PasswordIncorrectException() {
        super(INCORRECT_PASSWORD);
    }
}
