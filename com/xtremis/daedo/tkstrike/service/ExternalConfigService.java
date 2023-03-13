package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.ExternalConfig;
import com.xtremis.daedo.tkstrike.ui.model.ExternalConfigEntry;

public interface ExternalConfigService extends TkStrikeService<ExternalConfig, ExternalConfigEntry> {
  Boolean getWtCompetitionDataProtocol();
  
  ExternalConfig getExternalConfig() throws TkStrikeServiceException;
  
  ExternalConfigEntry getExternalConfigEntry() throws TkStrikeServiceException;
  
  void update(ExternalConfig paramExternalConfig) throws TkStrikeServiceException;
  
  void update(ExternalConfigEntry paramExternalConfigEntry) throws TkStrikeServiceException;
}
