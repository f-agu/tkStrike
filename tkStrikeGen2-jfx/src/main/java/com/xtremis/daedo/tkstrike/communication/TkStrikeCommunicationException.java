package com.xtremis.daedo.tkstrike.communication;

public class TkStrikeCommunicationException extends RuntimeException {
  public TkStrikeCommunicationException() {}
  
  public TkStrikeCommunicationException(String message) {
    super(message);
  }
  
  public TkStrikeCommunicationException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public TkStrikeCommunicationException(Throwable cause) {
    super(cause);
  }
  
  public TkStrikeCommunicationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
