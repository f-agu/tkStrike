package com.xtremis.daedo.tkstrike.service;

import java.io.File;
import java.util.List;

public interface CommonMatchLogHistoricalService<D extends com.xtremis.daedo.tkstrike.om.CommonMatchLogDto, ID extends com.xtremis.daedo.tkstrike.om.CommonMatchLogItemDto> {
  void exportAllMatchLog2DefaultDirectory() throws TkStrikeServiceException;
  
  void exportMatchLog2DefaultDirectory(String paramString) throws TkStrikeServiceException;
  
  void exportMatchLog(String paramString, File paramFile) throws TkStrikeServiceException;
  
  void exportMatchLogXLS(String paramString, File paramFile) throws TkStrikeServiceException;
  
  void exportMatchLogXLS2DefaultDirectory(String paramString) throws TkStrikeServiceException;
  
  void exportMatchLogPDF(String paramString, File paramFile) throws TkStrikeServiceException;
  
  void exportMatchLogPDF2DefaultDirectory(String paramString) throws TkStrikeServiceException;
  
  D getMatchLog(String paramString);
  
  List<ID> findItemsByMatchLogId(String paramString);
  
  void deleteMatchLog(String paramString);
  
  void deleteByMatchConfigurationId(String paramString);
  
  void deleteAll();
  
  void migrateToHistorical(String paramString) throws TkStrikeServiceException;
  
  void migrateToHistorical(D paramD) throws TkStrikeServiceException;
  
  void migrateToHistorical() throws TkStrikeServiceException;
}
