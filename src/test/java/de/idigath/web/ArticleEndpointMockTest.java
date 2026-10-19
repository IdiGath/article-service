package de.idigath.web;

import static io.restassured.RestAssured.when;

import de.idigath.service.ArticleService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.persistence.PersistenceException;
import java.sql.SQLTimeoutException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests for the {@link ArticleEndpoint} class using mocks to simulate
 * exceptional scenarios. This
 * class focuses on testing error handling and timeout conditions by mocking the
 * service layer.
 *
 * <p>
 * Unlike integration tests that use a real database, this class uses
 * {@link InjectMock} to
 * replace the {@link ArticleService} with a mock, allowing controlled
 * simulation of database
 * timeout conditions and other edge cases.
 *
 * @see ArticleEndpoint
 * @see SQLTimeoutExceptionMapper
 */
@QuarkusTest
@TestHTTPEndpoint(ArticleEndpoint.class)
public class ArticleEndpointMockTest {

  @InjectMock
  ArticleService articleService;

  @Test
  public void getArticle_serviceThrowsTimeout_returns504() {

    PersistenceException persistenceException = new PersistenceException(new SQLTimeoutException("Query timeout"));

    Mockito.when(articleService.findArticleById(1L)).thenThrow(persistenceException);

    when().get("/" + "1").then().statusCode(504);
  }
}
