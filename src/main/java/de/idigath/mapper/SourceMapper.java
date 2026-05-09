package de.idigath.mapper;

import de.idigath.entity.Source;
import de.idigath.model.SourceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SourceMapper {

  SourceMapper INSTANCE = Mappers.getMapper(SourceMapper.class);

  SourceDto sourceToSourceDto(Source source);
}
