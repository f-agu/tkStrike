package com.xtremis.daedo.tkstrike.ui.model;

import java.io.Serializable;
import javafx.scene.control.Control;

public class FormValidationError implements Serializable {
  private static final long serialVersionUID = -7085566703255151019L;
  
  private Entry entry;
  
  private String entryFieldName;
  
  private Control uiControl;
  
  private String message;
  
  public FormValidationError(Entry entry, String entryFieldName, Control uiControl, String message) {
    this.entry = entry;
    this.entryFieldName = entryFieldName;
    this.uiControl = uiControl;
    this.message = message;
  }
  
  public Entry getEntry() {
    return this.entry;
  }
  
  public void setEntry(Entry entry) {
    this.entry = entry;
  }
  
  public String getEntryFieldName() {
    return this.entryFieldName;
  }
  
  public void setEntryFieldName(String entryFieldName) {
    this.entryFieldName = entryFieldName;
  }
  
  public Control getUiControl() {
    return this.uiControl;
  }
  
  public void setUiControl(Control uiControl) {
    this.uiControl = uiControl;
  }
  
  public String getMessage() {
    return this.message;
  }
  
  public void setMessage(String message) {
    this.message = message;
  }
}
