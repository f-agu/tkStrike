package com.xtremis.daedo.tkstrike.configuration;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;

import javax.annotation.PostConstruct;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;

import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationServiceImpl;
import com.xtremis.daedo.tkstrike.tools.NodeIds;
import com.xtremis.daedo.tkstrike.tools.NodeIds.Color;
import com.xtremis.daedo.tkstrike.tools.NodeIds.Part;
import com.xtremis.daedo.tkstrike.tools.TkStrikeUtil;
import com.xtremis.daedo.tkstrike.tools.UpdateGen;
import com.xtremis.daedo.tkstrike.ui.CRMCombinationsHelper;
import com.xtremis.daedo.tkstrike.ui.configuration.CRMDisciplineController;
import com.xtremis.daedo.tkstrike.ui.configuration.CRMMainController;
import com.xtremis.daedo.tkstrike.ui.configuration.CRMMiscController;
import com.xtremis.daedo.tkstrike.ui.configuration.CRMPointController;
import com.xtremis.daedo.tkstrike.ui.configuration.CRMTestNetworkController;
import com.xtremis.daedo.tkstrike.ui.controller.CommonTkStrikeBaseController;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeController;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeMainControllerImpl;
import com.xtremis.daedo.tkstrike.ui.controller.configuration.ConfigurationMainController;
import com.xtremis.daedo.tkstrike.ui.controller.configuration.ConfigurationNetworkController;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;

/**
 * @author f.agu
 * @created 15 mars 2023 09:36:04
 */
@Configuration
@Import(MoreConfiguration.class)
public class PatchedConfiguration extends TkStrikeSpringConfiguration
		implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger LOGGER = Logger.getLogger(PatchedConfiguration.class);

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ConfigurationMainController configurationMainController;

	@Autowired
	private ConfigurationNetworkController configurationNetworkController;

	@Autowired
	private CRMMainController crmMainController;

	@Autowired
	private TkStrikeMainControllerImpl tkStrikeMainControllerImpl;

	@PostConstruct
	void patch() {
		patchLogLevel();
		patchTkStrikeMainControllerImpl();
		patchConfigurationNetworkController();
		patchConfigurationWindow();
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {
			UpdateGen.updateTkStrikeGenVersion(applicationContext, TkStrikeUtil.getInstance().getGeneration().name());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Bean
	CRMMainController crmController() throws IOException {
		return (CRMMainController) loadController("/META-INF/fxml/configuration/ConfigurationCRM.fxml");
	}

	@Bean
	CRMDisciplineController crmDisciplineController() throws IOException {
		return (CRMDisciplineController) loadController("/META-INF/fxml/configuration/Configuration-Discipline.fxml");
	}

	@Bean
	CRMMiscController crmMiscController() throws IOException {
		return (CRMMiscController) loadController("/META-INF/fxml/configuration/Configuration-Misc.fxml");
	}

	@Bean
	CRMPointController crmPointController() throws IOException {
		return (CRMPointController) loadController("/META-INF/fxml/configuration/Configuration-Point.fxml");
	}

	@Bean
	CRMTestNetworkController crmTestNetworkController() throws IOException {
		return (CRMTestNetworkController) loadController("/META-INF/fxml/configuration/Configuration-TestNetwork.fxml");
	}

	// ************************************************************

	private void patchLogLevel() {
		Logger.getLogger(TkStrikeCommunicationServiceImpl.class).setLevel(Level.DEBUG);
	}

	private void patchConfigurationWindow() {
		Node rootView = getRootView(configurationMainController);
		rootView.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (CRMCombinationsHelper.keyCombSetNodesIds.match(event))
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							BiConsumer<String, String> updater = (fieldName, value) -> {
								TextField textField = getField(ConfigurationNetworkController.class, fieldName,
										configurationNetworkController);
								textField.setText(value);
							};
							NodeIds nodeIds = null;
							try {
								nodeIds = TkStrikeUtil.getInstance().getNodeIds();
							} catch (IOException e) {
								LOGGER.info(e.getMessage(), e);
								return;
							}
							updater.accept("txtG1BB", nodeIds.getSensorId(Color.BLUE, 1, Part.BODY));
							updater.accept("txtG1HB", nodeIds.getSensorId(Color.BLUE, 1, Part.HEAD));
							updater.accept("txtG1BR", nodeIds.getSensorId(Color.RED, 1, Part.BODY));
							updater.accept("txtG1HR", nodeIds.getSensorId(Color.RED, 1, Part.HEAD));
							updater.accept("txtG2BB", nodeIds.getSensorId(Color.BLUE, 2, Part.BODY));
							updater.accept("txtG2HB", nodeIds.getSensorId(Color.BLUE, 2, Part.HEAD));
							updater.accept("txtG2BR", nodeIds.getSensorId(Color.RED, 2, Part.BODY));
							updater.accept("txtG2HR", nodeIds.getSensorId(Color.RED, 2, Part.HEAD));
						}
					});
			}
		});
	}

	private void patchTkStrikeMainControllerImpl() {
		patchTkStrikeMainControllerImplRootView(getRootView(tkStrikeMainControllerImpl));
	}

	private void patchTkStrikeMainControllerImplRootView(Node rootView) {
		rootView.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (CRMCombinationsHelper.keyCombCRM.match(event)) {
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

	private void openInNewStage(TkStrikeController tkStrikeController,
			EventHandler<WindowEvent> windowCloseEventHandler, String stageTitle, int width, int height,
			boolean resizeable) {
		try {
			Method method = CommonTkStrikeBaseController.class.getDeclaredMethod("openInNewStage",
					TkStrikeController.class, EventHandler.class, String.class, int.class, int.class, boolean.class);
			method.setAccessible(true);
			method.invoke(tkStrikeMainControllerImpl, tkStrikeController, windowCloseEventHandler, stageTitle, width,
					height, resizeable);
		} catch (Exception e) {
			LOGGER.info(e.getMessage(), e);
		}

	}

	private void patchConfigurationNetworkController() {
		ToggleButton button = getField(ConfigurationNetworkController.class, "tgCommunicationType",
				configurationNetworkController);
		button.setVisible(true);
	}

	private <V extends CommonTkStrikeBaseController> Node getRootView(V v) {
		return getField(CommonTkStrikeBaseController.class, "rootView", v);
	}

	private <F> F getField(Class<?> cls, String name, Object instance) {
		try {
			Field field = cls.getDeclaredField(name);
			field.setAccessible(true);
			return (F) field.get(instance);
		} catch (Exception e) {
			LOGGER.info(e.getMessage(), e);
			return null;
		}
	}

}
