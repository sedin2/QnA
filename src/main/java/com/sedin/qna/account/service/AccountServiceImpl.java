package com.sedin.qna.account.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.account.model.AccountDto;
import com.sedin.qna.account.repository.AccountRepository;
import com.sedin.qna.exception.DuplicatedException;
import com.sedin.qna.exception.NotFoundException;
import com.sedin.qna.exception.PermissionToAccessException;
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
            throw new DuplicatedException(create.getLoginId());
        }

        if (accountRepository.existsByEmail(create.getEmail())) {
            throw new DuplicatedException(create.getEmail());
        }

        create.setEncodingPassword(passwordEncoder.encode(create.getPassword()));
        Account target = create.toEntity();
        Account newAccount = accountRepository.save(target);

        return AccountDto.Response.of(newAccount);
    }

    @Override
    @Transactional
    public AccountDto.Response update(Long accountId, Long id, AccountDto.Update update) {
        checkPermissionToAccess(accountId, id);

        Account updated = accountRepository.findById(id)
                .map(update::complete)
                .orElseThrow(() -> new NotFoundException(id.toString()));

        return AccountDto.Response.of(updated);
    }

    @Override
    public void delete(Long accountId, Long id) {
        checkPermissionToAccess(accountId, id);

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id.toString()));

        accountRepository.delete(account);
    }

    private static void checkPermissionToAccess(Long accountId, Long id) {
        if (!accountId.equals(id)) {
            throw new PermissionToAccessException();
        }
    }
}
