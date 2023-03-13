package com.xtremis.daedo.tkstrike.ui.model;

import com.xtremis.daedo.tkstrike.orm.model.Entity;
import com.xtremis.daedo.tkstrike.orm.model.Phase;
import javafx.beans.property.SimpleStringProperty;

public class PhaseEntry implements Entry<Phase> {
  public SimpleStringProperty id = new SimpleStringProperty();
  
  public SimpleStringProperty name = new SimpleStringProperty();
  
  public SimpleStringProperty abbreviation = new SimpleStringProperty();
  
  public void fillByEntity(Phase phase) {
    if (phase != null) {
      this.id.set(phase.getId());
      this.name.set(phase.getName());
      this.abbreviation.set(phase.getAbbreviation());
    } 
  }
  
  public String getId() {
    return this.id.get();
  }
  
  public String getName() {
    return this.name.get();
  }
  
  public String getAbbreviation() {
    return this.abbreviation.get();
  }
  
  public SimpleStringProperty abbreviationProperty() {
    return this.abbreviation;
  }
  
  public String toString() {
    return getName();
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (!(o instanceof PhaseEntry))
      return false; 
    PhaseEntry that = (PhaseEntry)o;
    if (!this.id.equals(that.id))
      return false; 
    return true;
  }
  
  public int hashCode() {
    return this.id.hashCode();
  }
}
