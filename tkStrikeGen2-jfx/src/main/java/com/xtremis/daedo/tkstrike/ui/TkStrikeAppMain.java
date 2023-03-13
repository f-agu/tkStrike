package com.xtremis.daedo.tkstrike.ui;

import com.xtremis.daedo.tkstrike.configuration.TkStrikeSpringConfiguration;
import com.xtremis.daedo.tkstrike.service.CommonMatchConfigurationService;
import com.xtremis.daedo.tkstrike.service.MatchLogHistoricalService;
import com.xtremis.daedo.tkstrike.service.TkStrikeCSVImporter;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeMainController;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeMainControllerImpl;
import org.apache.log4j.Logger;

public class TkStrikeAppMain extends BaseTkStrikeAppMain {
  private static final Logger logger = Logger.getLogger(TkStrikeAppMain.class);
  
  public static void main(String[] args) {
    launch(args);
  }
  
  String getTkStrikeSplashUrl() {
    return "/images/TkStrikeSplash.jpg";
  }
  
  String getTkStrikeIconApp() {
    return "/images/icon_app.png";
  }
  
  String getGeneralLogFileName() {
    return "daedoTkStrike.log";
  }
  
  String getExternalIntegrationLogFileName() {
    return "daedoTkStrike-externalIntegration.log";
  }
  
  String getCSVImporterLogFileName() {
    return "daedoTkStrike-CSV-Importer.log";
  }
  
  Boolean customInitialize() {
    Boolean res = Boolean.FALSE;
    try {
      MatchLogHistoricalService matchLogHistoricalService = (MatchLogHistoricalService)this.contextAnnotation.getBean(MatchLogHistoricalService.class);
      matchLogHistoricalService.migrateToHistorical();
    } catch (Exception e) {
      e.printStackTrace();
      res = Boolean.TRUE;
    } 
    try {
      TkStrikeCSVImporter tkStrikeCSVImporter = (TkStrikeCSVImporter)this.contextAnnotation.getBean(TkStrikeCSVImporter.class);
      CommonMatchConfigurationService matchConfigurationService = (CommonMatchConfigurationService)this.contextAnnotation.getBean(CommonMatchConfigurationService.class);
      MatchLogHistoricalService matchLogHistoricalService = (MatchLogHistoricalService)this.contextAnnotation.getBean(MatchLogHistoricalService.class);
      if (tkStrikeCSVImporter.isImportFromCSVEnabled().booleanValue() && (tkStrikeCSVImporter
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
    } catch (Exception exception) {}
    return res;
  }
  
  Class getTkStrikeSpringConfigurationClass() {
    return TkStrikeSpringConfiguration.class;
  }
  
  Class<? extends TkStrikeMainController> getTkStrikeMainControllerClass() {
    return (Class)TkStrikeMainControllerImpl.class;
  }
}
