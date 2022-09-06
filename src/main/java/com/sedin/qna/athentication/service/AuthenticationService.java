package com.sedin.qna.athentication.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.account.model.AccountDto;
import com.sedin.qna.athentication.model.AuthenticationDto;

public interface AuthenticationService {

    AuthenticationDto.Response checkValidAuthentication(AccountDto.Login login);

    Account decodeAccessToken(String accessToken);

    String getAccessToken(String authorization);
}
