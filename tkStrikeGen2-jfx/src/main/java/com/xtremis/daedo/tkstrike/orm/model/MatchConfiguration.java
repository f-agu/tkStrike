package com.xtremis.daedo.tkstrike.orm.model;

import com.xtremis.daedo.tkstrike.tools.ei.om.MatchVictoryCriteria;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "TKS_MATCH_CONFIG", uniqueConstraints = {@UniqueConstraint(columnNames = {"MATCH_NUMBER"})})
@NamedQueries({@NamedQuery(name = "MatchConfiguration.getByMatchNumber", query = "SELECT mc FROM MatchConfiguration mc WHERE mc.matchNumber = ?1 "), @NamedQuery(name = "MatchConfiguration.getCountByAthlete", query = "SELECT COUNT(mc) FROM MatchConfiguration mc WHERE mc.blueAthlete.id = ?1 OR mc.redAthlete.id = ?1 "), @NamedQuery(name = "MatchConfiguration.findByDates", query = "SELECT mc FROM MatchConfiguration mc WHERE mc.lastUpdateDatetime BETWEEN ?1 AND ?2 "), @NamedQuery(name = "MatchConfiguration.findNotStarted", query = "SELECT mc FROM MatchConfiguration mc WHERE mc.matchStarted = false "), @NamedQuery(name = "MatchConfiguration.findStarted", query = "SELECT mc FROM MatchConfiguration mc WHERE mc.matchStarted = true ")})
public class MatchConfiguration extends BaseEntity implements MatchConfigurationEntity<RoundsConfig> {
  @Column(name = "SENSORS_GROUP")
  @Enumerated(EnumType.STRING)
  private SensorsGroup sensorsGroup;
  
  @Column(name = "MATCH_NUMBER")
  private String matchNumber;
  
  @Column(name = "CREATED_DATETIME")
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDatetime;
  
  @Column(name = "LAST_UPDATE_DATETIME")
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastUpdateDatetime;
  
  @Column(name = "MATCH_STARTED")
  private Boolean matchStarted;
  
  @Column(name = "VMS_MATCH_INTERNAL_ID")
  private String vmMatchInternalId;
  
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
  
  @Column(name = "IS_PARATKD_MATCH")
  private Boolean isParaTkdMatch;
  
  @Column(name = "DIFFERENCIAL_SCORE")
  private Integer differencialScore;
  
  @Column(name = "MAX_GAM_JEOMS")
  private Integer maxAllowedGamJeoms;
  
  @Column(name = "IS_WT_DM")
  private Boolean isWtCompetitionDataProtocol;
  
  @ManyToOne
  @JoinColumn(name = "REFEREE_CR_ID")
  private Referee refereeCR;
  
  @ManyToOne
  @JoinColumn(name = "REFEREE_J1_ID")
  private Referee refereeJ1;
  
  @ManyToOne
  @JoinColumn(name = "REFEREE_J2_ID")
  private Referee refereeJ2;
  
  @ManyToOne
  @JoinColumn(name = "REFEREE_J3_ID")
  private Referee refereeJ3;
  
  @ManyToOne
  @JoinColumn(name = "REFEREE_TA_ID")
  private Referee refereeTA;
  
  @ManyToOne
  @JoinColumn(name = "REFEREE_RJ_ID")
  private Referee refereeRJ;
  
  @Column(name = "MATCH_VICTORY_CRITERIA")
  @Enumerated(EnumType.STRING)
  private MatchVictoryCriteria matchVictoryCriteria;
  
  @Embedded
  private RoundsConfig roundsConfig;
  
  public SensorsGroup getSensorsGroup() {
    return this.sensorsGroup;
  }
  
  public void setSensorsGroup(SensorsGroup sensorsGroup) {
    this.sensorsGroup = sensorsGroup;
  }
  
  public String getMatchNumber() {
    return this.matchNumber;
  }
  
  public void setMatchNumber(String matchNumber) {
    this.matchNumber = matchNumber;
  }
  
  public String getVmMatchInternalId() {
    return this.vmMatchInternalId;
  }
  
  public void setVmMatchInternalId(String vmMatchInternalId) {
    this.vmMatchInternalId = vmMatchInternalId;
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
  
  public Boolean getParaTkdMatch() {
    return this.isParaTkdMatch;
  }
  
  public void setParaTkdMatch(Boolean paraTkdMatch) {
    this.isParaTkdMatch = paraTkdMatch;
  }
  
  public Integer getDifferencialScore() {
    return this.differencialScore;
  }
  
  public void setDifferencialScore(Integer differencialScore) {
    this.differencialScore = differencialScore;
  }
  
  public Integer getMaxAllowedGamJeoms() {
    return this.maxAllowedGamJeoms;
  }
  
  public void setMaxAllowedGamJeoms(Integer maxAllowedGamJeoms) {
    this.maxAllowedGamJeoms = maxAllowedGamJeoms;
  }
  
  public RoundsConfig getRoundsConfig() {
    return this.roundsConfig;
  }
  
  public void setRoundsConfig(RoundsConfig roundsConfig) {
    this.roundsConfig = roundsConfig;
  }
  
  public Boolean getWtCompetitionDataProtocol() {
    return this.isWtCompetitionDataProtocol;
  }
  
  public void setWtCompetitionDataProtocol(Boolean wtCompetitionDataProtocol) {
    this.isWtCompetitionDataProtocol = wtCompetitionDataProtocol;
  }
  
  public Referee getRefereeCR() {
    return this.refereeCR;
  }
  
  public void setRefereeCR(Referee refereeCR) {
    this.refereeCR = refereeCR;
  }
  
  public Referee getRefereeJ1() {
    return this.refereeJ1;
  }
  
  public void setRefereeJ1(Referee refereeJ1) {
    this.refereeJ1 = refereeJ1;
  }
  
  public Referee getRefereeJ2() {
    return this.refereeJ2;
  }
  
  public void setRefereeJ2(Referee refereeJ2) {
    this.refereeJ2 = refereeJ2;
  }
  
  public Referee getRefereeJ3() {
    return this.refereeJ3;
  }
  
  public void setRefereeJ3(Referee refereeJ3) {
    this.refereeJ3 = refereeJ3;
  }
  
  public Referee getRefereeTA() {
    return this.refereeTA;
  }
  
  public void setRefereeTA(Referee refereeTA) {
    this.refereeTA = refereeTA;
  }
  
  public Referee getRefereeRJ() {
    return this.refereeRJ;
  }
  
  public void setRefereeRJ(Referee refereeRJ) {
    this.refereeRJ = refereeRJ;
  }
  
  public Date getCreatedDatetime() {
    return this.createdDatetime;
  }
  
  public void setCreatedDatetime(Date createdDatetime) {
    this.createdDatetime = createdDatetime;
  }
  
  public Date getLastUpdateDatetime() {
    return this.lastUpdateDatetime;
  }
  
  public void setLastUpdateDatetime(Date lastUpdateDatetime) {
    this.lastUpdateDatetime = lastUpdateDatetime;
  }
  
  public Boolean getMatchStarted() {
    return this.matchStarted;
  }
  
  public void setMatchStarted(Boolean matchStarted) {
    this.matchStarted = matchStarted;
  }
  
  public MatchVictoryCriteria getMatchVictoryCriteria() {
    return this.matchVictoryCriteria;
  }
  
  public void setMatchVictoryCriteria(MatchVictoryCriteria matchVictoryCriteria) {
    this.matchVictoryCriteria = matchVictoryCriteria;
  }
  
  @PrePersist
  public void onPrePersist() {
    this.createdDatetime = new Date();
    this.lastUpdateDatetime = new Date();
  }
  
  @PreUpdate
  public void onPreUpdate() {
    this.lastUpdateDatetime = new Date();
  }
}
