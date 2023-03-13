package com.xtremis.daedo.tkstrike.orm.model;

public interface NetworkAthletesGroupConfigEntity {
  Boolean getHeadSensorsEnabled();
  
  void setHeadSensorsEnabled(Boolean paramBoolean);
  
  Boolean getBodySensorsEnabled();
  
  void setBodySensorsEnabled(Boolean paramBoolean);
  
  String getBodyBlueNodeId();
  
  void setBodyBlueNodeId(String paramString);
  
  String getHeadBlueNodeId();
  
  void setHeadBlueNodeId(String paramString);
  
  String getBodyRedNodeId();
  
  void setBodyRedNodeId(String paramString);
  
  String getHeadRedNodeId();
  
  void setHeadRedNodeId(String paramString);
}
