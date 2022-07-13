package com.sedin.qna.comment.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommentTest {

    @Test
    void When_New_Comment_Expect_Success() {

        // given
        final String CONTENT = "content";

        // when
        Comment comment = Comment.builder()
                .id(1L)
                .content(CONTENT)
                .build();

        // then
        assertThat(comment).isNotNull();
        assertThat(comment.getId()).isEqualTo(1L);
        assertThat(comment.getContent()).isEqualTo(CONTENT);
    }
}
