package com.sedin.qna.comment.repository;

import com.sedin.qna.comment.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByArticleId(Long articleId);

    Optional<Comment> findByArticleIdAndId(Long articleId, Long commentId);
}
