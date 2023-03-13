package com.xtremis.daedo.wtdata.model;

import java.io.Serializable;

public class Thresholds implements Serializable {
  private Integer body;
  
  private Integer head;
  
  public Integer getBody() {
    return this.body;
  }
  
  public void setBody(Integer body) {
    this.body = body;
  }
  
  public Integer getHead() {
    return this.head;
  }
  
  public void setHead(Integer head) {
    this.head = head;
  }
  
  public String toString() {
    return "Thresholds{body=" + this.body + ", head=" + this.head + '}';
  }
}
