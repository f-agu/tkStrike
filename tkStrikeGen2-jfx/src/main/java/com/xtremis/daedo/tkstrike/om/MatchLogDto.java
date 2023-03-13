package com.xtremis.daedo.tkstrike.om;

import com.xtremis.daedo.tkstrike.orm.model.Gender;
import com.xtremis.daedo.tkstrike.tools.ei.om.RoundsConfigDto;
import org.apache.commons.lang3.ArrayUtils;

public class MatchLogDto extends CommonMatchLogDto<RoundsConfigDto> {
  private static final long serialVersionUID = 7442977645258155588L;
  
  private final String[] headers = new String[] { 
      "minBodyLevel", "minHeadLevel", "subCategoryName", "gender", "categoryName", "categoryGender", "categoryBodyLevel", "categoryHeadLevel", "blueAthleteName", "blueAthleteWtfId", 
      "blueAthleteFlagName", "blueAthleteFlagShowName", "blueAthleteFlagAbbreviation", "blueAthleteVideoQuota", "redAthleteName", "redAthleteWtfId", "redAthleteFlagName", "redAthleteFlagShowName", "redAthleteFlagAbbreviation", "redAthleteVideoQuota" };
  
  private Integer minBodyLevel;
  
  private Integer minHeadLevel;
  
  private String subCategoryId;
  
  private String subCategoryName;
  
  private Gender gender;
  
  private String categoryId;
  
  private String categoryName;
  
  private Gender categoryGender;
  
  private Integer categoryBodyLevel;
  
  private Integer categoryHeadLevel;
  
  private String blueAthleteId;
  
  private String blueAthleteName;
  
  private String blueAthleteWtfId;
  
  private String blueAthleteFlagId;
  
  private String blueAthleteFlagName;
  
  private Boolean blueAthleteFlagShowName;
  
  private String blueAthleteFlagAbbreviation;
  
  private String blueAthleteFlagImagePath;
  
  private Integer blueAthleteVideoQuota;
  
  private String redAthleteId;
  
  private String redAthleteName;
  
  private String redAthleteWtfId;
  
  private String redAthleteFlagId;
  
  private String redAthleteFlagName;
  
  private Boolean redAthleteFlagShowName;
  
  private String redAthleteFlagAbbreviation;
  
  private String redAthleteFlagImagePath;
  
  private Integer redAthleteVideoQuota;
  
  public String getSubCategoryId() {
    return this.subCategoryId;
  }
  
  public void setSubCategoryId(String subCategoryId) {
    this.subCategoryId = subCategoryId;
  }
  
  public String getSubCategoryName() {
    return this.subCategoryName;
  }
  
  public void setSubCategoryName(String subCategoryName) {
    this.subCategoryName = subCategoryName;
  }
  
  public Gender getGender() {
    return this.gender;
  }
  
  public void setGender(Gender gender) {
    this.gender = gender;
  }
  
  public String getCategoryId() {
    return this.categoryId;
  }
  
  public void setCategoryId(String categoryId) {
    this.categoryId = categoryId;
  }
  
  public String getCategoryName() {
    return this.categoryName;
  }
  
  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }
  
  public Gender getCategoryGender() {
    return this.categoryGender;
  }
  
  public void setCategoryGender(Gender categoryGender) {
    this.categoryGender = categoryGender;
  }
  
  public Integer getCategoryBodyLevel() {
    return this.categoryBodyLevel;
  }
  
  public void setCategoryBodyLevel(Integer categoryBodyLevel) {
    this.categoryBodyLevel = categoryBodyLevel;
  }
  
  public Integer getCategoryHeadLevel() {
    return this.categoryHeadLevel;
  }
  
  public void setCategoryHeadLevel(Integer categoryHeadLevel) {
    this.categoryHeadLevel = categoryHeadLevel;
  }
  
  public String getBlueAthleteId() {
    return this.blueAthleteId;
  }
  
  public void setBlueAthleteId(String blueAthleteId) {
    this.blueAthleteId = blueAthleteId;
  }
  
  public String getBlueAthleteName() {
    return this.blueAthleteName;
  }
  
  public void setBlueAthleteName(String blueAthleteName) {
    this.blueAthleteName = blueAthleteName;
  }
  
  public String getBlueAthleteWtfId() {
    return this.blueAthleteWtfId;
  }
  
  public void setBlueAthleteWtfId(String blueAthleteWtfId) {
    this.blueAthleteWtfId = blueAthleteWtfId;
  }
  
  public String getBlueAthleteFlagId() {
    return this.blueAthleteFlagId;
  }
  
  public void setBlueAthleteFlagId(String blueAthleteFlagId) {
    this.blueAthleteFlagId = blueAthleteFlagId;
  }
  
  public Integer getBlueAthleteVideoQuota() {
    return this.blueAthleteVideoQuota;
  }
  
  public void setBlueAthleteVideoQuota(Integer blueAthleteVideoQuota) {
    this.blueAthleteVideoQuota = blueAthleteVideoQuota;
  }
  
  public String getRedAthleteId() {
    return this.redAthleteId;
  }
  
  public void setRedAthleteId(String redAthleteId) {
    this.redAthleteId = redAthleteId;
  }
  
  public String getRedAthleteName() {
    return this.redAthleteName;
  }
  
  public void setRedAthleteName(String redAthleteName) {
    this.redAthleteName = redAthleteName;
  }
  
  public String getRedAthleteWtfId() {
    return this.redAthleteWtfId;
  }
  
  public void setRedAthleteWtfId(String redAthleteWtfId) {
    this.redAthleteWtfId = redAthleteWtfId;
  }
  
  public String getRedAthleteFlagId() {
    return this.redAthleteFlagId;
  }
  
  public void setRedAthleteFlagId(String redAthleteFlagId) {
    this.redAthleteFlagId = redAthleteFlagId;
  }
  
  public Integer getRedAthleteVideoQuota() {
    return this.redAthleteVideoQuota;
  }
  
  public void setRedAthleteVideoQuota(Integer redAthleteVideoQuota) {
    this.redAthleteVideoQuota = redAthleteVideoQuota;
  }
  
  public String getBlueAthleteFlagAbbreviation() {
    return this.blueAthleteFlagAbbreviation;
  }
  
  public void setBlueAthleteFlagAbbreviation(String blueAthleteFlagAbbreviation) {
    this.blueAthleteFlagAbbreviation = blueAthleteFlagAbbreviation;
  }
  
  public String getRedAthleteFlagAbbreviation() {
    return this.redAthleteFlagAbbreviation;
  }
  
  public void setRedAthleteFlagAbbreviation(String redAthleteFlagAbbreviation) {
    this.redAthleteFlagAbbreviation = redAthleteFlagAbbreviation;
  }
  
  public String getBlueAthleteFlagImagePath() {
    return this.blueAthleteFlagImagePath;
  }
  
  public void setBlueAthleteFlagImagePath(String blueAthleteFlagImagePath) {
    this.blueAthleteFlagImagePath = blueAthleteFlagImagePath;
  }
  
  public String getRedAthleteFlagImagePath() {
    return this.redAthleteFlagImagePath;
  }
  
  public void setRedAthleteFlagImagePath(String redAthleteFlagImagePath) {
    this.redAthleteFlagImagePath = redAthleteFlagImagePath;
  }
  
  public Integer getMinBodyLevel() {
    return this.minBodyLevel;
  }
  
  public void setMinBodyLevel(Integer minBodyLevel) {
    this.minBodyLevel = minBodyLevel;
  }
  
  public Integer getMinHeadLevel() {
    return this.minHeadLevel;
  }
  
  public void setMinHeadLevel(Integer minHeadLevel) {
    this.minHeadLevel = minHeadLevel;
  }
  
  public String getBlueAthleteFlagName() {
    return this.blueAthleteFlagName;
  }
  
  public void setBlueAthleteFlagName(String blueAthleteFlagName) {
    this.blueAthleteFlagName = blueAthleteFlagName;
  }
  
  public Boolean getBlueAthleteFlagShowName() {
    return this.blueAthleteFlagShowName;
  }
  
  public void setBlueAthleteFlagShowName(Boolean blueAthleteFlagShowName) {
    this.blueAthleteFlagShowName = blueAthleteFlagShowName;
  }
  
  public String getRedAthleteFlagName() {
    return this.redAthleteFlagName;
  }
  
  public void setRedAthleteFlagName(String redAthleteFlagName) {
    this.redAthleteFlagName = redAthleteFlagName;
  }
  
  public Boolean getRedAthleteFlagShowName() {
    return this.redAthleteFlagShowName;
  }
  
  public void setRedAthleteFlagShowName(Boolean redAthleteFlagShowName) {
    this.redAthleteFlagShowName = redAthleteFlagShowName;
  }
  
  public String[] getCSVHeaders() {
    return (String[])ArrayUtils.addAll((Object[])super.getCSVHeaders(), (Object[])this.headers);
  }
  
  public String[] getCSVValues() {
    return super.getCSVValues();
  }
}
