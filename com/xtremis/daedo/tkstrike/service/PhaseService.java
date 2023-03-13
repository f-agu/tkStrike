package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.Entity;
import com.xtremis.daedo.tkstrike.orm.model.Phase;
import com.xtremis.daedo.tkstrike.ui.model.PhaseEntry;
import java.util.List;

public interface PhaseService extends TkStrikeService<Phase, PhaseEntry> {
  List<Phase> findAll() throws TkStrikeServiceException;
  
  Phase getByName(String paramString) throws TkStrikeServiceException;
  
  Phase getByAbbreviation(String paramString) throws TkStrikeServiceException;
  
  Phase getById(String paramString) throws TkStrikeServiceException;
  
  Phase doGetCreateOrUpdateEntity(String paramString) throws TkStrikeServiceException;
  
  PhaseEntry doGetCreateOrUpdateEntry(String paramString) throws TkStrikeServiceException;
  
  Phase createNew(String paramString1, String paramString2) throws TkStrikeServiceException;
  
  Phase update(String paramString1, String paramString2, String paramString3) throws TkStrikeServiceException;
  
  void delete(String paramString) throws TkStrikeServiceException;
}
