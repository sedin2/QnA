package com.sedin.qna.authentication.controller;

import com.sedin.qna.account.model.AccountDto;
import com.sedin.qna.authentication.model.AuthenticationDto;
import com.sedin.qna.authentication.service.AuthenticationService;
import com.sedin.qna.common.response.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/login")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping
    public ApiResponseDto<AuthenticationDto.Response> login(@RequestBody @Valid AccountDto.Login login) {
        return ApiResponseDto.OK(authenticationService.authenticate(login));
    }

}
