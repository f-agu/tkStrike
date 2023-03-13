package com.xtremis.daedo.tkstrike.ui.controller.ringmanager;

import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeBaseController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoriesMainControllerImpl extends TkStrikeBaseController implements CategoriesMainController {
  @FXML
  private Node rootView;
  
  @FXML
  private TabPane tabPane;
  
  @Autowired
  private SubCategoriesManagementController subCategoriesManagementController;
  
  @Autowired
  private CategoriesManagementController categoriesManagementController;
  
  public Node getRootView() {
    return this.rootView;
  }
  
  public void onWindowShowEvent() {
    this.subCategoriesManagementController.onWindowShowEvent();
    this.categoriesManagementController.onWindowShowEvent();
  }
  
  public void initialize(URL url, ResourceBundle resourceBundle) {
    this.tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
          public void changed(ObservableValue<? extends Tab> observableValue, Tab tab, Tab t1) {
            if ("tabSubCategories".equals(t1.getId())) {
              CategoriesMainControllerImpl.this.categoriesManagementController.filterCategoriesBySubCategoryId(null);
              CategoriesMainControllerImpl.this.subCategoriesManagementController.onWindowShowEvent();
              t1.setContent(CategoriesMainControllerImpl.this.subCategoriesManagementController.getRootView());
            } else if ("tabCategories".equals(t1.getId())) {
              CategoriesMainControllerImpl.this.categoriesManagementController.onWindowShowEvent();
              t1.setContent(CategoriesMainControllerImpl.this.categoriesManagementController.getRootView());
            } 
          }
        });
  }
  
  public void showCategoriesBySubCategoryId(final String subCategoryId) {
    for (Tab tab : this.tabPane.getTabs()) {
      if ("tabCategories".equals(tab.getId()))
        Platform.runLater(new Runnable() {
              public void run() {
                CategoriesMainControllerImpl.this.categoriesManagementController.filterCategoriesBySubCategoryId(subCategoryId);
                CategoriesMainControllerImpl.this.tabPane.getSelectionModel().selectLast();
              }
            }); 
    } 
  }
  
  public void afterPropertiesSet() throws Exception {
    for (Tab tab : this.tabPane.getTabs()) {
      if ("tabSubCategories".equals(tab.getId()))
        tab.setContent(this.subCategoriesManagementController.getRootView()); 
    } 
  }
}
