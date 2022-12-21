package com.sedin.qna.article.controller;

import com.sedin.qna.article.model.ArticleDto;
import com.sedin.qna.article.service.ArticleService;
import com.sedin.qna.common.response.ApiResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

import static com.sedin.qna.article.model.ArticleDto.ResponseList;
import static com.sedin.qna.article.model.ArticleDto.ResponseOne;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponseDto<ResponseOne<ArticleDto.ResponseChange>> create(@AuthenticationPrincipal String email,
                                                                         @RequestBody @Valid ArticleDto.Create create) {
        return ApiResponseDto.OK(new ResponseOne<>(articleService.create(email, create)));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<ResponseList<ArticleDto.ResponseAll>> findAll(
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponseDto.OK(new ResponseList<>(articleService.findAll(pageable)));
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<ResponseOne<ArticleDto.ResponseDetail>> findById(@PathVariable Long id) {
        return ApiResponseDto.OK(new ResponseOne<>(articleService.findById(id)));
    }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<ResponseOne<ArticleDto.ResponseChange>> update(@AuthenticationPrincipal String email,
                                                                         @PathVariable Long id,
                                                                         @RequestBody @Valid ArticleDto.Update update) {
        return ApiResponseDto.OK(new ArticleDto.ResponseOne<>(articleService.update(email, id, update)));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<String> delete(@AuthenticationPrincipal String email, @PathVariable Long id) {
        articleService.delete(email, id);
        return ApiResponseDto.DEFAULT_OK;
    }
}
