package com.xtremis.daedo.wtdata.model;

import java.io.Serializable;

public class MatchScore implements Serializable {
  private Integer home;
  
  private Integer away;
  
  public MatchScore() {}
  
  public MatchScore(Integer home, Integer away) {
    this.home = home;
    this.away = away;
  }
  
  public Integer getHome() {
    return this.home;
  }
  
  public void setHome(Integer home) {
    this.home = home;
  }
  
  public Integer getAway() {
    return this.away;
  }
  
  public void setAway(Integer away) {
    this.away = away;
  }
  
  public String toString() {
    return "MatchScore{home=" + this.home + ", away=" + this.away + '}';
  }
}
