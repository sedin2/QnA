package com.sedin.qna.likecomment.repository;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.comment.model.Comment;
import com.sedin.qna.likecomment.model.LikeComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeCommentRepository extends JpaRepository<LikeComment, Long> {

    boolean existsByAccountAndComment(Account account, Comment comment);

    Optional<LikeComment> findByAccountAndComment(Account account, Comment comment);
}
