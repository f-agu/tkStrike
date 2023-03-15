package com.xtremis.daedo.tkstrike.ui.configuration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationException;
import com.xtremis.daedo.tkstrike.configuration.TkStrikeCommunicationTypeUtil;
import com.xtremis.daedo.tkstrike.configuration.TkStrikeCommunicationTypeValue;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeBaseController;

import javafx.fxml.FXML;
import javafx.scene.Node;


// see com.xtremis.daedo.tkstrike.ui.controller.configuration.ConfigurationNetworkController
@Component
public class CRMDisciplineController extends TkStrikeBaseController {

	protected static final Logger logger = Logger.getLogger(CRMDisciplineController.class);

	@FXML
	private Node rootView;

	@Override
	public void initialize(URL location, ResourceBundle resources) {}

	@Override
	public void afterPropertiesSet() throws Exception {}

	public void doPatchWT() {
		TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				try {
					setTkStrikeCommunicationTypeValue(TkStrikeCommunicationTypeValue.NORMAL);
				} catch(TkStrikeCommunicationException e) {
					showErrorDialog(getMessage("title.default.error"),
							getMessage("message.error.serialComm"));
				}
				return null;
			}
		});
	}

	public void doPatchKido() {
		TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				try {
					setTkStrikeCommunicationTypeValue(TkStrikeCommunicationTypeValue.SIMULATOR);
				} catch(TkStrikeCommunicationException e) {
					showErrorDialog(getMessage("title.default.error"),
							getMessage("message.error.serialComm"));
				}
				return null;
			}
		});
	}

	// ***********************************************

	private void setTkStrikeCommunicationTypeValue(TkStrikeCommunicationTypeValue newValue) {
		TkStrikeCommunicationTypeValue currentType = TkStrikeCommunicationTypeUtil.getInstance().getTkStrikeCommunicationType();
		// TkStrikeCommunicationTypeValue newValue = TkStrikeCommunicationTypeValue.SIMULATOR;
		if(currentType == newValue) {
			return;
		}
		try {
			TkStrikeCommunicationTypeUtil.getInstance().setTkStrikeCommunicationType(newValue);
		} catch(Exception e) {
			manageException(e, "ChangeCommunicationType", null);
		}
		getAppStatusWorker().doForceExitTkStrike();
	}
}
