package com.xtremis.daedo.tkstrike.tools.ei.om;

import java.util.ArrayList;
import java.util.List;

public class TeamRoundsConfigDto extends RoundsConfigDto {
  private static final long serialVersionUID = -402646155319680174L;
  
  private List<TeamSimpleRoundConfigDto> simpleRoundsConfigs = new ArrayList<>();
  
  public List<TeamSimpleRoundConfigDto> getSimpleRoundsConfigs() {
    return this.simpleRoundsConfigs;
  }
  
  public void setSimpleRoundsConfigs(List<TeamSimpleRoundConfigDto> simpleRoundsConfigs) {
    this.simpleRoundsConfigs = simpleRoundsConfigs;
  }
}
