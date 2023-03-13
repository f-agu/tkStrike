package com.xtremis.daedo.tkstrike.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xtremis.daedo.tkstrike.communication.ChangeNetworkConfigurationEvent;
import com.xtremis.daedo.tkstrike.communication.ChangeNetworkStatusEvent;
import com.xtremis.daedo.tkstrike.communication.DataEvent;
import com.xtremis.daedo.tkstrike.communication.NetworkStatus;
import com.xtremis.daedo.tkstrike.om.AppStatusId;
import com.xtremis.daedo.tkstrike.om.MatchStatusId;
import com.xtremis.daedo.tkstrike.orm.model.SensorsGroup;
import com.xtremis.daedo.tkstrike.service.MatchWorker;
import com.xtremis.daedo.tkstrike.ui.scene.listener.ControlEnableListener;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;


@Component
public class NetworkStatusController extends NetworkStatusBaseController {

	@Autowired
	private MatchWorker matchWorker;

	@FXML
	private Node rootView;

	@FXML
	private Rectangle reNetworkStatus;

	@FXML
	private Text txtNetworkStatus;

	@FXML
	private ToggleButton tgGroup1;

	@FXML
	private ToggleButton tgGroup2;

	private final ToggleGroup tgGroupGroup = new ToggleGroup();

	@FXML
	private Pane hbJudge1;

	@FXML
	private Rectangle stJ1;

	@FXML
	private Text txtJ1Battery;

	@FXML
	private Pane hbJudge2;

	@FXML
	private Rectangle stJ2;

	@FXML
	private Text txtJ2Battery;

	@FXML
	private Pane hbJudge3;

	@FXML
	private Rectangle stJ3;

	@FXML
	private Text txtJ3Battery;

	@FXML
	private Pane hbG1BB;

	@FXML
	private Rectangle stG1BB;

	@FXML
	private Text txtG1BBBattery;

	@FXML
	private Pane hbG1HB;

	@FXML
	private Rectangle stG1HB;

	@FXML
	private Text txtG1HBBattery;

	@FXML
	private Pane hbG1BR;

	@FXML
	private Rectangle stG1BR;

	@FXML
	private Text txtG1BRBattery;

	@FXML
	private Pane hbG1HR;

	@FXML
	private Rectangle stG1HR;

	@FXML
	private Text txtG1HRBattery;

	@FXML
	private Pane hbG2BB;

	@FXML
	private Rectangle stG2BB;

	@FXML
	private Text txtG2BBBattery;

	@FXML
	private Pane hbG2HB;

	@FXML
	private Rectangle stG2HB;

	@FXML
	private Text txtG2HBBattery;

	@FXML
	private Pane hbG2BR;

	@FXML
	private Rectangle stG2BR;

	@FXML
	private Text txtG2BRBattery;

	@FXML
	private Pane hbG2HR;

	@FXML
	private Rectangle stG2HR;

	@FXML
	private Text txtG2HRBattery;

	@Override
	protected boolean hasUIStatus() {
		return true;
	}

	@Override
	protected boolean hasUIJudges() {
		return true;
	}

	@Override
	protected boolean hasUIAthletes() {
		return true;
	}

	@Override
	protected Text txtNetworkStatus() {
		return this.txtNetworkStatus;
	}

	@Override
	protected Rectangle reNetworkStatus() {
		return this.reNetworkStatus;
	}

	@Override
	protected Pane hbJudge1() {
		return this.hbJudge1;
	}

	@Override
	protected Rectangle stJ1() {
		return this.stJ1;
	}

	@Override
	protected Text txtJ1Battery() {
		return this.txtJ1Battery;
	}

	@Override
	protected Pane hbJudge2() {
		return this.hbJudge2;
	}

	@Override
	protected Rectangle stJ2() {
		return this.stJ2;
	}

	@Override
	protected Text txtJ2Battery() {
		return this.txtJ2Battery;
	}

	@Override
	protected Pane hbJudge3() {
		return this.hbJudge3;
	}

	@Override
	protected Rectangle stJ3() {
		return this.stJ3;
	}

	@Override
	protected Text txtJ3Battery() {
		return this.txtJ3Battery;
	}

	@Override
	protected Pane hbG1BB() {
		return this.hbG1BB;
	}

	@Override
	protected Rectangle stG1BB() {
		return this.stG1BB;
	}

	@Override
	protected Text txtG1BBBattery() {
		return this.txtG1BBBattery;
	}

	@Override
	protected Pane hbG1HB() {
		return this.hbG1HB;
	}

	@Override
	protected Rectangle stG1HB() {
		return this.stG1HB;
	}

	@Override
	protected Text txtG1HBBattery() {
		return this.txtG1HBBattery;
	}

	@Override
	protected Pane hbG1BR() {
		return this.hbG1BR;
	}

	@Override
	protected Rectangle stG1BR() {
		return this.stG1BR;
	}

	@Override
	protected Text txtG1BRBattery() {
		return this.txtG1BRBattery;
	}

	@Override
	protected Pane hbG1HR() {
		return this.hbG1HR;
	}

	@Override
	protected Rectangle stG1HR() {
		return this.stG1HR;
	}

	@Override
	protected Text txtG1HRBattery() {
		return this.txtG1HRBattery;
	}

	@Override
	protected Pane hbG2BB() {
		return this.hbG2BB;
	}

	@Override
	protected Rectangle stG2BB() {
		return this.stG2BB;
	}

	@Override
	protected Text txtG2BBBattery() {
		return this.txtG2BBBattery;
	}

	@Override
	protected Pane hbG2HB() {
		return this.hbG2HB;
	}

	@Override
	protected Rectangle stG2HB() {
		return this.stG2HB;
	}

	@Override
	protected Text txtG2HBBattery() {
		return this.txtG2HBBattery;
	}

	@Override
	protected Pane hbG2BR() {
		return this.hbG2BR;
	}

	@Override
	protected Rectangle stG2BR() {
		return this.stG2BR;
	}

	@Override
	protected Text txtG2BRBattery() {
		return this.txtG2BRBattery;
	}

	@Override
	protected Pane hbG2HR() {
		return this.hbG2HR;
	}

	@Override
	protected Rectangle stG2HR() {
		return this.stG2HR;
	}

	@Override
	protected Text txtG2HRBattery() {
		return this.txtG2HRBattery;
	}

	@Override
	protected void _customInitialize(URL url, ResourceBundle resourceBundle) {}

	@Override
	protected void _customAfterPropertiesSet() throws Exception {
		this.tgGroup1.setUserData(Boolean.TRUE);
		this.tgGroup2.setUserData(Boolean.TRUE);
		this.tgGroupGroup.getToggles().addAll(new Toggle[] {this.tgGroup1, this.tgGroup2});
		this.tgGroupGroup.selectToggle(this.tgGroup1);
		this.tgGroupGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if(newValue == null)
					NetworkStatusController.this.tgGroupGroup.selectToggle(oldValue);
			}
		});
		this.tgGroup1.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean selected) {
				if(selected.booleanValue()) {
					NetworkStatusController.this.tgGroup1.setText(NetworkStatusController.this.getMessage("toggle.group1.enabled"));
					if(((Boolean)NetworkStatusController.this.tgGroup1.getUserData()).booleanValue()) {
						if(NetworkStatusController.this.showConfirmDialog(NetworkStatusController.this.getMessage("title.default.question"),
								NetworkStatusController.this
										.getMessage("message.confirm.useGroup1")).get().equals(ButtonType.OK)) {
							NetworkStatusController.this.matchWorker.doChangeSensorsGroupSelection(SensorsGroup.GROUP1);
						} else {
							NetworkStatusController.this.tgGroup2.setUserData(Boolean.FALSE);
							NetworkStatusController.this.tgGroupGroup.selectToggle(NetworkStatusController.this.tgGroup2);
						}
					} else {
						NetworkStatusController.this.tgGroup1.setUserData(Boolean.TRUE);
					}
				} else {
					NetworkStatusController.this.tgGroup1.setText(NetworkStatusController.this.getMessage("toggle.group1.disabled"));
				}
			}
		});
		this.tgGroup2.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean selected) {
				if(selected.booleanValue()) {
					NetworkStatusController.this.tgGroup2.setText(NetworkStatusController.this.getMessage("toggle.group2.enabled"));
					if(((Boolean)NetworkStatusController.this.tgGroup2.getUserData()).booleanValue()) {
						if(NetworkStatusController.this.showConfirmDialog(NetworkStatusController.this.getMessage("title.default.question"),
								NetworkStatusController.this
										.getMessage("message.confirm.useGroup2")).get().equals(ButtonType.OK)) {
							NetworkStatusController.this.matchWorker.doChangeSensorsGroupSelection(SensorsGroup.GROUP2);
						} else {
							NetworkStatusController.this.tgGroup1.setUserData(Boolean.FALSE);
							NetworkStatusController.this.tgGroupGroup.selectToggle(NetworkStatusController.this.tgGroup1);
						}
					} else {
						NetworkStatusController.this.tgGroup2.setUserData(Boolean.TRUE);
					}
				} else {
					NetworkStatusController.this.tgGroup2.setText(NetworkStatusController.this.getMessage("toggle.group2.disabled"));
				}
			}
		});
		this.matchWorker.currentMatchStatusProperty().addListener(new ControlEnableListener(this.tgGroup1, new MatchStatusId[] {
				MatchStatusId.WAITING_4_MATCH, MatchStatusId.NETWORK_ERROR, MatchStatusId.NOT_READY, MatchStatusId.ROUND_KYESHI,
				MatchStatusId.ROUND_PAUSED, MatchStatusId.ROUND_FINISHED, MatchStatusId.WAITING_4_START_ROUND, MatchStatusId.MATCH_FINISHED}));
		this.matchWorker.currentMatchStatusProperty().addListener(new ControlEnableListener(this.tgGroup2, new MatchStatusId[] {
				MatchStatusId.WAITING_4_MATCH, MatchStatusId.NETWORK_ERROR, MatchStatusId.NOT_READY, MatchStatusId.ROUND_KYESHI,
				MatchStatusId.ROUND_PAUSED, MatchStatusId.ROUND_FINISHED, MatchStatusId.WAITING_4_START_ROUND, MatchStatusId.MATCH_FINISHED}));
	}

	@Override
	protected void _customBindControls() {
		this.hbG1BB.visibleProperty().bind((this.currentNetworkConfiguration.getGroup1Config()).bodySensorsEnabledProperty);
		this.hbG1BR.visibleProperty().bind((this.currentNetworkConfiguration.getGroup1Config()).bodySensorsEnabledProperty);
		this.hbG1HB.visibleProperty().bind((this.currentNetworkConfiguration.getGroup1Config()).headSensorsEnabledProperty);
		this.hbG1HR.visibleProperty().bind((this.currentNetworkConfiguration.getGroup1Config()).headSensorsEnabledProperty);
		this.hbG2BB.visibleProperty().bind((this.currentNetworkConfiguration.getGroup2Config()).bodySensorsEnabledProperty);
		this.hbG2BR.visibleProperty().bind((this.currentNetworkConfiguration.getGroup2Config()).bodySensorsEnabledProperty);
		this.hbG2HB.visibleProperty().bind((this.currentNetworkConfiguration.getGroup2Config()).headSensorsEnabledProperty);
		this.hbG2HR.visibleProperty().bind((this.currentNetworkConfiguration.getGroup2Config()).headSensorsEnabledProperty);
	}

	@Override
	protected void _customHasChangeNetworkStatusEvent(ChangeNetworkStatusEvent changeNetworkStatusEvent) {
		if(changeNetworkStatusEvent.getNetworkStatus().equals(NetworkStatus.OK))
			getAppStatusWorker().addAppStatusOk(AppStatusId.NETWORK_CONFIGURED);
	}

	@Override
	public void hasChangeNetworkConfigurationEvent(final ChangeNetworkConfigurationEvent changeNetworkConfigurationEvent) {
		if(changeNetworkConfigurationEvent.getNewNetworkConfigurationDto() != null)
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					NetworkStatusController.this.tgGroup2.setVisible(changeNetworkConfigurationEvent.getNewNetworkConfigurationDto()
							.getGroup2Enabled().booleanValue());
					NetworkStatusController.this.tgGroupGroup.selectToggle(NetworkStatusController.this.tgGroup1);
				}
			});
	}

	@Override
	protected void _customOnWindowShowEvent() {}

	@Override
	protected void _customOnWindowsCloseEvent() {}

	@Override
	public Node getRootView() {
		return this.rootView;
	}

	@Override
	public void hasNewDataEvent(DataEvent dataEvent) {}
}
