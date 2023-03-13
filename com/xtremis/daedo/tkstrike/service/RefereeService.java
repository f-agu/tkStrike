package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.Referee;
import com.xtremis.daedo.tkstrike.tools.ei.om.RefereeDto;
import com.xtremis.daedo.tkstrike.ui.scene.RefereeEntry;

public interface RefereeService extends TkStrikeService<Referee, RefereeEntry> {
  Referee doGetCreateOrUpdateEntity(RefereeDto paramRefereeDto) throws TkStrikeServiceException;
  
  RefereeEntry doGetCreateOrUpdateEntry(RefereeDto paramRefereeDto) throws TkStrikeServiceException;
  
  Referee createNew(RefereeDto paramRefereeDto) throws TkStrikeServiceException;
  
  Referee update(RefereeDto paramRefereeDto) throws TkStrikeServiceException;
}
