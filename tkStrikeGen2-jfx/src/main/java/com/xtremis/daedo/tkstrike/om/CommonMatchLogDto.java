package com.xtremis.daedo.tkstrike.om;

import com.xtremis.daedo.tkstrike.om.combat.FinalDecision;
import com.xtremis.daedo.tkstrike.om.combat.MatchWinner;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchVictoryCriteria;
import com.xtremis.daedo.tkstrike.tools.ei.om.RoundsConfigDto;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.beanutils.PropertyUtils;

public abstract class CommonMatchLogDto<RCD extends RoundsConfigDto> implements Serializable {
  private static final long serialVersionUID = -7288099943826827117L;
  
  private final SimpleDateFormat sdf4FileFullDate = new SimpleDateFormat("yyyyMMddHHmmss");
  
  private final SimpleDateFormat sdfFullDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");
  
  private final SimpleDateFormat sdfMatchTim = new SimpleDateFormat("mm:ss:SSS");
  
  final String[] headers = new String[] { 
      "matchStartTime", "matchEndTime", "matchNumber", "phaseName", "subCategoryName", "categoryGender", "categoryName", "numberOfJudges", "minBodyLevel", "bodySensorsEnabled", 
      "minHeadLevel", "headSensorsEnabled", "bodyPoints", "headPoints", "punchPoints", "bodyTechPoints", "headTechPoints", "overtimePoints", "overtimePenalties", "cellingScore", 
      "differencialScore", "nearMissLevel", "roundsConfig.rounds", "roundsConfig.roundTimeMinutes", "roundsConfig.roundTimeSeconds", "roundsConfig.kyeShiTimeMinutes", "roundsConfig.kyeShiTimeSeconds", "roundsConfig.restTimeMinutes", "roundsConfig.restTimeSeconds", "roundsConfig.goldenPointEnabled", 
      "roundsConfig.goldenPointTimeMinutes", "roundsConfig.goldenPointTimeSeconds", "matchWinner", "matchWinnerBy", "roundsWinners", "goldenPointTieBreakerInfo.haveTieBreaker", "goldenPointTieBreakerInfo.bluePunches", "goldenPointTieBreakerInfo.blueRoundWins", "goldenPointTieBreakerInfo.blueHits", "goldenPointTieBreakerInfo.bluePenalties", 
      "goldenPointTieBreakerInfo.redPunches", "goldenPointTieBreakerInfo.redRoundWins", "goldenPointTieBreakerInfo.redHits", "goldenPointTieBreakerInfo.redPenalties", "goldenPointTieBreakerInfo.bluePARATechPoints", "goldenPointTieBreakerInfo.redPARATechPoints", "maxAllowedGamJeoms", "matchResult", "matchVictoryCriteria", "paraTkdMatch" };
  
  private String id;
  
  private String matchConfigurationId;
  
  private Integer numberOfJudges;
  
  private Boolean bodySensorsEnabled;
  
  private Boolean headSensorsEnabled;
  
  private Long matchStartTime;
  
  private Long matchEndTime;
  
  private Integer bodyPoints;
  
  private Integer headPoints;
  
  private Integer punchPoints;
  
  private Integer bodyTechPoints;
  
  private Integer headTechPoints;
  
  private Integer overtimePoints;
  
  private Integer overtimePenalties;
  
  private Integer cellingScore;
  
  private Integer differencialScore;
  
  private Integer nearMissLevel;
  
  private String matchNumber;
  
  private String phaseId;
  
  private String phaseName;
  
  private RCD roundsConfig;
  
  private MatchWinner matchWinner;
  
  private FinalDecision matchWinnerBy;
  
  private String matchResult;
  
  private String roundsWinners;
  
  private GoldenPointTieBreakerInfoDto goldenPointTieBreakerInfo;
  
  private Integer maxAllowedGamJeoms;
  
  private MatchVictoryCriteria matchVictoryCriteria;
  
  private Boolean paraTkdMatch;
  
  public String getId() {
    return this.id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getMatchConfigurationId() {
    return this.matchConfigurationId;
  }
  
  public void setMatchConfigurationId(String matchConfigurationId) {
    this.matchConfigurationId = matchConfigurationId;
  }
  
  public Integer getNumberOfJudges() {
    return this.numberOfJudges;
  }
  
  public void setNumberOfJudges(Integer numberOfJudges) {
    this.numberOfJudges = numberOfJudges;
  }
  
  public Boolean getBodySensorsEnabled() {
    return this.bodySensorsEnabled;
  }
  
  public void setBodySensorsEnabled(Boolean bodySensorsEnabled) {
    this.bodySensorsEnabled = bodySensorsEnabled;
  }
  
  public Boolean getHeadSensorsEnabled() {
    return this.headSensorsEnabled;
  }
  
  public void setHeadSensorsEnabled(Boolean headSensorsEnabled) {
    this.headSensorsEnabled = headSensorsEnabled;
  }
  
  public Long getMatchStartTime() {
    return this.matchStartTime;
  }
  
  public void setMatchStartTime(Long matchStartTime) {
    this.matchStartTime = matchStartTime;
  }
  
  public Long getMatchEndTime() {
    return this.matchEndTime;
  }
  
  public void setMatchEndTime(Long matchEndTime) {
    this.matchEndTime = matchEndTime;
  }
  
  public Integer getBodyPoints() {
    return this.bodyPoints;
  }
  
  public void setBodyPoints(Integer bodyPoints) {
    this.bodyPoints = bodyPoints;
  }
  
  public Integer getHeadPoints() {
    return this.headPoints;
  }
  
  public void setHeadPoints(Integer headPoints) {
    this.headPoints = headPoints;
  }
  
  public Integer getPunchPoints() {
    return this.punchPoints;
  }
  
  public void setPunchPoints(Integer punchPoints) {
    this.punchPoints = punchPoints;
  }
  
  public Integer getBodyTechPoints() {
    return this.bodyTechPoints;
  }
  
  public void setBodyTechPoints(Integer bodyTechPoints) {
    this.bodyTechPoints = bodyTechPoints;
  }
  
  public Integer getHeadTechPoints() {
    return this.headTechPoints;
  }
  
  public void setHeadTechPoints(Integer headTechPoints) {
    this.headTechPoints = headTechPoints;
  }
  
  public Integer getOvertimePoints() {
    return this.overtimePoints;
  }
  
  public void setOvertimePoints(Integer overtimePoints) {
    this.overtimePoints = overtimePoints;
  }
  
  public Integer getOvertimePenalties() {
    return this.overtimePenalties;
  }
  
  public void setOvertimePenalties(Integer overtimePenalties) {
    this.overtimePenalties = overtimePenalties;
  }
  
  public Integer getCellingScore() {
    return this.cellingScore;
  }
  
  public void setCellingScore(Integer cellingScore) {
    this.cellingScore = cellingScore;
  }
  
  public Integer getDifferencialScore() {
    return this.differencialScore;
  }
  
  public void setDifferencialScore(Integer differencialScore) {
    this.differencialScore = differencialScore;
  }
  
  public Integer getNearMissLevel() {
    return this.nearMissLevel;
  }
  
  public void setNearMissLevel(Integer nearMissLevel) {
    this.nearMissLevel = nearMissLevel;
  }
  
  public String getMatchNumber() {
    return this.matchNumber;
  }
  
  public void setMatchNumber(String matchNumber) {
    this.matchNumber = matchNumber;
  }
  
  public String getPhaseId() {
    return this.phaseId;
  }
  
  public void setPhaseId(String phaseId) {
    this.phaseId = phaseId;
  }
  
  public String getPhaseName() {
    return this.phaseName;
  }
  
  public void setPhaseName(String phaseName) {
    this.phaseName = phaseName;
  }
  
  public RCD getRoundsConfig() {
    return this.roundsConfig;
  }
  
  public void setRoundsConfig(RCD roundsConfig) {
    this.roundsConfig = roundsConfig;
  }
  
  public MatchWinner getMatchWinner() {
    return this.matchWinner;
  }
  
  public void setMatchWinner(MatchWinner matchWinner) {
    this.matchWinner = matchWinner;
  }
  
  public FinalDecision getMatchWinnerBy() {
    return this.matchWinnerBy;
  }
  
  public void setMatchWinnerBy(FinalDecision matchWinnerBy) {
    this.matchWinnerBy = matchWinnerBy;
  }
  
  public String getMatchResult() {
    return this.matchResult;
  }
  
  public void setMatchResult(String matchResult) {
    this.matchResult = matchResult;
  }
  
  public String getRoundsWinners() {
    return this.roundsWinners;
  }
  
  public void setRoundsWinners(String roundsWinners) {
    this.roundsWinners = roundsWinners;
  }
  
  public GoldenPointTieBreakerInfoDto getGoldenPointTieBreakerInfo() {
    return this.goldenPointTieBreakerInfo;
  }
  
  public void setGoldenPointTieBreakerInfo(GoldenPointTieBreakerInfoDto goldenPointTieBreakerInfo) {
    this.goldenPointTieBreakerInfo = goldenPointTieBreakerInfo;
  }
  
  public Integer getMaxAllowedGamJeoms() {
    return this.maxAllowedGamJeoms;
  }
  
  public void setMaxAllowedGamJeoms(Integer maxAllowedGamJeoms) {
    this.maxAllowedGamJeoms = maxAllowedGamJeoms;
  }
  
  public MatchVictoryCriteria getMatchVictoryCriteria() {
    return this.matchVictoryCriteria;
  }
  
  public void setMatchVictoryCriteria(MatchVictoryCriteria matchVictoryCriteria) {
    this.matchVictoryCriteria = matchVictoryCriteria;
  }
  
  public Boolean getParaTkdMatch() {
    return this.paraTkdMatch;
  }
  
  public void setParaTkdMatch(Boolean paraTkdMatch) {
    this.paraTkdMatch = paraTkdMatch;
  }
  
  public String[] getCSVHeaders() {
    return this.headers;
  }
  
  public String[] getCSVValues() {
    String[] headers4Work = getCSVHeaders();
    String[] values = new String[headers4Work.length];
    for (int i = 0; i < headers4Work.length; i++) {
      String header = headers4Work[i];
      String value = "";
      try {
        Object obValue = PropertyUtils.getProperty(this, header);
        if ("matchStartTime".equals(header) || "matchEndTime".equals(header)) {
          value = this.sdfFullDate.format(new Date(((Long)obValue).longValue()));
        } else {
          value = (obValue != null) ? obValue.toString() : "";
        } 
      } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|NoSuchMethodException e) {
        value = "";
      } 
      values[i] = value;
    } 
    return values;
  }
}
