package com.sedin.qna.account.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountApiResponse {

    private Long id;

    private String loginId;

    private String name;

    private LocalDateTime bornDate;

    private String sex;

    private String email;
}
