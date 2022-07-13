package com.sedin.qna.account.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AccountTest {

    @Test
    void When_New_Account_Expect_Success() {

        // given
        final String LOGIN_ID = "sedin";
        final String PASSWORD = "1234";

        // when
        Account account = Account.builder()
                .id(1L)
                .loginId(LOGIN_ID)
                .password(PASSWORD)
                .build();

        // then
        assertThat(account).isNotNull();
        assertThat(account.getId()).isEqualTo(1L);
        assertThat(account.getLoginId()).isEqualTo(LOGIN_ID);
        assertThat(account.getPassword()).isEqualTo(PASSWORD);
    }
}
