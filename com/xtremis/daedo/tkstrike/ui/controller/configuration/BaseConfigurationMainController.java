package com.xtremis.daedo.tkstrike.ui.controller.configuration;

import com.xtremis.daedo.tkstrike.configuration.TkStrikeScoreboardGraphicDetailType;
import com.xtremis.daedo.tkstrike.configuration.TkStrikeScoreboardGraphicDetailTypeUtil;
import com.xtremis.daedo.tkstrike.service.AppStatusWorker;
import com.xtremis.daedo.tkstrike.ui.TkStrikeKeyCombinationsHelper;
import com.xtremis.daedo.tkstrike.ui.controller.CommonTkStrikeBaseController;
import com.xtremis.daedo.tkstrike.ui.model.INetworkConfigurationEntry;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import org.springframework.beans.factory.annotation.Value;

public abstract class BaseConfigurationMainController<NE extends INetworkConfigurationEntry> extends CommonTkStrikeBaseController<NE> {
  @Value("${tkStrike.scoreboard.graphicDetailType}")
  protected TkStrikeScoreboardGraphicDetailType scoreboardGraphicDetailType;
  
  @FXML
  protected ToggleButton tgGraphicDetail;
  
  public final void afterPropertiesSet() throws Exception {
    this.rootView.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
          public void handle(KeyEvent event) {
            if (TkStrikeKeyCombinationsHelper.keyCombGRAPHIC_DETAIL.match(event))
              Platform.runLater(new Runnable() {
                    public void run() {
                      BaseConfigurationMainController.this.tgGraphicDetail.setVisible(!BaseConfigurationMainController.this.tgGraphicDetail.isVisible());
                    }
                  }); 
          }
        });
    this.tgGraphicDetail.selectedProperty().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            BaseConfigurationMainController.this.tgGraphicDetail.setText(BaseConfigurationMainController.this.getMessage("toggle.scoreboardGraphicDetailType." + (newValue.booleanValue() ? "HIGH_GRAPHIC_DETAIL" : "LOW_GRAPHIC_DETAIL")));
          }
        });
    this.tgGraphicDetail.setSelected(TkStrikeScoreboardGraphicDetailType.HIGH_GRAPHIC_DETAIL.equals(this.scoreboardGraphicDetailType));
    this.tgGraphicDetail.setOnAction(new EventHandler<ActionEvent>() {
          public void handle(ActionEvent event) {
            if (((ButtonType)BaseConfigurationMainController.this.showConfirmDialog(BaseConfigurationMainController.this.getMessage("title.default.question"), BaseConfigurationMainController.this
                .getMessage("message.tkStrike.communicationType.changes")).get()).equals(ButtonType.OK)) {
              TkStrikeScoreboardGraphicDetailType newValue = TkStrikeScoreboardGraphicDetailType.HIGH_GRAPHIC_DETAIL;
              if (!BaseConfigurationMainController.this.tgGraphicDetail.isSelected())
                newValue = TkStrikeScoreboardGraphicDetailType.LOW_GRAPHIC_DETAIL; 
              try {
                TkStrikeScoreboardGraphicDetailTypeUtil.getInstance().changeScoreboardGraphicDetailType(newValue);
              } catch (Exception e) {
                BaseConfigurationMainController.this.manageException(e, "ChangeScoreboardGraphicDetailType", null);
              } 
              BaseConfigurationMainController.this.getAppStatusWorker().doForceExitTkStrike();
            } else {
              BaseConfigurationMainController.this.tgGraphicDetail.setSelected(!BaseConfigurationMainController.this.tgGraphicDetail.isSelected());
            } 
          }
        });
    _afterPropertiesSet();
  }
  
  protected abstract void _afterPropertiesSet() throws Exception;
}
