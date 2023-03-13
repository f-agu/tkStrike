package com.xtremis.daedo.tkstrike.ui.controller.configuration;

import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.xtremis.daedo.tkstrike.communication.ChangeNetworkStatusEvent;
import com.xtremis.daedo.tkstrike.communication.DataEvent;
import com.xtremis.daedo.tkstrike.communication.NetworkConfigurationDto;
import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationException;
import com.xtremis.daedo.tkstrike.configuration.TkStrikeCommunicationTypeUtil;
import com.xtremis.daedo.tkstrike.configuration.TkStrikeCommunicationTypeValue;
import com.xtremis.daedo.tkstrike.service.MatchWorker;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.TkStrikeKeyCombinationsHelper;
import com.xtremis.daedo.tkstrike.ui.controller.NetworkStatusBaseController;
import com.xtremis.daedo.tkstrike.ui.model.FormValidationError;
import com.xtremis.daedo.tkstrike.ui.model.NetworkAthletesGroupConfigEntry;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;


@Component
public class ConfigurationNetworkController extends NetworkStatusBaseController {

	protected static final Logger logger = Logger.getLogger(ConfigurationNetworkController.class);

	@Autowired
	private MatchWorker matchWorker;

	@FXML
	private Node rootView;

	@FXML
	private Node mainView;

	@FXML
	private Pane piPanel;

	@FXML
	private Button btStartNetwork;

	@FXML
	private ToggleButton tgAllowNetworkErrors;

	@FXML
	private ToggleButton tgCommunicationType;

	@Autowired
	@Qualifier("tkStrikeCommunicationType")
	private TkStrikeCommunicationTypeValue tkStrikeCommunicationTypeValue;

	@FXML
	private Rectangle reNetworkStatus;

	@FXML
	private Text txtNetworkStatus;

	@FXML
	private ComboBox<Integer> cmbChannel;

	@FXML
	private ComboBox<Integer> cmbNJudges;

	@FXML
	private Pane hbJudge1;

	@FXML
	private TextField txtJ1;

	@FXML
	private Rectangle stJ1;

	@FXML
	private Text txtJ1Battery;

	@FXML
	private Pane hbJudge2;

	@FXML
	private TextField txtJ2;

	@FXML
	private Rectangle stJ2;

	@FXML
	private Text txtJ2Battery;

	@FXML
	private Pane hbJudge3;

	@FXML
	private TextField txtJ3;

	@FXML
	private Rectangle stJ3;

	@FXML
	private Text txtJ3Battery;

	@FXML
	private ToggleButton tgBodySensors;

	@FXML
	private ToggleButton tgHeadSensors;

	@FXML
	private Pane hbG1BB;

	@FXML
	private TextField txtG1BB;

	@FXML
	private Rectangle stG1BB;

	@FXML
	private Text txtG1BBBattery;

	@FXML
	private Pane hbG1HB;

	@FXML
	private TextField txtG1HB;

	@FXML
	private Rectangle stG1HB;

	@FXML
	private Text txtG1HBBattery;

	@FXML
	private Pane hbG1BR;

	@FXML
	private TextField txtG1BR;

	@FXML
	private Rectangle stG1BR;

	@FXML
	private Text txtG1BRBattery;

	@FXML
	private Pane hbG1HR;

	@FXML
	private TextField txtG1HR;

	@FXML
	private Rectangle stG1HR;

	@FXML
	private Text txtG1HRBattery;

	@FXML
	private ToggleButton tgG2;

	@FXML
	private Pane hbG2BB;

	@FXML
	private TextField txtG2BB;

	@FXML
	private Rectangle stG2BB;

	@FXML
	private Text txtG2BBBattery;

	@FXML
	private Pane hbG2HB;

	@FXML
	private TextField txtG2HB;

	@FXML
	private Rectangle stG2HB;

	@FXML
	private Text txtG2HBBattery;

	@FXML
	private Pane hbG2BR;

	@FXML
	private TextField txtG2BR;

	@FXML
	private Rectangle stG2BR;

	@FXML
	private Text txtG2BRBattery;

	@FXML
	private Pane hbG2HR;

	@FXML
	private TextField txtG2HR;

	@FXML
	private Rectangle stG2HR;

	@FXML
	private Text txtG2HRBattery;

	protected void showProgressIndicator(final boolean show) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				ConfigurationNetworkController.this.mainView.setVisible( ! show);
				ConfigurationNetworkController.this.piPanel.setVisible(show);
			}
		});
	}

	@Override
	protected void _customOnWindowShowEvent() {
		this.btStartNetwork.requestFocus();
		Platform.runLater(new Runnable() {

			@Override
			public void run() {}
		});
	}

	@Override
	protected void _customOnWindowsCloseEvent() {}

	public void doStartNetwork() {
		if(isFormValid()) {
			showProgressIndicator(true);
			TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					NetworkConfigurationDto ncDto = ConfigurationNetworkController.this.currentNetworkConfiguration.getNetworkConfigurationDto();
					try {
						ConfigurationNetworkController.this.tkStrikeCommunicationService.startNetwork(ncDto);
						ConfigurationNetworkController.this.getAppStatusWorker().setNetworkConfigurationEntry(
								ConfigurationNetworkController.this.currentNetworkConfiguration);
						ConfigurationNetworkController.this.currentNetworkConfiguration.networkWasStartedProperty.set(true);
						try {
							ConfigurationNetworkController.this.networkConfigurationService.update(
									ConfigurationNetworkController.this.currentNetworkConfiguration.getNetworkConfiguration());
						} catch(TkStrikeServiceException e) {
							e.printStackTrace();
						}
						ConfigurationNetworkController.this.showProgressIndicator(false);
					} catch(TkStrikeCommunicationException e) {
						ConfigurationNetworkController.this.showErrorDialog(ConfigurationNetworkController.this.getMessage("title.default.error"),
								ConfigurationNetworkController.this.getMessage("message.error.serialComm"));
					} finally {
						ConfigurationNetworkController.this.showProgressIndicator(false);
					}
					return null;
				}
			});
		}
	}

	public void doTryToRecognize() {
		showProgressIndicator(true);
		TkStrikeExecutors.executeInThreadPool(new Runnable() {

			@Override
			public void run() {
				try {
					ConfigurationNetworkController.this.tkStrikeCommunicationService.tryToRecognizeWithConfig(
							ConfigurationNetworkController.this.currentNetworkConfiguration.getNetworkConfigurationDto(), true);
				} catch(TkStrikeCommunicationException e) {
					ConfigurationNetworkController.this.showErrorDialog(ConfigurationNetworkController.this.getMessage("title.default.error"),
							ConfigurationNetworkController.this.getMessage("message.error.serialComm"));
				} finally {
					ConfigurationNetworkController.this.showProgressIndicator(false);
				}
			}
		});
	}

	@Override
	protected Collection<Control> getFormControls() {
		return FXCollections.observableArrayList(new Control[] {this.txtJ1, this.txtJ2, this.txtJ3, this.txtG1BB, this.txtG1HB, this.txtG1BR,
				this.txtG1HR});
	}

	@Override
	public LinkedHashSet<FormValidationError> validateForm() {
		LinkedHashSet<FormValidationError> res = null;
		if(this.cmbNJudges.getValue().intValue() > 0 &&
				StringUtils.isBlank(this.txtJ1.getText())) {
			res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.currentNetworkConfiguration, "judge1NodeId", this.txtJ1, getMessage("validation.required")));
		}
		if(this.cmbNJudges.getValue().intValue() > 1 &&
				StringUtils.isBlank(this.txtJ2.getText())) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.currentNetworkConfiguration, "judge2NodeId", this.txtJ2, getMessage("validation.required")));
		}
		if(this.cmbNJudges.getValue().intValue() > 2 &&
				StringUtils.isBlank(this.txtJ3.getText())) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.currentNetworkConfiguration, "judge3NodeId", this.txtJ3, getMessage("validation.required")));
		}
		if(this.tgBodySensors.isSelected()) {
			if(StringUtils.isBlank(this.txtG1BB.getText())) {
				if(res == null)
					res = new LinkedHashSet<>();
				res.add(new FormValidationError(this.currentNetworkConfiguration, "group1Config.bodyBlueNodeId", this.txtG1BB, getMessage(
						"validation.required")));
			}
			if(StringUtils.isBlank(this.txtG1BR.getText())) {
				if(res == null)
					res = new LinkedHashSet<>();
				res.add(new FormValidationError(this.currentNetworkConfiguration, "group1Config.bodyRedNodeId", this.txtG1BR, getMessage(
						"validation.required")));
			}
		}
		if(this.tgHeadSensors.isSelected()) {
			if(StringUtils.isBlank(this.txtG1HB.getText())) {
				if(res == null)
					res = new LinkedHashSet<>();
				res.add(new FormValidationError(this.currentNetworkConfiguration, "group1Config.headBlueNodeId", this.txtG1HB, getMessage(
						"validation.required")));
			}
			if(StringUtils.isBlank(this.txtG1HR.getText())) {
				if(res == null)
					res = new LinkedHashSet<>();
				res.add(new FormValidationError(this.currentNetworkConfiguration, "group1Config.headRedNodeId", this.txtG1HR, getMessage(
						"validation.required")));
			}
		}
		if(isTkStrikeGen2Version() && this.currentNetworkConfiguration.getGroup2Enabled().booleanValue()) {
			if(this.tgHeadSensors.isSelected()) {
				if(StringUtils.isBlank(this.txtG2HB.getText())) {
					if(res == null)
						res = new LinkedHashSet<>();
					res.add(new FormValidationError(this.currentNetworkConfiguration, "group2Config.headBlueNodeId", this.txtG2HB, getMessage(
							"validation.required")));
				}
				if(StringUtils.isBlank(this.txtG2HR.getText())) {
					if(res == null)
						res = new LinkedHashSet<>();
					res.add(new FormValidationError(this.currentNetworkConfiguration, "group2Config.headRedNodeId", this.txtG2HR, getMessage(
							"validation.required")));
				}
			}
			if(this.tgBodySensors.isSelected()) {
				if(StringUtils.isBlank(this.txtG2BB.getText())) {
					if(res == null)
						res = new LinkedHashSet<>();
					res.add(new FormValidationError(this.currentNetworkConfiguration, "group2Config.bodyBlueNodeId", this.txtG2BB, getMessage(
							"validation.required")));
				}
				if(StringUtils.isBlank(this.txtG2BR.getText())) {
					if(res == null)
						res = new LinkedHashSet<>();
					res.add(new FormValidationError(this.currentNetworkConfiguration, "group2Config.bodyRedNodeId", this.txtG2BR, getMessage(
							"validation.required")));
				}
			}
		}
		return res;
	}

	@Override
	protected void _customInitialize(URL url, ResourceBundle resourceBundle) {
		this.tgBodySensors.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isSelected) {
				if(isSelected.booleanValue()) {
					ConfigurationNetworkController.this.tgBodySensors.setText(ConfigurationNetworkController.this.getMessage(
							"toggle.bodySensors.enabled"));
				} else {
					ConfigurationNetworkController.this.tgBodySensors.setText(ConfigurationNetworkController.this.getMessage(
							"toggle.bodySensors.disabled"));
					ConfigurationNetworkController.this.tgHeadSensors.setSelected(false);
				}
				ConfigurationNetworkController.this.hbG2BB.setVisible((ConfigurationNetworkController.this.tgG2.isSelected()
						&& ConfigurationNetworkController.this.tgBodySensors.isSelected()));
				ConfigurationNetworkController.this.hbG2BR.setVisible((ConfigurationNetworkController.this.tgG2.isSelected()
						&& ConfigurationNetworkController.this.tgBodySensors.isSelected()));
				ConfigurationNetworkController.this.hbG2HB.setVisible((ConfigurationNetworkController.this.tgG2.isSelected()
						&& ConfigurationNetworkController.this.tgHeadSensors.isSelected()));
				ConfigurationNetworkController.this.hbG2HR.setVisible((ConfigurationNetworkController.this.tgG2.isSelected()
						&& ConfigurationNetworkController.this.tgHeadSensors.isSelected()));
			}
		});
		this.hbG1BB.visibleProperty().bind(this.tgBodySensors.selectedProperty());
		this.hbG1BR.visibleProperty().bind(this.tgBodySensors.selectedProperty());
		this.tgHeadSensors.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isSelected) {
				if(isSelected.booleanValue()) {
					ConfigurationNetworkController.this.tgHeadSensors.setText(ConfigurationNetworkController.this.getMessage(
							"toggle.headSensors.enabled"));
				} else {
					ConfigurationNetworkController.this.tgHeadSensors.setText(ConfigurationNetworkController.this.getMessage(
							"toggle.headSensors.disabled"));
				}
				ConfigurationNetworkController.this.hbG2BB.setVisible((ConfigurationNetworkController.this.tgG2.isSelected()
						&& ConfigurationNetworkController.this.tgBodySensors.isSelected()));
				ConfigurationNetworkController.this.hbG2BR.setVisible((ConfigurationNetworkController.this.tgG2.isSelected()
						&& ConfigurationNetworkController.this.tgBodySensors.isSelected()));
				ConfigurationNetworkController.this.hbG2HB.setVisible((ConfigurationNetworkController.this.tgG2.isSelected()
						&& ConfigurationNetworkController.this.tgHeadSensors.isSelected()));
				ConfigurationNetworkController.this.hbG2HR.setVisible((ConfigurationNetworkController.this.tgG2.isSelected()
						&& ConfigurationNetworkController.this.tgHeadSensors.isSelected()));
			}
		});
		this.hbG1HB.visibleProperty().bind(this.tgHeadSensors.selectedProperty());
		this.hbG1HR.visibleProperty().bind(this.tgHeadSensors.selectedProperty());
		this.tgG2.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isSelected) {
				boolean bodyEnabled = true;
				boolean headEnabled = true;
				if(isSelected.booleanValue()) {
					ConfigurationNetworkController.this.tgG2.setText(ConfigurationNetworkController.this.getMessage("toggle.group2.enabled"));
					bodyEnabled = ConfigurationNetworkController.this.tgBodySensors.isSelected();
					headEnabled = ConfigurationNetworkController.this.tgHeadSensors.isSelected();
				} else {
					ConfigurationNetworkController.this.tgG2.setText(ConfigurationNetworkController.this.getMessage("toggle.group2.disabled"));
					ConfigurationNetworkController.this.txtG2BB.setText("0");
					ConfigurationNetworkController.this.txtG2BR.setText("0");
					ConfigurationNetworkController.this.txtG2HB.setText("0");
					ConfigurationNetworkController.this.txtG2HR.setText("0");
					bodyEnabled = false;
					headEnabled = false;
				}
				(ConfigurationNetworkController.this.currentNetworkConfiguration.getGroup2Config()).bodySensorsEnabledProperty.set(bodyEnabled);
				(ConfigurationNetworkController.this.currentNetworkConfiguration.getGroup2Config()).headSensorsEnabledProperty.set(headEnabled);
				ConfigurationNetworkController.this.hbG2BB.setVisible((ConfigurationNetworkController.this.tgG2.isSelected()
						&& ConfigurationNetworkController.this.tgBodySensors.isSelected()));
				ConfigurationNetworkController.this.hbG2BR.setVisible((ConfigurationNetworkController.this.tgG2.isSelected()
						&& ConfigurationNetworkController.this.tgBodySensors.isSelected()));
				ConfigurationNetworkController.this.hbG2HB.setVisible((ConfigurationNetworkController.this.tgG2.isSelected()
						&& ConfigurationNetworkController.this.tgHeadSensors.isSelected()));
				ConfigurationNetworkController.this.hbG2HR.setVisible((ConfigurationNetworkController.this.tgG2.isSelected()
						&& ConfigurationNetworkController.this.tgHeadSensors.isSelected()));
			}
		});
		int i;
		for(i = 1; i <= 15; i++)
			this.cmbChannel.getItems().add(Integer.valueOf(i));
		for(i = 0; i <= 3; i++)
			this.cmbNJudges.getItems().add(Integer.valueOf(i));
		this.cmbNJudges.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {

			@Override
			public void changed(ObservableValue<? extends Integer> observableValue, Integer integer, Integer t1) {
				switch(t1.intValue()) {
					case 0:
						ConfigurationNetworkController.this.currentNetworkConfiguration.judge1EnabledProperty.set(false);
						ConfigurationNetworkController.this.currentNetworkConfiguration.judge2EnabledProperty.set(false);
						ConfigurationNetworkController.this.currentNetworkConfiguration.judge3EnabledProperty.set(false);
						break;
					case 1:
						ConfigurationNetworkController.this.currentNetworkConfiguration.judge1EnabledProperty.set(true);
						ConfigurationNetworkController.this.currentNetworkConfiguration.judge2EnabledProperty.set(false);
						ConfigurationNetworkController.this.currentNetworkConfiguration.judge3EnabledProperty.set(false);
						break;
					case 2:
						ConfigurationNetworkController.this.currentNetworkConfiguration.judge1EnabledProperty.set(true);
						ConfigurationNetworkController.this.currentNetworkConfiguration.judge2EnabledProperty.set(true);
						ConfigurationNetworkController.this.currentNetworkConfiguration.judge3EnabledProperty.set(false);
						break;
					case 3:
						ConfigurationNetworkController.this.currentNetworkConfiguration.judge1EnabledProperty.set(true);
						ConfigurationNetworkController.this.currentNetworkConfiguration.judge2EnabledProperty.set(true);
						ConfigurationNetworkController.this.currentNetworkConfiguration.judge3EnabledProperty.set(true);
						break;
				}
			}
		});
	}

	@Override
	protected void _customAfterPropertiesSet() throws Exception {
		this.rootView.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if(TkStrikeKeyCombinationsHelper.keyCombSIMULATOR.match(event)) {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							ConfigurationNetworkController.this.tgCommunicationType.setVisible(
									! ConfigurationNetworkController.this.tgCommunicationType.isVisible());
							if(ConfigurationNetworkController.this.tgCommunicationType.isVisible())
								ConfigurationNetworkController.this.tgAllowNetworkErrors.setVisible(false);
						}
					});
				} else if(TkStrikeKeyCombinationsHelper.keyCombALLOW_NETWORK_ERRORS.match(event)) {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							ConfigurationNetworkController.this.tgAllowNetworkErrors.setVisible(
									! ConfigurationNetworkController.this.tgAllowNetworkErrors.isVisible());
							if(ConfigurationNetworkController.this.tgAllowNetworkErrors.isVisible())
								ConfigurationNetworkController.this.tgCommunicationType.setVisible(false);
						}
					});
				}
			}
		});
		this.tgAllowNetworkErrors.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if(newValue.booleanValue()) {
					ConfigurationNetworkController.this.tgAllowNetworkErrors.setText(ConfigurationNetworkController.this.getMessage(
							"toggle.networkErrorOnMatch.allow"));
				} else {
					ConfigurationNetworkController.this.tgAllowNetworkErrors.setText(ConfigurationNetworkController.this.getMessage(
							"toggle.networkErrorOnMatch.disallow"));
				}
			}
		});
		this.matchWorker.allowNetworkErrorProperty().bindBidirectional(this.tgAllowNetworkErrors.selectedProperty());
		this.tgCommunicationType.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if(newValue.booleanValue()) {
					ConfigurationNetworkController.this.tgCommunicationType.setText(ConfigurationNetworkController.this.getMessage(
							"toggle.communicationType.simulator"));
				} else {
					ConfigurationNetworkController.this.tgCommunicationType.setText(ConfigurationNetworkController.this.getMessage(
							"toggle.communicationType.normal"));
				}
			}
		});
		this.tgCommunicationType.setVisible(false);
		this.tgCommunicationType.setSelected(TkStrikeCommunicationTypeValue.SIMULATOR.equals(this.tkStrikeCommunicationTypeValue));
		this.tgCommunicationType.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if(ConfigurationNetworkController.this.showConfirmDialog(ConfigurationNetworkController.this.getMessage("title.default.question"),
						ConfigurationNetworkController.this
								.getMessage("message.tkStrike.communicationType.changes")).get().equals(ButtonType.OK)) {
					TkStrikeCommunicationTypeValue newValue = TkStrikeCommunicationTypeValue.SIMULATOR;
					if( ! ConfigurationNetworkController.this.tgCommunicationType.isSelected())
						newValue = TkStrikeCommunicationTypeValue.NORMAL;
					try {
						TkStrikeCommunicationTypeUtil.getInstance().setTkStrikeCommunicationType(newValue);
					} catch(Exception e) {
						ConfigurationNetworkController.this.manageException(e, "ChangeCommunicationType", null);
					}
					ConfigurationNetworkController.this.getAppStatusWorker().doForceExitTkStrike();
				} else {
					ConfigurationNetworkController.this.tgCommunicationType.setSelected( ! ConfigurationNetworkController.this.tgCommunicationType
							.isSelected());
				}
			}
		});
	}

	@Override
	protected void _customBindControls() {
		this.cmbChannel.valueProperty().bindBidirectional(this.currentNetworkConfiguration.getChannelNumberProperty());
		this.cmbNJudges.valueProperty().bindBidirectional(this.currentNetworkConfiguration.getJudgesNumberProperty());
		this.txtJ1.textProperty().bindBidirectional(this.currentNetworkConfiguration.judge1NodeIdProperty);
		this.txtJ2.textProperty().bindBidirectional(this.currentNetworkConfiguration.judge2NodeIdProperty);
		this.txtJ3.textProperty().bindBidirectional(this.currentNetworkConfiguration.judge3NodeIdProperty);
		NetworkAthletesGroupConfigEntry group1Config = this.currentNetworkConfiguration.group1ConfigProperty.get();
		NetworkAthletesGroupConfigEntry group2Config = this.currentNetworkConfiguration.group2ConfigProperty.get();
		this.tgBodySensors.selectedProperty().bindBidirectional(group1Config.bodySensorsEnabledProperty);
		this.tgHeadSensors.selectedProperty().bindBidirectional(group1Config.headSensorsEnabledProperty);
		this.txtG1BB.textProperty().bindBidirectional(group1Config.bodyBlueNodeIdProperty);
		this.txtG1HB.textProperty().bindBidirectional(group1Config.headBlueNodeIdProperty);
		this.txtG1BR.textProperty().bindBidirectional(group1Config.bodyRedNodeIdProperty);
		this.txtG1HR.textProperty().bindBidirectional(group1Config.headRedNodeIdProperty);
		this.tgG2.selectedProperty().bindBidirectional(this.currentNetworkConfiguration.group2EnabledProperty);
		this.txtG2BB.textProperty().bindBidirectional(group2Config.bodyBlueNodeIdProperty);
		this.txtG2HB.textProperty().bindBidirectional(group2Config.headBlueNodeIdProperty);
		this.txtG2BR.textProperty().bindBidirectional(group2Config.bodyRedNodeIdProperty);
		this.txtG2HR.textProperty().bindBidirectional(group2Config.headRedNodeIdProperty);
	}

	@Override
	public Node getRootView() {
		return this.rootView;
	}

	@Override
	public void hasNewDataEvent(DataEvent dataEvent) {}

	@Override
	protected void _customHasChangeNetworkStatusEvent(ChangeNetworkStatusEvent changeNetworkStatusEvent) {}

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

	private class Group2NodeTextChangeListener implements ChangeListener<String> {

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			if(isNodeEnabled(ConfigurationNetworkController.this.txtG2BB.getText()) &&
					isNodeEnabled(ConfigurationNetworkController.this.txtG2HB.getText()) &&
					isNodeEnabled(ConfigurationNetworkController.this.txtG2BR.getText()) &&
					isNodeEnabled(ConfigurationNetworkController.this.txtG2HR.getText())) {
				ConfigurationNetworkController.this.currentNetworkConfiguration.group2EnabledProperty.set(true);
			} else {
				ConfigurationNetworkController.this.currentNetworkConfiguration.group2EnabledProperty.set(false);
			}
		}

		private boolean isNodeEnabled(String value) {
			return (StringUtils.isNotBlank(value) && ! "0".equals(value));
		}
	}
}
