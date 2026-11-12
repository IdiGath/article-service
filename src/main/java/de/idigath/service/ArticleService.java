package de.idigath.service;

import de.idigath.entity.Article;
import de.idigath.entity.Source;
import de.idigath.entity.Tag;
import de.idigath.mapper.ArticleMapper;
import de.idigath.mapper.ArticlePreviewMapper;
import de.idigath.mapper.SourceMapper;
import de.idigath.mapper.TagMapper;
import de.idigath.model.ArticleDto;
import de.idigath.model.ArticleOverview;
import de.idigath.model.ArticlePreviewDto;
import de.idigath.model.SourceDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.QueryTimeoutException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
     * Retrieves the article with the given slug (exact match; slugs are stored lowercase).
     *
     * <p>If no article exists with the specified slug, a {@link NotFoundException} is thrown.
     *
     * <p>Database query timeouts are propagated as {@link QueryTimeoutException}, which is later
     * mapped to an HTTP 504 Gateway Timeout response by the exception mapper.
     *
     * @param slug exact article slug
     * @return the article data transfer object containing all article fields
     * @throws NotFoundException if no article exists with the given slug
     * @throws PersistenceException if a database error occurs (including timeout)
     */
    public ArticleDto findArticleBySlug(String slug) {
        var article =
                Article.findBySlugOptional(slug)
                        .orElseThrow(() ->
                                new NotFoundException("Article with slug: " + slug + " not found"));
        ArticleDto result = ArticleMapper.INSTANCE.articleToArticleDto(article);
        return result;
    }

    /**
     * Retrieves the article overview based on the current page, text in the headline and tags.
     *
     * <p>If no article will be found a method returns an overview with empty or default values.
     *
     * <p>An article matches only if it has ALL of the given tags. Null or blank tags are ignored.
     * If the tag list is null or empty, the tag filter is not applied.
     *
     * <p>Database query timeouts are propagated as {@link QueryTimeoutException}, which is later
     * mapped to an HTTP 504 Gateway Timeout response by the exception mapper.
     *
     * @param page 1-based page number; null or non-positive returns the first page
     * @param text text to search for in the headline (case-insensitive substring match; LIKE
     *     wildcards {@code %} and {@code _} are treated literally)
     * @param tags list of tags the article must have (case-insensitive)
     * @return the overview with matching article previews, total count and page info ({@code
     *     currentPage} is 1-based, matching the request parameter); the preview list is empty when
     *     nothing matches
     * @throws PersistenceException if a database error occurs (including timeout)
     */
    public ArticleOverview searchArticleOverview(Integer page, String text, List<String> tags) {
        int searchPage = Objects.nonNull(page) ? page : 0;
        searchPage = searchPage > 0 ? searchPage - 1 : 0;
        String searchText = Objects.nonNull(text) ? text : "";
        List<String> searchTags =
                Objects.nonNull(tags)
                        ? tags.stream()
                                .filter(Objects::nonNull)
                                .filter(t -> !t.isBlank())
                                .map(String::toLowerCase)
                                .toList()
                        : List.of();

        var articleQuery = Article.findByTitleAndTags(searchText, searchTags).page(searchPage, 10);

        List<Article> articleList = articleQuery.list();
        List<ArticlePreviewDto> articlePreviewList = new LinkedList<>();
        for (Article article : articleList) {
            articlePreviewList.add(
                    ArticlePreviewMapper.INSTANCE.articleToArticlePreviewDto(article));
        }
        var result =
                new ArticleOverview(
                        articlePreviewList,
                        articleQuery.count(),
                        searchPage + 1,
                        articleQuery.pageCount());
        return result;
    }

    /**
     * Updates the article with the given slug (exact match) to the state from the DTO.
     *
     * <p>The article is looked up by {@code slug}; the slug itself is frozen and never changed here
     * — if the DTO carries a different title, the article is renamed but keeps its URL (a title
     * already taken by another article violates the unique index). Sources are matched by exact
     * URL: unchanged sources keep their id, new ones are created, missing ones are removed. Tags
     * are resolved case-insensitively by name and reused across articles; duplicate names in the
     * DTO (matched case-insensitively) are applied only once.
     *
     * <p>Runs in its own transaction. Database query timeouts are propagated as {@link
     * QueryTimeoutException}, which is later mapped to an HTTP 504 Gateway Timeout response by the
     * exception mapper.
     *
     * @param articleDto new state of the article
     * @param slug exact slug of the article to update
     * @return the updated article as DTO
     * @throws NotFoundException if no article exists with the given slug
     * @throws PersistenceException if a database error occurs (including timeout)
     */
    @Transactional
    public ArticleDto editArticle(ArticleDto articleDto, String slug) {
        var article =
                Article.findBySlugOptional(slug)
                        .orElseThrow(() ->
                                new NotFoundException("Article with slug: " + slug + " not found"));

        article.title = articleDto.title();
        article.logoUrl = articleDto.logoUrl();
        article.summary = articleDto.summary();
        article.textContent = articleDto.textContent();

        editArticleSources(article, articleDto);
        setArticleTags(article, articleDto);
        Article.flush();
        return ArticleMapper.INSTANCE.articleToArticleDto(article);
    }

    private void editArticleSources(Article article, ArticleDto articleDto) {
        var sourceDtoUrls = articleDto.sources().stream().map(SourceDto::url).toList();
        article.sources.removeIf(s -> !sourceDtoUrls.contains(s.url));
        // New sources are collected separately and added only after the loop, so the
        // collection is never mutated while a stream over it is still in progress.
        var newSources = new LinkedList<Source>();
        for (var sourceDto : articleDto.sources()) {
            var existingSource =
                    article.sources.stream()
                            .filter(s -> s.url.equals(sourceDto.url()))
                            .findFirst()
                            .orElse(null);
            if (Objects.isNull(existingSource)) {
                existingSource = SourceMapper.INSTANCE.sourceDtoToSource(sourceDto);
                existingSource.article = article;
                newSources.add(existingSource);
            }
            existingSource.title = sourceDto.title();
            existingSource.citation = sourceDto.citation();
        }
        article.sources.addAll(newSources);
    }

    private void setArticleTags(Article article, ArticleDto articleDto) {
        article.tags.clear();
        var distinctTagDtos =
                articleDto.tags().stream()
                        .filter(tagDto -> Objects.nonNull(tagDto.name()))
                        .collect(
                                Collectors.toMap(
                                        tagDto -> tagDto.name().toLowerCase(),
                                        tagDto -> tagDto,
                                        (first, duplicate) -> first,
                                        LinkedHashMap::new))
                        .values();
        for (var tagDto : distinctTagDtos) {
            var tag =
                    Tag.findByNameOptional(tagDto.name())
                            .orElseGet(() -> TagMapper.INSTANCE.tagDtoToTag(tagDto));
            article.tags.add(tag);
        }
    }

    /**
     * Deletes the article with the given slug (exact match).
     *
     * <p>Runs in its own transaction. Database query timeouts are propagated as {@link
     * QueryTimeoutException}, which is later mapped to an HTTP 504 Gateway Timeout response by the
     * exception mapper.
     *
     * @param slug exact article slug
     * @return 1 if an article was deleted, 0 if no article matches
     * @throws PersistenceException if a database error occurs (including timeout)
     */
    @Transactional
    public long deleteArticleBySlug(String slug) {
        return Article.deleteBySlug(slug);
    }

    /**
     * Persists a new article from the given DTO.
     *
     * <p>A unique URL-friendly slug is generated from the title and frozen on the article. Sources
     * are linked back to the article (the owning side of the relationship, which the mapper
     * ignores). Tags are resolved case-insensitively by name and reused across articles; duplicate
     * names in the DTO (matched case-insensitively) are applied only once, so the unique constraint
     * on {@code tags.name} is never violated.
     *
     * <p>Runs in its own transaction. Database query timeouts are propagated as {@link
     * QueryTimeoutException}, which is later mapped to an HTTP 504 Gateway Timeout response by the
     * exception mapper.
     *
     * @param newArticleDto new article payload
     * @return the persisted article as DTO, including server-generated slug, id and timestamps
     * @throws PersistenceException if a database error occurs (including timeout)
     */
    @Transactional
    public ArticleDto addNewArticle(ArticleDto newArticleDto) {
        var article = ArticleMapper.INSTANCE.articleDtoToArticle(newArticleDto);
        article.slug = generateUniqueSlug(newArticleDto.title());
        article.sources.forEach(source -> source.article = article);
        setArticleTags(article, newArticleDto);
        article.persist();
        Article.flush();
        return ArticleMapper.INSTANCE.articleToArticleDto(article);
    }

    /**
     * Generates a unique slug from the title: lowercase, runs of characters that are neither
     * letters nor digits collapsed to a single {@code -}, leading/trailing dashes trimmed. If
     * another article already has the resulting slug, a numeric suffix is appended ({@code
     * my-title-2}, {@code my-title-3}, ...). A title without any letter or digit falls back to base
     * {@code article}.
     *
     * <p>The unique index on {@code slug} is the ultimate guard against races; this loop only
     * avoids the expected collision case.
     */
    private static String generateUniqueSlug(String title) {
        String base = slugify(title);
        if (base.isBlank()) {
            base = "article";
        }
        String slug = base;
        int suffix = 2;
        while (Article.findBySlugOptional(slug).isPresent()) {
            slug = base + "-" + suffix++;
        }
        return slug;
    }

    private static String slugify(String title) {
        return title.toLowerCase().replaceAll("[^\\p{L}\\p{N}]+", "-").replaceAll("^-+|-+$", "");
    }
}
