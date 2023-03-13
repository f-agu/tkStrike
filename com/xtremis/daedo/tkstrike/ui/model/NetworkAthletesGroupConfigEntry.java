package com.xtremis.daedo.tkstrike.ui.model;

import com.xtremis.daedo.tkstrike.communication.NetworkAthletesGroupConfigDto;
import com.xtremis.daedo.tkstrike.orm.model.NetworkAthletesGroupConfigEntity;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class NetworkAthletesGroupConfigEntry {
  public SimpleBooleanProperty headSensorsEnabledProperty = new SimpleBooleanProperty();
  
  public SimpleBooleanProperty bodySensorsEnabledProperty = new SimpleBooleanProperty();
  
  public SimpleStringProperty bodyBlueNodeIdProperty = new SimpleStringProperty();
  
  public SimpleStringProperty headBlueNodeIdProperty = new SimpleStringProperty();
  
  public SimpleStringProperty bodyRedNodeIdProperty = new SimpleStringProperty();
  
  public SimpleStringProperty headRedNodeIdProperty = new SimpleStringProperty();
  
  public void fillByEntity(NetworkAthletesGroupConfigEntity networkAthletesGroupConfig) {
    if (networkAthletesGroupConfig != null) {
      this.bodySensorsEnabledProperty.set(networkAthletesGroupConfig.getBodySensorsEnabled().booleanValue());
      this.headSensorsEnabledProperty.set(networkAthletesGroupConfig.getHeadSensorsEnabled().booleanValue());
      this.bodyBlueNodeIdProperty.set(networkAthletesGroupConfig.getBodyBlueNodeId());
      this.headBlueNodeIdProperty.set(networkAthletesGroupConfig.getHeadBlueNodeId());
      this.bodyRedNodeIdProperty.set(networkAthletesGroupConfig.getBodyRedNodeId());
      this.headRedNodeIdProperty.set(networkAthletesGroupConfig.getHeadRedNodeId());
    } 
  }
  
  public void fillByDto(NetworkAthletesGroupConfigDto dto) {
    if (dto != null) {
      this.bodySensorsEnabledProperty.set(dto.getBodySensorsEnabled().booleanValue());
      this.headSensorsEnabledProperty.set(dto.getHeadSensorsEnabled().booleanValue());
      this.bodyBlueNodeIdProperty.set(dto.getBodyBlueNodeId());
      this.headBlueNodeIdProperty.set(dto.getHeadBlueNodeId());
      this.bodyRedNodeIdProperty.set(dto.getBodyRedNodeId());
      this.headRedNodeIdProperty.set(dto.getHeadRedNodeId());
    } 
  }
  
  public Boolean getHeadSensorsEnabled() {
    return Boolean.valueOf(this.headSensorsEnabledProperty.get());
  }
  
  public Boolean getBodySensorsEnabled() {
    return Boolean.valueOf(this.bodySensorsEnabledProperty.get());
  }
  
  public String getBodyBlueNodeId() {
    return this.bodyBlueNodeIdProperty.get();
  }
  
  public String getHeadBlueNodeId() {
    return this.headBlueNodeIdProperty.get();
  }
  
  public String getBodyRedNodeId() {
    return this.bodyRedNodeIdProperty.get();
  }
  
  public String getHeadRedNodeId() {
    return this.headRedNodeIdProperty.get();
  }
}
