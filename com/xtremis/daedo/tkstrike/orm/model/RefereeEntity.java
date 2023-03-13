package com.xtremis.daedo.tkstrike.orm.model;

public interface RefereeEntity extends Entity {
  void setId(String paramString);
  
  String getLicenseNumber();
  
  void setLicenseNumber(String paramString);
  
  String getScoreboardName();
  
  void setScoreboardName(String paramString);
  
  String getCountry();
  
  void setCountry(String paramString);
}
