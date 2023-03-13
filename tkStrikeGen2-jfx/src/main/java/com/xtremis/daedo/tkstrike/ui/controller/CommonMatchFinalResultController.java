package com.xtremis.daedo.tkstrike.ui.controller;

import com.xtremis.daedo.tkstrike.om.MatchStatusId;
import com.xtremis.daedo.tkstrike.om.combat.FinalDecision;
import com.xtremis.daedo.tkstrike.om.combat.MatchWinner;
import com.xtremis.daedo.tkstrike.service.CommonMatchWorker;
import com.xtremis.daedo.tkstrike.ui.model.INetworkConfigurationEntry;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;
import javafx.stage.WindowEvent;
import jfxtras.labs.dialogs.MonologFXButton;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.springframework.beans.factory.annotation.Value;

public abstract class CommonMatchFinalResultController<NE extends INetworkConfigurationEntry, MW extends CommonMatchWorker, SBE extends ScoreboardEditorController, MLW extends TkStrikeController> extends CommonTkStrikeBaseController<NE> {
  @Value("${tkStrike.isTkStrikeKTAVersion}")
  private Boolean isTkStrikeKTAVersion;
  
  private Integer defaultWinnerByContainerVGap = Integer.valueOf(30);
  
  boolean closeWithButton = false;
  
  @FXML
  private Button btCancel;
  
  @FXML
  private Button btCancelGoldenPointHit;
  
  @FXML
  private ToggleButton tgShowSuperiority;
  
  private final ToggleGroup toggleGroupWinner = new ToggleGroup();
  
  @FXML
  private ToggleButton tgWinnerBLUE;
  
  @FXML
  private ToggleButton tgWinnerTIE;
  
  @FXML
  private ToggleButton tgWinnerRED;
  
  private final ToggleGroup toggleGroupFinalDecision = new ToggleGroup();
  
  @FXML
  private Label lblSelectedFinalDecision;
  
  @FXML
  private FlowPane pnWinnerByContainer;
  
  @FXML
  private ToggleButton tbFinalDecisionRSC;
  
  @FXML
  private ToggleButton tbFinalDecisionPTF;
  
  @FXML
  private ToggleButton tbFinalDecisionPTG;
  
  @FXML
  private ToggleButton tbFinalDecisionGDP;
  
  @FXML
  private ToggleButton tbFinalDecisionSUP;
  
  @FXML
  private ToggleButton tbFinalDecisionWDR;
  
  @FXML
  private ToggleButton tbFinalDecisionDSQ;
  
  @FXML
  private ToggleButton tbFinalDecisionPUN;
  
  @FXML
  private ToggleButton tbFinalDecisionDQB;
  
  private ToggleButton tbFinalDecisionBKO = new ToggleButton();
  
  private ToggleButton tbFinalDecisionHKO = new ToggleButton();
  
  private ToggleButton tbFinalDecisionPTS = new ToggleButton();
  
  private ToggleButton tbFinalDecisionIRM = new ToggleButton();
  
  private ToggleButton tbFinalDecisionCSC = new ToggleButton();
  
  @FXML
  private Pane pnExtraControls;
  
  private boolean opened;
  
  private boolean callConfirm;
  
  public boolean isOpened() {
    return this.opened;
  }
  
  public void doConfirm() {
    if (this.toggleGroupFinalDecision.getSelectedToggle() != null && this.toggleGroupWinner.getSelectedToggle() != null) {
      if (MatchWinner.TIE.equals(this.toggleGroupWinner.getSelectedToggle().getUserData())) {
        MutableBoolean confValidation = new MutableBoolean(false);
        Optional<ButtonType> confirmation = showAlertWithCheckbox(
            getMessage("title.default.question"), 
            getMessage("message.confirm.matchFinishedOnTie"), 
            getMessage("checkbox.confirm.matchFinishedOnTie"), p -> paramMutableBoolean.setValue(p.booleanValue()));
        if (!confirmation.isPresent() || !((ButtonType)confirmation.get()).equals(ButtonType.YES) || 
          !confValidation.booleanValue())
          return; 
      } 
      this.callConfirm = true;
      if (getMatchWorker().superiorityByRoundsProperty().get() && MatchWinner.TIE.equals(getMatchWorker().getMatchWinner()))
        getMatchWorker().superiorityByRoundsProperty().set(false); 
      getMatchWorker().confirmFinalResult((MatchWinner)this.toggleGroupWinner.getSelectedToggle().getUserData(), (FinalDecision)this.toggleGroupFinalDecision
          .getSelectedToggle().getUserData());
      if (getMatchWorker().isGoldenPointTieBreaker() && !this.tgShowSuperiority.isSelected())
        getMatchWorker().doShowGoldenPointTieBreakerOnScoreboard(true); 
      this.closeWithButton = true;
      doCloseThisStage();
    } 
  }
  
  public void doCancel() {
    if (MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION.equals(getMatchWorker().getCurrentMatchStatus()) && FinalDecision.PTG
      .equals(getMatchWorker().getMatchFinalDecision()))
      if (showMonologConfirmDialog(getMessage("title.default.question"), 
          getMessage("message.confirm.cancelPointGapVictory")).equals(MonologFXButton.Type.YES)) {
        getMatchWorker().cancelVictoryByPointGap();
      } else {
        return;
      }  
    this.closeWithButton = true;
    doCloseThisStage();
  }
  
  public void doCancelGoldenPointHit() {
    getMatchWorker().cancelGoldenPointHit();
    this.closeWithButton = true;
    doCloseThisStage();
  }
  
  public void openScoreboardEditor() {
    openInNewStage((TkStrikeController)getScoreboardEditorController(), new EventHandler<WindowEvent>() {
          public void handle(WindowEvent event) {
            if (event.getEventType().equals(WindowEvent.WINDOW_CLOSE_REQUEST) && 
              CommonMatchFinalResultController.this.getScoreboardEditorController().isChangesApplied()) {
              CommonTkStrikeBaseController.logger.debug("Scoreboard editor closed and Changes Applied!!!");
              CommonMatchFinalResultController.this.callConfirm = false;
              CommonMatchFinalResultController.this.closeWithButton = true;
              CommonMatchFinalResultController.this.getMatchWorker().doShowGoldenPointTieBreakerOnScoreboard(false);
              CommonMatchFinalResultController.this.doCloseThisStage();
            } 
          }
        },  getMessage("title.window.scoreboardEditor"), 960, 540, true);
  }
  
  public void openMatchLog() {
    openInNewStage((TkStrikeController)getMatchLogViewerController(), getMessage("title.window.matchLog"), 1200, 600);
  }
  
  public EventHandler<WindowEvent> getOnWindowCloseEventHandler() {
    return new EventHandler<WindowEvent>() {
        public void handle(WindowEvent windowEvent) {
          CommonMatchFinalResultController.this.opened = false;
          if (!CommonMatchFinalResultController.this.callConfirm && CommonMatchFinalResultController.this.closeWithButton && MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION
            
            .equals(CommonMatchFinalResultController.this.getMatchWorker().getCurrentMatchStatus()) && CommonMatchFinalResultController.this
            .getMatchWorker().isChangeMatchStatusToTimeoutOnScoreboardChanges()) {
            CommonMatchFinalResultController.this.getMatchWorker().confirmFinalResult((MatchWinner)CommonMatchFinalResultController.this.toggleGroupWinner.getSelectedToggle().getUserData(), 
                (FinalDecision)CommonMatchFinalResultController.this.toggleGroupFinalDecision.getSelectedToggle().getUserData());
          } else if (!CommonMatchFinalResultController.this.callConfirm || !CommonMatchFinalResultController.this.closeWithButton) {
            CommonMatchFinalResultController.this.getMatchWorker().undoFinalResult();
          } 
        }
      };
  }
  
  public void onWindowShowEvent() {
    super.onWindowShowEvent();
    getMatchWorker().finalResultOpenProperty().set(true);
    this.opened = true;
    this.callConfirm = false;
    this.closeWithButton = false;
    if (MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION.equals(getMatchWorker().getCurrentMatchStatus())) {
      this.btCancel.setVisible(FinalDecision.PTG.equals(getMatchWorker().getMatchFinalDecision()));
      this.pnExtraControls.setVisible(true);
      this.btCancelGoldenPointHit.setVisible((getMatchWorker().isGoldenPointWorking() && !getMatchWorker().isGoldenPointTieBreaker()));
      this.tgShowSuperiority.setVisible(((getMatchWorker().isGoldenPointWorking() && getMatchWorker().isGoldenPointTieBreaker()) || getMatchWorker().superiorityByRoundsProperty().get()));
      this.tgShowSuperiority.setSelected((getMatchWorker().showGoldenPointTieBreakerOnScoreboard().get() || getMatchWorker().showSuperiorityByRoundsProperty().get()));
    } else {
      this.btCancel.setVisible(true);
      this.pnExtraControls.setVisible(false);
      this.tgShowSuperiority.setVisible((getMatchWorker().isGoldenPointWorking() && getMatchWorker().isGoldenPointTieBreaker()));
      this.tgShowSuperiority.setSelected(getMatchWorker().showGoldenPointTieBreakerOnScoreboard().get());
    } 
    MatchWinner matchWinner = getMatchWorker().getMatchWinner();
    if (matchWinner != null)
      switch (matchWinner) {
        case RSC:
          this.tgWinnerBLUE.setSelected(true);
          break;
        case PTF:
          this.tgWinnerTIE.setSelected(true);
          break;
        case PTG:
          this.tgWinnerRED.setSelected(true);
          break;
      }  
    FinalDecision finalDecision = getMatchWorker().getMatchFinalDecision();
    if (finalDecision != null)
      switch (finalDecision) {
        case RSC:
          this.tbFinalDecisionRSC.setSelected(true);
          break;
        case PTF:
          this.tbFinalDecisionPTF.setSelected(true);
          break;
        case PTG:
          this.tbFinalDecisionPTG.setSelected(true);
          break;
        case GDP:
          this.tbFinalDecisionGDP.setSelected(true);
          break;
        case SUP:
          this.tbFinalDecisionSUP.setSelected(true);
          break;
        case WDR:
          this.tbFinalDecisionWDR.setSelected(true);
          break;
        case DSQ:
          this.tbFinalDecisionDSQ.setSelected(true);
          break;
        case PUN:
          this.tbFinalDecisionPUN.setSelected(true);
          break;
        case DQB:
          this.tbFinalDecisionDQB.setSelected(true);
          break;
      }  
    this.toggleGroupFinalDecision.getToggles().removeAll((Object[])new Toggle[] { (Toggle)this.tbFinalDecisionPTS, (Toggle)this.tbFinalDecisionIRM, (Toggle)this.tbFinalDecisionCSC });
    this.pnWinnerByContainer.getChildren().removeAll((Object[])new Node[] { (Node)this.tbFinalDecisionPTS, (Node)this.tbFinalDecisionIRM, (Node)this.tbFinalDecisionCSC });
    this.pnWinnerByContainer.setVgap(this.defaultWinnerByContainerVGap.intValue());
    if (getMatchWorker().isParaTkdMatch()) {
      this.toggleGroupFinalDecision.getToggles().addAll((Object[])new Toggle[] { (Toggle)this.tbFinalDecisionPTS, (Toggle)this.tbFinalDecisionIRM, (Toggle)this.tbFinalDecisionCSC });
      this.pnWinnerByContainer.getChildren().addAll((Object[])new Node[] { (Node)this.tbFinalDecisionPTS, (Node)this.tbFinalDecisionIRM, (Node)this.tbFinalDecisionCSC });
      this.pnWinnerByContainer.setVgap(5.0D);
    } 
  }
  
  public final void afterPropertiesSet() throws Exception {
    this.toggleGroupWinner.getToggles().addAll((Object[])new Toggle[] { (Toggle)this.tgWinnerBLUE, (Toggle)this.tgWinnerTIE, (Toggle)this.tgWinnerRED });
    this.tgWinnerBLUE.setText(getMessage("label.matchWinner.BLUE"));
    this.tgWinnerBLUE.setUserData(MatchWinner.BLUE);
    this.tgWinnerTIE.setText(getMessage("label.matchWinner.TIE"));
    this.tgWinnerTIE.setUserData(MatchWinner.TIE);
    this.tgWinnerRED.setText(getMessage("label.matchWinner.RED"));
    this.tgWinnerRED.setUserData(MatchWinner.RED);
    this.toggleGroupFinalDecision.getToggles().addAll((Object[])new Toggle[] { (Toggle)this.tbFinalDecisionRSC, (Toggle)this.tbFinalDecisionPTF, (Toggle)this.tbFinalDecisionPTG, (Toggle)this.tbFinalDecisionGDP, (Toggle)this.tbFinalDecisionSUP, (Toggle)this.tbFinalDecisionWDR, (Toggle)this.tbFinalDecisionDSQ, (Toggle)this.tbFinalDecisionDQB, (Toggle)this.tbFinalDecisionPUN });
    if (this.isTkStrikeKTAVersion.booleanValue()) {
      this.defaultWinnerByContainerVGap = Integer.valueOf(5);
      this.toggleGroupFinalDecision.getToggles().addAll((Object[])new Toggle[] { (Toggle)this.tbFinalDecisionBKO, (Toggle)this.tbFinalDecisionHKO });
      this.pnWinnerByContainer.getChildren().addAll((Object[])new Node[] { (Node)this.tbFinalDecisionBKO, (Node)this.tbFinalDecisionHKO });
    } 
    this.pnWinnerByContainer.setVgap(this.defaultWinnerByContainerVGap.intValue());
    this.toggleGroupFinalDecision.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
          public void changed(ObservableValue<? extends Toggle> observableValue, Toggle toggle, Toggle newToggle) {
            if (newToggle != null) {
              final FinalDecision finalDecision = (FinalDecision)newToggle.getUserData();
              if (finalDecision != null)
                Platform.runLater(new Runnable() {
                      public void run() {
                        CommonMatchFinalResultController.this.lblSelectedFinalDecision.setText(finalDecision.toString() + " - " + CommonMatchFinalResultController.this.getMessage("label.finalDecision." + finalDecision.toString()));
                      }
                    }); 
            } 
          }
        });
    this.tbFinalDecisionRSC.setText(getMessage("label.finalDecision.RSC") + "\n" + getMessage("label.finalDecision.RSC.text"));
    this.tbFinalDecisionPTF.setText(getMessage("label.finalDecision.PTF") + "\n" + getMessage("label.finalDecision.PTF.text"));
    this.tbFinalDecisionPTG.setText(getMessage("label.finalDecision.PTG") + "\n" + getMessage("label.finalDecision.PTG.text"));
    this.tbFinalDecisionGDP.setText(getMessage("label.finalDecision.GDP") + "\n" + getMessage("label.finalDecision.GDP.text"));
    this.tbFinalDecisionSUP.setText(getMessage("label.finalDecision.SUP") + "\n" + getMessage("label.finalDecision.SUP.text"));
    this.tbFinalDecisionWDR.setText(getMessage("label.finalDecision.WDR") + "\n" + getMessage("label.finalDecision.WDR.text"));
    this.tbFinalDecisionDSQ.setText(getMessage("label.finalDecision.DSQ") + "\n" + getMessage("label.finalDecision.DSQ.text"));
    this.tbFinalDecisionPUN.setText(getMessage("label.finalDecision.PUN") + "\n" + getMessage("label.finalDecision.PUN.text"));
    this.tbFinalDecisionDQB.setText(getMessage("label.finalDecision.DQB") + "\n" + getMessage("label.finalDecision.DQB.text"));
    this.tbFinalDecisionRSC.setUserData(FinalDecision.RSC);
    this.tbFinalDecisionPTF.setUserData(FinalDecision.PTF);
    this.tbFinalDecisionPTG.setUserData(FinalDecision.PTG);
    this.tbFinalDecisionGDP.setUserData(FinalDecision.GDP);
    this.tbFinalDecisionSUP.setUserData(FinalDecision.SUP);
    this.tbFinalDecisionWDR.setUserData(FinalDecision.WDR);
    this.tbFinalDecisionDSQ.setUserData(FinalDecision.DSQ);
    this.tbFinalDecisionPUN.setUserData(FinalDecision.PUN);
    this.tbFinalDecisionDQB.setUserData(FinalDecision.DQB);
    this.tbFinalDecisionBKO.setText(getMessage("label.finalDecision.BKO") + "\n" + getMessage("label.finalDecision.BKO.text"));
    this.tbFinalDecisionBKO.setUserData(FinalDecision.BKO);
    this.tbFinalDecisionBKO.getStyleClass().addAll((Object[])new String[] { "fd-finalDecision-toggle" });
    this.tbFinalDecisionBKO.setAlignment(Pos.CENTER);
    this.tbFinalDecisionBKO.setTextAlignment(TextAlignment.CENTER);
    this.tbFinalDecisionBKO.setWrapText(true);
    this.tbFinalDecisionBKO.setMinHeight(85.0D);
    this.tbFinalDecisionBKO.setMaxHeight(85.0D);
    this.tbFinalDecisionBKO.setPrefHeight(85.0D);
    this.tbFinalDecisionBKO.setMinWidth(235.0D);
    this.tbFinalDecisionBKO.setMaxWidth(235.0D);
    this.tbFinalDecisionBKO.setPrefWidth(235.0D);
    this.tbFinalDecisionHKO.setText(getMessage("label.finalDecision.HKO") + "\n" + getMessage("label.finalDecision.HKO.text"));
    this.tbFinalDecisionHKO.setUserData(FinalDecision.HKO);
    this.tbFinalDecisionHKO.getStyleClass().addAll((Object[])new String[] { "fd-finalDecision-toggle" });
    this.tbFinalDecisionHKO.setAlignment(Pos.CENTER);
    this.tbFinalDecisionHKO.setTextAlignment(TextAlignment.CENTER);
    this.tbFinalDecisionHKO.setWrapText(true);
    this.tbFinalDecisionHKO.setMinHeight(85.0D);
    this.tbFinalDecisionHKO.setMaxHeight(85.0D);
    this.tbFinalDecisionHKO.setPrefHeight(85.0D);
    this.tbFinalDecisionHKO.setMinWidth(235.0D);
    this.tbFinalDecisionHKO.setMaxWidth(235.0D);
    this.tbFinalDecisionHKO.setPrefWidth(235.0D);
    this.tbFinalDecisionPTS.setText(getMessage("label.finalDecision.PTS") + "\n" + getMessage("label.finalDecision.PTS.text"));
    this.tbFinalDecisionPTS.setUserData(FinalDecision.PTS);
    this.tbFinalDecisionPTS.getStyleClass().addAll((Object[])new String[] { "fd-finalDecision-toggle" });
    this.tbFinalDecisionPTS.setAlignment(Pos.CENTER);
    this.tbFinalDecisionPTS.setTextAlignment(TextAlignment.CENTER);
    this.tbFinalDecisionPTS.setWrapText(true);
    this.tbFinalDecisionPTS.setMinHeight(85.0D);
    this.tbFinalDecisionPTS.setMaxHeight(85.0D);
    this.tbFinalDecisionPTS.setPrefHeight(85.0D);
    this.tbFinalDecisionPTS.setMinWidth(235.0D);
    this.tbFinalDecisionPTS.setMaxWidth(235.0D);
    this.tbFinalDecisionPTS.setPrefWidth(235.0D);
    this.tbFinalDecisionIRM.setText(getMessage("label.finalDecision.IRM") + "\n" + getMessage("label.finalDecision.IRM.text"));
    this.tbFinalDecisionIRM.setUserData(FinalDecision.IRM);
    this.tbFinalDecisionIRM.getStyleClass().addAll((Object[])new String[] { "fd-finalDecision-toggle" });
    this.tbFinalDecisionIRM.setAlignment(Pos.CENTER);
    this.tbFinalDecisionIRM.setTextAlignment(TextAlignment.CENTER);
    this.tbFinalDecisionIRM.setWrapText(true);
    this.tbFinalDecisionIRM.setMinHeight(85.0D);
    this.tbFinalDecisionIRM.setMaxHeight(85.0D);
    this.tbFinalDecisionIRM.setPrefHeight(85.0D);
    this.tbFinalDecisionIRM.setMinWidth(235.0D);
    this.tbFinalDecisionIRM.setMaxWidth(235.0D);
    this.tbFinalDecisionIRM.setPrefWidth(235.0D);
    this.tbFinalDecisionCSC.setText(getMessage("label.finalDecision.CSC") + "\n" + getMessage("label.finalDecision.CSC.text"));
    this.tbFinalDecisionCSC.setUserData(FinalDecision.CSC);
    this.tbFinalDecisionCSC.getStyleClass().addAll((Object[])new String[] { "fd-finalDecision-toggle" });
    this.tbFinalDecisionCSC.setAlignment(Pos.CENTER);
    this.tbFinalDecisionCSC.setTextAlignment(TextAlignment.CENTER);
    this.tbFinalDecisionCSC.setWrapText(true);
    this.tbFinalDecisionCSC.setMinHeight(85.0D);
    this.tbFinalDecisionCSC.setMaxHeight(85.0D);
    this.tbFinalDecisionCSC.setPrefHeight(85.0D);
    this.tbFinalDecisionCSC.setMinWidth(235.0D);
    this.tbFinalDecisionCSC.setMaxWidth(235.0D);
    this.tbFinalDecisionCSC.setPrefWidth(235.0D);
    this.tgShowSuperiority.selectedProperty().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue.booleanValue()) {
              CommonMatchFinalResultController.this.tgShowSuperiority.setText(CommonMatchFinalResultController.this.getMessage("toggle.showSuperiority.enabled"));
            } else {
              CommonMatchFinalResultController.this.tgShowSuperiority.setText(CommonMatchFinalResultController.this.getMessage("toggle.showSuperiority.disabled"));
            } 
          }
        });
    this.tgShowSuperiority.setOnAction(new EventHandler<ActionEvent>() {
          public void handle(ActionEvent event) {
            boolean isSelected = CommonMatchFinalResultController.this.tgShowSuperiority.isSelected();
            if (CommonMatchFinalResultController.this.getMatchWorker().isGoldenPointWorking() && CommonMatchFinalResultController.this.getMatchWorker().isGoldenPointTieBreaker()) {
              CommonMatchFinalResultController.this.getMatchWorker().doShowGoldenPointTieBreakerOnScoreboard(isSelected);
            } else if (CommonMatchFinalResultController.this.getMatchWorker().superiorityByRoundsProperty().get()) {
              CommonMatchFinalResultController.this.getMatchWorker().showSuperiorityByRoundsProperty().set(isSelected);
            } 
          }
        });
    _afterPropertiesSet();
  }
  
  abstract MW getMatchWorker();
  
  abstract SBE getScoreboardEditorController();
  
  abstract MLW getMatchLogViewerController();
  
  abstract void _afterPropertiesSet() throws Exception;
}
