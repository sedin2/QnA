package com.sedin.qna.account.model;

import org.junit.jupiter.api.Test;

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

    @Test
    void When_Update_Account_Expect_Success() {
        // given
        final String PASSWORD = "password";
        final String NEW_PASSWORD = "newPassword";
        final String EMAIL = "email@email.com";
        final String NEW_EMAIL = "newEmail@email.com";

        // when
        Account account = Account.builder()
                .password(PASSWORD)
                .email(EMAIL)
                .build();

        account.updatePasswordAndEmail(NEW_PASSWORD, NEW_EMAIL);

        // then
        assertThat(account.getPassword()).isEqualTo(NEW_PASSWORD);
        assertThat(account.getEmail()).isEqualTo(NEW_EMAIL);
    }
}
