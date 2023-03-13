package com.xtremis.daedo.tkstrike.ui.model;

import com.xtremis.daedo.tkstrike.orm.model.Entity;
import com.xtremis.daedo.tkstrike.orm.model.UdpEventListener;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.springframework.beans.BeanUtils;

public class UdpEventListenerEntry implements Entry<UdpEventListener> {
  private SimpleStringProperty id = new SimpleStringProperty(this, "id");
  
  private SimpleStringProperty udpEventListenerIp = new SimpleStringProperty(this, "udpEventListenerIp");
  
  private SimpleIntegerProperty udpEventListenerPort = new SimpleIntegerProperty(this, "udpEventListenerPort");
  
  public void fillByEntity(UdpEventListener entity) {
    if (entity != null)
      BeanUtils.copyProperties(entity, this); 
  }
  
  public String getId() {
    return this.id.get();
  }
  
  public SimpleStringProperty idProperty() {
    return this.id;
  }
  
  public void setId(String id) {
    this.id.set(id);
  }
  
  public String getUdpEventListenerIp() {
    return this.udpEventListenerIp.get();
  }
  
  public SimpleStringProperty udpEventListenerIpProperty() {
    return this.udpEventListenerIp;
  }
  
  public void setUdpEventListenerIp(String udpEventListenerIp) {
    this.udpEventListenerIp.set(udpEventListenerIp);
  }
  
  public int getUdpEventListenerPort() {
    return this.udpEventListenerPort.get();
  }
  
  public SimpleIntegerProperty udpEventListenerPortProperty() {
    return this.udpEventListenerPort;
  }
  
  public void setUdpEventListenerPort(int udpEventListenerPort) {
    this.udpEventListenerPort.set(udpEventListenerPort);
  }
  
  public String toString() {
    return "UdpEventListenerEntry{udpEventListenerIp=" + this.udpEventListenerIp
      .get() + ", udpEventListenerPort=" + this.udpEventListenerPort
      .get() + '}';
  }
}
