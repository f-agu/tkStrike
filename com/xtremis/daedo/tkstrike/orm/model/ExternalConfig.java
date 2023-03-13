package com.xtremis.daedo.tkstrike.orm.model;

import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "TKS_EXTERNAL_CONFIG")
public class ExternalConfig extends BaseEntity {
  @ElementCollection
  @CollectionTable(name = "TKS_EVENT_LISTENER_URLS", joinColumns = {@JoinColumn(name = "EXTERNAL_CONFIG_ID")})
  @Column(name = "LISTENER_URL")
  private List<String> listenersURLs;
  
  @ElementCollection
  @CollectionTable(name = "TKS_NODE_EVENT_LISTENER_URLS", joinColumns = {@JoinColumn(name = "EXTERNAL_CONFIG_ID")})
  @Column(name = "LISTENER_URL")
  private List<String> nodeListenersURLs;
  
  @Column(name = "VM_URL")
  private String venueManagementURL;
  
  @Column(name = "VM_RING_NUMBER")
  private String venueManagementRingNumber;
  
  @Column(name = "EXT_SCOREBOARD_BLUEONLEFT")
  private Boolean extScoreboardBlueOnLeft;
  
  @Column(name = "EXT_SCOREBOARD_RESOLUTION")
  @Enumerated(EnumType.STRING)
  private ScreenResolution extScoreboardResolution;
  
  @Column(name = "RTB_IP")
  private String rtBroadcastIp;
  
  @Column(name = "RTB_PORT")
  private Long rtBroadcastPort;
  
  @Column(name = "RTB_RING_NUM")
  private String rtBroadcastRingNumber;
  
  @Column(name = "MATCH_LOG_OUTPUT_DIRECTORY")
  private String matchLogOutputDirectory;
  
  @Column(name = "WT_OVR_URL")
  private String wtOvrUrl;
  
  @Column(name = "WT_OVR_X_API_KEY")
  private String wtOvrXApiKey;
  
  @Column(name = "WT_OVR_MAT")
  private Integer wtOvrMat;
  
  @Column(name = "WT_OVR_UDP_IP")
  private String wtOvrUdpIp;
  
  @Column(name = "WT_OVR_UDP_LISTEN_PORT")
  private Integer wtOvrUdpListenPort;
  
  @Column(name = "WT_OVR_UDP_WRITE_PORT")
  private Integer wtOvrUdpWritePort;
  
  public List<String> getListenersURLs() {
    return this.listenersURLs;
  }
  
  public void setListenersURLs(List<String> listenersURLs) {
    this.listenersURLs = listenersURLs;
  }
  
  public List<String> getNodeListenersURLs() {
    return this.nodeListenersURLs;
  }
  
  public void setNodeListenersURLs(List<String> nodeListenersURLs) {
    this.nodeListenersURLs = nodeListenersURLs;
  }
  
  public String getVenueManagementURL() {
    return this.venueManagementURL;
  }
  
  public void setVenueManagementURL(String venueManagementURL) {
    this.venueManagementURL = venueManagementURL;
  }
  
  public String getVenueManagementRingNumber() {
    return this.venueManagementRingNumber;
  }
  
  public void setVenueManagementRingNumber(String vmRingNumber) {
    this.venueManagementRingNumber = vmRingNumber;
  }
  
  public Boolean getExtScoreboardBlueOnLeft() {
    return this.extScoreboardBlueOnLeft;
  }
  
  public void setExtScoreboardBlueOnLeft(Boolean extScoreboardBlueOnLeft) {
    this.extScoreboardBlueOnLeft = extScoreboardBlueOnLeft;
  }
  
  public ScreenResolution getExtScoreboardResolution() {
    return this.extScoreboardResolution;
  }
  
  public void setExtScoreboardResolution(ScreenResolution extScoreboardResolution) {
    this.extScoreboardResolution = extScoreboardResolution;
  }
  
  public String getRtBroadcastIp() {
    return this.rtBroadcastIp;
  }
  
  public void setRtBroadcastIp(String rtBroadcastIp) {
    this.rtBroadcastIp = rtBroadcastIp;
  }
  
  public Long getRtBroadcastPort() {
    return this.rtBroadcastPort;
  }
  
  public void setRtBroadcastPort(Long rtBroadcastPort) {
    this.rtBroadcastPort = rtBroadcastPort;
  }
  
  public String getRtBroadcastRingNumber() {
    return this.rtBroadcastRingNumber;
  }
  
  public void setRtBroadcastRingNumber(String rtBroadcastRingNumber) {
    this.rtBroadcastRingNumber = rtBroadcastRingNumber;
  }
  
  public String getMatchLogOutputDirectory() {
    return this.matchLogOutputDirectory;
  }
  
  public void setMatchLogOutputDirectory(String matchLogOutputDirectory) {
    this.matchLogOutputDirectory = matchLogOutputDirectory;
  }
  
  public String getWtOvrUrl() {
    return this.wtOvrUrl;
  }
  
  public void setWtOvrUrl(String wtOvrUrl) {
    this.wtOvrUrl = wtOvrUrl;
  }
  
  public String getWtOvrXApiKey() {
    return this.wtOvrXApiKey;
  }
  
  public void setWtOvrXApiKey(String wtOvrXApiKey) {
    this.wtOvrXApiKey = wtOvrXApiKey;
  }
  
  public Integer getWtOvrMat() {
    return this.wtOvrMat;
  }
  
  public void setWtOvrMat(Integer wtOvrMat) {
    this.wtOvrMat = wtOvrMat;
  }
  
  public String getWtOvrUdpIp() {
    return this.wtOvrUdpIp;
  }
  
  public void setWtOvrUdpIp(String wtOvrUdpIp) {
    this.wtOvrUdpIp = wtOvrUdpIp;
  }
  
  public Integer getWtOvrUdpListenPort() {
    return this.wtOvrUdpListenPort;
  }
  
  public void setWtOvrUdpListenPort(Integer wtOvrUdpPort) {
    this.wtOvrUdpListenPort = wtOvrUdpPort;
  }
  
  public Integer getWtOvrUdpWritePort() {
    return this.wtOvrUdpWritePort;
  }
  
  public void setWtOvrUdpWritePort(Integer wtOvrUdpWritePort) {
    this.wtOvrUdpWritePort = wtOvrUdpWritePort;
  }
}
