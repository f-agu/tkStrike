package com.xtremis.daedo.tkstrike.orm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "TKS_DIFFERENTIAL_SCORE_DEF")
@NamedQueries({@NamedQuery(name = "DifferentialScoreDefinition.getByPhaseIdAndSubCategoryId", query = "SELECT d FROM DifferentialScoreDefinition d WHERE d.phase.id = ?1 AND d.subCategory.id = ?2 ")})
public class DifferentialScoreDefinition extends BaseEntity {
  private static final long serialVersionUID = 3467862160377090342L;
  
  @ManyToOne
  @JoinColumn(name = "PHASE_ID")
  private Phase phase;
  
  @ManyToOne
  @JoinColumn(name = "SUBCATEGORY_ID")
  private SubCategory subCategory;
  
  @Column(name = "THE_VALUE")
  private Integer value;
  
  public Phase getPhase() {
    return this.phase;
  }
  
  public void setPhase(Phase phase) {
    this.phase = phase;
  }
  
  public SubCategory getSubCategory() {
    return this.subCategory;
  }
  
  public void setSubCategory(SubCategory subCategory) {
    this.subCategory = subCategory;
  }
  
  public Integer getValue() {
    return this.value;
  }
  
  public void setValue(Integer value) {
    this.value = value;
  }
}
