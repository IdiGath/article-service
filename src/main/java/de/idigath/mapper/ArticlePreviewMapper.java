package de.idigath.mapper;

import de.idigath.entity.Article;
import de.idigath.model.ArticlePreviewDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Maps between Article entity and ArticlePreviewDto for clean architecture separation.
 *
 * <p>This mapper isolates the persistence layer (JPA entities) from the API layer (DTOs),
 * preventing database-specific concerns (e.g., Hibernate proxies, lazy loading issues) from leaking
 * into the REST API responses. Using MapStruct ensures type-safe, maintainable, and performant
 * conversions without manual boilerplate code.
 */
@Mapper
public interface ArticlePreviewMapper {

  ArticlePreviewMapper INSTANCE = Mappers.getMapper(ArticlePreviewMapper.class);

  /**
   * Converts an Article entity to its Preview DTO representation for safe API exposure.
   *
   * <p>This prevents serialization issues with Hibernate proxies and lazy-loaded collections, while
   * ensuring the API contract remains stable regardless of entity changes.
   */
  ArticlePreviewDto articleToArticlePreviewDto(Article article);
}
