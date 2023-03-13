package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.om.GoldenPointTieBreakerInfoDto;
import com.xtremis.daedo.tkstrike.om.combat.FinalDecision;
import com.xtremis.daedo.tkstrike.om.combat.MatchWinner;
import com.xtremis.daedo.tkstrike.orm.model.MatchLogItemType;
import com.xtremis.daedo.tkstrike.ui.model.IMatchConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.IRulesEntry;
import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public interface CommonMatchLogService<D extends com.xtremis.daedo.tkstrike.om.CommonMatchLogDto, ID extends com.xtremis.daedo.tkstrike.om.CommonMatchLogItemDto> {
  D getById(String paramString) throws TkStrikeServiceException;
  
  List<D> findAll() throws TkStrikeServiceException;
  
  D getLastStarted() throws TkStrikeServiceException;
  
  D createNew(IMatchConfigurationEntry paramIMatchConfigurationEntry, IRulesEntry paramIRulesEntry, Integer paramInteger, Boolean paramBoolean1, Boolean paramBoolean2) throws TkStrikeServiceException;
  
  D updateNetworkInfo(String paramString, Integer paramInteger, Boolean paramBoolean1, Boolean paramBoolean2) throws TkStrikeServiceException;
  
  D updateMatchLogStartTime(String paramString, Long paramLong) throws TkStrikeServiceException;
  
  D updateMatchLogFinish(String paramString1, Long paramLong, MatchWinner paramMatchWinner, FinalDecision paramFinalDecision, String paramString2, GoldenPointTieBreakerInfoDto paramGoldenPointTieBreakerInfoDto) throws TkStrikeServiceException;
  
  Boolean updateMatchLogRoundsWinners(String paramString, Map<Integer, MatchWinner> paramMap) throws TkStrikeServiceException;
  
  void addMatchLogItem(String paramString1, String paramString2, Integer paramInteger1, Long paramLong1, Long paramLong2, Long paramLong3, MatchLogItemType paramMatchLogItemType, Integer paramInteger2, Integer paramInteger3, Integer paramInteger4, Integer paramInteger5, String paramString3) throws TkStrikeServiceException;
  
  void addMatchLogItem(String paramString, ID paramID) throws TkStrikeServiceException;
  
  List<ID> findByMatchLogId(String paramString) throws TkStrikeServiceException;
  
  void deleteByMatchConfigurationId(String paramString) throws TkStrikeServiceException;
  
  void deleteWhenSystemTimeLessThan(Long paramLong) throws TkStrikeServiceException;
  
  void delete(String paramString) throws TkStrikeServiceException;
  
  void deleteMatchLogItems(String paramString) throws TkStrikeServiceException;
  
  void deleteAll() throws TkStrikeServiceException;
  
  void deleteExceptLast() throws TkStrikeServiceException;
  
  void exportMatchLog(String paramString, File paramFile) throws TkStrikeServiceException;
  
  Comparator<ID> getComparator4Items();
}
