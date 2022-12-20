package com.sedin.qna.account.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Account Unit Test")
class AccountTest {

    @Test
    @DisplayName("Account instance 생성")
    void When_New_Account_Expect_Success() {
        // given
        final String PASSWORD = "1234";

        // when
        Account account = Account.builder()
                .password(PASSWORD)
                .build();

        // then
        assertThat(account).isNotNull();
        assertThat(account.getPassword()).isEqualTo(PASSWORD);
    }

    @Test
    @DisplayName("updatePasswordAndName 메서드 테스트")
    void When_Update_Account_Expect_Success() {
        // given
        final String PASSWORD = "password";
        final String NEW_PASSWORD = "newPassword";
        final String NAME = "Mocha";
        final String NEW_NAME = "CafeMocha";

        // when
        Account account = Account.builder()
                .password(PASSWORD)
                .name(NAME)
                .build();

        account.updatePasswordAndName(NEW_PASSWORD, NEW_NAME);

        // then
        assertThat(account.getPassword()).isEqualTo(NEW_PASSWORD);
        assertThat(account.getName()).isEqualTo(NEW_NAME);
    }
}
