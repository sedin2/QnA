package com.sedin.qna.account.service;

import com.sedin.qna.account.model.dto.AccountSignUpDto;

public interface AccountService {

    String signUp(AccountSignUpDto account);
}
