package com.sedin.qna.likecomment.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.account.repository.AccountRepository;
import com.sedin.qna.comment.model.Comment;
import com.sedin.qna.comment.repository.CommentRepository;
import com.sedin.qna.common.exception.DuplicatedException;
import com.sedin.qna.common.exception.NotFoundException;
import com.sedin.qna.common.response.ApiResponseDto;
import com.sedin.qna.likecomment.model.LikeComment;
import com.sedin.qna.likecomment.repository.LikeCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeCommentServiceImpl implements LikeCommentService {

    private final AccountRepository accountRepository;
    private final CommentRepository commentRepository;
    private final LikeCommentRepository likeCommentRepository;

    @Override
    public ApiResponseDto<String> create(String email, Long commentId) {
        Account account = findAccount(email);
        Comment comment = findComment(commentId);

        if (likeCommentRepository.existsByAccountAndComment(account, comment)) {
            throw new DuplicatedException("Already registered resource");
        }

        LikeComment likeComment = LikeComment.builder()
                .account(account)
                .comment(comment)
                .build();

        likeCommentRepository.save(likeComment);
        return ApiResponseDto.DEFAULT_OK;
    }

    @Override
    public ApiResponseDto<String> delete(String email, Long commentId) {
        Account account = findAccount(email);
        Comment comment = findComment(commentId);

        LikeComment likeComment = findLikeComment(account, comment);

        likeCommentRepository.delete(likeComment);
        return ApiResponseDto.DEFAULT_OK;
    }

    private Account findAccount(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(email));
    }

    private Comment findComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(commentId.toString()));
    }

    private LikeComment findLikeComment(Account account, Comment comment) {
        return likeCommentRepository.findByAccountAndComment(account, comment)
                .orElseThrow(() -> new NotFoundException(comment.getId().toString()));
    }
}
