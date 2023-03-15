package com.xtremis.daedo.tkstrike.ui.configuration;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeBaseController;

import javafx.fxml.FXML;
import javafx.scene.Node;


// see com.xtremis.daedo.tkstrike.ui.controller.configuration.ConfigurationNetworkController
@Component
public class CRMPointController extends TkStrikeBaseController {

	protected static final Logger logger = Logger.getLogger(CRMPointController.class);

	@FXML
	private Node rootView;

	@Override
	public void initialize(URL location, ResourceBundle resources) {}

	@Override
	public void afterPropertiesSet() throws Exception {}

	public void doStartNetwork() {

	}

	public void doTryToRecognize() {

	}

}
