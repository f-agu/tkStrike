package com.xtremis.daedo.tkstrike.service;

public interface SoundConfigurationService<E extends com.xtremis.daedo.tkstrike.orm.model.SoundConfigurationEntity, SE extends com.xtremis.daedo.tkstrike.ui.model.ISoundConfigurationEntry> extends TkStrikeService<E, SE> {
  E getSoundConfiguration() throws TkStrikeServiceException;
  
  SE getSoundConfigurationEntry() throws TkStrikeServiceException;
  
  void update(E paramE) throws TkStrikeServiceException;
  
  void update(SE paramSE) throws TkStrikeServiceException;
}
