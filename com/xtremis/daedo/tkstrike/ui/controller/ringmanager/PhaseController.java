package com.xtremis.daedo.tkstrike.ui.controller.ringmanager;

import com.xtremis.daedo.tkstrike.service.PhaseService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.ui.model.Entry;
import com.xtremis.daedo.tkstrike.ui.model.FormValidationError;
import com.xtremis.daedo.tkstrike.ui.model.MatchConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.PhaseEntry;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
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
import org.springframework.beans.factory.annotation.Autowired;

public class PhaseController extends BaseStepWizardController implements StepWizardController {
  @FXML
  private Node rootView;
  
  @FXML
  private Label lblTitle;
  
  @FXML
  private GridPane gpElements;
  
  private MatchConfigurationEntry matchConfigurationEntry = null;
  
  @Autowired
  private PhaseService phaseService;
  
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
      res.add(new FormValidationError((Entry)this.matchConfigurationEntry, "phase", (Control)this.lblTitle, getMessage("validation.required")));
    } 
    return res;
  }
  
  protected Collection<Control> getFormControls() {
    return null;
  }
  
  public void onWindowShowEvent() {
    super.onWindowShowEvent();
    clearForm();
    _bindControls();
  }
  
  public void setMatchConfigurationEntry(MatchConfigurationEntry matchConfigurationEntry) {
    this.matchConfigurationEntry = matchConfigurationEntry;
    this.gpElements.getChildren().clear();
    if (this.matchConfigurationEntry != null) {
      List<PhaseEntry> phaseEntries = null;
      try {
        phaseEntries = this.phaseService.findAllEntries();
      } catch (TkStrikeServiceException e) {
        manageException((Throwable)e, "Phases findAll", null);
      } 
      if (phaseEntries != null) {
        int col = 0;
        int row = 0;
        String currPhase = null;
        if (matchConfigurationEntry.getPhase() != null && matchConfigurationEntry.getPhase().getId() != null)
          currPhase = matchConfigurationEntry.getPhase().getId(); 
        BigDecimal bigDecimal = BigDecimal.valueOf(phaseEntries.size());
        int rows = bigDecimal.divide(BigDecimal.valueOf(3L), 0).intValue();
        for (PhaseEntry phaseEntry : phaseEntries) {
          ToggleButton toggleButton = new ToggleButton(phaseEntry.getName());
          toggleButton.getStyleClass().add("wz-selection-toggle");
          toggleButton.setToggleGroup(this.toggleGroup);
          toggleButton.setUserData(phaseEntry.getId());
          toggleButton.setSelected(phaseEntry.getId().equals(currPhase));
          toggleButton.setCursor(Cursor.HAND);
          toggleButton.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent event) {
                  PhaseController.this.forceSubmitForm();
                }
              });
          this.gpElements.add((Node)toggleButton, col, row);
          row++;
          if (row == rows) {
            row = 0;
            col++;
          } 
        } 
      } 
    } 
  }
  
  protected void _bindControls() {
    if (this.matchConfigurationEntry != null);
  }
  
  public MatchConfigurationEntry getMatchConfigurationEntry() {
    Toggle selectedToggleButton = this.toggleGroup.getSelectedToggle();
    if (selectedToggleButton != null && selectedToggleButton.getUserData() != null) {
      PhaseEntry phaseEntry = null;
      try {
        phaseEntry = (PhaseEntry)this.phaseService.getEntryById((String)selectedToggleButton.getUserData());
      } catch (TkStrikeServiceException e) {
        e.printStackTrace();
      } 
      if (phaseEntry != null)
        this.matchConfigurationEntry.phaseProperty().set(phaseEntry); 
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
                      PhaseController.this.toggleGroup.selectToggle(oldValue);
                      PhaseController.this.forceSubmitForm();
                    }
                  }); 
          }
        });
  }
  
  public void afterPropertiesSet() throws Exception {}
}
