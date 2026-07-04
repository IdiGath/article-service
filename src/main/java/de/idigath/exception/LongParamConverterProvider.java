package de.idigath.exception;

import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Registers {@link LongParamConverter} to handle all {@link Long} path parameters.
 *
 * <p>This provider intercepts any method parameter of type {@link Long} (annotated with
 * {@code @PathParam}, {@code @QueryParam}, etc.) and delegates the conversion to {@link
 * LongParamConverter}. This enables centralized validation and error handling for all numeric ID
 * parameters across the entire application.
 *
 * <p>The {@code @SuppressWarnings("unchecked")} is required because the {@code
 * ParamConverterProvider} interface is generic, but the JAX-RS runtime checks types at runtime
 * using {@code rawType}. There is no type-safe way to cast {@code LongParamConverter} to {@code
 * ParamConverter<T>} without this warning, as {@code T} is erased at compile time.
 *
 * @see ParamConverterProvider
 * @see LongParamConverter
 */
@Provider
public class LongParamConverterProvider implements ParamConverterProvider {

  @SuppressWarnings("unchecked")
  @Override
  public <T> ParamConverter<T> getConverter(
      Class<T> rawType, Type genericType, Annotation[] annotations) {
    if (rawType.equals(Long.class)) {
      return (ParamConverter<T>) new LongParamConverter();
    }
    return null;
  }
}
