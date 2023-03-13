package com.xtremis.daedo.tkstrike.service;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xtremis.daedo.tkstrike.orm.model.SoundConfiguration;
import com.xtremis.daedo.tkstrike.orm.repository.SoundConfigurationRepository;
import com.xtremis.daedo.tkstrike.ui.model.SoundConfigurationEntry;


@Service
@Transactional(readOnly = true)
public class SoundConfigurationServiceImpl extends BaseTkStrikeService<SoundConfiguration, SoundConfigurationEntry>
		implements SoundConfigurationService<SoundConfiguration, SoundConfigurationEntry> {

	@Autowired
	private AppStatusWorker appStatusWorker;

	@Resource
	private SoundConfigurationRepository soundConfigurationRepository;

	@Override
	protected JpaRepository<SoundConfiguration, String> getRepository() {
		return this.soundConfigurationRepository;
	}

	@Override
	protected Sort getDefaultSort() {
		return null;
	}

	@Override
	protected void deleteAllChild() throws TkStrikeServiceException {}

	@Override
	public Boolean canDelete(String id) {
		return Boolean.FALSE;
	}

	@Override
	public SoundConfiguration getSoundConfiguration() throws TkStrikeServiceException {
		try {
			return this.soundConfigurationRepository.getOne("1");
		} catch(Exception e) {
			throw new TkStrikeServiceException(e);
		}
	}

	@Override
	public SoundConfigurationEntry getSoundConfigurationEntry() throws TkStrikeServiceException {
		SoundConfigurationEntry res = null;
		SoundConfiguration soundConfiguration = getSoundConfiguration();
		if(soundConfiguration != null) {
			res = new SoundConfigurationEntry();
			res.fillByEntity(soundConfiguration);
		}
		return res;
	}

	@Override
	@Transactional(readOnly = false)
	public void update(SoundConfiguration soundConfiguration) throws TkStrikeServiceException {
		if(soundConfiguration != null)
			try {
				SoundConfiguration current = getSoundConfiguration();
				BeanUtils.copyProperties(soundConfiguration, current, new String[] {"id", "version"});
				this.soundConfigurationRepository.saveAndFlush(current);
				this.appStatusWorker.setSoundConfigurationEntry(getSoundConfigurationEntry());
			} catch(Exception e) {
				throw new TkStrikeServiceException(e);
			}
	}

	@Override
	@Transactional(readOnly = false)
	public void update(SoundConfigurationEntry soundConfigurationEntry) throws TkStrikeServiceException {
		if(soundConfigurationEntry != null)
			update(soundConfigurationEntry.createSoundConfiguration());
	}

	@Override
	public void delete(String id) throws TkStrikeServiceException {}
}
