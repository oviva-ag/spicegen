package com.oviva.spicegen.api.exceptions;

public class ConflictException extends ClientException {
  public ConflictException(String message) {
    super(message);
  }

  public ConflictException(String message, Throwable cause) {
    super(message, cause);
  }
}
