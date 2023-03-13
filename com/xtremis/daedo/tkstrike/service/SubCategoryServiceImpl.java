package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.SubCategory;
import com.xtremis.daedo.tkstrike.orm.repository.MatchConfigurationRepository;
import com.xtremis.daedo.tkstrike.orm.repository.SubCategoryRepository;
import com.xtremis.daedo.tkstrike.ui.model.SubCategoryEntry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SubCategoryServiceImpl extends BaseTkStrikeService<SubCategory, SubCategoryEntry> implements SubCategoryService {
  @Autowired
  private SubCategoryRepository subCategoryRepository;
  
  @Autowired
  private MatchConfigurationRepository matchConfigurationRepository;
  
  protected JpaRepository<SubCategory, String> getRepository() {
    return (JpaRepository<SubCategory, String>)this.subCategoryRepository;
  }
  
  protected Sort getDefaultSort() {
    return new Sort(Sort.Direction.ASC, new String[] { "name" });
  }
  
  protected void deleteAllChild() throws TkStrikeServiceException {}
  
  public SubCategory getByName(String name) throws TkStrikeServiceException {
    try {
      return this.subCategoryRepository.getScByName(name.toUpperCase());
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  @Transactional(readOnly = false)
  public SubCategory doGetCreateOrUpdateEntity(String name) throws TkStrikeServiceException {
    SubCategory subCategory = null;
    if (StringUtils.isNotBlank(name)) {
      subCategory = getByName(name.toUpperCase());
      if (subCategory == null)
        subCategory = createNew(name); 
    } 
    return subCategory;
  }
  
  @Transactional(readOnly = false)
  public SubCategoryEntry doGetCreateOrUpdateEntry(String name) throws TkStrikeServiceException {
    SubCategoryEntry entry = null;
    SubCategory entity = doGetCreateOrUpdateEntity(name);
    if (entity != null)
      entry = transform(entity); 
    return entry;
  }
  
  @Transactional(readOnly = false)
  public SubCategory createNew(String name) throws TkStrikeServiceException {
    SubCategory newSubCategory = getByName(name);
    if (newSubCategory != null)
      throw new TkStrikeServiceException("Phase with name " + name + " already exists!"); 
    try {
      newSubCategory = new SubCategory();
      newSubCategory.setName(name.toUpperCase());
      return (SubCategory)this.subCategoryRepository.saveAndFlush(newSubCategory);
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  @Transactional(readOnly = false)
  public SubCategory update(String id, String name) throws TkStrikeServiceException {
    try {
      SubCategory subCategory = getById(id);
      if (subCategory != null) {
        subCategory.setName(name);
        return (SubCategory)this.subCategoryRepository.saveAndFlush(subCategory);
      } 
      return null;
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  public Boolean canDelete(String id) {
    return Boolean.valueOf((this.matchConfigurationRepository.countByCategorySubCategoryId(id).longValue() == 0L));
  }
}
