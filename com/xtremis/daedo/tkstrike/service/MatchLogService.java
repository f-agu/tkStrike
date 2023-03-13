package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.om.MatchLogDto;
import com.xtremis.daedo.tkstrike.om.MatchLogItemDto;

public interface MatchLogService extends CommonMatchLogService<MatchLogDto, MatchLogItemDto> {
  MatchLogDto updateMatchLogMinBodyLevel(String paramString, Integer paramInteger) throws TkStrikeServiceException;
  
  MatchLogDto updateMatchLogMinHeadLevel(String paramString, Integer paramInteger) throws TkStrikeServiceException;
}
