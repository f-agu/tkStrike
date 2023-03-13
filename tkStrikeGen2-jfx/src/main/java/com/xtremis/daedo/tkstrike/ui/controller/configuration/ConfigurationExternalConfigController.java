package com.xtremis.daedo.tkstrike.ui.controller.configuration;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xtremis.daedo.tkstrike.ei.client.RtBroadcastSocketClient;
import com.xtremis.daedo.tkstrike.ei.client.TkStrikeCommonEventListenerClient;
import com.xtremis.daedo.tkstrike.ei.client.TkStrikeEventsListenerClient;
import com.xtremis.daedo.tkstrike.ei.client.TkStrikeNodeEventsListenerClient;
import com.xtremis.daedo.tkstrike.ei.client.TkStrikeUDPFacadeEventsListenerClient;
import com.xtremis.daedo.tkstrike.ei.client.VenueManagementClient;
import com.xtremis.daedo.tkstrike.ei.client.WtOvrClientService;
import com.xtremis.daedo.tkstrike.ei.client.WtUDPService;
import com.xtremis.daedo.tkstrike.orm.model.ScreenResolution;
import com.xtremis.daedo.tkstrike.service.ExternalConfigService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.service.UdpEventListenerService;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeBaseController;
import com.xtremis.daedo.tkstrike.ui.model.ExternalConfigEntry;
import com.xtremis.daedo.tkstrike.ui.model.FormValidationError;
import com.xtremis.daedo.tkstrike.ui.model.UdpEventListenerEntry;
import com.xtremis.daedo.tkstrike.ui.scene.control.RestrictiveTextField;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;


@Component
public class ConfigurationExternalConfigController extends TkStrikeBaseController {

	private static final String URL_REGEX = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

	private static final String IP_REGEX = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";

	private static final String HOSTNAME_REGEX = "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$";

	@FXML
	private Pane pnContainer;

	@FXML
	private ProgressIndicator pi;

	@FXML
	private Pane pnTkStrikeVM;

	@FXML
	private TextField txtVenueManagementURL;

	@FXML
	private TextField txtVenueManagementRingNumber;

	@FXML
	private Pane pnWTOvr;

	@FXML
	private Pane pnOvrXApiKey;

	@FXML
	private ToggleButton tgOvrXApiKey;

	@FXML
	private RestrictiveTextField txtWTOvrMat;

	@FXML
	private TextField txtWTOvrUrl;

	@FXML
	private TextField txtWTOvrXApiKey;

	@FXML
	private TextField txtWTOvrUdpIp;

	@FXML
	private TextField txtWTOvrUdpListenPort;

	@FXML
	private TextField txtWTOvrUdpWritePort;

	@FXML
	private ToggleButton tgWtUdpConnected;

	@FXML
	private RadioButton rbBlueOnLeft;

	@FXML
	private RadioButton rbRedOnLeft;

	@FXML
	private ComboBox<String> cmbScreenRessolution;

	@FXML
	private ToggleButton tgRtBroadcastConnection;

	@FXML
	private TextField txtRtBroadcastIp;

	private SimpleBooleanProperty rtBroadcastIpChanged = new SimpleBooleanProperty(this, "rtBroadcastIpChanged", Boolean.FALSE.booleanValue());

	@FXML
	private RestrictiveTextField txtRtBroadcastPort;

	private SimpleBooleanProperty rtBroadcastPortChanged = new SimpleBooleanProperty(this, "rtBroadcastPortChanged", Boolean.FALSE.booleanValue());

	@FXML
	private RestrictiveTextField txtRtBroadcastRingNumber;

	private SimpleBooleanProperty rtBroadcastRingNumberChanged = new SimpleBooleanProperty(this, "rtBroadcastRingNumberChanged", Boolean.FALSE
			.booleanValue());

	private SimpleBooleanProperty formSaved = new SimpleBooleanProperty(this, "formSaved", Boolean.FALSE.booleanValue());

	@FXML
	private VBox pnEventListeners;

	@FXML
	private VBox pnNodeEventListeners;

	@FXML
	private TextField txtUDPListenerIp;

	@FXML
	private TextField txtUDPListenerPort;

	@FXML
	private VBox pnUDPEventListeners;

	private final ToggleGroup toggleGroup = new ToggleGroup();

	@Autowired
	private ExternalConfigService externalConfigService;

	@Autowired
	private UdpEventListenerService udpEventListenerService;

	@Autowired
	private TkStrikeUDPFacadeEventsListenerClient tkStrikeUDPFacadeEventsListenerClient;

	@Autowired
	private VenueManagementClient venueManagementClient;

	@Autowired
	private TkStrikeEventsListenerClient tkStrikeEventsListenerClient;

	@Autowired
	private TkStrikeNodeEventsListenerClient tkStrikeNodeEventsListenerClient;

	@Autowired
	private RtBroadcastSocketClient rtBroadcastSocketClient;

	@Autowired
	private WtOvrClientService wtOvrClientService;

	@Autowired
	private WtUDPService wtUDPService;

	private ExternalConfigEntry currentExternalConfigEntry = new ExternalConfigEntry();

	protected void showProgressIndicator(final boolean show) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				ConfigurationExternalConfigController.this.pnContainer.setVisible( ! show);
				ConfigurationExternalConfigController.this.pi.setVisible(show);
			}
		});
	}

	@Override
	public void onWindowShowEvent() {
		super.onWindowShowEvent();
		showProgressIndicator(true);
		refreshExternalConfigAndRebind();
		initializeUDPFormControlsStyles();
	}

	protected boolean reallyCanClose() {
		if( ! this.formSaved.get() && (this.rtBroadcastIpChanged.get() || this.rtBroadcastRingNumberChanged.get() || this.rtBroadcastPortChanged
				.get()) && showConfirmDialog(getMessage("message.confirmDialog.title"),
						getMessage("message.confirmDialog.closeConfigNoSaving")).get().equals(ButtonType.CANCEL))
			return false;
		return true;
	}

	@Override
	public EventHandler<WindowEvent> getOnWindowCloseEventHandler() {
		return super.getOnWindowCloseEventHandler();
	}

	private void refreshExternalConfigAndRebind() {
		if( ! this.formSaved.get() && (this.rtBroadcastIpChanged.get() || this.rtBroadcastRingNumberChanged.get() || this.rtBroadcastPortChanged
				.get())) {
			showProgressIndicator(false);
			return;
		}
		try {
			this.currentExternalConfigEntry = this.externalConfigService.getExternalConfigEntry();
		} catch(TkStrikeServiceException e) {
			manageException(e, "refreshExternalConfigAndRebind - getExternalConfigEntry", null);
		}
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				ConfigurationExternalConfigController.this.pnTkStrikeVM.setVisible(
						! ConfigurationExternalConfigController.this.wtCompetitionDataProtocol.booleanValue());
				ConfigurationExternalConfigController.this.pnWTOvr.setVisible(ConfigurationExternalConfigController.this.wtCompetitionDataProtocol
						.booleanValue());
				ConfigurationExternalConfigController.this.txtWTOvrUrl.textProperty().bindBidirectional(
						ConfigurationExternalConfigController.this.currentExternalConfigEntry.wtOvrUrlProperty());
				ConfigurationExternalConfigController.this.txtWTOvrXApiKey.textProperty().bindBidirectional(
						ConfigurationExternalConfigController.this.currentExternalConfigEntry.wtOvrXApiKeyProperty());
				ConfigurationExternalConfigController.this.txtWTOvrMat.textProperty().bindBidirectional(
						(Property)ConfigurationExternalConfigController.this.currentExternalConfigEntry.wtOvrMatProperty(),
						(StringConverter)new NumberStringConverter());
				ConfigurationExternalConfigController.this.txtWTOvrUdpIp.textProperty().bindBidirectional(
						ConfigurationExternalConfigController.this.currentExternalConfigEntry.wtOvrUdpIpProperty());
				ConfigurationExternalConfigController.this.txtWTOvrUdpListenPort.textProperty().bindBidirectional(
						(Property)ConfigurationExternalConfigController.this.currentExternalConfigEntry.wtOvrUdpListenPortProperty(),
						(StringConverter)new NumberStringConverter("0000"));
				ConfigurationExternalConfigController.this.txtWTOvrUdpWritePort.textProperty().bindBidirectional(
						(Property)ConfigurationExternalConfigController.this.currentExternalConfigEntry.wtOvrUdpWritePortProperty(),
						(StringConverter)new NumberStringConverter("0000"));
				ConfigurationExternalConfigController.this.tgOvrXApiKey.setSelected(StringUtils.isNotBlank(
						ConfigurationExternalConfigController.this.currentExternalConfigEntry.getWtOvrXApiKey()));
				ConfigurationExternalConfigController.this.txtVenueManagementURL.textProperty().bindBidirectional(
						ConfigurationExternalConfigController.this.currentExternalConfigEntry.venueManagementURLProperty());
				ConfigurationExternalConfigController.this.txtVenueManagementRingNumber.textProperty().bindBidirectional(
						ConfigurationExternalConfigController.this.currentExternalConfigEntry.venueManagementRingNumberProperty());
				ConfigurationExternalConfigController.this.cmbScreenRessolution.valueProperty().bindBidirectional(
						ConfigurationExternalConfigController.this.currentExternalConfigEntry.extScoreboardResolutionProperty());
				ConfigurationExternalConfigController.this.rbBlueOnLeft.selectedProperty().bindBidirectional(
						ConfigurationExternalConfigController.this.currentExternalConfigEntry.extScoreboardBlueOnLeftProperty());
				if( ! ConfigurationExternalConfigController.this.currentExternalConfigEntry.getExtScoreboardBlueOnLeft())
					ConfigurationExternalConfigController.this.rbRedOnLeft.setSelected(true);
				ConfigurationExternalConfigController.this.pnEventListeners.getChildren().clear();
				if(ConfigurationExternalConfigController.this.currentExternalConfigEntry.getListenersURLs() != null
						&& ConfigurationExternalConfigController.this.currentExternalConfigEntry.getListenersURLs().size() > 0)
					for(String listenerURL : ConfigurationExternalConfigController.this.currentExternalConfigEntry.getListenersURLs())
						ConfigurationExternalConfigController.this.createNewEventsListenerComponent(listenerURL,
								ConfigurationExternalConfigController.this.pnEventListeners,
								ConfigurationExternalConfigController.this.tkStrikeEventsListenerClient);
				ConfigurationExternalConfigController.this.pnNodeEventListeners.getChildren().clear();
				if(ConfigurationExternalConfigController.this.currentExternalConfigEntry.getNodeListenersURLs() != null
						&& ConfigurationExternalConfigController.this.currentExternalConfigEntry.getNodeListenersURLs().size() > 0)
					for(String listenerURL : ConfigurationExternalConfigController.this.currentExternalConfigEntry.getNodeListenersURLs())
						ConfigurationExternalConfigController.this.createNewEventsListenerComponent(listenerURL,
								ConfigurationExternalConfigController.this.pnNodeEventListeners,
								ConfigurationExternalConfigController.this.tkStrikeNodeEventsListenerClient);
				ConfigurationExternalConfigController.this.pnUDPEventListeners.getChildren().clear();
				try {
					List<UdpEventListenerEntry> eventListenerEntryList = ConfigurationExternalConfigController.this.udpEventListenerService
							.findAllEntries();
					if(eventListenerEntryList != null && eventListenerEntryList.size() > 0)
						for(UdpEventListenerEntry entry : eventListenerEntryList)
							ConfigurationExternalConfigController.this.createNewUDPEventsListenerComponent(entry,
									ConfigurationExternalConfigController.this.pnUDPEventListeners);
				} catch(TkStrikeServiceException e) {
					ConfigurationExternalConfigController.this.manageException(e, "refreshExternalConfigAndRebind - udpEventListenerService", null);
				}
				ConfigurationExternalConfigController.this.txtRtBroadcastIp.textProperty().bindBidirectional(
						ConfigurationExternalConfigController.this.currentExternalConfigEntry.rtBroadcastIpProperty());
				ConfigurationExternalConfigController.this.txtRtBroadcastPort.textProperty().bindBidirectional(
						(Property)ConfigurationExternalConfigController.this.currentExternalConfigEntry.rtBroadcastPortProperty(),
						(StringConverter)new NumberStringConverter("0000"));
				ConfigurationExternalConfigController.this.txtRtBroadcastRingNumber.textProperty().bindBidirectional(
						ConfigurationExternalConfigController.this.currentExternalConfigEntry.rtBroadcastRingNumberProperty());
				ConfigurationExternalConfigController.this.rtBroadcastIpChanged.set(false);
				ConfigurationExternalConfigController.this.rtBroadcastPortChanged.set(false);
				ConfigurationExternalConfigController.this.rtBroadcastRingNumberChanged.set(false);
				ConfigurationExternalConfigController.this.initializeFormControlsStyles();
				ConfigurationExternalConfigController.this.formSaved.set(Boolean.FALSE.booleanValue());
				ConfigurationExternalConfigController.this.showProgressIndicator(false);
			}
		});
	}

	@Override
	protected Collection<Control> getFormControls() {
		ArrayList<Control> theList = getListenersTextfields(this.pnEventListeners);
		theList.addAll(getListenersTextfields(this.pnNodeEventListeners));
		theList.add(this.txtVenueManagementURL);
		theList.add(this.txtVenueManagementRingNumber);
		theList.add(this.cmbScreenRessolution);
		theList.add(this.txtRtBroadcastIp);
		theList.add(this.txtRtBroadcastPort);
		theList.add(this.txtRtBroadcastRingNumber);
		theList.add(this.txtWTOvrMat);
		theList.add(this.txtWTOvrUrl);
		theList.add(this.txtWTOvrXApiKey);
		theList.add(this.txtWTOvrUdpIp);
		theList.add(this.txtWTOvrUdpListenPort);
		theList.add(this.txtWTOvrUdpWritePort);
		return FXCollections.observableArrayList(theList);
	}

	@Override
	public LinkedHashSet<FormValidationError> validateForm() {
		LinkedHashSet<FormValidationError> res = null;
		if(StringUtils.isNotBlank(this.txtRtBroadcastIp.getText()) &&
				! this.txtRtBroadcastIp.getText().matches(
						"^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$") &&
				! this.txtRtBroadcastIp.getText().matches(
						"^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$")) {
			res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.currentExternalConfigEntry, "rtBroadcastIp", this.txtRtBroadcastIp, getMessage(
					"validation.invalidURL")));
		}
		if(this.wtCompetitionDataProtocol.booleanValue()) {
			if(StringUtils.isNotBlank(this.txtWTOvrUrl.getText()) && ! this.txtWTOvrUrl.getText().matches(
					"^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
				if(res == null)
					res = new LinkedHashSet<>();
				res.add(new FormValidationError(this.currentExternalConfigEntry, "wtOvrUlr", this.txtWTOvrUrl, getMessage("validation.invalidURL")));
			}
			if(StringUtils.isNotBlank(this.txtWTOvrUrl.getText()) && StringUtils.isBlank(this.txtWTOvrMat.getText())) {
				if(res == null)
					res = new LinkedHashSet<>();
				res.add(new FormValidationError(this.currentExternalConfigEntry, "txtWTOvrMat", this.txtWTOvrMat, getMessage("validation.required")));
			} else if( ! StringUtils.isNumeric(this.txtWTOvrMat.getText())) {
				if(res == null)
					res = new LinkedHashSet<>();
				res.add(new FormValidationError(this.currentExternalConfigEntry, "txtWTOvrMat", this.txtWTOvrMat, getMessage(
						"validation.invalidNumber")));
			}
			if(this.tgOvrXApiKey.isSelected() && StringUtils.isBlank(this.txtWTOvrXApiKey.getText())) {
				if(res == null)
					res = new LinkedHashSet<>();
				res.add(new FormValidationError(this.currentExternalConfigEntry, "txtWTOvrXApiKey", this.txtWTOvrXApiKey, getMessage(
						"validation.required")));
			}
		} else {
			if(StringUtils.isNotBlank(this.txtVenueManagementURL.getText()) && ! this.txtVenueManagementURL.getText().matches(
					"^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
				if(res == null)
					res = new LinkedHashSet<>();
				res.add(new FormValidationError(this.currentExternalConfigEntry, "venueManagementURL", this.txtVenueManagementURL, getMessage(
						"validation.invalidURL")));
			}
			if(StringUtils.isNotBlank(this.txtVenueManagementURL.getText()) && StringUtils.isBlank(this.txtVenueManagementRingNumber.getText())) {
				if(res == null)
					res = new LinkedHashSet<>();
				res.add(new FormValidationError(this.currentExternalConfigEntry, "txtVenueManagementRingNumber", this.txtVenueManagementRingNumber,
						getMessage("validation.required")));
			}
		}
		if(this.cmbScreenRessolution.getValue() == null || StringUtils.isBlank(this.cmbScreenRessolution.getValue())) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.currentExternalConfigEntry, "extScoreboardResolution", this.cmbScreenRessolution, getMessage(
					"validation.required")));
		}
		for(VBox vBox : Arrays.<VBox>asList(new VBox[] {this.pnEventListeners, this.pnNodeEventListeners})) {
			ArrayList<Control> listenerTextfields = getListenersTextfields(vBox);
			if(listenerTextfields != null)
				for(Control listenerTextfield : listenerTextfields) {
					TextField textField = (TextField)listenerTextfield;
					if(StringUtils.isBlank(textField.getText())) {
						if(res == null)
							res = new LinkedHashSet<>();
						res.add(new FormValidationError(this.currentExternalConfigEntry, "eventsListenerURL", textField, getMessage(
								"validation.required")));
						continue;
					}
					if( ! textField.getText().matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
						if(res == null)
							res = new LinkedHashSet<>();
						res.add(new FormValidationError(this.currentExternalConfigEntry, "eventsListenerURL", textField, getMessage(
								"validation.invalidURL")));
					}
				}
		}
		return res;
	}

	public void save() {
		if(isFormValid()) {
			showProgressIndicator(true);
			TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					ArrayList<String> strings = ConfigurationExternalConfigController.this.getListenersTextfieldValues(
							ConfigurationExternalConfigController.this.pnEventListeners);
					if(strings != null)
						ConfigurationExternalConfigController.this.currentExternalConfigEntry.setListenersURLs(strings);
					strings = ConfigurationExternalConfigController.this.getListenersTextfieldValues(
							ConfigurationExternalConfigController.this.pnNodeEventListeners);
					if(strings != null)
						ConfigurationExternalConfigController.this.currentExternalConfigEntry.setNodeListenersURLs(strings);
					try {
						ConfigurationExternalConfigController.this.externalConfigService.update(
								ConfigurationExternalConfigController.this.currentExternalConfigEntry.createExternalConfig());
						ConfigurationExternalConfigController.this.getAppStatusWorker().externalScreenResolutionProperty().set(ScreenResolution
								.valueOf(ConfigurationExternalConfigController.this.currentExternalConfigEntry.getExtScoreboardResolution()));
					} catch(TkStrikeServiceException e) {
						ConfigurationExternalConfigController.this.showProgressIndicator(false);
						ConfigurationExternalConfigController.this.manageException(e, "Saving external config", null);
						return null;
					}
					ConfigurationExternalConfigController.this.formSaved.set(Boolean.TRUE.booleanValue());
					ConfigurationExternalConfigController.this.refreshExternalConfigAndRebind();
					return null;
				}
			});
		}
	}

	public void undo() {
		refreshExternalConfigAndRebind();
	}

	public void testWtOvrConn() {
		if(StringUtils.isNotBlank(this.txtWTOvrUrl.getText())) {
			showProgressIndicator(true);
			TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					Boolean testResult = Boolean.FALSE;
					try {
						testResult = ConfigurationExternalConfigController.this.wtOvrClientService.doPing(
								ConfigurationExternalConfigController.this.txtWTOvrUrl.getText(),
								ConfigurationExternalConfigController.this.txtWTOvrXApiKey.getText());
					} catch(Exception e) {
						ConfigurationExternalConfigController.logger.error("Exception calling WT-OVR", e);
					}
					final Boolean testResultFinal = testResult;
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							ConfigurationExternalConfigController.this.showProgressIndicator(false);
							if(testResultFinal.booleanValue()) {
								ConfigurationExternalConfigController.this.showInfoDialog(ConfigurationExternalConfigController.this.getMessage(
										"title.default.info"), ConfigurationExternalConfigController.this.getMessage("message.info.wtOvrTestOk"));
							} else {
								ConfigurationExternalConfigController.this.showErrorDialog(ConfigurationExternalConfigController.this.getMessage(
										"title.default.error"), ConfigurationExternalConfigController.this.getMessage("message.error.wtOvrTest"));
							}
						}
					});
					return null;
				}
			});
		}
	}

	public void testVenueManagementConn() {
		showProgressIndicator(true);
		TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				Boolean testResult = Boolean.FALSE;
				try {
					testResult = ConfigurationExternalConfigController.this.venueManagementClient.doPing(
							ConfigurationExternalConfigController.this.txtVenueManagementURL.getText());
					if(testResult == null)
						testResult = Boolean.FALSE;
				} catch(TkStrikeServiceException e) {
					e.printStackTrace();
				}
				final Boolean testResultFinal = testResult;
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						ConfigurationExternalConfigController.this.showProgressIndicator(false);
						if(testResultFinal.booleanValue()) {
							ConfigurationExternalConfigController.this.showInfoDialog(ConfigurationExternalConfigController.this.getMessage(
									"title.default.info"), ConfigurationExternalConfigController.this.getMessage(
											"message.info.venueManagementTestOk"));
						} else {
							ConfigurationExternalConfigController.this.showErrorDialog(ConfigurationExternalConfigController.this.getMessage(
									"title.default.error"), ConfigurationExternalConfigController.this.getMessage("message.error.venueManagement"));
						}
					}
				});
				return null;
			}
		});
	}

	public void doAddNewEventListener() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				ConfigurationExternalConfigController.this.createNewEventsListenerComponent((String)null,
						ConfigurationExternalConfigController.this.pnEventListeners,
						ConfigurationExternalConfigController.this.tkStrikeEventsListenerClient);
			}
		});
	}

	public void doAddNewNodeEventListener() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				ConfigurationExternalConfigController.this.createNewEventsListenerComponent((String)null,
						ConfigurationExternalConfigController.this.pnNodeEventListeners,
						ConfigurationExternalConfigController.this.tkStrikeNodeEventsListenerClient);
			}
		});
	}

	public void doAddNewUDPEventListener() {
		boolean isValid = true;
		showProgressIndicator(true);
		if(StringUtils.isBlank(this.txtUDPListenerIp.getText())) {
			isValid = false;
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					ConfigurationExternalConfigController.this.txtUDPListenerIp.getStyleClass().removeAll(Collections.singleton("form-default"));
					ConfigurationExternalConfigController.this.txtUDPListenerIp.getStyleClass().add("form-error");
					Tooltip errorTooltip = new Tooltip(ConfigurationExternalConfigController.this.getMessage("validation.required"));
					errorTooltip.setHideOnEscape(false);
					ConfigurationExternalConfigController.this.txtUDPListenerIp.setTooltip(errorTooltip);
				}
			});
		} else if(StringUtils.isNotBlank(this.txtUDPListenerIp.getText()) &&
				! this.txtUDPListenerIp.getText().matches(
						"^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$")) {
			isValid = false;
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					ConfigurationExternalConfigController.this.txtUDPListenerIp.getStyleClass().removeAll(Collections.singleton("form-default"));
					ConfigurationExternalConfigController.this.txtUDPListenerIp.getStyleClass().add("form-error");
					Tooltip errorTooltip = new Tooltip(ConfigurationExternalConfigController.this.getMessage("validation.invalidURL"));
					errorTooltip.setHideOnEscape(false);
					ConfigurationExternalConfigController.this.txtUDPListenerIp.setTooltip(errorTooltip);
				}
			});
		}
		if( ! StringUtils.isNumeric(this.txtUDPListenerPort.getText())) {
			isValid = false;
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					ConfigurationExternalConfigController.this.txtUDPListenerPort.getStyleClass().removeAll(Collections.singleton("form-default"));
					ConfigurationExternalConfigController.this.txtUDPListenerPort.getStyleClass().add("form-error");
					Tooltip errorTooltip = new Tooltip(ConfigurationExternalConfigController.this.getMessage("validation.incorrectValue"));
					errorTooltip.setHideOnEscape(false);
					ConfigurationExternalConfigController.this.txtUDPListenerPort.setTooltip(errorTooltip);
				}
			});
		}
		if(isValid) {
			initializeUDPFormControlsStyles();
			try {
				UdpEventListenerEntry entry = this.udpEventListenerService.createEntry(this.txtUDPListenerIp.getText(), Integer.valueOf(Integer
						.parseInt(this.txtUDPListenerPort.getText())));
				if(entry != null) {
					createNewUDPEventsListenerComponent(entry, this.pnUDPEventListeners);
					this.txtUDPListenerIp.clear();
					this.txtUDPListenerPort.clear();
					this.tkStrikeUDPFacadeEventsListenerClient.updateUDPSockets();
				}
			} catch(TkStrikeServiceException e) {
				manageException(e, "", null);
			}
		}
		showProgressIndicator(false);
	}

	private void initializeUDPFormControlsStyles() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				ConfigurationExternalConfigController.this.txtUDPListenerIp.getStyleClass().removeAll(Collections.singleton("form-error"));
				ConfigurationExternalConfigController.this.txtUDPListenerIp.getStyleClass().add("form-default");
				ConfigurationExternalConfigController.this.txtUDPListenerIp.setTooltip(null);
				ConfigurationExternalConfigController.this.txtUDPListenerPort.getStyleClass().removeAll(Collections.singleton("form-error"));
				ConfigurationExternalConfigController.this.txtUDPListenerPort.getStyleClass().add("form-default");
				ConfigurationExternalConfigController.this.txtUDPListenerPort.setTooltip(null);
			}
		});
	}

	private void createNewEventsListenerComponent(String defaultURL, final VBox vBoxListeners,
			final TkStrikeCommonEventListenerClient tkStrikeCommonEventListenerClient) {
		final int newIndex = vBoxListeners.getChildren().size();
		HBox rootPane = new HBox();
		rootPane.setMinWidth(743.0D);
		rootPane.setMaxWidth(743.0D);
		rootPane.setPrefWidth(743.0D);
		rootPane.setUserData(Integer.valueOf(newIndex));
		rootPane.setSpacing(5.0D);
		rootPane.setAlignment(Pos.CENTER);
		rootPane.setPadding(new Insets(5.0D, 5.0D, 5.0D, 5.0D));
		Label label = new Label(getMessage("label.url"));
		rootPane.getChildren().add(label);
		TextField txtURL = new TextField();
		txtURL.setMinWidth(450.0D);
		txtURL.setPrefWidth(450.0D);
		txtURL.setMaxWidth(450.0D);
		if(StringUtils.isNotBlank(defaultURL))
			txtURL.setText(defaultURL);
		rootPane.getChildren().add(txtURL);
		Button btTest = new Button(getMessage("button.testConnection"));
		btTest.getStyleClass().addAll(new String[] {"button-window"});
		btTest.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent actionEvent) {
				ExecutorService es = Executors.newSingleThreadExecutor();
				es.submit(new Runnable() {

					@Override
					public void run() {
						Boolean testResult = Boolean.FALSE;
						ConfigurationExternalConfigController.this.showProgressIndicator(true);
						try {
							TextField txtControl = ConfigurationExternalConfigController.this.getURLTextFieldByIndex(vBoxListeners, Integer.valueOf(
									newIndex));
							if(txtControl != null) {
								testResult = tkStrikeCommonEventListenerClient.doPing(txtControl.getText());
								if(testResult == null)
									testResult = Boolean.FALSE;
							}
						} catch(TkStrikeServiceException e) {
							e.printStackTrace();
						}
						final Boolean testResultFinal = testResult;
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								ConfigurationExternalConfigController.this.showProgressIndicator(false);
								if(testResultFinal.booleanValue()) {
									ConfigurationExternalConfigController.this.showInfoDialog(ConfigurationExternalConfigController.this.getMessage(
											"title.default.info"), ConfigurationExternalConfigController.this.getMessage(
													"message.info.externalEventsListener.testOk"));
								} else {
									ConfigurationExternalConfigController.this.showErrorDialog(ConfigurationExternalConfigController.this.getMessage(
											"title.default.error"), ConfigurationExternalConfigController.this.getMessage(
													"message.error.externalEventsListener"));
								}
							}
						});
					}
				});
				es.shutdown();
			}
		});
		rootPane.getChildren().add(btTest);
		Button btDelete = new Button(getMessage("button.delete"));
		btDelete.getStyleClass().addAll(new String[] {"button-image-delete"});
		btDelete.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent actionEvent) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						int index2Delete = 0;
						for(int i = 0; i < vBoxListeners.getChildren().size(); i++) {
							Node node = vBoxListeners.getChildren().get(i);
							int index = ((Integer)node.getUserData()).intValue();
							if(index == newIndex)
								index2Delete = i;
						}
						vBoxListeners.getChildren().remove(index2Delete);
					}
				});
			}
		});
		rootPane.getChildren().add(btDelete);
		vBoxListeners.getChildren().add(rootPane);
		txtURL.requestFocus();
	}

	private void createNewUDPEventsListenerComponent(final UdpEventListenerEntry eventListenerEntry, final VBox vBoxListeners) {
		final int newIndex = vBoxListeners.getChildren().size();
		HBox rootPane = new HBox();
		rootPane.setMinWidth(460.0D);
		rootPane.setMaxWidth(460.0D);
		rootPane.setPrefWidth(460.0D);
		rootPane.setUserData(Integer.valueOf(newIndex));
		rootPane.setSpacing(5.0D);
		rootPane.setAlignment(Pos.CENTER);
		rootPane.setPadding(new Insets(5.0D, 5.0D, 5.0D, 5.0D));
		Label labelIp = new Label(getMessage("label.ip"));
		rootPane.getChildren().add(labelIp);
		TextField txtIP = new TextField();
		txtIP.setMinWidth(140.0D);
		txtIP.setPrefWidth(140.0D);
		txtIP.setMaxWidth(140.0D);
		txtIP.setText(eventListenerEntry.getUdpEventListenerIp());
		txtIP.setDisable(true);
		txtIP.setAlignment(Pos.CENTER);
		rootPane.getChildren().add(txtIP);
		Label labelPort = new Label(getMessage("label.port"));
		rootPane.getChildren().add(labelPort);
		TextField txtPort = new TextField();
		txtPort.setMinWidth(70.0D);
		txtPort.setPrefWidth(70.0D);
		txtPort.setMaxWidth(70.0D);
		txtPort.setText("" + eventListenerEntry.getUdpEventListenerPort());
		txtPort.setDisable(true);
		txtPort.setAlignment(Pos.CENTER);
		rootPane.getChildren().add(txtPort);
		Button btDelete = new Button(getMessage("button.delete"));
		btDelete.getStyleClass().addAll(new String[] {"button-image-delete"});
		btDelete.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent actionEvent) {
				ConfigurationExternalConfigController.this.showProgressIndicator(true);
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						try {
							ConfigurationExternalConfigController.this.udpEventListenerService.delete(eventListenerEntry.getId());
							int index2Delete = 0;
							for(int i = 0; i < vBoxListeners.getChildren().size(); i++) {
								Node node = vBoxListeners.getChildren().get(i);
								int index = ((Integer)node.getUserData()).intValue();
								if(index == newIndex)
									index2Delete = i;
							}
							vBoxListeners.getChildren().remove(index2Delete);
						} catch(TkStrikeServiceException e) {
							ConfigurationExternalConfigController.this.manageException(e, "", null);
						} finally {
							ConfigurationExternalConfigController.this.showProgressIndicator(false);
						}
					}
				});
			}
		});
		rootPane.getChildren().add(btDelete);
		vBoxListeners.getChildren().add(rootPane);
		txtIP.requestFocus();
	}

	private TextField getURLTextFieldByIndex(VBox thePaneListeners, Integer index) {
		TextField res = null;
		for(int i = 0; i < thePaneListeners.getChildren().size(); i++) {
			Node node = thePaneListeners.getChildren().get(i);
			int objectIndex = ((Integer)node.getUserData()).intValue();
			if(index.equals(Integer.valueOf(objectIndex))) {
				HBox hBox = (HBox)node;
				res = (TextField)hBox.getChildren().get(1);
			}
		}
		return res;
	}

	private ArrayList<Control> getListenersTextfields(VBox thePaneListeners) {
		ArrayList<Control> arrayList = new ArrayList<>();
		for(int i = 0; i < thePaneListeners.getChildren().size(); i++) {
			Node node = thePaneListeners.getChildren().get(i);
			HBox hBox = (HBox)node;
			arrayList.add((Control)hBox.getChildren().get(1));
		}
		return arrayList;
	}

	private ArrayList<String> getListenersTextfieldValues(VBox thePaneListeners) {
		ArrayList<String> arrayList = new ArrayList<>();
		for(int i = 0; i < thePaneListeners.getChildren().size(); i++) {
			Node node = thePaneListeners.getChildren().get(i);
			HBox hBox = (HBox)node;
			TextField textField = (TextField)hBox.getChildren().get(1);
			arrayList.add(textField.getText());
		}
		return arrayList;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		this.rbBlueOnLeft.setToggleGroup(this.toggleGroup);
		this.rbRedOnLeft.setToggleGroup(this.toggleGroup);
		this.cmbScreenRessolution.getItems().addAll(new String[] {ScreenResolution.HD.toString(), ScreenResolution.FHD
				.toString()});
		this.cmbScreenRessolution.setConverter(new StringConverter<String>() {

			@Override
			public String toString(String s) {
				return ConfigurationExternalConfigController.this.getMessage("enum.screenRessolution." + s);
			}

			@Override
			public String fromString(String s) {
				return s;
			}
		});
		this.txtRtBroadcastRingNumber.setMaxLength(2);
		this.txtRtBroadcastRingNumber.setRestrict("[0-9][0-9]");
		this.tgRtBroadcastConnection.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, final Boolean selected) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						if(selected.booleanValue()) {
							ConfigurationExternalConfigController.this.tgRtBroadcastConnection.setText(ConfigurationExternalConfigController.this
									.getMessage("toggle.rtBroadcast.connected"));
						} else {
							ConfigurationExternalConfigController.this.tgRtBroadcastConnection.setText(ConfigurationExternalConfigController.this
									.getMessage("toggle.rtBroadcast.notConnected"));
						}
					}
				});
			}
		});
		this.tgRtBroadcastConnection.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				ConfigurationExternalConfigController.this.showProgressIndicator(true);
				if( ! ConfigurationExternalConfigController.this.tgRtBroadcastConnection.isSelected()) {
					TkStrikeExecutors.executeInThreadPool(new Runnable() {

						@Override
						public void run() {
							ConfigurationExternalConfigController.this.rtBroadcastSocketClient.closeConnection();
						}
					});
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							ConfigurationExternalConfigController.this.pi.setVisible(false);
							ConfigurationExternalConfigController.this.pnContainer.setVisible(true);
							ConfigurationExternalConfigController.this.tgRtBroadcastConnection.setSelected(false);
						}
					});
				} else {
					if(ConfigurationExternalConfigController.this.isFormValid()) {
						TkStrikeExecutors.executeInThreadPool(new Runnable() {

							@Override
							public void run() {
								try {
									ConfigurationExternalConfigController.this.rtBroadcastSocketClient.connect(
											ConfigurationExternalConfigController.this.txtRtBroadcastIp.getText(), Long.valueOf(Long.parseLong(
													ConfigurationExternalConfigController.this.txtRtBroadcastPort.getText())),
											ConfigurationExternalConfigController.this.txtRtBroadcastRingNumber.getText());
								} catch(TkStrikeServiceException e) {
									e.printStackTrace();
								}
							}
						});
					} else {
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								ConfigurationExternalConfigController.this.tgRtBroadcastConnection.setSelected(false);
							}
						});
					}
					ConfigurationExternalConfigController.this.showProgressIndicator(false);
				}
			}
		});
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.rtBroadcastSocketClient.connectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, final Boolean connected) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						ConfigurationExternalConfigController.this.tgRtBroadcastConnection.setSelected(connected.booleanValue());
					}
				});
			}
		});
		this.txtRtBroadcastIp.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				ConfigurationExternalConfigController.this.rtBroadcastIpChanged.set(Boolean.TRUE.booleanValue());
			}
		});
		this.txtRtBroadcastPort.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				ConfigurationExternalConfigController.this.rtBroadcastPortChanged.set(Boolean.TRUE.booleanValue());
			}
		});
		this.txtRtBroadcastRingNumber.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				ConfigurationExternalConfigController.this.rtBroadcastRingNumberChanged.set(Boolean.TRUE.booleanValue());
			}
		});
		this.tgOvrXApiKey.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				ConfigurationExternalConfigController.this.tgOvrXApiKey.setText(ConfigurationExternalConfigController.this.getMessage(
						"toggle.wtOvrUseXApiKey." + (newValue.booleanValue() ? "enabled" : "disabled")));
				if( ! newValue.booleanValue())
					ConfigurationExternalConfigController.this.txtWTOvrXApiKey.clear();
			}
		});
		this.pnOvrXApiKey.visibleProperty().bind(this.tgOvrXApiKey.selectedProperty());
		this.txtWTOvrMat.setMaxLength(3);
		this.txtWTOvrMat.setRestrict("[0-9][0-9]");
		this.wtUDPService.connectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, final Boolean newValue) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						ConfigurationExternalConfigController.this.tgWtUdpConnected.setSelected(newValue.booleanValue());
					}
				});
			}
		});
		this.tgWtUdpConnected.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, final Boolean newValue) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						ConfigurationExternalConfigController.this.tgWtUdpConnected.setText(ConfigurationExternalConfigController.this.getMessage(
								"toggle." + (newValue.booleanValue() ? "connected" : "notConnected")));
					}
				});
			}
		});
		this.tgWtUdpConnected.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				ConfigurationExternalConfigController.this.showProgressIndicator(true);
				if( ! ConfigurationExternalConfigController.this.tgWtUdpConnected.isSelected()) {
					TkStrikeExecutors.executeInThreadPool(new Runnable() {

						@Override
						public void run() {
							ConfigurationExternalConfigController.this.wtUDPService.closeConnection(false);
						}
					});
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							ConfigurationExternalConfigController.this.showProgressIndicator(false);
							ConfigurationExternalConfigController.this.tgWtUdpConnected.setSelected(false);
						}
					});
				} else {
					if(ConfigurationExternalConfigController.this.isFormValid()) {
						TkStrikeExecutors.executeInThreadPool(new Runnable() {

							@Override
							public void run() {
								try {
									ConfigurationExternalConfigController.this.wtUDPService.connect(
											ConfigurationExternalConfigController.this.txtWTOvrUdpIp.getText(),
											Integer.valueOf(Integer.parseInt(ConfigurationExternalConfigController.this.txtWTOvrUdpListenPort
													.getText())),
											Integer.valueOf(Integer.parseInt(ConfigurationExternalConfigController.this.txtWTOvrUdpWritePort
													.getText())));
								} catch(TkStrikeServiceException e) {
									e.printStackTrace();
								}
							}
						});
					} else {
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								ConfigurationExternalConfigController.this.tgWtUdpConnected.setSelected(false);
							}
						});
					}
					ConfigurationExternalConfigController.this.showProgressIndicator(false);
				}
			}
		});
	}
}
