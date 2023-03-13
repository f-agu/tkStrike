package com.xtremis.daedo.tkstrike.service;

public interface NetworkConfigurationService<E extends com.xtremis.daedo.tkstrike.orm.model.NetworkConfigurationEntity, NE extends com.xtremis.daedo.tkstrike.ui.model.INetworkConfigurationEntry> extends TkStrikeService<E, NE> {
  E getNetworkConfiguration() throws TkStrikeServiceException;
  
  NE getNetworkConfigurationEntry() throws TkStrikeServiceException;
  
  void update(E paramE) throws TkStrikeServiceException;
  
  void update(NE paramNE) throws TkStrikeServiceException;
}
