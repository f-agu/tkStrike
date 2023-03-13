package com.xtremis.daedo.wtdata.model;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import java.io.Serializable;

@JsonApiResource(type = "medal-winners")
public class MedalWinner implements Serializable {
  @JsonApiId
  private String id;
  
  private Integer position;
  
  private String medalType;
  
  public String getId() {
    return this.id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public Integer getPosition() {
    return this.position;
  }
  
  public void setPosition(Integer position) {
    this.position = position;
  }
  
  public String getMedalType() {
    return this.medalType;
  }
  
  public void setMedalType(String medalType) {
    this.medalType = medalType;
  }
}
