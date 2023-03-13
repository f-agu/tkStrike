package com.xtremis.daedo.tkstrike.ui.controller;

import com.xtremis.daedo.tkstrike.ui.model.NetworkConfigurationEntry;
import org.apache.log4j.Logger;

public abstract class TkStrikeBaseController extends CommonTkStrikeBaseController<NetworkConfigurationEntry> {
  protected static final Logger logger = Logger.getLogger(TkStrikeBaseController.class);
}
