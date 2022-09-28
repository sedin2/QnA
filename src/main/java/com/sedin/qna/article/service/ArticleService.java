package com.sedin.qna.article.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.article.model.ArticleDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticleService {

    ArticleDto.ResponseChange create(Account account, ArticleDto.Create create);

    List<ArticleDto.ResponseAll> findAll(Pageable pageable);

    ArticleDto.ResponseDetail findById(Long id);

    ArticleDto.ResponseChange update(Account account, Long id, ArticleDto.Update update);

    void delete(Account account, Long id);
}
