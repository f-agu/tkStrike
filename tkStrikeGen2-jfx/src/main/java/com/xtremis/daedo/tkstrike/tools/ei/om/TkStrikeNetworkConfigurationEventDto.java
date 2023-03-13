package com.xtremis.daedo.tkstrike.tools.ei.om;

import com.xtremis.daedo.tkstrike.tools.om.BaseJSONDto;

public class TkStrikeNetworkConfigurationEventDto extends BaseJSONDto {
  private static final long serialVersionUID = -2523889173064143901L;
  
  private Integer judgesNumber;
  
  private String judge1NodeId;
  
  private String judge2NodeId;
  
  private String judge3NodeId;
  
  private Boolean headSensorsEnabled;
  
  private Boolean bodySensorsEnabled;
  
  private String bodyBlueNodeId;
  
  private String headBlueNodeId;
  
  private String bodyRedNodeId;
  
  private String headRedNodeId;
  
  public Integer getJudgesNumber() {
    return this.judgesNumber;
  }
  
  public void setJudgesNumber(Integer judgesNumber) {
    this.judgesNumber = judgesNumber;
  }
  
  public String getJudge1NodeId() {
    return this.judge1NodeId;
  }
  
  public void setJudge1NodeId(String judge1NodeId) {
    this.judge1NodeId = judge1NodeId;
  }
  
  public String getJudge2NodeId() {
    return this.judge2NodeId;
  }
  
  public void setJudge2NodeId(String judge2NodeId) {
    this.judge2NodeId = judge2NodeId;
  }
  
  public String getJudge3NodeId() {
    return this.judge3NodeId;
  }
  
  public void setJudge3NodeId(String judge3NodeId) {
    this.judge3NodeId = judge3NodeId;
  }
  
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
