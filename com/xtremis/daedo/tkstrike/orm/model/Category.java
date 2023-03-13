package com.xtremis.daedo.tkstrike.orm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "TKS_CATEGORY")
@NamedQueries({@NamedQuery(name = "Category.findByGender", query = "SELECT c FROM Category c WHERE c.gender = ?1 "), @NamedQuery(name = "Category.findBySC_G", query = "SELECT c FROM Category c WHERE c.subCategory.id = ?1 AND c.gender = ?2 "), @NamedQuery(name = "Category.findBySubCategoryName", query = "SELECT c FROM Category c WHERE c.subCategory.name = ?1 "), @NamedQuery(name = "Category.findBySubCategoryId", query = "SELECT c FROM Category c WHERE c.subCategory.id = ?1 "), @NamedQuery(name = "Category.getBySC_G_N", query = "SELECT c FROM Category c WHERE upper(c.subCategory.name) = ?1 AND c.gender = ?2 AND upper(c.name) = ?3 ")})
public class Category extends BaseEntity {
  @Column(name = "NAME")
  private String name;
  
  @Enumerated(EnumType.STRING)
  @Column(name = "GENDER")
  private Gender gender;
  
  @ManyToOne
  @JoinColumn(name = "SUBCATEGORY_ID")
  private SubCategory subCategory;
  
  @Column(name = "BODY_LEVEL")
  private Integer bodyLevel;
  
  @Column(name = "HEAD_LEVEL")
  private Integer headLevel;
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public Gender getGender() {
    return this.gender;
  }
  
  public void setGender(Gender gender) {
    this.gender = gender;
  }
  
  public SubCategory getSubCategory() {
    return this.subCategory;
  }
  
  public void setSubCategory(SubCategory subCategory) {
    this.subCategory = subCategory;
  }
  
  public Integer getBodyLevel() {
    return this.bodyLevel;
  }
  
  public void setBodyLevel(Integer bodyLevel) {
    this.bodyLevel = bodyLevel;
  }
  
  public Integer getHeadLevel() {
    return this.headLevel;
  }
  
  public void setHeadLevel(Integer headLevel) {
    this.headLevel = headLevel;
  }
}
