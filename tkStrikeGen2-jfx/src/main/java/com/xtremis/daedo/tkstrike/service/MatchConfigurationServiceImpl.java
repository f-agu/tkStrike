package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.Athlete;
import com.xtremis.daedo.tkstrike.orm.model.Category;
import com.xtremis.daedo.tkstrike.orm.model.MatchConfiguration;
import com.xtremis.daedo.tkstrike.orm.model.MatchConfigurationEntity;
import com.xtremis.daedo.tkstrike.orm.model.Phase;
import com.xtremis.daedo.tkstrike.orm.model.Referee;
import com.xtremis.daedo.tkstrike.orm.model.RoundsConfig;
import com.xtremis.daedo.tkstrike.orm.repository.MatchConfigurationRepository;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchConfigurationDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.RoundsConfigDto;
import com.xtremis.daedo.tkstrike.ui.model.AthleteEntry;
import com.xtremis.daedo.tkstrike.ui.model.CategoryEntry;
import com.xtremis.daedo.tkstrike.ui.model.IMatchConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.MatchConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.PhaseEntry;
import com.xtremis.daedo.tkstrike.ui.model.RoundsConfigEntry;
import com.xtremis.daedo.tkstrike.ui.scene.RefereeEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MatchConfigurationServiceImpl extends BaseTkStrikeService<MatchConfiguration, MatchConfigurationEntry> implements MatchConfigurationService {
  @Autowired
  private MatchConfigurationRepository matchConfigurationRepository;
  
  @Autowired
  private PhaseService phaseService;
  
  @Autowired
  private CategoryService categoryService;
  
  @Autowired
  private AthleteService athleteService;
  
  @Autowired
  private RefereeService refereeService;
  
  @Autowired
  private CommonMatchLogService matchLogService;
  
  @Autowired
  private CommonMatchLogHistoricalService matchLogHistoricalService;
  
  protected JpaRepository<MatchConfiguration, String> getRepository() {
    return (JpaRepository<MatchConfiguration, String>)this.matchConfigurationRepository;
  }
  
  protected Sort getDefaultSort() {
    return new Sort(Sort.Direction.ASC, new String[] { "matchNumber" });
  }
  
  @Transactional(readOnly = false)
  protected void deleteAllChild() throws TkStrikeServiceException {
    List<MatchConfiguration> matchConfigurations = findAll();
    if (matchConfigurations != null)
      for (MatchConfiguration matchConfiguration : matchConfigurations)
        this.matchLogService.deleteByMatchConfigurationId(matchConfiguration.getId());  
  }
  
  public MatchConfiguration getByMatchNumber(String matchNumber) throws TkStrikeServiceException {
    return (MatchConfiguration)this.matchConfigurationRepository.getByMatchNumber(matchNumber);
  }
  
  public MatchConfigurationEntry getEntryByMatchNumber(String matchNumber) throws TkStrikeServiceException {
    MatchConfigurationEntry res = null;
    MatchConfiguration entity = getByMatchNumber(matchNumber);
    if (entity != null) {
      res = new MatchConfigurationEntry();
      res.fillByEntity(entity);
    } 
    return res;
  }
  
  @Transactional(readOnly = false)
  public MatchConfigurationEntry transformByDto(MatchConfigurationDto matchConfigurationDto, boolean allowNullAthlete) throws TkStrikeServiceException {
    MatchConfigurationEntry res = new MatchConfigurationEntry();
    if (matchConfigurationDto != null) {
      res.vmMatchInternalIdProperty().set(matchConfigurationDto.getInternalId());
      res.matchNumberProperty().set(matchConfigurationDto.getMatchNumber());
      PhaseEntry phaseEntry = this.phaseService.doGetCreateOrUpdateEntry(matchConfigurationDto.getPhase());
      if (phaseEntry == null)
        throw new TkStrikeServiceException("Bad information for Phase Creation!"); 
      res.phaseProperty().set(phaseEntry);
      CategoryEntry categoryEntry = this.categoryService.doGetCreateOrUpdateEntry(matchConfigurationDto.getCategory());
      if (categoryEntry == null)
        throw new TkStrikeServiceException("Bad information for Category Creation!"); 
      res.categoryProperty().set(categoryEntry);
      AthleteEntry blueAthleteEntry = this.athleteService.doGetCreateOrUpdateEntry(matchConfigurationDto.getBlueAthlete());
      if (blueAthleteEntry == null && !allowNullAthlete)
        throw new TkStrikeServiceException("Bad information for Blue Athlete Creation!"); 
      res.blueAthleteProperty().set(blueAthleteEntry);
      if (matchConfigurationDto.getBlueAthleteVideoQuota() != null) {
        res.blueAthleteVideoQuotaProperty().set(matchConfigurationDto.getBlueAthleteVideoQuota().intValue());
      } else {
        res.blueAthleteVideoQuotaProperty().set(0);
      } 
      AthleteEntry redAthleteEntry = this.athleteService.doGetCreateOrUpdateEntry(matchConfigurationDto.getRedAthlete());
      if (redAthleteEntry == null && !allowNullAthlete)
        throw new TkStrikeServiceException("Bad information for Red Athlete Creation!"); 
      res.redAthleteProperty().set(redAthleteEntry);
      if (matchConfigurationDto.getRedAthleteVideoQuota() != null) {
        res.redAthleteVideoQuotaProperty().set(matchConfigurationDto.getRedAthleteVideoQuota().intValue());
      } else {
        res.redAthleteVideoQuotaProperty().set(0);
      } 
      if (matchConfigurationDto.getRefereeJ1() != null) {
        RefereeEntry refereeEntry = this.refereeService.doGetCreateOrUpdateEntry(matchConfigurationDto.getRefereeJ1());
        if (refereeEntry != null)
          res.setRefereeJ1(refereeEntry); 
      } 
      if (matchConfigurationDto.getRefereeJ2() != null) {
        RefereeEntry refereeEntry = this.refereeService.doGetCreateOrUpdateEntry(matchConfigurationDto.getRefereeJ2());
        if (refereeEntry != null)
          res.setRefereeJ2(refereeEntry); 
      } 
      if (matchConfigurationDto.getRefereeJ3() != null) {
        RefereeEntry refereeEntry = this.refereeService.doGetCreateOrUpdateEntry(matchConfigurationDto.getRefereeJ3());
        if (refereeEntry != null)
          res.setRefereeJ3(refereeEntry); 
      } 
      if (matchConfigurationDto.getRefereeTA() != null) {
        RefereeEntry refereeEntry = this.refereeService.doGetCreateOrUpdateEntry(matchConfigurationDto.getRefereeTA());
        if (refereeEntry != null)
          res.setRefereeTA(refereeEntry); 
      } 
      if (matchConfigurationDto.getRefereeCR() != null) {
        RefereeEntry refereeEntry = this.refereeService.doGetCreateOrUpdateEntry(matchConfigurationDto.getRefereeCR());
        if (refereeEntry != null)
          res.setRefereeCR(refereeEntry); 
      } 
      if (matchConfigurationDto.getRefereeRJ() != null) {
        RefereeEntry refereeEntry = this.refereeService.doGetCreateOrUpdateEntry(matchConfigurationDto.getRefereeRJ());
        if (refereeEntry != null)
          res.setRefereeRJ(refereeEntry); 
      } 
      res.isParaTkdMatchProperty().set(((matchConfigurationDto.getParaTkdMatch() != null) ? matchConfigurationDto.getParaTkdMatch() : Boolean.FALSE).booleanValue());
      res.differencialScoreProperty().set((matchConfigurationDto.getDifferencialScore() != null) ? matchConfigurationDto.getDifferencialScore().intValue() : 20);
      if (matchConfigurationDto.getMaxAllowedGamJeoms() != null) {
        res.setMaxAllowedGamJeoms(matchConfigurationDto.getMaxAllowedGamJeoms().intValue());
      } else {
        res.setMaxAllowedGamJeoms(0);
      } 
      if (matchConfigurationDto.getRoundsConfig() != null) {
        RoundsConfigEntry roundsConfigEntry = new RoundsConfigEntry();
        RoundsConfigDto roundsConfigDto = matchConfigurationDto.getRoundsConfig();
        roundsConfigEntry.fillByDto(roundsConfigDto);
        res.roundsConfigProperty().set(roundsConfigEntry);
      } else {
        throw new TkStrikeServiceException("No roundsConfig present!");
      } 
      res.setMatchVictoryCriteria(matchConfigurationDto.getMatchVictoryCriteria());
      res.setWtCompetitionDataProtocol(matchConfigurationDto.getWtCompetitionDataProtocol().booleanValue());
      res = createByEntry(res);
    } 
    return res;
  }
  
  @Transactional(readOnly = false)
  public MatchConfigurationEntry createByEntry(MatchConfigurationEntry matchConfigurationEntry) throws TkStrikeServiceException {
    MatchConfigurationEntry res = new MatchConfigurationEntry();
    MatchConfiguration matchConfiguration = create(matchConfigurationEntry);
    if (matchConfiguration != null)
      res.fillByEntity(matchConfiguration); 
    return res;
  }
  
  @Transactional(readOnly = false)
  public MatchConfiguration create(MatchConfigurationEntry matchConfigurationEntry) throws TkStrikeServiceException {
    if (matchConfigurationEntry != null)
      try {
        MatchConfiguration matchConfiguration = getByMatchNumber(matchConfigurationEntry.getMatchNumber());
        if (matchConfiguration == null)
          matchConfiguration = new MatchConfiguration(); 
        _transform(matchConfiguration, matchConfigurationEntry);
        matchConfiguration = (MatchConfiguration)this.matchConfigurationRepository.saveAndFlush(matchConfiguration);
        return matchConfiguration;
      } catch (Exception e) {
        throw new TkStrikeServiceException(e);
      }  
    return null;
  }
  
  @Transactional(readOnly = false)
  public MatchConfigurationEntry updateByEntry(MatchConfigurationEntry matchConfigurationEntry) throws TkStrikeServiceException {
    MatchConfigurationEntry res = new MatchConfigurationEntry();
    MatchConfiguration matchConfiguration = update(matchConfigurationEntry);
    if (matchConfiguration != null)
      res.fillByEntity(matchConfiguration); 
    return res;
  }
  
  @Transactional(readOnly = false)
  public void updateMatchIsStarted(String matchId) throws TkStrikeServiceException {
    MatchConfiguration matchConfiguration = getById(matchId);
    if (matchConfiguration != null) {
      matchConfiguration.setMatchStarted(Boolean.valueOf(true));
      this.matchConfigurationRepository.saveAndFlush(matchConfiguration);
    } 
  }
  
  @Transactional(readOnly = false)
  public MatchConfiguration update(MatchConfigurationEntry matchConfigurationEntry) throws TkStrikeServiceException {
    if (matchConfigurationEntry != null)
      try {
        MatchConfiguration matchConfiguration = getById(matchConfigurationEntry.getId());
        if (matchConfiguration != null) {
          _transform(matchConfiguration, matchConfigurationEntry);
          matchConfiguration = (MatchConfiguration)this.matchConfigurationRepository.saveAndFlush(matchConfiguration);
          return matchConfiguration;
        } 
      } catch (Exception e) {
        throw new TkStrikeServiceException(e);
      }  
    return null;
  }
  
  public Boolean existsByAthlete(String athleteId) throws TkStrikeServiceException {
    Boolean res = Boolean.valueOf(false);
    if (StringUtils.isNotBlank(athleteId)) {
      Long n = this.matchConfigurationRepository.getCountByAthlete(athleteId);
      res = Boolean.valueOf((n != null && n.longValue() > 0L));
    } 
    return res;
  }
  
  public Long countByPhaseId(String phaseId) throws TkStrikeServiceException {
    try {
      return this.matchConfigurationRepository.countByPhaseId(phaseId);
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  public Long countByBlueAthleteIdOrRedAthleteId(String blueAthleteId, String redAthleteId) throws TkStrikeServiceException {
    try {
      return this.matchConfigurationRepository.countByBlueAthleteIdOrRedAthleteId(blueAthleteId, redAthleteId);
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  public Long countByCategoryId(String categoryId) throws TkStrikeServiceException {
    try {
      return this.matchConfigurationRepository.countByCategoryId(categoryId);
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  public Long countByCategorySubCategoryId(String subCategoryId) throws TkStrikeServiceException {
    try {
      return this.matchConfigurationRepository.countByCategorySubCategoryId(subCategoryId);
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  public Boolean canDelete(String id) {
    return Boolean.valueOf(true);
  }
  
  @Transactional(readOnly = false)
  public void delete(String id) throws TkStrikeServiceException {
    this.matchLogService.deleteByMatchConfigurationId(id);
    this.matchLogHistoricalService.deleteByMatchConfigurationId(id);
    super.delete(id);
  }
  
  public List<MatchConfiguration> findByDates(Date startDate, Date endDate) throws TkStrikeServiceException {
    return this.matchConfigurationRepository.findByDates(startDate, endDate);
  }
  
  public List<MatchConfigurationEntry> findEntriesByDates(Date startDate, Date endDate) throws TkStrikeServiceException {
    List<MatchConfiguration> entities = findByDates(startDate, endDate);
    List<MatchConfigurationEntry> entries = new ArrayList<>();
    if (entities != null)
      for (MatchConfiguration entity : entities)
        entries.add(transform(entity));  
    return entries;
  }
  
  public List<MatchConfiguration> findNotStarted() throws TkStrikeServiceException {
    return this.matchConfigurationRepository.findNotStarted();
  }
  
  public List<MatchConfigurationEntry> findEntriesNotStarted() throws TkStrikeServiceException {
    List<MatchConfiguration> entities = findNotStarted();
    List<MatchConfigurationEntry> entries = new ArrayList<>();
    if (entities != null)
      for (MatchConfiguration entity : entities)
        entries.add(transform(entity));  
    return entries;
  }
  
  public List<MatchConfiguration> findStarted() throws TkStrikeServiceException {
    return this.matchConfigurationRepository.findStarted();
  }
  
  public List<MatchConfigurationEntry> findEntriesStarted() throws TkStrikeServiceException {
    List<MatchConfiguration> entities = findStarted();
    List<MatchConfigurationEntry> entries = new ArrayList<>();
    if (entities != null)
      for (MatchConfiguration entity : entities)
        entries.add(transform(entity));  
    return entries;
  }
  
  private void _transform(MatchConfiguration matchConfiguration, MatchConfigurationEntry matchConfigurationEntry) throws TkStrikeServiceException {
    BeanUtils.copyProperties(matchConfigurationEntry, matchConfiguration, new String[] { 
          "id", "version", "phase", "category", "blueAthlete", "redAthlete", "refereeCR", "refereeJ1", "refereeJ2", "refereeJ3", 
          "refereeTA", "refereeRJ" });
    Phase phase = this.phaseService.getById(matchConfigurationEntry.getPhase().getId());
    matchConfiguration.setPhase(phase);
    Category category = this.categoryService.getById(matchConfigurationEntry.getCategory().getId());
    matchConfiguration.setCategory(category);
    AthleteEntry blueAthleteEntry = matchConfigurationEntry.getBlueAthlete();
    Athlete blueAthlete = _getAthlete(blueAthleteEntry);
    matchConfiguration.setBlueAthlete(blueAthlete);
    AthleteEntry redAthleteEntry = matchConfigurationEntry.getRedAthlete();
    Athlete redAthlete = _getAthlete(redAthleteEntry);
    matchConfiguration.setRedAthlete(redAthlete);
    RoundsConfigEntry roundsConfigEntry = matchConfigurationEntry.getRoundsConfig();
    matchConfiguration.setRoundsConfig(_getRoundsConfig(roundsConfigEntry));
    if (matchConfigurationEntry.getRefereeCR() != null) {
      matchConfiguration.setRefereeCR(_getReferee(matchConfigurationEntry.getRefereeCR()));
    } else {
      matchConfiguration.setRefereeCR(null);
    } 
    if (matchConfigurationEntry.getRefereeRJ() != null) {
      matchConfiguration.setRefereeRJ(_getReferee(matchConfigurationEntry.getRefereeRJ()));
    } else {
      matchConfiguration.setRefereeRJ(null);
    } 
    if (matchConfigurationEntry.getRefereeTA() != null) {
      matchConfiguration.setRefereeTA(_getReferee(matchConfigurationEntry.getRefereeTA()));
    } else {
      matchConfiguration.setRefereeTA(null);
    } 
    if (matchConfigurationEntry.getRefereeJ1() != null) {
      matchConfiguration.setRefereeJ1(_getReferee(matchConfigurationEntry.getRefereeJ1()));
    } else {
      matchConfiguration.setRefereeJ1(null);
    } 
    if (matchConfigurationEntry.getRefereeJ2() != null) {
      matchConfiguration.setRefereeJ2(_getReferee(matchConfigurationEntry.getRefereeJ2()));
    } else {
      matchConfiguration.setRefereeJ2(null);
    } 
    if (matchConfigurationEntry.getRefereeJ3() != null) {
      matchConfiguration.setRefereeJ3(_getReferee(matchConfigurationEntry.getRefereeJ3()));
    } else {
      matchConfiguration.setRefereeJ3(null);
    } 
  }
  
  private Athlete _getAthlete(AthleteEntry athleteEntry) throws TkStrikeServiceException {
    Athlete athlete = null;
    if (athleteEntry != null)
      if (athleteEntry.getId() == null || StringUtils.isBlank(athleteEntry.getId())) {
        athlete = this.athleteService.createNew(athleteEntry.getScoreboardName(), athleteEntry.getWfId(), (String)null, (athleteEntry.getFlag() != null) ? athleteEntry.getFlag().getId() : null);
      } else {
        athlete = this.athleteService.update(athleteEntry.getId(), athleteEntry.getScoreboardName(), athleteEntry.getWfId(), athleteEntry.getOvrInternalId(), (athleteEntry.getFlag() != null) ? athleteEntry.getFlag().getId() : null);
      }  
    return athlete;
  }
  
  private Referee _getReferee(RefereeEntry refereeEntry) throws TkStrikeServiceException {
    if (refereeEntry != null)
      return this.refereeService.getById(refereeEntry.getId()); 
    return null;
  }
  
  private RoundsConfig _getRoundsConfig(RoundsConfigEntry roundsConfigEntry) {
    RoundsConfig roundsConfig = new RoundsConfig();
    if (roundsConfigEntry != null)
      BeanUtils.copyProperties(roundsConfigEntry, roundsConfig); 
    return roundsConfig;
  }
}
