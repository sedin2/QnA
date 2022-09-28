package com.sedin.qna.article.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.article.model.Article;
import com.sedin.qna.article.model.ArticleDto;
import com.sedin.qna.article.repository.ArticleRepository;
import com.sedin.qna.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    private static final String CONTENT = "content";
    private static final String NAME = "sejin";
    private static final String PREFIX = "prefix";
    private static final String TITLE = "title";

    private ArticleService articleService;

    @MockBean
    private ArticleRepository articleRepository = mock(ArticleRepository.class);

    private Account authenticatedAccount;

    @BeforeEach
    void prepare() {
        articleService = new ArticleServiceImpl(articleRepository);

        authenticatedAccount = Account.builder()
                .id(1L)
                .name(NAME)
                .build();
    }

    @Test
    void When_Create_Expect_New_Article() {

        // given
        ArticleDto.Create create = ArticleDto.Create.builder()
                .title(TITLE)
                .content(CONTENT)
                .build();

        Article article = Article.builder()
                .id(1L)
                .title(TITLE)
                .content(CONTENT)
                .author(NAME)
                .account(authenticatedAccount)
                .build();

        given(articleRepository.save(any())).willReturn(article);

        // when
        ArticleDto.ResponseChange response = articleService.create(authenticatedAccount, create);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo(TITLE);
        assertThat(response.getAuthor()).isEqualTo(NAME);

        verify(articleRepository, times(1)).save(any());
    }

    @Test
    void When_Find_All_Expect_Article_List() {

        // given
        List<Article> articles = List.of(
                Article.builder()
                        .id(2L)
                        .title(TITLE)
                        .content(CONTENT)
                        .account(authenticatedAccount)
                        .build(),
                Article.builder()
                        .id(1L)
                        .title(TITLE)
                        .content(CONTENT)
                        .account(authenticatedAccount)
                        .build()
        );

        Page<Article> pagedArticles = new PageImpl<>(articles);
        Pageable pageable = PageRequest.of(0, 10);

        given(articleRepository.findAll(pageable)).willReturn(pagedArticles);

        // when
        List<ArticleDto.ResponseAll> responseList = articleService.findAll(pageable);

        // then
        assertThat(responseList).hasSize(2);
    }

    @Test
    void When_Find_By_Id_Expect_Article_Detail() {

        // given
        Article article = Article.builder()
                .id(1L)
                .title(TITLE)
                .content(CONTENT)
                .author(NAME)
                .account(authenticatedAccount)
                .build();

        given(articleRepository.findById(anyLong())).willReturn(Optional.of(article));

        // when
        ArticleDto.ResponseDetail response = articleService.findById(1L);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getAuthor()).isEqualTo(NAME);
    }

    @Test
    void When_Find_By_Not_Existed_Id_Expect_Not_Found_Exception() {

        // given
        given(articleRepository.findById(0L))
                .willThrow(NotFoundException.class);

        // when & then
        assertThatThrownBy(() -> articleService.findById(0L))
                .isExactlyInstanceOf(NotFoundException.class);
    }

    @Test
    void When_Update_Expect_Updated_Article() {

        // given
        Article article = Article.builder()
                .id(1L)
                .title(TITLE)
                .content(CONTENT)
                .account(authenticatedAccount)
                .build();

        ArticleDto.Update update = ArticleDto.Update.builder()
                .title(PREFIX + TITLE)
                .content(PREFIX + CONTENT)
                .build();

        given(articleRepository.findById(1L)).willReturn(Optional.of(article));

        // when
        ArticleDto.ResponseChange response = articleService.update(authenticatedAccount, 1L, update);

        // then
        assertThat(response.getTitle()).isEqualTo(PREFIX + TITLE);
        assertThat(response.getContent()).isEqualTo(PREFIX + CONTENT);

        verify(articleRepository, times(1)).findById(1L);
    }

    @Test
    void When_Delete_Expect_Deleted_Article() {

        // given
        Article article = Article.builder()
                .id(1L)
                .title(TITLE)
                .content(CONTENT)
                .account(authenticatedAccount)
                .build();

        given(articleRepository.findById(1L)).willReturn(Optional.of(article));
        doNothing().when(articleRepository).delete(article);

        // when
        articleService.delete(authenticatedAccount, 1L);

        // then
        given(articleRepository.findById(1L)).willThrow(NotFoundException.class);
        assertThatThrownBy(() -> articleService.findById(1L))
                .isExactlyInstanceOf(NotFoundException.class);

        verify(articleRepository, times(1)).delete(any());
    }
}
