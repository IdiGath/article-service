package de.idigath.mapper;

import de.idigath.entity.Tag;
import de.idigath.model.TagDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Maps between Tag entity and TagDto for clean architecture separation.
 *
 * <p>This mapper isolates the persistence layer (JPA entities) from the API layer (DTOs),
 * preventing database specific concerns (e.g., Hibernate proxies, lazy loading issues) from leaking
 * into the Rest API responses. Using MapStruct ensures type-safe, maintainable, and performant
 * conversions without manual boilerplate code.
 */
@Mapper
public interface TagMapper {

  TagMapper INSTANCE = Mappers.getMapper(TagMapper.class);

  /**
   * Converts a Tag entity to its DTO representation for sage API exposure.
   *
   * <p>This prevents serialization issues with Hibernate proxies and lazy-loaded collections, while
   * ensuring the API contract remeins stable regardless of entity changes.
   */
  TagDto tagToTagDto(Tag tag);
}
