package com.xtremis.daedo.tkstrike.ui.model;

import com.xtremis.daedo.tkstrike.communication.NetworkConfigurationDto;
import javafx.beans.property.Property;

public interface INetworkConfigurationEntry<NE extends com.xtremis.daedo.tkstrike.orm.model.NetworkConfigurationEntity> extends Entry<NE> {
  void fillByDto(NetworkConfigurationDto paramNetworkConfigurationDto);
  
  NetworkConfigurationDto getNetworkConfigurationDto();
  
  NE getNetworkConfiguration();
  
  Boolean getNetworkWasStarted();
  
  Property getChannelNumberProperty();
  
  Property getJudgesNumberProperty();
  
  Integer getChannelNumber();
  
  Integer getJudgesNumber();
  
  String getJudge1NodeId();
  
  boolean isJudge1Enabled();
  
  String getJudge2NodeId();
  
  boolean isJudge2Enabled();
  
  String getJudge3NodeId();
  
  boolean isJudge3Enabled();
  
  NetworkAthletesGroupConfigEntry getGroup1Config();
  
  NetworkAthletesGroupConfigEntry getGroup2Config();
}
