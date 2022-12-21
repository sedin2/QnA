package com.sedin.qna.common.exception;

/**
 * 리소스가 중복일 경우에 던집니다.
 */
public class DuplicatedException extends RuntimeException {

    private static final String DUPLICATED_MESSAGE = "Duplicated Resource: ";

    public DuplicatedException(String resource) {
        super(DUPLICATED_MESSAGE + resource);
    }
}
