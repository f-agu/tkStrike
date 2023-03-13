package com.xtremis.daedo.tkstrike.orm.model;

public interface MatchLogItemEntity<MLE extends MatchLogEntity> extends Entity {
  MLE getMatchLog();
  
  void setMatchLog(MLE paramMLE);
  
  Integer getRoundNumber();
  
  void setRoundNumber(Integer paramInteger);
  
  String getRoundNumberStr();
  
  void setRoundNumberStr(String paramString);
  
  Long getEventTime();
  
  void setEventTime(Long paramLong);
  
  Long getRoundTime();
  
  void setRoundTime(Long paramLong);
  
  Long getSystemTime();
  
  void setSystemTime(Long paramLong);
  
  MatchLogItemType getMatchLogItemType();
  
  void setMatchLogItemType(MatchLogItemType paramMatchLogItemType);
  
  Integer getBlueGeneralPoints();
  
  void setBlueGeneralPoints(Integer paramInteger);
  
  Integer getRedGeneralPoints();
  
  void setRedGeneralPoints(Integer paramInteger);
  
  Integer getBlueAddPoints();
  
  void setBlueAddPoints(Integer paramInteger);
  
  Integer getRedAddPoints();
  
  void setRedAddPoints(Integer paramInteger);
  
  Integer getBluePoints();
  
  void setBluePoints(Integer paramInteger);
  
  Integer getRedPoints();
  
  void setRedPoints(Integer paramInteger);
  
  Integer getBluePenalties();
  
  void setBluePenalties(Integer paramInteger);
  
  Integer getRedPenalties();
  
  void setRedPenalties(Integer paramInteger);
  
  Integer getBlueTotalPenalties();
  
  void setBlueTotalPenalties(Integer paramInteger);
  
  Integer getRedTotalPenalties();
  
  void setRedTotalPenalties(Integer paramInteger);
  
  Integer getBlueGoldenPointHits();
  
  void setBlueGoldenPointHits(Integer paramInteger);
  
  Integer getRedGoldenPointHits();
  
  void setRedGoldenPointHits(Integer paramInteger);
  
  Integer getBlueGoldenPointPenalties();
  
  void setBlueGoldenPointPenalties(Integer paramInteger);
  
  Integer getRedGoldenPointPenalties();
  
  void setRedGoldenPointPenalties(Integer paramInteger);
  
  Boolean getGoldenPointRound();
  
  void setGoldenPointRound(Boolean paramBoolean);
  
  String getEntryValue();
  
  void setEntryValue(String paramString);
}
