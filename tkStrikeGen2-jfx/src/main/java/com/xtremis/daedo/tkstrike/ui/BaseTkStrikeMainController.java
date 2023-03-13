package com.xtremis.daedo.tkstrike.ui;

import com.xtremis.daedo.tkstrike.om.ActionSource;
import com.xtremis.daedo.tkstrike.om.AppStatusId;
import com.xtremis.daedo.tkstrike.om.MatchStatusId;
import com.xtremis.daedo.tkstrike.orm.model.ScreenResolution;
import com.xtremis.daedo.tkstrike.service.AppStatusWorker;
import com.xtremis.daedo.tkstrike.service.CommonMatchWorker;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchVictoryCriteria;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.controller.CommonMatchFinalResultController;
import com.xtremis.daedo.tkstrike.ui.controller.CommonRoundFinishedConfirmationController;
import com.xtremis.daedo.tkstrike.ui.controller.CommonTkStrikeBaseController;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeController;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeMainController;
import com.xtremis.daedo.tkstrike.ui.controller.externalscreen.CommonExternalScreenMainController;
import com.xtremis.daedo.tkstrike.ui.controller.ringmanager.CommonRingManagerController;
import com.xtremis.daedo.tkstrike.ui.model.IRulesEntry;
import com.xtremis.daedo.tkstrike.ui.scene.listener.ControlEnableListener;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import jfxtras.labs.dialogs.MonologFXButton;
import org.apache.log4j.Logger;
import org.controlsfx.control.Notifications;
import org.springframework.beans.factory.annotation.Value;

public abstract class BaseTkStrikeMainController<MW extends CommonMatchWorker, MFRC extends CommonMatchFinalResultController, RFC extends CommonRoundFinishedConfirmationController> extends CommonTkStrikeBaseController implements TkStrikeMainController {
  protected SimpleObjectProperty<MatchStatusId> currentMatchStatus = new SimpleObjectProperty(this, "currentMatchStatus", MatchStatusId.NOT_READY);
  
  @Value("${tkStrike.requireConfirmationForPenalty}")
  private Boolean requireConfirmationForPenalty;
  
  @Value("${tkStrike.matchFinalResultWindowHeight}")
  private Integer matchFinalResultWindowHeight;
  
  @Value("${tkStrike.configurationMainWindowHeight}")
  protected Integer configurationMainWindowHeight;
  
  @Value("${tkStrike.allowGroupSelectionByColor}")
  private Boolean allowGroupSelectionByColor;
  
  @FXML
  protected ToggleButton tgBackupSystem;
  
  @FXML
  protected ToggleButton tgExternalScoreboard;
  
  @FXML
  protected Button btStartRound;
  
  @FXML
  protected Button btNextRound;
  
  @FXML
  protected Button btKyeShi;
  
  @FXML
  protected ToggleButton btDoctor;
  
  @FXML
  protected Button btTimeout;
  
  @FXML
  protected Button btScoreboard;
  
  @FXML
  protected Button btMatchLog;
  
  @FXML
  protected Button btFinalResult;
  
  @FXML
  protected Button btConfig;
  
  @FXML
  protected Button btRingManager;
  
  @FXML
  protected Button btHardwareTest;
  
  @FXML
  protected Button btExit;
  
  @FXML
  protected Pane pnNetworkStatus;
  
  @FXML
  protected Pane pnAppMainButtons;
  
  @FXML
  protected Pane pnButtonsMatchControl;
  
  @FXML
  protected Pane pnButtonsPenalties;
  
  @FXML
  protected Label lblStatusNetworkConfigured;
  
  @FXML
  protected Label lblStatusMatchConfigured;
  
  @FXML
  protected Label lblErrorsOnExternals;
  
  @FXML
  protected Label lblStatusReady4Match;
  
  @FXML
  protected Label lblStatusNetworkError;
  
  @FXML
  protected Label lblMatchVictoryCriteria;
  
  protected void showInfoNotification(final String notificationText) {
    Platform.runLater(new Runnable() {
          public void run() {
            try {
              Notifications notifications = Notifications.create();
              notifications = notifications.darkStyle();
              notifications = notifications.owner(BaseTkStrikeMainController.this.getRootView());
              notifications = notifications.text(notificationText);
              notifications = notifications.title(BaseTkStrikeMainController.this.getMessage("title.default.notification.info"));
              notifications = notifications.hideAfter(Duration.seconds(3.0D));
              notifications.showInformation();
            } catch (Exception e) {
              BaseTkStrikeMainController.logger.warn("Exception when showNotification", e);
            } 
          }
        });
  }
  
  private static final SimpleBooleanProperty warningExternalErrorsLocked = new SimpleBooleanProperty(Boolean.FALSE.booleanValue());
  
  private void showNotificationExternalErrors() {}
  
  public final void openFinalResult() {
    openInNewStage((TkStrikeController)getMatchFinalResultController(), 
        getMessage("title.window.finalResult"), 930, this.matchFinalResultWindowHeight
        
        .intValue(), false, false);
  }
  
  public void addGamJeom2Blue() {
    if (_canAddGamJeom(true))
      getMatchWorker().addBlueGamJeom(ActionSource.MAIN_CONTROL, System.currentTimeMillis(), getMatchWorker().getCurrentRoundCountdownAsMillis()); 
  }
  
  public void removeGamJeom2Blue() {
    if (_canRemoveGamJeom())
      getMatchWorker().removeBlueGamJeom(ActionSource.MAIN_CONTROL, 
          System.currentTimeMillis(), 
          getMatchWorker().getCurrentRoundCountdownAsMillis()); 
  }
  
  public void addGamJeom2Red() {
    if (_canAddGamJeom(false))
      getMatchWorker().addRedGamJeom(ActionSource.MAIN_CONTROL, System.currentTimeMillis(), 
          getMatchWorker().getCurrentRoundCountdownAsMillis()); 
  }
  
  public void removeGamJeom2Red() {
    if (_canRemoveGamJeom())
      getMatchWorker().removeRedGamJeom(ActionSource.MAIN_CONTROL, System.currentTimeMillis(), 
          getMatchWorker().getCurrentRoundCountdownAsMillis()); 
  }
  
  private boolean _canAddGamJeom(boolean isBlue) {
    if (MatchVictoryCriteria.BESTOF3.equals(getMatchWorker().getMatchVictoryCriteria()) && MatchStatusId.ROUND_FINISHED
      .equals(getMatchWorker().getCurrentMatchStatus())) {
      ButtonType okPrevRound = new ButtonType(getMessage("button.yesPrevRound"), ButtonBar.ButtonData.BACK_PREVIOUS);
      ButtonType okNextRound = new ButtonType(getMessage("button.yesNextRound"), ButtonBar.ButtonData.NEXT_FORWARD);
      ButtonType cancel = new ButtonType(getMessage("button.no"), ButtonBar.ButtonData.NO);
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION, getMessage("message.addGamJeom.confirmMessage"), new ButtonType[] { okPrevRound, okNextRound, cancel });
      alert.setTitle(getMessage("message.confirmDialog.title"));
      alert.setOnCloseRequest(new EventHandler<DialogEvent>() {
            public void handle(DialogEvent event) {}
          });
      alert.getDialogPane().requestFocus();
      alert.showAndWait().ifPresent(response -> {
            if (response.getButtonData() == ButtonBar.ButtonData.BACK_PREVIOUS) {
              logger.info("Add to Previous");
              if (paramBoolean) {
                getMatchWorker().addBlueGamJeom(ActionSource.MAIN_CONTROL, System.currentTimeMillis(), getMatchWorker().getCurrentRoundCountdownAsMillis());
              } else {
                getMatchWorker().addRedGamJeom(ActionSource.MAIN_CONTROL, System.currentTimeMillis(), getMatchWorker().getCurrentRoundCountdownAsMillis());
              } 
            } else if (response.getButtonData() == ButtonBar.ButtonData.NEXT_FORWARD) {
              logger.info("Add to NextRound");
              if (paramBoolean) {
                getMatchWorker().addBlueGamJeomToNextRound(ActionSource.MAIN_CONTROL, System.currentTimeMillis(), getMatchWorker().getCurrentRoundCountdownAsMillis());
              } else {
                getMatchWorker().addRedGamJeomToNextRound(ActionSource.MAIN_CONTROL, System.currentTimeMillis(), getMatchWorker().getCurrentRoundCountdownAsMillis());
              } 
            } else if (response == ButtonType.CANCEL) {
              logger.info("Cancel");
            } 
          });
      return false;
    } 
    return _canAddPenalty();
  }
  
  private boolean _canRemoveGamJeom() {
    return _canRemovePenalty();
  }
  
  private boolean _canAddPenalty() {
    return (getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) || 
      getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT) || 
      getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_START_ROUND) || 
      getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_START_GOLDENPOINT) || 
      getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_FINISHED) || 
      getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_PAUSED) || 
      getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_KYESHI)) ? 
      _addGamJeomConfirmation() : false;
  }
  
  private boolean _canRemovePenalty() {
    return (getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) || 
      getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT) || 
      getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_START_ROUND) || 
      getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_START_GOLDENPOINT) || 
      getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_FINISHED) || 
      getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_PAUSED) || 
      getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_KYESHI)) ? 
      _removeGamJeomConfirmation() : false;
  }
  
  private boolean _addGamJeomConfirmation() {
    if (this.requireConfirmationForPenalty.booleanValue()) {
      Optional<ButtonType> opResponse = showConfirmDialog(getMessage("message.confirmDialog.title"), getMessage("message.addGamJeom.confirmMessage"));
      return (opResponse != null && opResponse.isPresent() && ((ButtonType)opResponse.get()).equals(ButtonType.OK));
    } 
    return true;
  }
  
  private boolean _removeGamJeomConfirmation() {
    if (this.requireConfirmationForPenalty.booleanValue()) {
      Optional<T> opResponse = showConfirmDialog(getMessage("message.confirmDialog.title"), getMessage("message.removeGamJeom.confirmMessage"));
      return (opResponse != null && opResponse.isPresent() && opResponse.get().equals(ButtonType.OK));
    } 
    return true;
  }
  
  final Delta dragDelta = new Delta();
  
  class Delta {
    double x;
    
    double y;
  }
  
  public final void showExternalScoreboard() {
    Scene esScene = null;
    int screenWidth = 1280;
    int screenHeight = 720;
    switch (getAppStatusWorker().getExternalScreenResolution()) {
      case ENTER:
        screenWidth = 1280;
        screenHeight = 720;
        break;
      case ESCAPE:
        screenWidth = 1920;
        screenHeight = 1080;
        break;
    } 
    screenHeight += 30;
    if (getExternalScreenMainController().getRootView().getScene() != null) {
      esScene = getExternalScreenMainController().getRootView().getScene();
    } else {
      esScene = new Scene((Parent)getExternalScreenMainController().getRootView(), screenWidth, screenHeight);
    } 
    double zoom = 1.3333333333333D;
    if (getAppStatusWorker().getExternalScreenResolution().equals(ScreenResolution.FHD))
      zoom = 2.0D; 
    esScene.getRoot().setScaleX(zoom);
    esScene.getRoot().setScaleY(zoom);
    esScene.getRoot().setScaleZ(zoom);
    double screenMinX = Screen.getPrimary().getBounds().getMinX();
    double screenMinY = Screen.getPrimary().getBounds().getMinY();
    for (Screen screen : Screen.getScreens()) {
      if (!screen.equals(Screen.getPrimary())) {
        screenMinX = screen.getBounds().getMinX();
        screenMinY = screen.getBounds().getMinY();
      } 
    } 
    esScene.getStylesheets().addAll((Object[])this.tkStrikeStyleSheetsHelper.getTkStrikeStyleSheets());
    final Stage esStage = new Stage();
    if ("Mac OS X".equals(System.getProperty("os.name"))) {
      esStage.initStyle(StageStyle.UTILITY);
    } else {
      esStage.initStyle(StageStyle.UNDECORATED);
    } 
    esStage.setTitle(getMessage("title.window.externalScreen"));
    esStage.setResizable(true);
    esStage.setX(screenMinX);
    esStage.setY(screenMinY);
    esScene.getRoot().setOnMousePressed(new EventHandler<MouseEvent>() {
          public void handle(MouseEvent mouseEvent) {
            BaseTkStrikeMainController.this.dragDelta.x = esStage.getX() - mouseEvent.getScreenX();
            BaseTkStrikeMainController.this.dragDelta.y = esStage.getY() - mouseEvent.getScreenY();
          }
        });
    esScene.getRoot().setOnMouseDragged(new EventHandler<MouseEvent>() {
          public void handle(MouseEvent mouseEvent) {
            esStage.setX(mouseEvent.getScreenX() + BaseTkStrikeMainController.this.dragDelta.x);
            esStage.setY(mouseEvent.getScreenY() + BaseTkStrikeMainController.this.dragDelta.y);
          }
        });
    esStage.setWidth(screenWidth);
    esStage.setMaxWidth(screenWidth);
    esStage.setMinWidth(screenWidth);
    esStage.setHeight(screenHeight);
    esStage.setMinHeight(screenHeight);
    esStage.setMaxHeight(screenHeight);
    esStage.setFullScreen(true);
    esStage.setFullScreenExitKeyCombination((KeyCombination)new KeyCodeCombination(KeyCode.E, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN }));
    esStage.setMaximized(true);
    esStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon_app.png")));
    esStage.setScene(esScene);
    esStage.addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler<WindowEvent>() {
          public void handle(WindowEvent windowEvent) {
            BaseTkStrikeMainController.this.getExternalScreenMainController().onWindowShowEvent();
            Platform.runLater(new Runnable() {
                  public void run() {
                    BaseTkStrikeMainController.this.getCurrentStage().requestFocus();
                  }
                });
          }
        });
    esStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>() {
          public void handle(WindowEvent windowEvent) {
            if (BaseTkStrikeMainController.this.tgExternalScoreboard.isSelected())
              BaseTkStrikeMainController.this.tgExternalScoreboard.setSelected(false); 
          }
        });
    esStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, getExternalScreenMainController().getOnWindowCloseEventHandler());
    esStage.show();
  }
  
  public final void hideExternalScoreboard() {
    if (getExternalScreenMainController().getRootView().getScene() != null) {
      if (getExternalScreenMainController().getCurrentStage().isFullScreen())
        getExternalScreenMainController().getCurrentStage().setFullScreen(false); 
      getExternalScreenMainController().getCurrentStage().fireEvent((Event)new WindowEvent((Window)getCurrentStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
    } 
  }
  
  public final void afterPropertiesSet() throws Exception {
    this.btFinalResult.setOnAction(new EventHandler<ActionEvent>() {
          public void handle(ActionEvent event) {
            BaseTkStrikeMainController.this.openFinalResult();
          }
        });
    getAppStatusWorker().appStatusChanged().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue.booleanValue())
              Platform.runLater(new Runnable() {
                    public void run() {
                      BaseTkStrikeMainController.this.addStatusCSSToLabel(BaseTkStrikeMainController.this.lblStatusNetworkError, "appStatus-no");
                      try {
                        for (AppStatusId appStatusId : BaseTkStrikeMainController.this.getAppStatusWorker().getCurrentAppStatuses()) {
                          if (AppStatusId.NETWORK_CONFIGURED.equals(appStatusId)) {
                            BaseTkStrikeMainController.this.addStatusCSSToLabel(BaseTkStrikeMainController.this.lblStatusNetworkConfigured, "appStatus-ok");
                          } else if (AppStatusId.MATCH_CONFIGURED.equals(appStatusId)) {
                            BaseTkStrikeMainController.this.addStatusCSSToLabel(BaseTkStrikeMainController.this.lblStatusMatchConfigured, "appStatus-ok");
                          } else if (!AppStatusId.HT_JUDGES_OK.equals(appStatusId) && 
                            !AppStatusId.HT_ATHLETES_OK.equals(appStatusId)) {
                            if (AppStatusId.READY_FOR_MATCH.equals(appStatusId)) {
                              BaseTkStrikeMainController.this.addStatusCSSToLabel(BaseTkStrikeMainController.this.lblStatusReady4Match, "appStatus-ok");
                            } else if (AppStatusId.NETWORK_ERROR.equals(appStatusId)) {
                              BaseTkStrikeMainController.this.addStatusCSSToLabel(BaseTkStrikeMainController.this.lblStatusReady4Match, "appStatus-warn");
                              BaseTkStrikeMainController.this.addStatusCSSToLabel(BaseTkStrikeMainController.this.lblStatusNetworkError, "appStatus-error");
                            } 
                          } 
                        } 
                      } catch (Exception exception) {}
                    }
                  }); 
          }
        });
    getMatchWorker().currentMatchStatusProperty().addListener(new ChangeListener<MatchStatusId>() {
          public void changed(ObservableValue<? extends MatchStatusId> observableValue, MatchStatusId oldMatchStatusId, final MatchStatusId newMatchStatus) {
            BaseTkStrikeMainController.this.currentMatchStatus.set(newMatchStatus);
            Platform.runLater(new Runnable() {
                  public void run() {
                    if (newMatchStatus != null)
                      try {
                        switch (newMatchStatus) {
                          case ESCAPE:
                            BaseTkStrikeMainController.this.btStartRound.setText(BaseTkStrikeMainController.this.getMessage("btn.main.start-round"));
                            BaseTkStrikeMainController.this.btStartRound.setUserData(Boolean.TRUE);
                            BaseTkStrikeMainController.this.btTimeout.setText(BaseTkStrikeMainController.this.getMessage("btn.main.timeout"));
                            BaseTkStrikeMainController.this.btTimeout.setUserData(BaseTkStrikeMainController.TimeoutButtonUserData.TIMEOUT);
                            break;
                          case SPACE:
                          case null:
                            BaseTkStrikeMainController.this.btTimeout.setText(BaseTkStrikeMainController.this.getMessage("btn.main.timeout"));
                            BaseTkStrikeMainController.this.btTimeout.setUserData(BaseTkStrikeMainController.TimeoutButtonUserData.TIMEOUT);
                            BaseTkStrikeMainController.this.btStartRound.setText(BaseTkStrikeMainController.this.getMessage("btn.main.end-round"));
                            BaseTkStrikeMainController.this.btStartRound.setUserData(Boolean.FALSE);
                            BaseTkStrikeMainController.this.btKyeShi.setUserData(BaseTkStrikeMainController.KyeShiButtonUserData.KYE_SHI);
                            BaseTkStrikeMainController.this.btKyeShi.setText(BaseTkStrikeMainController.this.getMessage("btn.main.kye-shi"));
                            break;
                          case null:
                            BaseTkStrikeMainController.this.btStartRound.setText(BaseTkStrikeMainController.this.getMessage("btn.main.end-round"));
                            BaseTkStrikeMainController.this.btStartRound.setUserData(Boolean.FALSE);
                            BaseTkStrikeMainController.this.btTimeout.setText(BaseTkStrikeMainController.this.getMessage("btn.main.resume"));
                            BaseTkStrikeMainController.this.btTimeout.setUserData(BaseTkStrikeMainController.TimeoutButtonUserData.RESUME);
                            BaseTkStrikeMainController.this.btKyeShi.setText(BaseTkStrikeMainController.this.getMessage("btn.main.kye-shi"));
                            BaseTkStrikeMainController.this.btKyeShi.setUserData(BaseTkStrikeMainController.KyeShiButtonUserData.KYE_SHI);
                            break;
                          case null:
                            BaseTkStrikeMainController.this.btTimeout.setText(BaseTkStrikeMainController.this.getMessage("button.cancelPARATimeout"));
                            BaseTkStrikeMainController.this.btTimeout.setUserData(BaseTkStrikeMainController.TimeoutButtonUserData.CANCEL_PARA_TIMEOUT);
                            break;
                          case null:
                          case null:
                            BaseTkStrikeMainController.this.btStartRound.setText(BaseTkStrikeMainController.this.getMessage("btn.main.start-round"));
                            BaseTkStrikeMainController.this.btStartRound.setUserData(Boolean.TRUE);
                            BaseTkStrikeMainController.this.btTimeout.setText(BaseTkStrikeMainController.this.getMessage("btn.main.timeout"));
                            BaseTkStrikeMainController.this.btTimeout.setUserData(BaseTkStrikeMainController.TimeoutButtonUserData.TIMEOUT);
                            BaseTkStrikeMainController.this.btKyeShi.setUserData(BaseTkStrikeMainController.KyeShiButtonUserData.KYE_SHI);
                            BaseTkStrikeMainController.this.btKyeShi.setText(BaseTkStrikeMainController.this.getMessage("btn.main.kye-shi"));
                            break;
                          case null:
                            BaseTkStrikeMainController.this.btStartRound.setText(BaseTkStrikeMainController.this.getMessage("btn.main.start-round"));
                            BaseTkStrikeMainController.this.btStartRound.setUserData(Boolean.TRUE);
                            BaseTkStrikeMainController.this.btTimeout.setText(BaseTkStrikeMainController.this.getMessage("btn.main.timeout"));
                            BaseTkStrikeMainController.this.btTimeout.setUserData(BaseTkStrikeMainController.TimeoutButtonUserData.TIMEOUT);
                            BaseTkStrikeMainController.this.btKyeShi.setUserData(BaseTkStrikeMainController.KyeShiButtonUserData.KYE_SHI);
                            BaseTkStrikeMainController.this.btKyeShi.setText(BaseTkStrikeMainController.this.getMessage("btn.main.kye-shi"));
                            break;
                          case null:
                            BaseTkStrikeMainController.this.btTimeout.setText(BaseTkStrikeMainController.this.getMessage("btn.main.resume"));
                            BaseTkStrikeMainController.this.btTimeout.setUserData(BaseTkStrikeMainController.TimeoutButtonUserData.RESUME);
                            BaseTkStrikeMainController.this.btKyeShi.setText(BaseTkStrikeMainController.this.getMessage("btn.main.kye-shi"));
                            BaseTkStrikeMainController.this.btKyeShi.setUserData(BaseTkStrikeMainController.KyeShiButtonUserData.KYE_SHI);
                            break;
                          case null:
                            if (!BaseTkStrikeMainController.this.getMatchFinalResultController().isOpened())
                              BaseTkStrikeMainController.this.openFinalResult(); 
                            break;
                        } 
                      } catch (RuntimeException e) {
                        BaseTkStrikeMainController.logger.error("RunTimeException - ", e);
                      }  
                  }
                });
          }
        });
    getMatchWorker().roundFinishedOpenProperty().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue.booleanValue())
              Platform.runLater(new Runnable() {
                    public void run() {
                      BaseTkStrikeMainController.this.openInNewStage((TkStrikeController)BaseTkStrikeMainController.this.getRoundFinishedController(), BaseTkStrikeMainController.this
                          .getMessage("title.window.roundFinished"), 430, 360, false, false);
                    }
                  }); 
          }
        });
    getMatchWorker().matchWinnerByPointGapNeedsConfirmationProperty().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue.booleanValue())
              Platform.runLater(new Runnable() {
                    public void run() {
                      boolean accepted = BaseTkStrikeMainController.this.showMonologConfirmDialog(BaseTkStrikeMainController.this.getMessage("message.confirmDialog.title"), BaseTkStrikeMainController.this.getMessage("message.confirm.confirmPointGapByPenalties")).equals(MonologFXButton.Type.YES);
                      BaseTkStrikeMainController.this.getMatchWorker().applyPointGapConfirmation(accepted);
                    }
                  }); 
          }
        });
    getAppStatusWorker().matchLogCSVGeneratedProperty().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue.booleanValue())
              BaseTkStrikeMainController.this.showInfoNotification(BaseTkStrikeMainController.this.getMessage("message.info.matchLogCSVGenerated")); 
          }
        });
    getAppStatusWorker().matchLogXLSGeneratedProperty().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue.booleanValue())
              BaseTkStrikeMainController.this.showInfoNotification(BaseTkStrikeMainController.this.getMessage("message.info.matchLogXLSGenerated")); 
          }
        });
    getAppStatusWorker().matchLogPDFGeneratedProperty().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue.booleanValue())
              BaseTkStrikeMainController.this.showInfoNotification(BaseTkStrikeMainController.this.getMessage("message.info.matchLogPDFGenerated")); 
          }
        });
    getAppStatusWorker().errorWithExternalServiceProperty().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, final Boolean newValue) {
            Platform.runLater(new Runnable() {
                  public void run() {
                    BaseTkStrikeMainController.this.lblErrorsOnExternals.setVisible(newValue.booleanValue());
                    BaseTkStrikeMainController.this.lblStatusMatchConfigured.setVisible(!newValue.booleanValue());
                  }
                });
            if (newValue.booleanValue())
              TkStrikeExecutors.schedule(new Runnable() {
                    public void run() {
                      BaseTkStrikeMainController.this.getAppStatusWorker().informNoErrorWithExternalService();
                    }
                  },  2L, TimeUnit.SECONDS); 
          }
        });
    MatchVictoryCriteria byRules = getAppStatusWorker().getRulesEntry().getMatchVictoryCriteria();
    if (MatchVictoryCriteria.BESTOF3.equals(byRules)) {
      this.lblMatchVictoryCriteria.setText(getMessage("toggle.matchVictoryCriteria.byRounds"));
    } else {
      this.lblMatchVictoryCriteria.setText(getMessage("toggle.matchVictoryCriteria.byPoints"));
    } 
    getAppStatusWorker().rulesChanges().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, final Boolean newValue) {
            Platform.runLater(new Runnable() {
                  public void run() {
                    if (newValue.booleanValue()) {
                      MatchVictoryCriteria byRules = BaseTkStrikeMainController.this.getAppStatusWorker().getRulesEntry().getMatchVictoryCriteria();
                      if (MatchVictoryCriteria.BESTOF3.equals(byRules)) {
                        BaseTkStrikeMainController.this.lblMatchVictoryCriteria.setText(BaseTkStrikeMainController.this.getMessage("toggle.matchVictoryCriteria.byRounds"));
                      } else {
                        BaseTkStrikeMainController.this.lblMatchVictoryCriteria.setText(BaseTkStrikeMainController.this.getMessage("toggle.matchVictoryCriteria.byPoints"));
                      } 
                    } 
                  }
                });
          }
        });
    this.btStartRound.setUserData(Boolean.TRUE);
    this.btStartRound.setOnAction(new EventHandler<ActionEvent>() {
          public void handle(ActionEvent actionEvent) {
            Boolean validate = (Boolean)BaseTkStrikeMainController.this.btStartRound.getUserData();
            if (validate.booleanValue()) {
              BaseTkStrikeMainController.this.getMatchWorker().startRound();
            } else {
              BaseTkStrikeMainController.this.getMatchWorker().endRound();
            } 
          }
        });
    this.btTimeout.setUserData(TimeoutButtonUserData.TIMEOUT);
    this.btTimeout.setOnAction(new EventHandler<ActionEvent>() {
          public void handle(ActionEvent actionEvent) {
            BaseTkStrikeMainController.TimeoutButtonUserData timeoutButtonUserData = (BaseTkStrikeMainController.TimeoutButtonUserData)BaseTkStrikeMainController.this.btTimeout.getUserData();
            switch (timeoutButtonUserData) {
              case ENTER:
                BaseTkStrikeMainController.this.btTimeout.setText(BaseTkStrikeMainController.this.getMessage("btn.main.timeout"));
                BaseTkStrikeMainController.this.btTimeout.setUserData(BaseTkStrikeMainController.TimeoutButtonUserData.TIMEOUT);
                BaseTkStrikeMainController.this.getMatchWorker().doResumeRound();
                break;
              case ESCAPE:
                BaseTkStrikeMainController.this.btTimeout.setText(BaseTkStrikeMainController.this.getMessage("btn.main.resume"));
                BaseTkStrikeMainController.this.btTimeout.setUserData(BaseTkStrikeMainController.TimeoutButtonUserData.RESUME);
                BaseTkStrikeMainController.this.getMatchWorker().doPauseRound();
                break;
              case SPACE:
                BaseTkStrikeMainController.this.btTimeout.setText(BaseTkStrikeMainController.this.getMessage("btn.main.resume"));
                BaseTkStrikeMainController.this.btTimeout.setUserData(BaseTkStrikeMainController.TimeoutButtonUserData.RESUME);
                BaseTkStrikeMainController.this.getMatchWorker().cancelParaTimeOutCountdown();
                break;
            } 
          }
        });
    this.btKyeShi.setUserData(KyeShiButtonUserData.KYE_SHI);
    this.btKyeShi.setOnAction(new EventHandler<ActionEvent>() {
          public void handle(ActionEvent actionEvent) {
            String buttonMessage;
            BaseTkStrikeMainController.KyeShiButtonUserData buttonUserData;
            MatchStatusId currentStatus;
            BaseTkStrikeMainController.KyeShiButtonUserData validate = (BaseTkStrikeMainController.KyeShiButtonUserData)BaseTkStrikeMainController.this.btKyeShi.getUserData();
            switch (validate) {
              case ENTER:
                buttonMessage = BaseTkStrikeMainController.this.getMessage("btn.main.timeout");
                buttonUserData = BaseTkStrikeMainController.KyeShiButtonUserData.PAUSE_ROUND;
                currentStatus = BaseTkStrikeMainController.this.getMatchWorker().getCurrentMatchStatus();
                if (MatchStatusId.WAITING_4_MATCH.equals(currentStatus) || MatchStatusId.WAITING_4_START_ROUND
                  .equals(currentStatus) || MatchStatusId.WAITING_4_START_GOLDENPOINT
                  .equals(currentStatus)) {
                  buttonMessage = BaseTkStrikeMainController.this.getMessage("btn.main.end-kye-shi");
                  buttonUserData = BaseTkStrikeMainController.KyeShiButtonUserData.END_KYE_SHI;
                } 
                BaseTkStrikeMainController.this.btKyeShi.setText(buttonMessage);
                BaseTkStrikeMainController.this.btKyeShi.setUserData(buttonUserData);
                BaseTkStrikeMainController.this.getMatchWorker().doKyeShiInRound();
                break;
              case ESCAPE:
                BaseTkStrikeMainController.this.btKyeShi.setText(BaseTkStrikeMainController.this.getMessage("btn.main.kye-shi"));
                BaseTkStrikeMainController.this.btKyeShi.setUserData(BaseTkStrikeMainController.KyeShiButtonUserData.KYE_SHI);
                BaseTkStrikeMainController.this.getMatchWorker().doPauseRound();
                break;
              case SPACE:
                BaseTkStrikeMainController.this.btKyeShi.setText(BaseTkStrikeMainController.this.getMessage("btn.main.kye-shi"));
                BaseTkStrikeMainController.this.btKyeShi.setUserData(BaseTkStrikeMainController.KyeShiButtonUserData.KYE_SHI);
                BaseTkStrikeMainController.this.getMatchWorker().doEndKyeShiIndRound();
                break;
              case null:
                BaseTkStrikeMainController.this.btKyeShi.setText(BaseTkStrikeMainController.this.getMessage("btn.main.kye-shi"));
                BaseTkStrikeMainController.this.btKyeShi.setUserData(BaseTkStrikeMainController.KyeShiButtonUserData.KYE_SHI);
                BaseTkStrikeMainController.this.getMatchWorker().doResumeRound();
                break;
            } 
          }
        });
    getMatchWorker().doctorInRoundProperty().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            BaseTkStrikeMainController.this.btDoctor.setSelected(newValue.booleanValue());
          }
        });
    this.btDoctor.setOnAction(new EventHandler<ActionEvent>() {
          public void handle(ActionEvent event) {
            if (BaseTkStrikeMainController.this.getMatchWorker().isDoctorInRound()) {
              BaseTkStrikeMainController.this.getMatchWorker().doctorQuitInRound();
            } else {
              BaseTkStrikeMainController.this.getMatchWorker().callDoctorInRound();
            } 
          }
        });
    this.btNextRound.setOnAction(new EventHandler<ActionEvent>() {
          public void handle(ActionEvent actionEvent) {
            BaseTkStrikeMainController.this.getMatchWorker().goNextRound();
          }
        });
    this.currentMatchStatus.addListener((ChangeListener)new ControlEnableListener((Node)this.pnButtonsPenalties, new MatchStatusId[] { MatchStatusId.ROUND_WORKING, MatchStatusId.WAITING_4_START_ROUND, MatchStatusId.WAITING_4_START_GOLDENPOINT, MatchStatusId.ROUND_IN_GOLDENPOINT, MatchStatusId.ROUND_KYESHI, MatchStatusId.ROUND_FINISHED, MatchStatusId.ROUND_PAUSED }));
    this.currentMatchStatus.addListener((ChangeListener)new ControlEnableListener((Node)this.pnButtonsMatchControl, new MatchStatusId[] { 
            MatchStatusId.WAITING_4_MATCH, MatchStatusId.WAITING_4_START_ROUND, MatchStatusId.WAITING_4_START_GOLDENPOINT, MatchStatusId.ROUND_WORKING, MatchStatusId.ROUND_PAUSED, MatchStatusId.ROUND_KYESHI, MatchStatusId.ROUND_FINISHED, MatchStatusId.ROUND_PARA_TIMEOUT_WORKING, MatchStatusId.ROUND_IN_GOLDENPOINT, MatchStatusId.ROUND_GOLDENPOINT_FINISHED, 
            MatchStatusId.MATCH_FINISHED, MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION, MatchStatusId.NETWORK_ERROR }));
    this.currentMatchStatus.addListener((ChangeListener)new ControlEnableListener((Node)this.btStartRound, new MatchStatusId[] { MatchStatusId.WAITING_4_MATCH, MatchStatusId.WAITING_4_START_ROUND, MatchStatusId.WAITING_4_START_GOLDENPOINT, MatchStatusId.ROUND_WORKING, MatchStatusId.ROUND_KYESHI, MatchStatusId.ROUND_PAUSED, MatchStatusId.ROUND_IN_GOLDENPOINT }));
    this.currentMatchStatus.addListener((ChangeListener)new ControlEnableListener((Node)this.btNextRound, new MatchStatusId[] { MatchStatusId.ROUND_PAUSED, MatchStatusId.ROUND_KYESHI, MatchStatusId.ROUND_FINISHED }));
    this.currentMatchStatus.addListener((ChangeListener)new ControlEnableListener((Node)this.btKyeShi, new MatchStatusId[] { MatchStatusId.WAITING_4_MATCH, MatchStatusId.WAITING_4_START_ROUND, MatchStatusId.WAITING_4_START_GOLDENPOINT, MatchStatusId.ROUND_WORKING, MatchStatusId.ROUND_IN_GOLDENPOINT, MatchStatusId.NETWORK_ERROR, MatchStatusId.ROUND_PAUSED, MatchStatusId.ROUND_KYESHI }));
    this.currentMatchStatus.addListener((ChangeListener)new ControlEnableListener((Node)this.btDoctor, new MatchStatusId[] { MatchStatusId.ROUND_KYESHI, MatchStatusId.WAITING_4_MATCH, MatchStatusId.WAITING_4_START_ROUND, MatchStatusId.WAITING_4_START_GOLDENPOINT, MatchStatusId.ROUND_WORKING, MatchStatusId.ROUND_IN_GOLDENPOINT, MatchStatusId.ROUND_PAUSED }));
    this.currentMatchStatus.addListener((ChangeListener)new ControlEnableListener((Node)this.btTimeout, new MatchStatusId[] { MatchStatusId.ROUND_WORKING, MatchStatusId.ROUND_IN_GOLDENPOINT, MatchStatusId.ROUND_PARA_TIMEOUT_WORKING, MatchStatusId.ROUND_PAUSED }));
    this.currentMatchStatus.addListener((ChangeListener)new ControlEnableListener((Node)this.btScoreboard, new MatchStatusId[] { MatchStatusId.WAITING_4_MATCH, MatchStatusId.ROUND_PAUSED, MatchStatusId.ROUND_PARA_TIMEOUT_WORKING, MatchStatusId.ROUND_KYESHI, MatchStatusId.ROUND_FINISHED, MatchStatusId.WAITING_4_START_ROUND, MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION, MatchStatusId.ROUND_GOLDENPOINT_FINISHED }));
    this.currentMatchStatus.addListener((ChangeListener)new ControlEnableListener((Node)this.btMatchLog, new MatchStatusId[] { MatchStatusId.ROUND_PAUSED, MatchStatusId.ROUND_PARA_TIMEOUT_WORKING, MatchStatusId.ROUND_KYESHI, MatchStatusId.ROUND_FINISHED, MatchStatusId.WAITING_4_START_ROUND, MatchStatusId.MATCH_FINISHED, MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION, MatchStatusId.ROUND_GOLDENPOINT_FINISHED }));
    this.currentMatchStatus.addListener((ChangeListener)new ControlEnableListener((Node)this.btFinalResult, new MatchStatusId[] { MatchStatusId.WAITING_4_MATCH, MatchStatusId.NETWORK_ERROR, MatchStatusId.ROUND_PARA_TIMEOUT_WORKING, MatchStatusId.ROUND_PAUSED, MatchStatusId.ROUND_KYESHI, MatchStatusId.ROUND_FINISHED, MatchStatusId.WAITING_4_START_ROUND, MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION, MatchStatusId.ROUND_GOLDENPOINT_FINISHED }));
    this.currentMatchStatus.addListener((ChangeListener)new ControlEnableListener((Node)this.btRingManager, new MatchStatusId[] { MatchStatusId.WAITING_4_MATCH, MatchStatusId.NETWORK_ERROR, MatchStatusId.NOT_READY, MatchStatusId.MATCH_FINISHED }));
    this.currentMatchStatus.addListener((ChangeListener)new ControlEnableListener((Node)this.btHardwareTest, new MatchStatusId[] { MatchStatusId.WAITING_4_MATCH, MatchStatusId.NETWORK_ERROR, MatchStatusId.ROUND_PAUSED, MatchStatusId.ROUND_FINISHED, MatchStatusId.WAITING_4_START_ROUND, MatchStatusId.NOT_READY, MatchStatusId.MATCH_FINISHED }));
    this.currentMatchStatus.addListener((ChangeListener)new ControlEnableListener((Node)this.btConfig, new MatchStatusId[] { MatchStatusId.WAITING_4_MATCH, MatchStatusId.NETWORK_ERROR, MatchStatusId.NOT_READY, MatchStatusId.MATCH_FINISHED }));
    this.rootView.setOnKeyPressed(new EventHandler<KeyEvent>() {
          public void handle(KeyEvent keyEvent) {
            switch (keyEvent.getCode()) {
              case ENTER:
                if (!BaseTkStrikeMainController.this.btStartRound.isDisabled()) {
                  Boolean validate = (Boolean)BaseTkStrikeMainController.this.btStartRound.getUserData();
                  if (validate.booleanValue())
                    BaseTkStrikeMainController.this.btStartRound.fire(); 
                } 
                break;
              case ESCAPE:
                BaseTkStrikeMainController.this.exitTkStrike();
                break;
              case SPACE:
                if (!BaseTkStrikeMainController.this.btTimeout.isDisabled())
                  BaseTkStrikeMainController.this.btTimeout.fire(); 
                break;
            } 
          }
        });
    this.rootView.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
          public void handle(KeyEvent event) {
            if (TkStrikeKeyCombinationsHelper.keyCombConfig.match(event) && 
              !BaseTkStrikeMainController.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) && 
              !BaseTkStrikeMainController.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT)) {
              Platform.runLater(new Runnable() {
                    public void run() {
                      BaseTkStrikeMainController.this._openConfiguration();
                    }
                  });
            } else if (TkStrikeKeyCombinationsHelper.keyCombRingManager.match(event) && 
              !BaseTkStrikeMainController.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) && 
              !BaseTkStrikeMainController.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT)) {
              Platform.runLater(new Runnable() {
                    public void run() {
                      BaseTkStrikeMainController.this._openRingManager(CommonRingManagerController.RingManagerOpenType.DEFAULT);
                    }
                  });
            } else if (TkStrikeKeyCombinationsHelper.keyCombRingManagerNextMatch.match(event) && 
              !BaseTkStrikeMainController.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) && 
              !BaseTkStrikeMainController.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT)) {
              Platform.runLater(new Runnable() {
                    public void run() {
                      BaseTkStrikeMainController.this._openRingManager(CommonRingManagerController.RingManagerOpenType.REQUEST_NEXT_MATCH);
                    }
                  });
            } else if (TkStrikeKeyCombinationsHelper.keyCombRingManagerPrevMatch.match(event) && 
              !BaseTkStrikeMainController.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) && 
              !BaseTkStrikeMainController.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT)) {
              Platform.runLater(new Runnable() {
                    public void run() {
                      BaseTkStrikeMainController.this._openRingManager(CommonRingManagerController.RingManagerOpenType.REQUEST_PREV_MATCH);
                    }
                  });
            } else if (TkStrikeKeyCombinationsHelper.keyCombFinalResult.match(event)) {
              Platform.runLater(new Runnable() {
                    public void run() {
                      BaseTkStrikeMainController.this.openFinalResult();
                    }
                  });
            } else if (TkStrikeKeyCombinationsHelper.keyCombHardwareTest.match(event) && 
              !BaseTkStrikeMainController.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) && 
              !BaseTkStrikeMainController.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT)) {
              Platform.runLater(new Runnable() {
                    public void run() {
                      BaseTkStrikeMainController.this._openHardwareTest();
                    }
                  });
            } else if (TkStrikeKeyCombinationsHelper.keyCombKyeShi.match(event) && !BaseTkStrikeMainController.this.btKyeShi.isDisabled()) {
              Platform.runLater(new Runnable() {
                    public void run() {
                      BaseTkStrikeMainController.this.btKyeShi.fire();
                    }
                  });
            } else if (TkStrikeKeyCombinationsHelper.keyCombDoctor.match(event) && !BaseTkStrikeMainController.this.btDoctor.isDisabled()) {
              if (BaseTkStrikeMainController.this.getMatchWorker().isDoctorInRound()) {
                BaseTkStrikeMainController.this.getMatchWorker().doctorQuitInRound();
              } else {
                BaseTkStrikeMainController.this.getMatchWorker().callDoctorInRound();
              } 
            } else if (TkStrikeKeyCombinationsHelper.keyCombAddGamJeom2Blue.match(event) && 
              !BaseTkStrikeMainController.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) && 
              !BaseTkStrikeMainController.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT)) {
              BaseTkStrikeMainController.this.addGamJeom2Blue();
            } else if (TkStrikeKeyCombinationsHelper.keyCombRemoveGamJeom2Blue.match(event) && 
              !BaseTkStrikeMainController.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) && 
              !BaseTkStrikeMainController.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT)) {
              BaseTkStrikeMainController.this.removeGamJeom2Blue();
            } else if (TkStrikeKeyCombinationsHelper.keyCombAddGamJeom2Red.match(event) && 
              !BaseTkStrikeMainController.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) && 
              !BaseTkStrikeMainController.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT)) {
              BaseTkStrikeMainController.this.addGamJeom2Red();
            } else if (TkStrikeKeyCombinationsHelper.keyCombRemoveGamJeom2Red.match(event) && 
              !BaseTkStrikeMainController.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) && 
              !BaseTkStrikeMainController.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT)) {
              BaseTkStrikeMainController.this.removeGamJeom2Red();
            } else if (TkStrikeKeyCombinationsHelper.keyCombExternalScreen.match(event)) {
              Platform.runLater(new Runnable() {
                    public void run() {
                      BaseTkStrikeMainController.this.tgExternalScoreboard.fire();
                    }
                  });
            } else if (TkStrikeKeyCombinationsHelper.keyCombShowBackupSystem.match(event) && 
              !BaseTkStrikeMainController.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) && 
              !BaseTkStrikeMainController.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT)) {
              Platform.runLater(new Runnable() {
                    public void run() {
                      BaseTkStrikeMainController.this.tgBackupSystem.setVisible(!BaseTkStrikeMainController.this.tgBackupSystem.isVisible());
                    }
                  });
            } else if (TkStrikeKeyCombinationsHelper.keyCombViewDifferentialScore.match(event)) {
              BaseTkStrikeMainController.this.showInfoNotification(BaseTkStrikeMainController.this
                  .getMessage("message.info.currentDifferentialScore", new String[] { "" + this.this$0.getMatchWorker().getDifferentialScore() }));
            } else if (TkStrikeKeyCombinationsHelper.keyCombUndoPointGap.match(event)) {
              BaseTkStrikeMainController.this.getMatchWorker().disableDifferentialScore();
              BaseTkStrikeMainController.this.showInfoNotification(BaseTkStrikeMainController.this
                  .getMessage("message.info.currentDifferentialScore", new String[] { "" + this.this$0.getMatchWorker().getDifferentialScore() }));
            } else if (BaseTkStrikeMainController.this.allowGroupSelectionByColor.booleanValue() && TkStrikeKeyCombinationsHelper.keyCombColorGroupSelectionVisible.match(event)) {
              BaseTkStrikeMainController.this.getMatchWorker().doChangeTogglesColorGroupSelectionVisible();
            } else if (BaseTkStrikeMainController.this.allowGroupSelectionByColor.booleanValue() && TkStrikeKeyCombinationsHelper.keyCombSelectBlueG1.match(event)) {
              if (BaseTkStrikeMainController.this.isConfirmedToChangeColorGroup(true, Integer.valueOf(1)))
                BaseTkStrikeMainController.this.getMatchWorker().doChangeBlueGroupSelected(Integer.valueOf(1)); 
            } else if (BaseTkStrikeMainController.this.allowGroupSelectionByColor.booleanValue() && TkStrikeKeyCombinationsHelper.keyCombSelectBlueG2.match(event)) {
              if (BaseTkStrikeMainController.this.isConfirmedToChangeColorGroup(true, Integer.valueOf(2)))
                BaseTkStrikeMainController.this.getMatchWorker().doChangeBlueGroupSelected(Integer.valueOf(2)); 
            } else if (BaseTkStrikeMainController.this.allowGroupSelectionByColor.booleanValue() && TkStrikeKeyCombinationsHelper.keyCombSelectBlueG3.match(event)) {
              if (BaseTkStrikeMainController.this.isConfirmedToChangeColorGroup(true, Integer.valueOf(3)))
                BaseTkStrikeMainController.this.getMatchWorker().doChangeBlueGroupSelected(Integer.valueOf(3)); 
            } else if (BaseTkStrikeMainController.this.allowGroupSelectionByColor.booleanValue() && TkStrikeKeyCombinationsHelper.keyCombSelectBlueG4.match(event)) {
              if (BaseTkStrikeMainController.this.isConfirmedToChangeColorGroup(true, Integer.valueOf(4)))
                BaseTkStrikeMainController.this.getMatchWorker().doChangeBlueGroupSelected(Integer.valueOf(4)); 
            } else if (BaseTkStrikeMainController.this.allowGroupSelectionByColor.booleanValue() && TkStrikeKeyCombinationsHelper.keyCombSelectBlueG5.match(event)) {
              if (BaseTkStrikeMainController.this.isConfirmedToChangeColorGroup(true, Integer.valueOf(5)))
                BaseTkStrikeMainController.this.getMatchWorker().doChangeBlueGroupSelected(Integer.valueOf(5)); 
            } else if (BaseTkStrikeMainController.this.allowGroupSelectionByColor.booleanValue() && TkStrikeKeyCombinationsHelper.keyCombSelectBlueG6.match(event)) {
              if (BaseTkStrikeMainController.this.isConfirmedToChangeColorGroup(true, Integer.valueOf(6)))
                BaseTkStrikeMainController.this.getMatchWorker().doChangeBlueGroupSelected(Integer.valueOf(6)); 
            } else if (BaseTkStrikeMainController.this.allowGroupSelectionByColor.booleanValue() && TkStrikeKeyCombinationsHelper.keyCombSelectRedG1.match(event)) {
              if (BaseTkStrikeMainController.this.isConfirmedToChangeColorGroup(false, Integer.valueOf(1)))
                BaseTkStrikeMainController.this.getMatchWorker().doChangeRedGroupSelected(Integer.valueOf(1)); 
            } else if (BaseTkStrikeMainController.this.allowGroupSelectionByColor.booleanValue() && TkStrikeKeyCombinationsHelper.keyCombSelectRedG2.match(event)) {
              if (BaseTkStrikeMainController.this.isConfirmedToChangeColorGroup(false, Integer.valueOf(2)))
                BaseTkStrikeMainController.this.getMatchWorker().doChangeRedGroupSelected(Integer.valueOf(2)); 
            } else if (BaseTkStrikeMainController.this.allowGroupSelectionByColor.booleanValue() && TkStrikeKeyCombinationsHelper.keyCombSelectRedG3.match(event)) {
              if (BaseTkStrikeMainController.this.isConfirmedToChangeColorGroup(false, Integer.valueOf(3)))
                BaseTkStrikeMainController.this.getMatchWorker().doChangeRedGroupSelected(Integer.valueOf(3)); 
            } else if (BaseTkStrikeMainController.this.allowGroupSelectionByColor.booleanValue() && TkStrikeKeyCombinationsHelper.keyCombSelectRedG4.match(event)) {
              if (BaseTkStrikeMainController.this.isConfirmedToChangeColorGroup(false, Integer.valueOf(4)))
                BaseTkStrikeMainController.this.getMatchWorker().doChangeRedGroupSelected(Integer.valueOf(4)); 
            } else if (BaseTkStrikeMainController.this.allowGroupSelectionByColor.booleanValue() && TkStrikeKeyCombinationsHelper.keyCombSelectRedG5.match(event)) {
              if (BaseTkStrikeMainController.this.isConfirmedToChangeColorGroup(false, Integer.valueOf(5)))
                BaseTkStrikeMainController.this.getMatchWorker().doChangeRedGroupSelected(Integer.valueOf(5)); 
            } else if (BaseTkStrikeMainController.this.allowGroupSelectionByColor.booleanValue() && TkStrikeKeyCombinationsHelper.keyCombSelectRedG6.match(event) && 
              BaseTkStrikeMainController.this.isConfirmedToChangeColorGroup(false, Integer.valueOf(6))) {
              BaseTkStrikeMainController.this.getMatchWorker().doChangeRedGroupSelected(Integer.valueOf(6));
            } 
            if (!BaseTkStrikeMainController.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) && 
              !BaseTkStrikeMainController.this.getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT)) {
              IRulesEntry rulesEntry = BaseTkStrikeMainController.this.getAppStatusWorker().getRulesEntry();
              long currentRoundCountdown = BaseTkStrikeMainController.this.getMatchWorker().getCurrentRoundCountdownAsMillis();
              if (TkStrikeKeyCombinationsHelper.keyCombP1.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumP1.match(event)) {
                BaseTkStrikeMainController.this.getMatchWorker().addBlueBodyPoint(rulesEntry.getBodyPoints(), ActionSource.SCOREBOARD_EDITOR, 
                    
                    System.currentTimeMillis(), currentRoundCountdown, 0);
              } else if (TkStrikeKeyCombinationsHelper.keyCombM1.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumM1.match(event)) {
                BaseTkStrikeMainController.this.getMatchWorker().addBlueBodyPoint(rulesEntry.getBodyPoints() * -1, ActionSource.SCOREBOARD_EDITOR, 
                    
                    System.currentTimeMillis(), currentRoundCountdown, 0);
              } else if (TkStrikeKeyCombinationsHelper.keyCombP2.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumP2.match(event)) {
                BaseTkStrikeMainController.this.getMatchWorker().addBlueBodyTechPoint(rulesEntry.getBodyTechPoints(), ActionSource.SCOREBOARD_EDITOR, 
                    
                    System.currentTimeMillis(), currentRoundCountdown);
              } else if (TkStrikeKeyCombinationsHelper.keyCombM2.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumM2.match(event)) {
                BaseTkStrikeMainController.this.getMatchWorker().addBlueBodyTechPoint(rulesEntry.getBodyTechPoints() * -1, ActionSource.SCOREBOARD_EDITOR, 
                    
                    System.currentTimeMillis(), currentRoundCountdown);
              } else if (TkStrikeKeyCombinationsHelper.keyCombP3.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumP3.match(event)) {
                BaseTkStrikeMainController.this.getMatchWorker().addBlueHeadPoint(rulesEntry.getHeadPoints(), ActionSource.SCOREBOARD_EDITOR, 
                    
                    System.currentTimeMillis(), currentRoundCountdown, 0);
              } else if (TkStrikeKeyCombinationsHelper.keyCombM3.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumM3.match(event)) {
                BaseTkStrikeMainController.this.getMatchWorker().addBlueHeadPoint(rulesEntry.getHeadPoints() * -1, ActionSource.SCOREBOARD_EDITOR, 
                    
                    System.currentTimeMillis(), currentRoundCountdown, 0);
              } else if (TkStrikeKeyCombinationsHelper.keyCombP4.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumP4.match(event)) {
                BaseTkStrikeMainController.this.getMatchWorker().addBlueHeadTechPoint(rulesEntry.getHeadTechPoints(), ActionSource.SCOREBOARD_EDITOR, 
                    
                    System.currentTimeMillis(), currentRoundCountdown);
              } else if (TkStrikeKeyCombinationsHelper.keyCombM4.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumM4.match(event)) {
                BaseTkStrikeMainController.this.getMatchWorker().addBlueHeadTechPoint(rulesEntry.getHeadTechPoints() * -1, ActionSource.SCOREBOARD_EDITOR, 
                    
                    System.currentTimeMillis(), currentRoundCountdown);
              } else if (TkStrikeKeyCombinationsHelper.keyCombP5.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumP5.match(event)) {
                BaseTkStrikeMainController.this.getMatchWorker().addBluePunchPoint(rulesEntry.getPunchPoints(), ActionSource.SCOREBOARD_EDITOR, 
                    
                    System.currentTimeMillis(), currentRoundCountdown);
              } else if (TkStrikeKeyCombinationsHelper.keyCombM5.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumM5.match(event)) {
                BaseTkStrikeMainController.this.getMatchWorker().addBluePunchPoint(rulesEntry.getPunchPoints() * -1, ActionSource.SCOREBOARD_EDITOR, 
                    
                    System.currentTimeMillis(), currentRoundCountdown);
              } else if (TkStrikeKeyCombinationsHelper.keyCombP6.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumP6.match(event)) {
                BaseTkStrikeMainController.this.getMatchWorker().addRedBodyPoint(rulesEntry.getBodyPoints(), ActionSource.SCOREBOARD_EDITOR, 
                    
                    System.currentTimeMillis(), currentRoundCountdown, 0);
              } else if (TkStrikeKeyCombinationsHelper.keyCombM6.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumM6.match(event)) {
                BaseTkStrikeMainController.this.getMatchWorker().addRedBodyPoint(rulesEntry.getBodyPoints() * -1, ActionSource.SCOREBOARD_EDITOR, 
                    
                    System.currentTimeMillis(), currentRoundCountdown, 0);
              } else if (TkStrikeKeyCombinationsHelper.keyCombP7.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumP7.match(event)) {
                BaseTkStrikeMainController.this.getMatchWorker().addRedBodyTechPoint(rulesEntry.getBodyTechPoints(), ActionSource.SCOREBOARD_EDITOR, 
                    
                    System.currentTimeMillis(), currentRoundCountdown);
              } else if (TkStrikeKeyCombinationsHelper.keyCombM7.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumM7.match(event)) {
                BaseTkStrikeMainController.this.getMatchWorker().addRedBodyTechPoint(rulesEntry.getBodyTechPoints() * -1, ActionSource.SCOREBOARD_EDITOR, 
                    
                    System.currentTimeMillis(), currentRoundCountdown);
              } else if (TkStrikeKeyCombinationsHelper.keyCombP8.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumP8.match(event)) {
                BaseTkStrikeMainController.this.getMatchWorker().addRedHeadPoint(rulesEntry.getHeadPoints(), ActionSource.SCOREBOARD_EDITOR, 
                    
                    System.currentTimeMillis(), currentRoundCountdown, 0);
              } else if (TkStrikeKeyCombinationsHelper.keyCombM8.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumM8.match(event)) {
                BaseTkStrikeMainController.this.getMatchWorker().addRedHeadPoint(rulesEntry.getHeadPoints() * -1, ActionSource.SCOREBOARD_EDITOR, 
                    
                    System.currentTimeMillis(), currentRoundCountdown, 0);
              } else if (TkStrikeKeyCombinationsHelper.keyCombP9.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumP9.match(event)) {
                BaseTkStrikeMainController.this.getMatchWorker().addRedHeadTechPoint(rulesEntry.getHeadTechPoints(), ActionSource.SCOREBOARD_EDITOR, 
                    
                    System.currentTimeMillis(), currentRoundCountdown);
              } else if (TkStrikeKeyCombinationsHelper.keyCombM9.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumM9.match(event)) {
                BaseTkStrikeMainController.this.getMatchWorker().addRedHeadTechPoint(rulesEntry.getHeadTechPoints() * -1, ActionSource.SCOREBOARD_EDITOR, 
                    
                    System.currentTimeMillis(), currentRoundCountdown);
              } else if (TkStrikeKeyCombinationsHelper.keyCombP0.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumP0.match(event)) {
                BaseTkStrikeMainController.this.getMatchWorker().addRedPunchPoint(rulesEntry.getPunchPoints(), ActionSource.SCOREBOARD_EDITOR, 
                    
                    System.currentTimeMillis(), currentRoundCountdown);
              } else if (TkStrikeKeyCombinationsHelper.keyCombM0.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumM0.match(event)) {
                BaseTkStrikeMainController.this.getMatchWorker().addRedPunchPoint(rulesEntry.getPunchPoints() * -1, ActionSource.SCOREBOARD_EDITOR, 
                    
                    System.currentTimeMillis(), currentRoundCountdown);
              } 
            } 
          }
        });
    this.tgBackupSystem.setOnAction(new EventHandler<ActionEvent>() {
          public void handle(ActionEvent event) {
            if (!BaseTkStrikeMainController.this.showMonologConfirmDialog(BaseTkStrikeMainController.this.getMessage("title.default.question"), BaseTkStrikeMainController.this.getMessage("message.confirm.enableBackupSystem." + BaseTkStrikeMainController.this.tgBackupSystem.isSelected())).equals(MonologFXButton.Type.YES)) {
              BaseTkStrikeMainController.this.tgBackupSystem.setSelected(!BaseTkStrikeMainController.this.tgBackupSystem.isSelected());
            } else {
              BaseTkStrikeMainController.this.getMatchWorker().backupSystemEnabled().setValue(Boolean.valueOf(BaseTkStrikeMainController.this.tgBackupSystem.isSelected()));
            } 
          }
        });
    this.tgBackupSystem.selectedProperty().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue.booleanValue()) {
              BaseTkStrikeMainController.this.tgBackupSystem.setText(BaseTkStrikeMainController.this.getMessage("label.backupSystem.on"));
            } else {
              BaseTkStrikeMainController.this.tgBackupSystem.setText(BaseTkStrikeMainController.this.getMessage("label.backupSystem.off"));
            } 
          }
        });
    _afterPropertiesSet();
  }
  
  private boolean isConfirmedToChangeColorGroup(boolean blue, Integer groupNumber) {
    if (!getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) && 
      !getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT)) {
      Optional<T> opResponse = showConfirmDialog(
          getMessage("message.confirmDialog.title"), 
          getMessage("message.confirm.changeColorSensorGroup." + (blue ? "blue" : "red"), new String[] { "" + groupNumber }));
      return (opResponse != null && opResponse.isPresent() && opResponse.get().equals(ButtonType.OK));
    } 
    return false;
  }
  
  public void initialize(URL url, ResourceBundle resourceBundle) {
    this.btDoctor.setFocusTraversable(false);
    this.tgExternalScoreboard.selectedProperty().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
            if (t1.booleanValue()) {
              BaseTkStrikeMainController.this.showExternalScoreboard();
              BaseTkStrikeMainController.this.tgExternalScoreboard.setText(BaseTkStrikeMainController.this.getMessage("label.externalScoreboard.on"));
            } else {
              BaseTkStrikeMainController.this.hideExternalScoreboard();
              BaseTkStrikeMainController.this.tgExternalScoreboard.setText(BaseTkStrikeMainController.this.getMessage("label.externalScoreboard.off"));
            } 
          }
        });
  }
  
  private void addStatusCSSToLabel(Label label, String css2Add) {
    label.getStyleClass().removeAll((Object[])new String[] { "appStatus-no", "appStatus-warn", "appStatus-error", "appStatus-ok" });
    label.getStyleClass().addAll((Object[])new String[] { css2Add });
  }
  
  protected abstract void _afterPropertiesSet() throws Exception;
  
  protected abstract MW getMatchWorker();
  
  protected abstract MFRC getMatchFinalResultController();
  
  protected abstract RFC getRoundFinishedController();
  
  protected abstract void _openRingManager(CommonRingManagerController.RingManagerOpenType paramRingManagerOpenType);
  
  protected abstract void _openConfiguration();
  
  protected abstract void _openHardwareTest();
  
  protected abstract CommonExternalScreenMainController getExternalScreenMainController();
  
  protected enum KyeShiButtonUserData {
    RESUME, KYE_SHI, PAUSE_ROUND, END_KYE_SHI;
  }
  
  protected enum TimeoutButtonUserData {
    TIMEOUT, RESUME, CANCEL_PARA_TIMEOUT;
  }
}
