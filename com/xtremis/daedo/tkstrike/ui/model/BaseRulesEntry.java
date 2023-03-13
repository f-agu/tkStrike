package com.xtremis.daedo.tkstrike.ui.model;

import com.xtremis.daedo.tkstrike.orm.model.Entity;
import com.xtremis.daedo.tkstrike.orm.model.RoundsConfigEntity;
import com.xtremis.daedo.tkstrike.orm.model.RulesEntity;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchVictoryCriteria;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import org.springframework.beans.BeanUtils;

public abstract class BaseRulesEntry<E extends RulesEntity, RC extends IRoundsConfigEntry, RCE extends RoundsConfigEntity> implements IRulesEntry<E, RC> {
  private SimpleStringProperty id = new SimpleStringProperty(this, "id");
  
  private SimpleIntegerProperty bodyPoints = new SimpleIntegerProperty(this, "bodyPoints");
  
  private SimpleIntegerProperty headPoints = new SimpleIntegerProperty(this, "headPoints");
  
  private SimpleIntegerProperty punchPoints = new SimpleIntegerProperty(this, "punchPoints");
  
  private SimpleIntegerProperty bodyTechPoints = new SimpleIntegerProperty(this, "bodyTechPoints");
  
  private SimpleIntegerProperty headTechPoints = new SimpleIntegerProperty(this, "headTechPoints");
  
  private SimpleIntegerProperty overtimePoints = new SimpleIntegerProperty(this, "overtimePoints");
  
  private SimpleIntegerProperty overtimePenalties = new SimpleIntegerProperty(this, "overtimePenalties");
  
  private SimpleIntegerProperty cellingScore = new SimpleIntegerProperty(this, "cellingScore");
  
  private SimpleIntegerProperty differencialScore = new SimpleIntegerProperty(this, "differencialScore");
  
  private SimpleBooleanProperty pointGapAllRounds = new SimpleBooleanProperty(this, "pointGapAllRounds", false);
  
  private SimpleIntegerProperty nearMissLevel = new SimpleIntegerProperty(this, "nearMissLevel");
  
  private SimpleIntegerProperty paraSpinningKickPoints = new SimpleIntegerProperty(this, "paraSpinningKickPoints");
  
  private SimpleIntegerProperty paraTurningKickPoints = new SimpleIntegerProperty(this, "paraTurningKickPoints");
  
  private SimpleIntegerProperty paraCellingScore = new SimpleIntegerProperty(this, "paraCellingScore");
  
  private SimpleObjectProperty<MatchVictoryCriteria> matchVictoryCriteria = new SimpleObjectProperty(this, "matchVictoryCriteria");
  
  private SimpleObjectProperty<RC> roundsConfig = new SimpleObjectProperty(this, "roundsConfig");
  
  private SimpleBooleanProperty gamJeomShowPointsOnGoldenPoint = new SimpleBooleanProperty(this, "gamJeomShowPointsOnGoldenPoint");
  
  private SimpleBooleanProperty forceShowName = new SimpleBooleanProperty(this, "forceShowName", false);
  
  private SimpleBooleanProperty bonusPointsEnabled = new SimpleBooleanProperty(this, "bonusPointsEnabled", false);
  
  private SimpleIntegerProperty bonusPointsMinLevel = new SimpleIntegerProperty(this, "bonusPointsMinLevel", 0);
  
  private SimpleIntegerProperty bonusPointsPoints2Add = new SimpleIntegerProperty(this, "bonusPointsPoints2Add", 0);
  
  private SimpleBooleanProperty forceMaxGamJomAllowed = new SimpleBooleanProperty(this, "forceMaxGamJomAllowed", false);
  
  private SimpleIntegerProperty maxGamJomAllowed = new SimpleIntegerProperty(this, "maxGamJomAllowed", 10);
  
  public void fillByEntity(E entity) {
    if (entity != null) {
      this.id.set(entity.getId());
      this.bodyPoints.set(entity.getBodyPoints().intValue());
      this.headPoints.set(entity.getHeadPoints().intValue());
      this.punchPoints.set(entity.getPunchPoints().intValue());
      this.bodyTechPoints.set(entity.getBodyTechPoints().intValue());
      this.headTechPoints.set(entity.getHeadTechPoints().intValue());
      this.overtimePoints.set(entity.getOvertimePoints().intValue());
      this.overtimePenalties.set(entity.getOvertimePenalties().intValue());
      this.cellingScore.set(entity.getCellingScore().intValue());
      this.differencialScore.set(entity.getDifferencialScore().intValue());
      this.pointGapAllRounds.set(entity.getPointGapAllRounds().booleanValue());
      this.nearMissLevel.set(entity.getNearMissLevel().intValue());
      this.paraSpinningKickPoints.setValue(entity.getParaSpinningKickPoints());
      this.paraTurningKickPoints.setValue(entity.getParaTurningKickPoints());
      this.paraCellingScore.setValue(entity.getParaCellingScore());
      this.matchVictoryCriteria.setValue(entity.getMatchVictoryCriteria());
      RC roundsConfigEntry = newRoundsConfigEntry();
      if (entity.getRoundsConfig() != null)
        roundsConfigEntry.fillByEntity(entity.getRoundsConfig()); 
      this.roundsConfig.set(roundsConfigEntry);
      this.gamJeomShowPointsOnGoldenPoint.set(entity.getGamJeomShowPointsOnGoldenPoint().booleanValue());
      this.forceShowName.set(entity.getForceShowName().booleanValue());
      this.bonusPointsEnabled.set(entity.getBonusPointsEnabled().booleanValue());
      this.bonusPointsMinLevel.setValue(entity.getBonusPointsMinLevel());
      this.bonusPointsPoints2Add.setValue(entity.getBonusPointsPoints2Add());
      this.forceMaxGamJomAllowed.set(entity.getForceMaxGamJomAllowed().booleanValue());
      this.maxGamJomAllowed.set(entity.getMaxGamJomAllowed().intValue());
    } 
  }
  
  public E createRules() {
    E res = newRulesInstance();
    BeanUtils.copyProperties(this, res, new String[] { "id", "version", "roundsConfig" });
    RCE roundsConfig1 = newRoundsConfigEntity();
    if (getRoundsConfig() != null)
      BeanUtils.copyProperties(getRoundsConfig(), roundsConfig1); 
    res.setRoundsConfig((RoundsConfigEntity)roundsConfig1);
    return res;
  }
  
  public String getId() {
    return this.id.get();
  }
  
  public SimpleStringProperty idProperty() {
    return this.id;
  }
  
  public int getBodyPoints() {
    return this.bodyPoints.get();
  }
  
  public Property bodyPointsProperty() {
    return (Property)this.bodyPoints;
  }
  
  public int getHeadPoints() {
    return this.headPoints.get();
  }
  
  public Property headPointsProperty() {
    return (Property)this.headPoints;
  }
  
  public int getPunchPoints() {
    return this.punchPoints.get();
  }
  
  public Property punchPointsProperty() {
    return (Property)this.punchPoints;
  }
  
  public int getBodyTechPoints() {
    return this.bodyTechPoints.get();
  }
  
  public Property bodyTechPointsProperty() {
    return (Property)this.bodyTechPoints;
  }
  
  public int getHeadTechPoints() {
    return this.headTechPoints.get();
  }
  
  public Property headTechPointsProperty() {
    return (Property)this.headTechPoints;
  }
  
  public int getOvertimePoints() {
    return this.overtimePoints.get();
  }
  
  public Property overtimePointsProperty() {
    return (Property)this.overtimePoints;
  }
  
  public int getOvertimePenalties() {
    return this.overtimePenalties.get();
  }
  
  public Property overtimePenaltiesProperty() {
    return (Property)this.overtimePenalties;
  }
  
  public int getCellingScore() {
    return this.cellingScore.get();
  }
  
  public SimpleIntegerProperty cellingScoreProperty() {
    return this.cellingScore;
  }
  
  public int getDifferencialScore() {
    return this.differencialScore.get();
  }
  
  public SimpleIntegerProperty differencialScoreProperty() {
    return this.differencialScore;
  }
  
  public int getNearMissLevel() {
    return this.nearMissLevel.get();
  }
  
  public SimpleIntegerProperty nearMissLevelProperty() {
    return this.nearMissLevel;
  }
  
  public int getParaSpinningKickPoints() {
    return this.paraSpinningKickPoints.get();
  }
  
  public Property paraSpinningKickPointsProperty() {
    return (Property)this.paraSpinningKickPoints;
  }
  
  public void setParaSpinningKickPoints(int paraSpinningKickPoints) {
    this.paraSpinningKickPoints.set(paraSpinningKickPoints);
  }
  
  public int getParaTurningKickPoints() {
    return this.paraTurningKickPoints.get();
  }
  
  public Property paraTurningKickPointsProperty() {
    return (Property)this.paraTurningKickPoints;
  }
  
  public void setParaTurningKickPoints(int paraTurningKickPoints) {
    this.paraTurningKickPoints.set(paraTurningKickPoints);
  }
  
  public RC getRoundsConfig() {
    return (RC)this.roundsConfig.get();
  }
  
  public SimpleObjectProperty<RC> roundsConfigProperty() {
    return this.roundsConfig;
  }
  
  public MatchVictoryCriteria getMatchVictoryCriteria() {
    return (MatchVictoryCriteria)this.matchVictoryCriteria.get();
  }
  
  public SimpleObjectProperty<MatchVictoryCriteria> matchVictoryCriteriaProperty() {
    return this.matchVictoryCriteria;
  }
  
  public void setMatchVictoryCriteria(MatchVictoryCriteria matchVictoryCriteria) {
    this.matchVictoryCriteria.set(matchVictoryCriteria);
  }
  
  public boolean isGamJeomShowPointsOnGoldenPoint() {
    return this.gamJeomShowPointsOnGoldenPoint.get();
  }
  
  public SimpleBooleanProperty gamJeomShowPointsOnGoldenPointProperty() {
    return this.gamJeomShowPointsOnGoldenPoint;
  }
  
  public void setGamJeomShowPointsOnGoldenPoint(boolean gamJeomShowPointsOnGoldenPoint) {
    this.gamJeomShowPointsOnGoldenPoint.set(gamJeomShowPointsOnGoldenPoint);
  }
  
  public int getParaCellingScore() {
    return this.paraCellingScore.get();
  }
  
  public SimpleIntegerProperty paraCellingScoreProperty() {
    return this.paraCellingScore;
  }
  
  public void setParaCellingScore(int paraCellingScore) {
    this.paraCellingScore.set(paraCellingScore);
  }
  
  public Boolean getPointGapAllRounds() {
    return Boolean.valueOf(this.pointGapAllRounds.get());
  }
  
  public SimpleBooleanProperty pointGapAllRoundsProperty() {
    return this.pointGapAllRounds;
  }
  
  public void setPointGapAllRounds(boolean pointGapAllRounds) {
    this.pointGapAllRounds.set(pointGapAllRounds);
  }
  
  public Boolean getForceShowName() {
    return Boolean.valueOf(this.forceShowName.get());
  }
  
  public SimpleBooleanProperty forceShowNameProperty() {
    return this.forceShowName;
  }
  
  public void setForceShowName(boolean forceShowName) {
    this.forceShowName.set(forceShowName);
  }
  
  public Property<Boolean> bonusPointsEnabledProperty() {
    return (Property<Boolean>)this.bonusPointsEnabled;
  }
  
  public void setBonusPointsEnabled(Boolean bonusPointsEnabled) {
    this.bonusPointsEnabled.set(bonusPointsEnabled.booleanValue());
  }
  
  public Boolean getBonusPointsEnabled() {
    return Boolean.valueOf(this.bonusPointsEnabled.get());
  }
  
  public Property bonusPointsMinLevelProperty() {
    return (Property)this.bonusPointsMinLevel;
  }
  
  public void setBonusPointsMinLevel(Integer bonusPointsMinLevel) {
    this.bonusPointsMinLevel.setValue(bonusPointsMinLevel);
  }
  
  public Integer getBonusPointsMinLevel() {
    return this.bonusPointsMinLevel.getValue();
  }
  
  public Property bonusPointsPoints2AddProperty() {
    return (Property)this.bonusPointsPoints2Add;
  }
  
  public Integer getBonusPointsPoints2Add() {
    return this.bonusPointsPoints2Add.getValue();
  }
  
  public void setBonusPointsPoints2Add(Integer bonusPointsPoints2Add) {
    this.bonusPointsPoints2Add.setValue(bonusPointsPoints2Add);
  }
  
  public Property<Boolean> forceMaxGamJomAllowedProperty() {
    return (Property<Boolean>)this.forceMaxGamJomAllowed;
  }
  
  public Boolean getForceMaxGamJomAllowed() {
    return this.forceMaxGamJomAllowed.getValue();
  }
  
  public void setForceMaxGamJomAllowed(Boolean forceMaxGamJomAllowed) {
    this.forceMaxGamJomAllowed.set(forceMaxGamJomAllowed.booleanValue());
  }
  
  public Property maxGamJomAllowedProperty() {
    return (Property)this.maxGamJomAllowed;
  }
  
  public Integer getMaxGamJomAllowed() {
    return Integer.valueOf(this.maxGamJomAllowed.get());
  }
  
  public void setMaxGamJomAllowed(Integer maxGamJomAllowed) {
    this.maxGamJomAllowed.set(maxGamJomAllowed.intValue());
  }
  
  abstract E newRulesInstance();
  
  abstract RC newRoundsConfigEntry();
  
  abstract RCE newRoundsConfigEntity();
}
