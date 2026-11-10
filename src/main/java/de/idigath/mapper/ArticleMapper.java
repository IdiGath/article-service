package de.idigath.mapper;

import de.idigath.entity.Article;
import de.idigath.model.ArticleDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Maps between Article entity and ArticleDto for clean architecture separation.
 *
 * <p>
 * This mapper isolates the persistence layer (JPA entities) from the API layer
 * (DTOs),
 * preventing database-specific concerns (e.g., Hibernate proxies, lazy loading
 * issues) from leaking
 * into the REST API responses. Using MapStruct ensures type-safe, maintainable,
 * and performant
 * conversions without manual boilerplate code.
 */
@Mapper(uses = {SourceMapper.class, TagMapper.class})
public interface ArticleMapper {

  ArticleMapper INSTANCE = Mappers.getMapper(ArticleMapper.class);

  /**
   * Converts an Article entity to its DTO representation for safe API exposure.
   *
   * <p>
   * This prevents serialization issues with Hibernate proxies and lazy-loaded
   * collections, while
   * ensuring the API contract remains stable regardless of entity changes.
   */
  ArticleDto articleToArticleDto(Article article);

  /**
   * Converts an Article DTO to its entity for persistence.
   *
   * <p>
   * {@code id}, {@code createdAt} and {@code updatedAt} are ignored: they are
   * server-managed ({@code @CreationTimestamp}/{@code @UpdateTimestamp}) and must never be
   * taken from client input.
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Article articleDtoToArticle(ArticleDto articleDto);
}
