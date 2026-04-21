package de.idigath.mappers;

import de.idigath.entities.Tag;
import de.idigath.model.TagDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TagMapper {

  TagMapper INSTANCE = Mappers.getMapper(TagMapper.class);

  TagDto tagToTagDto(Tag tag);
}
