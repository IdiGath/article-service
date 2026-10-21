package de.idigath.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
public class ArticleServiceTest {

  @Inject
  ArticleService articleService;

  @Test
  void searchArticleOverview_nullParameters_returnsFirstPage() {
    var result = articleService.searchArticleOverview(null, null, null);

    assertThat(result.totalArticles()).isEqualTo(5);
    assertThat(result.articleList()).hasSize(5);
  }

  @Test
  void searchArticleOverview_negativePage_returnsFirstPage() {
    var result = articleService.searchArticleOverview(-1, null, null);

    assertThat(result.totalArticles()).isEqualTo(5);
    assertThat(result.articleList()).hasSize(5);
  }

  @Test
  void searchArticleOverview_pageOne_returnsFirstPage() {
    var result = articleService.searchArticleOverview(1, null, null);

    assertThat(result.totalArticles()).isEqualTo(5);
    assertThat(result.articleList()).hasSize(5);
  }

  @Test
  void searchArticleOverview_pageTwo_returnsEmptyList() {
    var result = articleService.searchArticleOverview(2, null, null);

    assertThat(result.totalArticles()).isEqualTo(5);
    assertThat(result.articleList()).isEmpty();
  }

  @Test
  void searchArticleOverview_pageOneJava_returnsFirstPageThreeArticles() {
    var result = articleService.searchArticleOverview(1, null, "Java");

    assertThat(result.totalArticles()).isEqualTo(3);
    assertThat(result.articleList()).hasSize(3);
  }

}
