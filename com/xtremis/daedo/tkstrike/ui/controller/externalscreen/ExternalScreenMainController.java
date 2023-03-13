package com.xtremis.daedo.tkstrike.ui.controller.externalscreen;

import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeController;
import com.xtremis.daedo.tkstrike.ui.model.NetworkConfigurationEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExternalScreenMainController extends CommonExternalScreenMainController<NetworkConfigurationEntry, ExternalScoreboardController, ExternalHardwareTestController, ExternalHardwareTestController> {
  @Autowired
  private ExternalScoreboardController externalScoreboardController;
  
  @Autowired
  private ExternalHardwareTestController externalHardwareTestController;
  
  ExternalScoreboardController getExternalScoreboardController() {
    return this.externalScoreboardController;
  }
  
  ExternalHardwareTestController getExternalHTAthletesController() {
    return this.externalHardwareTestController;
  }
  
  ExternalHardwareTestController getExternalHTJudgesController() {
    return this.externalHardwareTestController;
  }
}
