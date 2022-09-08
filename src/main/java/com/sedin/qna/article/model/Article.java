package com.sedin.qna.article.model;

import com.sedin.qna.account.model.Account;
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
@Table(name = "Article")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Article extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "article_id")
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Builder
    private Article(String title, String content, Account account) {
        this.title = title;
        this.content = content;
        this.account = account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Article update(String title, String content) {
        this.title = title;
        this.content = content;

        return this;
    }
}
