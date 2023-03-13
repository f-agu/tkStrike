package com.xtremis.daedo.tkstrike.orm.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class GoldenPointTieBreakerInfo {
  @Column(name = "GDP_TIE_BREAKER")
  private Boolean haveTieBreaker;
  
  @Column(name = "GDP_BLUE_PUNCHES")
  private Integer bluePunches;
  
  @Column(name = "GDP_BLUE_ROUND_WINS")
  private Integer blueRoundWins;
  
  @Column(name = "GDP_BLUE_HITS")
  private Integer blueHits;
  
  @Column(name = "GDP_BLUE_PENALTIES")
  private Integer bluePenalties;
  
  @Column(name = "GDP_RED_PUNCHES")
  private Integer redPunches;
  
  @Column(name = "GDP_RED_ROUND_WINS")
  private Integer redRoundWins;
  
  @Column(name = "GDP_RED_HITS")
  private Integer redHits;
  
  @Column(name = "GDP_RED_PENALTIES")
  private Integer redPenalties;
  
  @Column(name = "BLUE_PARA_TECH_POINTS")
  private Integer bluePARATechPoints;
  
  @Column(name = "RED_PARA_TECH_POINTS")
  private Integer redPARATechPoints;
  
  public Boolean getHaveTieBreaker() {
    return this.haveTieBreaker;
  }
  
  public void setHaveTieBreaker(Boolean haveTieBreaker) {
    this.haveTieBreaker = haveTieBreaker;
  }
  
  public Integer getBlueRoundWins() {
    return this.blueRoundWins;
  }
  
  public void setBlueRoundWins(Integer blueRoundWins) {
    this.blueRoundWins = blueRoundWins;
  }
  
  public Integer getBlueHits() {
    return this.blueHits;
  }
  
  public void setBlueHits(Integer blueHits) {
    this.blueHits = blueHits;
  }
  
  public Integer getBluePenalties() {
    return this.bluePenalties;
  }
  
  public void setBluePenalties(Integer bluePenalties) {
    this.bluePenalties = bluePenalties;
  }
  
  public Integer getRedRoundWins() {
    return this.redRoundWins;
  }
  
  public void setRedRoundWins(Integer redRoundWins) {
    this.redRoundWins = redRoundWins;
  }
  
  public Integer getRedHits() {
    return this.redHits;
  }
  
  public void setRedHits(Integer redHits) {
    this.redHits = redHits;
  }
  
  public Integer getRedPenalties() {
    return this.redPenalties;
  }
  
  public void setRedPenalties(Integer redPenalties) {
    this.redPenalties = redPenalties;
  }
  
  public Integer getBluePunches() {
    return this.bluePunches;
  }
  
  public void setBluePunches(Integer bluePunches) {
    this.bluePunches = bluePunches;
  }
  
  public Integer getRedPunches() {
    return this.redPunches;
  }
  
  public void setRedPunches(Integer redPunches) {
    this.redPunches = redPunches;
  }
  
  public Integer getBluePARATechPoints() {
    return this.bluePARATechPoints;
  }
  
  public void setBluePARATechPoints(Integer bluePARATechPoints) {
    this.bluePARATechPoints = bluePARATechPoints;
  }
  
  public Integer getRedPARATechPoints() {
    return this.redPARATechPoints;
  }
  
  public void setRedPARATechPoints(Integer redPARATechPoints) {
    this.redPARATechPoints = redPARATechPoints;
  }
}
