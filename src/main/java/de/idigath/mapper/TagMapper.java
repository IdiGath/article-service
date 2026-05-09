package de.idigath.mapper;

import de.idigath.entity.Tag;
import de.idigath.model.TagDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TagMapper {

  TagMapper INSTANCE = Mappers.getMapper(TagMapper.class);

  TagDto tagToTagDto(Tag tag);
}
