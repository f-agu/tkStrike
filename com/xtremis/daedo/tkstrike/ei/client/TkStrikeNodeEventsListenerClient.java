package com.xtremis.daedo.tkstrike.ei.client;

import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeChangeNetworkStatusEventDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeNetworkConfigurationEventDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeNodeDataEvent;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeNodeStatusEvent;
import java.util.concurrent.Future;

public interface TkStrikeNodeEventsListenerClient extends TkStrikeCommonEventListenerClient {
  Future<Boolean> doFuturePing(String paramString) throws TkStrikeServiceException;
  
  Future<Boolean> sendNewNetworkStatusEvent(String paramString, TkStrikeChangeNetworkStatusEventDto paramTkStrikeChangeNetworkStatusEventDto) throws TkStrikeServiceException;
  
  Future<Boolean> sendNewNetworkConfigurationEvent(String paramString, TkStrikeNetworkConfigurationEventDto paramTkStrikeNetworkConfigurationEventDto) throws TkStrikeServiceException;
  
  Future<Boolean> sendNewStatusEvent(String paramString, TkStrikeNodeStatusEvent paramTkStrikeNodeStatusEvent) throws TkStrikeServiceException;
  
  Future<Boolean> sendNewDataEvent(String paramString, TkStrikeNodeDataEvent paramTkStrikeNodeDataEvent) throws TkStrikeServiceException;
}
