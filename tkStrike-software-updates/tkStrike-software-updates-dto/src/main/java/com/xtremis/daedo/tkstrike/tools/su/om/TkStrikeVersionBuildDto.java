package com.xtremis.daedo.tkstrike.tools.su.om;

import com.xtremis.daedo.tkstrike.tools.om.BaseJSONDto;
import java.util.List;

public class TkStrikeVersionBuildDto extends BaseJSONDto {
  private String version;
  
  private String buildNumber;
  
  private List<TkStrikeOSReleaseInfoDto> tkStrikeOSReleaseInfoDtos;
  
  public String getVersion() {
    return this.version;
  }
  
  public void setVersion(String version) {
    this.version = version;
  }
  
  public String getBuildNumber() {
    return this.buildNumber;
  }
  
  public void setBuildNumber(String buildNumber) {
    this.buildNumber = buildNumber;
  }
  
  public List<TkStrikeOSReleaseInfoDto> getTkStrikeOSReleaseInfoDtos() {
    return this.tkStrikeOSReleaseInfoDtos;
  }
  
  public void setTkStrikeOSReleaseInfoDtos(List<TkStrikeOSReleaseInfoDto> tkStrikeOSReleaseInfoDtos) {
    this.tkStrikeOSReleaseInfoDtos = tkStrikeOSReleaseInfoDtos;
  }
}
