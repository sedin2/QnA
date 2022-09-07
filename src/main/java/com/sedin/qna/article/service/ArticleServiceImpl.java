package com.sedin.qna.article.service;

import com.sedin.qna.account.model.Account;
import com.sedin.qna.article.model.Article;
import com.sedin.qna.article.model.ArticleDto;
import com.sedin.qna.article.repository.ArticleRepository;
import com.sedin.qna.exception.NotFoundException;
import com.sedin.qna.exception.PermissionToAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public ArticleDto.Response create(Account account, ArticleDto.Create create) {
        Article article = create.toEntity();
        article.setAccount(account);
        return ArticleDto.Response.of(articleRepository.save(article));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleDto.Response> findAll() {
        return articleRepository.findAll().stream()
                .map(ArticleDto.Response::of)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleDto.Response findById(Long id) {
        return ArticleDto.Response.of(findArticle(id));
    }

    @Override
    public ArticleDto.Response update(Account account, Long id, ArticleDto.Update update) {
        Article article = findArticle(id);
        checkPermissionBetweenAccountAndAuthor(account, article.getAccount());

        return ArticleDto.Response.of(update.apply(article));
    }

    @Override
    public void delete(Account account, Long id) {
        Article article = findArticle(id);
        checkPermissionBetweenAccountAndAuthor(account, article.getAccount());

        articleRepository.delete(article);
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
