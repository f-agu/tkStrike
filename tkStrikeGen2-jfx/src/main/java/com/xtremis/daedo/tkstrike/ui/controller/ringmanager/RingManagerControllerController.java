package com.xtremis.daedo.tkstrike.ui.controller.ringmanager;

import com.xtremis.daedo.tkstrike.ei.client.VenueManagementClient;
import com.xtremis.daedo.tkstrike.ei.client.WtOvrClientService;
import com.xtremis.daedo.tkstrike.om.MatchLogDto;
import com.xtremis.daedo.tkstrike.service.AppStatusWorker;
import com.xtremis.daedo.tkstrike.service.DifferentialScoreDefinitionService;
import com.xtremis.daedo.tkstrike.service.ExternalConfigService;
import com.xtremis.daedo.tkstrike.service.MatchConfigurationService;
import com.xtremis.daedo.tkstrike.service.MatchLogService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchConfigurationDto;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.TkStrikeKeyCombinationsHelper;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeController;
import com.xtremis.daedo.tkstrike.ui.model.DifferentialScoreDefinitionEntry;
import com.xtremis.daedo.tkstrike.ui.model.ExternalConfigEntry;
import com.xtremis.daedo.tkstrike.ui.model.IMatchConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.MatchConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.NetworkConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.RulesEntry;
import com.xtremis.daedo.tkstrike.utils.TkStrikeDatabaseMigration;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;
import jfxtras.labs.dialogs.MonologFXButton;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class RingManagerControllerController extends BaseCommonRingManagerController<NetworkConfigurationEntry> {
  protected static final Logger logger = Logger.getLogger(RingManagerControllerController.class);
  
  @Value("${tkStrike.maxGamJeomsAllowed}")
  private Integer maxGamJeomsAllowed;
  
  @FXML
  private ProgressIndicator pi;
  
  @FXML
  private Pane piPanel;
  
  @FXML
  private Label lblPiMessage;
  
  @FXML
  private Pane pnContainer;
  
  @FXML
  private Label lblMatchNumberInfo;
  
  @FXML
  private Label lblMaxGamJeomsAllowed;
  
  @FXML
  private Label lblDifferentialScore;
  
  @FXML
  private Label lblPhaseInfo;
  
  @FXML
  private Label lblCategoryInfo;
  
  @FXML
  private Label lblBlueInfo;
  
  @FXML
  private ImageView ivBlueInfo;
  
  @FXML
  private Label lblRedInfo;
  
  @FXML
  private ImageView ivRedInfo;
  
  @FXML
  private Label lblRoundsInfo;
  
  @FXML
  private Label lblRoundTimeInfo;
  
  @FXML
  private Label lblKyeShiTimeInfo;
  
  @FXML
  private Label lblRestTimeInfo;
  
  @FXML
  private Label lblGoldenPointTimeInfo;
  
  @FXML
  private Pane pnGoldenPoint;
  
  @FXML
  private Button btNextMatch;
  
  @FXML
  private Button btPrevMatch;
  
  @FXML
  private Pane pnTkStrikeOVR;
  
  @FXML
  private Button btNewMatch;
  
  @Autowired
  private MatchConfigurationService matchConfigurationService;
  
  @Autowired
  private PhasesManagementController phasesManagementController;
  
  @Autowired
  private CategoriesMainController categoriesMainController;
  
  @Autowired
  private DifferentialScoreDefinitionManagementController differentialScoreDefinitionManagementController;
  
  @Autowired
  private RingManagerWizardController ringManagerWizardController;
  
  @Autowired
  private FlagsManagementController flagsManagementController;
  
  @Autowired
  private AthletesManagementController athletesManagementController;
  
  @Autowired
  private MatchConfigurationManagementController matchConfigurationManagementController;
  
  @Autowired
  private ExternalConfigService externalConfigService;
  
  @Autowired
  private VenueManagementClient venueManagementClient;
  
  @Autowired
  private MatchLogService matchLogService;
  
  @Autowired
  private DifferentialScoreDefinitionService differentialScoreDefinitionService;
  
  @Autowired
  private WtOvrClientService wtOvrClientService;
  
  private boolean allowVM = false;
  
  private MatchConfigurationEntry matchConfigurationEntry = null;
  
  public void doChoseLastMatch() {
    showProgressIndicator(true);
    TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {
          public Void call() throws Exception {
            try {
              MatchLogDto lastMatchLog = (MatchLogDto)RingManagerControllerController.this.matchLogService.getLastStarted();
              if (lastMatchLog != null) {
                final MatchConfigurationEntry lastMatchConfig = (MatchConfigurationEntry)RingManagerControllerController.this.matchConfigurationService.getEntryById(lastMatchLog.getMatchConfigurationId());
                if (lastMatchConfig != null) {
                  if (lastMatchConfig.isReadyForStart()) {
                    Platform.runLater(new Runnable() {
                          public void run() {
                            RingManagerControllerController.this.matchConfigurationEntry = lastMatchConfig;
                            RingManagerControllerController.this.updateDifferentialScoreIfNeed();
                            RingManagerControllerController.this.getAppStatusWorker().setMatchConfigurationEntry((IMatchConfigurationEntry)RingManagerControllerController.this.matchConfigurationEntry);
                            RingManagerControllerController.this._refreshAllSteps();
                          }
                        });
                  } else {
                    RingManagerControllerController.this.showProgressIndicator(false);
                    RingManagerControllerController.this.showMonologInfoDialog(RingManagerControllerController.this.getMessage("title.default.error"), RingManagerControllerController.this.getMessage("message.warning.matchIsNotReady"));
                  } 
                } else {
                  RingManagerControllerController.this.showProgressIndicator(false);
                } 
              } else {
                RingManagerControllerController.this.showProgressIndicator(false);
              } 
            } catch (TkStrikeServiceException e) {
              e.printStackTrace();
            } 
            return null;
          }
        });
  }
  
  public void doRestoreLastMatch() {
    showProgressIndicator(true);
    try {
      final MatchLogDto lastMatchLog = (MatchLogDto)this.matchLogService.getLastStarted();
      if (lastMatchLog != null)
        if (lastMatchLog.getMatchVictoryCriteria() != null && lastMatchLog
          .getMatchVictoryCriteria().equals(getAppStatusWorker().getRulesEntry().getMatchVictoryCriteria())) {
          Optional<T> opResponse = showConfirmDialog(getMessage("message.confirmDialog.title"), 
              getMessage("message.confirm.restoreLastMatch", new String[] { lastMatchLog.getMatchNumber(), getDfFullFormat().format(new Date(lastMatchLog.getMatchStartTime().longValue())) }));
          if (opResponse != null && opResponse.isPresent() && opResponse.get().equals(ButtonType.OK)) {
            final MatchConfigurationEntry lastMatchConfig = (MatchConfigurationEntry)this.matchConfigurationService.getEntryById(lastMatchLog.getMatchConfigurationId());
            if (lastMatchConfig != null)
              Platform.runLater(new Runnable() {
                    public void run() {
                      RingManagerControllerController.this.matchConfigurationEntry = lastMatchConfig;
                      RingManagerControllerController.this.updateDifferentialScoreIfNeed();
                      RingManagerControllerController.this.getAppStatusWorker().doResetMatchWithMatchLogId((IMatchConfigurationEntry)RingManagerControllerController.this.matchConfigurationEntry, lastMatchLog.getId());
                      RingManagerControllerController.this._refreshAllSteps();
                    }
                  }); 
          } 
        } else {
          showErrorDialog(getMessage("title.default.error"), 
              getMessage("message.error.restoreLastMatch.matchVictoryCriteriaNotEquals"));
        }  
      showProgressIndicator(false);
    } catch (Exception e) {
      showProgressIndicator(false);
      e.printStackTrace();
      manageException(e, "", null);
    } 
  }
  
  public void doRestoreDefaultConfig() {
    if (((ButtonType)showConfirmDialog(getMessage("title.default.question"), 
        getMessage("message.confirmDialog.restoreDefaultConfig1")).get()).equals(ButtonType.OK) && (
      (ButtonType)showConfirmDialog(getMessage("title.default.question"), 
        getMessage("message.confirmDialog.restoreDefaultConfig2")).get()).equals(ButtonType.OK)) {
      showProgressIndicator(true);
      TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {
            public Void call() throws Exception {
              TkStrikeDatabaseMigration tkStrikeDatabaseMigration = new TkStrikeDatabaseMigration();
              tkStrikeDatabaseMigration.forceCleanAndMigrate();
              RingManagerControllerController.this.getAppStatusWorker().doTryToExitTkStrike();
              return null;
            }
          });
    } 
  }
  
  public void openAthletesManagement() {
    openInNewStage((TkStrikeController)this.athletesManagementController, getMessage("title.window.athletesManagement"), 900, 600);
  }
  
  public void openPhasesManagement() {
    openInNewStage((TkStrikeController)this.phasesManagementController, getMessage("title.window.phaseManagement"), 900, 600);
  }
  
  public void openCategoriesManagement() {
    openInNewStage(this.categoriesMainController, getMessage("title.window.categories"), 900, 600);
  }
  
  public void openDifferentialScoreDefinitions() {
    openInNewStage((TkStrikeController)this.differentialScoreDefinitionManagementController, getMessage("title.window.categories"), 900, 600);
  }
  
  public void openSelectMatch() {
    doOpenMatchSelection(MatchConfigurationManagementController.OpenType.NORMAL);
  }
  
  private void doOpenMatchSelection(MatchConfigurationManagementController.OpenType openType) {
    this.matchConfigurationManagementController.setOpenType(openType);
    openInNewStage((TkStrikeController)this.matchConfigurationManagementController, event -> {
          showProgressIndicator(true);
          final MatchConfigurationEntry selected = this.matchConfigurationManagementController.getMatchConfigurationEntry();
          if (selected != null) {
            if (selected.isReadyForStart()) {
              Platform.runLater(new Runnable() {
                    public void run() {
                      RingManagerControllerController.this.matchConfigurationEntry = selected;
                      RingManagerControllerController.this.updateDifferentialScoreIfNeed();
                      RingManagerControllerController.this.getAppStatusWorker().setMatchConfigurationEntry((IMatchConfigurationEntry)RingManagerControllerController.this.matchConfigurationEntry);
                      RingManagerControllerController.this._refreshAllSteps();
                    }
                  },  );
            } else {
              showProgressIndicator(false);
              showMonologInfoDialog(getMessage("title.default.error"), getMessage("message.warning.matchIsNotReady"));
            } 
          } else {
            showProgressIndicator(false);
          } 
        }getMessage("title.window.selectMatch"), 950, 500, true);
  }
  
  private void updateDifferentialScoreIfNeed() {
    if (this.matchConfigurationEntry.getPhase() != null && this.matchConfigurationEntry.getPhase().getId() != null && this.matchConfigurationEntry
      .getSubCategory() != null && this.matchConfigurationEntry.getSubCategory().getId() != null) {
      DifferentialScoreDefinitionEntry differentialScoreDefinitionEntry = null;
      try {
        differentialScoreDefinitionEntry = this.differentialScoreDefinitionService.getEntryByPhaseIdAndSubCategoryId(this.matchConfigurationEntry.getPhase().getId(), this.matchConfigurationEntry.getSubCategory().getId());
        if (differentialScoreDefinitionEntry != null) {
          this.matchConfigurationEntry.differencialScoreProperty().set(differentialScoreDefinitionEntry.getValue());
          this.matchConfigurationEntry = (MatchConfigurationEntry)this.matchConfigurationService.updateByEntry((IMatchConfigurationEntry)this.matchConfigurationEntry);
        } 
      } catch (TkStrikeServiceException e) {
        manageException((Throwable)e, "", null);
      } 
    } 
  }
  
  public void openWizard() {
    openInNewStage((TkStrikeController)this.ringManagerWizardController, new EventHandler<WindowEvent>() {
          public void handle(WindowEvent windowEvent) {
            if (!RingManagerControllerController.this.ringManagerWizardController.isWizardCanceled()) {
              RingManagerControllerController.this.showProgressIndicator(true);
              final MatchConfigurationEntry selected = RingManagerControllerController.this.ringManagerWizardController.getMatchConfigurationEntry();
              if (selected != null && StringUtils.isNotBlank(selected.getId())) {
                if (selected.isReadyForStart()) {
                  Platform.runLater(new Runnable() {
                        public void run() {
                          RingManagerControllerController.this.matchConfigurationEntry = selected;
                          RingManagerControllerController.this.getAppStatusWorker().setMatchConfigurationEntry((IMatchConfigurationEntry)RingManagerControllerController.this.matchConfigurationEntry);
                          RingManagerControllerController.this._refreshAllSteps();
                        }
                      },  );
                } else {
                  RingManagerControllerController.this.showProgressIndicator(false);
                  RingManagerControllerController.this.showMonologConfirmDialog(RingManagerControllerController.this.getMessage("title.default.error"), RingManagerControllerController.this.getMessage("message.warning.matchIsNotReady"));
                } 
              } else {
                RingManagerControllerController.this.showProgressIndicator(false);
              } 
            } 
          }
        }getMessage("title.window.ringManagerWizard"), 900, 550, true);
  }
  
  public void openFlagsManagement() {
    openInNewStage((TkStrikeController)this.flagsManagementController, getMessage("title.window.flagsManagement"), 900, 600);
  }
  
  public void close() {
    doCloseThisStage();
  }
  
  public void requestNextMatch() {
    if (this.wtCompetitionDataProtocol.booleanValue()) {
      doOpenMatchSelection(MatchConfigurationManagementController.OpenType.WT_OVR_MATCH_REQUEST);
    } else {
      _requestMatch(true);
    } 
  }
  
  public void requestPrevMatch() {
    if (this.wtCompetitionDataProtocol.booleanValue()) {
      doOpenMatchSelection(MatchConfigurationManagementController.OpenType.PREVIOUS_MATCHES);
    } else {
      _requestMatch(false);
    } 
  }
  
  public void wtOvrGetMatches() {
    TkStrikeExecutors.executeInThreadPool(new Runnable() {
          public void run() {
            RingManagerControllerController.this.showProgressIndicator(true);
            boolean showError = true;
            int matchesImported = 0;
            try {
              ExternalConfigEntry externalConfigEntry = RingManagerControllerController.this.externalConfigService.getExternalConfigEntry();
              if (externalConfigEntry != null) {
                RingManagerControllerController.logger.info("External Config entry OK");
                List<MatchConfigurationDto> matches = RingManagerControllerController.this.wtOvrClientService.findWtOvrMatches(externalConfigEntry.getWtOvrUrl(), externalConfigEntry.getWtOvrXApiKey(), Integer.valueOf(externalConfigEntry.getWtOvrMat()));
                if (matches != null) {
                  RingManagerControllerController.logger.info("Tenim matches " + matches.size());
                  for (MatchConfigurationDto match : matches) {
                    if (match != null && match.getInternalId() != null) {
                      Platform.runLater(new Runnable() {
                            public void run() {
                              RingManagerControllerController.this.lblPiMessage.setText("Processing match " + match.getInternalId());
                            }
                          });
                      RingManagerControllerController.logger.info("Anem a processar match " + match.getInternalId());
                      MatchConfigurationEntry newMatchConfigurationEntry = RingManagerControllerController.this.matchConfigurationService.transformByDto(match, true);
                      RingManagerControllerController.logger.info("Hem transformat i desat a la base de dades el match " + newMatchConfigurationEntry.getId());
                      matchesImported++;
                      continue;
                    } 
                    RingManagerControllerController.logger.info("Get a match without internalId ->" + ReflectionToStringBuilder.reflectionToString(match));
                  } 
                } 
              } 
            } catch (TkStrikeServiceException e) {
              RingManagerControllerController.this.showProgressIndicator(false);
              showError = false;
              RingManagerControllerController.this.manageException((Throwable)e, "RequestMatch to WT OVR", e.getMessage());
            } catch (Exception e) {
              RingManagerControllerController.this.manageException(e, "RequestMatch to WT OVR", e.getMessage());
            } 
            if (matchesImported > 0)
              RingManagerControllerController.this.showInfoDialog(RingManagerControllerController.this.getMessage("title.default.info"), RingManagerControllerController.this.getMessage("message.info.wtOvrMatchesImported", new String[] { "" + matchesImported })); 
            RingManagerControllerController.this.showProgressIndicator(false);
          }
        });
  }
  
  protected void showProgressIndicator(final boolean show) {
    Platform.runLater(new Runnable() {
          public void run() {
            RingManagerControllerController.this.pnContainer.setVisible(!show);
            RingManagerControllerController.this.piPanel.setVisible(show);
            RingManagerControllerController.this.lblPiMessage.setText(RingManagerControllerController.this.getMessage("message.info.workingPleaseWait"));
          }
        });
  }
  
  private void _requestMatch(final boolean next) {
    if (this.allowVM)
      TkStrikeExecutors.executeInThreadPool(new Runnable() {
            public void run() {
              RingManagerControllerController.this.showProgressIndicator(true);
              boolean showError = true;
              try {
                MatchConfigurationDto theMatch = next ? RingManagerControllerController.this.venueManagementClient.getNextMatch() : RingManagerControllerController.this.venueManagementClient.getPrevMatch();
                if (theMatch != null) {
                  final MatchConfigurationEntry newMatchConfigurationEntry = RingManagerControllerController.this.matchConfigurationService.transformByDto(theMatch, false);
                  if (newMatchConfigurationEntry != null) {
                    if (theMatch.getDifferencialScore() == null || theMatch.getDifferencialScore().equals(Integer.valueOf(0))) {
                      newMatchConfigurationEntry.differencialScoreProperty().set(RingManagerControllerController.this.getAppStatusWorker().getRulesEntry().getDifferencialScore());
                      if (newMatchConfigurationEntry.getPhase() != null && newMatchConfigurationEntry.getPhase().getId() != null && newMatchConfigurationEntry
                        .getSubCategory() != null && newMatchConfigurationEntry.getSubCategory().getId() != null) {
                        DifferentialScoreDefinitionEntry differentialScoreDefinitionEntry = RingManagerControllerController.this.differentialScoreDefinitionService.getEntryByPhaseIdAndSubCategoryId(newMatchConfigurationEntry.getPhase().getId(), newMatchConfigurationEntry.getSubCategory().getId());
                        if (differentialScoreDefinitionEntry != null)
                          newMatchConfigurationEntry.differencialScoreProperty().set(differentialScoreDefinitionEntry.getValue()); 
                      } 
                    } 
                    if (newMatchConfigurationEntry.getMaxAllowedGamJeoms() == null || newMatchConfigurationEntry.getMaxAllowedGamJeoms().equals(Integer.valueOf(0)))
                      newMatchConfigurationEntry.setMaxAllowedGamJeoms(RingManagerControllerController.this.maxGamJeomsAllowed.intValue()); 
                    RulesEntry rulesEntry = (RulesEntry)RingManagerControllerController.this.getAppStatusWorker().getRulesEntry();
                    if (rulesEntry != null && rulesEntry.isAllMatchPARA()) {
                      RingManagerControllerController.logger.info("Default RULES Configuration define all matches will be PARA Tkd.");
                      newMatchConfigurationEntry.setIsParaTkdMatch(true);
                    } 
                    showError = false;
                    if (newMatchConfigurationEntry.isReadyForStart()) {
                      Platform.runLater(new Runnable() {
                            public void run() {
                              RingManagerControllerController.this.matchConfigurationEntry = newMatchConfigurationEntry;
                              if (RingManagerControllerController.logger.isDebugEnabled())
                                RingManagerControllerController.logger.debug("Has new Match -" + ToStringBuilder.reflectionToString(RingManagerControllerController.this.matchConfigurationEntry)); 
                              RingManagerControllerController.this.getAppStatusWorker().setMatchConfigurationEntry((IMatchConfigurationEntry)RingManagerControllerController.this.matchConfigurationEntry);
                              if (RingManagerControllerController.logger.isDebugEnabled())
                                RingManagerControllerController.logger.debug("Go call refreshAllSteps"); 
                              RingManagerControllerController.this._refreshAllSteps();
                              RingManagerControllerController.this.showProgressIndicator(false);
                            }
                          });
                    } else {
                      RingManagerControllerController.this.showProgressIndicator(false);
                      RingManagerControllerController.this.showMonologInfoDialog(RingManagerControllerController.this.getMessage("title.default.error"), RingManagerControllerController.this.getMessage("message.warning.matchIsNotReady"));
                    } 
                  } 
                } 
              } catch (TkStrikeServiceException e) {
                RingManagerControllerController.this.showProgressIndicator(false);
                showError = false;
                RingManagerControllerController.this.manageException((Throwable)e, "RequestMatch to VM", e.getMessage());
              } catch (Exception e) {
                e.printStackTrace();
              } 
              if (showError) {
                RingManagerControllerController.this.showProgressIndicator(false);
                RingManagerControllerController.this.showErrorDialog(RingManagerControllerController.this.getMessage("title.default.error"), RingManagerControllerController.this.getMessage("message.error.venueManagement"));
              } 
            }
          }); 
  }
  
  void _onWindowShowEvent() {
    this.allowVM = false;
    ExternalConfigEntry externalConfigEntry = null;
    try {
      externalConfigEntry = this.externalConfigService.getExternalConfigEntry();
      if (externalConfigEntry != null)
        if (this.wtCompetitionDataProtocol.booleanValue()) {
          this.allowVM = StringUtils.isNotBlank(externalConfigEntry.getWtOvrUrl());
        } else {
          this.allowVM = StringUtils.isNotBlank(externalConfigEntry.getVenueManagementURL());
        }  
    } catch (TkStrikeServiceException e) {
      manageException((Throwable)e, "RingManager getExternalConfig", null);
    } 
    this.btNextMatch.setDisable(!this.allowVM);
    this.btPrevMatch.setDisable(!this.allowVM);
    _refreshAllSteps();
  }
  
  protected void _refreshAllSteps() {
    showProgressIndicator(false);
    this.lblMatchNumberInfo.setVisible(false);
    this.lblMaxGamJeomsAllowed.setVisible(false);
    this.lblDifferentialScore.setVisible(false);
    this.lblPhaseInfo.setVisible(false);
    this.lblCategoryInfo.setVisible(false);
    this.lblBlueInfo.setVisible(false);
    this.ivBlueInfo.setVisible(false);
    this.lblRedInfo.setVisible(false);
    this.ivRedInfo.setVisible(false);
    this.lblRoundsInfo.setVisible(false);
    this.lblRoundTimeInfo.setVisible(false);
    this.lblKyeShiTimeInfo.setVisible(false);
    this.lblRestTimeInfo.setVisible(false);
    this.pnGoldenPoint.setVisible(false);
    if (this.matchConfigurationEntry != null) {
      if (StringUtils.isNotBlank(this.matchConfigurationEntry.getMatchNumber())) {
        this.lblMatchNumberInfo.setVisible(true);
        this.lblMatchNumberInfo.setText(this.matchConfigurationEntry.getMatchNumber() + (this.matchConfigurationEntry.isParaTkdMatch() ? " (PARA TKD)" : ""));
      } 
      if (this.matchConfigurationEntry.getMaxAllowedGamJeoms() != null) {
        this.lblMaxGamJeomsAllowed.setVisible(true);
        this.lblMaxGamJeomsAllowed.setText(this.matchConfigurationEntry.getMaxAllowedGamJeoms().toString());
      } 
      if (this.matchConfigurationEntry.getDifferencialScore() != null) {
        this.lblDifferentialScore.setVisible(true);
        this.lblDifferentialScore.setText(this.matchConfigurationEntry.getDifferencialScore().toString());
      } 
      if (this.matchConfigurationEntry.getPhase() != null && 
        StringUtils.isNotBlank(this.matchConfigurationEntry.getPhase().getId())) {
        this.lblPhaseInfo.setVisible(true);
        this.lblPhaseInfo.setText(getMessage("message.phaseWithDiffScore", new String[] { this.matchConfigurationEntry.getPhase().getName(), "" + this.matchConfigurationEntry.getDifferencialScore() }));
      } 
      if (this.matchConfigurationEntry.getCategory() != null && 
        StringUtils.isNotBlank(this.matchConfigurationEntry.getCategory().getId())) {
        this.lblCategoryInfo.setVisible(true);
        this.lblCategoryInfo.setText(this.matchConfigurationEntry.getCategory().getSubCategory().getName() + " " + this.matchConfigurationEntry
            .getCategory().getGender().toString() + " " + this.matchConfigurationEntry
            .getCategory().getName());
      } 
      if (this.matchConfigurationEntry.getBlueAthlete() != null && 
        StringUtils.isNotBlank(this.matchConfigurationEntry.getBlueAthlete().getId())) {
        this.lblBlueInfo.setVisible(true);
        this.ivBlueInfo.setVisible(true);
        this.lblBlueInfo.setText(this.matchConfigurationEntry.getBlueAthlete().getScoreboardName() + " - " + (
            (this.matchConfigurationEntry.getBlueAthlete().getFlag() != null) ? (this.matchConfigurationEntry.getBlueAthlete().getFlag().isShowName() ? this.matchConfigurationEntry.getBlueAthlete().getFlag().getName() : this.matchConfigurationEntry.getBlueAthlete().getFlag().getAbbreviation()) : ""));
        this.ivBlueInfo.setImage(this.matchConfigurationEntry.getBlueAthlete().getFlag().getImage());
      } 
      if (this.matchConfigurationEntry.getRedAthlete() != null && 
        StringUtils.isNotBlank(this.matchConfigurationEntry.getRedAthlete().getId())) {
        this.lblRedInfo.setVisible(true);
        this.ivRedInfo.setVisible(true);
        this.lblRedInfo.setText(this.matchConfigurationEntry.getRedAthlete().getScoreboardName() + " - " + (
            (this.matchConfigurationEntry.getRedAthlete().getFlag() != null) ? (this.matchConfigurationEntry.getRedAthlete().getFlag().isShowName() ? this.matchConfigurationEntry.getRedAthlete().getFlag().getName() : this.matchConfigurationEntry.getRedAthlete().getFlag().getAbbreviation()) : ""));
        this.ivRedInfo.setImage(this.matchConfigurationEntry.getRedAthlete().getFlag().getImage());
      } 
      if (this.matchConfigurationEntry.getRoundsConfig() != null && this.matchConfigurationEntry
        .getRoundsConfig().getRounds() > 0) {
        this.lblRoundsInfo.setVisible(true);
        this.lblRoundsInfo.setText("" + this.matchConfigurationEntry.getRoundsConfig().getRounds());
      } 
      if (this.matchConfigurationEntry.getRoundsConfig() != null && 
        StringUtils.isNotBlank(this.matchConfigurationEntry.getRoundsConfig().getRoundTimeStr())) {
        this.lblRoundTimeInfo.setVisible(true);
        this.lblRoundTimeInfo.setText(this.matchConfigurationEntry.getRoundsConfig().getRoundTimeStr());
      } 
      if (this.matchConfigurationEntry.getRoundsConfig() != null && 
        StringUtils.isNotBlank(this.matchConfigurationEntry.getRoundsConfig().getKyeShiTimeStr())) {
        this.lblKyeShiTimeInfo.setVisible(true);
        this.lblKyeShiTimeInfo.setText(this.matchConfigurationEntry.getRoundsConfig().getKyeShiTimeStr());
      } 
      if (this.matchConfigurationEntry.getRoundsConfig() != null && 
        StringUtils.isNotBlank(this.matchConfigurationEntry.getRoundsConfig().getRestTimeStr())) {
        this.lblRestTimeInfo.setVisible(true);
        this.lblRestTimeInfo.setText(this.matchConfigurationEntry.getRoundsConfig().getRestTimeStr());
      } 
      if (this.matchConfigurationEntry.getRoundsConfig() != null && this.matchConfigurationEntry
        .getRoundsConfig().getGoldenPointEnabled()) {
        this.pnGoldenPoint.setVisible(true);
        this.lblGoldenPointTimeInfo.setText(this.matchConfigurationEntry.getRoundsConfig().getGoldenPointTimeStr());
      } 
    } 
  }
  
  public void initialize(URL url, ResourceBundle resourceBundle) {}
  
  void _afterPropertiesSet() throws Exception {
    this.rootView.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
          public void handle(KeyEvent event) {
            if (TkStrikeKeyCombinationsHelper.keyCombENTER.match(event))
              RingManagerControllerController.this.close(); 
          }
        });
  }
}
