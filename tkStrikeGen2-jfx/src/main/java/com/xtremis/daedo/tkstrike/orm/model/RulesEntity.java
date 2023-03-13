package com.xtremis.daedo.tkstrike.orm.model;

import com.xtremis.daedo.tkstrike.tools.ei.om.MatchVictoryCriteria;

public interface RulesEntity<R extends RoundsConfigEntity> extends Entity {
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
  
  Boolean getPointGapAllRounds();
  
  void setPointGapAllRounds(Boolean paramBoolean);
  
  Integer getNearMissLevel();
  
  void setNearMissLevel(Integer paramInteger);
  
  Integer getParaSpinningKickPoints();
  
  void setParaSpinningKickPoints(Integer paramInteger);
  
  Integer getParaTurningKickPoints();
  
  void setParaCellingScore(Integer paramInteger);
  
  Integer getParaCellingScore();
  
  void setParaTurningKickPoints(Integer paramInteger);
  
  R getRoundsConfig();
  
  void setRoundsConfig(R paramR);
  
  MatchVictoryCriteria getMatchVictoryCriteria();
  
  void setMatchVictoryCriteria(MatchVictoryCriteria paramMatchVictoryCriteria);
  
  Boolean getGamJeomShowPointsOnGoldenPoint();
  
  void setGamJeomShowPointsOnGoldenPoint(Boolean paramBoolean);
  
  Boolean getForceShowName();
  
  void setForceShowName(Boolean paramBoolean);
  
  Boolean getBonusPointsEnabled();
  
  void setBonusPointsEnabled(Boolean paramBoolean);
  
  Integer getBonusPointsMinLevel();
  
  void setBonusPointsMinLevel(Integer paramInteger);
  
  Integer getBonusPointsPoints2Add();
  
  void setBonusPointsPoints2Add(Integer paramInteger);
  
  Boolean getForceMaxGamJomAllowed();
  
  void setForceMaxGamJomAllowed(Boolean paramBoolean);
  
  Integer getMaxGamJomAllowed();
  
  void setMaxGamJomAllowed(Integer paramInteger);
}
