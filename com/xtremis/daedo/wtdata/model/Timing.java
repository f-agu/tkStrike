package com.xtremis.daedo.wtdata.model;

import java.io.Serializable;

public class Timing implements Serializable {
  private String round;
  
  private String rest;
  
  private String injury;
  
  public String getRound() {
    return this.round;
  }
  
  public void setRound(String round) {
    this.round = round;
  }
  
  public String getRest() {
    return this.rest;
  }
  
  public void setRest(String rest) {
    this.rest = rest;
  }
  
  public String getInjury() {
    return this.injury;
  }
  
  public void setInjury(String injury) {
    this.injury = injury;
  }
  
  public String toString() {
    return "Timing{round='" + this.round + '\'' + ", rest='" + this.rest + '\'' + ", injury='" + this.injury + '\'' + '}';
  }
}
