package com.sedin.qna.account.controller;

import com.sedin.qna.account.model.dto.AccountLoginDto;
import com.sedin.qna.account.model.dto.AccountSignUpDto;
import com.sedin.qna.account.model.dto.AccountUpdateDto;
import com.sedin.qna.account.model.response.AccountApiResponse;
import com.sedin.qna.account.service.AccountService;
import com.sedin.qna.network.Header;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Header<AccountApiResponse> signUp(@RequestBody @Valid AccountSignUpDto account) {
        return accountService.signUp(account);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Header<AccountApiResponse> update(@PathVariable Long id, @RequestBody @Valid AccountUpdateDto account) {
        return accountService.update(id, account);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        accountService.delete(id);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public Header<String> login(@RequestBody AccountLoginDto account) {
        return accountService.login(account);
    }
}
