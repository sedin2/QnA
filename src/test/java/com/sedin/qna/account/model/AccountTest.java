package com.sedin.qna.account.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountTest {

    @Test
    void When_New_Account_Expect_Success() {
        // given
        final String LOGIN_ID = "sedin";
        final String PASSWORD = "1234";
        final String NAME = "LeeSeJin";
        final LocalDateTime NOW = LocalDateTime.now();
        final String EMAIL = "sedin@email.com";

        // when
        Account account = Account.builder()
                .id(1L)
                .loginId(LOGIN_ID)
                .password(PASSWORD)
                .name(NAME)
                .bornDate(NOW)
                .sex("M")
                .email(EMAIL)
                .build();

        // then
        assertThat(account).isNotNull();
        assertThat(account.getId()).isEqualTo(1L);
        assertThat(account.getLoginId()).isEqualTo(LOGIN_ID);
        assertThat(account.getPassword()).isEqualTo(PASSWORD);
        assertThat(account.getName()).isEqualTo(NAME);
        assertThat(account.getBornDate()).isEqualTo(NOW);
        assertThat(account.getSex()).isEqualTo("M");
        assertThat(account.getEmail()).isEqualTo(EMAIL);
    }
}
