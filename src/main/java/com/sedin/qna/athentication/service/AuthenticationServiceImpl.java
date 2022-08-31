package com.sedin.qna.athentication.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.account.model.AccountDto;
import com.sedin.qna.account.repository.AccountRepository;
import com.sedin.qna.athentication.model.AuthenticationDto;
import com.sedin.qna.exception.NotFoundException;
import com.sedin.qna.exception.PasswordIncorrectException;
import com.sedin.qna.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    public AuthenticationServiceImpl(JwtUtil jwtUtil, PasswordEncoder passwordEncoder, AccountRepository accountRepository) {
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
    public Long decodeAccessToken(String accessToken) {
        return Long.valueOf(jwtUtil.decode(accessToken).get("accountId").toString());
    }

    private boolean isUnauthenticated(String rawPassword, String encodedPassword) {
        return !passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
