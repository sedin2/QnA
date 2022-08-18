package com.sedin.qna.common;

import com.sedin.qna.error.DuplicatedException;
import com.sedin.qna.network.Header;
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

    private static final String ERROR_MESSAGE = "Duplicated Error";

    /**
     * 사용자 등록 요청 데이터가 중복일 때 에러 메세지를 리턴합니다.
     *
     * @param exception 데이터 중복예외
     * @return 에러 메세지
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicatedException.class)
    public Header<Map<String, String>> handleAccountAlreadyExisted(DuplicatedException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(exception.getMessage(), ERROR_MESSAGE);

        return Header.ERROR(ERROR_MESSAGE, errorMap);
    }
}
