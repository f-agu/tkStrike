package com.xtremis.daedo.tkstrike.ui.configuration;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.xtremis.daedo.tkstrike.tools.Generation;
import com.xtremis.daedo.tkstrike.tools.TkProperties;
import com.xtremis.daedo.tkstrike.tools.UpdateGen;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeBaseController;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.stage.WindowEvent;

// see com.xtremis.daedo.tkstrike.ui.controller.configuration.ConfigurationNetworkController
@Component
public class CRMMiscController extends TkStrikeBaseController {

	protected static final Logger logger = Logger.getLogger(CRMMiscController.class);

	@Autowired
	private ApplicationContext applicationContext;

	@FXML
	private Node rootView;

	@FXML
	private ComboBox<String> cmbGen;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	}

	@Override
	public EventHandler<WindowEvent> getOnWindowCloseEventHandler() {
		return new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent windowEvent) {
				ObjectProperty<String> valueProperty = cmbGen.valueProperty();
				if (valueProperty != null) {
					String value = valueProperty.getValue();
					if (value != null) {
						try {
							Generation generation = Generation.valueOf(value.toUpperCase());
							TkProperties.getInstance().setGeneration(generation);
							UpdateGen.updateTkStrikeGenVersion(applicationContext, value);
						} catch (Exception e) {
							logger.info(e.getMessage(), e);
							showErrorDialog(getMessage("title.default.error"), e.toString());
						}
					}
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
				bindControls();
				customInitialize();
			}
		});
	}

	// *************************************

	protected final void customInitialize() {
		if (cmbGen.getItems().isEmpty()) {
			cmbGen.getItems().add("gen1");
			cmbGen.getItems().add("gen2");
		}
	}

	protected final void bindControls() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				cmbGen.setValue(TkProperties.getInstance().getGeneration().name().toLowerCase());
			}
		});
	}

}
