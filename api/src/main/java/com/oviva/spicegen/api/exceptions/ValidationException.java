package com.oviva.spicegen.api.exceptions;

public class ValidationException extends ClientException {
  public ValidationException(String message) {
    super(message);
  }

  public ValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}
