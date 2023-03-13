package com.xtremis.daedo.wtdata.model;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import java.io.Serializable;

@JsonApiResource(type = "organizations")
public class Organization implements Serializable {
  @JsonApiId
  private String id;
  
  private String name;
  
  private String country;
  
  public String getId() {
    return this.id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getCountry() {
    return this.country;
  }
  
  public void setCountry(String country) {
    this.country = country;
  }
}
