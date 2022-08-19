package com.sedin.qna.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sedin.qna.account.model.Account;
import com.sedin.qna.account.model.request.AccountLoginDto;
import com.sedin.qna.account.model.request.AccountSignUpDto;
import com.sedin.qna.account.model.request.AccountUpdateDto;
import com.sedin.qna.account.model.response.AccountApiResponse;
import com.sedin.qna.account.repository.AccountRepository;
import com.sedin.qna.error.DuplicatedException;
import com.sedin.qna.error.NotFoundException;
import com.sedin.qna.network.Header;
import com.sedin.qna.util.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final ObjectMapper mapper;
    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;

    public AccountServiceImpl(ObjectMapper mapper, JwtUtil jwtUtil, AccountRepository accountRepository) {
        this.mapper = mapper;
        this.jwtUtil = jwtUtil;
        this.accountRepository = accountRepository;
    }

    @Override
    public Header<AccountApiResponse> signUp(AccountSignUpDto account) {
        if (accountRepository.existsByLoginId(account.getLoginId())) {
            throw new DuplicatedException("loginId");
        }

        if (accountRepository.existsByEmail(account.getEmail())) {
            throw new DuplicatedException("email");
        }

        Account newAccount = mapper.convertValue(account, Account.class);
        accountRepository.save(newAccount);

        return response(newAccount);
    }

    @Override
    public Header<String> login(AccountLoginDto account) {
        Account loginAccount = accountRepository.findByLoginIdAndPassword(account.getLoginId(), account.getPassword())
                .orElseThrow(NoSuchElementException::new);

        String jwtToken = jwtUtil.encode(loginAccount.getId());

        return response(jwtToken);
    }

    @Override
    public Header<AccountApiResponse> update(Long id, AccountUpdateDto account) {
        Account updateAccount = findAccount(id);
        updateAccount.updatePasswordAndEmail(account.getNewPassword(), account.getEmail());

        return response(updateAccount);
    }

    @Override
    public void delete(Long id) {
        Account account = findAccount(id);

        accountRepository.delete(account);
    }

    private Account findAccount(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new NotFoundException("accountId"));
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
