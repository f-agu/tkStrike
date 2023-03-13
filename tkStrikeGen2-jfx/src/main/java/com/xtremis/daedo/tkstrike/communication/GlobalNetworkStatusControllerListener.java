package com.xtremis.daedo.tkstrike.communication;

import com.xtremis.daedo.tkstrike.om.NetworkErrorCause;
import java.io.Serializable;

public interface GlobalNetworkStatusControllerListener {
  void hasNewGlobalStatusEvent(StatusEvent paramStatusEvent);
  
  void hasNewNodeNetworkErrorEvent(NodeNetworkErrorEvent paramNodeNetworkErrorEvent);
  
  void hasNetworkOkEvent(NetworkOkEvent paramNetworkOkEvent);
  
  public static class NodeNetworkErrorEvent implements Serializable {
    private Long eventTimestamp;
    
    private Long lastNodeEventTimestamp;
    
    private NetworkNode networkNode;
    
    private NetworkErrorCause.NetworkErrorCauseType networkErrorCause;
    
    public NodeNetworkErrorEvent(Long eventTimestamp, Long lastNodeEventTimestamp, NetworkNode networkNode, NetworkErrorCause.NetworkErrorCauseType networkErrorCause) {
      this.eventTimestamp = eventTimestamp;
      this.lastNodeEventTimestamp = lastNodeEventTimestamp;
      this.networkNode = networkNode;
      this.networkErrorCause = networkErrorCause;
    }
    
    public Long getEventTimestamp() {
      return this.eventTimestamp;
    }
    
    public void setEventTimestamp(Long eventTimestamp) {
      this.eventTimestamp = eventTimestamp;
    }
    
    public Long getLastNodeEventTimestamp() {
      return this.lastNodeEventTimestamp;
    }
    
    public void setLastNodeEventTimestamp(Long lastNodeEventTimestamp) {
      this.lastNodeEventTimestamp = lastNodeEventTimestamp;
    }
    
    public NetworkNode getNetworkNode() {
      return this.networkNode;
    }
    
    public void setNetworkNode(NetworkNode networkNode) {
      this.networkNode = networkNode;
    }
    
    public NetworkErrorCause.NetworkErrorCauseType getNetworkErrorCause() {
      return this.networkErrorCause;
    }
    
    public void setNetworkErrorCause(NetworkErrorCause.NetworkErrorCauseType networkErrorCause) {
      this.networkErrorCause = networkErrorCause;
    }
  }
  
  public static class NetworkOkEvent implements Serializable {
    private Long eventTimestamp;
    
    public NetworkOkEvent(Long eventTimestamp) {
      this.eventTimestamp = eventTimestamp;
    }
    
    public Long getEventTimestamp() {
      return this.eventTimestamp;
    }
    
    public void setEventTimestamp(Long eventTimestamp) {
      this.eventTimestamp = eventTimestamp;
    }
  }
}
