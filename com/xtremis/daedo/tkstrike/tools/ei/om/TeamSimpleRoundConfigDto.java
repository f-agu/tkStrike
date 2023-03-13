package com.xtremis.daedo.tkstrike.tools.ei.om;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xtremis.daedo.tkstrike.tools.om.BaseJSONDto;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class TeamSimpleRoundConfigDto extends BaseJSONDto {
  private static final long serialVersionUID = 3654831257746075810L;
  
  private String id;
  
  private String matchConfigurationId;
  
  private Integer roundNumber;
  
  private TeamRoundType roundType;
  
  private Integer roundTimeMinutes;
  
  private Integer roundTimeSeconds;
  
  private Integer partsNumber;
  
  private List<RoundConfigPartDto> parts;
  
  private Integer goldenPointTimeMinutes;
  
  private Integer goldenPointTimeSeconds;
  
  private Integer goldenPointFinishPoints;
  
  public String getId() {
    return this.id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getMatchConfigurationId() {
    return this.matchConfigurationId;
  }
  
  public void setMatchConfigurationId(String matchConfigurationId) {
    this.matchConfigurationId = matchConfigurationId;
  }
  
  public Integer getRoundNumber() {
    return this.roundNumber;
  }
  
  public void setRoundNumber(Integer roundNumber) {
    this.roundNumber = roundNumber;
  }
  
  public TeamRoundType getRoundType() {
    return this.roundType;
  }
  
  public void setRoundType(TeamRoundType roundType) {
    this.roundType = roundType;
  }
  
  @JsonIgnore
  public String getRoundTimeStr() {
    return (this.roundTimeMinutes != null && this.roundTimeSeconds != null) ? (df.format(this.roundTimeMinutes) + ":" + df.format(this.roundTimeSeconds)) : null;
  }
  
  public void setRoundTimeStr(String roundTimeStr) {
    if (StringUtils.isNotBlank(roundTimeStr))
      try {
        Calendar calRound = Calendar.getInstance();
        calRound.setTime(timeDf.parse(roundTimeStr));
        this.roundTimeMinutes = Integer.valueOf(calRound.get(12));
        this.roundTimeSeconds = Integer.valueOf(calRound.get(13));
      } catch (Exception exception) {} 
  }
  
  public Integer getRoundTimeMinutes() {
    return this.roundTimeMinutes;
  }
  
  public void setRoundTimeMinutes(Integer roundTimeMinutes) {
    this.roundTimeMinutes = roundTimeMinutes;
  }
  
  public Integer getRoundTimeSeconds() {
    return this.roundTimeSeconds;
  }
  
  public void setRoundTimeSeconds(Integer roundTimeSeconds) {
    this.roundTimeSeconds = roundTimeSeconds;
  }
  
  public Integer getPartsNumber() {
    return this.partsNumber;
  }
  
  public void setPartsNumber(Integer partsNumber) {
    this.partsNumber = partsNumber;
  }
  
  public List<RoundConfigPartDto> getParts() {
    return this.parts;
  }
  
  public void setParts(List<RoundConfigPartDto> parts) {
    this.parts = parts;
  }
  
  @JsonIgnore
  public String getGoldenPointTimeStr() {
    return (this.goldenPointTimeMinutes != null && this.goldenPointTimeSeconds != null) ? (df.format(this.goldenPointTimeMinutes) + ":" + df.format(this.goldenPointTimeSeconds)) : null;
  }
  
  public void setGoldenPointTimeStr(String goldenPointTimeStr) {
    if (StringUtils.isNotBlank(goldenPointTimeStr))
      try {
        Calendar calRound = Calendar.getInstance();
        calRound.setTime(timeDf.parse(goldenPointTimeStr));
        this.goldenPointTimeMinutes = Integer.valueOf(calRound.get(12));
        this.goldenPointTimeSeconds = Integer.valueOf(calRound.get(13));
      } catch (Exception exception) {} 
  }
  
  public Integer getGoldenPointTimeMinutes() {
    return this.goldenPointTimeMinutes;
  }
  
  public void setGoldenPointTimeMinutes(Integer goldenPointTimeMinutes) {
    this.goldenPointTimeMinutes = goldenPointTimeMinutes;
  }
  
  public Integer getGoldenPointTimeSeconds() {
    return this.goldenPointTimeSeconds;
  }
  
  public void setGoldenPointTimeSeconds(Integer goldenPointTimeSeconds) {
    this.goldenPointTimeSeconds = goldenPointTimeSeconds;
  }
  
  public Integer getGoldenPointFinishPoints() {
    return this.goldenPointFinishPoints;
  }
  
  public void setGoldenPointFinishPoints(Integer goldenPointFinishPoints) {
    this.goldenPointFinishPoints = goldenPointFinishPoints;
  }
}
