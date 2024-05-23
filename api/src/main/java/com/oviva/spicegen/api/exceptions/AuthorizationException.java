package com.oviva.spicegen.api.exceptions;

public class AuthorizationException extends ClientException {
  public AuthorizationException(String message) {
    super(message);
  }

  public AuthorizationException(String message, Throwable cause) {
    super(message, cause);
  }
}
