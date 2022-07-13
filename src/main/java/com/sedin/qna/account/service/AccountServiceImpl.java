package com.sedin.qna.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sedin.qna.account.model.Account;
import com.sedin.qna.account.model.dto.AccountSignUpDto;
import com.sedin.qna.account.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final ObjectMapper mapper;
    private final AccountRepository accountRepository;

    public AccountServiceImpl(ObjectMapper mapper, AccountRepository accountRepository) {
        this.mapper = mapper;
        this.accountRepository = accountRepository;
    }

    @Override
    public String signUp(AccountSignUpDto account) {
        String message = "SIGN UP SUCCESS";
        Account newAccount = mapper.convertValue(account, Account.class);
        accountRepository.save(newAccount);

        return message;
    }
}
