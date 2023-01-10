package com.sedin.qna.recommendarticle.controller;

import com.sedin.qna.common.response.ApiResponseDto;
import com.sedin.qna.recommendarticle.service.RecommendArticleService;
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
@RequiredArgsConstructor
@RequestMapping("/api/articles/{articleId}/recommend")
public class RecommendArticleController {

    private final RecommendArticleService recommendArticleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponseDto<String> create(@AuthenticationPrincipal String email, @PathVariable Long articleId) {
        return recommendArticleService.createRecommendArticle(email, articleId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<String> delete(@AuthenticationPrincipal String email, @PathVariable Long articleId) {
        return recommendArticleService.deleteRecommendArticle(email, articleId);
    }
}
