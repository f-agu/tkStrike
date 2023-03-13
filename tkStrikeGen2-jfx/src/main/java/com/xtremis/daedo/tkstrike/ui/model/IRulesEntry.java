package com.xtremis.daedo.tkstrike.ui.model;

import com.xtremis.daedo.tkstrike.tools.ei.om.MatchVictoryCriteria;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public interface IRulesEntry<E extends com.xtremis.daedo.tkstrike.orm.model.RulesEntity, RC extends IRoundsConfigEntry> extends Entry<E> {
  E createRules();
  
  SimpleStringProperty idProperty();
  
  int getBodyPoints();
  
  Property bodyPointsProperty();
  
  int getHeadPoints();
  
  Property headPointsProperty();
  
  int getPunchPoints();
  
  Property punchPointsProperty();
  
  int getBodyTechPoints();
  
  Property bodyTechPointsProperty();
  
  int getHeadTechPoints();
  
  Property headTechPointsProperty();
  
  int getOvertimePoints();
  
  Property overtimePointsProperty();
  
  int getOvertimePenalties();
  
  Property overtimePenaltiesProperty();
  
  int getCellingScore();
  
  SimpleIntegerProperty cellingScoreProperty();
  
  int getParaCellingScore();
  
  SimpleIntegerProperty paraCellingScoreProperty();
  
  int getDifferencialScore();
  
  SimpleIntegerProperty differencialScoreProperty();
  
  Boolean getPointGapAllRounds();
  
  SimpleBooleanProperty pointGapAllRoundsProperty();
  
  Boolean getForceShowName();
  
  SimpleBooleanProperty forceShowNameProperty();
  
  int getNearMissLevel();
  
  SimpleIntegerProperty nearMissLevelProperty();
  
  int getParaSpinningKickPoints();
  
  Property paraSpinningKickPointsProperty();
  
  int getParaTurningKickPoints();
  
  Property paraTurningKickPointsProperty();
  
  RC getRoundsConfig();
  
  SimpleObjectProperty<RC> roundsConfigProperty();
  
  MatchVictoryCriteria getMatchVictoryCriteria();
  
  SimpleObjectProperty<MatchVictoryCriteria> matchVictoryCriteriaProperty();
  
  void setMatchVictoryCriteria(MatchVictoryCriteria paramMatchVictoryCriteria);
  
  boolean isGamJeomShowPointsOnGoldenPoint();
  
  SimpleBooleanProperty gamJeomShowPointsOnGoldenPointProperty();
  
  void setGamJeomShowPointsOnGoldenPoint(boolean paramBoolean);
  
  Property<Boolean> bonusPointsEnabledProperty();
  
  Boolean getBonusPointsEnabled();
  
  Property bonusPointsMinLevelProperty();
  
  Integer getBonusPointsMinLevel();
  
  Property bonusPointsPoints2AddProperty();
  
  Integer getBonusPointsPoints2Add();
  
  Property<Boolean> forceMaxGamJomAllowedProperty();
  
  Boolean getForceMaxGamJomAllowed();
  
  Property maxGamJomAllowedProperty();
  
  Integer getMaxGamJomAllowed();
}
