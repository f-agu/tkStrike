package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.SensorsGroup;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public interface MatchWorker extends CommonMatchWorker {
  boolean getBlueRequestVideoScoreboard();
  
  SimpleBooleanProperty blueRequestVideoScoreboardProperty();
  
  boolean getRedRequestVideoScoreboard();
  
  SimpleBooleanProperty redRequestVideoScoreboardProperty();
  
  int getBlueVideoQuota();
  
  SimpleIntegerProperty blueVideoQuotaProperty();
  
  int getRedVideoQuota();
  
  SimpleIntegerProperty redVideoQuotaProperty();
  
  SimpleObjectProperty<VideoRequestResult> blueVideoRequestResult();
  
  SimpleObjectProperty<VideoRequestResult> redVideoRequestResult();
  
  int getMinBodyLevel();
  
  ReadOnlyIntegerProperty minBodyLevelProperty();
  
  void setMinBodyLevel(int paramInt);
  
  int getMinHeadLevel();
  
  ReadOnlyIntegerProperty minHeadLevelProperty();
  
  void setMinHeadLevel(int paramInt);
  
  ReadOnlyObjectProperty<SensorsGroup> sensorsGroupSelectedProperty();
  
  SensorsGroup getSensorsGroupSelected();
  
  void doChangeSensorsGroupSelection(SensorsGroup paramSensorsGroup);
  
  public enum VideoRequestResult {
    ACCEPTED, REJECTED, CLOSED;
  }
}
