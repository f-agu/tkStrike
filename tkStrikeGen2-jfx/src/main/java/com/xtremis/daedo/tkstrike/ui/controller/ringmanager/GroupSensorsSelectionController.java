package com.xtremis.daedo.tkstrike.ui.controller.ringmanager;

import com.xtremis.daedo.tkstrike.orm.model.SensorsGroup;
import com.xtremis.daedo.tkstrike.ui.model.FormValidationError;
import com.xtremis.daedo.tkstrike.ui.model.MatchConfigurationEntry;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class GroupSensorsSelectionController extends BaseStepWizardController implements StepWizardController {
  @FXML
  private Node rootView;
  
  @FXML
  private Label lblLastGroupInfo;
  
  @FXML
  private RadioButton opGroup1;
  
  @FXML
  private RadioButton opGroup2;
  
  private MatchConfigurationEntry matchConfigurationEntry;
  
  private final ToggleGroup toggleGroup = new ToggleGroup();
  
  public Node getRootView() {
    return this.rootView;
  }
  
  public void clearForm() {}
  
  public LinkedHashSet<FormValidationError> validateForm() {
    LinkedHashSet<FormValidationError> res = null;
    return res;
  }
  
  public void initialize(URL url, ResourceBundle resourceBundle) {
    this.opGroup1.setToggleGroup(this.toggleGroup);
    this.opGroup1.setUserData(SensorsGroup.GROUP1);
    this.opGroup2.setToggleGroup(this.toggleGroup);
    this.opGroup2.setUserData(SensorsGroup.GROUP2);
  }
  
  public void afterPropertiesSet() throws Exception {}
  
  public void setMatchConfigurationEntry(MatchConfigurationEntry matchConfigurationEntry) {
    this.matchConfigurationEntry = matchConfigurationEntry;
  }
  
  public MatchConfigurationEntry getMatchConfigurationEntry() {
    this.matchConfigurationEntry.sensorsGroupProperty().set(((SensorsGroup)this.toggleGroup.getSelectedToggle().getUserData()).toString());
    return this.matchConfigurationEntry;
  }
}
