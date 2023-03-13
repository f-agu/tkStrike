package com.xtremis.daedo.tkstrike.om;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.ArrayUtils;

public class MatchLogItemDto extends CommonMatchLogItemDto {
  private static final long serialVersionUID = 1770709606898422323L;
  
  private final String[] headers = new String[] { "blueVideoQuota", "redVideoQuota" };
  
  private Integer blueVideoQuota;
  
  private Integer redVideoQuota;
  
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
  
  @JsonIgnore
  public String[] getCSVHeaders() {
    return (String[])ArrayUtils.addAll((Object[])super.getCSVHeaders(), (Object[])this.headers);
  }
  
  @JsonIgnore
  public String[] getCSVValues() {
    return super.getCSVValues();
  }
}
