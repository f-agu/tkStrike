package com.xtremis.daedo.tkstrike.ui.configuration;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.StringJoiner;
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
import com.xtremis.daedo.tkstrike.tools.Discipline;
import com.xtremis.daedo.tkstrike.tools.TkExtProperties;
import com.xtremis.daedo.tkstrike.tools.TkProperties;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeBaseController;
import com.xtremis.daedo.tkstrike.ui.model.RulesEntry;

import javafx.application.Platform;
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
					updateRules(Discipline.WT);
					if (!setTkStrikeCommunicationTypeValue(TkStrikeCommunicationTypeValue.NORMAL)) {
						showInfoDialog("Updated", "Pathed for WT: " + getUpdateMessage("Hardware required"));
					}
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
					updateRules(Discipline.KIDO);
					if (!setTkStrikeCommunicationTypeValue(TkStrikeCommunicationTypeValue.SIMULATOR)) {
						showInfoDialog("Updated", "Pathed for Kido: " + getUpdateMessage("Hardware disconnected"));
					}
				} catch (TkStrikeCommunicationException e) {
					showErrorDialog(getMessage("title.default.error"), getMessage("message.error.serialComm"));
				}
				return null;
			}
		});
	}

	// ***********************************************

	private boolean setTkStrikeCommunicationTypeValue(TkStrikeCommunicationTypeValue newValue) {
		TkStrikeCommunicationTypeValue currentType = TkStrikeCommunicationTypeUtil.getInstance()
				.getTkStrikeCommunicationType();
		if (currentType == newValue) {
			return false;
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
		return true;
	}

	private void bindControls(Discipline discipline) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if (discipline == Discipline.WT) {
					logger.info("Applies WT points");
					rulesEntry.punchPointsProperty().setValue(1);
					rulesEntry.bodyPointsProperty().setValue(2);
					rulesEntry.headPointsProperty().setValue(3);
					rulesEntry.bodyTechPointsProperty().setValue(2);
					rulesEntry.headTechPointsProperty().setValue(2);
				} else if (discipline == Discipline.KIDO) {
					logger.info("Applies KIDO points");
					rulesEntry.punchPointsProperty().setValue(1);
					rulesEntry.bodyPointsProperty().setValue(2);
					rulesEntry.headPointsProperty().setValue(3);
					rulesEntry.bodyTechPointsProperty().setValue(4);
					rulesEntry.headTechPointsProperty().setValue(5);
				}
			}
		});
	}

	private String getUpdateMessage(String... strings) {
		StringJoiner joiner = new StringJoiner("\n");
		Arrays.stream(strings).forEach(joiner::add);
		joiner.add("GamJom: 5");
		joiner.add("differencialScore: 12");
		return joiner.toString();
	}

	private void updateRules(Discipline discipline) {
		bindControls(discipline);
		TkExtProperties tkExtProperties = TkExtProperties.getInstance();
		try {
			tkExtProperties.setStyle(discipline);
		} catch (IOException e1) {
			logger.info(e1.toString(), e1);
		}

		TkProperties tkProperties = TkProperties.getInstance();
		rulesEntry.setForceMaxGamJomAllowed(Boolean.TRUE);
		rulesEntry.setMaxGamJomAllowed(tkProperties.getGamJom());
		rulesEntry.differencialScoreProperty().set(tkProperties.getDifferentialScore());
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
