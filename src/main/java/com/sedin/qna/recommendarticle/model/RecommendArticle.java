package com.sedin.qna.recommendarticle.model;

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
@Table(name = "recommend_article")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendArticle extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "recommend_article_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    @Builder
    private RecommendArticle(Long id, Account account, Article article) {
        this.id = id;
        this.account = account;
        this.article = article;
    }
}
