package de.idigath.model;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Immutable data transfer object representing an overview of articles for API consumption. This
 * objects includes metadata for the pagination.
 *
 * <p>This record decouples the API contract from the persistence model, preventing
 * database-specific concerns (e.g., Hibernate lazy loading, entity relationships) from affecting
 * clients. All temporal fields use the server's local time zone.
 */
public record ArticleOverview(
        List<ArticlePreviewDto> articleList,
        long totalArticles,
        @Schema(
                        description =
                                "Current page, 1-based; 1 when the request page is null or non-positive")
                int currentPage,
        int totalPages) {}
