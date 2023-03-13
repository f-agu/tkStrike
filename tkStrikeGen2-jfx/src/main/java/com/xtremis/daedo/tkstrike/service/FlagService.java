package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.Flag;
import com.xtremis.daedo.tkstrike.ui.scene.FlagEntry;

public interface FlagService extends TkStrikeService<Flag, FlagEntry> {
  Flag doGetCreateOrUpdateEntity(String paramString1, String paramString2, Boolean paramBoolean) throws TkStrikeServiceException;
  
  FlagEntry doGetCreateOrUpdateEntry(String paramString1, String paramString2, Boolean paramBoolean) throws TkStrikeServiceException;
  
  Flag createNew(String paramString1, String paramString2, Boolean paramBoolean, byte[] paramArrayOfbyte) throws TkStrikeServiceException;
  
  Flag update(String paramString1, String paramString2, String paramString3, Boolean paramBoolean, byte[] paramArrayOfbyte) throws TkStrikeServiceException;
  
  void delete(String paramString) throws TkStrikeServiceException;
  
  Flag getByAbbreviation(String paramString) throws TkStrikeServiceException;
  
  FlagEntry getEntryByAbbreviation(String paramString) throws TkStrikeServiceException;
}
