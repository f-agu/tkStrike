package com.xtremis.daedo.tkstrike.communication;

public class JudgeNode extends BaseNode {
  private static final long serialVersionUID = -5025610908129938581L;
  
  private int judgeNumber;
  
  public JudgeNode(String nodeId, long lastTimestampStatusOk, double batteryPct, Boolean nodeStatusOk, int judgeNumber) {
    super(nodeId, lastTimestampStatusOk, batteryPct, nodeStatusOk);
    this.judgeNumber = judgeNumber;
  }
  
  public JudgeNode(String nodeId, int judgeNumber) {
    super(nodeId);
    this.judgeNumber = judgeNumber;
  }
  
  public int getJudgeNumber() {
    return this.judgeNumber;
  }
  
  public void setJudgeNumber(int judgeNumber) {
    this.judgeNumber = judgeNumber;
  }
}
