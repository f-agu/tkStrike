package com.xtremis.daedo.tkstrike.ui.scene;

import com.xtremis.daedo.tkstrike.orm.model.Entity;
import com.xtremis.daedo.tkstrike.orm.model.Referee;
import com.xtremis.daedo.tkstrike.tools.ei.om.RefereeDto;
import com.xtremis.daedo.tkstrike.ui.model.Entry;
import javafx.beans.property.SimpleStringProperty;
import org.springframework.beans.BeanUtils;

public class RefereeEntry implements Entry<Referee> {
  private SimpleStringProperty id = new SimpleStringProperty(this, "id");
  
  private SimpleStringProperty licenseNumber = new SimpleStringProperty(this, "licenseNumber");
  
  private SimpleStringProperty scoreboardName = new SimpleStringProperty(this, "scoreboardName");
  
  private SimpleStringProperty country = new SimpleStringProperty(this, "country");
  
  public void fillByEntity(Referee entity) {
    if (entity != null) {
      this.id.set(entity.getId());
      this.licenseNumber.set(entity.getLicenseNumber());
      this.scoreboardName.set(entity.getScoreboardName());
      this.country.set(entity.getCountry());
    } 
  }
  
  public RefereeDto getRefereeDto() {
    RefereeDto res = new RefereeDto();
    BeanUtils.copyProperties(this, res);
    return res;
  }
  
  public String getId() {
    return this.id.get();
  }
  
  public SimpleStringProperty idProperty() {
    return this.id;
  }
  
  public void setId(String id) {
    this.id.set(id);
  }
  
  public String getLicenseNumber() {
    return this.licenseNumber.get();
  }
  
  public SimpleStringProperty licenseNumberProperty() {
    return this.licenseNumber;
  }
  
  public void setLicenseNumber(String licenseNumber) {
    this.licenseNumber.set(licenseNumber);
  }
  
  public String getScoreboardName() {
    return this.scoreboardName.get();
  }
  
  public SimpleStringProperty scoreboardNameProperty() {
    return this.scoreboardName;
  }
  
  public void setScoreboardName(String scoreboardName) {
    this.scoreboardName.set(scoreboardName);
  }
  
  public String getCountry() {
    return this.country.get();
  }
  
  public SimpleStringProperty countryProperty() {
    return this.country;
  }
  
  public void setCountry(String country) {
    this.country.set(country);
  }
}
