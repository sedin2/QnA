package com.sedin.qna.account.controller;

import com.sedin.qna.account.model.AccountDto;
import com.sedin.qna.account.service.AccountService;
import com.sedin.qna.common.response.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponseDto<AccountDto.ResponseOne> signUp(@RequestBody @Valid AccountDto.Create create) {
        return ApiResponseDto.OK(new AccountDto.ResponseOne(accountService.signUp(create)));
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<AccountDto.ResponseOne> update(@AuthenticationPrincipal String email,
                                                         @RequestBody @Valid AccountDto.Update update) {
        return ApiResponseDto.OK(new AccountDto.ResponseOne(accountService.update(email, update)));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<String> delete(@AuthenticationPrincipal String email) {
        accountService.delete(email);
        return ApiResponseDto.DEFAULT_OK;
    }

}
