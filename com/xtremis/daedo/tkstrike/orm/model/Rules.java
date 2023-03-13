package com.xtremis.daedo.tkstrike.orm.model;

import com.xtremis.daedo.tkstrike.tools.ei.om.MatchVictoryCriteria;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "TKS_RULES")
public class Rules extends BaseEntity implements RulesEntity<RoundsConfig> {
  @Column(name = "BODY_POINTS")
  private Integer bodyPoints;
  
  @Column(name = "HEAD_POINTS")
  private Integer headPoints;
  
  @Column(name = "PUNCH_POINTS")
  private Integer punchPoints;
  
  @Column(name = "BODY_TECH_POINTS")
  private Integer bodyTechPoints;
  
  @Column(name = "HEAD_TECH_POINTS")
  private Integer headTechPoints;
  
  @Column(name = "OVERTIME_POINTS")
  private Integer overtimePoints;
  
  @Column(name = "OVERTIME_PENALTIES")
  private Integer overtimePenalties;
  
  @Column(name = "CELLING_SCORE")
  private Integer cellingScore;
  
  @Column(name = "DIFFERENCIAL_SCORE")
  private Integer differencialScore;
  
  @Column(name = "POINT_GAP_ALL_ROUNDS")
  private Boolean pointGapAllRounds;
  
  @Column(name = "NEAR_MISS_LEVEL")
  private Integer nearMissLevel;
  
  @Column(name = "PARA_SPINNING_KICK_POINTS")
  private Integer paraSpinningKickPoints;
  
  @Column(name = "PARA_TURNING_KICK_POINTS")
  private Integer paraTurningKickPoints;
  
  @Column(name = "PARA_CELLING_SCORE")
  private Integer paraCellingScore;
  
  @Column(name = "ALL_MATCH_PARA")
  private Boolean allMatchPARA;
  
  @Column(name = "MATCH_VICTORY_CRITERIA")
  @Enumerated(EnumType.STRING)
  private MatchVictoryCriteria matchVictoryCriteria;
  
  @Embedded
  private RoundsConfig roundsConfig;
  
  @Column(name = "GAMJEOM_SHOW_POINTS_ON_GOLDENPOINT")
  private Boolean gamJeomShowPointsOnGoldenPoint;
  
  @Column(name = "FORCE_SHOW_NAME")
  private Boolean forceShowName;
  
  @Column(name = "BONUS_POINTS_ENABLED")
  private Boolean bonusPointsEnabled;
  
  @Column(name = "BONUS_POINTS_MIN_LEVEL")
  private Integer bonusPointsMinLevel;
  
  @Column(name = "BONUS_POINTS_POINTS")
  private Integer bonusPointsPoints2Add;
  
  @Column(name = "FORCE_MAX_GAMJOM")
  private Boolean forceMaxGamJom;
  
  @Column(name = "MAX_GAMJOM_ALLOWED")
  private Integer maxGamJomAllowed;
  
  public Integer getBodyPoints() {
    return this.bodyPoints;
  }
  
  public void setBodyPoints(Integer bodyPoints) {
    this.bodyPoints = bodyPoints;
  }
  
  public Integer getHeadPoints() {
    return this.headPoints;
  }
  
  public void setHeadPoints(Integer headPoints) {
    this.headPoints = headPoints;
  }
  
  public Integer getPunchPoints() {
    return this.punchPoints;
  }
  
  public void setPunchPoints(Integer punchPoints) {
    this.punchPoints = punchPoints;
  }
  
  public Integer getBodyTechPoints() {
    return this.bodyTechPoints;
  }
  
  public void setBodyTechPoints(Integer bodyTechPoints) {
    this.bodyTechPoints = bodyTechPoints;
  }
  
  public Integer getHeadTechPoints() {
    return this.headTechPoints;
  }
  
  public void setHeadTechPoints(Integer headTechPoints) {
    this.headTechPoints = headTechPoints;
  }
  
  public Integer getOvertimePoints() {
    return this.overtimePoints;
  }
  
  public void setOvertimePoints(Integer overtimePoints) {
    this.overtimePoints = overtimePoints;
  }
  
  public Integer getOvertimePenalties() {
    return this.overtimePenalties;
  }
  
  public void setOvertimePenalties(Integer overtimePenalties) {
    this.overtimePenalties = overtimePenalties;
  }
  
  public Integer getCellingScore() {
    return this.cellingScore;
  }
  
  public void setCellingScore(Integer cellingScore) {
    this.cellingScore = cellingScore;
  }
  
  public Integer getDifferencialScore() {
    return this.differencialScore;
  }
  
  public void setDifferencialScore(Integer differencialScore) {
    this.differencialScore = differencialScore;
  }
  
  public Integer getNearMissLevel() {
    return this.nearMissLevel;
  }
  
  public void setNearMissLevel(Integer nearMissLevel) {
    this.nearMissLevel = nearMissLevel;
  }
  
  public Integer getParaSpinningKickPoints() {
    return this.paraSpinningKickPoints;
  }
  
  public void setParaSpinningKickPoints(Integer paraSpinningKickPoints) {
    this.paraSpinningKickPoints = paraSpinningKickPoints;
  }
  
  public Integer getParaTurningKickPoints() {
    return this.paraTurningKickPoints;
  }
  
  public void setParaTurningKickPoints(Integer paraTurningKickPoints) {
    this.paraTurningKickPoints = paraTurningKickPoints;
  }
  
  public RoundsConfig getRoundsConfig() {
    return this.roundsConfig;
  }
  
  public void setRoundsConfig(RoundsConfig roundsConfig) {
    this.roundsConfig = roundsConfig;
  }
  
  public Boolean getAllMatchPARA() {
    return this.allMatchPARA;
  }
  
  public void setAllMatchPARA(Boolean allMatchPARA) {
    this.allMatchPARA = allMatchPARA;
  }
  
  public MatchVictoryCriteria getMatchVictoryCriteria() {
    return this.matchVictoryCriteria;
  }
  
  public void setMatchVictoryCriteria(MatchVictoryCriteria matchVictoryCriteria) {
    this.matchVictoryCriteria = matchVictoryCriteria;
  }
  
  public Boolean getGamJeomShowPointsOnGoldenPoint() {
    return this.gamJeomShowPointsOnGoldenPoint;
  }
  
  public void setGamJeomShowPointsOnGoldenPoint(Boolean gamJeomShowPointsOnGoldenPoint) {
    this.gamJeomShowPointsOnGoldenPoint = gamJeomShowPointsOnGoldenPoint;
  }
  
  public Integer getParaCellingScore() {
    return this.paraCellingScore;
  }
  
  public void setParaCellingScore(Integer paraCellingScore) {
    this.paraCellingScore = paraCellingScore;
  }
  
  public Boolean getPointGapAllRounds() {
    return this.pointGapAllRounds;
  }
  
  public void setPointGapAllRounds(Boolean pointGapAllRounds) {
    this.pointGapAllRounds = pointGapAllRounds;
  }
  
  public Boolean getForceShowName() {
    return this.forceShowName;
  }
  
  public void setForceShowName(Boolean forceShowName) {
    this.forceShowName = forceShowName;
  }
  
  public Boolean getBonusPointsEnabled() {
    return this.bonusPointsEnabled;
  }
  
  public void setBonusPointsEnabled(Boolean bonusPointsEnabled) {
    this.bonusPointsEnabled = bonusPointsEnabled;
  }
  
  public Integer getBonusPointsMinLevel() {
    return this.bonusPointsMinLevel;
  }
  
  public void setBonusPointsMinLevel(Integer bonusPointsMinLevel) {
    this.bonusPointsMinLevel = bonusPointsMinLevel;
  }
  
  public Integer getBonusPointsPoints2Add() {
    return this.bonusPointsPoints2Add;
  }
  
  public void setBonusPointsPoints2Add(Integer bonusPointsPoints2Add) {
    this.bonusPointsPoints2Add = bonusPointsPoints2Add;
  }
  
  public Boolean getForceMaxGamJomAllowed() {
    return this.forceMaxGamJom;
  }
  
  public void setForceMaxGamJomAllowed(Boolean forceMaxGamJom) {
    this.forceMaxGamJom = forceMaxGamJom;
  }
  
  public Integer getMaxGamJomAllowed() {
    return this.maxGamJomAllowed;
  }
  
  public void setMaxGamJomAllowed(Integer maxGamJomAllowed) {
    this.maxGamJomAllowed = maxGamJomAllowed;
  }
}
