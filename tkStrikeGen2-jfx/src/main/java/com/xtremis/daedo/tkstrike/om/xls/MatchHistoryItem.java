package com.xtremis.daedo.tkstrike.om.xls;

import java.io.Serializable;
import java.util.Date;

public class MatchHistoryItem implements Serializable {
  private static final long serialVersionUID = 5757209003833167869L;
  
  private Integer order;
  
  private Integer roundNumber;
  
  private String roundStr;
  
  private String action;
  
  private Date timing;
  
  private Date time;
  
  private String source;
  
  private String score;
  
  private Integer bluePoint;
  
  private Integer blueHit;
  
  private String blueJudge1;
  
  private String blueJudge2;
  
  private String blueJudge3;
  
  private Integer redPoint;
  
  private Integer redHit;
  
  private String redJudge1;
  
  private String redJudge2;
  
  private String redJudge3;
  
  public Integer getOrder() {
    return this.order;
  }
  
  public void setOrder(Integer order) {
    this.order = order;
  }
  
  public Integer getRoundNumber() {
    return this.roundNumber;
  }
  
  public void setRoundNumber(Integer roundNumber) {
    this.roundNumber = roundNumber;
  }
  
  public String getRoundStr() {
    return this.roundStr;
  }
  
  public void setRoundStr(String roundStr) {
    this.roundStr = roundStr;
  }
  
  public String getAction() {
    return this.action;
  }
  
  public void setAction(String action) {
    this.action = action;
  }
  
  public Date getTiming() {
    return this.timing;
  }
  
  public void setTiming(Date timing) {
    this.timing = timing;
  }
  
  public Date getTime() {
    return this.time;
  }
  
  public void setTime(Date time) {
    this.time = time;
  }
  
  public String getSource() {
    return this.source;
  }
  
  public void setSource(String source) {
    this.source = source;
  }
  
  public String getScore() {
    return this.score;
  }
  
  public void setScore(String score) {
    this.score = score;
  }
  
  public Integer getBluePoint() {
    return this.bluePoint;
  }
  
  public void setBluePoint(Integer bluePoint) {
    this.bluePoint = bluePoint;
  }
  
  public Integer getBlueHit() {
    return this.blueHit;
  }
  
  public void setBlueHit(Integer blueHit) {
    this.blueHit = blueHit;
  }
  
  public String getBlueJudge1() {
    return this.blueJudge1;
  }
  
  public void setBlueJudge1(String blueJudge1) {
    this.blueJudge1 = blueJudge1;
  }
  
  public String getBlueJudge2() {
    return this.blueJudge2;
  }
  
  public void setBlueJudge2(String blueJudge2) {
    this.blueJudge2 = blueJudge2;
  }
  
  public String getBlueJudge3() {
    return this.blueJudge3;
  }
  
  public void setBlueJudge3(String blueJudge3) {
    this.blueJudge3 = blueJudge3;
  }
  
  public Integer getRedPoint() {
    return this.redPoint;
  }
  
  public void setRedPoint(Integer redPoint) {
    this.redPoint = redPoint;
  }
  
  public Integer getRedHit() {
    return this.redHit;
  }
  
  public void setRedHit(Integer redHit) {
    this.redHit = redHit;
  }
  
  public String getRedJudge1() {
    return this.redJudge1;
  }
  
  public void setRedJudge1(String redJudge1) {
    this.redJudge1 = redJudge1;
  }
  
  public String getRedJudge2() {
    return this.redJudge2;
  }
  
  public void setRedJudge2(String redJudge2) {
    this.redJudge2 = redJudge2;
  }
  
  public String getRedJudge3() {
    return this.redJudge3;
  }
  
  public void setRedJudge3(String redJudge3) {
    this.redJudge3 = redJudge3;
  }
}
