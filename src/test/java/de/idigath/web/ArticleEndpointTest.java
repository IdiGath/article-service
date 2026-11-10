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
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the {@link ArticleEndpoint} class. Verifies HTTP status
 * codes and response content for article search scenarios.
 *
 * <p>
 * The correctness of data mapping and database content is verified in the
 * Mapper and Entity test classes. All test classes use a dedicated test
 * container to ensure isolated test data for each test execution.
 */
@QuarkusTest
@TestHTTPEndpoint(ArticleEndpoint.class)
public class ArticleEndpointTest {

  @Test
  void getArticle_existingTitle_returns200() {
    when().get("/{title}", "NixOS for Java Developers").then().statusCode(200);
  }

  @Test
  void getArticle_nonExistentTitle_returns404() {
    when().get("/" + "some-nonexistent-article-title").then().statusCode(404);
  }

  @Test
  void deleteArticle_existingTitle_returns204AndRemovesArticle() {
    String title = "Endpoint delete test article";
    persistTestArticle(title);

    when().delete("/{title}", title).then().statusCode(204);
    when().get("/{title}", title).then().statusCode(404);
  }

  @Test
  void deleteArticle_nonExistentTitle_returns404() {
    when().delete("/" + "some-nonexistent-article-title").then().statusCode(404);
  }

  @Test
  void searchArticle_noParameters_returnsAllArticles() {
    when()
        .get("/search")
        .then()
        .statusCode(200)
        .body("totalArticles", equalTo(5))
        .body("articleList.size()", equalTo(5))
        .body("currentPage", equalTo(1));
  }

  @Test
  void searchArticle_withPageParameter_returnsFirstPage() {
    when()
        .get("/search?page=1")
        .then()
        .statusCode(200)
        .body("totalArticles", equalTo(5))
        .body("articleList.size()", equalTo(5))
        .body("currentPage", equalTo(1));
  }

  @Test
  void searchArticle_pageTwo_returnsEmptyListButTotalFive() {
    when()
        .get("/search?page=2")
        .then()
        .statusCode(200)
        .body("totalArticles", equalTo(5))
        .body("articleList.size()", equalTo(0))
        .body("currentPage", equalTo(2));
  }

  @Test
  void searchArticle_singleTagJava_returnsThreeArticles() {
    when()
        .get("/search?tag=Java")
        .then()
        .statusCode(200)
        .body("totalArticles", equalTo(3))
        .body("articleList.size()", equalTo(3));
  }

  @Test
  void searchArticle_twoTagsJavaAndPerformance_returnsOneMatchingArticle() {
    when()
        .get("/search?page=1&tag=Java&tag=Performance")
        .then()
        .statusCode(200)
        .body("totalArticles", equalTo(1))
        .body("articleList.size()", equalTo(1))
        .body("articleList[0].title", equalTo("Java 21: New Features"));
  }

  @Test
  void searchArticle_textWithEncodedPercentSign_returnsNoArticles() {
    when()
        .get("/search?text=%25")
        .then()
        .statusCode(200)
        .body("totalArticles", equalTo(0))
        .body("articleList.size()", equalTo(0));
  }

  @Test
  void editArticle_existingTitle_returns200AndUpdatesArticle() {
    String title = "Endpoint edit test article";
    persistTestArticle(title);

    given().contentType(ContentType.JSON).body(articleJson(title, "Updated summary"))
        .when().put("/{title}", title)
        .then().statusCode(200)
        .body("summary", equalTo("Updated summary"))
        .header("Last-Modified", notNullValue());

    when().get("/{title}", title).then().statusCode(200).body("summary", equalTo("Updated summary"));

    when().delete("/{title}", title).then().statusCode(204);
  }

  @Test
  void editArticle_nonExistentTitle_returns404() {
    String title = "some-nonexistent-article-title";

    given().contentType(ContentType.JSON).body(articleJson(title, "Summary"))
        .when().put("/{title}", title)
        .then().statusCode(404);
  }

  @Test
  void editArticle_timestampsInRequestBody_returnsServerManagedTimestamps() {
    String title = "Endpoint edit timestamps test article";
    persistTestArticle(title);

    String createdAtBefore = when().get("/{title}", title)
        .then().statusCode(200)
        .extract().path("createdAt");

    String foreignTimestamp = "2000-01-01T00:00:00";
    given().contentType(ContentType.JSON).body(articleJson(title, "Updated summary", foreignTimestamp))
        .when().put("/{title}", title)
        .then().statusCode(200)
        .body("createdAt", equalTo(createdAtBefore))
        .body("updatedAt", not(equalTo(foreignTimestamp)))
        .header("Last-Modified", notNullValue());

    when().get("/{title}", title)
        .then().statusCode(200)
        .body("createdAt", equalTo(createdAtBefore));

    when().delete("/{title}", title).then().statusCode(204);
  }

  @Test
  void editArticle_missingTitle_returns400AndLeavesArticleUnchanged() {
    String title = "Endpoint validation title test article";
    persistTestArticle(title);

    String bodyWithoutTitle = """
        {
          "logoUrl": "https://example.com/logos/test.png",
          "summary": "Summary",
          "textContent": "Test content",
          "sources": [],
          "tags": []
        }
        """;
    given().contentType(ContentType.JSON).body(bodyWithoutTitle)
        .when().put("/{title}", title)
        .then().statusCode(400)
        .body("errors", hasItem(containsString("title")));

    when().get("/{title}", title)
        .then().statusCode(200)
        .body("title", equalTo(title))
        .body("summary", equalTo("Test summary"));

    when().delete("/{title}", title).then().statusCode(204);
  }

  @Test
  void editArticle_tooLongSummary_returns400AndLeavesArticleUnchanged() {
    String title = "Endpoint validation summary test article";
    persistTestArticle(title);

    given().contentType(ContentType.JSON).body(articleJson(title, "x".repeat(501)))
        .when().put("/{title}", title)
        .then().statusCode(400)
        .body("errors", hasItem(containsString("summary")));

    when().get("/{title}", title)
        .then().statusCode(200)
        .body("summary", equalTo("Test summary"));

    when().delete("/{title}", title).then().statusCode(204);
  }

  @Test
  void editArticle_nullSources_returns400() {
    String title = "Endpoint validation sources test article";
    persistTestArticle(title);

    String bodyWithoutSources = """
        {
          "title": "%s",
          "logoUrl": "https://example.com/logos/test.png",
          "summary": "Summary",
          "textContent": "Test content",
          "tags": []
        }
        """.formatted(title);
    given().contentType(ContentType.JSON).body(bodyWithoutSources)
        .when().put("/{title}", title)
        .then().statusCode(400)
        .body("errors", hasItem(containsString("sources")));

    when().delete("/{title}", title).then().statusCode(204);
  }

  @Test
  void getArticle_tableLockedByOtherTransaction_returns504() {
    // statement_timeout=1s applies to every pooled connection via
    // quarkus.datasource.jdbc.additional-jdbc-properties in src/test/resources/application.properties,
    // so the request blocked on the lock times out as QueryTimeoutException -> 504.
    QuarkusTransaction.requiringNew().run(() -> {
      Article.getEntityManager()
          .createNativeQuery("LOCK TABLE articles IN ACCESS EXCLUSIVE MODE")
          .executeUpdate();

      when().get("/{title}", "NixOS for Java Developers").then().statusCode(504);
    });
  }

  private void persistTestArticle(String title) {
    QuarkusTransaction.requiringNew().run(() -> {
      Article article = new Article();
      article.title = title;
      article.logoUrl = "https://example.com/logos/test.png";
      article.summary = "Test summary";
      article.textContent = "Test content";
      article.persist();
    });
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
        """.formatted(title, summary);
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
        """.formatted(title, summary, timestamp, timestamp);
  }

}
