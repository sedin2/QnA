package com.sedin.qna.comment.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.article.model.Article;
import com.sedin.qna.article.repository.ArticleRepository;
import com.sedin.qna.comment.model.Comment;
import com.sedin.qna.comment.model.CommentDto;
import com.sedin.qna.comment.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    private static final Long ARTICLE_ID = 1L;
    private static final String CONTENT = "content";
    private static final String NAME = "sejin";
    private static final String PREFIX = "prefix";
    private static final String TITLE = "title";

    @MockBean
    private ArticleRepository articleRepository = mock(ArticleRepository.class);

    @MockBean
    private CommentRepository commentRepository = mock(CommentRepository.class);

    private CommentService commentService;

    private Account authenticatedAccount;
    private Article article;

    @BeforeEach
    void prepare() {
        commentService = new CommentServiceImpl(articleRepository, commentRepository);

        authenticatedAccount = Account.builder()
                .id(1L)
                .name(NAME)
                .build();

        article = Article.builder()
                .id(ARTICLE_ID)
                .title(TITLE)
                .content(CONTENT)
                .account(authenticatedAccount)
                .build();
    }

    @Test
    void When_Create_Expect_New_Comment() {

        // given
        CommentDto.Create create = CommentDto.Create.builder()
                .content(CONTENT)
                .build();

        Comment comment = Comment.builder()
                .content(CONTENT)
                .article(article)
                .account(authenticatedAccount)
                .build();

        given(articleRepository.findById(anyLong())).willReturn(Optional.of(article));
        given(commentRepository.save(any())).willReturn(comment);

        // when
        CommentDto.Response response = commentService.create(authenticatedAccount, ARTICLE_ID, create);

        // then
        assertThat(response.getContent()).isEqualTo(CONTENT);
        assertThat(response.getAuthor()).isEqualTo(NAME);

        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void When_Find_All_Expect_Comment_List() {

        // given
        List<Comment> comments = List.of(
                Comment.builder()
                        .content(CONTENT)
                        .article(article)
                        .account(authenticatedAccount)
                        .build(),
                Comment.builder()
                        .content(CONTENT)
                        .article(article)
                        .account(authenticatedAccount)
                        .build()
        );

        given(commentRepository.findAllByArticleId(ARTICLE_ID)).willReturn(comments);

        // when
        List<CommentDto.Response> responseList = commentService.findAll(ARTICLE_ID);

        // then
        assertThat(responseList).hasSize(2);
        verify(commentRepository, times(1)).findAllByArticleId(anyLong());
    }
}
