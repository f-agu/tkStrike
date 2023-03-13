package com.xtremis.daedo.tkstrike.ui.controller.ringmanager;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.xtremis.daedo.tkstrike.orm.model.Phase;
import com.xtremis.daedo.tkstrike.service.PhaseService;
import com.xtremis.daedo.tkstrike.service.TkStrikeService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeBaseTableWithDeleteManagementController;
import com.xtremis.daedo.tkstrike.ui.model.PhaseEntry;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;


public class PhasesManagementController extends TkStrikeBaseTableWithDeleteManagementController<Phase, PhaseEntry, PhaseService> {

	@FXML
	private Node rootView;

	@FXML
	private TableView<PhaseEntry> tbPhase;

	@FXML
	private TableColumn<PhaseEntry, String> tbPhaseColId;

	@FXML
	private TableColumn<PhaseEntry, String> tbPhaseColAbbreviation;

	@FXML
	private TableColumn<PhaseEntry, String> tbPhaseColName;

	@FXML
	private TableColumn<PhaseEntry, Boolean> tbPhaseColDelete;

	@FXML
	private ProgressIndicator pi;

	@FXML
	private Button btNew;

	@FXML
	private Button btOk;

	@FXML
	private Button btUndo;

	@FXML
	private TextField txtPhaseAbbreviation;

	@FXML
	private TextField txtPhaseName;

	private ObservableList<PhaseEntry> phaseEntries = FXCollections.observableArrayList();

	@Autowired
	private PhaseService phaseService;

	@Override
	public Node getRootView() {
		return this.rootView;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		this.pi.setVisible(false);
		this.tbPhase.setItems(this.phaseEntries);
		this.btOk.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/ic_ok.png"))));
		this.btOk.setDisable(true);
		this.btUndo.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/ic_undo.png"))));
		this.btUndo.setDisable(true);
		this.txtPhaseAbbreviation.setDisable(true);
		this.txtPhaseName.setDisable(true);
		this.tbPhase.setEditable(true);
		this.tbPhaseColId.setCellValueFactory(new PropertyValueFactory("id"));
		this.tbPhaseColAbbreviation.setCellValueFactory(new PropertyValueFactory("abbreviation"));
		this.tbPhaseColName.setCellValueFactory(new PropertyValueFactory("name"));
		this.tbPhaseColName.setCellFactory(TextFieldTableCell.forTableColumn());
		this.tbPhaseColName.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<PhaseEntry, String>>() {

			@Override
			public void handle(TableColumn.CellEditEvent<PhaseEntry, String> t) {
				PhaseEntry phaseEntry = t.getTableView().getItems().get(t.getTablePosition().getRow());
				try {
					PhasesManagementController.this.phaseService.update(phaseEntry.getId(), null, t.getNewValue());
				} catch(TkStrikeServiceException e) {
					PhasesManagementController.this.manageException(e, "Phases update", null);
				}
				PhasesManagementController.this.refreshPhases();
			}
		});
		this.tbPhaseColAbbreviation.setCellFactory(TextFieldTableCell.forTableColumn());
		this.tbPhaseColAbbreviation.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<PhaseEntry, String>>() {

			@Override
			public void handle(TableColumn.CellEditEvent<PhaseEntry, String> t) {
				PhaseEntry phaseEntry = t.getTableView().getItems().get(t.getTablePosition().getRow());
				try {
					PhasesManagementController.this.phaseService.update(phaseEntry.getId(), t.getNewValue(), null);
				} catch(TkStrikeServiceException e) {
					PhasesManagementController.this.manageException(e, "Phases update", null);
				}
				PhasesManagementController.this.refreshPhases();
			}
		});
		this.tbPhaseColDelete.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PhaseEntry, Boolean>, ObservableValue<Boolean>>() {

			@Override
			public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<PhaseEntry, Boolean> features) {
				return new SimpleBooleanProperty((features.getValue() != null));
			}
		});
		this.tbPhaseColDelete.setCellFactory(new Callback<TableColumn<PhaseEntry, Boolean>, TableCell<PhaseEntry, Boolean>>() {

			@Override
			public TableCell<PhaseEntry, Boolean> call(TableColumn<PhaseEntry, Boolean> personBooleanTableColumn) {
				return new TkStrikeBaseTableWithDeleteManagementController.DeleteEntryCell(tbPhase);
			}
		});
	}

	@Override
	public void afterPropertiesSet() throws Exception {}

	@Override
	public void onWindowShowEvent() {
		super.onWindowShowEvent();
		refreshPhases();
	}

	@Override
	protected TkStrikeService<Phase, PhaseEntry> getTkStrikeService() {
		return this.phaseService;
	}

	@Override
	protected ObservableList<PhaseEntry> getObservableList4Table() {
		return this.phaseEntries;
	}

	@Override
	protected TableView<PhaseEntry> getTableView() {
		return this.tbPhase;
	}

	@Override
	protected ProgressIndicator getProgressIndicator() {
		return this.pi;
	}

	public void refreshPhases() {
		refreshTable();
	}

	public void newPhase() {
		this.txtPhaseAbbreviation.clear();
		this.txtPhaseAbbreviation.setDisable(false);
		this.txtPhaseName.clear();
		this.txtPhaseName.setDisable(false);
		this.btOk.setDisable(false);
		this.btUndo.setDisable(false);
	}

	public void savePhase() {
		String newPhaseName = this.txtPhaseName.getText();
		String newPhaseAbbreviation = this.txtPhaseAbbreviation.getText();
		if(StringUtils.isNotBlank(newPhaseName) &&
				StringUtils.isNotBlank(newPhaseAbbreviation)) {
			try {
				this.phaseService.createNew(newPhaseAbbreviation, newPhaseName);
			} catch(TkStrikeServiceException e) {
				manageException(e, "Phases update", null);
			}
			refreshPhases();
			undoPhase();
		}
	}

	public void undoPhase() {
		this.txtPhaseAbbreviation.clear();
		this.txtPhaseAbbreviation.setDisable(true);
		this.txtPhaseName.clear();
		this.txtPhaseName.setDisable(true);
		this.btOk.setDisable(true);
		this.btUndo.setDisable(true);
	}
}
