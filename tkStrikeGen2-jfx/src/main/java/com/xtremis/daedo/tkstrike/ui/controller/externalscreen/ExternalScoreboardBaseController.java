package com.xtremis.daedo.tkstrike.ui.controller.externalscreen;

import com.xtremis.daedo.tkstrike.ui.controller.BaseScoreboardController;
import com.xtremis.daedo.tkstrike.ui.controller.RoundCountdownController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class ExternalScoreboardBaseController extends BaseScoreboardController implements ExternalScoreboardController {
  private boolean enabled = false;
  
  @Autowired
  @Qualifier("EXTERNAL")
  private RoundCountdownController roundCountdownController;
  
  protected RoundCountdownController getRoundCountdownController() {
    return this.roundCountdownController;
  }
  
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
  
  protected final void _internalOnWindowCloseEvent() {}
  
  final Delta dragDelta = new Delta();
  
  class Delta {
    double x;
    
    double y;
  }
  
  protected final void _internalAfterPropertiesSet() throws Exception {}
}
