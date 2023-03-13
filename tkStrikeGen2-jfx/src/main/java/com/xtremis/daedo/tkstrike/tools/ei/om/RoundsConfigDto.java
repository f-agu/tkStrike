package com.xtremis.daedo.tkstrike.tools.ei.om;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xtremis.daedo.tkstrike.tools.om.BaseJSONDto;

public class RoundsConfigDto extends BaseJSONDto {
  private static final long serialVersionUID = 6259931884781054050L;
  
  private Integer rounds;
  
  private Integer roundTimeMinutes;
  
  private Integer roundTimeSeconds;
  
  private Integer kyeShiTimeMinutes;
  
  private Integer kyeShiTimeSeconds;
  
  private Integer restTimeMinutes;
  
  private Integer restTimeSeconds;
  
  private Boolean goldenPointEnabled;
  
  private Integer goldenPointTimeMinutes;
  
  private Integer goldenPointTimeSeconds;
  
  public Integer getRounds() {
    return this.rounds;
  }
  
  public void setRounds(Integer rounds) {
    this.rounds = rounds;
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
  
  public Integer getKyeShiTimeMinutes() {
    return this.kyeShiTimeMinutes;
  }
  
  public void setKyeShiTimeMinutes(Integer kyeShiTimeMinutes) {
    this.kyeShiTimeMinutes = kyeShiTimeMinutes;
  }
  
  public Integer getKyeShiTimeSeconds() {
    return this.kyeShiTimeSeconds;
  }
  
  public void setKyeShiTimeSeconds(Integer kyeShiTimeSeconds) {
    this.kyeShiTimeSeconds = kyeShiTimeSeconds;
  }
  
  public Integer getRestTimeMinutes() {
    return this.restTimeMinutes;
  }
  
  public void setRestTimeMinutes(Integer restTimeMinutes) {
    this.restTimeMinutes = restTimeMinutes;
  }
  
  public Integer getRestTimeSeconds() {
    return this.restTimeSeconds;
  }
  
  public void setRestTimeSeconds(Integer restTimeSeconds) {
    this.restTimeSeconds = restTimeSeconds;
  }
  
  public Boolean getGoldenPointEnabled() {
    return this.goldenPointEnabled;
  }
  
  public void setGoldenPointEnabled(Boolean goldenPointEnabled) {
    this.goldenPointEnabled = goldenPointEnabled;
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
  
  @JsonIgnore
  public String getRoundTimeStr() {
    return (this.roundTimeMinutes != null && this.roundTimeSeconds != null) ? (df.format(this.roundTimeMinutes) + ":" + df.format(this.roundTimeSeconds)) : null;
  }
  
  @JsonIgnore
  public String getKyeShiTimeStr() {
    return (this.kyeShiTimeMinutes != null && this.kyeShiTimeSeconds != null) ? (df.format(this.kyeShiTimeMinutes) + ":" + df.format(this.kyeShiTimeSeconds)) : null;
  }
  
  @JsonIgnore
  public String getRestTimeStr() {
    return (this.restTimeMinutes != null && this.restTimeSeconds != null) ? (df.format(this.restTimeMinutes) + ":" + df.format(this.restTimeSeconds)) : null;
  }
  
  @JsonIgnore
  public String getGoldenPointTimeStr() {
    return (this.goldenPointTimeMinutes != null && this.goldenPointTimeSeconds != null) ? (df.format(this.goldenPointTimeMinutes) + ":" + df.format(this.goldenPointTimeSeconds)) : null;
  }
}
