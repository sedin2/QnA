package com.sedin.qna.advice;

import com.sedin.qna.exception.PasswordNotCorrectException;
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

    private static final String ERROR_MESSAGE = "Password Incorrect Error";

    /**
     * 패스워드가 틀릴 때 에러 메세지를 리턴합니다.
     *
     * @param exception 패스워드 불일치 예외
     * @return 에러 응답
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(PasswordNotCorrectException.class)
    public ApiResponseDto<Map<String, String>> handlePasswordIncorrect(PasswordNotCorrectException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(exception.getMessage(), ERROR_MESSAGE);

        return ApiResponseDto.ERROR(ApiResponseCode.UNAUTHORIZED, errorMap);
    }
}
