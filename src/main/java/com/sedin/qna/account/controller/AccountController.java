package com.sedin.qna.account.controller;

import com.sedin.qna.account.model.AccountDto;
import com.sedin.qna.account.service.AccountService;
import com.sedin.qna.network.ApiResponseDto;
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
    public ApiResponseDto<AccountDto.ResponseOne> signUp(@RequestBody @Valid AccountDto.Create create) {
        return ApiResponseDto.OK(new AccountDto.ResponseOne(accountService.signUp(create)));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<AccountDto.ResponseOne> update(@PathVariable Long id,
                                                         @RequestBody @Valid AccountDto.Update update) {
        return ApiResponseDto.OK(new AccountDto.ResponseOne(accountService.update(id, update)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<String> delete(@PathVariable Long id) {
        accountService.delete(id);
        return ApiResponseDto.DEFAULT_OK;
    }
}
