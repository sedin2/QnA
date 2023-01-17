package com.sedin.qna.likecomment.service;

import com.sedin.qna.common.response.ApiResponseDto;

public interface LikeCommentService {

    ApiResponseDto<String> create(String email, Long commentId);

    ApiResponseDto<String> delete(String email, Long commentId);
}
