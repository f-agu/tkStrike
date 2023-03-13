package com.xtremis.daedo.tkstrike.orm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "TKS_SUBCATEGORY")
@NamedQueries({@NamedQuery(name = "Subcategory.getScByName", query = "SELECT sc FROM Subcategory sc WHERE upper(sc.name) = ?1 ")})
public class SubCategory extends BaseEntity {
  @Column(name = "NAME")
  private String name;
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
}
