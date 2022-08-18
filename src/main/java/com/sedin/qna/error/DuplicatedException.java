package com.sedin.qna.error;

public class DuplicatedException extends RuntimeException {
    public DuplicatedException(String message) {
        super(message + " is Already Existed");
    }
}
