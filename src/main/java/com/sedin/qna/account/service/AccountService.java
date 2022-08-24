package com.sedin.qna.account.service;

import com.sedin.qna.account.model.AccountDto;

public interface AccountService {

    AccountDto.Response signUp(AccountDto.Create create);

    AccountDto.Response update(Long id, AccountDto.Update update);

    void delete(Long id);
}
