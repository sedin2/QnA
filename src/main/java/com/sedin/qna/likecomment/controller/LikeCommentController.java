package com.sedin.qna.likecomment.controller;

import com.sedin.qna.common.response.ApiResponseDto;
import com.sedin.qna.likecomment.service.LikeCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments/{commentId}/like")
@RequiredArgsConstructor
public class LikeCommentController {

    private final LikeCommentService likeCommentService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<String> create(@AuthenticationPrincipal String email, @PathVariable Long commentId) {
        return likeCommentService.create(email, commentId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<String> delete(@AuthenticationPrincipal String email, @PathVariable Long commentId) {
        return likeCommentService.delete(email, commentId);
    }
}
