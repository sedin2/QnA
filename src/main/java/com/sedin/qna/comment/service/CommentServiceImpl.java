package com.sedin.qna.comment.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.account.repository.AccountRepository;
import com.sedin.qna.article.model.Article;
import com.sedin.qna.article.repository.ArticleRepository;
import com.sedin.qna.comment.model.Comment;
import com.sedin.qna.comment.model.CommentDto;
import com.sedin.qna.comment.repository.CommentRepository;
import com.sedin.qna.common.exception.NotFoundException;
import com.sedin.qna.common.exception.PermissionToAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final AccountRepository accountRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    @Override
    public CommentDto.Response create(String email, Long articleId, CommentDto.Create create) {
        Account account = findAccount(email);
        Article article = findArticle(articleId);
        Comment comment = create.toEntity();
        comment.attachToArticle(account, article);
        return CommentDto.Response.of(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto.Response> findAll(Long articleId) {
        return commentRepository.findAllByArticleId(articleId).stream()
                .map(CommentDto.Response::of)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto.Response findById(Long articleId, Long commentId) {
        return CommentDto.Response.of(findComment(articleId, commentId));
    }

    @Override
    public CommentDto.Response update(String email, Long articleId, Long commentId, CommentDto.Update update) {
        Account account = findAccount(email);
        Comment comment = findComment(articleId, commentId);
        checkPermissionBetweenAccountAndAuthor(account, comment.getAccount());
        comment.update(update.getContent());
        return CommentDto.Response.of(comment);
    }

    @Override
    public void delete(String email, Long articleId, Long commentId) {
        Account account = findAccount(email);
        Comment comment = findComment(articleId, commentId);
        checkPermissionBetweenAccountAndAuthor(account, comment.getAccount());
        comment.detachToArticle();
        commentRepository.delete(comment);
    }

    public Account findAccount(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(email));
    }

    private Article findArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException(articleId.toString()));
    }

    private Comment findComment(Long articleId, Long commentId) {
        return commentRepository.findByArticleIdAndId(articleId, commentId)
                .orElseThrow(() -> new NotFoundException(articleId.toString() + ", " + commentId.toString()));
    }

    private void checkPermissionBetweenAccountAndAuthor(Account account, Account author) {
        if (!author.equals(account)) {
            throw new PermissionToAccessException();
        }
    }
}
