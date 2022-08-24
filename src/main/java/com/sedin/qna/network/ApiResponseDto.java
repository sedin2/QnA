package com.sedin.qna.network;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class ApiResponseDto<T> {

    public static final ApiResponseDto<String> DEFAULT_OK = new ApiResponseDto<>(ApiResponseCode.OK);

    private ApiResponseCode code;

    private String message;

    private T data;

    private ApiResponseDto(ApiResponseCode status) {
        this.bindStatus(status);
    }

    private ApiResponseDto(ApiResponseCode status, T data) {
        this.bindStatus(status);
        this.data = data;
    }

    private ApiResponseDto(ApiResponseCode code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    private void bindStatus(ApiResponseCode status) {
        this.code = status;
        this.message = status.getText();
        this.data = (T) "";
    }

    public static <T> ApiResponseDto<T> OK(T data) {
        return new ApiResponseDto<>(ApiResponseCode.OK, data);
    }

    public static <T> ApiResponseDto<T> ERROR(ApiResponseCode code, T data) {
        return new ApiResponseDto<>(code, data);
    }
}
