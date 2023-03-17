package com.xtremis.daedo.tkstrike.ui.configuration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.xtremis.daedo.tkstrike.communication.CommunicationHelper;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeBaseController;

import javafx.fxml.FXML;
import javafx.scene.Node;


// see com.xtremis.daedo.tkstrike.ui.controller.configuration.ConfigurationNetworkController
@Component
public class CRMTestNetworkController extends TkStrikeBaseController {

	protected static final Logger logger = Logger.getLogger(CRMTestNetworkController.class);

	@FXML
	private Node rootView;

	@Override
	public void initialize(URL location, ResourceBundle resources) {}

	@Override
	public void afterPropertiesSet() throws Exception {}

	public void doTestNetwork() {
		TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				System.out.println("===================");
				new CommunicationHelper().testComm();
				return null;
			}
		});
	}

	public void doTryToRecognize() {

	}

}
