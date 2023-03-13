package com.xtremis.daedo.tkstrike.orm.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "TKS_REFEREE")
public class Referee implements RefereeEntity, Serializable, Persistable<String> {
  @Id
  @Column(name = "ID")
  private String id;
  
  @Version
  private Integer version;
  
  @Column(name = "LICENSE_NUMBER")
  private String licenseNumber;
  
  @Column(name = "SCOREBOARD_NAME")
  private String scoreboardName;
  
  @Column(name = "COUNTRY")
  private String country;
  
  public String getId() {
    return this.id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public Integer getVersion() {
    return this.version;
  }
  
  public void setVersion(Integer version) {
    this.version = version;
  }
  
  public String getLicenseNumber() {
    return this.licenseNumber;
  }
  
  public void setLicenseNumber(String licenseNumber) {
    this.licenseNumber = licenseNumber;
  }
  
  public String getScoreboardName() {
    return this.scoreboardName;
  }
  
  public void setScoreboardName(String scoreboardName) {
    this.scoreboardName = scoreboardName;
  }
  
  public String getCountry() {
    return this.country;
  }
  
  public void setCountry(String country) {
    this.country = country;
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (!(o instanceof Referee))
      return false; 
    Referee that = (Referee)o;
    if ((this.id != null) ? !this.id.equals(that.id) : (that.id != null))
      return false; 
    return true;
  }
  
  public int hashCode() {
    return (this.id != null) ? this.id.hashCode() : 0;
  }
  
  public boolean isNew() {
    return (null == getId());
  }
}
