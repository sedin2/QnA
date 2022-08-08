package com.sedin.qna.network;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Header<T> {

    private String resultCode;

    private String description;

    private T data;

    public static <T> Header<T> OK() {
        return (Header<T>) Header.builder()
                .resultCode("OK")
                .description("OK")
                .build();
    }

    public static <T> Header<T> OK(T data) {
        return (Header<T>) Header.builder()
                .resultCode("OK")
                .description("OK")
                .data(data)
                .build();
    }

    public static <T> Header<T> ERROR(String description) {
        return (Header<T>) Header.builder()
                .resultCode("ERROR")
                .description(description)
                .build();
    }
}
