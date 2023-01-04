package com.sedin.qna.article.comment.model;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.article.model.Article;
import com.sedin.qna.common.model.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Getter
@Table(name = "Comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private String author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Builder
    private Comment(String content, String author, Article article, Account account) {
        this.content = content;
        this.author = author;
        this.article = article;
        this.account = account;
    }

    public Comment update(String content) {
        this.content = content;
        return this;
    }

    public void attachToArticle(Account account, Article article) {
        if (this.article != null) {
            article.getComments().remove(this);
        }

        this.author = account.getName();
        this.account = account;
        this.article = article;
        article.getComments().add(this);
        article.plusCommentsCount();
    }

    public void detachToArticle() {
        this.article.getComments().remove(this);
        this.article.minusCommentsCount();
    }
}
