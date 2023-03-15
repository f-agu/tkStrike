package com.xtremis.daedo.tkstrike.ui.controller;

import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Value;

import com.xtremis.daedo.tkstrike.om.ActionSource;
import com.xtremis.daedo.tkstrike.om.MatchStatusId;
import com.xtremis.daedo.tkstrike.om.combat.FinalDecision;
import com.xtremis.daedo.tkstrike.service.CommonMatchWorker;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchVictoryCriteria;
import com.xtremis.daedo.tkstrike.ui.TkStrikeKeyCombinationsHelper;
import com.xtremis.daedo.tkstrike.ui.model.INetworkConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.ScoreboardAction;
import com.xtremis.daedo.tkstrike.ui.model.ScoreboardEditAction;
import com.xtremis.daedo.tkstrike.ui.scene.control.RestrictiveTextField;
import com.xtremis.daedo.tkstrike.ui.scene.listener.CustomTimeLostFocusListener;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;


public abstract class CommonScoreboardEditorController<NE extends INetworkConfigurationEntry, MW extends CommonMatchWorker>
		extends CommonTkStrikeBaseController<NE>
		implements ScoreboardEditorController {

	@Value("${tkStrike.allowChangeRoundCentiSeconds}")
	protected Boolean allowChangeRoundCentiSeconds;

	@FXML
	private Pane pnMain;

	@FXML
	private ProgressIndicator pi;

	private boolean gamJeomShowPointsOnGoldenPoint = false;

	@FXML
	Label lblRoundNumber;

	@FXML
	Label lblRoundTime;

	@FXML
	RestrictiveTextField txtRoundMinutes;

	@FXML
	RestrictiveTextField txtRoundSeconds;

	@FXML
	Label lblCentisecondsPoint;

	@FXML
	RestrictiveTextField txtRoundCentiseconds;

	@FXML
	Button btConfirmNewScore;

	@FXML
	private Label lblBluePoints;

	@FXML
	private Label lblRedPoints;

	@FXML
	private Label lblBluePenalties;

	@FXML
	private Label lblRedPenalties;

	@FXML
	private Pane pnBlueNearMissHitsControls;

	@FXML
	private Label lblBlueNearMissHitsCtrl;

	@FXML
	private Pane pnRedNearMissHitsControls;

	@FXML
	private Label lblRedNearMissHitsCtrl;

	@FXML
	private Pane pnBlueNearMissHits;

	@FXML
	private Label lblBlueNearMissHits;

	@FXML
	private Pane pnRedNearMissHits;

	@FXML
	private Label lblRedNearMissHits;

	boolean changesApplied = false;

	boolean closeWithButton = false;

	LinkedHashSet<ScoreboardEditAction> scoreboardEditActions = new LinkedHashSet<>();

	SimpleIntegerProperty roundTimeMinutes = new SimpleIntegerProperty(this, "roundTimeMinutes", 0);

	SimpleIntegerProperty roundTimeSeconds = new SimpleIntegerProperty(this, "roundTimeSeconds", 0);

	SimpleIntegerProperty roundTimeCentiseconds = new SimpleIntegerProperty(this, "roundTimeCentiseconds", 0);

	SimpleIntegerProperty bluePoints = new SimpleIntegerProperty(this, "bluePoints", 0);

	private SimpleIntegerProperty blueGeneralPoints = new SimpleIntegerProperty(this, "blueGeneralPoints", 0);

	private SimpleIntegerProperty bluePenalties = new SimpleIntegerProperty(this, "bluePenalties", 0);

	private SimpleIntegerProperty blueGoldenPointNearMissHits = new SimpleIntegerProperty(this, "blueGoldenPointNearMissHits", 0);

	SimpleIntegerProperty redPoints = new SimpleIntegerProperty(this, "bluePoints", 0);

	private SimpleIntegerProperty redGeneralPoints = new SimpleIntegerProperty(this, "redGeneralPoints", 0);

	private SimpleIntegerProperty redPenalties = new SimpleIntegerProperty(this, "redPenalties", 0);

	private SimpleIntegerProperty redGoldenPointNearMissHits = new SimpleIntegerProperty(this, "redGoldenPointNearMissHits", 0);

	@Override
	public final EventHandler<WindowEvent> getOnWindowCloseEventHandler() {
		return new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent windowEvent) {
				_doBeforeOnWindowCloseEventHandler();
				bluePoints.removeListener(bluePenaltiesListener);
				bluePenalties.removeListener(bluePenaltiesListener);
				redPoints.removeListener(redPointsListener);
				redPenalties.removeListener(redPenaltiesListener);
				bluePoints.set(0);
				blueGeneralPoints.set(0);
				blueGoldenPointNearMissHits.set(0);
				redPoints.set(0);
				redGeneralPoints.set(0);
				redGoldenPointNearMissHits.set(0);
				_doAfterOnWindowCloseEventHandler();
			}
		};
	}

	@Override
	public final void onWindowShowEvent() {
		super.onWindowShowEvent();
		this.gamJeomShowPointsOnGoldenPoint = getAppStatusWorker().getRulesEntry().isGamJeomShowPointsOnGoldenPoint();
		getMatchWorker().scoreboardEditorOpenProperty().set(false);
		getMatchWorker().scoreboardEditorOpenProperty().set(true);
		this.changesApplied = false;
		this.closeWithButton = false;
		this.scoreboardEditActions = new LinkedHashSet<>();
		_doBeforeOnWindowShowEvent();
		_initializeRoundAndTime();
		this.bluePoints.removeListener(this.bluePenaltiesListener);
		this.bluePenalties.removeListener(this.bluePenaltiesListener);
		this.redPoints.removeListener(this.redPointsListener);
		this.redPenalties.removeListener(this.redPenaltiesListener);
		this.blueGeneralPoints.set(getMatchWorker().getBlueGeneralPoints());
		this.redGeneralPoints.set(getMatchWorker().getRedGeneralPoints());
		this.bluePenalties.set(getMatchWorker().isGoldenPointWorking() ? getMatchWorker().getBlueGoldenPointPenalties()
				: getMatchWorker().getBluePenalties());
		this.redPenalties.set(getMatchWorker().isGoldenPointWorking() ? getMatchWorker().getRedGoldenPointPenalties()
				: getMatchWorker().getRedPenalties());
		this.bluePoints.set(_calculatePoints(this.blueGeneralPoints.get(), this.redPenalties.get()));
		this.redPoints.set(_calculatePoints(this.redGeneralPoints.get(), this.bluePenalties.get()));
		Integer maxGamJeomsAllowed = getMatchWorker().getMaxGamJeomsAllowed();
		this.lblBluePenalties.setText("" + this.bluePenalties.get());
		this.lblRedPenalties.setText("" + this.redPenalties.get());
		this.bluePoints.addListener(this.bluePointsListener);
		this.bluePenalties.addListener(this.bluePenaltiesListener);
		this.redPoints.addListener(this.redPointsListener);
		this.redPenalties.addListener(this.redPenaltiesListener);
		this.pnBlueNearMissHits.setVisible(getMatchWorker().showNearMissHitsOnScoreboardEditor());
		this.pnRedNearMissHits.setVisible(getMatchWorker().showNearMissHitsOnScoreboardEditor());
		this.pnBlueNearMissHitsControls.setVisible(getMatchWorker().showNearMissHitsOnScoreboardEditor());
		this.pnRedNearMissHitsControls.setVisible(getMatchWorker().showNearMissHitsOnScoreboardEditor());
		this.lblBlueNearMissHitsCtrl.setVisible(getMatchWorker().showNearMissHitsOnScoreboardEditor());
		this.lblRedNearMissHitsCtrl.setVisible(getMatchWorker().showNearMissHitsOnScoreboardEditor());
		this.blueGoldenPointNearMissHits.set(0);
		this.redGoldenPointNearMissHits.set(0);
		if(getMatchWorker().isGoldenPointWorking()) {
			this.blueGoldenPointNearMissHits.set(getMatchWorker().getBlueGoldenPointImpacts());
			this.redGoldenPointNearMissHits.set(getMatchWorker().getRedGoldenPointImpacts());
		} else if(MatchVictoryCriteria.BESTOF3.equals(getMatchWorker().getMatchVictoryCriteria())) {
			this.blueGoldenPointNearMissHits.set(getMatchWorker().getBlueNearMissHits());
			this.redGoldenPointNearMissHits.set(getMatchWorker().getRedNearMissHits());
		}
		_doAfterOnWindowShowEvent();
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				btConfirmNewScore.requestFocus();
			}
		});
	}

	@Override
	public final boolean isChangesApplied() {
		return this.changesApplied;
	}

	public final void cancel() {
		this.closeWithButton = true;
		doCloseThisStage();
	}

	public final void applyChangesOnMatch() {
		if(this.scoreboardEditActions != null) {
			boolean changesOnRoundOrTime = _applyChangesOnRoundIfNeed();
			long matchRoundTimestamp = getMatchWorker().getCurrentRoundCountdownAsMillis();
			long prevTime = System.currentTimeMillis();
			if(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION.equals(getMatchWorker().getCurrentMatchStatus()) && this.scoreboardEditActions
					.size() > 0 &&
					! getMatchWorker().getMatchFinalDecision().equals(FinalDecision.PTG)) {
				if(logger.isDebugEnabled())
					logger.info("Match is finished and have changes on Points, validate if it produces new round.");
				if(getMatchWorker().simulateIfApplyingChangesMatchHasNextRound(this.scoreboardEditActions)) {
					if(logger.isDebugEnabled())
						logger.info("If this changes is applied a new round is required.");
					Optional<ButtonType> opResponse = showConfirmDialog(getMessage("message.confirmDialog.title"),
							getMessage("message.scoreboardChangesProducesGoldenPoint.confirmMessage"));
					if(opResponse != null && opResponse
							.isPresent() && ButtonType.CANCEL
									.equals(opResponse.get()))
						getMatchWorker().disableChangeMatchStatusToTimeoutOnScoreboardChanges();
				}
			}
			if( ! MatchVictoryCriteria.BESTOF3.equals(getMatchWorker().getMatchVictoryCriteria()) || this.scoreboardEditActions.size() > 1)
				;
			int actionCount = 0;
			for(ScoreboardEditAction scoreboardEditAction : this.scoreboardEditActions) {
				actionCount++;
				if( ! MatchVictoryCriteria.BESTOF3.equals(getMatchWorker().getMatchVictoryCriteria()) || actionCount == this.scoreboardEditActions
						.size())
					;
				long newTime = System.currentTimeMillis();
				newTime = (newTime == prevTime) ? (newTime + 1L) : newTime;
				switch(scoreboardEditAction.getAction()) {
					case BODY_POINT:
						if(scoreboardEditAction.isBlue()) {
							getMatchWorker().addBlueBodyPoint(scoreboardEditAction.getValue(), ActionSource.SCOREBOARD_EDITOR, newTime,
									matchRoundTimestamp, 0);
							break;
						}
						getMatchWorker().addRedBodyPoint(scoreboardEditAction.getValue(), ActionSource.SCOREBOARD_EDITOR, newTime,
								matchRoundTimestamp, 0);
						break;
					case BODY_TECH_POINT:
						if(scoreboardEditAction.isBlue()) {
							getMatchWorker().addBlueBodyTechPoint(scoreboardEditAction.getValue(), ActionSource.SCOREBOARD_EDITOR, newTime,
									matchRoundTimestamp);
							break;
						}
						getMatchWorker().addRedBodyTechPoint(scoreboardEditAction.getValue(), ActionSource.SCOREBOARD_EDITOR, newTime,
								matchRoundTimestamp);
						break;
					case HEAD_POINT:
						if(scoreboardEditAction.isBlue()) {
							getMatchWorker().addBlueHeadPoint(scoreboardEditAction.getValue(), ActionSource.SCOREBOARD_EDITOR, newTime,
									matchRoundTimestamp, 0);
							break;
						}
						getMatchWorker().addRedHeadPoint(scoreboardEditAction.getValue(), ActionSource.SCOREBOARD_EDITOR, newTime,
								matchRoundTimestamp, 0);
						break;
					case HEAD_TECH_POINT:
						if(scoreboardEditAction.isBlue()) {
							getMatchWorker().addBlueHeadTechPoint(scoreboardEditAction.getValue(), ActionSource.SCOREBOARD_EDITOR, newTime,
									matchRoundTimestamp);
							break;
						}
						getMatchWorker().addRedHeadTechPoint(scoreboardEditAction.getValue(), ActionSource.SCOREBOARD_EDITOR, newTime,
								matchRoundTimestamp);
						break;
					case PUNCH_POINT:
						if(scoreboardEditAction.isBlue()) {
							getMatchWorker().addBluePunchPoint(scoreboardEditAction.getValue(), ActionSource.SCOREBOARD_EDITOR, newTime,
									matchRoundTimestamp);
							break;
						}
						getMatchWorker().addRedPunchPoint(scoreboardEditAction.getValue(), ActionSource.SCOREBOARD_EDITOR, newTime,
								matchRoundTimestamp);
						break;
					case GAME_JEON:
						if(scoreboardEditAction.isBlue()) {
							if(scoreboardEditAction.getValue() > 0) {
								getMatchWorker().addBlueGamJeom(ActionSource.SCOREBOARD_EDITOR, newTime, matchRoundTimestamp);
								break;
							}
							getMatchWorker().removeBlueGamJeom(ActionSource.SCOREBOARD_EDITOR, newTime, matchRoundTimestamp);
							break;
						}
						if(scoreboardEditAction.getValue() > 0) {
							getMatchWorker().addRedGamJeom(ActionSource.SCOREBOARD_EDITOR, newTime, matchRoundTimestamp);
							break;
						}
						getMatchWorker().removeRedGamJeom(ActionSource.SCOREBOARD_EDITOR, newTime, matchRoundTimestamp);
						break;
					case NEAR_MISS_HIT:
						if(scoreboardEditAction.isBlue()) {
							if(scoreboardEditAction.getValue() == - 1) {
								getMatchWorker().removeBlueNearMissHit(ActionSource.SCOREBOARD_EDITOR, newTime, matchRoundTimestamp);
								break;
							}
							getMatchWorker().addBlueNearMissHit(ActionSource.SCOREBOARD_EDITOR, newTime, matchRoundTimestamp);
							break;
						}
						if(scoreboardEditAction.getValue() == - 1) {
							getMatchWorker().removeRedNearMissHit(ActionSource.SCOREBOARD_EDITOR, newTime, matchRoundTimestamp);
							break;
						}
						getMatchWorker().addRedNearMissHit(ActionSource.SCOREBOARD_EDITOR, newTime, matchRoundTimestamp);
						break;
				}
				prevTime = newTime;
			}
			this.changesApplied = (changesOnRoundOrTime || this.scoreboardEditActions.size() > 0);
		}
		this.closeWithButton = true;
		doCloseThisStage();
	}

	private boolean canRemovePoint(Integer points, Integer points2Remove) {
		return (points.intValue() - points2Remove.intValue() >= 0);
	}

	public void addBlueBodyPoint() {
		this.scoreboardEditActions.add(new ScoreboardEditAction(true, ScoreboardAction.BODY_POINT, getBodyPoints().intValue()));
		this.bluePoints.set(this.bluePoints.add(getBodyPoints().intValue()).get());
	}

	public void removeBlueBodyPoint() {
		if(canRemovePoint(Integer.valueOf(this.bluePoints.get()), getBodyPoints())) {
			this.scoreboardEditActions.add(new ScoreboardEditAction(true, ScoreboardAction.BODY_POINT, getBodyPoints().intValue() * - 1));
			this.bluePoints.set(this.bluePoints.subtract(getBodyPoints().intValue()).get());
		}
	}

	public void addBlueBodyTechPoint() {
		this.scoreboardEditActions.add(new ScoreboardEditAction(true, ScoreboardAction.BODY_TECH_POINT, getBodyTechPoints().intValue()));
		this.bluePoints.set(this.bluePoints.add(getBodyTechPoints().intValue()).get());
	}

	public void removeBlueBodyTechPoint() {
		if(canRemovePoint(Integer.valueOf(this.bluePoints.get()), getBodyTechPoints())) {
			this.scoreboardEditActions.add(new ScoreboardEditAction(true, ScoreboardAction.BODY_TECH_POINT, getBodyTechPoints().intValue() * - 1));
			this.bluePoints.set(this.bluePoints.subtract(getBodyTechPoints().intValue()).get());
		}
	}

	public void addBlueHeadPoint() {
		this.scoreboardEditActions.add(new ScoreboardEditAction(true, ScoreboardAction.HEAD_POINT, getHeadPoints().intValue()));
		this.bluePoints.set(this.bluePoints.add(getHeadPoints().intValue()).get());
	}

	public void removeBlueHeadPoint() {
		if(canRemovePoint(Integer.valueOf(this.bluePoints.get()), getHeadPoints())) {
			this.scoreboardEditActions.add(new ScoreboardEditAction(true, ScoreboardAction.HEAD_POINT, getHeadPoints().intValue() * - 1));
			this.bluePoints.set(this.bluePoints.subtract(getHeadPoints().intValue()).get());
		}
	}

	public void addBlueHeadTechPoint() {
		this.scoreboardEditActions.add(new ScoreboardEditAction(true, ScoreboardAction.HEAD_TECH_POINT, getHeadTechPoints().intValue()));
		this.bluePoints.set(this.bluePoints.add(getHeadTechPoints().intValue()).get());
	}

	public void removeBlueHeadTechPoint() {
		if(canRemovePoint(Integer.valueOf(this.bluePoints.get()), getHeadTechPoints())) {
			this.scoreboardEditActions.add(new ScoreboardEditAction(true, ScoreboardAction.HEAD_TECH_POINT, getHeadTechPoints().intValue() * - 1));
			this.bluePoints.set(this.bluePoints.subtract(getHeadTechPoints().intValue()).get());
		}
	}

	public void addBluePunchPoint() {
		this.scoreboardEditActions.add(new ScoreboardEditAction(true, ScoreboardAction.PUNCH_POINT, getPunchPoints().intValue()));
		this.bluePoints.set(this.bluePoints.add(getPunchPoints().intValue()).get());
	}

	public void removeBluePunchPoint() {
		if(canRemovePoint(Integer.valueOf(this.bluePoints.get()), getPunchPoints())) {
			this.scoreboardEditActions.add(new ScoreboardEditAction(true, ScoreboardAction.PUNCH_POINT, getPunchPoints().intValue() * - 1));
			this.bluePoints.set(this.bluePoints.subtract(getPunchPoints().intValue()).get());
		}
	}

	public void addBlueGameJeon() {
		this.scoreboardEditActions.add(new ScoreboardEditAction(true, ScoreboardAction.GAME_JEON, 2));
		if(this.bluePenalties.get() < getMatchWorker().getMaxGamJeomsAllowed().intValue())
			this.bluePenalties.set(this.bluePenalties.add(1).get());
	}

	public void removeBlueGameJeon() {
		this.scoreboardEditActions.add(new ScoreboardEditAction(true, ScoreboardAction.GAME_JEON, - 2));
		if(this.bluePenalties.get() > 0)
			this.bluePenalties.set(this.bluePenalties.get() - 1);
	}

	public void addRedBodyPoint() {
		this.scoreboardEditActions.add(new ScoreboardEditAction(false, ScoreboardAction.BODY_POINT, getBodyPoints().intValue()));
		this.redPoints.set(this.redPoints.add(getBodyPoints().intValue()).get());
	}

	public void removeRedBodyPoint() {
		if(canRemovePoint(Integer.valueOf(this.redPoints.get()), getBodyPoints())) {
			this.scoreboardEditActions.add(new ScoreboardEditAction(false, ScoreboardAction.BODY_POINT, getBodyPoints().intValue() * - 1));
			this.redPoints.set(this.redPoints.subtract(getBodyPoints().intValue()).get());
		}
	}

	public void addRedBodyTechPoint() {
		this.scoreboardEditActions.add(new ScoreboardEditAction(false, ScoreboardAction.BODY_TECH_POINT, getBodyTechPoints().intValue()));
		this.redPoints.set(this.redPoints.add(getBodyTechPoints().intValue()).get());
	}

	public void removeRedBodyTechPoint() {
		if(canRemovePoint(Integer.valueOf(this.redPoints.get()), getBodyTechPoints())) {
			this.scoreboardEditActions.add(new ScoreboardEditAction(false, ScoreboardAction.BODY_TECH_POINT, getBodyTechPoints().intValue() * - 1));
			this.redPoints.set(this.redPoints.subtract(getBodyTechPoints().intValue()).get());
		}
	}

	public void addRedHeadPoint() {
		this.scoreboardEditActions.add(new ScoreboardEditAction(false, ScoreboardAction.HEAD_POINT, getHeadPoints().intValue()));
		this.redPoints.set(this.redPoints.add(getHeadPoints().intValue()).get());
	}

	public void removeRedHeadPoint() {
		if(canRemovePoint(Integer.valueOf(this.redPoints.get()), getHeadPoints())) {
			this.scoreboardEditActions.add(new ScoreboardEditAction(false, ScoreboardAction.HEAD_POINT, getHeadPoints().intValue() * - 1));
			this.redPoints.set(this.redPoints.subtract(getHeadPoints().intValue()).get());
		}
	}

	public void addRedHeadTechPoint() {
		this.scoreboardEditActions.add(new ScoreboardEditAction(false, ScoreboardAction.HEAD_TECH_POINT, getHeadTechPoints().intValue()));
		this.redPoints.set(this.redPoints.add(getHeadTechPoints().intValue()).get());
	}

	public void removeRedHeadTechPoint() {
		if(canRemovePoint(Integer.valueOf(this.redPoints.get()), getHeadTechPoints())) {
			this.scoreboardEditActions.add(new ScoreboardEditAction(false, ScoreboardAction.HEAD_TECH_POINT, getHeadTechPoints().intValue() * - 1));
			this.redPoints.set(this.redPoints.subtract(getHeadTechPoints().intValue()).get());
		}
	}

	public void addRedPunchPoint() {
		this.scoreboardEditActions.add(new ScoreboardEditAction(false, ScoreboardAction.PUNCH_POINT, getPunchPoints().intValue()));
		this.redPoints.set(this.redPoints.add(getPunchPoints().intValue()).get());
	}

	public void removeRedPunchPoint() {
		if(canRemovePoint(Integer.valueOf(this.redPoints.get()), getPunchPoints())) {
			this.scoreboardEditActions.add(new ScoreboardEditAction(false, ScoreboardAction.PUNCH_POINT, getPunchPoints().intValue() * - 1));
			this.redPoints.set(this.redPoints.subtract(getPunchPoints().intValue()).get());
		}
	}

	public void addRedGameJeon() {
		this.scoreboardEditActions.add(new ScoreboardEditAction(false, ScoreboardAction.GAME_JEON, 2));
		if(this.redPenalties.get() < getMatchWorker().getMaxGamJeomsAllowed().intValue())
			this.redPenalties.set(this.redPenalties.add(1).get());
	}

	public void removeRedGameJeon() {
		this.scoreboardEditActions.add(new ScoreboardEditAction(false, ScoreboardAction.GAME_JEON, - 2));
		if(this.redPenalties.get() > 0)
			this.redPenalties.set(this.redPenalties.get() - 1);
	}

	public void addBlueNearMissHit() {
		this.scoreboardEditActions.add(new ScoreboardEditAction(true, ScoreboardAction.NEAR_MISS_HIT, 1));
		this.blueGoldenPointNearMissHits.set(this.blueGoldenPointNearMissHits.get() + 1);
	}

	public void removeBlueNearMissHit() {
		if(this.blueGoldenPointNearMissHits.get() > 0) {
			this.scoreboardEditActions.add(new ScoreboardEditAction(true, ScoreboardAction.NEAR_MISS_HIT, - 1));
			this.blueGoldenPointNearMissHits.set(this.blueGoldenPointNearMissHits.get() - 1);
		}
	}

	public void addRedNearMissHit() {
		this.scoreboardEditActions.add(new ScoreboardEditAction(false, ScoreboardAction.NEAR_MISS_HIT, 1));
		this.redGoldenPointNearMissHits.set(this.redGoldenPointNearMissHits.get() + 1);
	}

	public void removeRedNearMissHit() {
		if(this.redGoldenPointNearMissHits.get() > 0) {
			this.scoreboardEditActions.add(new ScoreboardEditAction(false, ScoreboardAction.NEAR_MISS_HIT, - 1));
			this.redGoldenPointNearMissHits.set(this.redGoldenPointNearMissHits.get() - 1);
		}
	}

	private void _calculateBlueGeneralPoints() {
		_calculateGeneralPoints(this.blueGeneralPoints, this.bluePoints.get(), this.redPenalties.get());
	}

	private void _calculateRedGeneralPoints() {
		_calculateGeneralPoints(this.redGeneralPoints, this.redPoints.get(), this.bluePenalties.get());
	}

	private void _calculateGeneralPoints(SimpleIntegerProperty generalPoints, int points, int penalties) {
		int newGeneralPoints = points + ((getMatchWorker().isGoldenPointWorking() && ! this.gamJeomShowPointsOnGoldenPoint && penalties <= 1) ? 0
				: penalties);
		generalPoints.set(newGeneralPoints);
	}

	private int _calculatePoints(int generalPoints, int penalties) {
		int res = generalPoints;
		if(penalties > 0)
			res -= (getMatchWorker().isGoldenPointWorking() && ! this.gamJeomShowPointsOnGoldenPoint && penalties <= 1) ? 0 : penalties;
		return res;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.txtRoundMinutes.setRestrict("[0-5][0-9]");
		this.txtRoundMinutes.setPromptText("mm");
		this.txtRoundMinutes.setDefaultValue("00");
		this.txtRoundMinutes.focusedProperty().addListener(new CustomTimeLostFocusListener(this.txtRoundMinutes, "00"));
		this.txtRoundMinutes.textProperty().bindBidirectional((Property)this.roundTimeMinutes, (StringConverter)new NumberStringConverter());
		this.txtRoundSeconds.setRestrict("[0-5][0-9]");
		this.txtRoundSeconds.setPromptText("ss");
		this.txtRoundSeconds.setDefaultValue("00");
		this.txtRoundSeconds.focusedProperty().addListener(new CustomTimeLostFocusListener(this.txtRoundSeconds, "00"));
		this.txtRoundSeconds.textProperty().bindBidirectional((Property)this.roundTimeSeconds, (StringConverter)new NumberStringConverter());
		this.txtRoundCentiseconds.setVisible(false);
		if(this.allowChangeRoundCentiSeconds.booleanValue()) {
			this.roundTimeMinutes.addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					final boolean show = (newValue != null && newValue.intValue() == 0 && roundTimeSeconds.intValue() <= 9);
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							txtRoundCentiseconds.setVisible(show);
							lblCentisecondsPoint.setVisible(show);
						}
					});
				}
			});
			this.roundTimeSeconds.addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					final boolean show = (newValue != null && roundTimeMinutes.intValue() == 0 && newValue.intValue() <= 9);
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							txtRoundCentiseconds.setVisible(show);
							lblCentisecondsPoint.setVisible(show);
						}
					});
				}
			});
			this.txtRoundCentiseconds.setRestrict("[0-9]");
			this.txtRoundCentiseconds.setPromptText("c");
			this.txtRoundCentiseconds.setDefaultValue("0");
			this.txtRoundCentiseconds.focusedProperty().addListener(new CustomTimeLostFocusListener(this.txtRoundCentiseconds, "00"));
			this.txtRoundCentiseconds.textProperty().bindBidirectional((Property)this.roundTimeCentiseconds,
					(StringConverter)new NumberStringConverter());
		}
		this.lblBluePoints.textProperty().bindBidirectional((Property)this.blueGeneralPoints, (StringConverter)new NumberStringConverter());
		this.lblRedPoints.textProperty().bindBidirectional((Property)this.redGeneralPoints, (StringConverter)new NumberStringConverter());
		this.lblBlueNearMissHits.textProperty().bindBidirectional((Property)this.blueGoldenPointNearMissHits,
				(StringConverter)new NumberStringConverter());
		this.lblRedNearMissHits.textProperty().bindBidirectional((Property)this.redGoldenPointNearMissHits,
				(StringConverter)new NumberStringConverter());
		this.bluePenalties.addListener(this.bluePenaltiesListener);
		this.redPenalties.addListener(this.redPenaltiesListener);
		this.bluePoints.addListener(this.bluePointsListener);
		this.redPoints.addListener(this.redPointsListener);
		this.rootView.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if(TkStrikeKeyCombinationsHelper.keyCombENTER.match(event)) {
					applyChangesOnMatch();
				} else if(TkStrikeKeyCombinationsHelper.keyCombESCAPE.match(event)) {
					cancel();
				} else if(TkStrikeKeyCombinationsHelper.keyCombP1.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumP1.match(event)) {
					addBlueBodyPoint();
				} else if(TkStrikeKeyCombinationsHelper.keyCombM1.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumM1.match(event)) {
					removeBlueBodyPoint();
				} else if(TkStrikeKeyCombinationsHelper.keyCombP2.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumP2.match(event)) {
					addBlueBodyTechPoint();
				} else if(TkStrikeKeyCombinationsHelper.keyCombM2.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumM2.match(event)) {
					removeBlueBodyTechPoint();
				} else if(TkStrikeKeyCombinationsHelper.keyCombP3.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumP3.match(event)) {
					addBlueHeadPoint();
				} else if(TkStrikeKeyCombinationsHelper.keyCombM3.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumM3.match(event)) {
					removeBlueHeadPoint();
				} else if(TkStrikeKeyCombinationsHelper.keyCombP4.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumP4.match(event)) {
					addBlueHeadTechPoint();
				} else if(TkStrikeKeyCombinationsHelper.keyCombM4.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumM4.match(event)) {
					removeBlueHeadTechPoint();
				} else if(TkStrikeKeyCombinationsHelper.keyCombP5.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumP5.match(event)) {
					addBluePunchPoint();
				} else if(TkStrikeKeyCombinationsHelper.keyCombM5.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumM5.match(event)) {
					removeBluePunchPoint();
				} else if(TkStrikeKeyCombinationsHelper.keyCombP6.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumP6.match(event)) {
					addRedBodyPoint();
				} else if(TkStrikeKeyCombinationsHelper.keyCombM6.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumM6.match(event)) {
					removeRedBodyPoint();
				} else if(TkStrikeKeyCombinationsHelper.keyCombP7.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumP7.match(event)) {
					addRedBodyTechPoint();
				} else if(TkStrikeKeyCombinationsHelper.keyCombM7.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumM7.match(event)) {
					removeRedBodyTechPoint();
				} else if(TkStrikeKeyCombinationsHelper.keyCombP8.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumP8.match(event)) {
					addRedHeadPoint();
				} else if(TkStrikeKeyCombinationsHelper.keyCombM8.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumM8.match(event)) {
					removeRedHeadPoint();
				} else if(TkStrikeKeyCombinationsHelper.keyCombP9.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumP9.match(event)) {
					addRedHeadTechPoint();
				} else if(TkStrikeKeyCombinationsHelper.keyCombM9.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumM9.match(event)) {
					removeRedHeadTechPoint();
				} else if(TkStrikeKeyCombinationsHelper.keyCombP0.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumP0.match(event)) {
					addRedPunchPoint();
				} else if(TkStrikeKeyCombinationsHelper.keyCombM0.match(event) || TkStrikeKeyCombinationsHelper.keyCombNumM0.match(event)) {
					removeRedPunchPoint();
				} else if(TkStrikeKeyCombinationsHelper.keyCombAddGamJeom2Blue.match(event)) {
					addBlueGameJeon();
				} else if(TkStrikeKeyCombinationsHelper.keyCombRemoveGamJeom2Blue.match(event)) {
					removeBlueGameJeon();
				} else if(TkStrikeKeyCombinationsHelper.keyCombAddGamJeom2Red.match(event)) {
					addRedGameJeon();
				} else if(TkStrikeKeyCombinationsHelper.keyCombRemoveGamJeom2Red.match(event)) {
					removeRedGameJeon();
				}
			}
		});
		_afterPropertiesSet();
	}

	private final ChangeListener<Number> bluePointsListener = new ChangeListener<Number>() {

		@Override
		public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newPoints) {
			_calculateBlueGeneralPoints();
		}
	};

	private final ChangeListener<Number> redPointsListener = new ChangeListener<Number>() {

		@Override
		public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newPoints) {
			_calculateRedGeneralPoints();
		}
	};

	private final ChangeListener<Number> bluePenaltiesListener = new ChangeListener<Number>() {

		@Override
		public void changed(ObservableValue<? extends Number> observableValue, Number number, final Number t1) {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					lblBluePenalties.setText("" + t1);
				}
			});
			_calculateRedGeneralPoints();
		}
	};

	private final ChangeListener<Number> redPenaltiesListener = new ChangeListener<Number>() {

		@Override
		public void changed(ObservableValue<? extends Number> observableValue, Number number, final Number t1) {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					lblRedPenalties.setText("" + t1);
				}
			});
			_calculateBlueGeneralPoints();
		}
	};

	abstract MW getMatchWorker();

	abstract void _doBeforeOnWindowCloseEventHandler();

	abstract void _doAfterOnWindowCloseEventHandler();

	abstract void _doBeforeOnWindowShowEvent();

	abstract void _doAfterOnWindowShowEvent();

	abstract void _afterPropertiesSet() throws Exception;

	abstract void _initializeRoundAndTime();

	abstract boolean _applyChangesOnRoundIfNeed();

	abstract Integer getBodyPoints();

	abstract Integer getBodyTechPoints();

	abstract Integer getHeadPoints();

	abstract Integer getHeadTechPoints();

	abstract Integer getPunchPoints();
}
