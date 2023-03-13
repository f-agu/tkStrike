package com.xtremis.daedo.tkstrike.tools.ei.om;

public class TkStrikeNodeDataEvent extends TkStrikeNodeEvent {
  private static final long serialVersionUID = -1636849947851259271L;
  
  private Integer hitValue;
  
  private String dataEventHitType;
  
  public Integer getHitValue() {
    return this.hitValue;
  }
  
  public void setHitValue(Integer hitValue) {
    this.hitValue = hitValue;
  }
  
  public String getDataEventHitType() {
    return this.dataEventHitType;
  }
  
  public void setDataEventHitType(String dataEventHitType) {
    this.dataEventHitType = dataEventHitType;
  }
}
