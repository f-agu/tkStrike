package com.xtremis.daedo.tkstrike.ui.controller;

import com.xtremis.daedo.tkstrike.service.AppStatusWorker;
import com.xtremis.daedo.tkstrike.service.NetworkConfigurationService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.service.TkStrikeStyleSheetsHelper;
import com.xtremis.daedo.tkstrike.ui.model.FormValidationError;
import com.xtremis.daedo.tkstrike.ui.model.INetworkConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.scene.FlagEntry;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import jfxtras.labs.dialogs.MonologFX;
import jfxtras.labs.dialogs.MonologFXButton;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;

public abstract class CommonTkStrikeBaseController<NE extends INetworkConfigurationEntry> implements Initializable, InitializingBean, TkStrikeController {
  protected static final Logger logger = Logger.getLogger(CommonTkStrikeBaseController.class);
  
  protected static final String PATH_VIDEO_YES = "/images/ico-video.png";
  
  protected static final String PATH_VIDEO_NO = "/images/ico-novideo.png";
  
  protected static final Image IMAGE_VIDEO = new Image(CommonRoundFinishedConfirmationController.class.getResourceAsStream("/images/ico-video.png"));
  
  protected static final Image IMAGE_NO_VIDEO = new Image(CommonRoundFinishedConfirmationController.class.getResourceAsStream("/images/ico-novideo.png"));
  
  protected static final String CSS_FORM_ERROR_CLASS = "form-error";
  
  protected static final String CSS_FORM_DEFAULT_CLASS = "form-default";
  
  @FXML
  protected Node rootView;
  
  @Value("${tkStrike.genVersion}")
  protected String tkStrikeGenVersion;
  
  @Value("${tkStrike.wtCompetitionDataProtocol.enabled}")
  protected Boolean wtCompetitionDataProtocol;
  
  @Value("${tkStrike.dfFullFormat.pattern}")
  protected String dfFullFormatPattern;
  
  @Autowired
  @Qualifier("tkStrikeIconApp")
  private String tkStrikeIconApp;
  
  @Autowired
  @Qualifier("tkStrikeMessageSource")
  private MessageSource tkStrikeMessageSource;
  
  @Autowired
  @Qualifier("tkStrikeLanguage")
  private String tkStrikeLanguage;
  
  @Autowired
  private AppStatusWorker appStatusWorker;
  
  @Autowired
  protected TkStrikeStyleSheetsHelper tkStrikeStyleSheetsHelper;
  
  @Autowired
  private NetworkConfigurationService networkConfigurationService;
  
  private String getTkStrikeIconApp() {
    return this.tkStrikeIconApp;
  }
  
  protected final AppStatusWorker getAppStatusWorker() {
    return this.appStatusWorker;
  }
  
  protected boolean isTkStrikeGen2Version() {
    return "gen2".equals(this.tkStrikeGenVersion);
  }
  
  protected NE getCurrentNetworkConfiguration() {
    try {
      return (NE)this.networkConfigurationService.getNetworkConfigurationEntry();
    } catch (TkStrikeServiceException e) {
      manageException((Throwable)e, "TkStrikeBase - getNetworkConfigurationEntry", null);
      return null;
    } 
  }
  
  protected class DefaultAction4Toggle implements EventHandler<ActionEvent> {
    final ToggleButton toggleButton;
    
    public DefaultAction4Toggle(ToggleButton toggleButton) {
      this.toggleButton = toggleButton;
    }
    
    public void handle(ActionEvent actionEvent) {
      if (this.toggleButton.isSelected()) {
        this.toggleButton.setText(CommonTkStrikeBaseController.this.getMessage("toggle.enabled"));
      } else {
        this.toggleButton.setText(CommonTkStrikeBaseController.this.getMessage("toggle.disabled"));
      } 
    }
  }
  
  protected class DefaultChangeListener4Toggle implements ChangeListener<Boolean> {
    final ToggleButton toggleButton;
    
    public DefaultChangeListener4Toggle(ToggleButton toggleButton) {
      this.toggleButton = toggleButton;
    }
    
    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
      if (t1.booleanValue()) {
        this.toggleButton.setText(CommonTkStrikeBaseController.this.getMessage("toggle.enabled"));
      } else {
        this.toggleButton.setText(CommonTkStrikeBaseController.this.getMessage("toggle.disabled"));
      } 
    }
  }
  
  public Stage getCurrentStage() {
    return (Stage)getRootView().getScene().getWindow();
  }
  
  public Node getRootView() {
    return this.rootView;
  }
  
  protected String getMessage(String messageKey) {
    return getMessage(messageKey, null);
  }
  
  protected String getMessage(String messageKey, String... params) {
    return this.tkStrikeMessageSource.getMessage(messageKey, (Object[])params, 
        
        Locale.forLanguageTag(this.tkStrikeLanguage));
  }
  
  protected void openInNewStage(TkStrikeController tkStrikeController) {
    openInNewStage(tkStrikeController, (EventHandler<WindowEvent>)null, (String)null, 500, 500, true);
  }
  
  protected void openInNewStage(TkStrikeController tkStrikeController, EventHandler<WindowEvent> windowCloseEventHandler) {
    openInNewStage(tkStrikeController, windowCloseEventHandler, (String)null, 500, 500, true);
  }
  
  protected void openInNewStage(TkStrikeController tkStrikeController, EventHandler<WindowEvent> windowCloseEventHandler, String stageTitle) {
    openInNewStage(tkStrikeController, windowCloseEventHandler, stageTitle, 500, 500, true);
  }
  
  protected void openInNewStage(TkStrikeController tkStrikeController, String stageTitle) {
    openInNewStage(tkStrikeController, stageTitle, 500, 500);
  }
  
  protected void openInNewStage(TkStrikeController tkStrikeController, int width, int height) {
    openInNewStage(tkStrikeController, "TkStrike", width, height);
  }
  
  protected void openInNewStage(TkStrikeController tkStrikeController, EventHandler<WindowEvent> windowCloseEventHandler, int width, int height) {
    openInNewStage(tkStrikeController, windowCloseEventHandler, "TkStrike", width, height, true);
  }
  
  protected void openInNewStage(TkStrikeController tkStrikeController, String stageTitle, int width, int height) {
    openInNewStage(tkStrikeController, (EventHandler<WindowEvent>)null, stageTitle, width, height, true);
  }
  
  protected void openInNewStage(TkStrikeController tkStrikeController, String stageTitle, int width, int height, boolean resizeable) {
    openInNewStage(tkStrikeController, (EventHandler<WindowEvent>)null, stageTitle, width, height, resizeable);
  }
  
  protected void openInNewStage(TkStrikeController tkStrikeController, String stageTitle, int width, int height, boolean resizeable, boolean closeWithExitPress) {
    openInNewStage(tkStrikeController, null, stageTitle, width, height, resizeable, closeWithExitPress);
  }
  
  protected void openInNewStage(TkStrikeController tkStrikeController, EventHandler<WindowEvent> windowCloseEventHandler, String stageTitle, int width, int height, boolean resizeable) {
    openInNewStage(tkStrikeController, windowCloseEventHandler, stageTitle, width, height, resizeable, true);
  }
  
  protected void openInNewStage(final TkStrikeController tkStrikeController, EventHandler<WindowEvent> windowCloseEventHandler, String stageTitle, int width, int height, boolean resizeable, boolean closeWithExitPress) {
    Scene scene = null;
    if (tkStrikeController.getRootView().getScene() != null) {
      scene = tkStrikeController.getRootView().getScene();
    } else {
      scene = new Scene((Parent)tkStrikeController.getRootView(), width, height);
    } 
    scene.getStylesheets().addAll((Object[])this.tkStrikeStyleSheetsHelper.getTkStrikeStyleSheets());
    final Stage stage = new Stage();
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.initOwner((Window)getCurrentStage());
    stage.getIcons().add(new Image(getClass().getResourceAsStream(getTkStrikeIconApp())));
    stage.setScene(scene);
    stage.setTitle(stageTitle);
    stage.setResizable(resizeable);
    if (closeWithExitPress)
      scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent keyEvent) {
              if (keyEvent.getCode().equals(KeyCode.ESCAPE))
                stage.fireEvent((Event)new WindowEvent((Window)CommonTkStrikeBaseController.this.getCurrentStage(), WindowEvent.WINDOW_CLOSE_REQUEST)); 
            }
          }); 
    stage.addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler<WindowEvent>() {
          public void handle(WindowEvent windowEvent) {
            tkStrikeController.onWindowShowEvent();
          }
        });
    if (windowCloseEventHandler != null)
      stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, windowCloseEventHandler); 
    if (tkStrikeController.getOnWindowCloseEventHandler() != null)
      stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, tkStrikeController.getOnWindowCloseEventHandler()); 
    stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>() {
          public void handle(WindowEvent event) {
            CommonTkStrikeBaseController.this.appStatusWorker.doDialogWindowCloses();
          }
        });
    stage.show();
  }
  
  public void onWindowShowEvent() {
    initializeFormControlsStyles();
  }
  
  public final boolean isFormValid() {
    boolean res = true;
    LinkedHashSet<FormValidationError> validationErrors = validateForm();
    if (validationErrors != null && validationErrors.size() > 0) {
      for (FormValidationError validationError : validationErrors) {
        final Control currControl = validationError.getUiControl();
        if (currControl != null)
          Platform.runLater(new Runnable() {
                public void run() {
                  currControl.getStyleClass().removeAll(Collections.singleton("form-default"));
                  currControl.getStyleClass().add("form-error");
                  Tooltip errorTooltip = new Tooltip(validationError.getMessage());
                  errorTooltip.setHideOnEscape(false);
                  currControl.setTooltip(errorTooltip);
                }
              }); 
      } 
      res = false;
    } 
    if (res)
      initializeFormControlsStyles(); 
    return res;
  }
  
  protected void initializeFormControlsStyles() {
    Collection<Control> formControls = getFormControls();
    if (formControls != null)
      for (Control formControl : formControls) {
        Platform.runLater(new Runnable() {
              public void run() {
                formControl.getStyleClass().removeAll(Collections.singleton("form-error"));
                formControl.getStyleClass().add("form-default");
                formControl.setTooltip(null);
              }
            });
      }  
  }
  
  protected Collection<Control> getFormControls() {
    return null;
  }
  
  public LinkedHashSet<FormValidationError> validateForm() {
    return null;
  }
  
  public EventHandler<WindowEvent> getOnWindowCloseEventHandler() {
    return null;
  }
  
  protected void doCloseThisStage() {
    getCurrentStage().fireEvent((Event)new WindowEvent((Window)getCurrentStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
  }
  
  protected void showInfoDialog(String title, String message) {
    showSimpleDialog(title, message, Alert.AlertType.INFORMATION);
  }
  
  protected void showErrorDialog(String title, String message) {
    showSimpleDialog(title, message, Alert.AlertType.ERROR);
  }
  
  private void showSimpleDialog(final String title, final String message, final Alert.AlertType dialogType) {
    Platform.runLater(new Runnable() {
          public void run() {
            Alert alert = new Alert(dialogType);
            alert.setTitle(title);
            alert.setContentText(message);
            alert.showAndWait();
          }
        });
  }
  
  protected Optional<ButtonType> showConfirmDialog(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle(title);
    alert.setContentText(message);
    alert.setOnCloseRequest(new EventHandler<DialogEvent>() {
          public void handle(DialogEvent event) {
            CommonTkStrikeBaseController.this.appStatusWorker.doDialogWindowCloses();
          }
        });
    alert.getDialogPane().requestFocus();
    return alert.showAndWait();
  }
  
  protected Optional<ButtonType> showAlertWithCheckbox(String title, String message, final String checkBoxText, final Consumer<Boolean> opAction) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.getDialogPane().applyCss();
    Node graphic = alert.getDialogPane().getGraphic();
    alert.setDialogPane(new DialogPane() {
          protected Node createDetailsButton() {
            CheckBox optOut = new CheckBox();
            optOut.setText(checkBoxText);
            optOut.setOnAction(e -> param1Consumer.accept(Boolean.valueOf(param1CheckBox.isSelected())));
            return (Node)optOut;
          }
        });
    alert.getDialogPane().setContentText(message);
    alert.getDialogPane().getButtonTypes().addAll((Object[])new ButtonType[] { ButtonType.YES, ButtonType.NO });
    alert.getDialogPane().setExpandableContent((Node)new Group());
    alert.getDialogPane().setExpanded(true);
    alert.getDialogPane().setGraphic(graphic);
    alert.setTitle(title);
    return alert.showAndWait();
  }
  
  protected void manageException(Throwable exception, String source, final String message2Show) {
    exception.printStackTrace();
    logger.error("Exception on " + ((source != null) ? source : "general"), exception);
    Platform.runLater(new Runnable() {
          public void run() {
            CommonTkStrikeBaseController.this.showErrorDialog(CommonTkStrikeBaseController.this.getMessage("title.default.error"), (message2Show != null) ? message2Show : CommonTkStrikeBaseController.this
                .getMessage("message.error.default"));
          }
        });
  }
  
  protected MonologFXButton.Type showMonologConfirmDialog(String title, String message) {
    MonologFXButton mlb = new MonologFXButton();
    mlb.setDefaultButton(true);
    mlb.setIcon("/images/ic_ok.png");
    mlb.setLabel(getMessage("button.dialog.YES"));
    mlb.setType(MonologFXButton.Type.YES);
    MonologFXButton mlb2 = new MonologFXButton();
    mlb2.setCancelButton(true);
    mlb2.setLabel(getMessage("button.dialog.NO"));
    mlb2.setIcon("/images/ic_delete.png");
    mlb2.setType(MonologFXButton.Type.NO);
    MonologFX mono = new MonologFX(MonologFX.Type.QUESTION);
    mono.setModal(true);
    mono.addButton(mlb);
    mono.addButton(mlb2);
    mono.setButtonAlignment(MonologFX.ButtonAlignment.RIGHT);
    mono.setTitleText(title);
    mono.setMessage(message);
    mono.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>() {
          public void handle(WindowEvent event) {
            CommonTkStrikeBaseController.this.appStatusWorker.doDialogWindowCloses();
          }
        });
    mono.requestFocus();
    return mono.showDialog();
  }
  
  protected void showMonologInfoDialog(String title, String message) {
    showMonologSimpleDialog(title, message, MonologFX.Type.INFO);
  }
  
  protected void showMonologSimpleDialog(String title, String message, MonologFX.Type dialogType) {
    MonologFXButton mlb = new MonologFXButton();
    mlb.setDefaultButton(true);
    mlb.setLabel(getMessage("button.dialog.OK"));
    mlb.setIcon("/images/ic_ok.png");
    mlb.setType(MonologFXButton.Type.OK);
    MonologFX mono = new MonologFX(dialogType);
    mono.setModal(true);
    mono.addButton(mlb);
    mono.setButtonAlignment(MonologFX.ButtonAlignment.RIGHT);
    mono.setTitleText(title);
    mono.setMessage(message);
    mono.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>() {
          public void handle(WindowEvent event) {
            CommonTkStrikeBaseController.this.appStatusWorker.doDialogWindowCloses();
          }
        });
    mono.showDialog();
  }
  
  protected void _updateFlag(FlagEntry flagEntry, TextField txtAbbr, ImageView imageView) {
    if (flagEntry != null) {
      txtAbbr.setText(flagEntry.getAbbreviation());
      imageView.setImage(flagEntry.getImage());
    } else {
      txtAbbr.clear();
      imageView.setImage(null);
    } 
  }
  
  protected static final DecimalFormat pctFormat = new DecimalFormat("0.00 '%'");
  
  protected static final DecimalFormat minutesOrSecondsFormat = new DecimalFormat("00");
  
  protected static final SimpleDateFormat dfMinuteSecondTime = new SimpleDateFormat("mm:ss");
  
  private SimpleDateFormat dfFullFormat = null;
  
  private static HostServices hostServices;
  
  protected SimpleDateFormat getDfFullFormat() {
    if (this.dfFullFormat == null)
      this.dfFullFormat = new SimpleDateFormat(this.dfFullFormatPattern); 
    return this.dfFullFormat;
  }
  
  public HostServices getHostServices() {
    return hostServices;
  }
  
  public void setHostServices(HostServices hostServices) {
    this;
    CommonTkStrikeBaseController.hostServices = hostServices;
  }
  
  protected Date convertToDateViaInstant(LocalDate dateToConvert) {
    return Date.from(dateToConvert.atStartOfDay()
        .atZone(ZoneId.systemDefault())
        .toInstant());
  }
  
  protected LocalDate convertToLocalDateViaMilisecond(Date dateToConvert) {
    return Instant.ofEpochMilli(dateToConvert.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
  }
}
