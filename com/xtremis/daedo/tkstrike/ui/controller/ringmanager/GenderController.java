package com.xtremis.daedo.tkstrike.ui.controller.ringmanager;

import com.xtremis.daedo.tkstrike.orm.model.Gender;
import com.xtremis.daedo.tkstrike.ui.model.Entry;
import com.xtremis.daedo.tkstrike.ui.model.FormValidationError;
import com.xtremis.daedo.tkstrike.ui.model.MatchConfigurationEntry;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

public class GenderController extends BaseStepWizardController implements StepWizardController {
  @FXML
  private Node rootView;
  
  @FXML
  private Label lblTitle;
  
  @FXML
  private GridPane gpElements;
  
  private MatchConfigurationEntry matchConfigurationEntry = null;
  
  private final ToggleGroup toggleGroup = new ToggleGroup();
  
  public Node getRootView() {
    return this.rootView;
  }
  
  public void clearForm() {}
  
  public LinkedHashSet<FormValidationError> validateForm() {
    LinkedHashSet<FormValidationError> res = null;
    Toggle selectedToggleButton = this.toggleGroup.getSelectedToggle();
    if (selectedToggleButton == null || selectedToggleButton.getUserData() == null) {
      res = new LinkedHashSet<>();
      res.add(new FormValidationError((Entry)this.matchConfigurationEntry, "gender", (Control)this.lblTitle, getMessage("validation.required")));
    } 
    return res;
  }
  
  protected Collection<Control> getFormControls() {
    return null;
  }
  
  public void onWindowShowEvent() {
    super.onWindowShowEvent();
    clearForm();
  }
  
  public void setMatchConfigurationEntry(MatchConfigurationEntry matchConfigurationEntry) {
    this.matchConfigurationEntry = matchConfigurationEntry;
    if (this.matchConfigurationEntry != null) {
      Gender current = null;
      if (matchConfigurationEntry.getGender() != null)
        current = matchConfigurationEntry.getGender(); 
      ToggleButton toggleButton1 = new ToggleButton(getMessage("enum.gender.MALE"));
      toggleButton1.getStyleClass().add("wz-selection-toggle");
      toggleButton1.setToggleGroup(this.toggleGroup);
      toggleButton1.setUserData(Gender.MALE);
      toggleButton1.setSelected(Gender.MALE.equals(current));
      toggleButton1.setCursor(Cursor.HAND);
      toggleButton1.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
              GenderController.this.forceSubmitForm();
            }
          });
      this.gpElements.add((Node)toggleButton1, 0, 0);
      ToggleButton toggleButton2 = new ToggleButton(getMessage("enum.gender.FEMALE"));
      toggleButton2.getStyleClass().add("wz-selection-toggle");
      toggleButton2.setToggleGroup(this.toggleGroup);
      toggleButton2.setUserData(Gender.FEMALE);
      toggleButton2.setSelected(Gender.FEMALE.equals(current));
      toggleButton2.setCursor(Cursor.HAND);
      toggleButton2.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
              GenderController.this.forceSubmitForm();
            }
          });
      this.gpElements.add((Node)toggleButton2, 1, 0);
    } 
  }
  
  public MatchConfigurationEntry getMatchConfigurationEntry() {
    Toggle selectedToggleButton = this.toggleGroup.getSelectedToggle();
    if (selectedToggleButton != null && selectedToggleButton.getUserData() != null) {
      Gender gender = (Gender)selectedToggleButton.getUserData();
      this.matchConfigurationEntry.genderProperty().set(gender);
    } 
    return this.matchConfigurationEntry;
  }
  
  public void initialize(URL url, ResourceBundle resourceBundle) {
    this.gpElements.setHgap(10.0D);
    this.gpElements.setVgap(10.0D);
    this.toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
          public void changed(ObservableValue<? extends Toggle> observable, final Toggle oldValue, Toggle newValue) {
            if (oldValue != null && newValue == null)
              Platform.runLater(new Runnable() {
                    public void run() {
                      GenderController.this.toggleGroup.selectToggle(oldValue);
                      GenderController.this.forceSubmitForm();
                    }
                  }); 
          }
        });
  }
  
  public void afterPropertiesSet() throws Exception {}
}
