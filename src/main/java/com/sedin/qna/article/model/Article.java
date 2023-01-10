package com.sedin.qna.article.model;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.comment.model.Comment;
import com.sedin.qna.common.model.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "Article")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "article_id")
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private Long commentsCount = 0L;

    @Column(nullable = false)
    private Long articleViewCount = 0L;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Builder
    private Article(Long id, String title, String content, String author, Account account) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.account = account;
    }

    public Article update(String title, String content) {
        this.title = title;
        this.content = content;
        return this;
    }

    public void plusCommentsCount() {
        this.commentsCount++;
    }

    public void minusCommentsCount() {
        this.commentsCount--;
    }

    public void increaseArticleViewCount() {
        this.articleViewCount++;
    }
}
