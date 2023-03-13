package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.DifferentialScoreDefinition;
import com.xtremis.daedo.tkstrike.orm.model.Phase;
import com.xtremis.daedo.tkstrike.orm.model.SubCategory;
import com.xtremis.daedo.tkstrike.orm.repository.DifferentialScoreDefinitionRepository;
import com.xtremis.daedo.tkstrike.orm.repository.PhaseRepository;
import com.xtremis.daedo.tkstrike.orm.repository.SubCategoryRepository;
import com.xtremis.daedo.tkstrike.ui.model.DifferentialScoreDefinitionEntry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DifferentialScoreDefinitionServiceImpl extends BaseTkStrikeService<DifferentialScoreDefinition, DifferentialScoreDefinitionEntry> implements DifferentialScoreDefinitionService {
  private final DifferentialScoreDefinitionRepository differentialScoreDefinitionRepository;
  
  private final PhaseRepository phaseRepository;
  
  private final SubCategoryRepository subCategoryRepository;
  
  @Autowired
  public DifferentialScoreDefinitionServiceImpl(DifferentialScoreDefinitionRepository differentialScoreDefinitionRepository, PhaseRepository phaseRepository, SubCategoryRepository subCategoryRepository) {
    this.differentialScoreDefinitionRepository = differentialScoreDefinitionRepository;
    this.phaseRepository = phaseRepository;
    this.subCategoryRepository = subCategoryRepository;
  }
  
  @Transactional(readOnly = false)
  public DifferentialScoreDefinitionEntry createNew(String phaseId, String subCategoryId, Integer value) throws TkStrikeServiceException {
    if (StringUtils.isNotBlank(phaseId) && StringUtils.isNotBlank(subCategoryId)) {
      DifferentialScoreDefinition ifExists = getByPhaseIdAndSubCategoryId(phaseId, subCategoryId);
      if (ifExists != null)
        throw new TkStrikeServiceException("Already exists a Differential Score Definition for these phase and contest!!"); 
      Phase phase = (Phase)this.phaseRepository.findOne(phaseId);
      if (phase == null)
        throw new TkStrikeServiceException("Can't find Phase with id: " + phaseId); 
      SubCategory subCategory = (SubCategory)this.subCategoryRepository.findOne(subCategoryId);
      if (subCategory == null)
        throw new TkStrikeServiceException("Can't find Contest with id:" + subCategoryId); 
      if (value == null || value.intValue() < 0)
        throw new TkStrikeServiceException("Invalid differential score : " + value); 
      DifferentialScoreDefinition differentialScoreDefinition = new DifferentialScoreDefinition();
      differentialScoreDefinition.setPhase(phase);
      differentialScoreDefinition.setSubCategory(subCategory);
      differentialScoreDefinition.setValue(value);
      try {
        return transform((DifferentialScoreDefinition)this.differentialScoreDefinitionRepository.saveAndFlush(differentialScoreDefinition));
      } catch (Exception e) {
        throw new TkStrikeServiceException(e);
      } 
    } 
    return null;
  }
  
  @Transactional(readOnly = false)
  public DifferentialScoreDefinitionEntry updateEntry(DifferentialScoreDefinitionEntry entry) throws TkStrikeServiceException {
    if (entry != null && entry
      .getPhase() != null && entry.getPhase().getId() != null && entry
      .getSubCategory() != null && entry.getSubCategory().getId() != null) {
      DifferentialScoreDefinition entity = (DifferentialScoreDefinition)this.differentialScoreDefinitionRepository.findOne(entry.getId());
      if (entity == null)
        throw new TkStrikeServiceException("Can't find differentialScoreDefinition with id:" + entry.getId()); 
      Phase phase = (Phase)this.phaseRepository.findOne(entry.getPhase().getId());
      if (phase == null)
        throw new TkStrikeServiceException("Can't find Phase with id: " + entry.getPhase().getId()); 
      entity.setPhase(phase);
      SubCategory subCategory = (SubCategory)this.subCategoryRepository.findOne(entry.getSubCategory().getId());
      if (subCategory == null)
        throw new TkStrikeServiceException("Can't find Contest with id:" + entry.getSubCategory().getId()); 
      entity.setSubCategory(subCategory);
      entity.setValue(Integer.valueOf(entry.getValue()));
      try {
        return transform((DifferentialScoreDefinition)this.differentialScoreDefinitionRepository.saveAndFlush(entity));
      } catch (Exception e) {
        throw new TkStrikeServiceException(e);
      } 
    } 
    return null;
  }
  
  public DifferentialScoreDefinition getByPhaseIdAndSubCategoryId(String phaseId, String subCategoryId) throws TkStrikeServiceException {
    return this.differentialScoreDefinitionRepository.getByPhaseIdAndSubCategoryId(phaseId, subCategoryId);
  }
  
  public DifferentialScoreDefinitionEntry getEntryByPhaseIdAndSubCategoryId(String phaseId, String subCategoryId) throws TkStrikeServiceException {
    DifferentialScoreDefinition entity = getByPhaseIdAndSubCategoryId(phaseId, subCategoryId);
    if (entity != null && entity.getId() != null)
      return transform(entity); 
    return null;
  }
  
  protected JpaRepository<DifferentialScoreDefinition, String> getRepository() {
    return (JpaRepository<DifferentialScoreDefinition, String>)this.differentialScoreDefinitionRepository;
  }
  
  protected Sort getDefaultSort() {
    return new Sort(Sort.Direction.ASC, new String[] { "id" });
  }
  
  protected void deleteAllChild() throws TkStrikeServiceException {}
  
  public Boolean canDelete(String id) {
    return Boolean.valueOf(true);
  }
}
