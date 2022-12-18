package com.sedin.qna.common.exception;

/**
 * 리소스를 찾을 수 없을 경우에 던집니다.
 */
public class NotFoundException extends RuntimeException {

    private static final String NOT_FOUND = "Not Found: ";

    public NotFoundException(String resource) {
        super(NOT_FOUND + resource);
    }
}
