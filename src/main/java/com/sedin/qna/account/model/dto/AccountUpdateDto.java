package com.sedin.qna.account.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountUpdateDto {

    private String originalPassword;
    private String newPassword;
    private String email;
}
