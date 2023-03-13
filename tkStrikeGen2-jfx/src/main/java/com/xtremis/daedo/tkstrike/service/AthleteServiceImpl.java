// 
// Decompiled by Procyon v0.5.36
// 

package com.xtremis.daedo.tkstrike.service;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xtremis.daedo.tkstrike.orm.model.Athlete;
import com.xtremis.daedo.tkstrike.orm.model.Flag;
import com.xtremis.daedo.tkstrike.orm.repository.AthleteRepository;
import com.xtremis.daedo.tkstrike.orm.repository.MatchConfigurationRepository;
import com.xtremis.daedo.tkstrike.orm.repository.MatchLogRepository;
import com.xtremis.daedo.tkstrike.tools.ei.om.AthleteDto;
import com.xtremis.daedo.tkstrike.ui.model.AthleteEntry;
import com.xtremis.daedo.wtdata.model.constants.CompetitorType;
import com.xtremis.daedo.wtdata.model.constants.Gender;


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

	@Override
	protected JpaRepository<Athlete, String> getRepository() {
		return this.athleteRepository;
	}

	@Override
	protected Sort getDefaultSort() {
		return new Sort(Sort.Direction.ASC, new String[] {"scoreboardName"});
	}

	@Override
	protected void deleteAllChild() throws TkStrikeServiceException {}

	@Override
	public AthleteEntry getEntryBytWfId(final String wfId) throws TkStrikeServiceException {
		AthleteEntry res = null;
		final Athlete athlete = this.getByWfId(wfId);
		if(athlete != null) {
			res = new AthleteEntry();
			res.fillByEntity(athlete);
		}
		return res;
	}

	@Override
	public Athlete getByWfId(final String wfId) throws TkStrikeServiceException {
		if(StringUtils.isNotBlank(wfId)) {
			try {
				return this.athleteRepository.getByWfId(wfId);
			} catch(Exception e) {
				throw new TkStrikeServiceException(e);
			}
		}
		return null;
	}

	@Override
	public Athlete getByOvrInternalId(final String ovrInternalId) throws TkStrikeServiceException {
		if(StringUtils.isNotBlank(ovrInternalId)) {
			try {
				return this.athleteRepository.getByOvrInternalId(ovrInternalId);
			} catch(Exception e) {
				return null;
			}
		}
		return null;
	}

	@Override
	public List<Athlete> findByFlagId(final String flagId) throws TkStrikeServiceException {
		try {
			return this.athleteRepository.findByFlagId(flagId);
		} catch(Exception e) {
			throw new TkStrikeServiceException(e);
		}
	}

	@Override
	public List<Athlete> findByFlagName(final String flagName) throws TkStrikeServiceException {
		try {
			return this.athleteRepository.findByFlagName(flagName);
		} catch(Exception e) {
			throw new TkStrikeServiceException(e);
		}
	}

	@Override
	public List<Athlete> findByFlagAbbreviation(final String flagAbbreviation) throws TkStrikeServiceException {
		try {
			return this.athleteRepository.findByFlagName(flagAbbreviation);
		} catch(Exception e) {
			throw new TkStrikeServiceException(e);
		}
	}

	@Transactional(readOnly = false)
	@Override
	public Athlete doGetCreateOrUpdateEntity(final AthleteDto athleteDto) throws TkStrikeServiceException {
		Athlete athlete = null;
		if(athleteDto != null && athleteDto.getWfId() != null && athleteDto.getFlagAbbreviation() != null) {
			athlete = this.getByWfId(athleteDto.getWfId());
			final Flag flag = this.flagService.doGetCreateOrUpdateEntity(athleteDto.getFlagAbbreviation(), athleteDto.getFlagName(), athleteDto
					.getFlagShowName());
			if(athlete == null) {
				athlete = this.createNew(athleteDto, flag);
			} else {
				athlete = this.update(athlete.getId(), athleteDto, flag);
			}
		}
		return athlete;
	}

	@Transactional(readOnly = false)
	@Override
	public AthleteEntry doGetCreateOrUpdateEntry(final AthleteDto athleteDto) throws TkStrikeServiceException {
		AthleteEntry res = null;
		final Athlete athlete = this.doGetCreateOrUpdateEntity(athleteDto);
		if(athlete != null) {
			res = new AthleteEntry();
			res.fillByEntity(athlete);
		}
		return res;
	}

	@Transactional(readOnly = false)
	@Override
	public Athlete createNew(final String name, final String wfId, final String ovrInternalId, final String flagId) throws TkStrikeServiceException {
		return this.createNew(name, wfId, ovrInternalId, StringUtils.isNotBlank(flagId) ? flagService.getById(flagId)
				: null);
	}

	@Transactional(readOnly = false)
	@Override
	public Athlete createNew(final String name, final String wfId, final String ovrInternalId, final Flag flag) throws TkStrikeServiceException {
		final Athlete byWtfId = this.getByWfId(wfId);
		if(byWtfId != null) {
			throw new TkStrikeServiceException("Athlete with the same WF-ID exist!");
		}
		try {
			final Athlete athlete = new Athlete();
			if(StringUtils.isNotBlank(ovrInternalId)) {
				athlete.setOvrInternalId(ovrInternalId);
			}
			athlete.setScoreboardName(name);
			athlete.setWfId(wfId);
			athlete.setFlag(flag);
			return this.athleteRepository.saveAndFlush(athlete);
		} catch(Exception e) {
			throw new TkStrikeServiceException(e);
		}
	}

	@Transactional(readOnly = false)
	@Override
	public Athlete createNew(final AthleteDto athleteDto, final Flag flag) throws TkStrikeServiceException {
		if(athleteDto != null && flag != null) {
			final Athlete byWtfId = this.getByWfId(athleteDto.getWfId());
			if(byWtfId != null) {
				throw new TkStrikeServiceException("Athlete with the same WF-ID exist!");
			}
			try {
				final Athlete athlete = new Athlete();
				BeanUtils.copyProperties(athleteDto, athlete, new String[] {"id", "version", "flag", "gender", "competitorType"});
				athlete.setFlag(flag);
				if(athleteDto.getGender() != null) {
					try {
						athlete.setGender(Gender.valueOf(athleteDto.getGender()));
					} catch(Exception ex) {}
				}
				if(athleteDto.getCompetitorType() != null) {
					try {
						athlete.setCompetitorType(CompetitorType.valueOf(athleteDto.getCompetitorType()));
					} catch(Exception ex2) {}
				}
				return this.athleteRepository.saveAndFlush(athlete);
			} catch(Exception e) {
				throw new TkStrikeServiceException(e);
			}
		}
		return null;
	}

	@Transactional(readOnly = false)
	@Override
	public Athlete update(final String id, final AthleteDto athleteDto, final Flag flag) throws TkStrikeServiceException {
		try {
			final Athlete athlete = getById(id);
			if(athlete != null) {
				BeanUtils.copyProperties(athleteDto, athlete, new String[] {"id", "version", "flag", "gender", "competitorType"});
				if(flag != null) {
					athlete.setFlag(flag);
				}
				if(athleteDto.getGender() != null) {
					try {
						athlete.setGender(Gender.valueOf(athleteDto.getGender()));
					} catch(Exception ex) {}
				}
				if(athleteDto.getCompetitorType() != null) {
					try {
						athlete.setCompetitorType(CompetitorType.valueOf(athleteDto.getCompetitorType()));
					} catch(Exception ex2) {}
				}
				return this.athleteRepository.saveAndFlush(athlete);
			}
			return null;
		} catch(Exception e) {
			throw new TkStrikeServiceException(e);
		}
	}

	@Transactional(readOnly = false)
	@Override
	public Athlete update(final String id, final String name, final String wfId, final String ovrInternalId, final String flagId)
			throws TkStrikeServiceException {
		return this.update(id, name, wfId, ovrInternalId, StringUtils.isNotBlank(flagId) ? flagService.getById(
				flagId) : null);
	}

	@Transactional(readOnly = false)
	@Override
	public Athlete update(final String id, final String name, final String wfId, final String ovrInternalId, final Flag flag)
			throws TkStrikeServiceException {
		try {
			final Athlete athlete = getById(id);
			if(athlete != null) {
				if(StringUtils.isNotBlank(name)) {
					athlete.setScoreboardName(name);
				}
				if(StringUtils.isNotBlank(wfId)) {
					athlete.setWfId(wfId);
				}
				if(StringUtils.isNotBlank(ovrInternalId)) {
					athlete.setOvrInternalId(ovrInternalId);
				}
				if(flag != null) {
					athlete.setFlag(flag);
				}
				return this.athleteRepository.saveAndFlush(athlete);
			}
			return null;
		} catch(Exception e) {
			throw new TkStrikeServiceException(e);
		}
	}

	@Override
	public Boolean canDelete(final String id) {
		try {
			return this.canDeleteAthlete(id);
		} catch(TkStrikeServiceException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Boolean canDeleteAthlete(final String athleteId) throws TkStrikeServiceException {
		if(StringUtils.isNotBlank(athleteId)) {
			final Long nML = this.matchLogRepository.getCountByAthlete(athleteId);
			final Long nMC = this.matchConfigurationRepository.getCountByAthlete(athleteId);
			return (nML == null || nML == 0L) && (nMC == null || nMC == 0L);
		}
		return Boolean.FALSE;
	}
}
