package com.sedin.qna.article.comment.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sedin.qna.account.model.Account;
import com.sedin.qna.article.model.Article;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentDto {

    @Getter
    public static class Create {

        @NotBlank
        private String content;

        private Create() {

        }

        @Builder
        private Create(String content) {
            this.content = content;
        }

        public Comment toEntity() {
            return Comment.builder()
                    .content(content)
                    .build();
        }
    }

    @Getter
    public static class Update {

        @NotBlank
        private String content;

        private Update() {

        }

        @Builder
        private Update(String content) {
            this.content = content;
        }
    }

    @Getter
    public static class Response {

        private final Long id;

        private final String content;

        private final String author;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private final LocalDateTime createdAt;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private final LocalDateTime modifiedAt;

        @Builder
        private Response(Long id, String content, String author, LocalDateTime createdAt, LocalDateTime modifiedAt) {
            this.id = id;
            this.content = content;
            this.author = author;
            this.createdAt = createdAt;
            this.modifiedAt = modifiedAt;
        }

        public static Response of(Comment comment) {
            return Response.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .author(comment.getAuthor())
                    .createdAt(comment.getCreatedAt())
                    .modifiedAt(comment.getModifiedAt())
                    .build();
        }
    }

    @Getter
    public static class ResponseOne {

        private final Response comment;

        public ResponseOne(Response comment) {
            this.comment = comment;
        }
    }

    @Getter
    public static class ResponseList {

        private final List<Response> comments;

        public ResponseList(List<Response> comments) {
            this.comments = comments;
        }
    }
}
