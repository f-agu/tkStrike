package com.xtremis.daedo.tkstrike.ei.client;

import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchConfigurationDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchResultDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeEventDto;

public interface TkStrikeEventsListenerClient extends TkStrikeCommonEventListenerClient {
  Boolean sendNewMatchEvent(String paramString, TkStrikeEventDto paramTkStrikeEventDto) throws TkStrikeServiceException;
  
  Boolean sendHasNewMatchConfigured(String paramString, MatchConfigurationDto paramMatchConfigurationDto) throws TkStrikeServiceException;
  
  Boolean doSendMatchResult(String paramString, MatchResultDto paramMatchResultDto) throws TkStrikeServiceException;
}
