package de.idigath.web;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import de.idigath.entity.Article;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.util.Collections;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the {@link ArticleEndpoint} class. Verifies HTTP status codes and response
 * content for article search scenarios.
 *
 * <p>The correctness of data mapping and database content is verified in the Mapper and Entity test
 * classes. All test classes use a dedicated test container to ensure isolated test data for each
 * test execution.
 */
@QuarkusTest
@TestHTTPEndpoint(ArticleEndpoint.class)
public class ArticleEndpointTest {

    @Test
    void getArticle_existingSlug_returns200() {
        when().get("/{slug}", "nixos-for-java-developers").then().statusCode(200);
    }

    @Test
    void getArticle_nonExistentSlug_returns404() {
        when().get("/{slug}", "some-nonexistent-article-slug").then().statusCode(404);
    }

    @Test
    void deleteArticle_existingSlug_returns204AndRemovesArticle() {
        String title = "Endpoint delete test article";
        String slug = persistTestArticle(title);

        when().delete("/{slug}", slug).then().statusCode(204);
        when().get("/{slug}", slug).then().statusCode(404);
    }

    @Test
    void deleteArticle_nonExistentSlug_returns404() {
        when().delete("/{slug}", "some-nonexistent-article-slug").then().statusCode(404);
    }

    @Test
    void searchArticle_noParameters_returnsAllArticles() {
        int all = (int) seedArticleCount();

        when().get("/search")
                .then()
                .statusCode(200)
                .body("totalArticles", equalTo(all))
                .body("articleList.size()", equalTo(all))
                .body("currentPage", equalTo(1));
    }

    @Test
    void searchArticle_withPageParameter_returnsFirstPage() {
        int all = (int) seedArticleCount();

        when().get("/search?page=1")
                .then()
                .statusCode(200)
                .body("totalArticles", equalTo(all))
                .body("articleList.size()", equalTo(all))
                .body("currentPage", equalTo(1));
    }

    @Test
    void searchArticle_pageTwo_returnsEmptyListButTotalAll() {
        int all = (int) seedArticleCount();

        when().get("/search?page=2")
                .then()
                .statusCode(200)
                .body("totalArticles", equalTo(all))
                .body("articleList.size()", equalTo(0))
                .body("currentPage", equalTo(2));
    }

    @Test
    void searchArticle_singleTagJava_returnsAllJavaTaggedArticles() {
        int javaTagged = (int) seedArticleCountWithAllTags("Java");

        when().get("/search?tag=Java")
                .then()
                .statusCode(200)
                .body("totalArticles", equalTo(javaTagged))
                .body("articleList.size()", equalTo(javaTagged));
    }

    @Test
    void searchArticle_twoTagsJavaAndPerformance_returnsMatchingArticle() {
        int bothTagged = (int) seedArticleCountWithAllTags("Java", "Performance");

        when().get("/search?page=1&tag=Java&tag=Performance")
                .then()
                .statusCode(200)
                .body("totalArticles", equalTo(bothTagged))
                .body("articleList.size()", equalTo(bothTagged))
                .body("articleList[0].title", equalTo("Java 21: New Features"));
    }

    @Test
    void searchArticle_textWithEncodedPercentSign_returnsNoArticles() {
        when().get("/search?text=%25")
                .then()
                .statusCode(200)
                .body("totalArticles", equalTo(0))
                .body("articleList.size()", equalTo(0));
    }

    @Test
    void editArticle_existingSlug_returns200AndUpdatesArticle() {
        String title = "Endpoint edit test article";
        String slug = persistTestArticle(title);

        given().contentType(ContentType.JSON)
                .body(articleJson(title, "Updated summary"))
                .when()
                .put("/{slug}", slug)
                .then()
                .statusCode(200)
                .body("summary", equalTo("Updated summary"))
                .body("slug", equalTo(slug))
                .header("Last-Modified", notNullValue());

        when().get("/{slug}", slug)
                .then()
                .statusCode(200)
                .body("summary", equalTo("Updated summary"));

        when().delete("/{slug}", slug).then().statusCode(204);
    }

    @Test
    void editArticle_nonExistentSlug_returns404() {
        String title = "some-nonexistent-article-title";

        given().contentType(ContentType.JSON)
                .body(articleJson(title, "Summary"))
                .when()
                .put("/{slug}", "some-nonexistent-article-slug")
                .then()
                .statusCode(404);
    }

    @Test
    void editArticle_timestampsInRequestBody_returnsServerManagedTimestamps() {
        String title = "Endpoint edit timestamps test article";
        String slug = persistTestArticle(title);

        String createdAtBefore =
                when().get("/{slug}", slug).then().statusCode(200).extract().path("createdAt");

        String foreignTimestamp = "2000-01-01T00:00:00";
        given().contentType(ContentType.JSON)
                .body(articleJson(title, "Updated summary", foreignTimestamp))
                .when()
                .put("/{slug}", slug)
                .then()
                .statusCode(200)
                .body("createdAt", equalTo(createdAtBefore))
                .body("updatedAt", not(equalTo(foreignTimestamp)))
                .header("Last-Modified", notNullValue());

        when().get("/{slug}", slug)
                .then()
                .statusCode(200)
                .body("createdAt", equalTo(createdAtBefore));

        when().delete("/{slug}", slug).then().statusCode(204);
    }

    @Test
    void editArticle_missingTitle_returns400AndLeavesArticleUnchanged() {
        String title = "Endpoint validation title test article";
        String slug = persistTestArticle(title);

        String bodyWithoutTitle =
                """
                {
                  "logoUrl": "https://example.com/logos/test.png",
                  "summary": "Summary",
                  "textContent": "Test content",
                  "sources": [],
                  "tags": []
                }
                """;
        given().contentType(ContentType.JSON)
                .body(bodyWithoutTitle)
                .when()
                .put("/{slug}", slug)
                .then()
                .statusCode(400)
                .body("errors", hasItem(containsString("title")));

        when().get("/{slug}", slug)
                .then()
                .statusCode(200)
                .body("title", equalTo(title))
                .body("summary", equalTo("Test summary"));

        when().delete("/{slug}", slug).then().statusCode(204);
    }

    @Test
    void editArticle_tooLongSummary_returns400AndLeavesArticleUnchanged() {
        String title = "Endpoint validation summary test article";
        String slug = persistTestArticle(title);

        given().contentType(ContentType.JSON)
                .body(articleJson(title, "x".repeat(501)))
                .when()
                .put("/{slug}", slug)
                .then()
                .statusCode(400)
                .body("errors", hasItem(containsString("summary")));

        when().get("/{slug}", slug).then().statusCode(200).body("summary", equalTo("Test summary"));

        when().delete("/{slug}", slug).then().statusCode(204);
    }

    @Test
    void editArticle_nullSources_returns400() {
        String title = "Endpoint validation sources test article";
        String slug = persistTestArticle(title);

        String bodyWithoutSources =
                """
                {
                  "title": "%s",
                  "logoUrl": "https://example.com/logos/test.png",
                  "summary": "Summary",
                  "textContent": "Test content",
                  "tags": []
                }
                """
                        .formatted(title);
        given().contentType(ContentType.JSON)
                .body(bodyWithoutSources)
                .when()
                .put("/{slug}", slug)
                .then()
                .statusCode(400)
                .body("errors", hasItem(containsString("sources")));

        when().delete("/{slug}", slug).then().statusCode(204);
    }

    @Test
    void getArticle_tableLockedByOtherTransaction_returns504() {
        // statement_timeout=1s applies to every pooled connection via
        // quarkus.datasource.jdbc.additional-jdbc-properties in
        // src/test/resources/application.properties,
        // so the request blocked on the lock times out as QueryTimeoutException -> 504.
        QuarkusTransaction.requiringNew()
                .run(() -> {
                    Article.getEntityManager()
                            .createNativeQuery("LOCK TABLE articles IN ACCESS EXCLUSIVE MODE")
                            .executeUpdate();

                    when().get("/{slug}", "nixos-for-java-developers").then().statusCode(504);
                });
    }

    private long seedArticleCount() {
        return QuarkusTransaction.requiringNew()
                .call(() ->
                        ((Number)
                                        Article.getEntityManager()
                                                .createNativeQuery("SELECT count(*) FROM articles")
                                                .getSingleResult())
                                .longValue());
    }

    /**
     * Mirrors {@link Article#findByTitleAndTags} tag semantics: an article matches when it has ALL
     * given tags (case-insensitive).
     */
    private long seedArticleCountWithAllTags(String... tagNames) {
        String placeholders = String.join(",", Collections.nCopies(tagNames.length, "?"));
        return QuarkusTransaction.requiringNew()
                .call(() -> {
                    var query =
                            Article.getEntityManager()
                                    .createNativeQuery(
                                            "SELECT count(*) FROM (SELECT a.id FROM articles a"
                                                    + " JOIN article_tags at ON at.article_id = a.id"
                                                    + " JOIN tags t ON t.id = at.tag_id"
                                                    + " WHERE lower(t.name) IN ("
                                                    + placeholders
                                                    + ")"
                                                    + " GROUP BY a.id HAVING count(DISTINCT t.id) = "
                                                    + tagNames.length
                                                    + ") matched");
                    for (int i = 0; i < tagNames.length; i++) {
                        query.setParameter(i + 1, tagNames[i].toLowerCase());
                    }
                    return ((Number) query.getSingleResult()).longValue();
                });
    }

    private String persistTestArticle(String title) {
        String slug = slugOf(title);
        QuarkusTransaction.requiringNew()
                .run(() -> {
                    Article article = new Article();
                    article.title = title;
                    article.slug = slug;
                    article.logoUrl = "https://example.com/logos/test.png";
                    article.summary = "Test summary";
                    article.textContent = "Test content";
                    article.persist();
                });
        return slug;
    }

    private static String slugOf(String title) {
        return title.toLowerCase().replaceAll("[^\\p{L}\\p{N}]+", "-").replaceAll("^-+|-+$", "");
    }

    private String articleJson(String title, String summary) {
        return """
        {
          "title": "%s",
          "logoUrl": "https://example.com/logos/test.png",
          "summary": "%s",
          "textContent": "Test content",
          "sources": [],
          "tags": []
        }
        """
                .formatted(title, summary);
    }

    private String articleJson(String title, String summary, String timestamp) {
        return """
        {
          "title": "%s",
          "logoUrl": "https://example.com/logos/test.png",
          "summary": "%s",
          "textContent": "Test content",
          "createdAt": "%s",
          "updatedAt": "%s",
          "sources": [],
          "tags": []
        }
        """
                .formatted(title, summary, timestamp, timestamp);
    }
}
