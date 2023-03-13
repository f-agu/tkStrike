package com.xtremis.daedo.tkstrike.orm.model;

import com.xtremis.daedo.tkstrike.om.combat.FinalDecision;
import com.xtremis.daedo.tkstrike.om.combat.MatchWinner;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchVictoryCriteria;

public interface MatchLogEntity<RC extends RoundsConfigEntity> extends Entity {
  Boolean getBodySensorsEnabled();
  
  void setBodySensorsEnabled(Boolean paramBoolean);
  
  Boolean getHeadSensorsEnabled();
  
  void setHeadSensorsEnabled(Boolean paramBoolean);
  
  String getMatchConfigurationId();
  
  void setMatchConfigurationId(String paramString);
  
  Integer getNumberOfJudges();
  
  void setNumberOfJudges(Integer paramInteger);
  
  Long getMatchStartTime();
  
  void setMatchStartTime(Long paramLong);
  
  Long getMatchEndTime();
  
  void setMatchEndTime(Long paramLong);
  
  Integer getBodyPoints();
  
  void setBodyPoints(Integer paramInteger);
  
  Integer getHeadPoints();
  
  void setHeadPoints(Integer paramInteger);
  
  Integer getPunchPoints();
  
  void setPunchPoints(Integer paramInteger);
  
  Integer getBodyTechPoints();
  
  void setBodyTechPoints(Integer paramInteger);
  
  Integer getHeadTechPoints();
  
  void setHeadTechPoints(Integer paramInteger);
  
  Integer getOvertimePoints();
  
  void setOvertimePoints(Integer paramInteger);
  
  Integer getOvertimePenalties();
  
  void setOvertimePenalties(Integer paramInteger);
  
  Integer getCellingScore();
  
  void setCellingScore(Integer paramInteger);
  
  Integer getDifferencialScore();
  
  void setDifferencialScore(Integer paramInteger);
  
  Integer getNearMissLevel();
  
  void setNearMissLevel(Integer paramInteger);
  
  String getMatchNumber();
  
  void setMatchNumber(String paramString);
  
  Phase getPhase();
  
  void setPhase(Phase paramPhase);
  
  RC getRoundsConfig();
  
  void setRoundsConfig(RC paramRC);
  
  String getRoundsWinners();
  
  void setRoundsWinners(String paramString);
  
  MatchWinner getMatchWinner();
  
  void setMatchWinner(MatchWinner paramMatchWinner);
  
  FinalDecision getMatchWinnerBy();
  
  void setMatchWinnerBy(FinalDecision paramFinalDecision);
  
  String getMatchResult();
  
  void setMatchResult(String paramString);
  
  GoldenPointTieBreakerInfo getGoldenPointTieBreakerInfo();
  
  void setGoldenPointTieBreakerInfo(GoldenPointTieBreakerInfo paramGoldenPointTieBreakerInfo);
  
  MatchVictoryCriteria getMatchVictoryCriteria();
  
  void setMatchVictoryCriteria(MatchVictoryCriteria paramMatchVictoryCriteria);
}
