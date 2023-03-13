package com.xtremis.daedo.tkstrike.ui.controller;

import com.xtremis.daedo.tkstrike.om.MatchStatusId;
import com.xtremis.daedo.tkstrike.service.CommonMatchWorker;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoundCountdownController extends CommonTkStrikeBaseController {
  @FXML
  private Text roundCountdown;
  
  @FXML
  private Rectangle recRoundCountdown;
  
  @Autowired
  private CommonMatchWorker matchWorker;
  
  public void initialize(URL location, ResourceBundle resources) {}
  
  public void forceChangeCountdown(final String newCountDown) {
    if (StringUtils.isNotBlank(newCountDown))
      Platform.runLater(new Runnable() {
            public void run() {
              RoundCountdownController.this.roundCountdown.setText(newCountDown);
            }
          }); 
  }
  
  public void afterPropertiesSet() throws Exception {
    this.matchWorker.currentMatchStatusProperty().addListener(new ChangeListener<MatchStatusId>() {
          public void changed(ObservableValue<? extends MatchStatusId> observableValue, MatchStatusId prevMatchStatusId, final MatchStatusId newMatchStatus) {
            Platform.runLater(new Runnable() {
                  public void run() {
                    try {
                      if (MatchStatusId.ROUND_KYESHI.equals(newMatchStatus)) {
                        RoundCountdownController.this.recRoundCountdown.getStyleClass().remove("pn-newCountdown");
                        RoundCountdownController.this.recRoundCountdown.getStyleClass().add("pn-countdownInKyeShi");
                        RoundCountdownController.this.roundCountdown.getStyleClass().remove("roundCountdown");
                        RoundCountdownController.this.roundCountdown.getStyleClass().add("roundCountdownInKyeShi");
                      } else {
                        RoundCountdownController.this.recRoundCountdown.getStyleClass().add("pn-newCountdown");
                        RoundCountdownController.this.recRoundCountdown.getStyleClass().remove("pn-countdownInKyeShi");
                        RoundCountdownController.this.roundCountdown.getStyleClass().add("roundCountdown");
                        RoundCountdownController.this.roundCountdown.getStyleClass().remove("roundCountdownInKyeShi");
                      } 
                    } catch (RuntimeException e) {
                      e.printStackTrace();
                    } 
                  }
                });
          }
        });
    this.matchWorker.setTextCountdown(this.roundCountdown);
  }
}
