package com.sedin.qna.article.comment.controller;

import com.sedin.qna.article.comment.model.CommentDto;
import com.sedin.qna.article.comment.service.CommentService;
import com.sedin.qna.common.response.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/articles/{articleId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponseDto<CommentDto.ResponseOne> create(@AuthenticationPrincipal String email,
                                                         @PathVariable Long articleId,
                                                         @RequestBody @Valid CommentDto.Create create) {
        return ApiResponseDto.OK(new CommentDto.ResponseOne(commentService.create(email, articleId, create)));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<CommentDto.ResponseList> findAll(@PathVariable Long articleId) {
        return ApiResponseDto.OK(new CommentDto.ResponseList(commentService.findAll(articleId)));
    }

    @GetMapping("{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<CommentDto.ResponseOne> findById(@PathVariable Long articleId,
                                                           @PathVariable Long commentId) {
        return ApiResponseDto.OK(new CommentDto.ResponseOne(commentService.findById(articleId, commentId)));
    }

    @PatchMapping("{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<CommentDto.ResponseOne> update(@AuthenticationPrincipal String email,
                                                         @PathVariable Long articleId,
                                                         @PathVariable Long commentId,
                                                         @RequestBody @Valid CommentDto.Update update) {
        return ApiResponseDto.OK(new CommentDto.ResponseOne(commentService.update(email, articleId, commentId, update)));
    }

    @DeleteMapping("{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<String> delete(@AuthenticationPrincipal String email,
                                         @PathVariable Long articleId,
                                         @PathVariable Long commentId) {
        commentService.delete(email, articleId, commentId);
        return ApiResponseDto.DEFAULT_OK;
    }
}
