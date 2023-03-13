package com.xtremis.daedo.tkstrike.ui.controller.ringmanager;

import com.xtremis.daedo.tkstrike.service.CategoryService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.ui.model.CategoryEntry;
import com.xtremis.daedo.tkstrike.ui.model.Entry;
import com.xtremis.daedo.tkstrike.ui.model.FormValidationError;
import com.xtremis.daedo.tkstrike.ui.model.MatchConfigurationEntry;
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

public class CategoryController extends BaseStepWizardController implements StepWizardController {
  @FXML
  private Node rootView;
  
  @FXML
  private Label lblTitle;
  
  @FXML
  private Label lblSelectedMinBodyLevel;
  
  @FXML
  private Label lblSelectedMinHeadLevel;
  
  @FXML
  private GridPane gpElements;
  
  private MatchConfigurationEntry matchConfigurationEntry = null;
  
  @Autowired
  private CategoryService categoryService;
  
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
      res.add(new FormValidationError((Entry)this.matchConfigurationEntry, "category", (Control)this.lblTitle, getMessage("validation.required")));
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
      List<CategoryEntry> categoryEntries = null;
      try {
        categoryEntries = this.categoryService.findEntriesBySC_G(this.matchConfigurationEntry.getSubCategory().getId(), this.matchConfigurationEntry.getGender());
      } catch (TkStrikeServiceException e) {
        manageException((Throwable)e, "setMatchConfigurationEntry - findEntriesBySC_G", null);
      } 
      if (categoryEntries != null) {
        int col = 0;
        int row = 0;
        String current = null;
        if (matchConfigurationEntry.getCategory() != null && matchConfigurationEntry.getCategory().getId() != null)
          current = matchConfigurationEntry.getCategory().getId(); 
        for (CategoryEntry categoryEntry : categoryEntries) {
          ToggleButton toggleButton = new ToggleButton(categoryEntry.getName());
          toggleButton.getStyleClass().add("wz-selection-toggle");
          toggleButton.setToggleGroup(this.toggleGroup);
          toggleButton.setUserData(categoryEntry.getId());
          toggleButton.setSelected(categoryEntry.getId().equals(current));
          toggleButton.setCursor(Cursor.HAND);
          toggleButton.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent event) {
                  CategoryController.this.forceSubmitForm();
                }
              });
          this.gpElements.add((Node)toggleButton, col, row);
          col++;
          if (col == 3) {
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
      CategoryEntry categoryEntry = null;
      try {
        categoryEntry = (CategoryEntry)this.categoryService.getEntryById((String)selectedToggleButton.getUserData());
      } catch (TkStrikeServiceException e) {
        manageException((Throwable)e, "getMatchConfigurationEtnry - getEntryById", null);
      } 
      if (categoryEntry != null)
        this.matchConfigurationEntry.categoryProperty().set(categoryEntry); 
    } 
    return this.matchConfigurationEntry;
  }
  
  public void initialize(URL url, ResourceBundle resourceBundle) {
    this.gpElements.setHgap(10.0D);
    this.gpElements.setVgap(10.0D);
    this.toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
          public void changed(ObservableValue<? extends Toggle> observableValue, Toggle toggle, Toggle selected) {
            if (selected != null && selected.getUserData() != null && selected.getUserData() instanceof String) {
              String selectedCategoryId = (String)selected.getUserData();
              CategoryEntry categoryEntry = null;
              try {
                categoryEntry = (CategoryEntry)CategoryController.this.categoryService.getEntryById(selectedCategoryId);
              } catch (TkStrikeServiceException e) {
                CategoryController.this.manageException((Throwable)e, "ToogleChange getEntryById", null);
              } 
              if (categoryEntry != null) {
                CategoryController.this.lblSelectedMinBodyLevel.setText(CategoryController.this.getMessage("message.selectedMinBodyLevel", new String[] { "" + categoryEntry.getBodyLevel() }));
                CategoryController.this.lblSelectedMinHeadLevel.setText(CategoryController.this.getMessage("message.selectedMinHeadLevel", new String[] { "" + categoryEntry.getHeadLevel() }));
              } 
            } 
          }
        });
    this.toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
          public void changed(ObservableValue<? extends Toggle> observable, final Toggle oldValue, Toggle newValue) {
            if (oldValue != null && newValue == null)
              Platform.runLater(new Runnable() {
                    public void run() {
                      CategoryController.this.toggleGroup.selectToggle(oldValue);
                      CategoryController.this.forceSubmitForm();
                    }
                  }); 
          }
        });
  }
  
  public void afterPropertiesSet() throws Exception {}
}
