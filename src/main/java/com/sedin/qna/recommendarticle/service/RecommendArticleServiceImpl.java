package com.sedin.qna.recommendarticle.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.account.repository.AccountRepository;
import com.sedin.qna.article.model.Article;
import com.sedin.qna.article.repository.ArticleRepository;
import com.sedin.qna.common.exception.DuplicatedException;
import com.sedin.qna.common.exception.NotFoundException;
import com.sedin.qna.common.response.ApiResponseDto;
import com.sedin.qna.recommendarticle.model.RecommendArticle;
import com.sedin.qna.recommendarticle.repository.RecommendArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RecommendArticleServiceImpl implements RecommendArticleService {

    private final AccountRepository accountRepository;
    private final ArticleRepository articleRepository;
    private final RecommendArticleRepository recommendArticleRepository;

    @Override
    public ApiResponseDto<String> createRecommendArticle(String email, Long articleId) {
        Account account = findAccount(email);
        Article article = findArticle(articleId);

        if (recommendArticleRepository.existsByAccountAndArticle(account, article)) {
            throw new DuplicatedException("Already registered resource");
        }

        RecommendArticle recommendArticle = RecommendArticle.builder()
                .account(account)
                .article(article)
                .build();

        recommendArticleRepository.save(recommendArticle);
        return ApiResponseDto.DEFAULT_OK;
    }

    @Override
    public ApiResponseDto<String> deleteRecommendArticle(String email, Long articleId) {
        Account account = findAccount(email);
        Article article = findArticle(articleId);
        RecommendArticle recommendArticle = findRecommendArticle(account, article);
        recommendArticleRepository.delete(recommendArticle);

        return ApiResponseDto.DEFAULT_OK;
    }

    private Account findAccount(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(email));
    }

    private Article findArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException(articleId.toString()));
    }

    private RecommendArticle findRecommendArticle(Account account, Article article) {
        return recommendArticleRepository.findByAccountAndArticle(account, article)
                .orElseThrow(() -> new NotFoundException("추천을 찾을 수 없음"));
    }
}
