package com.xtremis.daedo.tkstrike.tools.su.om;

import com.xtremis.daedo.tkstrike.tools.om.BaseJSONDto;

public class TkStrikeOSReleaseInfoDto extends BaseJSONDto {
  private OSType OSType;
  
  private String osIconImage;
  
  private String releaseId;
  
  private String downloadURL;
  
  public OSType getOSType() {
    return this.OSType;
  }
  
  public void setOSType(OSType oSType) {
    this.OSType = oSType;
  }
  
  public String getOsIconImage() {
    return this.osIconImage;
  }
  
  public void setOsIconImage(String osIconImage) {
    this.osIconImage = osIconImage;
  }
  
  public String getReleaseId() {
    return this.releaseId;
  }
  
  public void setReleaseId(String releaseId) {
    this.releaseId = releaseId;
  }
  
  public String getDownloadURL() {
    return this.downloadURL;
  }
  
  public void setDownloadURL(String downloadURL) {
    this.downloadURL = downloadURL;
  }
}
