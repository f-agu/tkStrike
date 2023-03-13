package com.xtremis.daedo.tkstrike.ui.controller.ringmanager;

import com.xtremis.daedo.tkstrike.orm.model.SubCategory;
import com.xtremis.daedo.tkstrike.service.SubCategoryService;
import com.xtremis.daedo.tkstrike.service.TkStrikeService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeBaseTableWithDeleteManagementController;
import com.xtremis.daedo.tkstrike.ui.model.SubCategoryEntry;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class SubCategoriesManagementController extends TkStrikeBaseTableWithDeleteManagementController<SubCategory, SubCategoryEntry, SubCategoryService> {
  @FXML
  private Parent root;
  
  @FXML
  private Node rootView;
  
  @FXML
  private ProgressIndicator pi;
  
  @FXML
  private TableView<SubCategoryEntry> tbSubCategories;
  
  @FXML
  private TableColumn<SubCategoryEntry, String> tbSubCategoriesColId;
  
  @FXML
  private TableColumn<SubCategoryEntry, String> tbSubCategoriesColName;
  
  @FXML
  private TableColumn<SubCategoryEntry, Boolean> tbSubCategoriesColEditChilds;
  
  @FXML
  private TableColumn<SubCategoryEntry, Boolean> tbSubCategoriesColDelete;
  
  @FXML
  private Button btNew;
  
  @FXML
  private Button btOk;
  
  @FXML
  private Button btUndo;
  
  @FXML
  private TextField txtSubCategoryName;
  
  private ObservableList<SubCategoryEntry> subCategoryEntries = FXCollections.observableArrayList();
  
  @Autowired
  private SubCategoryService subCategoryService;
  
  @Autowired
  private CategoriesMainController categoriesMainController;
  
  public Node getRootView() {
    return this.rootView;
  }
  
  public void onWindowShowEvent() {
    refreshSubCategories();
  }
  
  public void initialize(URL url, ResourceBundle resourceBundle) {
    this.tbSubCategories.setItems(this.subCategoryEntries);
    this.btOk.setGraphic((Node)new ImageView(new Image(getClass().getResourceAsStream("/images/ic_ok.png"))));
    this.btOk.setDisable(true);
    this.btUndo.setGraphic((Node)new ImageView(new Image(getClass().getResourceAsStream("/images/ic_undo.png"))));
    this.btUndo.setDisable(true);
    this.txtSubCategoryName.setDisable(true);
    this.tbSubCategories.setEditable(true);
    this.tbSubCategoriesColId.setCellValueFactory((Callback)new PropertyValueFactory("id"));
    this.tbSubCategoriesColName.setCellValueFactory((Callback)new PropertyValueFactory("name"));
    this.tbSubCategoriesColName.setCellFactory(TextFieldTableCell.forTableColumn());
    this.tbSubCategoriesColName.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<SubCategoryEntry, String>>() {
          public void handle(TableColumn.CellEditEvent<SubCategoryEntry, String> t) {
            SubCategoryEntry subCategoryEntry = (SubCategoryEntry)t.getTableView().getItems().get(t.getTablePosition().getRow());
            try {
              SubCategoriesManagementController.this.subCategoryService.update(subCategoryEntry.getId(), (String)t.getNewValue());
            } catch (TkStrikeServiceException e) {
              SubCategoriesManagementController.this.manageException((Throwable)e, "SubCategories - update by name", null);
            } 
            SubCategoriesManagementController.this.refreshSubCategories();
          }
        });
    this.tbSubCategoriesColEditChilds.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SubCategoryEntry, Boolean>, ObservableValue<Boolean>>() {
          public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<SubCategoryEntry, Boolean> features) {
            return (ObservableValue<Boolean>)new SimpleBooleanProperty((features.getValue() != null));
          }
        });
    this.tbSubCategoriesColEditChilds.setCellFactory(new Callback<TableColumn<SubCategoryEntry, Boolean>, TableCell<SubCategoryEntry, Boolean>>() {
          public TableCell<SubCategoryEntry, Boolean> call(TableColumn<SubCategoryEntry, Boolean> personBooleanTableColumn) {
            return new TableCell<SubCategoryEntry, Boolean>() {
                final Button cellButton;
                
                protected void updateItem(Boolean t, boolean empty) {
                  super.updateItem(t, empty);
                  if (!empty)
                    setGraphic((Node)this.cellButton); 
                }
              };
          }
        });
    this.tbSubCategoriesColDelete.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SubCategoryEntry, Boolean>, ObservableValue<Boolean>>() {
          public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<SubCategoryEntry, Boolean> features) {
            return (ObservableValue<Boolean>)new SimpleBooleanProperty((features.getValue() != null));
          }
        });
    this.tbSubCategoriesColDelete.setCellFactory(new Callback<TableColumn<SubCategoryEntry, Boolean>, TableCell<SubCategoryEntry, Boolean>>() {
          public TableCell<SubCategoryEntry, Boolean> call(TableColumn<SubCategoryEntry, Boolean> personBooleanTableColumn) {
            return (TableCell<SubCategoryEntry, Boolean>)new TkStrikeBaseTableWithDeleteManagementController.DeleteEntryCell(SubCategoriesManagementController.this, SubCategoriesManagementController.this.tbSubCategories);
          }
        });
  }
  
  public void afterPropertiesSet() throws Exception {}
  
  public void refreshSubCategories() {
    refreshTable();
  }
  
  protected TkStrikeService<SubCategory, SubCategoryEntry> getTkStrikeService() {
    return (TkStrikeService<SubCategory, SubCategoryEntry>)this.subCategoryService;
  }
  
  protected ObservableList<SubCategoryEntry> getObservableList4Table() {
    return this.subCategoryEntries;
  }
  
  protected TableView<SubCategoryEntry> getTableView() {
    return this.tbSubCategories;
  }
  
  protected ProgressIndicator getProgressIndicator() {
    return this.pi;
  }
  
  public void newSubCategory() {
    this.txtSubCategoryName.clear();
    this.txtSubCategoryName.setDisable(false);
    this.btOk.setDisable(false);
    this.btUndo.setDisable(false);
  }
  
  public void saveSubCategory() {
    String newPhaseName = this.txtSubCategoryName.getText();
    if (StringUtils.isNotBlank(newPhaseName)) {
      try {
        this.subCategoryService.createNew(newPhaseName);
      } catch (TkStrikeServiceException e) {
        manageException((Throwable)e, "SubCategories - save", null);
      } 
      refreshSubCategories();
      this.txtSubCategoryName.clear();
      this.txtSubCategoryName.setDisable(true);
      this.btOk.setDisable(true);
      this.btUndo.setDisable(true);
    } 
  }
  
  public void undoSubCategory() {
    this.txtSubCategoryName.clear();
    this.txtSubCategoryName.setDisable(true);
    this.btOk.setDisable(true);
    this.btUndo.setDisable(true);
  }
}
