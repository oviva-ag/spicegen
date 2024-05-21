package com.oviva.spicegen.spicedbbinding.test;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

// borrowed:
// https://github.com/eugenp/tutorials/blob/master/testing-modules/junit5-annotations/src/test/java/com/baeldung/junit5/templates/GenericTypedParameterResolver.java
public class GenericTypedParameterResolver<T> implements ParameterResolver {
  T data;

  public GenericTypedParameterResolver(T data) {
    this.data = data;
  }

  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isInstance(data);
  }

  @Override
  public Object resolveParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return data;
  }
}
