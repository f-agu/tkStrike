package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.DifferentialScoreDefinition;
import com.xtremis.daedo.tkstrike.ui.model.DifferentialScoreDefinitionEntry;

public interface DifferentialScoreDefinitionService extends TkStrikeService<DifferentialScoreDefinition, DifferentialScoreDefinitionEntry> {
  DifferentialScoreDefinitionEntry createNew(String paramString1, String paramString2, Integer paramInteger) throws TkStrikeServiceException;
  
  DifferentialScoreDefinitionEntry updateEntry(DifferentialScoreDefinitionEntry paramDifferentialScoreDefinitionEntry) throws TkStrikeServiceException;
  
  DifferentialScoreDefinition getByPhaseIdAndSubCategoryId(String paramString1, String paramString2) throws TkStrikeServiceException;
  
  DifferentialScoreDefinitionEntry getEntryByPhaseIdAndSubCategoryId(String paramString1, String paramString2) throws TkStrikeServiceException;
}
