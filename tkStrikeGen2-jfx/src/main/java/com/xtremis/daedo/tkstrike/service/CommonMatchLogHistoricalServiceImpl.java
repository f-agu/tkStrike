package com.xtremis.daedo.tkstrike.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.xtremis.daedo.tkstrike.om.CommonMatchLogDto;
import com.xtremis.daedo.tkstrike.om.CommonMatchLogItemDto;
import com.xtremis.daedo.tkstrike.om.xls.DataTableRoundInfoItem;
import com.xtremis.daedo.tkstrike.om.xls.MatchHistoryItem;
import com.xtremis.daedo.tkstrike.om.xls.MatchInfo;
import com.xtremis.daedo.tkstrike.orm.model.MatchLogItemType;
import com.xtremis.daedo.tkstrike.utils.MatchLogExporterUtil;


public abstract class CommonMatchLogHistoricalServiceImpl<D extends CommonMatchLogDto, ID extends CommonMatchLogItemDto, MS extends CommonMatchLogService<D, ID>>
		implements CommonMatchLogHistoricalService<D, ID> {

	private final SimpleDateFormat sdf4Directory = new SimpleDateFormat("yyyyMMdd");

	private final DataSource dataSource;

	private final ExternalConfigService externalConfigService;

	private final MatchLogExporterUtil matchLogExporterUtil;

	public CommonMatchLogHistoricalServiceImpl(DataSource dataSource, ExternalConfigService externalConfigService,
			MatchLogExporterUtil matchLogExporterUtil) {
		this.dataSource = dataSource;
		this.externalConfigService = externalConfigService;
		this.matchLogExporterUtil = matchLogExporterUtil;
	}

	abstract MS getMatchLogService();

	abstract RowMapper<D> getMatchLogRowMapper();

	abstract RowMapper<ID> getMatchLogItemRowMapper();

	abstract String getHistoMatchLogTableName();

	abstract String getHistoMatchLogItemTableName();

	abstract void _migrateToHistorical(List<D> paramList);

	abstract void putAthletesInfo(D paramD, DataTableRoundInfoItem paramDataTableRoundInfoItem);

	abstract String getMatchWeight(@Nonnull D paramD);

	abstract String getMatchDivision(@Nonnull D paramD);

	abstract Integer getMinBodyLevel(@Nonnull D paramD);

	abstract Integer getMinHeadLevel(@Nonnull D paramD);

	abstract String getMatchBlueName(@Nonnull D paramD);

	abstract String getMatchBlueFlagName(@Nonnull D paramD);

	abstract String getMatchBlueFlagAbbreviation(@Nonnull D paramD);

	abstract String getMatchRedName(@Nonnull D paramD);

	abstract String getMatchRedFlagName(@Nonnull D paramD);

	abstract String getMatchRedFlagAbbreviation(@Nonnull D paramD);

	@Override
	public final void exportAllMatchLog2DefaultDirectory() throws TkStrikeServiceException {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
		String sqlQuery = "SELECT ID FROM " + getHistoMatchLogTableName();
		List<String> matchLogIds = jdbcTemplate.queryForList(sqlQuery, null, String.class);
		for(String matchLogId : matchLogIds)
			exportMatchLog2DefaultDirectory(matchLogId);
	}

	@Override
	public final void exportMatchLog2DefaultDirectory(String matchLogId) throws TkStrikeServiceException {
		exportMatchLog(matchLogId, getDefaultExportDirectory());
	}

	@Override
	public final void exportMatchLog(String matchLogId, File targetDirectory) throws TkStrikeServiceException {
		if(matchLogId != null && targetDirectory != null) {
			D matchLogEntry = getMatchLog(matchLogId);
			List<ID> matchLogItemEntries = findItemsByMatchLogId(matchLogId);
			if(matchLogEntry != null && matchLogItemEntries != null)
				this.matchLogExporterUtil.exportToCSVFile(matchLogEntry, matchLogItemEntries, targetDirectory);
		}
	}

	@Override
	public final void exportMatchLogXLS(String matchLogId, File targetDirectory) throws TkStrikeServiceException {
		if(matchLogId != null && targetDirectory != null) {
			D matchLogEntry = getMatchLog(matchLogId);
			List<ID> matchLogItemEntries = findItemsByMatchLogId(matchLogId);
			if(matchLogEntry != null && matchLogItemEntries != null) {
				matchLogItemEntries.sort(getMatchLogService().getComparator4Items());
				MatchInfo matchInfo = generateMatchInfo(matchLogEntry);
				Map<Integer, DataTableRoundInfoItem> dataTablesItems = generateDataTableRoundInfos(matchLogEntry, matchLogItemEntries, matchInfo);
				List<MatchHistoryItem> matchHistoryItems = new ArrayList<>();
				ID prevMatchLogItem = null;
				int nOrder = 0;
				for(CommonMatchLogItemDto commonMatchLogItemDto : matchLogItemEntries) {
					if(MatchLogItemType.START_ROUND.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.END_ROUND
							.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.RESUME
									.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.TIMEOUT
											.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.KYE_SHI
													.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.DOCTOR
															.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.NETWORK_ERROR
																	.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.MEETING
																			.equals(commonMatchLogItemDto.getMatchLogItemType())
							|| MatchLogItemType.BLUE_VIDEO_REQUEST
									.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.RED_VIDEO_REQUEST
											.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.BLUE_JUDGE_PUNCH

													.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.RED_JUDGE_PUNCH
															.equals(commonMatchLogItemDto.getMatchLogItemType())
							|| MatchLogItemType.BLUE_JUDGE_BODY_TECH
									.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.RED_JUDGE_BODY_TECH
											.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.BLUE_JUDGE_HEAD_TECH
													.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.RED_JUDGE_HEAD_TECH
															.equals(commonMatchLogItemDto.getMatchLogItemType())
							|| MatchLogItemType.BLUE_PARA_SPINNING_KICK

									.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.RED_PARA_SPINNING_KICK
											.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.BLUE_PARA_TURNING_KICK
													.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.RED_PARA_TURNING_KICK
															.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.BLUE_PUNCH_POINT

																	.equals(commonMatchLogItemDto.getMatchLogItemType())
							|| MatchLogItemType.RED_PUNCH_POINT
									.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.BLUE_BODY_POINT
											.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.RED_BODY_POINT
													.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.BLUE_HEAD_POINT
															.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.RED_HEAD_POINT
																	.equals(commonMatchLogItemDto.getMatchLogItemType())
							|| MatchLogItemType.BLUE_BODY_TECH_POINT
									.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.RED_BODY_TECH_POINT
											.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.BLUE_HEAD_TECH_POINT
													.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.RED_HEAD_TECH_POINT
															.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.BLUE_BODY_HIT

																	.equals(commonMatchLogItemDto.getMatchLogItemType())
							|| MatchLogItemType.BLUE_HEAD_HIT
									.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.RED_BODY_HIT
											.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.RED_HEAD_HIT
													.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.BLUE_ADD_GAME_JEON

															.equals(commonMatchLogItemDto.getMatchLogItemType())
							|| MatchLogItemType.BLUE_REMOVE_GAME_JEON
									.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.RED_ADD_GAME_JEON
											.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.RED_REMOVE_GAME_JEON
													.equals(commonMatchLogItemDto.getMatchLogItemType())) {
						MatchHistoryItem item = new MatchHistoryItem();
						item.setOrder(Integer.valueOf(nOrder));
						item.setRoundStr(commonMatchLogItemDto.getRoundNumberStr());
						item.setRoundNumber(commonMatchLogItemDto.getRoundNumber());
						item.setAction(getMatchHistoryAction(commonMatchLogItemDto.getMatchLogItemType(), commonMatchLogItemDto.getRoundNumber()));
						item.setTiming(new Date(commonMatchLogItemDto.getRoundTime().longValue()));
						item.setTime(new Date(commonMatchLogItemDto.getSystemTime().longValue()));
						item.setSource(getMatchHistorySource(commonMatchLogItemDto.getMatchLogItemType(), "SCOREBOARD_EDITOR".equals(
								commonMatchLogItemDto.getEntryValue())));
						item.setScore(commonMatchLogItemDto.getBlueGeneralPoints() + " - " + commonMatchLogItemDto.getRedGeneralPoints());
						fillJudgesInfoIfNeed((ID)commonMatchLogItemDto, item);
						if(MatchLogItemType.BLUE_BODY_HIT.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.BLUE_HEAD_HIT
								.equals(commonMatchLogItemDto.getMatchLogItemType())) {
							item.setBlueHit(Integer.valueOf(Integer.parseInt(commonMatchLogItemDto.getEntryValue())));
						} else if(MatchLogItemType.RED_BODY_HIT.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.RED_HEAD_HIT
								.equals(commonMatchLogItemDto.getMatchLogItemType())) {
							item.setRedHit(Integer.valueOf(Integer.parseInt(commonMatchLogItemDto.getEntryValue())));
						}
						if(MatchLogItemType.BLUE_ADD_GAME_JEON.equals(commonMatchLogItemDto.getMatchLogItemType())
								|| MatchLogItemType.BLUE_REMOVE_GAME_JEON
										.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.RED_ADD_GAME_JEON
												.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.RED_REMOVE_GAME_JEON
														.equals(commonMatchLogItemDto.getMatchLogItemType())) {
							boolean isBlue = commonMatchLogItemDto.getMatchLogItemType().toString().startsWith("BLUE_");
							boolean isAdd = commonMatchLogItemDto.getMatchLogItemType().toString().contains("_ADD_");
							if(isBlue) {
								item.setRedPoint(Integer.valueOf(isAdd ? 1 : - 1));
							} else {
								item.setBluePoint(Integer.valueOf(isAdd ? 1 : - 1));
							}
						}
						boolean removePrevious = fillExtraPointInfoIfNeed((ID)commonMatchLogItemDto, item, prevMatchLogItem);
						if(removePrevious)
							matchHistoryItems.remove(matchHistoryItems.size() - 1);
						matchHistoryItems.add(item);
						nOrder++;
						if(MatchLogItemType.BLUE_BODY_HIT.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.BLUE_HEAD_HIT
								.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.RED_BODY_HIT
										.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.RED_HEAD_HIT
												.equals(commonMatchLogItemDto.getMatchLogItemType())) {
							CommonMatchLogItemDto commonMatchLogItemDto1 = commonMatchLogItemDto;
							continue;
						}
						prevMatchLogItem = null;
					}
				}
				this.matchLogExporterUtil.exportToXLSFile(matchLogEntry, matchInfo, dataTablesItems, matchHistoryItems, targetDirectory);
			}
		}
	}

	private boolean fillExtraPointInfoIfNeed(ID matchLogItem, MatchHistoryItem item, ID prevMatchLogItem) {
		boolean res = false;
		if(MatchLogItemType.BLUE_PUNCH_POINT.equals(matchLogItem.getMatchLogItemType()) || MatchLogItemType.RED_PUNCH_POINT
				.equals(matchLogItem.getMatchLogItemType()) || MatchLogItemType.BLUE_BODY_POINT
						.equals(matchLogItem.getMatchLogItemType()) || MatchLogItemType.RED_BODY_POINT
								.equals(matchLogItem.getMatchLogItemType()) || MatchLogItemType.BLUE_HEAD_POINT
										.equals(matchLogItem.getMatchLogItemType()) || MatchLogItemType.RED_HEAD_POINT
												.equals(matchLogItem.getMatchLogItemType()) || MatchLogItemType.BLUE_BODY_TECH_POINT
														.equals(matchLogItem.getMatchLogItemType()) || MatchLogItemType.RED_BODY_TECH_POINT
																.equals(matchLogItem.getMatchLogItemType()) || MatchLogItemType.BLUE_HEAD_TECH_POINT
																		.equals(matchLogItem.getMatchLogItemType())
				|| MatchLogItemType.RED_HEAD_TECH_POINT
						.equals(matchLogItem.getMatchLogItemType())) {
			if(matchLogItem.getBlueAddPoints().intValue() < 0 || matchLogItem.getRedAddPoints().intValue() < 0)
				item.setAction("Remove " + item.getAction());
			if(matchLogItem.getBlueAddPoints().intValue() > 0 || matchLogItem.getBlueAddPoints().intValue() < 0)
				item.setBluePoint(matchLogItem.getBlueAddPoints());
			if(matchLogItem.getRedAddPoints().intValue() > 0 || matchLogItem.getRedAddPoints().intValue() < 0)
				item.setRedPoint(matchLogItem.getRedAddPoints());
			if(prevMatchLogItem != null)
				if(MatchLogItemType.BLUE_BODY_HIT.equals(prevMatchLogItem.getMatchLogItemType()) || MatchLogItemType.BLUE_HEAD_HIT
						.equals(prevMatchLogItem.getMatchLogItemType())) {
					item.setBlueHit(Integer.valueOf(Integer.parseInt(prevMatchLogItem.getEntryValue())));
					res = true;
				} else if(MatchLogItemType.RED_BODY_HIT.equals(prevMatchLogItem.getMatchLogItemType()) || MatchLogItemType.RED_HEAD_HIT
						.equals(prevMatchLogItem.getMatchLogItemType())) {
					item.setRedHit(Integer.valueOf(Integer.parseInt(prevMatchLogItem.getEntryValue())));
					res = true;
				}
		}
		return res;
	}

	private void fillJudgesInfoIfNeed(ID matchLogItem, MatchHistoryItem item) {
		if(MatchLogItemType.BLUE_JUDGE_PUNCH.equals(matchLogItem.getMatchLogItemType()) || MatchLogItemType.BLUE_JUDGE_BODY_TECH
				.equals(matchLogItem.getMatchLogItemType()) || MatchLogItemType.BLUE_JUDGE_HEAD_TECH
						.equals(matchLogItem.getMatchLogItemType()) || MatchLogItemType.RED_JUDGE_PUNCH
								.equals(matchLogItem.getMatchLogItemType()) || MatchLogItemType.RED_JUDGE_BODY_TECH
										.equals(matchLogItem.getMatchLogItemType()) || MatchLogItemType.RED_JUDGE_HEAD_TECH
												.equals(matchLogItem.getMatchLogItemType()) || MatchLogItemType.BLUE_PARA_TURNING_KICK

														.equals(matchLogItem.getMatchLogItemType()) || MatchLogItemType.BLUE_PARA_SPINNING_KICK
																.equals(matchLogItem.getMatchLogItemType()) || MatchLogItemType.RED_PARA_TURNING_KICK
																		.equals(matchLogItem.getMatchLogItemType())
				|| MatchLogItemType.RED_PARA_SPINNING_KICK
						.equals(matchLogItem.getMatchLogItemType())) {
			String str2Put = (MatchLogItemType.BLUE_JUDGE_PUNCH.equals(matchLogItem.getMatchLogItemType()) || MatchLogItemType.RED_JUDGE_PUNCH.equals(
					matchLogItem.getMatchLogItemType())) ? "P" : "T";
			boolean isBlue = (MatchLogItemType.BLUE_JUDGE_PUNCH.equals(matchLogItem.getMatchLogItemType()) || MatchLogItemType.BLUE_JUDGE_BODY_TECH
					.equals(matchLogItem.getMatchLogItemType()) || MatchLogItemType.BLUE_JUDGE_HEAD_TECH.equals(matchLogItem.getMatchLogItemType())
					|| MatchLogItemType.BLUE_PARA_SPINNING_KICK.equals(matchLogItem.getMatchLogItemType()) || MatchLogItemType.BLUE_PARA_TURNING_KICK
							.equals(matchLogItem.getMatchLogItemType()));
			if("1".equals(matchLogItem.getEntryValue())) {
				if(isBlue) {
					item.setBlueJudge1(str2Put);
				} else {
					item.setRedJudge1(str2Put);
				}
			} else if("2".equals(matchLogItem.getEntryValue())) {
				if(isBlue) {
					item.setBlueJudge2(str2Put);
				} else {
					item.setRedJudge2(str2Put);
				}
			} else if("3".equals(matchLogItem.getEntryValue())) {
				if(isBlue) {
					item.setBlueJudge3(str2Put);
				} else {
					item.setRedJudge3(str2Put);
				}
			}
		}
	}

	private String getMatchHistoryAction(MatchLogItemType matchLogItemType, Integer roundNumber) {
		String res = "";
		switch(matchLogItemType) {
			case START_ROUND:
				res = "Start round " + roundNumber;
				break;
			case END_ROUND:
				res = "End round " + roundNumber;
				break;
			case RESUME:
				res = "Match resume";
				break;
			case TIMEOUT:
				res = "Time out";
				break;
			case KYE_SHI:
				res = "Kye-shi";
				break;
			case DOCTOR:
				res = "Call for doctor";
				break;
			case NETWORK_ERROR:
				res = "Technical issue";
				break;
			case MEETING:
				res = "Call for meeting";
				break;
			case BLUE_VIDEO_REQUEST:
				res = "BLUE IVR";
				break;
			case RED_VIDEO_REQUEST:
				res = "RED IVR";
				break;
			case BLUE_JUDGE_PUNCH:
				res = "Blue punch judge";
				break;
			case RED_JUDGE_PUNCH:
				res = "Red punch judge";
				break;
			case BLUE_JUDGE_BODY_TECH:
			case BLUE_JUDGE_HEAD_TECH:
			case BLUE_PARA_SPINNING_KICK:
			case BLUE_PARA_TURNING_KICK:
				res = "Blue tech judge";
				break;
			case RED_JUDGE_BODY_TECH:
			case RED_JUDGE_HEAD_TECH:
			case RED_PARA_SPINNING_KICK:
			case RED_PARA_TURNING_KICK:
				res = "Red tech judge";
				break;
			case BLUE_PUNCH_POINT:
				res = "Blue punch point";
				break;
			case RED_PUNCH_POINT:
				res = "Red punch point";
				break;
			case BLUE_BODY_POINT:
				res = "Blue body point";
				break;
			case RED_BODY_POINT:
				res = "Red body point";
				break;
			case BLUE_HEAD_POINT:
				res = "Blue head point";
				break;
			case RED_HEAD_POINT:
				res = "Red head point";
				break;
			case BLUE_BODY_TECH_POINT:
			case BLUE_HEAD_TECH_POINT:
				res = "Blue tech point";
				break;
			case RED_BODY_TECH_POINT:
			case RED_HEAD_TECH_POINT:
				res = "Red tech point";
				break;
			case BLUE_BODY_HIT:
				res = "Blue body hit";
				break;
			case BLUE_HEAD_HIT:
				res = "Blue head hit";
				break;
			case RED_BODY_HIT:
				res = "Red body hit";
				break;
			case RED_HEAD_HIT:
				res = "Red head hit";
				break;
			case BLUE_ADD_GAME_JEON:
				res = "Blue gam-jeom";
				break;
			case BLUE_REMOVE_GAME_JEON:
				res = "Remove blue gam-jeom";
				break;
			case RED_ADD_GAME_JEON:
				res = "Red gam-jeom";
				break;
			case RED_REMOVE_GAME_JEON:
				res = "Remove red gam-jeom";
				break;
		}
		return res;
	}

	private String getMatchHistorySource(MatchLogItemType matchLogItemType, boolean fromScoreBoardEditor) {
		String res = "";
		switch(matchLogItemType) {
			case START_ROUND:
			case END_ROUND:
			case RESUME:
			case TIMEOUT:
			case NETWORK_ERROR:
				res = "S";
				break;
			case KYE_SHI:
			case DOCTOR:
			case MEETING:
				res = "CR";
				break;
			case BLUE_VIDEO_REQUEST:
				res = "CB";
				break;
			case RED_VIDEO_REQUEST:
				res = "CR";
				break;
			case BLUE_JUDGE_PUNCH:
			case RED_JUDGE_PUNCH:
			case BLUE_JUDGE_BODY_TECH:
			case BLUE_JUDGE_HEAD_TECH:
			case BLUE_PARA_SPINNING_KICK:
			case BLUE_PARA_TURNING_KICK:
			case RED_JUDGE_BODY_TECH:
			case RED_JUDGE_HEAD_TECH:
			case RED_PARA_SPINNING_KICK:
			case RED_PARA_TURNING_KICK:
			case BLUE_PUNCH_POINT:
			case RED_PUNCH_POINT:
			case BLUE_BODY_TECH_POINT:
			case BLUE_HEAD_TECH_POINT:
			case RED_BODY_TECH_POINT:
			case RED_HEAD_TECH_POINT:
				res = "J";
				break;
			case BLUE_BODY_POINT:
			case RED_BODY_POINT:
				res = ! fromScoreBoardEditor ? "BP" : "J";
				break;
			case BLUE_HEAD_POINT:
			case RED_HEAD_POINT:
				res = ! fromScoreBoardEditor ? "HP" : "J";
				break;
			case BLUE_BODY_HIT:
			case RED_BODY_HIT:
				res = "BH";
				break;
			case BLUE_HEAD_HIT:
			case RED_HEAD_HIT:
				res = "HH";
				break;
			case BLUE_ADD_GAME_JEON:
			case BLUE_REMOVE_GAME_JEON:
			case RED_ADD_GAME_JEON:
			case RED_REMOVE_GAME_JEON:
				res = "CR";
				break;
		}
		return res;
	}

	private MatchInfo generateMatchInfo(D matchLogEntry) {
		MatchInfo matchInfo = new MatchInfo();
		matchInfo.setMatchNumber(matchLogEntry.getMatchNumber());
		matchInfo.setMatchDate(new Date(matchLogEntry.getMatchStartTime().longValue()));
		matchInfo.setWeightName(getMatchWeight(matchLogEntry));
		matchInfo.setPhaseName(matchLogEntry.getPhaseName());
		matchInfo.setDivisionName(getMatchDivision(matchLogEntry));
		matchInfo.setBodyMinLevel(getMinBodyLevel(matchLogEntry));
		matchInfo.setHeadMinLevel(getMinHeadLevel(matchLogEntry));
		matchInfo.setMatchStartTimestamp(matchLogEntry.getMatchStartTime());
		matchInfo.setMatchEndTimestamp(matchLogEntry.getMatchEndTime());
		matchInfo.setBlueName(getMatchBlueName(matchLogEntry));
		matchInfo.setBlueFlagName(getMatchBlueFlagName(matchLogEntry));
		matchInfo.setBlueFlagAbbreviation(getMatchBlueFlagAbbreviation(matchLogEntry));
		matchInfo.setRedName(getMatchRedName(matchLogEntry));
		matchInfo.setRedFlagName(getMatchRedFlagName(matchLogEntry));
		matchInfo.setRedFlagAbbreviation(getMatchRedFlagAbbreviation(matchLogEntry));
		matchInfo.setMatchDuration(Duration.between(Instant.ofEpochMilli(matchInfo.getMatchStartTimestamp().longValue()), Instant.ofEpochMilli(
				matchInfo.getMatchEndTimestamp().longValue())));
		return matchInfo;
	}

	private Map<Integer, DataTableRoundInfoItem> generateDataTableRoundInfos(D matchLogEntry, List<ID> matchLogItemEntries, MatchInfo matchInfo) {
		Map<Integer, DataTableRoundInfoItem> dataTablesItems = new HashMap<>(matchLogEntry.getRoundsConfig().getRounds().intValue() + 1);
		int matchRounds = matchLogEntry.getRoundsConfig().getGoldenPointEnabled().booleanValue() ? (matchLogEntry.getRoundsConfig().getRounds()
				.intValue() + 1) : matchLogEntry.getRoundsConfig().getRounds().intValue();
		for(int i = 1; i <= matchRounds; i++) {
			DataTableRoundInfoItem dataTableRoundInfoItem = new DataTableRoundInfoItem(Integer.valueOf(i));
			dataTableRoundInfoItem.setRoundStr("" + i);
			putAthletesInfo(matchLogEntry, dataTableRoundInfoItem);
			dataTablesItems.put(Integer.valueOf(i), dataTableRoundInfoItem);
		}
		dataTablesItems.put(Integer.valueOf(999), new DataTableRoundInfoItem(Integer.valueOf(999)));
		DataTableRoundInfoItem totalInfoItem = dataTablesItems.get(Integer.valueOf(999));
		totalInfoItem.setRoundStr("TOTAL");
		putAthletesInfo(matchLogEntry, totalInfoItem);
		for(CommonMatchLogItemDto commonMatchLogItemDto : matchLogItemEntries) {
			DataTableRoundInfoItem roundInfoItem = dataTablesItems.get(commonMatchLogItemDto.getRoundNumber());
			if(roundInfoItem != null) {
				if(MatchLogItemType.BLUE_PUNCH_POINT.equals(commonMatchLogItemDto.getMatchLogItemType())) {
					if("SCOREBOARD_EDITOR".equals(commonMatchLogItemDto.getEntryValue())) {
						if(commonMatchLogItemDto.getBlueAddPoints().intValue() >= 0) {
							roundInfoItem.setBluePointPlusDeskPoint(Integer.valueOf(roundInfoItem.getBluePointPlusDeskAction().intValue()
									+ commonMatchLogItemDto.getBlueAddPoints().intValue()));
							totalInfoItem.setBluePointPlusDeskPoint(Integer.valueOf(totalInfoItem.getBluePointPlusDeskAction().intValue()
									+ commonMatchLogItemDto.getBlueAddPoints().intValue()));
						} else {
							roundInfoItem.setBluePointMinusDeskAction(Integer.valueOf(roundInfoItem.getBluePointMinusDeskAction().intValue()
									+ commonMatchLogItemDto.getBlueAddPoints().intValue()));
							totalInfoItem.setBluePointMinusDeskAction(Integer.valueOf(totalInfoItem.getBluePointMinusDeskAction().intValue()
									+ commonMatchLogItemDto.getBlueAddPoints().intValue()));
						}
					} else {
						roundInfoItem.setBluePunchPoint(Integer.valueOf(roundInfoItem.getBluePunchPoint().intValue() + commonMatchLogItemDto
								.getBlueAddPoints().intValue()));
						totalInfoItem.setBluePunchPoint(Integer.valueOf(totalInfoItem.getBluePunchPoint().intValue() + commonMatchLogItemDto
								.getBlueAddPoints().intValue()));
					}
					roundInfoItem.setBlueTotal(Integer.valueOf(roundInfoItem.getBlueTotal().intValue() + commonMatchLogItemDto.getBlueAddPoints()
							.intValue()));
					totalInfoItem.setBlueTotal(Integer.valueOf(totalInfoItem.getBlueTotal().intValue() + commonMatchLogItemDto.getBlueAddPoints()
							.intValue()));
					continue;
				}
				if(MatchLogItemType.BLUE_JUDGE_PUNCH.equals(commonMatchLogItemDto.getMatchLogItemType())) {
					roundInfoItem.setBluePunchAction(Integer.valueOf(roundInfoItem.getBluePunchAction().intValue() + 1));
					totalInfoItem.setBluePunchAction(Integer.valueOf(totalInfoItem.getBluePunchAction().intValue() + 1));
					continue;
				}
				if(MatchLogItemType.RED_PUNCH_POINT.equals(commonMatchLogItemDto.getMatchLogItemType())) {
					if("SCOREBOARD_EDITOR".equals(commonMatchLogItemDto.getEntryValue())) {
						if(commonMatchLogItemDto.getRedAddPoints().intValue() >= 0) {
							roundInfoItem.setRedPointPlusDeskPoint(Integer.valueOf(roundInfoItem.getRedPointPlusDeskAction().intValue()
									+ commonMatchLogItemDto.getRedAddPoints().intValue()));
							totalInfoItem.setRedPointPlusDeskPoint(Integer.valueOf(totalInfoItem.getRedPointPlusDeskAction().intValue()
									+ commonMatchLogItemDto.getRedAddPoints().intValue()));
						} else {
							roundInfoItem.setRedPointMinusDeskAction(Integer.valueOf(roundInfoItem.getRedPointMinusDeskAction().intValue()
									+ commonMatchLogItemDto.getRedAddPoints().intValue()));
							totalInfoItem.setRedPointMinusDeskAction(Integer.valueOf(totalInfoItem.getRedPointMinusDeskAction().intValue()
									+ commonMatchLogItemDto.getRedAddPoints().intValue()));
						}
					} else {
						roundInfoItem.setRedPunchPoint(Integer.valueOf(roundInfoItem.getRedPunchPoint().intValue() + commonMatchLogItemDto
								.getRedAddPoints().intValue()));
						totalInfoItem.setRedPunchPoint(Integer.valueOf(totalInfoItem.getRedPunchPoint().intValue() + commonMatchLogItemDto
								.getRedAddPoints().intValue()));
					}
					roundInfoItem.setRedTotal(Integer.valueOf(roundInfoItem.getRedTotal().intValue() + commonMatchLogItemDto.getRedAddPoints()
							.intValue()));
					totalInfoItem.setRedTotal(Integer.valueOf(totalInfoItem.getRedTotal().intValue() + commonMatchLogItemDto.getRedAddPoints()
							.intValue()));
					continue;
				}
				if(MatchLogItemType.RED_JUDGE_PUNCH.equals(commonMatchLogItemDto.getMatchLogItemType())) {
					roundInfoItem.setRedPunchAction(Integer.valueOf(roundInfoItem.getRedPunchAction().intValue() + 1));
					totalInfoItem.setRedPunchAction(Integer.valueOf(totalInfoItem.getRedPunchAction().intValue() + 1));
					continue;
				}
				if(MatchLogItemType.BLUE_BODY_POINT.equals(commonMatchLogItemDto.getMatchLogItemType())) {
					if("SCOREBOARD_EDITOR".equals(commonMatchLogItemDto.getEntryValue())) {
						if(commonMatchLogItemDto.getBlueAddPoints().intValue() >= 0) {
							roundInfoItem.setBluePointPlusDeskPoint(Integer.valueOf(roundInfoItem.getBluePointPlusDeskAction().intValue()
									+ commonMatchLogItemDto.getBlueAddPoints().intValue()));
							totalInfoItem.setBluePointPlusDeskPoint(Integer.valueOf(totalInfoItem.getBluePointPlusDeskAction().intValue()
									+ commonMatchLogItemDto.getBlueAddPoints().intValue()));
						} else {
							roundInfoItem.setBluePointMinusDeskPoint(Integer.valueOf(roundInfoItem.getBluePointMinusDeskPoint().intValue()
									+ commonMatchLogItemDto.getBlueAddPoints().intValue()));
							totalInfoItem.setBluePointMinusDeskPoint(Integer.valueOf(totalInfoItem.getBluePointMinusDeskPoint().intValue()
									+ commonMatchLogItemDto.getBlueAddPoints().intValue()));
						}
					} else {
						roundInfoItem.setBlueBodyKickPoint(Integer.valueOf(roundInfoItem.getBlueBodyKickPoint().intValue() + commonMatchLogItemDto
								.getBlueAddPoints().intValue()));
						totalInfoItem.setBlueBodyKickPoint(Integer.valueOf(totalInfoItem.getBlueBodyKickPoint().intValue() + commonMatchLogItemDto
								.getBlueAddPoints().intValue()));
					}
					roundInfoItem.setBlueTotal(Integer.valueOf(roundInfoItem.getBlueTotal().intValue() + commonMatchLogItemDto.getBlueAddPoints()
							.intValue()));
					totalInfoItem.setBlueTotal(Integer.valueOf(totalInfoItem.getBlueTotal().intValue() + commonMatchLogItemDto.getBlueAddPoints()
							.intValue()));
					continue;
				}
				if(MatchLogItemType.BLUE_BODY_HIT.equals(commonMatchLogItemDto.getMatchLogItemType())) {
					roundInfoItem.setBlueBodyKickAction(Integer.valueOf(roundInfoItem.getBlueBodyKickAction().intValue() + 1));
					totalInfoItem.setBlueBodyKickAction(Integer.valueOf(totalInfoItem.getBlueBodyKickAction().intValue() + 1));
					continue;
				}
				if(MatchLogItemType.RED_BODY_POINT.equals(commonMatchLogItemDto.getMatchLogItemType())) {
					if("SCOREBOARD_EDITOR".equals(commonMatchLogItemDto.getEntryValue())) {
						if(commonMatchLogItemDto.getRedAddPoints().intValue() >= 0) {
							roundInfoItem.setRedPointPlusDeskPoint(Integer.valueOf(roundInfoItem.getRedPointPlusDeskAction().intValue()
									+ commonMatchLogItemDto.getRedAddPoints().intValue()));
							totalInfoItem.setRedPointPlusDeskPoint(Integer.valueOf(totalInfoItem.getRedPointPlusDeskAction().intValue()
									+ commonMatchLogItemDto.getRedAddPoints().intValue()));
						} else {
							roundInfoItem.setRedPointMinusDeskPoint(Integer.valueOf(roundInfoItem.getRedPointMinusDeskPoint().intValue()
									+ commonMatchLogItemDto.getRedAddPoints().intValue()));
							totalInfoItem.setRedPointMinusDeskPoint(Integer.valueOf(totalInfoItem.getRedPointMinusDeskPoint().intValue()
									+ commonMatchLogItemDto.getRedAddPoints().intValue()));
						}
					} else {
						roundInfoItem.setRedBodyKickPoint(Integer.valueOf(roundInfoItem.getRedBodyKickPoint().intValue() + commonMatchLogItemDto
								.getRedAddPoints().intValue()));
						totalInfoItem.setRedBodyKickPoint(Integer.valueOf(totalInfoItem.getRedBodyKickPoint().intValue() + commonMatchLogItemDto
								.getRedAddPoints().intValue()));
					}
					roundInfoItem.setRedTotal(Integer.valueOf(roundInfoItem.getRedTotal().intValue() + commonMatchLogItemDto.getRedAddPoints()
							.intValue()));
					totalInfoItem.setRedTotal(Integer.valueOf(totalInfoItem.getRedTotal().intValue() + commonMatchLogItemDto.getRedAddPoints()
							.intValue()));
					continue;
				}
				if(MatchLogItemType.RED_BODY_HIT.equals(commonMatchLogItemDto.getMatchLogItemType())) {
					roundInfoItem.setRedBodyKickAction(Integer.valueOf(roundInfoItem.getRedBodyKickAction().intValue() + 1));
					totalInfoItem.setRedBodyKickAction(Integer.valueOf(totalInfoItem.getRedBodyKickAction().intValue() + 1));
					continue;
				}
				if(MatchLogItemType.BLUE_BODY_TECH_POINT.equals(commonMatchLogItemDto.getMatchLogItemType())) {
					if("SCOREBOARD_EDITOR".equals(commonMatchLogItemDto.getEntryValue())) {
						if(commonMatchLogItemDto.getBlueAddPoints().intValue() >= 0) {
							roundInfoItem.setBluePointPlusDeskPoint(Integer.valueOf(roundInfoItem.getBluePointPlusDeskAction().intValue()
									+ commonMatchLogItemDto.getBlueAddPoints().intValue()));
							totalInfoItem.setBluePointPlusDeskPoint(Integer.valueOf(totalInfoItem.getBluePointPlusDeskAction().intValue()
									+ commonMatchLogItemDto.getBlueAddPoints().intValue()));
						} else {
							roundInfoItem.setBluePointMinusDeskPoint(Integer.valueOf(roundInfoItem.getBluePointMinusDeskPoint().intValue()
									+ commonMatchLogItemDto.getBlueAddPoints().intValue()));
							totalInfoItem.setBluePointMinusDeskPoint(Integer.valueOf(totalInfoItem.getBluePointMinusDeskPoint().intValue()
									+ commonMatchLogItemDto.getBlueAddPoints().intValue()));
						}
					} else {
						roundInfoItem.setBlueTurningBodyKickPoint(Integer.valueOf(roundInfoItem.getBlueTurningBodyKickPoint().intValue()
								+ commonMatchLogItemDto.getBlueAddPoints().intValue()));
						totalInfoItem.setBlueTurningBodyKickPoint(Integer.valueOf(totalInfoItem.getBlueTurningBodyKickPoint().intValue()
								+ commonMatchLogItemDto.getBlueAddPoints().intValue()));
					}
					roundInfoItem.setBlueTotal(Integer.valueOf(roundInfoItem.getBlueTotal().intValue() + commonMatchLogItemDto.getBlueAddPoints()
							.intValue()));
					totalInfoItem.setBlueTotal(Integer.valueOf(totalInfoItem.getBlueTotal().intValue() + commonMatchLogItemDto.getBlueAddPoints()
							.intValue()));
					continue;
				}
				if(MatchLogItemType.RED_BODY_TECH_POINT.equals(commonMatchLogItemDto.getMatchLogItemType())) {
					if("SCOREBOARD_EDITOR".equals(commonMatchLogItemDto.getEntryValue())) {
						if(commonMatchLogItemDto.getRedAddPoints().intValue() >= 0) {
							roundInfoItem.setRedPointPlusDeskPoint(Integer.valueOf(roundInfoItem.getRedPointPlusDeskAction().intValue()
									+ commonMatchLogItemDto.getRedAddPoints().intValue()));
							totalInfoItem.setRedPointPlusDeskPoint(Integer.valueOf(totalInfoItem.getRedPointPlusDeskAction().intValue()
									+ commonMatchLogItemDto.getRedAddPoints().intValue()));
						} else {
							roundInfoItem.setRedPointMinusDeskPoint(Integer.valueOf(roundInfoItem.getRedPointMinusDeskPoint().intValue()
									+ commonMatchLogItemDto.getRedAddPoints().intValue()));
							totalInfoItem.setRedPointMinusDeskPoint(Integer.valueOf(totalInfoItem.getRedPointMinusDeskPoint().intValue()
									+ commonMatchLogItemDto.getRedAddPoints().intValue()));
						}
					} else {
						roundInfoItem.setRedTurningBodyKickPoint(Integer.valueOf(roundInfoItem.getRedTurningBodyKickPoint().intValue()
								+ commonMatchLogItemDto.getRedAddPoints().intValue()));
						totalInfoItem.setRedTurningBodyKickPoint(Integer.valueOf(totalInfoItem.getRedTurningBodyKickPoint().intValue()
								+ commonMatchLogItemDto.getRedAddPoints().intValue()));
					}
					roundInfoItem.setRedTotal(Integer.valueOf(roundInfoItem.getRedTotal().intValue() + commonMatchLogItemDto.getRedAddPoints()
							.intValue()));
					totalInfoItem.setRedTotal(Integer.valueOf(totalInfoItem.getRedTotal().intValue() + commonMatchLogItemDto.getRedAddPoints()
							.intValue()));
					continue;
				}
				if(MatchLogItemType.BLUE_HEAD_POINT.equals(commonMatchLogItemDto.getMatchLogItemType())) {
					if("SCOREBOARD_EDITOR".equals(commonMatchLogItemDto.getEntryValue())) {
						if(commonMatchLogItemDto.getBlueAddPoints().intValue() >= 0) {
							roundInfoItem.setBluePointPlusDeskPoint(Integer.valueOf(roundInfoItem.getBluePointPlusDeskAction().intValue()
									+ commonMatchLogItemDto.getBlueAddPoints().intValue()));
							totalInfoItem.setBluePointPlusDeskPoint(Integer.valueOf(totalInfoItem.getBluePointPlusDeskAction().intValue()
									+ commonMatchLogItemDto.getBlueAddPoints().intValue()));
						} else {
							roundInfoItem.setBluePointMinusDeskPoint(Integer.valueOf(roundInfoItem.getBluePointMinusDeskPoint().intValue()
									+ commonMatchLogItemDto.getBlueAddPoints().intValue()));
							totalInfoItem.setBluePointMinusDeskPoint(Integer.valueOf(totalInfoItem.getBluePointMinusDeskPoint().intValue()
									+ commonMatchLogItemDto.getBlueAddPoints().intValue()));
						}
					} else {
						roundInfoItem.setBlueHeadKickPoint(Integer.valueOf(roundInfoItem.getBlueHeadKickPoint().intValue() + commonMatchLogItemDto
								.getBlueAddPoints().intValue()));
						totalInfoItem.setBlueHeadKickPoint(Integer.valueOf(totalInfoItem.getBlueHeadKickPoint().intValue() + commonMatchLogItemDto
								.getBlueAddPoints().intValue()));
					}
					roundInfoItem.setBlueTotal(Integer.valueOf(roundInfoItem.getBlueTotal().intValue() + commonMatchLogItemDto.getBlueAddPoints()
							.intValue()));
					totalInfoItem.setBlueTotal(Integer.valueOf(totalInfoItem.getBlueTotal().intValue() + commonMatchLogItemDto.getBlueAddPoints()
							.intValue()));
					continue;
				}
				if(MatchLogItemType.BLUE_HEAD_HIT.equals(commonMatchLogItemDto.getMatchLogItemType())) {
					roundInfoItem.setBlueHeadKickAction(Integer.valueOf(roundInfoItem.getBlueHeadKickAction().intValue() + 1));
					totalInfoItem.setBlueHeadKickAction(Integer.valueOf(totalInfoItem.getBlueHeadKickAction().intValue() + 1));
					continue;
				}
				if(MatchLogItemType.RED_HEAD_POINT.equals(commonMatchLogItemDto.getMatchLogItemType())) {
					if("SCOREBOARD_EDITOR".equals(commonMatchLogItemDto.getEntryValue())) {
						if(commonMatchLogItemDto.getRedAddPoints().intValue() >= 0) {
							roundInfoItem.setRedPointPlusDeskPoint(Integer.valueOf(roundInfoItem.getRedPointPlusDeskAction().intValue()
									+ commonMatchLogItemDto.getRedAddPoints().intValue()));
							totalInfoItem.setRedPointPlusDeskPoint(Integer.valueOf(totalInfoItem.getRedPointPlusDeskAction().intValue()
									+ commonMatchLogItemDto.getRedAddPoints().intValue()));
						} else {
							roundInfoItem.setRedPointMinusDeskPoint(Integer.valueOf(roundInfoItem.getRedPointMinusDeskPoint().intValue()
									+ commonMatchLogItemDto.getRedAddPoints().intValue()));
							totalInfoItem.setRedPointMinusDeskPoint(Integer.valueOf(totalInfoItem.getRedPointMinusDeskPoint().intValue()
									+ commonMatchLogItemDto.getRedAddPoints().intValue()));
						}
					} else {
						roundInfoItem.setRedHeadKickPoint(Integer.valueOf(roundInfoItem.getRedHeadKickPoint().intValue() + commonMatchLogItemDto
								.getRedAddPoints().intValue()));
						totalInfoItem.setRedHeadKickPoint(Integer.valueOf(totalInfoItem.getRedHeadKickPoint().intValue() + commonMatchLogItemDto
								.getRedAddPoints().intValue()));
					}
					roundInfoItem.setRedTotal(Integer.valueOf(roundInfoItem.getRedTotal().intValue() + commonMatchLogItemDto.getRedAddPoints()
							.intValue()));
					totalInfoItem.setRedTotal(Integer.valueOf(totalInfoItem.getRedTotal().intValue() + commonMatchLogItemDto.getRedAddPoints()
							.intValue()));
					continue;
				}
				if(MatchLogItemType.RED_HEAD_HIT.equals(commonMatchLogItemDto.getMatchLogItemType())) {
					roundInfoItem.setRedHeadKickAction(Integer.valueOf(roundInfoItem.getRedHeadKickAction().intValue() + 1));
					totalInfoItem.setRedHeadKickAction(Integer.valueOf(totalInfoItem.getRedHeadKickAction().intValue() + 1));
					continue;
				}
				if(MatchLogItemType.BLUE_HEAD_TECH_POINT.equals(commonMatchLogItemDto.getMatchLogItemType())) {
					if("SCOREBOARD_EDITOR".equals(commonMatchLogItemDto.getEntryValue())) {
						if(commonMatchLogItemDto.getBlueAddPoints().intValue() >= 0) {
							roundInfoItem.setBluePointPlusDeskPoint(Integer.valueOf(roundInfoItem.getBluePointPlusDeskAction().intValue()
									+ commonMatchLogItemDto.getBlueAddPoints().intValue()));
							totalInfoItem.setBluePointPlusDeskPoint(Integer.valueOf(totalInfoItem.getBluePointPlusDeskAction().intValue()
									+ commonMatchLogItemDto.getBlueAddPoints().intValue()));
						} else {
							roundInfoItem.setBluePointMinusDeskPoint(Integer.valueOf(roundInfoItem.getBluePointMinusDeskPoint().intValue()
									+ commonMatchLogItemDto.getBlueAddPoints().intValue()));
							totalInfoItem.setBluePointMinusDeskPoint(Integer.valueOf(totalInfoItem.getBluePointMinusDeskPoint().intValue()
									+ commonMatchLogItemDto.getBlueAddPoints().intValue()));
						}
					} else {
						roundInfoItem.setBlueTurningHeadKickPoint(Integer.valueOf(roundInfoItem.getBlueTurningHeadKickPoint().intValue()
								+ commonMatchLogItemDto.getBlueAddPoints().intValue()));
						totalInfoItem.setBlueTurningHeadKickPoint(Integer.valueOf(totalInfoItem.getBlueTurningHeadKickPoint().intValue()
								+ commonMatchLogItemDto.getBlueAddPoints().intValue()));
					}
					roundInfoItem.setBlueTotal(Integer.valueOf(roundInfoItem.getBlueTotal().intValue() + commonMatchLogItemDto.getBlueAddPoints()
							.intValue()));
					totalInfoItem.setBlueTotal(Integer.valueOf(totalInfoItem.getBlueTotal().intValue() + commonMatchLogItemDto.getBlueAddPoints()
							.intValue()));
					continue;
				}
				if(MatchLogItemType.RED_HEAD_TECH_POINT.equals(commonMatchLogItemDto.getMatchLogItemType())) {
					if("SCOREBOARD_EDITOR".equals(commonMatchLogItemDto.getEntryValue())) {
						if(commonMatchLogItemDto.getRedAddPoints().intValue() >= 0) {
							roundInfoItem.setRedPointPlusDeskPoint(Integer.valueOf(roundInfoItem.getRedPointPlusDeskAction().intValue()
									+ commonMatchLogItemDto.getRedAddPoints().intValue()));
							totalInfoItem.setRedPointPlusDeskPoint(Integer.valueOf(totalInfoItem.getRedPointPlusDeskAction().intValue()
									+ commonMatchLogItemDto.getRedAddPoints().intValue()));
						} else {
							roundInfoItem.setRedPointMinusDeskPoint(Integer.valueOf(roundInfoItem.getRedPointMinusDeskPoint().intValue()
									+ commonMatchLogItemDto.getRedAddPoints().intValue()));
							totalInfoItem.setRedPointMinusDeskPoint(Integer.valueOf(totalInfoItem.getRedPointMinusDeskPoint().intValue()
									+ commonMatchLogItemDto.getRedAddPoints().intValue()));
						}
					} else {
						roundInfoItem.setRedTurningHeadKickPoint(Integer.valueOf(roundInfoItem.getRedTurningHeadKickPoint().intValue()
								+ commonMatchLogItemDto.getRedAddPoints().intValue()));
						totalInfoItem.setRedTurningHeadKickPoint(Integer.valueOf(totalInfoItem.getRedTurningHeadKickPoint().intValue()
								+ commonMatchLogItemDto.getRedAddPoints().intValue()));
					}
					roundInfoItem.setRedTotal(Integer.valueOf(roundInfoItem.getRedTotal().intValue() + commonMatchLogItemDto.getRedAddPoints()
							.intValue()));
					totalInfoItem.setRedTotal(Integer.valueOf(totalInfoItem.getRedTotal().intValue() + commonMatchLogItemDto.getRedAddPoints()
							.intValue()));
					continue;
				}
				if(MatchLogItemType.BLUE_VIDEO_REQUEST.equals(commonMatchLogItemDto.getMatchLogItemType())) {
					roundInfoItem.setBlueVideoReplayAction(Integer.valueOf(roundInfoItem.getBlueVideoReplayAction().intValue() + 1));
					totalInfoItem.setBlueVideoReplayAction(Integer.valueOf(totalInfoItem.getBlueVideoReplayAction().intValue() + 1));
					matchInfo.setCallVideoReplayTimes(Integer.valueOf(matchInfo.getCallVideoReplayTimes().intValue() + 1));
					continue;
				}
				if(MatchLogItemType.RED_VIDEO_REQUEST.equals(commonMatchLogItemDto.getMatchLogItemType())) {
					roundInfoItem.setRedVideoReplayAction(Integer.valueOf(roundInfoItem.getRedVideoReplayAction().intValue() + 1));
					totalInfoItem.setRedVideoReplayAction(Integer.valueOf(totalInfoItem.getRedVideoReplayAction().intValue() + 1));
					matchInfo.setCallVideoReplayTimes(Integer.valueOf(matchInfo.getCallVideoReplayTimes().intValue() + 1));
					continue;
				}
				if(MatchLogItemType.DOCTOR.equals(commonMatchLogItemDto.getMatchLogItemType())) {
					matchInfo.setCallDoctorTimes(Integer.valueOf(matchInfo.getCallDoctorTimes().intValue() + 1));
					continue;
				}
				if(MatchLogItemType.BLUE_ADD_GAME_JEON.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.BLUE_REMOVE_GAME_JEON
						.equals(commonMatchLogItemDto.getMatchLogItemType())) {
					if("SCOREBOARD_EDITOR".equals(commonMatchLogItemDto.getEntryValue())) {
						if(MatchLogItemType.BLUE_ADD_GAME_JEON.equals(commonMatchLogItemDto.getMatchLogItemType())) {
							roundInfoItem.setRedPointPlusDeskPoint(Integer.valueOf(roundInfoItem.getRedPointPlusDeskAction().intValue() + 1));
							totalInfoItem.setRedPointPlusDeskPoint(Integer.valueOf(totalInfoItem.getRedPointPlusDeskAction().intValue() + 1));
						} else {
							roundInfoItem.setRedPointMinusDeskPoint(Integer.valueOf(roundInfoItem.getRedPointMinusDeskPoint().intValue() + 1));
							totalInfoItem.setRedPointMinusDeskPoint(Integer.valueOf(totalInfoItem.getRedPointMinusDeskPoint().intValue() + 1));
						}
					} else {
						if(MatchLogItemType.BLUE_ADD_GAME_JEON.equals(commonMatchLogItemDto.getMatchLogItemType())) {
							roundInfoItem.setRedGamJeomPoint(Integer.valueOf(roundInfoItem.getRedGamJeomPoint().intValue() + 1));
							totalInfoItem.setRedGamJeomPoint(Integer.valueOf(totalInfoItem.getRedGamJeomPoint().intValue() + 1));
						} else {
							roundInfoItem.setRedGamJeomPoint(Integer.valueOf(roundInfoItem.getRedGamJeomPoint().intValue() - 1));
							totalInfoItem.setRedGamJeomPoint(Integer.valueOf(totalInfoItem.getRedGamJeomPoint().intValue() - 1));
						}
						roundInfoItem.setBlueGamJeomAction(Integer.valueOf(roundInfoItem.getBlueGamJeomAction().intValue() + 1));
						totalInfoItem.setBlueGamJeomAction(Integer.valueOf(totalInfoItem.getBlueGamJeomAction().intValue() + 1));
					}
					if(MatchLogItemType.BLUE_ADD_GAME_JEON.equals(commonMatchLogItemDto.getMatchLogItemType())) {
						roundInfoItem.setRedTotal(Integer.valueOf(roundInfoItem.getRedTotal().intValue() + 1));
						totalInfoItem.setRedTotal(Integer.valueOf(totalInfoItem.getRedTotal().intValue() + 1));
						continue;
					}
					roundInfoItem.setRedTotal(Integer.valueOf(roundInfoItem.getRedTotal().intValue() - 1));
					totalInfoItem.setRedTotal(Integer.valueOf(totalInfoItem.getRedTotal().intValue() - 1));
					continue;
				}
				if(MatchLogItemType.RED_ADD_GAME_JEON.equals(commonMatchLogItemDto.getMatchLogItemType()) || MatchLogItemType.RED_REMOVE_GAME_JEON
						.equals(commonMatchLogItemDto.getMatchLogItemType())) {
					if("SCOREBOARD_EDITOR".equals(commonMatchLogItemDto.getEntryValue())) {
						if(MatchLogItemType.RED_ADD_GAME_JEON.equals(commonMatchLogItemDto.getMatchLogItemType())) {
							roundInfoItem.setBluePointPlusDeskPoint(Integer.valueOf(roundInfoItem.getBluePointPlusDeskAction().intValue() + 1));
							totalInfoItem.setBluePointPlusDeskPoint(Integer.valueOf(totalInfoItem.getBluePointPlusDeskAction().intValue() + 1));
						} else {
							roundInfoItem.setBluePointMinusDeskPoint(Integer.valueOf(roundInfoItem.getBluePointMinusDeskPoint().intValue() + 1));
							totalInfoItem.setBluePointMinusDeskPoint(Integer.valueOf(totalInfoItem.getBluePointMinusDeskPoint().intValue() + 1));
						}
					} else {
						if(MatchLogItemType.RED_ADD_GAME_JEON.equals(commonMatchLogItemDto.getMatchLogItemType())) {
							roundInfoItem.setBlueGamJeomPoint(Integer.valueOf(roundInfoItem.getBlueGamJeomPoint().intValue() + 1));
							totalInfoItem.setBlueGamJeomPoint(Integer.valueOf(totalInfoItem.getBlueGamJeomPoint().intValue() + 1));
						} else {
							roundInfoItem.setBlueGamJeomPoint(Integer.valueOf(roundInfoItem.getBlueGamJeomPoint().intValue() - 1));
							totalInfoItem.setBlueGamJeomPoint(Integer.valueOf(totalInfoItem.getBlueGamJeomPoint().intValue() - 1));
						}
						roundInfoItem.setRedGamJeomAction(Integer.valueOf(roundInfoItem.getRedGamJeomAction().intValue() + 1));
						totalInfoItem.setRedGamJeomAction(Integer.valueOf(totalInfoItem.getRedGamJeomAction().intValue() + 1));
					}
					if(MatchLogItemType.RED_ADD_GAME_JEON.equals(commonMatchLogItemDto.getMatchLogItemType())) {
						roundInfoItem.setBlueTotal(Integer.valueOf(roundInfoItem.getBlueTotal().intValue() + 1));
						totalInfoItem.setBlueTotal(Integer.valueOf(totalInfoItem.getBlueTotal().intValue() + 1));
						continue;
					}
					roundInfoItem.setBlueTotal(Integer.valueOf(roundInfoItem.getBlueTotal().intValue() - 1));
					totalInfoItem.setBlueTotal(Integer.valueOf(totalInfoItem.getBlueTotal().intValue() - 1));
				}
			}
		}
		return dataTablesItems;
	}

	@Override
	public final void exportMatchLogXLS2DefaultDirectory(String matchLogId) throws TkStrikeServiceException {
		exportMatchLogXLS(matchLogId, getDefaultExportDirectory());
	}

	@Override
	public final void exportMatchLogPDF(String matchLogId, File targetDirectory) throws TkStrikeServiceException {
		if(matchLogId != null && targetDirectory != null) {
			D matchLogEntry = getMatchLog(matchLogId);
			List<ID> matchLogItemEntries = findItemsByMatchLogId(matchLogId);
			if(matchLogEntry != null && matchLogItemEntries != null) {
				matchLogItemEntries.sort(getMatchLogService().getComparator4Items());
				this.matchLogExporterUtil.exportToPDFFile((CommonMatchLogDto)matchLogEntry, (List<CommonMatchLogItemDto>)matchLogItemEntries,
						targetDirectory);
			}
		}
	}

	@Override
	public final void exportMatchLogPDF2DefaultDirectory(String matchLogId) throws TkStrikeServiceException {
		exportMatchLogPDF(matchLogId, getDefaultExportDirectory());
	}

	@Override
	public final D getMatchLog(String matchLogId) {
		if(StringUtils.isNotBlank(matchLogId)) {
			JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
			String sqlQuery = "SELECT * FROM " + getHistoMatchLogTableName() + " WHERE ID = ? ";
			return jdbcTemplate.queryForObject(sqlQuery, new Object[] {matchLogId}, getMatchLogRowMapper());
		}
		return null;
	}

	@Override
	public final List<ID> findItemsByMatchLogId(String matchLogId) {
		if(StringUtils.isNotBlank(matchLogId)) {
			JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
			String sqlQuery = "SELECT * FROM " + getHistoMatchLogItemTableName() + " WHERE MATCH_LOG_ID = ? ";
			try {
				return jdbcTemplate.query(sqlQuery, new Object[] {matchLogId}, getMatchLogItemRowMapper());
			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	@Override
	public final void deleteMatchLog(String matchLogId) {
		if(StringUtils.isNotBlank(matchLogId)) {
			JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
			jdbcTemplate.execute("DELETE FROM " + getHistoMatchLogItemTableName() + " WHERE MATCH_LOG_ID = '" + matchLogId + "'");
			jdbcTemplate.execute("DELETE FROM " + getHistoMatchLogTableName() + " WHERE ID = '" + matchLogId + "'");
		}
	}

	@Override
	public void deleteByMatchConfigurationId(String matchConfigurationId) {
		if(StringUtils.isNotBlank(matchConfigurationId)) {
			JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
			jdbcTemplate.execute("DELETE FROM " + getHistoMatchLogItemTableName() + " WHERE MATCH_LOG_ID IN (SELECT ID FROM " +
					getHistoMatchLogTableName() + " WHERE MATCH_CONFIGURATION_ID = '" + matchConfigurationId + "') ");
			jdbcTemplate.execute("DELETE FROM " + getHistoMatchLogTableName() + " WHERE MATCH_CONFIGURATION_ID = '" + matchConfigurationId + "'");
		}
	}

	@Override
	public final void deleteAll() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource, true);
		jdbcTemplate.execute("DELETE FROM " + getHistoMatchLogItemTableName());
		jdbcTemplate.execute("DELETE FROM " + getHistoMatchLogTableName());
	}

	@Override
	public final void migrateToHistorical(String matchLogId) throws TkStrikeServiceException {
		if(StringUtils.isNotBlank(matchLogId)) {
			List<D> matchLogs = new ArrayList<>();
			matchLogs.add(getMatchLogService().getById(matchLogId));
			_migrateToHistorical(matchLogs);
		}
	}

	@Override
	public void migrateToHistorical(D matchLog) throws TkStrikeServiceException {
		if(matchLog != null)
			_migrateToHistorical(Collections.singletonList(matchLog));
	}

	@Override
	public final void migrateToHistorical() throws TkStrikeServiceException {
		try {
			D last = getMatchLogService().getLastStarted();
			if(last != null)
				_migrateToHistorical(Collections.singletonList(last));
		} catch(Exception e) {
			e.printStackTrace();
		}
		getMatchLogService().deleteExceptLast();
	}

	private File getDefaultExportDirectory() throws TkStrikeServiceException {
		String basePath = this.externalConfigService.getExternalConfigEntry().getMatchLogOutputDirectory();
		if( ! basePath.endsWith("/"))
			basePath = basePath + "/";
		basePath = basePath + this.sdf4Directory.format(new Date()) + "/";
		return new File(basePath);
	}
}
