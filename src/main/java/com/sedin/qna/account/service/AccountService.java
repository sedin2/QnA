package com.sedin.qna.account.service;

import com.sedin.qna.account.model.request.AccountLoginDto;
import com.sedin.qna.account.model.request.AccountSignUpDto;
import com.sedin.qna.account.model.request.AccountUpdateDto;
import com.sedin.qna.account.model.response.AccountApiResponse;
import com.sedin.qna.network.Header;

public interface AccountService {

    Header<AccountApiResponse> signUp(AccountSignUpDto account);

    Header<String> login(AccountLoginDto account);

    Header<AccountApiResponse> update(Long id, AccountUpdateDto account);

    void delete(Long id);
}
