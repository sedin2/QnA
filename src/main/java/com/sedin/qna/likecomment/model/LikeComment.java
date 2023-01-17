package com.sedin.qna.likecomment.model;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.comment.model.Comment;
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
@Table(name = "like_comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeComment extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "like_comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Builder
    private LikeComment(Long id, Account account, Comment comment) {
        this.id = id;
        this.account = account;
        this.comment = comment;
    }
}
