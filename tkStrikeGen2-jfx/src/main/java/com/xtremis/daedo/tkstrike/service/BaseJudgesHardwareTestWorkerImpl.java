package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.communication.ChangeNetworkConfigurationEvent;
import com.xtremis.daedo.tkstrike.communication.ChangeNetworkStatusEvent;
import com.xtremis.daedo.tkstrike.communication.DataEvent;
import com.xtremis.daedo.tkstrike.communication.StatusEvent;
import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationListener;
import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationService;
import com.xtremis.daedo.tkstrike.ui.model.INetworkConfigurationEntry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseJudgesHardwareTestWorkerImpl<NE extends INetworkConfigurationEntry> implements TkStrikeCommunicationListener, InitializingBean, JudgesHardwareTestWorker {
  private CopyOnWriteArrayList<JudgesHardwareTestWorker.JudgesHardwareTestWorkerListener> listeners = new CopyOnWriteArrayList<>();
  
  protected final TkStrikeCommunicationService tkStrikeCommunicationService;
  
  private JudgeValidate j1TestValidate;
  
  private JudgeValidate j2TestValidate;
  
  private JudgeValidate j3TestValidate;
  
  private SimpleBooleanProperty testPassed;
  
  private SimpleBooleanProperty testReset;
  
  private SimpleBooleanProperty executing;
  
  @Autowired
  public BaseJudgesHardwareTestWorkerImpl(TkStrikeCommunicationService tkStrikeCommunicationService) {
    this.j1TestValidate = new JudgeValidate();
    this.j2TestValidate = new JudgeValidate();
    this.j3TestValidate = new JudgeValidate();
    this.testPassed = new SimpleBooleanProperty(this, "testPassed", false);
    this.testReset = new SimpleBooleanProperty(this, "testReset", false);
    this.executing = new SimpleBooleanProperty(this, "executing", false);
    this.tkStrikeCommunicationService = tkStrikeCommunicationService;
  }
  
  public void startTest() throws TkStrikeServiceException {
    if (!this.executing.get())
      this.executing.set(true); 
    setCurrentNetworkConfiguration((NE)getNetworkConfigurationService().getNetworkConfigurationEntry());
    this.j1TestValidate.reset();
    this.j2TestValidate.reset();
    this.j3TestValidate.reset();
    this.testPassed.set(false);
    int itemsOfJudge = 6;
    if (!getCurrentNetworkConfiguration().isJudge1Enabled()) {
      this.j1TestValidate.setTechBodyBlue(Boolean.TRUE);
      this.j1TestValidate.setTechBodyRed(Boolean.TRUE);
      this.j1TestValidate.setTechHeadBlue(Boolean.TRUE);
      this.j1TestValidate.setTechHeadRed(Boolean.TRUE);
      this.j1TestValidate.setPunchBlue(Boolean.TRUE);
      this.j1TestValidate.setPunchRed(Boolean.TRUE);
    } 
    if (!getCurrentNetworkConfiguration().isJudge2Enabled()) {
      this.j2TestValidate.setTechBodyBlue(Boolean.TRUE);
      this.j2TestValidate.setTechBodyRed(Boolean.TRUE);
      this.j2TestValidate.setTechHeadBlue(Boolean.TRUE);
      this.j2TestValidate.setTechHeadRed(Boolean.TRUE);
      this.j2TestValidate.setPunchBlue(Boolean.TRUE);
      this.j2TestValidate.setPunchRed(Boolean.TRUE);
    } 
    if (!getCurrentNetworkConfiguration().isJudge3Enabled()) {
      this.j3TestValidate.setTechBodyBlue(Boolean.TRUE);
      this.j3TestValidate.setTechBodyRed(Boolean.TRUE);
      this.j3TestValidate.setTechHeadBlue(Boolean.TRUE);
      this.j3TestValidate.setTechHeadRed(Boolean.TRUE);
      this.j3TestValidate.setPunchBlue(Boolean.TRUE);
      this.j3TestValidate.setPunchRed(Boolean.TRUE);
    } 
    this.tkStrikeCommunicationService.addListener(this);
  }
  
  public void stopTest() {
    if (this.executing.get()) {
      this.executing.set(false);
      this.tkStrikeCommunicationService.removeListener(this);
    } 
  }
  
  public boolean getTestPassed() {
    return this.testPassed.get();
  }
  
  public SimpleBooleanProperty testPassedProperty() {
    return this.testPassed;
  }
  
  public SimpleBooleanProperty testResetProperty() {
    return this.testReset;
  }
  
  public boolean getExecuting() {
    return this.executing.get();
  }
  
  public SimpleBooleanProperty executingProperty() {
    return this.executing;
  }
  
  public void addListener(JudgesHardwareTestWorker.JudgesHardwareTestWorkerListener judgesHardwareTestWorkerListener) {
    this.listeners.add(judgesHardwareTestWorkerListener);
  }
  
  public void removeListener(JudgesHardwareTestWorker.JudgesHardwareTestWorkerListener judgesHardwareTestWorkerListener) {
    this.listeners.remove(judgesHardwareTestWorkerListener);
  }
  
  public void afterPropertiesSet() throws Exception {
    this.testPassed.set(false);
    this.j1TestValidate.valid.addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
            if (t1.booleanValue() && 
              BaseJudgesHardwareTestWorkerImpl.this.j2TestValidate.getValid() && BaseJudgesHardwareTestWorkerImpl.this.j3TestValidate.getValid())
              BaseJudgesHardwareTestWorkerImpl.this._testPassedOk(); 
          }
        });
    this.j2TestValidate.valid.addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
            if (t1.booleanValue() && 
              BaseJudgesHardwareTestWorkerImpl.this.j1TestValidate.getValid() && BaseJudgesHardwareTestWorkerImpl.this.j3TestValidate.getValid())
              BaseJudgesHardwareTestWorkerImpl.this._testPassedOk(); 
          }
        });
    this.j3TestValidate.valid.addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
            if (t1.booleanValue() && 
              BaseJudgesHardwareTestWorkerImpl.this.j1TestValidate.getValid() && BaseJudgesHardwareTestWorkerImpl.this.j2TestValidate.getValid())
              BaseJudgesHardwareTestWorkerImpl.this._testPassedOk(); 
          }
        });
  }
  
  protected void _testPassedOk() {
    this.testPassed.set(true);
    this.testPassed.set(false);
  }
  
  public void hasNewDataEvent(DataEvent dataEvent) {
    if (dataEvent != null) {
      if (dataEvent.getNodeId().equals(getCurrentNetworkConfiguration().getJudge1NodeId())) {
        this.j1TestValidate.setByDataEventDef(dataEvent.getHitValue().intValue());
      } else if (dataEvent.getNodeId().equals(getCurrentNetworkConfiguration().getJudge2NodeId())) {
        this.j2TestValidate.setByDataEventDef(dataEvent.getHitValue().intValue());
      } else if (dataEvent.getNodeId().equals(getCurrentNetworkConfiguration().getJudge3NodeId())) {
        this.j3TestValidate.setByDataEventDef(dataEvent.getHitValue().intValue());
      } 
      fireNewDataEvent(dataEvent);
    } 
  }
  
  private void fireNewDataEvent(final DataEvent newDataEvent) {
    for (JudgesHardwareTestWorker.JudgesHardwareTestWorkerListener listener : this.listeners) {
      ExecutorService es = Executors.newSingleThreadExecutor();
      es.submit(new Runnable() {
            public void run() {
              listener.hasNewJudgesHardwareTestHitEvent(newDataEvent);
            }
          });
      es.shutdown();
    } 
  }
  
  public void hasNewStatusEvent(StatusEvent statusEvent) {}
  
  public void hasChangeNetworkStatusEvent(ChangeNetworkStatusEvent changeNetworkStatusEvent) {}
  
  public void hasChangeNetworkConfigurationEvent(ChangeNetworkConfigurationEvent changeNetworkConfigurationEvent) {}
  
  abstract NetworkConfigurationService getNetworkConfigurationService();
  
  abstract NE getCurrentNetworkConfiguration();
  
  abstract void setCurrentNetworkConfiguration(NE paramNE);
  
  private class JudgeValidate {
    private SimpleBooleanProperty valid = new SimpleBooleanProperty(this, "valid", Boolean.FALSE.booleanValue());
    
    private Boolean techHeadBlue = Boolean.FALSE;
    
    private Boolean techBodyBlue = Boolean.FALSE;
    
    private Boolean punchBlue = Boolean.FALSE;
    
    private Boolean techHeadRed = Boolean.FALSE;
    
    private Boolean techBodyRed = Boolean.FALSE;
    
    private Boolean punchRed = Boolean.FALSE;
    
    public void reset() {
      this.techHeadBlue = Boolean.FALSE;
      this.techBodyBlue = Boolean.FALSE;
      this.punchBlue = Boolean.FALSE;
      this.techHeadRed = Boolean.FALSE;
      this.techBodyRed = Boolean.FALSE;
      this.punchRed = Boolean.FALSE;
    }
    
    public void setByDataEventDef(int dataEventDef) {
      if (43521 == dataEventDef) {
        setTechHeadBlue(Boolean.TRUE);
      } else if (43522 == dataEventDef) {
        setTechBodyBlue(Boolean.TRUE);
      } else if (43552 == dataEventDef) {
        setPunchBlue(Boolean.TRUE);
      } else if (43524 == dataEventDef) {
        setTechHeadRed(Boolean.TRUE);
      } else if (43528 == dataEventDef) {
        setTechBodyRed(Boolean.TRUE);
      } else if (43536 == dataEventDef) {
        setPunchRed(Boolean.TRUE);
      } 
    }
    
    public Boolean getTechHeadBlue() {
      return this.techHeadBlue;
    }
    
    public void setTechHeadBlue(Boolean techHeadBlue) {
      this.techHeadBlue = techHeadBlue;
      _reviseIfValid();
    }
    
    public Boolean getTechBodyBlue() {
      return this.techBodyBlue;
    }
    
    public void setTechBodyBlue(Boolean techBodyBlue) {
      this.techBodyBlue = techBodyBlue;
      _reviseIfValid();
    }
    
    public Boolean getPunchBlue() {
      return this.punchBlue;
    }
    
    public void setPunchBlue(Boolean punchBlue) {
      this.punchBlue = punchBlue;
      _reviseIfValid();
    }
    
    public Boolean getTechHeadRed() {
      return this.techHeadRed;
    }
    
    public void setTechHeadRed(Boolean techHeadRed) {
      this.techHeadRed = techHeadRed;
      _reviseIfValid();
    }
    
    public Boolean getTechBodyRed() {
      return this.techBodyRed;
    }
    
    public void setTechBodyRed(Boolean techBodyRed) {
      this.techBodyRed = techBodyRed;
      _reviseIfValid();
    }
    
    public Boolean getPunchRed() {
      return this.punchRed;
    }
    
    public void setPunchRed(Boolean punchRed) {
      this.punchRed = punchRed;
      _reviseIfValid();
    }
    
    public boolean getValid() {
      return this.valid.get();
    }
    
    public ReadOnlyBooleanProperty validProperty() {
      return (ReadOnlyBooleanProperty)this.valid;
    }
    
    private void _reviseIfValid() {
      this.valid.set((this.techBodyBlue.booleanValue() && this.techHeadBlue.booleanValue() && this.punchBlue.booleanValue() && this.techBodyRed
          .booleanValue() && this.techHeadRed.booleanValue() && this.punchRed.booleanValue()));
    }
    
    private JudgeValidate() {}
  }
}
