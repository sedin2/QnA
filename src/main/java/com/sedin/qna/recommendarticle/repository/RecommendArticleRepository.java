package com.sedin.qna.recommendarticle.repository;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.article.model.Article;
import com.sedin.qna.recommendarticle.model.RecommendArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecommendArticleRepository extends JpaRepository<RecommendArticle, Long> {

    boolean existsByAccountAndArticle(Account account, Article article);

    Optional<RecommendArticle> findByAccountAndArticle(Account account, Article article);
}
