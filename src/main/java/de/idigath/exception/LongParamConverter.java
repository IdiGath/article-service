package de.idigath.exception;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.Provider;

/**
 * Converts a string path parameter to a {@link Long} and handles invalid numeric formats.
 *
 * <p>By default, when a path parameter fails to convert to the expected type (e.g., {@code
 * "/123ABC"} for a {@code Long} parameter), JAX-RS does not map it to a meaningful HTTP status
 * code. This converter intercepts the conversion process and explicitly throws {@link
 * BadRequestException} (HTTP 400) when the provided value is not a valid number.
 *
 * <p>Without this converter, an invalid numeric ID would incorrectly result in HTTP 404 (Not
 * Found), which misrepresents the actual error — the resource path is correct, but the parameter
 * format is invalid.
 *
 * @see BadRequestException
 * @see ParamConverter
 */
@Provider
public class LongParamConverter implements ParamConverter<Long> {

  @Override
  public Long fromString(String value) {
    try {
      return Long.valueOf(value);
    } catch (NumberFormatException exception) {
      throw new BadRequestException(exception);
    }
  }

  @Override
  public String toString(Long value) {
    return value.toString();
  }
}
