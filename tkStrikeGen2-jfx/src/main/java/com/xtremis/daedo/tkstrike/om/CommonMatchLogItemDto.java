package com.xtremis.daedo.tkstrike.om;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xtremis.daedo.tkstrike.orm.model.MatchLogItemType;
import com.xtremis.daedo.tkstrike.tools.om.BaseJSONDto;
import org.apache.commons.beanutils.PropertyUtils;

public class CommonMatchLogItemDto extends BaseJSONDto {
  private static final long serialVersionUID = 7927909847740049359L;
  
  private final String[] headers = new String[] { 
      "eventTime", "roundNumber", "roundTime", "systemTime", "matchLogItemType", "entryValue", "blueAddPoints", "redAddPoints", "score", "blueGeneralPoints", 
      "redGeneralPoints", "bluePoints", "redPoints", "bluePenalties", "redPenalties", "blueTotalPenalties", "redTotalPenalties", "blueGoldenPointHits", "redGoldenPointHits", "blueGoldenPointPenalties", 
      "redGoldenPointPenalties", "goldenPointRound", "entryValue" };
  
  private String id;
  
  private String matchLogId;
  
  private Integer roundNumber;
  
  private String roundNumberStr;
  
  private Long eventTime;
  
  private Long roundTime;
  
  private Long systemTime;
  
  private Integer blueAddPoints;
  
  private Integer redAddPoints;
  
  private Integer blueGeneralPoints;
  
  private Integer redGeneralPoints;
  
  private Integer bluePoints;
  
  private Integer redPoints;
  
  private Integer bluePenalties;
  
  private Integer redPenalties;
  
  private Integer blueTotalPenalties;
  
  private Integer redTotalPenalties;
  
  private Integer blueGoldenPointHits;
  
  private Integer redGoldenPointHits;
  
  private Integer blueGoldenPointPenalties;
  
  private Integer redGoldenPointPenalties;
  
  private Boolean isGoldenPointRound;
  
  private String entryValue;
  
  private MatchLogItemType matchLogItemType;
  
  public String getId() {
    return this.id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getMatchLogId() {
    return this.matchLogId;
  }
  
  public void setMatchLogId(String matchLogId) {
    this.matchLogId = matchLogId;
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
  
  public String getEntryValue() {
    return this.entryValue;
  }
  
  public void setEntryValue(String entryValue) {
    this.entryValue = entryValue;
  }
  
  @JsonIgnore
  public String getScore() {
    return getBlueGeneralPoints() + "-" + getRedGeneralPoints();
  }
  
  public void setScore(String score) {}
  
  public MatchLogItemType getMatchLogItemType() {
    return this.matchLogItemType;
  }
  
  public void setMatchLogItemType(MatchLogItemType matchLogItemType) {
    this.matchLogItemType = matchLogItemType;
  }
  
  @JsonIgnore
  public String[] getCSVHeaders() {
    return this.headers;
  }
  
  @JsonIgnore
  public String[] getCSVValues() {
    String[] headers4Work = getCSVHeaders();
    String[] values = new String[headers4Work.length];
    for (int i = 0; i < headers4Work.length; i++) {
      String header = headers4Work[i];
      String value = "";
      try {
        Object obValue = PropertyUtils.getProperty(this, header);
        value = (obValue != null) ? ((obValue instanceof Boolean) ? Boolean.toString(((Boolean)obValue).booleanValue()) : obValue.toString()) : "";
      } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|NoSuchMethodException e) {
        value = "";
      } 
      values[i] = value;
    } 
    return values;
  }
}
