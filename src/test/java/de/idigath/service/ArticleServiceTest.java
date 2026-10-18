package de.idigath.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
public class ArticleServiceTest {

  @Inject
  ArticleService articleService;

  @Test
  void testGetArticleOverviewListEmptyParametersFirstPageOk() {
    var result = articleService.searchArticleOverview(null, null, null);

    assertEquals(5, result.totalArticles());
    assertEquals(5, result.articleList().size());
  }

  @Test
  void testGetArticleOverviewListNegativePageGetFirstPage() {
    var result = articleService.searchArticleOverview(-1, null, null);

    assertEquals(5, result.totalArticles());
    assertEquals(5, result.articleList().size());

  }

  @Test
  void testGetArticleOverviewListParameterFirstPageOk() {

    var result = articleService.searchArticleOverview(1, null, null);

    assertEquals(5, result.totalArticles());
    assertEquals(5, result.articleList().size());

  }

  @Test
  void testGetArticleOverviewListParameterSecondPageOk() {

    var result = articleService.searchArticleOverview(2, null, null);

    assertEquals(5, result.totalArticles());
    assertEquals(0, result.articleList().size());

  }

}
