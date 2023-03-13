package com.xtremis.daedo.tkstrike.ui.controller.ringmanager;

import java.net.URL;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xtremis.daedo.tkstrike.orm.model.Athlete;
import com.xtremis.daedo.tkstrike.orm.model.Flag;
import com.xtremis.daedo.tkstrike.service.AthleteService;
import com.xtremis.daedo.tkstrike.service.FlagService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.ei.om.AthleteDto;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeBaseController;
import com.xtremis.daedo.tkstrike.ui.model.AthleteEntry;
import com.xtremis.daedo.tkstrike.ui.model.FormValidationError;
import com.xtremis.daedo.tkstrike.ui.scene.FlagEntry;
import com.xtremis.daedo.tkstrike.ui.scene.control.RestrictiveTextField;
import com.xtremis.daedo.wtdata.model.constants.CompetitorType;
import com.xtremis.daedo.wtdata.model.constants.Gender;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;


@Component
public class EditAthleteController extends TkStrikeBaseController {

	private final ToggleGroup tgGender = new ToggleGroup();

	private final ToggleGroup tgCompetitorType = new ToggleGroup();

	private boolean saved = false;

	private String currentAthleteId;

	@Autowired
	private FlagService flagService;

	@Autowired
	private AthleteService athleteService;

	private AthleteEntry currentAthleteEntry = new AthleteEntry();

	@FXML
	private Pane pnMain;

	@FXML
	private Pane piPanel;

	@FXML
	private TextField txtWtId;

	@FXML
	private TextField txtOvrInternalId;

	@FXML
	private Circle ciExists;

	private SimpleBooleanProperty exists = new SimpleBooleanProperty();

	@FXML
	private TextField txtScoreboardName;

	@FXML
	private RestrictiveTextField txtFlagAbbr;

	private FlagEntry flag;

	@FXML
	private ImageView ivFlag;

	@FXML
	private TextField txtGivenName;

	@FXML
	private TextField txtFamilyName;

	@FXML
	private TextField txtPassportGivenName;

	@FXML
	private TextField txtPassportFamilyName;

	@FXML
	private TextField txtPreferredGivenName;

	@FXML
	private TextField txtPreferredFamilyName;

	@FXML
	private TextField txtPrintName;

	@FXML
	private TextField txtPrintInitialName;

	@FXML
	private TextField txtTVName;

	@FXML
	private TextField txtTVInitialName;

	@FXML
	private DatePicker dpBirthDate;

	@FXML
	private ToggleButton tgCompetitorTypeA;

	@FXML
	private ToggleButton tgCompetitorTypeT;

	@FXML
	private ToggleButton tgGenderFEMALE;

	@FXML
	private ToggleButton tgGenderMALE;

	@FXML
	private ToggleButton tgGenderMIXED;

	@FXML
	private RestrictiveTextField txtRank;

	@FXML
	private RestrictiveTextField txtSeed;

	public boolean isSaved() {
		return this.saved;
	}

	public String getCurrentAthleteId() {
		return this.currentAthleteId;
	}

	public void setCurrentAthleteId(String currentAthleteId) {
		this.currentAthleteId = currentAthleteId;
	}

	public void doCancel() {
		doCloseThisStage();
	}

	public void doSave() {
		this.saved = true;
		if(isFormValid())
			TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					EditAthleteController.this.showProgressIndicator(true);
					AthleteDto athleteDto = EditAthleteController.this.currentAthleteEntry.getAthleteDto();
					Flag flagEntity = EditAthleteController.this.flagService.getById(EditAthleteController.this.flag.getId());
					try {
						if(EditAthleteController.this.currentAthleteId == null) {
							Athlete created = EditAthleteController.this.athleteService.createNew(athleteDto, flagEntity);
							if(created != null)
								EditAthleteController.this.currentAthleteId = created.getId();
						} else {
							EditAthleteController.this.athleteService.update(EditAthleteController.this.currentAthleteId, athleteDto, flagEntity);
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
					EditAthleteController.this.showProgressIndicator(false);
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							EditAthleteController.this.doCloseThisStage();
						}
					});
					return null;
				}
			});
	}

	@Override
	public LinkedHashSet<FormValidationError> validateForm() {
		LinkedHashSet<FormValidationError> res = null;
		if(StringUtils.isBlank(this.txtWtId.getText())) {
			res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.currentAthleteEntry, "wtId", this.txtWtId, getMessage("validation.required")));
		}
		if(StringUtils.isBlank(this.txtScoreboardName.getText())) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.currentAthleteEntry, "scoreboardName", this.txtScoreboardName, getMessage("validation.required")));
		}
		if(StringUtils.isBlank(this.txtFlagAbbr.getText())) {
			if(res == null)
				res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.currentAthleteEntry, "flag", this.txtFlagAbbr, getMessage("validation.required")));
		}
		return res;
	}

	@Override
	protected Collection<Control> getFormControls() {
		return FXCollections.observableArrayList(new Control[] {
				this.txtWtId, this.txtOvrInternalId, this.txtScoreboardName, this.txtFlagAbbr, this.txtGivenName, this.txtFamilyName,
				this.txtPassportGivenName, this.txtPassportFamilyName, this.txtPreferredGivenName, this.txtPreferredFamilyName,
				this.txtPrintName, this.txtPrintInitialName, this.txtTVName, this.txtTVInitialName, this.dpBirthDate, this.tgCompetitorTypeA,
				this.tgComp
				etitorTypeT, this.tgGenderFEMALE, this.tgGenderMALE, this.tgGenderMIXED,
				this.txtRank, this.txtSeed});
	}

	protected void showProgressIndicator(final boolean show) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				EditAthleteController.this.pnMain.setVisible( ! show);
				EditAthleteController.this.piPanel.setVisible(show);
			}
		});
	}

	private void fillAthleteEntryInfo(AthleteEntry athleteEntry) {
		if(athleteEntry != null) {
			this.exists.setValue(Boolean.valueOf(false));
			this.exists.setValue(Boolean.valueOf((athleteEntry.getId() != null)));
			this.txtWtId.setDisable(this.exists.getValue().booleanValue());
			this.currentAthleteId = athleteEntry.getId();
			this.currentAthleteEntry.id.set(athleteEntry.getId());
			if(athleteEntry.getFlag() != null) {
				if(athleteEntry.getFlag() != null) {
					this.flag = athleteEntry.getFlag();
				} else {
					this.flag = null;
				}
				_updateFlag(this.flag, this.txtFlagAbbr, this.ivFlag);
			}
			BeanUtils.copyProperties(athleteEntry, this.currentAthleteEntry, new String[] {"id", "flag", "rank", "seed"});
			if(athleteEntry.rankProperty().getValue() != null) {
				this.currentAthleteEntry.setRank(athleteEntry.getRank());
			} else {
				this.currentAthleteEntry.setRank(0);
			}
			if(athleteEntry.seed.getValue() != null) {
				this.currentAthleteEntry.setSeed(athleteEntry.getSeed());
			} else {
				this.currentAthleteEntry.setSeed(0);
			}
		}
	}

	@Override
	public void onWindowShowEvent() {
		super.onWindowShowEvent();
		showProgressIndicator(true);
		this.saved = false;
		TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				AthleteEntry athleteEntry = new AthleteEntry();
				if(EditAthleteController.this.currentAthleteId == null) {
					EditAthleteController.logger.info("Create a new Athlete");
				} else {
					EditAthleteController.logger.info("Go to edit existent Athlete");
					try {
						athleteEntry = EditAthleteController.this.athleteService.getEntryById(EditAthleteController.this.currentAthleteId);
					} catch(TkStrikeServiceException e) {
						e.printStackTrace();
					}
				}
				if(athleteEntry != null)
					EditAthleteController.this.fillAthleteEntryInfo(athleteEntry);
				EditAthleteController.this.showProgressIndicator(false);
				return null;
			}
		});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.txtWtId.textProperty().bindBidirectional(this.currentAthleteEntry.wfId);
		this.txtOvrInternalId.textProperty().bindBidirectional(this.currentAthleteEntry.ovrInternalId);
		this.txtOvrInternalId.setDisable(true);
		this.txtScoreboardName.textProperty().bindBidirectional(this.currentAthleteEntry.scoreboardName);
		this.txtGivenName.textProperty().bindBidirectional(this.currentAthleteEntry.givenName);
		this.txtFamilyName.textProperty().bindBidirectional(this.currentAthleteEntry.familyName);
		this.txtPassportGivenName.textProperty().bindBidirectional(this.currentAthleteEntry.passportGivenName);
		this.txtPassportFamilyName.textProperty().bindBidirectional(this.currentAthleteEntry.passportFamilyName);
		this.txtPreferredGivenName.textProperty().bindBidirectional(this.currentAthleteEntry.preferredGivenName);
		this.txtPreferredFamilyName.textProperty().bindBidirectional(this.currentAthleteEntry.preferredFamilyName);
		this.txtPrintName.textProperty().bindBidirectional(this.currentAthleteEntry.printName);
		this.txtPrintInitialName.textProperty().bindBidirectional(this.currentAthleteEntry.printInitialName);
		this.txtTVName.textProperty().bindBidirectional(this.currentAthleteEntry.tvName);
		this.txtTVInitialName.textProperty().bindBidirectional(this.currentAthleteEntry.tvInitialName);
		this.dpBirthDate.valueProperty().addListener(new ChangeListener<LocalDate>() {

			@Override
			public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
				EditAthleteController.this.currentAthleteEntry.setBirthDate((newValue != null) ? EditAthleteController.this.convertToDateViaInstant(
						newValue) : null);
			}
		});
		this.currentAthleteEntry.birthDateProperty().addListener(new ChangeListener<Date>() {

			@Override
			public void changed(ObservableValue<? extends Date> observable, Date oldValue, Date newValue) {
				EditAthleteController.this.dpBirthDate.setValue((newValue != null) ? EditAthleteController.this.convertToLocalDateViaMilisecond(
						newValue) : null);
			}
		});
		this.tgCompetitorTypeA.setToggleGroup(this.tgCompetitorType);
		this.tgCompetitorTypeA.setUserData(CompetitorType.A);
		this.tgCompetitorTypeT.setToggleGroup(this.tgCompetitorType);
		this.tgCompetitorTypeT.setUserData(CompetitorType.T);
		this.tgCompetitorType.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if(newValue != null) {
					EditAthleteController.this.currentAthleteEntry.setCompetitorType((CompetitorType)newValue.getUserData());
				} else {
					EditAthleteController.this.currentAthleteEntry.setCompetitorType(null);
				}
			}
		});
		this.currentAthleteEntry.competitorType.addListener(new ChangeListener<CompetitorType>() {

			@Override
			public void changed(ObservableValue<? extends CompetitorType> observable, CompetitorType oldValue, CompetitorType newValue) {
				if(newValue != null) {
					if(CompetitorType.A.equals(newValue)) {
						EditAthleteController.this.tgCompetitorType.selectToggle(EditAthleteController.this.tgCompetitorTypeA);
					} else {
						EditAthleteController.this.tgCompetitorType.selectToggle(EditAthleteController.this.tgCompetitorTypeT);
					}
				} else {
					EditAthleteController.this.tgCompetitorType.selectToggle(null);
				}
			}
		});
		this.tgGenderFEMALE.setToggleGroup(this.tgGender);
		this.tgGenderFEMALE.setUserData(Gender.FEMALE);
		this.tgGenderMALE.setToggleGroup(this.tgGender);
		this.tgGenderMALE.setUserData(Gender.MALE);
		this.tgGenderMIXED.setToggleGroup(this.tgGender);
		this.tgGenderMIXED.setUserData(Gender.MIXED);
		this.tgGender.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if(newValue != null) {
					EditAthleteController.this.currentAthleteEntry.setGender((Gender)newValue.getUserData());
				} else {
					EditAthleteController.this.currentAthleteEntry.setGender(null);
				}
			}
		});
		this.currentAthleteEntry.gender.addListener(new ChangeListener<Gender>() {

			@Override
			public void changed(ObservableValue<? extends Gender> observable, Gender oldValue, Gender newValue) {
				if(newValue != null) {
					if(Gender.MALE.equals(newValue)) {
						EditAthleteController.this.tgGender.selectToggle(EditAthleteController.this.tgGenderMALE);
					} else if(Gender.FEMALE.equals(newValue)) {
						EditAthleteController.this.tgGender.selectToggle(EditAthleteController.this.tgGenderFEMALE);
					} else {
						EditAthleteController.this.tgGender.selectToggle(EditAthleteController.this.tgGenderMIXED);
					}
				} else {
					EditAthleteController.this.tgGender.selectToggle(null);
				}
			}
		});
		this.txtRank.setRestrict("^1*([1-9][0-9]{0,2}|1000)$");
		this.txtRank.setDefaultValue("0");
		this.txtRank.textProperty().bindBidirectional((Property)this.currentAthleteEntry.rank, (StringConverter)new NumberStringConverter());
		this.txtSeed.setRestrict("^1*([1-9][0-9]{0,2}|1000)$");
		this.txtSeed.setDefaultValue("0");
		this.txtSeed.textProperty().bindBidirectional((Property)this.currentAthleteEntry.seed, (StringConverter)new NumberStringConverter());
		this.exists.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, final Boolean t1) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						Color color = Color.RED;
						if(t1.booleanValue())
							color = Color.GREEN;
						EditAthleteController.this.ciExists.setFill(color);
					}
				});
			}
		});
		this.txtWtId.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
				if( ! t1.booleanValue() && StringUtils.isNotBlank(EditAthleteController.this.txtWtId.getText()))
					TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

						@Override
						public Void call() throws Exception {
							EditAthleteController.this.showProgressIndicator(true);
							try {
								AthleteEntry athleteEntry = EditAthleteController.this.athleteService.getEntryBytWfId(
										EditAthleteController.this.txtWtId.getText());
								if(athleteEntry != null && athleteEntry.getId() != null) {
									EditAthleteController.this.exists.setValue(Boolean.valueOf(true));
									EditAthleteController.this.txtWtId.setDisable(true);
									EditAthleteController.this.fillAthleteEntryInfo(athleteEntry);
								} else {
									EditAthleteController.this.exists.setValue(Boolean.valueOf(false));
									EditAthleteController.this.txtWtId.setDisable(false);
								}
							} catch(TkStrikeServiceException e) {
								EditAthleteController.this.manageException(e, null, e.getMessage());
								e.printStackTrace();
							} finally {
								EditAthleteController.this.showProgressIndicator(false);
							}
							return null;
						}
					});
			}
		});
		this.txtFlagAbbr.setMaxLength(4);
		this.txtFlagAbbr.setDefaultValue(null);
		this.txtFlagAbbr.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
				if( ! t1.booleanValue() && EditAthleteController.this.txtFlagAbbr.getText() != null)
					try {
						FlagEntry flagEntry = EditAthleteController.this.flagService.getEntryByAbbreviation(EditAthleteController.this.txtFlagAbbr
								.getText().toUpperCase());
						if(flagEntry != null) {
							EditAthleteController.this.flag = flagEntry;
						} else {
							EditAthleteController.this.flag = null;
						}
						EditAthleteController.this._updateFlag(EditAthleteController.this.flag, EditAthleteController.this.txtFlagAbbr,
								EditAthleteController.this.ivFlag);
					} catch(TkStrikeServiceException e) {
						e.printStackTrace();
					}
			}
		});
	}
}
