package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.communication.DataEvent;
import javafx.beans.property.SimpleBooleanProperty;

public interface JudgesHardwareTestWorker {
  void startTest() throws TkStrikeServiceException;
  
  void stopTest();
  
  boolean getTestPassed();
  
  SimpleBooleanProperty testPassedProperty();
  
  SimpleBooleanProperty testResetProperty();
  
  boolean getExecuting();
  
  SimpleBooleanProperty executingProperty();
  
  void addListener(JudgesHardwareTestWorkerListener paramJudgesHardwareTestWorkerListener);
  
  void removeListener(JudgesHardwareTestWorkerListener paramJudgesHardwareTestWorkerListener);
  
  public static interface JudgesHardwareTestWorkerListener {
    void hasNewJudgesHardwareTestHitEvent(DataEvent param1DataEvent);
  }
}
