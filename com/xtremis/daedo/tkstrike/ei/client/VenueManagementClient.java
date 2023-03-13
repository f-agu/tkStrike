package com.xtremis.daedo.tkstrike.ei.client;

import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchConfigurationDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchResultDto;

public interface VenueManagementClient {
  Boolean doPing() throws TkStrikeServiceException;
  
  Boolean doPing(String paramString) throws TkStrikeServiceException;
  
  MatchConfigurationDto getNextMatch() throws TkStrikeServiceException;
  
  MatchConfigurationDto getPrevMatch() throws TkStrikeServiceException;
  
  Boolean doSendMatchResult(MatchResultDto paramMatchResultDto) throws TkStrikeServiceException;
}
