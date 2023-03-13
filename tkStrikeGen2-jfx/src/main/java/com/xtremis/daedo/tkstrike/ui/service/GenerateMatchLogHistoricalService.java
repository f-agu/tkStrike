package com.xtremis.daedo.tkstrike.ui.service;

import com.xtremis.daedo.tkstrike.service.AppStatusWorker;
import com.xtremis.daedo.tkstrike.service.CommonMatchLogHistoricalService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class GenerateMatchLogHistoricalService extends Service<Void> {
  private final CommonMatchLogHistoricalService matchLogHistoricalService;
  
  private final AppStatusWorker appStatusWorker;
  
  private final String matchLogId;
  
  public GenerateMatchLogHistoricalService(CommonMatchLogHistoricalService matchLogHistoricalService, AppStatusWorker appStatusWorker, String matchLogId) {
    this.matchLogHistoricalService = matchLogHistoricalService;
    this.appStatusWorker = appStatusWorker;
    this.matchLogId = matchLogId;
  }
  
  protected void failed() {
    super.failed();
  }
  
  protected Task<Void> createTask() {
    return new Task<Void>() {
        protected Void call() throws Exception {
          GenerateMatchLogHistoricalService.this.matchLogHistoricalService.exportMatchLog2DefaultDirectory(GenerateMatchLogHistoricalService.this.matchLogId);
          GenerateMatchLogHistoricalService.this.appStatusWorker.setNewMatchLogCSVGenerated();
          GenerateMatchLogHistoricalService.this.matchLogHistoricalService.exportMatchLogXLS2DefaultDirectory(GenerateMatchLogHistoricalService.this.matchLogId);
          GenerateMatchLogHistoricalService.this.appStatusWorker.setNewMatchLogXLSGenerated();
          GenerateMatchLogHistoricalService.this.matchLogHistoricalService.exportMatchLogPDF2DefaultDirectory(GenerateMatchLogHistoricalService.this.matchLogId);
          GenerateMatchLogHistoricalService.this.appStatusWorker.setNewMatchLogPDFGenerated();
          System.gc();
          return null;
        }
      };
  }
}
