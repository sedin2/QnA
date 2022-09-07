package com.sedin.qna.article.model;

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
        private LocalDateTime createdAt;
        private String createdBy;
        private LocalDateTime modifiedAt;
        private String modifiedBy;
        private Account account;

        private Create() {

        }

        @Builder
        private Create(String title, String content, LocalDateTime createdAt, String createdBy,
                      LocalDateTime modifiedAt, String modifiedBy, Account account) {
            this.title = title;
            this.content = content;
            this.createdAt = createdAt;
            this.createdBy = createdBy;
            this.modifiedAt = modifiedAt;
            this.modifiedBy = modifiedBy;
            this.account = account;
        }

        public Article toEntity() {
            return Article.builder()
                    .title(title)
                    .content(content)
                    .createdAt(createdAt)
                    .createdBy(createdBy)
                    .modifiedAt(modifiedAt)
                    .modifiedBy(modifiedBy)
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
        private String content;
        private LocalDateTime createdAt;
        private String createdBy;
        private LocalDateTime modifiedAt;
        private String modifiedBy;

        @Builder
        private Response(Long id, String title, String content,
                         LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.createdAt = createdAt;
            this.createdBy = createdBy;
            this.modifiedAt = modifiedAt;
            this.modifiedBy = modifiedBy;
        }

        public static Response of(Article article) {
            return Response.builder()
                    .id(article.getId())
                    .title(article.getTitle())
                    .content(article.getContent())
                    .createdAt(article.getCreatedAt())
                    .createdBy(article.getModifiedBy())
                    .modifiedAt(article.getModifiedAt())
                    .modifiedBy(article.getModifiedBy())
                    .build();
        }
    }

    @Getter
    public static class ResponseOne {

        private Response article;

        public ResponseOne(Response article) {
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