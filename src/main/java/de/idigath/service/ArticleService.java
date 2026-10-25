package de.idigath.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import de.idigath.entity.Article;
import de.idigath.mapper.ArticleMapper;
import de.idigath.mapper.ArticlePreviewMapper;
import de.idigath.model.ArticleDto;
import de.idigath.model.ArticleOverview;
import de.idigath.model.ArticlePreviewDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.NotFoundException;

/**
 * Service layer for article-related business operations.
 *
 * <p>
 * This class encapsulates the core business logic for article management,
 * acting as an
 * intermediary between the REST controllers and the data access layer. It
 * handles entity-to-DTO
 * mapping, exception translation, and coordinates database operations.
 *
 * <p>
 * The service is stateless and application-scoped, making it suitable for
 * concurrent request
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
   * <p>
   * If no article exists with the specified ID, a {@link NotFoundException} is
   * thrown.
   *
   * <p>
   * Database query timeouts are propagated as {@link PersistenceException}
   * wrapping a {@link
   * SQLTimeoutException}, which is later mapped to an HTTP 504 Gateway Timeout
   * response by the
   * exception mapper.
   *
   * @param id the unique identifier of the article
   * @return the article data transfer object containing all article fields
   * @throws NotFoundException    if no article exists with the given ID
   * @throws PersistenceException if a database error occurs (including timeout)
   */
  public ArticleDto findArticleById(Long id) {
    var article = Article.<Article>findByIdOptional(id)
        .orElseThrow(() -> new NotFoundException("Article with id: " + id + " not found"));
    ArticleDto result = ArticleMapper.INSTANCE.articleToArticleDto(article);
    return result;
  }

  /**
   * Retrieves the article overview based on the current page, text in the
   * headline and tags.
   *
   * <p>
   * If no article will be found a method returns an overview with empty or
   * default values.
   *
   * <p>
   * An article matches only if it has ALL of the given tags. Null or blank tags
   * are ignored. If the tag list is null or empty, the tag filter is not applied.
   *
   * <p>
   * Database query timeouts are propagated as {@link PersistenceException}
   * wrapping a {@link
   * SQLTimeoutException}, which is later mapped to an HTTP 504 Gateway Timeout
   * response by the
   * exception mapper.
   *
   * @param page the current page number in the query
   * @param text text to search for in the headline (case-insensitive substring match)
   * @param tags list of tags the article must have (case-insensitive)
   * @throws PersistenceException if a database error occurs (including timeout)
   *
   */
  public ArticleOverview searchArticleOverview(Integer page, String text, List<String> tags) {
    int searchPage = Objects.nonNull(page) ? page : 0;
    searchPage = searchPage > 0 ? searchPage - 1 : 0;
    String searchText = Objects.nonNull(text) ? text : "";
    List<String> searchTags = Objects.nonNull(tags)
        ? tags.stream().filter(Objects::nonNull).filter(t -> !t.isBlank()).map(String::toLowerCase).toList()
        : List.of();

    var articleQuery = Article.findByTitleAndTags(searchText, searchTags).page(searchPage, 10);

    List<Article> articleList = articleQuery.list();
    List<ArticlePreviewDto> articlePreviewList = new LinkedList<>();
    for (Article article : articleList) {
      articlePreviewList.add(ArticlePreviewMapper.INSTANCE.articleToArticlePreviewDto(article));
    }
    var result = new ArticleOverview(articlePreviewList, articleQuery.count(), searchPage, articleQuery.pageCount());
    return result;
  }
}
