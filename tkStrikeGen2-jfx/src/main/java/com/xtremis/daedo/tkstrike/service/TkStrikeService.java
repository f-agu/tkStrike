package com.xtremis.daedo.tkstrike.service;

import java.util.List;

public interface TkStrikeService<E extends com.xtremis.daedo.tkstrike.orm.model.Entity, D extends com.xtremis.daedo.tkstrike.ui.model.Entry> {
  List<E> findAll() throws TkStrikeServiceException;
  
  E getById(String paramString) throws TkStrikeServiceException;
  
  D getEntryById(String paramString) throws TkStrikeServiceException;
  
  List<D> findAllEntries() throws TkStrikeServiceException;
  
  D transform(E paramE) throws TkStrikeServiceException;
  
  void delete(String paramString) throws TkStrikeServiceException;
  
  Boolean canDelete(String paramString);
  
  void deleteAll() throws TkStrikeServiceException;
}
