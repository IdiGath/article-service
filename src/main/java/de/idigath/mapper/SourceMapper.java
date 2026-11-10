package de.idigath.mapper;

import de.idigath.entity.Source;
import de.idigath.model.SourceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Maps between Source entity and SourceDto for clean architecture separation.
 *
 * <p>This mapper isolates the persistence layer (JPA entities) from the API layer (DTOs),
 * preventing database specific concerns (e.g., Hibernate proxies, lazy loading issues) from leaking
 * into the Rest API responses. Using MapStruct ensures type-safe, maintainable, and performant
 * conversions without manual boilerplate code.
 */
@Mapper
public interface SourceMapper {

    SourceMapper INSTANCE = Mappers.getMapper(SourceMapper.class);

    /**
     * Converts a Source entity to its DTO representation for sage API exposure.
     *
     * <p>This prevents serialization issues with Hibernate proxies and lazy-loaded collections,
     * while ensuring the API contract remeins stable regardless of entity changes.
     */
    SourceDto sourceToSourceDto(Source source);

    /**
     * Converts Source DTO to its entity for persistence.
     *
     * <p>ignored attributes can be mapped only in DB context.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "article", ignore = true)
    Source sourceDtoToSource(SourceDto sourceDto);
}
