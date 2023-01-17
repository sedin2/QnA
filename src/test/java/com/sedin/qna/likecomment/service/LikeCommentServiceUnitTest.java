package com.sedin.qna.likecomment.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.account.model.Role;
import com.sedin.qna.account.repository.AccountRepository;
import com.sedin.qna.article.model.Article;
import com.sedin.qna.comment.model.Comment;
import com.sedin.qna.comment.repository.CommentRepository;
import com.sedin.qna.common.exception.DuplicatedException;
import com.sedin.qna.common.exception.NotFoundException;
import com.sedin.qna.common.response.ApiResponseCode;
import com.sedin.qna.common.response.ApiResponseDto;
import com.sedin.qna.likecomment.model.LikeComment;
import com.sedin.qna.likecomment.repository.LikeCommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LikeCommentServiceUnitTest {

    private static final String EMAIL = "cafe@mocha.com";
    private static final String NAME = "mocha";
    private static final String TITLE = "article title";
    private static final String CONTENT = "article content";
    private static final Long ACCOUNT_ID = 1L;
    private static final Long COMMENT_ID = 1L;

    private LikeCommentService likeCommentService;

    @MockBean
    private AccountRepository accountRepository = mock(AccountRepository.class);

    @MockBean
    private CommentRepository commentRepository = mock(CommentRepository.class);

    @MockBean
    private LikeCommentRepository likeCommentRepository = mock(LikeCommentRepository.class);

    private Account account;
    private Article article;
    private Comment comment;

    @BeforeEach
    void setUp() {
        likeCommentService = new LikeCommentServiceImpl(accountRepository, commentRepository, likeCommentRepository);

        account = Account.builder()
                .id(ACCOUNT_ID)
                .email(EMAIL)
                .name(NAME)
                .role(Role.USER)
                .build();

        article = Article.builder()
                .id(COMMENT_ID)
                .title(TITLE)
                .content(CONTENT)
                .author(account.getName())
                .account(account)
                .build();

        comment = Comment.builder()
                .content(CONTENT)
                .author(account.getName())
                .account(account)
                .article(article)
                .build();
    }

    @Test
    @DisplayName("계정, 댓글은 존재하고 좋아요는 존재하지 않을 때 - 댓글 좋아요 생성 시 - 등록한다")
    void createLikeCommentWithSuccess(){
        // given
        when(accountRepository.findByEmail(EMAIL)).thenReturn(Optional.of(account));
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(likeCommentRepository.existsByAccountAndComment(account, comment))
                .thenReturn(false);

        // when
        ApiResponseDto<String> response = likeCommentService.create(EMAIL, COMMENT_ID);

        // then
        assertThat(response.getCode()).isSameAs(ApiResponseCode.OK);
    }

    @Test
    @DisplayName("이미 좋아요가 존재할 때 - 댓글 좋아요 생성 시 - 중복예외를 던진다")
    void throwDuplicatedExceptionWithAlreadyExistLikeComment(){
        // given
        when(accountRepository.findByEmail(EMAIL)).thenReturn(Optional.of(account));
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(likeCommentRepository.existsByAccountAndComment(account, comment))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> likeCommentService.create(EMAIL, COMMENT_ID))
                .isInstanceOf(DuplicatedException.class);
    }

    @Test
    @DisplayName("계정, 댓글, 댓글 좋아요가 존재 할 때 - 댓글 좋아요 삭제 시 - 삭제한다")
    void deleteLikeCommentWithSuccess() {
        // given
        LikeComment likeComment = LikeComment.builder()
                .account(account)
                .comment(comment)
                .build();

        when(accountRepository.findByEmail(EMAIL)).thenReturn(Optional.of(account));
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(likeCommentRepository.findByAccountAndComment(account, comment)).thenReturn(Optional.of(likeComment));

        // when
        ApiResponseDto<String> response = likeCommentService.delete(EMAIL, COMMENT_ID);

        // then
        assertThat(response.getCode()).isSameAs(ApiResponseCode.OK);
    }

    @Test
    @DisplayName("댓글 좋아요가 존재하지 않을 때 - 댓글 좋아요 삭제 시 - NotFoundException 예외를 던진다")
    void throwNotFoundExceptionWithNotExistLikeComment() {
        // given
        when(likeCommentRepository.findByAccountAndComment(account, comment)).thenThrow(NotFoundException.class);

        // when & then
        assertThatThrownBy(() -> likeCommentService.delete(EMAIL, COMMENT_ID))
                .isInstanceOf(NotFoundException.class);
    }
}
