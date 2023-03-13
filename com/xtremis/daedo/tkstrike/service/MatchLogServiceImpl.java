package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.om.CommonMatchLogDto;
import com.xtremis.daedo.tkstrike.om.MatchLogDto;
import com.xtremis.daedo.tkstrike.om.MatchLogItemDto;
import com.xtremis.daedo.tkstrike.orm.model.MatchLog;
import com.xtremis.daedo.tkstrike.orm.model.MatchLogItem;
import com.xtremis.daedo.tkstrike.orm.model.MatchLogItemEntity;
import com.xtremis.daedo.tkstrike.orm.model.MatchLogItemType;
import com.xtremis.daedo.tkstrike.orm.model.RoundsConfig;
import com.xtremis.daedo.tkstrike.orm.repository.DefaultMatchLogItemRepository;
import com.xtremis.daedo.tkstrike.orm.repository.DefaultMatchLogRepository;
import com.xtremis.daedo.tkstrike.orm.repository.MatchLogItemRepository;
import com.xtremis.daedo.tkstrike.orm.repository.MatchLogRepository;
import com.xtremis.daedo.tkstrike.ui.model.IMatchConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.IRulesEntry;
import com.xtremis.daedo.tkstrike.ui.model.MatchConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.RoundsConfigEntry;
import com.xtremis.daedo.tkstrike.utils.MatchLogExporterUtil;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MatchLogServiceImpl extends BaseMatchLogService<MatchLogDto, MatchLogItemDto, MatchLogTransformerImpl, MatchLogItemTransformerImpl, MatchLogRepository, MatchLog, MatchLogItem, MatchLogItemRepository> implements MatchLogService {
  private final MatchLogTransformer matchLogTransformer;
  
  private final MatchLogItemTransformer matchLogItemTransformer;
  
  private final MatchLogRepository matchLogRepository;
  
  private final MatchLogItemRepository matchLogItemRepository;
  
  private final PhaseService phaseService;
  
  private final CategoryService categoryService;
  
  private final AthleteService athleteService;
  
  @Value("${tkStrike.maxGamJeomsAllowed}")
  private Integer maxGamJeomsAllowed;
  
  private final SimpleDateFormat sdfFullDate;
  
  private final SimpleDateFormat sdfMatchTim;
  
  @Autowired
  public MatchLogServiceImpl(MatchLogExporterUtil matchLogExporterUtil, MatchLogTransformer matchLogTransformer, MatchLogItemTransformer matchLogItemTransformer, MatchLogRepository matchLogRepository, MatchLogItemRepository matchLogItemRepository, PhaseService phaseService, CategoryService categoryService, AthleteService athleteService) {
    super(matchLogExporterUtil);
    this.sdfFullDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");
    this.sdfMatchTim = new SimpleDateFormat("mm:ss:SSS");
    this.matchLogTransformer = matchLogTransformer;
    this.matchLogItemTransformer = matchLogItemTransformer;
    this.matchLogRepository = matchLogRepository;
    this.matchLogItemRepository = matchLogItemRepository;
    this.phaseService = phaseService;
    this.categoryService = categoryService;
    this.athleteService = athleteService;
  }
  
  MatchLogTransformerImpl getMatchLogTransformer() {
    return (MatchLogTransformerImpl)this.matchLogTransformer;
  }
  
  MatchLogItemTransformerImpl getMatchLogItemTransformer() {
    return (MatchLogItemTransformerImpl)this.matchLogItemTransformer;
  }
  
  MatchLogRepository getMatchLogRepository() {
    return this.matchLogRepository;
  }
  
  MatchLogItemRepository getMatchLogItemRepository() {
    return this.matchLogItemRepository;
  }
  
  MatchLogItem newMatchLogItemEntity() {
    return new MatchLogItem();
  }
  
  public MatchLogDto createNew(IMatchConfigurationEntry iMatchConfigurationEntry, IRulesEntry rulesEntry, Integer judgesNumber, Boolean bodySensorsEnabled, Boolean headSensorsEnabled) throws TkStrikeServiceException {
    MatchLog matchLog = null;
    MatchConfigurationEntry matchConfigurationEntry = (MatchConfigurationEntry)iMatchConfigurationEntry;
    if (matchConfigurationEntry != null && StringUtils.isNotBlank(matchConfigurationEntry.getId()) && matchConfigurationEntry.getPhase() != null && StringUtils.isNotBlank(matchConfigurationEntry.getPhase().getId()) && matchConfigurationEntry.getCategory() != null && StringUtils.isNotBlank(matchConfigurationEntry.getCategory().getId()) && matchConfigurationEntry.getRedAthlete() != null && StringUtils.isNotBlank(matchConfigurationEntry.getRedAthlete().getId()) && matchConfigurationEntry.getBlueAthlete() != null && StringUtils.isNotBlank(matchConfigurationEntry.getBlueAthlete().getId())) {
      matchLog = new MatchLog();
      BeanUtils.copyProperties(matchConfigurationEntry, matchLog, new String[] { "id", "version" });
      matchLog.setMatchConfigurationId(matchConfigurationEntry.getId());
      matchLog.setPhase(this.phaseService.getById(matchConfigurationEntry.getPhase().getId()));
      matchLog.setCategory(this.categoryService.getById(matchConfigurationEntry.getCategory().getId()));
      matchLog.setBlueAthlete(this.athleteService.getById(matchConfigurationEntry.getBlueAthlete().getId()));
      matchLog.setRedAthlete(this.athleteService.getById(matchConfigurationEntry.getRedAthlete().getId()));
      matchLog.setMinBodyLevel(Integer.valueOf(matchConfigurationEntry.getCategory().getBodyLevel()));
      matchLog.setMinHeadLevel(Integer.valueOf(matchConfigurationEntry.getCategory().getHeadLevel()));
      matchLog.setNumberOfJudges(judgesNumber);
      matchLog.setBodySensorsEnabled(bodySensorsEnabled);
      matchLog.setHeadSensorsEnabled(headSensorsEnabled);
      if (matchConfigurationEntry.getMaxAllowedGamJeoms() == null || matchConfigurationEntry.getMaxAllowedGamJeoms().equals(Integer.valueOf(0))) {
        matchLog.setMaxAllowedGamJeoms(this.maxGamJeomsAllowed);
      } else {
        matchLog.setMaxAllowedGamJeoms(matchConfigurationEntry.getMaxAllowedGamJeoms());
      } 
      matchLog.setMatchVictoryCriteria(matchConfigurationEntry.getMatchVictoryCriteria());
      if (rulesEntry != null) {
        BeanUtils.copyProperties(rulesEntry, matchLog, new String[] { "id", "version", "differencialScore", "matchVictoryCriteria" });
        if (matchConfigurationEntry.isParaTkdMatch())
          matchLog.setCellingScore(Integer.valueOf(rulesEntry.getParaCellingScore())); 
        RoundsConfigEntry roundsConfigEntry = (RoundsConfigEntry)rulesEntry.getRoundsConfig();
        RoundsConfig roundsConfig = new RoundsConfig();
        BeanUtils.copyProperties(roundsConfigEntry, roundsConfig, new String[] { "id", "version" });
        matchLog.setRoundsConfig(roundsConfig);
      } 
      try {
        matchLog = (MatchLog)this.matchLogRepository.saveAndFlush(matchLog);
      } catch (Exception e) {
        throw new TkStrikeServiceException(e);
      } 
    } 
    return (matchLog != null) ? getMatchLogTransformer().transferToDto(matchLog) : null;
  }
  
  @Transactional(readOnly = false)
  public MatchLogDto updateMatchLogMinBodyLevel(String matchLogId, Integer newMinBodyLevel) throws TkStrikeServiceException {
    MatchLog res = _getById(matchLogId);
    if (newMinBodyLevel != null) {
      res.setMinBodyLevel(newMinBodyLevel);
      res = _update(res);
    } 
    return getMatchLogTransformer().transferToDto(res);
  }
  
  @Transactional(readOnly = false)
  public MatchLogDto updateMatchLogMinHeadLevel(String matchLogId, Integer newMinHeadLevel) throws TkStrikeServiceException {
    MatchLog res = _getById(matchLogId);
    if (newMinHeadLevel != null) {
      res.setMinHeadLevel(newMinHeadLevel);
      res = _update(res);
    } 
    return getMatchLogTransformer().transferToDto(res);
  }
  
  public Comparator<MatchLogItemDto> getComparator4Items() {
    return new Comparator<MatchLogItemDto>() {
        public int compare(MatchLogItemDto o1, MatchLogItemDto o2) {
          int res = Integer.valueOf(o1.getRoundNumber().intValue()).compareTo(o2.getRoundNumber());
          if (res == 0) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTimeInMillis(o1.getRoundTime().longValue());
            Calendar cal2 = Calendar.getInstance();
            cal2.setTimeInMillis(o2.getRoundTime().longValue());
            res = Integer.compare(cal2.get(12), cal1.get(12));
            if (res == 0) {
              res = Integer.compare(cal2.get(13), cal1.get(13));
              if (res == 0) {
                res = Integer.compare(cal2.get(14), cal1.get(14));
                if (res == 0) {
                  Integer o1Type = Integer.valueOf(MatchLogItemType.MATCH_FINISHED.equals(o1.getMatchLogItemType()) ? 3 : (MatchLogItemType.MATCH_FINAL_NEEDS_CONFIRM_DECISION.equals(o1.getMatchLogItemType()) ? 2 : ((!MatchLogItemType.CANCEL_GOLDENPOINT_POINT.equals(o1.getMatchLogItemType()) && o1.getMatchLogItemType().toString().endsWith("POINT")) ? 1 : 0)));
                  Integer o2Type = Integer.valueOf(MatchLogItemType.MATCH_FINISHED.equals(o2.getMatchLogItemType()) ? 3 : (MatchLogItemType.MATCH_FINAL_NEEDS_CONFIRM_DECISION.equals(o2.getMatchLogItemType()) ? 2 : ((!MatchLogItemType.CANCEL_GOLDENPOINT_POINT.equals(o2.getMatchLogItemType()) && o2.getMatchLogItemType().toString().endsWith("POINT")) ? 1 : 0)));
                  res = o1Type.compareTo(o2Type);
                } 
              } 
            } 
          } 
          return res;
        }
      };
  }
}
