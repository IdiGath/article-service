package de.idigath.service;

import de.idigath.entity.Article;
import de.idigath.mapper.ArticleMapper;
import de.idigath.model.ArticleDto;
import de.idigath.model.ArticleOverview;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

/**
 * Service layer for article-related business operations.
 *
 * <p>This class encapsulates the core business logic for article management, acting as an
 * intermediary between the REST controllers and the data access layer. It handles entity-to-DTO
 * mapping, exception translation, and coordinates database operations.
 *
 * <p>The service is stateless and application-scoped, making it suitable for concurrent request
 * handling in a CDI environment.
 *
 * @see Article
 * @see ArticleDto
 * @see ArticleMapper
 */
@ApplicationScoped
public class ArticleService {

  /**
   * Retrieves an article by its unique identifier.
   *
   * <p>If no article exists with the specified ID, a {@link NotFoundException} is thrown.
   *
   * <p>Database query timeouts are propagated as {@link PersistenceException} wrapping a {@link
   * SQLTimeoutException}, which is later mapped to an HTTP 504 Gateway Timeout response by the
   * exception mapper.
   *
   * @param id the unique identifier of the article
   * @return the article data transfer object containing all article fields
   * @throws NotFoundException if no article exists with the given ID
   * @throws PersistenceException if a database error occurs (including timeout)
   */
  public ArticleDto findArticleById(Long id) {
    var article =
        Article.<Article>findByIdOptional(id)
            .orElseThrow(() -> new NotFoundException("Article with id: " + id + " not found"));
    ArticleDto result = ArticleMapper.INSTANCE.articleToArticleDto(article);
    return result;
  }

  // TODO: Implement
  public ArticleOverview getArticleOverviewList(Integer page, String text, String tag) {

    var articleList = Article.findByTitleAndTag(text, tag).page(page, 10);

    return null;
  }
}
