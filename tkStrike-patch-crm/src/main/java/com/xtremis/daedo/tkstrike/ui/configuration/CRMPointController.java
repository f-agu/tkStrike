package com.xtremis.daedo.tkstrike.ui.configuration;

import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionSystemException;

import com.xtremis.daedo.tkstrike.orm.model.Rules;
import com.xtremis.daedo.tkstrike.service.RulesService;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeBaseController;
import com.xtremis.daedo.tkstrike.ui.model.FormValidationError;
import com.xtremis.daedo.tkstrike.ui.model.RulesEntry;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.stage.WindowEvent;
import javafx.util.converter.NumberStringConverter;


// see com.xtremis.daedo.tkstrike.ui.controller.configuration.ConfigurationNetworkController
@Component
public class CRMPointController extends TkStrikeBaseController {

	protected static final Logger logger = Logger.getLogger(CRMPointController.class);

	@Autowired
	private RulesService<Rules, RulesEntry> rulesService;

	@FXML
	private Node rootView;

	@FXML
	private TextField txtPoint1_6;

	@FXML
	private TextField txtPoint2_7;

	@FXML
	private TextField txtPoint3_8;

	@FXML
	private TextField txtPoint4_9;

	@FXML
	private TextField txtPoint5_0;

	protected RulesEntry rulesEntry = new RulesEntry();

	@Override
	public void initialize(URL location, ResourceBundle resources) {}

	@Override
	public void afterPropertiesSet() throws Exception {}

	public void doStartNetwork() {

	}

	public void doTryToRecognize() {

	}

	@Override
	protected Collection<Control> getFormControls() {
		return FXCollections.observableArrayList(new Control[] {
				txtPoint1_6,
				txtPoint2_7,
				txtPoint3_8,
				txtPoint4_9,
				txtPoint5_0});
	}

	@Override
	public LinkedHashSet<FormValidationError> validateForm() {
		LinkedHashSet<FormValidationError> res = new LinkedHashSet<>();
		checkIsDigitBetween1And5(res, txtPoint1_6, "point1or6");
		checkIsDigitBetween1And5(res, txtPoint2_7, "point2or7");
		checkIsDigitBetween1And5(res, txtPoint3_8, "point3or8");
		checkIsDigitBetween1And5(res, txtPoint4_9, "point4or9");
		checkIsDigitBetween1And5(res, txtPoint5_0, "point5or0");
		return res.isEmpty() ? null : res;
	}

	@Override
	public EventHandler<WindowEvent> getOnWindowCloseEventHandler() {
		return new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent windowEvent) {
				try {
					rulesService.update(rulesEntry);
				} catch(TransactionSystemException e) {
					// ignore
				} catch(Exception e) {
					logger.info(e.getMessage(), e);
					showErrorDialog(getMessage("title.default.error"), e.toString());
				}
			}
		};
	}

	@Override
	public void onWindowShowEvent() {
		super.onWindowShowEvent();
		TkStrikeExecutors.executeInThreadPool(new Runnable() {

			@Override
			public void run() {
				rulesEntry = (RulesEntry)getAppStatusWorker().getRulesEntry();
				bindControls();
			}
		});
	}

	// *************************************

	protected final void bindControls() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				txtPoint1_6.textProperty().bindBidirectional(rulesEntry.bodyPointsProperty(), new NumberStringConverter());
				txtPoint2_7.textProperty().bindBidirectional(rulesEntry.bodyTechPointsProperty(), new NumberStringConverter());
				txtPoint3_8.textProperty().bindBidirectional(rulesEntry.headPointsProperty(), new NumberStringConverter());
				txtPoint4_9.textProperty().bindBidirectional(rulesEntry.headTechPointsProperty(), new NumberStringConverter());
				txtPoint5_0.textProperty().bindBidirectional(rulesEntry.punchPointsProperty(), new NumberStringConverter());
			}
		});
	}

	// *************************************

	private void checkIsDigitBetween1And5(LinkedHashSet<FormValidationError> res, TextField textField, String entryFieldName) {
		if(checkIsDigitBetween1And5(textField)) {
			res = new LinkedHashSet<>();
			res.add(new FormValidationError(rulesEntry, entryFieldName, textField, getMessage("validation.required")));
		}
	}

	private boolean checkIsDigitBetween1And5(TextField textField) {
		String s = textField.getText();
		if(StringUtils.isBlank(textField.getText())) {
			return false;
		}
		s = s.trim();
		return "1".equals(s)
				|| "2".equals(s)
				|| "3".equals(s)
				|| "4".equals(s)
				|| "5".equals(s);
	}

}
