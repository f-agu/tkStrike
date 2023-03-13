package com.xtremis.daedo.tkstrike.tools.om;

import javax.xml.bind.annotation.XmlElement;

public abstract class BasePaginatedRequestDto extends BaseJSONDto {
  @XmlElement(name = "pageNumber", required = true)
  private Integer pageNumber;
  
  @XmlElement(name = "pageSize", required = true)
  private Integer pageSize;
  
  public Integer getPageNumber() {
    return this.pageNumber;
  }
  
  public void setPageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
  }
  
  public Integer getPageSize() {
    return this.pageSize;
  }
  
  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }
}
