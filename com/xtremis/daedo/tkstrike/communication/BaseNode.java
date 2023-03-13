package com.xtremis.daedo.tkstrike.communication;

public abstract class BaseNode implements NetworkNode {
  private static final long serialVersionUID = 2627627198795347633L;
  
  private String nodeId;
  
  private int nodeOfflineTimes;
  
  private long lastTimestampStatusOk;
  
  private double batteryPct;
  
  private Boolean nodeStatusOk;
  
  public BaseNode(String nodeId, long lastTimestampStatusOk, double batteryPct, Boolean nodeStatusOk) {
    this.nodeOfflineTimes = 0;
    this.nodeId = nodeId;
    this.lastTimestampStatusOk = lastTimestampStatusOk;
    this.batteryPct = batteryPct;
    this.nodeStatusOk = nodeStatusOk;
  }
  
  public BaseNode(String nodeId) {
    this(nodeId, 0L, 0.0D, Boolean.valueOf(true));
  }
  
  public String getNodeId() {
    return this.nodeId;
  }
  
  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }
  
  public long getLastTimestampStatusOk() {
    return this.lastTimestampStatusOk;
  }
  
  public void setLastTimestampStatusOk(long lastTimestampStatusOk) {
    this.lastTimestampStatusOk = lastTimestampStatusOk;
  }
  
  public double getBatteryPct() {
    return this.batteryPct;
  }
  
  public void setBatteryPct(double batteryPct) {
    this.batteryPct = batteryPct;
  }
  
  public Boolean getNodeStatusOk() {
    return this.nodeStatusOk;
  }
  
  public void setNodeStatusOk(Boolean nodeStatusOk) {
    this.nodeStatusOk = nodeStatusOk;
  }
  
  public int getNodeOfflineTimes() {
    return this.nodeOfflineTimes;
  }
  
  public void setNodeOfflineTimes(int nodeOfflineTimes) {
    this.nodeOfflineTimes = nodeOfflineTimes;
  }
}
