package com.sedin.qna.advice;

import com.sedin.qna.exception.InvalidTokenException;
import com.sedin.qna.exception.PasswordIncorrectException;
import com.sedin.qna.exception.PermissionToAccessException;
import com.sedin.qna.network.ApiResponseCode;
import com.sedin.qna.network.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 인증 관련 예외처리를 담당합니다.
 */
@RestControllerAdvice
public class AuthorizedErrorAdvice {

    private static final String MESSAGE = "message";

    /**
     * 패스워드가 틀릴 때 에러 메세지를 리턴합니다.
     *
     * @param exception 패스워드 불일치 예외
     * @return 에러 응답
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(PasswordIncorrectException.class)
    public ApiResponseDto<Map<String, String>> handlePasswordIncorrect(PasswordIncorrectException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(MESSAGE, exception.getMessage());

        return ApiResponseDto.ERROR(ApiResponseCode.UNAUTHORIZED, errorMap);
    }

    /**
     * 토큰이 유효하지 않을 때 에러 메세지를 리턴합니다.
     * @param exception 토큰 유효성 예외
     * @return 에러 응답
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidTokenException.class)
    public ApiResponseDto<Map<String, String>> handleTokenInvalid(InvalidTokenException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(MESSAGE, exception.getMessage());

        return ApiResponseDto.ERROR(ApiResponseCode.UNAUTHORIZED, errorMap);
    }

    /**
     * 리소스에 접근 권한이 없을 경우에 에러 메세지를 리턴합니다.
     * @param exception 리소스
     * @return 에러 응답
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(PermissionToAccessException.class)
    public ApiResponseDto<Map<String, String>> handlePermissionToAccess(PermissionToAccessException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(MESSAGE, exception.getMessage());

        return ApiResponseDto.ERROR(ApiResponseCode.UNAUTHORIZED, errorMap);
    }
}
