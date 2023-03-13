package com.xtremis.daedo.tkstrike.ui.controller.hardwaretest;

import com.xtremis.daedo.tkstrike.communication.DataEvent;
import com.xtremis.daedo.tkstrike.communication.GlobalNetworkStatusController;
import com.xtremis.daedo.tkstrike.communication.GlobalNetworkStatusControllerListener;
import com.xtremis.daedo.tkstrike.communication.StatusEvent;
import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationService;
import com.xtremis.daedo.tkstrike.om.ExternalScreenViewId;
import com.xtremis.daedo.tkstrike.service.AppStatusWorker;
import com.xtremis.daedo.tkstrike.service.AthletesHardwareTestWorker;
import com.xtremis.daedo.tkstrike.service.JudgesHardwareTestWorker;
import com.xtremis.daedo.tkstrike.service.MatchWorker;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeBaseController;
import com.xtremis.daedo.tkstrike.ui.model.INetworkConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.NetworkConfigurationEntry;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseNewHardwareTestController extends TkStrikeBaseController implements AthletesHardwareTestWorker.AthletesHardwareTestWorkerListener, JudgesHardwareTestWorker.JudgesHardwareTestWorkerListener, GlobalNetworkStatusControllerListener {
  private static final Image IMAGE_BODY_WHITE = new Image(HardwareTestMainController.class.getResourceAsStream("/images/ht_body_white.png"));
  
  private static final Image IMAGE_BODY_BLUE = new Image(HardwareTestMainController.class.getResourceAsStream("/images/ht_body_blue.png"));
  
  private static final Image IMAGE_BODY_RED = new Image(HardwareTestMainController.class.getResourceAsStream("/images/ht_body_red.png"));
  
  private static final Image IMAGE_HEAD_WHITE = new Image(HardwareTestMainController.class.getResourceAsStream("/images/ht_head_white.png"));
  
  private static final Image IMAGE_HEAD_BLUE = new Image(HardwareTestMainController.class.getResourceAsStream("/images/ht_head_blue.png"));
  
  private static final Image IMAGE_HEAD_RED = new Image(HardwareTestMainController.class.getResourceAsStream("/images/ht_head_red.png"));
  
  private static final Image IMAGE_JOYSTICK = new Image(HardwareTestMainController.class.getResourceAsStream("/images/ht_joystick.png"));
  
  private static final Image IMAGE_JOYSTICK_OK = new Image(HardwareTestMainController.class.getResourceAsStream("/images/ht_joystick_ok.png"));
  
  @Autowired
  protected MatchWorker matchWorker;
  
  @Autowired
  protected AthletesHardwareTestWorker athletesHardwareTestWorker;
  
  @Autowired
  protected JudgesHardwareTestWorker judgesHardwareTestWorker;
  
  @Autowired
  protected TkStrikeCommunicationService tkStrikeCommunicationService;
  
  @Autowired
  protected GlobalNetworkStatusController globalNetworkStatusController;
  
  protected static boolean opened = false;
  
  private SimpleBooleanProperty judge1Ok = new SimpleBooleanProperty(this, "judge1Ok", false);
  
  private SimpleBooleanProperty judge2Ok = new SimpleBooleanProperty(this, "judge2Ok", false);
  
  private SimpleBooleanProperty judge3Ok = new SimpleBooleanProperty(this, "judge3Ok", false);
  
  private final JudgeValidator judge1Validator = new JudgeValidator(this.judge1Ok);
  
  private final JudgeValidator judge2Validator = new JudgeValidator(this.judge2Ok);
  
  private final JudgeValidator judge3Validator = new JudgeValidator(this.judge3Ok);
  
  @FXML
  private ImageView ivLogoDaedo;
  
  @FXML
  private ImageView ivWT;
  
  @FXML
  private ImageView ivBodyRed;
  
  @FXML
  private Label lblBodyRed;
  
  @FXML
  private Shape ciBodyRed;
  
  @FXML
  private ImageView ivHeadRed;
  
  @FXML
  private Label lblHeadRed;
  
  @FXML
  private Shape ciHeadRed;
  
  @FXML
  private ImageView ivBodyBlue;
  
  @FXML
  private Label lblBodyBlue;
  
  @FXML
  private Shape ciBodyBlue;
  
  @FXML
  private ImageView ivHeadBlue;
  
  @FXML
  private Label lblHeadBlue;
  
  @FXML
  private Shape ciHeadBlue;
  
  @FXML
  private ImageView ivJudge1;
  
  @FXML
  private Label lblJudge1;
  
  @FXML
  private ImageView ivJudge2;
  
  @FXML
  private Label lblJudge2;
  
  @FXML
  private ImageView ivJudge3;
  
  @FXML
  private Label lblJudge3;
  
  @FXML
  private Shape ciJ1Red1;
  
  @FXML
  private Shape ciJ1Red2;
  
  @FXML
  private Shape ciJ1Red3;
  
  @FXML
  private Shape ciJ1Blue1;
  
  @FXML
  private Shape ciJ1Blue2;
  
  @FXML
  private Shape ciJ1Blue3;
  
  @FXML
  private ImageView ivJ1Green;
  
  @FXML
  private Shape ciJ2Red1;
  
  @FXML
  private Shape ciJ2Red2;
  
  @FXML
  private Shape ciJ2Red3;
  
  @FXML
  private Shape ciJ2Blue1;
  
  @FXML
  private Shape ciJ2Blue2;
  
  @FXML
  private Shape ciJ2Blue3;
  
  @FXML
  private ImageView ivJ2Green;
  
  @FXML
  private Shape ciJ3Red1;
  
  @FXML
  private Shape ciJ3Red2;
  
  @FXML
  private Shape ciJ3Red3;
  
  @FXML
  private Shape ciJ3Blue1;
  
  @FXML
  private Shape ciJ3Blue2;
  
  @FXML
  private Shape ciJ3Blue3;
  
  @FXML
  private ImageView ivJ3Green;
  
  private NetworkConfigurationEntry currentNetworkConfiguration = new NetworkConfigurationEntry();
  
  public void hasHardwareTestNewHitEvent(DataEvent dataEvent) {
    if (this.currentNetworkConfiguration != null) {
      ImageView iv = null;
      Image image = null;
      Label label = null;
      if (this.matchWorker.isBlueBodyNodeId(dataEvent.getNodeId())) {
        iv = this.ivBodyBlue;
        image = IMAGE_BODY_BLUE;
        label = this.lblBodyBlue;
        this.athletesHardwareTestWorker.doBlueBodyPassed();
      } else if (this.matchWorker.isBlueHeadNodeId(dataEvent.getNodeId())) {
        iv = this.ivHeadBlue;
        image = IMAGE_HEAD_BLUE;
        label = this.lblHeadBlue;
        this.athletesHardwareTestWorker.doBlueHeadPassed();
      } else if (this.matchWorker.isRedBodyNodeId(dataEvent.getNodeId())) {
        iv = this.ivBodyRed;
        image = IMAGE_BODY_RED;
        label = this.lblBodyRed;
        this.athletesHardwareTestWorker.doRedBodyPassed();
      } else if (this.matchWorker.isRedHeadNodeId(dataEvent.getNodeId())) {
        iv = this.ivHeadRed;
        image = IMAGE_HEAD_RED;
        label = this.lblHeadRed;
        this.athletesHardwareTestWorker.doRedHeadPassed();
      } 
      if (iv != null) {
        final ImageView fiIv = iv;
        final Image fiImage = image;
        final Label fiLabel = label;
        Platform.runLater(new Runnable() {
              public void run() {
                fiIv.setImage(fiImage);
                fiLabel.setVisible(true);
              }
            });
      } 
    } 
  }
  
  public void hasNewJudgesHardwareTestHitEvent(DataEvent dataEvent) {
    if (this.currentNetworkConfiguration != null) {
      Shape shape = null;
      if (43521 == dataEvent.getHitValue().intValue()) {
        if (dataEvent.getNodeId().equals(this.currentNetworkConfiguration.getJudge1NodeId())) {
          shape = this.ciJ1Blue1;
          this.judge1Validator.setButtonBlue1OK();
        } else if (dataEvent.getNodeId().equals(this.currentNetworkConfiguration.getJudge2NodeId())) {
          shape = this.ciJ2Blue1;
          this.judge2Validator.setButtonBlue1OK();
        } else if (dataEvent.getNodeId().equals(this.currentNetworkConfiguration.getJudge3NodeId())) {
          shape = this.ciJ3Blue1;
          this.judge3Validator.setButtonBlue1OK();
        } 
      } else if (43522 == dataEvent.getHitValue().intValue()) {
        if (dataEvent.getNodeId().equals(this.currentNetworkConfiguration.getJudge1NodeId())) {
          shape = this.ciJ1Blue2;
          this.judge1Validator.setButtonBlue2OK();
        } else if (dataEvent.getNodeId().equals(this.currentNetworkConfiguration.getJudge2NodeId())) {
          shape = this.ciJ2Blue2;
          this.judge2Validator.setButtonBlue2OK();
        } else if (dataEvent.getNodeId().equals(this.currentNetworkConfiguration.getJudge3NodeId())) {
          shape = this.ciJ3Blue2;
          this.judge3Validator.setButtonBlue2OK();
        } 
      } else if (43552 == dataEvent.getHitValue().intValue()) {
        if (dataEvent.getNodeId().equals(this.currentNetworkConfiguration.getJudge1NodeId())) {
          shape = this.ciJ1Blue3;
          this.judge1Validator.setButtonBlue3OK();
        } else if (dataEvent.getNodeId().equals(this.currentNetworkConfiguration.getJudge2NodeId())) {
          shape = this.ciJ2Blue3;
          this.judge2Validator.setButtonBlue3OK();
        } else if (dataEvent.getNodeId().equals(this.currentNetworkConfiguration.getJudge3NodeId())) {
          shape = this.ciJ3Blue3;
          this.judge3Validator.setButtonBlue3OK();
        } 
      } else if (43524 == dataEvent.getHitValue().intValue()) {
        if (dataEvent.getNodeId().equals(this.currentNetworkConfiguration.getJudge1NodeId())) {
          shape = this.ciJ1Red1;
          this.judge1Validator.setButtonRed1OK();
        } else if (dataEvent.getNodeId().equals(this.currentNetworkConfiguration.getJudge2NodeId())) {
          shape = this.ciJ2Red1;
          this.judge2Validator.setButtonRed1OK();
        } else if (dataEvent.getNodeId().equals(this.currentNetworkConfiguration.getJudge3NodeId())) {
          shape = this.ciJ3Red1;
          this.judge3Validator.setButtonRed1OK();
        } 
      } else if (43528 == dataEvent.getHitValue().intValue()) {
        if (dataEvent.getNodeId().equals(this.currentNetworkConfiguration.getJudge1NodeId())) {
          shape = this.ciJ1Red2;
          this.judge1Validator.setButtonRed2OK();
        } else if (dataEvent.getNodeId().equals(this.currentNetworkConfiguration.getJudge2NodeId())) {
          shape = this.ciJ2Red2;
          this.judge2Validator.setButtonRed2OK();
        } else if (dataEvent.getNodeId().equals(this.currentNetworkConfiguration.getJudge3NodeId())) {
          shape = this.ciJ3Red2;
          this.judge3Validator.setButtonRed2OK();
        } 
      } else if (43536 == dataEvent.getHitValue().intValue()) {
        if (dataEvent.getNodeId().equals(this.currentNetworkConfiguration.getJudge1NodeId())) {
          shape = this.ciJ1Red3;
          this.judge1Validator.setButtonRed3OK();
        } else if (dataEvent.getNodeId().equals(this.currentNetworkConfiguration.getJudge2NodeId())) {
          shape = this.ciJ2Red3;
          this.judge2Validator.setButtonRed3OK();
        } else if (dataEvent.getNodeId().equals(this.currentNetworkConfiguration.getJudge3NodeId())) {
          shape = this.ciJ3Red3;
          this.judge3Validator.setButtonRed3OK();
        } 
      } 
      if (shape != null) {
        final Shape fiShape = shape;
        Platform.runLater(new Runnable() {
              public void run() {
                fiShape.setVisible(true);
              }
            });
      } 
    } 
  }
  
  public EventHandler<WindowEvent> getOnWindowCloseEventHandler() {
    return new EventHandler<WindowEvent>() {
        public void handle(WindowEvent windowEvent) {
          BaseNewHardwareTestController.this.athletesHardwareTestWorker.stopTest();
          BaseNewHardwareTestController.this.judgesHardwareTestWorker.stopTest();
          BaseNewHardwareTestController.opened = false;
        }
      };
  }
  
  public void doResetHardwareTest() {
    if (!isExternal())
      TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {
            public Void call() throws Exception {
              try {
                BaseNewHardwareTestController.this.athletesHardwareTestWorker.stopTest();
                BaseNewHardwareTestController.this.judgesHardwareTestWorker.stopTest();
                try {
                  TimeUnit.MILLISECONDS.sleep(100L);
                } catch (InterruptedException interruptedException) {}
                BaseNewHardwareTestController.this.athletesHardwareTestWorker.startTest();
                BaseNewHardwareTestController.this.judgesHardwareTestWorker.startTest();
              } catch (TkStrikeServiceException e) {
                e.printStackTrace();
              } 
              return null;
            }
          }); 
  }
  
  public void onWindowShowEvent() {
    super.onWindowShowEvent();
    opened = true;
    if (!isExternal())
      TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {
            public Void call() throws Exception {
              try {
                BaseNewHardwareTestController.this.athletesHardwareTestWorker.stopTest();
                BaseNewHardwareTestController.this.judgesHardwareTestWorker.stopTest();
                try {
                  TimeUnit.MILLISECONDS.sleep(100L);
                } catch (InterruptedException interruptedException) {}
                BaseNewHardwareTestController.this.athletesHardwareTestWorker.startTest();
                BaseNewHardwareTestController.this.judgesHardwareTestWorker.startTest();
              } catch (TkStrikeServiceException e) {
                e.printStackTrace();
              } 
              return null;
            }
          }); 
  }
  
  public final void afterPropertiesSet() throws Exception {
    this.athletesHardwareTestWorker.addListener(this);
    this.judgesHardwareTestWorker.addListener(this);
    this.athletesHardwareTestWorker.executingProperty().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue.booleanValue()) {
              BaseNewHardwareTestController.this.judge1Validator.reset();
              BaseNewHardwareTestController.this.judge2Validator.reset();
              BaseNewHardwareTestController.this.judge3Validator.reset();
              BaseNewHardwareTestController.this.currentNetworkConfiguration = (NetworkConfigurationEntry)BaseNewHardwareTestController.this.getCurrentNetworkConfiguration();
              Platform.runLater(new Runnable() {
                    public void run() {
                      BaseNewHardwareTestController.this.ivBodyRed.setImage(BaseNewHardwareTestController.IMAGE_BODY_WHITE);
                      BaseNewHardwareTestController.this.lblBodyRed.setVisible(false);
                      BaseNewHardwareTestController.this.ivHeadRed.setImage(BaseNewHardwareTestController.IMAGE_HEAD_WHITE);
                      BaseNewHardwareTestController.this.lblHeadRed.setVisible(false);
                      BaseNewHardwareTestController.this.ivBodyBlue.setImage(BaseNewHardwareTestController.IMAGE_BODY_WHITE);
                      BaseNewHardwareTestController.this.lblBodyBlue.setVisible(false);
                      BaseNewHardwareTestController.this.ivHeadBlue.setImage(BaseNewHardwareTestController.IMAGE_HEAD_WHITE);
                      BaseNewHardwareTestController.this.lblHeadBlue.setVisible(false);
                      BaseNewHardwareTestController.this.ivJudge1.setImage(BaseNewHardwareTestController.IMAGE_JOYSTICK);
                      BaseNewHardwareTestController.this.ivJudge2.setImage(BaseNewHardwareTestController.IMAGE_JOYSTICK);
                      BaseNewHardwareTestController.this.ivJudge3.setImage(BaseNewHardwareTestController.IMAGE_JOYSTICK);
                      BaseNewHardwareTestController.this.ciJ1Red1.setVisible(false);
                      BaseNewHardwareTestController.this.ciJ1Red2.setVisible(false);
                      BaseNewHardwareTestController.this.ciJ1Red3.setVisible(false);
                      BaseNewHardwareTestController.this.ciJ1Blue1.setVisible(false);
                      BaseNewHardwareTestController.this.ciJ1Blue2.setVisible(false);
                      BaseNewHardwareTestController.this.ciJ1Blue3.setVisible(false);
                      BaseNewHardwareTestController.this.ciJ2Red1.setVisible(false);
                      BaseNewHardwareTestController.this.ciJ2Red2.setVisible(false);
                      BaseNewHardwareTestController.this.ciJ2Red3.setVisible(false);
                      BaseNewHardwareTestController.this.ciJ2Blue1.setVisible(false);
                      BaseNewHardwareTestController.this.ciJ2Blue2.setVisible(false);
                      BaseNewHardwareTestController.this.ciJ2Blue3.setVisible(false);
                      BaseNewHardwareTestController.this.ciJ3Red1.setVisible(false);
                      BaseNewHardwareTestController.this.ciJ3Red2.setVisible(false);
                      BaseNewHardwareTestController.this.ciJ3Red3.setVisible(false);
                      BaseNewHardwareTestController.this.ciJ3Blue1.setVisible(false);
                      BaseNewHardwareTestController.this.ciJ3Blue2.setVisible(false);
                      BaseNewHardwareTestController.this.ciJ3Blue3.setVisible(false);
                      BaseNewHardwareTestController.this.judge1Ok.set(false);
                      BaseNewHardwareTestController.this.judge2Ok.set(false);
                      BaseNewHardwareTestController.this.judge3Ok.set(false);
                      BaseNewHardwareTestController.this._onWindowShowEvent();
                      BaseNewHardwareTestController.this.getAppStatusWorker().doChangeExternalScreenView(ExternalScreenViewId.HT_ATHLETES);
                    }
                  });
            } 
          }
        });
    this.judge1Ok.addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, final Boolean newValue) {
            Platform.runLater(new Runnable() {
                  public void run() {
                    BaseNewHardwareTestController.this.ivJudge1.setImage(newValue.booleanValue() ? BaseNewHardwareTestController.IMAGE_JOYSTICK_OK : BaseNewHardwareTestController.IMAGE_JOYSTICK);
                  }
                });
          }
        });
    this.judge2Ok.addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, final Boolean newValue) {
            Platform.runLater(new Runnable() {
                  public void run() {
                    BaseNewHardwareTestController.this.ivJudge2.setImage(newValue.booleanValue() ? BaseNewHardwareTestController.IMAGE_JOYSTICK_OK : BaseNewHardwareTestController.IMAGE_JOYSTICK);
                  }
                });
          }
        });
    this.judge3Ok.addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, final Boolean newValue) {
            Platform.runLater(new Runnable() {
                  public void run() {
                    BaseNewHardwareTestController.this.ivJudge3.setImage(newValue.booleanValue() ? BaseNewHardwareTestController.IMAGE_JOYSTICK_OK : BaseNewHardwareTestController.IMAGE_JOYSTICK);
                  }
                });
          }
        });
    this.lblJudge1.visibleProperty().bind((ObservableValue)this.judge1Ok);
    this.ivJ1Green.visibleProperty().bind((ObservableValue)this.judge1Ok);
    this.lblJudge2.visibleProperty().bind((ObservableValue)this.judge2Ok);
    this.ivJ2Green.visibleProperty().bind((ObservableValue)this.judge2Ok);
    this.lblJudge3.visibleProperty().bind((ObservableValue)this.judge3Ok);
    this.ivJ3Green.visibleProperty().bind((ObservableValue)this.judge3Ok);
    this.globalNetworkStatusController.addListener(this);
    _afterPropertiesSet();
  }
  
  public void hasNewGlobalStatusEvent(final StatusEvent statusEvent) {
    if (opened) {
      Shape shape = null;
      if (this.matchWorker.isBlueBodyNodeId(statusEvent.getNodeId())) {
        shape = this.ciBodyBlue;
      } else if (this.matchWorker.isBlueHeadNodeId(statusEvent.getNodeId())) {
        shape = this.ciHeadBlue;
      } else if (this.matchWorker.isRedBodyNodeId(statusEvent.getNodeId())) {
        shape = this.ciBodyRed;
      } else if (this.matchWorker.isRedHeadNodeId(statusEvent.getNodeId())) {
        shape = this.ciHeadRed;
      } 
      if (shape != null) {
        final Shape fiShape = shape;
        Platform.runLater(new Runnable() {
              public void run() {
                fiShape.setFill(statusEvent.getConnOffline().booleanValue() ? (Paint)Color.RED : (statusEvent.getSensorOk().booleanValue() ? (Paint)Color.GREEN : (Paint)Color.ORANGE));
              }
            });
      } 
    } 
  }
  
  public void hasNewNodeNetworkErrorEvent(GlobalNetworkStatusControllerListener.NodeNetworkErrorEvent nodeNetworkErrorEvent) {}
  
  public void hasNetworkOkEvent(GlobalNetworkStatusControllerListener.NetworkOkEvent networkOkEvent) {}
  
  protected abstract boolean isExternal();
  
  protected abstract void _onWindowShowEvent();
  
  protected abstract void _afterPropertiesSet() throws Exception;
  
  class JudgeValidator {
    private boolean buttonBlue1 = false;
    
    private boolean buttonBlue2 = false;
    
    private boolean buttonBlue3 = false;
    
    private boolean buttonRed1 = false;
    
    private boolean buttonRed2 = false;
    
    private boolean buttonRed3 = false;
    
    private final SimpleBooleanProperty relatedProperty;
    
    public JudgeValidator(SimpleBooleanProperty relatedProperty) {
      this.relatedProperty = relatedProperty;
    }
    
    public void setButtonBlue1OK() {
      this.buttonBlue1 = true;
      this.relatedProperty.set((this.buttonBlue1 && this.buttonBlue2 && this.buttonBlue3 && this.buttonRed1 && this.buttonRed2 && this.buttonRed3));
    }
    
    public void setButtonBlue2OK() {
      this.buttonBlue2 = true;
      this.relatedProperty.set((this.buttonBlue1 && this.buttonBlue2 && this.buttonBlue3 && this.buttonRed1 && this.buttonRed2 && this.buttonRed3));
    }
    
    public void setButtonBlue3OK() {
      this.buttonBlue3 = true;
      this.relatedProperty.set((this.buttonBlue1 && this.buttonBlue2 && this.buttonBlue3 && this.buttonRed1 && this.buttonRed2 && this.buttonRed3));
    }
    
    public void setButtonRed1OK() {
      this.buttonRed1 = true;
      this.relatedProperty.set((this.buttonBlue1 && this.buttonBlue2 && this.buttonBlue3 && this.buttonRed1 && this.buttonRed2 && this.buttonRed3));
    }
    
    public void setButtonRed2OK() {
      this.buttonRed2 = true;
      this.relatedProperty.set((this.buttonBlue1 && this.buttonBlue2 && this.buttonBlue3 && this.buttonRed1 && this.buttonRed2 && this.buttonRed3));
    }
    
    public void setButtonRed3OK() {
      this.buttonRed3 = true;
      this.relatedProperty.set((this.buttonBlue1 && this.buttonBlue2 && this.buttonBlue3 && this.buttonRed1 && this.buttonRed2 && this.buttonRed3));
    }
    
    public void reset() {
      this.buttonBlue1 = false;
      this.buttonBlue2 = false;
      this.buttonBlue3 = false;
      this.buttonRed1 = false;
      this.buttonRed2 = false;
      this.buttonRed3 = false;
    }
  }
}
