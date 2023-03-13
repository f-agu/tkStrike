package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.communication.DataEvent;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public interface AthletesHardwareTestWorker {
  void startTest() throws TkStrikeServiceException;
  
  void stopTest();
  
  boolean getExecuting();
  
  ReadOnlyBooleanProperty executingProperty();
  
  int getMinBodyHit();
  
  SimpleIntegerProperty minBodyHitProperty();
  
  int getMinHeadHit();
  
  SimpleIntegerProperty minHeadHitProperty();
  
  int getTestRemain();
  
  SimpleIntegerProperty testRemainProperty();
  
  boolean getTestPassed();
  
  SimpleBooleanProperty testPassedProperty();
  
  SimpleBooleanProperty testResetProperty();
  
  void addListener(AthletesHardwareTestWorkerListener paramAthletesHardwareTestWorkerListener);
  
  void removeListener(AthletesHardwareTestWorkerListener paramAthletesHardwareTestWorkerListener);
  
  void doBlueBodyPassed();
  
  void doBlueHeadPassed();
  
  void doRedBodyPassed();
  
  void doRedHeadPassed();
  
  public static interface AthletesHardwareTestWorkerListener {
    void hasHardwareTestNewHitEvent(DataEvent param1DataEvent);
  }
}
