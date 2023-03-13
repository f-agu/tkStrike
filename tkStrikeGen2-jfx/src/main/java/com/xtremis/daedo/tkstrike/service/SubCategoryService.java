package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.Entity;
import com.xtremis.daedo.tkstrike.orm.model.SubCategory;
import com.xtremis.daedo.tkstrike.ui.model.SubCategoryEntry;
import java.util.List;

public interface SubCategoryService extends TkStrikeService<SubCategory, SubCategoryEntry> {
  List<SubCategory> findAll() throws TkStrikeServiceException;
  
  SubCategory getById(String paramString) throws TkStrikeServiceException;
  
  SubCategory getByName(String paramString) throws TkStrikeServiceException;
  
  SubCategory doGetCreateOrUpdateEntity(String paramString) throws TkStrikeServiceException;
  
  SubCategoryEntry doGetCreateOrUpdateEntry(String paramString) throws TkStrikeServiceException;
  
  SubCategory createNew(String paramString) throws TkStrikeServiceException;
  
  SubCategory update(String paramString1, String paramString2) throws TkStrikeServiceException;
  
  void delete(String paramString) throws TkStrikeServiceException;
}
