package com.xtremis.daedo.tkstrike.ui.controller;

import com.xtremis.daedo.tkstrike.ei.client.WtUDPService;
import com.xtremis.daedo.tkstrike.om.combat.MatchWinner;
import com.xtremis.daedo.tkstrike.orm.model.NetworkConfigurationEntity;
import com.xtremis.daedo.tkstrike.service.CommonMatchWorker;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchVictoryCriteria;
import com.xtremis.daedo.tkstrike.ui.model.INetworkConfigurationEntry;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class CommonRoundFinishedConfirmationController<NE extends INetworkConfigurationEntry<? extends NetworkConfigurationEntity>, MW extends CommonMatchWorker, SBE extends ScoreboardEditorController, MLW extends TkStrikeController> extends CommonTkStrikeBaseController<NE> {
  @Autowired
  protected WtUDPService wtUDPService;
  
  private final AtomicBoolean open = new AtomicBoolean(Boolean.FALSE.booleanValue());
  
  private final AtomicBoolean confirmRoundFinished = new AtomicBoolean(Boolean.TRUE.booleanValue());
  
  private final AtomicBoolean bestOf3RoundWithTie = new AtomicBoolean(Boolean.FALSE.booleanValue());
  
  @FXML
  private Pane pnMain;
  
  @FXML
  private ProgressIndicator pi;
  
  @FXML
  private Pane pnVideoReplay;
  
  @FXML
  protected Pane pnVideoBlue;
  
  @FXML
  protected ImageView ivVideoBlue;
  
  @FXML
  protected Pane pnVideoRed;
  
  @FXML
  protected ImageView ivVideoRed;
  
  @FXML
  protected Pane pnBestOf3Superiority;
  
  @FXML
  protected ToggleButton tgBestOf3ShowSuperiority;
  
  @FXML
  protected Pane pnBestOf3SelectWinner;
  
  @FXML
  protected ToggleButton tgBestOf3WinnerBlue;
  
  @FXML
  protected ToggleButton tgBestOf3WinnerRed;
  
  @FXML
  private Button btConfirm;
  
  private final ToggleGroup tgBestOf3RoundWinner = new ToggleGroup();
  
  public void openMatchLog() {
    openInNewStage((TkStrikeController)getMatchLogViewerController(), getMessage("title.window.matchLog"), 1200, 600);
  }
  
  public void openScoreboardEditor() {
    openInNewStage((TkStrikeController)getScoreboardEditorController(), new EventHandler<WindowEvent>() {
          public void handle(WindowEvent event) {
            Platform.runLater(new Runnable() {
                  public void run() {
                    CommonRoundFinishedConfirmationController.this.onWindowShowEvent();
                  }
                },  );
          }
        },  getMessage("title.window.scoreboardEditor"), 960, 560, false);
  }
  
  public void doConfirm() {
    doCloseThisStage();
  }
  
  public final EventHandler<WindowEvent> getOnWindowCloseEventHandler() {
    return new EventHandler<WindowEvent>() {
        public void handle(WindowEvent windowEvent) {
          CommonRoundFinishedConfirmationController.this.open.set(false);
          if (CommonRoundFinishedConfirmationController.this.confirmRoundFinished.get()) {
            if (CommonRoundFinishedConfirmationController.this.bestOf3RoundWithTie.get()) {
              CommonRoundFinishedConfirmationController.this.getMatchWorker().confirmRoundEndsWithWinner((MatchWinner)CommonRoundFinishedConfirmationController.this.tgBestOf3RoundWinner.getSelectedToggle().getUserData());
            } else {
              CommonRoundFinishedConfirmationController.this.getMatchWorker().confirmRoundEnds();
            } 
          } else {
            CommonTkStrikeBaseController.logger.info("Close without confirm roundFinished!");
          } 
        }
      };
  }
  
  public final void onWindowShowEvent() {
    super.onWindowShowEvent();
    this.open.set(true);
    this.btConfirm.setDisable(MatchWinner.TIE.equals(getMatchWorker().getRoundWinner(Integer.valueOf(getMatchWorker().getCurrentRound()))));
    this.confirmRoundFinished.set(true);
    this.pnBestOf3Superiority.setVisible(getMatchWorker().bestOf3RoundWithSuperiority().getValue().booleanValue());
    this.tgBestOf3ShowSuperiority.setSelected(false);
    this.bestOf3RoundWithTie.set((MatchVictoryCriteria.BESTOF3.equals(getMatchWorker().getMatchVictoryCriteria()) && MatchWinner.TIE
        .equals(getMatchWorker().getRoundWinner(Integer.valueOf(getMatchWorker().getCurrentRound())))));
    this.pnBestOf3SelectWinner.setVisible(this.bestOf3RoundWithTie.get());
    this.tgBestOf3RoundWinner.selectToggle(null);
    internalOnWindowShowEvent();
  }
  
  public void initialize(URL url, ResourceBundle resourceBundle) {
    this.pnVideoReplay.setVisible(showVideoReplay());
    if (showVideoReplay()) {
      this.ivVideoBlue.setImage(IMAGE_VIDEO);
      this.ivVideoRed.setImage(IMAGE_VIDEO);
    } 
  }
  
  public void afterPropertiesSet() throws Exception {
    internalAfterPropertiesSet();
    this.tgBestOf3RoundWinner.getToggles().addAll((Object[])new Toggle[] { (Toggle)this.tgBestOf3WinnerBlue, (Toggle)this.tgBestOf3WinnerRed });
    this.tgBestOf3WinnerBlue.setUserData(MatchWinner.BLUE);
    this.tgBestOf3WinnerRed.setUserData(MatchWinner.RED);
    this.tgBestOf3RoundWinner.selectToggle((Toggle)this.tgBestOf3WinnerBlue);
    this.tgBestOf3RoundWinner.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
          public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            CommonTkStrikeBaseController.logger.debug("tgBestOf3RoundWinner new toggle selected " + newValue);
            CommonRoundFinishedConfirmationController.this.btConfirm.setDisable(false);
            if (CommonRoundFinishedConfirmationController.this.pnBestOf3SelectWinner.isVisible()) {
              CommonRoundFinishedConfirmationController.this.getAppStatusWorker().bestOf3SuperiorityRoundWinnerProperty().setValue(MatchWinner.TIE);
              if (newValue == null) {
                CommonRoundFinishedConfirmationController.this.btConfirm.setDisable(true);
              } else if (!MatchWinner.TIE.equals(newValue.getUserData())) {
                CommonTkStrikeBaseController.logger.info("tgBestOf3RoundWinner changed to " + newValue.getUserData());
                CommonRoundFinishedConfirmationController.this.getAppStatusWorker().bestOf3SuperiorityRoundWinnerProperty().setValue(newValue.getUserData());
              } 
            } 
          }
        });
    this.tgBestOf3WinnerBlue.setOnAction(new EventHandler<ActionEvent>() {
          public void handle(ActionEvent event) {}
        });
    getMatchWorker().bestOf3RoundWithSuperiority().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, final Boolean newValue) {
            CommonTkStrikeBaseController.logger.info("bestOf3RoundWithSuperiority changes new value " + newValue);
            Platform.runLater(new Runnable() {
                  public void run() {
                    CommonRoundFinishedConfirmationController.this.pnBestOf3Superiority.setVisible(newValue.booleanValue());
                  }
                });
          }
        });
    getMatchWorker().bestOf3RoundSuperiorityOnScoreboard().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (CommonRoundFinishedConfirmationController.this.tgBestOf3ShowSuperiority.isSelected() && !newValue.booleanValue())
              CommonRoundFinishedConfirmationController.this.tgBestOf3ShowSuperiority.setSelected(false); 
          }
        });
    getMatchWorker().roundFinishedOpenProperty().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (!newValue.booleanValue() && CommonRoundFinishedConfirmationController.this.open.get()) {
              CommonTkStrikeBaseController.logger.info("roundFinishedOpenProperty changes to false and this form are showing... close it without confirmation!");
              CommonRoundFinishedConfirmationController.this.confirmRoundFinished.set(false);
              CommonRoundFinishedConfirmationController.this.doCloseThisStage();
            } 
          }
        });
    this.tgBestOf3ShowSuperiority.selectedProperty().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            CommonTkStrikeBaseController.logger.info("tgBestOf3ShowSuperiority selectedChanged " + newValue);
            String message = CommonRoundFinishedConfirmationController.this.getMessage("toggle.showSuperiority.disabled");
            if (newValue.booleanValue()) {
              CommonRoundFinishedConfirmationController.this.getMatchWorker().bestOf3ShowRoundSuperiorityOnScoreboard();
              message = CommonRoundFinishedConfirmationController.this.getMessage("toggle.showSuperiority.enabled");
            } else {
              CommonRoundFinishedConfirmationController.this.getMatchWorker().bestOf3HideRoundSuperiorityOnScoreboard();
            } 
            CommonRoundFinishedConfirmationController.this.tgBestOf3ShowSuperiority.setText(message);
          }
        });
  }
  
  abstract MW getMatchWorker();
  
  abstract SBE getScoreboardEditorController();
  
  abstract MLW getMatchLogViewerController();
  
  abstract boolean showVideoReplay();
  
  protected abstract void internalOnWindowShowEvent();
  
  protected abstract void internalAfterPropertiesSet();
}
