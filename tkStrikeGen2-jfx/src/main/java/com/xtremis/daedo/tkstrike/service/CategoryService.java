package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.Category;
import com.xtremis.daedo.tkstrike.orm.model.Entity;
import com.xtremis.daedo.tkstrike.orm.model.Gender;
import com.xtremis.daedo.tkstrike.orm.model.SubCategory;
import com.xtremis.daedo.tkstrike.tools.ei.om.CategoryDto;
import com.xtremis.daedo.tkstrike.ui.model.CategoryEntry;
import java.util.List;

public interface CategoryService extends TkStrikeService<Category, CategoryEntry> {
  Category getBySC_G_N(String paramString1, Gender paramGender, String paramString2) throws TkStrikeServiceException;
  
  List<Category> findByGender(Gender paramGender) throws TkStrikeServiceException;
  
  List<Category> findBySC_G(String paramString, Gender paramGender) throws TkStrikeServiceException;
  
  List<Category> findBySubCategoryName(String paramString) throws TkStrikeServiceException;
  
  List<Category> findBySubCategoryId(String paramString) throws TkStrikeServiceException;
  
  List<CategoryEntry> findEntriesBySubCategoryId(String paramString) throws TkStrikeServiceException;
  
  List<CategoryEntry> findEntriesBySC_G(String paramString, Gender paramGender) throws TkStrikeServiceException;
  
  Category getById(String paramString) throws TkStrikeServiceException;
  
  Category doGetCreateOrUpdateEntity(CategoryDto paramCategoryDto) throws TkStrikeServiceException;
  
  CategoryEntry doGetCreateOrUpdateEntry(CategoryDto paramCategoryDto) throws TkStrikeServiceException;
  
  Category createNew(String paramString, SubCategory paramSubCategory, Gender paramGender, Integer paramInteger1, Integer paramInteger2) throws TkStrikeServiceException;
  
  Category createNew(String paramString1, String paramString2, Gender paramGender, Integer paramInteger1, Integer paramInteger2) throws TkStrikeServiceException;
  
  Category update(String paramString1, String paramString2, SubCategory paramSubCategory, Gender paramGender, Integer paramInteger1, Integer paramInteger2) throws TkStrikeServiceException;
  
  Category update(String paramString1, String paramString2, String paramString3, Gender paramGender, Integer paramInteger1, Integer paramInteger2) throws TkStrikeServiceException;
  
  void delete(String paramString) throws TkStrikeServiceException;
}
