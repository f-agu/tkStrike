package com.xtremis.daedo.tkstrike.ui.controller.configuration;

import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xtremis.daedo.tkstrike.service.RulesService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchVictoryCriteria;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeBaseController;
import com.xtremis.daedo.tkstrike.ui.model.FormValidationError;
import com.xtremis.daedo.tkstrike.ui.model.RulesEntry;
import com.xtremis.daedo.tkstrike.ui.scene.control.RestrictiveTextField;
import com.xtremis.daedo.tkstrike.ui.scene.listener.CustomTimeLostFocusListener;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;


@Component
public class ConfigurationRulesController extends TkStrikeBaseController {

	@FXML
	private Pane pnContainer;

	@FXML
	private ProgressIndicator pi;

	@FXML
	private ComboBox<Integer> cmbBodyPoints;

	@FXML
	private ComboBox<Integer> cmbHeadPoints;

	@FXML
	private ComboBox<Integer> cmbPunchPoints;

	@FXML
	private ComboBox<Integer> cmbBodyTechPoints;

	@FXML
	private ComboBox<Integer> cmbHeadTechPoints;

	@FXML
	private ComboBox<Integer> cmbOvertimePoints;

	@FXML
	private RestrictiveTextField txtCellingScore;

	@FXML
	private RestrictiveTextField txtDifferencialScore;

	@FXML
	private RestrictiveTextField txtNearMissLevel;

	@FXML
	private ComboBox<Integer> cmbParaSpinningKickPoints;

	@FXML
	private ComboBox<Integer> cmbParaTurningKickPoints;

	@FXML
	private RestrictiveTextField txtPARACellingScore;

	private SimpleObjectProperty<MatchVictoryCriteria> matchVictoryCriteria = new SimpleObjectProperty(this, "matchVictoryCriteria",
			MatchVictoryCriteria.CONVENTIONAL);

	@FXML
	private ToggleButton tgMatchVictoryCriteria;

	@FXML
	private Label lblMatchVictoryCriteria;

	@FXML
	private ToggleButton tgGamJeomShowPointsOnGoldenPoint;

	@FXML
	private ToggleButton tgPointGapAllRounds;

	@FXML
	private ToggleButton tgBonusPointsEnabled;

	@FXML
	private RestrictiveTextField txtBonusPointsMinLevel;

	@FXML
	private RestrictiveTextField txtBonusPointsPoints2Add;

	@FXML
	private ToggleButton tgForceMaxGamJomAllowed;

	@FXML
	private RestrictiveTextField txtMaxGamJomAllowed;

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
	private ToggleButton tgMatchConfigPARA;

	@FXML
	private ToggleButton tgForceShowName;

	@Autowired
	private RulesService rulesService;

	private RulesEntry currentRulesEntry = new RulesEntry();

	protected void showProgressIndicator(final boolean show) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				ConfigurationRulesController.this.pnContainer.setVisible( ! show);
				ConfigurationRulesController.this.pi.setVisible(show);
			}
		});
	}

	@Override
	public void onWindowShowEvent() {
		super.onWindowShowEvent();
		showProgressIndicator(true);
		refreshRulesAndRebind();
	}

	private void refreshRulesAndRebind() {
		try {
			this.currentRulesEntry = (RulesEntry)this.rulesService.getRulesEntry();
		} catch(TkStrikeServiceException e) {
			manageException(e, "refreshRulesAndRebind  - getRulesEntry", null);
		}
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				ConfigurationRulesController.this.cmbBodyPoints.valueProperty().bindBidirectional(ConfigurationRulesController.this.currentRulesEntry
						.bodyPointsProperty());
				ConfigurationRulesController.this.cmbHeadPoints.valueProperty().bindBidirectional(ConfigurationRulesController.this.currentRulesEntry
						.headPointsProperty());
				ConfigurationRulesController.this.cmbPunchPoints.valueProperty().bindBidirectional(ConfigurationRulesController.this.currentRulesEntry
						.punchPointsProperty());
				ConfigurationRulesController.this.cmbBodyTechPoints.valueProperty().bindBidirectional(
						ConfigurationRulesController.this.currentRulesEntry.bodyTechPointsProperty());
				ConfigurationRulesController.this.cmbHeadTechPoints.valueProperty().bindBidirectional(
						ConfigurationRulesController.this.currentRulesEntry.headTechPointsProperty());
				ConfigurationRulesController.this.cmbOvertimePoints.valueProperty().bindBidirectional(
						ConfigurationRulesController.this.currentRulesEntry.overtimePointsProperty());
				ConfigurationRulesController.this.cmbParaSpinningKickPoints.valueProperty().bindBidirectional(
						ConfigurationRulesController.this.currentRulesEntry.paraSpinningKickPointsProperty());
				ConfigurationRulesController.this.cmbParaTurningKickPoints.valueProperty().bindBidirectional(
						ConfigurationRulesController.this.currentRulesEntry.paraTurningKickPointsProperty());
				ConfigurationRulesController.this.txtPARACellingScore.textProperty().bindBidirectional(
						(Property)ConfigurationRulesController.this.currentRulesEntry.paraCellingScoreProperty(),
						(StringConverter)new NumberStringConverter());
				ConfigurationRulesController.this.txtCellingScore.textProperty().bindBidirectional(
						(Property)ConfigurationRulesController.this.currentRulesEntry.cellingScoreProperty(),
						(StringConverter)new NumberStringConverter());
				ConfigurationRulesController.this.txtDifferencialScore.textProperty().bindBidirectional(
						(Property)ConfigurationRulesController.this.currentRulesEntry.differencialScoreProperty(),
						(StringConverter)new NumberStringConverter());
				ConfigurationRulesController.this.txtNearMissLevel.textProperty().bindBidirectional(
						(Property)ConfigurationRulesController.this.currentRulesEntry.nearMissLevelProperty(),
						(StringConverter)new NumberStringConverter());
				ConfigurationRulesController.this.roundsProperty.bindBidirectional(ConfigurationRulesController.this.currentRulesEntry
						.getRoundsConfig().roundsProperty());
				ConfigurationRulesController.this.txtRoundMinutes.textProperty().bindBidirectional(ConfigurationRulesController.this.currentRulesEntry
						.getRoundsConfig().roundTimeMinutesProperty());
				ConfigurationRulesController.this.txtRoundSeconds.textProperty().bindBidirectional(ConfigurationRulesController.this.currentRulesEntry
						.getRoundsConfig().roundTimeSecondsProperty());
				ConfigurationRulesController.this.txtKyeShiMinutes.textProperty().bindBidirectional(
						ConfigurationRulesController.this.currentRulesEntry.getRoundsConfig().kyeShiTimeMinutesProperty());
				ConfigurationRulesController.this.txtKyeShiSeconds.textProperty().bindBidirectional(
						ConfigurationRulesController.this.currentRulesEntry.getRoundsConfig().kyeShiTimeSecondsProperty());
				ConfigurationRulesController.this.txtRestMinutes.textProperty().bindBidirectional(ConfigurationRulesController.this.currentRulesEntry
						.getRoundsConfig().restTimeMinutesProperty());
				ConfigurationRulesController.this.txtRestSeconds.textProperty().bindBidirectional(ConfigurationRulesController.this.currentRulesEntry
						.getRoundsConfig().restTimeSecondsProperty());
				ConfigurationRulesController.this.chkGoldenPoint.selectedProperty().bindBidirectional(
						ConfigurationRulesController.this.currentRulesEntry.getRoundsConfig().goldenPointEnabledProperty());
				ConfigurationRulesController.this.txtGoldenPointTimeMinutes.textProperty().bindBidirectional(
						ConfigurationRulesController.this.currentRulesEntry.getRoundsConfig().goldenPointTimeMinutesProperty());
				ConfigurationRulesController.this.txtGoldenPointTimeSeconds.textProperty().bindBidirectional(
						ConfigurationRulesController.this.currentRulesEntry.getRoundsConfig().goldenPointTimeSecondsProperty());
				ConfigurationRulesController.this.tgMatchConfigPARA.selectedProperty().bindBidirectional(
						ConfigurationRulesController.this.currentRulesEntry.allMatchPARAProperty());
				ConfigurationRulesController.this.tgForceShowName.selectedProperty().bindBidirectional(
						ConfigurationRulesController.this.currentRulesEntry.forceShowNameProperty());
				ConfigurationRulesController.this.tgMatchVictoryCriteria.setSelected(MatchVictoryCriteria.BESTOF3.equals(
						ConfigurationRulesController.this.currentRulesEntry.getMatchVictoryCriteria()));
				ConfigurationRulesController.this.matchVictoryCriteria.bindBidirectional(ConfigurationRulesController.this.currentRulesEntry
						.matchVictoryCriteriaProperty());
				ConfigurationRulesController.this.tgGamJeomShowPointsOnGoldenPoint.selectedProperty().bindBidirectional(
						ConfigurationRulesController.this.currentRulesEntry.gamJeomShowPointsOnGoldenPointProperty());
				ConfigurationRulesController.this.tgPointGapAllRounds.selectedProperty().bindBidirectional(
						ConfigurationRulesController.this.currentRulesEntry.pointGapAllRoundsProperty());
				ConfigurationRulesController.this.tgBonusPointsEnabled.selectedProperty().bindBidirectional(
						ConfigurationRulesController.this.currentRulesEntry.bonusPointsEnabledProperty());
				ConfigurationRulesController.this.txtBonusPointsMinLevel.textProperty().bindBidirectional(
						ConfigurationRulesController.this.currentRulesEntry.bonusPointsMinLevelProperty(),
						(StringConverter)new NumberStringConverter());
				ConfigurationRulesController.this.txtBonusPointsMinLevel.setDisable( ! ConfigurationRulesController.this.currentRulesEntry
						.getBonusPointsEnabled().booleanValue());
				ConfigurationRulesController.this.txtBonusPointsPoints2Add.textProperty().bindBidirectional(
						ConfigurationRulesController.this.currentRulesEntry.bonusPointsPoints2AddProperty(),
						(StringConverter)new NumberStringConverter());
				ConfigurationRulesController.this.txtBonusPointsPoints2Add.setDisable( ! ConfigurationRulesController.this.currentRulesEntry
						.getBonusPointsEnabled().booleanValue());
				ConfigurationRulesController.this.tgForceMaxGamJomAllowed.selectedProperty().bindBidirectional(
						ConfigurationRulesController.this.currentRulesEntry.forceMaxGamJomAllowedProperty());
				ConfigurationRulesController.this.txtMaxGamJomAllowed.textProperty().bindBidirectional(
						ConfigurationRulesController.this.currentRulesEntry.maxGamJomAllowedProperty(), (StringConverter)new NumberStringConverter());
				ConfigurationRulesController.this.txtMaxGamJomAllowed.setDisable( ! ConfigurationRulesController.this.currentRulesEntry
						.getForceMaxGamJomAllowed().booleanValue());
				ConfigurationRulesController.this.showProgressIndicator(false);
			}
		});
	}

	public void save() {
		if(isFormValid()) {
			showProgressIndicator(true);
			TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					try {
						ConfigurationRulesController.this.rulesService.update(ConfigurationRulesController.this.currentRulesEntry.createRules());
					} catch(TkStrikeServiceException e) {
						ConfigurationRulesController.this.showProgressIndicator(false);
						ConfigurationRulesController.this.manageException(e, "", null);
						return null;
					}
					ConfigurationRulesController.this.refreshRulesAndRebind();
					return null;
				}
			});
		}
	}

	public void undo() {
		refreshRulesAndRebind();
	}

	@Override
	protected Collection<Control> getFormControls() {
		return FXCollections.observableArrayList(new Control[] {
				(Control)this.cmbBodyPoints, (Control)this.cmbHeadPoints, (Control)this.cmbPunchPoints, (Control)this.cmbBodyTechPoints,
				(Control)this.cmbHeadTechPoints, (Control)this.cmbOvertimePoints, (Control)this.cmbParaSpinningKickPoints,
				(Control)this.cmbParaTurningKickPoints, this.txtPARACellingScore, this.txtCellingScore,
				this.txtDifferencialScore, this.txtNearMissLevel, this.txtRoundMinutes, this.txtKyeShiMinutes, this.txtRestMinutes,
				this.txtGoldenPointTimeMinutes, (Control)this.cmbRounds});
	}

	@Override
	public LinkedHashSet<FormValidationError> validateForm() {
		LinkedHashSet<FormValidationError> res = null;
		if(this.cmbBodyPoints.getValue() == null || this.cmbBodyPoints.getValue().intValue() == 0) {
			res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.currentRulesEntry, "bodyPoints", this.cmbBodyPoints, getMessage("validation.required")));
		}
		if(this.cmbHeadPoints.getValue() == null || this.cmbHeadPoints.getValue().intValue() == 0) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.currentRulesEntry, "headPoints", this.cmbHeadPoints, getMessage("validation.required")));
		}
		if(this.cmbPunchPoints.getValue() == null || this.cmbPunchPoints.getValue().intValue() == 0) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.currentRulesEntry, "punchPoints", this.cmbPunchPoints, getMessage("validation.required")));
		}
		if(this.cmbBodyTechPoints.getValue() == null || this.cmbBodyTechPoints.getValue().intValue() == 0) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.currentRulesEntry, "bodyTechPoints", this.cmbBodyTechPoints, getMessage("validation.required")));
		}
		if(this.cmbHeadTechPoints.getValue() == null || this.cmbHeadTechPoints.getValue().intValue() == 0) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.currentRulesEntry, "headTechPoints", this.cmbHeadTechPoints, getMessage("validation.required")));
		}
		if(this.cmbOvertimePoints.getValue() == null || this.cmbOvertimePoints.getValue().intValue() == 0) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.currentRulesEntry, "overtimePoints", this.cmbOvertimePoints, getMessage("validation.required")));
		}
		if(this.cmbParaSpinningKickPoints.getValue() == null || this.cmbParaSpinningKickPoints.getValue().intValue() == 0) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.currentRulesEntry, "paraSpinningKickPoints", this.cmbParaSpinningKickPoints, getMessage(
					"validation.required")));
		}
		if(this.cmbParaTurningKickPoints.getValue() == null || this.cmbParaTurningKickPoints.getValue().intValue() == 0) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.currentRulesEntry, "paraTurningKickPoints", this.cmbParaTurningKickPoints, getMessage(
					"validation.required")));
		}
		if(StringUtils.isBlank(this.txtPARACellingScore.getText())) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.currentRulesEntry, "paraCellingScore", this.txtPARACellingScore, getMessage("validation.required")));
		}
		if(StringUtils.isBlank(this.txtCellingScore.getText())) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.currentRulesEntry, "cellingScore", this.txtCellingScore, getMessage("validation.required")));
		}
		if(StringUtils.isBlank(this.txtNearMissLevel.getText())) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.currentRulesEntry, "nearMissLevel", this.txtNearMissLevel, getMessage("validation.required")));
		}
		if(StringUtils.isBlank(this.txtDifferencialScore.getText())) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.currentRulesEntry, "differencialScore", this.txtDifferencialScore, getMessage(
					"validation.required")));
		}
		if(_validateTime(this.txtRestMinutes.getText(), this.txtRestSeconds.getText())) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.currentRulesEntry, "roundTimeMinutes", this.txtRoundMinutes, getMessage(
					"validation.incorrectValue")));
		}
		if(_validateTime(this.txtKyeShiMinutes.getText(), this.txtKyeShiSeconds.getText())) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.currentRulesEntry, "kyeShiTimeMinutes", this.txtKyeShiMinutes, getMessage(
					"validation.incorrectValue")));
		}
		if(_validateTime(this.txtRestMinutes.getText(), this.txtRestSeconds.getText())) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.currentRulesEntry, "restTimeMinutes", this.txtRestMinutes, getMessage("validation.incorrectValue")));
		}
		if(this.chkGoldenPoint.isSelected() &&
				_validateTime(this.txtGoldenPointTimeMinutes.getText(), this.txtGoldenPointTimeSeconds.getText())) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.currentRulesEntry, "goldenPointTimeMinutes", this.txtGoldenPointTimeMinutes, getMessage(
					"validation.incorrectValue")));
		}
		if(this.cmbRounds.getValue() == null || this.cmbRounds.getValue().intValue() <= 0) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.currentRulesEntry, "rounds", this.cmbRounds, getMessage("validation.required")));
		}
		return res;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		_initializePointsComboBox(this.cmbBodyPoints);
		_initializePointsComboBox(this.cmbHeadPoints);
		_initializePointsComboBox(this.cmbPunchPoints);
		_initializePointsComboBox(this.cmbBodyTechPoints);
		_initializePointsComboBox(this.cmbHeadTechPoints);
		_initializePointsComboBox(this.cmbOvertimePoints);
		_initializePointsComboBox(this.cmbParaSpinningKickPoints);
		_initializePointsComboBox(this.cmbParaTurningKickPoints);
		this.txtPARACellingScore.setMaxLength(3);
		this.txtPARACellingScore.setRestrict("^0*(?:[1-9][0-9]?|100)$");
		this.txtPARACellingScore.setDefaultValue("1");
		this.txtCellingScore.setMaxLength(3);
		this.txtCellingScore.setRestrict("^0*(?:[1-9][0-9]?|100)$");
		this.txtCellingScore.setDefaultValue("1");
		this.txtDifferencialScore.setMaxLength(3);
		this.txtDifferencialScore.setRestrict("^0*(?:[1-9][0-9]?|100)$");
		this.txtDifferencialScore.setDefaultValue("1");
		this.txtNearMissLevel.setMaxLength(3);
		this.txtNearMissLevel.setRestrict("^0*(?:[1-9][0-9]?|100)$");
		this.txtNearMissLevel.setDefaultValue("1");
		for(int i = 1; i <= 10; i++)
			this.cmbRounds.getItems().add(Integer.valueOf(i));
		this.cmbRounds.valueProperty().addListener(new ChangeListener<Integer>() {

			@Override
			public void changed(ObservableValue<? extends Integer> observableValue, Integer integer, Integer t1) {
				if(t1 != null && t1.intValue() > 0)
					ConfigurationRulesController.this.roundsProperty.setValue(t1);
			}
		});
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
		this.tgMatchConfigPARA.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				String label = ConfigurationRulesController.this.getMessage("toggle.paraTKD.byConfig");
				if(newValue.booleanValue())
					label = ConfigurationRulesController.this.getMessage("toggle.paraTKD.all");
				ConfigurationRulesController.this.tgMatchConfigPARA.setText(label);
			}
		});
		this.tgForceShowName.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				ConfigurationRulesController.this.tgForceShowName.setText(ConfigurationRulesController.this.getMessage(String.format(
						"toggle.forceShowName.%s", new Object[] {newValue.toString().toLowerCase()})));
			}
		});
		this.tgMatchVictoryCriteria.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, final Boolean newValue) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						String toggleText = ConfigurationRulesController.this.getMessage("toggle.matchVictoryCriteria.byPoints");
						String labelText = ConfigurationRulesController.this.getMessage("toggle.matchVictoryCriteria.byPoints.extraInfo");
						MatchVictoryCriteria theNewValue = MatchVictoryCriteria.CONVENTIONAL;
						if(newValue.booleanValue()) {
							toggleText = ConfigurationRulesController.this.getMessage("toggle.matchVictoryCriteria.byRounds");
							labelText = ConfigurationRulesController.this.getMessage("toggle.matchVictoryCriteria.byRounds.extraInfo");
							theNewValue = MatchVictoryCriteria.BESTOF3;
						}
						ConfigurationRulesController.this.tgMatchVictoryCriteria.setText(toggleText);
						ConfigurationRulesController.this.lblMatchVictoryCriteria.setText(labelText);
						ConfigurationRulesController.this.matchVictoryCriteria.setValue(theNewValue);
					}
				});
			}
		});
		this.tgGamJeomShowPointsOnGoldenPoint.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				String textKey = "toggle.gamJeomShowPointsOnGoldenPoint.true";
				if( ! newValue.booleanValue())
					textKey = "toggle.gamJeomShowPointsOnGoldenPoint.false";
				ConfigurationRulesController.this.tgGamJeomShowPointsOnGoldenPoint.setText(ConfigurationRulesController.this.getMessage(textKey));
			}
		});
		this.tgPointGapAllRounds.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				ConfigurationRulesController.this.tgPointGapAllRounds.setText(ConfigurationRulesController.this.getMessage(String.format(
						"toggle.pointGapAllRounds.%s", new Object[] {newValue.toString().toLowerCase()})));
			}
		});
		this.txtBonusPointsMinLevel.setMaxLength(3);
		this.txtBonusPointsMinLevel.setRestrict("^0*(?:[1-9][0-9]?|100)$");
		this.txtBonusPointsMinLevel.setDefaultValue("1");
		this.txtBonusPointsPoints2Add.setMaxLength(2);
		this.txtBonusPointsPoints2Add.setRestrict("^\\d{1,2}$");
		this.txtBonusPointsPoints2Add.setDefaultValue("1");
		this.tgBonusPointsEnabled.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				ConfigurationRulesController.this.tgBonusPointsEnabled.setText(ConfigurationRulesController.this.getMessage(String.format(
						"toggle.bonusPoints.%s", new Object[] {newValue.booleanValue() ? "enabled" : "disabled"})));
				ConfigurationRulesController.this.txtBonusPointsMinLevel.setDisable( ! newValue.booleanValue());
				ConfigurationRulesController.this.txtBonusPointsPoints2Add.setDisable( ! newValue.booleanValue());
			}
		});
		this.tgForceMaxGamJomAllowed.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				ConfigurationRulesController.this.tgForceMaxGamJomAllowed.setText(ConfigurationRulesController.this.getMessage(String.format(
						"toggle.maxGamJomAllowed.%s", new Object[] {newValue.booleanValue() ? "enabled" : "disabled"})));
				ConfigurationRulesController.this.txtMaxGamJomAllowed.setDisable( ! newValue.booleanValue());
			}
		});
		this.txtMaxGamJomAllowed.setMaxLength(2);
		this.txtMaxGamJomAllowed.setRestrict("^\\d{1,2}$");
		this.txtMaxGamJomAllowed.setDefaultValue("10");
	}

	@Override
	public void afterPropertiesSet() throws Exception {}

	private void _initializePointsComboBox(ComboBox<Integer> cmb) {
		for(int i = 1; i <= 10; i++)
			cmb.getItems().add(Integer.valueOf(i));
	}

	private boolean _validateTime(String minutes, String seconds) {
		return (Integer.parseInt(minutes) == 0 && Integer.parseInt(seconds) == 0);
	}
}
