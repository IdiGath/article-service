package de.idigath.exception;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.QueryTimeoutException;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import org.hibernate.exception.JDBCConnectionException;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link QueryTimeoutExceptionMapper}. Pins the contract boundary: only query
 * timeouts map to 504, while connection failures (e.g. a hung or dropped database connection)
 * must fall through to the default 500 handling.
 */
class QueryTimeoutExceptionMapperTest {

  private final QueryTimeoutExceptionMapper mapper = new QueryTimeoutExceptionMapper();

  @Test
  void toResponse_queryTimeout_returns504() {
    Response response = mapper.toResponse(new QueryTimeoutException("canceling statement due to statement timeout"));

    assertThat(response.getStatus()).isEqualTo(504);
    assertThat((String) response.getEntity()).contains("Database timeout");
  }

  @Test
  void mapper_handlesOnlyQueryTimeout_connectionFailureNotMappedTo504() {
    assertThat(QueryTimeoutExceptionMapper.class.getGenericInterfaces()[0].getTypeName())
        .contains("ExceptionMapper<jakarta.persistence.QueryTimeoutException>");
    assertThat(new JDBCConnectionException("connection broken", new SQLException("08006")))
        .isNotInstanceOf(QueryTimeoutException.class);
  }
}
