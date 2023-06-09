package com.xtremis.daedo.tkstrike.ui.controller.ringmanager;

import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.xtremis.daedo.tkstrike.service.DifferentialScoreDefinitionService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchVictoryCriteria;
import com.xtremis.daedo.tkstrike.ui.model.DifferentialScoreDefinitionEntry;
import com.xtremis.daedo.tkstrike.ui.model.FormValidationError;
import com.xtremis.daedo.tkstrike.ui.model.MatchConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.RulesEntry;
import com.xtremis.daedo.tkstrike.ui.scene.control.RestrictiveTextField;
import com.xtremis.daedo.tkstrike.ui.scene.listener.CustomTimeLostFocusListener;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;


public class MatchConfigurationController extends BaseStepWizardController implements StepWizardController {

	private SimpleBooleanProperty differentialScoreByDef = new SimpleBooleanProperty(this, "differentialScoreByDef", Boolean.FALSE.booleanValue());

	@FXML
	private ComboBox<Integer> cmbRounds;

	private Property roundsProperty = new SimpleIntegerProperty();

	@FXML
	private RestrictiveTextField txtRoundMinutes;

	@FXML
	private RestrictiveTextField txtRoundSeconds;

	@FXML
	private RestrictiveTextField txtKyeShiMinutes;

	@FXML
	private RestrictiveTextField txtKyeShiSeconds;

	@FXML
	private RestrictiveTextField txtRestMinutes;

	@FXML
	private RestrictiveTextField txtRestSeconds;

	@FXML
	private RestrictiveTextField txtGoldenPointTimeMinutes;

	@FXML
	private RestrictiveTextField txtGoldenPointTimeSeconds;

	@FXML
	private CheckBox chkGoldenPoint;

	@FXML
	private CheckBox chkIsParaTkdMatch;

	@FXML
	private Label lblDifferentialScore;

	@FXML
	private RestrictiveTextField txtDifferencialScore;

	@FXML
	private RestrictiveTextField txtMaxGamJeoms;

	private SimpleStringProperty maxGamJeomsProperty = new SimpleStringProperty(this, "maxGamJeoms", "1");

	private SimpleObjectProperty<MatchVictoryCriteria> matchVictoryCriteria = new SimpleObjectProperty(this, "matchVictoryCriteria",
			MatchVictoryCriteria.CONVENTIONAL);

	@FXML
	private ToggleButton tgMatchVictoryCriteria;

	@FXML
	private Label lblMatchVictoryCriteria;

	@Value("${tkStrike.maxGamJeomsAllowed}")
	private Integer maxGamJeomsAllowed;

	@Autowired
	private DifferentialScoreDefinitionService differentialScoreDefinitionService;

	private MatchConfigurationEntry matchConfigurationEntry = null;

	@Override
	public void clearForm() {
		this.txtRoundMinutes.clear();
		this.txtRoundSeconds.clear();
		this.txtKyeShiMinutes.clear();
		this.txtKyeShiSeconds.clear();
		this.txtRestMinutes.clear();
		this.txtRestSeconds.clear();
		this.txtGoldenPointTimeMinutes.clear();
		this.txtGoldenPointTimeSeconds.clear();
		this.txtMaxGamJeoms.clear();
	}

	@Override
	protected Collection<Control> getFormControls() {
		return FXCollections.observableArrayList(new Control[] {this.txtRoundMinutes, this.txtKyeShiMinutes, this.txtRestMinutes,
				this.txtGoldenPointTimeMinutes, this.txtDifferencialScore, this.txtMaxGamJeoms, (Control)this.cmbRounds});
	}

	@Override
	public LinkedHashSet<FormValidationError> validateForm() {
		LinkedHashSet<FormValidationError> res = null;
		if(_validateTime(this.txtRestMinutes.getText(), this.txtRestSeconds.getText())) {
			res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.matchConfigurationEntry, "roundTimeMinutes", this.txtRoundMinutes, getMessage(
					"validation.incorrectValue")));
		}
		if(_validateTime(this.txtKyeShiMinutes.getText(), this.txtKyeShiSeconds.getText())) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.matchConfigurationEntry, "kyeShiTimeMinutes", this.txtKyeShiMinutes, getMessage(
					"validation.incorrectValue")));
		}
		if(_validateTime(this.txtRestMinutes.getText(), this.txtRestSeconds.getText())) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.matchConfigurationEntry, "restTimeMinutes", this.txtRestMinutes, getMessage(
					"validation.incorrectValue")));
		}
		if(StringUtils.isBlank(this.txtDifferencialScore.getText())) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.matchConfigurationEntry, "differencialScore", this.txtDifferencialScore, getMessage(
					"validation.required")));
		}
		if(this.chkGoldenPoint.isSelected() &&
				_validateTime(this.txtGoldenPointTimeMinutes.getText(), this.txtGoldenPointTimeSeconds.getText())) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.matchConfigurationEntry, "goldenPointTimeMinutes", this.txtGoldenPointTimeMinutes, getMessage(
					"validation.incorrectValue")));
		}
		if(this.cmbRounds.getValue() == null || this.cmbRounds.getValue().intValue() <= 0) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.matchConfigurationEntry, "rounds", this.cmbRounds, getMessage("validation.required")));
		}
		if(StringUtils.isBlank(this.txtMaxGamJeoms.getText()) || ! StringUtils.isNumeric(this.txtMaxGamJeoms.getText())) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(null, "maxGamJeoms", this.txtMaxGamJeoms, getMessage("validation.required")));
		}
		return res;
	}

	private boolean _validateTime(String minutes, String seconds) {
		return (Integer.parseInt(minutes) == 0 && Integer.parseInt(seconds) == 0);
	}

	@Override
	public void onWindowShowEvent() {
		super.onWindowShowEvent();
		_bindControls();
	}

	@Override
	public void setMatchConfigurationEntry(MatchConfigurationEntry matchConfigurationEntry) {
		this.matchConfigurationEntry = matchConfigurationEntry;
		this.roundsProperty.setValue(matchConfigurationEntry.roundsConfigProperty().get().roundsProperty().getValue());
		Integer tempMaxAllowedGamJeoms = matchConfigurationEntry.getMaxAllowedGamJeoms();
		if(tempMaxAllowedGamJeoms.equals(Integer.valueOf(0))) {
			this.maxGamJeomsProperty.set("" + this.maxGamJeomsAllowed);
		} else {
			this.maxGamJeomsProperty.set("" + tempMaxAllowedGamJeoms);
		}
		_bindControls();
		try {
			DifferentialScoreDefinitionEntry differentialScoreDefinitionEntry = this.differentialScoreDefinitionService
					.getEntryByPhaseIdAndSubCategoryId(matchConfigurationEntry.getPhase().getId(), matchConfigurationEntry.getSubCategory().getId());
			if(differentialScoreDefinitionEntry != null) {
				matchConfigurationEntry.differencialScoreProperty().setValue(Integer.valueOf(differentialScoreDefinitionEntry.getValue()));
				this.differentialScoreByDef.set(true);
			} else {
				this.differentialScoreByDef.set(false);
			}
		} catch(TkStrikeServiceException e) {
			e.printStackTrace();
		}
	}

	private void _bindControls() {
		if(this.matchConfigurationEntry != null) {
			this.txtRoundMinutes.textProperty().bindBidirectional(this.matchConfigurationEntry.getRoundsConfig().roundTimeMinutesProperty());
			this.txtRoundSeconds.textProperty().bindBidirectional(this.matchConfigurationEntry.getRoundsConfig().roundTimeSecondsProperty());
			this.txtKyeShiMinutes.textProperty().bindBidirectional(this.matchConfigurationEntry.getRoundsConfig().kyeShiTimeMinutesProperty());
			this.txtKyeShiSeconds.textProperty().bindBidirectional(this.matchConfigurationEntry.getRoundsConfig().kyeShiTimeSecondsProperty());
			this.txtRestMinutes.textProperty().bindBidirectional(this.matchConfigurationEntry.getRoundsConfig().restTimeMinutesProperty());
			this.txtRestSeconds.textProperty().bindBidirectional(this.matchConfigurationEntry.getRoundsConfig().restTimeSecondsProperty());
			this.chkGoldenPoint.selectedProperty().bindBidirectional(this.matchConfigurationEntry.getRoundsConfig().goldenPointEnabledProperty());
			this.txtGoldenPointTimeMinutes.textProperty().bindBidirectional(this.matchConfigurationEntry.getRoundsConfig()
					.goldenPointTimeMinutesProperty());
			this.txtGoldenPointTimeSeconds.textProperty().bindBidirectional(this.matchConfigurationEntry.getRoundsConfig()
					.goldenPointTimeSecondsProperty());
			RulesEntry rulesEntry = (RulesEntry)getAppStatusWorker().getRulesEntry();
			if(rulesEntry != null && rulesEntry.isAllMatchPARA()) {
				this.chkIsParaTkdMatch.setSelected(true);
				this.chkIsParaTkdMatch.setDisable(true);
				this.matchConfigurationEntry.isParaTkdMatchProperty().set(true);
			} else {
				this.chkIsParaTkdMatch.selectedProperty().bindBidirectional(this.matchConfigurationEntry.isParaTkdMatchProperty());
				this.chkIsParaTkdMatch.setDisable(false);
			}
			this.txtDifferencialScore.textProperty().bindBidirectional((Property)this.matchConfigurationEntry.differencialScoreProperty(),
					(StringConverter)new NumberStringConverter());
			if(this.matchConfigurationEntry.getId() == null)
				this.matchConfigurationEntry.setMatchVictoryCriteria(getAppStatusWorker().getRulesEntry()
						.getMatchVictoryCriteria());
			this.tgMatchVictoryCriteria.setSelected(MatchVictoryCriteria.BESTOF3.equals(this.matchConfigurationEntry.getMatchVictoryCriteria()));
			this.matchVictoryCriteria.bindBidirectional(this.matchConfigurationEntry.matchVictoryCriteriaProperty());
		}
	}

	@Override
	public MatchConfigurationEntry getMatchConfigurationEntry() {
		this.matchConfigurationEntry.getRoundsConfig().roundsProperty().set(((Integer)this.roundsProperty.getValue()).intValue());
		this.matchConfigurationEntry.setMaxAllowedGamJeoms(Integer.parseInt(this.maxGamJeomsProperty.getValue()));
		return this.matchConfigurationEntry;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		this.differentialScoreByDef.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				final StringBuilder lblMessage = new StringBuilder(MatchConfigurationController.this.getMessage("label.differencialScore"));
				if(newValue.booleanValue())
					lblMessage.append(" " + MatchConfigurationController.this.getMessage("message.differentialScoreDefinedByPhaseAndSubCategory"));
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						MatchConfigurationController.this.lblDifferentialScore.setText(lblMessage.toString());
					}
				});
			}
		});
		for(int i = 1; i <= 10; i++)
			this.cmbRounds.getItems().add(Integer.valueOf(i));
		this.txtGoldenPointTimeMinutes.visibleProperty().bind(this.chkGoldenPoint.selectedProperty());
		this.txtGoldenPointTimeSeconds.visibleProperty().bind(this.chkGoldenPoint.selectedProperty());
		this.cmbRounds.valueProperty().bindBidirectional(this.roundsProperty);
		this.txtRoundMinutes.setRestrict("[0-5][0-9]");
		this.txtRoundMinutes.setPromptText("mm");
		this.txtRoundMinutes.setDefaultValue("00");
		this.txtRoundMinutes.focusedProperty().addListener(new CustomTimeLostFocusListener(this.txtRoundMinutes, "00"));
		this.txtRoundSeconds.setRestrict("[0-5][0-9]");
		this.txtRoundSeconds.setPromptText("ss");
		this.txtRoundSeconds.setDefaultValue("00");
		this.txtRoundSeconds.focusedProperty().addListener(new CustomTimeLostFocusListener(this.txtRoundSeconds, "00"));
		this.txtKyeShiMinutes.setRestrict("[0-5][0-9]");
		this.txtKyeShiMinutes.setPromptText("mm");
		this.txtKyeShiMinutes.setDefaultValue("00");
		this.txtKyeShiMinutes.focusedProperty().addListener(new CustomTimeLostFocusListener(this.txtKyeShiMinutes, "00"));
		this.txtKyeShiSeconds.setRestrict("[0-5][0-9]");
		this.txtKyeShiSeconds.setDefaultValue("00");
		this.txtKyeShiSeconds.focusedProperty().addListener(new CustomTimeLostFocusListener(this.txtKyeShiSeconds, "00"));
		this.txtKyeShiSeconds.setPromptText("ss");
		this.txtRestMinutes.setRestrict("[0-5][0-9]");
		this.txtRestMinutes.setPromptText("mm");
		this.txtRestMinutes.setDefaultValue("00");
		this.txtRestMinutes.focusedProperty().addListener(new CustomTimeLostFocusListener(this.txtRestMinutes, "00"));
		this.txtRestSeconds.setRestrict("[0-5][0-9]");
		this.txtRestSeconds.setDefaultValue("00");
		this.txtRestSeconds.setPromptText("ss");
		this.txtRestSeconds.focusedProperty().addListener(new CustomTimeLostFocusListener(this.txtRestSeconds, "00"));
		this.txtGoldenPointTimeMinutes.setRestrict("[0-5][0-9]");
		this.txtGoldenPointTimeMinutes.setPromptText("mm");
		this.txtGoldenPointTimeMinutes.setDefaultValue("00");
		this.txtGoldenPointTimeMinutes.focusedProperty().addListener(new CustomTimeLostFocusListener(this.txtGoldenPointTimeMinutes, "00"));
		this.txtGoldenPointTimeSeconds.setRestrict("[0-5][0-9]");
		this.txtGoldenPointTimeSeconds.setPromptText("ss");
		this.txtGoldenPointTimeSeconds.setDefaultValue("00");
		this.txtGoldenPointTimeSeconds.focusedProperty().addListener(new CustomTimeLostFocusListener(this.txtGoldenPointTimeSeconds, "00"));
		this.txtDifferencialScore.setMaxLength(3);
		this.txtDifferencialScore.setRestrict("^0*(?:[1-9][0-9]?|100)$");
		this.txtDifferencialScore.setDefaultValue("1");
		this.txtMaxGamJeoms.setRestrict("^0*(?:[1-9][0-9]?|100)$");
		this.txtMaxGamJeoms.setDefaultValue("10");
		this.txtMaxGamJeoms.setMaxLength(3);
		this.txtMaxGamJeoms.textProperty().bindBidirectional(this.maxGamJeomsProperty);
		this.tgMatchVictoryCriteria.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, final Boolean newValue) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						String toggleText = MatchConfigurationController.this.getMessage("toggle.matchVictoryCriteria.byPoints");
						String labelText = MatchConfigurationController.this.getMessage("toggle.matchVictoryCriteria.byPoints.extraInfo");
						MatchVictoryCriteria theNewValue = MatchVictoryCriteria.CONVENTIONAL;
						if(newValue.booleanValue()) {
							toggleText = MatchConfigurationController.this.getMessage("toggle.matchVictoryCriteria.byRounds");
							labelText = MatchConfigurationController.this.getMessage("toggle.matchVictoryCriteria.byRounds.extraInfo");
							theNewValue = MatchVictoryCriteria.BESTOF3;
						}
						MatchConfigurationController.this.tgMatchVictoryCriteria.setText(toggleText);
						MatchConfigurationController.this.lblMatchVictoryCriteria.setText(labelText);
						MatchConfigurationController.this.matchVictoryCriteria.setValue(theNewValue);
					}
				});
			}
		});
	}

	@Override
	public void afterPropertiesSet() throws Exception {}
}
