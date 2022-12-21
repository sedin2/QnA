package com.sedin.qna.article.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.account.service.AccountService;
import com.sedin.qna.article.model.Article;
import com.sedin.qna.article.model.ArticleDto;
import com.sedin.qna.article.repository.ArticleRepository;
import com.sedin.qna.common.exception.NotFoundException;
import com.sedin.qna.common.exception.PermissionToAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final AccountService accountService;
    private final ArticleRepository articleRepository;

    @Override
    public ArticleDto.ResponseChange create(String email, ArticleDto.Create create) {
        Account account = accountService.findAccount(email);
        Article article = create.toEntity(account);
        return ArticleDto.ResponseChange.of(articleRepository.save(article));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleDto.ResponseAll> findAll(Pageable pageable) {
        return articleRepository.findAll(pageable).stream()
                .map(ArticleDto.ResponseAll::of)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleDto.ResponseDetail findById(Long id) {
        return ArticleDto.ResponseDetail.of(findArticle(id));
    }

    @Override
    public ArticleDto.ResponseChange update(String email, Long id, ArticleDto.Update update) {
        Account account = accountService.findAccount(email);
        Article article = findArticle(id);
        checkPermissionBetweenAccountAndAuthor(account, article.getAccount());

        return ArticleDto.ResponseChange.of(update.apply(article));
    }

    @Override
    public void delete(String email, Long id) {
        Account account = accountService.findAccount(email);
        Article article = findArticle(id);
        checkPermissionBetweenAccountAndAuthor(account, article.getAccount());

        articleRepository.delete(article);
    }

    @Override
    public List<ArticleDto.ResponseDetail> findAllWithComments(Pageable pageable) {
        return articleRepository.findAllEntityGraph(pageable).stream()
                .map(ArticleDto.ResponseDetail::of)
                .collect(Collectors.toList());
    }

    private Article findArticle(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id.toString()));
    }

    private void checkPermissionBetweenAccountAndAuthor(Account account, Account author) {
        if (!author.equals(account)) {
            throw new PermissionToAccessException();
        }
    }

}
