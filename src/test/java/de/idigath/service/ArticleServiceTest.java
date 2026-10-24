package de.idigath.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

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
    var result = articleService.searchArticleOverview(1, null, List.of("Java"));

    assertThat(result.totalArticles()).isEqualTo(3);
    assertThat(result.articleList()).hasSize(3);
  }

  @Test
  void searchArticleOverview_pageOneJavaWrongCase_returnsFirstPageThreeArticles() {
    var result = articleService.searchArticleOverview(1, null, List.of("jAVa"));

    assertThat(result.totalArticles()).isEqualTo(3);
    assertThat(result.articleList()).hasSize(3);
  }

  @Test
  void searchArticleOverview_twoTagsJavaAndPerformance_returnsOneArticle() {
    var result = articleService.searchArticleOverview(1, null, List.of("Java", "Performance"));

    assertThat(result.totalArticles()).isEqualTo(1);
    assertThat(result.articleList()).hasSize(1);
  }

  @Test
  void searchArticleOverview_twoTagsJavaAndNixOS_returnsOneArticle() {
    var result = articleService.searchArticleOverview(1, null, List.of("Java", "NixOS"));

    assertThat(result.totalArticles()).isEqualTo(1);
    assertThat(result.articleList()).hasSize(1);
  }

  @Test
  void searchArticleOverview_twoTagsWrongCase_returnsOneArticle() {
    var result = articleService.searchArticleOverview(1, null, List.of("jAvA", "pErFoRmAnCe"));

    assertThat(result.totalArticles()).isEqualTo(1);
    assertThat(result.articleList()).hasSize(1);
  }

  @Test
  void searchArticleOverview_duplicateTags_treatedAsSingleTag() {
    var result = articleService.searchArticleOverview(1, null, List.of("Java", "Java"));

    assertThat(result.totalArticles()).isEqualTo(3);
    assertThat(result.articleList()).hasSize(3);
  }

  @Test
  void searchArticleOverview_emptyTagList_returnsAllArticles() {
    var result = articleService.searchArticleOverview(1, null, List.of());

    assertThat(result.totalArticles()).isEqualTo(5);
    assertThat(result.articleList()).hasSize(5);
  }

  @Test
  void searchArticleOverview_tagWithBlankStrings_ignoredReturnsAllArticles() {
    var result = articleService.searchArticleOverview(1, null, List.of("", "  "));

    assertThat(result.totalArticles()).isEqualTo(5);
    assertThat(result.articleList()).hasSize(5);
  }

  @Test
  void searchArticleOverview_nonexistentTag_returnsEmptyList() {
    var result = articleService.searchArticleOverview(1, null, List.of("NonexistentTag"));

    assertThat(result.totalArticles()).isZero();
    assertThat(result.articleList()).isEmpty();
  }

  @Test
  void searchArticleOverview_textAndTwoTags_returnsMatchingArticle() {
    var result = articleService.searchArticleOverview(1, "Java 21", List.of("Java", "Performance"));

    assertThat(result.totalArticles()).isEqualTo(1);
    assertThat(result.articleList()).hasSize(1);
  }

}
