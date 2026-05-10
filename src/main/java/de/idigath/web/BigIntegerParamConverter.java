package de.idigath.web;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.Provider;
import java.math.BigInteger;

/**
 * Converts a string path parameter to a {@link BigInteger} and handles invalid numeric formats.
 *
 * <p>By default, when a path parameter fails to convert to the expected type (e.g., {@code
 * "/123ABC"} for a {@code BigInteger} parameter), JAX-RS does not map it to a meaningful HTTP
 * status code. This converter intercepts the conversion process and explicitly throws {@link
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
public class BigIntegerParamConverter implements ParamConverter<BigInteger> {

  @Override
  public BigInteger fromString(String value) {
    try {
      return new BigInteger(value);
    } catch (NumberFormatException exception) {
      throw new BadRequestException(exception);
    }
  }

  @Override
  public String toString(BigInteger value) {
    return value.toString();
  }
}
