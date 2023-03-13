package com.xtremis.daedo.tkstrike.ei.client;

import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;

public interface TkStrikeCommonEventListenerClient {
  Boolean doPing(String paramString) throws TkStrikeServiceException;
}
