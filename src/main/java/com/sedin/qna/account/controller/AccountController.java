package com.sedin.qna.account.controller;

import com.sedin.qna.account.model.dto.AccountSignUpDto;
import com.sedin.qna.account.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public String signUp(@RequestBody AccountSignUpDto account) {
        return accountService.signUp(account);
    }
}
