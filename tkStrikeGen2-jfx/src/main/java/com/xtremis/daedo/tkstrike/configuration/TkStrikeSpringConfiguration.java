package com.xtremis.daedo.tkstrike.configuration;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;

import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationService;
import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationServiceImpl;
import com.xtremis.daedo.tkstrike.service.TkStrikeExtraCommunicationServiceImpl;
import com.xtremis.daedo.tkstrike.ui.controller.MatchFinalResultController;
import com.xtremis.daedo.tkstrike.ui.controller.MatchLogViewerController;
import com.xtremis.daedo.tkstrike.ui.controller.NetworkStatusController;
import com.xtremis.daedo.tkstrike.ui.controller.RoundCountdownController;
import com.xtremis.daedo.tkstrike.ui.controller.RoundFinishedConfirmationController;
import com.xtremis.daedo.tkstrike.ui.controller.ScoreboardController;
import com.xtremis.daedo.tkstrike.ui.controller.ScoreboardEditorControllerImpl;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeMainControllerImpl;
import com.xtremis.daedo.tkstrike.ui.controller.configuration.ConfigurationExternalConfigController;
import com.xtremis.daedo.tkstrike.ui.controller.configuration.ConfigurationMainController;
import com.xtremis.daedo.tkstrike.ui.controller.configuration.ConfigurationMatchLogController;
import com.xtremis.daedo.tkstrike.ui.controller.configuration.ConfigurationNetworkController;
import com.xtremis.daedo.tkstrike.ui.controller.configuration.ConfigurationRulesController;
import com.xtremis.daedo.tkstrike.ui.controller.configuration.ConfigurationSoftwareUpdateController;
import com.xtremis.daedo.tkstrike.ui.controller.configuration.ConfigurationSoundsController;
import com.xtremis.daedo.tkstrike.ui.controller.externalscreen.ExternalHardwareTestController;
import com.xtremis.daedo.tkstrike.ui.controller.externalscreen.ExternalScoreboardController;
import com.xtremis.daedo.tkstrike.ui.controller.externalscreen.ExternalScreenMainController;
import com.xtremis.daedo.tkstrike.ui.controller.hardwaretest.HardwareTestMainController;
import com.xtremis.daedo.tkstrike.ui.controller.ringmanager.AthletesInformationController;
import com.xtremis.daedo.tkstrike.ui.controller.ringmanager.AthletesManagementController;
import com.xtremis.daedo.tkstrike.ui.controller.ringmanager.CategoriesMainController;
import com.xtremis.daedo.tkstrike.ui.controller.ringmanager.CategoriesManagementController;
import com.xtremis.daedo.tkstrike.ui.controller.ringmanager.CategoryController;
import com.xtremis.daedo.tkstrike.ui.controller.ringmanager.DifferentialScoreDefinitionManagementController;
import com.xtremis.daedo.tkstrike.ui.controller.ringmanager.EditAthleteController;
import com.xtremis.daedo.tkstrike.ui.controller.ringmanager.FlagsManagementController;
import com.xtremis.daedo.tkstrike.ui.controller.ringmanager.GenderController;
import com.xtremis.daedo.tkstrike.ui.controller.ringmanager.MatchConfigurationController;
import com.xtremis.daedo.tkstrike.ui.controller.ringmanager.MatchConfigurationManagementController;
import com.xtremis.daedo.tkstrike.ui.controller.ringmanager.MatchNumberController;
import com.xtremis.daedo.tkstrike.ui.controller.ringmanager.PhaseController;
import com.xtremis.daedo.tkstrike.ui.controller.ringmanager.PhasesManagementController;
import com.xtremis.daedo.tkstrike.ui.controller.ringmanager.RingManagerControllerController;
import com.xtremis.daedo.tkstrike.ui.controller.ringmanager.RingManagerWizardController;
import com.xtremis.daedo.tkstrike.ui.controller.ringmanager.SubCategoriesManagementController;
import com.xtremis.daedo.tkstrike.ui.controller.ringmanager.SubCategoryController;

@Configuration
@ImportResource({ "classpath:/META-INF/tkStrike-context.xml" })
public class TkStrikeSpringConfiguration extends BaseTkStrikeSpringConfiguration {

	@Bean
	@Qualifier("DEFAULT")
	public RoundCountdownController getRoundCountdownControllerDEFAULT() throws IOException {
		return (RoundCountdownController) loadController("/META-INF/fxml/Countdown.fxml");
	}

	@Bean
	@Qualifier("EXTERNAL")
	public RoundCountdownController getRoundCountdownControllerEXT() throws IOException {
		return (RoundCountdownController) loadController("/META-INF/fxml/Countdown.fxml");
	}

	@Bean
	@Scope("singleton")
	@Conditional({ RealCommunicationConditional.class })
	public TkStrikeCommunicationService getTkStrikeCommunicationServiceDefault() {
		return new TkStrikeCommunicationServiceImpl();
	}

	@Bean
	@Scope("singleton")
	@Conditional({ TkStrikeExtraCommunicationConditional.class })
	public TkStrikeExtraCommunicationServiceImpl getTkStrikeExtraCommunicationService() {
		return new TkStrikeExtraCommunicationServiceImpl();
	}

	@Bean
	public TkStrikeMainControllerImpl getTkStrikeMainController() throws IOException {
		return (TkStrikeMainControllerImpl) loadController("/fxml/TkStrikeMain.fxml");
	}

	@Bean
	public ScoreboardController getScoreboardController() throws IOException {
		return (ScoreboardController) loadController("/fxml/BaseScoreboard.fxml");
	}

	@Bean
	public NetworkStatusController getNetworkStatusController() throws IOException {
		return (NetworkStatusController) loadController("/META-INF/fxml/NetworkStatus.fxml");
	}

	@Bean
	public MatchFinalResultController getMatchFinalResultController() throws IOException {
		return (MatchFinalResultController) loadController("/fxml/MatchFinalResult.fxml");
	}

	@Bean
	public ScoreboardEditorControllerImpl getScoreboardEditorController() throws IOException {
		return (ScoreboardEditorControllerImpl) loadController("/fxml/ScoreboardEditor.fxml");
	}

	@Bean
	public MatchLogViewerController getMatchLogViewerController() throws IOException {
		return (MatchLogViewerController) loadController("/fxml/MatchLogViewer.fxml");
	}

	@Bean
	public RoundFinishedConfirmationController roundFinishedConfirmationController() throws IOException {
		return (RoundFinishedConfirmationController) loadController(new RoundFinishedConfirmationController(),
				"/META-INF/fxml/RoundFinishedConfirmation.fxml");
	}

	@Bean
	public ExternalScreenMainController getExternalScreenMainController() throws IOException {
		return (ExternalScreenMainController) loadController("/fxml/externalscreen/ExternalScreenMain.fxml");
	}

	@Bean
	public ExternalScoreboardController getExternalScoreboardHDController() throws IOException {
		return (ExternalScoreboardController) loadController("/fxml/externalscreen/ExternalScoreboard-HD.fxml");
	}

	@Bean
	public ExternalHardwareTestController getExternalHardwareTestController() throws IOException {
		return (ExternalHardwareTestController) loadController("/fxml/externalscreen/ExterenalNewHardwareTest.fxml");
	}

	@Bean
	public ConfigurationMainController getConfigurationMainController() throws IOException {
		return (ConfigurationMainController) loadController("/fxml/configuration/ConfigurationMain.fxml");
	}

	@Bean
	public ConfigurationNetworkController getConfigurationNetworkController() throws IOException {
		return (ConfigurationNetworkController) loadController(
				"/META-INF/fxml/configuration/Configuration-Network.fxml");
	}

	@Bean
	public ConfigurationRulesController getConfigurationRingManagerController() throws IOException {
		return (ConfigurationRulesController) loadController("/fxml/configuration/Configuration-Rules.fxml");
	}

	@Bean
	public ConfigurationSoundsController getConfigurationSoundsController() throws IOException {
		return (ConfigurationSoundsController) loadController("/fxml/configuration/Configuration-Sounds.fxml");
	}

	@Bean
	public ConfigurationExternalConfigController getConfigurationExternalConfigController() throws IOException {
		return (ConfigurationExternalConfigController) loadController(
				"/fxml/configuration/Configuration-ExternalConfig.fxml");
	}

	@Bean
	public ConfigurationMatchLogController getConfigurationMatchLogController() throws IOException {
		return (ConfigurationMatchLogController) loadController("/fxml/configuration/Configuration-MatchLog.fxml");
	}

	@Bean
	public ConfigurationSoftwareUpdateController getConfigurationSoftwareUpdateController() throws IOException {
		return (ConfigurationSoftwareUpdateController) loadController(
				"/fxml/configuration/Configuration-SoftwareUpdate.fxml");
	}

	@Bean
	public RingManagerControllerController getRingManagerController() throws IOException {
		return (RingManagerControllerController) loadController("/fxml/ringmanager/RingManagerMain.fxml");
	}

	@Bean
	public RingManagerWizardController getRingManagerWizardController() throws IOException {
		return (RingManagerWizardController) loadController("/fxml/ringmanager/RingManagerWizardMain.fxml");
	}

	@Bean
	public MatchNumberController getMatchNumberController() throws IOException {
		return (MatchNumberController) loadController("/fxml/ringmanager/Step-MatchNumber.fxml");
	}

	@Bean
	public PhaseController getPhaseController() throws IOException {
		return (PhaseController) loadController("/fxml/ringmanager/Step-Phase.fxml");
	}

	@Bean
	public SubCategoryController getSubCategoryController() throws IOException {
		return (SubCategoryController) loadController("/fxml/ringmanager/Step-SubCategory.fxml");
	}

	@Bean
	public GenderController getGenderController() throws IOException {
		return (GenderController) loadController("/fxml/ringmanager/Step-Gender.fxml");
	}

	@Bean
	public CategoryController getCategoryController() throws IOException {
		return (CategoryController) loadController("/fxml/ringmanager/Step-Category.fxml");
	}

	@Bean
	public MatchConfigurationController getMatchConfigurationController() throws IOException {
		return (MatchConfigurationController) loadController("/fxml/ringmanager/Step-MatchConfiguration.fxml");
	}

	@Bean
	public EditAthleteController getEditAthleteController() throws IOException {
		return (EditAthleteController) loadController("/fxml/ringmanager/EditAthlete.fxml");
	}

	@Bean
	public AthletesInformationController getAthletesInformationControllerWizard() throws IOException {
		return (AthletesInformationController) loadController("/fxml/ringmanager/Step-AthletesInformation.fxml");
	}

	@Bean
	public PhasesManagementController getPhasesManagementController() throws IOException {
		return (PhasesManagementController) loadController("/META-INF/fxml/ringmanager/PhasesManagement.fxml");
	}

	@Bean
	public AthletesManagementController getAthletesManagementController() throws IOException {
		return (AthletesManagementController) loadController("/fxml/ringmanager/AthletesManagement.fxml");
	}

	@Bean
	public FlagsManagementController getFlagsManagementController() throws IOException {
		return (FlagsManagementController) loadController("/META-INF/fxml/ringmanager/FlagsManagement.fxml");
	}

	@Bean
	public CategoriesMainController getCategoriesMainController() throws IOException {
		return (CategoriesMainController) loadController("/fxml/ringmanager/CategoriesMain.fxml");
	}

	@Bean
	public SubCategoriesManagementController getSubCategoryManagementController() throws IOException {
		return (SubCategoriesManagementController) loadController("/fxml/ringmanager/SubCategoriesManagement.fxml");
	}

	@Bean
	public CategoriesManagementController getCategoriesManagementController() throws IOException {
		return (CategoriesManagementController) loadController("/fxml/ringmanager/CategoriesManagement.fxml");
	}

	@Bean
	public DifferentialScoreDefinitionManagementController getDifferentialScoreDefinitionManagementController()
			throws IOException {
		return (DifferentialScoreDefinitionManagementController) loadController(
				"/fxml/ringmanager/DifferentialScoreDefinitionManagement.fxml");
	}

	@Bean
	public MatchConfigurationManagementController getMatchConfigurationManagementController() throws IOException {
		return (MatchConfigurationManagementController) loadController(
				"/fxml/ringmanager/MatchConfigurationsManagement.fxml");
	}

	@Bean
	public HardwareTestMainController getHardwareTestMainController() throws IOException {
		return (HardwareTestMainController) loadController("/fxml/hardwaretest/NewHardwareTest.fxml");
	}
}
