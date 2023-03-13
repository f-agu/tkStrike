package com.xtremis.daedo.tkstrike.ui.controller.ringmanager;

import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeBaseController;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public abstract class BaseStepWizardController extends TkStrikeBaseController implements StepWizardController {
  private SimpleBooleanProperty submitForm = new SimpleBooleanProperty(this, "submitForm", Boolean.FALSE.booleanValue());
  
  protected void forceSubmitForm() {
    this.submitForm.set(Boolean.TRUE.booleanValue());
    this.submitForm.set(Boolean.FALSE.booleanValue());
  }
  
  public ReadOnlyBooleanProperty submitFormProperty() {
    return (ReadOnlyBooleanProperty)this.submitForm;
  }
}
