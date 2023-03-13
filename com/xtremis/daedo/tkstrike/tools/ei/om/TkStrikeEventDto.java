package com.xtremis.daedo.tkstrike.tools.ei.om;

import com.xtremis.daedo.tkstrike.tools.om.BaseJSONDto;

public class TkStrikeEventDto extends BaseJSONDto {
  private static final long serialVersionUID = 1389364538169375361L;
  
  private String matchNumber;
  
  private String matchVMInternalId;
  
  private String matchVMRingNumber;
  
  private String matchCategoryName;
  
  private String matchCategoryGender;
  
  private String matchSubCategoryName;
  
  private Integer roundNumber;
  
  private String roundNumberStr;
  
  private Long tkStrikeSystemTimestamp;
  
  private String tkStrikeSystemTimestampStr;
  
  private Long roundTimestamp;
  
  private String roundTimestampStr;
  
  private String eventType;
  
  private boolean eventAddPoints;
  
  private boolean eventRemovePoints;
  
  private Integer bluePoints;
  
  private Integer bluePenalties;
  
  private Integer blueRoundWins;
  
  private Integer redPoints;
  
  private Integer redPenalties;
  
  private Integer redRoundWins;
  
  private String matchWinner;
  
  private String matchFinalDecision;
  
  private Integer blueBodyLevel;
  
  private Integer blueHeadLevel;
  
  private Integer redBodyLevel;
  
  private Integer redHeadLevel;
  
  private Integer calledByJudgeNumber;
  
  private Integer hitlevel;
  
  public TkStrikeEventDto() {}
  
  public TkStrikeEventDto(String matchNumber, String matchVMInternalId, String matchVMRingNumber, Integer roundNumber, String roundNumberStr, Long tkStrikeSystemTimestamp, Long roundTimestamp, String eventType, boolean eventAddPoints, boolean eventRemovePoints, Integer bluePoints, Integer bluePenalties, Integer redPoints, Integer redPenalties, Integer blueRoundWins, Integer redRoundWins, String matchWinner, String matchFinalDecision) {
    this.matchNumber = matchNumber;
    this.matchVMInternalId = matchVMInternalId;
    this.matchVMRingNumber = matchVMRingNumber;
    this.roundNumber = roundNumber;
    this.roundNumberStr = roundNumberStr;
    this.tkStrikeSystemTimestamp = tkStrikeSystemTimestamp;
    this.roundTimestamp = roundTimestamp;
    this.eventType = eventType;
    this.eventAddPoints = eventAddPoints;
    this.eventRemovePoints = eventRemovePoints;
    this.bluePoints = bluePoints;
    this.bluePenalties = bluePenalties;
    this.redPoints = redPoints;
    this.redPenalties = redPenalties;
    this.blueRoundWins = blueRoundWins;
    this.redRoundWins = redRoundWins;
    this.matchWinner = matchWinner;
    this.matchFinalDecision = matchFinalDecision;
  }
  
  public TkStrikeEventDto(String matchNumber, String matchVMInternalId, String matchVMRingNumber, Integer roundNumber, String roundNumberStr, Long tkStrikeSystemTimestamp, String tkStrikeSystemTimestampStr, Long roundTimestamp, String roundTimestampStr, String eventType, boolean eventAddPoints, boolean eventRemovePoints, Integer bluePoints, Integer bluePenalties, Integer redPoints, Integer redPenalties, Integer blueRoundWins, Integer redRoundWins, String matchWinner, String matchFinalDecision, Integer blueBodyLevel, Integer blueHeadLevel, Integer redBodyLevel, Integer redHeadLevel, Integer calledByJudgeNumber, Integer hitlevel) {
    this.matchNumber = matchNumber;
    this.matchVMInternalId = matchVMInternalId;
    this.matchVMRingNumber = matchVMRingNumber;
    this.roundNumber = roundNumber;
    this.roundNumberStr = roundNumberStr;
    this.tkStrikeSystemTimestamp = tkStrikeSystemTimestamp;
    this.tkStrikeSystemTimestampStr = tkStrikeSystemTimestampStr;
    this.roundTimestamp = roundTimestamp;
    this.roundTimestampStr = roundTimestampStr;
    this.eventType = eventType;
    this.eventAddPoints = eventAddPoints;
    this.eventRemovePoints = eventRemovePoints;
    this.bluePoints = bluePoints;
    this.bluePenalties = bluePenalties;
    this.redPoints = redPoints;
    this.redPenalties = redPenalties;
    this.blueRoundWins = blueRoundWins;
    this.redRoundWins = redRoundWins;
    this.matchWinner = matchWinner;
    this.matchFinalDecision = matchFinalDecision;
    this.blueBodyLevel = blueBodyLevel;
    this.blueHeadLevel = blueHeadLevel;
    this.redBodyLevel = redBodyLevel;
    this.redHeadLevel = redHeadLevel;
    this.calledByJudgeNumber = calledByJudgeNumber;
    this.hitlevel = hitlevel;
  }
  
  public String getTkStrikeSystemTimestampStr() {
    return this.tkStrikeSystemTimestampStr;
  }
  
  public void setTkStrikeSystemTimestampStr(String tkStrikeSystemTimestampStr) {
    this.tkStrikeSystemTimestampStr = tkStrikeSystemTimestampStr;
  }
  
  public String getRoundTimestampStr() {
    return this.roundTimestampStr;
  }
  
  public void setRoundTimestampStr(String roundTimestampStr) {
    this.roundTimestampStr = roundTimestampStr;
  }
  
  public String getMatchNumber() {
    return this.matchNumber;
  }
  
  public void setMatchNumber(String matchNumber) {
    this.matchNumber = matchNumber;
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
  
  public Long getTkStrikeSystemTimestamp() {
    return this.tkStrikeSystemTimestamp;
  }
  
  public void setTkStrikeSystemTimestamp(Long tkStrikeSystemTimestamp) {
    this.tkStrikeSystemTimestamp = tkStrikeSystemTimestamp;
  }
  
  public Long getRoundTimestamp() {
    return this.roundTimestamp;
  }
  
  public void setRoundTimestamp(Long roundTimestamp) {
    this.roundTimestamp = roundTimestamp;
  }
  
  public String getEventType() {
    return this.eventType;
  }
  
  public void setEventType(String eventType) {
    this.eventType = eventType;
  }
  
  public boolean isEventAddPoints() {
    return this.eventAddPoints;
  }
  
  public void setEventAddPoints(boolean eventAddPoints) {
    this.eventAddPoints = eventAddPoints;
  }
  
  public boolean isEventRemovePoints() {
    return this.eventRemovePoints;
  }
  
  public void setEventRemovePoints(boolean eventRemovePoints) {
    this.eventRemovePoints = eventRemovePoints;
  }
  
  public Integer getBluePoints() {
    return this.bluePoints;
  }
  
  public void setBluePoints(Integer bluePoints) {
    this.bluePoints = bluePoints;
  }
  
  public Integer getRedPenalties() {
    return this.redPenalties;
  }
  
  public void setRedPenalties(Integer redPenalties) {
    this.redPenalties = redPenalties;
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
  
  public String getMatchWinner() {
    return this.matchWinner;
  }
  
  public void setMatchWinner(String matchWinner) {
    this.matchWinner = matchWinner;
  }
  
  public String getMatchFinalDecision() {
    return this.matchFinalDecision;
  }
  
  public void setMatchFinalDecision(String matchFinalDecision) {
    this.matchFinalDecision = matchFinalDecision;
  }
  
  public Integer getBlueBodyLevel() {
    return this.blueBodyLevel;
  }
  
  public void setBlueBodyLevel(Integer blueBodyLevel) {
    this.blueBodyLevel = blueBodyLevel;
  }
  
  public Integer getBlueHeadLevel() {
    return this.blueHeadLevel;
  }
  
  public void setBlueHeadLevel(Integer blueHeadLevel) {
    this.blueHeadLevel = blueHeadLevel;
  }
  
  public Integer getRedBodyLevel() {
    return this.redBodyLevel;
  }
  
  public void setRedBodyLevel(Integer redBodyLevel) {
    this.redBodyLevel = redBodyLevel;
  }
  
  public Integer getRedHeadLevel() {
    return this.redHeadLevel;
  }
  
  public void setRedHeadLevel(Integer redHeadLevel) {
    this.redHeadLevel = redHeadLevel;
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
  
  public String getMatchVMInternalId() {
    return this.matchVMInternalId;
  }
  
  public void setMatchVMInternalId(String matchVMInternalId) {
    this.matchVMInternalId = matchVMInternalId;
  }
  
  public String getMatchVMRingNumber() {
    return this.matchVMRingNumber;
  }
  
  public void setMatchVMRingNumber(String matchVMRingNumber) {
    this.matchVMRingNumber = matchVMRingNumber;
  }
  
  public String getMatchCategoryName() {
    return this.matchCategoryName;
  }
  
  public void setMatchCategoryName(String matchCategoryName) {
    this.matchCategoryName = matchCategoryName;
  }
  
  public String getMatchCategoryGender() {
    return this.matchCategoryGender;
  }
  
  public void setMatchCategoryGender(String matchCategoryGender) {
    this.matchCategoryGender = matchCategoryGender;
  }
  
  public String getMatchSubCategoryName() {
    return this.matchSubCategoryName;
  }
  
  public void setMatchSubCategoryName(String matchSubCategoryName) {
    this.matchSubCategoryName = matchSubCategoryName;
  }
  
  public Integer getCalledByJudgeNumber() {
    return this.calledByJudgeNumber;
  }
  
  public void setCalledByJudgeNumber(Integer calledByJudgeNumber) {
    this.calledByJudgeNumber = calledByJudgeNumber;
  }
  
  public Integer getHitlevel() {
    return this.hitlevel;
  }
  
  public void setHitlevel(Integer hitlevel) {
    this.hitlevel = hitlevel;
  }
}
