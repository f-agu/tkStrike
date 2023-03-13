package com.xtremis.daedo.tkstrike.tools.ei.om;

import com.xtremis.daedo.tkstrike.tools.om.BaseJSONDto;

public abstract class TkStrikeNodeEvent extends BaseJSONDto {
  private static final long serialVersionUID = -3925286156180726228L;
  
  private Long eventTimestamp;
  
  private TkStrikeNodeEventSource tkStrikeNodeEventSource;
  
  private String networkStatus;
  
  private String nodeId;
  
  private String nativePacket;
  
  public TkStrikeNodeEventSource getTkStrikeNodeEventSource() {
    return this.tkStrikeNodeEventSource;
  }
  
  public void setTkStrikeNodeEventSource(TkStrikeNodeEventSource tkStrikeNodeEventSource) {
    this.tkStrikeNodeEventSource = tkStrikeNodeEventSource;
  }
  
  public Long getEventTimestamp() {
    return this.eventTimestamp;
  }
  
  public void setEventTimestamp(Long eventTimestamp) {
    this.eventTimestamp = eventTimestamp;
  }
  
  public String getNetworkStatus() {
    return this.networkStatus;
  }
  
  public void setNetworkStatus(String networkStatus) {
    this.networkStatus = networkStatus;
  }
  
  public String getNodeId() {
    return this.nodeId;
  }
  
  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }
  
  public String getNativePacket() {
    return this.nativePacket;
  }
  
  public void setNativePacket(String nativePacket) {
    this.nativePacket = nativePacket;
  }
}
