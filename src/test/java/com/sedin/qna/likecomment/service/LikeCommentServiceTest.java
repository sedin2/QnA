package com.sedin.qna.likecomment.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.account.model.Role;
import com.sedin.qna.account.repository.AccountRepository;
import com.sedin.qna.article.model.Article;
import com.sedin.qna.article.repository.ArticleRepository;
import com.sedin.qna.comment.model.Comment;
import com.sedin.qna.comment.repository.CommentRepository;
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
class LikeCommentServiceTest {

    private static final String EMAIL = "cafe@mocha.com";
    private static final String PASSWORD = "coffee";
    private static final String NAME = "mocha";
    private static final String TITLE = "article title";
    private static final String CONTENT = "content";

    @Autowired
    private EntityManager em;

    @Autowired
    private LikeCommentService likeCommentService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;

    private Account account;
    private Article article;
    private Comment comment;

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

        comment = Comment.builder()
                .content(CONTENT)
                .author(account.getName())
                .account(account)
                .article(article)
                .build();

        accountRepository.save(account);
        articleRepository.save(article);
        commentRepository.save(comment);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("댓글 좋아요 생성 시 - 계정, 댓글은 존재하고 좋아요는 존재 하지 않을 때 - 성공")
    void createLikeCommentWithSuccess() {
        // given & when
        ApiResponseDto<String> responseDto = likeCommentService.create(account.getEmail(), comment.getId());

        // then
        assertThat(responseDto.getCode()).isEqualTo(ApiResponseCode.OK);
    }

    @Test
    @DisplayName("댓글 좋아요 생성 시 - 첫 번째 좋아요는 성공하고 두 번째 좋아요는 중복(한 계정당 한 댓글에 좋아요는 1개만 가능) - 실패")
    void createLikeCommentWithFail() {
        // given & when
        likeCommentService.create(account.getEmail(), comment.getId());

        // then
        assertThatThrownBy(() -> likeCommentService.create(account.getEmail(), comment.getId()))
                .isExactlyInstanceOf(DuplicatedException.class);
    }

    @Test
    @DisplayName("댓글 좋아요 삭제 시 - 계정, 게시글, 좋아요가 모두 존재할 때 - 성공")
    void deleteLikeCommentWithSuccess() {
        // given
        likeCommentService.create(account.getEmail(), comment.getId());
        em.flush();
        em.clear();

        // when
        ApiResponseDto<String> response = likeCommentService.delete(account.getEmail(), comment.getId());

        // then
        assertThat(response.getCode()).isSameAs(ApiResponseCode.OK);
    }

    @Test
    @DisplayName("댓글 좋아요 삭제 시 - 좋아요가 존재 하지 않을 때 - 실패")
    void deleteLikeCommentWithFail() {
        // when & then
        assertThatThrownBy(() -> likeCommentService.delete(account.getEmail(), comment.getId()))
                .isInstanceOf(NotFoundException.class);
    }
}
