package com.xtremis.daedo.tkstrike.ui.controller.ringmanager;

import com.xtremis.daedo.tkstrike.service.SubCategoryService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.ui.model.Entry;
import com.xtremis.daedo.tkstrike.ui.model.FormValidationError;
import com.xtremis.daedo.tkstrike.ui.model.MatchConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.SubCategoryEntry;
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

public class SubCategoryController extends BaseStepWizardController implements StepWizardController {
  @FXML
  private Node rootView;
  
  @FXML
  private Label lblTitle;
  
  @FXML
  private GridPane gpElements;
  
  private MatchConfigurationEntry matchConfigurationEntry = null;
  
  @Autowired
  private SubCategoryService subCategoryService;
  
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
      res.add(new FormValidationError((Entry)this.matchConfigurationEntry, "subCategory", (Control)this.lblTitle, getMessage("validation.required")));
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
    this.gpElements.getChildren().clear();
    if (this.matchConfigurationEntry != null) {
      List<SubCategoryEntry> subCategoryEntries = null;
      try {
        subCategoryEntries = this.subCategoryService.findAllEntries();
      } catch (TkStrikeServiceException e) {
        manageException((Throwable)e, "SubCategories - finAllEntries ", null);
      } 
      if (subCategoryEntries != null) {
        int col = 0;
        int row = 0;
        String current = null;
        if (matchConfigurationEntry.getSubCategory() != null && matchConfigurationEntry.getSubCategory().getId() != null)
          current = matchConfigurationEntry.getSubCategory().getId(); 
        for (SubCategoryEntry subCategoryEntry : subCategoryEntries) {
          ToggleButton toggleButton = new ToggleButton(subCategoryEntry.getName());
          toggleButton.getStyleClass().add("wz-selection-toggle");
          toggleButton.setToggleGroup(this.toggleGroup);
          toggleButton.setUserData(subCategoryEntry.getId());
          toggleButton.setSelected(subCategoryEntry.getId().equals(current));
          toggleButton.setCursor(Cursor.HAND);
          toggleButton.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent event) {
                  SubCategoryController.this.forceSubmitForm();
                }
              });
          this.gpElements.add((Node)toggleButton, col, row);
          col++;
          if (col == 2) {
            col = 0;
            row++;
          } 
        } 
      } 
    } 
  }
  
  public MatchConfigurationEntry getMatchConfigurationEntry() {
    Toggle selectedToggleButton = this.toggleGroup.getSelectedToggle();
    if (selectedToggleButton != null && selectedToggleButton.getUserData() != null) {
      SubCategoryEntry subCategoryEntry = null;
      try {
        subCategoryEntry = (SubCategoryEntry)this.subCategoryService.getEntryById((String)selectedToggleButton.getUserData());
      } catch (TkStrikeServiceException e) {
        e.printStackTrace();
      } 
      if (subCategoryEntry != null)
        this.matchConfigurationEntry.subCategoryProperty().set(subCategoryEntry); 
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
                      SubCategoryController.this.toggleGroup.selectToggle(oldValue);
                      SubCategoryController.this.forceSubmitForm();
                    }
                  }); 
          }
        });
  }
  
  public void afterPropertiesSet() throws Exception {}
}
