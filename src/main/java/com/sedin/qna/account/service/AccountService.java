package com.sedin.qna.account.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.account.model.AccountDto;

public interface AccountService {

    AccountDto.Response signUp(AccountDto.Create create);

    AccountDto.Response update(Account account, Long id, AccountDto.Update update);

    void delete(Account account, Long id);
}
