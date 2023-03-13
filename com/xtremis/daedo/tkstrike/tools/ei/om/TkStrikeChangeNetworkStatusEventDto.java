package com.xtremis.daedo.tkstrike.tools.ei.om;

import com.xtremis.daedo.tkstrike.tools.om.BaseJSONDto;

public class TkStrikeChangeNetworkStatusEventDto extends BaseJSONDto {
  private static final long serialVersionUID = -3963065689348783593L;
  
  private Long eventTimestamp;
  
  private String networkStatus;
  
  private String prevNetworkStatus;
  
  public Long getEventTimestamp() {
    return this.eventTimestamp;
  }
  
  public void setEventTimestamp(Long eventTimestamp) {
    this.eventTimestamp = eventTimestamp;
  }
  
  public String getNetworkStatus() {
    return this.networkStatus;
  }
  
  public void setNetworkStatus(String networkStatus) {
    this.networkStatus = networkStatus;
  }
  
  public String getPrevNetworkStatus() {
    return this.prevNetworkStatus;
  }
  
  public void setPrevNetworkStatus(String prevNetworkStatus) {
    this.prevNetworkStatus = prevNetworkStatus;
  }
}
