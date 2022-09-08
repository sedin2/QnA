package com.sedin.qna.article.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sedin.qna.account.model.Account;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ArticleDto {

    @Getter
    public static class Create {

        @NotBlank
        private String title;
        @NotBlank
        private String content;
        private Account account;

        private Create() {

        }

        @Builder
        private Create(String title, String content) {
            this.title = title;
            this.content = content;
        }

        public Article toEntity(Account account) {
            return Article.builder()
                    .title(title)
                    .content(content)
                    .account(account)
                    .build();
        }
    }

    @Getter
    public static class Update {

        @NotBlank
        private String title;
        @NotBlank
        private String content;

        private Update() {

        }

        @Builder
        private Update(String title, String content) {
            this.title = title;
            this.content = content;
        }

        public Article apply(Article article) {
            return article.update(title, content);
        }
    }

    @Getter
    public static class Response {

        private Long id;
        private String title;
        private String author;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime modifiedAt;

        @Builder
        private Response(Long id, String title, String author, LocalDateTime createdAt, LocalDateTime modifiedAt) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.createdAt = createdAt;
            this.modifiedAt = modifiedAt;
        }

        public static Response of(Article article) {
            return Response.builder()
                    .id(article.getId())
                    .title(article.getTitle())
                    .author(article.getAccount().getName())
                    .createdAt(article.getCreatedAt())
                    .modifiedAt(article.getModifiedAt())
                    .build();
        }
    }

    @Getter
    public static class ResponseDetail {

        private Long id;
        private String title;
        private String content;
        private String author;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime modifiedAt;

        @Builder
        private ResponseDetail(Long id, String title, String content, String author,
                               LocalDateTime createdAt, LocalDateTime modifiedAt) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.author = author;
            this.createdAt = createdAt;
            this.modifiedAt = modifiedAt;
        }

        public static ResponseDetail of(Article article) {
            return ResponseDetail.builder()
                    .id(article.getId())
                    .title(article.getTitle())
                    .content(article.getContent())
                    .author(article.getAccount().getName())
                    .createdAt(article.getCreatedAt())
                    .modifiedAt(article.getModifiedAt())
                    .build();
        }
    }

    @Getter
    public static class ResponseOne {

        private ResponseDetail article;

        public ResponseOne(ResponseDetail article) {
            this.article = article;
        }
    }

    @Getter
    public static class ResponseList {

        private List<Response> articles;

        public ResponseList(List<Response> articles) {
            this.articles = articles;
        }
    }
}
