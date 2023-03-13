package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.Phase;
import com.xtremis.daedo.tkstrike.orm.repository.CommonMatchConfigurationRepository;
import com.xtremis.daedo.tkstrike.orm.repository.PhaseRepository;
import com.xtremis.daedo.tkstrike.ui.model.PhaseEntry;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PhaseServiceImpl extends BaseTkStrikeService<Phase, PhaseEntry> implements PhaseService {
  @Resource
  private PhaseRepository phaseRepository;
  
  @Autowired
  private CommonMatchConfigurationRepository matchConfigurationRepository;
  
  protected JpaRepository<Phase, String> getRepository() {
    return (JpaRepository<Phase, String>)this.phaseRepository;
  }
  
  protected Sort getDefaultSort() {
    return new Sort(Sort.Direction.ASC, new String[] { "id" });
  }
  
  protected void deleteAllChild() throws TkStrikeServiceException {}
  
  public Phase getByName(String phaseName) throws TkStrikeServiceException {
    if (StringUtils.isNotBlank(phaseName))
      try {
        return this.phaseRepository.getByName(phaseName.toUpperCase());
      } catch (Exception e) {
        throw new TkStrikeServiceException(e);
      }  
    return null;
  }
  
  public Phase getByAbbreviation(String abbreviation) throws TkStrikeServiceException {
    if (StringUtils.isNotBlank(abbreviation))
      try {
        return this.phaseRepository.getByAbbreviation(abbreviation.toUpperCase());
      } catch (Exception e) {
        throw new TkStrikeServiceException(e);
      }  
    return null;
  }
  
  @Transactional(readOnly = false)
  public Phase doGetCreateOrUpdateEntity(String phaseName) throws TkStrikeServiceException {
    Phase phase = null;
    if (StringUtils.isNotBlank(phaseName)) {
      phase = getByAbbreviation(phaseName);
      if (phase == null) {
        phase = getByName(phaseName);
        if (phase == null)
          phase = createNew(phaseName, phaseName); 
      } 
    } 
    return phase;
  }
  
  @Transactional(readOnly = false)
  public PhaseEntry doGetCreateOrUpdateEntry(String phaseName) throws TkStrikeServiceException {
    PhaseEntry phaseEntry = null;
    Phase phase = doGetCreateOrUpdateEntity(phaseName);
    if (phase != null)
      phaseEntry = transform(phase); 
    return phaseEntry;
  }
  
  @Transactional(readOnly = false)
  public Phase createNew(String abbreviation, String name) throws TkStrikeServiceException {
    Phase newPhase = getByAbbreviation(abbreviation);
    if (newPhase != null)
      throw new TkStrikeServiceException("Phase with abbreviation " + abbreviation + " already exists!"); 
    newPhase = getByName(name);
    if (newPhase != null)
      throw new TkStrikeServiceException("Phase with name " + name + " already exists!"); 
    try {
      newPhase = new Phase();
      newPhase.setAbbreviation(abbreviation);
      newPhase.setName(name);
      return (Phase)this.phaseRepository.saveAndFlush(newPhase);
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  @Transactional(readOnly = false)
  public Phase update(String id, String abbreviation, String name) throws TkStrikeServiceException {
    try {
      Phase phase = getById(id);
      if (phase != null) {
        if (StringUtils.isNotBlank(abbreviation))
          phase.setAbbreviation(abbreviation); 
        if (StringUtils.isNotBlank(name))
          phase.setName(name); 
        return (Phase)this.phaseRepository.saveAndFlush(phase);
      } 
      return null;
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  public Boolean canDelete(String id) {
    try {
      return Boolean.valueOf((this.matchConfigurationRepository.countByPhaseId(id).longValue() == 0L));
    } catch (Exception e) {
      e.printStackTrace();
      return Boolean.FALSE;
    } 
  }
}
