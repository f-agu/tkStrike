package com.xtremis.daedo.tkstrike.ui;

import org.apache.log4j.Logger;

import com.xtremis.daedo.tkstrike.configuration.TkStrikeSpringConfiguration;
import com.xtremis.daedo.tkstrike.service.CommonMatchConfigurationService;
import com.xtremis.daedo.tkstrike.service.MatchLogHistoricalService;
import com.xtremis.daedo.tkstrike.service.TkStrikeCSVImporter;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeMainController;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeMainControllerImpl;


public class TkStrikeAppMain extends BaseTkStrikeAppMain {

	private static final Logger logger = Logger.getLogger(TkStrikeAppMain.class);

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	String getTkStrikeSplashUrl() {
		return "/images/TkStrikeSplash.jpg";
	}

	@Override
	String getTkStrikeIconApp() {
		return "/images/icon_app.png";
	}

	@Override
	String getGeneralLogFileName() {
		return "daedoTkStrike.log";
	}

	@Override
	String getExternalIntegrationLogFileName() {
		return "daedoTkStrike-externalIntegration.log";
	}

	@Override
	String getCSVImporterLogFileName() {
		return "daedoTkStrike-CSV-Importer.log";
	}

	@Override
	Boolean customInitialize() {
		Boolean res = Boolean.FALSE;
		try {
			MatchLogHistoricalService matchLogHistoricalService = this.contextAnnotation.getBean(MatchLogHistoricalService.class);
			matchLogHistoricalService.migrateToHistorical();
		} catch(Exception e) {
			e.printStackTrace();
			res = Boolean.TRUE;
		}
		try {
			TkStrikeCSVImporter tkStrikeCSVImporter = contextAnnotation.getBean(TkStrikeCSVImporter.class);
			CommonMatchConfigurationService matchConfigurationService = contextAnnotation.getBean(CommonMatchConfigurationService.class);
			MatchLogHistoricalService matchLogHistoricalService = contextAnnotation.getBean(MatchLogHistoricalService.class);
			if(tkStrikeCSVImporter.isImportFromCSVEnabled().booleanValue() && (tkStrikeCSVImporter
					.isDeleteAthletes().booleanValue() || tkStrikeCSVImporter
							.isDeleteContests().booleanValue() || tkStrikeCSVImporter
									.isDeletePhases().booleanValue() || tkStrikeCSVImporter
											.isDeleteWeightDivisions().booleanValue())) {
				matchConfigurationService.deleteAll();
				matchLogHistoricalService.deleteAll();
				tkStrikeCSVImporter.tryToImportPhases();
				tkStrikeCSVImporter.tryToImportContests();
				tkStrikeCSVImporter.tryToImportWeightDivisions();
				tkStrikeCSVImporter.tryToImportAthletes();
			}
		} catch(Exception exception) {}
		return res;
	}

	@Override
	Class getTkStrikeSpringConfigurationClass() {
		return TkStrikeSpringConfiguration.class;
	}

	@Override
	Class<? extends TkStrikeMainController> getTkStrikeMainControllerClass() {
		return TkStrikeMainControllerImpl.class;
	}
}
