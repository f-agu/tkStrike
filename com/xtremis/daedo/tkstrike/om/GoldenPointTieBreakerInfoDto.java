package com.xtremis.daedo.tkstrike.om;

import java.io.Serializable;

public class GoldenPointTieBreakerInfoDto implements Serializable {
  private static final long serialVersionUID = 1646743859081674791L;
  
  private Boolean haveTieBreaker;
  
  private Integer bluePunches;
  
  private Integer blueRoundWins;
  
  private Integer blueHits;
  
  private Integer bluePenalties;
  
  private Integer redPunches;
  
  private Integer redRoundWins;
  
  private Integer redHits;
  
  private Integer redPenalties;
  
  private Integer bluePARATechPoints;
  
  private Integer redPARATechPoints;
  
  public GoldenPointTieBreakerInfoDto() {}
  
  public GoldenPointTieBreakerInfoDto(Boolean haveTieBreaker, Integer bluePunches, Integer blueRoundWins, Integer blueHits, Integer bluePenalties, Integer redPunches, Integer redRoundWins, Integer redHits, Integer redPenalties, Integer bluePARATechPoints, Integer redPARATechPoints) {
    this.haveTieBreaker = haveTieBreaker;
    this.bluePunches = bluePunches;
    this.blueRoundWins = blueRoundWins;
    this.blueHits = blueHits;
    this.bluePenalties = bluePenalties;
    this.redPunches = redPunches;
    this.redRoundWins = redRoundWins;
    this.redHits = redHits;
    this.redPenalties = redPenalties;
    this.bluePARATechPoints = bluePARATechPoints;
    this.redPARATechPoints = redPARATechPoints;
  }
  
  public Boolean getHaveTieBreaker() {
    return this.haveTieBreaker;
  }
  
  public void setHaveTieBreaker(Boolean haveTieBreaker) {
    this.haveTieBreaker = haveTieBreaker;
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
