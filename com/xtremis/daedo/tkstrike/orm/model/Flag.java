package com.xtremis.daedo.tkstrike.orm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "TKS_FLAG", uniqueConstraints = {@UniqueConstraint(columnNames = {"ABBREVIATION"})})
@NamedQueries({@NamedQuery(name = "Flag.getByAbbreviation", query = "SELECT f FROM Flag f WHERE f.abbreviation = ?1 ")})
public class Flag extends BaseEntity {
  @Column(name = "NAME")
  private String name;
  
  @Column(name = "ABBREVIATION")
  private String abbreviation;
  
  @Column(name = "FLAG_IMAGE_PATH")
  private String flagImagePath;
  
  @Column(name = "SHOW_NAME")
  private Boolean showName;
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getAbbreviation() {
    return this.abbreviation;
  }
  
  public void setAbbreviation(String abbreviation) {
    this.abbreviation = abbreviation;
  }
  
  public String getFlagImagePath() {
    return this.flagImagePath;
  }
  
  public void setFlagImagePath(String flagImagePath) {
    this.flagImagePath = flagImagePath;
  }
  
  public Boolean getShowName() {
    return this.showName;
  }
  
  public void setShowName(Boolean showName) {
    this.showName = showName;
  }
}
