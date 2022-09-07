package com.sedin.qna.article.model;

import com.sedin.qna.account.model.Account;
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
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "Article")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Article {

    @Id
    @GeneratedValue
    @Column(name = "article_id")
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    @Column(name = "modified_by", nullable = false)
    private String modifiedBy;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Builder
    private Article(String title, String content, LocalDateTime createdAt, String createdBy,
                    LocalDateTime modifiedAt, String modifiedBy, Account account) {
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
        this.account = account;
    }

    public void setAccount(Account account) {
        this.account = account;
        createdAt = LocalDateTime.now();
        createdBy = account.getName();
        modifiedAt = LocalDateTime.now();
        modifiedBy = account.getName();
    }

    public Article update(String title, String content) {
        this.title = title;
        this.content = content;

        return this;
    }
}
