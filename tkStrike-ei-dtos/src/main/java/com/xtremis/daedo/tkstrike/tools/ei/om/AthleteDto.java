package com.xtremis.daedo.tkstrike.tools.ei.om;

import com.xtremis.daedo.tkstrike.tools.om.BaseJSONDto;
import java.util.Date;

public class AthleteDto extends BaseJSONDto {
  private String ovrInternalId;
  
  private String scoreboardName;
  
  private String wfId;
  
  private String flagAbbreviation;
  
  private String flagName;
  
  private Boolean flagShowName;
  
  private String givenName;
  
  private String familyName;
  
  private String passportGivenName;
  
  private String passportFamilyName;
  
  private String preferredGivenName;
  
  private String preferredFamilyName;
  
  private String printName;
  
  private String printInitialName;
  
  private String tvName;
  
  private String tvInitialName;
  
  private String gender;
  
  private Date birthDate;
  
  private String competitorType;
  
  private Integer rank;
  
  private Integer seed;
  
  public String getOvrInternalId() {
    return this.ovrInternalId;
  }
  
  public void setOvrInternalId(String ovrInternalId) {
    this.ovrInternalId = ovrInternalId;
  }
  
  public String getScoreboardName() {
    return this.scoreboardName;
  }
  
  public void setScoreboardName(String scoreboardName) {
    this.scoreboardName = scoreboardName;
  }
  
  public String getWfId() {
    return this.wfId;
  }
  
  public void setWfId(String wfId) {
    this.wfId = wfId;
  }
  
  public String getFlagAbbreviation() {
    return this.flagAbbreviation;
  }
  
  public void setFlagAbbreviation(String flagAbbreviation) {
    this.flagAbbreviation = flagAbbreviation;
  }
  
  public String getFlagName() {
    return this.flagName;
  }
  
  public void setFlagName(String flagName) {
    this.flagName = flagName;
  }
  
  public Boolean getFlagShowName() {
    return this.flagShowName;
  }
  
  public void setFlagShowName(Boolean flagShowName) {
    this.flagShowName = flagShowName;
  }
  
  public String getGivenName() {
    return this.givenName;
  }
  
  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }
  
  public String getFamilyName() {
    return this.familyName;
  }
  
  public void setFamilyName(String familyName) {
    this.familyName = familyName;
  }
  
  public String getPassportGivenName() {
    return this.passportGivenName;
  }
  
  public void setPassportGivenName(String passportGivenName) {
    this.passportGivenName = passportGivenName;
  }
  
  public String getPassportFamilyName() {
    return this.passportFamilyName;
  }
  
  public void setPassportFamilyName(String passportFamilyName) {
    this.passportFamilyName = passportFamilyName;
  }
  
  public String getPreferredGivenName() {
    return this.preferredGivenName;
  }
  
  public void setPreferredGivenName(String preferredGivenName) {
    this.preferredGivenName = preferredGivenName;
  }
  
  public String getPreferredFamilyName() {
    return this.preferredFamilyName;
  }
  
  public void setPreferredFamilyName(String preferredFamilyName) {
    this.preferredFamilyName = preferredFamilyName;
  }
  
  public String getPrintName() {
    return this.printName;
  }
  
  public void setPrintName(String printName) {
    this.printName = printName;
  }
  
  public String getPrintInitialName() {
    return this.printInitialName;
  }
  
  public void setPrintInitialName(String printInitialName) {
    this.printInitialName = printInitialName;
  }
  
  public String getTvName() {
    return this.tvName;
  }
  
  public void setTvName(String tvName) {
    this.tvName = tvName;
  }
  
  public String getTvInitialName() {
    return this.tvInitialName;
  }
  
  public void setTvInitialName(String tvInitialName) {
    this.tvInitialName = tvInitialName;
  }
  
  public String getGender() {
    return this.gender;
  }
  
  public void setGender(String gender) {
    this.gender = gender;
  }
  
  public Date getBirthDate() {
    return this.birthDate;
  }
  
  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }
  
  public String getCompetitorType() {
    return this.competitorType;
  }
  
  public void setCompetitorType(String competitorType) {
    this.competitorType = competitorType;
  }
  
  public Integer getRank() {
    return this.rank;
  }
  
  public void setRank(Integer rank) {
    this.rank = rank;
  }
  
  public Integer getSeed() {
    return this.seed;
  }
  
  public void setSeed(Integer seed) {
    this.seed = seed;
  }
}
