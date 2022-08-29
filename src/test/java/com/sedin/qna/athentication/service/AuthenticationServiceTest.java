package com.sedin.qna.athentication.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.account.model.AccountDto;
import com.sedin.qna.account.repository.AccountRepository;
import com.sedin.qna.athentication.model.AuthenticationDto;
import com.sedin.qna.exception.NotFoundException;
import com.sedin.qna.exception.PasswordIncorrectException;
import com.sedin.qna.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    private final String UNREGISTERED_LOGIN_ID = "not register login id";
    private final String LOGIN_ID = "loginId";
    private final String PASSWORD = "password";
    private final String INCORRECT_PASSWORD = "incorrect";
    private final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50SWQiOjF9." +
            "LwF0Ms-3xGGJX9JBIrc7bzGl1gYUAq3R3gesg35BA1w";

    @MockBean
    private JwtUtil jwtUtil = mock(JwtUtil.class);

    @MockBean
    private PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

    @MockBean
    private AccountRepository accountRepository = mock(AccountRepository.class);

    private AuthenticationService authenticationService;

    private Account account;

    @BeforeEach
    void prepare() {
        authenticationService = new AuthenticationServiceImpl(jwtUtil, passwordEncoder, accountRepository);

        account = Account.builder()
                .loginId(LOGIN_ID)
                .password(PASSWORD)
                .build();
    }

    @Test
    @DisplayName("유효한 로그인 정보가 주어지면 AccessToken 반환한다")
    void When_Login_Expect_Returns_Response_With_AccessToken() {
        // given
        AccountDto.Login login = AccountDto.Login.builder()
                .loginId(LOGIN_ID)
                .password(PASSWORD)
                .build();

        given(accountRepository.findByLoginId(any(String.class))).willReturn(Optional.of(account));
        given(passwordEncoder.matches(eq(PASSWORD), any(String.class))).willReturn(true);
        given(jwtUtil.encode(account.getId())).willReturn(ACCESS_TOKEN);

        // when
        AuthenticationDto.Response response = authenticationService.checkValidAuthentication(login);
        String accessToken = response.getAccessToken();

        // then
        assertThat(response).isNotNull();
        assertThat(accessToken).isEqualTo(ACCESS_TOKEN);

        verify(accountRepository, times(1)).findByLoginId(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(jwtUtil, times(1)).encode(any());
    }

    @Test
    @DisplayName("등록되지 않은 로그인 아이디가 주어지면 '리소스를 찾을 수 없음' 예외를 던진다")
    void When_Login_With_Unregistered_LoginId_Expect_Throw_Not_Found_Exception() {
        // given
        AccountDto.Login loginWithUnregisteredLoginId = AccountDto.Login.builder()
                .loginId(UNREGISTERED_LOGIN_ID)
                .password(PASSWORD)
                .build();

        given(accountRepository.findByLoginId(anyString())).willThrow(new NotFoundException("loginId"));

        // when & then
        assertThatThrownBy(() -> authenticationService.checkValidAuthentication(loginWithUnregisteredLoginId))
                .isExactlyInstanceOf(NotFoundException.class);

        verify(accountRepository, times(1)).findByLoginId(anyString());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).encode(anyLong());
    }

    @Test
    @DisplayName("틀린 비밀번호가 주어지면 '패스워드가 틀림' 예외를 던진다")
    void When_Login_With_Incorrect_Password_Expect_Throw_Password_Incorrect_Exception() {
        // given
        AccountDto.Login loginWithIncorrectPassword = AccountDto.Login.builder()
                .loginId(LOGIN_ID)
                .password(INCORRECT_PASSWORD)
                .build();

        given(accountRepository.findByLoginId(any(String.class))).willReturn(Optional.of(account));
        given(passwordEncoder.matches(eq(INCORRECT_PASSWORD), any(String.class))).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authenticationService.checkValidAuthentication(loginWithIncorrectPassword))
                .isExactlyInstanceOf(PasswordIncorrectException.class);

        verify(accountRepository, times(1)).findByLoginId(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(jwtUtil, never()).encode(anyLong());
    }
}
