package com.sedin.qna.test;

import com.sedin.qna.network.ApiResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test")
    public ApiResponseDto<String> test() {
        return ApiResponseDto.OK("자동 배포 수정 테스트");
    }
}
