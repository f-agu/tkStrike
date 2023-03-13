package com.xtremis.daedo.tkstrike.om.xls;

import java.io.Serializable;
import java.util.Objects;

public class DataTableRoundInfoItem implements Serializable {
  private static final long serialVersionUID = -22611033082353436L;
  
  private Integer round;
  
  private String roundStr;
  
  private String blueName;
  
  private String blueNoc;
  
  private Integer bluePunchPoint = Integer.valueOf(0);
  
  private Integer bluePunchAction = Integer.valueOf(0);
  
  private Integer blueBodyKickPoint = Integer.valueOf(0);
  
  private Integer blueBodyKickAction = Integer.valueOf(0);
  
  private Integer blueHeadKickPoint = Integer.valueOf(0);
  
  private Integer blueHeadKickAction = Integer.valueOf(0);
  
  private Integer blueTurningBodyKickPoint = Integer.valueOf(0);
  
  private Integer blueTurningBodyKickAction = Integer.valueOf(0);
  
  private Integer blueTurningHeadKickPoint = Integer.valueOf(0);
  
  private Integer blueTurningHeadKickAction = Integer.valueOf(0);
  
  private Integer blueGamJeomPoint = Integer.valueOf(0);
  
  private Integer blueGamJeomAction = Integer.valueOf(0);
  
  private Integer blueVideoReplayPoint = Integer.valueOf(0);
  
  private Integer blueVideoReplayAction = Integer.valueOf(0);
  
  private Integer bluePointPlusDeskPoint = Integer.valueOf(0);
  
  private Integer bluePointPlusDeskAction = Integer.valueOf(0);
  
  private Integer bluePointMinusDeskPoint = Integer.valueOf(0);
  
  private Integer bluePointMinusDeskAction = Integer.valueOf(0);
  
  private Integer blueTotal = Integer.valueOf(0);
  
  private String redName;
  
  private String redNoc;
  
  private Integer redPunchPoint = Integer.valueOf(0);
  
  private Integer redPunchAction = Integer.valueOf(0);
  
  private Integer redBodyKickPoint = Integer.valueOf(0);
  
  private Integer redBodyKickAction = Integer.valueOf(0);
  
  private Integer redHeadKickPoint = Integer.valueOf(0);
  
  private Integer redHeadKickAction = Integer.valueOf(0);
  
  private Integer redTurningBodyKickPoint = Integer.valueOf(0);
  
  private Integer redTurningBodyKickAction = Integer.valueOf(0);
  
  private Integer redTurningHeadKickPoint = Integer.valueOf(0);
  
  private Integer redTurningHeadKickAction = Integer.valueOf(0);
  
  private Integer redGamJeomPoint = Integer.valueOf(0);
  
  private Integer redGamJeomAction = Integer.valueOf(0);
  
  private Integer redVideoReplayPoint = Integer.valueOf(0);
  
  private Integer redVideoReplayAction = Integer.valueOf(0);
  
  private Integer redPointPlusDeskPoint = Integer.valueOf(0);
  
  private Integer redPointPlusDeskAction = Integer.valueOf(0);
  
  private Integer redPointMinusDeskPoint = Integer.valueOf(0);
  
  private Integer redPointMinusDeskAction = Integer.valueOf(0);
  
  private Integer redTotal = Integer.valueOf(0);
  
  public DataTableRoundInfoItem(Integer round) {
    this.round = round;
  }
  
  public Integer getRound() {
    return this.round;
  }
  
  public void setRound(Integer round) {
    this.round = round;
  }
  
  public String getRoundStr() {
    return this.roundStr;
  }
  
  public void setRoundStr(String roundStr) {
    this.roundStr = roundStr;
  }
  
  public String getBlueName() {
    return this.blueName;
  }
  
  public void setBlueName(String blueName) {
    this.blueName = blueName;
  }
  
  public String getBlueNoc() {
    return this.blueNoc;
  }
  
  public void setBlueNoc(String blueNoc) {
    this.blueNoc = blueNoc;
  }
  
  public Integer getBluePunchPoint() {
    return this.bluePunchPoint;
  }
  
  public void setBluePunchPoint(Integer bluePunchPoint) {
    this.bluePunchPoint = bluePunchPoint;
  }
  
  public Integer getBluePunchAction() {
    return this.bluePunchAction;
  }
  
  public void setBluePunchAction(Integer bluePunchAction) {
    this.bluePunchAction = bluePunchAction;
  }
  
  public Integer getBlueBodyKickPoint() {
    return this.blueBodyKickPoint;
  }
  
  public void setBlueBodyKickPoint(Integer blueBodyKickPoint) {
    this.blueBodyKickPoint = blueBodyKickPoint;
  }
  
  public Integer getBlueBodyKickAction() {
    return this.blueBodyKickAction;
  }
  
  public void setBlueBodyKickAction(Integer blueBodyKickAction) {
    this.blueBodyKickAction = blueBodyKickAction;
  }
  
  public Integer getBlueHeadKickPoint() {
    return this.blueHeadKickPoint;
  }
  
  public void setBlueHeadKickPoint(Integer blueHeadKickPoint) {
    this.blueHeadKickPoint = blueHeadKickPoint;
  }
  
  public Integer getBlueHeadKickAction() {
    return this.blueHeadKickAction;
  }
  
  public void setBlueHeadKickAction(Integer blueHeadKickAction) {
    this.blueHeadKickAction = blueHeadKickAction;
  }
  
  public Integer getBlueTurningBodyKickPoint() {
    return this.blueTurningBodyKickPoint;
  }
  
  public void setBlueTurningBodyKickPoint(Integer blueTurningBodyKickPoint) {
    this.blueTurningBodyKickPoint = blueTurningBodyKickPoint;
  }
  
  public Integer getBlueTurningBodyKickAction() {
    return this.blueTurningBodyKickAction;
  }
  
  public void setBlueTurningBodyKickAction(Integer blueTurningBodyKickAction) {
    this.blueTurningBodyKickAction = blueTurningBodyKickAction;
  }
  
  public Integer getBlueTurningHeadKickPoint() {
    return this.blueTurningHeadKickPoint;
  }
  
  public void setBlueTurningHeadKickPoint(Integer blueTurningHeadKickPoint) {
    this.blueTurningHeadKickPoint = blueTurningHeadKickPoint;
  }
  
  public Integer getBlueTurningHeadKickAction() {
    return this.blueTurningHeadKickAction;
  }
  
  public void setBlueTurningHeadKickAction(Integer blueTurningHeadKickAction) {
    this.blueTurningHeadKickAction = blueTurningHeadKickAction;
  }
  
  public Integer getBlueGamJeomPoint() {
    return this.blueGamJeomPoint;
  }
  
  public void setBlueGamJeomPoint(Integer blueGamJeomPoint) {
    this.blueGamJeomPoint = blueGamJeomPoint;
  }
  
  public Integer getBlueGamJeomAction() {
    return this.blueGamJeomAction;
  }
  
  public void setBlueGamJeomAction(Integer blueGamJeomAction) {
    this.blueGamJeomAction = blueGamJeomAction;
  }
  
  public Integer getBlueVideoReplayPoint() {
    return this.blueVideoReplayPoint;
  }
  
  public void setBlueVideoReplayPoint(Integer blueVideoReplayPoint) {
    this.blueVideoReplayPoint = blueVideoReplayPoint;
  }
  
  public Integer getBlueVideoReplayAction() {
    return this.blueVideoReplayAction;
  }
  
  public void setBlueVideoReplayAction(Integer blueVideoReplayAction) {
    this.blueVideoReplayAction = blueVideoReplayAction;
  }
  
  public Integer getBluePointPlusDeskPoint() {
    return this.bluePointPlusDeskPoint;
  }
  
  public void setBluePointPlusDeskPoint(Integer bluePointPlusDeskPoint) {
    this.bluePointPlusDeskPoint = bluePointPlusDeskPoint;
  }
  
  public Integer getBluePointPlusDeskAction() {
    return this.bluePointPlusDeskAction;
  }
  
  public void setBluePointPlusDeskAction(Integer bluePointPlusDeskAction) {
    this.bluePointPlusDeskAction = bluePointPlusDeskAction;
  }
  
  public Integer getBluePointMinusDeskPoint() {
    return this.bluePointMinusDeskPoint;
  }
  
  public void setBluePointMinusDeskPoint(Integer bluePointMinusDeskPoint) {
    this.bluePointMinusDeskPoint = bluePointMinusDeskPoint;
  }
  
  public Integer getBluePointMinusDeskAction() {
    return this.bluePointMinusDeskAction;
  }
  
  public void setBluePointMinusDeskAction(Integer bluePointMinusDeskAction) {
    this.bluePointMinusDeskAction = bluePointMinusDeskAction;
  }
  
  public Integer getBlueTotal() {
    return this.blueTotal;
  }
  
  public void setBlueTotal(Integer blueTotal) {
    this.blueTotal = blueTotal;
  }
  
  public String getRedName() {
    return this.redName;
  }
  
  public void setRedName(String redName) {
    this.redName = redName;
  }
  
  public String getRedNoc() {
    return this.redNoc;
  }
  
  public void setRedNoc(String redNoc) {
    this.redNoc = redNoc;
  }
  
  public Integer getRedPunchPoint() {
    return this.redPunchPoint;
  }
  
  public void setRedPunchPoint(Integer redPunchPoint) {
    this.redPunchPoint = redPunchPoint;
  }
  
  public Integer getRedPunchAction() {
    return this.redPunchAction;
  }
  
  public void setRedPunchAction(Integer redPunchAction) {
    this.redPunchAction = redPunchAction;
  }
  
  public Integer getRedBodyKickPoint() {
    return this.redBodyKickPoint;
  }
  
  public void setRedBodyKickPoint(Integer redBodyKickPoint) {
    this.redBodyKickPoint = redBodyKickPoint;
  }
  
  public Integer getRedBodyKickAction() {
    return this.redBodyKickAction;
  }
  
  public void setRedBodyKickAction(Integer redBodyKickAction) {
    this.redBodyKickAction = redBodyKickAction;
  }
  
  public Integer getRedHeadKickPoint() {
    return this.redHeadKickPoint;
  }
  
  public void setRedHeadKickPoint(Integer redHeadKickPoint) {
    this.redHeadKickPoint = redHeadKickPoint;
  }
  
  public Integer getRedHeadKickAction() {
    return this.redHeadKickAction;
  }
  
  public void setRedHeadKickAction(Integer redHeadKickAction) {
    this.redHeadKickAction = redHeadKickAction;
  }
  
  public Integer getRedTurningBodyKickPoint() {
    return this.redTurningBodyKickPoint;
  }
  
  public void setRedTurningBodyKickPoint(Integer redTurningBodyKickPoint) {
    this.redTurningBodyKickPoint = redTurningBodyKickPoint;
  }
  
  public Integer getRedTurningBodyKickAction() {
    return this.redTurningBodyKickAction;
  }
  
  public void setRedTurningBodyKickAction(Integer redTurningBodyKickAction) {
    this.redTurningBodyKickAction = redTurningBodyKickAction;
  }
  
  public Integer getRedTurningHeadKickPoint() {
    return this.redTurningHeadKickPoint;
  }
  
  public void setRedTurningHeadKickPoint(Integer redTurningHeadKickPoint) {
    this.redTurningHeadKickPoint = redTurningHeadKickPoint;
  }
  
  public Integer getRedTurningHeadKickAction() {
    return this.redTurningHeadKickAction;
  }
  
  public void setRedTurningHeadKickAction(Integer redTurningHeadKickAction) {
    this.redTurningHeadKickAction = redTurningHeadKickAction;
  }
  
  public Integer getRedGamJeomPoint() {
    return this.redGamJeomPoint;
  }
  
  public void setRedGamJeomPoint(Integer redGamJeomPoint) {
    this.redGamJeomPoint = redGamJeomPoint;
  }
  
  public Integer getRedGamJeomAction() {
    return this.redGamJeomAction;
  }
  
  public void setRedGamJeomAction(Integer redGamJeomAction) {
    this.redGamJeomAction = redGamJeomAction;
  }
  
  public Integer getRedVideoReplayPoint() {
    return this.redVideoReplayPoint;
  }
  
  public void setRedVideoReplayPoint(Integer redVideoReplayPoint) {
    this.redVideoReplayPoint = redVideoReplayPoint;
  }
  
  public Integer getRedVideoReplayAction() {
    return this.redVideoReplayAction;
  }
  
  public void setRedVideoReplayAction(Integer redVideoReplayAction) {
    this.redVideoReplayAction = redVideoReplayAction;
  }
  
  public Integer getRedPointPlusDeskPoint() {
    return this.redPointPlusDeskPoint;
  }
  
  public void setRedPointPlusDeskPoint(Integer redPointPlusDeskPoint) {
    this.redPointPlusDeskPoint = redPointPlusDeskPoint;
  }
  
  public Integer getRedPointPlusDeskAction() {
    return this.redPointPlusDeskAction;
  }
  
  public void setRedPointPlusDeskAction(Integer redPointPlusDeskAction) {
    this.redPointPlusDeskAction = redPointPlusDeskAction;
  }
  
  public Integer getRedPointMinusDeskPoint() {
    return this.redPointMinusDeskPoint;
  }
  
  public void setRedPointMinusDeskPoint(Integer redPointMinusDeskPoint) {
    this.redPointMinusDeskPoint = redPointMinusDeskPoint;
  }
  
  public Integer getRedPointMinusDeskAction() {
    return this.redPointMinusDeskAction;
  }
  
  public void setRedPointMinusDeskAction(Integer redPointMinusDeskAction) {
    this.redPointMinusDeskAction = redPointMinusDeskAction;
  }
  
  public Integer getRedTotal() {
    return this.redTotal;
  }
  
  public void setRedTotal(Integer redTotal) {
    this.redTotal = redTotal;
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (!(o instanceof DataTableRoundInfoItem))
      return false; 
    DataTableRoundInfoItem that = (DataTableRoundInfoItem)o;
    return Objects.equals(this.round, that.round);
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.round });
  }
}
