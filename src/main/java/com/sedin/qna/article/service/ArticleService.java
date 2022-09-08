package com.sedin.qna.article.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.article.model.ArticleDto;

import java.util.List;

public interface ArticleService {

    public ArticleDto.ResponseDetail create(Account account, ArticleDto.Create create);

    public List<ArticleDto.Response> findAll();

    public ArticleDto.ResponseDetail findById(Long id);

    public ArticleDto.ResponseDetail update(Account account, Long id, ArticleDto.Update update);

    public void delete(Account account, Long id);
}
