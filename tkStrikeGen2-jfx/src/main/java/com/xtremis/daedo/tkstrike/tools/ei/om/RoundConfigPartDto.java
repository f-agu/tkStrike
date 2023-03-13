package com.xtremis.daedo.tkstrike.tools.ei.om;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xtremis.daedo.tkstrike.tools.om.BaseJSONDto;
import java.util.Calendar;
import org.apache.commons.lang3.StringUtils;

public class RoundConfigPartDto extends BaseJSONDto {
  private static final long serialVersionUID = 6257030544162438372L;
  
  private String id;
  
  private String roundConfigId;
  
  private Integer partNumber;
  
  private Integer partTimeMinutes;
  
  private Integer partTimeSeconds;
  
  public String getId() {
    return this.id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getRoundConfigId() {
    return this.roundConfigId;
  }
  
  public void setRoundConfigId(String roundConfigId) {
    this.roundConfigId = roundConfigId;
  }
  
  public Integer getPartNumber() {
    return this.partNumber;
  }
  
  public void setPartNumber(Integer partNumber) {
    this.partNumber = partNumber;
  }
  
  @JsonIgnore
  public String getPartTimeStr() {
    return (this.partTimeMinutes != null && this.partTimeSeconds != null) ? (df.format(this.partTimeMinutes) + ":" + df.format(this.partTimeSeconds)) : null;
  }
  
  public void setPartTimeStr(String partTimeStr) {
    if (StringUtils.isNotBlank(partTimeStr))
      try {
        Calendar calRound = Calendar.getInstance();
        calRound.setTime(timeDf.parse(partTimeStr));
        this.partTimeMinutes = Integer.valueOf(calRound.get(12));
        this.partTimeSeconds = Integer.valueOf(calRound.get(13));
      } catch (Exception exception) {} 
  }
  
  public Integer getPartTimeMinutes() {
    return this.partTimeMinutes;
  }
  
  public void setPartTimeMinutes(Integer partTimeMinutes) {
    this.partTimeMinutes = partTimeMinutes;
  }
  
  public Integer getPartTimeSeconds() {
    return this.partTimeSeconds;
  }
  
  public void setPartTimeSeconds(Integer partTimeSeconds) {
    this.partTimeSeconds = partTimeSeconds;
  }
}
