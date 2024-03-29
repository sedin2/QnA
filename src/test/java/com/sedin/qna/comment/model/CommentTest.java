package com.sedin.qna.comment.model;

import com.sedin.qna.comment.model.Comment;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommentTest {

    @Test
    void When_New_Comment_Expect_Success() {

        // given
        final String CONTENT = "content";

        // when
        Comment comment = Comment.builder()
                .content(CONTENT)
                .build();

        // then
        assertThat(comment).isNotNull();
        assertThat(comment.getContent()).isEqualTo(CONTENT);
    }
}
