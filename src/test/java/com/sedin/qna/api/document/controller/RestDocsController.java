package com.sedin.qna.api.document.controller;

import com.sedin.qna.account.model.Gender;
import com.sedin.qna.api.document.Docs;
import com.sedin.qna.network.ApiResponseCode;
import com.sedin.qna.network.ApiResponseDto;
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

        Map<String, String> gender = Arrays.stream(Gender.values())
                .collect(Collectors.toMap(Gender::getId, Gender::getText));

        return ApiResponseDto.OK(
                Docs.builder()
                        .apiResponseCodes(apiResponseCodes)
                        .genders(gender)
                        .build()
        );
    }
}
