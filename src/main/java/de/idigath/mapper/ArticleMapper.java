package de.idigath.mapper;

import de.idigath.entity.Article;
import de.idigath.model.ArticleDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ArticleMapper {

  ArticleMapper INSTANCE = Mappers.getMapper(ArticleMapper.class);

  ArticleDto articleToArticleDto(Article article);
}
