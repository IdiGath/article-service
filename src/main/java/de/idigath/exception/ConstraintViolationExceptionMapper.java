package de.idigath.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import java.util.Map;

/**
 * Maps {@link ConstraintViolationException} (Bean Validation failures on request bodies, e.g. from
 * {@code @Valid} endpoint parameters) to an HTTP 400 Bad Request response.
 *
 * <p>The response body is a JSON object with an {@code errors} array of {@code "field: message"}
 * entries, one per violated constraint, so clients can pinpoint the invalid fields instead of
 * receiving a 500 from the database layer.
 *
 * @see ConstraintViolationException
 * @see jakarta.ws.rs.core.Response.Status#BAD_REQUEST
 */
@Provider
public class ConstraintViolationExceptionMapper
        implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<String> errors =
                exception.getConstraintViolations().stream()
                        .map(violation ->
                                leafField(violation.getPropertyPath())
                                        + ": "
                                        + violation.getMessage())
                        .sorted()
                        .toList();
        return Response.status(Response.Status.BAD_REQUEST) // 400
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("errors", errors))
                .build();
    }

    private static String leafField(Path path) {
        String leaf = null;
        for (Path.Node node : path) {
            leaf = node.getName();
        }
        return leaf != null ? leaf : "request";
    }
}
