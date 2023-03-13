package com.xtremis.daedo.tkstrike.tools.om;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "res")
@XmlRootElement(name = "res")
public class BasePaginatedResponseDto<D extends ItemDto> extends BaseListResponseDto<D> {
  @XmlElement(name = "pageNumber", required = true)
  private Integer pageNumber;
  
  @XmlElement(name = "pageSize", required = true)
  private Integer pageSize;
  
  @XmlElement(name = "previousPage", required = true)
  private boolean previousPage;
  
  @XmlElement(name = "nextPage", required = true)
  private boolean nextPage;
  
  @XmlElement(name = "lastPage", required = true)
  private boolean lastPage;
  
  @XmlElement(name = "totalPages", required = true)
  private Integer totalPages;
  
  @XmlElement(name = "totalRows", required = true)
  private Long totalRows;
  
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
  
  public boolean isPreviousPage() {
    return this.previousPage;
  }
  
  public void setPreviousPage(boolean previousPage) {
    this.previousPage = previousPage;
  }
  
  public boolean isNextPage() {
    return this.nextPage;
  }
  
  public void setNextPage(boolean nextPage) {
    this.nextPage = nextPage;
  }
  
  public boolean isLastPage() {
    return this.lastPage;
  }
  
  public void setLastPage(boolean lastPage) {
    this.lastPage = lastPage;
  }
  
  public Integer getTotalPages() {
    return this.totalPages;
  }
  
  public void setTotalPages(Integer totalPages) {
    this.totalPages = totalPages;
  }
  
  public Long getTotalRows() {
    return this.totalRows;
  }
  
  public void setTotalRows(Long totalRows) {
    this.totalRows = totalRows;
  }
}
