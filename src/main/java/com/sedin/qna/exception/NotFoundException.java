package com.sedin.qna.exception;

/**
 * 리소스를 찾을 수 없을 때 던집니다.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
