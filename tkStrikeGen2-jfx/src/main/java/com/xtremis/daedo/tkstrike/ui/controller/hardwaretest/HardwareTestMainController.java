package com.xtremis.daedo.tkstrike.ui.controller.hardwaretest;

import com.xtremis.daedo.tkstrike.om.ExternalScreenViewId;
import com.xtremis.daedo.tkstrike.service.AppStatusWorker;
import com.xtremis.daedo.tkstrike.ui.scene.control.RestrictiveTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import org.springframework.stereotype.Component;

@Component
public class HardwareTestMainController extends BaseNewHardwareTestController {
  @FXML
  private RestrictiveTextField txtMinBodyHit;
  
  private SimpleIntegerProperty minBodyHit = new SimpleIntegerProperty(this, "minBodyHit", 0);
  
  @FXML
  private RestrictiveTextField txtMinHeadHit;
  
  private SimpleIntegerProperty minHeadHit = new SimpleIntegerProperty(this, "minHeadHit", 0);
  
  protected boolean isExternal() {
    return false;
  }
  
  protected void _onWindowShowEvent() {
    getAppStatusWorker().doChangeExternalScreenView(ExternalScreenViewId.HT_ATHLETES);
  }
  
  public void doClose() {
    doCloseThisStage();
  }
  
  public EventHandler<WindowEvent> getOnWindowCloseEventHandler() {
    return new EventHandler<WindowEvent>() {
        public void handle(WindowEvent windowEvent) {
          HardwareTestMainController.this.athletesHardwareTestWorker.stopTest();
          BaseNewHardwareTestController.opened = false;
          HardwareTestMainController.this.getAppStatusWorker().doChangeExternalScreenView(ExternalScreenViewId.SCOREBOARD);
        }
      };
  }
  
  public void initialize(URL url, ResourceBundle resourceBundle) {}
  
  protected void _afterPropertiesSet() throws Exception {
    this.txtMinBodyHit.setMaxLength(3);
    this.txtMinBodyHit.setRestrict("^0*(?:[1-9][0-9]?|100)$");
    this.txtMinBodyHit.setDefaultValue("5");
    this.txtMinHeadHit.setMaxLength(3);
    this.txtMinHeadHit.setRestrict("^0*(?:[1-9][0-9]?|100)$");
    this.txtMinHeadHit.setDefaultValue("5");
    this.txtMinBodyHit.textProperty().bindBidirectional((Property)this.minBodyHit, (StringConverter)new NumberStringConverter());
    this.txtMinHeadHit.textProperty().bindBidirectional((Property)this.minHeadHit, (StringConverter)new NumberStringConverter());
    this.minBodyHit.set(0);
    this.minHeadHit.set(0);
    this.minBodyHit.addListener(new ChangeListener<Number>() {
          public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
            HardwareTestMainController.this.athletesHardwareTestWorker.minBodyHitProperty().set(t1.intValue());
          }
        });
    this.minHeadHit.addListener(new ChangeListener<Number>() {
          public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
            HardwareTestMainController.this.athletesHardwareTestWorker.minHeadHitProperty().set(t1.intValue());
          }
        });
  }
}
