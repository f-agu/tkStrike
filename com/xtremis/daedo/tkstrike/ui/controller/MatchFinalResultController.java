package com.xtremis.daedo.tkstrike.ui.controller;

import com.xtremis.daedo.tkstrike.service.CommonMatchWorker;
import com.xtremis.daedo.tkstrike.service.MatchWorker;
import com.xtremis.daedo.tkstrike.ui.model.NetworkConfigurationEntry;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MatchFinalResultController extends CommonMatchFinalResultController<NetworkConfigurationEntry, MatchWorker, ScoreboardEditorControllerImpl, MatchLogViewerController> {
  @FXML
  private StackPane pnBlueVideo;
  
  @FXML
  private ImageView imgBlueVideo;
  
  @FXML
  private StackPane pnRedVideo;
  
  @FXML
  private ImageView imgRedVideo;
  
  @Autowired
  private MatchWorker matchWorker;
  
  @Autowired
  private ScoreboardEditorControllerImpl scoreboardEditorController;
  
  @Autowired
  private MatchLogViewerController matchLogViewerController;
  
  MatchWorker getMatchWorker() {
    return this.matchWorker;
  }
  
  ScoreboardEditorControllerImpl getScoreboardEditorController() {
    return this.scoreboardEditorController;
  }
  
  MatchLogViewerController getMatchLogViewerController() {
    return this.matchLogViewerController;
  }
  
  public void initialize(URL location, ResourceBundle resources) {}
  
  protected void _afterPropertiesSet() throws Exception {
    this.imgBlueVideo.setImage(new Image(getClass().getResourceAsStream("/images/ico-novideo.png")));
    this.imgBlueVideo.setUserData(Boolean.FALSE);
    this.imgBlueVideo.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
          public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getClickCount() == 1 && Boolean.TRUE
              .equals(MatchFinalResultController.this.imgBlueVideo.getUserData())) {
              MatchFinalResultController.this.scoreboardEditorController.setBlueVideoRequested(true);
              MatchFinalResultController.this.scoreboardEditorController.setRedVideoRequested(false);
              MatchFinalResultController.this.openScoreboardEditor();
            } 
          }
        });
    this.imgRedVideo.setImage(new Image(getClass().getResourceAsStream("/images/ico-novideo.png")));
    this.imgRedVideo.setUserData(Boolean.FALSE);
    this.imgRedVideo.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
          public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getClickCount() == 1 && Boolean.TRUE
              .equals(MatchFinalResultController.this.imgRedVideo.getUserData())) {
              MatchFinalResultController.this.scoreboardEditorController.setBlueVideoRequested(false);
              MatchFinalResultController.this.scoreboardEditorController.setRedVideoRequested(true);
              MatchFinalResultController.this.openScoreboardEditor();
            } 
          }
        });
    this.matchWorker.blueVideoQuotaProperty().addListener(new ChangeListener<Number>() {
          public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newValue) {
            Platform.runLater(new Runnable() {
                  public void run() {
                    if (newValue.intValue() > 0) {
                      MatchFinalResultController.this.imgBlueVideo.setImage(CommonTkStrikeBaseController.IMAGE_VIDEO);
                      MatchFinalResultController.this.imgBlueVideo.setCursor(Cursor.HAND);
                      MatchFinalResultController.this.imgBlueVideo.setUserData(Boolean.TRUE);
                    } else {
                      MatchFinalResultController.this.imgBlueVideo.setImage(CommonTkStrikeBaseController.IMAGE_NO_VIDEO);
                      MatchFinalResultController.this.imgBlueVideo.setCursor(Cursor.DEFAULT);
                      MatchFinalResultController.this.imgBlueVideo.setUserData(Boolean.FALSE);
                    } 
                  }
                });
          }
        });
    this.matchWorker.redVideoQuotaProperty().addListener(new ChangeListener<Number>() {
          public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newValue) {
            Platform.runLater(new Runnable() {
                  public void run() {
                    if (newValue.intValue() > 0) {
                      MatchFinalResultController.this.imgRedVideo.setImage(new Image(getClass().getResourceAsStream("/images/ico-video.png")));
                      MatchFinalResultController.this.imgRedVideo.setCursor(Cursor.HAND);
                      MatchFinalResultController.this.imgRedVideo.setUserData(Boolean.TRUE);
                    } else {
                      MatchFinalResultController.this.imgRedVideo.setImage(new Image(getClass().getResourceAsStream("/images/ico-novideo.png")));
                      MatchFinalResultController.this.imgRedVideo.setCursor(Cursor.DEFAULT);
                      MatchFinalResultController.this.imgRedVideo.setUserData(Boolean.FALSE);
                    } 
                  }
                });
          }
        });
  }
}
