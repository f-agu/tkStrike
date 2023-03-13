package com.xtremis.daedo.tkstrike.ui.controller;

import com.xtremis.daedo.tkstrike.om.CommonMatchLogItemDto;
import com.xtremis.daedo.tkstrike.om.MatchLogDto;
import com.xtremis.daedo.tkstrike.om.MatchLogItemDto;
import com.xtremis.daedo.tkstrike.orm.model.MatchLogItemType;
import com.xtremis.daedo.tkstrike.service.MatchLogHistoricalService;
import com.xtremis.daedo.tkstrike.service.MatchLogService;
import com.xtremis.daedo.tkstrike.service.MatchWorker;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchVictoryCriteria;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.utils.TimestampToStringConverter;
import com.xtremis.daedo.tkstrike.utils.TkStrikeUIUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MatchLogViewerController extends TkStrikeBaseController {
  @FXML
  private Pane rootView;
  
  @FXML
  private Pane pnMain;
  
  @FXML
  private ProgressIndicator pi;
  
  @FXML
  private Label lblMatchNumberInfo;
  
  @FXML
  private Label lblPhaseInfo;
  
  @FXML
  private Label lblCategory;
  
  @FXML
  private Label lblMaxGamJeomsAllowed;
  
  @FXML
  private Label lblBlueInfo;
  
  @FXML
  private ImageView ivBlueInfo;
  
  @FXML
  private Label lblRedInfo;
  
  @FXML
  private ImageView ivRedInfo;
  
  @FXML
  private Label lblMinBodyLevel;
  
  @FXML
  private Label lblMinHeadLevel;
  
  @FXML
  private Label lblJudgesNumber;
  
  @FXML
  private Label lblStartTime;
  
  @FXML
  private Label lblEndTime;
  
  private MatchVictoryCriteria matchVictoryCriteria;
  
  @FXML
  private TableView<CommonMatchLogItemDto> tvMatchLog;
  
  @FXML
  private TableColumn<CommonMatchLogItemDto, String> colId;
  
  @FXML
  private TableColumn<CommonMatchLogItemDto, String> colRoundNumber;
  
  @FXML
  private TableColumn<String, Long> colRoundTime;
  
  @FXML
  private TableColumn<String, Long> colSystemTime;
  
  @FXML
  private TableColumn<String, MatchLogItemType> colEventType;
  
  @FXML
  private TableColumn<CommonMatchLogItemDto, Pane> colBlueJ1;
  
  @FXML
  private TableColumn<CommonMatchLogItemDto, Pane> colBlueJ2;
  
  @FXML
  private TableColumn<CommonMatchLogItemDto, Pane> colBlueJ3;
  
  @FXML
  private TableColumn<CommonMatchLogItemDto, Pane> colBlueHit;
  
  @FXML
  private TableColumn<CommonMatchLogItemDto, Pane> colBlueAddPoints;
  
  @FXML
  private TableColumn<CommonMatchLogItemDto, Pane> colRedJ1;
  
  @FXML
  private TableColumn<CommonMatchLogItemDto, Pane> colRedJ2;
  
  @FXML
  private TableColumn<CommonMatchLogItemDto, Pane> colRedJ3;
  
  @FXML
  private TableColumn<CommonMatchLogItemDto, Pane> colRedHit;
  
  @FXML
  private TableColumn<CommonMatchLogItemDto, Pane> colRedAddPoints;
  
  @FXML
  private TableColumn<CommonMatchLogItemDto, String> colEntryValue;
  
  @FXML
  private TableColumn<CommonMatchLogItemDto, String> colScore;
  
  private ObservableList<CommonMatchLogItemDto> matchLogItemEntries = FXCollections.observableArrayList();
  
  @Autowired
  private MatchWorker matchWorker;
  
  @Autowired
  private MatchLogService matchLogService;
  
  @Autowired
  private MatchLogHistoricalService matchLogHistoricalService;
  
  private String matchLogId = null;
  
  private Boolean isHistorical = Boolean.FALSE;
  
  private final FileChooser fileChooser = new FileChooser();
  
  private final DirectoryChooser directoryChooser = new DirectoryChooser();
  
  private final SimpleDateFormat sdfFullDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");
  
  private final SimpleDateFormat sdfMatchTim = new SimpleDateFormat("mm:ss:SSS");
  
  public Node getRootView() {
    return (Node)this.rootView;
  }
  
  public EventHandler<WindowEvent> getOnWindowCloseEventHandler() {
    return new EventHandler<WindowEvent>() {
        public void handle(WindowEvent windowEvent) {
          MatchLogViewerController.this.isHistorical = Boolean.FALSE;
          MatchLogViewerController.this.matchLogId = null;
          MatchLogViewerController.this.matchLogItemEntries.clear();
          System.gc();
        }
      };
  }
  
  public Boolean getIsHistorical() {
    return this.isHistorical;
  }
  
  public void setIsHistorical(Boolean isHistorical) {
    this.isHistorical = isHistorical;
  }
  
  public void setMatchLogId(String matchLogId) {
    this.matchLogId = matchLogId;
  }
  
  public void onWindowShowEvent() {
    super.onWindowShowEvent();
    refreshTable();
  }
  
  protected void refreshTable() {
    Platform.runLater(new Runnable() {
          public void run() {
            MatchLogViewerController.this.pnMain.setVisible(false);
            MatchLogViewerController.this.pi.setVisible(true);
          }
        });
    TkStrikeExecutors.executeInThreadPool(new Runnable() {
          public void run() {
            try {
              if (MatchLogViewerController.this.matchLogId == null)
                MatchLogViewerController.this.matchLogId = MatchLogViewerController.this.matchWorker.getMatchLogId(); 
              TkStrikeBaseController.logger.info("MatchLogViewerController - matchLogId ->" + MatchLogViewerController.this.matchLogId + " isHistorical?" + MatchLogViewerController.this.isHistorical);
              MatchLogDto matchLog = null;
              if (!MatchLogViewerController.this.isHistorical.booleanValue()) {
                matchLog = (MatchLogDto)MatchLogViewerController.this.matchLogService.getById(MatchLogViewerController.this.matchLogId);
              } else {
                matchLog = (MatchLogDto)MatchLogViewerController.this.matchLogHistoricalService.getMatchLog(MatchLogViewerController.this.matchLogId);
              } 
              if (matchLog != null) {
                MatchLogViewerController.this.updateMatchLogInfo(matchLog);
                List<MatchLogItemDto> matchLogItemEntries1 = null;
                if (!MatchLogViewerController.this.isHistorical.booleanValue()) {
                  matchLogItemEntries1 = MatchLogViewerController.this.matchLogService.findByMatchLogId(matchLog.getId());
                } else {
                  matchLogItemEntries1 = MatchLogViewerController.this.matchLogHistoricalService.findItemsByMatchLogId(matchLog.getId());
                } 
                matchLogItemEntries1.sort(MatchLogViewerController.this.matchLogService.getComparator4Items());
                MatchLogViewerController.this.matchLogItemEntries.clear();
                MatchLogViewerController.this.matchLogItemEntries.addAll(matchLogItemEntries1);
              } 
            } catch (Exception e) {
              MatchLogViewerController.this.manageException(e, "matchLog.refreshTable", null);
            } finally {
              Platform.runLater(new Runnable() {
                    public void run() {
                      MatchLogViewerController.this.pi.setVisible(false);
                      MatchLogViewerController.this.pnMain.setVisible(true);
                    }
                  });
            } 
          }
        });
  }
  
  private void updateMatchLogInfo(final MatchLogDto matchLog) {
    if (matchLog != null)
      Platform.runLater(new Runnable() {
            public void run() {
              try {
                MatchLogViewerController.this.matchVictoryCriteria = matchLog.getMatchVictoryCriteria();
                MatchLogViewerController.this.lblMatchNumberInfo.setText(matchLog.getMatchNumber());
                MatchLogViewerController.this.lblPhaseInfo.setText(MatchLogViewerController.this.getMessage("message.phaseWithDiffScore", new String[] { this.val$matchLog.getPhaseName(), "" + this.val$matchLog.getDifferencialScore() }));
                MatchLogViewerController.this.lblCategory.setText(matchLog.getSubCategoryName() + " " + matchLog
                    .getCategoryGender().toString() + " " + matchLog
                    .getCategoryName());
                MatchLogViewerController.this.lblMaxGamJeomsAllowed.setText((matchLog.getMaxAllowedGamJeoms() != null && matchLog.getMaxAllowedGamJeoms().intValue() > 0) ? ("" + matchLog.getMaxAllowedGamJeoms()) : "-");
                MatchLogViewerController.this.lblBlueInfo.setText(matchLog.getBlueAthleteName() + " - " + (
                    (matchLog.getBlueAthleteFlagAbbreviation() != null) ? ((matchLog.getBlueAthleteFlagShowName() != null && matchLog.getBlueAthleteFlagShowName().booleanValue()) ? matchLog.getBlueAthleteFlagName() : matchLog.getBlueAthleteFlagAbbreviation()) : ""));
                MatchLogViewerController.this.ivBlueInfo.setImage(MatchLogViewerController.this._getImageByPath(matchLog.getBlueAthleteFlagImagePath()));
                MatchLogViewerController.this.ivBlueInfo.setFitHeight(25.0D);
                MatchLogViewerController.this.ivBlueInfo.setPreserveRatio(true);
                MatchLogViewerController.this.lblRedInfo.setText(matchLog.getRedAthleteName() + " - " + (
                    (matchLog.getRedAthleteFlagAbbreviation() != null) ? ((matchLog.getRedAthleteFlagShowName() != null && matchLog.getRedAthleteFlagShowName().booleanValue()) ? matchLog.getRedAthleteFlagName() : matchLog.getRedAthleteFlagAbbreviation()) : ""));
                MatchLogViewerController.this.ivRedInfo.setImage(MatchLogViewerController.this._getImageByPath(matchLog.getRedAthleteFlagImagePath()));
                MatchLogViewerController.this.ivRedInfo.setFitHeight(25.0D);
                MatchLogViewerController.this.ivRedInfo.setPreserveRatio(true);
                MatchLogViewerController.this.lblJudgesNumber.setText("" + matchLog.getNumberOfJudges());
                MatchLogViewerController.this.lblMinBodyLevel.setText("" + matchLog.getMinBodyLevel());
                MatchLogViewerController.this.lblMinHeadLevel.setText("" + matchLog.getMinHeadLevel());
                if (matchLog.getMatchStartTime() != null && matchLog.getMatchStartTime().longValue() > 0L) {
                  MatchLogViewerController.this.lblStartTime.setText(MatchLogViewerController.this.getDfFullFormat().format(new Date(matchLog.getMatchStartTime().longValue())));
                } else {
                  MatchLogViewerController.this.lblStartTime.setText("");
                } 
                if (matchLog.getMatchEndTime() != null && matchLog.getMatchEndTime().longValue() > 0L) {
                  MatchLogViewerController.this.lblEndTime.setText(MatchLogViewerController.this.getDfFullFormat().format(new Date(matchLog.getMatchEndTime().longValue())));
                } else {
                  MatchLogViewerController.this.lblEndTime.setText("");
                } 
              } catch (RuntimeException e) {
                MatchLogViewerController.this.manageException(e, "Updating MatchLogInfo UI", null);
              } 
            }
          }); 
  }
  
  public void cancel() {
    doCloseThisStage();
  }
  
  public void doExportToCSV() {
    this.directoryChooser.setTitle(getMessage("title.selectMatchLogTargetDirectory"));
    this.directoryChooser.setInitialDirectory(new File("/"));
    File targetDirectory = this.directoryChooser.showDialog((Window)getCurrentStage());
    if (targetDirectory != null)
      try {
        if (this.isHistorical.booleanValue()) {
          this.matchLogHistoricalService.exportMatchLog(this.matchLogId, targetDirectory);
        } else {
          this.matchLogService.exportMatchLog(this.matchLogId, targetDirectory);
        } 
      } catch (TkStrikeServiceException e) {
        manageException((Throwable)e, "doExportToCSV", null);
      }  
  }
  
  public void initialize(URL url, ResourceBundle resourceBundle) {}
  
  private Image _getImageByPath(String imagePath) {
    Image image = null;
    if (StringUtils.isNotBlank(imagePath)) {
      byte[] imageBytes = null;
      try {
        imageBytes = FileUtils.readFileToByteArray(new File(imagePath));
      } catch (IOException e) {
        e.printStackTrace();
      } 
      if (imageBytes != null)
        image = new Image(new ByteArrayInputStream(imageBytes)); 
    } 
    return image;
  }
  
  public void afterPropertiesSet() throws Exception {
    this.tvMatchLog.setItems(this.matchLogItemEntries);
    this.colId.setCellValueFactory((Callback)new PropertyValueFactory("id"));
    this.colRoundNumber.setCellValueFactory((Callback)new PropertyValueFactory("roundNumberStr"));
    this.colRoundTime.setCellValueFactory((Callback)new PropertyValueFactory("roundTime"));
    this.colRoundTime.setCellFactory(TextFieldTableCell.forTableColumn((StringConverter)new TimestampToStringConverter("mm:ss:SSS")));
    this.colSystemTime.setCellValueFactory((Callback)new PropertyValueFactory("eventTime"));
    this.colSystemTime.setCellFactory(TextFieldTableCell.forTableColumn((StringConverter)new TimestampToStringConverter(this.dfFullFormatPattern)));
    this.colEventType.setCellValueFactory((Callback)new PropertyValueFactory("matchLogItemType"));
    this.colEventType.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<MatchLogItemType>() {
            public String toString(MatchLogItemType matchLogItemType) {
              return matchLogItemType.toString();
            }
            
            public MatchLogItemType fromString(String s) {
              return MatchLogItemType.valueOf(s);
            }
          }));
    this.colEntryValue.setCellValueFactory((Callback)new PropertyValueFactory("entryValue"));
    this.colBlueJ1.setCellValueFactory(new SensorHitCellValuePropertyFactory(Boolean.valueOf(true), Boolean.valueOf(true), Integer.valueOf(1)));
    this.colBlueJ1.setCellFactory(new SensorHitCellFactory());
    this.colBlueJ2.setCellValueFactory(new SensorHitCellValuePropertyFactory(Boolean.valueOf(true), Boolean.valueOf(true), Integer.valueOf(2)));
    this.colBlueJ2.setCellFactory(new SensorHitCellFactory());
    this.colBlueJ3.setCellValueFactory(new SensorHitCellValuePropertyFactory(Boolean.valueOf(true), Boolean.valueOf(true), Integer.valueOf(3)));
    this.colBlueJ3.setCellFactory(new SensorHitCellFactory());
    this.colBlueHit.setCellValueFactory(new SensorHitCellValuePropertyFactory(Boolean.valueOf(true), Boolean.valueOf(false)));
    this.colBlueHit.setCellFactory(new SensorHitCellFactory());
    this.colBlueAddPoints.setCellValueFactory(new PointsCellValuePropertyFactory(Boolean.valueOf(true)));
    this.colRedJ1.setCellValueFactory(new SensorHitCellValuePropertyFactory(Boolean.valueOf(false), Boolean.valueOf(true), Integer.valueOf(1)));
    this.colRedJ1.setCellFactory(new SensorHitCellFactory());
    this.colRedJ2.setCellValueFactory(new SensorHitCellValuePropertyFactory(Boolean.valueOf(false), Boolean.valueOf(true), Integer.valueOf(2)));
    this.colRedJ2.setCellFactory(new SensorHitCellFactory());
    this.colRedJ3.setCellValueFactory(new SensorHitCellValuePropertyFactory(Boolean.valueOf(false), Boolean.valueOf(true), Integer.valueOf(3)));
    this.colRedJ3.setCellFactory(new SensorHitCellFactory());
    this.colRedHit.setCellValueFactory(new SensorHitCellValuePropertyFactory(Boolean.valueOf(false), Boolean.valueOf(false)));
    this.colRedHit.setCellFactory(new SensorHitCellFactory());
    this.colRedAddPoints.setCellValueFactory(new PointsCellValuePropertyFactory(Boolean.valueOf(false)));
    this.colScore.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CommonMatchLogItemDto, String>, ObservableValue<String>>() {
          public ObservableValue<String> call(TableColumn.CellDataFeatures<CommonMatchLogItemDto, String> matchLogItemEntryStringCellDataFeatures) {
            SimpleStringProperty res = new SimpleStringProperty();
            CommonMatchLogItemDto matchLogItemEntry = (CommonMatchLogItemDto)matchLogItemEntryStringCellDataFeatures.getValue();
            if (matchLogItemEntry != null)
              res.set(matchLogItemEntry.getBlueGeneralPoints() + "-" + matchLogItemEntry.getRedGeneralPoints()); 
            return (ObservableValue<String>)res;
          }
        });
  }
  
  private class SensorHitCellValuePropertyFactory implements Callback<TableColumn.CellDataFeatures<CommonMatchLogItemDto, Pane>, ObservableValue<Pane>> {
    private final Boolean blue;
    
    private final Boolean judge;
    
    private final Integer judgeNumber;
    
    public SensorHitCellValuePropertyFactory(Boolean blue, Boolean judge) {
      this.blue = blue;
      this.judge = judge;
      this.judgeNumber = null;
    }
    
    public SensorHitCellValuePropertyFactory(Boolean blue, Boolean judge, Integer judgeNumber) {
      this.blue = blue;
      this.judge = judge;
      this.judgeNumber = judgeNumber;
    }
    
    public ObservableValue<Pane> call(TableColumn.CellDataFeatures<CommonMatchLogItemDto, Pane> param) {
      SimpleObjectProperty<Pane> res = new SimpleObjectProperty();
      StackPane container = new StackPane();
      container = new StackPane();
      container.setPrefWidth(30.0D);
      container.setMinWidth(30.0D);
      container.setMaxWidth(30.0D);
      container.setPrefHeight(30.0D);
      container.setMinHeight(30.0D);
      container.setMaxHeight(30.0D);
      container.setAlignment(Pos.CENTER);
      CommonMatchLogItemDto matchLogItemEntry = (CommonMatchLogItemDto)param.getValue();
      if (matchLogItemEntry != null && 
        validForMatchLogItemEntry(matchLogItemEntry).booleanValue())
        if (!this.judge.booleanValue() && (MatchLogItemType.BLUE_VIDEO_REQUEST.equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_VIDEO_QUOTA_CHANGED
          .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_VIDEO_REQUEST
          .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_VIDEO_QUOTA_CHANGED
          .equals(matchLogItemEntry.getMatchLogItemType()))) {
          if ((this.blue.booleanValue() && MatchLogItemType.BLUE_VIDEO_REQUEST.equals(matchLogItemEntry.getMatchLogItemType())) || (
            !this.blue.booleanValue() && MatchLogItemType.RED_VIDEO_REQUEST.equals(matchLogItemEntry.getMatchLogItemType()))) {
            Rectangle rectangle = new Rectangle();
            rectangle.setWidth(30.0D);
            rectangle.setHeight(30.0D);
            rectangle.setFill((Paint)Color.DARKGRAY);
            container.getChildren().add(rectangle);
            ImageView iv = new ImageView();
            iv.setImage(new Image(getClass().getResourceAsStream("/images/ico-video.png")));
            iv.setFitWidth(30.0D);
            iv.setPreserveRatio(true);
            container.getChildren().add(iv);
            Text text = new Text("?");
            text.getStyleClass().addAll((Object[])new String[] { "matchLogVideoRequestCellText" });
            text.setFill(this.blue.booleanValue() ? (Paint)Color.BLUE : (Paint)Color.RED);
            container.getChildren().add(text);
          } else if ((this.blue.booleanValue() && MatchLogItemType.BLUE_VIDEO_QUOTA_CHANGED.equals(matchLogItemEntry.getMatchLogItemType())) || (
            !this.blue.booleanValue() && MatchLogItemType.RED_VIDEO_QUOTA_CHANGED.equals(matchLogItemEntry.getMatchLogItemType()))) {
            Rectangle rectangle = new Rectangle();
            rectangle.setWidth(30.0D);
            rectangle.setHeight(30.0D);
            rectangle.setFill((Paint)Color.DARKGRAY);
            container.getChildren().add(rectangle);
            ImageView iv = new ImageView();
            iv.setImage(new Image(getClass().getResourceAsStream("/images/ico-novideo.png")));
            iv.setFitWidth(30.0D);
            iv.setPreserveRatio(true);
            container.getChildren().add(iv);
          } 
        } else {
          Shape shape = getShapeForItemType(matchLogItemEntry.getMatchLogItemType());
          if (shape != null) {
            shape.setFill(getShapePaintForItemType(matchLogItemEntry.getMatchLogItemType()));
            container.getChildren().add(shape);
            Text text = getTextForMatchIntemEntry(matchLogItemEntry);
            if (text != null)
              container.getChildren().add(text); 
          } 
        }  
      res.set(container);
      return (ObservableValue<Pane>)res;
    }
    
    private Boolean validForMatchLogItemEntry(CommonMatchLogItemDto matchLogItemEntry) {
      Boolean res = Boolean.FALSE;
      if (matchLogItemEntry != null && (
        MatchLogItemType.BLUE_BODY_HIT.equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_BLUE_BODY_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_HEAD_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_BLUE_HEAD_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_ADD_KYONG_GO
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_ADD_GAME_JEON
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_REMOVE_KYONG_GO
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_REMOVE_GAME_JEON
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_JUDGE_PUNCH
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_BLUE_JUDGE_PUNCH
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_JUDGE_BODY_TECH
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_BLUE_JUDGE_BODY_TECH
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_JUDGE_HEAD_TECH
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_BLUE_JUDGE_HEAD_TECH
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_JUDGE_SPECIAL_HEAD_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_BLUE_JUDGE_SPECIAL_HEAD_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_JUDGE_SPECIAL_BODY_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_BLUE_JUDGE_SPECIAL_BODY_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_JUDGE_SPECIAL_HEAD_TECH
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_BLUE_JUDGE_SPECIAL_HEAD_TECH
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_VIDEO_REQUEST
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_VIDEO_QUOTA_CHANGED
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_BODY_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_RED_BODY_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_HEAD_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_RED_HEAD_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_ADD_KYONG_GO
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_ADD_GAME_JEON
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_REMOVE_KYONG_GO
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_REMOVE_GAME_JEON
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_JUDGE_PUNCH
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_RED_JUDGE_PUNCH
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_JUDGE_BODY_TECH
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_RED_JUDGE_BODY_TECH
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_JUDGE_HEAD_TECH
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_RED_JUDGE_HEAD_TECH
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_JUDGE_SPECIAL_HEAD_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_RED_JUDGE_SPECIAL_HEAD_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_JUDGE_SPECIAL_BODY_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_RED_JUDGE_SPECIAL_BODY_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_JUDGE_SPECIAL_HEAD_TECH
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_RED_JUDGE_SPECIAL_HEAD_TECH
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_REMOVE_NEAR_MISS_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_REMOVE_NEAR_MISS_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_ADD_NEAR_MISS_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_ADD_NEAR_MISS_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_VIDEO_REQUEST
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_VIDEO_QUOTA_CHANGED
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_PARA_TURNING_KICK
        
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_PARA_TURNING_KICK
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_BLUE_PARA_TURNING_KICK
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_RED_PARA_TURNING_KICK
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_PARA_SPINNING_KICK
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_PARA_SPINNING_KICK
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_BLUE_PARA_SPINNING_KICK
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_RED_PARA_SPINNING_KICK
        .equals(matchLogItemEntry.getMatchLogItemType())))
        if (this.blue.booleanValue()) {
          if (this.judge.booleanValue()) {
            if (StringUtils.isNumeric(matchLogItemEntry.getEntryValue()) && this.judgeNumber
              .intValue() == Integer.parseInt(matchLogItemEntry.getEntryValue()) && (MatchLogItemType.BLUE_JUDGE_PUNCH
              .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_JUDGE_BODY_TECH
              .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_JUDGE_HEAD_TECH
              .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_JUDGE_SPECIAL_HEAD_HIT
              .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_JUDGE_SPECIAL_BODY_HIT
              .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_JUDGE_SPECIAL_HEAD_TECH
              .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_BLUE_JUDGE_PUNCH
              .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_BLUE_JUDGE_BODY_TECH
              .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_BLUE_JUDGE_HEAD_TECH
              .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_BLUE_JUDGE_SPECIAL_HEAD_HIT
              .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_BLUE_JUDGE_SPECIAL_BODY_HIT
              .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_BLUE_JUDGE_SPECIAL_HEAD_TECH
              .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_PARA_TURNING_KICK
              
              .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_BLUE_PARA_TURNING_KICK
              .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_PARA_SPINNING_KICK
              .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_BLUE_PARA_SPINNING_KICK
              .equals(matchLogItemEntry.getMatchLogItemType())))
              res = Boolean.TRUE; 
          } else if (MatchLogItemType.BLUE_BODY_HIT.equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_HEAD_HIT
            .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_BLUE_BODY_HIT
            .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_BLUE_HEAD_HIT
            .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_ADD_KYONG_GO
            .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_ADD_GAME_JEON
            .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_REMOVE_KYONG_GO
            .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_REMOVE_GAME_JEON
            .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_REMOVE_NEAR_MISS_HIT
            .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_ADD_NEAR_MISS_HIT
            .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_VIDEO_REQUEST
            .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_VIDEO_QUOTA_CHANGED
            .equals(matchLogItemEntry.getMatchLogItemType())) {
            res = Boolean.TRUE;
          } 
        } else if (this.judge.booleanValue()) {
          if (StringUtils.isNumeric(matchLogItemEntry.getEntryValue()) && this.judgeNumber
            .intValue() == Integer.parseInt(matchLogItemEntry.getEntryValue()) && (MatchLogItemType.RED_JUDGE_PUNCH
            .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_JUDGE_BODY_TECH
            .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_JUDGE_HEAD_TECH
            .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_JUDGE_SPECIAL_HEAD_HIT
            .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_JUDGE_SPECIAL_BODY_HIT
            .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_JUDGE_SPECIAL_HEAD_TECH
            .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_RED_JUDGE_PUNCH
            .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_RED_JUDGE_BODY_TECH
            .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_RED_JUDGE_HEAD_TECH
            .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_RED_JUDGE_SPECIAL_HEAD_HIT
            .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_RED_JUDGE_SPECIAL_BODY_HIT
            .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_RED_JUDGE_SPECIAL_HEAD_TECH
            .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_PARA_TURNING_KICK
            
            .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_RED_PARA_TURNING_KICK
            .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_PARA_SPINNING_KICK
            .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_RED_PARA_SPINNING_KICK
            .equals(matchLogItemEntry.getMatchLogItemType())))
            res = Boolean.TRUE; 
        } else if (MatchLogItemType.RED_BODY_HIT.equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_HEAD_HIT
          .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_RED_BODY_HIT
          .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_RED_HEAD_HIT
          .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_ADD_KYONG_GO
          .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_ADD_GAME_JEON
          .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_REMOVE_KYONG_GO
          .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_REMOVE_GAME_JEON
          .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_REMOVE_NEAR_MISS_HIT
          .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_ADD_NEAR_MISS_HIT
          .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_VIDEO_REQUEST
          .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_VIDEO_QUOTA_CHANGED
          .equals(matchLogItemEntry.getMatchLogItemType())) {
          res = Boolean.TRUE;
        }  
      return res;
    }
    
    private Shape getShapeForItemType(MatchLogItemType matchLogItemType) {
      Rectangle rectangle1;
      Ellipse ellipse1;
      Polygon polygon1;
      Rectangle recHit;
      Ellipse eliHit;
      Polygon polygon;
      Shape res = null;
      switch (matchLogItemType) {
        case BLUE_BODY_HIT:
        case RED_BODY_HIT:
        case BLUE_JUDGE_BODY_TECH:
        case RED_JUDGE_BODY_TECH:
        case OT_BLUE_BODY_HIT:
        case OT_RED_BODY_HIT:
        case OT_BLUE_JUDGE_BODY_TECH:
        case OT_RED_JUDGE_BODY_TECH:
        case BLUE_REMOVE_NEAR_MISS_HIT:
        case RED_REMOVE_NEAR_MISS_HIT:
        case BLUE_ADD_NEAR_MISS_HIT:
        case RED_ADD_NEAR_MISS_HIT:
        case BLUE_HEAD_HIT:
        case BLUE_JUDGE_HEAD_TECH:
        case BLUE_JUDGE_SPECIAL_HEAD_HIT:
        case BLUE_JUDGE_SPECIAL_BODY_HIT:
        case BLUE_JUDGE_SPECIAL_HEAD_TECH:
        case RED_HEAD_HIT:
        case RED_JUDGE_HEAD_TECH:
        case RED_JUDGE_SPECIAL_HEAD_HIT:
        case RED_JUDGE_SPECIAL_BODY_HIT:
        case RED_JUDGE_SPECIAL_HEAD_TECH:
        case OT_BLUE_HEAD_HIT:
        case OT_BLUE_JUDGE_HEAD_TECH:
        case OT_BLUE_JUDGE_SPECIAL_HEAD_HIT:
        case OT_BLUE_JUDGE_SPECIAL_BODY_HIT:
        case OT_BLUE_JUDGE_SPECIAL_HEAD_TECH:
        case OT_RED_HEAD_HIT:
        case OT_RED_JUDGE_HEAD_TECH:
        case OT_RED_JUDGE_SPECIAL_HEAD_HIT:
        case OT_RED_JUDGE_SPECIAL_BODY_HIT:
        case OT_RED_JUDGE_SPECIAL_HEAD_TECH:
        case BLUE_PARA_TURNING_KICK:
        case RED_PARA_TURNING_KICK:
        case OT_BLUE_PARA_TURNING_KICK:
        case OT_RED_PARA_TURNING_KICK:
          recHit = new Rectangle();
          recHit.setWidth(30.0D);
          recHit.setHeight(30.0D);
          rectangle1 = recHit;
          break;
        case BLUE_JUDGE_PUNCH:
        case RED_JUDGE_PUNCH:
        case OT_BLUE_JUDGE_PUNCH:
        case OT_RED_JUDGE_PUNCH:
        case BLUE_ADD_KYONG_GO:
        case BLUE_ADD_GAME_JEON:
        case BLUE_REMOVE_KYONG_GO:
        case BLUE_REMOVE_GAME_JEON:
        case RED_ADD_KYONG_GO:
        case RED_ADD_GAME_JEON:
        case RED_REMOVE_KYONG_GO:
        case RED_REMOVE_GAME_JEON:
          eliHit = new Ellipse();
          eliHit.setRadiusX(15.0D);
          eliHit.setRadiusY(15.0D);
          ellipse1 = eliHit;
          break;
        case BLUE_PARA_SPINNING_KICK:
        case RED_PARA_SPINNING_KICK:
        case OT_BLUE_PARA_SPINNING_KICK:
        case OT_RED_PARA_SPINNING_KICK:
          polygon = new Polygon();
          polygon.getPoints().addAll((Object[])new Double[] { Double.valueOf(-15.0D), Double.valueOf(15.0D), 
                Double.valueOf(15.0D), Double.valueOf(15.0D), 
                Double.valueOf(0.0D), Double.valueOf(-15.0D) });
          polygon1 = polygon;
          break;
      } 
      return (Shape)polygon1;
    }
    
    private Paint getShapePaintForItemType(MatchLogItemType matchLogItemType) {
      Color color;
      Paint res = null;
      switch (matchLogItemType) {
        case BLUE_JUDGE_PUNCH:
        case RED_JUDGE_PUNCH:
        case OT_BLUE_JUDGE_PUNCH:
        case OT_RED_JUDGE_PUNCH:
          color = Color.web("#eaff00");
          break;
        case BLUE_BODY_HIT:
        case RED_BODY_HIT:
        case BLUE_JUDGE_BODY_TECH:
        case RED_JUDGE_BODY_TECH:
        case OT_BLUE_BODY_HIT:
        case OT_RED_BODY_HIT:
        case OT_BLUE_JUDGE_BODY_TECH:
        case OT_RED_JUDGE_BODY_TECH:
        case BLUE_PARA_TURNING_KICK:
        case RED_PARA_TURNING_KICK:
        case OT_BLUE_PARA_TURNING_KICK:
        case OT_RED_PARA_TURNING_KICK:
          color = Color.web("#00ff00");
          break;
        case BLUE_PARA_SPINNING_KICK:
        case RED_PARA_SPINNING_KICK:
        case OT_BLUE_PARA_SPINNING_KICK:
        case OT_RED_PARA_SPINNING_KICK:
          color = Color.web("#eee33f");
          break;
        case BLUE_HEAD_HIT:
        case BLUE_JUDGE_HEAD_TECH:
        case BLUE_JUDGE_SPECIAL_HEAD_HIT:
        case BLUE_JUDGE_SPECIAL_BODY_HIT:
        case RED_HEAD_HIT:
        case RED_JUDGE_HEAD_TECH:
        case RED_JUDGE_SPECIAL_HEAD_HIT:
        case RED_JUDGE_SPECIAL_BODY_HIT:
        case OT_BLUE_HEAD_HIT:
        case OT_BLUE_JUDGE_HEAD_TECH:
        case OT_BLUE_JUDGE_SPECIAL_HEAD_HIT:
        case OT_BLUE_JUDGE_SPECIAL_BODY_HIT:
        case OT_RED_HEAD_HIT:
        case OT_RED_JUDGE_HEAD_TECH:
        case OT_RED_JUDGE_SPECIAL_HEAD_HIT:
        case OT_RED_JUDGE_SPECIAL_BODY_HIT:
          color = Color.web("#00ff00");
          break;
        case BLUE_JUDGE_SPECIAL_HEAD_TECH:
        case RED_JUDGE_SPECIAL_HEAD_TECH:
        case OT_BLUE_JUDGE_SPECIAL_HEAD_TECH:
        case OT_RED_JUDGE_SPECIAL_HEAD_TECH:
          color = Color.web("#bf12f6");
          break;
        case BLUE_ADD_KYONG_GO:
        case BLUE_REMOVE_KYONG_GO:
        case RED_ADD_KYONG_GO:
        case RED_REMOVE_KYONG_GO:
          color = TkStrikeUIUtils.colorKyongGo;
          break;
        case BLUE_REMOVE_NEAR_MISS_HIT:
        case RED_REMOVE_NEAR_MISS_HIT:
        case BLUE_ADD_NEAR_MISS_HIT:
        case RED_ADD_NEAR_MISS_HIT:
        case BLUE_ADD_GAME_JEON:
        case BLUE_REMOVE_GAME_JEON:
        case RED_ADD_GAME_JEON:
        case RED_REMOVE_GAME_JEON:
          color = TkStrikeUIUtils.colorGameJeon;
          break;
      } 
      return (Paint)color;
    }
    
    private Text getTextForMatchIntemEntry(CommonMatchLogItemDto matchLogItemEntry) {
      Text text = null;
      if (MatchLogItemType.BLUE_BODY_HIT.equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_HEAD_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_BODY_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_HEAD_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_BLUE_BODY_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_BLUE_HEAD_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_RED_BODY_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.OT_RED_HEAD_HIT
        .equals(matchLogItemEntry.getMatchLogItemType())) {
        text = new Text(matchLogItemEntry.getEntryValue().equals("100") ? "99" : matchLogItemEntry.getEntryValue());
        text.getStyleClass().addAll((Object[])new String[] { "matchLogHitCellText" });
        text.setFill((Paint)Color.WHITE);
      } else if (MatchLogItemType.BLUE_ADD_KYONG_GO.equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_ADD_GAME_JEON
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_ADD_KYONG_GO
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_ADD_GAME_JEON
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_ADD_NEAR_MISS_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_ADD_NEAR_MISS_HIT
        .equals(matchLogItemEntry.getMatchLogItemType())) {
        text = new Text("+");
        text.getStyleClass().addAll((Object[])new String[] { "matchLogHitCellText" });
        text.setFill((Paint)Color.WHITE);
      } else if (MatchLogItemType.BLUE_REMOVE_KYONG_GO.equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_REMOVE_GAME_JEON
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_REMOVE_KYONG_GO
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_REMOVE_GAME_JEON
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.BLUE_REMOVE_NEAR_MISS_HIT
        .equals(matchLogItemEntry.getMatchLogItemType()) || MatchLogItemType.RED_REMOVE_NEAR_MISS_HIT
        .equals(matchLogItemEntry.getMatchLogItemType())) {
        text = new Text("-");
        text.getStyleClass().addAll((Object[])new String[] { "matchLogHitCellText" });
        text.setFill((Paint)Color.WHITE);
      } 
      return text;
    }
  }
  
  private class PointsCellValuePropertyFactory implements Callback<TableColumn.CellDataFeatures<CommonMatchLogItemDto, Pane>, ObservableValue<Pane>> {
    private final Boolean blue;
    
    public PointsCellValuePropertyFactory(Boolean blue) {
      this.blue = blue;
    }
    
    public ObservableValue<Pane> call(TableColumn.CellDataFeatures<CommonMatchLogItemDto, Pane> param) {
      SimpleObjectProperty<Pane> res = new SimpleObjectProperty();
      StackPane container = new StackPane();
      container = new StackPane();
      container.setPrefWidth(30.0D);
      container.setMinWidth(30.0D);
      container.setMaxWidth(30.0D);
      container.setPrefHeight(30.0D);
      container.setMinHeight(30.0D);
      container.setMaxHeight(30.0D);
      container.setAlignment(Pos.CENTER);
      addChildrensIfNeed((CommonMatchLogItemDto)param.getValue(), container);
      res.set(container);
      return (ObservableValue<Pane>)res;
    }
    
    private void addChildrensIfNeed(CommonMatchLogItemDto matchLogItemEntry, StackPane container) {
      if (matchLogItemEntry != null && ((this.blue
        .booleanValue() && matchLogItemEntry.getBlueAddPoints().intValue() != 0) || (!this.blue.booleanValue() && matchLogItemEntry.getRedAddPoints().intValue() != 0))) {
        Rectangle recPoints = new Rectangle();
        recPoints.setWidth(30.0D);
        recPoints.setHeight(30.0D);
        recPoints.setFill(this.blue.booleanValue() ? (Paint)Color.BLUE : (Paint)Color.RED);
        recPoints.setStroke((Paint)Color.WHITE);
        recPoints.setStrokeWidth(2.0D);
        container.getChildren().add(recPoints);
        Text text = new Text(getStringForAddPoints((this.blue.booleanValue() ? matchLogItemEntry.getBlueAddPoints() : matchLogItemEntry.getRedAddPoints()).intValue()));
        text.getStyleClass().addAll((Object[])new String[] { "matchLogHitCellText" });
        text.setFill((Paint)Color.WHITE);
        container.getChildren().add(text);
      } 
    }
    
    private String getStringForAddPoints(int addPoints) {
      return "" + ((addPoints > 0) ? "+" : "") + addPoints;
    }
  }
  
  private class SensorHitCellFactory implements Callback<TableColumn<CommonMatchLogItemDto, Pane>, TableCell<CommonMatchLogItemDto, Pane>> {
    private SensorHitCellFactory() {}
    
    public TableCell<CommonMatchLogItemDto, Pane> call(TableColumn<CommonMatchLogItemDto, Pane> param) {
      return new TableCell<CommonMatchLogItemDto, Pane>() {
          protected void updateItem(Pane item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null)
              setGraphic((Node)item); 
          }
        };
    }
  }
}
