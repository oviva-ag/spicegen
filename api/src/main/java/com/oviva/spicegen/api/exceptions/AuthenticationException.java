package com.oviva.spicegen.api.exceptions;

public class AuthenticationException extends ClientException {
  public AuthenticationException(String message) {
    super(message);
  }

  public AuthenticationException(String message, Throwable cause) {
    super(message, cause);
  }
}
