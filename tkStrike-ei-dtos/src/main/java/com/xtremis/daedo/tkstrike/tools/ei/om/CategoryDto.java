package com.xtremis.daedo.tkstrike.tools.ei.om;

import com.xtremis.daedo.tkstrike.tools.om.BaseJSONDto;

public class CategoryDto extends BaseJSONDto {
  private static final long serialVersionUID = -7313945600504178993L;
  
  private String name;
  
  private String gender;
  
  private String subCategory;
  
  private Integer bodyLevel;
  
  private Integer headLevel;
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getGender() {
    return this.gender;
  }
  
  public void setGender(String gender) {
    this.gender = gender;
  }
  
  public String getSubCategory() {
    return this.subCategory;
  }
  
  public void setSubCategory(String subCategory) {
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
