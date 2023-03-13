package com.xtremis.daedo.tkstrike.orm.model;

import com.xtremis.daedo.tkstrike.tools.ei.om.MatchVictoryCriteria;
import java.util.Date;

public interface MatchConfigurationEntity<R extends RoundsConfigEntity> extends Entity {
  String getMatchNumber();
  
  void setMatchNumber(String paramString);
  
  Phase getPhase();
  
  void setPhase(Phase paramPhase);
  
  R getRoundsConfig();
  
  void setRoundsConfig(R paramR);
  
  Integer getDifferencialScore();
  
  void setDifferencialScore(Integer paramInteger);
  
  Boolean getWtCompetitionDataProtocol();
  
  void setWtCompetitionDataProtocol(Boolean paramBoolean);
  
  Referee getRefereeCR();
  
  void setRefereeCR(Referee paramReferee);
  
  Referee getRefereeJ1();
  
  void setRefereeJ1(Referee paramReferee);
  
  Referee getRefereeJ2();
  
  void setRefereeJ2(Referee paramReferee);
  
  Referee getRefereeJ3();
  
  void setRefereeJ3(Referee paramReferee);
  
  Referee getRefereeTA();
  
  void setRefereeTA(Referee paramReferee);
  
  Referee getRefereeRJ();
  
  void setRefereeRJ(Referee paramReferee);
  
  Date getCreatedDatetime();
  
  void setCreatedDatetime(Date paramDate);
  
  Date getLastUpdateDatetime();
  
  void setLastUpdateDatetime(Date paramDate);
  
  Boolean getMatchStarted();
  
  void setMatchStarted(Boolean paramBoolean);
  
  MatchVictoryCriteria getMatchVictoryCriteria();
  
  void setMatchVictoryCriteria(MatchVictoryCriteria paramMatchVictoryCriteria);
}
