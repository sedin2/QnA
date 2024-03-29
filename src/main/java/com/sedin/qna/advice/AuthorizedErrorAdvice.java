package com.sedin.qna.advice;

import com.sedin.qna.common.exception.PasswordIncorrectException;
import com.sedin.qna.common.response.ApiResponseCode;
import com.sedin.qna.common.response.ApiResponseDto;
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

}
