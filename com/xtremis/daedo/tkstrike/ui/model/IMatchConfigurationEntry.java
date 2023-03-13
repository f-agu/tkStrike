package com.xtremis.daedo.tkstrike.ui.model;

import com.xtremis.daedo.tkstrike.orm.model.Entity;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchVictoryCriteria;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public interface IMatchConfigurationEntry<E extends com.xtremis.daedo.tkstrike.orm.model.MatchConfigurationEntity, R extends IRoundsConfigEntry, D extends com.xtremis.daedo.tkstrike.tools.ei.om.MatchConfigurationDto> extends Entry<E> {
  boolean isReadyForStart();
  
  void fillByEntity(E paramE);
  
  D getMatchConfigurationDto();
  
  String getId();
  
  SimpleStringProperty idProperty();
  
  String getMatchNumber();
  
  SimpleStringProperty matchNumberProperty();
  
  String getVmMatchInternalId();
  
  SimpleStringProperty vmMatchInternalIdProperty();
  
  String getBlueAthleteOvrInternalId();
  
  SimpleStringProperty blueAthleteOvrInternalIdProperty();
  
  String getRedAthleteOvrInternalId();
  
  SimpleStringProperty redAthleteOvrInternalIdProperty();
  
  PhaseEntry getPhase();
  
  SimpleObjectProperty<PhaseEntry> phaseProperty();
  
  R getRoundsConfig();
  
  SimpleObjectProperty<R> roundsConfigProperty();
  
  SimpleIntegerProperty differencialScoreProperty();
  
  Integer getDifferencialScore();
  
  SimpleBooleanProperty wtCompetitionDataProtocolProperty();
  
  Boolean getWtCompetitionDataProtocol();
  
  SimpleObjectProperty<MatchVictoryCriteria> matchVictoryCriteriaProperty();
  
  MatchVictoryCriteria getMatchVictoryCriteria();
  
  Integer getMaxAllowedGamJeoms();
  
  SimpleIntegerProperty maxAllowedGamJeomsProperty();
  
  void setMaxAllowedGamJeoms(int paramInt);
}
