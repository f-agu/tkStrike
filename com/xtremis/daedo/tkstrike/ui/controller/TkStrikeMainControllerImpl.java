package com.xtremis.daedo.tkstrike.ui.controller;

import com.xtremis.daedo.tkstrike.om.MatchStatusId;
import com.xtremis.daedo.tkstrike.service.CommonMatchLogService;
import com.xtremis.daedo.tkstrike.service.CommonMatchWorker;
import com.xtremis.daedo.tkstrike.service.MatchWorker;
import com.xtremis.daedo.tkstrike.ui.BaseTkStrikeMainController;
import com.xtremis.daedo.tkstrike.ui.TkStrikeKeyCombinationsHelper;
import com.xtremis.daedo.tkstrike.ui.controller.configuration.ConfigurationMainController;
import com.xtremis.daedo.tkstrike.ui.controller.externalscreen.CommonExternalScreenMainController;
import com.xtremis.daedo.tkstrike.ui.controller.externalscreen.ExternalScreenMainController;
import com.xtremis.daedo.tkstrike.ui.controller.hardwaretest.HardwareTestMainController;
import com.xtremis.daedo.tkstrike.ui.controller.ringmanager.CommonRingManagerController;
import com.xtremis.daedo.tkstrike.ui.controller.ringmanager.RingManagerControllerController;
import com.xtremis.daedo.tkstrike.ui.scene.listener.ControlEnableListener;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.WindowEvent;
import jfxtras.labs.dialogs.MonologFXButton;
import org.springframework.beans.factory.annotation.Autowired;

public class TkStrikeMainControllerImpl extends BaseTkStrikeMainController<MatchWorker, MatchFinalResultController, RoundFinishedConfirmationController> implements TkStrikeMainController {
  private Boolean databaseInitializedOnStart = Boolean.FALSE;
  
  @Autowired
  private ConfigurationMainController configurationMainController;
  
  @Autowired
  private RingManagerControllerController ringManagerController;
  
  @Autowired
  private NetworkStatusController networkStatusController;
  
  @Autowired
  private HardwareTestMainController hardwareTestMainController;
  
  @Autowired
  private ExternalScreenMainController externalScreenMainController;
  
  @Autowired
  private ScoreboardController scoreboardController;
  
  @Autowired
  private MatchFinalResultController matchFinalResultController;
  
  @Autowired
  private ScoreboardEditorControllerImpl scoreboardEditorController;
  
  @Autowired
  private MatchLogViewerController matchLogViewerController;
  
  @Autowired
  private RoundFinishedConfirmationController roundFinishedConfirmationController;
  
  @Autowired
  private MatchWorker matchWorker;
  
  @Autowired
  private CommonMatchLogService matchLogService;
  
  public void setDatabaseInitializedOnStart(Boolean databaseInitializedOnStart) {
    this.databaseInitializedOnStart = databaseInitializedOnStart;
  }
  
  public void exitTkStrike() {
    doCloseThisStage();
    getAppStatusWorker().doDialogWindowCloses();
  }
  
  public boolean confirmExitTkStrike() {
    if (getAppStatusWorker().forceExitTkStrike().get())
      return true; 
    if (showMonologConfirmDialog(getMessage("title.default.question"), getMessage("message.exit.confirmMessage")).equals(MonologFXButton.Type.YES))
      if (this.matchWorker.getCurrentMatchStatus().equals(MatchStatusId.NOT_READY) || 
        showMonologConfirmDialog(getMessage("title.default.question"), getMessage("message.exit.matchInCourse.confirmMessage")).equals(MonologFXButton.Type.YES))
        return true;  
    return false;
  }
  
  public void openHardwareTest() {
    _openHardwareTest();
  }
  
  public void openScoreboardEditor() {
    openInNewStage(this.scoreboardEditorController, getMessage("title.window.scoreboardEditor"), 960, 560, false);
  }
  
  public void openMatchLog() {
    openInNewStage(this.matchLogViewerController, getMessage("title.window.matchLog"), 1200, 600);
  }
  
  protected void _openConfiguration() {
    openInNewStage((TkStrikeController)this.configurationMainController, new EventHandler<WindowEvent>() {
          public void handle(WindowEvent windowEvent) {
            TkStrikeMainControllerImpl.this.networkStatusController.onWindowShowEvent();
            TkStrikeMainControllerImpl.this.scoreboardController.onWindowShowEvent();
          }
        },  getMessage("title.window.configuration"), 970, this.configurationMainWindowHeight.intValue(), false);
  }
  
  protected void _openRingManager(CommonRingManagerController.RingManagerOpenType ringManagerOpenType) {
    this.ringManagerController.setRingManagerOpenType(ringManagerOpenType);
    openInNewStage((TkStrikeController)this.ringManagerController, getMessage("title.window.ringManager"), 1100, 550, false);
  }
  
  protected void _openHardwareTest() {
    openInNewStage((TkStrikeController)this.hardwareTestMainController, getMessage("title.window.hardwareTest"), 960, 600, false);
  }
  
  protected void _afterPropertiesSet() throws Exception {
    this.btConfig.setOnAction(new EventHandler<ActionEvent>() {
          public void handle(ActionEvent actionEvent) {
            TkStrikeMainControllerImpl.this._openConfiguration();
          }
        });
    this.btRingManager.setOnAction(new EventHandler<ActionEvent>() {
          public void handle(ActionEvent actionEvent) {
            TkStrikeMainControllerImpl.this._openRingManager(CommonRingManagerController.RingManagerOpenType.DEFAULT);
          }
        });
    this.pnNetworkStatus.getChildren().add(this.networkStatusController.getRootView());
    this.pnButtonsMatchControl.setDisable(true);
    getAppStatusWorker().dialogWindowClose().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean wasClosed) {
            if (wasClosed.booleanValue())
              Platform.runLater(new Runnable() {
                    public void run() {
                      TkStrikeMainControllerImpl.this.getCurrentStage().requestFocus();
                    }
                  }); 
          }
        });
    getAppStatusWorker().tryToExitTkStrike().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean tryToExit) {
            if (tryToExit.booleanValue())
              TkStrikeMainControllerImpl.this.doCloseThisStage(); 
          }
        });
    getAppStatusWorker().forceExitTkStrike().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean forceExit) {
            if (forceExit.booleanValue())
              TkStrikeMainControllerImpl.this.doCloseThisStage(); 
          }
        });
    this.currentMatchStatus.addListener((ChangeListener)new ControlEnableListener((Node)this.tgBackupSystem, new MatchStatusId[] { MatchStatusId.WAITING_4_MATCH, MatchStatusId.WAITING_4_START_ROUND, MatchStatusId.WAITING_4_START_GOLDENPOINT, MatchStatusId.NETWORK_ERROR, MatchStatusId.ROUND_PAUSED, MatchStatusId.ROUND_FINISHED, MatchStatusId.ROUND_GOLDENPOINT_FINISHED, MatchStatusId.NOT_READY, MatchStatusId.MATCH_FINISHED }));
    this.rootView.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
          public void handle(KeyEvent event) {
            if (TkStrikeKeyCombinationsHelper.keyCombVideoRequestBlue.match(event) && 
              !TkStrikeMainControllerImpl.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) && 
              !TkStrikeMainControllerImpl.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT)) {
              TkStrikeMainControllerImpl.this.scoreboardController.doCallVideoRequestByBlue();
            } else if (TkStrikeKeyCombinationsHelper.keyCombVideoRequestRed.match(event) && 
              !TkStrikeMainControllerImpl.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) && 
              !TkStrikeMainControllerImpl.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT)) {
              TkStrikeMainControllerImpl.this.scoreboardController.doCallVideoRequestByRed();
            } 
          }
        });
    ((BorderPane)getRootView()).setCenter(this.scoreboardController.getRootView());
  }
  
  public void onWindowShowEvent() {
    super.onWindowShowEvent();
    this.networkStatusController.onWindowShowEvent();
    this.scoreboardController.onWindowShowEvent();
    this.externalScreenMainController.onWindowShowEvent();
    this.lblStatusNetworkConfigured.requestFocus();
    if (this.databaseInitializedOnStart.booleanValue())
      showInfoDialog(getMessage("title.default.info"), getMessage("message.tkStrike.databaseInitialized")); 
    this.tgBackupSystem.setDisable(true);
    this.tgBackupSystem.setVisible(false);
  }
  
  protected MatchWorker getMatchWorker() {
    return this.matchWorker;
  }
  
  protected MatchFinalResultController getMatchFinalResultController() {
    return this.matchFinalResultController;
  }
  
  protected CommonExternalScreenMainController getExternalScreenMainController() {
    return (CommonExternalScreenMainController)this.externalScreenMainController;
  }
  
  protected RoundFinishedConfirmationController getRoundFinishedController() {
    return this.roundFinishedConfirmationController;
  }
}
