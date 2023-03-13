package com.xtremis.daedo.tkstrike.tools.ei.om;

public class TkStrikeNodeStatusEvent extends TkStrikeNodeEvent {
  private static final long serialVersionUID = 5715323875968778059L;
  
  private Boolean connOffline;
  
  private Boolean sensorOk;
  
  private Double nodeBattery;
  
  private Double nodeBatteryPct;
  
  public Boolean getConnOffline() {
    return this.connOffline;
  }
  
  public void setConnOffline(Boolean connOffline) {
    this.connOffline = connOffline;
  }
  
  public Boolean getSensorOk() {
    return this.sensorOk;
  }
  
  public void setSensorOk(Boolean sensorOk) {
    this.sensorOk = sensorOk;
  }
  
  public Double getNodeBattery() {
    return this.nodeBattery;
  }
  
  public void setNodeBattery(Double nodeBattery) {
    this.nodeBattery = nodeBattery;
  }
  
  public Double getNodeBatteryPct() {
    return this.nodeBatteryPct;
  }
  
  public void setNodeBatteryPct(Double nodeBatteryPct) {
    this.nodeBatteryPct = nodeBatteryPct;
  }
}
