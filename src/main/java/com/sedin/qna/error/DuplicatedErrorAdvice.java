package com.sedin.qna.error;

import com.sedin.qna.network.Header;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DuplicatedErrorAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicatedException.class)
    public Header<String> handleAccountAlreadyExisted(DuplicatedException dex) {
        return Header.ERROR(dex.getMessage());
    }
}
