package com.xtremis.daedo.tkstrike.ei.client;

import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.su.om.TkStrikeHasNewVersionResponseDto;

public interface TkStrikeSoftwareUpdatesClient {
  TkStrikeHasNewVersionResponseDto hasNewVersion() throws TkStrikeServiceException;
}
