package de.idigath.exception;

import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.math.BigInteger;

/**
 * Registers {@link BigIntegerParamConverter} to handle all {@link BigInteger} path parameters.
 *
 * <p>This provider intercepts any method parameter of type {@link BigInteger} (annotated with
 * {@code @PathParam}, {@code @QueryParam}, etc.) and delegates the conversion to {@link
 * BigIntegerParamConverter}. This enables centralized validation and error handling for all numeric
 * ID parameters across the entire application.
 *
 * <p>The {@code @SuppressWarnings("unchecked")} is required because the {@code
 * ParamConverterProvider} interface is generic, but the JAX-RS runtime checks types at runtime
 * using {@code rawType}. There is no type-safe way to cast {@code BigIntegerParamConverter} to
 * {@code ParamConverter<T>} without this warning, as {@code T} is erased at compile time.
 *
 * @see ParamConverterProvider
 * @see BigIntegerParamConverter
 */
@Provider
public class BigIntegerParamConverterProvider implements ParamConverterProvider {

  @SuppressWarnings("unchecked")
  @Override
  public <T> ParamConverter<T> getConverter(
      Class<T> rawType, Type genericType, Annotation[] annotations) {
    if (rawType.equals(BigInteger.class)) {
      return (ParamConverter<T>) new BigIntegerParamConverter();
    }
    return null;
  }
}
