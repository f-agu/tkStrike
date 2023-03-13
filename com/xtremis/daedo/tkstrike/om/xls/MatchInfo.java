package com.xtremis.daedo.tkstrike.om.xls;

import java.io.Serializable;
import java.time.Duration;
import java.util.Date;

public class MatchInfo implements Serializable {
  private static final long serialVersionUID = 7223534070707817350L;
  
  private String matchNumber;
  
  private Date matchDate;
  
  private String weightName;
  
  private String divisionName;
  
  private String phaseName;
  
  private Integer bodyMinLevel;
  
  private Integer headMinLevel;
  
  private Long matchStartTimestamp;
  
  private Long matchEndTimestamp;
  
  private String blueName;
  
  private String blueFlagAbbreviation;
  
  private String blueFlagName;
  
  private String redName;
  
  private String redFlagAbbreviation;
  
  private String redFlagName;
  
  private Duration matchDuration;
  
  private Integer callDoctorTimes = Integer.valueOf(0);
  
  private Integer callVideoReplayTimes = Integer.valueOf(0);
  
  public String getMatchNumber() {
    return this.matchNumber;
  }
  
  public void setMatchNumber(String matchNumber) {
    this.matchNumber = matchNumber;
  }
  
  public Date getMatchDate() {
    return this.matchDate;
  }
  
  public void setMatchDate(Date matchDate) {
    this.matchDate = matchDate;
  }
  
  public String getWeightName() {
    return this.weightName;
  }
  
  public void setWeightName(String weightName) {
    this.weightName = weightName;
  }
  
  public String getDivisionName() {
    return this.divisionName;
  }
  
  public void setDivisionName(String divisionName) {
    this.divisionName = divisionName;
  }
  
  public String getPhaseName() {
    return this.phaseName;
  }
  
  public void setPhaseName(String phaseName) {
    this.phaseName = phaseName;
  }
  
  public Integer getBodyMinLevel() {
    return this.bodyMinLevel;
  }
  
  public void setBodyMinLevel(Integer bodyMinLevel) {
    this.bodyMinLevel = bodyMinLevel;
  }
  
  public Integer getHeadMinLevel() {
    return this.headMinLevel;
  }
  
  public void setHeadMinLevel(Integer headMinLevel) {
    this.headMinLevel = headMinLevel;
  }
  
  public Long getMatchStartTimestamp() {
    return this.matchStartTimestamp;
  }
  
  public void setMatchStartTimestamp(Long matchStartTimestamp) {
    this.matchStartTimestamp = matchStartTimestamp;
  }
  
  public Long getMatchEndTimestamp() {
    return this.matchEndTimestamp;
  }
  
  public void setMatchEndTimestamp(Long matchEndTimestamp) {
    this.matchEndTimestamp = matchEndTimestamp;
  }
  
  public String getBlueName() {
    return this.blueName;
  }
  
  public void setBlueName(String blueName) {
    this.blueName = blueName;
  }
  
  public String getBlueFlagAbbreviation() {
    return this.blueFlagAbbreviation;
  }
  
  public void setBlueFlagAbbreviation(String blueFlagAbbreviation) {
    this.blueFlagAbbreviation = blueFlagAbbreviation;
  }
  
  public String getBlueFlagName() {
    return this.blueFlagName;
  }
  
  public void setBlueFlagName(String blueFlagName) {
    this.blueFlagName = blueFlagName;
  }
  
  public String getRedName() {
    return this.redName;
  }
  
  public void setRedName(String redName) {
    this.redName = redName;
  }
  
  public String getRedFlagAbbreviation() {
    return this.redFlagAbbreviation;
  }
  
  public void setRedFlagAbbreviation(String redFlagAbbreviation) {
    this.redFlagAbbreviation = redFlagAbbreviation;
  }
  
  public String getRedFlagName() {
    return this.redFlagName;
  }
  
  public void setRedFlagName(String redFlagName) {
    this.redFlagName = redFlagName;
  }
  
  public Duration getMatchDuration() {
    return this.matchDuration;
  }
  
  public void setMatchDuration(Duration matchDuration) {
    this.matchDuration = matchDuration;
  }
  
  public Integer getCallDoctorTimes() {
    return this.callDoctorTimes;
  }
  
  public void setCallDoctorTimes(Integer callDoctorTimes) {
    this.callDoctorTimes = callDoctorTimes;
  }
  
  public Integer getCallVideoReplayTimes() {
    return this.callVideoReplayTimes;
  }
  
  public void setCallVideoReplayTimes(Integer callVideoReplayTimes) {
    this.callVideoReplayTimes = callVideoReplayTimes;
  }
}
