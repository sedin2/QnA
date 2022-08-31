package com.sedin.qna.exception;

/**
 * 리소스가 중복일 때 던집니다.
 */
public class DuplicatedException extends RuntimeException {

    private static final String DUPLICATED_MESSAGE = "Duplicated Resource: ";

    public DuplicatedException(String resource) {
        super(DUPLICATED_MESSAGE + resource);
    }
}
