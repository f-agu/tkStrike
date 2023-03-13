package com.xtremis.daedo.tkstrike.ui.model;

import com.xtremis.daedo.tkstrike.orm.model.Category;
import com.xtremis.daedo.tkstrike.orm.model.Entity;
import com.xtremis.daedo.tkstrike.orm.model.Gender;
import com.xtremis.daedo.tkstrike.tools.ei.om.CategoryDto;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class CategoryEntry implements Entry<Category> {
  private SimpleStringProperty id = new SimpleStringProperty(this, "id");
  
  private SimpleStringProperty name = new SimpleStringProperty(this, "name");
  
  private SimpleObjectProperty<Gender> gender = new SimpleObjectProperty(this, "gender");
  
  private SimpleObjectProperty<SubCategoryEntry> subCategory = new SimpleObjectProperty(this, "subCategory");
  
  private SimpleIntegerProperty bodyLevel = new SimpleIntegerProperty(this, "bodyLevel");
  
  private SimpleIntegerProperty headLevel = new SimpleIntegerProperty(this, "headLevel");
  
  public void fillByEntity(Category category) {
    if (category != null) {
      this.id.set(category.getId());
      this.name.set(category.getName());
      this.gender.set(category.getGender());
      SubCategoryEntry subCategoryEntry = new SubCategoryEntry();
      subCategoryEntry.fillByEntity(category.getSubCategory());
      this.subCategory.set(subCategoryEntry);
      this.bodyLevel.set(category.getBodyLevel().intValue());
      this.headLevel.set(category.getHeadLevel().intValue());
    } 
  }
  
  public CategoryDto getCategoryDto() {
    CategoryDto res = new CategoryDto();
    res.setSubCategory((getSubCategory() != null) ? getSubCategory().getName() : null);
    res.setGender((getGender() != null) ? getGender().toString() : null);
    res.setName(getName());
    res.setHeadLevel(Integer.valueOf(getHeadLevel()));
    res.setBodyLevel(Integer.valueOf(getBodyLevel()));
    return res;
  }
  
  public SimpleStringProperty idProperty() {
    return this.id;
  }
  
  public String getName() {
    return this.name.get();
  }
  
  public SimpleStringProperty nameProperty() {
    return this.name;
  }
  
  public SubCategoryEntry getSubCategory() {
    return (SubCategoryEntry)this.subCategory.get();
  }
  
  public SimpleObjectProperty<SubCategoryEntry> subCategoryProperty() {
    return this.subCategory;
  }
  
  public int getBodyLevel() {
    return this.bodyLevel.get();
  }
  
  public SimpleIntegerProperty bodyLevelProperty() {
    return this.bodyLevel;
  }
  
  public int getHeadLevel() {
    return this.headLevel.get();
  }
  
  public SimpleIntegerProperty headLevelProperty() {
    return this.headLevel;
  }
  
  public Gender getGender() {
    return (Gender)this.gender.get();
  }
  
  public SimpleObjectProperty<Gender> genderProperty() {
    return this.gender;
  }
  
  public String getId() {
    return this.id.get();
  }
  
  public String toString() {
    return getName();
  }
}
