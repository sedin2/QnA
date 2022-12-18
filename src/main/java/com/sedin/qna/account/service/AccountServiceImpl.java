package com.sedin.qna.account.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.account.model.AccountDto;
import com.sedin.qna.account.repository.AccountRepository;
import com.sedin.qna.common.exception.DuplicatedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    public AccountServiceImpl(PasswordEncoder passwordEncoder, AccountRepository accountRepository) {
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountDto.Response signUp(AccountDto.Create create) {
        if (accountRepository.existsByEmail(create.getEmail())) {
            throw new DuplicatedException(create.getEmail());
        }

        create.setEncodingPassword(passwordEncoder.encode(create.getPassword()));
        Account target = create.toEntity();
        Account newAccount = accountRepository.save(target);

        return AccountDto.Response.of(newAccount);
    }

    @Override
    public AccountDto.Response update(String email, AccountDto.Update update) {
        Account account = findAccount(email);
        String encodedPassword = passwordEncoder.encode(update.getNewPassword());
        account.updatePasswordAndEmail(encodedPassword, update.getEmail());
        return AccountDto.Response.of(account);
    }

    @Override
    public void delete(String email) {
        Account account = findAccount(email);
        accountRepository.delete(account);
    }

    private Account findAccount(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }

}
