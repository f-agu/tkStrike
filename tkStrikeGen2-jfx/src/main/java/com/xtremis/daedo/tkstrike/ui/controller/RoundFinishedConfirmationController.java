package com.xtremis.daedo.tkstrike.ui.controller;

import com.xtremis.daedo.tkstrike.service.CommonMatchWorker;
import com.xtremis.daedo.tkstrike.service.MatchWorker;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.model.MatchConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.NetworkConfigurationEntry;
import java.util.concurrent.Callable;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;

public class RoundFinishedConfirmationController extends CommonRoundFinishedConfirmationController<NetworkConfigurationEntry, MatchWorker, ScoreboardEditorControllerImpl, MatchLogViewerController> {
  @Autowired
  private MatchWorker matchWorker;
  
  @Autowired
  private ScoreboardEditorControllerImpl scoreboardEditorController;
  
  @Autowired
  private MatchLogViewerController matchLogViewerController;
  
  protected void internalOnWindowShowEvent() {
    MatchConfigurationEntry currentMatchConfiguration = (MatchConfigurationEntry)getAppStatusWorker().getMatchConfigurationEntry();
    Platform.runLater(new Runnable() {
          public void run() {
            RoundFinishedConfirmationController.this.ivVideoBlue.setImage((RoundFinishedConfirmationController.this.matchWorker.getBlueVideoQuota() > 0) ? CommonTkStrikeBaseController.IMAGE_VIDEO : CommonTkStrikeBaseController.IMAGE_NO_VIDEO);
            RoundFinishedConfirmationController.this.ivVideoRed.setImage((RoundFinishedConfirmationController.this.matchWorker.getRedVideoQuota() > 0) ? CommonTkStrikeBaseController.IMAGE_VIDEO : CommonTkStrikeBaseController.IMAGE_NO_VIDEO);
          }
        });
  }
  
  protected void internalAfterPropertiesSet() {
    this.pnVideoBlue.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
          if (event.getClickCount() >= 1 && MouseButton.PRIMARY.equals(event.getButton()) && this.matchWorker.getBlueVideoQuota() > 0) {
            if (this.tgBestOf3ShowSuperiority.isSelected())
              this.tgBestOf3ShowSuperiority.setSelected(false); 
            doCallVideoRequest(true);
          } 
        });
    this.pnVideoRed.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
          if (event.getClickCount() >= 1 && MouseButton.PRIMARY.equals(event.getButton()) && this.matchWorker.getRedVideoQuota() > 0) {
            if (this.tgBestOf3ShowSuperiority.isSelected())
              this.tgBestOf3ShowSuperiority.setSelected(false); 
            doCallVideoRequest(false);
          } 
        });
    this.matchWorker.blueVideoQuotaProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(new Runnable() {
            public void run() {
              RoundFinishedConfirmationController.this.ivVideoBlue.setImage((newValue.intValue() > 0) ? CommonTkStrikeBaseController.IMAGE_VIDEO : CommonTkStrikeBaseController.IMAGE_NO_VIDEO);
            }
          }));
    this.matchWorker.redVideoQuotaProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(new Runnable() {
            public void run() {
              RoundFinishedConfirmationController.this.ivVideoRed.setImage((newValue.intValue() > 0) ? CommonTkStrikeBaseController.IMAGE_VIDEO : CommonTkStrikeBaseController.IMAGE_NO_VIDEO);
            }
          }));
  }
  
  private void doCallVideoRequest(final boolean isBlue) {
    if (this.wtUDPService.isConnected())
      TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {
            public Void call() throws Exception {
              RoundFinishedConfirmationController.this.wtUDPService.sendVideoReplay(isBlue, null);
              return null;
            }
          }); 
    this.scoreboardEditorController.setBlueVideoRequested(isBlue);
    this.scoreboardEditorController.setRedVideoRequested(!isBlue);
    openInNewStage(this.scoreboardEditorController, getMessage("title.window.scoreboardEditor"), 960, 560);
  }
  
  MatchWorker getMatchWorker() {
    return this.matchWorker;
  }
  
  ScoreboardEditorControllerImpl getScoreboardEditorController() {
    return this.scoreboardEditorController;
  }
  
  MatchLogViewerController getMatchLogViewerController() {
    return this.matchLogViewerController;
  }
  
  boolean showVideoReplay() {
    return true;
  }
}
