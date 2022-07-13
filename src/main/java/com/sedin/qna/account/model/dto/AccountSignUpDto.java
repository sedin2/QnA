package com.sedin.qna.account.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountSignUpDto {

    private String loginId;
    private String password;
    private String name;
    private LocalDateTime bornDate;
    private String sex;
    private String email;
}
