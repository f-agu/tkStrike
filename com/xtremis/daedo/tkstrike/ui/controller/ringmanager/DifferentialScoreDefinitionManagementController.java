package com.xtremis.daedo.tkstrike.ui.controller.ringmanager;

import com.xtremis.daedo.tkstrike.orm.model.DifferentialScoreDefinition;
import com.xtremis.daedo.tkstrike.service.DifferentialScoreDefinitionService;
import com.xtremis.daedo.tkstrike.service.PhaseService;
import com.xtremis.daedo.tkstrike.service.SubCategoryService;
import com.xtremis.daedo.tkstrike.service.TkStrikeService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeBaseTableWithDeleteManagementController;
import com.xtremis.daedo.tkstrike.ui.model.DifferentialScoreDefinitionEntry;
import com.xtremis.daedo.tkstrike.ui.model.PhaseEntry;
import com.xtremis.daedo.tkstrike.ui.model.SubCategoryEntry;
import com.xtremis.daedo.tkstrike.ui.scene.control.RestrictiveTextField;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;

public class DifferentialScoreDefinitionManagementController extends TkStrikeBaseTableWithDeleteManagementController<DifferentialScoreDefinition, DifferentialScoreDefinitionEntry, DifferentialScoreDefinitionService> {
  @FXML
  private Parent root;
  
  @FXML
  private Node rootView;
  
  @FXML
  private TableView<DifferentialScoreDefinitionEntry> tbDifferentialScoreDefs;
  
  private ObservableList<DifferentialScoreDefinitionEntry> differentialScoreDefinitionEntries = FXCollections.observableArrayList();
  
  @FXML
  private TableColumn<DifferentialScoreDefinitionEntry, String> tbDifferentialScoreDefsColId;
  
  @FXML
  private TableColumn<String, PhaseEntry> tbDifferentialScoreDefsColPhase;
  
  @FXML
  private TableColumn<String, SubCategoryEntry> tbDifferentialScoreDefsColSubCategory;
  
  @FXML
  private TableColumn<DifferentialScoreDefinitionEntry, Integer> tbDifferentialScoreDefsColValue;
  
  @FXML
  private TableColumn<DifferentialScoreDefinitionEntry, Boolean> tbDifferentialScoreDefsColDelete;
  
  @FXML
  private Button btNew;
  
  @FXML
  private Button btOk;
  
  @FXML
  private Button btUndo;
  
  @FXML
  private ProgressIndicator pi;
  
  @FXML
  private ComboBox<PhaseEntry> cmbPhase;
  
  private ObservableList<PhaseEntry> phaseEntries = FXCollections.observableArrayList();
  
  @FXML
  private ComboBox<SubCategoryEntry> cmbSubCategory;
  
  private ObservableList<SubCategoryEntry> subCategoryEntries = FXCollections.observableArrayList();
  
  @FXML
  private RestrictiveTextField txtValue;
  
  @Autowired
  private DifferentialScoreDefinitionService differentialScoreDefinitionService;
  
  @Autowired
  private PhaseService phaseService;
  
  @Autowired
  private SubCategoryService subCategoryService;
  
  public Node getRootView() {
    return this.rootView;
  }
  
  public void onWindowShowEvent() {
    super.onWindowShowEvent();
    clearForm();
    refreshRelateds();
  }
  
  public void initialize(URL url, ResourceBundle resourceBundle) {
    this.btOk.setDisable(true);
    this.btUndo.setDisable(true);
    this.tbDifferentialScoreDefs.setItems(this.differentialScoreDefinitionEntries);
    this.cmbPhase.setDisable(true);
    this.cmbPhase.setItems(this.phaseEntries);
    this.cmbSubCategory.setDisable(true);
    this.cmbSubCategory.setItems(this.subCategoryEntries);
    this.txtValue.setDisable(true);
    this.txtValue.setMaxLength(3);
    this.txtValue.setRestrict("^0*(?:[1-9][0-9]?|100)$");
    this.txtValue.setDefaultValue("1");
    this.tbDifferentialScoreDefs.setEditable(true);
    this.tbDifferentialScoreDefsColId.setCellValueFactory((Callback)new PropertyValueFactory("id"));
    this.tbDifferentialScoreDefsColPhase.setCellValueFactory((Callback)new PropertyValueFactory("phase"));
    this.tbDifferentialScoreDefsColSubCategory.setCellValueFactory((Callback)new PropertyValueFactory("subCategory"));
    this.tbDifferentialScoreDefsColValue.setCellValueFactory((Callback)new PropertyValueFactory("value"));
    this.tbDifferentialScoreDefsColValue.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Integer>() {
            public String toString(Integer integer) {
              return "" + integer;
            }
            
            public Integer fromString(String s) {
              return Integer.valueOf(Integer.parseInt(s));
            }
          }));
    this.tbDifferentialScoreDefsColValue.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<DifferentialScoreDefinitionEntry, Integer>>() {
          public void handle(TableColumn.CellEditEvent<DifferentialScoreDefinitionEntry, Integer> t) {
            DifferentialScoreDefinitionManagementController.this.showTableProgressIndicator(true);
            DifferentialScoreDefinitionEntry differentialScoreDefinitionEntry = (DifferentialScoreDefinitionEntry)DifferentialScoreDefinitionManagementController.this.tbDifferentialScoreDefs.getItems().get(t.getTablePosition().getRow());
            differentialScoreDefinitionEntry.setValue(((Integer)t.getNewValue()).intValue());
            try {
              DifferentialScoreDefinitionManagementController.this.differentialScoreDefinitionService.updateEntry(differentialScoreDefinitionEntry);
            } catch (TkStrikeServiceException e) {
              DifferentialScoreDefinitionManagementController.this.manageException((Throwable)e, "DifferentialScoreDefinition - changeValue", e.getMessage());
            } 
            DifferentialScoreDefinitionManagementController.this.refreshTable();
          }
        });
    this.tbDifferentialScoreDefsColDelete.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DifferentialScoreDefinitionEntry, Boolean>, ObservableValue<Boolean>>() {
          public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<DifferentialScoreDefinitionEntry, Boolean> features) {
            return (ObservableValue<Boolean>)new SimpleBooleanProperty((features.getValue() != null));
          }
        });
    this.tbDifferentialScoreDefsColDelete.setCellFactory(new Callback<TableColumn<DifferentialScoreDefinitionEntry, Boolean>, TableCell<DifferentialScoreDefinitionEntry, Boolean>>() {
          public TableCell<DifferentialScoreDefinitionEntry, Boolean> call(TableColumn<DifferentialScoreDefinitionEntry, Boolean> personBooleanTableColumn) {
            return (TableCell<DifferentialScoreDefinitionEntry, Boolean>)new TkStrikeBaseTableWithDeleteManagementController.DeleteEntryCell(DifferentialScoreDefinitionManagementController.this, DifferentialScoreDefinitionManagementController.this.tbDifferentialScoreDefs);
          }
        });
  }
  
  public void afterPropertiesSet() throws Exception {}
  
  protected TkStrikeService<DifferentialScoreDefinition, DifferentialScoreDefinitionEntry> getTkStrikeService() {
    return (TkStrikeService<DifferentialScoreDefinition, DifferentialScoreDefinitionEntry>)this.differentialScoreDefinitionService;
  }
  
  protected ObservableList<DifferentialScoreDefinitionEntry> getObservableList4Table() {
    return this.differentialScoreDefinitionEntries;
  }
  
  protected TableView<DifferentialScoreDefinitionEntry> getTableView() {
    return this.tbDifferentialScoreDefs;
  }
  
  protected ProgressIndicator getProgressIndicator() {
    return this.pi;
  }
  
  public void refreshRelateds() {
    showTableProgressIndicator(true);
    refreshTable();
    Platform.runLater(new Runnable() {
          public void run() {
            DifferentialScoreDefinitionManagementController.this.phaseEntries.clear();
            DifferentialScoreDefinitionManagementController.this.subCategoryEntries.clear();
            try {
              DifferentialScoreDefinitionManagementController.this.phaseEntries.addAll(DifferentialScoreDefinitionManagementController.this.phaseService.findAllEntries());
            } catch (TkStrikeServiceException e) {
              DifferentialScoreDefinitionManagementController.this.manageException((Throwable)e, "", null);
            } 
            try {
              DifferentialScoreDefinitionManagementController.this.subCategoryEntries.addAll(DifferentialScoreDefinitionManagementController.this.subCategoryService.findAllEntries());
            } catch (TkStrikeServiceException e) {
              e.printStackTrace();
            } 
            DifferentialScoreDefinitionManagementController.this.showTableProgressIndicator(false);
          }
        });
  }
  
  public void doNew() {
    this.txtValue.clear();
    this.cmbPhase.setDisable(false);
    this.cmbSubCategory.setDisable(false);
    this.txtValue.setDisable(false);
    this.btOk.setDisable(false);
    this.btUndo.setDisable(false);
  }
  
  public void doSave() {
    if (this.cmbPhase.getValue() != null && this.cmbSubCategory != null && this.txtValue.getText() != null) {
      showTableProgressIndicator(true);
      TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {
            public Void call() throws Exception {
              try {
                DifferentialScoreDefinitionManagementController.this.differentialScoreDefinitionService.createNew(((PhaseEntry)DifferentialScoreDefinitionManagementController.this.cmbPhase.getValue()).getId(), ((SubCategoryEntry)DifferentialScoreDefinitionManagementController.this.cmbSubCategory.getValue()).getId(), Integer.valueOf(Integer.parseInt(DifferentialScoreDefinitionManagementController.this.txtValue.getText())));
              } catch (TkStrikeServiceException e) {
                DifferentialScoreDefinitionManagementController.this.manageException((Throwable)e, "DifferentialScoreDefinition - createNew", e.getMessage());
              } 
              DifferentialScoreDefinitionManagementController.this.refreshTable();
              return null;
            }
          });
    } 
  }
  
  public void doUndo() {
    clearForm();
  }
  
  private void clearForm() {
    Platform.runLater(new Runnable() {
          public void run() {
            DifferentialScoreDefinitionManagementController.this.cmbPhase.getSelectionModel().clearSelection();
            DifferentialScoreDefinitionManagementController.this.cmbSubCategory.getSelectionModel().clearSelection();
            DifferentialScoreDefinitionManagementController.this.cmbPhase.setDisable(true);
            DifferentialScoreDefinitionManagementController.this.cmbSubCategory.setDisable(true);
            DifferentialScoreDefinitionManagementController.this.txtValue.setDisable(true);
            DifferentialScoreDefinitionManagementController.this.txtValue.clear();
            DifferentialScoreDefinitionManagementController.this.btOk.setDisable(true);
            DifferentialScoreDefinitionManagementController.this.btUndo.setDisable(true);
          }
        });
  }
}
