package com.xtremis.daedo.tkstrike.ui.controller;

import com.xtremis.daedo.tkstrike.ui.model.FormValidationError;
import java.util.LinkedHashSet;
import javafx.application.HostServices;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.stage.WindowEvent;

public interface TkStrikeController {
  Node getRootView();
  
  void onWindowShowEvent();
  
  EventHandler<WindowEvent> getOnWindowCloseEventHandler();
  
  LinkedHashSet<FormValidationError> validateForm();
  
  boolean isFormValid();
  
  HostServices getHostServices();
  
  void setHostServices(HostServices paramHostServices);
}
