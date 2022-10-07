package com.sedin.qna.article.repository;

import com.sedin.qna.article.model.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query("select a from Article a left join fetch a.comments")
    List<Article> findAllFetchJoin(Pageable pageable);
}
