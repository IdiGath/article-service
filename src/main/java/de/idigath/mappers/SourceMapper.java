package de.idigath.mappers;

import de.idigath.entities.Source;
import de.idigath.model.SourceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SourceMapper {

  SourceMapper INSTANCE = Mappers.getMapper(SourceMapper.class);

  SourceDto sourceToSourceDto(Source source);
}
