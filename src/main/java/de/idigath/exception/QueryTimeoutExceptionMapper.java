package de.idigath.exception;

import jakarta.persistence.QueryTimeoutException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Maps {@link QueryTimeoutException} to an HTTP 504 Gateway Timeout response.
 *
 * <p>This exception mapper handles database query timeout exceptions that occur when a database
 * operation exceeds the configured statement timeout (PostgreSQL {@code statement_timeout}).
 *
 * <p>The mapper converts the technical database exception into a proper HTTP response with status
 * code 504 (Gateway Timeout), indicating that the upstream server (the database) failed to respond
 * in a timely manner.
 *
 * <p>This ensures that API clients receive a meaningful HTTP status code instead of an internal
 * server error (500) or a raw JDBC exception.
 *
 * @see QueryTimeoutException
 * @see jakarta.ws.rs.core.Response.Status#GATEWAY_TIMEOUT
 */
@Provider
public class QueryTimeoutExceptionMapper implements ExceptionMapper<QueryTimeoutException> {

  @Override
  public Response toResponse(QueryTimeoutException exception) {
    return Response.status(Response.Status.GATEWAY_TIMEOUT) // 504
        .entity("Database timeout: " + exception.getMessage())
        .build();
  }
}
