package com.sedin.qna.article.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.article.model.ArticleDto;

import java.util.List;

public interface ArticleService {

    ArticleDto.ResponseDetail create(Account account, ArticleDto.Create create);

    List<ArticleDto.Response> findAll();

    ArticleDto.ResponseDetail findById(Long id);

    ArticleDto.ResponseDetail update(Account account, Long id, ArticleDto.Update update);

    void delete(Account account, Long id);
}
