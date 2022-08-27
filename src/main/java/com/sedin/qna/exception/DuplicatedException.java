package com.sedin.qna.exception;

/**
 * 리소스가 중복일 때 던집니다.
 */
public class DuplicatedException extends RuntimeException {

    public DuplicatedException(String message) {
        super(message);
    }
}
