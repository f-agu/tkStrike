package com.xtremis.daedo.tkstrike.tools.su.om;

import com.xtremis.daedo.tkstrike.tools.om.BaseJSONDto;

public class TkStrikeHasNewVersionResponseDto extends BaseJSONDto {
  private Boolean hasNewVersion;
  
  private TkStrikeVersionBuildDto tkStrikeVersionBuildDto;
  
  public Boolean getHasNewVersion() {
    return this.hasNewVersion;
  }
  
  public void setHasNewVersion(Boolean hasNewVersion) {
    this.hasNewVersion = hasNewVersion;
  }
  
  public TkStrikeVersionBuildDto getTkStrikeVersionBuildDto() {
    return this.tkStrikeVersionBuildDto;
  }
  
  public void setTkStrikeVersionBuildDto(TkStrikeVersionBuildDto tkStrikeVersionBuildDto) {
    this.tkStrikeVersionBuildDto = tkStrikeVersionBuildDto;
  }
}
