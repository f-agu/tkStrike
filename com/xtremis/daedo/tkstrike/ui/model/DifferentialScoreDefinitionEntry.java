package com.xtremis.daedo.tkstrike.ui.model;

import com.xtremis.daedo.tkstrike.orm.model.DifferentialScoreDefinition;
import com.xtremis.daedo.tkstrike.orm.model.Entity;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import org.springframework.beans.BeanUtils;

public class DifferentialScoreDefinitionEntry implements Entry<DifferentialScoreDefinition> {
  private SimpleStringProperty id = new SimpleStringProperty(this, "id");
  
  private SimpleObjectProperty<PhaseEntry> phase = new SimpleObjectProperty(this, "phase");
  
  private SimpleObjectProperty<SubCategoryEntry> subCategory = new SimpleObjectProperty(this, "subCategory");
  
  private SimpleIntegerProperty value = new SimpleIntegerProperty(this, "value");
  
  public void fillByEntity(DifferentialScoreDefinition entity) {
    BeanUtils.copyProperties(entity, this, new String[] { "phase", "subCategory" });
    if (entity.getPhase() != null) {
      PhaseEntry phaseEntry = new PhaseEntry();
      phaseEntry.fillByEntity(entity.getPhase());
      setPhase(phaseEntry);
    } else {
      setPhase(null);
    } 
    if (entity.getSubCategory() != null) {
      SubCategoryEntry subCategoryEntry = new SubCategoryEntry();
      subCategoryEntry.fillByEntity(entity.getSubCategory());
      setSubCategory(subCategoryEntry);
    } else {
      setSubCategory(null);
    } 
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
  
  public PhaseEntry getPhase() {
    return (PhaseEntry)this.phase.get();
  }
  
  public SimpleObjectProperty<PhaseEntry> phaseProperty() {
    return this.phase;
  }
  
  public void setPhase(PhaseEntry phase) {
    this.phase.set(phase);
  }
  
  public SubCategoryEntry getSubCategory() {
    return (SubCategoryEntry)this.subCategory.get();
  }
  
  public SimpleObjectProperty<SubCategoryEntry> subCategoryProperty() {
    return this.subCategory;
  }
  
  public void setSubCategory(SubCategoryEntry subCategory) {
    this.subCategory.set(subCategory);
  }
  
  public int getValue() {
    return this.value.get();
  }
  
  public SimpleIntegerProperty valueProperty() {
    return this.value;
  }
  
  public void setValue(int value) {
    this.value.set(value);
  }
}
