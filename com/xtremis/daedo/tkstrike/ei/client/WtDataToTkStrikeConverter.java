package com.xtremis.daedo.tkstrike.ei.client;

import com.xtremis.daedo.tkstrike.om.combat.MatchWinner;
import com.xtremis.daedo.tkstrike.tools.ei.om.AthleteDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.CategoryDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchConfigurationDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchResultDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchVictoryCriteria;
import com.xtremis.daedo.tkstrike.tools.ei.om.RefereeDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.RoundsConfigDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeEventDto;
import com.xtremis.daedo.wtdata.model.Competitor;
import com.xtremis.daedo.wtdata.model.Event;
import com.xtremis.daedo.wtdata.model.Match;
import com.xtremis.daedo.wtdata.model.MatchAction;
import com.xtremis.daedo.wtdata.model.MatchConfiguration;
import com.xtremis.daedo.wtdata.model.MatchInternalResult;
import com.xtremis.daedo.wtdata.model.MatchResult;
import com.xtremis.daedo.wtdata.model.MatchScore;
import com.xtremis.daedo.wtdata.model.Participant;
import com.xtremis.daedo.wtdata.model.constants.Action;
import com.xtremis.daedo.wtdata.model.constants.ResultStatus;
import com.xtremis.daedo.wtdata.model.constants.ResultType;
import com.xtremis.daedo.wtdata.model.constants.Rules;
import java.beans.PropertyDescriptor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;

public final class WtDataToTkStrikeConverter {
  private static final Logger logger = Logger.getLogger(WtDataToTkStrikeConverter.class);
  
  private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX").withZone(ZoneId.systemDefault());
  
  private static final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
  
  public static MatchConfigurationDto convertMatch(Match match, Participant homeParticipant, Participant awayParticipant, Participant refJ1, Participant refJ2, Participant refJ3, Participant refCR, Participant refRJ, Participant refTA, Integer defaultMaxGamJeomAllowed) {
    MatchConfigurationDto dto = new MatchConfigurationDto();
    dto.setWtCompetitionDataProtocol(Boolean.TRUE);
    logger.info("Go to Convert Match based on WT OVR model");
    dto.setInternalId(match.getId());
    dto.setMatchNumber(match.getNumber());
    dto.setRingNumber("" + match.getMat());
    logger.info("InternalId, MatchNumber and RingNumber set");
    dto.setPhase(match.getPhase().toString());
    logger.info("Phase set");
    dto.setCategory(convertEvent(match.getEvent()));
    logger.info("Category converted success!");
    dto.getCategory().setBodyLevel(Integer.valueOf((match.getMatchConfiguration() != null) ? match.getMatchConfiguration().getThresholds().getBody().intValue() : 25));
    dto.getCategory().setHeadLevel(Integer.valueOf((match.getMatchConfiguration() != null) ? match.getMatchConfiguration().getThresholds().getHead().intValue() : 6));
    logger.info("Body and Head level set!");
    dto.setParaTkdMatch(Boolean.valueOf(match.getEvent().getDiscipline().toUpperCase().contains("PARA")));
    if (match.getMatchConfiguration().getMaxPenalties() != null) {
      dto.setMaxAllowedGamJeoms(match.getMatchConfiguration().getMaxPenalties());
    } else {
      dto.setMaxAllowedGamJeoms(defaultMaxGamJeomAllowed);
    } 
    if (match.getMatchConfiguration() != null) {
      dto.setRoundsConfig(createRoundsConfig(match.getMatchConfiguration()));
      dto.setDifferencialScore(match.getMatchConfiguration().getMaxDifference());
      dto.setMatchVictoryCriteria(MatchVictoryCriteria.CONVENTIONAL);
      if (match.getMatchConfiguration().getRules() != null && 
        Rules.BESTOF3.equals(match.getMatchConfiguration().getRules()))
        dto.setMatchVictoryCriteria(MatchVictoryCriteria.BESTOF3); 
      if (match.getMatchConfiguration().getVideoReplayQuota() != null) {
        dto.setBlueAthleteVideoQuota(match.getMatchConfiguration().getVideoReplayQuota().getHome());
        dto.setRedAthleteVideoQuota(match.getMatchConfiguration().getVideoReplayQuota().getAway());
      } 
    } 
    if (match.getHomeCompetitor() != null) {
      logger.info("Go to convert Competitor and Participant to BlueAthlete");
      dto.setBlueAthlete(convertCompetitor(match.getHomeCompetitor(), homeParticipant));
    } else {
      logger.warn("Competitor is null for Blue");
    } 
    if (match.getAwayCompetitor() != null) {
      logger.info("Go to convert Competitor and Participant to RedAthlete");
      dto.setRedAthlete(convertCompetitor(match.getAwayCompetitor(), awayParticipant));
    } else {
      logger.warn("Competitor is null for Red");
    } 
    if (match.getRefereeAssignment() != null && match.getRefereeAssignment().getRefJ1() != null && refJ1 != null) {
      logger.info("Go to convert Referee J1");
      dto.setRefereeJ1(convertRefereeFromParticipant(refJ1));
    } else {
      logger.warn("Referee J1 is null");
    } 
    if (match.getRefereeAssignment() != null && match.getRefereeAssignment().getRefJ2() != null && refJ2 != null) {
      logger.info("Go to convert Referee J2");
      dto.setRefereeJ2(convertRefereeFromParticipant(refJ2));
    } else {
      logger.warn("Referee J2 is null");
    } 
    if (match.getRefereeAssignment() != null && match.getRefereeAssignment().getRefJ3() != null && refJ3 != null) {
      logger.info("Go to convert Referee J3");
      dto.setRefereeJ3(convertRefereeFromParticipant(refJ3));
    } else {
      logger.warn("Referee J3 is null");
    } 
    if (match.getRefereeAssignment() != null && match.getRefereeAssignment().getRefCR() != null && refCR != null) {
      logger.info("Go to convert Referee CR");
      dto.setRefereeCR(convertRefereeFromParticipant(refCR));
    } else {
      logger.warn("Referee CR is null");
    } 
    if (match.getRefereeAssignment() != null && match.getRefereeAssignment().getRefTA() != null && refTA != null) {
      logger.info("Go to convert Referee TA");
      dto.setRefereeTA(convertRefereeFromParticipant(refTA));
    } else {
      logger.warn("Referee TA is null");
    } 
    if (match.getRefereeAssignment() != null && match.getRefereeAssignment().getRefRJ() != null && refRJ != null) {
      logger.info("Go to convert Referee RJ");
      dto.setRefereeRJ(convertRefereeFromParticipant(refRJ));
    } else {
      logger.warn("Referee RJ is null");
    } 
    return dto;
  }
  
  public static RoundsConfigDto createRoundsConfig(MatchConfiguration matchConfiguration) {
    RoundsConfigDto dto = new RoundsConfigDto();
    dto.setRounds(matchConfiguration.getRounds());
    dto.setRoundTimeMinutes(extractMinutesFromTime(matchConfiguration.getTiming().getRound()));
    dto.setRoundTimeSeconds(extractSecondsFromTime(matchConfiguration.getTiming().getRound()));
    dto.setRestTimeMinutes(extractMinutesFromTime(matchConfiguration.getTiming().getRest()));
    dto.setRestTimeSeconds(extractSecondsFromTime(matchConfiguration.getTiming().getRest()));
    dto.setKyeShiTimeMinutes(extractMinutesFromTime(matchConfiguration.getTiming().getInjury()));
    dto.setKyeShiTimeSeconds(extractSecondsFromTime(matchConfiguration.getTiming().getInjury()));
    dto.setGoldenPointEnabled(matchConfiguration.getGoldenPoint().getEnabled());
    dto.setGoldenPointTimeMinutes(extractMinutesFromTime(matchConfiguration.getGoldenPoint().getTime()));
    dto.setGoldenPointTimeSeconds(extractSecondsFromTime(matchConfiguration.getGoldenPoint().getTime()));
    return dto;
  }
  
  private static Integer extractMinutesFromTime(String time) {
    if (StringUtils.isNotBlank(time) && time.contains(":"))
      try {
        return Integer.valueOf(Integer.parseInt(StringUtils.substringBefore(time, ":")));
      } catch (Exception e) {
        return Integer.valueOf(0);
      }  
    return Integer.valueOf(0);
  }
  
  private static Integer extractSecondsFromTime(String time) {
    if (StringUtils.isNotBlank(time) && time.contains(":"))
      try {
        return Integer.valueOf(Integer.parseInt(StringUtils.substringAfter(time, ":")));
      } catch (Exception e) {
        return Integer.valueOf(0);
      }  
    return Integer.valueOf(0);
  }
  
  public static AthleteDto convertCompetitor(Competitor competitor, Participant participant) {
    AthleteDto dto = new AthleteDto();
    dto.setOvrInternalId(competitor.getId());
    dto.setWfId((participant != null && participant.getLicenseNumber() != null) ? participant.getLicenseNumber() : competitor.getId());
    dto.setFlagAbbreviation(competitor.getCountry());
    dto.setFlagName(null);
    dto.setFlagShowName(Boolean.valueOf(false));
    dto.setScoreboardName(competitor.getScoreboardName());
    BeanUtils.copyProperties(competitor, dto);
    if (participant != null) {
      myCopyProperties(participant, dto, new String[] { "birthDate", "gender" });
      if (participant.getBirthDate() != null)
        try {
          dto.setBirthDate(sdfDate.parse(participant.getBirthDate()));
        } catch (ParseException parseException) {} 
      if (participant.getGender() != null)
        dto.setGender(participant.getGender().toString()); 
    } 
    return dto;
  }
  
  public static String[] getNullPropertyNames(Object source) {
    BeanWrapperImpl beanWrapperImpl = new BeanWrapperImpl(source);
    PropertyDescriptor[] pds = beanWrapperImpl.getPropertyDescriptors();
    Set<String> emptyNames = new HashSet<>();
    for (PropertyDescriptor pd : pds) {
      Object srcValue = beanWrapperImpl.getPropertyValue(pd.getName());
      if (srcValue == null)
        emptyNames.add(pd.getName()); 
    } 
    String[] result = new String[emptyNames.size()];
    return emptyNames.<String>toArray(result);
  }
  
  public static void myCopyProperties(Object src, Object target, String... extra) {
    String[] ignore = extra;
    ignore = (String[])ArrayUtils.addAll((Object[])ignore, (Object[])getNullPropertyNames(src));
    BeanUtils.copyProperties(src, target, ignore);
  }
  
  public static RefereeDto convertRefereeFromParticipant(Participant participant) {
    RefereeDto res = new RefereeDto();
    res.setId(participant.getId());
    res.setCountry(participant.getCountry());
    res.setLicenseNumber(participant.getLicenseNumber());
    res.setScoreboardName(participant.getScoreboardName());
    return res;
  }
  
  public static CategoryDto convertEvent(Event event) {
    CategoryDto dto = new CategoryDto();
    dto.setName(event.getWeightCategory());
    dto.setSubCategory(event.getDivision());
    dto.setGender(event.getGender().toString());
    return dto;
  }
  
  public static MatchAction convertMatchConfiguration(MatchConfigurationDto matchConfiguration, String blueAthleteOvrInternalId, String redAthleteOvrInternalId) {
    MatchAction matchAction = new MatchAction();
    matchAction.setAction(Action.MATCH_LOADED);
    matchAction.setDescription(Action.MATCH_LOADED.toString());
    matchAction.setRound(Integer.valueOf(1));
    matchAction.setRoundTime(matchConfiguration.getRoundsConfig().getRoundTimeStr());
    matchAction.setTimestamp(dtf.format(Instant.now()));
    matchAction.setPosition(Integer.valueOf(1));
    matchAction.setScore(new MatchScore(Integer.valueOf(0), Integer.valueOf(0)));
    matchAction.setPenalties(new MatchScore(Integer.valueOf(0), Integer.valueOf(0)));
    Competitor home = new Competitor();
    home.setId(blueAthleteOvrInternalId);
    matchAction.setHomeCompetitor(home);
    Competitor away = new Competitor();
    away.setId(redAthleteOvrInternalId);
    matchAction.setAwayCompetitor(away);
    return matchAction;
  }
  
  public static MatchAction convertTkStrikeEvent(TkStrikeEventDto tkStrikeEvent, String blueAthleteOvrInternalId, String redAthleteOvrInternalId, boolean eventFromScoreboardEditor) {
    MatchAction matchAction = null;
    Action action = null;
    if ("START_MATCH".equals(tkStrikeEvent.getEventType())) {
      action = Action.MATCH_START;
    } else if ("START_ROUND".equals(tkStrikeEvent.getEventType())) {
      action = Action.ROUND_START;
    } else if ("ROUNDCOUNTDOWN_CHANGE".equals(tkStrikeEvent.getEventType()) || "GOLDENPOINTCOUNTDOWN_CHANGE".equals(tkStrikeEvent.getEventType())) {
      action = Action.MATCH_TIME;
    } else if ("TIMEOUT".equals(tkStrikeEvent.getEventType())) {
      action = Action.MATCH_TIMEOUT;
    } else if ("RESUME".equals(tkStrikeEvent.getEventType())) {
      action = Action.MATCH_RESUME;
    } else if ("END_ROUND".equals(tkStrikeEvent.getEventType())) {
      action = Action.ROUND_END;
    } else if ("MATCH_FINISHED".equals(tkStrikeEvent.getEventType())) {
      action = Action.MATCH_END;
    } else if ("BLUE_PUNCH_POINT".equals(tkStrikeEvent.getEventType())) {
      action = eventFromScoreboardEditor ? Action.ADJUST_SCORE : Action.SCORE_HOME_PUNCH;
    } else if ("BLUE_BODY_POINT".equals(tkStrikeEvent.getEventType())) {
      action = eventFromScoreboardEditor ? Action.ADJUST_SCORE : Action.SCORE_HOME_KICK;
    } else if ("BLUE_BODY_TECH_POINT".equals(tkStrikeEvent.getEventType())) {
      action = eventFromScoreboardEditor ? Action.ADJUST_SCORE : Action.SCORE_HOME_TKICK;
    } else if ("BLUE_HEAD_POINT".equals(tkStrikeEvent.getEventType())) {
      action = eventFromScoreboardEditor ? Action.ADJUST_SCORE : Action.SCORE_HOME_HEAD;
    } else if ("BLUE_HEAD_TECH_POINT".equals(tkStrikeEvent.getEventType())) {
      action = eventFromScoreboardEditor ? Action.ADJUST_SCORE : Action.SCORE_HOME_THEAD;
    } else if ("BLUE_ADD_GAME_JEON".equals(tkStrikeEvent.getEventType())) {
      action = eventFromScoreboardEditor ? Action.ADJUST_SCORE : Action.PENALTY_HOME;
    } else if ("RED_PUNCH_POINT".equals(tkStrikeEvent.getEventType())) {
      action = eventFromScoreboardEditor ? Action.ADJUST_SCORE : Action.SCORE_AWAY_PUNCH;
    } else if ("RED_BODY_POINT".equals(tkStrikeEvent.getEventType())) {
      action = eventFromScoreboardEditor ? Action.ADJUST_SCORE : Action.SCORE_AWAY_KICK;
    } else if ("RED_BODY_TECH_POINT".equals(tkStrikeEvent.getEventType())) {
      action = eventFromScoreboardEditor ? Action.ADJUST_SCORE : Action.SCORE_AWAY_TKICK;
    } else if ("RED_HEAD_POINT".equals(tkStrikeEvent.getEventType())) {
      action = eventFromScoreboardEditor ? Action.ADJUST_SCORE : Action.SCORE_AWAY_HEAD;
    } else if ("RED_HEAD_TECH_POINT".equals(tkStrikeEvent.getEventType())) {
      action = eventFromScoreboardEditor ? Action.ADJUST_SCORE : Action.SCORE_AWAY_THEAD;
    } else if ("RED_ADD_GAME_JEON".equals(tkStrikeEvent.getEventType())) {
      action = eventFromScoreboardEditor ? Action.ADJUST_SCORE : Action.PENALTY_AWAY;
    } else if ("BLUE_REMOVE_GAME_JEON".equals(tkStrikeEvent.getEventType()) || "RED_REMOVE_GAME_JEON"
      .equals(tkStrikeEvent.getEventType())) {
      action = Action.ADJUST_PENALTY;
    } else if ("CANCEL_GOLDENPOINT_POINT".equals(tkStrikeEvent.getEventType())) {
      action = Action.INVALIDATE_SCORE;
    } else if ("BLUE_VIDEO_REQUEST".equals(tkStrikeEvent.getEventType())) {
      action = Action.VR_HOME_REQUEST;
    } else if ("BLUE_VIDEO_QUOTA_CHANGED".equals(tkStrikeEvent.getEventType())) {
      action = Action.VR_HOME_REJECTED;
    } else if ("BLUE_VIDEO_QUOTA_ACCEPTED".equals(tkStrikeEvent.getEventType())) {
      action = Action.VR_HOME_ACCEPTED;
    } else if ("RED_VIDEO_REQUEST".equals(tkStrikeEvent.getEventType())) {
      action = Action.VR_AWAY_REQUEST;
    } else if ("RED_VIDEO_QUOTA_CHANGED".equals(tkStrikeEvent.getEventType())) {
      action = Action.VR_AWAY_REJECTED;
    } else if ("RED_VIDEO_QUOTA_ACCEPTED".equals(tkStrikeEvent.getEventType())) {
      action = Action.VR_AWAY_ACCEPTED;
    } 
    if (action != null) {
      matchAction = new MatchAction();
      matchAction.setAction(action);
      if (tkStrikeEvent.getHitlevel() != null) {
        matchAction.setHitlevel(tkStrikeEvent.getHitlevel());
      } else {
        matchAction.setHitlevel(null);
      } 
      matchAction.setDescription(action.toString());
      matchAction.setRound(tkStrikeEvent.getRoundNumber());
      if (tkStrikeEvent.getRoundTimestampStr() != null)
        matchAction.setRoundTime(StringUtils.substringBefore(tkStrikeEvent.getRoundTimestampStr(), ".")); 
      matchAction.setTimestamp(dtf.format(Instant.now()));
      matchAction.setPosition(Integer.valueOf(1));
      matchAction.setScore(new MatchScore(tkStrikeEvent.getBluePoints(), tkStrikeEvent.getRedPoints()));
      matchAction.setPenalties(new MatchScore(tkStrikeEvent.getBluePenalties(), tkStrikeEvent.getRedPenalties()));
      Competitor home = new Competitor();
      home.setId(blueAthleteOvrInternalId);
      matchAction.setHomeCompetitor(home);
      Competitor away = new Competitor();
      away.setId(redAthleteOvrInternalId);
      matchAction.setAwayCompetitor(away);
    } 
    return matchAction;
  }
  
  public static MatchResult convertMatchResultDto(MatchResultDto dto, ResultStatus resultStatus, String blueAthleteOvrInternalId, String redAthleteOvrInternalId) {
    MatchResult matchResult = new MatchResult();
    matchResult.setStatus(resultStatus);
    matchResult.setDescription(resultStatus.toString());
    matchResult.setRound(dto.getRoundFinish());
    MatchInternalResult matchInternalResult = new MatchInternalResult();
    if (MatchWinner.TIE.toString().equals(dto.getMatchWinnerColor())) {
      matchInternalResult.setHomeType(ResultType.TIE);
      matchInternalResult.setAwayType(ResultType.TIE);
    } else if (MatchWinner.BLUE.toString().equals(dto.getMatchWinnerColor())) {
      matchInternalResult.setHomeType(ResultType.WIN);
      matchInternalResult.setAwayType(ResultType.LOSS);
    } else if (MatchWinner.RED.toString().equals(dto.getMatchWinnerColor())) {
      matchInternalResult.setHomeType(ResultType.LOSS);
      matchInternalResult.setAwayType(ResultType.WIN);
    } 
    matchInternalResult.setStatus(resultStatus);
    matchInternalResult.setDecision(dto.getMatchFinalDecision());
    matchResult.setResult(matchInternalResult);
    matchResult.setScore(new MatchScore(dto.getBluePoints(), dto.getRedPoints()));
    matchResult.setPenalties(new MatchScore(dto.getBluePenalties(), dto.getRedPenalties()));
    matchResult.setTimestamp(dtf.format(Instant.now()));
    Competitor home = new Competitor();
    home.setId(blueAthleteOvrInternalId);
    matchResult.setHomeCompetitor(home);
    Competitor away = new Competitor();
    away.setId(redAthleteOvrInternalId);
    matchResult.setAwayCompetitor(away);
    return matchResult;
  }
}
