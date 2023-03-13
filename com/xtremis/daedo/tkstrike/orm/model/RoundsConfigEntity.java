package com.xtremis.daedo.tkstrike.orm.model;

public interface RoundsConfigEntity {
  Integer getRounds();
  
  void setRounds(Integer paramInteger);
  
  String getRoundTimeStr();
  
  void setRoundTimeStr(String paramString);
  
  String getKyeShiTimeStr();
  
  void setKyeShiTimeStr(String paramString);
  
  String getRestTimeStr();
  
  void setRestTimeStr(String paramString);
  
  Boolean getGoldenPointEnabled();
  
  void setGoldenPointEnabled(Boolean paramBoolean);
  
  String getGoldenPointTimeStr();
  
  void setGoldenPointTimeStr(String paramString);
}
