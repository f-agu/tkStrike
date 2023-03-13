package com.xtremis.daedo.tkstrike.communication;

import com.xtremis.daedo.tkstrike.orm.model.SensorsGroup;

public interface GlobalNetworkStatusController extends CommonGlobalNetworkStatusController {
  void doChangeSensorsGroupSelection(SensorsGroup paramSensorsGroup);
  
  void doChangeBlueGroupSelected(Integer paramInteger);
  
  void doChangeRedGroupSelected(Integer paramInteger);
}
