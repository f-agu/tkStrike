package com.xtremis.daedo.wtdata.model;

import com.xtremis.daedo.wtdata.model.constants.ResultStatus;
import com.xtremis.daedo.wtdata.model.constants.ResultType;
import java.io.Serializable;

public class MatchInternalResult implements Serializable {
  private ResultStatus status;
  
  private String decision;
  
  private ResultType homeType;
  
  private ResultType awayType;
  
  public MatchInternalResult() {}
  
  public MatchInternalResult(ResultStatus status, String decision, ResultType homeType, ResultType awayType) {
    this.status = status;
    this.decision = decision;
    this.homeType = homeType;
    this.awayType = awayType;
  }
  
  public ResultStatus getStatus() {
    return this.status;
  }
  
  public void setStatus(ResultStatus status) {
    this.status = status;
  }
  
  public String getDecision() {
    return this.decision;
  }
  
  public void setDecision(String decision) {
    this.decision = decision;
  }
  
  public ResultType getHomeType() {
    return this.homeType;
  }
  
  public void setHomeType(ResultType homeType) {
    this.homeType = homeType;
  }
  
  public ResultType getAwayType() {
    return this.awayType;
  }
  
  public void setAwayType(ResultType awayType) {
    this.awayType = awayType;
  }
  
  public String toString() {
    return "MatchResult{status=" + this.status + ", decision='" + this.decision + '\'' + ", homeType=" + this.homeType + ", awayType=" + this.awayType + '}';
  }
}
