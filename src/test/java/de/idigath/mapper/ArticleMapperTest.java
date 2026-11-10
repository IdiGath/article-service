package de.idigath.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.idigath.model.ArticleDto;
import de.idigath.model.SourceDto;
import de.idigath.model.TagDto;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ArticleMapper}. MapStruct mappers are pure code, so no Quarkus
 * context is needed.
 */
class ArticleMapperTest {

  @Test
  void articleDtoToArticle_clientSuppliedTimestamps_notMappedToEntity() {
    var dto = new ArticleDto(
        "Title",
        "https://example.com/logo.png",
        "Summary",
        "Text",
        LocalDateTime.of(2000, 1, 1, 0, 0),
        LocalDateTime.of(2000, 1, 2, 0, 0),
        Set.of(new SourceDto("Source title", "https://example.com/source", "p. 42")),
        Set.of(new TagDto("Java")));

    var entity = ArticleMapper.INSTANCE.articleDtoToArticle(dto);

    assertThat(entity.id).isNull();
    assertThat(entity.createdAt).isNull();
    assertThat(entity.updatedAt).isNull();
    assertThat(entity.title).isEqualTo("Title");
    assertThat(entity.logoUrl).isEqualTo("https://example.com/logo.png");
    assertThat(entity.summary).isEqualTo("Summary");
    assertThat(entity.textContent).isEqualTo("Text");
    assertThat(entity.sources).hasSize(1);
    assertThat(entity.tags).hasSize(1);
  }
}
