package com.sedin.qna.advice;

import com.sedin.qna.exception.NotFoundException;
import com.sedin.qna.network.ApiResponseCode;
import com.sedin.qna.network.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 데이터 검색 관련 예외처리를 담당합니다.
 */
@RestControllerAdvice
public class NotFoundErrorAdvice {

    private static final String ERROR_MESSAGE = "Not Found Error";

    /**
     * 사용자를 찾을 수 없을 때 에러 메세지를 리턴합니다.
     *
     * @param exception 데이터 검색 예외
     * @return 에러 메세지
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotFoundException.class)
    public ApiResponseDto<Map<String, String>> handleAccountNotFound(NotFoundException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(exception.getMessage(), ERROR_MESSAGE);

        return ApiResponseDto.ERROR(ApiResponseCode.NOT_FOUND, errorMap);
    }
}
