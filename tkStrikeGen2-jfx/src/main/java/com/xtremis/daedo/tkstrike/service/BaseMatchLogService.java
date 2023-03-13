package com.xtremis.daedo.tkstrike.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.xtremis.daedo.tkstrike.om.CommonMatchLogDto;
import com.xtremis.daedo.tkstrike.om.CommonMatchLogItemDto;
import com.xtremis.daedo.tkstrike.om.GoldenPointTieBreakerInfoDto;
import com.xtremis.daedo.tkstrike.om.combat.FinalDecision;
import com.xtremis.daedo.tkstrike.om.combat.MatchWinner;
import com.xtremis.daedo.tkstrike.orm.model.GoldenPointTieBreakerInfo;
import com.xtremis.daedo.tkstrike.orm.model.MatchLogEntity;
import com.xtremis.daedo.tkstrike.orm.model.MatchLogItemEntity;
import com.xtremis.daedo.tkstrike.orm.model.MatchLogItemType;
import com.xtremis.daedo.tkstrike.orm.repository.DefaultMatchLogItemRepository;
import com.xtremis.daedo.tkstrike.orm.repository.DefaultMatchLogRepository;
import com.xtremis.daedo.tkstrike.ui.model.IMatchConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.IRulesEntry;
import com.xtremis.daedo.tkstrike.utils.MatchLogExporterUtil;


@Transactional(readOnly = true)
public abstract class BaseMatchLogService<D extends CommonMatchLogDto, ID extends CommonMatchLogItemDto, T extends MatchLogTransformer<D, ID, DE>, IT extends MatchLogItemTransformer, R extends DefaultMatchLogRepository<DE>, DE extends MatchLogEntity, EI extends MatchLogItemEntity<DE>, IR extends DefaultMatchLogItemRepository<EI>>
		implements CommonMatchLogService<D, ID> {

	private final MatchLogExporterUtil matchLogExporterUtil;

	@Autowired
	public BaseMatchLogService(MatchLogExporterUtil matchLogExporterUtil) {
		this.matchLogExporterUtil = matchLogExporterUtil;
	}

	@Override
	public D getById(String id) throws TkStrikeServiceException {
		try {
			return getMatchLogTransformer().transferToDto(_getById(id));
		} catch(Exception e) {
			throw new TkStrikeServiceException(e);
		}
	}

	DE _getById(String id) {
		return getMatchLogRepository().findOne(id);
	}

	@Transactional(readOnly = false)
	DE _update(DE matchLog) throws TkStrikeServiceException {
		if(matchLog != null)
			return getMatchLogRepository().saveAndFlush(matchLog);
		return null;
	}

	@Override
	public List<D> findAll() throws TkStrikeServiceException {
		List<DE> entities = getMatchLogRepository().findAll();
		if(entities != null)
			return getMatchLogTransformer().transferToDtos(entities);
		return null;
	}

	@Override
	public D getLastStarted() throws TkStrikeServiceException {
		final DE entity = this.getMatchLogRepository().getLastStarted();
		if(entity != null) {
			return this.getMatchLogTransformer().transferToDto(entity);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = false)
	public D updateNetworkInfo(String matchLogId, Integer judgesNumber, Boolean bodySensorsEnabled, Boolean headSensorsEnabled)
			throws TkStrikeServiceException {
		DE res = _getById(matchLogId);
		if(judgesNumber != null) {
			res.setNumberOfJudges(judgesNumber);
			res.setBodySensorsEnabled(bodySensorsEnabled);
			res.setHeadSensorsEnabled(headSensorsEnabled);
			res = _update(res);
		}
		return getMatchLogTransformer().transferToDto(res);
	}

	@Override
	public D updateMatchLogStartTime(String matchLogId, Long matchLogStartTime) throws TkStrikeServiceException {
		DE res = _getById(matchLogId);
		if(matchLogStartTime != null) {
			res.setMatchStartTime(matchLogStartTime);
			res = _update(res);
		}
		return getMatchLogTransformer().transferToDto(res);
	}

	@Override
	public D updateMatchLogFinish(String matchLogId, Long matchLogEndTime, MatchWinner matchWinner, FinalDecision finalDecision, String result,
			GoldenPointTieBreakerInfoDto goldenPointTieBreakerInfo) throws TkStrikeServiceException {
		DE res = _getById(matchLogId);
		if(matchLogEndTime != null && res != null) {
			res.setMatchEndTime(matchLogEndTime);
			res.setMatchWinner(matchWinner);
			res.setMatchWinnerBy(finalDecision);
			res.setMatchResult(result);
			GoldenPointTieBreakerInfo goldenPointTieBreakerInfoEntity = new GoldenPointTieBreakerInfo();
			if(goldenPointTieBreakerInfo != null)
				BeanUtils.copyProperties(goldenPointTieBreakerInfo, goldenPointTieBreakerInfoEntity);
			res.setGoldenPointTieBreakerInfo(goldenPointTieBreakerInfoEntity);
			res = _update(res);
		}
		return getMatchLogTransformer().transferToDto(res);
	}

	@Override
	public Boolean updateMatchLogRoundsWinners(String matchLogId, Map<Integer, MatchWinner> roundsWinners) throws TkStrikeServiceException {
		DE res = _getById(matchLogId);
		if(roundsWinners != null && res != null) {
			StringBuilder sb = new StringBuilder();
			roundsWinners.keySet().forEach(rw -> sb.append(rw.toString() + ":" + roundsWinners.get(rw) + ";"));
			res.setRoundsWinners(sb.toString());
			res = _update(res);
		}
		return Boolean.FALSE;
	}

	@Override
	public void addMatchLogItem(String matchLogId, String roundNumberString, Integer roundNumber, Long eventTime, Long roundTime, Long systemTime,
			MatchLogItemType matchLogItemType, Integer blueGeneralPoints, Integer redGeneralPoints, Integer blueAddPoints, Integer redAddPoints,
			String entryValue) throws TkStrikeServiceException {
		if(matchLogId != null) {
			EI matchLogItem = newMatchLogItemEntity();
			matchLogItem.setMatchLog(this._getById(matchLogId));
			matchLogItem.setRoundNumber(roundNumber);
			matchLogItem.setRoundNumberStr(roundNumberString);
			matchLogItem.setEventTime(eventTime);
			matchLogItem.setRoundTime(roundTime);
			matchLogItem.setSystemTime(systemTime);
			matchLogItem.setMatchLogItemType(matchLogItemType);
			matchLogItem.setBlueGeneralPoints(blueGeneralPoints);
			matchLogItem.setRedGeneralPoints(redGeneralPoints);
			matchLogItem.setBlueAddPoints(blueAddPoints);
			matchLogItem.setRedAddPoints(redAddPoints);
			matchLogItem.setEntryValue(entryValue);
			try {
				getMatchLogItemRepository().saveAndFlush(matchLogItem);
			} catch(Exception e) {
				throw new TkStrikeServiceException(e);
			}
		}
	}

	@Override
	public void addMatchLogItem(String matchLogId, ID matchLogItemDto) throws TkStrikeServiceException {
		if(matchLogId != null) {
			EI matchLogItem = newMatchLogItemEntity();
			matchLogItem.setMatchLog(this._getById(matchLogId));
			BeanUtils.copyProperties(matchLogItemDto, matchLogItem, new String[] {"matchLog", "version", "id"});
			try {
				getMatchLogItemRepository().saveAndFlush(matchLogItem);
			} catch(Exception e) {
				throw new TkStrikeServiceException(e);
			}
		}
	}

	@Override
	public List<ID> findByMatchLogId(String matchLogId) throws TkStrikeServiceException {
		List<EI> matchLogItems = getMatchLogItemRepository().findByMatchLogId(matchLogId);
		if(matchLogItems != null)
			return getMatchLogItemTransformer().transferToDtos(matchLogItems);
		return null;
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteByMatchConfigurationId(String matchConfigurationId) throws TkStrikeServiceException {
		if(StringUtils.isNotBlank(matchConfigurationId))
			try {
				getMatchLogRepository().deleteByMatchConfigurationId(matchConfigurationId);
			} catch(Exception e) {
				throw new TkStrikeServiceException(e);
			}
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteWhenSystemTimeLessThan(Long systemTime) throws TkStrikeServiceException {
		try {
			getMatchLogItemRepository().deleteBySystemTimeLessThan(systemTime);
		} catch(Exception e) {
			e.printStackTrace();
			throw new TkStrikeServiceException(e);
		}
	}

	@Override
	public void delete(String matchLogId) throws TkStrikeServiceException {
		try {
			deleteMatchLogItems(matchLogId);
			getMatchLogRepository().delete(matchLogId);
		} catch(EmptyResultDataAccessException emptyResultDataAccessException) {

		} catch(Exception e) {
			e.printStackTrace();
			throw new TkStrikeServiceException(e);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteMatchLogItems(String matchLogId) throws TkStrikeServiceException {
		try {
			getMatchLogItemRepository().deleteByMatchLogId(matchLogId);
		} catch(Exception e) {
			e.printStackTrace();
			throw new TkStrikeServiceException(e);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAll() throws TkStrikeServiceException {
		try {
			getMatchLogItemRepository().deleteAllNative();
		} catch(Exception e) {
			throw new TkStrikeServiceException(e);
		}
		try {
			getMatchLogRepository().deleteAll();
		} catch(Exception e) {
			throw new TkStrikeServiceException(e);
		}
	}

	@Override
	@Transactional(readOnly = false, noRollbackFor = {Exception.class})
	public void deleteExceptLast() throws TkStrikeServiceException {
		try {
			MatchLogEntity matchLogEntity = getMatchLogRepository().getLastStarted();
			if(matchLogEntity != null) {
				getMatchLogItemRepository().deleteNotOfMatchLog(matchLogEntity.getId());
			} else {
				getMatchLogItemRepository().deleteAllNative();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		try {
			List<DE> matchLogs = getMatchLogRepository().getAllExceptLast();
			for(MatchLogEntity matchLogEntity : matchLogs)
				getMatchLogRepository().delete(matchLogEntity);
		} catch(Exception e) {
			e.printStackTrace();
			getMatchLogItemRepository().deleteAllNative();
			getMatchLogRepository().deleteAll();
		}
	}

	@Override
	public void exportMatchLog(String matchLogId, File targetDirectory) throws TkStrikeServiceException {
		if(matchLogId != null && targetDirectory != null) {
			D matchLogEntry = getById(matchLogId);
			List<ID> matchLogItemEntries = findByMatchLogId(matchLogId);
			if(matchLogEntry != null && matchLogItemEntries != null)
				this.matchLogExporterUtil.exportToCSVFile(matchLogEntry, matchLogItemEntries, targetDirectory);
		}
	}

	abstract T getMatchLogTransformer();

	abstract IT getMatchLogItemTransformer();

	abstract R getMatchLogRepository();

	abstract IR getMatchLogItemRepository();

	abstract EI newMatchLogItemEntity();

	@Override
	@Transactional(readOnly = false)
	public abstract D createNew(IMatchConfigurationEntry paramIMatchConfigurationEntry, IRulesEntry paramIRulesEntry, Integer paramInteger,
			Boolean paramBoolean1, Boolean paramBoolean2) throws TkStrikeServiceException;
}
