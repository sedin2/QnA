package com.sedin.qna.account.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.account.model.AccountDto;
import com.sedin.qna.account.repository.AccountRepository;
import com.sedin.qna.common.exception.DuplicatedException;
import com.sedin.qna.common.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    private static final Long ID = 1L;
    private static final String RAW_PASSWORD = "12341234";
    private static final String NEW_PASSWORD = "newPassword";
    private static final String ENCODING_PASSWORD = "{bcrypt}$2a$10$yamrqEu34.7AQN50aInswukKIi4Ir.a.9d2tZ0YzIXgxZhdI4Nhki\n";
    private static final String NAME = "Mocha";
    private static final String NEW_NAME = "CafeMocha";
    private static final String EXISTED_EMAIL = "cafe@mocha.com";
    private static final String NOT_EXISTED_EMAIL = "noob@email.com";

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
                .id(ID)
                .email(EXISTED_EMAIL)
                .password(ENCODING_PASSWORD)
                .name(NAME)
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
                        .email(EXISTED_EMAIL)
                        .password(RAW_PASSWORD)
                        .name(NAME)
                        .build();

                given(accountRepository.save(any(Account.class))).willReturn(account);
                given(passwordEncoder.encode(RAW_PASSWORD)).willReturn(ENCODING_PASSWORD);
            }

            @Test
            @DisplayName("사용자를 등록하고 Account 정보를 담은 응답을 리턴한다")
            void it_returns_response_with_new_account() {
                response = accountService.signUp(createDto);

                assertThat(response.getPassword()).isNotEqualTo(RAW_PASSWORD);
                assertThat(response.getName()).isEqualTo(NAME);

                verify(accountRepository).save(any(Account.class));
                verify(passwordEncoder).encode(RAW_PASSWORD);
            }
        }

        @Nested
        @DisplayName("이미 등록된 이메일이 주어지면")
        class Context_with_duplicated_email_in_accountSignUpDto {

            @BeforeEach
            void prepareDuplicatedEmail() {
                duplicatedCreateDto = AccountDto.Create.builder()
                        .password(RAW_PASSWORD)
                        .name(NAME)
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
    @DisplayName("존재하는 이메일이 주어지면")
    class Context_with_existed_email {

        @Nested
        @DisplayName("update 메소드는")
        class Describe_update {

            @BeforeEach
            void prepareExistedEmail() {
                updateDto = AccountDto.Update.builder()
                        .originalPassword(RAW_PASSWORD)
                        .newPassword(NEW_PASSWORD)
                        .name(NEW_NAME)
                        .build();

                account = Account.builder()
                        .id(ID)
                        .password(NEW_PASSWORD)
                        .email(EXISTED_EMAIL)
                        .name(NEW_NAME)
                        .build();

                given(accountRepository.findByEmail(EXISTED_EMAIL)).willReturn(Optional.ofNullable(account));
                given(passwordEncoder.encode(NEW_PASSWORD)).willReturn(ENCODING_PASSWORD);
            }

            @Test
            @DisplayName("사용자를 수정하고 Account 정보를 담은 응답을 리턴한다")
            void it_returns_response_with_updated_account() {
                response = accountService.update(EXISTED_EMAIL, updateDto);

                assertThat(response.getName()).isEqualTo(NEW_NAME);
                assertThat(response.getPassword()).isNotEqualTo(NEW_PASSWORD);

                verify(passwordEncoder).encode(NEW_PASSWORD);
            }
        }

        @Nested
        @DisplayName("delete 메소드는")
        class Describe_delete {

            @BeforeEach
            void prepareFoundAccount() {
                given(accountRepository.findByEmail(EXISTED_EMAIL)).willReturn(Optional.ofNullable(account));
                doNothing().when(accountRepository).delete(account);
            }

            @Test
            @DisplayName("사용자를 삭제한다")
            void it_deletes_account() {
                accountService.delete(EXISTED_EMAIL);
                verify(accountRepository).delete(any(Account.class));
            }
        }
    }

    @Nested
    @DisplayName("존재하지 않는 이메일이 주어지면")
    class Context_with_not_existed_email {

        @Nested
        @DisplayName("update 메소드는")
        class Describe_update {

            @Test
            @DisplayName("NotFoundException 예외를 던진다")
            void it_returns_notFoundException() {
                assertThatThrownBy(() -> accountService.update(NOT_EXISTED_EMAIL, updateDto))
                        .isExactlyInstanceOf(NotFoundException.class);

                verify(accountRepository, never()).save(any(Account.class));
            }
        }

        @Nested
        @DisplayName("delete 메소드는")
        class Describe_delete {

            @Test
            @DisplayName("NotFoundException 예외를 던진다")
            void it_returns_notFoundException() {
                assertThatThrownBy(() -> accountService.delete(NOT_EXISTED_EMAIL))
                        .isExactlyInstanceOf(NotFoundException.class);

                verify(accountRepository, never()).delete(any(Account.class));
            }
        }
    }
}
