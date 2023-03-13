package com.xtremis.daedo.tkstrike.ui;

import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationException;
import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationListener;
import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationService;
import com.xtremis.daedo.tkstrike.ei.client.RtBroadcastSocketClient;
import com.xtremis.daedo.tkstrike.ei.client.WtUDPService;
import com.xtremis.daedo.tkstrike.service.AppStatusWorker;
import com.xtremis.daedo.tkstrike.service.ExternalConfigService;
import com.xtremis.daedo.tkstrike.service.NetworkConfigurationService;
import com.xtremis.daedo.tkstrike.service.TkStrikeStyleSheetsHelper;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeController;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeMainController;
import com.xtremis.daedo.tkstrike.ui.model.ExternalConfigEntry;
import com.xtremis.daedo.tkstrike.ui.model.INetworkConfigurationEntry;
import com.xtremis.daedo.tkstrike.utils.TkStrikeBaseDirectoriesUtil;
import com.xtremis.daedo.tkstrike.utils.TkStrikeDatabaseMigration;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.RollingFileAppender;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.PropertySource;

public abstract class BaseTkStrikeAppMain extends Application {
  private static final Logger logger = Logger.getLogger(BaseTkStrikeAppMain.class);
  
  ApplicationContext contextAnnotation = null;
  
  private TkStrikeCommunicationService tkStrikeCommunicationService;
  
  private VBox splashLayout;
  
  private ProgressBar loadProgress;
  
  private Label progressText;
  
  private Stage mainStage;
  
  private static final int SPLASH_WIDTH = 685;
  
  private static final int SPLASH_HEIGHT = 490;
  
  public void init() throws Exception {
    this.loadProgress = new ProgressBar();
    this.loadProgress.setPrefWidth(665.0D);
    this.progressText = new Label("");
    this.splashLayout = new VBox();
    this.splashLayout.setMaxWidth(685.0D);
    this.splashLayout.setMinWidth(685.0D);
    this.splashLayout.setPrefWidth(685.0D);
    this.splashLayout.setMaxHeight(490.0D);
    this.splashLayout.setMinHeight(490.0D);
    this.splashLayout.setPrefHeight(490.0D);
    this.splashLayout.setAlignment(Pos.CENTER);
    this.progressText.setAlignment(Pos.BOTTOM_CENTER);
    Pane empty = new Pane();
    empty.setMaxHeight(400.0D);
    empty.setMinHeight(400.0D);
    empty.setPrefHeight(400.0D);
    this.splashLayout.setSpacing(10.0D);
    this.splashLayout.getChildren().addAll((Object[])new Node[] { (Node)empty, (Node)this.loadProgress, (Node)this.progressText });
    this.splashLayout.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-border-width:1; -fx-border-color: black;-fx-background-image: url('" + 
        
        getTkStrikeSplashUrl() + "');");
    this.splashLayout.setEffect((Effect)new DropShadow());
  }
  
  public void start(Stage stage) throws Exception {
    Task<Boolean> friendTask = new Task<Boolean>() {
        protected Boolean call() throws InterruptedException {
          Boolean initErrors = Boolean.FALSE;
          updateMessage("Directories...");
          TkStrikeBaseDirectoriesUtil tkStrikeBaseDirectoriesUtil = TkStrikeBaseDirectoriesUtil.getInstance();
          try {
            tkStrikeBaseDirectoriesUtil.validateOrCreate();
          } catch (IOException e) {
            e.printStackTrace();
            Platform.exit();
            System.exit(0);
            throw new RuntimeException("Can't create directories");
          } 
          updateMessage("Logger...");
          File log4jProperties = new File(tkStrikeBaseDirectoriesUtil.getWorkBaseDir() + "log4j.properties");
          if (log4jProperties.exists()) {
            updateMessage("Initialize with external file");
            PropertyConfigurator.configure(log4jProperties.getAbsolutePath());
          } else {
            URL defaultLog4jPropertiesUrl = getClass().getResource("/META-INF/log4j.properties");
            if (defaultLog4jPropertiesUrl != null) {
              updateMessage("Initialize with default file");
              PropertyConfigurator.configure(defaultLog4jPropertiesUrl);
            } else {
              RollingFileAppender appenderDefault = new RollingFileAppender();
              appenderDefault.setFile(tkStrikeBaseDirectoriesUtil.getLogBaseDir() + BaseTkStrikeAppMain.this.getGeneralLogFileName());
              appenderDefault.setMaxBackupIndex(10);
              appenderDefault.setMaxFileSize("2048KB");
              appenderDefault.setLayout((Layout)new PatternLayout("%d{dd/MM/yyyy HH:mm:ss,SSS} - %m%n"));
              appenderDefault.setThreshold((Priority)Level.INFO);
              appenderDefault.setImmediateFlush(true);
              appenderDefault.setAppend(true);
              appenderDefault.activateOptions();
              Logger.getRootLogger().addAppender((Appender)appenderDefault);
              RollingFileAppender appenderExternalIntegration = new RollingFileAppender();
              appenderExternalIntegration.setName("EXTERNAL_INTEGRATION");
              appenderExternalIntegration.setFile(tkStrikeBaseDirectoriesUtil.getLogBaseDir() + BaseTkStrikeAppMain.this.getExternalIntegrationLogFileName());
              appenderExternalIntegration.setMaxBackupIndex(10);
              appenderExternalIntegration.setMaxFileSize("2048KB");
              appenderExternalIntegration.setLayout((Layout)new PatternLayout("%d{dd/MM/yyyy HH:mm:ss,SSS} - %m%n"));
              appenderExternalIntegration.setThreshold((Priority)Level.INFO);
              appenderExternalIntegration.setImmediateFlush(true);
              appenderExternalIntegration.setAppend(true);
              appenderExternalIntegration.activateOptions();
              Logger loggerExternalIntegration = Logger.getLogger("EXTERNAL_INTEGRATION");
              loggerExternalIntegration.addAppender((Appender)appenderExternalIntegration);
              RollingFileAppender appenderCSVImporter = new RollingFileAppender();
              appenderCSVImporter.setName("CSV_IMPORTER");
              appenderCSVImporter.setFile(tkStrikeBaseDirectoriesUtil.getLogBaseDir() + BaseTkStrikeAppMain.this.getCSVImporterLogFileName());
              appenderCSVImporter.setMaxBackupIndex(10);
              appenderCSVImporter.setMaxFileSize("2048KB");
              appenderCSVImporter.setLayout((Layout)new PatternLayout("%d{dd/MM/yyyy HH:mm:ss,SSS} - %m%n"));
              appenderCSVImporter.setThreshold((Priority)Level.INFO);
              appenderCSVImporter.setImmediateFlush(true);
              appenderCSVImporter.setAppend(true);
              appenderCSVImporter.activateOptions();
              Logger loggerCSVImporter = Logger.getLogger("CSV_IMPORTER");
              loggerCSVImporter.addAppender((Appender)appenderCSVImporter);
            } 
          } 
          try {
            updateMessage("Create backup Database");
            updateMessage("Database migration...");
            TkStrikeDatabaseMigration tkStrikeDatabaseMigration = new TkStrikeDatabaseMigration();
            initErrors = Boolean.valueOf(tkStrikeDatabaseMigration.databaseMigration());
            updateMessage("Initializing Context...");
            try {
              BaseTkStrikeAppMain.this.contextAnnotation = (ApplicationContext)new AnnotationConfigApplicationContext(new Class[] { this.this$0.getTkStrikeSpringConfigurationClass() });
            } catch (Exception e) {
              e.printStackTrace();
              BaseTkStrikeAppMain.logger.info("Exception starting", e);
              Platform.exit();
              System.exit(0);
              throw new RuntimeException(e);
            } 
            initErrors = BaseTkStrikeAppMain.this.customInitialize();
            try {
              updateMessage("Initializing Comm...");
              BaseTkStrikeAppMain.this.tkStrikeCommunicationService = (TkStrikeCommunicationService)BaseTkStrikeAppMain.this.contextAnnotation.getBean(TkStrikeCommunicationService.class);
              BaseTkStrikeAppMain.this.tkStrikeCommunicationService.startComm();
            } catch (Exception e) {
              e.printStackTrace();
            } 
            updateMessage("RtBroadcast validation and connection...");
            ExternalConfigEntry externalConfigEntry = null;
            Boolean wtCompetitionDataProtocol = Boolean.FALSE;
            try {
              ExternalConfigService externalConfigService = (ExternalConfigService)BaseTkStrikeAppMain.this.contextAnnotation.getBean(ExternalConfigService.class);
              externalConfigEntry = externalConfigService.getExternalConfigEntry();
              wtCompetitionDataProtocol = externalConfigService.getWtCompetitionDataProtocol();
            } catch (Exception e) {
              initErrors = Boolean.TRUE;
              e.printStackTrace();
              tkStrikeDatabaseMigration.forceCleanAndMigrate();
            } 
            if (externalConfigEntry != null) {
              if (StringUtils.isNotBlank(externalConfigEntry.getRtBroadcastIp()) && 
                StringUtils.isNotBlank(externalConfigEntry.getRtBroadcastRingNumber()) && externalConfigEntry
                .getRtBroadcastPort() > 0L)
                try {
                  RtBroadcastSocketClient rtBroadcastSocketClient = (RtBroadcastSocketClient)BaseTkStrikeAppMain.this.contextAnnotation.getBean(RtBroadcastSocketClient.class);
                  rtBroadcastSocketClient.connect(externalConfigEntry.getRtBroadcastIp(), Long.valueOf(externalConfigEntry.getRtBroadcastPort()), externalConfigEntry.getRtBroadcastRingNumber());
                } catch (Exception exception) {} 
              if (wtCompetitionDataProtocol.booleanValue()) {
                updateMessage("WT Ovr UDP validation and connection...");
                WtUDPService wtUDPService = (WtUDPService)BaseTkStrikeAppMain.this.contextAnnotation.getBean(WtUDPService.class);
                wtUDPService.connect(externalConfigEntry.getWtOvrUdpIp(), 
                    Integer.valueOf(externalConfigEntry.getWtOvrUdpListenPort()), 
                    Integer.valueOf(externalConfigEntry.getWtOvrUdpWritePort()));
              } 
            } 
            AppStatusWorker appStatusWorker = (AppStatusWorker)BaseTkStrikeAppMain.this.contextAnnotation.getBean(AppStatusWorker.class);
            BaseTkStrikeAppMain.this.tkStrikeCommunicationService.addListener((TkStrikeCommunicationListener)appStatusWorker);
            updateMessage("Network recognition...");
            NetworkConfigurationService networkConfigurationService = (NetworkConfigurationService)BaseTkStrikeAppMain.this.contextAnnotation.getBean(NetworkConfigurationService.class);
            INetworkConfigurationEntry currentNetworkConfiguration = networkConfigurationService.getNetworkConfigurationEntry();
            try {
              BaseTkStrikeAppMain.this.tkStrikeCommunicationService.tryToRecognizeWithConfig(currentNetworkConfiguration.getNetworkConfigurationDto(), false);
            } catch (Exception e) {
              e.printStackTrace();
            } 
          } catch (Exception e) {
            e.printStackTrace();
            Platform.exit();
            System.exit(0);
            throw new RuntimeException("Can't start TkStrike!");
          } 
          return initErrors;
        }
      };
    showSplash(stage, friendTask, new InitCompletionHandler() {
          public void complete(Boolean initErrors) {
            BaseTkStrikeAppMain.this.showMainStage(initErrors);
          }
        });
    (new Thread((Runnable)friendTask)).start();
  }
  
  private void showSplash(final Stage initStage, final Task<Boolean> task, final InitCompletionHandler initCompletionHandler) {
    this.progressText.textProperty().bind((ObservableValue)task.messageProperty());
    this.loadProgress.progressProperty().bind((ObservableValue)task.progressProperty());
    task.stateProperty().addListener(new ChangeListener<Worker.State>() {
          public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State oldState, Worker.State newState) {
            if (newState == Worker.State.SUCCEEDED) {
              BaseTkStrikeAppMain.this.loadProgress.progressProperty().unbind();
              BaseTkStrikeAppMain.this.loadProgress.setProgress(1.0D);
              initStage.toFront();
              FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2D), (Node)BaseTkStrikeAppMain.this.splashLayout);
              fadeSplash.setFromValue(1.0D);
              fadeSplash.setToValue(0.0D);
              fadeSplash.setOnFinished(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent actionEvent) {
                      initStage.hide();
                    }
                  });
              fadeSplash.play();
              try {
                initCompletionHandler.complete((Boolean)task.get());
              } catch (InterruptedException|java.util.concurrent.ExecutionException e) {
                BaseTkStrikeAppMain.logger.info("Can't start TkStrike");
                Platform.exit();
                System.exit(0);
              } 
            } 
          }
        });
    Scene splashScene = new Scene((Parent)this.splashLayout);
    splashScene.getStylesheets().addAll((Object[])new String[] { TkStrikeController.class.getResource("/styles/TkStrikeMain.css").toExternalForm() });
    initStage.initStyle(StageStyle.UNDECORATED);
    initStage.setScene(splashScene);
    initStage.show();
  }
  
  private void showMainStage(Boolean initErrors) {
    final TkStrikeMainController tkStrikeMainController = (TkStrikeMainController)this.contextAnnotation.getBean(getTkStrikeMainControllerClass());
    tkStrikeMainController.setHostServices(getHostServices());
    tkStrikeMainController.setDatabaseInitializedOnStart(initErrors);
    TkStrikeStyleSheetsHelper tkStrikeStyleSheetsHelper = (TkStrikeStyleSheetsHelper)this.contextAnnotation.getBean(TkStrikeStyleSheetsHelper.class);
    String[] styleSheets = tkStrikeStyleSheetsHelper.getTkStrikeStyleSheets();
    this.mainStage = new Stage();
    Scene scene = new Scene((Parent)tkStrikeMainController.getRootView(), 1280.0D, 720.0D);
    scene.getStylesheets().addAll((Object[])styleSheets);
    this.mainStage.getIcons().add(new Image(getClass().getResourceAsStream(getTkStrikeIconApp())));
    this.mainStage.setScene(scene);
    this.mainStage.addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler<WindowEvent>() {
          public void handle(WindowEvent windowEvent) {
            tkStrikeMainController.onWindowShowEvent();
          }
        });
    Platform.setImplicitExit(false);
    this.mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
          public void handle(final WindowEvent windowEvent) {
            Platform.runLater(new Runnable() {
                  public void run() {
                    if (tkStrikeMainController.confirmExitTkStrike()) {
                      try {
                        BaseTkStrikeAppMain.this.tkStrikeCommunicationService.stopComm();
                      } catch (TkStrikeCommunicationException e) {
                        e.printStackTrace();
                      } 
                      try {
                        RtBroadcastSocketClient rtBroadcastSocketClient = (RtBroadcastSocketClient)BaseTkStrikeAppMain.this.contextAnnotation.getBean(RtBroadcastSocketClient.class);
                        if (rtBroadcastSocketClient != null)
                          rtBroadcastSocketClient.closeConnection(); 
                      } catch (Exception exception) {}
                      try {
                        WtUDPService wtUDPService = (WtUDPService)BaseTkStrikeAppMain.this.contextAnnotation.getBean(WtUDPService.class);
                        if (wtUDPService != null)
                          wtUDPService.closeConnection(true); 
                      } catch (Exception exception) {}
                      TkStrikeExecutors.shutdownThreadPool();
                      TkStrikeExecutors.shutdownThreadScheduled();
                      Platform.exit();
                      System.exit(0);
                    } else {
                      windowEvent.consume();
                      BaseTkStrikeAppMain.this.mainStage.show();
                      BaseTkStrikeAppMain.this.mainStage.requestFocus();
                    } 
                  }
                });
          }
        });
    this.mainStage.setResizable(false);
    PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = (PropertySourcesPlaceholderConfigurer)this.contextAnnotation.getBean(PropertySourcesPlaceholderConfigurer.class);
    PropertySource<?> propertySource = propertySourcesPlaceholderConfigurer.getAppliedPropertySources().get("localProperties");
    String currentVersion = (String)propertySource.getProperty("tkStrike.current.version");
    this.mainStage.setTitle((String)this.contextAnnotation.getBean("tkStrikeName") + " (v. " + currentVersion + ")");
    this.mainStage.show();
  }
  
  abstract String getTkStrikeSplashUrl();
  
  abstract String getTkStrikeIconApp();
  
  abstract Boolean customInitialize();
  
  abstract Class getTkStrikeSpringConfigurationClass();
  
  abstract Class<? extends TkStrikeMainController> getTkStrikeMainControllerClass();
  
  abstract String getGeneralLogFileName();
  
  abstract String getExternalIntegrationLogFileName();
  
  abstract String getCSVImporterLogFileName();
  
  public static interface InitCompletionHandler {
    void complete(Boolean param1Boolean);
  }
}
