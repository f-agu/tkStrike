package com.xtremis.daedo.tkstrike.tools.ei.om;

import com.xtremis.daedo.tkstrike.tools.om.BaseJSONDto;

public class MatchResultDto extends BaseJSONDto {
  private static final long serialVersionUID = 5156628776508088292L;
  
  private String vmMatchInternalId;
  
  private String matchNumber;
  
  private String vmRingNumber;
  
  private String categoryName;
  
  private String categoryGender;
  
  private String subCategoryName;
  
  private String phaseName;
  
  private Long matchStartTime;
  
  private Long matchEndTime;
  
  private Boolean goldenPointTieBreakerHaveTieBreaker;
  
  private Integer goldenPointTieBreakerBluePunches;
  
  private Integer goldenPointTieBreakerBlueRoundWins;
  
  private Integer goldenPointTieBreakerBlueHits;
  
  private Integer goldenPointTieBreakerBluePenalties;
  
  private Integer goldenPointTieBreakerRedPunches;
  
  private Integer goldenPointTieBreakerRedRoundWins;
  
  private Integer goldenPointTieBreakerRedHits;
  
  private Integer goldenPointTieBreakerRedPenalties;
  
  private Integer goldenPointTieBreakerBluePARATechPoints;
  
  private Integer goldenPointTieBreakerRedPARATechPoints;
  
  private String matchVictoryCriteria;
  
  private Boolean paraTkdMatch;
  
  private String matchWinnerColor;
  
  private AthleteDto matchWinner;
  
  private String matchFinalDecision;
  
  private Integer roundFinish;
  
  private Integer bluePoints;
  
  private Integer blueRoundWins;
  
  private Integer bluePenalties;
  
  private Integer blueVideoQuota;
  
  private Integer redPoints;
  
  private Integer redRoundWins;
  
  private Integer redPenalties;
  
  private Integer redVideoQuota;
  
  public String getVmMatchInternalId() {
    return this.vmMatchInternalId;
  }
  
  public void setVmMatchInternalId(String vmMatchInternalId) {
    this.vmMatchInternalId = vmMatchInternalId;
  }
  
  public String getMatchNumber() {
    return this.matchNumber;
  }
  
  public void setMatchNumber(String matchNumber) {
    this.matchNumber = matchNumber;
  }
  
  public String getMatchWinnerColor() {
    return this.matchWinnerColor;
  }
  
  public void setMatchWinnerColor(String matchWinnerColor) {
    this.matchWinnerColor = matchWinnerColor;
  }
  
  public AthleteDto getMatchWinner() {
    return this.matchWinner;
  }
  
  public void setMatchWinner(AthleteDto matchWinner) {
    this.matchWinner = matchWinner;
  }
  
  public String getMatchFinalDecision() {
    return this.matchFinalDecision;
  }
  
  public void setMatchFinalDecision(String matchFinalDecision) {
    this.matchFinalDecision = matchFinalDecision;
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
  
  public Integer getBlueRoundWins() {
    return this.blueRoundWins;
  }
  
  public void setBlueRoundWins(Integer blueRoundWins) {
    this.blueRoundWins = blueRoundWins;
  }
  
  public Integer getRedRoundWins() {
    return this.redRoundWins;
  }
  
  public void setRedRoundWins(Integer redRoundWins) {
    this.redRoundWins = redRoundWins;
  }
  
  public String getVmRingNumber() {
    return this.vmRingNumber;
  }
  
  public void setVmRingNumber(String vmRingNumber) {
    this.vmRingNumber = vmRingNumber;
  }
  
  public String getCategoryName() {
    return this.categoryName;
  }
  
  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }
  
  public String getCategoryGender() {
    return this.categoryGender;
  }
  
  public void setCategoryGender(String categoryGender) {
    this.categoryGender = categoryGender;
  }
  
  public String getSubCategoryName() {
    return this.subCategoryName;
  }
  
  public void setSubCategoryName(String subCategoryName) {
    this.subCategoryName = subCategoryName;
  }
  
  public String getPhaseName() {
    return this.phaseName;
  }
  
  public void setPhaseName(String phaseName) {
    this.phaseName = phaseName;
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
  
  public Boolean getGoldenPointTieBreakerHaveTieBreaker() {
    return this.goldenPointTieBreakerHaveTieBreaker;
  }
  
  public void setGoldenPointTieBreakerHaveTieBreaker(Boolean goldenPointTieBreakerHaveTieBreaker) {
    this.goldenPointTieBreakerHaveTieBreaker = goldenPointTieBreakerHaveTieBreaker;
  }
  
  public Integer getGoldenPointTieBreakerBluePunches() {
    return this.goldenPointTieBreakerBluePunches;
  }
  
  public void setGoldenPointTieBreakerBluePunches(Integer goldenPointTieBreakerBluePunches) {
    this.goldenPointTieBreakerBluePunches = goldenPointTieBreakerBluePunches;
  }
  
  public Integer getGoldenPointTieBreakerBlueRoundWins() {
    return this.goldenPointTieBreakerBlueRoundWins;
  }
  
  public void setGoldenPointTieBreakerBlueRoundWins(Integer goldenPointTieBreakerBlueRoundWins) {
    this.goldenPointTieBreakerBlueRoundWins = goldenPointTieBreakerBlueRoundWins;
  }
  
  public Integer getGoldenPointTieBreakerBlueHits() {
    return this.goldenPointTieBreakerBlueHits;
  }
  
  public void setGoldenPointTieBreakerBlueHits(Integer goldenPointTieBreakerBlueHits) {
    this.goldenPointTieBreakerBlueHits = goldenPointTieBreakerBlueHits;
  }
  
  public Integer getGoldenPointTieBreakerBluePenalties() {
    return this.goldenPointTieBreakerBluePenalties;
  }
  
  public void setGoldenPointTieBreakerBluePenalties(Integer goldenPointTieBreakerBluePenalties) {
    this.goldenPointTieBreakerBluePenalties = goldenPointTieBreakerBluePenalties;
  }
  
  public Integer getGoldenPointTieBreakerRedPunches() {
    return this.goldenPointTieBreakerRedPunches;
  }
  
  public void setGoldenPointTieBreakerRedPunches(Integer goldenPointTieBreakerRedPunches) {
    this.goldenPointTieBreakerRedPunches = goldenPointTieBreakerRedPunches;
  }
  
  public Integer getGoldenPointTieBreakerRedRoundWins() {
    return this.goldenPointTieBreakerRedRoundWins;
  }
  
  public void setGoldenPointTieBreakerRedRoundWins(Integer goldenPointTieBreakerRedRoundWins) {
    this.goldenPointTieBreakerRedRoundWins = goldenPointTieBreakerRedRoundWins;
  }
  
  public Integer getGoldenPointTieBreakerRedHits() {
    return this.goldenPointTieBreakerRedHits;
  }
  
  public void setGoldenPointTieBreakerRedHits(Integer goldenPointTieBreakerRedHits) {
    this.goldenPointTieBreakerRedHits = goldenPointTieBreakerRedHits;
  }
  
  public Integer getGoldenPointTieBreakerRedPenalties() {
    return this.goldenPointTieBreakerRedPenalties;
  }
  
  public void setGoldenPointTieBreakerRedPenalties(Integer goldenPointTieBreakerRedPenalties) {
    this.goldenPointTieBreakerRedPenalties = goldenPointTieBreakerRedPenalties;
  }
  
  public String getMatchVictoryCriteria() {
    return this.matchVictoryCriteria;
  }
  
  public void setMatchVictoryCriteria(String matchVictoryCriteria) {
    this.matchVictoryCriteria = matchVictoryCriteria;
  }
  
  public Integer getGoldenPointTieBreakerBluePARATechPoints() {
    return this.goldenPointTieBreakerBluePARATechPoints;
  }
  
  public void setGoldenPointTieBreakerBluePARATechPoints(Integer goldenPointTieBreakerBluePARATechPoints) {
    this.goldenPointTieBreakerBluePARATechPoints = goldenPointTieBreakerBluePARATechPoints;
  }
  
  public Integer getGoldenPointTieBreakerRedPARATechPoints() {
    return this.goldenPointTieBreakerRedPARATechPoints;
  }
  
  public void setGoldenPointTieBreakerRedPARATechPoints(Integer goldenPointTieBreakerRedPARATechPoints) {
    this.goldenPointTieBreakerRedPARATechPoints = goldenPointTieBreakerRedPARATechPoints;
  }
  
  public Boolean getParaTkdMatch() {
    return this.paraTkdMatch;
  }
  
  public void setParaTkdMatch(Boolean paraTkdMatch) {
    this.paraTkdMatch = paraTkdMatch;
  }
  
  public Integer getRoundFinish() {
    return this.roundFinish;
  }
  
  public void setRoundFinish(Integer roundFinish) {
    this.roundFinish = roundFinish;
  }
}
