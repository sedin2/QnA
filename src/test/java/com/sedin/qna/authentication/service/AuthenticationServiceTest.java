package com.sedin.qna.authentication.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.account.model.AccountDto;
import com.sedin.qna.account.model.Role;
import com.sedin.qna.account.repository.AccountRepository;
import com.sedin.qna.authentication.model.AuthenticationDto;
import com.sedin.qna.common.exception.NotFoundException;
import com.sedin.qna.common.exception.PasswordIncorrectException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationService Unit Test")
class AuthenticationServiceTest {

    private final String EMAIL = "cafe@mocha.com";
    private final String UNREGISTERED_EMAIL = "noob@email.com";
    private final String PASSWORD = "password";
    private final String INCORRECT_PASSWORD = "incorrect";
    private final String ACCESS_TOKEN = "header.payload.verify-signature";

    @MockBean
    private JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);

    @MockBean
    private PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

    @MockBean
    private AccountRepository accountRepository = mock(AccountRepository.class);

    private AuthenticationService authenticationService;

    private Account account;

    @BeforeEach
    void prepare() {
        authenticationService = new AuthenticationService(passwordEncoder, jwtTokenProvider, accountRepository);

        account = Account.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    @DisplayName("유효한 로그인 정보가 주어지면 AccessToken 반환한다")
    void When_Login_Expect_Returns_Response_With_AccessToken() {
        // given
        AccountDto.Login login = AccountDto.Login.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(account.getRole().name()));

        given(accountRepository.findByEmail(EMAIL)).willReturn(Optional.of(account));
        given(passwordEncoder.matches(eq(PASSWORD), any(String.class))).willReturn(true);
        given(jwtTokenProvider.encode(account.getEmail(), authorities)).willReturn(ACCESS_TOKEN);

        // when
        AuthenticationDto.Response response = authenticationService.authenticate(login);
        String accessToken = response.getAccessToken();

        // then
        assertThat(response).isNotNull();
        assertThat(accessToken).isEqualTo(ACCESS_TOKEN);

        verify(accountRepository).findByEmail(anyString());
        verify(passwordEncoder).matches(eq(PASSWORD), anyString());
        verify(jwtTokenProvider).encode(account.getEmail(), authorities);
    }

    @Test
    @DisplayName("등록되지 않은 이메일이 주어지면 '리소스를 찾을 수 없음' 예외를 던진다")
    void When_Login_With_Unregistered_Email_Expect_Throw_Not_Found_Exception() {
        // given
        AccountDto.Login loginWithUnregisteredEmail = AccountDto.Login.builder()
                .email(UNREGISTERED_EMAIL)
                .password(PASSWORD)
                .build();

        given(accountRepository.findByEmail(UNREGISTERED_EMAIL)).willThrow(new NotFoundException(UNREGISTERED_EMAIL));

        // when & then
        assertThatThrownBy(() -> authenticationService.authenticate(loginWithUnregisteredEmail))
                .isExactlyInstanceOf(NotFoundException.class);

        verify(accountRepository).findByEmail(UNREGISTERED_EMAIL);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtTokenProvider, never()).encode(any(), any());
    }

    @Test
    @DisplayName("틀린 비밀번호가 주어지면 '패스워드가 틀림' 예외를 던진다")
    void When_Login_With_Incorrect_Password_Expect_Throw_Password_Incorrect_Exception() {
        // given
        AccountDto.Login loginWithIncorrectPassword = AccountDto.Login.builder()
                .email(EMAIL)
                .password(INCORRECT_PASSWORD)
                .build();

        given(accountRepository.findByEmail(EMAIL)).willReturn(Optional.of(account));

        // when & then
        assertThatThrownBy(() -> authenticationService.authenticate(loginWithIncorrectPassword))
                .isExactlyInstanceOf(PasswordIncorrectException.class);

        verify(accountRepository).findByEmail(EMAIL);
        verify(passwordEncoder).matches(anyString(), anyString());
        verify(jwtTokenProvider, never()).encode(anyString(), any());
    }
}
