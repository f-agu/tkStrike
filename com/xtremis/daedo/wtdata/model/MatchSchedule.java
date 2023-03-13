package com.xtremis.daedo.wtdata.model;

import com.xtremis.daedo.wtdata.model.constants.ScheduleStatus;
import java.io.Serializable;

public class MatchSchedule implements Serializable {
  private ScheduleStatus status;
  
  private String scheduledStart;
  
  private String estimatedStart;
  
  private String actualStart;
  
  public ScheduleStatus getStatus() {
    return this.status;
  }
  
  public void setStatus(ScheduleStatus status) {
    this.status = status;
  }
  
  public String getScheduledStart() {
    return this.scheduledStart;
  }
  
  public void setScheduledStart(String scheduledStart) {
    this.scheduledStart = scheduledStart;
  }
  
  public String getEstimatedStart() {
    return this.estimatedStart;
  }
  
  public void setEstimatedStart(String estimatedStart) {
    this.estimatedStart = estimatedStart;
  }
  
  public String getActualStart() {
    return this.actualStart;
  }
  
  public void setActualStart(String actualStart) {
    this.actualStart = actualStart;
  }
  
  public String toString() {
    return "MatchSchedule{status=" + this.status + ", scheduledStart='" + this.scheduledStart + '\'' + ", estimatedStart='" + this.estimatedStart + '\'' + ", actualStart='" + this.actualStart + '\'' + '}';
  }
}
