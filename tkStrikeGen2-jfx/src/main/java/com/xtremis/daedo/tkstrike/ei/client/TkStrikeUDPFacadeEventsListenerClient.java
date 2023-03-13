package com.xtremis.daedo.tkstrike.ei.client;

import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchConfigurationDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeEventDto;

public interface TkStrikeUDPFacadeEventsListenerClient {
  void updateUDPSockets();
  
  void sendNewMatchEvent(TkStrikeEventDto paramTkStrikeEventDto) throws TkStrikeServiceException;
  
  void sendHasNewMatchConfigured(MatchConfigurationDto paramMatchConfigurationDto) throws TkStrikeServiceException;
}
