package com.xtremis.daedo.tkstrike.communication;

import java.io.Serializable;

public interface NetworkNode extends Serializable {
  String getNodeId();
  
  void setNodeId(String paramString);
  
  long getLastTimestampStatusOk();
  
  void setLastTimestampStatusOk(long paramLong);
  
  double getBatteryPct();
  
  void setBatteryPct(double paramDouble);
  
  Boolean getNodeStatusOk();
  
  void setNodeStatusOk(Boolean paramBoolean);
}
