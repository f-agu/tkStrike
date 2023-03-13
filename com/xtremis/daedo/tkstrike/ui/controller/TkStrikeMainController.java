package com.xtremis.daedo.tkstrike.ui.controller;

import javafx.fxml.Initializable;
import org.springframework.beans.factory.InitializingBean;

public interface TkStrikeMainController extends Initializable, InitializingBean, TkStrikeController {
  void setDatabaseInitializedOnStart(Boolean paramBoolean);
  
  void exitTkStrike();
  
  boolean confirmExitTkStrike();
}
