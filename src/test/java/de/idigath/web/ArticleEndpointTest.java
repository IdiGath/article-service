package de.idigath.web;

import static io.restassured.RestAssured.when;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link ArticleEndpoint} class. Each test scenario verifies the expected HTTP status
 * code for a specific case. This class focuses solely on HTTP response validation and does not
 * check the response content.
 *
 * <p>The correctness of data mapping and database content is verified in the Mapper and Entity test
 * classes. All test classes use a dedicated test container to ensure isolated test data for each
 * test execution.
 */
@QuarkusTest
@TestHTTPEndpoint(ArticleEndpoint.class)
public class ArticleEndpointTest {

  /**
   * Tests the successful retrieval of an article by its ID. Expects HTTP status code 200 (OK) for a
   * valid article ID.
   */
  @Test
  public void testGetArticleHappyCase200() {
    when().get("/" + "151").then().statusCode(200);
  }

  @Test
  public void testGetArticleWrongNumber() {
    when().get("/" + "9999999").then().statusCode(404);
  }
}
