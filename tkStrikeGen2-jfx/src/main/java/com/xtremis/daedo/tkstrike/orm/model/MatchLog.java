package com.xtremis.daedo.tkstrike.orm.model;

import com.xtremis.daedo.tkstrike.om.combat.FinalDecision;
import com.xtremis.daedo.tkstrike.om.combat.MatchWinner;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchVictoryCriteria;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "TKS_MATCH_LOG")
@NamedQueries({@NamedQuery(name = "MatchLog.getLastStarted", query = "SELECT ml FROM MatchLog ml WHERE ml.matchStartTime = (SELECT MAX(ml2.matchStartTime)                            FROM MatchLog ml2) "), @NamedQuery(name = "MatchLog.getAllExceptLast", query = "SELECT ml FROM MatchLog ml WHERE ml.matchStartTime <> (SELECT MAX(ml2.matchStartTime)                             FROM MatchLog ml2) OR ml.matchStartTime IS NULL "), @NamedQuery(name = "MatchLog.getCountByAthlete", query = "SELECT COUNT(ml) FROM MatchLog ml WHERE ml.blueAthlete.id = ?1 OR ml.redAthlete.id = ?1 ")})
public class MatchLog extends BaseEntity implements MatchLogEntity<RoundsConfig> {
  @Column(name = "MATCH_CONFIGURATION_ID")
  private String matchConfigurationId;
  
  @Column(name = "NUMBER_OF_JUDGES")
  private Integer numberOfJudges;
  
  @Column(name = "MIN_BODY_LEVEL")
  private Integer minBodyLevel;
  
  @Column(name = "BODY_SENSORS_ENABLED")
  private Boolean bodySensorsEnabled;
  
  @Column(name = "MIN_HEAD_LEVEL")
  private Integer minHeadLevel;
  
  @Column(name = "HEAD_SENSORS_ENABLED")
  private Boolean headSensorsEnabled;
  
  @Column(name = "MATCH_START_TIME")
  private Long matchStartTime;
  
  @Column(name = "MATCH_END_TIME")
  private Long matchEndTime;
  
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
  
  @Column(name = "NEAR_MISS_LEVEL")
  private Integer nearMissLevel;
  
  @Column(name = "MATCH_NUMBER")
  private String matchNumber;
  
  @ManyToOne
  @JoinColumn(name = "PHASE_ID")
  private Phase phase;
  
  @ManyToOne
  @JoinColumn(name = "CATEGORY_ID")
  private Category category;
  
  @ManyToOne
  @JoinColumn(name = "BLUE_ATHLETE_ID")
  private Athlete blueAthlete;
  
  @Column(name = "BLUE_VIDEO_QUOTA")
  private Integer blueAthleteVideoQuota;
  
  @ManyToOne
  @JoinColumn(name = "RED_ATHLETE_ID")
  private Athlete redAthlete;
  
  @Column(name = "RED_VIDEO_QUOTA")
  private Integer redAthleteVideoQuota;
  
  @Embedded
  private RoundsConfig roundsConfig;
  
  @Column(name = "ROUNDS_WINNER")
  private String roundsWinners;
  
  @Column(name = "MATCH_WINNER")
  @Enumerated(EnumType.STRING)
  private MatchWinner matchWinner;
  
  @Column(name = "MATCH_WINNER_BY")
  @Enumerated(EnumType.STRING)
  private FinalDecision matchWinnerBy;
  
  @Column(name = "MATCH_RESULT")
  private String matchResult;
  
  @Embedded
  private GoldenPointTieBreakerInfo goldenPointTieBreakerInfo;
  
  @Column(name = "MAX_GAM_JEOMS")
  private Integer maxAllowedGamJeoms;
  
  @Column(name = "MATCH_VICTORY_CRITERIA")
  @Enumerated(EnumType.STRING)
  private MatchVictoryCriteria matchVictoryCriteria;
  
  @Column(name = "IS_PARATKD_MATCH")
  private Boolean isParaTkdMatch;
  
  public Boolean getBodySensorsEnabled() {
    return this.bodySensorsEnabled;
  }
  
  public void setBodySensorsEnabled(Boolean bodySensorsEnabled) {
    this.bodySensorsEnabled = bodySensorsEnabled;
  }
  
  public Boolean getHeadSensorsEnabled() {
    return this.headSensorsEnabled;
  }
  
  public void setHeadSensorsEnabled(Boolean headSensorsEnabled) {
    this.headSensorsEnabled = headSensorsEnabled;
  }
  
  public String getMatchConfigurationId() {
    return this.matchConfigurationId;
  }
  
  public void setMatchConfigurationId(String matchConfigurationId) {
    this.matchConfigurationId = matchConfigurationId;
  }
  
  public Integer getNumberOfJudges() {
    return this.numberOfJudges;
  }
  
  public void setNumberOfJudges(Integer numberOfJudges) {
    this.numberOfJudges = numberOfJudges;
  }
  
  public Integer getMinBodyLevel() {
    return this.minBodyLevel;
  }
  
  public void setMinBodyLevel(Integer minBodyLevel) {
    this.minBodyLevel = minBodyLevel;
  }
  
  public Integer getMinHeadLevel() {
    return this.minHeadLevel;
  }
  
  public void setMinHeadLevel(Integer minHeadLevel) {
    this.minHeadLevel = minHeadLevel;
  }
  
  public Long getMatchStartTime() {
    return this.matchStartTime;
  }
  
  public void setMatchStartTime(Long matchStartTime) {
    this.matchStartTime = matchStartTime;
  }
  
  public Long getMatchEndTime() {
    return this.matchEndTime;
  }
  
  public void setMatchEndTime(Long matchEndTime) {
    this.matchEndTime = matchEndTime;
  }
  
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
  
  public String getMatchNumber() {
    return this.matchNumber;
  }
  
  public void setMatchNumber(String matchNumber) {
    this.matchNumber = matchNumber;
  }
  
  public Phase getPhase() {
    return this.phase;
  }
  
  public void setPhase(Phase phase) {
    this.phase = phase;
  }
  
  public Category getCategory() {
    return this.category;
  }
  
  public void setCategory(Category category) {
    this.category = category;
  }
  
  public Athlete getBlueAthlete() {
    return this.blueAthlete;
  }
  
  public void setBlueAthlete(Athlete blueAthlete) {
    this.blueAthlete = blueAthlete;
  }
  
  public Integer getBlueAthleteVideoQuota() {
    return this.blueAthleteVideoQuota;
  }
  
  public void setBlueAthleteVideoQuota(Integer blueAthleteVideoQuota) {
    this.blueAthleteVideoQuota = blueAthleteVideoQuota;
  }
  
  public Athlete getRedAthlete() {
    return this.redAthlete;
  }
  
  public void setRedAthlete(Athlete redAthlete) {
    this.redAthlete = redAthlete;
  }
  
  public Integer getRedAthleteVideoQuota() {
    return this.redAthleteVideoQuota;
  }
  
  public void setRedAthleteVideoQuota(Integer redAthleteVideoQuota) {
    this.redAthleteVideoQuota = redAthleteVideoQuota;
  }
  
  public RoundsConfig getRoundsConfig() {
    return this.roundsConfig;
  }
  
  public void setRoundsConfig(RoundsConfig roundsConfig) {
    this.roundsConfig = roundsConfig;
  }
  
  public String getRoundsWinners() {
    return this.roundsWinners;
  }
  
  public void setRoundsWinners(String roundsWinners) {
    this.roundsWinners = roundsWinners;
  }
  
  public MatchWinner getMatchWinner() {
    return this.matchWinner;
  }
  
  public void setMatchWinner(MatchWinner matchWinner) {
    this.matchWinner = matchWinner;
  }
  
  public FinalDecision getMatchWinnerBy() {
    return this.matchWinnerBy;
  }
  
  public void setMatchWinnerBy(FinalDecision finalDecision) {
    this.matchWinnerBy = finalDecision;
  }
  
  public String getMatchResult() {
    return this.matchResult;
  }
  
  public void setMatchResult(String matchResult) {
    this.matchResult = matchResult;
  }
  
  public GoldenPointTieBreakerInfo getGoldenPointTieBreakerInfo() {
    return this.goldenPointTieBreakerInfo;
  }
  
  public void setGoldenPointTieBreakerInfo(GoldenPointTieBreakerInfo goldenPointTieBreaker) {
    this.goldenPointTieBreakerInfo = goldenPointTieBreaker;
  }
  
  public Integer getMaxAllowedGamJeoms() {
    return this.maxAllowedGamJeoms;
  }
  
  public void setMaxAllowedGamJeoms(Integer maxAllowedGamJeoms) {
    this.maxAllowedGamJeoms = maxAllowedGamJeoms;
  }
  
  public MatchVictoryCriteria getMatchVictoryCriteria() {
    return this.matchVictoryCriteria;
  }
  
  public void setMatchVictoryCriteria(MatchVictoryCriteria matchVictoryCriteria) {
    this.matchVictoryCriteria = matchVictoryCriteria;
  }
  
  public Boolean getParaTkdMatch() {
    return this.isParaTkdMatch;
  }
  
  public void setParaTkdMatch(Boolean paraTkdMatch) {
    this.isParaTkdMatch = paraTkdMatch;
  }
}
