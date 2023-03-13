package com.xtremis.daedo.tkstrike.ui.controller.externalscreen;

import com.xtremis.daedo.tkstrike.ui.controller.hardwaretest.BaseNewHardwareTestController;
import java.net.URL;
import java.util.ResourceBundle;
import org.springframework.stereotype.Component;

@Component
public class ExternalHardwareTestController extends BaseNewHardwareTestController {
  protected boolean isExternal() {
    return true;
  }
  
  protected void _onWindowShowEvent() {}
  
  public void initialize(URL url, ResourceBundle resourceBundle) {}
  
  protected void _afterPropertiesSet() throws Exception {}
}
