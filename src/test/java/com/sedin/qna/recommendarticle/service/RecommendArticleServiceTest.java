package com.sedin.qna.recommendarticle.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.account.model.Role;
import com.sedin.qna.account.repository.AccountRepository;
import com.sedin.qna.article.model.Article;
import com.sedin.qna.article.repository.ArticleRepository;
import com.sedin.qna.common.exception.DuplicatedException;
import com.sedin.qna.common.exception.NotFoundException;
import com.sedin.qna.common.response.ApiResponseCode;
import com.sedin.qna.common.response.ApiResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class RecommendArticleServiceTest {

    private static final String EMAIL = "cafe@mocha.com";
    private static final String PASSWORD = "coffee";
    private static final String NAME = "mocha";
    private static final String TITLE = "article title";
    private static final String CONTENT = "article content";

    @Autowired
    private EntityManager em;

    @Autowired
    private RecommendArticleService recommendArticleService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ArticleRepository articleRepository;

    private Account account;
    private Article article;

    @BeforeEach
    void setUp() {
        account = Account.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .name(NAME)
                .role(Role.USER)
                .build();

        article = Article.builder()
                .title(TITLE)
                .content(CONTENT)
                .author(account.getName())
                .account(account)
                .build();

        accountRepository.save(account);
        articleRepository.save(article);
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("게시글 추천 생성 시 - 계정, 게시글은 존재하고 추천은 존재 하지 않을 때 - 성공")
    void createRecommendArticleWithSuccess() {
        // when
        ApiResponseDto<String> response = recommendArticleService.createRecommendArticle(account.getEmail(), article.getId());

        // then
        assertThat(response.getCode()).isSameAs(ApiResponseCode.OK);
    }

    @Test
    @DisplayName("게시글 추천 생성 시 - 첫 번째 추천은 성공하고 두 번째 추천은 중복(한 계정당 한 게시글에 추천은 1개만 가능) - 실패")
    void createRecommendArticleWithFail() {
        // given & when
        recommendArticleService.createRecommendArticle(account.getEmail(), article.getId());
        
        // then
        assertThatThrownBy(() -> recommendArticleService.createRecommendArticle(account.getEmail(), article.getId()))
                .isInstanceOf(DuplicatedException.class);
    }

    @Test
    @DisplayName("게시글 추천 삭제 시 - 계정, 게시글, 추천이 모두 존재할 때 - 성공")
    void deleteRecommendArticleWithSuccess() {
        // given
        recommendArticleService.createRecommendArticle(account.getEmail(), article.getId());
        em.flush();
        em.clear();

        // when
        ApiResponseDto<String> response = recommendArticleService.deleteRecommendArticle(account.getEmail(), article.getId());

        // then
        assertThat(response.getCode()).isSameAs(ApiResponseCode.OK);
    }

    @Test
    @DisplayName("게시글 추천 삭제 시 - 추천이 존재 하지 않을 때 - 실패")
    void deleteRecommendArticleWithFail() {
        // when & then
        assertThatThrownBy(() -> recommendArticleService.deleteRecommendArticle(account.getEmail(), article.getId()))
                .isInstanceOf(NotFoundException.class);
    }
}
