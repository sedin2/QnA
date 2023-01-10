package com.sedin.qna.account.repository;

import com.sedin.qna.account.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@ExtendWith(SpringExtension.class)
class AccountRepositoryTest {

    private static final String EMAIL = "sejin@email.com";
    private static final String NAME = "LeeSeJin";
    private static final String PASSWORD = "12341234";

    @Autowired
    private AccountRepository accountRepository;

    private Account registeredAccount;

    @BeforeEach
    void prepare() {
        registeredAccount = Account.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .name(NAME)
                .build();
    }

    @Test
    @DisplayName("이미 이메일이 등록되있다면 true를 반환한다")
    void When_Exists_By_Email_Expect_Success() {
        // given
        Account savedAccount = accountRepository.save(registeredAccount);
        assertThat(savedAccount).isNotNull().isEqualTo(registeredAccount);

        // when
        boolean result = accountRepository.existsByEmail(EMAIL);

        // then
        assertThat(result).isTrue();
    }
}
