package com.sedin.qna.api.document.controller;

import com.sedin.qna.account.model.Role;
import com.sedin.qna.api.document.Docs;
import com.sedin.qna.common.response.ApiResponseCode;
import com.sedin.qna.common.response.ApiResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class RestDocsController {

    @GetMapping("/docs")
    public ApiResponseDto<Docs> findAll() {

        Map<String, String> apiResponseCodes = Arrays.stream(ApiResponseCode.values())
                .collect(Collectors.toMap(ApiResponseCode::getId, ApiResponseCode::getText));

        return ApiResponseDto.OK(
                Docs.builder()
                        .apiResponseCodes(apiResponseCodes)
                        .build()
        );
    }
}
