package com.sedin.qna.account.model;

import com.sedin.qna.util.EnumType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender implements EnumType {
    MALE("남자"),
    FEMALE("여자");

    private final String text;

    @Override
    public String getId() {
        return this.name();
    }
}
