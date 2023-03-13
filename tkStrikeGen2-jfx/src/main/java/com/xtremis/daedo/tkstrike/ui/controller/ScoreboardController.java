package com.xtremis.daedo.tkstrike.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.xtremis.daedo.tkstrike.ei.client.WtUDPService;
import com.xtremis.daedo.tkstrike.om.MatchStatusId;
import com.xtremis.daedo.tkstrike.service.MatchWorker;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.scene.control.RestrictiveTextField;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import jfxtras.labs.dialogs.MonologFXButton;


@Component
public class ScoreboardController extends BaseScoreboardController {

	@FXML
	private RestrictiveTextField txtChangeBodyLevel;

	@FXML
	private RestrictiveTextField txtChangeHeadLevel;

	@Autowired
	@Qualifier("DEFAULT")
	private RoundCountdownController roundCountdownController;

	@Autowired
	private WtUDPService wtUDPService;

	@Autowired
	private MatchWorker matchWorker;

	@Autowired
	private ScoreboardEditorControllerImpl scoreboardEditorController;

	@Override
	protected RoundCountdownController getRoundCountdownController() {
		return this.roundCountdownController;
	}

	@Override
	protected double getFlagImageHeight() {
		return 63.75D;
	}

	@Override
	protected double getVideoImageHeight() {
		return 46.5D;
	}

	@Override
	protected Double getImpactImageHeight() {
		return Double.valueOf(57.5D);
	}

	@Override
	protected int getHitsControlDefaultFontSize() {
		return 15;
	}

	@Override
	protected double getHitsControlHeight() {
		return 25.0D;
	}

	@Override
	protected double getHitsControlSpacing() {
		return 5.0D;
	}

	@Override
	protected double getHitsControlJudgesWidth() {
		return 25.0D;
	}

	@Override
	protected double getHitsControlHitWidth() {
		return 25.0D;
	}

	@Override
	protected boolean isBlueOnLeft() {
		return true;
	}

	@Override
	protected void setBlueOnLeft(boolean blueOnLeft) {}

	@Override
	protected void _internalOnWindowShowEvent() {}

	@Override
	protected void _internalBindUIControls() {}

	@Override
	protected void _internalOnWindowCloseEvent() {}

	@Override
	protected void _internalUnbindUIControls() {}

	@Override
	protected void _internalInitialize(URL url, ResourceBundle resourceBundle) {}

	@Override
	protected boolean showNoVideoImage() {
		return true;
	}

	@Override
	protected void _internalAfterPropertiesSet() throws Exception {
		this.ivLeftRoundsWinsVideo.setCursor(Cursor.HAND);
		this.ivRightRoundsWinsVideo.setCursor(Cursor.HAND);
		this.ivLeftRoundsWinsVideo.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if(event.getButton().equals(MouseButton.PRIMARY) && ScoreboardController.this.matchWorker.getCurrentMatchStatus().equals(
						MatchStatusId.ROUND_FINISHED))
					ScoreboardController.this.doCallVideoRequest(ScoreboardController.this.isBlueOnLeft());
			}
		});
		this.ivRightRoundsWinsVideo.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if(event.getButton().equals(MouseButton.PRIMARY) && ScoreboardController.this.matchWorker.getCurrentMatchStatus().equals(
						MatchStatusId.ROUND_FINISHED))
					ScoreboardController.this.doCallVideoRequest( ! ScoreboardController.this.isBlueOnLeft());
			}
		});
		pnLeftVideo().setCursor(Cursor.HAND);
		pnRightVideo().setCursor(Cursor.HAND);
		pnLeftVideo().addEventHandler(MouseEvent.MOUSE_CLICKED, new VideoQuotaClickEventHandler(pnLeftVideo(), isBlueOnLeft()));
		pnRightVideo().addEventHandler(MouseEvent.MOUSE_CLICKED, new VideoQuotaClickEventHandler(pnRightVideo(), ! isBlueOnLeft()));
		this.txtChangeBodyLevel.setMaxLength(3);
		this.txtChangeBodyLevel.setRestrict("^0*(?:[0-9][0-9]?|100)$");
		this.txtChangeBodyLevel.setVisible(false);
		this.txtBodyLevel.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent mouseEvent) {
				if(mouseEvent.getClickCount() == 2 && ! ScoreboardController.this.matchWorker.getCurrentMatchStatus().equals(
						MatchStatusId.ROUND_WORKING) &&
						! ScoreboardController.this.matchWorker.getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT)) {
					ScoreboardController.this.txtChangeBodyLevel.setText(ScoreboardController.this.txtBodyLevel.getText());
					ScoreboardController.this.txtChangeBodyLevel.setVisible(true);
					ScoreboardController.this.txtChangeHeadLevel.setVisible(false);
				}
			}
		});
		this.txtChangeBodyLevel.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean hasFocus) {
				if( ! hasFocus.booleanValue())
					ScoreboardController.this.txtChangeBodyLevel.setVisible(false);
			}
		});
		this.txtChangeBodyLevel.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent keyEvent) {
				if(keyEvent.getCode().equals(KeyCode.TAB) || keyEvent.getCode().equals(KeyCode.ENTER)) {
					if( ! ScoreboardController.this.txtChangeBodyLevel.getText().equals(ScoreboardController.this.txtBodyLevel.getText())) {
						if(ScoreboardController.this.showMonologConfirmDialog(ScoreboardController.this.getMessage("title.default.question"),
								ScoreboardController.this
										.getMessage("message.confirmChangeBodyLevel", new String[] {keyEvent.getText()})).equals(
												MonologFXButton.Type.YES))
							ScoreboardController.this.matchWorker.setMinBodyLevel(Integer.parseInt(ScoreboardController.this.txtChangeBodyLevel
									.getText()));
						ScoreboardController.this.getAppStatusWorker().doDialogWindowCloses();
					}
					ScoreboardController.this.getRootView().requestFocus();
				}
			}
		});
		this.txtChangeHeadLevel.setMaxLength(3);
		this.txtChangeHeadLevel.setRestrict("^0*(?:[0-9][0-9]?|100)$");
		this.txtChangeHeadLevel.setVisible(false);
		this.txtHeadLevel.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent mouseEvent) {
				if(mouseEvent.getClickCount() == 2 && ! ScoreboardController.this.matchWorker.getCurrentMatchStatus().equals(
						MatchStatusId.ROUND_WORKING) &&
						! ScoreboardController.this.matchWorker.getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT)) {
					ScoreboardController.this.txtChangeHeadLevel.setText(ScoreboardController.this.txtHeadLevel.getText());
					ScoreboardController.this.txtChangeHeadLevel.setVisible(true);
					ScoreboardController.this.txtChangeBodyLevel.setVisible(false);
				}
			}
		});
		this.txtChangeHeadLevel.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean hasFocus) {
				if( ! hasFocus.booleanValue())
					ScoreboardController.this.txtChangeHeadLevel.setVisible(false);
			}
		});
		this.txtChangeHeadLevel.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent keyEvent) {
				if(keyEvent.getCode().equals(KeyCode.ENTER) || keyEvent.getCode().equals(KeyCode.TAB)) {
					if( ! ScoreboardController.this.txtChangeHeadLevel.getText().equals(ScoreboardController.this.txtHeadLevel.getText())) {
						if(ScoreboardController.this.showMonologConfirmDialog(ScoreboardController.this.getMessage("title.default.question"),
								ScoreboardController.this.getMessage("message.confirmChangeHeadLevel", new String[] {keyEvent.getText()})).equals(
										MonologFXButton.Type.YES))
							ScoreboardController.this.matchWorker.setMinHeadLevel(Integer.parseInt(ScoreboardController.this.txtChangeHeadLevel
									.getText()));
						ScoreboardController.this.getAppStatusWorker().doDialogWindowCloses();
					}
					ScoreboardController.this.getRootView().requestFocus();
				}
			}
		});
	}

	void doCallVideoRequestByBlue() {
		doCallVideoRequest(true);
	}

	void doCallVideoRequestByRed() {
		doCallVideoRequest(false);
	}

	private void doCallVideoRequest(final boolean isBlue) {
		blueRequestVideo = isBlue;
		redRequestVideo = ! isBlue;
		if(this.wtUDPService.isConnected())
			TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					ScoreboardController.this.wtUDPService.sendVideoReplay(isBlue, null);
					return null;
				}
			});
		this.scoreboardEditorController.setBlueVideoRequested(isBlue);
		this.scoreboardEditorController.setRedVideoRequested( ! isBlue);
		openInNewStage(this.scoreboardEditorController, getMessage("title.window.scoreboardEditor"), 960, 560);
	}

	class VideoQuotaClickEventHandler implements EventHandler<MouseEvent> {

		private Pane pnVideo;

		private boolean isBlue;

		public VideoQuotaClickEventHandler(Pane pnVideo, boolean isBlue) {
			this.pnVideo = pnVideo;
			this.isBlue = isBlue;
		}

		@Override
		public void handle(final MouseEvent mouseEvent) {
			if(mouseEvent.getClickCount() >= 1) {
				boolean hasVideoQuota = ((Boolean)this.pnVideo.getUserData()).booleanValue();
				if(mouseEvent.getButton().equals(MouseButton.PRIMARY) && ScoreboardController.this.matchWorker.getCurrentMatchStatus().equals(
						MatchStatusId.ROUND_PAUSED)) {
					ScoreboardController.this.doCallVideoRequest(this.isBlue);
				} else if(mouseEvent.getButton().equals(MouseButton.SECONDARY) && (ScoreboardController.this.matchWorker.getCurrentMatchStatus()
						.equals(MatchStatusId.ROUND_PAUSED) || ScoreboardController.this.matchWorker.getCurrentMatchStatus().equals(
								MatchStatusId.WAITING_4_MATCH) || ScoreboardController.this.matchWorker.getCurrentMatchStatus().equals(
										MatchStatusId.WAITING_4_START_GOLDENPOINT) || ScoreboardController.this.matchWorker.getCurrentMatchStatus()
												.equals(MatchStatusId.WAITING_4_START_ROUND) || ScoreboardController.this.matchWorker
														.getCurrentMatchStatus().equals(MatchStatusId.ROUND_FINISHED)
						|| ScoreboardController.this.matchWorker.getCurrentMatchStatus().equals(MatchStatusId.MATCH_FINISHED)
						|| ScoreboardController.this.matchWorker.getCurrentMatchStatus().equals(MatchStatusId.NETWORK_ERROR)
						|| ScoreboardController.this.matchWorker.getCurrentMatchStatus().equals(MatchStatusId.ROUND_KYESHI))) {
					final ContextMenu mrContextMenu = new ContextMenu();
					MenuItem miIncrease = new MenuItem(ScoreboardController.this.getMessage("button.increaseVideoQuota"));
					miIncrease.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {
							BaseScoreboardController.blueRequestVideo = false;
							BaseScoreboardController.redRequestVideo = false;
							if(ScoreboardController.VideoQuotaClickEventHandler.this.isBlue) {
								int newVideoQuota = ScoreboardController.this.matchWorker.getBlueVideoQuota();
								newVideoQuota++;
								ScoreboardController.this.matchWorker.blueVideoQuotaProperty().set(newVideoQuota);
							} else {
								int newVideoQuota = ScoreboardController.this.matchWorker.getRedVideoQuota();
								newVideoQuota++;
								ScoreboardController.this.matchWorker.redVideoQuotaProperty().set(newVideoQuota);
							}
						}
					});
					MenuItem miReduce = new MenuItem(ScoreboardController.this.getMessage("button.reduceVideoQuota"));
					miReduce.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {
							BaseScoreboardController.blueRequestVideo = false;
							BaseScoreboardController.redRequestVideo = false;
							if(ScoreboardController.VideoQuotaClickEventHandler.this.isBlue) {
								int newVideoQuota = ScoreboardController.this.matchWorker.getBlueVideoQuota();
								newVideoQuota--;
								ScoreboardController.this.matchWorker.blueVideoQuotaProperty().set(newVideoQuota);
							} else {
								int newVideoQuota = ScoreboardController.this.matchWorker.getRedVideoQuota();
								newVideoQuota--;
								ScoreboardController.this.matchWorker.redVideoQuotaProperty().set(newVideoQuota);
							}
						}
					});
					miReduce.setDisable( ! hasVideoQuota);
					mrContextMenu.getItems().add(miIncrease);
					mrContextMenu.getItems().add(miReduce);
					if( ! mrContextMenu.isShowing())
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								mrContextMenu.show(ScoreboardController.VideoQuotaClickEventHandler.this.pnVideo, mouseEvent.getScreenX(), mouseEvent
										.getScreenY());
							}
						});
				}
			}
		}
	}
}
