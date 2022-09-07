package com.sedin.qna.article.repository;

import com.sedin.qna.article.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {

}
