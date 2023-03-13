package com.xtremis.daedo.tkstrike.ei.client;

import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchConfigurationDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchResultDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeEventDto;
import com.xtremis.daedo.wtdata.model.constants.ResultStatus;
import java.util.List;

public interface WtOvrClientService {
  void resetCounter();
  
  Integer getCurrentCounter();
  
  Boolean doPing(String paramString1, String paramString2) throws TkStrikeServiceException;
  
  MatchConfigurationDto getWtOvrMatch(String paramString1, String paramString2, String paramString3) throws TkStrikeServiceException;
  
  List<MatchConfigurationDto> findWtOvrMatches(String paramString1, String paramString2, Integer paramInteger) throws TkStrikeServiceException;
  
  Boolean sendMatchLoadedAction(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, MatchConfigurationDto paramMatchConfigurationDto) throws TkStrikeServiceException;
  
  Boolean sendMatchAction(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, TkStrikeEventDto paramTkStrikeEventDto, boolean paramBoolean) throws TkStrikeServiceException;
  
  Boolean sendMatchResult(String paramString1, String paramString2, String paramString3, MatchResultDto paramMatchResultDto, ResultStatus paramResultStatus, String paramString4, String paramString5) throws TkStrikeServiceException;
}
