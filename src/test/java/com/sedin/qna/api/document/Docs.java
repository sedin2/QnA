package com.sedin.qna.api.document;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class Docs {

    Map<String, String> genders;
    Map<String, String> apiResponseCodes;

    @Builder
    private Docs(Map<String, String> genders, Map<String, String> apiResponseCodes) {
        this.genders = genders;
        this.apiResponseCodes = apiResponseCodes;
    }
}
