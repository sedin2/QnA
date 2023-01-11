package com.sedin.qna.article.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sedin.qna.account.model.Account;
import com.sedin.qna.comment.model.CommentDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ArticleDto {

    @Getter
    public static class Create {

        @NotBlank
        private String title;
        @NotBlank
        private String content;

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
                    .author(account.getName())
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
    public static class ResponseChange {

        private Long id;
        private String title;
        private String content;
        private String author;
        private Long commentsCount;
        private Long articleViewCount;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime modifiedAt;

        @Builder
        private ResponseChange(Long id, String title, String content, String author, Long commentsCount,
                               Long articleViewCount, LocalDateTime createdAt, LocalDateTime modifiedAt) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.author = author;
            this.commentsCount = commentsCount;
            this.articleViewCount = articleViewCount;
            this.createdAt = createdAt;
            this.modifiedAt = modifiedAt;
        }

        public static ResponseChange of(Article article) {
            return ResponseChange.builder()
                    .id(article.getId())
                    .title(article.getTitle())
                    .content(article.getContent())
                    .author(article.getAuthor())
                    .commentsCount(article.getCommentsCount())
                    .articleViewCount(article.getArticleViewCount())
                    .createdAt(article.getCreatedAt())
                    .modifiedAt(article.getModifiedAt())
                    .build();
        }
    }

    @Getter
    public static class ResponseAll {

        private Long id;
        private String title;
        private String author;
        private Long commentsCount;
        private Long articleViewCount;
        private Long recommendArticleCount;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime modifiedAt;

        @Builder
        private ResponseAll(Long id, String title, String author, Long commentsCount,
                            Long articleViewCount, Long recommendArticleCount,
                            LocalDateTime createdAt, LocalDateTime modifiedAt) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.commentsCount = commentsCount;
            this.articleViewCount = articleViewCount;
            this.recommendArticleCount = recommendArticleCount;
            this.createdAt = createdAt;
            this.modifiedAt = modifiedAt;
        }

        public static ResponseAll of(Article article) {
            return ResponseAll.builder()
                    .id(article.getId())
                    .title(article.getTitle())
                    .author(article.getAuthor())
                    .commentsCount(article.getCommentsCount())
                    .articleViewCount(article.getArticleViewCount())
                    .recommendArticleCount((long) article.getRecommendArticles().size())
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
        private Long commentsCount;
        private Long articleViewCount;
        private Long recommendArticleCount;
        private List<CommentDto.Response> comments;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime modifiedAt;

        @Builder
        private ResponseDetail(Long id, String title, String content, String author, Long commentsCount,
                               Long articleViewCount, Long recommendArticleCount, List<CommentDto.Response> comments,
                               LocalDateTime createdAt, LocalDateTime modifiedAt) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.author = author;
            this.commentsCount = commentsCount;
            this.articleViewCount = articleViewCount;
            this.recommendArticleCount = recommendArticleCount;
            this.comments = comments;
            this.createdAt = createdAt;
            this.modifiedAt = modifiedAt;
        }

        public static ResponseDetail of(Article article) {
            return ResponseDetail.builder()
                    .id(article.getId())
                    .title(article.getTitle())
                    .content(article.getContent())
                    .author(article.getAuthor())
                    .commentsCount(article.getCommentsCount())
                    .articleViewCount(article.getArticleViewCount())
                    .recommendArticleCount((long) article.getRecommendArticles().size())
                    .comments(article.getComments().stream().map(CommentDto.Response::of).collect(Collectors.toList()))
                    .createdAt(article.getCreatedAt())
                    .modifiedAt(article.getModifiedAt())
                    .build();
        }
    }

    @Getter
    public static class ResponseOne<T> {

        private T article;

        public ResponseOne(T article) {
            this.article = article;
        }
    }

    @Getter
    public static class ResponseList<T> {

        private List<T> articles;

        public ResponseList(List<T> articles) {
            this.articles = articles;
        }
    }
}
