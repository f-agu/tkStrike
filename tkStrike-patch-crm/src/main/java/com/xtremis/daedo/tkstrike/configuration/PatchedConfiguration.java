package com.xtremis.daedo.tkstrike.configuration;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.xtremis.daedo.tkstrike.ui.CRMCombinationsHelper;
import com.xtremis.daedo.tkstrike.ui.configuration.CRMDisciplineController;
import com.xtremis.daedo.tkstrike.ui.configuration.CRMMainController;
import com.xtremis.daedo.tkstrike.ui.configuration.CRMPointController;
import com.xtremis.daedo.tkstrike.ui.configuration.CRMTestNetworkController;
import com.xtremis.daedo.tkstrike.ui.controller.CommonTkStrikeBaseController;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeController;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeMainControllerImpl;
import com.xtremis.daedo.tkstrike.ui.controller.configuration.ConfigurationNetworkController;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;


/**
 * @author f.agu
 * @created 15 mars 2023 09:36:04
 */
@Configuration
@Import(MoreConfiguration.class)
public class PatchedConfiguration extends TkStrikeSpringConfiguration {

	private static final Logger logger = Logger.getLogger(PatchedConfiguration.class);

	@Autowired
	private ConfigurationNetworkController configurationNetworkController;

	@Autowired
	private CRMMainController crmMainController;

	@Autowired
	private TkStrikeMainControllerImpl tkStrikeMainControllerImpl;

	@PostConstruct
	void patch() {
		patchTkStrikeMainControllerImpl();
		patchConfigurationNetworkController();
	}

	@Bean
	CRMMainController crmController() throws IOException {
		return (CRMMainController)loadController("/META-INF/fxml/configuration/ConfigurationCRM.fxml");
	}

	@Bean
	CRMDisciplineController crmDisciplineController() throws IOException {
		return (CRMDisciplineController)loadController("/META-INF/fxml/configuration/Configuration-Discipline.fxml");
	}

	@Bean
	CRMPointController crmPointController() throws IOException {
		return (CRMPointController)loadController("/META-INF/fxml/configuration/Configuration-Point.fxml");
	}

	@Bean
	CRMTestNetworkController crmTestNetworkController() throws IOException {
		return (CRMTestNetworkController)loadController("/META-INF/fxml/configuration/Configuration-TestNetwork.fxml");
	}

	// ************************************************************

	private void patchTkStrikeMainControllerImpl() {
		try {
			Field field = CommonTkStrikeBaseController.class.getDeclaredField("rootView");
			field.setAccessible(true);
			Node rootView = (Node)field.get(tkStrikeMainControllerImpl);
			patchTkStrikeMainControllerImplRootView(rootView);
		} catch(Exception e) {
			logger.info(e.getMessage(), e);
		}
	}

	private void patchTkStrikeMainControllerImplRootView(Node rootView) {
		rootView.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if(CRMCombinationsHelper.keyCombCRM.match(event)) {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							openCRMConfiguration();
						}
					});
				}
			}
		});
	}

	private void openCRMConfiguration() {
		openInNewStage(crmMainController, new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent windowEvent) {
				crmMainController.onWindowShowEvent();
				// scoreboardController.onWindowShowEvent();
			}
		}, "CRM Patcher", 970, 700, false);
	}

	private void openInNewStage(TkStrikeController tkStrikeController, EventHandler<WindowEvent> windowCloseEventHandler, String stageTitle,
			int width, int height, boolean resizeable) {
		try {
			Method method = CommonTkStrikeBaseController.class.getDeclaredMethod("openInNewStage", TkStrikeController.class, EventHandler.class,
					String.class, int.class, int.class, boolean.class);
			method.setAccessible(true);
			method.invoke(tkStrikeMainControllerImpl, tkStrikeController, windowCloseEventHandler, stageTitle,
					width, height, resizeable);
		} catch(Exception e) {
			logger.info(e.getMessage(), e);
		}

	}

	private void patchConfigurationNetworkController() {
		try {
			Field field = ConfigurationNetworkController.class.getDeclaredField("tgCommunicationType");
			field.setAccessible(true);
			ToggleButton button = (ToggleButton)field.get(configurationNetworkController);
			button.setVisible(true);
		} catch(Exception e) {
			logger.info(e.getMessage(), e);
		}
	}

}
