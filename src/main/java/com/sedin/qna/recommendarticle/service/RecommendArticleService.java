package com.sedin.qna.recommendarticle.service;

import com.sedin.qna.common.response.ApiResponseDto;

public interface RecommendArticleService {

    ApiResponseDto<String> createRecommendArticle(String email, Long articleId);

    ApiResponseDto<String> deleteRecommendArticle(String email, Long articleId);
}
