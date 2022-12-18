package com.sedin.qna.article.comment.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.article.comment.model.CommentDto;

import java.util.List;

public interface CommentService {

    CommentDto.Response create(Account account, Long articleId, CommentDto.Create create);

    List<CommentDto.Response> findAll(Long articleId);

    CommentDto.Response findById(Long articleId, Long commentId);

    CommentDto.Response update(Account account, Long articleId, Long commentId, CommentDto.Update update);

    void delete(Account account, Long articleId, Long commentId);
}
