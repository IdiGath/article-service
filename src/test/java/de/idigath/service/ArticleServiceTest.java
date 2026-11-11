package de.idigath.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.idigath.entity.Article;
import de.idigath.entity.Source;
import de.idigath.entity.Tag;
import de.idigath.model.ArticleDto;
import de.idigath.model.SourceDto;
import de.idigath.model.TagDto;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ArticleServiceTest {

    @Inject ArticleService articleService;

    @Test
    @TestTransaction
    void findArticleByTitle_existingTitleDifferentCase_returnsArticle() {
        String title = "Weekend test article: 100%";
        persistTestArticle(title);

        var result = articleService.findArticleByTitle("weekend test article: 100%");

        assertThat(result.title()).isEqualTo(title);
    }

    @Test
    void findArticleByTitle_nonExistentTitle_throwsNotFoundException() {
        assertThatThrownBy(() -> articleService.findArticleByTitle("no such article title"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("no such article title");
    }

    @Test
    void searchArticleOverview_nullParameters_returnsFirstPage() {
        var all = seedArticleCount();
        var result = articleService.searchArticleOverview(null, null, null);

        assertThat(result.totalArticles()).isEqualTo(all);
        assertThat(result.articleList()).hasSize((int) all);
        assertThat(result.currentPage()).isEqualTo(1);
    }

    @Test
    void searchArticleOverview_negativePage_returnsFirstPage() {
        var all = seedArticleCount();
        var result = articleService.searchArticleOverview(-1, null, null);

        assertThat(result.totalArticles()).isEqualTo(all);
        assertThat(result.articleList()).hasSize((int) all);
        assertThat(result.currentPage()).isEqualTo(1);
    }

    @Test
    void searchArticleOverview_pageOne_returnsFirstPage() {
        var all = seedArticleCount();
        var result = articleService.searchArticleOverview(1, null, null);

        assertThat(result.totalArticles()).isEqualTo(all);
        assertThat(result.articleList()).hasSize((int) all);
        assertThat(result.currentPage()).isEqualTo(1);
    }

    @Test
    void searchArticleOverview_pageTwo_returnsEmptyList() {
        var all = seedArticleCount();
        var result = articleService.searchArticleOverview(2, null, null);

        assertThat(result.totalArticles()).isEqualTo(all);
        assertThat(result.articleList()).isEmpty();
        assertThat(result.currentPage()).isEqualTo(2);
    }

    @Test
    void searchArticleOverview_pageOneJava_returnsAllJavaTaggedArticles() {
        var javaTagged = seedArticleCountWithAllTags("Java");
        var result = articleService.searchArticleOverview(1, null, List.of("Java"));

        assertThat(result.totalArticles()).isEqualTo(javaTagged);
        assertThat(result.articleList()).hasSize((int) javaTagged);
    }

    @Test
    void searchArticleOverview_pageOneJavaWrongCase_returnsAllJavaTaggedArticles() {
        var javaTagged = seedArticleCountWithAllTags("jAVa");
        var result = articleService.searchArticleOverview(1, null, List.of("jAVa"));

        assertThat(result.totalArticles()).isEqualTo(javaTagged);
        assertThat(result.articleList()).hasSize((int) javaTagged);
    }

    @Test
    void searchArticleOverview_twoTagsJavaAndPerformance_returnsMatchingArticles() {
        var bothTagged = seedArticleCountWithAllTags("Java", "Performance");
        var result = articleService.searchArticleOverview(1, null, List.of("Java", "Performance"));

        assertThat(result.totalArticles()).isEqualTo(bothTagged);
        assertThat(result.articleList()).hasSize((int) bothTagged);
    }

    @Test
    void searchArticleOverview_twoTagsJavaAndNixOS_returnsMatchingArticles() {
        var bothTagged = seedArticleCountWithAllTags("Java", "NixOS");
        var result = articleService.searchArticleOverview(1, null, List.of("Java", "NixOS"));

        assertThat(result.totalArticles()).isEqualTo(bothTagged);
        assertThat(result.articleList()).hasSize((int) bothTagged);
    }

    @Test
    void searchArticleOverview_twoTagsWrongCase_returnsMatchingArticles() {
        var bothTagged = seedArticleCountWithAllTags("jAvA", "pErFoRmAnCe");
        var result = articleService.searchArticleOverview(1, null, List.of("jAvA", "pErFoRmAnCe"));

        assertThat(result.totalArticles()).isEqualTo(bothTagged);
        assertThat(result.articleList()).hasSize((int) bothTagged);
    }

    @Test
    void searchArticleOverview_duplicateTags_treatedAsSingleTag() {
        var javaTagged = seedArticleCountWithAllTags("Java", "Java");
        var result = articleService.searchArticleOverview(1, null, List.of("Java", "Java"));

        assertThat(result.totalArticles()).isEqualTo(javaTagged);
        assertThat(result.articleList()).hasSize((int) javaTagged);
    }

    @Test
    void searchArticleOverview_emptyTagList_returnsAllArticles() {
        var all = seedArticleCount();
        var result = articleService.searchArticleOverview(1, null, List.of());

        assertThat(result.totalArticles()).isEqualTo(all);
        assertThat(result.articleList()).hasSize((int) all);
    }

    @Test
    void searchArticleOverview_tagWithBlankStrings_ignoredReturnsAllArticles() {
        var all = seedArticleCount();
        var result = articleService.searchArticleOverview(1, null, List.of("", "  "));

        assertThat(result.totalArticles()).isEqualTo(all);
        assertThat(result.articleList()).hasSize((int) all);
    }

    @Test
    void searchArticleOverview_nonexistentTag_returnsEmptyList() {
        var result = articleService.searchArticleOverview(1, null, List.of("NonexistentTag"));

        assertThat(result.totalArticles()).isZero();
        assertThat(result.articleList()).isEmpty();
    }

    @Test
    void searchArticleOverview_textAndTwoTags_returnsMatchingArticles() {
        var expected = seedArticleCount("Java 21", List.of("Java", "Performance"));
        var result =
                articleService.searchArticleOverview(1, "Java 21", List.of("Java", "Performance"));

        assertThat(result.totalArticles()).isEqualTo(expected);
        assertThat(result.articleList()).hasSize((int) expected);
    }

    @Test
    void searchArticleOverview_textWithPercentSign_returnsNoArticles() {
        var result = articleService.searchArticleOverview(1, "%", null);

        assertThat(result.totalArticles()).isZero();
        assertThat(result.articleList()).isEmpty();
    }

    @Test
    void searchArticleOverview_textWithUnderscore_returnsNoArticles() {
        var result = articleService.searchArticleOverview(1, "_", null);

        assertThat(result.totalArticles()).isZero();
        assertThat(result.articleList()).isEmpty();
    }

    @Test
    @TestTransaction
    void searchArticleOverview_textWithPercentSign_returnsOnlyLiteralMatches() {
        String title = "Weekend test article: 100%";
        persistTestArticle(title);

        var result = articleService.searchArticleOverview(1, "100%", null);

        assertThat(result.totalArticles()).isEqualTo(1);
        assertThat(result.articleList()).hasSize(1);
        assertThat(result.articleList().get(0).title()).isEqualTo(title);
    }

    /**
     * Counts seed articles, mirroring {@link Article#findByTitleAndTags}: literal case-insensitive
     * title substring (LIKE wildcards escaped), and the article must have ALL given tags
     * (case-insensitive, blanks ignored, duplicates deduped).
     */
    private long seedArticleCount(String text, List<String> tags) {
        String escaped =
                (text == null ? "" : text).replace("!", "!!").replace("%", "!%").replace("_", "!_");
        List<String> distinctTags =
                tags == null
                        ? List.of()
                        : tags.stream()
                                .filter(t -> t != null && !t.isBlank())
                                .map(String::toLowerCase)
                                .distinct()
                                .toList();
        return QuarkusTransaction.requiringNew()
                .call(() -> {
                    StringBuilder sql =
                            new StringBuilder(
                                    "SELECT count(*) FROM articles a WHERE lower(a.title) LIKE lower(?) ESCAPE '!'");
                    List<Object> params = new ArrayList<>();
                    params.add("%" + escaped + "%");
                    if (!distinctTags.isEmpty()) {
                        String placeholders =
                                String.join(",", Collections.nCopies(distinctTags.size(), "?"));
                        sql.append(
                                        " AND a.id IN (SELECT at.article_id FROM article_tags at JOIN tags t ON t.id = at.tag_id WHERE lower(t.name) IN (")
                                .append(placeholders)
                                .append(") GROUP BY at.article_id HAVING count(DISTINCT t.id) = ")
                                .append(distinctTags.size())
                                .append(")");
                        params.addAll(distinctTags);
                    }
                    var query = Article.getEntityManager().createNativeQuery(sql.toString());
                    for (int i = 0; i < params.size(); i++) {
                        query.setParameter(i + 1, params.get(i));
                    }
                    return ((Number) query.getSingleResult()).longValue();
                });
    }

    private long seedArticleCount() {
        return seedArticleCount(null, null);
    }

    private long seedArticleCountWithAllTags(String... tagNames) {
        return seedArticleCount(null, List.of(tagNames));
    }

    private Article persistTestArticle(String title) {
        Article article = new Article();
        article.title = title;
        article.logoUrl = "https://example.com/logos/sale.png";
        article.summary = "My test article";
        article.textContent = "My test article";
        article.persist();
        return article;
    }

    @Test
    void deleteArticleByTitle_nonExistentTitle_nothingChanged() {
        var firstSelect = articleService.searchArticleOverview(null, null, null);
        articleService.deleteArticleByTitle("dsdfjsdfsdfj");
        var secondSelect = articleService.searchArticleOverview(null, null, null);

        assertThat(secondSelect.totalArticles()).isEqualTo(firstSelect.totalArticles());
    }

    @Test
    @TestTransaction
    void deleteArticleByTitle_existingTitle_minusOneArticle() {
        persistTestArticle("Weekend test article: 100%");
        var firstSelect = articleService.searchArticleOverview(null, null, null);
        var expected = firstSelect.totalArticles() - 1;
        articleService.deleteArticleByTitle("Weekend test article: 100%");
        var secondSelect = articleService.searchArticleOverview(null, null, null);

        assertThat(secondSelect.totalArticles()).isEqualTo(expected);
    }

    @Test
    @TestTransaction
    void editArticle_existingTitle_updateArticle() {
        var title = "Weekend test article: 100%";
        var newSummary = "Another Summary";
        var article = persistTestArticle(title);
        var articleDto =
                new ArticleDto(
                        article.title,
                        article.logoUrl,
                        newSummary,
                        article.textContent,
                        article.createdAt,
                        article.updatedAt,
                        new HashSet<SourceDto>(),
                        new HashSet<TagDto>());
        var result = articleService.editArticle(articleDto, title);
        assertThat(newSummary).isEqualTo(result.summary());
    }

    @Test
    @TestTransaction
    void editArticle_notExistingTitle_notFoundException() {
        var title = "Weekend test article: 100%";
        var articleDto =
                new ArticleDto(
                        title,
                        "url",
                        "Another Summary",
                        "text",
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        new HashSet<SourceDto>(),
                        new HashSet<TagDto>());

        assertThatThrownBy(() -> articleService.editArticle(articleDto, title))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @TestTransaction
    void editArticle_duplicateTagNamesDifferentCase_persistsSingleTag() {
        var title = "Tag dedupe test article";
        var article = persistTestArticle(title);
        Set<TagDto> tags =
                new HashSet<>(List.of(new TagDto("Neu"), new TagDto("neu"), new TagDto("NEU")));
        var articleDto =
                new ArticleDto(
                        article.title,
                        article.logoUrl,
                        article.summary,
                        article.textContent,
                        article.createdAt,
                        article.updatedAt,
                        new HashSet<SourceDto>(),
                        tags);

        var result = articleService.editArticle(articleDto, title);

        assertThat(result.tags()).hasSize(1);
        assertThat(result.tags().iterator().next().name()).isEqualToIgnoringCase("neu");
        assertThat(Tag.count("lower(name) = lower(?1)", "neu")).isEqualTo(1);
    }

    @Test
    @TestTransaction
    void editArticle_newSourceUrl_addsAndPersistsNewSource() {
        var title = "Source add test article";
        var article = persistTestArticle(title);
        var oldSource = new Source();
        oldSource.article = article;
        oldSource.title = "Old source";
        oldSource.url = "https://example.com/old";
        oldSource.citation = "old citation";
        article.sources.add(oldSource);
        var newSourceDto = new SourceDto("New source", "https://example.com/new", "new citation");
        var articleDto =
                new ArticleDto(
                        article.title,
                        article.logoUrl,
                        article.summary,
                        article.textContent,
                        article.createdAt,
                        article.updatedAt,
                        new HashSet<>(
                                Set.of(
                                        new SourceDto(
                                                "Old source",
                                                "https://example.com/old",
                                                "old citation"),
                                        newSourceDto)),
                        new HashSet<TagDto>());

        var result = articleService.editArticle(articleDto, title);

        assertThat(result.sources()).hasSize(2);
        assertThat(result.sources().stream().map(SourceDto::url).toList())
                .containsExactlyInAnyOrder("https://example.com/old", "https://example.com/new");
        assertThat(Source.count("url = ?1", "https://example.com/new")).isEqualTo(1);
        assertThat(Source.count("url = ?1", "https://example.com/old")).isEqualTo(1);
    }

    @Test
    @TestTransaction
    void addNewArticle_validDto_persistsArticleWithSourceAndTag() {
        var dto =
                new ArticleDto(
                        "Brand new article",
                        "https://example.com/logo.png",
                        "Summary",
                        "Text content",
                        null,
                        null,
                        new HashSet<>(
                                Set.of(new SourceDto("Source", "https://example.com/src", "citation"))),
                        new HashSet<>(Set.of(new TagDto("NewTag"))));

        var result = articleService.addNewArticle(dto);

        assertThat(result.title()).isEqualTo("Brand new article");
        assertThat(result.createdAt()).isNotNull();
        assertThat(result.updatedAt()).isNotNull();
        assertThat(result.sources()).hasSize(1);
        assertThat(result.tags()).hasSize(1);
        assertThat(Article.count("lower(title) = lower(?1)", "Brand new article")).isEqualTo(1);
        assertThat(Source.count("url = ?1", "https://example.com/src")).isEqualTo(1);
        assertThat(Tag.count("lower(name) = lower(?1)", "NewTag")).isEqualTo(1);
    }

    @Test
    @TestTransaction
    void addNewArticle_tagNameAlreadyExistsDifferentCase_reusesTagWithoutDuplicate() {
        var existingTag = new Tag();
        existingTag.name = "ReuseTag";
        existingTag.persist();
        var dto =
                new ArticleDto(
                        "Tag reuse article",
                        "https://example.com/logo.png",
                        "Summary",
                        "Text content",
                        null,
                        null,
                        new HashSet<SourceDto>(),
                        new HashSet<>(Set.of(new TagDto("reusetag"))));

        var result = articleService.addNewArticle(dto);

        assertThat(result.tags()).hasSize(1);
        assertThat(result.tags().iterator().next().name()).isEqualToIgnoringCase("reusetag");
        assertThat(Tag.count("lower(name) = lower(?1)", "reusetag")).isEqualTo(1);
    }
}
