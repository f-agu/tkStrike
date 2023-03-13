package com.xtremis.daedo.tkstrike.orm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "TKS_MATCH_LOG_ITEM")
@NamedQueries({@NamedQuery(name = "MatchLogItem.findByMatchLogId", query = "SELECT mli FROM MatchLogItem mli WHERE mli.matchLog.id = ?1 AND mli.roundNumber > 0ORDER BY mli.roundNumber ASC,mli.roundTime DESC,mli.eventTime ASC ")})
public class MatchLogItem extends BaseEntity implements MatchLogItemEntity<MatchLog> {
  @ManyToOne
  @JoinColumn(name = "MATCH_LOG_ID")
  private MatchLog matchLog;
  
  @Column(name = "ROUND_NUMBER")
  private Integer roundNumber;
  
  @Column(name = "ROUND_NUMBER_STR")
  private String roundNumberStr;
  
  @Column(name = "EVENT_TIME")
  private Long eventTime;
  
  @Column(name = "ROUND_TIME")
  private Long roundTime;
  
  @Column(name = "SYSTEM_TIME")
  private Long systemTime;
  
  @Column(name = "MATCH_LOG_ITEM_TYPE")
  @Enumerated(EnumType.STRING)
  private MatchLogItemType matchLogItemType;
  
  @Column(name = "BLUE_GEN_POINTS")
  private Integer blueGeneralPoints;
  
  @Column(name = "RED_GEN_POINTS")
  private Integer redGeneralPoints;
  
  @Column(name = "BLUE_ADD_POINTS")
  private Integer blueAddPoints;
  
  @Column(name = "RED_ADD_POINTS")
  private Integer redAddPoints;
  
  @Column(name = "BLUE_POINTS")
  private Integer bluePoints;
  
  @Column(name = "RED_POINTS")
  private Integer redPoints;
  
  @Column(name = "BLUE_PENALTIES")
  private Integer bluePenalties;
  
  @Column(name = "RED_PENALTIES")
  private Integer redPenalties;
  
  @Column(name = "BLUE_TOTAL_PENALTIES")
  private Integer blueTotalPenalties;
  
  @Column(name = "RED_TOTAL_PENALTIES")
  private Integer redTotalPenalties;
  
  @Column(name = "BLUE_VIDEO_QUOTA")
  private Integer blueVideoQuota;
  
  @Column(name = "RED_VIDEO_QUOTA")
  private Integer redVideoQuota;
  
  @Column(name = "BLUE_GOLDENPOINT_HITS")
  private Integer blueGoldenPointHits;
  
  @Column(name = "RED_GOLDENPOINT_HITS")
  private Integer redGoldenPointHits;
  
  @Column(name = "BLUE_GOLDENPOINT_PENALTIES")
  private Integer blueGoldenPointPenalties;
  
  @Column(name = "RED_GOLDENPOINT_PENALTIES")
  private Integer redGoldenPointPenalties;
  
  @Column(name = "IS_GOLDENPOINT_ROUND")
  private Boolean isGoldenPointRound;
  
  @Column(name = "ENTRY_VALUE")
  private String entryValue;
  
  public MatchLog getMatchLog() {
    return this.matchLog;
  }
  
  public void setMatchLog(MatchLog matchLog) {
    this.matchLog = matchLog;
  }
  
  public Integer getRoundNumber() {
    return this.roundNumber;
  }
  
  public void setRoundNumber(Integer roundNumber) {
    this.roundNumber = roundNumber;
  }
  
  public String getRoundNumberStr() {
    return this.roundNumberStr;
  }
  
  public void setRoundNumberStr(String roundNumberStr) {
    this.roundNumberStr = roundNumberStr;
  }
  
  public Long getEventTime() {
    return this.eventTime;
  }
  
  public void setEventTime(Long eventTime) {
    this.eventTime = eventTime;
  }
  
  public Long getRoundTime() {
    return this.roundTime;
  }
  
  public void setRoundTime(Long roundTime) {
    this.roundTime = roundTime;
  }
  
  public Long getSystemTime() {
    return this.systemTime;
  }
  
  public void setSystemTime(Long systemTime) {
    this.systemTime = systemTime;
  }
  
  public MatchLogItemType getMatchLogItemType() {
    return this.matchLogItemType;
  }
  
  public void setMatchLogItemType(MatchLogItemType matchLogItemType) {
    this.matchLogItemType = matchLogItemType;
  }
  
  public Integer getBlueGeneralPoints() {
    return this.blueGeneralPoints;
  }
  
  public void setBlueGeneralPoints(Integer blueGeneralPoints) {
    this.blueGeneralPoints = blueGeneralPoints;
  }
  
  public Integer getRedGeneralPoints() {
    return this.redGeneralPoints;
  }
  
  public void setRedGeneralPoints(Integer redGeneralPoints) {
    this.redGeneralPoints = redGeneralPoints;
  }
  
  public Integer getBlueAddPoints() {
    return this.blueAddPoints;
  }
  
  public void setBlueAddPoints(Integer blueAddPoints) {
    this.blueAddPoints = blueAddPoints;
  }
  
  public Integer getRedAddPoints() {
    return this.redAddPoints;
  }
  
  public void setRedAddPoints(Integer redAddPoints) {
    this.redAddPoints = redAddPoints;
  }
  
  public Integer getBluePoints() {
    return this.bluePoints;
  }
  
  public void setBluePoints(Integer bluePoints) {
    this.bluePoints = bluePoints;
  }
  
  public Integer getRedPoints() {
    return this.redPoints;
  }
  
  public void setRedPoints(Integer redPoints) {
    this.redPoints = redPoints;
  }
  
  public Integer getBluePenalties() {
    return this.bluePenalties;
  }
  
  public void setBluePenalties(Integer bluePenalties) {
    this.bluePenalties = bluePenalties;
  }
  
  public Integer getRedPenalties() {
    return this.redPenalties;
  }
  
  public void setRedPenalties(Integer redPenalties) {
    this.redPenalties = redPenalties;
  }
  
  public Integer getBlueTotalPenalties() {
    return this.blueTotalPenalties;
  }
  
  public void setBlueTotalPenalties(Integer blueTotalPenalties) {
    this.blueTotalPenalties = blueTotalPenalties;
  }
  
  public Integer getRedTotalPenalties() {
    return this.redTotalPenalties;
  }
  
  public void setRedTotalPenalties(Integer redTotalPenalties) {
    this.redTotalPenalties = redTotalPenalties;
  }
  
  public Integer getBlueGoldenPointHits() {
    return this.blueGoldenPointHits;
  }
  
  public void setBlueGoldenPointHits(Integer blueGoldenPointHits) {
    this.blueGoldenPointHits = blueGoldenPointHits;
  }
  
  public Integer getRedGoldenPointHits() {
    return this.redGoldenPointHits;
  }
  
  public void setRedGoldenPointHits(Integer redGoldenPointHits) {
    this.redGoldenPointHits = redGoldenPointHits;
  }
  
  public Integer getBlueGoldenPointPenalties() {
    return this.blueGoldenPointPenalties;
  }
  
  public void setBlueGoldenPointPenalties(Integer blueGoldenPointPenalties) {
    this.blueGoldenPointPenalties = blueGoldenPointPenalties;
  }
  
  public Integer getRedGoldenPointPenalties() {
    return this.redGoldenPointPenalties;
  }
  
  public void setRedGoldenPointPenalties(Integer redGoldenPointPenalties) {
    this.redGoldenPointPenalties = redGoldenPointPenalties;
  }
  
  public Boolean getGoldenPointRound() {
    return this.isGoldenPointRound;
  }
  
  public void setGoldenPointRound(Boolean goldenPointRound) {
    this.isGoldenPointRound = goldenPointRound;
  }
  
  public Integer getBlueVideoQuota() {
    return this.blueVideoQuota;
  }
  
  public void setBlueVideoQuota(Integer blueVideoQuota) {
    this.blueVideoQuota = blueVideoQuota;
  }
  
  public Integer getRedVideoQuota() {
    return this.redVideoQuota;
  }
  
  public void setRedVideoQuota(Integer redVideoQuota) {
    this.redVideoQuota = redVideoQuota;
  }
  
  public String getEntryValue() {
    return this.entryValue;
  }
  
  public void setEntryValue(String entryValue) {
    this.entryValue = entryValue;
  }
}
