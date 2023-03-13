package com.xtremis.daedo.tkstrike.ui.controller.configuration;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xtremis.daedo.tkstrike.om.MatchLogDto;
import com.xtremis.daedo.tkstrike.orm.model.Gender;
import com.xtremis.daedo.tkstrike.service.AthleteService;
import com.xtremis.daedo.tkstrike.service.CategoryService;
import com.xtremis.daedo.tkstrike.service.ExternalConfigService;
import com.xtremis.daedo.tkstrike.service.MatchLogHistoricalService;
import com.xtremis.daedo.tkstrike.service.PhaseService;
import com.xtremis.daedo.tkstrike.service.SubCategoryService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.controller.MatchLogViewerController;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeBaseController;
import com.xtremis.daedo.tkstrike.ui.model.AthleteEntry;
import com.xtremis.daedo.tkstrike.ui.model.CategoryEntry;
import com.xtremis.daedo.tkstrike.ui.model.ExternalConfigEntry;
import com.xtremis.daedo.tkstrike.ui.model.FormValidationError;
import com.xtremis.daedo.tkstrike.ui.model.PhaseEntry;
import com.xtremis.daedo.tkstrike.ui.model.SubCategoryEntry;
import com.xtremis.daedo.tkstrike.utils.TimestampToStringConverter;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import javafx.util.StringConverter;


@Component
public class ConfigurationMatchLogController extends TkStrikeBaseController {

	@FXML
	private Node rootView;

	@FXML
	private Pane pnContainer;

	@FXML
	private ProgressIndicator pi;

	@FXML
	private TextField txtMatchLogOutputDirectory;

	private final DirectoryChooser directoryChooser = new DirectoryChooser();

	@FXML
	private Pane pnMatchLogMain;

	@FXML
	private Pane pnFilterAthletes;

	@FXML
	private Label lblSelectAthletesInfo;

	@FXML
	private ComboBox<PhaseEntry> cmbPhase;

	private SimpleObjectProperty<PhaseEntry> selectedPhaseEntry = new SimpleObjectProperty(this, "selectedPhaseEntry");

	private ObservableList<PhaseEntry> phaseEntries = FXCollections.observableArrayList();

	@FXML
	private ComboBox<SubCategoryEntry> cmbSubCategory;

	private SimpleObjectProperty<SubCategoryEntry> selectedSubCategoryEntry = new SimpleObjectProperty(this, "selectedSubCategoryEntry");

	private ObservableList<SubCategoryEntry> subCategoryEntries = FXCollections.observableArrayList();

	@FXML
	private ComboBox<Gender> cmbGender;

	private SimpleObjectProperty<Gender> selectedGender = new SimpleObjectProperty(this, "selectedGender");

	@FXML
	private ComboBox<CategoryEntry> cmbCategory;

	private ObservableList<CategoryEntry> categoryEntries = FXCollections.observableArrayList();

	private SimpleObjectProperty<CategoryEntry> selectedCategoryEntry = new SimpleObjectProperty(this, "selectedCategoryEntry");

	@FXML
	private DatePicker dpStartDate;

	@FXML
	private DatePicker dpEndDate;

	@FXML
	private TableView<MatchLogDto> tbMatchLogHisto;

	private ObservableList<MatchLogDto> matchLogEntries = FXCollections.observableArrayList();

	@FXML
	private TableColumn<MatchLogDto, String> colId;

	@FXML
	private TableColumn<MatchLogDto, String> colMatchNumber;

	@FXML
	private TableColumn<String, Long> colMatchStartTime;

	@FXML
	private TableColumn<String, Long> colMatchEndTime;

	@FXML
	private TableColumn<MatchLogDto, String> colBlueName;

	@FXML
	private TableColumn<MatchLogDto, String> colRedName;

	@FXML
	private TableColumn<MatchLogDto, String> colScore;

	@FXML
	private TableColumn<MatchLogDto, Boolean> colPdf;

	@FXML
	private TableColumn<MatchLogDto, Boolean> colCsv;

	@FXML
	private TableColumn<MatchLogDto, Boolean> colXls;

	@FXML
	private TableColumn<MatchLogDto, Boolean> colDelete;

	@FXML
	private TableColumn<MatchLogDto, Boolean> colView;

	@FXML
	private TableView<AthleteEntry> tbAthletes;

	private ObservableList<AthleteEntry> athleteEntries = FXCollections.observableArrayList();

	private ObservableMap<String, SimpleBooleanProperty> selectedAthletes = FXCollections.observableHashMap();

	@FXML
	private TableColumn<AthleteEntry, Boolean> colAthleteSelect;

	@FXML
	private TableColumn<AthleteEntry, String> colAthleteId;

	@FXML
	private TableColumn<AthleteEntry, String> colAthleteName;

	@FXML
	private TableColumn<AthleteEntry, String> colAthleteWtfId;

	@FXML
	private TableColumn<AthleteEntry, String> colAthleteAbbreviation;

	@FXML
	private TableColumn<AthleteEntry, Image> colAthleteAbbreviationImage;

	@Autowired
	private ExternalConfigService externalConfigService;

	@Autowired
	private PhaseService phaseService;

	@Autowired
	private SubCategoryService subCategoryService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private MatchLogHistoricalService matchLogHistoricalService;

	@Autowired
	private MatchLogViewerController matchLogViewerController;

	@Autowired
	private AthleteService athleteService;

	private ExternalConfigEntry currentExternalConfigEntry = new ExternalConfigEntry();

	@Override
	public void onWindowShowEvent() {
		super.onWindowShowEvent();
		showProgressIndicator(true);
		showMatchLogMain(true);
		refreshAndRebind();
		_refreshPhasesCombo();
		_refreshSubCategoriesCombo();
		_refreshAthletes();
	}

	public void doChangeDirectory() {
		File fTemp = new File(this.currentExternalConfigEntry.getMatchLogOutputDirectory());
		if(fTemp.exists()) {
			this.directoryChooser.setInitialDirectory(new File(this.currentExternalConfigEntry.getMatchLogOutputDirectory()));
		} else {
			this.directoryChooser.setInitialDirectory(new File("/"));
		}
		File newDirectory = this.directoryChooser.showDialog(getCurrentStage());
		if(newDirectory != null) {
			this.currentExternalConfigEntry.matchLogOutputDirectoryProperty().set(newDirectory.getAbsolutePath());
			try {
				this.externalConfigService.update(this.currentExternalConfigEntry.createExternalConfig());
			} catch(TkStrikeServiceException e) {
				e.printStackTrace();
			}
			refreshAndRebind();
		}
	}

	public void exportAllToDirectory() {
		showProgressIndicator(true);
		TkStrikeExecutors.executeInThreadPool(new Runnable() {

			@Override
			public void run() {
				try {
					ConfigurationMatchLogController.this.matchLogHistoricalService.exportAllMatchLog2DefaultDirectory();
					ConfigurationMatchLogController.this.showInfoDialog(ConfigurationMatchLogController.this.getMessage("title.default.info"),
							ConfigurationMatchLogController.this
									.getMessage("message.matchLog.csvGeneratedOnDirectory", new String[] {ConfigurationMatchLogController.access$200(
											this.this$0).getExternalConfigEntry().getMatchLogOutputDirectory()}));
				} catch(TkStrikeServiceException e) {
					ConfigurationMatchLogController.this.manageException(e, "exportAllToDirectory", null);
				} finally {
					ConfigurationMatchLogController.this.showProgressIndicator(false);
				}
			}
		});
	}

	public void doExecuteFilter() {
		showProgressIndicator(true);
		String phaseId = (this.selectedPhaseEntry.get() != null) ? this.selectedPhaseEntry.get().getId() : null;
		String subCategoryId = (this.selectedSubCategoryEntry.get() != null) ? this.selectedSubCategoryEntry.get().getId() : null;
		Gender gender = (this.selectedGender.get() != null) ? (Gender)this.selectedGender.get() : null;
		String categoryId = (this.selectedCategoryEntry.get() != null) ? this.selectedCategoryEntry.get().getId() : null;
		Date startDate = (this.dpStartDate.getValue() != null) ? Date.from(this.dpStartDate.getValue().atStartOfDay(ZoneId.systemDefault())
				.toInstant()) : null;
		Date endDate = (this.dpEndDate.getValue() != null) ? Date.from(this.dpEndDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())
				: null;
		List<String> athleteIds = new ArrayList<>();
		if(this.selectedAthletes != null)
			for(Map.Entry<String, SimpleBooleanProperty> entry : (Iterable<Map.Entry<String, SimpleBooleanProperty>>)this.selectedAthletes
					.entrySet()) {
				if(entry.getValue().get())
					athleteIds.add(entry.getKey());
			}
		try {
			final List<MatchLogDto> matchLogEntriesList = this.matchLogHistoricalService.find(phaseId, subCategoryId, gender, categoryId, athleteIds,
					startDate, endDate);
			if(matchLogEntriesList != null)
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						try {
							ConfigurationMatchLogController.this.matchLogEntries.clear();
							ConfigurationMatchLogController.this.matchLogEntries.addAll(matchLogEntriesList);
						} catch(Exception e) {
							e.printStackTrace();
							ConfigurationMatchLogController.this.manageException(e, "doExecuteFilter", null);
						}
					}
				});
		} catch(TkStrikeServiceException e) {
			manageException(e, "doExecuteFilter", null);
		} finally {
			showProgressIndicator(false);
		}
	}

	public void doGenerateAggregateCSV() {
		final String phaseId = (this.selectedPhaseEntry.get() != null) ? this.selectedPhaseEntry.get().getId() : null;
		final String subCategoryId = (this.selectedSubCategoryEntry.get() != null) ? this.selectedSubCategoryEntry.get().getId() : null;
		final Gender gender = (this.selectedGender.get() != null) ? (Gender)this.selectedGender.get() : null;
		final String categoryId = (this.selectedCategoryEntry.get() != null) ? this.selectedCategoryEntry.get().getId() : null;
		final Date startDate = (this.dpStartDate.getValue() != null) ? Date.from(this.dpStartDate.getValue().atStartOfDay(ZoneId.systemDefault())
				.toInstant()) : null;
		final Date endDate = (this.dpEndDate.getValue() != null) ? Date.from(this.dpEndDate.getValue().atStartOfDay(ZoneId.systemDefault())
				.toInstant()) : null;
		showProgressIndicator(true);
		TkStrikeExecutors.executeInThreadPool(new Runnable() {

			@Override
			public void run() {
				try {
					ConfigurationMatchLogController.this.matchLogHistoricalService.generateAggregateCSVReport2DefaultDirectory(phaseId, subCategoryId,
							gender, categoryId, null, startDate, endDate);
					ConfigurationMatchLogController.this.showInfoDialog(ConfigurationMatchLogController.this.getMessage("title.default.info"),
							ConfigurationMatchLogController.this
									.getMessage("message.matchLog.csvAggregatedGeneratedOnDirectory", new String[] {ConfigurationMatchLogController
											.access$200(this.this$0).getExternalConfigEntry().getMatchLogOutputDirectory()}));
				} catch(TkStrikeServiceException e) {
					ConfigurationMatchLogController.this.manageException(e, "doGenerateAggregateCSV", null);
				} finally {
					ConfigurationMatchLogController.this.showProgressIndicator(false);
				}
			}
		});
	}

	public void showFilterAthletes() {
		showProgressIndicator(true);
		showMatchLogMain(false);
		showProgressIndicator(false);
	}

	public void confirmAthletesFilter() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				ConfigurationMatchLogController.this.lblSelectAthletesInfo.setText(ConfigurationMatchLogController.this.getMessage(
						"label.athletesFilterYes"));
				ConfigurationMatchLogController.this.lblSelectAthletesInfo.getStyleClass().clear();
				ConfigurationMatchLogController.this.lblSelectAthletesInfo.getStyleClass().addAll((Object[])new String[] {"label-green"});
			}
		});
		showMatchLogMain(true);
	}

	public void cancelAthletesFilter() {
		_cleanSelectedAthletes();
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				ConfigurationMatchLogController.this.lblSelectAthletesInfo.setText(ConfigurationMatchLogController.this.getMessage(
						"label.athletesFilterNo"));
				ConfigurationMatchLogController.this.lblSelectAthletesInfo.getStyleClass().clear();
				ConfigurationMatchLogController.this.lblSelectAthletesInfo.getStyleClass().addAll((Object[])new String[] {"label-red"});
			}
		});
		showMatchLogMain(true);
	}

	private void showMatchLogMain(final boolean show) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				ConfigurationMatchLogController.this.pnMatchLogMain.setVisible(show);
				ConfigurationMatchLogController.this.pnFilterAthletes.setVisible( ! show);
			}
		});
	}

	private void _refreshAthletes() {
		showProgressIndicator(true);
		try {
			List<AthleteEntry> athleteEntryList = this.athleteService.findAllEntries();
			this.athleteEntries.clear();
			this.selectedAthletes.clear();
			this.athleteEntries.addAll(athleteEntryList);
			for(AthleteEntry athleteEntry : athleteEntryList)
				this.selectedAthletes.put(athleteEntry.getId(), new SimpleBooleanProperty(false));
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					ConfigurationMatchLogController.this.lblSelectAthletesInfo.setText(ConfigurationMatchLogController.this.getMessage(
							"label.athletesFilterNo"));
					ConfigurationMatchLogController.this.lblSelectAthletesInfo.getStyleClass().clear();
					ConfigurationMatchLogController.this.lblSelectAthletesInfo.getStyleClass().addAll(new String[] {"label-red"});
				}
			});
			showProgressIndicator(false);
		} catch(TkStrikeServiceException e) {
			manageException(e, "_refreshAthletes", null);
		}
	}

	private void _cleanSelectedAthletes() {
		for(AthleteEntry athleteEntry : this.athleteEntries)
			this.selectedAthletes.get(athleteEntry.getId()).set(false);
	}

	private void _refreshPhasesCombo() {
		showProgressIndicator(true);
		try {
			List<PhaseEntry> phaseEntryList = this.phaseService.findAllEntries();
			this.phaseEntries.clear();
			this.phaseEntries.addAll(phaseEntryList);
			showProgressIndicator(false);
		} catch(TkStrikeServiceException e) {
			manageException(e, "_refreshPhasesCombo", null);
		}
	}

	private void _refreshSubCategoriesCombo() {
		showProgressIndicator(true);
		try {
			List<SubCategoryEntry> subCategoryEntryList = this.subCategoryService.findAllEntries();
			this.subCategoryEntries.clear();
			this.subCategoryEntries.addAll(subCategoryEntryList);
			showProgressIndicator(false);
		} catch(TkStrikeServiceException e) {
			manageException(e, "_refreshPhasesCombo", null);
		}
	}

	private void _refreshCategoriesCombo(String selectedCategoryId, Gender selectedGender) {
		if(StringUtils.isNotBlank(selectedCategoryId) && selectedGender != null) {
			showProgressIndicator(true);
			try {
				final List<CategoryEntry> categoryEntryList = this.categoryService.findEntriesBySC_G(selectedCategoryId, selectedGender);
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						ConfigurationMatchLogController.this.categoryEntries.clear();
						ConfigurationMatchLogController.this.categoryEntries.addAll(categoryEntryList);
					}
				});
			} catch(TkStrikeServiceException e) {
				manageException(e, "_refreshPhasesCombo", null);
			} finally {
				showProgressIndicator(false);
			}
		}
	}

	private void refreshAndRebind() {
		try {
			this.currentExternalConfigEntry = this.externalConfigService.getExternalConfigEntry();
		} catch(TkStrikeServiceException e) {
			manageException(e, "refreshAndRebind  - getExternalConfigEntry", null);
		}
		this.txtMatchLogOutputDirectory.textProperty().bindBidirectional(this.currentExternalConfigEntry.matchLogOutputDirectoryProperty());
		showProgressIndicator(false);
	}

	@Override
	protected Collection<Control> getFormControls() {
		return null;
	}

	@Override
	public LinkedHashSet<FormValidationError> validateForm() {
		LinkedHashSet<FormValidationError> res = null;
		return res;
	}

	protected void showProgressIndicator(final boolean show) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				ConfigurationMatchLogController.this.pnContainer.setVisible( ! show);
				ConfigurationMatchLogController.this.pi.setVisible(show);
			}
		});
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.dpStartDate.setValue(LocalDate.now());
		this.dpEndDate.setValue(LocalDate.now());
		this.cmbPhase.valueProperty().bindBidirectional(this.selectedPhaseEntry);
		this.cmbSubCategory.valueProperty().bindBidirectional(this.selectedSubCategoryEntry);
		this.cmbGender.valueProperty().bindBidirectional(this.selectedGender);
		this.cmbCategory.valueProperty().bindBidirectional(this.selectedCategoryEntry);
		this.cmbGender.getItems().clear();
		this.cmbGender.getItems().addAll(new Gender[] {Gender.MALE, Gender.FEMALE});
		this.cmbPhase.setItems(this.phaseEntries);
		this.cmbSubCategory.setItems(this.subCategoryEntries);
		this.cmbCategory.setItems(this.categoryEntries);
		this.cmbSubCategory.valueProperty().addListener(new ChangeListener<SubCategoryEntry>() {

			@Override
			public void changed(ObservableValue<? extends SubCategoryEntry> observable, SubCategoryEntry oldValue, SubCategoryEntry newValue) {
				if(newValue != null)
					ConfigurationMatchLogController.this._refreshCategoriesCombo(newValue.getId(), ConfigurationMatchLogController.this.cmbGender
							.getValue());
			}
		});
		this.cmbGender.valueProperty().addListener(new ChangeListener<Gender>() {

			@Override
			public void changed(ObservableValue<? extends Gender> observable, Gender oldValue, Gender newValue) {
				if(newValue != null)
					ConfigurationMatchLogController.this._refreshCategoriesCombo((ConfigurationMatchLogController.this.cmbSubCategory
							.getValue() != null) ? ConfigurationMatchLogController.this.cmbSubCategory.getValue().getId() : null, newValue);
			}
		});
		this.tbMatchLogHisto.setItems(this.matchLogEntries);
		this.colId.setCellValueFactory(new PropertyValueFactory("id"));
		this.colMatchNumber.setCellValueFactory(new PropertyValueFactory("matchNumber"));
		this.colMatchStartTime.setCellValueFactory(new PropertyValueFactory("matchStartTime"));
		this.colMatchStartTime.setCellFactory(TextFieldTableCell.forTableColumn((StringConverter)new TimestampToStringConverter(
				this.dfFullFormatPattern)));
		this.colMatchEndTime.setCellValueFactory(new PropertyValueFactory("matchEndTime"));
		this.colMatchEndTime.setCellFactory(TextFieldTableCell.forTableColumn((StringConverter)new TimestampToStringConverter(
				this.dfFullFormatPattern)));
		this.colBlueName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MatchLogDto, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<MatchLogDto, String> matchLogItemEntryStringCellDataFeatures) {
				SimpleStringProperty res = new SimpleStringProperty();
				MatchLogDto matchLogItemEntry = matchLogItemEntryStringCellDataFeatures.getValue();
				if(matchLogItemEntry != null && matchLogItemEntry.getBlueAthleteName() != null)
					res.set(matchLogItemEntry.getBlueAthleteName());
				return res;
			}
		});
		this.colRedName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MatchLogDto, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<MatchLogDto, String> matchLogItemEntryStringCellDataFeatures) {
				SimpleStringProperty res = new SimpleStringProperty();
				MatchLogDto matchLogItemEntry = matchLogItemEntryStringCellDataFeatures.getValue();
				if(matchLogItemEntry != null && matchLogItemEntry.getRedAthleteName() != null)
					res.set(matchLogItemEntry.getRedAthleteName());
				return res;
			}
		});
		this.colScore.setCellValueFactory(new PropertyValueFactory("matchResult"));
		this.colPdf.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MatchLogDto, Boolean>, ObservableValue<Boolean>>() {

			@Override
			public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<MatchLogDto, Boolean> features) {
				return new SimpleBooleanProperty((features.getValue() != null));
			}
		});
		this.colPdf.setCellFactory(new Callback<TableColumn<MatchLogDto, Boolean>, TableCell<MatchLogDto, Boolean>>() {

			@Override
			public TableCell<MatchLogDto, Boolean> call(TableColumn<MatchLogDto, Boolean> personBooleanTableColumn) {
				return new ConfigurationMatchLogController.ViewMatchLogPDFEntryCell(ConfigurationMatchLogController.this.tbMatchLogHisto);
			}
		});
		this.colCsv.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MatchLogDto, Boolean>, ObservableValue<Boolean>>() {

			@Override
			public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<MatchLogDto, Boolean> features) {
				return new SimpleBooleanProperty((features.getValue() != null));
			}
		});
		this.colCsv.setCellFactory(new Callback<TableColumn<MatchLogDto, Boolean>, TableCell<MatchLogDto, Boolean>>() {

			@Override
			public TableCell<MatchLogDto, Boolean> call(TableColumn<MatchLogDto, Boolean> personBooleanTableColumn) {
				return new ConfigurationMatchLogController.ViewMatchLogCSVEntryCell(ConfigurationMatchLogController.this.tbMatchLogHisto, Boolean
						.valueOf(false));
			}
		});
		this.colXls.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MatchLogDto, Boolean>, ObservableValue<Boolean>>() {

			@Override
			public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<MatchLogDto, Boolean> features) {
				return new SimpleBooleanProperty((features.getValue() != null));
			}
		});
		this.colXls.setCellFactory(new Callback<TableColumn<MatchLogDto, Boolean>, TableCell<MatchLogDto, Boolean>>() {

			@Override
			public TableCell<MatchLogDto, Boolean> call(TableColumn<MatchLogDto, Boolean> personBooleanTableColumn) {
				return new ConfigurationMatchLogController.ViewMatchLogCSVEntryCell(ConfigurationMatchLogController.this.tbMatchLogHisto, Boolean
						.valueOf(true));
			}
		});
		this.colView.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MatchLogDto, Boolean>, ObservableValue<Boolean>>() {

			@Override
			public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<MatchLogDto, Boolean> features) {
				return new SimpleBooleanProperty((features.getValue() != null));
			}
		});
		this.colView.setCellFactory(new Callback<TableColumn<MatchLogDto, Boolean>, TableCell<MatchLogDto, Boolean>>() {

			@Override
			public TableCell<MatchLogDto, Boolean> call(TableColumn<MatchLogDto, Boolean> personBooleanTableColumn) {
				return new ConfigurationMatchLogController.ViewMatchLogDetailEntryCell(ConfigurationMatchLogController.this.tbMatchLogHisto);
			}
		});
		this.colDelete.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MatchLogDto, Boolean>, ObservableValue<Boolean>>() {

			@Override
			public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<MatchLogDto, Boolean> features) {
				return new SimpleBooleanProperty((features.getValue() != null));
			}
		});
		this.colDelete.setCellFactory(new Callback<TableColumn<MatchLogDto, Boolean>, TableCell<MatchLogDto, Boolean>>() {

			@Override
			public TableCell<MatchLogDto, Boolean> call(TableColumn<MatchLogDto, Boolean> personBooleanTableColumn) {
				return new ConfigurationMatchLogController.DeleteMatchLogEntryCell(ConfigurationMatchLogController.this.tbMatchLogHisto);
			}
		});
		this.tbAthletes.setItems(this.athleteEntries);
		this.tbAthletes.setEditable(true);
		this.colAthleteId.setCellValueFactory(new PropertyValueFactory("id"));
		this.colAthleteName.setCellValueFactory(new PropertyValueFactory("name"));
		this.colAthleteWtfId.setCellValueFactory(new PropertyValueFactory("wfId"));
		this.colAthleteAbbreviation.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AthleteEntry, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<AthleteEntry, String> matchLogItemEntryStringCellDataFeatures) {
				SimpleStringProperty res = new SimpleStringProperty();
				AthleteEntry athleteEntry = matchLogItemEntryStringCellDataFeatures.getValue();
				if(athleteEntry != null)
					res.set(athleteEntry.getFlag().getAbbreviation());
				return res;
			}
		});
		this.colAthleteAbbreviationImage.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<AthleteEntry, Image>, ObservableValue<Image>>() {

					@Override
					public ObservableValue<Image> call(TableColumn.CellDataFeatures<AthleteEntry, Image> matchLogItemEntryStringCellDataFeatures) {
						SimpleObjectProperty<Image> res = new SimpleObjectProperty();
						AthleteEntry athleteEntry = matchLogItemEntryStringCellDataFeatures.getValue();
						if(athleteEntry != null)
							res.set(athleteEntry.getFlag().getImage());
						return res;
					}
				});
		this.colAthleteAbbreviationImage.setCellFactory(new Callback<TableColumn<AthleteEntry, Image>, TableCell<AthleteEntry, Image>>() {

			@Override
			public TableCell<AthleteEntry, Image> call(TableColumn<AthleteEntry, Image> flagEntryImageTableColumn) {
				return new TableCell<AthleteEntry, Image>() {

					ImageView imageView;

					@Override
					protected void updateItem(Image image, boolean b) {
						if(image != null)
							this.imageView.setImage(image);
					}
				};
			}
		});
		this.colAthleteSelect.setEditable(true);
		this.colAthleteSelect.setCellFactory(CheckBoxTableCell.forTableColumn(this.colAthleteSelect));
		this.colAthleteSelect.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AthleteEntry, Boolean>, ObservableValue<Boolean>>() {

			@Override
			public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<AthleteEntry, Boolean> param) {
				if(param.getValue() != null && StringUtils.isNotBlank(param.getValue().getId()))
					return ConfigurationMatchLogController.this.selectedAthletes.get(param.getValue().getId());
				return new SimpleBooleanProperty(false);
			}
		});
	}

	@Override
	public Node getRootView() {
		return this.rootView;
	}

	protected class ViewMatchLogPDFEntryCell extends TableCell<MatchLogDto, Boolean> {

		final Button cellButton = new Button();

		public ViewMatchLogPDFEntryCell(final TableView<MatchLogDto> tableView) {
			this.cellButton.setTooltip(new Tooltip(ConfigurationMatchLogController.this.getMessage("message.matchLog.viewPDF")));
			this.cellButton.getStyleClass().addAll(new String[] {"button-image-pdf"});
			this.cellButton.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent t) {
					int selectedIndex = ConfigurationMatchLogController.ViewMatchLogPDFEntryCell.this.getTableRow().getIndex();
					final MatchLogDto entry = tableView.getItems().get(selectedIndex);
					if(entry != null) {
						ConfigurationMatchLogController.this.showProgressIndicator(true);
						TkStrikeExecutors.executeInThreadPool(new Runnable() {

							@Override
							public void run() {
								try {
									ConfigurationMatchLogController.this.matchLogHistoricalService.exportMatchLogPDF2DefaultDirectory(entry.getId());
									ConfigurationMatchLogController.this.showInfoDialog(ConfigurationMatchLogController.this.getMessage(
											"title.default.info"), ConfigurationMatchLogController.this
													.getMessage("message.matchLog.pdfGeneratedOnDirectory", new String[] {
															ConfigurationMatchLogController.access$200(this.this$2.this$1.this$0)
																	.getExternalConfigEntry().getMatchLogOutputDirectory()}));
								} catch(TkStrikeServiceException e) {
									ConfigurationMatchLogController.this.manageException(e, "viewMatchLogASPDF", null);
								} finally {
									ConfigurationMatchLogController.this.showProgressIndicator(false);
								}
							}
						});
					}
				}
			});
		}

		@Override
		protected void updateItem(Boolean t, boolean empty) {
			super.updateItem(t, empty);
			if( ! empty) {
				setGraphic(this.cellButton);
			} else {
				setGraphic(null);
			}
		}
	}

	protected class ViewMatchLogCSVEntryCell extends TableCell<MatchLogDto, Boolean> {

		final Button cellButton = new Button();

		final Boolean isForXls;

		public ViewMatchLogCSVEntryCell(final TableView<MatchLogDto> tableView, final Boolean isForXls) {
			this.isForXls = isForXls;
			this.cellButton.setTooltip(new Tooltip(ConfigurationMatchLogController.this.getMessage(isForXls.booleanValue()
					? "message.matchLog.viewXLS"
					: "message.matchLog.viewCSV")));
			this.cellButton.getStyleClass().addAll(new String[] {isForXls.booleanValue() ? "button-image-xls" : "button-image-csv"});
			this.cellButton.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent t) {
					int selectedIndex = ConfigurationMatchLogController.ViewMatchLogCSVEntryCell.this.getTableRow().getIndex();
					final MatchLogDto entry = tableView.getItems().get(selectedIndex);
					if(entry != null) {
						ConfigurationMatchLogController.this.showProgressIndicator(true);
						TkStrikeExecutors.executeInThreadPool(new Runnable() {

							@Override
							public void run() {
								try {
									if(isForXls.booleanValue()) {
										ConfigurationMatchLogController.this.matchLogHistoricalService.exportMatchLogXLS2DefaultDirectory(entry
												.getId());
									} else {
										ConfigurationMatchLogController.this.matchLogHistoricalService.exportMatchLog2DefaultDirectory(entry.getId());
									}
									ConfigurationMatchLogController.this.showInfoDialog(ConfigurationMatchLogController.this.getMessage(
											"title.default.info"), ConfigurationMatchLogController.this
													.getMessage("message.matchLog.csvGeneratedOnDirectory", new String[] {
															ConfigurationMatchLogController.access$200(this.this$2.this$1.this$0)
																	.getExternalConfigEntry().getMatchLogOutputDirectory()}));
								} catch(TkStrikeServiceException e) {
									ConfigurationMatchLogController.this.manageException(e, "viewMatchLogASCSV", null);
								} finally {
									ConfigurationMatchLogController.this.showProgressIndicator(false);
								}
							}
						});
					}
				}
			});
		}

		@Override
		protected void updateItem(Boolean t, boolean empty) {
			super.updateItem(t, empty);
			if( ! empty) {
				setGraphic(this.cellButton);
			} else {
				setGraphic(null);
			}
		}
	}

	protected class ViewMatchLogDetailEntryCell extends TableCell<MatchLogDto, Boolean> {

		final Button cellButton = new Button();

		public ViewMatchLogDetailEntryCell(final TableView<MatchLogDto> tableView) {
			this.cellButton.setTooltip(new Tooltip(ConfigurationMatchLogController.this.getMessage("message.matchLog.viewDetail")));
			this.cellButton.getStyleClass().addAll(new String[] {"button-image-view"});
			this.cellButton.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent t) {
					int selectedIndex = ConfigurationMatchLogController.ViewMatchLogDetailEntryCell.this.getTableRow().getIndex();
					MatchLogDto entry = tableView.getItems().get(selectedIndex);
					if(entry != null) {
						ConfigurationMatchLogController.this.matchLogViewerController.setIsHistorical(Boolean.TRUE);
						ConfigurationMatchLogController.this.matchLogViewerController.setMatchLogId(entry.getId());
						ConfigurationMatchLogController.this.openInNewStage(ConfigurationMatchLogController.this.matchLogViewerController,
								ConfigurationMatchLogController.this.getMessage("title.window.matchLog"), 1200, 600);
					}
				}
			});
		}

		@Override
		protected void updateItem(Boolean t, boolean empty) {
			super.updateItem(t, empty);
			if( ! empty) {
				setGraphic(this.cellButton);
			} else {
				setGraphic(null);
			}
		}
	}

	protected class DeleteMatchLogEntryCell extends TableCell<MatchLogDto, Boolean> {

		final Button cellButton = new Button();

		public DeleteMatchLogEntryCell(final TableView<MatchLogDto> tableView) {
			this.cellButton.setTooltip(new Tooltip(ConfigurationMatchLogController.this.getMessage("message.matchLog.delete")));
			this.cellButton.getStyleClass().addAll(new String[] {"button-image-delete", "button-image-delete-little"});
			this.cellButton.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent t) {
					int selectedIndex = ConfigurationMatchLogController.DeleteMatchLogEntryCell.this.getTableRow().getIndex();
					final MatchLogDto entry = tableView.getItems().get(selectedIndex);
					if(entry != null && StringUtils.isNotBlank(entry.getId()))
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								if(ConfigurationMatchLogController.this.showConfirmDialog(ConfigurationMatchLogController.this.getMessage(
										"message.confirmDialog.title"), ConfigurationMatchLogController.this
												.getMessage("message.confirmDialog.delete")).get().equals(ButtonType.OK)) {
									ConfigurationMatchLogController.this.matchLogHistoricalService.deleteMatchLog(entry.getId());
									ConfigurationMatchLogController.this.doExecuteFilter();
								}
							}
						});
				}
			});
		}

		@Override
		protected void updateItem(Boolean t, boolean empty) {
			super.updateItem(t, empty);
			if( ! empty) {
				setGraphic(this.cellButton);
			} else {
				setGraphic(null);
			}
		}
	}
}
