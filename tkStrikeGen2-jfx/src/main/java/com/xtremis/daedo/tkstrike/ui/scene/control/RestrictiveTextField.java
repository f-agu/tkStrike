package com.xtremis.daedo.tkstrike.ui.scene.control;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class RestrictiveTextField extends TextField {
  private IntegerProperty maxLength = (IntegerProperty)new SimpleIntegerProperty(this, "maxLength", -1);
  
  private StringProperty restrict = (StringProperty)new SimpleStringProperty(this, "restrict");
  
  private StringProperty defaultValue = (StringProperty)new SimpleStringProperty(this, "defaultValue", null);
  
  public RestrictiveTextField() {
    textProperty().addListener(new ChangeListener<String>() {
          private boolean ignore;
          
          public void changed(ObservableValue<? extends String> observableValue, String s, String s1) {
            if (this.ignore || s1 == null)
              return; 
            if (RestrictiveTextField.this.maxLength.get() > -1 && s1.length() > RestrictiveTextField.this.maxLength.get()) {
              this.ignore = true;
              if (RestrictiveTextField.this.defaultValue.get() != null) {
                RestrictiveTextField.this.setText((String)RestrictiveTextField.this.defaultValue.get());
              } else {
                RestrictiveTextField.this.setText(s1.substring(0, RestrictiveTextField.this.maxLength.get()));
              } 
              this.ignore = false;
            } 
            if (RestrictiveTextField.this.restrict.get() != null && !((String)RestrictiveTextField.this.restrict.get()).equals("") && !s1.matches((String)RestrictiveTextField.this.restrict.get() + "*")) {
              this.ignore = true;
              if (RestrictiveTextField.this.defaultValue.get() != null) {
                RestrictiveTextField.this.setText((String)RestrictiveTextField.this.defaultValue.get());
              } else {
                RestrictiveTextField.this.setText(s);
              } 
              this.ignore = false;
            } 
          }
        });
  }
  
  public IntegerProperty maxLengthProperty() {
    return this.maxLength;
  }
  
  public int getMaxLength() {
    return this.maxLength.get();
  }
  
  public void setMaxLength(int maxLength) {
    this.maxLength.set(maxLength);
  }
  
  public StringProperty restrictProperty() {
    return this.restrict;
  }
  
  public String getRestrict() {
    return (String)this.restrict.get();
  }
  
  public void setRestrict(String restrict) {
    this.restrict.set(restrict);
  }
  
  public String getDefaultValue() {
    return (String)this.defaultValue.get();
  }
  
  public StringProperty defaultValueProperty() {
    return this.defaultValue;
  }
  
  public void setDefaultValue(String defaultValue) {
    this.defaultValue.set(defaultValue);
  }
}
