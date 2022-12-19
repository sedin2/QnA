package com.sedin.qna.account.model;

import com.sedin.qna.common.util.EnumType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role implements EnumType {

    ROLE_USER("사용자"),
    ROLE_ADMIN("관리자");

    private final String type;

    @Override
    public String getId() {
        return name();
    }

    @Override
    public String getText() {
        return type;
    }

}
