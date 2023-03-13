package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.communication.AthleteNode;
import com.xtremis.daedo.tkstrike.communication.ChangeNetworkConfigurationEvent;
import com.xtremis.daedo.tkstrike.communication.ChangeNetworkStatusEvent;
import com.xtremis.daedo.tkstrike.communication.DataEvent;
import com.xtremis.daedo.tkstrike.communication.StatusEvent;
import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationListener;
import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationService;
import com.xtremis.daedo.tkstrike.ei.client.TkStrikeNodeEventsListenerClient;
import com.xtremis.daedo.tkstrike.orm.model.SensorsGroup;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeChangeNetworkStatusEventDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeNetworkConfigurationEventDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeNodeDataEvent;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeNodeEventSource;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeNodeStatusEvent;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.model.ExternalConfigEntry;
import com.xtremis.daedo.tkstrike.ui.model.INetworkConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.NetworkAthletesGroupConfigEntry;
import com.xtremis.daedo.tkstrike.ui.model.NetworkConfigurationEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public class TkStrikeExtraCommunicationServiceImpl implements TkStrikeCommunicationListener, InitializingBean {
  private static final Logger logger = Logger.getLogger("EXTRA_COMMUNICATION");
  
  @Autowired
  private TkStrikeCommunicationService tkStrikeCommunicationService;
  
  @Autowired
  private AppStatusWorker appStatusWorker;
  
  @Autowired
  private MatchWorker matchWorker;
  
  @Autowired
  private ExternalConfigService externalConfigService;
  
  @Autowired
  private TkStrikeNodeEventsListenerClient tkStrikeNodeEventsListenerClient;
  
  private SimpleObjectProperty<SensorsGroup> sensorsGroupSelected = new SimpleObjectProperty(this, "sensorsGroupSelected", SensorsGroup.GROUP1);
  
  private TkStrikeNetworkConfigurationEventDto networkConfigurationEvent = null;
  
  private AthleteNode bodyBlueNode = null;
  
  private AthleteNode headBlueNode = null;
  
  private AthleteNode bodyRedNode = null;
  
  private AthleteNode headRedNode = null;
  
  public void hasNewDataEvent(final DataEvent dataEvent) {
    if (isAValidNodeId(dataEvent.getNodeId())) {
      logger.info("New Data Event ->" + ReflectionToStringBuilder.toString(dataEvent));
      try {
        List<Callable<Void>> tasks = null;
        ExternalConfigEntry externalConfigEntry = this.externalConfigService.getExternalConfigEntry();
        if (externalConfigEntry.getNodeListenersURLs() != null) {
          tasks = new ArrayList<>(externalConfigEntry.getListenersURLs().size());
          for (String listenerURL : externalConfigEntry.getNodeListenersURLs()) {
            tasks.add(new Callable<Void>() {
                  public Void call() throws Exception {
                    TkStrikeNodeDataEvent tkStrikeNodeDataEvent = new TkStrikeNodeDataEvent();
                    BeanUtils.copyProperties(dataEvent, tkStrikeNodeDataEvent);
                    tkStrikeNodeDataEvent.setTkStrikeNodeEventSource(TkStrikeExtraCommunicationServiceImpl.this.getNodeEventSourceByNodeId(dataEvent.getNodeId()));
                    TkStrikeExtraCommunicationServiceImpl.this.tkStrikeNodeEventsListenerClient.sendNewDataEvent(listenerURL, tkStrikeNodeDataEvent);
                    return null;
                  }
                });
          } 
        } 
        if (tasks != null)
          TkStrikeExecutors.executeInParallel(tasks); 
      } catch (Exception e) {
        logger.error("on sendNewMatchConfigured to Listener", e);
      } 
    } 
  }
  
  public void hasNewStatusEvent(final StatusEvent statusEvent) {
    if (isAValidNodeId(statusEvent.getNodeId())) {
      logger.info("New Status Event ->" + ReflectionToStringBuilder.toString(statusEvent));
      try {
        List<Callable<Void>> tasks = null;
        ExternalConfigEntry externalConfigEntry = this.externalConfigService.getExternalConfigEntry();
        if (externalConfigEntry.getNodeListenersURLs() != null) {
          tasks = new ArrayList<>(externalConfigEntry.getListenersURLs().size());
          for (String listenerURL : externalConfigEntry.getNodeListenersURLs()) {
            tasks.add(new Callable<Void>() {
                  public Void call() throws Exception {
                    TkStrikeNodeStatusEvent tkStrikeNodeStatusEvent = new TkStrikeNodeStatusEvent();
                    BeanUtils.copyProperties(statusEvent, tkStrikeNodeStatusEvent);
                    tkStrikeNodeStatusEvent.setTkStrikeNodeEventSource(TkStrikeExtraCommunicationServiceImpl.this.getNodeEventSourceByNodeId(statusEvent.getNodeId()));
                    TkStrikeExtraCommunicationServiceImpl.this.tkStrikeNodeEventsListenerClient.sendNewStatusEvent(listenerURL, tkStrikeNodeStatusEvent);
                    return null;
                  }
                });
          } 
        } 
        if (tasks != null)
          TkStrikeExecutors.executeInParallel(tasks); 
      } catch (Exception e) {
        logger.error("on sendNewMatchConfigured to Listener", e);
      } 
    } 
  }
  
  public void hasChangeNetworkStatusEvent(final ChangeNetworkStatusEvent changeNetworkStatusEvent) {
    if (changeNetworkStatusEvent != null) {
      logger.info("New Change Network Status Event ->" + ReflectionToStringBuilder.toString(changeNetworkStatusEvent));
      try {
        List<Callable<Void>> tasks = null;
        ExternalConfigEntry externalConfigEntry = this.externalConfigService.getExternalConfigEntry();
        if (externalConfigEntry.getNodeListenersURLs() != null) {
          tasks = new ArrayList<>(externalConfigEntry.getListenersURLs().size());
          for (String listenerURL : externalConfigEntry.getNodeListenersURLs()) {
            tasks.add(new Callable<Void>() {
                  public Void call() throws Exception {
                    TkStrikeChangeNetworkStatusEventDto changeNetworkStatusEventDto = new TkStrikeChangeNetworkStatusEventDto();
                    BeanUtils.copyProperties(changeNetworkStatusEvent, changeNetworkStatusEventDto);
                    TkStrikeExtraCommunicationServiceImpl.this.tkStrikeNodeEventsListenerClient.sendNewNetworkStatusEvent(listenerURL, changeNetworkStatusEventDto);
                    return null;
                  }
                });
          } 
        } 
        if (tasks != null)
          TkStrikeExecutors.executeInParallel(tasks); 
      } catch (Exception e) {
        logger.error("on sendNewNetworkStatusEvent to Listener", e);
      } 
    } 
  }
  
  public void hasChangeNetworkConfigurationEvent(ChangeNetworkConfigurationEvent changeNetworkConfigurationEvent) {
    if (changeNetworkConfigurationEvent != null) {
      logger.info("Change Network Configuration Event ->" + ReflectionToStringBuilder.toString(changeNetworkConfigurationEvent));
      updateNetworkConfiguration();
    } 
  }
  
  private boolean isAValidNodeId(String nodeId) {
    if (StringUtils.isNotBlank(nodeId))
      return ((this.bodyBlueNode != null && nodeId.equals(this.bodyBlueNode.getNodeId())) || (this.bodyRedNode != null && nodeId
        .equals(this.bodyRedNode.getNodeId())) || (this.headBlueNode != null && nodeId
        .equals(this.headBlueNode.getNodeId())) || (this.headRedNode != null && nodeId
        .equals(this.headRedNode.getNodeId()))); 
    return false;
  }
  
  private TkStrikeNodeEventSource getNodeEventSourceByNodeId(String nodeId) {
    if (StringUtils.isNotBlank(nodeId)) {
      if (this.bodyBlueNode != null && nodeId.equals(this.bodyBlueNode.getNodeId()))
        return TkStrikeNodeEventSource.BODY_BLUE; 
      if (this.headBlueNode != null && nodeId.equals(this.headBlueNode.getNodeId()))
        return TkStrikeNodeEventSource.HEAD_BLUE; 
      if (this.bodyRedNode != null && nodeId.equals(this.bodyRedNode.getNodeId()))
        return TkStrikeNodeEventSource.BODY_RED; 
      if (this.headRedNode != null && nodeId.equals(this.headRedNode.getNodeId()))
        return TkStrikeNodeEventSource.HEAD_RED; 
    } 
    return null;
  }
  
  private void updateNetworkConfiguration() {
    logger.info("updateNetworkConfiguration() ");
    try {
      NetworkConfigurationEntry networkConfigurationEntry = (NetworkConfigurationEntry)this.appStatusWorker.getNetworkConfigurationEntry();
      if (networkConfigurationEntry != null) {
        _updateNetworkConfiguration((INetworkConfigurationEntry)networkConfigurationEntry);
        if (this.networkConfigurationEvent != null)
          try {
            List<Callable<Void>> tasks = null;
            ExternalConfigEntry externalConfigEntry = this.externalConfigService.getExternalConfigEntry();
            if (externalConfigEntry.getNodeListenersURLs() != null) {
              tasks = new ArrayList<>(externalConfigEntry.getListenersURLs().size());
              for (String listenerURL : externalConfigEntry.getNodeListenersURLs()) {
                tasks.add(new Callable<Void>() {
                      public Void call() throws Exception {
                        TkStrikeExtraCommunicationServiceImpl.this.tkStrikeNodeEventsListenerClient.sendNewNetworkConfigurationEvent(listenerURL, TkStrikeExtraCommunicationServiceImpl.this.networkConfigurationEvent);
                        return null;
                      }
                    });
              } 
            } 
            if (tasks != null)
              TkStrikeExecutors.executeInParallel(tasks); 
          } catch (Exception e) {
            logger.error("on sendNewNetworkStatusEvent to Listener", e);
          }  
      } 
    } catch (TkStrikeServiceException e) {
      logger.error("Update Network Configuration", e);
    } 
  }
  
  private void _updateNetworkConfiguration(INetworkConfigurationEntry networkConfigurationEntry) {
    if (networkConfigurationEntry != null) {
      NetworkConfigurationEntry currNetworkConfigurationEntry = (NetworkConfigurationEntry)networkConfigurationEntry;
      if (!SensorsGroup.GROUP2.equals(this.sensorsGroupSelected.get()) && currNetworkConfigurationEntry
        .getGroup2Enabled().booleanValue()) {
        logger.info("Initialize By Group2 config" + ToStringBuilder.reflectionToString(networkConfigurationEntry.getGroup2Config()));
        initializeByGroupConfig(networkConfigurationEntry.getGroup2Config(), Integer.valueOf(2));
      } else if (SensorsGroup.GROUP2.equals(this.sensorsGroupSelected.get()) && currNetworkConfigurationEntry.getGroup2Enabled().booleanValue()) {
        logger.info("Initialize By Group1 config " + ToStringBuilder.reflectionToString(networkConfigurationEntry.getGroup1Config()));
        initializeByGroupConfig(networkConfigurationEntry.getGroup1Config(), Integer.valueOf(1));
      } else {
        logger.info("No groups disabled");
        this.bodyBlueNode = null;
        this.bodyRedNode = null;
        this.bodyRedNode = null;
        this.headRedNode = null;
      } 
    } 
  }
  
  private void initializeByGroupConfig(NetworkAthletesGroupConfigEntry networkAthletesGroupConfig, Integer groupNumber) {
    this.networkConfigurationEvent = new TkStrikeNetworkConfigurationEventDto();
    if (networkAthletesGroupConfig != null) {
      this.networkConfigurationEvent.setBodySensorsEnabled(networkAthletesGroupConfig.getBodySensorsEnabled());
      this.networkConfigurationEvent.setHeadSensorsEnabled(networkAthletesGroupConfig.getHeadSensorsEnabled());
      if (networkAthletesGroupConfig.getBodySensorsEnabled().booleanValue()) {
        this.bodyBlueNode = new AthleteNode(groupNumber, networkAthletesGroupConfig.getBodyBlueNodeId());
        this.networkConfigurationEvent.setBodyBlueNodeId(this.bodyBlueNode.getNodeId());
        this.bodyRedNode = new AthleteNode(groupNumber, networkAthletesGroupConfig.getBodyRedNodeId());
        this.networkConfigurationEvent.setBodyRedNodeId(this.bodyRedNode.getNodeId());
      } else {
        this.bodyBlueNode = null;
        this.bodyRedNode = null;
        this.networkConfigurationEvent.setBodyBlueNodeId(null);
        this.networkConfigurationEvent.setBodyRedNodeId(null);
      } 
      if (networkAthletesGroupConfig.getHeadSensorsEnabled().booleanValue()) {
        this.headBlueNode = new AthleteNode(groupNumber, networkAthletesGroupConfig.getHeadBlueNodeId());
        this.networkConfigurationEvent.setHeadBlueNodeId(this.headBlueNode.getNodeId());
        this.headRedNode = new AthleteNode(groupNumber, networkAthletesGroupConfig.getHeadRedNodeId());
        this.networkConfigurationEvent.setHeadRedNodeId(this.headRedNode.getNodeId());
      } else {
        this.headBlueNode = null;
        this.headRedNode = null;
        this.networkConfigurationEvent.setHeadBlueNodeId(null);
        this.networkConfigurationEvent.setHeadRedNodeId(null);
      } 
      if (logger.isDebugEnabled()) {
        logger.debug("Athletes config ================================================");
        logger.debug("  bodyBlue = " + ((this.bodyBlueNode != null) ? this.bodyBlueNode.getNodeId() : ""));
        logger.debug("  headBlue = " + ((this.headBlueNode != null) ? this.headBlueNode.getNodeId() : ""));
        logger.debug("  bodyRed = " + ((this.bodyRedNode != null) ? this.bodyRedNode.getNodeId() : ""));
        logger.debug("  headRed = " + ((this.headRedNode != null) ? this.headRedNode.getNodeId() : ""));
        logger.debug("================================================ Athletes config");
      } 
    } 
  }
  
  public void afterPropertiesSet() throws Exception {
    Assert.notNull(this.tkStrikeCommunicationService, "Can't initialize Communication");
    this.tkStrikeCommunicationService.addListener(this);
    logger.info("Started EXTRA Communication Listenner");
    this.appStatusWorker.networkConfigurationChanges().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
            TkStrikeExtraCommunicationServiceImpl.logger.info("Changes on Network Configuration...");
            TkStrikeExtraCommunicationServiceImpl.this.updateNetworkConfiguration();
          }
        });
    this.sensorsGroupSelected.bind((ObservableValue)this.matchWorker.sensorsGroupSelectedProperty());
    this.matchWorker.sensorsGroupSelectedProperty().addListener(new ChangeListener<SensorsGroup>() {
          public void changed(ObservableValue<? extends SensorsGroup> observable, SensorsGroup oldValue, SensorsGroup newValue) {
            TkStrikeExtraCommunicationServiceImpl.logger.info("Changes on Group Selected...");
            TkStrikeExtraCommunicationServiceImpl.this.updateNetworkConfiguration();
          }
        });
  }
}
