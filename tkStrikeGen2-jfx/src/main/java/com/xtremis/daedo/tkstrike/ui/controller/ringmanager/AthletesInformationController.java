package com.xtremis.daedo.tkstrike.ui.controller.ringmanager;

import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xtremis.daedo.tkstrike.service.AthleteService;
import com.xtremis.daedo.tkstrike.service.FlagService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.ui.model.AthleteEntry;
import com.xtremis.daedo.tkstrike.ui.model.FormValidationError;
import com.xtremis.daedo.tkstrike.ui.model.MatchConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.scene.FlagEntry;
import com.xtremis.daedo.tkstrike.ui.scene.control.RestrictiveTextField;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;


@Component
public class AthletesInformationController extends BaseStepWizardController implements StepWizardController {

	@FXML
	private Node rootView;

	@FXML
	private TextField txtBlueName;

	@FXML
	private TextField txtBlueWTFID;

	@FXML
	private Circle ciBlueExists;

	@FXML
	private Button btEditBlue;

	private SimpleBooleanProperty blueExists = new SimpleBooleanProperty();

	@FXML
	private RestrictiveTextField txtBlueAbbr;

	private FlagEntry blueFlag;

	@FXML
	private ImageView ivBlueFlag;

	@FXML
	private RestrictiveTextField txtBlueVrQuota;

	private FlagEntry redFlag;

	@FXML
	private TextField txtRedName;

	@FXML
	private TextField txtRedWTFID;

	@FXML
	private Circle ciRedExists;

	private SimpleBooleanProperty redExists = new SimpleBooleanProperty();

	@FXML
	private Button btEditRed;

	@FXML
	private RestrictiveTextField txtRedAbbr;

	@FXML
	private ImageView ivRedFlag;

	@FXML
	private RestrictiveTextField txtRedVrQuota;

	private ObservableList<FlagEntry> flagEntries = FXCollections.emptyObservableList();

	private MatchConfigurationEntry matchConfigurationEntry;

	@Autowired
	private FlagService flagService;

	@Autowired
	private AthleteService athleteService;

	@Autowired
	private EditAthleteController editAthleteController;

	@Override
	public Node getRootView() {
		return this.rootView;
	}

	@Override
	public void clearForm() {
		this.txtBlueName.clear();
		this.txtBlueWTFID.clear();
		this.txtBlueAbbr.clear();
		this.blueFlag = null;
		this.txtBlueVrQuota.setText("1");
		this.txtRedName.clear();
		this.txtRedWTFID.clear();
		this.txtRedAbbr.clear();
		this.redFlag = null;
		this.txtRedVrQuota.setText("1");
		this.blueExists.set(false);
		this.redExists.set(false);
	}

	@Override
	protected Collection<Control> getFormControls() {
		return FXCollections.observableArrayList(new Control[] {this.txtBlueName, this.txtBlueWTFID, this.txtBlueAbbr, this.txtRedName,
				this.txtRedWTFID, this.txtRedAbbr});
	}

	@Override
	public LinkedHashSet<FormValidationError> validateForm() {
		LinkedHashSet<FormValidationError> res = null;
		if(StringUtils.isBlank(this.txtBlueName.getText())) {
			res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.matchConfigurationEntry, "blueAthlete.name", this.txtBlueName, getMessage("validation.required")));
		}
		if(StringUtils.isBlank(this.txtRedName.getText())) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.matchConfigurationEntry, "redAthlete.name", this.txtRedName, getMessage("validation.required")));
		}
		return res;
	}

	@Override
	public void onWindowShowEvent() {
		super.onWindowShowEvent();
		this.ciBlueExists.setFill(Color.RED);
		this.ciRedExists.setFill(Color.RED);
		this.blueExists.set(false);
		this.redExists.set(false);
	}

	protected void _bindControls() {
		_bindBlueInfo();
		_bindRedInfo();
		if(this.matchConfigurationEntry != null) {
			this.txtBlueVrQuota.textProperty().bindBidirectional((Property)this.matchConfigurationEntry.blueAthleteVideoQuotaProperty(),
					(StringConverter)new NumberStringConverter());
			this.txtRedVrQuota.textProperty().bindBidirectional((Property)this.matchConfigurationEntry.redAthleteVideoQuotaProperty(),
					(StringConverter)new NumberStringConverter());
		}
	}

	protected void _bindBlueInfo() {
		if(this.matchConfigurationEntry != null) {
			final AthleteEntry blueAthleteEntry = this.matchConfigurationEntry.blueAthleteProperty().get();
			if(blueAthleteEntry != null)
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						AthletesInformationController.this.txtBlueName.textProperty().bindBidirectional(blueAthleteEntry.scoreboardName);
						AthletesInformationController.this.txtBlueWTFID.textProperty().bindBidirectional(blueAthleteEntry.wfId);
						if(blueAthleteEntry.flag.get() != null) {
							AthletesInformationController.this.blueFlag = blueAthleteEntry.flag.get();
							if(AthletesInformationController.this.blueFlag != null)
								AthletesInformationController.this._updateFlag(AthletesInformationController.this.blueFlag,
										AthletesInformationController.this.txtBlueAbbr, AthletesInformationController.this.ivBlueFlag);
						}
					}
				});
		}
	}

	protected void _bindRedInfo() {
		if(this.matchConfigurationEntry != null) {
			final AthleteEntry redAthleteEntry = this.matchConfigurationEntry.redAthleteProperty().get();
			if(redAthleteEntry != null)
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						AthletesInformationController.this.txtRedName.textProperty().bindBidirectional(redAthleteEntry.scoreboardName);
						AthletesInformationController.this.txtRedWTFID.textProperty().bindBidirectional(redAthleteEntry.wfId);
						if(redAthleteEntry.flag.get() != null) {
							AthletesInformationController.this.redFlag = redAthleteEntry.flag.get();
							if(AthletesInformationController.this.redFlag != null)
								AthletesInformationController.this._updateFlag(AthletesInformationController.this.redFlag,
										AthletesInformationController.this.txtRedAbbr, AthletesInformationController.this.ivRedFlag);
						}
					}
				});
		}
	}

	public void editBlueAthlete() {
		createOrUpdateAthlete(this.matchConfigurationEntry.blueAthleteProperty().get().getId(), true);
	}

	public void newAthleteFromBlue() {
		createOrUpdateAthlete((String)null, true);
	}

	public void editRedAthlete() {
		createOrUpdateAthlete(this.matchConfigurationEntry.getRedAthlete().getId(), false);
	}

	public void newAthleteFromRed() {
		createOrUpdateAthlete((String)null, false);
	}

	private void createOrUpdateAthlete(String athleteId, final boolean blue) {
		this.editAthleteController.setCurrentAthleteId(athleteId);
		openInNewStage(this.editAthleteController, new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				if(AthletesInformationController.this.editAthleteController.isSaved() && AthletesInformationController.this.editAthleteController
						.getCurrentAthleteId() != null) {
					AthleteEntry athleteEntry = null;
					try {
						athleteEntry = AthletesInformationController.this.athleteService.getEntryById(
								AthletesInformationController.this.editAthleteController.getCurrentAthleteId());
					} catch(TkStrikeServiceException e) {
						AthletesInformationController.this.manageException(e, null, e.getMessage());
					}
					if(blue) {
						AthletesInformationController.this.matchConfigurationEntry.blueAthleteProperty().set(athleteEntry);
						AthletesInformationController.this.blueExists.setValue(Boolean.valueOf(true));
						AthletesInformationController.this._bindBlueInfo();
					} else {
						AthletesInformationController.this.matchConfigurationEntry.redAthleteProperty().set(athleteEntry);
						AthletesInformationController.this.redExists.setValue(Boolean.valueOf(true));
						AthletesInformationController.this._bindRedInfo();
					}
				}
			}
		},
				getMessage("title.window.editAthlete"), 950, 600, true);
	}

	@Override
	public void setMatchConfigurationEntry(MatchConfigurationEntry matchConfigurationEntry) {
		this.matchConfigurationEntry = matchConfigurationEntry;
		_bindControls();
	}

	@Override
	public MatchConfigurationEntry getMatchConfigurationEntry() {
		this.matchConfigurationEntry.blueAthleteProperty().get().flag.set(this.blueFlag);
		this.matchConfigurationEntry.redAthleteProperty().get().flag.set(this.redFlag);
		return this.matchConfigurationEntry;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		this.txtBlueVrQuota.setRestrict("^\\d{1,3}$");
		this.txtBlueVrQuota.setDefaultValue("0");
		this.txtRedVrQuota.setRestrict("^\\d{1,3}$");
		this.txtRedVrQuota.setDefaultValue("0");
		this.blueExists.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, final Boolean t1) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						Color color = Color.RED;
						if(t1.booleanValue())
							color = Color.GREEN;
						AthletesInformationController.this.ciBlueExists.setFill(color);
					}
				});
			}
		});
		this.btEditBlue.visibleProperty().bind(this.blueExists);
		this.redExists.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, final Boolean t1) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						Color color = Color.RED;
						if(t1.booleanValue())
							color = Color.GREEN;
						AthletesInformationController.this.ciRedExists.setFill(color);
					}
				});
			}
		});
		this.btEditRed.visibleProperty().bind(this.redExists);
		this.txtBlueWTFID.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
				if( ! t1.booleanValue())
					try {
						AthleteEntry athleteEntry = AthletesInformationController.this.athleteService.getEntryBytWfId(
								AthletesInformationController.this.txtBlueWTFID.getText());
						if(athleteEntry != null && athleteEntry.getId() != null) {
							AthletesInformationController.this.matchConfigurationEntry.blueAthleteProperty().set(athleteEntry);
							AthletesInformationController.this.blueExists.setValue(Boolean.valueOf(true));
						} else {
							AthletesInformationController.this.matchConfigurationEntry.blueAthleteProperty().get().id.set(null);
							AthletesInformationController.this.blueExists.setValue(Boolean.valueOf(false));
						}
						AthletesInformationController.this._bindBlueInfo();
					} catch(TkStrikeServiceException e) {
						e.printStackTrace();
					}
			}
		});
		this.txtBlueAbbr.setMaxLength(15);
		this.txtBlueAbbr.setDefaultValue(null);
		this.txtBlueAbbr.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
				if( ! t1.booleanValue() && AthletesInformationController.this.txtBlueAbbr.getText() != null)
					try {
						FlagEntry flagEntry = AthletesInformationController.this.flagService.getEntryByAbbreviation(
								AthletesInformationController.this.txtBlueAbbr.getText().toUpperCase());
						if(flagEntry != null) {
							AthletesInformationController.this.blueFlag = flagEntry;
						} else {
							AthletesInformationController.this.blueFlag = null;
						}
						AthletesInformationController.this._updateFlag(AthletesInformationController.this.blueFlag,
								AthletesInformationController.this.txtBlueAbbr, AthletesInformationController.this.ivBlueFlag);
					} catch(TkStrikeServiceException e) {
						e.printStackTrace();
					}
			}
		});
		this.txtRedWTFID.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
				if( ! t1.booleanValue())
					try {
						AthleteEntry athleteEntry = AthletesInformationController.this.athleteService.getEntryBytWfId(
								AthletesInformationController.this.txtRedWTFID.getText());
						if(athleteEntry != null && athleteEntry.getId() != null) {
							AthletesInformationController.this.matchConfigurationEntry.redAthleteProperty().set(athleteEntry);
							AthletesInformationController.this.redExists.set(true);
						} else {
							AthletesInformationController.this.matchConfigurationEntry.redAthleteProperty().get().id.set(null);
							AthletesInformationController.this.redExists.set(false);
						}
						AthletesInformationController.this._bindRedInfo();
					} catch(TkStrikeServiceException e) {
						e.printStackTrace();
					}
			}
		});
		this.txtRedAbbr.setMaxLength(15);
		this.txtRedAbbr.setDefaultValue(null);
		this.txtRedAbbr.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
				if( ! t1.booleanValue() && AthletesInformationController.this.txtRedAbbr.getText() != null)
					try {
						FlagEntry flagEntry = AthletesInformationController.this.flagService.getEntryByAbbreviation(
								AthletesInformationController.this.txtRedAbbr.getText().toUpperCase());
						if(flagEntry != null) {
							AthletesInformationController.this.redFlag = flagEntry;
						} else {
							AthletesInformationController.this.redFlag = null;
						}
						AthletesInformationController.this._updateFlag(AthletesInformationController.this.redFlag,
								AthletesInformationController.this.txtRedAbbr, AthletesInformationController.this.ivRedFlag);
					} catch(TkStrikeServiceException e) {
						e.printStackTrace();
					}
			}
		});
	}

	@Override
	public void afterPropertiesSet() throws Exception {}
}
