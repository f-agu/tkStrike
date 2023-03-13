package com.xtremis.daedo.tkstrike.communication;

public class ChangeNetworkStatusEvent extends TkStrikeCommunicationBaseEvent {
  private static final long serialVersionUID = -8506693456696859831L;
  
  private NetworkStatus prevNetworkStatus;
  
  public ChangeNetworkStatusEvent(Long eventTimestamp, NetworkStatus newNetworkStatus, NetworkStatus prevNetworkStatus) {
    super(eventTimestamp, newNetworkStatus);
    this.prevNetworkStatus = prevNetworkStatus;
  }
  
  public NetworkStatus getPrevNetworkStatus() {
    return this.prevNetworkStatus;
  }
}
