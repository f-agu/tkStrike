package com.xtremis.daedo.tkstrike.om.combat;

import java.io.Serializable;

public class BestOf3RoundSuperiority implements Serializable {
  private MatchWinner roundWinner;
  
  private Integer blueTurningSpinning;
  
  private String blueTech;
  
  private Integer blueHits;
  
  private Integer redTurningSpinning;
  
  private String redTech;
  
  private Integer redHits;
  
  public BestOf3RoundSuperiority() {
    this.roundWinner = MatchWinner.TIE;
  }
  
  public BestOf3RoundSuperiority(MatchWinner roundWinner, Integer blueTurningSpinning, String blueTech, Integer blueHits, Integer redTurningSpinning, String redTech, Integer redHits) {
    this.roundWinner = roundWinner;
    this.blueTurningSpinning = blueTurningSpinning;
    this.blueTech = blueTech;
    this.blueHits = blueHits;
    this.redTurningSpinning = redTurningSpinning;
    this.redTech = redTech;
    this.redHits = redHits;
  }
  
  public MatchWinner getRoundWinner() {
    return this.roundWinner;
  }
  
  public void setRoundWinner(MatchWinner roundWinner) {
    this.roundWinner = roundWinner;
  }
  
  public Integer getBlueTurningSpinning() {
    return this.blueTurningSpinning;
  }
  
  public void setBlueTurningSpinning(Integer blueTurningSpinning) {
    this.blueTurningSpinning = blueTurningSpinning;
  }
  
  public String getBlueTech() {
    return this.blueTech;
  }
  
  public void setBlueTech(String blueTech) {
    this.blueTech = blueTech;
  }
  
  public Integer getBlueHits() {
    return this.blueHits;
  }
  
  public void setBlueHits(Integer blueHits) {
    this.blueHits = blueHits;
  }
  
  public Integer getRedTurningSpinning() {
    return this.redTurningSpinning;
  }
  
  public void setRedTurningSpinning(Integer redTurningSpinning) {
    this.redTurningSpinning = redTurningSpinning;
  }
  
  public String getRedTech() {
    return this.redTech;
  }
  
  public void setRedTech(String redTech) {
    this.redTech = redTech;
  }
  
  public Integer getRedHits() {
    return this.redHits;
  }
  
  public void setRedHits(Integer redHits) {
    this.redHits = redHits;
  }
}
