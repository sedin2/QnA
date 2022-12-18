package com.sedin.qna.authentication.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.account.model.AccountDto;
import com.sedin.qna.account.repository.AccountRepository;
import com.sedin.qna.authentication.model.AuthenticationDto;
import com.sedin.qna.common.exception.PasswordIncorrectException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccountRepository accountRepository;

    public AuthenticationDto.Response authenticate(AccountDto.Login login) {
        Account account = findAccount(login.getEmail());

        if (isUnauthenticated(login.getPassword(), account.getPassword())) {
            throw new PasswordIncorrectException();
        }

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(account.getRole().name()));
        return AuthenticationDto.Response.of(jwtTokenProvider.encode(account.getEmail(), authorities));
    }

    private Account findAccount(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }

    private boolean isUnauthenticated(String rawPassword, String encodedPassword) {
        return !passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
