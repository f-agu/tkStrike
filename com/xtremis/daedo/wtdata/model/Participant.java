package com.xtremis.daedo.wtdata.model;

import com.xtremis.daedo.wtdata.model.constants.Gender;
import com.xtremis.daedo.wtdata.model.constants.Role;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import java.io.Serializable;

@JsonApiResource(type = "participants")
public class Participant implements Serializable {
  @JsonApiId
  private String id;
  
  private String licenseNumber;
  
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
  
  private String scoreboardName;
  
  private Gender gender;
  
  private String birthDate;
  
  private Role mainRole;
  
  private String country;
  
  @JsonApiRelation
  private Organization organization;
  
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
  
  public String getScoreboardName() {
    return this.scoreboardName;
  }
  
  public void setScoreboardName(String scoreboardName) {
    this.scoreboardName = scoreboardName;
  }
  
  public Gender getGender() {
    return this.gender;
  }
  
  public void setGender(Gender gender) {
    this.gender = gender;
  }
  
  public String getBirthDate() {
    return this.birthDate;
  }
  
  public void setBirthDate(String birthDate) {
    this.birthDate = birthDate;
  }
  
  public Role getMainRole() {
    return this.mainRole;
  }
  
  public void setMainRole(Role mainRole) {
    this.mainRole = mainRole;
  }
  
  public String getCountry() {
    return this.country;
  }
  
  public void setCountry(String country) {
    this.country = country;
  }
  
  public Organization getOrganization() {
    return this.organization;
  }
  
  public void setOrganization(Organization organization) {
    this.organization = organization;
  }
  
  public String toString() {
    return "Participant{id='" + this.id + '\'' + ", licenseNumber='" + this.licenseNumber + '\'' + ", givenName='" + this.givenName + '\'' + ", familyName='" + this.familyName + '\'' + ", passportGivenName='" + this.passportGivenName + '\'' + ", passportFamilyName='" + this.passportFamilyName + '\'' + ", preferredGivenName='" + this.preferredGivenName + '\'' + ", preferredFamilyName='" + this.preferredFamilyName + '\'' + ", printName='" + this.printName + '\'' + ", printInitialName='" + this.printInitialName + '\'' + ", tvName='" + this.tvName + '\'' + ", tvInitialName='" + this.tvInitialName + '\'' + ", scoreboardName='" + this.scoreboardName + '\'' + ", gender='" + this.gender + '\'' + ", birthDate='" + this.birthDate + '\'' + ", mainRole='" + this.mainRole + '\'' + ", country='" + this.country + '\'' + ", organization=" + this.organization + '}';
  }
}
