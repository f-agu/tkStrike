package com.xtremis.daedo.tkstrike.ui.controller.ringmanager;

import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeController;
import com.xtremis.daedo.tkstrike.ui.model.MatchConfigurationEntry;
import javafx.beans.property.ReadOnlyBooleanProperty;

public interface StepWizardController extends TkStrikeController {
  void setMatchConfigurationEntry(MatchConfigurationEntry paramMatchConfigurationEntry);
  
  MatchConfigurationEntry getMatchConfigurationEntry();
  
  void clearForm();
  
  ReadOnlyBooleanProperty submitFormProperty();
}
