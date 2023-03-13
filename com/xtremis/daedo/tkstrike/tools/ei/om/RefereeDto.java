package com.xtremis.daedo.tkstrike.tools.ei.om;

import com.xtremis.daedo.tkstrike.tools.om.BaseJSONDto;

public class RefereeDto extends BaseJSONDto {
  private String id;
  
  private String licenseNumber;
  
  private String scoreboardName;
  
  private String country;
  
  public RefereeDto() {}
  
  public RefereeDto(String id, String licenseNumber, String scoreboardName, String country) {
    this.id = id;
    this.licenseNumber = licenseNumber;
    this.scoreboardName = scoreboardName;
    this.country = country;
  }
  
  public String getId() {
    return this.id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getLicenseNumber() {
    return this.licenseNumber;
  }
  
  public void setLicenseNumber(String licenseNumber) {
    this.licenseNumber = licenseNumber;
  }
  
  public String getScoreboardName() {
    return this.scoreboardName;
  }
  
  public void setScoreboardName(String scoreboardName) {
    this.scoreboardName = scoreboardName;
  }
  
  public String getCountry() {
    return this.country;
  }
  
  public void setCountry(String country) {
    this.country = country;
  }
}
