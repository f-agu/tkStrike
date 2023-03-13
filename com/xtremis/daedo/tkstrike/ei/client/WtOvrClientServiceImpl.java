package com.xtremis.daedo.tkstrike.ei.client;

import com.xtremis.daedo.tkstrike.service.AppStatusWorker;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchConfigurationDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchResultDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeEventDto;
import com.xtremis.daedo.wtdata.client.MatchClient;
import com.xtremis.daedo.wtdata.model.Match;
import com.xtremis.daedo.wtdata.model.MatchAction;
import com.xtremis.daedo.wtdata.model.MatchResult;
import com.xtremis.daedo.wtdata.model.Participant;
import com.xtremis.daedo.wtdata.model.constants.ResultStatus;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WtOvrClientServiceImpl implements WtOvrClientService {
  private static final Logger logger = Logger.getLogger("EXTERNAL_INTEGRATION");
  
  private final MatchClient matchClient;
  
  private final AppStatusWorker appStatusWorker;
  
  private final SimpleIntegerProperty positionCounter = new SimpleIntegerProperty(this, "positionCounter", 1);
  
  @Value("${tkStrike.maxGamJeomsAllowed}")
  private Integer maxGamJeomsAllowed;
  
  @Autowired
  public WtOvrClientServiceImpl(MatchClient matchClient, AppStatusWorker appStatusWorker) {
    this.matchClient = matchClient;
    this.appStatusWorker = appStatusWorker;
  }
  
  public void resetCounter() {
    this.positionCounter.setValue(Integer.valueOf(1));
  }
  
  public Integer getCurrentCounter() {
    return Integer.valueOf(this.positionCounter.get());
  }
  
  public Boolean doPing(String ovrUrl, String xApiKey) throws TkStrikeServiceException {
    if (StringUtils.isNotBlank(ovrUrl))
      try {
        return this.matchClient.doPing(ovrUrl, xApiKey).get();
      } catch (Exception e) {
        logger.error("Exception calling status", e);
        this.appStatusWorker.informErrorWithExternalService();
        throw new TkStrikeServiceException(e);
      }  
    return null;
  }
  
  public MatchConfigurationDto getWtOvrMatch(String ovrUrl, String xApiKey, String matchId) throws TkStrikeServiceException {
    if (StringUtils.isNotBlank(ovrUrl) && StringUtils.isNotBlank(matchId))
      try {
        Match match = this.matchClient.getMatch(ovrUrl, xApiKey, matchId).get();
        return _transformMatchToMatchConfiguration(ovrUrl, xApiKey, match);
      } catch (Exception e) {
        logger.error("Exception calling getMatch", e);
        this.appStatusWorker.informErrorWithExternalService();
        throw new TkStrikeServiceException(e);
      }  
    return null;
  }
  
  public List<MatchConfigurationDto> findWtOvrMatches(String ovrUrl, String xApiKey, Integer matNumber) throws TkStrikeServiceException {
    List<MatchConfigurationDto> res = new ArrayList<>();
    if (StringUtils.isNotBlank(ovrUrl) && matNumber != null)
      try {
        List<Match> matches = this.matchClient.getMatches(ovrUrl, xApiKey, matNumber).get();
        for (Match match : matches) {
          logger.info("Match with id " + match.getId());
          res.add(_transformMatchToMatchConfiguration(ovrUrl, xApiKey, match));
        } 
      } catch (Exception e) {
        logger.error("Exception calling getMatches", e);
        this.appStatusWorker.informErrorWithExternalService();
        throw new TkStrikeServiceException(e);
      }  
    return res;
  }
  
  private MatchConfigurationDto _transformMatchToMatchConfiguration(String ovrUrl, String xApiKey, Match match) throws TkStrikeServiceException {
    logger.info("Go to transform WT-OVR Match to TkStrike standard");
    MatchConfigurationDto res = null;
    Participant homeParticipant = null;
    Participant awayParticipant = null;
    if (match != null && StringUtils.isNotBlank(ovrUrl))
      try {
        if (match.getHomeCompetitor() != null) {
          logger.info("Go to get HomeParticipant with id " + match.getHomeCompetitor().getId() + " based on Competitor");
          try {
            homeParticipant = this.matchClient.getParticipant(ovrUrl, xApiKey, match.getHomeCompetitor().getId()).get();
          } catch (Exception e) {
            logger.warn("Exception getting HomeParticipant", e);
            homeParticipant = null;
          } 
        } 
        if (match.getAwayCompetitor() != null) {
          logger.info("Go to get AwayCompetitor with id " + match.getAwayCompetitor().getId() + " based on Competitor");
          try {
            awayParticipant = this.matchClient.getParticipant(ovrUrl, xApiKey, match.getAwayCompetitor().getId()).get();
          } catch (Exception e) {
            logger.warn("Exception getting AwayParticipant", e);
            homeParticipant = null;
          } 
        } 
        Participant refCR = null;
        Participant refJ1 = null;
        Participant refJ2 = null;
        Participant refJ3 = null;
        Participant refTA = null;
        Participant refRJ = null;
        if (match.getRefereeAssignment() != null) {
          logger.info("Go to get Referees...");
          if (match.getRefereeAssignment().getRefCR() != null)
            try {
              refCR = this.matchClient.getParticipant(ovrUrl, xApiKey, match.getRefereeAssignment().getRefCR().getId()).get();
            } catch (Exception e) {
              logger.warn("Exception getting Referee RefCR", e);
              refCR = null;
            }  
          if (match.getRefereeAssignment().getRefJ1() != null)
            try {
              refJ1 = this.matchClient.getParticipant(ovrUrl, xApiKey, match.getRefereeAssignment().getRefJ1().getId()).get();
            } catch (Exception e) {
              logger.warn("Exception getting Referee RefJ1", e);
              refJ1 = null;
            }  
          if (match.getRefereeAssignment().getRefJ2() != null)
            try {
              refJ2 = this.matchClient.getParticipant(ovrUrl, xApiKey, match.getRefereeAssignment().getRefJ2().getId()).get();
            } catch (Exception e) {
              logger.warn("Exception getting Referee RefJ2", e);
              refJ2 = null;
            }  
          if (match.getRefereeAssignment().getRefJ3() != null)
            try {
              refJ3 = this.matchClient.getParticipant(ovrUrl, xApiKey, match.getRefereeAssignment().getRefJ3().getId()).get();
            } catch (Exception e) {
              logger.warn("Exception getting Referee RefJ3", e);
              refJ3 = null;
            }  
          if (match.getRefereeAssignment().getRefTA() != null)
            try {
              refTA = this.matchClient.getParticipant(ovrUrl, xApiKey, match.getRefereeAssignment().getRefTA().getId()).get();
            } catch (Exception e) {
              logger.warn("Exception getting Referee RefTA", e);
              refTA = null;
            }  
          if (match.getRefereeAssignment().getRefRJ() != null)
            try {
              refRJ = this.matchClient.getParticipant(ovrUrl, xApiKey, match.getRefereeAssignment().getRefRJ().getId()).get();
            } catch (Exception e) {
              logger.warn("Exception getting Referee RefRJ", e);
              refRJ = null;
            }  
        } 
        res = WtDataToTkStrikeConverter.convertMatch(match, homeParticipant, awayParticipant, refJ1, refJ2, refJ3, refCR, refRJ, refTA, this.maxGamJeomsAllowed);
      } catch (Exception e) {
        logger.error("Exception calling _transformMatchToMatchConfiguration", e);
        this.appStatusWorker.informErrorWithExternalService();
        throw new TkStrikeServiceException(e);
      }  
    return res;
  }
  
  public Boolean sendMatchLoadedAction(String ovrUrl, String xApiKey, String matchId, String blueAthleteOvrInternalId, String redAthleteOvrInternalId, MatchConfigurationDto matchConfiguration) throws TkStrikeServiceException {
    MatchAction matchAction = WtDataToTkStrikeConverter.convertMatchConfiguration(matchConfiguration, blueAthleteOvrInternalId, redAthleteOvrInternalId);
    if (matchAction != null)
      try {
        matchAction.setPosition(Integer.valueOf(this.positionCounter.get()));
        this.positionCounter.setValue(Integer.valueOf(this.positionCounter.get() + 1));
        return this.matchClient.sendMatchAction(ovrUrl, xApiKey, (matchId != null) ? matchId : "1", matchAction).get();
      } catch (Exception e) {
        logger.warn("Exception calling sendMatchLoadedAction", e);
        this.appStatusWorker.informErrorWithExternalService();
      }  
    return null;
  }
  
  public Boolean sendMatchAction(String ovrUrl, String xApiKey, String matchId, String blueAthleteOvrInternalId, String redAthleteOvrInternalId, TkStrikeEventDto tkStrikeEventDto, boolean eventFromScoreboardEditor) throws TkStrikeServiceException {
    MatchAction matchAction = WtDataToTkStrikeConverter.convertTkStrikeEvent(tkStrikeEventDto, blueAthleteOvrInternalId, redAthleteOvrInternalId, eventFromScoreboardEditor);
    if (matchAction != null) {
      matchAction.setPosition(Integer.valueOf(this.positionCounter.get()));
      this.positionCounter.setValue(Integer.valueOf(this.positionCounter.get() + 1));
      try {
        return this.matchClient.sendMatchAction(ovrUrl, xApiKey, (matchId != null) ? matchId : "1", matchAction).get();
      } catch (Exception e) {
        logger.warn("Exception calling sendMatchAction " + matchAction.getAction().toString(), e);
        this.appStatusWorker.informErrorWithExternalService();
      } 
    } 
    return Boolean.FALSE;
  }
  
  public Boolean sendMatchResult(String ovrUrl, String xApiKey, String matchId, MatchResultDto matchResult, ResultStatus resultStatus, String blueAthleteOvrInternalId, String redAthleteOvrInternalId) throws TkStrikeServiceException {
    MatchResult result = WtDataToTkStrikeConverter.convertMatchResultDto(matchResult, resultStatus, blueAthleteOvrInternalId, redAthleteOvrInternalId);
    try {
      result.setPosition(Integer.valueOf(this.positionCounter.get()));
      this.positionCounter.setValue(Integer.valueOf(this.positionCounter.get() + 1));
      return this.matchClient.sendMatchResult(ovrUrl, xApiKey, (matchId != null) ? matchId : "1", result).get();
    } catch (Exception e) {
      logger.warn("Exception calling sendMatchResult", e);
      this.appStatusWorker.informErrorWithExternalService();
      return Boolean.FALSE;
    } 
  }
}
