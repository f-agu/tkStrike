package com.xtremis.daedo.wtdata.model;

import com.xtremis.daedo.wtdata.model.constants.Rules;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import java.io.Serializable;

@JsonApiResource(type = "match-configurations")
public class MatchConfiguration implements Serializable {
  @JsonApiId
  private String id;
  
  private Rules rules;
  
  private Integer rounds;
  
  private Timing timing;
  
  private Thresholds thresholds;
  
  private MatchScore videoReplayQuota;
  
  private GoldenPoint goldenPoint;
  
  private Integer maxDifference;
  
  private Integer maxPenalties;
  
  public String getId() {
    return this.id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public Rules getRules() {
    return this.rules;
  }
  
  public void setRules(Rules rules) {
    this.rules = rules;
  }
  
  public Integer getRounds() {
    return this.rounds;
  }
  
  public void setRounds(Integer rounds) {
    this.rounds = rounds;
  }
  
  public Timing getTiming() {
    return this.timing;
  }
  
  public void setTiming(Timing timing) {
    this.timing = timing;
  }
  
  public Thresholds getThresholds() {
    return this.thresholds;
  }
  
  public void setThresholds(Thresholds thresholds) {
    this.thresholds = thresholds;
  }
  
  public MatchScore getVideoReplayQuota() {
    return this.videoReplayQuota;
  }
  
  public void setVideoReplayQuota(MatchScore videoReplayQuota) {
    this.videoReplayQuota = videoReplayQuota;
  }
  
  public GoldenPoint getGoldenPoint() {
    return this.goldenPoint;
  }
  
  public void setGoldenPoint(GoldenPoint goldenPoint) {
    this.goldenPoint = goldenPoint;
  }
  
  public Integer getMaxDifference() {
    return this.maxDifference;
  }
  
  public void setMaxDifference(Integer maxDifference) {
    this.maxDifference = maxDifference;
  }
  
  public Integer getMaxPenalties() {
    return this.maxPenalties;
  }
  
  public void setMaxPenalties(Integer maxPenalties) {
    this.maxPenalties = maxPenalties;
  }
  
  public String toString() {
    return "MatchConfiguration{id='" + this.id + '\'' + ", rounds=" + ((this.rounds != null) ? this.rounds
      
      .toString() : "null") + ", timing=" + ((this.timing != null) ? this.timing
      .toString() : "null") + ", thresholds=" + ((this.thresholds != null) ? this.thresholds
      .toString() : "null") + ", videoReplayQuota=" + ((this.videoReplayQuota != null) ? this.videoReplayQuota
      .toString() : "null") + ", goldenPoint=" + ((this.goldenPoint != null) ? this.goldenPoint
      .toString() : "null") + ", maxDifference=" + this.maxDifference + ", maxPenalties=" + this.maxPenalties + '}';
  }
}
