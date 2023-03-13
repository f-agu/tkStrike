package com.xtremis.daedo.tkstrike.ui.controller.ringmanager;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.xtremis.daedo.tkstrike.orm.model.Flag;
import com.xtremis.daedo.tkstrike.service.FlagService;
import com.xtremis.daedo.tkstrike.service.TkStrikeService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeBaseTableWithDeleteManagementController;
import com.xtremis.daedo.tkstrike.ui.scene.FlagEntry;
import com.xtremis.daedo.tkstrike.ui.scene.control.RestrictiveTextField;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.Callback;


public class FlagsManagementController extends TkStrikeBaseTableWithDeleteManagementController<Flag, FlagEntry, FlagService> {

	@FXML
	private TableView<FlagEntry> tbFlag;

	@FXML
	private TableColumn<FlagEntry, String> tbFlagColId;

	@FXML
	private TableColumn<FlagEntry, String> tbFlagColName;

	@FXML
	private TableColumn<FlagEntry, String> tbFlagColAbbreviation;

	@FXML
	private TableColumn<FlagEntry, Image> tbFlagColImage;

	@FXML
	private TableColumn<FlagEntry, Boolean> tbFlagColShowName;

	@FXML
	private TableColumn<FlagEntry, Boolean> tbFlagColDelete;

	@FXML
	private ProgressIndicator pi;

	@FXML
	private Button btNew;

	@FXML
	private Button btOk;

	@FXML
	private Button btUndo;

	@FXML
	private TextField txtFlagName;

	@FXML
	private RestrictiveTextField txtFlagAbbreviation;

	@FXML
	private ImageView ivFlagImage;

	@FXML
	private ToggleButton tgShowName;

	@FXML
	private Button btChangeImage;

	final FileChooser fileChooser = new FileChooser();

	private ObservableList<FlagEntry> flagEntries = FXCollections.observableArrayList();

	@Autowired
	private FlagService flagService;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		this.pi.setVisible(false);
		this.tbFlag.setItems(this.flagEntries);
		this.btOk.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/ic_ok.png"))));
		this.btOk.setDisable(true);
		this.btUndo.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/ic_undo.png"))));
		this.btUndo.setDisable(true);
		this.txtFlagName.setDisable(true);
		this.txtFlagAbbreviation.setMaxLength(15);
		this.txtFlagAbbreviation.setDefaultValue(null);
		this.txtFlagAbbreviation.setDisable(true);
		this.tgShowName.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				FlagsManagementController.this.tgShowName.setText(FlagsManagementController.this.getMessage("toggle.showName." + (newValue
						.booleanValue() ? "enabled" : "disabled")));
			}
		});
		this.tgShowName.setDisable(true);
		this.btChangeImage.setDisable(true);
		this.tbFlag.setEditable(true);
		this.tbFlagColId.setCellValueFactory(new PropertyValueFactory("id"));
		this.tbFlagColName.setCellValueFactory(new PropertyValueFactory("name"));
		this.tbFlagColName.setCellFactory(TextFieldTableCell.forTableColumn());
		this.tbFlagColName.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<FlagEntry, String>>() {

			@Override
			public void handle(TableColumn.CellEditEvent<FlagEntry, String> t) {
				FlagEntry flagEntry = t.getTableView().getItems().get(t.getTablePosition().getRow());
				try {
					FlagsManagementController.this.flagService.update(flagEntry.getId(), t.getNewValue(), null, null, null);
				} catch(TkStrikeServiceException e) {
					FlagsManagementController.this.manageException(e, "Flags - update by name", null);
				}
				FlagsManagementController.this.refreshFlags();
			}
		});
		this.tbFlagColAbbreviation.setCellValueFactory(new PropertyValueFactory("abbreviation"));
		this.tbFlagColAbbreviation.setCellFactory(TextFieldTableCell.forTableColumn());
		this.tbFlagColAbbreviation.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<FlagEntry, String>>() {

			@Override
			public void handle(TableColumn.CellEditEvent<FlagEntry, String> t) {
				FlagEntry flagEntry = t.getTableView().getItems().get(t.getTablePosition().getRow());
				try {
					FlagsManagementController.this.flagService.update(flagEntry.getId(), flagEntry.getName(), t.getNewValue(), null, null);
				} catch(TkStrikeServiceException e) {
					FlagsManagementController.this.manageException(e, "Flags - update by abbr", null);
				}
				FlagsManagementController.this.refreshFlags();
			}
		});
		this.tbFlagColImage.setCellValueFactory(new PropertyValueFactory("image"));
		this.tbFlagColImage.setCellFactory(new Callback<TableColumn<FlagEntry, Image>, TableCell<FlagEntry, Image>>() {

			@Override
			public TableCell<FlagEntry, Image> call(TableColumn<FlagEntry, Image> flagEntryImageTableColumn) {
				return new TableCell<FlagEntry, Image>() {

					ImageView imageView;

					@Override
					protected void updateItem(Image image, boolean b) {
						if(image != null)
							this.imageView.setImage(image);
					}
				};
			}
		});
		this.tbFlagColShowName.setCellValueFactory(new PropertyValueFactory("showName"));
		this.tbFlagColShowName.setCellFactory(new Callback<TableColumn<FlagEntry, Boolean>, TableCell<FlagEntry, Boolean>>() {

			@Override
			public TableCell<FlagEntry, Boolean> call(TableColumn<FlagEntry, Boolean> flagEntryImageTableColumn) {
				return new FlagsManagementController.ShowNameEntryCell(FlagsManagementController.this.tbFlag);
			}
		});
		this.tbFlagColDelete.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<FlagEntry, Boolean>, ObservableValue<Boolean>>() {

			@Override
			public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<FlagEntry, Boolean> features) {
				return new SimpleBooleanProperty((features.getValue() != null));
			}
		});
		this.tbFlagColDelete.setCellFactory(new Callback<TableColumn<FlagEntry, Boolean>, TableCell<FlagEntry, Boolean>>() {

			@Override
			public TableCell<FlagEntry, Boolean> call(TableColumn<FlagEntry, Boolean> personBooleanTableColumn) {
				return new TkStrikeBaseTableWithDeleteManagementController.DeleteEntryCell(tbFlag);
			}
		});
	}

	@Override
	public void afterPropertiesSet() throws Exception {}

	@Override
	public void onWindowShowEvent() {
		super.onWindowShowEvent();
		refreshFlags();
	}

	@Override
	protected TkStrikeService<Flag, FlagEntry> getTkStrikeService() {
		return this.flagService;
	}

	@Override
	protected ObservableList<FlagEntry> getObservableList4Table() {
		return this.flagEntries;
	}

	@Override
	protected TableView<FlagEntry> getTableView() {
		return this.tbFlag;
	}

	@Override
	protected ProgressIndicator getProgressIndicator() {
		return this.pi;
	}

	public void refreshFlags() {
		refreshTable();
	}

	public void newFlag() {
		this.txtFlagName.clear();
		this.txtFlagName.setDisable(false);
		this.txtFlagAbbreviation.clear();
		this.txtFlagAbbreviation.setDisable(false);
		this.tgShowName.setSelected(false);
		this.tgShowName.setDisable(false);
		this.ivFlagImage.setImage(null);
		this.btChangeImage.setDisable(false);
		this.btOk.setDisable(false);
		this.btUndo.setDisable(false);
	}

	public void saveFlag() {
		String newFlagName = this.txtFlagName.getText();
		String newFlagAbbr = this.txtFlagAbbreviation.getText();
		Image newFlagImage = this.ivFlagImage.getImage();
		if(StringUtils.isNotBlank(newFlagName)) {
			try {
				byte[] imageBytes = null;
				if(newFlagImage != null) {
					BufferedImage bImage = SwingFXUtils.fromFXImage(newFlagImage, null);
					ByteArrayOutputStream s = new ByteArrayOutputStream();
					ImageIO.write(bImage, "png", s);
					imageBytes = s.toByteArray();
				}
				this.flagService.createNew(newFlagName, newFlagAbbr, Boolean.valueOf(this.tgShowName.isSelected()), imageBytes);
			} catch(Exception e) {
				manageException(e, "Flags - createNew", null);
			}
			refreshFlags();
			undoFlag();
		}
	}

	public void undoFlag() {
		this.txtFlagName.clear();
		this.txtFlagName.setDisable(true);
		this.txtFlagAbbreviation.clear();
		this.txtFlagAbbreviation.setDisable(true);
		this.ivFlagImage.setImage(null);
		this.btChangeImage.setDisable(true);
		this.tgShowName.setDisable(true);
		this.tgShowName.setSelected(false);
		this.btOk.setDisable(true);
		this.btUndo.setDisable(true);
	}

	public void changeFlagImage() {
		this.fileChooser.setTitle(getMessage("title.selectImage"));
		this.fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter[] {new FileChooser.ExtensionFilter("All Images", new String[] {
				"*.*"}), new FileChooser.ExtensionFilter("JPG", new String[] {"*.jpg"}), new FileChooser.ExtensionFilter("PNG", new String[] {
						"*.png"}), new FileChooser.ExtensionFilter("BMP", new String[] {"*.bmp"})});
		File selectedImage = this.fileChooser.showOpenDialog(getCurrentStage());
		if(selectedImage != null)
			try {
				this.ivFlagImage.setImage(new Image(new FileInputStream(selectedImage)));
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			}
	}

	class ShowNameEntryCell extends TableCell<FlagEntry, Boolean> {

		final ToggleButton toggleButton;

		public ShowNameEntryCell(final TableView<FlagEntry> tableView) {
			this.toggleButton = new ToggleButton();
			this.toggleButton.selectedProperty().addListener(new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					FlagsManagementController.ShowNameEntryCell.this.toggleButton.setText(FlagsManagementController.this.getMessage("toggle.showName."
							+ (newValue.booleanValue() ? "enabled" : "disabled")));
				}
			});
			this.toggleButton.setSelected(false);
			this.toggleButton.setText(FlagsManagementController.this.getMessage("toggle.showName.disabled"));
			this.toggleButton.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					int selectedIndex = FlagsManagementController.ShowNameEntryCell.this.getTableRow().getIndex();
					FlagEntry entry = tableView.getItems().get(selectedIndex);
					if(entry != null && StringUtils.isNotBlank(entry.getId())) {
						try {
							FlagsManagementController.this.flagService.update(entry.getId(), null, null, Boolean.valueOf(
									FlagsManagementController.ShowNameEntryCell.this.toggleButton.isSelected()), null);
						} catch(TkStrikeServiceException e) {
							e.printStackTrace();
						}
						FlagsManagementController.this.refreshFlags();
					}
				}
			});
			setGraphic(this.toggleButton);
		}

		@Override
		protected void updateItem(Boolean selected, boolean b) {
			if(selected != null)
				this.toggleButton.setSelected(selected.booleanValue());
		}
	}
}
