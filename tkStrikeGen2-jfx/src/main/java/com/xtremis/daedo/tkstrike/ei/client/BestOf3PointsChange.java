package com.xtremis.daedo.tkstrike.ei.client;

import java.io.Serializable;

public class BestOf3PointsChange implements Serializable {
  private final Integer blueR1;
  
  private final Integer blueR2;
  
  private final Integer blueR3;
  
  private final Integer redR1;
  
  private final Integer redR2;
  
  private final Integer redR3;
  
  public BestOf3PointsChange() {
    this(Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
  }
  
  public BestOf3PointsChange(Integer blueR1, Integer blueR2, Integer blueR3, Integer redR1, Integer redR2, Integer redR3) {
    this.blueR1 = blueR1;
    this.blueR2 = blueR2;
    this.blueR3 = blueR3;
    this.redR1 = redR1;
    this.redR2 = redR2;
    this.redR3 = redR3;
  }
  
  public Integer getBlueR1() {
    return this.blueR1;
  }
  
  public Integer getBlueR2() {
    return this.blueR2;
  }
  
  public Integer getBlueR3() {
    return this.blueR3;
  }
  
  public Integer getRedR1() {
    return this.redR1;
  }
  
  public Integer getRedR2() {
    return this.redR2;
  }
  
  public Integer getRedR3() {
    return this.redR3;
  }
  
  public String toString() {
    return "BestOf3PointsChange{blueR1=" + this.blueR1 + ", blueR2=" + this.blueR2 + ", blueR3=" + this.blueR3 + ", redR1=" + this.redR1 + ", redR2=" + this.redR2 + ", redR3=" + this.redR3 + '}';
  }
}
