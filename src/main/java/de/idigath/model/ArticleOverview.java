package de.idigath.model;

import java.util.List;

/**
 * Immutable data transfer object representing an overview of articles for API consumption. This
 * objects includes metadata for the pagination.
 *
 * <p>This record decouples the API contract from the persistence model, preventing
 * database-specific concerns (e.g., Hibernate lazy loading, entity relationships) from affecting
 * clients. All temporal fields use the server's local time zone.
 */
public record ArticleOverview(
    List<ArticlePreview> atircleList, long totalArticles, int currentPage, int totalPages) {}
