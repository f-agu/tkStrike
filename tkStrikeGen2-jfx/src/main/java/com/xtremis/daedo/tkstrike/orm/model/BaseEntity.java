package com.xtremis.daedo.tkstrike.orm.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.domain.Persistable;

@MappedSuperclass
public abstract class BaseEntity implements Serializable, Persistable<String>, Entity {
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "ID")
  private String id;
  
  @Version
  private Integer version;
  
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
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (!(o instanceof BaseEntity))
      return false; 
    BaseEntity that = (BaseEntity)o;
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
