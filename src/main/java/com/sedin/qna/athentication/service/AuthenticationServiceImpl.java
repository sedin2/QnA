package com.sedin.qna.athentication.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.account.model.AccountDto;
import com.sedin.qna.account.repository.AccountRepository;
import com.sedin.qna.athentication.model.AuthenticationDto;
import com.sedin.qna.exception.InvalidTokenException;
import com.sedin.qna.exception.NotFoundException;
import com.sedin.qna.exception.PasswordIncorrectException;
import com.sedin.qna.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final String BEARER = "Bearer ";

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    public AuthenticationServiceImpl(JwtUtil jwtUtil, PasswordEncoder passwordEncoder,
                                     AccountRepository accountRepository) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
    }

    @Override
    public AuthenticationDto.Response checkValidAuthentication(AccountDto.Login login) {
        Account account = accountRepository.findByLoginId(login.getLoginId())
                .orElseThrow(() -> new NotFoundException(login.getLoginId()));

        if (isUnauthenticated(login.getPassword(), account.getPassword())) {
            throw new PasswordIncorrectException();
        }

        return AuthenticationDto.Response.of(jwtUtil.encode(account.getId()));
    }

    @Override
    public Account decodeAccessToken(String accessToken) {
        Long accountId = Long.parseLong(String.valueOf(jwtUtil.decode(accessToken).get(JwtUtil.ACCOUNT_ID)));
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException(accountId.toString()));
    }

    @Override
    public String getAccessToken(String authorization) {
        if (authorization == null || !authorization.startsWith(BEARER)) {
            throw new InvalidTokenException();
        }

        return authorization.substring(BEARER.length());
    }

    private boolean isUnauthenticated(String rawPassword, String encodedPassword) {
        return !passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
