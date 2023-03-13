package com.xtremis.daedo.tkstrike.tools.ei.om;

public class TeamMatchConfigurationDto extends MatchConfigurationDto {
  private static final long serialVersionUID = 6269888373899310388L;
  
  private TeamGender teamGender;
  
  private TeamRoundsConfigDto teamRoundsConfig;
  
  public TeamGender getTeamGender() {
    return this.teamGender;
  }
  
  public void setTeamGender(TeamGender teamGender) {
    this.teamGender = teamGender;
  }
  
  public TeamRoundsConfigDto getTeamRoundsConfig() {
    return this.teamRoundsConfig;
  }
  
  public void setTeamRoundsConfig(TeamRoundsConfigDto teamRoundsConfig) {
    this.teamRoundsConfig = teamRoundsConfig;
  }
}
