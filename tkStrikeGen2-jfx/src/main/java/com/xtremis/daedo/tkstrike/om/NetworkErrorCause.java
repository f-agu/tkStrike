package com.xtremis.daedo.tkstrike.om;

public class NetworkErrorCause {
  private final NetworkErrorCauseType networkErrorCauseType;
  
  private final boolean isBody;
  
  private final boolean isHead;
  
  private final boolean isBlue;
  
  public NetworkErrorCause(NetworkErrorCauseType networkErrorCauseType, boolean isBody, boolean isHead, boolean isBlue) {
    this.networkErrorCauseType = networkErrorCauseType;
    this.isBody = isBody;
    this.isHead = isHead;
    this.isBlue = isBlue;
  }
  
  public NetworkErrorCauseType getNetworkErrorCauseType() {
    return this.networkErrorCauseType;
  }
  
  public boolean isBody() {
    return this.isBody;
  }
  
  public boolean isHead() {
    return this.isHead;
  }
  
  public boolean isBlue() {
    return this.isBlue;
  }
  
  public enum NetworkErrorCauseType {
    LOST_CONNECTION, SENSOR_ERROR, LOW_BATTERY;
  }
}
