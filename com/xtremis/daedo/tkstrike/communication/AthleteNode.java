package com.xtremis.daedo.tkstrike.communication;

public class AthleteNode extends BaseNode {
  private static final long serialVersionUID = -3864376132979998233L;
  
  private final Integer groupNumber;
  
  private Boolean blue;
  
  private Boolean body;
  
  public AthleteNode(Integer groupNumber, String nodeId, long lastTimestampStatusOk, double batteryPct, Boolean nodeStatusOk) {
    super(nodeId, lastTimestampStatusOk, batteryPct, nodeStatusOk);
    this.groupNumber = groupNumber;
  }
  
  public AthleteNode(Integer groupNumber, String nodeId, Boolean blue, Boolean body) {
    super(nodeId);
    this.groupNumber = groupNumber;
    this.blue = blue;
    this.body = body;
  }
  
  public AthleteNode(Integer groupNumber, String nodeId) {
    super(nodeId);
    this.groupNumber = groupNumber;
  }
  
  public Integer getGroupNumber() {
    return this.groupNumber;
  }
  
  public Boolean getBlue() {
    return this.blue;
  }
  
  public void setBlue(Boolean blue) {
    this.blue = blue;
  }
  
  public Boolean getBody() {
    return this.body;
  }
  
  public void setBody(Boolean body) {
    this.body = body;
  }
}
