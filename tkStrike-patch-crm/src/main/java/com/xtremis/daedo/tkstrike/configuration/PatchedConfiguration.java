package com.xtremis.daedo.tkstrike.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ContextRefreshedEvent;

import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationServiceImpl;
import com.xtremis.daedo.tkstrike.tools.NodeIds;
import com.xtremis.daedo.tkstrike.tools.NodeIds.Color;
import com.xtremis.daedo.tkstrike.tools.NodeIds.Part;
import com.xtremis.daedo.tkstrike.tools.TkProperties;
import com.xtremis.daedo.tkstrike.tools.UpdateGen;
import com.xtremis.daedo.tkstrike.ui.CRMCombinationsHelper;
import com.xtremis.daedo.tkstrike.ui.configuration.CRMDisciplineController;
import com.xtremis.daedo.tkstrike.ui.configuration.CRMMainController;
import com.xtremis.daedo.tkstrike.ui.configuration.CRMMiscController;
import com.xtremis.daedo.tkstrike.ui.configuration.CRMPointController;
import com.xtremis.daedo.tkstrike.ui.configuration.CRMTestNetworkController;
import com.xtremis.daedo.tkstrike.ui.configuration.OverrideConfigurationNetworkController;
import com.xtremis.daedo.tkstrike.ui.controller.CommonTkStrikeBaseController;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeController;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeMainControllerImpl;
import com.xtremis.daedo.tkstrike.ui.controller.configuration.ConfigurationMainController;
import com.xtremis.daedo.tkstrike.ui.controller.configuration.ConfigurationNetworkController;
import com.xtremis.daedo.tkstrike.ui.controller.externalscreen.ExternalScoreboardHDController;
import com.xtremis.daedo.tkstrike.ui.controller.ringmanager.MatchConfigurationController;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
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

	@Autowired
	private ExternalScoreboardHDController externalScoreboardHDController;

	@Autowired
	private MatchConfigurationController matchConfigurationController;

	@PostConstruct
	void patch() {
		patchLogLevel();
		patchLogo();
		patchTkStrikeMainControllerImpl();
		patchConfigurationNetworkController();
		patchMatchConfigurationController();
		patchConfigurationWindow();
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {
			UpdateGen.updateTkStrikeGenVersion(applicationContext, TkProperties.getInstance().getGeneration().name());
		} catch(Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Bean
	@Primary
	OverrideConfigurationNetworkController overrideConfigurationNetworkController() throws IOException {
		return (OverrideConfigurationNetworkController)loadController("/META-INF/fxml/configuration/Configuration-Network2.fxml");
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
	CRMMiscController crmMiscController() throws IOException {
		return (CRMMiscController)loadController("/META-INF/fxml/configuration/Configuration-Misc.fxml");
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

	private void patchLogLevel() {
		TkProperties tkProperties = TkProperties.getInstance();
		Consumer<String> updater = k -> Logger.getLogger(k).setLevel(tkProperties.getLogLevel(k, Level.DEBUG));
		updater.accept(TkStrikeCommunicationServiceImpl.class.getName());
		updater.accept("COMM_EVENT");
		updater.accept("STATUS_EVENT");
		updater.accept("GLOBAL_NETWORK_STATUS_CONTROLLER");
		updater.accept("DATA_EVENT");
		updater.accept("EXTERNAL_INTEGRATION");
		updater.accept("MATCH_WORKER");
		updater.accept("CSV_IMPORTER");
		updater.accept("EXTRA_COMMUNICATION");
	}

	private void patchLogo() {
		Node lblMatchConfigNode = getRootView(tkStrikeMainControllerImpl).lookup("#lblMatchConfig");
		ObservableList<Node> children = ((HBox)lblMatchConfigNode.getParent()).getChildren();
		children.set(1, createImageViewTKKD());

		lblMatchConfigNode = getRootView(externalScoreboardHDController).lookup("#lblMatchConfig");
		children = ((HBox)lblMatchConfigNode.getParent()).getChildren();
		children.set(1, createImageViewTKKD());
	}

	private void patchConfigurationWindow() {
		Node rootView = getRootView(configurationMainController);
		if(rootView == null) {
			return;
		}
		rootView.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if(CRMCombinationsHelper.keyCombSetNodesIds.match(event))
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							BiConsumer<String, String> updater = (fieldName, value) -> {
								TextField textField = getFieldValue(ConfigurationNetworkController.class, fieldName,
										configurationNetworkController);
								textField.setText(value);
							};
							NodeIds nodeIds = null;
							try {
								nodeIds = TkProperties.getInstance().getNodeIds();
							} catch(IOException e) {
								LOGGER.info(e.getMessage(), e);
								return;
							}
							updater.accept("txtJ1", nodeIds.getJudge(1));
							updater.accept("txtJ2", nodeIds.getJudge(2));
							updater.accept("txtJ3", nodeIds.getJudge(3));

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
		Node rootView = getRootView(tkStrikeMainControllerImpl);
		patchTkStrikeMainControllerImplRootView(rootView);
		// HBox hbox = (HBox) rootView.lookup("#pnAppMainButtons");
		// ObservableList<Node> children = hbox.getChildren();
		// Button button = new Button("CONFIGURATION CRM");
		// button.setFocusTraversable(false);
		// button.setMnemonicParsing(false);
		// children.add(3, button);
		// children.stream().map(Button.class::cast).forEach(b -> {
		// b.getStyleClass().remove("button-main");
		// b.setStyle("-fx-cursor: hand");
		// b.setStyle("-fx-max-width: 150");
		// b.setStyle("-fx-min-width: 150");
		// b.setStyle("-fx-pref-width: 150");
		// b.setStyle("-fx-max-height: 40");
		// b.setStyle("-fx-min-height: 40");
		// b.setStyle("-fx-pref-height: 40");
		// b.setStyle("-fx-background-color: #3f3f3f");
		// b.setStyle("-fx-text-fill: white");
		// b.setStyle("-fx-font-weight: bold");
		// b.setStyle("-fx-text-alignment: center");
		// b.setStyle("-fx-font-size: 14");
		// b.setStyle("-fx-border-radius: 0");
		// b.setStyle("-fx-background-radius: 0");
		// });
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

	private void openInNewStage(TkStrikeController tkStrikeController,
			EventHandler<WindowEvent> windowCloseEventHandler, String stageTitle, int width, int height,
			boolean resizeable) {
		try {
			Method method = CommonTkStrikeBaseController.class.getDeclaredMethod("openInNewStage",
					TkStrikeController.class, EventHandler.class, String.class, int.class, int.class, boolean.class);
			method.setAccessible(true);
			method.invoke(tkStrikeMainControllerImpl, tkStrikeController, windowCloseEventHandler, stageTitle, width,
					height, resizeable);
		} catch(Exception e) {
			LOGGER.info(e.getMessage(), e);
		}

	}

	private void patchConfigurationNetworkController() {
		ToggleButton button = getFieldValue(ConfigurationNetworkController.class, "tgCommunicationType",
				configurationNetworkController);
		button.setVisible(true);
	}

	private void patchMatchConfigurationController() {
		Field field = getField(MatchConfigurationController.class, "maxGamJeomsAllowed");
		try {
			field.set(matchConfigurationController, TkProperties.getInstance().getGamJom());
		} catch(IllegalArgumentException | IllegalAccessException e) {
			LOGGER.info(e.getMessage(), e);
		}
	}

	private <V extends CommonTkStrikeBaseController> Node getRootView(V v) {
		return getFieldValue(CommonTkStrikeBaseController.class, "rootView", v);
	}

	private Field getField(Class<?> cls, String name) {
		try {
			Field field = cls.getDeclaredField(name);
			field.setAccessible(true);
			return field;
		} catch(Exception e) {
			LOGGER.info(e.getMessage(), e);
			return null;
		}
	}

	private <F> F getFieldValue(Class<?> cls, String name, Object instance) {
		try {
			Field field = getField(cls, name);
			return (F)field.get(instance);
		} catch(Exception e) {
			LOGGER.info(e.getMessage(), e);
			return null;
		}
	}

	private ImageView createImageViewTKKD() {
		try (InputStream is = getClass().getResourceAsStream("/images/logo-taekwonkido.png")) {
			Image image = new Image(is);
			ImageView imageView = new ImageView();
			imageView.setImage(image);
			imageView.setFitHeight(50);
			imageView.setPickOnBounds(true);
			imageView.setPreserveRatio(true);
			return imageView;
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}
