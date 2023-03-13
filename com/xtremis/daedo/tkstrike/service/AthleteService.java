package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.Athlete;
import com.xtremis.daedo.tkstrike.orm.model.Flag;
import com.xtremis.daedo.tkstrike.tools.ei.om.AthleteDto;
import com.xtremis.daedo.tkstrike.ui.model.AthleteEntry;
import java.util.List;

public interface AthleteService extends TkStrikeService<Athlete, AthleteEntry> {
  AthleteEntry getEntryBytWfId(String paramString) throws TkStrikeServiceException;
  
  Athlete getByWfId(String paramString) throws TkStrikeServiceException;
  
  Athlete getByOvrInternalId(String paramString) throws TkStrikeServiceException;
  
  List<Athlete> findByFlagId(String paramString) throws TkStrikeServiceException;
  
  List<Athlete> findByFlagName(String paramString) throws TkStrikeServiceException;
  
  List<Athlete> findByFlagAbbreviation(String paramString) throws TkStrikeServiceException;
  
  Athlete doGetCreateOrUpdateEntity(AthleteDto paramAthleteDto) throws TkStrikeServiceException;
  
  AthleteEntry doGetCreateOrUpdateEntry(AthleteDto paramAthleteDto) throws TkStrikeServiceException;
  
  Athlete createNew(String paramString1, String paramString2, String paramString3, String paramString4) throws TkStrikeServiceException;
  
  Athlete createNew(String paramString1, String paramString2, String paramString3, Flag paramFlag) throws TkStrikeServiceException;
  
  Athlete createNew(AthleteDto paramAthleteDto, Flag paramFlag) throws TkStrikeServiceException;
  
  Athlete update(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) throws TkStrikeServiceException;
  
  Athlete update(String paramString1, String paramString2, String paramString3, String paramString4, Flag paramFlag) throws TkStrikeServiceException;
  
  Athlete update(String paramString, AthleteDto paramAthleteDto, Flag paramFlag) throws TkStrikeServiceException;
  
  Boolean canDeleteAthlete(String paramString) throws TkStrikeServiceException;
}
