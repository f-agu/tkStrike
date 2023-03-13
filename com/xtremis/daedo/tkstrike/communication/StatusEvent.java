package com.xtremis.daedo.tkstrike.communication;

public class StatusEvent extends TkStrikeCommunicationBaseEvent {
  private String nodeId;
  
  private Boolean connOffline;
  
  private Boolean sensorOk;
  
  private Double nodeBattery;
  
  private Double nodeBatteryPct;
  
  private final String nativePacket;
  
  public StatusEvent(Long eventTimestamp, NetworkStatus networkStatus, String nodeId, Boolean connOffline, Boolean sensorOk, Double nodeBattery, Double nodeBatteryPct) {
    this(eventTimestamp, networkStatus, nodeId, connOffline, sensorOk, nodeBattery, nodeBatteryPct, null);
  }
  
  public StatusEvent(Long eventTimestamp, NetworkStatus networkStatus, String nodeId, Boolean connOffline, Boolean sensorOk, Double nodeBattery, Double nodeBatteryPct, String nativePacket) {
    super(eventTimestamp, networkStatus);
    this.nodeId = nodeId;
    this.connOffline = connOffline;
    this.sensorOk = sensorOk;
    this.nodeBattery = nodeBattery;
    this.nodeBatteryPct = nodeBatteryPct;
    this.nativePacket = nativePacket;
  }
  
  public String getNodeId() {
    return this.nodeId;
  }
  
  public Boolean getConnOffline() {
    return this.connOffline;
  }
  
  public Boolean getSensorOk() {
    return this.sensorOk;
  }
  
  public Double getNodeBattery() {
    return this.nodeBattery;
  }
  
  public Double getNodeBatteryPct() {
    return this.nodeBatteryPct;
  }
  
  public String getNativePacket() {
    return this.nativePacket;
  }
  
  public String toString() {
    return "StatusEvent{nodeId='" + this.nodeId + '\'' + ", connOffline=" + this.connOffline + ", sensorOk=" + this.sensorOk + ", nodeBattery=" + this.nodeBattery + ", nodeBatteryPct=" + this.nodeBatteryPct + "} " + super
      
      .toString();
  }
}
