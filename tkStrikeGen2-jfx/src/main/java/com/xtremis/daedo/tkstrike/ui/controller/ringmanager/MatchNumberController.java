package com.xtremis.daedo.tkstrike.ui.controller.ringmanager;

import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.xtremis.daedo.tkstrike.service.MatchConfigurationService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.ui.model.FormValidationError;
import com.xtremis.daedo.tkstrike.ui.model.MatchConfigurationEntry;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;


public class MatchNumberController extends BaseStepWizardController implements StepWizardController {

	@FXML
	private Node rootView;

	@FXML
	private TextField txtMatchNumber;

	private SimpleStringProperty matchNumberProperty = new SimpleStringProperty();

	private MatchConfigurationEntry matchConfigurationEntry = null;

	@Autowired
	private MatchConfigurationService matchConfigurationService;

	@Autowired
	private MatchConfigurationManagementController matchConfigurationManagementController;

	@Override
	public Node getRootView() {
		return this.rootView;
	}

	@Override
	public void clearForm() {
		this.txtMatchNumber.clear();
	}

	@Override
	public LinkedHashSet<FormValidationError> validateForm() {
		LinkedHashSet<FormValidationError> res = null;
		if(StringUtils.isBlank(this.txtMatchNumber.getText())) {
			res = new LinkedHashSet<>();
			res.add(new FormValidationError(this.matchConfigurationEntry, "matchNumber", this.txtMatchNumber, getMessage("validation.required")));
		}
		return res;
	}

	@Override
	protected Collection<Control> getFormControls() {
		return FXCollections.observableArrayList(new Control[] {this.txtMatchNumber});
	}

	@Override
	public void onWindowShowEvent() {
		super.onWindowShowEvent();
		clearForm();
		this.txtMatchNumber.textProperty().bindBidirectional(this.matchNumberProperty);
		_bindControls();
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				MatchNumberController.this.txtMatchNumber.requestFocus();
			}
		});
	}

	@Override
	public void setMatchConfigurationEntry(MatchConfigurationEntry matchConfigurationEntry) {
		this.matchConfigurationEntry = matchConfigurationEntry;
		if(this.matchConfigurationEntry != null)
			this.matchNumberProperty.set(matchConfigurationEntry.getMatchNumber());
	}

	protected void _bindControls() {
		if(this.matchConfigurationEntry != null)
			this.matchNumberProperty.set(this.matchConfigurationEntry.getMatchNumber());
	}

	@Override
	public MatchConfigurationEntry getMatchConfigurationEntry() {
		if(this.matchNumberProperty.get() != null)
			this.matchConfigurationEntry.matchNumberProperty().set(this.matchNumberProperty.get());
		return this.matchConfigurationEntry;
	}

	public void openMatchsConfigurationSelection() {
		this.matchConfigurationManagementController.setOpenType(MatchConfigurationManagementController.OpenType.NORMAL);
		openInNewStage(this.matchConfigurationManagementController, new EventHandler() {

			@Override
			public void handle(Event event) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						MatchConfigurationEntry selected = MatchNumberController.this.matchConfigurationManagementController
								.getMatchConfigurationEntry();
						MatchNumberController.this.onWindowShowEvent();
						if(selected != null)
							MatchNumberController.this.setMatchConfigurationEntry(selected);
					}
				});
			}
		}, getMessage("title.window.selectMatch"), 950, 500, true);
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		this.txtMatchNumber.textProperty().bindBidirectional(this.matchNumberProperty);
		this.txtMatchNumber.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
				if( ! t1.booleanValue() && StringUtils.isNotBlank(MatchNumberController.this.txtMatchNumber.getText()))
					try {
						MatchConfigurationEntry selected = MatchNumberController.this.matchConfigurationService.getEntryByMatchNumber(
								MatchNumberController.this.txtMatchNumber.getText());
						if(selected != null)
							MatchNumberController.this.setMatchConfigurationEntry(selected);
					} catch(TkStrikeServiceException e) {
						MatchNumberController.this.manageException(e, "On lost focus matchNumber - getEntryByMatchNumber", null);
					}
			}
		});
		this.txtMatchNumber.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				MatchNumberController.this.forceSubmitForm();
			}
		});
	}

	@Override
	public void afterPropertiesSet() throws Exception {}
}
