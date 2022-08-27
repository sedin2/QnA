package com.sedin.qna.athentication.controller;

import com.sedin.qna.account.model.AccountDto;
import com.sedin.qna.athentication.model.AuthenticationDto;
import com.sedin.qna.athentication.service.AuthenticationService;
import com.sedin.qna.network.ApiResponseDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ApiResponseDto<AuthenticationDto.Response> login(@RequestBody @Valid AccountDto.Login login) {
        return ApiResponseDto.OK(authenticationService.checkValidAuthentication(login));
    }
}
