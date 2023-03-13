package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.Category;
import com.xtremis.daedo.tkstrike.orm.model.Gender;
import com.xtremis.daedo.tkstrike.orm.model.SubCategory;
import com.xtremis.daedo.tkstrike.orm.repository.CategoryRepository;
import com.xtremis.daedo.tkstrike.orm.repository.MatchConfigurationRepository;
import com.xtremis.daedo.tkstrike.tools.ei.om.CategoryDto;
import com.xtremis.daedo.tkstrike.ui.model.CategoryEntry;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl extends BaseTkStrikeService<Category, CategoryEntry> implements CategoryService {
  @Autowired
  private SubCategoryService subCategoryService;
  
  @Autowired
  private CategoryRepository categoryRepository;
  
  @Autowired
  private MatchConfigurationRepository matchConfigurationRepository;
  
  protected JpaRepository<Category, String> getRepository() {
    return (JpaRepository<Category, String>)this.categoryRepository;
  }
  
  protected Sort getDefaultSort() {
    return new Sort(Sort.Direction.ASC, new String[] { "name" });
  }
  
  protected void deleteAllChild() throws TkStrikeServiceException {}
  
  public Category getBySC_G_N(String subcategoryName, Gender gender, String categoryName) throws TkStrikeServiceException {
    if (StringUtils.isNotBlank(subcategoryName) && gender != null && StringUtils.isNotBlank(categoryName))
      try {
        return this.categoryRepository.getBySC_G_N(subcategoryName.toUpperCase(), gender, categoryName.toUpperCase());
      } catch (Exception e) {
        throw new TkStrikeServiceException(e);
      }  
    return null;
  }
  
  public List<Category> findBySubCategoryName(String subcategoryName) throws TkStrikeServiceException {
    if (StringUtils.isNotBlank(subcategoryName))
      try {
        return this.categoryRepository.findBySubCategoryName(subcategoryName.toUpperCase());
      } catch (Exception e) {
        throw new TkStrikeServiceException(e);
      }  
    return null;
  }
  
  public List<Category> findBySubCategoryId(String subcategoryId) throws TkStrikeServiceException {
    try {
      return this.categoryRepository.findBySubCategoryId(subcategoryId);
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  public List<CategoryEntry> findEntriesBySubCategoryId(String subcategoryId) throws TkStrikeServiceException {
    List<CategoryEntry> entries = new ArrayList<>();
    List<Category> entities = findBySubCategoryId(subcategoryId);
    if (entities != null)
      for (Category entity : entities)
        entries.add(transform(entity));  
    return entries;
  }
  
  public List<Category> findByGender(Gender gender) throws TkStrikeServiceException {
    try {
      return this.categoryRepository.findByGender(gender);
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  public List<Category> findBySC_G(String subcategoryId, Gender gender) throws TkStrikeServiceException {
    if (StringUtils.isNotBlank(subcategoryId) && gender != null)
      try {
        return this.categoryRepository.findBySC_G(subcategoryId, gender);
      } catch (Exception e) {
        throw new TkStrikeServiceException(e);
      }  
    return null;
  }
  
  public List<CategoryEntry> findEntriesBySC_G(String subcategoryId, Gender gender) throws TkStrikeServiceException {
    List<CategoryEntry> entries = new ArrayList<>();
    List<Category> entities = findBySC_G(subcategoryId, gender);
    if (entities != null)
      for (Category entity : entities)
        entries.add(transform(entity));  
    return entries;
  }
  
  @Transactional(readOnly = false)
  public Category doGetCreateOrUpdateEntity(CategoryDto categoryDto) throws TkStrikeServiceException {
    Category category = null;
    if (categoryDto != null && categoryDto
      .getSubCategory() != null && categoryDto
      .getName() != null && categoryDto
      .getBodyLevel() != null && categoryDto
      .getHeadLevel() != null) {
      Gender gender = null;
      try {
        gender = Gender.valueOf(categoryDto.getGender().toUpperCase());
      } catch (Exception e) {
        gender = Gender.MALE;
      } 
      category = getBySC_G_N(categoryDto.getSubCategory(), gender, categoryDto.getName());
      if (category == null) {
        SubCategory subCategory = this.subCategoryService.doGetCreateOrUpdateEntity(categoryDto.getSubCategory());
        category = createNew(categoryDto.getName(), subCategory, gender, categoryDto
            
            .getBodyLevel(), categoryDto
            .getHeadLevel());
      } else {
        category = update(category.getId(), category.getName(), category.getSubCategory(), gender, categoryDto
            .getBodyLevel(), categoryDto.getHeadLevel());
      } 
    } 
    return category;
  }
  
  @Transactional(readOnly = false)
  public CategoryEntry doGetCreateOrUpdateEntry(CategoryDto categoryDto) throws TkStrikeServiceException {
    CategoryEntry entry = null;
    Category entity = doGetCreateOrUpdateEntity(categoryDto);
    if (entity != null)
      entry = transform(entity); 
    return entry;
  }
  
  @Transactional(readOnly = false)
  public Category createNew(String name, SubCategory subCategory, Gender gender, Integer bodyLevel, Integer headLevel) throws TkStrikeServiceException {
    Category category = getBySC_G_N(subCategory.getName(), gender, name);
    if (category != null)
      throw new TkStrikeServiceException("A category with these values already exists!"); 
    try {
      category = new Category();
      category.setName(name.toUpperCase());
      category.setGender(gender);
      category.setSubCategory(subCategory);
      category.setBodyLevel(bodyLevel);
      category.setHeadLevel(headLevel);
      return (Category)this.categoryRepository.saveAndFlush(category);
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  @Transactional(readOnly = false)
  public Category createNew(String name, String subcategoryId, Gender gender, Integer bodyLevel, Integer headLevel) throws TkStrikeServiceException {
    return createNew(name, this.subCategoryService.getById(subcategoryId), gender, bodyLevel, headLevel);
  }
  
  @Transactional(readOnly = false)
  public Category update(String id, String name, SubCategory subCategory, Gender gender, Integer bodyLevel, Integer headLevel) throws TkStrikeServiceException {
    try {
      Category category = getById(id);
      boolean someChange = false;
      if (StringUtils.isNotBlank(name)) {
        category.setName(name.toUpperCase());
        someChange = true;
      } 
      if (subCategory != null && !subCategory.getId().equals(category.getSubCategory().getId())) {
        category.setSubCategory(subCategory);
        someChange = true;
      } 
      if (gender != null)
        category.setGender(gender); 
      if (bodyLevel != null && bodyLevel.intValue() > 0) {
        category.setBodyLevel(bodyLevel);
        someChange = true;
      } 
      if (headLevel != null && headLevel.intValue() > 0) {
        category.setHeadLevel(headLevel);
        someChange = true;
      } 
      if (someChange)
        return (Category)this.categoryRepository.saveAndFlush(category); 
      return category;
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  @Transactional(readOnly = false)
  public Category update(String id, String name, String subcategoryId, Gender gender, Integer bodyLevel, Integer headLevel) throws TkStrikeServiceException {
    return update(id, name, (subcategoryId != null) ? this.subCategoryService.getById(subcategoryId) : null, gender, bodyLevel, headLevel);
  }
  
  public Boolean canDelete(String id) {
    return Boolean.valueOf((this.matchConfigurationRepository.countByCategoryId(id).longValue() == 0L));
  }
}
