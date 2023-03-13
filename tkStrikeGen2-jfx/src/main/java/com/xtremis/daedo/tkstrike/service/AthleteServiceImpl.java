package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.Athlete;
import com.xtremis.daedo.tkstrike.orm.model.Flag;
import com.xtremis.daedo.tkstrike.orm.repository.AthleteRepository;
import com.xtremis.daedo.tkstrike.orm.repository.MatchConfigurationRepository;
import com.xtremis.daedo.tkstrike.orm.repository.MatchLogRepository;
import com.xtremis.daedo.tkstrike.tools.ei.om.AthleteDto;
import com.xtremis.daedo.tkstrike.ui.model.AthleteEntry;
import com.xtremis.daedo.wtdata.model.constants.CompetitorType;
import com.xtremis.daedo.wtdata.model.constants.Gender;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AthleteServiceImpl extends BaseTkStrikeService<Athlete, AthleteEntry> implements AthleteService {
  @Resource
  private AthleteRepository athleteRepository;
  
  @Autowired
  private FlagService flagService;
  
  @Resource
  private MatchLogRepository matchLogRepository;
  
  @Resource
  private MatchConfigurationRepository matchConfigurationRepository;
  
  protected JpaRepository<Athlete, String> getRepository() {
    return (JpaRepository<Athlete, String>)this.athleteRepository;
  }
  
  protected Sort getDefaultSort() {
    return new Sort(Sort.Direction.ASC, new String[] { "scoreboardName" });
  }
  
  protected void deleteAllChild() throws TkStrikeServiceException {}
  
  public AthleteEntry getEntryBytWfId(String wfId) throws TkStrikeServiceException {
    AthleteEntry res = null;
    Athlete athlete = getByWfId(wfId);
    if (athlete != null) {
      res = new AthleteEntry();
      res.fillByEntity(athlete);
    } 
    return res;
  }
  
  public Athlete getByWfId(String wfId) throws TkStrikeServiceException {
    if (StringUtils.isNotBlank(wfId))
      try {
        return this.athleteRepository.getByWfId(wfId);
      } catch (Exception e) {
        throw new TkStrikeServiceException(e);
      }  
    return null;
  }
  
  public Athlete getByOvrInternalId(String ovrInternalId) throws TkStrikeServiceException {
    if (StringUtils.isNotBlank(ovrInternalId))
      try {
        return this.athleteRepository.getByOvrInternalId(ovrInternalId);
      } catch (Exception e) {
        return null;
      }  
    return null;
  }
  
  public List<Athlete> findByFlagId(String flagId) throws TkStrikeServiceException {
    try {
      return this.athleteRepository.findByFlagId(flagId);
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  public List<Athlete> findByFlagName(String flagName) throws TkStrikeServiceException {
    try {
      return this.athleteRepository.findByFlagName(flagName);
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  public List<Athlete> findByFlagAbbreviation(String flagAbbreviation) throws TkStrikeServiceException {
    try {
      return this.athleteRepository.findByFlagName(flagAbbreviation);
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  @Transactional(readOnly = false)
  public Athlete doGetCreateOrUpdateEntity(AthleteDto athleteDto) throws TkStrikeServiceException {
    Athlete athlete = null;
    if (athleteDto != null && athleteDto.getWfId() != null && athleteDto.getFlagAbbreviation() != null) {
      athlete = getByWfId(athleteDto.getWfId());
      Flag flag = this.flagService.doGetCreateOrUpdateEntity(athleteDto.getFlagAbbreviation(), athleteDto
          .getFlagName(), athleteDto
          .getFlagShowName());
      if (athlete == null) {
        athlete = createNew(athleteDto, flag);
      } else {
        athlete = update(athlete.getId(), athleteDto, flag);
      } 
    } 
    return athlete;
  }
  
  @Transactional(readOnly = false)
  public AthleteEntry doGetCreateOrUpdateEntry(AthleteDto athleteDto) throws TkStrikeServiceException {
    AthleteEntry res = null;
    Athlete athlete = doGetCreateOrUpdateEntity(athleteDto);
    if (athlete != null) {
      res = new AthleteEntry();
      res.fillByEntity(athlete);
    } 
    return res;
  }
  
  @Transactional(readOnly = false)
  public Athlete createNew(String name, String wfId, String ovrInternalId, String flagId) throws TkStrikeServiceException {
    return createNew(name, wfId, ovrInternalId, StringUtils.isNotBlank(flagId) ? this.flagService.getById(flagId) : null);
  }
  
  @Transactional(readOnly = false)
  public Athlete createNew(String name, String wfId, String ovrInternalId, Flag flag) throws TkStrikeServiceException {
    Athlete byWtfId = getByWfId(wfId);
    if (byWtfId != null)
      throw new TkStrikeServiceException("Athlete with the same WF-ID exist!"); 
    try {
      Athlete athlete = new Athlete();
      if (StringUtils.isNotBlank(ovrInternalId))
        athlete.setOvrInternalId(ovrInternalId); 
      athlete.setScoreboardName(name);
      athlete.setWfId(wfId);
      athlete.setFlag(flag);
      return (Athlete)this.athleteRepository.saveAndFlush(athlete);
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  @Transactional(readOnly = false)
  public Athlete createNew(AthleteDto athleteDto, Flag flag) throws TkStrikeServiceException {
    if (athleteDto != null && flag != null) {
      Athlete byWtfId = getByWfId(athleteDto.getWfId());
      if (byWtfId != null)
        throw new TkStrikeServiceException("Athlete with the same WF-ID exist!"); 
      try {
        Athlete athlete = new Athlete();
        BeanUtils.copyProperties(athleteDto, athlete, new String[] { "id", "version", "flag", "gender", "competitorType" });
        athlete.setFlag(flag);
        if (athleteDto.getGender() != null)
          try {
            athlete.setGender(Gender.valueOf(athleteDto.getGender()));
          } catch (Exception exception) {} 
        if (athleteDto.getCompetitorType() != null)
          try {
            athlete.setCompetitorType(CompetitorType.valueOf(athleteDto.getCompetitorType()));
          } catch (Exception exception) {} 
        return (Athlete)this.athleteRepository.saveAndFlush(athlete);
      } catch (Exception e) {
        throw new TkStrikeServiceException(e);
      } 
    } 
    return null;
  }
  
  @Transactional(readOnly = false)
  public Athlete update(String id, AthleteDto athleteDto, Flag flag) throws TkStrikeServiceException {
    try {
      Athlete athlete = getById(id);
      if (athlete != null) {
        BeanUtils.copyProperties(athleteDto, athlete, new String[] { "id", "version", "flag", "gender", "competitorType" });
        if (flag != null)
          athlete.setFlag(flag); 
        if (athleteDto.getGender() != null)
          try {
            athlete.setGender(Gender.valueOf(athleteDto.getGender()));
          } catch (Exception exception) {} 
        if (athleteDto.getCompetitorType() != null)
          try {
            athlete.setCompetitorType(CompetitorType.valueOf(athleteDto.getCompetitorType()));
          } catch (Exception exception) {} 
        return (Athlete)this.athleteRepository.saveAndFlush(athlete);
      } 
      return null;
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  @Transactional(readOnly = false)
  public Athlete update(String id, String name, String wfId, String ovrInternalId, String flagId) throws TkStrikeServiceException {
    return update(id, name, wfId, ovrInternalId, StringUtils.isNotBlank(flagId) ? this.flagService.getById(flagId) : null);
  }
  
  @Transactional(readOnly = false)
  public Athlete update(String id, String name, String wfId, String ovrInternalId, Flag flag) throws TkStrikeServiceException {
    try {
      Athlete athlete = getById(id);
      if (athlete != null) {
        if (StringUtils.isNotBlank(name))
          athlete.setScoreboardName(name); 
        if (StringUtils.isNotBlank(wfId))
          athlete.setWfId(wfId); 
        if (StringUtils.isNotBlank(ovrInternalId))
          athlete.setOvrInternalId(ovrInternalId); 
        if (flag != null)
          athlete.setFlag(flag); 
        return (Athlete)this.athleteRepository.saveAndFlush(athlete);
      } 
      return null;
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  public Boolean canDelete(String id) {
    try {
      return canDeleteAthlete(id);
    } catch (TkStrikeServiceException e) {
      e.printStackTrace();
      return Boolean.valueOf(false);
    } 
  }
  
  public Boolean canDeleteAthlete(String athleteId) throws TkStrikeServiceException {
    if (StringUtils.isNotBlank(athleteId)) {
      Long nML = this.matchLogRepository.getCountByAthlete(athleteId);
      Long nMC = this.matchConfigurationRepository.getCountByAthlete(athleteId);
      return Boolean.valueOf(((nML == null || nML.longValue() == 0L) && (nMC == null || nMC.longValue() == 0L)));
    } 
    return Boolean.FALSE;
  }
}
