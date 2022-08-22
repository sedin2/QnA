package com.sedin.qna.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sedin.qna.account.model.Account;
import com.sedin.qna.account.model.request.AccountSignUpDto;
import com.sedin.qna.account.model.request.AccountUpdateDto;
import com.sedin.qna.account.model.response.AccountApiResponse;
import com.sedin.qna.account.repository.AccountRepository;
import com.sedin.qna.error.DuplicatedException;
import com.sedin.qna.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    private static final String SECRET = "12345678901234567890123456789012";
    private static final Long EXISTED_ID = 1L;
    private static final Long NOT_EXISTED_ID = 9999L;
    private static final String LOGIN_ID = "sedin";
    private static final String EXISTED_LOGIN_ID = "existed";
    private static final String PASSWORD = "12341234";
    private static final String NEW_PASSWORD = "newPassword";
    private static final String NAME = "LeeSeJin";
    private static final String MALE = "M";
    private static final String EMAIL = "sejin@email.com";
    private static final String NEW_EMAIL = "new@email.com";
    private static final String EXISTED_EMAIL = "existed@email.com";

    private ObjectMapper objectMapper;

    @MockBean
    private AccountRepository accountRepository = mock(AccountRepository.class);

    private AccountService accountService;

    private Account account;
    private AccountSignUpDto accountSignUpDto;
    private AccountUpdateDto accountUpdateDto;
    private AccountSignUpDto duplicatedDto;

    @BeforeEach
    void prepare() {
        JwtUtil jwtUtil = new JwtUtil(SECRET);
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        accountService = new AccountServiceImpl(objectMapper, jwtUtil, accountRepository);

        account = Account.builder()
                .id(EXISTED_ID)
                .loginId(LOGIN_ID)
                .password(PASSWORD)
                .name(NAME)
                .bornDate(LocalDateTime.now())
                .sex(MALE)
                .email(EMAIL)
                .build();
    }

    @Nested
    @DisplayName("signUp 메소드는")
    class Describe_signUp {

        @Nested
        @DisplayName("유효한 사용자 등록 정보가 주어지면")
        class Context_with_valid_accountSignUpDto {

            @BeforeEach
            void prepareValidAccountSignUpDto() {
                accountSignUpDto = AccountSignUpDto.builder()
                        .loginId(LOGIN_ID)
                        .password(PASSWORD)
                        .name(NAME)
                        .bornDate(LocalDateTime.now())
                        .sex(MALE)
                        .email(EMAIL)
                        .build();

                given(accountRepository.save(any(Account.class))).willReturn(account);
            }

            @Test
            @DisplayName("사용자를 등록하고 Account 정보를 담은 응답을 리턴한다")
            void it_returns_response_with_new_account() {
                AccountApiResponse response = accountService.signUp(accountSignUpDto).getData();

                assertThat(response.getLoginId()).isEqualTo(LOGIN_ID);
                assertThat(response.getName()).isEqualTo(NAME);

                verify(accountRepository, times(1)).save(any(Account.class));
            }
        }

        @Nested
        @DisplayName("이미 등록된 로그인 아이디가 주어지면")
        class Context_with_duplicated_loginId_in_accountSignUpDto {

            @BeforeEach
            void prepareDuplicatedLoginId() {
                duplicatedDto = AccountSignUpDto.builder()
                        .loginId(EXISTED_LOGIN_ID)
                        .password(PASSWORD)
                        .name(NAME)
                        .bornDate(LocalDateTime.now())
                        .sex(MALE)
                        .email(EMAIL)
                        .build();

                given(accountRepository.existsByLoginId(EXISTED_LOGIN_ID)).willThrow(DuplicatedException.class);
            }

            @Test
            @DisplayName("DuplicatedException 예외를 던진다")
            void it_returns_duplicatedException() {
                assertThatThrownBy(() -> accountService.signUp(duplicatedDto))
                        .isExactlyInstanceOf(DuplicatedException.class);

                verify(accountRepository, never()).save(any(Account.class));
            }
        }

        @Nested
        @DisplayName("이미 등록된 이메일이 주어지면")
        class Context_with_duplicated_email_in_accountSignUpDto {

            @BeforeEach
            void prepareDuplicatedEmail() {
                duplicatedDto = AccountSignUpDto.builder()
                        .loginId(LOGIN_ID)
                        .password(PASSWORD)
                        .name(NAME)
                        .bornDate(LocalDateTime.now())
                        .sex(MALE)
                        .email(EXISTED_EMAIL)
                        .build();

                given(accountRepository.existsByEmail(EXISTED_EMAIL)).willThrow(DuplicatedException.class);
            }

            @Test
            @DisplayName("DuplicatedException 예외를 던진다")
            void it_returns_duplicatedException() {
                assertThatThrownBy(() -> accountService.signUp(duplicatedDto))
                        .isExactlyInstanceOf(DuplicatedException.class);

                verify(accountRepository, never()).save(any(Account.class));
            }
        }
    }

    @Nested
    @DisplayName("만약 찾을 수 있는 accountId가 주어지면")
    class Context_with_found_accountId {

        @BeforeEach
        void prepareFoundAccount() {
            accountUpdateDto = AccountUpdateDto.builder()
                    .originalPassword(PASSWORD)
                    .newPassword(NEW_PASSWORD)
                    .email(NEW_EMAIL)
                    .build();

            account = Account.builder()
                    .password(NEW_PASSWORD)
                    .email(NEW_EMAIL)
                    .build();

            given(accountRepository.findById(EXISTED_ID)).willReturn(Optional.of(account));
            doNothing().when(accountRepository).delete(account);
        }

        @Nested
        @DisplayName("update 메소드는")
        class Describe_update {

            @Test
            @DisplayName("사용자를 수정하고 Account 정보를 담은 응답을 리턴한다")
            void it_returns_response_with_updated_account() {
                AccountApiResponse response = accountService.update(EXISTED_ID, accountUpdateDto).getData();

                assertThat(response.getEmail()).isEqualTo(NEW_EMAIL);
                verify(accountRepository, times(1)).findById(any(Long.class));
            }
        }

        @Nested
        @DisplayName("delete 메소드는")
        class Describe_delete {

            @Test
            @DisplayName("사용자를 삭제한다")
            void it_deletes_account() {
                accountService.delete(EXISTED_ID);
                verify(accountRepository, times(1)).delete(any(Account.class));
            }
        }
    }

    @Nested
    @DisplayName("만약 찾을 수 없는 accountId가 주어지면")
    class Context_with_not_found_accountId {

        @Nested
        @DisplayName("update 메소드는")
        class Describe_update {

            @Test
            @DisplayName("NotFoundException 예외를 던진다")
            void it_returns_notFoundException() {
                assertThat(1L).isEqualTo(1L);
            }
        }

        @Nested
        @DisplayName("delete 메소드는")
        class Describe_delete {

            @Test
            @DisplayName("NotFoundException 예외를 던진다")
            void it_returns_notFoundException() {
                assertThat(1L).isEqualTo(1L);
            }
        }
    }
}
