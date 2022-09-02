package com.sedin.qna.exception;

/**
 * 리소스에 접근 권한이 없을 경우에 던집니다.
 */
public class PermissionToAccessException extends RuntimeException {

    private static final String UNAUTHORIZED_MESSAGE = "There is no permission to access that resource";

    public PermissionToAccessException() {
        super(UNAUTHORIZED_MESSAGE);
    }
}
