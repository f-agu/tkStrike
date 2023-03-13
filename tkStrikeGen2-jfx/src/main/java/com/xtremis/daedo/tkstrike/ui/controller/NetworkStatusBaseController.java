package com.xtremis.daedo.tkstrike.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.xtremis.daedo.tkstrike.communication.ChangeNetworkConfigurationEvent;
import com.xtremis.daedo.tkstrike.communication.ChangeNetworkStatusEvent;
import com.xtremis.daedo.tkstrike.communication.GlobalNetworkStatusController;
import com.xtremis.daedo.tkstrike.communication.GlobalNetworkStatusControllerListener;
import com.xtremis.daedo.tkstrike.communication.StatusEvent;
import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationListener;
import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationService;
import com.xtremis.daedo.tkstrike.om.NetworkErrorCause;
import com.xtremis.daedo.tkstrike.service.NetworkConfigurationService;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.model.NetworkConfigurationEntry;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;


public abstract class NetworkStatusBaseController extends TkStrikeBaseController
		implements TkStrikeCommunicationListener, GlobalNetworkStatusControllerListener {

	protected static final Logger logger = Logger.getLogger(NetworkStatusBaseController.class);

	@Value("${tkStrike.networkStatus.removeListenerWhenCloseWindow}")
	private Boolean removeListenerWhenCloseWindow;

	private long judge1LastStatusOk = - 1L;

	private SimpleStringProperty j1Battery = new SimpleStringProperty();

	private long judge2LastStatusOk = - 1L;

	private SimpleStringProperty j2Battery = new SimpleStringProperty();

	private long judge3LastStatusOk = - 1L;

	private SimpleStringProperty j3Battery = new SimpleStringProperty();

	private long g1BBLastStatusOk = - 1L;

	private SimpleStringProperty g1BBBattery = new SimpleStringProperty();

	private SimpleStringProperty g1HBBattery = new SimpleStringProperty();

	private long g1HBLastStatusOk = - 1L;

	private SimpleStringProperty g1BRBattery = new SimpleStringProperty();

	private long g1BRLastStatusOk = - 1L;

	private SimpleStringProperty g1HRBattery = new SimpleStringProperty();

	private long g1HRLastStatusOk = - 1L;

	private SimpleStringProperty g2BBBattery = new SimpleStringProperty();

	private long g2BBLastStatusOk = - 1L;

	private SimpleStringProperty g2HBBattery = new SimpleStringProperty();

	private long g2HBLastStatusOk = - 1L;

	private SimpleStringProperty g2BRBattery = new SimpleStringProperty();

	private long g2BRLastStatusOk = - 1L;

	private SimpleStringProperty g2HRBattery = new SimpleStringProperty();

	private long g2HRLastStatusOk = - 1L;

	private long EVENT_CYCLE = 1000L;

	@Autowired
	protected NetworkConfigurationService networkConfigurationService;

	@Autowired
	protected TkStrikeCommunicationService tkStrikeCommunicationService;

	@Autowired
	protected GlobalNetworkStatusController globalNetworkStatusController;

	protected NetworkConfigurationEntry currentNetworkConfiguration = new NetworkConfigurationEntry();

	@Override
	public final void onWindowShowEvent() {
		super.onWindowShowEvent();
		if(logger.isDebugEnabled())
			logger.debug("On Window show Event, ADD listener ?" + this.removeListenerWhenCloseWindow);
		if(this.removeListenerWhenCloseWindow.booleanValue())
			this.tkStrikeCommunicationService.addListener(this);
		TkStrikeExecutors.executeInThreadPool(new Runnable() {

			@Override
			public void run() {
				NetworkStatusBaseController.this.currentNetworkConfiguration = NetworkStatusBaseController.this.getCurrentNetworkConfiguration();
				if(NetworkStatusBaseController.this.hasUIStatus())
					NetworkStatusBaseController.this.txtNetworkStatus().setText(NetworkStatusBaseController.this.getMessage("label.networkStatus."
							+ NetworkStatusBaseController.this.tkStrikeCommunicationService.getCurrentNetworkStatus().toString()));
				NetworkStatusBaseController.this._bindControls();
			}
		});
		_customOnWindowShowEvent();
	}

	@Override
	public EventHandler<WindowEvent> getOnWindowCloseEventHandler() {
		final TkStrikeCommunicationListener listener = this;
		return new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent windowEvent) {
				if(NetworkStatusBaseController.logger.isDebugEnabled())
					NetworkStatusBaseController.logger.debug("On Window Close Event, REMOVE listener ?"
							+ NetworkStatusBaseController.this.removeListenerWhenCloseWindow);
				if(NetworkStatusBaseController.this.removeListenerWhenCloseWindow.booleanValue())
					NetworkStatusBaseController.this.tkStrikeCommunicationService.removeListener(listener);
				NetworkStatusBaseController.this._customOnWindowsCloseEvent();
			}
		};
	}

	@Override
	public final void initialize(URL url, ResourceBundle resourceBundle) {
		_customInitialize(url, resourceBundle);
	}

	@Override
	public final void afterPropertiesSet() throws Exception {
		this.tkStrikeCommunicationService.addListener(this);
		this.globalNetworkStatusController.addListener(this);
		_customAfterPropertiesSet();
	}

	protected final void _bindControls() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if(NetworkStatusBaseController.this.hasUIJudges()) {
					NetworkStatusBaseController.this.txtJ1Battery().textProperty().bind(NetworkStatusBaseController.this.j1Battery);
					NetworkStatusBaseController.this.txtJ2Battery().textProperty().bind(NetworkStatusBaseController.this.j2Battery);
					NetworkStatusBaseController.this.txtJ3Battery().textProperty().bind(NetworkStatusBaseController.this.j3Battery);
					NetworkStatusBaseController.this.hbJudge1().visibleProperty().bind(
							NetworkStatusBaseController.this.currentNetworkConfiguration.judge1EnabledProperty);
					NetworkStatusBaseController.this.hbJudge2().visibleProperty().bind(
							NetworkStatusBaseController.this.currentNetworkConfiguration.judge2EnabledProperty);
					NetworkStatusBaseController.this.hbJudge3().visibleProperty().bind(
							NetworkStatusBaseController.this.currentNetworkConfiguration.judge3EnabledProperty);
				}
				if(NetworkStatusBaseController.this.hasUIAthletes()) {
					NetworkStatusBaseController.this.txtG1BBBattery().textProperty().bind(NetworkStatusBaseController.this.g1BBBattery);
					NetworkStatusBaseController.this.txtG1HBBattery().textProperty().bind(NetworkStatusBaseController.this.g1HBBattery);
					NetworkStatusBaseController.this.txtG1BRBattery().textProperty().bind(NetworkStatusBaseController.this.g1BRBattery);
					NetworkStatusBaseController.this.txtG1HRBattery().textProperty().bind(NetworkStatusBaseController.this.g1HRBattery);
					NetworkStatusBaseController.this.txtG2BBBattery().textProperty().bind(NetworkStatusBaseController.this.g2BBBattery);
					NetworkStatusBaseController.this.txtG2HBBattery().textProperty().bind(NetworkStatusBaseController.this.g2HBBattery);
					NetworkStatusBaseController.this.txtG2BRBattery().textProperty().bind(NetworkStatusBaseController.this.g2BRBattery);
					NetworkStatusBaseController.this.txtG2HRBattery().textProperty().bind(NetworkStatusBaseController.this.g2HRBattery);
				}
				NetworkStatusBaseController.this._customBindControls();
			}
		});
	}

	@Override
	public void hasNetworkOkEvent(GlobalNetworkStatusControllerListener.NetworkOkEvent networkOkEvent) {}

	@Override
	public void hasNewNodeNetworkErrorEvent(final GlobalNetworkStatusControllerListener.NodeNetworkErrorEvent nodeNetworkErrorEvent) {
		if(nodeNetworkErrorEvent != null && nodeNetworkErrorEvent.getNetworkNode() != null) {
			Rectangle rectangle = null;
			Shape shape = null;
			SimpleStringProperty pctProperty = null;
			final boolean offLine = nodeNetworkErrorEvent.getNetworkErrorCause().equals(NetworkErrorCause.NetworkErrorCauseType.LOST_CONNECTION);
			final boolean sensorKO = nodeNetworkErrorEvent.getNetworkErrorCause().equals(NetworkErrorCause.NetworkErrorCauseType.SENSOR_ERROR);
			if(hasUIJudges())
				if(nodeNetworkErrorEvent.getNetworkNode().getNodeId().equals(this.currentNetworkConfiguration.getJudge1NodeId())) {
					rectangle = stJ1();
					pctProperty = this.j1Battery;
				} else if(nodeNetworkErrorEvent.getNetworkNode().getNodeId().equals(this.currentNetworkConfiguration.getJudge2NodeId())) {
					rectangle = stJ2();
					pctProperty = this.j2Battery;
				} else if(nodeNetworkErrorEvent.getNetworkNode().getNodeId().equals(this.currentNetworkConfiguration.getJudge3NodeId())) {
					rectangle = stJ3();
					pctProperty = this.j3Battery;
				}
			if(hasUIAthletes())
				if(this.currentNetworkConfiguration.getGroup1Config() != null && nodeNetworkErrorEvent.getNetworkNode().getNodeId().equals(
						this.currentNetworkConfiguration.getGroup1Config().getBodyBlueNodeId())) {
					rectangle = stG1BB();
					pctProperty = this.g1BBBattery;
				} else if(this.currentNetworkConfiguration.getGroup1Config() != null && nodeNetworkErrorEvent.getNetworkNode().getNodeId().equals(
						this.currentNetworkConfiguration.getGroup1Config().getHeadBlueNodeId())) {
					rectangle = stG1HB();
					pctProperty = this.g1HBBattery;
				} else if(this.currentNetworkConfiguration.getGroup1Config() != null && nodeNetworkErrorEvent.getNetworkNode().getNodeId().equals(
						this.currentNetworkConfiguration.getGroup1Config().getBodyRedNodeId())) {
					rectangle = stG1BR();
					pctProperty = this.g1BRBattery;
				} else if(this.currentNetworkConfiguration.getGroup1Config() != null && nodeNetworkErrorEvent.getNetworkNode().getNodeId().equals(
						this.currentNetworkConfiguration.getGroup1Config().getHeadRedNodeId())) {
					rectangle = stG1HR();
					pctProperty = this.g1HRBattery;
				} else if(this.currentNetworkConfiguration.getGroup2Config() != null && nodeNetworkErrorEvent.getNetworkNode().getNodeId().equals(
						this.currentNetworkConfiguration.getGroup2Config().getBodyBlueNodeId())) {
					rectangle = stG2BB();
					pctProperty = this.g2BBBattery;
				} else if(this.currentNetworkConfiguration.getGroup2Config() != null && nodeNetworkErrorEvent.getNetworkNode().getNodeId().equals(
						this.currentNetworkConfiguration.getGroup2Config().getHeadBlueNodeId())) {
					rectangle = stG2HB();
					pctProperty = this.g2HBBattery;
				} else if(this.currentNetworkConfiguration.getGroup2Config() != null && nodeNetworkErrorEvent.getNetworkNode().getNodeId().equals(
						this.currentNetworkConfiguration.getGroup2Config().getBodyRedNodeId())) {
					rectangle = stG2BR();
					pctProperty = this.g2BRBattery;
				} else if(this.currentNetworkConfiguration.getGroup2Config() != null && nodeNetworkErrorEvent.getNetworkNode().getNodeId().equals(
						this.currentNetworkConfiguration.getGroup2Config().getHeadRedNodeId())) {
					rectangle = stG2HR();
					pctProperty = this.g2HRBattery;
				}
			if(rectangle != null) {
				final Rectangle theShape = rectangle;
				final SimpleStringProperty thePctProperty = pctProperty;
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						if(offLine) {
							theShape.setFill(Color.RED);
							thePctProperty.set("");
						} else if(sensorKO) {
							if(NetworkStatusBaseController.this.isTkStrikeGen2Version()) {
								thePctProperty.set((nodeNetworkErrorEvent.getNetworkNode().getBatteryPct() <= 20.0D) ? "LOW" : "OK");
							} else {
								thePctProperty.set(CommonTkStrikeBaseController.pctFormat.format(nodeNetworkErrorEvent.getNetworkNode()
										.getBatteryPct()));
							}
							theShape.setFill(Color.ORANGE);
						}
					}
				});
			}
		}
	}

	@Override
	public void hasNewGlobalStatusEvent(StatusEvent statusEvent) {
		if(statusEvent != null && statusEvent.getNodeId() != null) {
			Rectangle rectangle = null;
			final boolean sensorOk = statusEvent.getSensorOk().booleanValue();
			final boolean connOffline = statusEvent.getConnOffline().booleanValue();
			final double progress = statusEvent.getNodeBatteryPct().doubleValue();
			SimpleStringProperty pctProperty = null;
			Shape shape = null;
			if(hasUIJudges())
				if(statusEvent.getNodeId().equals(this.currentNetworkConfiguration.getJudge1NodeId())) {
					rectangle = stJ1();
					pctProperty = this.j1Battery;
				} else if(statusEvent.getNodeId().equals(this.currentNetworkConfiguration.getJudge2NodeId())) {
					rectangle = stJ2();
					pctProperty = this.j2Battery;
				} else if(statusEvent.getNodeId().equals(this.currentNetworkConfiguration.getJudge3NodeId())) {
					rectangle = stJ3();
					pctProperty = this.j3Battery;
				}
			if(hasUIAthletes())
				if(this.currentNetworkConfiguration.getGroup1Config() != null && statusEvent.getNodeId().equals(this.currentNetworkConfiguration
						.getGroup1Config().getBodyBlueNodeId())) {
					rectangle = stG1BB();
					pctProperty = this.g1BBBattery;
				} else if(this.currentNetworkConfiguration.getGroup1Config() != null && statusEvent.getNodeId().equals(
						this.currentNetworkConfiguration.getGroup1Config().getHeadBlueNodeId())) {
					rectangle = stG1HB();
					pctProperty = this.g1HBBattery;
				} else if(this.currentNetworkConfiguration.getGroup1Config() != null && statusEvent.getNodeId().equals(
						this.currentNetworkConfiguration.getGroup1Config().getBodyRedNodeId())) {
					rectangle = stG1BR();
					pctProperty = this.g1BRBattery;
				} else if(this.currentNetworkConfiguration.getGroup1Config() != null && statusEvent.getNodeId().equals(
						this.currentNetworkConfiguration.getGroup1Config().getHeadRedNodeId())) {
					rectangle = stG1HR();
					pctProperty = this.g1HRBattery;
				} else if(this.currentNetworkConfiguration.getGroup2Config() != null && statusEvent.getNodeId().equals(
						this.currentNetworkConfiguration.getGroup2Config().getBodyBlueNodeId())) {
					rectangle = stG2BB();
					pctProperty = this.g2BBBattery;
				} else if(this.currentNetworkConfiguration.getGroup2Config() != null && statusEvent.getNodeId().equals(
						this.currentNetworkConfiguration.getGroup2Config().getHeadBlueNodeId())) {
					rectangle = stG2HB();
					pctProperty = this.g2HBBattery;
				} else if(this.currentNetworkConfiguration.getGroup2Config() != null && statusEvent.getNodeId().equals(
						this.currentNetworkConfiguration.getGroup2Config().getBodyRedNodeId())) {
					rectangle = stG2BR();
					pctProperty = this.g2BRBattery;
				} else if(this.currentNetworkConfiguration.getGroup2Config() != null && statusEvent.getNodeId().equals(
						this.currentNetworkConfiguration.getGroup2Config().getHeadRedNodeId())) {
					rectangle = stG2HR();
					pctProperty = this.g2HRBattery;
				}
			if(rectangle != null) {
				final Rectangle theShape = rectangle;
				final SimpleStringProperty fiPctProperty = pctProperty;
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						if(NetworkStatusBaseController.this.isTkStrikeGen2Version()) {
							fiPctProperty.set((progress <= 20.0D) ? "LOW" : "OK");
						} else {
							fiPctProperty.set(CommonTkStrikeBaseController.pctFormat.format(progress));
						}
						if(connOffline) {
							fiPctProperty.set("");
							theShape.setFill(Color.RED);
						} else if(sensorOk && progress > 20.0D) {
							theShape.setFill(Color.GREEN);
						} else {
							theShape.setFill(Color.ORANGE);
						}
					}
				});
			}
		}
	}

	@Override
	public final void hasNewStatusEvent(StatusEvent statusEvent) {}

	@Override
	public final void hasChangeNetworkStatusEvent(final ChangeNetworkStatusEvent changeNetworkStatusEvent) {
		if(hasUIStatus())
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					NetworkStatusBaseController.this.txtNetworkStatus().setText(NetworkStatusBaseController.this.getMessage("label.networkStatus."
							+ changeNetworkStatusEvent.getNetworkStatus().toString()));
					switch(changeNetworkStatusEvent.getNetworkStatus()) {
						case OK:
							NetworkStatusBaseController.this.reNetworkStatus().setFill(Color.GREEN);
							NetworkStatusBaseController.this.getAppStatusWorker().setNetworkConfigurationEntry(NetworkStatusBaseController.this
									.getCurrentNetworkConfiguration());
							break;
						case ANALYZING:
							NetworkStatusBaseController.this.reNetworkStatus().setFill(Color.ORANGE);
							break;
						case NOT_RECOGNIZED:
						case NOT_STARTED:
							NetworkStatusBaseController.this.reNetworkStatus().setFill(Color.RED);
							break;
						case NOT_CONNECTED:
							NetworkStatusBaseController.this.reNetworkStatus().setFill(Color.web("cdcfd0"));
							break;
					}
					NetworkStatusBaseController.this._customHasChangeNetworkStatusEvent(changeNetworkStatusEvent);
				}
			});
	}

	@Override
	public void hasChangeNetworkConfigurationEvent(ChangeNetworkConfigurationEvent changeNetworkConfigurationEvent) {}

	protected abstract boolean hasUIStatus();

	protected abstract boolean hasUIJudges();

	protected abstract boolean hasUIAthletes();

	protected abstract Text txtNetworkStatus();

	protected abstract Rectangle reNetworkStatus();

	protected abstract Pane hbJudge1();

	protected abstract Rectangle stJ1();

	protected abstract Text txtJ1Battery();

	protected abstract Pane hbJudge2();

	protected abstract Rectangle stJ2();

	protected abstract Text txtJ2Battery();

	protected abstract Pane hbJudge3();

	protected abstract Rectangle stJ3();

	protected abstract Text txtJ3Battery();

	protected abstract Pane hbG1BB();

	protected abstract Rectangle stG1BB();

	protected abstract Text txtG1BBBattery();

	protected abstract Pane hbG1HB();

	protected abstract Rectangle stG1HB();

	protected abstract Text txtG1HBBattery();

	protected abstract Pane hbG1BR();

	protected abstract Rectangle stG1BR();

	protected abstract Text txtG1BRBattery();

	protected abstract Pane hbG1HR();

	protected abstract Rectangle stG1HR();

	protected abstract Text txtG1HRBattery();

	protected abstract Pane hbG2BB();

	protected abstract Rectangle stG2BB();

	protected abstract Text txtG2BBBattery();

	protected abstract Pane hbG2HB();

	protected abstract Rectangle stG2HB();

	protected abstract Text txtG2HBBattery();

	protected abstract Pane hbG2BR();

	protected abstract Rectangle stG2BR();

	protected abstract Text txtG2BRBattery();

	protected abstract Pane hbG2HR();

	protected abstract Rectangle stG2HR();

	protected abstract Text txtG2HRBattery();

	protected abstract void _customOnWindowShowEvent();

	protected abstract void _customOnWindowsCloseEvent();

	protected abstract void _customInitialize(URL paramURL, ResourceBundle paramResourceBundle);

	protected abstract void _customAfterPropertiesSet() throws Exception;

	protected abstract void _customBindControls();

	protected abstract void _customHasChangeNetworkStatusEvent(ChangeNetworkStatusEvent paramChangeNetworkStatusEvent);
}
