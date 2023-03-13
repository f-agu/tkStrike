package com.xtremis.daedo.tkstrike.service;

public interface RulesService<E extends com.xtremis.daedo.tkstrike.orm.model.RulesEntity, RE extends com.xtremis.daedo.tkstrike.ui.model.IRulesEntry> extends TkStrikeService<E, RE> {
  E getRules() throws TkStrikeServiceException;
  
  RE getRulesEntry() throws TkStrikeServiceException;
  
  void update(E paramE) throws TkStrikeServiceException;
  
  void update(RE paramRE) throws TkStrikeServiceException;
}
