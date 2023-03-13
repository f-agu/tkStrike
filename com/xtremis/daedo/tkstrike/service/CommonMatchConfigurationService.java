package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.tools.ei.om.MatchConfigurationDto;
import java.util.Date;
import java.util.List;

public interface CommonMatchConfigurationService<E extends com.xtremis.daedo.tkstrike.orm.model.MatchConfigurationEntity, ME extends com.xtremis.daedo.tkstrike.ui.model.IMatchConfigurationEntry> extends TkStrikeService<E, ME> {
  E getByMatchNumber(String paramString) throws TkStrikeServiceException;
  
  ME getEntryByMatchNumber(String paramString) throws TkStrikeServiceException;
  
  ME createByEntry(ME paramME) throws TkStrikeServiceException;
  
  E create(ME paramME) throws TkStrikeServiceException;
  
  ME updateByEntry(ME paramME) throws TkStrikeServiceException;
  
  void updateMatchIsStarted(String paramString) throws TkStrikeServiceException;
  
  E update(ME paramME) throws TkStrikeServiceException;
  
  Long countByPhaseId(String paramString) throws TkStrikeServiceException;
  
  Long countByBlueAthleteIdOrRedAthleteId(String paramString1, String paramString2) throws TkStrikeServiceException;
  
  Long countByCategoryId(String paramString) throws TkStrikeServiceException;
  
  Long countByCategorySubCategoryId(String paramString) throws TkStrikeServiceException;
  
  ME transformByDto(MatchConfigurationDto paramMatchConfigurationDto, boolean paramBoolean) throws TkStrikeServiceException;
  
  List<E> findByDates(Date paramDate1, Date paramDate2) throws TkStrikeServiceException;
  
  List<ME> findEntriesByDates(Date paramDate1, Date paramDate2) throws TkStrikeServiceException;
  
  List<E> findNotStarted() throws TkStrikeServiceException;
  
  List<ME> findEntriesNotStarted() throws TkStrikeServiceException;
  
  List<E> findStarted() throws TkStrikeServiceException;
  
  List<ME> findEntriesStarted() throws TkStrikeServiceException;
}
