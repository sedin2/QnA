package com.sedin.qna.article.controller;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.article.model.ArticleDto;
import com.sedin.qna.article.service.ArticleService;
import com.sedin.qna.common.LoginRequired;
import com.sedin.qna.network.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.sedin.qna.article.model.ArticleDto.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping
    @LoginRequired
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponseDto<ResponseOne<ArticleDto.ResponseChange>> create(@RequestAttribute Account account,
                                                                         @RequestBody @Valid ArticleDto.Create create) {
        return ApiResponseDto.OK(new ResponseOne<>(articleService.create(account, create)));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<ResponseList<ArticleDto.ResponseAll>> findAll() {
        return ApiResponseDto.OK(new ResponseList<>(articleService.findAll()));
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<ResponseOne<ArticleDto.ResponseDetail>> findById(@PathVariable Long id) {
        return ApiResponseDto.OK(new ResponseOne<>(articleService.findById(id)));
    }

    @PatchMapping("{id}")
    @LoginRequired
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<ResponseOne<ArticleDto.ResponseChange>> update(@RequestAttribute Account account,
                                                                         @PathVariable Long id,
                                                                         @RequestBody @Valid ArticleDto.Update update) {
        return ApiResponseDto.OK(new ArticleDto.ResponseOne<>(articleService.update(account, id, update)));
    }

    @DeleteMapping("{id}")
    @LoginRequired
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<String> delete(@RequestAttribute Account account, @PathVariable Long id) {
        articleService.delete(account, id);
        return ApiResponseDto.DEFAULT_OK;
    }
}
