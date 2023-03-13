package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.communication.ChangeNetworkConfigurationEvent;
import com.xtremis.daedo.tkstrike.communication.ChangeNetworkStatusEvent;
import com.xtremis.daedo.tkstrike.communication.DataEvent;
import com.xtremis.daedo.tkstrike.communication.StatusEvent;
import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationListener;
import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationService;
import com.xtremis.daedo.tkstrike.ei.client.WtUDPService;
import com.xtremis.daedo.tkstrike.orm.model.SensorsGroup;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.model.NetworkAthletesGroupConfigEntry;
import com.xtremis.daedo.tkstrike.ui.model.NetworkConfigurationEntry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class AthletesHardwareTestWorkerImpl implements TkStrikeCommunicationListener, AthletesHardwareTestWorker, InitializingBean {
  private CopyOnWriteArrayList<AthletesHardwareTestWorker.AthletesHardwareTestWorkerListener> listeners = new CopyOnWriteArrayList<>();
  
  private SimpleIntegerProperty testRemain = new SimpleIntegerProperty(this, "testRemain", 8);
  
  private SimpleBooleanProperty testPassed = new SimpleBooleanProperty(this, "testPassed", false);
  
  private SimpleBooleanProperty testReset = new SimpleBooleanProperty(this, "testReset", false);
  
  private SimpleBooleanProperty g1BBTest = new SimpleBooleanProperty(this, "g1BBTest", false);
  
  private SimpleBooleanProperty g1HBTest = new SimpleBooleanProperty(this, "g1HBTest", false);
  
  private SimpleBooleanProperty g1BRTest = new SimpleBooleanProperty(this, "g1BRTest", false);
  
  private SimpleBooleanProperty g1HRTest = new SimpleBooleanProperty(this, "g1HRTest", false);
  
  @Value("${tkStrike.allowGroupSelectionByColor}")
  private Boolean allowGroupSelectionByColor;
  
  @Autowired
  private MatchWorker matchWorker;
  
  @Autowired
  protected NetworkConfigurationService networkConfigurationService;
  
  @Autowired
  protected TkStrikeCommunicationService tkStrikeCommunicationService;
  
  @Autowired
  private WtUDPService wtUDPService;
  
  protected NetworkConfigurationEntry currentNetworkConfiguration = new NetworkConfigurationEntry();
  
  private SimpleBooleanProperty executing = new SimpleBooleanProperty(this, "executing", false);
  
  private SimpleIntegerProperty minBodyHit = new SimpleIntegerProperty(this, "minBodyHit", 0);
  
  private SimpleIntegerProperty minHeadHit = new SimpleIntegerProperty(this, "minHeadHit", 0);
  
  public void startTest() throws TkStrikeServiceException {
    TkStrikeExecutors.executeInThreadPool(new Runnable() {
          public void run() {
            AthletesHardwareTestWorkerImpl.this.wtUDPService.sendHardwareTestOpened();
          }
        });
    if (!this.executing.get())
      this.executing.set(true); 
    this.currentNetworkConfiguration = (NetworkConfigurationEntry)this.networkConfigurationService.getNetworkConfigurationEntry();
    int groupSelected = 1;
    switch (this.matchWorker.getSensorsGroupSelected()) {
      case GROUP1:
        groupSelected = 1;
        break;
      case GROUP2:
        groupSelected = 2;
        break;
      case GROUP3:
        groupSelected = 3;
        break;
      case GROUP4:
        groupSelected = 4;
        break;
      case GROUP5:
        groupSelected = 5;
        break;
      case GROUP6:
        groupSelected = 6;
        break;
    } 
    NetworkAthletesGroupConfigEntry groupConfig = this.currentNetworkConfiguration.getGroupConfig(Integer.valueOf(groupSelected));
    this.g1BBTest.set(false);
    this.g1HBTest.set(false);
    this.g1BRTest.set(false);
    this.g1HRTest.set(false);
    this.testRemain.set(8);
    if (!groupConfig.getBodySensorsEnabled().booleanValue()) {
      this.testRemain.set(this.testRemain.get() - 2);
      this.g1BBTest.set(true);
      this.g1BRTest.set(true);
    } 
    if (!groupConfig.getHeadSensorsEnabled().booleanValue()) {
      this.testRemain.set(this.testRemain.get() - 2);
      this.g1HBTest.set(true);
      this.g1HRTest.set(true);
    } 
    this.tkStrikeCommunicationService.addListener(this);
  }
  
  public void stopTest() {
    if (this.executing.get()) {
      this.executing.set(false);
      this.tkStrikeCommunicationService.removeListener(this);
    } 
    TkStrikeExecutors.executeInThreadPool(new Runnable() {
          public void run() {
            AthletesHardwareTestWorkerImpl.this.wtUDPService.sendHardwareTestClosed();
          }
        });
  }
  
  public boolean getExecuting() {
    return this.executing.get();
  }
  
  public ReadOnlyBooleanProperty executingProperty() {
    return (ReadOnlyBooleanProperty)this.executing;
  }
  
  public SimpleBooleanProperty testResetProperty() {
    return this.testReset;
  }
  
  public int getMinBodyHit() {
    return this.minBodyHit.get();
  }
  
  public SimpleIntegerProperty minBodyHitProperty() {
    return this.minBodyHit;
  }
  
  public int getMinHeadHit() {
    return this.minHeadHit.get();
  }
  
  public SimpleIntegerProperty minHeadHitProperty() {
    return this.minHeadHit;
  }
  
  public int getTestRemain() {
    return this.testRemain.get();
  }
  
  public SimpleIntegerProperty testRemainProperty() {
    return this.testRemain;
  }
  
  public boolean getTestPassed() {
    return this.testPassed.get();
  }
  
  public SimpleBooleanProperty testPassedProperty() {
    return this.testPassed;
  }
  
  public void addListener(AthletesHardwareTestWorker.AthletesHardwareTestWorkerListener athletesHardwareTestWorkerListener) {
    this.listeners.add(athletesHardwareTestWorkerListener);
  }
  
  public void removeListener(AthletesHardwareTestWorker.AthletesHardwareTestWorkerListener athletesHardwareTestWorkerListener) {
    this.listeners.remove(athletesHardwareTestWorkerListener);
  }
  
  public void doBlueBodyPassed() {
    if (this.executing.get())
      TkStrikeExecutors.executeInThreadPool(new Runnable() {
            public void run() {
              AthletesHardwareTestWorkerImpl.this.wtUDPService.sendHardwareTestHit(WtUDPService.HardwareTestHit.BLUE_BODY);
            }
          }); 
  }
  
  public void doBlueHeadPassed() {
    if (this.executing.get())
      TkStrikeExecutors.executeInThreadPool(new Runnable() {
            public void run() {
              AthletesHardwareTestWorkerImpl.this.wtUDPService.sendHardwareTestHit(WtUDPService.HardwareTestHit.BLUE_HEAD);
            }
          }); 
  }
  
  public void doRedBodyPassed() {
    if (this.executing.get())
      TkStrikeExecutors.executeInThreadPool(new Runnable() {
            public void run() {
              AthletesHardwareTestWorkerImpl.this.wtUDPService.sendHardwareTestHit(WtUDPService.HardwareTestHit.RED_BODY);
            }
          }); 
  }
  
  public void doRedHeadPassed() {
    if (this.executing.get())
      TkStrikeExecutors.executeInThreadPool(new Runnable() {
            public void run() {
              AthletesHardwareTestWorkerImpl.this.wtUDPService.sendHardwareTestHit(WtUDPService.HardwareTestHit.RED_HEAD);
            }
          }); 
  }
  
  private void fireNewDataEvent(final DataEvent newDataEvent) {
    for (AthletesHardwareTestWorker.AthletesHardwareTestWorkerListener listener : this.listeners) {
      ExecutorService es = Executors.newSingleThreadExecutor();
      es.submit(new Runnable() {
            public void run() {
              listener.hasHardwareTestNewHitEvent(newDataEvent);
            }
          });
      es.shutdown();
    } 
  }
  
  public final void hasNewDataEvent(DataEvent dataEvent) {
    if (dataEvent != null && this.executing.get())
      if (isHitFromValidAthleteBodyNode(dataEvent.getNodeId())) {
        if (dataEvent.getHitValue().intValue() > this.minBodyHit.get()) {
          fireNewDataEvent(dataEvent);
          if (this.matchWorker.isBlueBodyNodeId(dataEvent.getNodeId())) {
            if (!this.g1BBTest.get()) {
              this.testRemain.set(this.testRemain.get() - 1);
              this.g1BBTest.set(true);
            } 
          } else if (this.matchWorker.isRedBodyNodeId(dataEvent.getNodeId()) && 
            !this.g1BRTest.get()) {
            this.testRemain.set(this.testRemain.get() - 1);
            this.g1BRTest.set(true);
          } 
        } 
      } else if (isHitFromValidAthleteHeadNode(dataEvent.getNodeId()) && 
        dataEvent.getHitValue().intValue() > this.minHeadHit.get()) {
        fireNewDataEvent(dataEvent);
        if (this.matchWorker.isBlueHeadNodeId(dataEvent.getNodeId())) {
          if (!this.g1HBTest.get()) {
            this.testRemain.set(this.testRemain.get() - 1);
            this.g1HBTest.set(true);
          } 
        } else if (this.matchWorker.isRedHeadNodeId(dataEvent.getNodeId()) && 
          !this.g1HRTest.get()) {
          this.testRemain.set(this.testRemain.get() - 1);
          this.g1HRTest.set(true);
        } 
      }  
  }
  
  private boolean isHitFromValidAthleteBodyNode(String nodeId) {
    return (this.matchWorker.isBlueBodyNodeId(nodeId) || this.matchWorker.isRedBodyNodeId(nodeId));
  }
  
  private boolean isHitFromValidAthleteHeadNode(String nodeId) {
    return (this.matchWorker.isBlueHeadNodeId(nodeId) || this.matchWorker.isRedHeadNodeId(nodeId));
  }
  
  public final void hasNewStatusEvent(StatusEvent statusEvent) {}
  
  public final void hasChangeNetworkStatusEvent(ChangeNetworkStatusEvent changeNetworkStatusEvent) {}
  
  public void hasChangeNetworkConfigurationEvent(ChangeNetworkConfigurationEvent changeNetworkConfigurationEvent) {}
  
  public void afterPropertiesSet() throws Exception {
    this.g1BBTest.set(false);
    this.g1HBTest.set(false);
    this.g1BRTest.set(false);
    this.g1HRTest.set(false);
    this.testRemain.addListener(new ChangeListener<Number>() {
          public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
            if (t1.intValue() == 0)
              AthletesHardwareTestWorkerImpl.this._testPassedOk(); 
          }
        });
  }
  
  protected void _testPassedOk() {
    this.testPassed.set(true);
    this.testPassed.set(false);
  }
}
