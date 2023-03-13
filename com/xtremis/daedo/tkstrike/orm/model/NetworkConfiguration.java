package com.xtremis.daedo.tkstrike.orm.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "TKS_NETWORK_CONFIG")
public class NetworkConfiguration extends BaseEntity implements NetworkConfigurationEntity {
  @Column(name = "NETWORK_WAS_STARTED")
  private Boolean networkWasStarted;
  
  @Column(name = "CHANNEL")
  private Integer channelNumber;
  
  @Column(name = "N_JUDGES")
  private Integer judgesNumber;
  
  @Column(name = "JD_1_NODE_ID")
  private String judge1NodeId;
  
  @Column(name = "JD_2_NODE_ID")
  private String judge2NodeId;
  
  @Column(name = "JD_3_NODE_ID")
  private String judge3NodeId;
  
  @Column(name = "HEAD_TP_ENABLED")
  private Boolean headTechPointEnabled;
  
  @Column(name = "N_GROUPS")
  private Integer groupsNumber;
  
  @Embedded
  @AttributeOverrides({@AttributeOverride(name = "bodySensorsEnabled", column = @Column(name = "G1_HS_ENABLED")), @AttributeOverride(name = "headSensorsEnabled", column = @Column(name = "G1_BS_ENABLED")), @AttributeOverride(name = "bodyBlueNodeId", column = @Column(name = "G1_BB_NODE_ID")), @AttributeOverride(name = "headBlueNodeId", column = @Column(name = "G1_HB_NODE_ID")), @AttributeOverride(name = "bodyRedNodeId", column = @Column(name = "G1_BR_NODE_ID")), @AttributeOverride(name = "headRedNodeId", column = @Column(name = "G1_HR_NODE_ID"))})
  private NetworkAthletesGroupConfig group1Config;
  
  @Column(name = "GROUP2_ENABLED")
  private Boolean group2Enabled;
  
  @Embedded
  @AttributeOverrides({@AttributeOverride(name = "bodySensorsEnabled", column = @Column(name = "G2_HS_ENABLED")), @AttributeOverride(name = "headSensorsEnabled", column = @Column(name = "G2_BS_ENABLED")), @AttributeOverride(name = "bodyBlueNodeId", column = @Column(name = "G2_BB_NODE_ID")), @AttributeOverride(name = "headBlueNodeId", column = @Column(name = "G2_HB_NODE_ID")), @AttributeOverride(name = "bodyRedNodeId", column = @Column(name = "G2_BR_NODE_ID")), @AttributeOverride(name = "headRedNodeId", column = @Column(name = "G2_HR_NODE_ID"))})
  private NetworkAthletesGroupConfig group2Config;
  
  @Embedded
  @AttributeOverrides({@AttributeOverride(name = "bodySensorsEnabled", column = @Column(name = "G3_HS_ENABLED")), @AttributeOverride(name = "headSensorsEnabled", column = @Column(name = "G3_BS_ENABLED")), @AttributeOverride(name = "bodyBlueNodeId", column = @Column(name = "G3_BB_NODE_ID")), @AttributeOverride(name = "headBlueNodeId", column = @Column(name = "G3_HB_NODE_ID")), @AttributeOverride(name = "bodyRedNodeId", column = @Column(name = "G3_BR_NODE_ID")), @AttributeOverride(name = "headRedNodeId", column = @Column(name = "G3_HR_NODE_ID"))})
  private NetworkAthletesGroupConfig group3Config;
  
  @Embedded
  @AttributeOverrides({@AttributeOverride(name = "bodySensorsEnabled", column = @Column(name = "G4_HS_ENABLED")), @AttributeOverride(name = "headSensorsEnabled", column = @Column(name = "G4_BS_ENABLED")), @AttributeOverride(name = "bodyBlueNodeId", column = @Column(name = "G4_BB_NODE_ID")), @AttributeOverride(name = "headBlueNodeId", column = @Column(name = "G4_HB_NODE_ID")), @AttributeOverride(name = "bodyRedNodeId", column = @Column(name = "G4_BR_NODE_ID")), @AttributeOverride(name = "headRedNodeId", column = @Column(name = "G4_HR_NODE_ID"))})
  private NetworkAthletesGroupConfig group4Config;
  
  @Embedded
  @AttributeOverrides({@AttributeOverride(name = "bodySensorsEnabled", column = @Column(name = "G5_HS_ENABLED")), @AttributeOverride(name = "headSensorsEnabled", column = @Column(name = "G5_BS_ENABLED")), @AttributeOverride(name = "bodyBlueNodeId", column = @Column(name = "G5_BB_NODE_ID")), @AttributeOverride(name = "headBlueNodeId", column = @Column(name = "G5_HB_NODE_ID")), @AttributeOverride(name = "bodyRedNodeId", column = @Column(name = "G5_BR_NODE_ID")), @AttributeOverride(name = "headRedNodeId", column = @Column(name = "G5_HR_NODE_ID"))})
  private NetworkAthletesGroupConfig group5Config;
  
  @Embedded
  @AttributeOverrides({@AttributeOverride(name = "bodySensorsEnabled", column = @Column(name = "G6_HS_ENABLED")), @AttributeOverride(name = "headSensorsEnabled", column = @Column(name = "G6_BS_ENABLED")), @AttributeOverride(name = "bodyBlueNodeId", column = @Column(name = "G6_BB_NODE_ID")), @AttributeOverride(name = "headBlueNodeId", column = @Column(name = "G6_HB_NODE_ID")), @AttributeOverride(name = "bodyRedNodeId", column = @Column(name = "G6_BR_NODE_ID")), @AttributeOverride(name = "headRedNodeId", column = @Column(name = "G6_HR_NODE_ID"))})
  private NetworkAthletesGroupConfig group6Config;
  
  public Boolean getNetworkWasStarted() {
    return this.networkWasStarted;
  }
  
  public void setNetworkWasStarted(Boolean networkStarted) {
    this.networkWasStarted = networkStarted;
  }
  
  public Integer getChannelNumber() {
    return this.channelNumber;
  }
  
  public void setChannelNumber(Integer channelNumber) {
    this.channelNumber = channelNumber;
  }
  
  public Integer getJudgesNumber() {
    return this.judgesNumber;
  }
  
  public void setJudgesNumber(Integer judgesNumber) {
    this.judgesNumber = judgesNumber;
  }
  
  public String getJudge1NodeId() {
    return this.judge1NodeId;
  }
  
  public void setJudge1NodeId(String judge1NodeId) {
    this.judge1NodeId = judge1NodeId;
  }
  
  public String getJudge2NodeId() {
    return this.judge2NodeId;
  }
  
  public void setJudge2NodeId(String judge2NodeId) {
    this.judge2NodeId = judge2NodeId;
  }
  
  public String getJudge3NodeId() {
    return this.judge3NodeId;
  }
  
  public void setJudge3NodeId(String judge3NodeId) {
    this.judge3NodeId = judge3NodeId;
  }
  
  public Boolean getHeadTechPointEnabled() {
    return this.headTechPointEnabled;
  }
  
  public void setHeadTechPointEnabled(Boolean headTechPointEnabled) {
    this.headTechPointEnabled = headTechPointEnabled;
  }
  
  public Boolean getGroup2Enabled() {
    return this.group2Enabled;
  }
  
  public void setGroup2Enabled(Boolean group2Enabled) {
    this.group2Enabled = group2Enabled;
  }
  
  public Integer getGroupsNumber() {
    return this.groupsNumber;
  }
  
  public void setGroupsNumber(Integer groupsNumber) {
    this.groupsNumber = groupsNumber;
  }
  
  public NetworkAthletesGroupConfig getGroup1Config() {
    return this.group1Config;
  }
  
  public void setGroup1Config(NetworkAthletesGroupConfig group1Config) {
    this.group1Config = group1Config;
  }
  
  public NetworkAthletesGroupConfig getGroup2Config() {
    return this.group2Config;
  }
  
  public void setGroup2Config(NetworkAthletesGroupConfig group2Config) {
    this.group2Config = group2Config;
  }
  
  public NetworkAthletesGroupConfig getGroup3Config() {
    return this.group3Config;
  }
  
  public void setGroup3Config(NetworkAthletesGroupConfig group3Config) {
    this.group3Config = group3Config;
  }
  
  public NetworkAthletesGroupConfig getGroup4Config() {
    return this.group4Config;
  }
  
  public void setGroup4Config(NetworkAthletesGroupConfig group4Config) {
    this.group4Config = group4Config;
  }
  
  public NetworkAthletesGroupConfig getGroup5Config() {
    return this.group5Config;
  }
  
  public void setGroup5Config(NetworkAthletesGroupConfig group5Config) {
    this.group5Config = group5Config;
  }
  
  public NetworkAthletesGroupConfig getGroup6Config() {
    return this.group6Config;
  }
  
  public void setGroup6Config(NetworkAthletesGroupConfig group6Config) {
    this.group6Config = group6Config;
  }
}
