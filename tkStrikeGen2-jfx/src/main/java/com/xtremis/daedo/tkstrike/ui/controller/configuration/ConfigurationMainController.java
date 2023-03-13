package com.xtremis.daedo.tkstrike.ui.controller.configuration;

import com.xtremis.daedo.tkstrike.ui.model.NetworkConfigurationEntry;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfigurationMainController extends BaseConfigurationMainController<NetworkConfigurationEntry> {
  @FXML
  private TabPane tabPane;
  
  @Autowired
  private ConfigurationNetworkController configurationNetworkController;
  
  @Autowired
  private ConfigurationRulesController configurationRulesController;
  
  @Autowired
  private ConfigurationExternalConfigController configurationExternalConfigController;
  
  @Autowired
  private ConfigurationSoundsController configurationSoundsController;
  
  @Autowired
  private ConfigurationMatchLogController configurationMatchLogController;
  
  @Autowired
  private ConfigurationSoftwareUpdateController configurationSoftwareUpdateController;
  
  public void doCloseConfig() {
    if ("tabExternal".equals(((Tab)this.tabPane.getSelectionModel().getSelectedItem()).getId())) {
      if (this.configurationExternalConfigController.reallyCanClose())
        doCloseThisStage(); 
    } else {
      doCloseThisStage();
    } 
  }
  
  public void onWindowShowEvent() {
    super.onWindowShowEvent();
    this.tabPane.getSelectionModel().selectFirst();
    ((Tab)this.tabPane.getSelectionModel().getSelectedItem()).setContent(this.configurationNetworkController.getRootView());
    this.configurationNetworkController.onWindowShowEvent();
  }
  
  public EventHandler<WindowEvent> getOnWindowCloseEventHandler() {
    return new EventHandler<WindowEvent>() {
        public void handle(WindowEvent windowEvent) {
          ConfigurationMainController.this.configurationNetworkController.getOnWindowCloseEventHandler().handle((Event)windowEvent);
        }
      };
  }
  
  public void initialize(URL url, ResourceBundle resourceBundle) {
    this.tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
          public void changed(ObservableValue<? extends Tab> observableValue, Tab tab, Tab t1) {
            if ("tabExternal".equals(tab.getId()) && !ConfigurationMainController.this.configurationExternalConfigController.reallyCanClose()) {
              ConfigurationMainController.this.tabPane.getSelectionModel().select(tab);
            } else if ("tabNetwork".equals(t1.getId())) {
              t1.setContent(ConfigurationMainController.this.configurationNetworkController.getRootView());
              ConfigurationMainController.this.configurationNetworkController.onWindowShowEvent();
            } else if ("tabRules".equals(t1.getId())) {
              t1.setContent(ConfigurationMainController.this.configurationRulesController.getRootView());
              ConfigurationMainController.this.configurationRulesController.onWindowShowEvent();
            } else if ("tabExternal".equals(t1.getId())) {
              t1.setContent(ConfigurationMainController.this.configurationExternalConfigController.getRootView());
              ConfigurationMainController.this.configurationExternalConfigController.onWindowShowEvent();
            } else if ("tabSound".equals(t1.getId())) {
              t1.setContent(ConfigurationMainController.this.configurationSoundsController.getRootView());
              ConfigurationMainController.this.configurationSoundsController.onWindowShowEvent();
            } else if ("tabMatchLog".equals(t1.getId())) {
              t1.setContent(ConfigurationMainController.this.configurationMatchLogController.getRootView());
              ConfigurationMainController.this.configurationMatchLogController.onWindowShowEvent();
            } else if ("tabSoftwareUpdates".equals(t1.getId())) {
              t1.setContent(ConfigurationMainController.this.configurationSoftwareUpdateController.getRootView());
              ConfigurationMainController.this.configurationSoftwareUpdateController.onWindowShowEvent();
            } 
          }
        });
  }
  
  protected void _afterPropertiesSet() throws Exception {}
}
