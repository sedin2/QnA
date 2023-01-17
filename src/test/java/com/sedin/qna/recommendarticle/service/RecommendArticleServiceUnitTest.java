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
import com.sedin.qna.recommendarticle.model.RecommendArticle;
import com.sedin.qna.recommendarticle.repository.RecommendArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class RecommendArticleServiceUnitTest {

    private static final String EMAIL = "cafe@mocha.com";
    private static final String NAME = "mocha";
    private static final String TITLE = "article title";
    private static final String CONTENT = "article content";
    private static final Long ACCOUNT_ID = 1L;
    private static final Long ARTICLE_ID = 1L;

    private RecommendArticleService recommendArticleService;

    @MockBean
    private AccountRepository accountRepository = mock(AccountRepository.class);

    @MockBean
    private ArticleRepository articleRepository = mock(ArticleRepository.class);

    @MockBean
    private RecommendArticleRepository recommendArticleRepository = mock(RecommendArticleRepository.class);

    private Account account;
    private Article article;

    @BeforeEach
    void setUp() {
        recommendArticleService = new RecommendArticleServiceImpl(accountRepository,
                articleRepository, recommendArticleRepository);

        account = Account.builder()
                .id(ACCOUNT_ID)
                .email(EMAIL)
                .name(NAME)
                .role(Role.USER)
                .build();

        article = Article.builder()
                .id(ARTICLE_ID)
                .title(TITLE)
                .content(CONTENT)
                .author(account.getName())
                .account(account)
                .build();
    }

    @Test
    @DisplayName("계정, 게시글은 존재하고 추천은 존재하지 않을 때 - 게시글 추천 생성 시 - 등록한다")
    void createRecommendArticleWithSuccess(){
        // given
        given(accountRepository.findByEmail(EMAIL)).willReturn(Optional.of(account));
        given(articleRepository.findById(ARTICLE_ID)).willReturn(Optional.of(article));
        given(recommendArticleRepository.existsByAccountAndArticle(account, article))
                .willReturn(false);

        // when
        ApiResponseDto<String> response = recommendArticleService.createRecommendArticle(EMAIL, ARTICLE_ID);

        // then
        assertThat(response.getCode()).isSameAs(ApiResponseCode.OK);
        then(recommendArticleRepository).should().save(any(RecommendArticle.class));
    }

    @Test
    @DisplayName("이미 추천이 존재할 때 - 게시글 추천 생성 시 - 중복예외를 던진다")
    void throwDuplicatedExceptionWithAlreadyExistRecommendArticle(){
        // given
        given(accountRepository.findByEmail(EMAIL)).willReturn(Optional.of(account));
        given(articleRepository.findById(ARTICLE_ID)).willReturn(Optional.of(article));
        given(recommendArticleRepository.existsByAccountAndArticle(account, article))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> recommendArticleService.createRecommendArticle(EMAIL, ARTICLE_ID))
                .isInstanceOf(DuplicatedException.class);
        then(recommendArticleRepository).should(never()).save(any(RecommendArticle.class));
    }

    @Test
    @DisplayName("계정, 게시글, 게시글 추천이 존재 할 때 - 게시글 추천 삭제 시 - 삭제한다")
    void deleteRecommendArticleWithSuccess() {
        // given
        RecommendArticle recommendArticle = RecommendArticle.builder()
                .account(account)
                .article(article)
                .build();

        given(accountRepository.findByEmail(EMAIL)).willReturn(Optional.of(account));
        given(articleRepository.findById(ARTICLE_ID)).willReturn(Optional.of(article));
        given(recommendArticleRepository.findByAccountAndArticle(account, article))
                .willReturn(Optional.of(recommendArticle));

        // when
        ApiResponseDto<String> response = recommendArticleService.deleteRecommendArticle(EMAIL, ARTICLE_ID);

        // then
        assertThat(response.getCode()).isSameAs(ApiResponseCode.OK);
        then(recommendArticleRepository).should().delete(any(RecommendArticle.class));
    }
    
    @Test
    @DisplayName("게시글 추천이 존재하지 않을 때 - 게시글 추천 삭제 시 - NotFoundException 예외를 던진다")
    void throwNotFoundExceptionWithNotExistRecommendArticle() {
        // given
        given(recommendArticleRepository.findByAccountAndArticle(account, article)).willThrow(NotFoundException.class);

        // when & then
        assertThatThrownBy(() -> recommendArticleService.deleteRecommendArticle(EMAIL, ARTICLE_ID))
                .isInstanceOf(NotFoundException.class);
        then(recommendArticleRepository).should(never()).delete(any(RecommendArticle.class));
    }
}
