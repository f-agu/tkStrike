package com.xtremis.daedo.tkstrike.om.combat;

import java.io.Serializable;
import java.util.Calendar;
import org.apache.log4j.Logger;

public class HitEventValidator implements Serializable, Comparable {
  private static final long serialVersionUID = -2471615219008546265L;
  
  private static final Logger logger = Logger.getLogger(HitEventValidator.class);
  
  private boolean blue;
  
  private long hitTimestamp = -1L;
  
  private HitEventType hitEventType;
  
  private Integer hitValue;
  
  private int judgesEnabled;
  
  private boolean givenPoint = false;
  
  private HitJudgeStatus judge1HitStatus = HitJudgeStatus.NOT_VALIDATED;
  
  private HitJudgeStatus judge2HitStatus = HitJudgeStatus.NOT_ENABLED;
  
  private HitJudgeStatus judge3HitStatus = HitJudgeStatus.NOT_ENABLED;
  
  private final boolean autoRemove;
  
  private final boolean backupSystemEnabled;
  
  private ParaTkdTechEvent paraTkdTechEvent = ParaTkdTechEvent.NONE;
  
  public int compareTo(Object o) {
    int res = 0;
    HitEventValidator o2 = (HitEventValidator)o;
    Calendar cal1 = Calendar.getInstance();
    cal1.setTimeInMillis(getHitTimestamp());
    Calendar cal2 = Calendar.getInstance();
    cal2.setTimeInMillis(o2.getHitTimestamp());
    res = Integer.compare(cal2.get(12), cal1.get(12));
    if (res == 0) {
      res = Integer.compare(cal2.get(13), cal1.get(13));
      if (res == 0)
        return Integer.compare(cal2.get(14), cal1.get(14)); 
    } 
    return res;
  }
  
  public HitEventValidator(boolean backupSystemEnabled) {
    this.backupSystemEnabled = backupSystemEnabled;
    this.autoRemove = false;
  }
  
  public HitEventValidator(boolean blue, long hitTimestamp, HitEventType hitEventType, boolean backupSystemEnabled) {
    this.backupSystemEnabled = backupSystemEnabled;
    this.autoRemove = false;
    this.blue = blue;
    this.hitTimestamp = hitTimestamp;
    this.hitEventType = hitEventType;
  }
  
  public HitEventValidator(boolean blue, long hitTimestamp, HitEventType hitEventType, Integer hitValue, int judgesEnabled, HitJudgeStatus judge1HitStatus, HitJudgeStatus judge2HitStatus, HitJudgeStatus judge3HitStatus, boolean backupSystemEnabled) {
    this.backupSystemEnabled = backupSystemEnabled;
    this.autoRemove = false;
    this.blue = blue;
    this.hitTimestamp = hitTimestamp;
    this.hitEventType = hitEventType;
    this.hitValue = hitValue;
    this.judgesEnabled = judgesEnabled;
    this.judge1HitStatus = judge1HitStatus;
    this.judge2HitStatus = judge2HitStatus;
    this.judge3HitStatus = judge3HitStatus;
  }
  
  public HitEventValidator(boolean blue, long hitTimestamp, HitEventType hitEventType, Integer hitValue, int judgesEnabled, HitJudgeStatus judge1HitStatus, HitJudgeStatus judge2HitStatus, HitJudgeStatus judge3HitStatus, boolean autoRemove, boolean backupSystemEnabled) {
    this.autoRemove = autoRemove;
    this.blue = blue;
    this.hitTimestamp = hitTimestamp;
    this.hitEventType = hitEventType;
    this.hitValue = hitValue;
    this.judgesEnabled = judgesEnabled;
    this.judge1HitStatus = judge1HitStatus;
    this.judge2HitStatus = judge2HitStatus;
    this.judge3HitStatus = judge3HitStatus;
    this.backupSystemEnabled = backupSystemEnabled;
  }
  
  public HitEventValidator(boolean blue, long hitTimestamp, HitEventType hitEventType, Integer hitValue, int judgesEnabled, HitJudgeStatus judge1HitStatus, HitJudgeStatus judge2HitStatus, HitJudgeStatus judge3HitStatus, boolean backupSystemEnabled, ParaTkdTechEvent paraTkdTechEvent) {
    this.backupSystemEnabled = backupSystemEnabled;
    this.autoRemove = false;
    this.blue = blue;
    this.hitTimestamp = hitTimestamp;
    this.hitEventType = hitEventType;
    this.hitValue = hitValue;
    this.judgesEnabled = judgesEnabled;
    this.judge1HitStatus = judge1HitStatus;
    this.judge2HitStatus = judge2HitStatus;
    this.judge3HitStatus = judge3HitStatus;
    this.paraTkdTechEvent = paraTkdTechEvent;
  }
  
  public HitEventValidator(boolean blue, long hitTimestamp, HitEventType hitEventType, Integer hitValue, int judgesEnabled, HitJudgeStatus judge1HitStatus, HitJudgeStatus judge2HitStatus, HitJudgeStatus judge3HitStatus, ParaTkdTechEvent paraTkdTechEvent, boolean autoRemove, boolean backupSystemEnabled) {
    this.autoRemove = autoRemove;
    this.blue = blue;
    this.hitTimestamp = hitTimestamp;
    this.hitEventType = hitEventType;
    this.hitValue = hitValue;
    this.judgesEnabled = judgesEnabled;
    this.judge1HitStatus = judge1HitStatus;
    this.judge2HitStatus = judge2HitStatus;
    this.judge3HitStatus = judge3HitStatus;
    this.paraTkdTechEvent = paraTkdTechEvent;
    this.backupSystemEnabled = backupSystemEnabled;
  }
  
  public boolean isBackupSystemEnabled() {
    return this.backupSystemEnabled;
  }
  
  public boolean allJudgesValidated() {
    logger.debug("?allValidated = " + this.judgesEnabled + " 1:" + this.judge1HitStatus + " 2:" + this.judge2HitStatus + " 3:" + this.judge3HitStatus);
    switch (this.judgesEnabled) {
      case 1:
        return this.judge1HitStatus.equals(HitJudgeStatus.VALIDATED);
      case 2:
        return (this.judge1HitStatus.equals(HitJudgeStatus.VALIDATED) && this.judge2HitStatus.equals(HitJudgeStatus.VALIDATED));
      case 3:
        return ((this.judge1HitStatus.equals(HitJudgeStatus.VALIDATED) && this.judge2HitStatus.equals(HitJudgeStatus.VALIDATED)) || (this.judge1HitStatus
          .equals(HitJudgeStatus.VALIDATED) && this.judge3HitStatus.equals(HitJudgeStatus.VALIDATED)) || (this.judge2HitStatus
          .equals(HitJudgeStatus.VALIDATED) && this.judge3HitStatus.equals(HitJudgeStatus.VALIDATED)));
    } 
    return false;
  }
  
  public boolean isGivenPoint() {
    return this.givenPoint;
  }
  
  public void setGivenPoint(boolean givenPoint) {
    this.givenPoint = givenPoint;
  }
  
  public boolean isBlue() {
    return this.blue;
  }
  
  public void setBlue(boolean blue) {
    this.blue = blue;
  }
  
  public long getHitTimestamp() {
    return this.hitTimestamp;
  }
  
  public void setHitTimestamp(long hitTimestamp) {
    this.hitTimestamp = hitTimestamp;
  }
  
  public HitEventType getHitEventType() {
    return this.hitEventType;
  }
  
  public void setHitEventType(HitEventType hitEventType) {
    this.hitEventType = hitEventType;
  }
  
  public Integer getHitValue() {
    return this.hitValue;
  }
  
  public void setHitValue(Integer hitValue) {
    this.hitValue = hitValue;
  }
  
  public int getJudgesEnabled() {
    return this.judgesEnabled;
  }
  
  public void setJudgesEnabled(int judgesEnabled) {
    this.judgesEnabled = judgesEnabled;
  }
  
  public HitJudgeStatus getJudge1HitStatus() {
    return this.judge1HitStatus;
  }
  
  public void setJudge1HitStatus(HitJudgeStatus judge1HitStatus) {
    this.judge1HitStatus = judge1HitStatus;
  }
  
  public HitJudgeStatus getJudge2HitStatus() {
    return this.judge2HitStatus;
  }
  
  public void setJudge2HitStatus(HitJudgeStatus judge2HitStatus) {
    this.judge2HitStatus = judge2HitStatus;
  }
  
  public HitJudgeStatus getJudge3HitStatus() {
    return this.judge3HitStatus;
  }
  
  public void setJudge3HitStatus(HitJudgeStatus judge3HitStatus) {
    this.judge3HitStatus = judge3HitStatus;
  }
  
  public ParaTkdTechEvent getParaTkdTechEvent() {
    return this.paraTkdTechEvent;
  }
  
  public void setParaTkdTechEvent(ParaTkdTechEvent paraTkdTechEvent) {
    this.paraTkdTechEvent = paraTkdTechEvent;
  }
  
  public boolean isAutoRemove() {
    return this.autoRemove;
  }
  
  public String toString() {
    return "HitEventValidator{blue=" + this.blue + ", hitTimestamp=" + this.hitTimestamp + ", hitEventType=" + this.hitEventType + ", hitValue=" + this.hitValue + ", judgesEnabled=" + this.judgesEnabled + ", givenPoint=" + this.givenPoint + ", judge1HitStatus=" + this.judge1HitStatus + ", judge2HitStatus=" + this.judge2HitStatus + ", judge3HitStatus=" + this.judge3HitStatus + ", autoRemove=" + this.autoRemove + ", backupSystemEnabled=" + this.backupSystemEnabled + ", paraTkdTechEvent=" + this.paraTkdTechEvent + '}';
  }
  
  public enum ParaTkdTechEvent {
    SPINNING_KICK_TECH, TURNING_KICK_TECH, NONE;
  }
}
