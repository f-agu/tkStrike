package com.xtremis.daedo.wtdata.model;

import com.xtremis.daedo.wtdata.model.constants.ScheduleStatus;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import java.io.Serializable;
import java.util.Set;

@JsonApiResource(type = "sessions")
public class Session implements Serializable {
  @JsonApiId
  private String id;
  
  private String name;
  
  private String startTime;
  
  private String endTime;
  
  private ScheduleStatus scheduleStatus;
  
  @JsonApiRelation
  private Set<Match> matches;
  
  public String getId() {
    return this.id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getStartTime() {
    return this.startTime;
  }
  
  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }
  
  public String getEndTime() {
    return this.endTime;
  }
  
  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }
  
  public ScheduleStatus getScheduleStatus() {
    return this.scheduleStatus;
  }
  
  public void setScheduleStatus(ScheduleStatus scheduleStatus) {
    this.scheduleStatus = scheduleStatus;
  }
  
  public Set<Match> getMatches() {
    return this.matches;
  }
  
  public void setMatches(Set<Match> matches) {
    this.matches = matches;
  }
  
  public String toString() {
    return "Session{id='" + this.id + '\'' + ", name='" + this.name + '\'' + ", startTime='" + this.startTime + '\'' + ", endTime='" + this.endTime + '\'' + ", scheduleStatus=" + this.scheduleStatus + '}';
  }
}
