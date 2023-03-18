package org.crm.tkstrike.testnetwork;

public abstract class TkStrikeCommunicationBaseEvent implements TkStrikeCommunicationEvent {
  private static final long serialVersionUID = -5466770107112062174L;
  
  private Long eventTimestamp;
  
  private NetworkStatus networkStatus;
  
  public TkStrikeCommunicationBaseEvent(Long eventTimestamp, NetworkStatus networkStatus) {
    this.eventTimestamp = eventTimestamp;
    this.networkStatus = networkStatus;
  }
  
  public Long getEventTimestamp() {
    return this.eventTimestamp;
  }
  
  public NetworkStatus getNetworkStatus() {
    return this.networkStatus;
  }
}
