package org.crm.tkstrike.testnetwork;

public class DataEvent extends TkStrikeCommunicationBaseEvent {
  private String nodeId;
  
  private Integer hitValue;
  
  private DataEventHitType dataEventHitType;
  
  private final String nativePacket;
  
  public DataEvent(Long eventTimestamp, NetworkStatus networkStatus, String nodeId, Integer hitValue, DataEventHitType dataEventHitType) {
    this(eventTimestamp, networkStatus, nodeId, hitValue, dataEventHitType, null);
  }
  
  public DataEvent(Long eventTimestamp, NetworkStatus networkStatus, String nodeId, Integer hitValue, DataEventHitType dataEventHitType, String nativePacket) {
    super(eventTimestamp, networkStatus);
    this.nodeId = nodeId;
    this.hitValue = hitValue;
    this.dataEventHitType = dataEventHitType;
    this.nativePacket = nativePacket;
  }
  
  public String getNodeId() {
    return this.nodeId;
  }
  
  public Integer getHitValue() {
    return this.hitValue;
  }
  
  public String getNativePacket() {
    return this.nativePacket;
  }
  
  public DataEventHitType getDataEventHitType() {
    return this.dataEventHitType;
  }
  
  public enum DataEventHitType {
    BODY, PUNCH, HEAD, JUDGE_TECH_HEAD_BLUE, JUDGE_TECH_BODY_BLUE, JUDGE_PUNCH_BLUE, JUDGE_TECH_HEAD_RED, JUDGE_TECH_BODY_RED, JUDGE_PUNCH_RED;
  }
}
