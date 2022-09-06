package com.sedin.qna.article.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleTest {

    @Test
    void When_New_Article_Expect_Success() {

        // given
        final String TITLE = "title";
        final String CONTENT = "content";

        // when
        Article article = Article.builder()
                .title(TITLE)
                .content(CONTENT)
                .build();

        // then
        assertThat(article).isNotNull();
        assertThat(article.getTitle()).isEqualTo(TITLE);
        assertThat(article.getContent()).isEqualTo(CONTENT);
    }
}
