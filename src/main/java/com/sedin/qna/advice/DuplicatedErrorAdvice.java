package com.sedin.qna.advice;

import com.sedin.qna.exception.DuplicatedException;
import com.sedin.qna.network.ApiResponseCode;
import com.sedin.qna.network.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 데이터 중복 검사 관련 예외처리를 담당합니다.
 */
@RestControllerAdvice
public class DuplicatedErrorAdvice {

    private static final String MESSAGE = "message";

    /**
     * 사용자 등록 요청 데이터가 중복일 때 에러 메세지를 리턴합니다.
     *
     * @param exception 데이터 중복예외
     * @return 에러 메세지
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicatedException.class)
    public ApiResponseDto<Map<String, String>> handleAccountAlreadyExisted(DuplicatedException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(MESSAGE, exception.getMessage());

        return ApiResponseDto.ERROR(ApiResponseCode.DUPLICATED_ERROR, errorMap);
    }
}
