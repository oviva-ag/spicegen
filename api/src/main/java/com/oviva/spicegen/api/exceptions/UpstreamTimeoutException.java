package com.oviva.spicegen.api.exceptions;

public class UpstreamTimeoutException extends ClientException {

  public UpstreamTimeoutException(String message) {
    super(message);
  }

  public UpstreamTimeoutException(String message, Throwable cause) {
    super(message, cause);
  }
}
