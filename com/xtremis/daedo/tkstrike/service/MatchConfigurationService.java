package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.MatchConfiguration;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchConfigurationDto;
import com.xtremis.daedo.tkstrike.ui.model.IMatchConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.MatchConfigurationEntry;

public interface MatchConfigurationService extends CommonMatchConfigurationService<MatchConfiguration, MatchConfigurationEntry> {
  MatchConfigurationEntry transformByDto(MatchConfigurationDto paramMatchConfigurationDto, boolean paramBoolean) throws TkStrikeServiceException;
  
  Boolean existsByAthlete(String paramString) throws TkStrikeServiceException;
}
