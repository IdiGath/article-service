package de.idigath.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.sql.SQLTimeoutException;

/**
 * Maps {@link SQLTimeoutException} to an HTTP 504 Gateway Timeout response.
 *
 * <p>This exception mapper handles database query timeout exceptions that occur when a database
 * operation exceeds the configured timeout threshold (e.g., {@code
 * quarkus.hibernate-orm.jdbc.timeout}).
 *
 * <p>The mapper converts the technical database exception into a proper HTTP response with status
 * code 504 (Gateway Timeout), indicating that the upstream server (the database) failed to respond
 * in a timely manner.
 *
 * <p>This ensures that API clients receive a meaningful HTTP status code instead of an internal
 * server error (500) or a raw JDBC exception.
 *
 * @see SQLTimeoutException
 * @see jakarta.ws.rs.core.Response.Status#GATEWAY_TIMEOUT
 */
@Provider
public class SqlTimeoutExceptionMapper implements ExceptionMapper<SQLTimeoutException> {

  @Override
  public Response toResponse(SQLTimeoutException exception) {
    return Response.status(Response.Status.GATEWAY_TIMEOUT) // 504
        .entity("Database timeout: " + exception.getMessage())
        .build();
  }
}
