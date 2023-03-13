package com.xtremis.daedo.tkstrike.ui.controller.ringmanager;

import com.xtremis.daedo.tkstrike.ei.client.WtOvrClientService;
import com.xtremis.daedo.tkstrike.orm.model.MatchConfiguration;
import com.xtremis.daedo.tkstrike.service.CommonMatchConfigurationService;
import com.xtremis.daedo.tkstrike.service.ExternalConfigService;
import com.xtremis.daedo.tkstrike.service.TkStrikeService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchConfigurationDto;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeBaseTableWithDeleteManagementController;
import com.xtremis.daedo.tkstrike.ui.model.ExternalConfigEntry;
import com.xtremis.daedo.tkstrike.ui.model.MatchConfigurationEntry;
import java.net.URL;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DateTimeStringConverter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class MatchConfigurationManagementController extends TkStrikeBaseTableWithDeleteManagementController<MatchConfiguration, MatchConfigurationEntry, CommonMatchConfigurationService<MatchConfiguration, MatchConfigurationEntry>> {
  private OpenType openType = OpenType.NORMAL;
  
  @FXML
  private Pane pnMain;
  
  @FXML
  private Pane piPanel;
  
  @FXML
  private Label lblPiMessage;
  
  @FXML
  private DatePicker dpDateFilter;
  
  @FXML
  private TableView<MatchConfigurationEntry> tbMatch;
  
  @FXML
  private TableColumn<MatchConfigurationEntry, String> tbMatchColId;
  
  @FXML
  private TableColumn<MatchConfigurationEntry, String> tbMatchColMatchNumber;
  
  @FXML
  private TableColumn<MatchConfigurationEntry, String> tbMatchColPhase;
  
  @FXML
  private TableColumn<MatchConfigurationEntry, String> tbMatchColCategory;
  
  @FXML
  private TableColumn<MatchConfigurationEntry, String> tbMatchColBlueWTFID;
  
  @FXML
  private TableColumn<MatchConfigurationEntry, String> tbMatchColRedWTFID;
  
  @FXML
  private TableColumn<MatchConfigurationEntry, Boolean> tbMatchColStarted;
  
  @FXML
  private TableColumn<MatchConfigurationEntry, Date> tbMatchColCreatedOrImportedDate;
  
  @FXML
  private TableColumn<MatchConfigurationEntry, Boolean> tbMatchColDelete;
  
  @FXML
  private TableColumn<MatchConfigurationEntry, Boolean> tbMatchColRefresh;
  
  @FXML
  private ProgressIndicator pi;
  
  @Autowired
  private CommonMatchConfigurationService matchConfigurationService;
  
  @Autowired
  private ExternalConfigService externalConfigService;
  
  @Autowired
  private WtOvrClientService wtOvrClientService;
  
  private ObservableList<MatchConfigurationEntry> matchConfigurationEntries = FXCollections.observableArrayList();
  
  private MatchConfigurationEntry matchConfigurationEntry = null;
  
  protected TkStrikeService<MatchConfiguration, MatchConfigurationEntry> getTkStrikeService() {
    return (TkStrikeService<MatchConfiguration, MatchConfigurationEntry>)this.matchConfigurationService;
  }
  
  protected ObservableList<MatchConfigurationEntry> getObservableList4Table() {
    return this.matchConfigurationEntries;
  }
  
  protected TableView<MatchConfigurationEntry> getTableView() {
    return this.tbMatch;
  }
  
  protected ProgressIndicator getProgressIndicator() {
    return this.pi;
  }
  
  public Node getRootView() {
    return this.rootView;
  }
  
  public void onWindowShowEvent() {
    super.onWindowShowEvent();
    this.matchConfigurationEntry = null;
    this.dpDateFilter.setValue(LocalDate.now());
    if (OpenType.NORMAL.equals(this.openType)) {
      refreshTable();
    } else if (OpenType.WT_OVR_MATCH_REQUEST.equals(this.openType)) {
      showTableProgressIndicator(true);
      TkStrikeExecutors.executeInThreadPool(new Runnable() {
            public void run() {
              boolean showError = true;
              int matchesImported = 0;
              try {
                ExternalConfigEntry externalConfigEntry = MatchConfigurationManagementController.this.externalConfigService.getExternalConfigEntry();
                if (externalConfigEntry != null) {
                  MatchConfigurationManagementController.logger.info("External Config entry OK");
                  List<MatchConfigurationDto> matches = MatchConfigurationManagementController.this.wtOvrClientService.findWtOvrMatches(externalConfigEntry.getWtOvrUrl(), externalConfigEntry.getWtOvrXApiKey(), Integer.valueOf(externalConfigEntry.getWtOvrMat()));
                  if (matches != null) {
                    MatchConfigurationManagementController.logger.info("Tenim matches " + matches.size());
                    for (MatchConfigurationDto match : matches) {
                      if (match != null && match.getInternalId() != null) {
                        Platform.runLater(new Runnable() {
                              public void run() {
                                MatchConfigurationManagementController.this.lblPiMessage.setText("Processing match " + match.getInternalId());
                              }
                            });
                        MatchConfigurationManagementController.logger.info("Anem a processar match " + match.getInternalId());
                        MatchConfigurationEntry newMatchConfigurationEntry = (MatchConfigurationEntry)MatchConfigurationManagementController.this.matchConfigurationService.transformByDto(match, true);
                        MatchConfigurationManagementController.logger.info("Hem transformat i desat a la base de dades el match " + newMatchConfigurationEntry.getId());
                        matchesImported++;
                        continue;
                      } 
                      MatchConfigurationManagementController.logger.info("Get a match without internalId ->" + ReflectionToStringBuilder.reflectionToString(match));
                    } 
                  } 
                } 
              } catch (TkStrikeServiceException e) {
                MatchConfigurationManagementController.this.showTableProgressIndicator(false);
                showError = false;
                MatchConfigurationManagementController.this.manageException((Throwable)e, "RequestMatch to WT OVR", e.getMessage());
              } catch (Exception e) {
                MatchConfigurationManagementController.this.manageException(e, "RequestMatch to WT OVR", e.getMessage());
              } 
              if (matchesImported > 0)
                MatchConfigurationManagementController.this.showInfoDialog(MatchConfigurationManagementController.this.getMessage("title.default.info"), MatchConfigurationManagementController.this.getMessage("message.info.wtOvrMatchesImported", new String[] { "" + matchesImported })); 
              MatchConfigurationManagementController.this.doRefreshTable(MatchConfigurationManagementController.OpenType.WT_OVR_MATCH_REQUEST, (Date)null);
              MatchConfigurationManagementController.this.showTableProgressIndicator(false);
            }
          });
    } else if (OpenType.PREVIOUS_MATCHES.equals(this.openType)) {
      doRefreshTable(OpenType.PREVIOUS_MATCHES, (Date)null);
    } 
  }
  
  public OpenType getOpenType() {
    return this.openType;
  }
  
  public void setOpenType(OpenType openType) {
    this.openType = openType;
  }
  
  public void doDeleteAllMatchConfigurations() {
    Optional<T> opResponse = showConfirmDialog(getMessage("message.confirmDialog.title"), 
        getMessage("message.confirm.removeAllMatches"));
    if (opResponse != null && opResponse.isPresent() && opResponse.get().equals(ButtonType.OK)) {
      showTableProgressIndicator(true);
      TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {
            public Void call() throws Exception {
              try {
                MatchConfigurationManagementController.this.matchConfigurationService.deleteAll();
                MatchConfigurationManagementController.this.doRefreshTable(MatchConfigurationManagementController.this.openType, (Date)null);
              } catch (TkStrikeServiceException e) {
                MatchConfigurationManagementController.this.manageException((Throwable)e, "Delete all Match Configuration", e.getMessage());
              } finally {
                MatchConfigurationManagementController.this.showTableProgressIndicator(false);
              } 
              return null;
            }
          });
    } 
  }
  
  public void doApplyFilter() {
    doRefreshTable(this.openType, convertToDateViaInstant((LocalDate)this.dpDateFilter.getValue()));
  }
  
  public void close() {
    doCloseThisStage();
  }
  
  public void initialize(URL url, ResourceBundle resourceBundle) {
    this.tbMatch.setItems(this.matchConfigurationEntries);
    this.tbMatch.setEditable(false);
    this.tbMatch.setRowFactory(new Callback<TableView<MatchConfigurationEntry>, TableRow<MatchConfigurationEntry>>() {
          public TableRow<MatchConfigurationEntry> call(TableView<MatchConfigurationEntry> matchConfigurationEntryTableView) {
            final TableRow<MatchConfigurationEntry> row = new TableRow();
            row.setOnMouseClicked(new EventHandler<MouseEvent>() {
                  public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.getClickCount() > 1 && !row.isEmpty()) {
                      MatchConfigurationManagementController.this.matchConfigurationEntry = (MatchConfigurationEntry)row.getItem();
                      MatchConfigurationManagementController.this.doCloseThisStage();
                    } 
                  }
                });
            return row;
          }
        });
    this.tbMatchColId.setCellValueFactory((Callback)new PropertyValueFactory("id"));
    this.tbMatchColMatchNumber.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MatchConfigurationEntry, String>, ObservableValue<String>>() {
          public ObservableValue<String> call(TableColumn.CellDataFeatures<MatchConfigurationEntry, String> param) {
            SimpleStringProperty res = new SimpleStringProperty("");
            if (param != null && param.getValue() != null) {
              MatchConfigurationEntry configurationEntry = (MatchConfigurationEntry)param.getValue();
              if (configurationEntry != null)
                res.setValue(configurationEntry.getMatchNumber() + (configurationEntry.isParaTkdMatch() ? " (PARA TKD)" : "")); 
            } 
            return (ObservableValue<String>)res;
          }
        });
    this.tbMatchColPhase.setCellValueFactory((Callback)new PropertyValueFactory("phase"));
    this.tbMatchColCategory.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MatchConfigurationEntry, String>, ObservableValue<String>>() {
          public ObservableValue<String> call(TableColumn.CellDataFeatures<MatchConfigurationEntry, String> matchLogItemEntryStringCellDataFeatures) {
            SimpleStringProperty res = new SimpleStringProperty();
            MatchConfigurationEntry matchConfiguration = (MatchConfigurationEntry)matchLogItemEntryStringCellDataFeatures.getValue();
            if (matchConfiguration != null && 
              StringUtils.isNotBlank(matchConfiguration.getSubCategory().getName()) && 
              StringUtils.isNotBlank(matchConfiguration.getCategory().getName()))
              res.set(matchConfiguration.getSubCategory().getName() + " " + matchConfiguration.getGender().toString() + " " + matchConfiguration.getCategory().getName()); 
            return (ObservableValue<String>)res;
          }
        });
    this.tbMatchColBlueWTFID.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MatchConfigurationEntry, String>, ObservableValue<String>>() {
          public ObservableValue<String> call(TableColumn.CellDataFeatures<MatchConfigurationEntry, String> cellDataFeatures) {
            SimpleStringProperty res = new SimpleStringProperty("");
            MatchConfigurationEntry current = (MatchConfigurationEntry)cellDataFeatures.getValue();
            if (current != null && current.getBlueAthlete() != null)
              res.set(current.getBlueAthlete().getWfId()); 
            return (ObservableValue<String>)res;
          }
        });
    this.tbMatchColRedWTFID.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MatchConfigurationEntry, String>, ObservableValue<String>>() {
          public ObservableValue<String> call(TableColumn.CellDataFeatures<MatchConfigurationEntry, String> cellDataFeatures) {
            SimpleStringProperty res = new SimpleStringProperty("");
            MatchConfigurationEntry current = (MatchConfigurationEntry)cellDataFeatures.getValue();
            if (current != null && current.getRedAthlete() != null)
              res.set(current.getRedAthlete().getWfId()); 
            return (ObservableValue<String>)res;
          }
        });
    this.tbMatchColStarted.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MatchConfigurationEntry, Boolean>, ObservableValue<Boolean>>() {
          public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<MatchConfigurationEntry, Boolean> features) {
            return (ObservableValue<Boolean>)new SimpleBooleanProperty((features.getValue() != null && ((MatchConfigurationEntry)features.getValue()).isMatchStarted()));
          }
        });
    this.tbMatchColStarted.setCellFactory(new Callback<TableColumn<MatchConfigurationEntry, Boolean>, TableCell<MatchConfigurationEntry, Boolean>>() {
          public TableCell<MatchConfigurationEntry, Boolean> call(TableColumn<MatchConfigurationEntry, Boolean> personBooleanTableColumn) {
            return new MatchConfigurationManagementController.IsMatchStartedTableCell(MatchConfigurationManagementController.this.tbMatch);
          }
        });
    this.tbMatchColCreatedOrImportedDate.setCellValueFactory((Callback)new PropertyValueFactory("lastUpdateDatetime"));
    this.tbMatchColCreatedOrImportedDate.setCellFactory(TextFieldTableCell.forTableColumn((StringConverter)new DateTimeStringConverter(this.dfFullFormatPattern)));
    this.tbMatchColDelete.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MatchConfigurationEntry, Boolean>, ObservableValue<Boolean>>() {
          public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<MatchConfigurationEntry, Boolean> features) {
            return (ObservableValue<Boolean>)new SimpleBooleanProperty((features.getValue() != null));
          }
        });
    this.tbMatchColDelete.setCellFactory(new Callback<TableColumn<MatchConfigurationEntry, Boolean>, TableCell<MatchConfigurationEntry, Boolean>>() {
          public TableCell<MatchConfigurationEntry, Boolean> call(TableColumn<MatchConfigurationEntry, Boolean> personBooleanTableColumn) {
            return (TableCell<MatchConfigurationEntry, Boolean>)new TkStrikeBaseTableWithDeleteManagementController.DeleteEntryCell(MatchConfigurationManagementController.this, MatchConfigurationManagementController.this.tbMatch);
          }
        });
    this.tbMatchColRefresh.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MatchConfigurationEntry, Boolean>, ObservableValue<Boolean>>() {
          public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<MatchConfigurationEntry, Boolean> features) {
            return (ObservableValue<Boolean>)new SimpleBooleanProperty((features.getValue() != null && ((MatchConfigurationEntry)features.getValue()).getWtCompetitionDataProtocol().booleanValue()));
          }
        });
    this.tbMatchColRefresh.setCellFactory(new Callback<TableColumn<MatchConfigurationEntry, Boolean>, TableCell<MatchConfigurationEntry, Boolean>>() {
          public TableCell<MatchConfigurationEntry, Boolean> call(TableColumn<MatchConfigurationEntry, Boolean> personBooleanTableColumn) {
            return new MatchConfigurationManagementController.GoRefreshMatchConfiguration(MatchConfigurationManagementController.this.tbMatch);
          }
        });
  }
  
  protected boolean canDeleteEntryById(String entryId) {
    boolean res = true;
    if (getAppStatusWorker().getMatchConfigurationEntry() != null && 
      getAppStatusWorker().getMatchConfigurationEntry().getId() != null && 
      getAppStatusWorker().getMatchConfigurationEntry().getId().equals(entryId))
      res = false; 
    return res;
  }
  
  protected String getMessageEntityCantBeDeleted() {
    return getMessage("message.error.cantDeleteMatchConfiguration.isCurrentMatch");
  }
  
  public void afterPropertiesSet() throws Exception {}
  
  protected void showTableProgressIndicator(final boolean show) {
    Platform.runLater(new Runnable() {
          public void run() {
            MatchConfigurationManagementController.this.pnMain.setVisible(!show);
            MatchConfigurationManagementController.this.piPanel.setVisible(show);
          }
        });
  }
  
  private void doRefreshTable(final OpenType openType, final Date filterDate) {
    showTableProgressIndicator(true);
    TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {
          public Void call() throws Exception {
            final ObservableList<MatchConfigurationEntry> theList = MatchConfigurationManagementController.this.getObservableList4Table();
            try {
              List<MatchConfigurationEntry> allEntries = null;
              if (MatchConfigurationManagementController.OpenType.WT_OVR_MATCH_REQUEST.equals(openType) && filterDate == null) {
                allEntries = MatchConfigurationManagementController.this.matchConfigurationService.findEntriesNotStarted();
              } else if (MatchConfigurationManagementController.OpenType.PREVIOUS_MATCHES.equals(openType) && filterDate == null) {
                allEntries = MatchConfigurationManagementController.this.matchConfigurationService.findEntriesStarted();
              } else if (MatchConfigurationManagementController.OpenType.NORMAL.equals(openType) && filterDate == null) {
                allEntries = MatchConfigurationManagementController.this.matchConfigurationService.findAllEntries();
              } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(filterDate);
                calendar.set(11, 0);
                calendar.set(12, 0);
                calendar.set(13, 0);
                calendar.set(14, 0);
                Date firstDate = calendar.getTime();
                calendar.set(11, 23);
                calendar.set(12, 59);
                calendar.set(13, 59);
                Date lastDate = calendar.getTime();
                allEntries = MatchConfigurationManagementController.this.matchConfigurationService.findEntriesByDates(firstDate, lastDate);
              } 
              final List<MatchConfigurationEntry> fiEntries = allEntries;
              Platform.runLater(new Runnable() {
                    public void run() {
                      theList.clear();
                      if (fiEntries != null && fiEntries.size() > 0)
                        theList.addAll(fiEntries); 
                      MatchConfigurationManagementController.this.showTableProgressIndicator(false);
                    }
                  });
            } catch (TkStrikeServiceException e) {
              MatchConfigurationManagementController.this.manageException((Throwable)e, "Updating a table", null);
            } 
            return null;
          }
        });
  }
  
  protected void refreshTable() {
    doRefreshTable(OpenType.NORMAL, (Date)null);
  }
  
  public MatchConfigurationEntry getMatchConfigurationEntry() {
    return this.matchConfigurationEntry;
  }
  
  private class GoRefreshMatchConfiguration extends TableCell<MatchConfigurationEntry, Boolean> {
    final Button cellButton = new Button();
    
    final TableView<MatchConfigurationEntry> tableView;
    
    GoRefreshMatchConfiguration(final TableView<MatchConfigurationEntry> tableView) {
      this.tableView = tableView;
      Tooltip t = new Tooltip(MatchConfigurationManagementController.this.getMessage("message.tooltip.refreshMatchFromOVR"));
      Tooltip.install((Node)this.cellButton, t);
      this.cellButton.getStyleClass().addAll((Object[])new String[] { "button-image-refresh" });
      this.cellButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
              int selectedIndex = MatchConfigurationManagementController.GoRefreshMatchConfiguration.this.getTableRow().getIndex();
              MatchConfigurationEntry item = (MatchConfigurationEntry)tableView.getItems().get(selectedIndex);
              if (item != null) {
                final String matchId = item.getVmMatchInternalId();
                TkStrikeExecutors.executeInThreadPool(new Runnable() {
                      public void run() {
                        MatchConfigurationManagementController.this.showTableProgressIndicator(true);
                        try {
                          ExternalConfigEntry externalConfigEntry = MatchConfigurationManagementController.this.externalConfigService.getExternalConfigEntry();
                          if (externalConfigEntry != null) {
                            MatchConfigurationManagementController.logger.info("External Config entry OK");
                            MatchConfigurationDto match = MatchConfigurationManagementController.this.wtOvrClientService.getWtOvrMatch(externalConfigEntry.getWtOvrUrl(), externalConfigEntry.getWtOvrXApiKey(), matchId);
                            if (match != null) {
                              MatchConfigurationManagementController.logger.info("Anem a processar match " + match.getInternalId());
                              MatchConfigurationEntry newMatchConfigurationEntry = (MatchConfigurationEntry)MatchConfigurationManagementController.this.matchConfigurationService.transformByDto(match, true);
                              MatchConfigurationManagementController.logger.info("Hem transformat i desat a la base de dades el match " + newMatchConfigurationEntry.getId());
                            } 
                          } 
                        } catch (Exception e) {
                          MatchConfigurationManagementController.this.showTableProgressIndicator(false);
                          MatchConfigurationManagementController.this.manageException(e, "RequestMatch to WT OVR", e.getMessage());
                        } finally {
                          MatchConfigurationManagementController.this.showTableProgressIndicator(false);
                          MatchConfigurationManagementController.this.refreshTable();
                        } 
                      }
                    });
              } 
            }
          });
    }
    
    protected void updateItem(Boolean t, boolean empty) {
      Button button;
      super.updateItem(t, empty);
      Node graphic = null;
      if (!empty) {
        int selectedIndex = getTableRow().getIndex();
        MatchConfigurationEntry item = (MatchConfigurationEntry)this.tableView.getItems().get(selectedIndex);
        if (item != null && item.getWtCompetitionDataProtocol().booleanValue())
          button = this.cellButton; 
      } 
      setGraphic((Node)button);
    }
  }
  
  private class IsMatchStartedTableCell extends TableCell<MatchConfigurationEntry, Boolean> {
    final Circle circle = new Circle(10.0D);
    
    final TableView<MatchConfigurationEntry> tableView;
    
    IsMatchStartedTableCell(TableView<MatchConfigurationEntry> tableView) {
      this.tableView = tableView;
    }
    
    protected void updateItem(Boolean t, boolean empty) {
      Circle circle;
      super.updateItem(t, empty);
      Node graphic = null;
      if (!empty) {
        int selectedIndex = getTableRow().getIndex();
        MatchConfigurationEntry item = (MatchConfigurationEntry)this.tableView.getItems().get(selectedIndex);
        if (item != null) {
          if (item.isMatchStarted()) {
            this.circle.setFill((Paint)Color.GREEN);
          } else {
            this.circle.setFill((Paint)Color.RED);
          } 
          circle = this.circle;
        } 
      } 
      setGraphic((Node)circle);
    }
  }
  
  public enum OpenType {
    WT_OVR_MATCH_REQUEST, PREVIOUS_MATCHES, NORMAL;
  }
}
