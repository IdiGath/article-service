package de.idigath.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ConstraintViolationExceptionMapper}. Pins the contract: validation failures
 * map to 400 with a JSON body listing {@code "field: message"} entries.
 */
class ConstraintViolationExceptionMapperTest {

    private final ConstraintViolationExceptionMapper mapper =
            new ConstraintViolationExceptionMapper();

    @Test
    @SuppressWarnings("unchecked")
    void toResponse_violations_returns400WithJsonFieldList() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        Path.Node node = mock(Path.Node.class);
        when(node.getName()).thenReturn("title");
        when(path.iterator()).thenReturn(List.of(node).iterator());
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be blank");

        Response response = mapper.toResponse(new ConstraintViolationException(Set.of(violation)));

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getMediaType()).isEqualTo(MediaType.APPLICATION_JSON_TYPE);
        assertThat((Map<String, List<String>>) response.getEntity())
                .isEqualTo(Map.of("errors", List.of("title: must not be blank")));
    }
}
