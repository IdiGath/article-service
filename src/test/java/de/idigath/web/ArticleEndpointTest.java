package de.idigath.web;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

import de.idigath.entity.Article;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
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
  public void getArticle_existingTitle_returns200() {
    when().get("/{title}", "NixOS for Java Developers").then().statusCode(200);
  }

  @Test
  public void getArticle_nonExistentTitle_returns404() {
    when().get("/" + "some-nonexistent-article-title").then().statusCode(404);
  }

  @Test
  public void deleteArticle_existingTitle_returns204AndRemovesArticle() {
    String title = "Endpoint delete test article";
    QuarkusTransaction.requiringNew().run(() -> {
      Article article = new Article();
      article.title = title;
      article.logoUrl = "https://example.com/logos/delete-test.png";
      article.summary = "Delete test summary";
      article.textContent = "Delete test content";
      article.persist();
    });

    when().delete("/{title}", title).then().statusCode(204);
    when().get("/{title}", title).then().statusCode(404);
  }

  @Test
  public void deleteArticle_nonExistentTitle_returns404() {
    when().delete("/" + "some-nonexistent-article-title").then().statusCode(404);
  }

  @Test
  public void searchArticle_noParameters_returnsAllArticles() {
    when()
        .get("/search")
        .then()
        .statusCode(200)
        .body("totalArticles", equalTo(5))
        .body("articleList.size()", equalTo(5));
  }

  @Test
  public void searchArticle_withPageParameter_returnsFirstPage() {
    when()
        .get("/search?page=1")
        .then()
        .statusCode(200)
        .body("totalArticles", equalTo(5))
        .body("articleList.size()", equalTo(5));
  }

  @Test
  public void searchArticle_pageTwo_returnsEmptyListButTotalFive() {
    when()
        .get("/search?page=2")
        .then()
        .statusCode(200)
        .body("totalArticles", equalTo(5))
        .body("articleList.size()", equalTo(0));
  }

  @Test
  public void searchArticle_singleTagJava_returnsThreeArticles() {
    when()
        .get("/search?tag=Java")
        .then()
        .statusCode(200)
        .body("totalArticles", equalTo(3))
        .body("articleList.size()", equalTo(3));
  }

  @Test
  public void searchArticle_twoTagsJavaAndPerformance_returnsOneMatchingArticle() {
    when()
        .get("/search?page=1&tag=Java&tag=Performance")
        .then()
        .statusCode(200)
        .body("totalArticles", equalTo(1))
        .body("articleList.size()", equalTo(1))
        .body("articleList[0].title", equalTo("Java 21: New Features"));
  }

  @Test
  public void searchArticle_textWithEncodedPercentSign_returnsNoArticles() {
    when()
        .get("/search?text=%25")
        .then()
        .statusCode(200)
        .body("totalArticles", equalTo(0))
        .body("articleList.size()", equalTo(0));
  }

}
