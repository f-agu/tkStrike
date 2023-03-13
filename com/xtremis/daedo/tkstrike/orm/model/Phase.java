package com.xtremis.daedo.tkstrike.orm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "TKS_PHASE")
@NamedQueries({@NamedQuery(name = "Phase.getByName", query = "SELECT p FROM Phase p WHERE upper(p.name) = ?1 "), @NamedQuery(name = "Phase.getByAbbreviation", query = "SELECT p FROM Phase p WHERE upper(p.abbreviation) = ?1 ")})
public class Phase extends BaseEntity implements PhaseEntity {
  @Column(name = "NAME")
  private String name;
  
  @Column(name = "ABBREVIATION")
  private String abbreviation;
  
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
}
