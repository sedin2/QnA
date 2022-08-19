package com.sedin.qna.account.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountUpdateDto {

    @NotBlank
    private String originalPassword;

    @NotBlank
    private String newPassword;

    @Email
    private String email;
}
