package com.sedin.qna.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sedin.qna.account.model.Account;
import com.sedin.qna.account.model.request.AccountSignUpDto;
import com.sedin.qna.account.model.response.AccountApiResponse;
import com.sedin.qna.account.repository.AccountRepository;
import com.sedin.qna.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    private static final String SECRET = "12345678901234567890123456789012";
    private static final Long EXISTED_ID = 1L;
    private static final Long NOT_EXISTED_ID = 9999L;
    private static final String PREFIX = "prefix";
    private static final String LOGIN_ID = "sedin";
    private static final String EXISTED_LOGIN_ID = "sedin";
    private static final String PASSWORD = "12341234";
    private static final String NAME = "LeeSeJin";
    private static final String SEX = "M";
    private static final String EMAIL = "sejin@email.com";

    @Mock
    private ObjectMapper objectMapper;

    private AccountService accountService;

    private AccountRepository accountRepository = mock(AccountRepository.class);

    private Account account;

    @BeforeEach
    void prepare() {
        JwtUtil jwtUtil = new JwtUtil(SECRET);
        accountService = new AccountServiceImpl(objectMapper, jwtUtil, accountRepository);

        account = Account.builder()
                .id(EXISTED_ID)
                .loginId(LOGIN_ID)
                .password(PASSWORD)
                .name(NAME)
                .bornDate(LocalDateTime.now())
                .sex(SEX)
                .email(EMAIL)
                .build();

        when(objectMapper.convertValue(any(AccountSignUpDto.class), eq(Account.class))).thenReturn(account);
        given(accountRepository.save(any(Account.class))).willReturn(account);
    }

    @Test
    @DisplayName("AccountSignUpDto가 정상일 때")
    void createAccount() {
        AccountSignUpDto accountSignUpDto = AccountSignUpDto.builder()
                .loginId(LOGIN_ID)
                .password(PASSWORD)
                .name(NAME)
                .bornDate(LocalDateTime.now())
                .sex(SEX)
                .email(EMAIL)
                .build();

        AccountApiResponse accountApiResponse = accountService.signUp(accountSignUpDto).getData();
        verify(accountRepository, times(1)).save(any(Account.class));
        assertThat(accountApiResponse.getId()).isEqualTo(EXISTED_ID);
        assertThat(accountApiResponse.getName()).isEqualTo(NAME);
    }
}
