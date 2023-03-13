package com.xtremis.daedo.tkstrike.ui.controller.ringmanager;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.xtremis.daedo.tkstrike.orm.model.Category;
import com.xtremis.daedo.tkstrike.orm.model.Gender;
import com.xtremis.daedo.tkstrike.service.CategoryService;
import com.xtremis.daedo.tkstrike.service.SubCategoryService;
import com.xtremis.daedo.tkstrike.service.TkStrikeService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeBaseTableWithDeleteManagementController;
import com.xtremis.daedo.tkstrike.ui.model.CategoryEntry;
import com.xtremis.daedo.tkstrike.ui.model.SubCategoryEntry;
import com.xtremis.daedo.tkstrike.ui.scene.control.RestrictiveTextField;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;


public class CategoriesManagementController extends TkStrikeBaseTableWithDeleteManagementController<Category, CategoryEntry, CategoryService> {

	@FXML
	private Parent root;

	@FXML
	private Node rootView;

	@FXML
	private TableView<CategoryEntry> tbCategories;

	@FXML
	private TableColumn<CategoryEntry, String> tbCategoriesColId;

	@FXML
	private TableColumn<String, SubCategoryEntry> tbCategoriesColSubCategory;

	@FXML
	private TableColumn<String, Gender> tbCategoriesColGender;

	@FXML
	private TableColumn<CategoryEntry, String> tbCategoriesColName;

	@FXML
	private TableColumn<CategoryEntry, Integer> tbCategoriesColBodyLevel;

	@FXML
	private TableColumn<CategoryEntry, Integer> tbCategoriesColHeadLevel;

	@FXML
	private TableColumn<CategoryEntry, Boolean> tbCategoriesColDelete;

	@FXML
	private Button btNew;

	@FXML
	private Button btOk;

	@FXML
	private Button btUndo;

	@FXML
	private ProgressIndicator pi;

	@FXML
	private ComboBox<Gender> cmbGender;

	@FXML
	private TextField txtCategoryName;

	@FXML
	private ComboBox<SubCategoryEntry> cmbSubCategory;

	@FXML
	private RestrictiveTextField txtBodyLevel;

	@FXML
	private RestrictiveTextField txtHeadLevel;

	private ObservableList<CategoryEntry> categoryEntries = FXCollections.observableArrayList();

	private ObservableList<SubCategoryEntry> subCategoryEntries = FXCollections.observableArrayList();

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private SubCategoryService subCategoryService;

	private String subCategoryIdFilter = null;

	@Override
	public Node getRootView() {
		return this.rootView;
	}

	@Override
	public EventHandler<WindowEvent> getOnWindowCloseEventHandler() {
		return new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				CategoriesManagementController.this.subCategoryIdFilter = null;
			}
		};
	}

	@Override
	public void onWindowShowEvent() {
		if(StringUtils.isNotBlank(this.subCategoryIdFilter)) {
			refreshCategories(this.subCategoryIdFilter);
		} else {
			refreshCategories();
		}
		refreshSubCategories();
	}

	public void filterCategoriesBySubCategoryId(String subCategoryId) {
		this.subCategoryIdFilter = subCategoryId;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		this.tbCategories.setItems(this.categoryEntries);
		this.btOk.setDisable(true);
		this.btUndo.setDisable(true);
		this.txtCategoryName.setDisable(true);
		this.cmbSubCategory.setItems(this.subCategoryEntries);
		this.cmbSubCategory.setDisable(true);
		this.cmbGender.setDisable(true);
		this.txtBodyLevel.setDisable(true);
		this.txtBodyLevel.setRestrict("[0-9]");
		this.txtBodyLevel.setDefaultValue("0");
		this.txtBodyLevel.setMaxLength(3);
		this.txtHeadLevel.setDisable(true);
		this.txtHeadLevel.setRestrict("[0-9]");
		this.txtHeadLevel.setDefaultValue("0");
		this.txtHeadLevel.setMaxLength(3);
		this.tbCategories.setEditable(true);
		this.tbCategoriesColId.setCellValueFactory(new PropertyValueFactory("id"));
		this.tbCategoriesColSubCategory.setCellValueFactory(new PropertyValueFactory("subCategory"));
		this.tbCategoriesColSubCategory.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<String, SubCategoryEntry>>() {

			@Override
			public void handle(TableColumn.CellEditEvent<String, SubCategoryEntry> t) {
				CategoryEntry categoryEntry = CategoriesManagementController.this.tbCategories.getItems().get(t.getTablePosition().getRow());
				try {
					CategoriesManagementController.this.categoryService.update(categoryEntry.getId(), categoryEntry
							.getName(), t
									.getNewValue().getId(), categoryEntry
											.getGender(),
							Integer.valueOf(categoryEntry.getBodyLevel()),
							Integer.valueOf(categoryEntry.getHeadLevel()));
				} catch(TkStrikeServiceException e) {
					CategoriesManagementController.this.manageException(e, "Categories - saveBySubCategory", null);
				}
				if(StringUtils.isNotBlank(CategoriesManagementController.this.subCategoryIdFilter)) {
					CategoriesManagementController.this.refreshCategories(CategoriesManagementController.this.subCategoryIdFilter);
				} else {
					CategoriesManagementController.this.refreshCategories();
				}
			}
		});
		ArrayList<Gender> genderList = new ArrayList<>(2);
		genderList.add(Gender.MALE);
		genderList.add(Gender.FEMALE);
		ObservableList<Gender> genderObservableList = FXCollections.observableList(genderList);
		this.cmbGender.setItems(genderObservableList);
		this.tbCategoriesColGender.setCellValueFactory(new PropertyValueFactory("gender"));
		this.tbCategoriesColGender.setCellFactory(ComboBoxTableCell.forTableColumn(new StringConverter<Gender>() {

			@Override
			public String toString(Gender o) {
				return o.toString();
			}

			@Override
			public Gender fromString(String s) {
				return Gender.valueOf(s);
			}
		}, genderObservableList));
		this.tbCategoriesColGender.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<String, Gender>>() {

			@Override
			public void handle(TableColumn.CellEditEvent<String, Gender> t) {
				CategoryEntry categoryEntry = CategoriesManagementController.this.tbCategories.getItems().get(t.getTablePosition().getRow());
				try {
					CategoriesManagementController.this.categoryService.update(categoryEntry.getId(), categoryEntry
							.getName(), categoryEntry
									.getSubCategory().getId(), t
											.getNewValue(),
							Integer.valueOf(categoryEntry.getBodyLevel()),
							Integer.valueOf(categoryEntry.getHeadLevel()));
				} catch(TkStrikeServiceException e) {
					CategoriesManagementController.this.manageException(e, "Categories - saveByGender", null);
				}
				if(StringUtils.isNotBlank(CategoriesManagementController.this.subCategoryIdFilter)) {
					CategoriesManagementController.this.refreshCategories(CategoriesManagementController.this.subCategoryIdFilter);
				} else {
					CategoriesManagementController.this.refreshCategories();
				}
			}
		});
		this.tbCategoriesColName.setCellValueFactory(new PropertyValueFactory("name"));
		this.tbCategoriesColName.setCellFactory(TextFieldTableCell.forTableColumn());
		this.tbCategoriesColName.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<CategoryEntry, String>>() {

			@Override
			public void handle(TableColumn.CellEditEvent<CategoryEntry, String> t) {
				CategoryEntry categoryEntry = t.getTableView().getItems().get(t.getTablePosition().getRow());
				try {
					CategoriesManagementController.this.categoryService.update(categoryEntry.getId(), t
							.getNewValue(), categoryEntry
									.getSubCategory().getId(), categoryEntry
											.getGender(),
							Integer.valueOf(categoryEntry.getBodyLevel()),
							Integer.valueOf(categoryEntry.getHeadLevel()));
				} catch(TkStrikeServiceException e) {
					CategoriesManagementController.this.manageException(e, "Categories - saveByName", null);
				}
				if(StringUtils.isNotBlank(CategoriesManagementController.this.subCategoryIdFilter)) {
					CategoriesManagementController.this.refreshCategories(CategoriesManagementController.this.subCategoryIdFilter);
				} else {
					CategoriesManagementController.this.refreshCategories();
				}
			}
		});
		this.tbCategoriesColBodyLevel.setCellValueFactory(new PropertyValueFactory("bodyLevel"));
		this.tbCategoriesColBodyLevel.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Integer>() {

			@Override
			public String toString(Integer integer) {
				return "" + integer;
			}

			@Override
			public Integer fromString(String s) {
				return Integer.valueOf(Integer.parseInt(s));
			}
		}));
		this.tbCategoriesColBodyLevel.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<CategoryEntry, Integer>>() {

			@Override
			public void handle(TableColumn.CellEditEvent<CategoryEntry, Integer> t) {
				CategoryEntry categoryEntry = t.getTableView().getItems().get(t.getTablePosition().getRow());
				try {
					CategoriesManagementController.this.categoryService.update(categoryEntry.getId(), categoryEntry
							.getName(), categoryEntry
									.getSubCategory().getId(), categoryEntry
											.getGender(), t
													.getNewValue(),
							Integer.valueOf(categoryEntry.getHeadLevel()));
				} catch(TkStrikeServiceException e) {
					CategoriesManagementController.this.manageException(e, "Categories - saveByBodyLevel", null);
				}
				if(StringUtils.isNotBlank(CategoriesManagementController.this.subCategoryIdFilter)) {
					CategoriesManagementController.this.refreshCategories(CategoriesManagementController.this.subCategoryIdFilter);
				} else {
					CategoriesManagementController.this.refreshCategories();
				}
			}
		});
		this.tbCategoriesColHeadLevel.setCellValueFactory(new PropertyValueFactory("headLevel"));
		this.tbCategoriesColHeadLevel.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Integer>() {

			@Override
			public String toString(Integer integer) {
				return "" + integer;
			}

			@Override
			public Integer fromString(String s) {
				return Integer.valueOf(Integer.parseInt(s));
			}
		}));
		this.tbCategoriesColHeadLevel.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<CategoryEntry, Integer>>() {

			@Override
			public void handle(TableColumn.CellEditEvent<CategoryEntry, Integer> t) {
				CategoryEntry categoryEntry = t.getTableView().getItems().get(t.getTablePosition().getRow());
				try {
					CategoriesManagementController.this.categoryService.update(categoryEntry.getId(), categoryEntry
							.getName(), categoryEntry
									.getSubCategory().getId(), categoryEntry
											.getGender(),
							Integer.valueOf(categoryEntry.getBodyLevel()), t
									.getNewValue());
				} catch(TkStrikeServiceException e) {
					CategoriesManagementController.this.manageException(e, "Categories - saveByHeadLevel", null);
				}
				if(StringUtils.isNotBlank(CategoriesManagementController.this.subCategoryIdFilter)) {
					CategoriesManagementController.this.refreshCategories(CategoriesManagementController.this.subCategoryIdFilter);
				} else {
					CategoriesManagementController.this.refreshCategories();
				}
			}
		});
		this.tbCategoriesColDelete.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<CategoryEntry, Boolean>, ObservableValue<Boolean>>() {

					@Override
					public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<CategoryEntry, Boolean> features) {
						return new SimpleBooleanProperty((features.getValue() != null));
					}
				});
		this.tbCategoriesColDelete.setCellFactory(new Callback<TableColumn<CategoryEntry, Boolean>, TableCell<CategoryEntry, Boolean>>() {

			@Override
			public TableCell<CategoryEntry, Boolean> call(TableColumn<CategoryEntry, Boolean> personBooleanTableColumn) {
				return new TkStrikeBaseTableWithDeleteManagementController.DeleteEntryCell(tbCategories);
			}
		});
	}

	@Override
	public void afterPropertiesSet() throws Exception {}

	@Override
	protected TkStrikeService<Category, CategoryEntry> getTkStrikeService() {
		return this.categoryService;
	}

	@Override
	protected ObservableList<CategoryEntry> getObservableList4Table() {
		return this.categoryEntries;
	}

	@Override
	protected TableView<CategoryEntry> getTableView() {
		return this.tbCategories;
	}

	@Override
	protected ProgressIndicator getProgressIndicator() {
		return this.pi;
	}

	public void refreshCategories() {
		refreshTable();
	}

	public void refreshCategories(final String subCategoryId) {
		showTableProgressIndicator(true);
		ExecutorService es = Executors.newSingleThreadExecutor();
		es.submit(new Runnable() {

			@Override
			public void run() {
				ObservableList<CategoryEntry> theList = CategoriesManagementController.this.getObservableList4Table();
				try {
					theList = FXCollections.observableArrayList(CategoriesManagementController.this.categoryService.findEntriesBySubCategoryId(
							subCategoryId));
				} catch(TkStrikeServiceException e) {
					CategoriesManagementController.this.manageException(e, "Categories - refreshCategories", null);
				}
				final ObservableList<CategoryEntry> list = theList;
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						CategoriesManagementController.this.getTableView().setItems(list);
						CategoriesManagementController.this.showTableProgressIndicator(false);
					}
				});
			}
		});
		es.shutdown();
	}

	public void refreshSubCategories() {
		try {
			this.subCategoryEntries.clear();
			this.subCategoryEntries.addAll(this.subCategoryService.findAllEntries());
		} catch(TkStrikeServiceException e) {
			manageException(e, "Categories - refreshSubCategories", null);
		}
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				CategoriesManagementController.this.tbCategoriesColSubCategory.setCellFactory(ComboBoxTableCell.forTableColumn(
						new StringConverter<SubCategoryEntry>() {

							@Override
							public String toString(SubCategoryEntry o) {
								return o.getName();
							}

							@Override
							public SubCategoryEntry fromString(String s) {
								SubCategoryEntry subCategoryEntry = new SubCategoryEntry();
								subCategoryEntry.name.set(s);
								return subCategoryEntry;
							}
						}, CategoriesManagementController.this.subCategoryEntries));
			}
		});
	}

	public void newCategory() {
		this.txtCategoryName.clear();
		this.txtBodyLevel.clear();
		this.txtHeadLevel.clear();
		this.txtCategoryName.setDisable(false);
		this.cmbSubCategory.setDisable(false);
		this.cmbGender.setDisable(false);
		this.txtBodyLevel.setDisable(false);
		this.txtHeadLevel.setDisable(false);
		this.btOk.setDisable(false);
		this.btUndo.setDisable(false);
	}

	public void saveCategory() {
		String txtCategoryNameText = this.txtCategoryName.getText();
		if(StringUtils.isNotBlank(txtCategoryNameText)) {
			try {
				this.categoryService.createNew(txtCategoryNameText, this.cmbSubCategory.getValue().getId(), this.cmbGender.getValue(),
						Integer.valueOf(Integer.parseInt(this.txtBodyLevel.getText())),
						Integer.valueOf(Integer.parseInt(this.txtHeadLevel.getText())));
			} catch(TkStrikeServiceException e) {
				manageException(e, "Categories - saveCategory", null);
			}
			if(StringUtils.isNotBlank(this.subCategoryIdFilter)) {
				refreshCategories(this.subCategoryIdFilter);
			} else {
				refreshCategories();
			}
			this.txtCategoryName.clear();
			this.txtBodyLevel.clear();
			this.txtHeadLevel.clear();
			this.txtCategoryName.setDisable(true);
			this.cmbSubCategory.setDisable(true);
			this.cmbGender.setDisable(true);
			this.txtBodyLevel.setDisable(true);
			this.txtHeadLevel.setDisable(true);
			this.btOk.setDisable(true);
			this.btUndo.setDisable(true);
		}
	}

	public void undoCategory() {
		this.txtCategoryName.clear();
		this.txtBodyLevel.clear();
		this.txtHeadLevel.clear();
		this.txtCategoryName.setDisable(true);
		this.cmbSubCategory.setDisable(true);
		this.cmbGender.setDisable(true);
		this.txtBodyLevel.setDisable(true);
		this.txtHeadLevel.setDisable(true);
		this.btOk.setDisable(true);
		this.btUndo.setDisable(true);
	}
}
