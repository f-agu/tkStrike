package com.xtremis.daedo.tkstrike.tools.su.om;

import com.xtremis.daedo.tkstrike.tools.om.BaseJSONDto;

public class TkStrikeHasNewVersionRequestDto extends BaseJSONDto {
  private String currentVersion;
  
  private String currentBuild;
  
  public String getCurrentVersion() {
    return this.currentVersion;
  }
  
  public void setCurrentVersion(String currentVersion) {
    this.currentVersion = currentVersion;
  }
  
  public String getCurrentBuild() {
    return this.currentBuild;
  }
  
  public void setCurrentBuild(String currentBuild) {
    this.currentBuild = currentBuild;
  }
}
