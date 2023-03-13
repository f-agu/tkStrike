package com.xtremis.daedo.tkstrike.ui.model;

import com.xtremis.daedo.tkstrike.orm.model.Entity;
import com.xtremis.daedo.tkstrike.orm.model.SubCategory;
import javafx.beans.property.SimpleStringProperty;

public class SubCategoryEntry implements Entry<SubCategory> {
  public SimpleStringProperty id = new SimpleStringProperty();
  
  public SimpleStringProperty name = new SimpleStringProperty();
  
  public void fillByEntity(SubCategory subCategory) {
    if (subCategory != null) {
      this.id.set(subCategory.getId());
      this.name.set(subCategory.getName());
    } 
  }
  
  public String getId() {
    return this.id.get();
  }
  
  public String getName() {
    return this.name.get();
  }
  
  public String toString() {
    return getName();
  }
}
