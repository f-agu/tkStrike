package com.xtremis.daedo.tkstrike.ui.controller;

import com.xtremis.daedo.tkstrike.om.MatchStatusId;
import com.xtremis.daedo.tkstrike.service.CommonMatchWorker;
import com.xtremis.daedo.tkstrike.service.MatchWorker;
import com.xtremis.daedo.tkstrike.ui.model.MatchConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.NetworkConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.RulesEntry;
import com.xtremis.daedo.tkstrike.ui.model.ScoreboardAction;
import com.xtremis.daedo.tkstrike.ui.model.ScoreboardEditAction;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ScoreboardEditorControllerImpl extends CommonScoreboardEditorController<NetworkConfigurationEntry, MatchWorker> implements ScoreboardEditorController {
  @FXML
  private Pane pnRoundSelection;
  
  @FXML
  private Label lblVideoRequested;
  
  @FXML
  private Label lblBlueBodyTech;
  
  @FXML
  private Pane pnBlueBodyTech;
  
  @FXML
  private Pane pnBlueTurning;
  
  @FXML
  private Label lblBlueHead;
  
  @FXML
  private Pane pnBlueHead;
  
  @FXML
  private Pane pnBlueSpinning;
  
  @FXML
  private Label lblBlueHeadTech;
  
  @FXML
  private Pane pnBlueHeadTech;
  
  @FXML
  private Label lblBluePunch;
  
  @FXML
  private Pane pnBluePunch;
  
  @FXML
  private Label lblRedBodyTech;
  
  @FXML
  private Pane pnRedBodyTech;
  
  @FXML
  private Pane pnRedTurning;
  
  @FXML
  private Label lblRedHead;
  
  @FXML
  private Pane pnRedHead;
  
  @FXML
  private Pane pnRedSpinning;
  
  @FXML
  private Label lblRedHeadTech;
  
  @FXML
  private Pane pnRedHeadTech;
  
  @FXML
  private Label lblRedPunch;
  
  @FXML
  private Pane pnRedPunch;
  
  @FXML
  private Button btPARAResetTOBlue;
  
  @FXML
  private Button btPARAResetTORed;
  
  @Autowired
  private MatchWorker matchWorker;
  
  @Value("${tkStrike.rejectVideoReplayWhenCloseWindow}")
  private Boolean rejectVideoReplayWhenCloseWindow;
  
  private boolean blueVideoRequested = false;
  
  private boolean redVideoRequested = false;
  
  final ToggleGroup toggleGroup = new ToggleGroup();
  
  private SimpleIntegerProperty round = new SimpleIntegerProperty(this, "round", 1);
  
  private boolean roundTimeChanged = false;
  
  private boolean roundChanged = false;
  
  private RulesEntry rulesEntry;
  
  public void addBlueTurningKickPoint() {
    this.scoreboardEditActions.add(new ScoreboardEditAction(true, ScoreboardAction.BODY_TECH_POINT, this.rulesEntry.getParaTurningKickPoints()));
    this.bluePoints.set(this.bluePoints.add(this.rulesEntry.getParaTurningKickPoints()).get());
  }
  
  public void removeBlueTurningKickPoint() {
    this.scoreboardEditActions.add(new ScoreboardEditAction(true, ScoreboardAction.BODY_TECH_POINT, this.rulesEntry.getParaTurningKickPoints() * -1));
    this.bluePoints.set(this.bluePoints.subtract(this.rulesEntry.getParaTurningKickPoints()).get());
  }
  
  public void addRedTurningKickPoint() {
    this.scoreboardEditActions.add(new ScoreboardEditAction(false, ScoreboardAction.BODY_TECH_POINT, this.rulesEntry.getParaTurningKickPoints()));
    this.redPoints.set(this.redPoints.add(this.rulesEntry.getParaTurningKickPoints()).get());
  }
  
  public void removeRedTurningKickPoint() {
    this.scoreboardEditActions.add(new ScoreboardEditAction(false, ScoreboardAction.BODY_TECH_POINT, this.rulesEntry.getParaTurningKickPoints() * -1));
    this.redPoints.set(this.redPoints.subtract(this.rulesEntry.getParaTurningKickPoints()).get());
  }
  
  public void addBlueSpinningKickPoint() {
    this.scoreboardEditActions.add(new ScoreboardEditAction(true, ScoreboardAction.BODY_TECH_POINT, this.rulesEntry.getParaSpinningKickPoints()));
    this.bluePoints.set(this.bluePoints.add(this.rulesEntry.getParaSpinningKickPoints()).get());
  }
  
  public void removeBlueSpinningKickPoint() {
    this.scoreboardEditActions.add(new ScoreboardEditAction(true, ScoreboardAction.BODY_TECH_POINT, this.rulesEntry.getParaSpinningKickPoints() * -1));
    this.bluePoints.set(this.bluePoints.subtract(this.rulesEntry.getParaSpinningKickPoints()).get());
  }
  
  public void addRedSpinningKickPoint() {
    this.scoreboardEditActions.add(new ScoreboardEditAction(false, ScoreboardAction.BODY_TECH_POINT, this.rulesEntry.getParaSpinningKickPoints()));
    this.redPoints.set(this.redPoints.add(this.rulesEntry.getParaSpinningKickPoints()).get());
  }
  
  public void removeRedSpinningKickPoint() {
    this.scoreboardEditActions.add(new ScoreboardEditAction(false, ScoreboardAction.BODY_TECH_POINT, this.rulesEntry.getParaSpinningKickPoints() * -1));
    this.redPoints.set(this.redPoints.subtract(this.rulesEntry.getParaSpinningKickPoints()).get());
  }
  
  void _doBeforeOnWindowCloseEventHandler() {
    if (!this.closeWithButton) {
      if (this.blueVideoRequested || this.redVideoRequested)
        if (this.blueVideoRequested) {
          this.matchWorker.blueVideoRequestResult().setValue(MatchWorker.VideoRequestResult.CLOSED);
        } else {
          this.matchWorker.redVideoRequestResult().setValue(MatchWorker.VideoRequestResult.CLOSED);
        }  
    } else {
      validateChangesAppliedIfVideoRequested();
    } 
    this.blueVideoRequested = false;
    this.redVideoRequested = false;
    this.matchWorker.blueRequestVideoScoreboardProperty().set(false);
    this.matchWorker.redRequestVideoScoreboardProperty().set(false);
  }
  
  void _doAfterOnWindowCloseEventHandler() {}
  
  void _doBeforeOnWindowShowEvent() {
    this.matchWorker.blueVideoRequestResult().setValue(null);
    this.matchWorker.redVideoRequestResult().setValue(null);
    if (this.blueVideoRequested) {
      this.lblVideoRequested.setText(getMessage("message.videoRequestedBy", new String[] { getMessage("label.blue") }));
      this.matchWorker.blueRequestVideoScoreboardProperty().set(true);
    } else if (this.redVideoRequested) {
      this.lblVideoRequested.setText(getMessage("message.videoRequestedBy", new String[] { getMessage("label.red") }));
      this.matchWorker.redRequestVideoScoreboardProperty().set(true);
    } 
    this.lblVideoRequested.setVisible((this.blueVideoRequested || this.redVideoRequested));
    this.roundTimeChanged = false;
    this.roundChanged = false;
  }
  
  void _doAfterOnWindowShowEvent() {
    this.rulesEntry = (RulesEntry)getAppStatusWorker().getRulesEntry();
    MatchConfigurationEntry matchConfigurationEntry = (MatchConfigurationEntry)getAppStatusWorker().getMatchConfigurationEntry();
    if (matchConfigurationEntry.isParaTkdMatch()) {
      this.lblBlueBodyTech.setText(getMessage("label.paraTurningKickPoints"));
      this.lblRedBodyTech.setText(getMessage("label.paraTurningKickPoints"));
      this.lblBlueHead.setText(getMessage("label.paraSpinningKickPoints"));
      this.lblRedHead.setText(getMessage("label.paraSpinningKickPoints"));
    } else {
      this.lblBlueBodyTech.setText(getMessage("label.main.bodyTech"));
      this.lblRedBodyTech.setText(getMessage("label.main.bodyTech"));
      this.lblBlueHead.setText(getMessage("label.main.head"));
      this.lblRedHead.setText(getMessage("label.main.head"));
    } 
    this.pnBlueBodyTech.setVisible(!matchConfigurationEntry.isParaTkdMatch());
    this.pnBlueTurning.setVisible(matchConfigurationEntry.isParaTkdMatch());
    this.pnRedBodyTech.setVisible(!matchConfigurationEntry.isParaTkdMatch());
    this.pnRedTurning.setVisible(matchConfigurationEntry.isParaTkdMatch());
    this.pnBlueHead.setVisible(!matchConfigurationEntry.isParaTkdMatch());
    this.pnBlueSpinning.setVisible(matchConfigurationEntry.isParaTkdMatch());
    this.pnRedHead.setVisible(!matchConfigurationEntry.isParaTkdMatch());
    this.pnRedSpinning.setVisible(matchConfigurationEntry.isParaTkdMatch());
    this.lblBlueHeadTech.setVisible(!matchConfigurationEntry.isParaTkdMatch());
    this.lblRedHeadTech.setVisible(!matchConfigurationEntry.isParaTkdMatch());
    this.lblBluePunch.setVisible(!matchConfigurationEntry.isParaTkdMatch());
    this.lblRedPunch.setVisible(!matchConfigurationEntry.isParaTkdMatch());
    this.pnBlueHeadTech.setVisible(!matchConfigurationEntry.isParaTkdMatch());
    this.pnBluePunch.setVisible(!matchConfigurationEntry.isParaTkdMatch());
    this.pnRedHeadTech.setVisible(!matchConfigurationEntry.isParaTkdMatch());
    this.pnRedPunch.setVisible(!matchConfigurationEntry.isParaTkdMatch());
    this.btPARAResetTOBlue.setVisible((matchConfigurationEntry.isParaTkdMatch() && !getMatchWorker().bluePARATimeOutQuotaProperty().getValue().booleanValue()));
    this.btPARAResetTORed.setVisible((matchConfigurationEntry.isParaTkdMatch() && !getMatchWorker().redPARATimeOutQuotaProperty().getValue().booleanValue()));
  }
  
  boolean _applyChangesOnRoundIfNeed() {
    Integer newRound = null;
    Integer newRoundTimeMinutes = null;
    Integer newRoundTimeSeconds = null;
    Integer newRoundTimeCentiseconds = null;
    if (this.roundChanged)
      newRound = Integer.valueOf(this.round.get()); 
    if (this.roundTimeChanged) {
      newRoundTimeMinutes = Integer.valueOf(this.roundTimeMinutes.get());
      newRoundTimeSeconds = Integer.valueOf(this.roundTimeSeconds.get());
      if (this.txtRoundCentiseconds.isVisible())
        newRoundTimeCentiseconds = Integer.valueOf(this.roundTimeCentiseconds.get()); 
    } 
    if (this.roundChanged || this.roundTimeChanged) {
      this.matchWorker.changeCurrentRoundAndTime(newRound, newRoundTimeMinutes, newRoundTimeSeconds, newRoundTimeCentiseconds);
      return true;
    } 
    return false;
  }
  
  private void validateChangesAppliedIfVideoRequested() {
    if (this.blueVideoRequested || this.redVideoRequested)
      if (this.blueVideoRequested) {
        this.matchWorker.blueVideoRequestResult().setValue(this.changesApplied ? MatchWorker.VideoRequestResult.ACCEPTED : MatchWorker.VideoRequestResult.REJECTED);
      } else {
        this.matchWorker.redVideoRequestResult().setValue(this.changesApplied ? MatchWorker.VideoRequestResult.ACCEPTED : MatchWorker.VideoRequestResult.REJECTED);
      }  
    if ((this.blueVideoRequested || this.redVideoRequested) && !this.changesApplied)
      if (this.blueVideoRequested) {
        showMonologInfoDialog(getMessage("title.default.info"), getMessage("message.removeVideoQuota", new String[] { getMessage("label.blue") }));
        int newVideoQuota = this.matchWorker.getBlueVideoQuota();
        if (newVideoQuota > 0) {
          newVideoQuota--;
          this.matchWorker.blueVideoQuotaProperty().set(newVideoQuota);
        } 
      } else {
        showMonologInfoDialog(getMessage("title.default.info"), getMessage("message.removeVideoQuota", new String[] { getMessage("label.red") }));
        int newVideoQuota = this.matchWorker.getRedVideoQuota();
        if (newVideoQuota > 0) {
          newVideoQuota--;
          this.matchWorker.redVideoQuotaProperty().set(newVideoQuota);
        } 
      }  
  }
  
  public boolean isBlueVideoRequested() {
    return this.blueVideoRequested;
  }
  
  public void setBlueVideoRequested(boolean blueVideoRequested) {
    this.blueVideoRequested = blueVideoRequested;
  }
  
  public boolean isRedVideoRequested() {
    return this.redVideoRequested;
  }
  
  public void setRedVideoRequested(boolean redVideoRequested) {
    this.redVideoRequested = redVideoRequested;
  }
  
  public void doChangeRound() {
    if (this.toggleGroup.getSelectedToggle() != null) {
      Integer newRound = (Integer)this.toggleGroup.getSelectedToggle().getUserData();
      if (newRound.intValue() != this.round.get()) {
        this.roundChanged = true;
        this.round.set(newRound.intValue());
      } 
    } 
  }
  
  public void doChangeRoundTime() {
    this.roundTimeChanged = true;
    this.lblRoundTime.setText(minutesOrSecondsFormat.format(this.roundTimeMinutes.get()) + ":" + minutesOrSecondsFormat
        .format(this.roundTimeSeconds.get()) + (
        this.txtRoundCentiseconds.isVisible() ? ("." + this.roundTimeCentiseconds.intValue()) : ""));
  }
  
  public void doResetTOBlue() {
    getMatchWorker().doResetPARATimeOutForBlue();
    onWindowShowEvent();
  }
  
  public void doResetTORed() {
    getMatchWorker().doResetPARATimeOutForRed();
    onWindowShowEvent();
  }
  
  void _initializeRoundAndTime() {
    int currentRound = getMatchWorker().getCurrentRound();
    if (MatchStatusId.WAITING_4_START_ROUND.equals(getMatchWorker().getCurrentMatchStatus()) && currentRound > 1) {
      currentRound--;
      logger.debug("We are Waiting for start round.. we move to previous round, currentRound: " + currentRound);
    } 
    int rounds = getMatchWorker().getMatchRounds();
    boolean goldenPoint = getMatchWorker().isGoldenPointEnabled();
    this.pnRoundSelection.getChildren().clear();
    if (rounds > 0) {
      for (int i = 1; i <= rounds; i++) {
        ToggleButton toggleButton = new ToggleButton("" + i);
        toggleButton.getStyleClass().add("wz-selection-toggle");
        toggleButton.setToggleGroup(this.toggleGroup);
        toggleButton.setUserData(Integer.valueOf(i));
        toggleButton.setSelected((currentRound == i));
        toggleButton.setCursor(Cursor.HAND);
        this.pnRoundSelection.getChildren().addAll((Object[])new Node[] { (Node)toggleButton });
      } 
      if (goldenPoint) {
        ToggleButton toggleButton = new ToggleButton(getMessage("label.goldenPoint"));
        toggleButton.getStyleClass().add("wz-selection-toggle");
        toggleButton.setToggleGroup(this.toggleGroup);
        toggleButton.setUserData(Integer.valueOf(-1));
        toggleButton.setSelected(getMatchWorker().isGoldenPointWorking());
        toggleButton.setCursor(Cursor.HAND);
        this.pnRoundSelection.getChildren().addAll((Object[])new Node[] { (Node)toggleButton });
      } 
    } 
    if (getMatchWorker().isGoldenPointEnabled() && getMatchWorker().isGoldenPointWorking()) {
      this.round.set(-1);
      this.lblRoundTime.setText((String)getMatchWorker().roundCountdownCurrentTimeAsStringProperty().get());
      this.txtRoundMinutes.setText("" + getMatchWorker().getRoundCountdownMinutes());
      this.txtRoundSeconds.setText("" + minutesOrSecondsFormat.format(getMatchWorker().getRoundCountdownSeconds()));
      this.txtRoundCentiseconds.setText("" + getMatchWorker().getRoundCountdownCentiseconds());
    } else {
      this.round.set(currentRound);
      this.lblRoundTime.setText((String)getMatchWorker().roundCountdownCurrentTimeAsStringProperty().get());
      this.txtRoundMinutes.setText("" + getMatchWorker().getRoundCountdownMinutes());
      this.txtRoundSeconds.setText("" + minutesOrSecondsFormat.format(getMatchWorker().getRoundCountdownSeconds()));
      this.txtRoundCentiseconds.setText("" + getMatchWorker().getRoundCountdownCentiseconds());
    } 
  }
  
  void _afterPropertiesSet() throws Exception {
    this.btPARAResetTOBlue.setVisible(false);
    this.btPARAResetTORed.setVisible(false);
    this.round.addListener(new ChangeListener<Number>() {
          public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newRound) {
            if (newRound.intValue() == -1) {
              ScoreboardEditorControllerImpl.this.lblRoundNumber.setText("GOLDEN POINT");
            } else {
              ScoreboardEditorControllerImpl.this.lblRoundNumber.setText("" + newRound);
            } 
          }
        });
  }
  
  MatchWorker getMatchWorker() {
    return this.matchWorker;
  }
  
  Integer getBodyPoints() {
    return Integer.valueOf(this.rulesEntry.getBodyPoints());
  }
  
  Integer getBodyTechPoints() {
    return Integer.valueOf(this.rulesEntry.getBodyTechPoints());
  }
  
  Integer getHeadPoints() {
    return Integer.valueOf(this.rulesEntry.getHeadPoints());
  }
  
  Integer getHeadTechPoints() {
    return Integer.valueOf(this.rulesEntry.getHeadTechPoints());
  }
  
  Integer getPunchPoints() {
    return Integer.valueOf(this.rulesEntry.getPunchPoints());
  }
}
