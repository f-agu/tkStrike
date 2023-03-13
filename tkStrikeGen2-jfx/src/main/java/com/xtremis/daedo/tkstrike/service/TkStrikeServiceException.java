package com.xtremis.daedo.tkstrike.service;

public class TkStrikeServiceException extends Exception {
  public TkStrikeServiceException() {}
  
  public TkStrikeServiceException(String message) {
    super(message);
  }
  
  public TkStrikeServiceException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public TkStrikeServiceException(Throwable cause) {
    super(cause);
  }
  
  public TkStrikeServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
