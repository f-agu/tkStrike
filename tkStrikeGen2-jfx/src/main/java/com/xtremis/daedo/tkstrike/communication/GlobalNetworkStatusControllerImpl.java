package com.xtremis.daedo.tkstrike.communication;

import com.xtremis.daedo.tkstrike.orm.model.SensorsGroup;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class GlobalNetworkStatusControllerImpl extends BaseCommonGlobalNetworkStatusControllerImpl implements GlobalNetworkStatusController, TkStrikeCommunicationListener {
  private SimpleObjectProperty<SensorsGroup> sensorsGroupSelected = new SimpleObjectProperty(this, "sensorsGroupSelected", SensorsGroup.GROUP1);
  
  private AthleteNode bodyBlueNode = null;
  
  private AthleteNode headBlueNode = null;
  
  private AthleteNode bodyRedNode = null;
  
  private AthleteNode headRedNode = null;
  
  @Autowired
  public GlobalNetworkStatusControllerImpl(TkStrikeCommunicationService tkStrikeCommunicationService) {
    super(tkStrikeCommunicationService);
  }
  
  boolean _validateAthletesNodes(long currentTime) {
    boolean allOk = true;
    if (!validateNode(this.bodyBlueNode, currentTime))
      allOk = false; 
    if (!validateNode(this.bodyRedNode, currentTime))
      allOk = false; 
    if (!validateNode(this.headBlueNode, currentTime))
      allOk = false; 
    if (!validateNode(this.headRedNode, currentTime))
      allOk = false; 
    return allOk;
  }
  
  void _initializeAthletesNodes(NetworkConfigurationDto networkConfigurationDto) {
    if (gnscLogger.isDebugEnabled())
      gnscLogger.debug("Initialize By Group config " + this.sensorsGroupSelected.get() + " - " + ToStringBuilder.reflectionToString(networkConfigurationDto.getGroup2Config())); 
    switch ((SensorsGroup)this.sensorsGroupSelected.get()) {
      case GROUP1:
        initializeByGroupConfig(networkConfigurationDto.getGroup1Config());
        break;
      case GROUP2:
        initializeByGroupConfig(networkConfigurationDto.getGroup2Config());
        break;
      case GROUP3:
        initializeByGroupConfig(networkConfigurationDto.getNetworkAthletesGroupConfig(Integer.valueOf(3)));
        break;
      case GROUP4:
        initializeByGroupConfig(networkConfigurationDto.getNetworkAthletesGroupConfig(Integer.valueOf(4)));
        break;
      case GROUP5:
        initializeByGroupConfig(networkConfigurationDto.getNetworkAthletesGroupConfig(Integer.valueOf(5)));
        break;
      case GROUP6:
        initializeByGroupConfig(networkConfigurationDto.getNetworkAthletesGroupConfig(Integer.valueOf(6)));
        break;
    } 
  }
  
  private void initializeByGroupConfig(NetworkAthletesGroupConfigDto networkAthletesGroupConfig) {
    if (networkAthletesGroupConfig != null) {
      if (networkAthletesGroupConfig.getBodySensorsEnabled().booleanValue()) {
        this.bodyBlueNode = new AthleteNode(networkAthletesGroupConfig.getGroupNumber(), networkAthletesGroupConfig.getBodyBlueNodeId());
        this.nodesInNetwork++;
        this.bodyRedNode = new AthleteNode(networkAthletesGroupConfig.getGroupNumber(), networkAthletesGroupConfig.getBodyRedNodeId());
        this.nodesInNetwork++;
      } else {
        this.bodyBlueNode = null;
        this.bodyRedNode = null;
      } 
      if (networkAthletesGroupConfig.getHeadSensorsEnabled().booleanValue()) {
        this.headBlueNode = new AthleteNode(networkAthletesGroupConfig.getGroupNumber(), networkAthletesGroupConfig.getHeadBlueNodeId());
        this.nodesInNetwork++;
        this.headRedNode = new AthleteNode(networkAthletesGroupConfig.getGroupNumber(), networkAthletesGroupConfig.getHeadRedNodeId());
        this.nodesInNetwork++;
      } else {
        this.headBlueNode = null;
        this.headRedNode = null;
      } 
      if (gnscLogger.isDebugEnabled()) {
        gnscLogger.debug("Athletes config ================================================");
        gnscLogger.debug("  bodyBlue = " + ((this.bodyBlueNode != null) ? this.bodyBlueNode.getNodeId() : ""));
        gnscLogger.debug("  headBlue = " + ((this.headBlueNode != null) ? this.headBlueNode.getNodeId() : ""));
        gnscLogger.debug("  bodyRed = " + ((this.bodyRedNode != null) ? this.bodyRedNode.getNodeId() : ""));
        gnscLogger.debug("  headRed = " + ((this.headRedNode != null) ? this.headRedNode.getNodeId() : ""));
        gnscLogger.debug("================================================ Athletes config");
      } 
    } 
  }
  
  public void doChangeSensorsGroupSelection(SensorsGroup newSensorsGroupSelection) {
    if (newSensorsGroupSelection != null)
      this.sensorsGroupSelected.set(newSensorsGroupSelection); 
  }
  
  public void doChangeBlueGroupSelected(Integer newGroupSelected) {
    NetworkAthletesGroupConfigDto networkAthletesGroupConfig = this.networkConfigurationDto.getNetworkAthletesGroupConfig(newGroupSelected);
    if (networkAthletesGroupConfig.getBodySensorsEnabled().booleanValue()) {
      this.bodyBlueNode = new AthleteNode(newGroupSelected, networkAthletesGroupConfig.getBodyBlueNodeId());
    } else {
      this.bodyBlueNode = null;
    } 
    if (networkAthletesGroupConfig.getHeadSensorsEnabled().booleanValue()) {
      this.headBlueNode = new AthleteNode(newGroupSelected, networkAthletesGroupConfig.getHeadBlueNodeId());
    } else {
      this.headBlueNode = null;
    } 
    if (this.schedulerValidateNetwork != null)
      this.schedulerValidateNetwork.shutdownNow(); 
    initializeScheduleValidateNetwork();
  }
  
  public void doChangeRedGroupSelected(Integer newGroupSelected) {
    NetworkAthletesGroupConfigDto networkAthletesGroupConfig = this.networkConfigurationDto.getNetworkAthletesGroupConfig(newGroupSelected);
    if (networkAthletesGroupConfig.getBodySensorsEnabled().booleanValue()) {
      this.bodyRedNode = new AthleteNode(newGroupSelected, networkAthletesGroupConfig.getBodyRedNodeId());
    } else {
      this.bodyRedNode = null;
    } 
    if (networkAthletesGroupConfig.getHeadSensorsEnabled().booleanValue()) {
      this.headRedNode = new AthleteNode(newGroupSelected, networkAthletesGroupConfig.getHeadRedNodeId());
    } else {
      this.headRedNode = null;
    } 
    if (this.schedulerValidateNetwork != null)
      this.schedulerValidateNetwork.shutdownNow(); 
    initializeScheduleValidateNetwork();
  }
  
  BaseNode _lookup4NodeInAthletes(String nodeId) {
    if (this.bodyBlueNode != null && nodeId.equals(this.bodyBlueNode.getNodeId()))
      return this.bodyBlueNode; 
    if (this.headBlueNode != null && nodeId.equals(this.headBlueNode.getNodeId()))
      return this.headBlueNode; 
    if (this.bodyRedNode != null && nodeId.equals(this.bodyRedNode.getNodeId()))
      return this.bodyRedNode; 
    if (this.headRedNode != null && nodeId.equals(this.headRedNode.getNodeId()))
      return this.headRedNode; 
    return null;
  }
  
  void _afterPropertiesSet() throws Exception {
    this.sensorsGroupSelected.addListener(new ChangeListener<SensorsGroup>() {
          public void changed(ObservableValue<? extends SensorsGroup> observable, SensorsGroup oldValue, SensorsGroup newValue) {
            if (newValue != null && GlobalNetworkStatusControllerImpl.this.networkConfigurationDto != null) {
              GlobalNetworkStatusControllerImpl.this.updateNetworkConfiguration(GlobalNetworkStatusControllerImpl.this.networkConfigurationDto);
              if (GlobalNetworkStatusControllerImpl.this.schedulerValidateNetwork != null)
                GlobalNetworkStatusControllerImpl.this.schedulerValidateNetwork.shutdownNow(); 
              GlobalNetworkStatusControllerImpl.this.initializeScheduleValidateNetwork();
            } 
          }
        });
  }
}
