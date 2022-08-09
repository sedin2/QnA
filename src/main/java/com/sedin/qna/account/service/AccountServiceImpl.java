package com.sedin.qna.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sedin.qna.account.model.Account;
import com.sedin.qna.account.model.dto.AccountLoginDto;
import com.sedin.qna.account.model.dto.AccountSignUpDto;
import com.sedin.qna.account.model.response.AccountApiResponse;
import com.sedin.qna.account.repository.AccountRepository;
import com.sedin.qna.network.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

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
    public Header<AccountApiResponse> signUp(AccountSignUpDto account) {
        Account newAccount = mapper.convertValue(account, Account.class);
        accountRepository.save(newAccount);

        return response(newAccount);
    }

    @Override
    public Header<String> login(AccountLoginDto account) {
        accountRepository.findByLoginIdAndPassword(account.getLoginId(), account.getPassword())
                .orElseThrow(NoSuchElementException::new);

        return response("JWT TOKEN");
    }

    private Header<String> response(String token) {
        return Header.OK(token);
    }

    private Header<AccountApiResponse> response(Account account) {
        AccountApiResponse accountApiResponse = AccountApiResponse.builder()
                .id(account.getId())
                .loginId(account.getLoginId())
                .name(account.getName())
                .bornDate(account.getBornDate())
                .sex(account.getSex())
                .email(account.getEmail())
                .build();

        return Header.OK(accountApiResponse);
    }
}
