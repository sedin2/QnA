package com.sedin.qna.account.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.account.model.AccountDto;
import com.sedin.qna.account.repository.AccountRepository;
import com.sedin.qna.exception.DuplicatedException;
import com.sedin.qna.exception.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountServiceImpl implements AccountService {

    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    public AccountServiceImpl(PasswordEncoder passwordEncoder, AccountRepository accountRepository) {
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountDto.Response signUp(AccountDto.Create create) {
        if (accountRepository.existsByLoginId(create.getLoginId())) {
            throw new DuplicatedException("loginId");
        }

        if (accountRepository.existsByEmail(create.getEmail())) {
            throw new DuplicatedException("email");
        }

        create.setEncodingPassword(passwordEncoder.encode(create.getPassword()));
        Account target = create.toEntity();
        Account newAccount = accountRepository.save(target);

        return AccountDto.Response.of(newAccount);
    }

    @Override
    @Transactional
    public AccountDto.Response update(Long id, AccountDto.Update update) {
        Account updated = accountRepository.findById(id)
                .map(update::complete)
                .orElseThrow(() -> new NotFoundException("accountId"));

        return AccountDto.Response.of(updated);
    }

    @Override
    public void delete(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("accountId"));

        accountRepository.delete(account);
    }
}
