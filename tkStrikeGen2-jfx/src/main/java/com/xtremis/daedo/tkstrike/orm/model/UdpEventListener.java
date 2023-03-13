package com.xtremis.daedo.tkstrike.orm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "TKS_UDP_EVENT_LISTENER")
public class UdpEventListener extends BaseEntity {
  private static final long serialVersionUID = 2577610576382396086L;
  
  @Column(name = "LISTENER_IP")
  private String udpEventListenerIp;
  
  @Column(name = "LISTENER_PORT")
  private Integer udpEventListenerPort;
  
  public String getUdpEventListenerIp() {
    return this.udpEventListenerIp;
  }
  
  public void setUdpEventListenerIp(String udpEventListenerIp) {
    this.udpEventListenerIp = udpEventListenerIp;
  }
  
  public Integer getUdpEventListenerPort() {
    return this.udpEventListenerPort;
  }
  
  public void setUdpEventListenerPort(Integer udpEventListenerPort) {
    this.udpEventListenerPort = udpEventListenerPort;
  }
}
