package com.sedin.qna.article.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.article.model.ArticleDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ArticleService {

    public ArticleDto.Response create(Account account, ArticleDto.Create create);

    public List<ArticleDto.Response> findAll();

    public ArticleDto.Response findById(Long id);

    public ArticleDto.Response update(Account account, Long id, ArticleDto.Update update);

    public void delete(Account account, Long id);
}
