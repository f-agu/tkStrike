package com.xtremis.daedo.wtdata.model;

import java.io.Serializable;

public class GoldenPoint implements Serializable {
  private Boolean enabled;
  
  private String time;
  
  public Boolean getEnabled() {
    return this.enabled;
  }
  
  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }
  
  public String getTime() {
    return this.time;
  }
  
  public void setTime(String time) {
    this.time = time;
  }
  
  public String toString() {
    return "GoldenPoint{enabled=" + this.enabled + ", time='" + this.time + '\'' + '}';
  }
}
