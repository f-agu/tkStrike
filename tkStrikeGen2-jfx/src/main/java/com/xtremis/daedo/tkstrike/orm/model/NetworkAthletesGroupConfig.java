package com.xtremis.daedo.tkstrike.orm.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class NetworkAthletesGroupConfig implements NetworkAthletesGroupConfigEntity {
  @Column(name = "HS_ENABLED")
  private Boolean headSensorsEnabled;
  
  @Column(name = "BS_ENABLED")
  private Boolean bodySensorsEnabled;
  
  @Column(name = "BB_NODE_ID")
  private String bodyBlueNodeId;
  
  @Column(name = "HB_NODE_ID")
  private String headBlueNodeId;
  
  @Column(name = "BR_NODE_ID")
  private String bodyRedNodeId;
  
  @Column(name = "HR_NODE_ID")
  private String headRedNodeId;
  
  public Boolean getHeadSensorsEnabled() {
    return this.headSensorsEnabled;
  }
  
  public void setHeadSensorsEnabled(Boolean headSensorsEnabled) {
    this.headSensorsEnabled = headSensorsEnabled;
  }
  
  public Boolean getBodySensorsEnabled() {
    return this.bodySensorsEnabled;
  }
  
  public void setBodySensorsEnabled(Boolean bodySensorsEnabled) {
    this.bodySensorsEnabled = bodySensorsEnabled;
  }
  
  public String getBodyBlueNodeId() {
    return this.bodyBlueNodeId;
  }
  
  public void setBodyBlueNodeId(String bodyBlueNodeId) {
    this.bodyBlueNodeId = bodyBlueNodeId;
  }
  
  public String getHeadBlueNodeId() {
    return this.headBlueNodeId;
  }
  
  public void setHeadBlueNodeId(String headBlueNodeId) {
    this.headBlueNodeId = headBlueNodeId;
  }
  
  public String getBodyRedNodeId() {
    return this.bodyRedNodeId;
  }
  
  public void setBodyRedNodeId(String bodyRedNodeId) {
    this.bodyRedNodeId = bodyRedNodeId;
  }
  
  public String getHeadRedNodeId() {
    return this.headRedNodeId;
  }
  
  public void setHeadRedNodeId(String headRedNodeId) {
    this.headRedNodeId = headRedNodeId;
  }
}
