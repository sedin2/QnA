package com.sedin.qna.article.service;

import com.sedin.qna.article.model.ArticleDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticleService {

    ArticleDto.ResponseChange create(String email, ArticleDto.Create create);

    List<ArticleDto.ResponseAll> findAll(Pageable pageable);

    ArticleDto.ResponseDetail findById(Long id);

    ArticleDto.ResponseChange update(String email, Long id, ArticleDto.Update update);

    void delete(String email, Long id);

    List<ArticleDto.ResponseDetail> findAllWithComments(Pageable pageable);

}
