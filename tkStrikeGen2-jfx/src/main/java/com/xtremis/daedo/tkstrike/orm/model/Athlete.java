package com.xtremis.daedo.tkstrike.orm.model;

import com.xtremis.daedo.wtdata.model.constants.CompetitorType;
import com.xtremis.daedo.wtdata.model.constants.Gender;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "TKS_ATHLETE", uniqueConstraints = {@UniqueConstraint(columnNames = {"WF_ID"})})
@NamedQueries({@NamedQuery(name = "Athlete.getByWfId", query = "SELECT a FROM Athlete a WHERE a.wfId = ?1 "), @NamedQuery(name = "Athlete.getByOvrInternalId", query = "SELECT a FROM Athlete a WHERE a.ovrInternalId IS NOT NULL AND a.ovrInternalId = ?1 "), @NamedQuery(name = "Athlete.findByFlagId", query = "SELECT a FROM Athlete a WHERE a.flag.id = ?1 "), @NamedQuery(name = "Athlete.findByFlagName", query = "SELECT a FROM Athlete a WHERE a.flag.name = ?1 "), @NamedQuery(name = "Athlete.findByFlagAbbreviation", query = "SELECT a FROM Athlete a WHERE a.flag.abbreviation = ?1 ")})
public class Athlete extends BaseEntity {
  @Column(name = "SCOREBOARD_NAME")
  private String scoreboardName;
  
  @Column(name = "WF_ID")
  private String wfId;
  
  @Column(name = "OVR_INTERNAL_ID")
  private String ovrInternalId;
  
  @ManyToOne
  @JoinColumn(name = "FLAG_ID")
  private Flag flag;
  
  @Column(name = "GIVEN_NAME")
  private String givenName;
  
  @Column(name = "FAMILY_NAME")
  private String familyName;
  
  @Column(name = "PASSPORT_GIVEN_NAME")
  private String passportGivenName;
  
  @Column(name = "PASSPORT_FAMILY_NAME")
  private String passportFamilyName;
  
  @Column(name = "PREFERRED_GIVEN_NAME")
  private String preferredGivenName;
  
  @Column(name = "PREFERRED_FAMILY_NAME")
  private String preferredFamilyName;
  
  @Column(name = "PRINT_NAME")
  private String printName;
  
  @Column(name = "PRINT_INITIAL_NAME")
  private String printInitialName;
  
  @Column(name = "TV_NAME")
  private String tvName;
  
  @Column(name = "TV_INITIAL_NAME")
  private String tvInitialName;
  
  @Column(name = "GENDER")
  @Enumerated(EnumType.STRING)
  private Gender gender;
  
  @Column(name = "BIRTH_DATE")
  private Date birthDate;
  
  @Column(name = "COMPETITOR_TYPE")
  @Enumerated(EnumType.STRING)
  private CompetitorType competitorType;
  
  @Column(name = "RANK")
  private Integer rank;
  
  @Column(name = "SEED")
  private Integer seed;
  
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
  
  public String getOvrInternalId() {
    return this.ovrInternalId;
  }
  
  public void setOvrInternalId(String ovrInternalId) {
    this.ovrInternalId = ovrInternalId;
  }
  
  public Flag getFlag() {
    return this.flag;
  }
  
  public void setFlag(Flag flag) {
    this.flag = flag;
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
  
  public Gender getGender() {
    return this.gender;
  }
  
  public void setGender(Gender gender) {
    this.gender = gender;
  }
  
  public Date getBirthDate() {
    return this.birthDate;
  }
  
  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }
  
  public CompetitorType getCompetitorType() {
    return this.competitorType;
  }
  
  public void setCompetitorType(CompetitorType competitorType) {
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
