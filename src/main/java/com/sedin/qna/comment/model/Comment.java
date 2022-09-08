package com.sedin.qna.comment.model;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.article.model.Article;
import com.sedin.qna.common.model.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Getter
@Table(name = "Comment")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false, length = 1000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Builder
    private Comment(String content, Article article, Account account) {
        this.content = content;
        this.article = article;
        this.account = account;
    }
}
