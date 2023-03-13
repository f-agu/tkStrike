package com.xtremis.daedo.tkstrike.orm.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class RoundsConfig implements RoundsConfigEntity {
  @Column(name = "ROUNDS")
  private Integer rounds;
  
  @Column(name = "ROUND_TIME")
  private String roundTimeStr;
  
  @Column(name = "KYESHI_TIME")
  private String kyeShiTimeStr;
  
  @Column(name = "REST_TIME")
  private String restTimeStr;
  
  @Column(name = "GOLDENPOINT_ENABLED")
  private Boolean goldenPointEnabled;
  
  @Column(name = "GOLDENPOINT_TIME")
  private String goldenPointTimeStr;
  
  public Integer getRounds() {
    return this.rounds;
  }
  
  public void setRounds(Integer rounds) {
    this.rounds = rounds;
  }
  
  public String getRoundTimeStr() {
    return this.roundTimeStr;
  }
  
  public void setRoundTimeStr(String roundTimeStr) {
    this.roundTimeStr = roundTimeStr;
  }
  
  public String getKyeShiTimeStr() {
    return this.kyeShiTimeStr;
  }
  
  public void setKyeShiTimeStr(String kyeShiTimeStr) {
    this.kyeShiTimeStr = kyeShiTimeStr;
  }
  
  public String getRestTimeStr() {
    return this.restTimeStr;
  }
  
  public void setRestTimeStr(String restTimeStr) {
    this.restTimeStr = restTimeStr;
  }
  
  public Boolean getGoldenPointEnabled() {
    return this.goldenPointEnabled;
  }
  
  public void setGoldenPointEnabled(Boolean extraTimeEnabled) {
    this.goldenPointEnabled = extraTimeEnabled;
  }
  
  public String getGoldenPointTimeStr() {
    return this.goldenPointTimeStr;
  }
  
  public void setGoldenPointTimeStr(String extraTimeStr) {
    this.goldenPointTimeStr = extraTimeStr;
  }
}
