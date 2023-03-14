package com.xtremis.daedo.tkstrike.ui.controller.ringmanager;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.xtremis.daedo.tkstrike.orm.model.Athlete;
import com.xtremis.daedo.tkstrike.service.AthleteService;
import com.xtremis.daedo.tkstrike.service.FlagService;
import com.xtremis.daedo.tkstrike.service.TkStrikeService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeBaseTableWithDeleteManagementController;
import com.xtremis.daedo.tkstrike.ui.model.AthleteEntry;
import com.xtremis.daedo.tkstrike.ui.scene.FlagEntry;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;


public class AthletesManagementController extends TkStrikeBaseTableWithDeleteManagementController<Athlete, AthleteEntry, AthleteService> {

	@FXML
	private TableView<AthleteEntry> tbAthletes;

	@FXML
	private TableColumn<AthleteEntry, String> tbColId;

	@FXML
	private TableColumn<AthleteEntry, String> tbColScoreboardName;

	@FXML
	private TableColumn<AthleteEntry, String> tbColWtfId;

	@FXML
	private TableColumn<String, FlagEntry> tbColFlagAbbr;

	@FXML
	private TableColumn<Image, FlagEntry> tbColFlag;

	@FXML
	private TableColumn<AthleteEntry, Boolean> tbColEdit;

	@FXML
	private TableColumn<AthleteEntry, Boolean> tbColDelete;

	@FXML
	private ProgressIndicator pi;

	@FXML
	private Button btNew;

	private ObservableList<AthleteEntry> athleteEntries = FXCollections.observableArrayList();

	@Autowired
	private AthleteService athleteService;

	@Autowired
	private FlagService flagService;

	@Autowired
	private EditAthleteController editAthleteController;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		this.pi.setVisible(false);
		this.tbAthletes.setItems(this.athleteEntries);
		this.tbAthletes.setEditable(true);
		this.tbColId.setCellValueFactory(new PropertyValueFactory("id"));
		this.tbColScoreboardName.setCellValueFactory(new PropertyValueFactory("scoreboardName"));
		this.tbColScoreboardName.setCellFactory(TextFieldTableCell.forTableColumn());
		this.tbColScoreboardName.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<AthleteEntry, String>>() {

			@Override
			public void handle(TableColumn.CellEditEvent<AthleteEntry, String> t) {
				AthleteEntry athleteEntry = t.getTableView().getItems().get(t.getTablePosition().getRow());
				try {
					AthletesManagementController.this.athleteService.update(athleteEntry.getId(), t.getNewValue(), athleteEntry.getWfId(), null,
							(athleteEntry.getFlag() != null) ? athleteEntry.getFlag().getId() : null);
				} catch(TkStrikeServiceException e) {
					AthletesManagementController.this.manageException(e, "Flags - update by name", null);
				}
				AthletesManagementController.this.refreshTable();
			}
		});
		this.tbColWtfId.setCellValueFactory(new PropertyValueFactory("wfId"));
		this.tbColWtfId.setCellFactory(TextFieldTableCell.forTableColumn());
		this.tbColWtfId.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<AthleteEntry, String>>() {

			@Override
			public void handle(TableColumn.CellEditEvent<AthleteEntry, String> t) {
				AthleteEntry athleteEntry = t.getTableView().getItems().get(t.getTablePosition().getRow());
				try {
					AthletesManagementController.this.athleteService.update(athleteEntry.getId(), athleteEntry.getScoreboardName(), t.getNewValue(),
							null, (athleteEntry.getFlag() != null) ? athleteEntry.getFlag().getId() : null);
				} catch(TkStrikeServiceException e) {
					AthletesManagementController.this.manageException(e, "Flags - update by abbr", null);
				}
				AthletesManagementController.this.refreshTable();
			}
		});
		this.tbColFlagAbbr.setCellValueFactory(new PropertyValueFactory("flag"));
		this.tbColFlagAbbr.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<FlagEntry>() {

			@Override
			public String toString(FlagEntry object) {
				if(object != null && object.getAbbreviation() != null)
					return object.getAbbreviation();
				return "";
			}

			@Override
			public FlagEntry fromString(String string) {
				if(StringUtils.isNotBlank(string))
					try {
						return AthletesManagementController.this.flagService.getEntryByAbbreviation(string);
					} catch(TkStrikeServiceException e) {
						AthletesManagementController.this.manageException(e, "Athletes - find by Flag Abbr", null);
						return null;
					}
				return null;
			}
		}));
		this.tbColFlagAbbr.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<String, FlagEntry>>() {

			@Override
			public void handle(TableColumn.CellEditEvent<String, FlagEntry> event) {
				FlagEntry newFlag = event.getNewValue();
				if(newFlag != null) {
					AthleteEntry athleteEntry = AthletesManagementController.this.tbAthletes.getItems().get(event.getTablePosition().getRow());
					try {
						AthletesManagementController.this.athleteService.update(athleteEntry.getId(), athleteEntry.getScoreboardName(), athleteEntry
								.getWfId(), null, newFlag.getId());
					} catch(TkStrikeServiceException e) {
						AthletesManagementController.this.manageException(e, "Flags - update by abbr", null);
					}
					AthletesManagementController.this.refreshTable();
				}
			}
		});
		this.tbColFlag.setCellValueFactory(new PropertyValueFactory("flag"));
		this.tbColFlag.setCellFactory(new Callback<TableColumn<Image, FlagEntry>, TableCell<Image, FlagEntry>>() {

			@Override
			public TableCell<Image, FlagEntry> call(TableColumn<Image, FlagEntry> flagEntryImageTableColumn) {
				return new TableCell<Image, FlagEntry>() {

					ImageView imageView;
					{
						(this.imageView = new ImageView()).setPreserveRatio(true);
						this.imageView.setFitHeight(22.0);
						this.setGraphic(this.imageView);
					}

					@Override
					protected void updateItem(FlagEntry item, boolean empty) {
						if(item != null && item.getImage() != null)
							this.imageView.setImage(item.getImage());
					}
				};
			}
		});
		this.tbColDelete.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AthleteEntry, Boolean>, ObservableValue<Boolean>>() {

			@Override
			public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<AthleteEntry, Boolean> features) {
				return new SimpleBooleanProperty((features.getValue() != null));
			}
		});
		this.tbColDelete.setCellFactory(new Callback<TableColumn<AthleteEntry, Boolean>, TableCell<AthleteEntry, Boolean>>() {

			@Override
			public TableCell<AthleteEntry, Boolean> call(TableColumn<AthleteEntry, Boolean> personBooleanTableColumn) {
				return new TkStrikeBaseTableWithDeleteManagementController.DeleteEntryCell(tbAthletes);
				// return new DeleteEntryCell(AthletesManagementController.this.tbAthletes);

			}
		});
		this.tbColEdit.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AthleteEntry, Boolean>, ObservableValue<Boolean>>() {

			@Override
			public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<AthleteEntry, Boolean> features) {
				return new SimpleBooleanProperty((features.getValue() != null));
			}
		});
		this.tbColEdit.setCellFactory(new Callback<TableColumn<AthleteEntry, Boolean>, TableCell<AthleteEntry, Boolean>>() {

			@Override
			public TableCell<AthleteEntry, Boolean> call(TableColumn<AthleteEntry, Boolean> personBooleanTableColumn) {
				return new AthletesManagementController.EditEntryCell(AthletesManagementController.this.tbAthletes);
			}
		});
	}

	@Override
	public void afterPropertiesSet() throws Exception {}

	@Override
	public void onWindowShowEvent() {
		super.onWindowShowEvent();
		refreshTable();
	}

	@Override
	protected TkStrikeService<Athlete, AthleteEntry> getTkStrikeService() {
		return this.athleteService;
	}

	@Override
	protected ObservableList<AthleteEntry> getObservableList4Table() {
		return this.athleteEntries;
	}

	@Override
	protected TableView<AthleteEntry> getTableView() {
		return this.tbAthletes;
	}

	@Override
	protected ProgressIndicator getProgressIndicator() {
		return this.pi;
	}

	public void newAthlete() {
		this.editAthleteController.setCurrentAthleteId(null);
		openInNewStage(this.editAthleteController, new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				if(AthletesManagementController.this.editAthleteController.isSaved())
					AthletesManagementController.this.refreshTable();
			}
		}, getMessage("title.window.editAthlete"), 950, 600, true);
	}

	public void doClose() {
		doCloseThisStage();
	}

	class EditEntryCell extends TableCell<AthleteEntry, Boolean> {

		final Button cellButton = new Button();

		public EditEntryCell(final TableView<AthleteEntry> tableView) {
			this.cellButton.getStyleClass().addAll(new String[] {"button-image-edit"});
			this.cellButton.setTooltip(new Tooltip(AthletesManagementController.this.getMessage("label.editAthlete")));
			this.cellButton.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent t) {
					int selectedIndex = AthletesManagementController.EditEntryCell.this.getTableRow().getIndex();
					AthleteEntry entry = tableView.getItems().get(selectedIndex);
					if(entry != null && StringUtils.isNotBlank(entry.getId())) {
						AthletesManagementController.this.editAthleteController.setCurrentAthleteId(entry.getId());
						AthletesManagementController.this.openInNewStage(AthletesManagementController.this.editAthleteController,
								new EventHandler<WindowEvent>() {

									@Override
									public void handle(WindowEvent event) {
										if(AthletesManagementController.this.editAthleteController.isSaved())
											AthletesManagementController.this.refreshTable();
									}
								}, AthletesManagementController.this.getMessage("title.window.editAthlete"), 950, 600, true);
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
}
