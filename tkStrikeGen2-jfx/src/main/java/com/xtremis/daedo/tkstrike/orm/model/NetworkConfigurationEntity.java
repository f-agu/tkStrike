package com.xtremis.daedo.tkstrike.orm.model;

public interface NetworkConfigurationEntity extends Entity {
  Boolean getNetworkWasStarted();
  
  void setNetworkWasStarted(Boolean paramBoolean);
  
  Integer getChannelNumber();
  
  void setChannelNumber(Integer paramInteger);
  
  Integer getJudgesNumber();
  
  void setJudgesNumber(Integer paramInteger);
  
  String getJudge1NodeId();
  
  void setJudge1NodeId(String paramString);
  
  String getJudge2NodeId();
  
  void setJudge2NodeId(String paramString);
  
  String getJudge3NodeId();
  
  void setJudge3NodeId(String paramString);
  
  Boolean getHeadTechPointEnabled();
  
  void setHeadTechPointEnabled(Boolean paramBoolean);
}
