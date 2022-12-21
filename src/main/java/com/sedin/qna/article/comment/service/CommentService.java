package com.sedin.qna.article.comment.service;

import com.sedin.qna.article.comment.model.CommentDto;

import java.util.List;

public interface CommentService {

    CommentDto.Response create(String email, Long articleId, CommentDto.Create create);

    List<CommentDto.Response> findAll(Long articleId);

    CommentDto.Response findById(Long articleId, Long commentId);

    CommentDto.Response update(String email, Long articleId, Long commentId, CommentDto.Update update);

    void delete(String email, Long articleId, Long commentId);
}
