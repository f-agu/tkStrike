package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.om.CommonMatchLogDto;
import com.xtremis.daedo.tkstrike.om.GoldenPointTieBreakerInfoDto;
import com.xtremis.daedo.tkstrike.om.MatchLogDto;
import com.xtremis.daedo.tkstrike.om.MatchLogItemDto;
import com.xtremis.daedo.tkstrike.om.combat.FinalDecision;
import com.xtremis.daedo.tkstrike.om.combat.MatchWinner;
import com.xtremis.daedo.tkstrike.om.xls.DataTableRoundInfoItem;
import com.xtremis.daedo.tkstrike.orm.model.Gender;
import com.xtremis.daedo.tkstrike.orm.model.MatchLogItemType;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchVictoryCriteria;
import com.xtremis.daedo.tkstrike.tools.ei.om.RoundsConfigDto;
import com.xtremis.daedo.tkstrike.ui.model.AthleteEntry;
import com.xtremis.daedo.tkstrike.ui.model.CategoryEntry;
import com.xtremis.daedo.tkstrike.ui.model.PhaseEntry;
import com.xtremis.daedo.tkstrike.ui.scene.FlagEntry;
import com.xtremis.daedo.tkstrike.utils.MatchLogExporterUtil;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.sql.DataSource;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class MatchLogHistoricalServiceImpl extends CommonMatchLogHistoricalServiceImpl<MatchLogDto, MatchLogItemDto, MatchLogService> implements MatchLogHistoricalService {
  private static final SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
  
  private static final DecimalFormat df = new DecimalFormat("00");
  
  @Autowired
  private DataSource dataSource;
  
  @Autowired
  private MatchLogService matchLogService;
  
  @Autowired
  private PhaseService phaseService;
  
  @Autowired
  private CategoryService categoryService;
  
  @Autowired
  private AthleteService athleteService;
  
  @Autowired
  private MatchLogExporterUtil matchLogExporterUtil;
  
  @Autowired
  private ExternalConfigService externalConfigService;
  
  @Autowired
  private FlagService flagService;
  
  @Autowired
  public MatchLogHistoricalServiceImpl(DataSource dataSource, ExternalConfigService externalConfigService, MatchLogExporterUtil matchLogExporterUtil) {
    super(dataSource, externalConfigService, matchLogExporterUtil);
  }
  
  MatchLogService getMatchLogService() {
    return this.matchLogService;
  }
  
  RowMapper<MatchLogDto> getMatchLogRowMapper() {
    return new MatchLogDtoRowMapper();
  }
  
  RowMapper<MatchLogItemDto> getMatchLogItemRowMapper() {
    return new MatchLogItemEntryRowMapper();
  }
  
  String getHistoMatchLogTableName() {
    return "TKS_HISTO_MATCH_LOG";
  }
  
  String getHistoMatchLogItemTableName() {
    return "TKS_HISTO_MATCH_LOG_ITEM";
  }
  
  public void generateAggregateCSVReport2DefaultDirectory(String phaseId, String subCategoryId, Gender gender, String categoryId, List<String> athleteIds, Date startDate, Date endDate) throws TkStrikeServiceException {
    String[][] defaultAggregatted = new String[2][20];
    (new String[20])[0] = "TotalHeadHits";
    (new String[20])[1] = "TotalBodyHits";
    (new String[20])[2] = "TotalHeadValidHits";
    (new String[20])[3] = "TotalBodyValidHits";
    (new String[20])[4] = "TotalHeadTech";
    (new String[20])[5] = "TotalBodyTech";
    (new String[20])[6] = "TotalPunch";
    (new String[20])[7] = "SumPointsBody";
    (new String[20])[8] = "SumPointsBodyTech";
    (new String[20])[9] = "SumPointsHead";
    (new String[20])[10] = "SumPointsHeadTech";
    (new String[20])[11] = "SumPointsPunch";
    (new String[20])[12] = "SumPointsKyongGo";
    (new String[20])[13] = "SumPointsGamJeom";
    (new String[20])[14] = "TotalVideoReplayRequests";
    (new String[20])[15] = "TotalVideoReplayRejecteds";
    (new String[20])[16] = "TotalVideoReplayApprovedByHeadPoint";
    (new String[20])[17] = "TotalVideoReplayApprovedByHeadTechPoint";
    (new String[20])[18] = "TotalVideoReplayApprovedByBodyTechPoint";
    (new String[20])[19] = "TotalVideoReplayApprovedByPenalty";
    defaultAggregatted[0] = new String[20];
    (new String[20])[0] = "" + 
      getTotalHeadHits(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    (new String[20])[1] = "" + 
      getTotalBodyHits(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    (new String[20])[2] = "" + 
      getTotalHeadValidHits(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    (new String[20])[3] = "" + 
      getTotalBodyValidHits(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    (new String[20])[4] = "" + 
      getTotalHeadTech(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    (new String[20])[5] = "" + 
      getTotalBodyTech(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    (new String[20])[6] = "" + 
      getTotalPunch(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    (new String[20])[7] = "" + 
      getSumPointsBody(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    (new String[20])[8] = "" + 
      getSumPointsBodyTech(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    (new String[20])[9] = "" + 
      getSumPointsHead(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    (new String[20])[10] = "" + 
      getSumPointsHeadTech(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    (new String[20])[11] = "" + 
      getSumPointsPunch(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    (new String[20])[12] = "" + 
      getSumPointsKyongGo(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    (new String[20])[13] = "" + 
      getSumPointsGamJeom(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    (new String[20])[14] = "" + 
      getTotalVideoReplayRequests(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    (new String[20])[15] = "" + 
      getTotalVideoReplayRejecteds(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    (new String[20])[16] = "" + 
      getTotalVideoReplayApprovedByHeadPoint(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    (new String[20])[17] = "" + 
      getTotalVideoReplayApprovedByHeadTechPoint(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    (new String[20])[18] = "" + 
      getTotalVideoReplayApprovedByBodyTechPoint(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    (new String[20])[19] = "" + 
      getTotalVideoReplayApprovedByPenalty(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    defaultAggregatted[1] = new String[20];
    String[][] hitsByCategoryCount = new String[0][];
    List<CategoryEntry> categoryEntries = this.categoryService.findAllEntries();
    if (categoryEntries != null) {
      String[] headers = new String[0];
      headers = (String[])ArrayUtils.add((Object[])headers, "Power Level");
      for (int j = 0; j < categoryEntries.size(); j++) {
        CategoryEntry categoryEntry = categoryEntries.get(j);
        headers = (String[])ArrayUtils.add((Object[])headers, categoryEntry.getSubCategory().getName() + " " + categoryEntry
            .getGender() + " " + categoryEntry
            .getName());
      } 
      hitsByCategoryCount = (String[][])ArrayUtils.add((Object[])hitsByCategoryCount, headers);
      for (int i = 0; i < 100; i++) {
        String[] values = new String[0];
        values = (String[])ArrayUtils.add((Object[])values, "" + i);
        for (int k = 0; k < categoryEntries.size(); k++) {
          CategoryEntry categoryEntry = categoryEntries.get(k);
          Long value = getTotalHitsByLevelAndCategory(Integer.valueOf(i), phaseId, categoryEntry
              
              .getSubCategory().getId(), categoryEntry
              .getGender(), categoryEntry
              .getId(), athleteIds, startDate, endDate);
          values = (String[])ArrayUtils.add((Object[])values, "" + value);
        } 
        hitsByCategoryCount = (String[][])ArrayUtils.add((Object[])hitsByCategoryCount, values);
      } 
    } 
    this.matchLogExporterUtil.exportAggregatedsToCSVFile(defaultAggregatted, hitsByCategoryCount, new File(this.externalConfigService
          
          .getExternalConfigEntry().getMatchLogOutputDirectory()));
  }
  
  public List<MatchLogDto> find(String phaseId, String subCategoryId, Gender gender, String categoryId, List<String> athleteIds, Date startDate, Date endDate) throws TkStrikeServiceException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
    String sqlQuery = "SELECT m.* FROM TKS_HISTO_MATCH_LOG m ";
    Map<String, Object> whereMap = generateWhere(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    String where = (String)whereMap.get("where");
    Object[] params = (Object[])whereMap.get("params");
    if (!"".equals(where))
      sqlQuery = sqlQuery + " WHERE " + where; 
    return jdbcTemplate.query(sqlQuery, params, new MatchLogDtoRowMapper());
  }
  
  void putAthletesInfo(MatchLogDto matchLog, DataTableRoundInfoItem dataTableRoundInfoItem) {
    if (matchLog != null && dataTableRoundInfoItem != null) {
      dataTableRoundInfoItem.setBlueName(matchLog.getBlueAthleteName());
      dataTableRoundInfoItem.setBlueNoc(matchLog.getBlueAthleteFlagAbbreviation());
      dataTableRoundInfoItem.setRedName(matchLog.getRedAthleteName());
      dataTableRoundInfoItem.setRedNoc(matchLog.getRedAthleteFlagAbbreviation());
    } 
  }
  
  String getMatchWeight(@Nonnull MatchLogDto matchLog) {
    return matchLog.getCategoryName();
  }
  
  String getMatchDivision(@Nonnull MatchLogDto matchLog) {
    return matchLog.getSubCategoryName();
  }
  
  Integer getMinBodyLevel(@Nonnull MatchLogDto matchLog) {
    return matchLog.getMinBodyLevel();
  }
  
  Integer getMinHeadLevel(@Nonnull MatchLogDto matchLog) {
    return matchLog.getMinHeadLevel();
  }
  
  String getMatchBlueName(@Nonnull MatchLogDto matchLog) {
    return matchLog.getBlueAthleteName();
  }
  
  String getMatchBlueFlagName(@Nonnull MatchLogDto matchLog) {
    try {
      FlagEntry flag = this.flagService.getEntryById(matchLog.getBlueAthleteFlagId());
      return (flag != null) ? flag.getName() : matchLog.getBlueAthleteFlagAbbreviation();
    } catch (TkStrikeServiceException tkStrikeServiceException) {
      return matchLog.getBlueAthleteFlagAbbreviation();
    } 
  }
  
  String getMatchBlueFlagAbbreviation(@Nonnull MatchLogDto matchLog) {
    return matchLog.getBlueAthleteFlagAbbreviation();
  }
  
  String getMatchRedName(@Nonnull MatchLogDto matchLog) {
    return matchLog.getRedAthleteName();
  }
  
  String getMatchRedFlagName(@Nonnull MatchLogDto matchLog) {
    try {
      FlagEntry flag = this.flagService.getEntryById(matchLog.getRedAthleteFlagId());
      return (flag != null) ? flag.getName() : matchLog.getRedAthleteFlagAbbreviation();
    } catch (TkStrikeServiceException tkStrikeServiceException) {
      return matchLog.getRedAthleteFlagAbbreviation();
    } 
  }
  
  String getMatchRedFlagAbbreviation(@Nonnull MatchLogDto matchLog) {
    return matchLog.getRedAthleteFlagAbbreviation();
  }
  
  void _migrateToHistorical(final List<MatchLogDto> matchLogs) {
    if (matchLogs != null) {
      final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
      jdbcTemplate.execute("INSERT INTO TKS_HISTO_MATCH_LOG (ID,MATCH_CONFIGURATION_ID,NUMBER_OF_JUDGES      ,MIN_BODY_LEVEL        ,BODY_SENSORS_ENABLED  ,MIN_HEAD_LEVEL        ,HEAD_SENSORS_ENABLED  ,MATCH_START_TIME      ,MATCH_END_TIME        ,BODY_POINTS           ,HEAD_POINTS           ,PUNCH_POINTS          ,BODY_TECH_POINTS      ,HEAD_TECH_POINTS      ,OVERTIME_POINTS       ,CELLING_SCORE         ,DIFFERENCIAL_SCORE    ,NEAR_MISS_LEVEL       ,MATCH_NUMBER          ,PHASE_ID              ,SUBCATEGORY_ID        ,GENDER                ,CATEGORY_ID           ,BLUE_ATHLETE_ID       ,BLUE_VIDEO_QUOTA      ,RED_ATHLETE_ID        ,RED_VIDEO_QUOTA       ,ROUNDS                ,ROUND_TIME            ,KYESHI_TIME           ,REST_TIME             ,GOLDENPOINT_ENABLED   ,GOLDENPOINT_TIME      ,MATCH_WINNER          ,MATCH_WINNER_BY       ,MATCH_RESULT,GDP_BLUE_PUNCHES ,GDP_BLUE_ROUND_WINS,GDP_BLUE_HITS,GDP_BLUE_PENALTIES,GDP_RED_PUNCHES,GDP_RED_ROUND_WINS,GDP_RED_HITS,GDP_RED_PENALTIES,GDP_TIE_BREAKER,MAX_GAM_JEOMS, ROUNDS_WINNER, OVERTIME_PENALTIES,MATCH_VICTORY_CRITERIA, IS_PARATKD_MATCH,BLUE_PARA_TECH_POINTS,RED_PARA_TECH_POINTS)VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new PreparedStatementCallback<Object>() {
            public Object doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException {
              for (MatchLogDto matchLog : matchLogs) {
                if (matchLog.getMatchEndTime() != null && matchLog.getMatchEndTime().longValue() > 0L) {
                  jdbcTemplate.execute("DELETE FROM TKS_HISTO_MATCH_LOG_ITEM WHERE MATCH_LOG_ID = '" + matchLog.getId() + "'");
                  jdbcTemplate.execute("DELETE FROM TKS_HISTO_MATCH_LOG WHERE ID = '" + matchLog.getId() + "'");
                  preparedStatement.setString(1, matchLog.getId());
                  preparedStatement.setString(2, matchLog.getMatchConfigurationId());
                  preparedStatement.setInt(3, matchLog.getNumberOfJudges().intValue());
                  preparedStatement.setInt(4, matchLog.getMinBodyLevel().intValue());
                  preparedStatement.setBoolean(5, matchLog.getBodySensorsEnabled().booleanValue());
                  preparedStatement.setInt(6, matchLog.getMinHeadLevel().intValue());
                  preparedStatement.setBoolean(7, matchLog.getHeadSensorsEnabled().booleanValue());
                  preparedStatement.setLong(8, matchLog.getMatchStartTime().longValue());
                  preparedStatement.setLong(9, matchLog.getMatchEndTime().longValue());
                  preparedStatement.setInt(10, matchLog.getBodyPoints().intValue());
                  preparedStatement.setInt(11, matchLog.getHeadPoints().intValue());
                  preparedStatement.setInt(12, matchLog.getPunchPoints().intValue());
                  preparedStatement.setInt(13, matchLog.getBodyTechPoints().intValue());
                  preparedStatement.setInt(14, matchLog.getHeadTechPoints().intValue());
                  preparedStatement.setInt(15, matchLog.getOvertimePoints().intValue());
                  preparedStatement.setInt(16, matchLog.getCellingScore().intValue());
                  preparedStatement.setInt(17, matchLog.getDifferencialScore().intValue());
                  preparedStatement.setInt(18, matchLog.getNearMissLevel().intValue());
                  preparedStatement.setString(19, matchLog.getMatchNumber());
                  preparedStatement.setString(20, matchLog.getPhaseId());
                  preparedStatement.setString(21, matchLog.getSubCategoryId());
                  preparedStatement.setString(22, matchLog.getCategoryGender().toString());
                  preparedStatement.setString(23, matchLog.getCategoryId());
                  preparedStatement.setString(24, matchLog.getBlueAthleteId());
                  preparedStatement.setInt(25, matchLog.getBlueAthleteVideoQuota().intValue());
                  preparedStatement.setString(26, matchLog.getRedAthleteId());
                  preparedStatement.setInt(27, matchLog.getRedAthleteVideoQuota().intValue());
                  preparedStatement.setInt(28, matchLog.getRoundsConfig().getRounds().intValue());
                  preparedStatement.setString(29, matchLog.getRoundsConfig().getRoundTimeStr());
                  preparedStatement.setString(30, matchLog.getRoundsConfig().getKyeShiTimeStr());
                  preparedStatement.setString(31, matchLog.getRoundsConfig().getRestTimeStr());
                  preparedStatement.setBoolean(32, matchLog.getRoundsConfig().getGoldenPointEnabled().booleanValue());
                  preparedStatement.setString(33, matchLog.getRoundsConfig().getGoldenPointTimeStr());
                  preparedStatement.setString(34, (matchLog.getMatchWinner() != null) ? matchLog.getMatchWinner().toString() : null);
                  preparedStatement.setString(35, (matchLog.getMatchWinnerBy() != null) ? matchLog.getMatchWinnerBy().toString() : null);
                  preparedStatement.setString(36, matchLog.getMatchResult());
                  if (matchLog.getGoldenPointTieBreakerInfo() != null && matchLog.getGoldenPointTieBreakerInfo().getBluePunches() != null) {
                    preparedStatement.setInt(37, matchLog.getGoldenPointTieBreakerInfo().getBluePunches().intValue());
                  } else {
                    preparedStatement.setNull(37, 4);
                  } 
                  if (matchLog.getGoldenPointTieBreakerInfo() != null && matchLog.getGoldenPointTieBreakerInfo().getBlueRoundWins() != null) {
                    preparedStatement.setInt(38, matchLog.getGoldenPointTieBreakerInfo().getBlueRoundWins().intValue());
                  } else {
                    preparedStatement.setNull(38, 4);
                  } 
                  if (matchLog.getGoldenPointTieBreakerInfo() != null && matchLog.getGoldenPointTieBreakerInfo().getBlueHits() != null) {
                    preparedStatement.setInt(39, matchLog.getGoldenPointTieBreakerInfo().getBlueHits().intValue());
                  } else {
                    preparedStatement.setNull(39, 4);
                  } 
                  if (matchLog.getGoldenPointTieBreakerInfo() != null && matchLog.getGoldenPointTieBreakerInfo().getBluePenalties() != null) {
                    preparedStatement.setInt(40, matchLog.getGoldenPointTieBreakerInfo().getBluePenalties().intValue());
                  } else {
                    preparedStatement.setNull(40, 4);
                  } 
                  if (matchLog.getGoldenPointTieBreakerInfo() != null && matchLog.getGoldenPointTieBreakerInfo().getRedPunches() != null) {
                    preparedStatement.setInt(41, matchLog.getGoldenPointTieBreakerInfo().getRedPunches().intValue());
                  } else {
                    preparedStatement.setNull(41, 4);
                  } 
                  if (matchLog.getGoldenPointTieBreakerInfo() != null && matchLog.getGoldenPointTieBreakerInfo().getRedRoundWins() != null) {
                    preparedStatement.setInt(42, matchLog.getGoldenPointTieBreakerInfo().getRedRoundWins().intValue());
                  } else {
                    preparedStatement.setNull(42, 4);
                  } 
                  if (matchLog.getGoldenPointTieBreakerInfo() != null && matchLog.getGoldenPointTieBreakerInfo().getRedHits() != null) {
                    preparedStatement.setInt(43, matchLog.getGoldenPointTieBreakerInfo().getRedHits().intValue());
                  } else {
                    preparedStatement.setNull(43, 4);
                  } 
                  if (matchLog.getGoldenPointTieBreakerInfo() != null && matchLog.getGoldenPointTieBreakerInfo().getRedPenalties() != null) {
                    preparedStatement.setInt(44, matchLog.getGoldenPointTieBreakerInfo().getRedPenalties().intValue());
                  } else {
                    preparedStatement.setNull(44, 4);
                  } 
                  if (matchLog.getGoldenPointTieBreakerInfo() != null && matchLog.getGoldenPointTieBreakerInfo().getHaveTieBreaker() != null) {
                    preparedStatement.setBoolean(45, matchLog.getGoldenPointTieBreakerInfo().getHaveTieBreaker().booleanValue());
                  } else {
                    preparedStatement.setBoolean(45, Boolean.FALSE.booleanValue());
                  } 
                  preparedStatement.setInt(46, (matchLog.getMaxAllowedGamJeoms() != null) ? matchLog.getMaxAllowedGamJeoms().intValue() : 10);
                  if (matchLog.getRoundsWinners() != null) {
                    preparedStatement.setString(47, matchLog.getRoundsWinners());
                  } else {
                    preparedStatement.setNull(47, 12);
                  } 
                  preparedStatement.setInt(48, matchLog.getOvertimePenalties().intValue());
                  preparedStatement.setString(49, matchLog.getMatchVictoryCriteria().toString());
                  preparedStatement.setBoolean(50, matchLog.getParaTkdMatch().booleanValue());
                  if (matchLog.getGoldenPointTieBreakerInfo() != null && matchLog.getGoldenPointTieBreakerInfo().getBluePARATechPoints() != null) {
                    preparedStatement.setInt(51, matchLog.getGoldenPointTieBreakerInfo().getBluePARATechPoints().intValue());
                  } else {
                    preparedStatement.setNull(51, 4);
                  } 
                  if (matchLog.getGoldenPointTieBreakerInfo() != null && matchLog.getGoldenPointTieBreakerInfo().getRedPARATechPoints() != null) {
                    preparedStatement.setInt(52, matchLog.getGoldenPointTieBreakerInfo().getRedPARATechPoints().intValue());
                  } else {
                    preparedStatement.setNull(52, 4);
                  } 
                  preparedStatement.execute();
                  try {
                    final List<MatchLogItemDto> matchLogItems = MatchLogHistoricalServiceImpl.this.matchLogService.findByMatchLogId(matchLog.getId());
                    if (matchLogItems != null) {
                      JdbcTemplate jdbcTemplate = new JdbcTemplate(MatchLogHistoricalServiceImpl.this.dataSource, true);
                      jdbcTemplate.execute("INSERT INTO TKS_HISTO_MATCH_LOG_ITEM (ID,MATCH_LOG_ID       ,ROUND_NUMBER       ,ROUND_NUMBER_STR   ,EVENT_TIME         ,ROUND_TIME         ,SYSTEM_TIME        ,MATCH_LOG_ITEM_TYPE,BLUE_GEN_POINTS    ,RED_GEN_POINTS     ,BLUE_ADD_POINTS    ,RED_ADD_POINTS     ,BLUE_POINTS,RED_POINTS,BLUE_PENALTIES,RED_PENALTIES,BLUE_TOTAL_PENALTIES,RED_TOTAL_PENALTIES,BLUE_VIDEO_QUOTA,RED_VIDEO_QUOTA,BLUE_GOLDENPOINT_HITS,RED_GOLDENPOINT_HITS,BLUE_GOLDENPOINT_PENALTIES,RED_GOLDENPOINT_PENALTIES,IS_GOLDENPOINT_ROUND,ENTRY_VALUE        ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new PreparedStatementCallback<Object>() {
                            public Object doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException {
                              for (MatchLogItemDto matchLogItem : matchLogItems) {
                                preparedStatement.setString(1, matchLogItem.getId());
                                preparedStatement.setString(2, matchLog.getId());
                                preparedStatement.setInt(3, matchLogItem.getRoundNumber().intValue());
                                preparedStatement.setString(4, matchLogItem.getRoundNumberStr());
                                preparedStatement.setLong(5, matchLogItem.getEventTime().longValue());
                                preparedStatement.setLong(6, matchLogItem.getRoundTime().longValue());
                                preparedStatement.setLong(7, matchLogItem.getSystemTime().longValue());
                                preparedStatement.setString(8, matchLogItem.getMatchLogItemType().toString());
                                preparedStatement.setInt(9, matchLogItem.getBlueGeneralPoints().intValue());
                                preparedStatement.setInt(10, matchLogItem.getRedGeneralPoints().intValue());
                                preparedStatement.setInt(11, matchLogItem.getBlueAddPoints().intValue());
                                preparedStatement.setInt(12, matchLogItem.getRedAddPoints().intValue());
                                preparedStatement.setInt(13, matchLogItem.getBluePoints().intValue());
                                preparedStatement.setInt(14, matchLogItem.getRedPoints().intValue());
                                preparedStatement.setInt(15, matchLogItem.getBluePenalties().intValue());
                                preparedStatement.setInt(16, matchLogItem.getRedPenalties().intValue());
                                preparedStatement.setInt(17, matchLogItem.getBlueTotalPenalties().intValue());
                                preparedStatement.setInt(18, matchLogItem.getRedTotalPenalties().intValue());
                                preparedStatement.setInt(19, matchLogItem.getBlueVideoQuota().intValue());
                                preparedStatement.setInt(20, matchLogItem.getRedVideoQuota().intValue());
                                preparedStatement.setInt(21, matchLogItem.getBlueGoldenPointHits().intValue());
                                preparedStatement.setInt(22, matchLogItem.getRedGoldenPointHits().intValue());
                                preparedStatement.setInt(23, matchLogItem.getBlueGoldenPointPenalties().intValue());
                                preparedStatement.setInt(24, matchLogItem.getRedGoldenPointPenalties().intValue());
                                preparedStatement.setBoolean(25, matchLogItem.getGoldenPointRound().booleanValue());
                                preparedStatement.setString(26, matchLogItem.getEntryValue());
                                preparedStatement.execute();
                              } 
                              return null;
                            }
                          });
                    } 
                  } catch (TkStrikeServiceException e) {
                    e.printStackTrace();
                  } 
                } 
              } 
              return null;
            }
          });
    } 
  }
  
  protected Long getTotalHeadHits(String phaseId, String subCategoryId, Gender gender, String categoryId, List<String> athleteIds, Date startDate, Date endDate) throws TkStrikeServiceException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
    String sqlQuery = "SELECT COUNT(*) FROM TKS_HISTO_MATCH_LOG m,TKS_HISTO_MATCH_LOG_ITEM i WHERE m.id = i.MATCH_LOG_ID   AND i.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.BLUE_HEAD_HIT.toString() + "','" + MatchLogItemType.RED_HEAD_HIT.toString() + "') ";
    Map<String, Object> whereMap = generateWhere(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    String where = (String)whereMap.get("where");
    Object[] params = (Object[])whereMap.get("params");
    if (!"".equals(where))
      sqlQuery = sqlQuery + " AND " + where; 
    Long res = (Long)jdbcTemplate.queryForObject(sqlQuery, params, Long.class);
    return Long.valueOf((res != null) ? res.longValue() : 0L);
  }
  
  protected Long getTotalHeadValidHits(String phaseId, String subCategoryId, Gender gender, String categoryId, List<String> athleteIds, Date startDate, Date endDate) throws TkStrikeServiceException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
    String sqlQuery = "SELECT COUNT(*) FROM TKS_HISTO_MATCH_LOG m,TKS_HISTO_MATCH_LOG_ITEM i WHERE m.id = i.MATCH_LOG_ID   AND i.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.BLUE_HEAD_HIT.toString() + "','" + MatchLogItemType.RED_HEAD_HIT.toString() + "') " + "  AND convert(i.ENTRY_VALUE,integer) >= m.MIN_HEAD_LEVEL ";
    Map<String, Object> whereMap = generateWhere(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    String where = (String)whereMap.get("where");
    Object[] params = (Object[])whereMap.get("params");
    if (!"".equals(where))
      sqlQuery = sqlQuery + " AND " + where; 
    Long res = (Long)jdbcTemplate.queryForObject(sqlQuery, params, Long.class);
    return Long.valueOf((res != null) ? res.longValue() : 0L);
  }
  
  protected Long getTotalBodyHits(String phaseId, String subCategoryId, Gender gender, String categoryId, List<String> athleteIds, Date startDate, Date endDate) throws TkStrikeServiceException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
    String sqlQuery = "SELECT COUNT(*) FROM TKS_HISTO_MATCH_LOG m,TKS_HISTO_MATCH_LOG_ITEM i WHERE m.id = i.MATCH_LOG_ID   AND i.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.BLUE_BODY_HIT.toString() + "','" + MatchLogItemType.RED_BODY_HIT.toString() + "') ";
    Map<String, Object> whereMap = generateWhere(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    String where = (String)whereMap.get("where");
    Object[] params = (Object[])whereMap.get("params");
    if (!"".equals(where))
      sqlQuery = sqlQuery + " AND " + where; 
    Long res = (Long)jdbcTemplate.queryForObject(sqlQuery, params, Long.class);
    return Long.valueOf((res != null) ? res.longValue() : 0L);
  }
  
  protected Long getTotalBodyValidHits(String phaseId, String subCategoryId, Gender gender, String categoryId, List<String> athleteIds, Date startDate, Date endDate) throws TkStrikeServiceException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
    String sqlQuery = "SELECT COUNT(*) FROM TKS_HISTO_MATCH_LOG m,TKS_HISTO_MATCH_LOG_ITEM i WHERE m.id = i.MATCH_LOG_ID   AND i.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.BLUE_BODY_HIT.toString() + "','" + MatchLogItemType.RED_BODY_HIT.toString() + "') " + "  AND convert(i.ENTRY_VALUE,integer) >= m.MIN_BODY_LEVEL ";
    Map<String, Object> whereMap = generateWhere(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    String where = (String)whereMap.get("where");
    Object[] params = (Object[])whereMap.get("params");
    if (!"".equals(where))
      sqlQuery = sqlQuery + " AND " + where; 
    Long res = (Long)jdbcTemplate.queryForObject(sqlQuery, params, Long.class);
    return Long.valueOf((res != null) ? res.longValue() : 0L);
  }
  
  protected Long getTotalHeadTech(String phaseId, String subCategoryId, Gender gender, String categoryId, List<String> athleteIds, Date startDate, Date endDate) throws TkStrikeServiceException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
    String sqlQuery = "SELECT COUNT(*) FROM TKS_HISTO_MATCH_LOG m,TKS_HISTO_MATCH_LOG_ITEM i WHERE m.id = i.MATCH_LOG_ID   AND i.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.BLUE_JUDGE_HEAD_TECH.toString() + "','" + MatchLogItemType.RED_JUDGE_HEAD_TECH.toString() + "') ";
    Map<String, Object> whereMap = generateWhere(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    String where = (String)whereMap.get("where");
    Object[] params = (Object[])whereMap.get("params");
    if (!"".equals(where))
      sqlQuery = sqlQuery + " AND " + where; 
    Long res = (Long)jdbcTemplate.queryForObject(sqlQuery, params, Long.class);
    return Long.valueOf((res != null) ? res.longValue() : 0L);
  }
  
  protected Long getTotalBodyTech(String phaseId, String subCategoryId, Gender gender, String categoryId, List<String> athleteIds, Date startDate, Date endDate) throws TkStrikeServiceException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
    String sqlQuery = "SELECT COUNT(*) FROM TKS_HISTO_MATCH_LOG m,TKS_HISTO_MATCH_LOG_ITEM i WHERE m.id = i.MATCH_LOG_ID   AND i.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.BLUE_JUDGE_BODY_TECH.toString() + "','" + MatchLogItemType.RED_JUDGE_BODY_TECH.toString() + "') ";
    Map<String, Object> whereMap = generateWhere(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    String where = (String)whereMap.get("where");
    Object[] params = (Object[])whereMap.get("params");
    if (!"".equals(where))
      sqlQuery = sqlQuery + " AND " + where; 
    Long res = (Long)jdbcTemplate.queryForObject(sqlQuery, params, Long.class);
    return Long.valueOf((res != null) ? res.longValue() : 0L);
  }
  
  protected Long getTotalPunch(String phaseId, String subCategoryId, Gender gender, String categoryId, List<String> athleteIds, Date startDate, Date endDate) throws TkStrikeServiceException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
    String sqlQuery = "SELECT COUNT(*) FROM TKS_HISTO_MATCH_LOG m,TKS_HISTO_MATCH_LOG_ITEM i WHERE m.id = i.MATCH_LOG_ID   AND i.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.BLUE_JUDGE_PUNCH.toString() + "','" + MatchLogItemType.RED_JUDGE_PUNCH.toString() + "') ";
    Map<String, Object> whereMap = generateWhere(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    String where = (String)whereMap.get("where");
    Object[] params = (Object[])whereMap.get("params");
    if (!"".equals(where))
      sqlQuery = sqlQuery + " AND " + where; 
    Long res = (Long)jdbcTemplate.queryForObject(sqlQuery, params, Long.class);
    return Long.valueOf((res != null) ? res.longValue() : 0L);
  }
  
  protected Long getSumPointsBody(String phaseId, String subCategoryId, Gender gender, String categoryId, List<String> athleteIds, Date startDate, Date endDate) throws TkStrikeServiceException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
    String sqlQuery = "SELECT SUM(i.BLUE_ADD_POINTS + i.RED_ADD_POINTS) FROM TKS_HISTO_MATCH_LOG m,TKS_HISTO_MATCH_LOG_ITEM i WHERE m.id = i.MATCH_LOG_ID   AND i.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.BLUE_BODY_POINT.toString() + "','" + MatchLogItemType.RED_BODY_POINT.toString() + "') " + "  AND i.ENTRY_VALUE = 'SENSOR' ";
    Map<String, Object> whereMap = generateWhere(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    String where = (String)whereMap.get("where");
    Object[] params = (Object[])whereMap.get("params");
    if (!"".equals(where))
      sqlQuery = sqlQuery + " AND " + where; 
    Long res = (Long)jdbcTemplate.queryForObject(sqlQuery, params, Long.class);
    return Long.valueOf((res != null) ? res.longValue() : 0L);
  }
  
  protected Long getSumPointsBodyTech(String phaseId, String subCategoryId, Gender gender, String categoryId, List<String> athleteIds, Date startDate, Date endDate) throws TkStrikeServiceException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
    String sqlQuery = "SELECT SUM(i.BLUE_ADD_POINTS + i.RED_ADD_POINTS) FROM TKS_HISTO_MATCH_LOG m,TKS_HISTO_MATCH_LOG_ITEM i WHERE m.id = i.MATCH_LOG_ID   AND i.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.BLUE_BODY_TECH_POINT.toString() + "','" + MatchLogItemType.RED_BODY_TECH_POINT.toString() + "') " + "  AND i.ENTRY_VALUE = 'JUDGE' ";
    Map<String, Object> whereMap = generateWhere(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    String where = (String)whereMap.get("where");
    Object[] params = (Object[])whereMap.get("params");
    if (!"".equals(where))
      sqlQuery = sqlQuery + " AND " + where; 
    Long res = (Long)jdbcTemplate.queryForObject(sqlQuery, params, Long.class);
    return Long.valueOf((res != null) ? res.longValue() : 0L);
  }
  
  protected Long getSumPointsHead(String phaseId, String subCategoryId, Gender gender, String categoryId, List<String> athleteIds, Date startDate, Date endDate) throws TkStrikeServiceException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
    String sqlQuery = "SELECT SUM(i.BLUE_ADD_POINTS + i.RED_ADD_POINTS) FROM TKS_HISTO_MATCH_LOG m,TKS_HISTO_MATCH_LOG_ITEM i WHERE m.id = i.MATCH_LOG_ID   AND i.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.BLUE_HEAD_POINT.toString() + "','" + MatchLogItemType.RED_HEAD_POINT.toString() + "') " + "  AND i.ENTRY_VALUE = 'SENSOR' ";
    Map<String, Object> whereMap = generateWhere(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    String where = (String)whereMap.get("where");
    Object[] params = (Object[])whereMap.get("params");
    if (!"".equals(where))
      sqlQuery = sqlQuery + " AND " + where; 
    Long res = (Long)jdbcTemplate.queryForObject(sqlQuery, params, Long.class);
    return Long.valueOf((res != null) ? res.longValue() : 0L);
  }
  
  protected Long getSumPointsHeadTech(String phaseId, String subCategoryId, Gender gender, String categoryId, List<String> athleteIds, Date startDate, Date endDate) throws TkStrikeServiceException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
    String sqlQuery = "SELECT SUM(i.BLUE_ADD_POINTS + i.RED_ADD_POINTS) FROM TKS_HISTO_MATCH_LOG m,TKS_HISTO_MATCH_LOG_ITEM i WHERE m.id = i.MATCH_LOG_ID   AND i.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.BLUE_HEAD_TECH_POINT.toString() + "','" + MatchLogItemType.RED_HEAD_TECH_POINT.toString() + "') " + "  AND i.ENTRY_VALUE = 'JUDGE' ";
    Map<String, Object> whereMap = generateWhere(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    String where = (String)whereMap.get("where");
    Object[] params = (Object[])whereMap.get("params");
    if (!"".equals(where))
      sqlQuery = sqlQuery + " AND " + where; 
    Long res = (Long)jdbcTemplate.queryForObject(sqlQuery, params, Long.class);
    return Long.valueOf((res != null) ? res.longValue() : 0L);
  }
  
  protected Long getSumPointsPunch(String phaseId, String subCategoryId, Gender gender, String categoryId, List<String> athleteIds, Date startDate, Date endDate) throws TkStrikeServiceException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
    String sqlQuery = "SELECT SUM(i.BLUE_ADD_POINTS + i.RED_ADD_POINTS) FROM TKS_HISTO_MATCH_LOG m,TKS_HISTO_MATCH_LOG_ITEM i WHERE m.id = i.MATCH_LOG_ID   AND i.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.BLUE_PUNCH_POINT.toString() + "','" + MatchLogItemType.RED_PUNCH_POINT.toString() + "') " + "  AND i.ENTRY_VALUE = 'JUDGE' ";
    Map<String, Object> whereMap = generateWhere(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    String where = (String)whereMap.get("where");
    Object[] params = (Object[])whereMap.get("params");
    if (!"".equals(where))
      sqlQuery = sqlQuery + " AND " + where; 
    Long res = (Long)jdbcTemplate.queryForObject(sqlQuery, params, Long.class);
    return Long.valueOf((res != null) ? res.longValue() : 0L);
  }
  
  protected Long getSumPointsKyongGo(String phaseId, String subCategoryId, Gender gender, String categoryId, List<String> athleteIds, Date startDate, Date endDate) throws TkStrikeServiceException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
    Map<String, Object> whereMap = generateWhere(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    String where = (String)whereMap.get("where");
    Object[] params = (Object[])whereMap.get("params");
    Object[] params2 = new Object[0];
    params2 = ArrayUtils.addAll(params2, params);
    params2 = ArrayUtils.addAll(params2, params);
    String sqlQueryBlue = " select m.ID,m.MATCH_NUMBER,i.ROUND_NUMBER,COUNT(*) / 2 Q    from TKS_HISTO_MATCH_LOG m,TKS_HISTO_MATCH_LOG_ITEM i  WHERE m.id = i.MATCH_LOG_ID    AND i.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.BLUE_ADD_KYONG_GO + "') " + "   AND i.ENTRY_VALUE = 'MAIN_CONTROL' ";
    if (!"".equals(where))
      sqlQueryBlue = sqlQueryBlue + " AND " + where; 
    sqlQueryBlue = sqlQueryBlue + " GROUP BY m.ID,m.MATCH_NUMBER,i.ROUND_NUMBER ";
    String sqlQueryRed = " select m.ID,m.MATCH_NUMBER,i.ROUND_NUMBER,COUNT(*) / 2 Q    from TKS_HISTO_MATCH_LOG m,TKS_HISTO_MATCH_LOG_ITEM i  WHERE m.id = i.MATCH_LOG_ID    AND i.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.RED_ADD_KYONG_GO + "') " + "   AND i.ENTRY_VALUE = 'MAIN_CONTROL' ";
    if (!"".equals(where))
      sqlQueryRed = sqlQueryRed + " AND " + where; 
    sqlQueryRed = sqlQueryRed + " GROUP BY m.ID,m.MATCH_NUMBER,i.ROUND_NUMBER ";
    String sqlQuery = " SELECT SUM(blue.q + red.q)  FROM ( " + sqlQueryBlue + ") blue, " + "   (" + sqlQueryRed + ") red ";
    Long res = (Long)jdbcTemplate.queryForObject(sqlQuery, params2, Long.class);
    return Long.valueOf((res != null) ? res.longValue() : 0L);
  }
  
  protected Long getSumPointsGamJeom(String phaseId, String subCategoryId, Gender gender, String categoryId, List<String> athleteIds, Date startDate, Date endDate) throws TkStrikeServiceException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
    String sqlQuery = "SELECT COUNT(*) FROM TKS_HISTO_MATCH_LOG m,TKS_HISTO_MATCH_LOG_ITEM i WHERE m.id = i.MATCH_LOG_ID   AND i.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.BLUE_ADD_GAME_JEON.toString() + "','" + MatchLogItemType.RED_ADD_GAME_JEON.toString() + "') " + "  AND i.ENTRY_VALUE = 'MAIN_CONTROL' ";
    Map<String, Object> whereMap = generateWhere(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    String where = (String)whereMap.get("where");
    Object[] params = (Object[])whereMap.get("params");
    if (!"".equals(where))
      sqlQuery = sqlQuery + " AND " + where; 
    Long res = (Long)jdbcTemplate.queryForObject(sqlQuery, params, Long.class);
    return Long.valueOf((res != null) ? res.longValue() : 0L);
  }
  
  protected Long getTotalVideoReplayRequests(String phaseId, String subCategoryId, Gender gender, String categoryId, List<String> athleteIds, Date startDate, Date endDate) throws TkStrikeServiceException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
    String sqlQuery = "SELECT COUNT(*) FROM TKS_HISTO_MATCH_LOG m,TKS_HISTO_MATCH_LOG_ITEM i WHERE m.id = i.MATCH_LOG_ID   AND i.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.BLUE_VIDEO_REQUEST.toString() + "','" + MatchLogItemType.RED_VIDEO_REQUEST.toString() + "') ";
    Map<String, Object> whereMap = generateWhere(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    String where = (String)whereMap.get("where");
    Object[] params = (Object[])whereMap.get("params");
    if (!"".equals(where))
      sqlQuery = sqlQuery + " AND " + where; 
    Long res = (Long)jdbcTemplate.queryForObject(sqlQuery, params, Long.class);
    return Long.valueOf((res != null) ? res.longValue() : 0L);
  }
  
  protected Long getTotalVideoReplayRejecteds(String phaseId, String subCategoryId, Gender gender, String categoryId, List<String> athleteIds, Date startDate, Date endDate) throws TkStrikeServiceException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
    String sqlQuery = "SELECT COUNT(*) FROM TKS_HISTO_MATCH_LOG m,TKS_HISTO_MATCH_LOG_ITEM i WHERE m.id = i.MATCH_LOG_ID   AND i.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.BLUE_VIDEO_QUOTA_CHANGED.toString() + "','" + MatchLogItemType.RED_VIDEO_QUOTA_CHANGED.toString() + "') ";
    Map<String, Object> whereMap = generateWhere(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    String where = (String)whereMap.get("where");
    Object[] params = (Object[])whereMap.get("params");
    if (!"".equals(where))
      sqlQuery = sqlQuery + " AND " + where; 
    Long res = (Long)jdbcTemplate.queryForObject(sqlQuery, params, Long.class);
    return Long.valueOf((res != null) ? res.longValue() : 0L);
  }
  
  protected Long getTotalVideoReplayApprovedByHeadPoint(String phaseId, String subCategoryId, Gender gender, String categoryId, List<String> athleteIds, Date startDate, Date endDate) throws TkStrikeServiceException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
    String sqlQuery = "SELECT count(*)    FROM TKS_HISTO_MATCH_LOG m,TKS_HISTO_MATCH_LOG_ITEM i,TKS_HISTO_MATCH_LOG_ITEM i2 WHERE m.id = i.MATCH_LOG_ID   AND m.id = i2.MATCH_LOG_ID   AND i.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.BLUE_HEAD_POINT + "','" + MatchLogItemType.RED_HEAD_POINT + "') " + "  AND i.ENTRY_VALUE = 'SCOREBOARD_EDITOR' " + "  AND i2.SYSTEM_TIME  = (SELECT MAX(ii.SYSTEM_TIME) " + "                         FROM TKS_HISTO_MATCH_LOG_ITEM ii " + "                         WHERE ii.SYSTEM_TIME < i.SYSTEM_TIME " + "                         AND ii.MATCH_LOG_ID = i.MATCH_LOG_ID) " + "  AND i2.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.BLUE_VIDEO_REQUEST + "','" + MatchLogItemType.RED_VIDEO_REQUEST + "') ";
    Map<String, Object> whereMap = generateWhere(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    String where = (String)whereMap.get("where");
    Object[] params = (Object[])whereMap.get("params");
    if (!"".equals(where))
      sqlQuery = sqlQuery + " AND " + where; 
    Long res = (Long)jdbcTemplate.queryForObject(sqlQuery, params, Long.class);
    return Long.valueOf((res != null) ? res.longValue() : 0L);
  }
  
  protected Long getTotalVideoReplayApprovedByHeadTechPoint(String phaseId, String subCategoryId, Gender gender, String categoryId, List<String> athleteIds, Date startDate, Date endDate) throws TkStrikeServiceException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
    String sqlQuery = "SELECT count(*)    FROM TKS_HISTO_MATCH_LOG m,TKS_HISTO_MATCH_LOG_ITEM i,TKS_HISTO_MATCH_LOG_ITEM i2 WHERE m.id = i.MATCH_LOG_ID   AND m.id = i2.MATCH_LOG_ID   AND i.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.BLUE_HEAD_TECH_POINT + "','" + MatchLogItemType.RED_HEAD_TECH_POINT + "') " + "  AND i.ENTRY_VALUE = 'SCOREBOARD_EDITOR' " + "  AND i2.SYSTEM_TIME  = (SELECT MAX(ii.SYSTEM_TIME) " + "                         FROM TKS_HISTO_MATCH_LOG_ITEM ii " + "                         WHERE ii.SYSTEM_TIME < i.SYSTEM_TIME " + "                         AND ii.MATCH_LOG_ID = i.MATCH_LOG_ID) " + "  AND i2.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.BLUE_VIDEO_REQUEST + "','" + MatchLogItemType.RED_VIDEO_REQUEST + "') ";
    Map<String, Object> whereMap = generateWhere(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    String where = (String)whereMap.get("where");
    Object[] params = (Object[])whereMap.get("params");
    if (!"".equals(where))
      sqlQuery = sqlQuery + " AND " + where; 
    Long res = (Long)jdbcTemplate.queryForObject(sqlQuery, params, Long.class);
    return Long.valueOf((res != null) ? res.longValue() : 0L);
  }
  
  protected Long getTotalVideoReplayApprovedByBodyTechPoint(String phaseId, String subCategoryId, Gender gender, String categoryId, List<String> athleteIds, Date startDate, Date endDate) throws TkStrikeServiceException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
    String sqlQuery = "SELECT count(*)    FROM TKS_HISTO_MATCH_LOG m,TKS_HISTO_MATCH_LOG_ITEM i,TKS_HISTO_MATCH_LOG_ITEM i2 WHERE m.id = i.MATCH_LOG_ID   AND m.id = i2.MATCH_LOG_ID   AND i.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.BLUE_BODY_TECH_POINT + "','" + MatchLogItemType.RED_BODY_TECH_POINT + "') " + "  AND i.ENTRY_VALUE = 'SCOREBOARD_EDITOR' " + "  AND i2.SYSTEM_TIME  = (SELECT MAX(ii.SYSTEM_TIME) " + "                         FROM TKS_HISTO_MATCH_LOG_ITEM ii " + "                         WHERE ii.SYSTEM_TIME < i.SYSTEM_TIME " + "                         AND ii.MATCH_LOG_ID = i.MATCH_LOG_ID) " + "  AND i2.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.BLUE_VIDEO_REQUEST + "','" + MatchLogItemType.RED_VIDEO_REQUEST + "') ";
    Map<String, Object> whereMap = generateWhere(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    String where = (String)whereMap.get("where");
    Object[] params = (Object[])whereMap.get("params");
    if (!"".equals(where))
      sqlQuery = sqlQuery + " AND " + where; 
    Long res = (Long)jdbcTemplate.queryForObject(sqlQuery, params, Long.class);
    return Long.valueOf((res != null) ? res.longValue() : 0L);
  }
  
  protected Long getTotalVideoReplayApprovedByPenalty(String phaseId, String subCategoryId, Gender gender, String categoryId, List<String> athleteIds, Date startDate, Date endDate) throws TkStrikeServiceException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
    String sqlQuery = "SELECT count(*)    FROM TKS_HISTO_MATCH_LOG m,TKS_HISTO_MATCH_LOG_ITEM i,TKS_HISTO_MATCH_LOG_ITEM i2 WHERE m.id = i.MATCH_LOG_ID   AND m.id = i2.MATCH_LOG_ID   AND i.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.BLUE_ADD_KYONG_GO + "','" + MatchLogItemType.BLUE_ADD_GAME_JEON + "','" + MatchLogItemType.RED_ADD_KYONG_GO + "','" + MatchLogItemType.RED_ADD_GAME_JEON + "') " + "  AND i.ENTRY_VALUE = 'SCOREBOARD_EDITOR' " + "  AND i2.SYSTEM_TIME  = (SELECT MAX(ii.SYSTEM_TIME) " + "                         FROM TKS_HISTO_MATCH_LOG_ITEM ii " + "                         WHERE ii.SYSTEM_TIME < i.SYSTEM_TIME " + "                         AND ii.MATCH_LOG_ID = i.MATCH_LOG_ID) " + "  AND i2.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.BLUE_VIDEO_REQUEST + "','" + MatchLogItemType.RED_VIDEO_REQUEST + "') ";
    Map<String, Object> whereMap = generateWhere(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    String where = (String)whereMap.get("where");
    Object[] params = (Object[])whereMap.get("params");
    if (!"".equals(where))
      sqlQuery = sqlQuery + " AND " + where; 
    Long res = (Long)jdbcTemplate.queryForObject(sqlQuery, params, Long.class);
    return Long.valueOf((res != null) ? res.longValue() : 0L);
  }
  
  protected Long getTotalHitsByLevelAndCategory(Integer level, String phaseId, String subCategoryId, Gender gender, String categoryId, List<String> athleteIds, Date startDate, Date endDate) throws TkStrikeServiceException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
    String sqlQuery = "SELECT count(*)    FROM TKS_HISTO_MATCH_LOG m,TKS_HISTO_MATCH_LOG_ITEM i WHERE m.id = i.MATCH_LOG_ID   AND i.MATCH_LOG_ITEM_TYPE IN ('" + MatchLogItemType.BLUE_BODY_HIT + "','" + MatchLogItemType.BLUE_HEAD_HIT + "','" + MatchLogItemType.RED_BODY_HIT + "','" + MatchLogItemType.RED_HEAD_HIT + "') " + "  AND convert(i.ENTRY_VALUE,integer) = " + level + " ";
    Map<String, Object> whereMap = generateWhere(phaseId, subCategoryId, gender, categoryId, athleteIds, startDate, endDate);
    String where = (String)whereMap.get("where");
    Object[] params = (Object[])whereMap.get("params");
    if (!"".equals(where))
      sqlQuery = sqlQuery + " AND " + where; 
    Long res = (Long)jdbcTemplate.queryForObject(sqlQuery, params, Long.class);
    return Long.valueOf((res != null) ? res.longValue() : 0L);
  }
  
  protected Map<String, Object> generateWhere(String phaseId, String subCategoryId, Gender gender, String categoryId, List<String> athleteIds, Date startDate, Date endDate) {
    String where = "";
    Object[] params = new Object[0];
    if (startDate != null && endDate != null) {
      if (!"".equals(where))
        where = where + " AND "; 
      where = where + " TRUNC(m.MATCH_START_TIME) BETWEEN ? AND ? ";
      Calendar cal = Calendar.getInstance();
      cal.setTime(startDate);
      cal.set(11, 0);
      cal.set(12, 0);
      cal.set(13, 0);
      cal.set(14, 0);
      params = ArrayUtils.add(params, Long.valueOf(cal.getTimeInMillis()));
      cal.setTime(endDate);
      cal.set(11, 23);
      cal.set(12, 59);
      cal.set(13, 59);
      cal.set(14, 999);
      params = ArrayUtils.add(params, Long.valueOf(cal.getTimeInMillis()));
    } 
    if (StringUtils.isNotBlank(phaseId)) {
      if (!"".equals(where))
        where = where + " AND "; 
      where = where + " m.PHASE_ID = ? ";
      params = ArrayUtils.add(params, phaseId);
    } 
    if (StringUtils.isNotBlank(subCategoryId)) {
      if (!"".equals(where))
        where = where + " AND "; 
      where = where + " m.SUBCATEGORY_ID = ? ";
      params = ArrayUtils.add(params, subCategoryId);
    } 
    if (gender != null) {
      if (!"".equals(where))
        where = where + " AND "; 
      where = where + " m.GENDER = ? ";
      params = ArrayUtils.add(params, gender.toString());
    } 
    if (StringUtils.isNotBlank(categoryId)) {
      if (!"".equals(where))
        where = where + " AND "; 
      where = where + " m.CATEGORY_ID = ? ";
      params = ArrayUtils.add(params, categoryId);
    } 
    if (athleteIds != null && athleteIds.size() > 0) {
      if (!"".equals(where))
        where = where + " AND "; 
      StringBuilder sb = new StringBuilder();
      for (String athleteId : athleteIds) {
        sb.append(" ?,");
        params = ArrayUtils.add(params, athleteId);
        params = ArrayUtils.add(params, athleteId);
      } 
      sb = sb.replace(sb.length() - 1, sb.length(), " ");
      where = where + "( m.BLUE_ATHLETE_ID IN (" + sb.toString() + ")  OR m.RED_ATHLETE_ID IN (" + sb.toString() + ") ) ";
    } 
    Map<String, Object> res = new HashMap<>();
    res.put("where", where);
    res.put("params", params);
    return res;
  }
  
  private class MatchLogDtoRowMapper implements RowMapper<MatchLogDto> {
    private MatchLogDtoRowMapper() {}
    
    public MatchLogDto mapRow(ResultSet resultSet, int i) throws SQLException {
      MatchLogDto res = new MatchLogDto();
      res.setId(resultSet.getString("ID"));
      res.setMatchConfigurationId(resultSet.getString("MATCH_CONFIGURATION_ID"));
      res.setNumberOfJudges(Integer.valueOf(resultSet.getInt("NUMBER_OF_JUDGES")));
      res.setMinBodyLevel(Integer.valueOf(resultSet.getInt("MIN_BODY_LEVEL")));
      res.setBodySensorsEnabled(Boolean.valueOf(resultSet.getBoolean("BODY_SENSORS_ENABLED")));
      res.setMinHeadLevel(Integer.valueOf(resultSet.getInt("MIN_HEAD_LEVEL")));
      res.setHeadSensorsEnabled(Boolean.valueOf(resultSet.getBoolean("HEAD_SENSORS_ENABLED")));
      res.setMatchStartTime(Long.valueOf(resultSet.getLong("MATCH_START_TIME")));
      res.setMatchEndTime(Long.valueOf(resultSet.getLong("MATCH_END_TIME")));
      res.setBodyPoints(Integer.valueOf(resultSet.getInt("BODY_POINTS")));
      res.setHeadPoints(Integer.valueOf(resultSet.getInt("HEAD_POINTS")));
      res.setPunchPoints(Integer.valueOf(resultSet.getInt("PUNCH_POINTS")));
      res.setBodyTechPoints(Integer.valueOf(resultSet.getInt("BODY_TECH_POINTS")));
      res.setHeadTechPoints(Integer.valueOf(resultSet.getInt("HEAD_TECH_POINTS")));
      res.setOvertimePoints(Integer.valueOf(resultSet.getInt("OVERTIME_POINTS")));
      res.setOvertimePenalties(Integer.valueOf(resultSet.getInt("OVERTIME_PENALTIES")));
      res.setCellingScore(Integer.valueOf(resultSet.getInt("CELLING_SCORE")));
      res.setDifferencialScore(Integer.valueOf(resultSet.getInt("DIFFERENCIAL_SCORE")));
      res.setNearMissLevel(Integer.valueOf(resultSet.getInt("NEAR_MISS_LEVEL")));
      res.setMatchNumber(resultSet.getString("MATCH_NUMBER"));
      try {
        String phaseId = resultSet.getString("PHASE_ID");
        if (StringUtils.isNotBlank(phaseId)) {
          PhaseEntry phase = MatchLogHistoricalServiceImpl.this.phaseService.getEntryById(phaseId);
          res.setPhaseId(phaseId);
          res.setPhaseName(phase.getName());
        } 
        String categoryId = resultSet.getString("CATEGORY_ID");
        if (StringUtils.isNotBlank(categoryId)) {
          CategoryEntry category = MatchLogHistoricalServiceImpl.this.categoryService.getEntryById(categoryId);
          if (category != null) {
            res.setCategoryId(category.getId());
            res.setCategoryName(category.getName());
            res.setCategoryBodyLevel(Integer.valueOf(category.getBodyLevel()));
            res.setCategoryHeadLevel(Integer.valueOf(category.getHeadLevel()));
            res.setCategoryGender(category.getGender());
            res.setGender(category.getGender());
            if (category.getSubCategory() != null) {
              res.setSubCategoryId(category.getSubCategory().getId());
              res.setSubCategoryName(category.getSubCategory().getName());
            } 
          } 
        } 
        String blueAthleteId = resultSet.getString("BLUE_ATHLETE_ID");
        if (StringUtils.isNotBlank(blueAthleteId)) {
          AthleteEntry athleteEntry = MatchLogHistoricalServiceImpl.this.athleteService.getEntryById(blueAthleteId);
          res.setBlueAthleteId(athleteEntry.getId());
          res.setBlueAthleteName(athleteEntry.getScoreboardName());
          res.setBlueAthleteWtfId(athleteEntry.getWfId());
          if (athleteEntry.getFlag() != null) {
            res.setBlueAthleteFlagId(athleteEntry.getFlag().getId());
            res.setBlueAthleteFlagName(athleteEntry.getFlag().getName());
            res.setBlueAthleteFlagShowName(Boolean.valueOf(athleteEntry.getFlag().isShowName()));
            res.setBlueAthleteFlagAbbreviation(athleteEntry.getFlag().getAbbreviation());
            res.setBlueAthleteFlagImagePath(athleteEntry.getFlag().getImagePath());
          } 
        } 
        String redAthleteId = resultSet.getString("RED_ATHLETE_ID");
        if (StringUtils.isNotBlank(redAthleteId)) {
          AthleteEntry athleteEntry = MatchLogHistoricalServiceImpl.this.athleteService.getEntryById(redAthleteId);
          res.setRedAthleteId(athleteEntry.getId());
          res.setRedAthleteName(athleteEntry.getScoreboardName());
          res.setRedAthleteWtfId(athleteEntry.getWfId());
          if (athleteEntry.getFlag() != null) {
            res.setRedAthleteFlagId(athleteEntry.getFlag().getId());
            res.setRedAthleteFlagName(athleteEntry.getFlag().getName());
            res.setRedAthleteFlagShowName(Boolean.valueOf(athleteEntry.getFlag().isShowName()));
            res.setRedAthleteFlagAbbreviation(athleteEntry.getFlag().getAbbreviation());
            res.setRedAthleteFlagImagePath(athleteEntry.getFlag().getImagePath());
          } 
        } 
      } catch (TkStrikeServiceException e) {
        e.printStackTrace();
      } 
      res.setBlueAthleteVideoQuota(Integer.valueOf(resultSet.getInt("BLUE_VIDEO_QUOTA")));
      res.setRedAthleteVideoQuota(Integer.valueOf(resultSet.getInt("RED_VIDEO_QUOTA")));
      RoundsConfigDto roundsConfigEntry = new RoundsConfigDto();
      roundsConfigEntry.setRounds(Integer.valueOf(resultSet.getInt("ROUNDS")));
      roundsConfigEntry.setGoldenPointEnabled(Boolean.valueOf(resultSet.getBoolean("GOLDENPOINT_ENABLED")));
      String roundTimeStr = resultSet.getString("ROUND_TIME");
      String kyeShiTimeStr = resultSet.getString("KYESHI_TIME");
      String restTimeStr = resultSet.getString("REST_TIME");
      String goldenPointTimeStr = resultSet.getString("GOLDENPOINT_TIME");
      try {
        if (StringUtils.isNotBlank(roundTimeStr)) {
          Calendar calRound = Calendar.getInstance();
          calRound.setTime(MatchLogHistoricalServiceImpl.sdf.parse(roundTimeStr));
          roundsConfigEntry.setRoundTimeMinutes(Integer.valueOf(calRound.get(12)));
          roundsConfigEntry.setRoundTimeSeconds(Integer.valueOf(calRound.get(13)));
        } 
        if (StringUtils.isNotBlank(kyeShiTimeStr)) {
          Calendar calKyeShi = Calendar.getInstance();
          calKyeShi.setTime(MatchLogHistoricalServiceImpl.sdf.parse(kyeShiTimeStr));
          roundsConfigEntry.setKyeShiTimeMinutes(Integer.valueOf(calKyeShi.get(12)));
          roundsConfigEntry.setKyeShiTimeSeconds(Integer.valueOf(calKyeShi.get(13)));
        } 
        if (StringUtils.isNotBlank(restTimeStr)) {
          Calendar calRest = Calendar.getInstance();
          calRest.setTime(MatchLogHistoricalServiceImpl.sdf.parse(restTimeStr));
          roundsConfigEntry.setRestTimeMinutes(Integer.valueOf(calRest.get(12)));
          roundsConfigEntry.setRestTimeSeconds(Integer.valueOf(calRest.get(13)));
        } 
        if (StringUtils.isNotBlank(goldenPointTimeStr)) {
          Calendar calExtra = Calendar.getInstance();
          calExtra.setTime(MatchLogHistoricalServiceImpl.sdf.parse(goldenPointTimeStr));
          roundsConfigEntry.setGoldenPointTimeMinutes(Integer.valueOf(calExtra.get(12)));
          roundsConfigEntry.setGoldenPointTimeSeconds(Integer.valueOf(calExtra.get(13)));
        } 
      } catch (ParseException parseException) {}
      res.setRoundsConfig(roundsConfigEntry);
      String matchWinnerStr = resultSet.getString("MATCH_WINNER");
      try {
        res.setMatchWinner(StringUtils.isNotBlank(matchWinnerStr) ? MatchWinner.valueOf(matchWinnerStr) : null);
      } catch (IllegalArgumentException e) {
        res.setMatchWinner(null);
      } 
      String matchWinnerByStr = resultSet.getString("MATCH_WINNER_BY");
      try {
        res.setMatchWinnerBy(StringUtils.isNotBlank(matchWinnerByStr) ? FinalDecision.valueOf(matchWinnerByStr) : null);
      } catch (IllegalArgumentException e) {
        res.setMatchWinnerBy(null);
      } 
      res.setMatchResult(resultSet.getString("MATCH_RESULT"));
      res.setRoundsWinners(resultSet.getString("ROUNDS_WINNER"));
      Boolean haveTieBreaker = Boolean.valueOf(resultSet.getBoolean("GDP_TIE_BREAKER"));
      Integer bluePunches = Integer.valueOf(resultSet.getInt("GDP_BLUE_PUNCHES"));
      Integer blueRoundWins = Integer.valueOf(resultSet.getInt("GDP_BLUE_ROUND_WINS"));
      Integer blueHits = Integer.valueOf(resultSet.getInt("GDP_BLUE_HITS"));
      Integer bluePenalties = Integer.valueOf(resultSet.getInt("GDP_BLUE_PENALTIES"));
      Integer redPunches = Integer.valueOf(resultSet.getInt("GDP_RED_PUNCHES"));
      Integer redRoundWins = Integer.valueOf(resultSet.getInt("GDP_RED_ROUND_WINS"));
      Integer redHits = Integer.valueOf(resultSet.getInt("GDP_RED_HITS"));
      Integer redPenalties = Integer.valueOf(resultSet.getInt("GDP_RED_PENALTIES"));
      Integer bluePARATechPoints = Integer.valueOf(resultSet.getInt("BLUE_PARA_TECH_POINTS"));
      Integer redPARATechPoints = Integer.valueOf(resultSet.getInt("RED_PARA_TECH_POINTS"));
      if (haveTieBreaker != null && bluePunches != null && blueRoundWins != null && blueHits != null && bluePenalties != null && redPunches != null && redRoundWins != null && redHits != null && redPenalties != null && bluePARATechPoints != null && redPARATechPoints != null) {
        res.setGoldenPointTieBreakerInfo(new GoldenPointTieBreakerInfoDto(haveTieBreaker, bluePunches, blueRoundWins, blueHits, bluePenalties, redPunches, redRoundWins, redHits, redPenalties, bluePARATechPoints, redPARATechPoints));
      } else {
        res.setGoldenPointTieBreakerInfo(new GoldenPointTieBreakerInfoDto(Boolean.FALSE, Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0)));
      } 
      res.setMaxAllowedGamJeoms(Integer.valueOf(resultSet.getInt("MAX_GAM_JEOMS")));
      try {
        res.setMatchVictoryCriteria(MatchVictoryCriteria.valueOf(resultSet.getString("MATCH_VICTORY_CRITERIA")));
      } catch (IllegalArgumentException iae) {
        res.setMatchVictoryCriteria(MatchVictoryCriteria.CONVENTIONAL);
      } 
      res.setParaTkdMatch(Boolean.valueOf(resultSet.getBoolean("IS_PARATKD_MATCH")));
      return res;
    }
  }
  
  private class MatchLogItemEntryRowMapper implements RowMapper<MatchLogItemDto> {
    private MatchLogItemEntryRowMapper() {}
    
    public MatchLogItemDto mapRow(ResultSet resultSet, int i) throws SQLException {
      MatchLogItemDto res = new MatchLogItemDto();
      res.setId(resultSet.getString("ID"));
      res.setMatchLogId(resultSet.getString("MATCH_LOG_ID"));
      res.setRoundNumber(Integer.valueOf(resultSet.getInt("ROUND_NUMBER")));
      res.setRoundNumberStr(resultSet.getString("ROUND_NUMBER_STR"));
      res.setEventTime(Long.valueOf(resultSet.getLong("EVENT_TIME")));
      res.setRoundTime(Long.valueOf(resultSet.getLong("ROUND_TIME")));
      res.setSystemTime(Long.valueOf(resultSet.getLong("SYSTEM_TIME")));
      try {
        res.setMatchLogItemType(MatchLogItemType.valueOf(resultSet.getString("MATCH_LOG_ITEM_TYPE")));
      } catch (IllegalArgumentException e) {
        res.setMatchLogItemType(MatchLogItemType.NOT_DEFINED);
      } 
      res.setBlueGeneralPoints(Integer.valueOf(resultSet.getInt("BLUE_GEN_POINTS")));
      res.setRedGeneralPoints(Integer.valueOf(resultSet.getInt("RED_GEN_POINTS")));
      res.setBlueAddPoints(Integer.valueOf(resultSet.getInt("BLUE_ADD_POINTS")));
      res.setRedAddPoints(Integer.valueOf(resultSet.getInt("RED_ADD_POINTS")));
      res.setBluePoints(Integer.valueOf(resultSet.getInt("BLUE_POINTS")));
      res.setRedPoints(Integer.valueOf(resultSet.getInt("RED_POINTS")));
      res.setBluePenalties(Integer.valueOf(resultSet.getInt("BLUE_PENALTIES")));
      res.setRedPenalties(Integer.valueOf(resultSet.getInt("RED_PENALTIES")));
      res.setBlueTotalPenalties(Integer.valueOf(resultSet.getInt("BLUE_TOTAL_PENALTIES")));
      res.setRedTotalPenalties(Integer.valueOf(resultSet.getInt("RED_TOTAL_PENALTIES")));
      res.setBlueVideoQuota(Integer.valueOf(resultSet.getInt("BLUE_VIDEO_QUOTA")));
      res.setRedVideoQuota(Integer.valueOf(resultSet.getInt("RED_VIDEO_QUOTA")));
      res.setBlueGoldenPointHits(Integer.valueOf(resultSet.getInt("BLUE_GOLDENPOINT_HITS")));
      res.setRedGoldenPointHits(Integer.valueOf(resultSet.getInt("RED_GOLDENPOINT_HITS")));
      res.setBlueGoldenPointPenalties(Integer.valueOf(resultSet.getInt("BLUE_GOLDENPOINT_PENALTIES")));
      res.setRedGoldenPointPenalties(Integer.valueOf(resultSet.getInt("RED_GOLDENPOINT_PENALTIES")));
      res.setGoldenPointRound(Boolean.valueOf(resultSet.getBoolean("IS_GOLDENPOINT_ROUND")));
      res.setEntryValue(resultSet.getString("ENTRY_VALUE"));
      return res;
    }
  }
}
