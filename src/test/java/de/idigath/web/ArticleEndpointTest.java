package de.idigath.web;

import static io.restassured.RestAssured.when;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestHTTPEndpoint(ArticleEndpoint.class)
public class ArticleEndpointTest {

  @Test
  public void testGetArticleHappyCase() {
    when().get("/" + "151").then().statusCode(200);
  }
}
