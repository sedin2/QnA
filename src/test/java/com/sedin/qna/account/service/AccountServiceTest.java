package com.sedin.qna.account.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.account.model.AccountDto;
import com.sedin.qna.account.model.Gender;
import com.sedin.qna.account.repository.AccountRepository;
import com.sedin.qna.exception.DuplicatedException;
import com.sedin.qna.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
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

    private static final Long EXISTED_ID = 1L;
    private static final Long NOT_EXISTED_ID = 9999L;
    private static final String LOGIN_ID = "sedin";
    private static final String EXISTED_LOGIN_ID = "existed";
    private static final String ENCODED_PASSWORD = "1e2n3c4o1d2e34";
    private static final String RAW_PASSWORD = "12341234";
    private static final String NEW_PASSWORD = "newPassword";
    private static final String NAME = "LeeSeJin";
    private static final LocalDate BORN_DATE = LocalDate.of(1994, 8, 30);
    private static final String EMAIL = "sejin@email.com";
    private static final String NEW_EMAIL = "new@email.com";
    private static final String EXISTED_EMAIL = "existed@email.com";

    @MockBean
    private PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

    @MockBean
    private AccountRepository accountRepository = mock(AccountRepository.class);

    private AccountService accountService;

    private Account account;
    private AccountDto.Create createDto;
    private AccountDto.Create duplicatedCreateDto;
    private AccountDto.Update updateDto;
    private AccountDto.Response response;

    @BeforeEach
    void prepare() {
        accountService = new AccountServiceImpl(passwordEncoder, accountRepository);

        account = Account.builder()
                .loginId(LOGIN_ID)
                .password(ENCODED_PASSWORD)
                .name(NAME)
                .bornDate(BORN_DATE)
                .gender(Gender.MALE)
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
                createDto = AccountDto.Create.builder()
                        .loginId(LOGIN_ID)
                        .password(RAW_PASSWORD)
                        .name(NAME)
                        .bornDate(BORN_DATE)
                        .gender(Gender.MALE)
                        .email(EMAIL)
                        .build();

                given(accountRepository.save(any(Account.class))).willReturn(account);
            }

            @Test
            @DisplayName("사용자를 등록하고 Account 정보를 담은 응답을 리턴한다")
            void it_returns_response_with_new_account() {
                response = accountService.signUp(createDto);

                assertThat(response.getLoginId()).isEqualTo(LOGIN_ID);
                assertThat(response.getName()).isEqualTo(NAME);
                assertThat(response.getPassword()).isNotEqualTo(RAW_PASSWORD);

                verify(accountRepository, times(1)).save(any(Account.class));
            }
        }

        @Nested
        @DisplayName("이미 등록된 로그인 아이디가 주어지면")
        class Context_with_duplicated_loginId_in_accountSignUpDto {

            @BeforeEach
            void prepareDuplicatedLoginId() {
                duplicatedCreateDto = AccountDto.Create.builder()
                        .loginId(EXISTED_LOGIN_ID)
                        .password(RAW_PASSWORD)
                        .name(NAME)
                        .bornDate(BORN_DATE)
                        .gender(Gender.MALE)
                        .email(EMAIL)
                        .build();

                given(accountRepository.existsByLoginId(EXISTED_LOGIN_ID)).willReturn(true);
            }

            @Test
            @DisplayName("DuplicatedException 예외를 던진다")
            void it_returns_duplicatedException() {
                assertThatThrownBy(() -> accountService.signUp(duplicatedCreateDto))
                        .isExactlyInstanceOf(DuplicatedException.class);

                verify(accountRepository, never()).save(any(Account.class));
            }
        }

        @Nested
        @DisplayName("이미 등록된 이메일이 주어지면")
        class Context_with_duplicated_email_in_accountSignUpDto {

            @BeforeEach
            void prepareDuplicatedEmail() {
                duplicatedCreateDto = AccountDto.Create.builder()
                        .loginId(LOGIN_ID)
                        .password(RAW_PASSWORD)
                        .name(NAME)
                        .bornDate(BORN_DATE)
                        .gender(Gender.MALE)
                        .email(EXISTED_EMAIL)
                        .build();

                given(accountRepository.existsByEmail(EXISTED_EMAIL)).willReturn(true);
            }

            @Test
            @DisplayName("DuplicatedException 예외를 던진다")
            void it_returns_duplicatedException() {
                assertThatThrownBy(() -> accountService.signUp(duplicatedCreateDto))
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
            updateDto = AccountDto.Update.builder()
                    .originalPassword(RAW_PASSWORD)
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
                response = accountService.update(EXISTED_ID, EXISTED_ID, updateDto);

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
                accountService.delete(EXISTED_ID, EXISTED_ID);
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

            @BeforeEach
            void prepareUpdate() {
                updateDto = AccountDto.Update.builder()
                        .originalPassword(RAW_PASSWORD)
                        .newPassword(NEW_PASSWORD)
                        .email(NEW_EMAIL)
                        .build();
            }

            @Test
            @DisplayName("NotFoundException 예외를 던진다")
            void it_returns_notFoundException() {
                assertThatThrownBy(() -> accountService.update(NOT_EXISTED_ID, NOT_EXISTED_ID, updateDto))
                        .isExactlyInstanceOf(NotFoundException.class);

                verify(accountRepository, times(1)).findById(any(Long.class));
                verify(accountRepository, never()).save(any(Account.class));
            }
        }

        @Nested
        @DisplayName("delete 메소드는")
        class Describe_delete {

            @Test
            @DisplayName("NotFoundException 예외를 던진다")
            void it_returns_notFoundException() {
                assertThatThrownBy(() -> accountService.delete(NOT_EXISTED_ID, NOT_EXISTED_ID))
                        .isExactlyInstanceOf(NotFoundException.class);

                verify(accountRepository, times(1)).findById(any(Long.class));
                verify(accountRepository, never()).delete(any(Account.class));
            }
        }
    }
}
