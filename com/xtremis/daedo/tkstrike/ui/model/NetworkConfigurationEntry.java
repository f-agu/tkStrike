package com.xtremis.daedo.tkstrike.ui.model;

import com.xtremis.daedo.tkstrike.communication.NetworkAthletesGroupConfigDto;
import com.xtremis.daedo.tkstrike.communication.NetworkConfigurationDto;
import com.xtremis.daedo.tkstrike.orm.model.Entity;
import com.xtremis.daedo.tkstrike.orm.model.NetworkAthletesGroupConfig;
import com.xtremis.daedo.tkstrike.orm.model.NetworkAthletesGroupConfigEntity;
import com.xtremis.daedo.tkstrike.orm.model.NetworkConfiguration;
import com.xtremis.daedo.tkstrike.orm.model.NetworkConfigurationEntity;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import org.springframework.beans.BeanUtils;

public class NetworkConfigurationEntry implements INetworkConfigurationEntry<NetworkConfiguration> {
  public SimpleStringProperty id = new SimpleStringProperty();
  
  private SimpleIntegerProperty groupsNumber = new SimpleIntegerProperty(2);
  
  public SimpleBooleanProperty networkWasStartedProperty = new SimpleBooleanProperty();
  
  public SimpleIntegerProperty channelNumberProperty = new SimpleIntegerProperty();
  
  public SimpleIntegerProperty judgesNumberProperty = new SimpleIntegerProperty();
  
  public SimpleStringProperty judge1NodeIdProperty = new SimpleStringProperty();
  
  public SimpleBooleanProperty judge1EnabledProperty = new SimpleBooleanProperty();
  
  public SimpleStringProperty judge2NodeIdProperty = new SimpleStringProperty();
  
  public SimpleBooleanProperty judge2EnabledProperty = new SimpleBooleanProperty();
  
  public SimpleStringProperty judge3NodeIdProperty = new SimpleStringProperty();
  
  public SimpleBooleanProperty judge3EnabledProperty = new SimpleBooleanProperty();
  
  public SimpleBooleanProperty group2EnabledProperty = new SimpleBooleanProperty();
  
  public SimpleObjectProperty<NetworkAthletesGroupConfigEntry> group1ConfigProperty = new SimpleObjectProperty();
  
  public SimpleObjectProperty<NetworkAthletesGroupConfigEntry> group2ConfigProperty = new SimpleObjectProperty();
  
  public SimpleObjectProperty<NetworkAthletesGroupConfigEntry> group3ConfigProperty = new SimpleObjectProperty();
  
  public SimpleObjectProperty<NetworkAthletesGroupConfigEntry> group4ConfigProperty = new SimpleObjectProperty();
  
  public SimpleObjectProperty<NetworkAthletesGroupConfigEntry> group5ConfigProperty = new SimpleObjectProperty();
  
  public SimpleObjectProperty<NetworkAthletesGroupConfigEntry> group6ConfigProperty = new SimpleObjectProperty();
  
  public void fillByEntity(NetworkConfiguration entity) {
    if (entity != null) {
      this.id.set(entity.getId());
      this.networkWasStartedProperty.set(entity.getNetworkWasStarted().booleanValue());
      this.channelNumberProperty.set(entity.getChannelNumber().intValue());
      this.judgesNumberProperty.set(entity.getJudgesNumber().intValue());
      this.judge1EnabledProperty.set((this.judgesNumberProperty.get() >= 1));
      this.judge2EnabledProperty.set((this.judgesNumberProperty.get() >= 2));
      this.judge3EnabledProperty.set((this.judgesNumberProperty.get() >= 3));
      this.judge1NodeIdProperty.set(entity.getJudge1NodeId());
      this.judge2NodeIdProperty.set(entity.getJudge2NodeId());
      this.judge3NodeIdProperty.set(entity.getJudge3NodeId());
      setGroupsNumber(entity.getGroupsNumber().intValue());
      NetworkAthletesGroupConfigEntry group1 = new NetworkAthletesGroupConfigEntry();
      group1.fillByEntity((NetworkAthletesGroupConfigEntity)entity.getGroup1Config());
      this.group1ConfigProperty.set(group1);
      this.group2EnabledProperty.set(entity.getGroup2Enabled().booleanValue());
      NetworkAthletesGroupConfigEntry group2 = new NetworkAthletesGroupConfigEntry();
      group2.fillByEntity((NetworkAthletesGroupConfigEntity)entity.getGroup2Config());
      this.group2ConfigProperty.set(group2);
      NetworkAthletesGroupConfigEntry group3 = new NetworkAthletesGroupConfigEntry();
      group3.fillByEntity((NetworkAthletesGroupConfigEntity)entity.getGroup3Config());
      this.group3ConfigProperty.set(group3);
      NetworkAthletesGroupConfigEntry group4 = new NetworkAthletesGroupConfigEntry();
      group4.fillByEntity((NetworkAthletesGroupConfigEntity)entity.getGroup4Config());
      this.group4ConfigProperty.set(group4);
      NetworkAthletesGroupConfigEntry group5 = new NetworkAthletesGroupConfigEntry();
      group5.fillByEntity((NetworkAthletesGroupConfigEntity)entity.getGroup5Config());
      this.group5ConfigProperty.set(group5);
      NetworkAthletesGroupConfigEntry group6 = new NetworkAthletesGroupConfigEntry();
      group6.fillByEntity((NetworkAthletesGroupConfigEntity)entity.getGroup6Config());
      this.group6ConfigProperty.set(group6);
    } 
  }
  
  public void fillByDto(NetworkConfigurationDto dto) {
    if (dto != null) {
      this.id.set("1");
      this.networkWasStartedProperty.set(dto.getNetworkWasStarted().booleanValue());
      this.channelNumberProperty.set(dto.getChannelNumber().intValue());
      this.judgesNumberProperty.set(dto.getJudgesNumber().intValue());
      this.judge1EnabledProperty.set((this.judgesNumberProperty.get() >= 1));
      this.judge2EnabledProperty.set((this.judgesNumberProperty.get() >= 2));
      this.judge3EnabledProperty.set((this.judgesNumberProperty.get() >= 3));
      this.judge1NodeIdProperty.set(dto.getJudge1NodeId());
      this.judge2NodeIdProperty.set(dto.getJudge2NodeId());
      this.judge3NodeIdProperty.set(dto.getJudge3NodeId());
      setGroupsNumber(dto.getGroupsNumber().intValue());
      NetworkAthletesGroupConfigEntry group1 = new NetworkAthletesGroupConfigEntry();
      group1.fillByDto(dto.getGroup1Config());
      this.group1ConfigProperty.set(group1);
      this.group2EnabledProperty.set(dto.getGroup2Enabled().booleanValue());
      NetworkAthletesGroupConfigEntry group2 = new NetworkAthletesGroupConfigEntry();
      group2.fillByDto(dto.getGroup2Config());
      group2.bodySensorsEnabledProperty.set(group1.getBodySensorsEnabled().booleanValue());
      group2.headSensorsEnabledProperty.set(group1.getHeadSensorsEnabled().booleanValue());
      this.group2ConfigProperty.set(group2);
      NetworkAthletesGroupConfigEntry group3 = new NetworkAthletesGroupConfigEntry();
      group3.fillByDto(dto.getNetworkAthletesGroupConfig(Integer.valueOf(3)));
      group3.bodySensorsEnabledProperty.set(group1.getBodySensorsEnabled().booleanValue());
      group3.headSensorsEnabledProperty.set(group1.getHeadSensorsEnabled().booleanValue());
      this.group3ConfigProperty.set(group3);
      NetworkAthletesGroupConfigEntry group4 = new NetworkAthletesGroupConfigEntry();
      group4.fillByDto(dto.getNetworkAthletesGroupConfig(Integer.valueOf(4)));
      group4.bodySensorsEnabledProperty.set(group1.getBodySensorsEnabled().booleanValue());
      group4.headSensorsEnabledProperty.set(group1.getHeadSensorsEnabled().booleanValue());
      this.group4ConfigProperty.set(group4);
      NetworkAthletesGroupConfigEntry group5 = new NetworkAthletesGroupConfigEntry();
      group5.fillByDto(dto.getNetworkAthletesGroupConfig(Integer.valueOf(5)));
      group5.bodySensorsEnabledProperty.set(group1.getBodySensorsEnabled().booleanValue());
      group5.headSensorsEnabledProperty.set(group1.getHeadSensorsEnabled().booleanValue());
      this.group5ConfigProperty.set(group5);
      NetworkAthletesGroupConfigEntry group6 = new NetworkAthletesGroupConfigEntry();
      group6.fillByDto(dto.getNetworkAthletesGroupConfig(Integer.valueOf(6)));
      group6.bodySensorsEnabledProperty.set(group1.getBodySensorsEnabled().booleanValue());
      group6.headSensorsEnabledProperty.set(group1.getHeadSensorsEnabled().booleanValue());
      this.group6ConfigProperty.set(group6);
    } 
  }
  
  public NetworkConfigurationDto getNetworkConfigurationDto() {
    NetworkConfigurationDto res = new NetworkConfigurationDto(Integer.valueOf((this.group2EnabledProperty.get() && getGroupsNumber() == 1) ? 2 : getGroupsNumber()));
    BeanUtils.copyProperties(this, res, new String[] { "group1Config", "group2Config", "group3Config", "group4Config", "group5Config", "group6Config" });
    NetworkAthletesGroupConfigDto group1 = new NetworkAthletesGroupConfigDto(Integer.valueOf(1));
    BeanUtils.copyProperties(getGroup1Config(), group1);
    res.setGroup1Config(group1);
    NetworkAthletesGroupConfigDto group2 = new NetworkAthletesGroupConfigDto(Integer.valueOf(2));
    BeanUtils.copyProperties(getGroup2Config(), group2);
    group2.setBodySensorsEnabled(group1.getBodySensorsEnabled());
    group2.setHeadSensorsEnabled(group1.getHeadSensorsEnabled());
    res.setGroup2Config(group2);
    NetworkAthletesGroupConfigDto group3 = new NetworkAthletesGroupConfigDto(Integer.valueOf(3));
    BeanUtils.copyProperties(getGroup3Config(), group3);
    group3.setBodySensorsEnabled(group1.getBodySensorsEnabled());
    group3.setHeadSensorsEnabled(group1.getHeadSensorsEnabled());
    res.setNetworkAthletesGroupConfig(group3);
    NetworkAthletesGroupConfigDto group4 = new NetworkAthletesGroupConfigDto(Integer.valueOf(4));
    BeanUtils.copyProperties(getGroup4Config(), group4);
    group4.setBodySensorsEnabled(group1.getBodySensorsEnabled());
    group4.setHeadSensorsEnabled(group1.getHeadSensorsEnabled());
    res.setNetworkAthletesGroupConfig(group4);
    NetworkAthletesGroupConfigDto group5 = new NetworkAthletesGroupConfigDto(Integer.valueOf(5));
    BeanUtils.copyProperties(getGroup5Config(), group5);
    group5.setBodySensorsEnabled(group1.getBodySensorsEnabled());
    group5.setHeadSensorsEnabled(group1.getHeadSensorsEnabled());
    res.setNetworkAthletesGroupConfig(group5);
    NetworkAthletesGroupConfigDto group6 = new NetworkAthletesGroupConfigDto(Integer.valueOf(6));
    BeanUtils.copyProperties(getGroup6Config(), group6);
    group6.setBodySensorsEnabled(group1.getBodySensorsEnabled());
    group6.setHeadSensorsEnabled(group1.getHeadSensorsEnabled());
    res.setNetworkAthletesGroupConfig(group6);
    res.calculateNumberOfGroups();
    return res;
  }
  
  public NetworkConfiguration getNetworkConfiguration() {
    NetworkConfiguration res = new NetworkConfiguration();
    BeanUtils.copyProperties(this, res, new String[] { "version", "id", "group1Config", "group2Config" });
    NetworkAthletesGroupConfig group1 = new NetworkAthletesGroupConfig();
    BeanUtils.copyProperties(getGroup1Config(), group1);
    res.setGroup1Config(group1);
    NetworkAthletesGroupConfig group2 = new NetworkAthletesGroupConfig();
    BeanUtils.copyProperties(getGroup2Config(), group2);
    res.setGroup2Config(group2);
    NetworkAthletesGroupConfig group3 = new NetworkAthletesGroupConfig();
    BeanUtils.copyProperties(getGroup3Config(), group3);
    group3.setBodySensorsEnabled(group1.getBodySensorsEnabled());
    group3.setHeadSensorsEnabled(group1.getHeadSensorsEnabled());
    res.setGroup3Config(group3);
    NetworkAthletesGroupConfig group4 = new NetworkAthletesGroupConfig();
    BeanUtils.copyProperties(getGroup4Config(), group4);
    group4.setBodySensorsEnabled(group1.getBodySensorsEnabled());
    group4.setHeadSensorsEnabled(group1.getHeadSensorsEnabled());
    res.setGroup4Config(group4);
    NetworkAthletesGroupConfig group5 = new NetworkAthletesGroupConfig();
    BeanUtils.copyProperties(getGroup5Config(), group5);
    group5.setBodySensorsEnabled(group1.getBodySensorsEnabled());
    group5.setHeadSensorsEnabled(group1.getHeadSensorsEnabled());
    res.setGroup5Config(group5);
    NetworkAthletesGroupConfig group6 = new NetworkAthletesGroupConfig();
    BeanUtils.copyProperties(getGroup6Config(), group6);
    group6.setBodySensorsEnabled(group1.getBodySensorsEnabled());
    group6.setHeadSensorsEnabled(group1.getHeadSensorsEnabled());
    res.setGroup6Config(group6);
    return res;
  }
  
  public String getId() {
    return this.id.get();
  }
  
  public int getGroupsNumber() {
    return this.groupsNumber.get();
  }
  
  public Property groupsNumberProperty() {
    return (Property)this.groupsNumber;
  }
  
  public void setGroupsNumber(int groupsNumber) {
    this.groupsNumber.set(groupsNumber);
  }
  
  public Boolean getNetworkWasStarted() {
    return Boolean.valueOf(this.networkWasStartedProperty.get());
  }
  
  public Property getChannelNumberProperty() {
    return (Property)this.channelNumberProperty;
  }
  
  public Property getJudgesNumberProperty() {
    return (Property)this.judgesNumberProperty;
  }
  
  public Integer getChannelNumber() {
    return Integer.valueOf(this.channelNumberProperty.get());
  }
  
  public Integer getJudgesNumber() {
    return Integer.valueOf(this.judgesNumberProperty.get());
  }
  
  public String getJudge1NodeId() {
    return this.judge1NodeIdProperty.get();
  }
  
  public String getJudge2NodeId() {
    return this.judge2NodeIdProperty.get();
  }
  
  public String getJudge3NodeId() {
    return this.judge3NodeIdProperty.get();
  }
  
  public boolean isJudge1Enabled() {
    return this.judge1EnabledProperty.get();
  }
  
  public boolean isJudge2Enabled() {
    return this.judge2EnabledProperty.get();
  }
  
  public boolean isJudge3Enabled() {
    return this.judge3EnabledProperty.get();
  }
  
  public NetworkAthletesGroupConfigEntry getGroupConfig(Integer groupNumber) {
    switch (groupNumber.intValue()) {
      case 1:
        return getGroup1Config();
      case 2:
        return getGroup2Config();
      case 3:
        return getGroup3Config();
      case 4:
        return getGroup4Config();
      case 5:
        return getGroup5Config();
      case 6:
        return getGroup6Config();
    } 
    return null;
  }
  
  public NetworkAthletesGroupConfigEntry getGroup1Config() {
    return (NetworkAthletesGroupConfigEntry)this.group1ConfigProperty.get();
  }
  
  public Boolean getGroup2Enabled() {
    return Boolean.valueOf(this.group2EnabledProperty.get());
  }
  
  public NetworkAthletesGroupConfigEntry getGroup2Config() {
    return (NetworkAthletesGroupConfigEntry)this.group2ConfigProperty.get();
  }
  
  public NetworkAthletesGroupConfigEntry getGroup3Config() {
    return (NetworkAthletesGroupConfigEntry)this.group3ConfigProperty.get();
  }
  
  public NetworkAthletesGroupConfigEntry getGroup4Config() {
    return (NetworkAthletesGroupConfigEntry)this.group4ConfigProperty.get();
  }
  
  public NetworkAthletesGroupConfigEntry getGroup5Config() {
    return (NetworkAthletesGroupConfigEntry)this.group5ConfigProperty.get();
  }
  
  public NetworkAthletesGroupConfigEntry getGroup6Config() {
    return (NetworkAthletesGroupConfigEntry)this.group6ConfigProperty.get();
  }
}
