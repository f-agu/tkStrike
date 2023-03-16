package com.xtremis.daedo.tkstrike.ui.configuration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionSystemException;

import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationException;
import com.xtremis.daedo.tkstrike.configuration.TkStrikeCommunicationTypeUtil;
import com.xtremis.daedo.tkstrike.configuration.TkStrikeCommunicationTypeValue;
import com.xtremis.daedo.tkstrike.orm.model.Rules;
import com.xtremis.daedo.tkstrike.service.RulesService;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeBaseController;
import com.xtremis.daedo.tkstrike.ui.model.RulesEntry;

import javafx.fxml.FXML;
import javafx.scene.Node;

// see com.xtremis.daedo.tkstrike.ui.controller.configuration.ConfigurationNetworkController
@Component
public class CRMDisciplineController extends TkStrikeBaseController {

	protected static final Logger logger = Logger.getLogger(CRMDisciplineController.class);

	@Autowired
	private RulesService<Rules, RulesEntry> rulesService;

	@FXML
	private Node rootView;

	protected RulesEntry rulesEntry = new RulesEntry();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	}

	@Override
	public void onWindowShowEvent() {
		super.onWindowShowEvent();
		TkStrikeExecutors.executeInThreadPool(new Runnable() {

			@Override
			public void run() {
				rulesEntry = (RulesEntry) getAppStatusWorker().getRulesEntry();
			}
		});
	}

	public void doPatchWT() {
		TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				try {
					updateRules();
					setTkStrikeCommunicationTypeValue(TkStrikeCommunicationTypeValue.NORMAL);
				} catch (TkStrikeCommunicationException e) {
					showErrorDialog(getMessage("title.default.error"), getMessage("message.error.serialComm"));
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
					updateRules();
					setTkStrikeCommunicationTypeValue(TkStrikeCommunicationTypeValue.SIMULATOR);
				} catch (TkStrikeCommunicationException e) {
					showErrorDialog(getMessage("title.default.error"), getMessage("message.error.serialComm"));
				}
				return null;
			}
		});
	}

	// ***********************************************

	private void setTkStrikeCommunicationTypeValue(TkStrikeCommunicationTypeValue newValue) {
		TkStrikeCommunicationTypeValue currentType = TkStrikeCommunicationTypeUtil.getInstance()
				.getTkStrikeCommunicationType();
		if (currentType == newValue) {
			return;
		}
		try {
			TkStrikeCommunicationTypeUtil.getInstance().setTkStrikeCommunicationType(newValue);
		} catch (Exception e) {
			manageException(e, "ChangeCommunicationType", null);
		}
		showInfoDialog("Restart", "tkStrike will stop in few seconds. You need to restart it");
		try {
			TimeUnit.SECONDS.sleep(1L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		getAppStatusWorker().doForceExitTkStrike();
	}

	private void updateRules() {
		rulesEntry.setForceMaxGamJomAllowed(Boolean.TRUE);
		rulesEntry.setMaxGamJomAllowed(5);
		try {
			rulesService.update(rulesEntry);
		} catch (TransactionSystemException e) {
			// ignore
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			showErrorDialog(getMessage("title.default.error"), e.toString());
		}
	}
}
