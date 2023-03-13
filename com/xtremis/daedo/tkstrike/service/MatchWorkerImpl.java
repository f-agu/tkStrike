package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.communication.AthleteNode;
import com.xtremis.daedo.tkstrike.communication.ChangeNetworkConfigurationEvent;
import com.xtremis.daedo.tkstrike.communication.ChangeNetworkStatusEvent;
import com.xtremis.daedo.tkstrike.communication.CommonGlobalNetworkStatusController;
import com.xtremis.daedo.tkstrike.communication.GlobalNetworkStatusController;
import com.xtremis.daedo.tkstrike.communication.GlobalNetworkStatusControllerListener;
import com.xtremis.daedo.tkstrike.communication.StatusEvent;
import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationListener;
import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationService;
import com.xtremis.daedo.tkstrike.ei.client.RtBroadcastSocketClient;
import com.xtremis.daedo.tkstrike.ei.client.TkStrikeEventsListenerClient;
import com.xtremis.daedo.tkstrike.ei.client.TkStrikeUDPFacadeEventsListenerClient;
import com.xtremis.daedo.tkstrike.ei.client.VenueManagementClient;
import com.xtremis.daedo.tkstrike.ei.client.WtUDPService;
import com.xtremis.daedo.tkstrike.om.CommonMatchLogDto;
import com.xtremis.daedo.tkstrike.om.CommonMatchLogItemDto;
import com.xtremis.daedo.tkstrike.om.MatchLogDto;
import com.xtremis.daedo.tkstrike.om.MatchLogItemDto;
import com.xtremis.daedo.tkstrike.om.MatchStatusId;
import com.xtremis.daedo.tkstrike.om.combat.FinalDecision;
import com.xtremis.daedo.tkstrike.om.combat.MatchWinner;
import com.xtremis.daedo.tkstrike.orm.model.MatchConfiguration;
import com.xtremis.daedo.tkstrike.orm.model.MatchLogItemType;
import com.xtremis.daedo.tkstrike.orm.model.SensorsGroup;
import com.xtremis.daedo.tkstrike.tools.ei.om.AthleteDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchResultDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchVictoryCriteria;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeEventDto;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.model.ExternalConfigEntry;
import com.xtremis.daedo.tkstrike.ui.model.IMatchConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.INetworkConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.IRulesEntry;
import com.xtremis.daedo.tkstrike.ui.model.ISoundConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.MatchConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.NetworkAthletesGroupConfigEntry;
import com.xtremis.daedo.tkstrike.ui.model.NetworkConfigurationEntry;
import com.xtremis.daedo.wtdata.model.constants.ResultStatus;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Scope("singleton")
public class MatchWorkerImpl extends BaseCommonMatchWorker<GlobalNetworkStatusController, MatchLogService, MatchLogHistoricalService, SoundPlayerService, MatchLogDto, MatchLogItemDto, MatchConfigurationService, MatchConfiguration, MatchConfigurationEntry> implements GlobalNetworkStatusControllerListener, TkStrikeCommunicationListener, InitializingBean, MatchWorker {
  private NetworkConfigurationEntry currNetworkConfigurationEntry;
  
  private SimpleBooleanProperty blueRequestVideoScoreboard = new SimpleBooleanProperty(this, "blueRequestVideoScoreboard", false);
  
  private SimpleBooleanProperty redRequestVideoScoreboard = new SimpleBooleanProperty(this, "redRequestVideoScoreboard", false);
  
  private SimpleObjectProperty<MatchWorker.VideoRequestResult> blueVideoRequestResult = new SimpleObjectProperty(this, "blueVideoRequestResult", null);
  
  private SimpleObjectProperty<MatchWorker.VideoRequestResult> redVideoRequestResult = new SimpleObjectProperty(this, "redVideoRequestResult", null);
  
  private SimpleIntegerProperty blueVideoQuota = new SimpleIntegerProperty(this, "blueVideoQuota", 0);
  
  private SimpleIntegerProperty redVideoQuota = new SimpleIntegerProperty(this, "redVideoQuota", 0);
  
  private SimpleIntegerProperty minBodyLevel = new SimpleIntegerProperty(this, "minBodyLevel", 0);
  
  private int minBodyLevelVar = 0;
  
  private SimpleIntegerProperty minHeadLevel = new SimpleIntegerProperty(this, "minHeadLevel", 0);
  
  private int minHeadLevelVar = 0;
  
  private Boolean isParaTkdMatch = Boolean.FALSE;
  
  private SimpleObjectProperty<SensorsGroup> sensorsGroupSelected = new SimpleObjectProperty(this, "sensorsGroupSelected", SensorsGroup.GROUP1);
  
  private SimpleBooleanProperty togglesColorGroupSelectionVisible = new SimpleBooleanProperty(this, "togglesColorGroupSelectionVisible", Boolean.TRUE.booleanValue());
  
  private SimpleIntegerProperty blueGroupSelected = new SimpleIntegerProperty(this, "blueGroupSelected", 1);
  
  private SimpleIntegerProperty redGroupSelected = new SimpleIntegerProperty(this, "redGroupSelected", 1);
  
  private String currentMatchCategoryName = null;
  
  private String currentMatchCategoryGender = null;
  
  private String currentMatchSubCategoryName = null;
  
  private AthleteNode bodyBlueNode = null;
  
  private AthleteNode headBlueNode = null;
  
  private AthleteNode bodyRedNode = null;
  
  private AthleteNode headRedNode = null;
  
  private final VenueManagementClient venueManagementClient;
  
  private final GlobalNetworkStatusController globalNetworkStatusController;
  
  private final MatchLogService matchLogService;
  
  private final SoundPlayerService soundPlayerService;
  
  @Value("${tkStrike.maxGamJeomsAllowed}")
  private Integer maxGamJeomsAllowed;
  
  @Value("${tkStrike.allowGroupSelectionByColor}")
  private Boolean allowGroupSelectionByColor;
  
  @Autowired
  private MatchLogHistoricalService matchLogHistoricalService;
  
  @Autowired
  private MatchConfigurationService matchConfigurationService;
  
  @Autowired
  public MatchWorkerImpl(TkStrikeCommunicationService tkStrikeCommunicationService, AppStatusWorker appStatusWorker, RulesService rulesService, SoundConfigurationService soundConfigurationService, ExternalConfigService externalConfigService, TkStrikeEventsListenerClient tkStrikeEventsListenerClient, TkStrikeUDPFacadeEventsListenerClient tkStrikeUDPFacadeEventsListenerClient, RtBroadcastSocketClient rtBroadcastSocketClient, VenueManagementClient venueManagementClient, GlobalNetworkStatusController globalNetworkStatusController, MatchLogService matchLogService, SoundPlayerService soundPlayerService) {
    super(tkStrikeCommunicationService, appStatusWorker, rulesService, soundConfigurationService, externalConfigService, tkStrikeEventsListenerClient, tkStrikeUDPFacadeEventsListenerClient, rtBroadcastSocketClient);
    this.venueManagementClient = venueManagementClient;
    this.globalNetworkStatusController = globalNetworkStatusController;
    this.matchLogService = matchLogService;
    this.soundPlayerService = soundPlayerService;
  }
  
  MatchConfigurationService getMatchConfigurationService() {
    return this.matchConfigurationService;
  }
  
  MatchLogService getMatchLogService() {
    return this.matchLogService;
  }
  
  MatchLogHistoricalService getMatchLogHistoricalService() {
    return this.matchLogHistoricalService;
  }
  
  GlobalNetworkStatusController getGlobalNetworkStatusController() {
    return this.globalNetworkStatusController;
  }
  
  SoundPlayerService getSoundPlayerService() {
    return this.soundPlayerService;
  }
  
  void _changeRoundAndTime(MatchLogDto matchLog, MatchLogItemDto lastMatchLogItem) {
    Calendar roundTime = Calendar.getInstance();
    roundTime.setTimeInMillis(lastMatchLogItem.getRoundTime().longValue());
    matchWorkerLogger.info("Change to round " + lastMatchLogItem
        .getRoundNumber() + " " + lastMatchLogItem
        .getGoldenPointRound() + " Time " + roundTime
        .get(12) + ":" + roundTime.get(13));
    changeCurrentRoundAndTime(Integer.valueOf(lastMatchLogItem.getGoldenPointRound().booleanValue() ? -1 : lastMatchLogItem.getRoundNumber().intValue()), 
        Integer.valueOf(roundTime.get(12)), 
        Integer.valueOf(roundTime.get(13)), null);
  }
  
  void _restartMatchByMatchLog(MatchLogDto matchLog, List<MatchLogItemDto> matchLogItems) {
    if (matchLog != null && matchLogItems != null && matchLogItems.size() > 0) {
      matchLogItems.sort(getMatchLogService().getComparator4Items());
      MatchLogItemDto lastItem = matchLogItems.get(matchLogItems.size() - 1);
      this.blueVideoQuota.setValue(lastItem.getBlueVideoQuota());
      this.redVideoQuota.setValue(lastItem.getRedVideoQuota());
      MatchConfigurationEntry matchConfigurationEntry = (MatchConfigurationEntry)this.appStatusWorker.getMatchConfigurationEntry();
      matchConfigurationEntry.blueAthleteVideoQuotaProperty().setValue(lastItem.getBlueVideoQuota());
      matchConfigurationEntry.redAthleteVideoQuotaProperty().setValue(lastItem.getRedVideoQuota());
    } 
  }
  
  MatchLogItemDto newMatchLogItemDtoInstance() {
    return new MatchLogItemDto();
  }
  
  void fillExtraInfo2MatchLogItem(MatchLogItemDto matchLogItemDto) {
    matchLogItemDto.setBlueVideoQuota(Integer.valueOf(this.blueVideoQuota.get()));
    matchLogItemDto.setRedVideoQuota(Integer.valueOf(this.redVideoQuota.get()));
  }
  
  void fillExtraInfo2TkStrikeEvent(TkStrikeEventDto tkStrikeEvent) {
    if (tkStrikeEvent != null) {
      tkStrikeEvent.setMatchCategoryName(this.currentMatchCategoryName);
      tkStrikeEvent.setMatchCategoryGender(this.currentMatchCategoryGender);
      tkStrikeEvent.setMatchSubCategoryName(this.currentMatchSubCategoryName);
    } 
  }
  
  public boolean isBlueBodyNodeId(String nodeId) {
    return (this.bodyBlueNode != null && this.bodyBlueNode.getNodeId().equals(nodeId));
  }
  
  public boolean isBlueHeadNodeId(String nodeId) {
    return (this.headBlueNode != null && this.headBlueNode.getNodeId().equals(nodeId));
  }
  
  public boolean isRedBodyNodeId(String nodeId) {
    return (this.bodyRedNode != null && this.bodyRedNode.getNodeId().equals(nodeId));
  }
  
  public boolean isRedHeadNodeId(String nodeId) {
    return (this.headRedNode != null && this.headRedNode.getNodeId().equals(nodeId));
  }
  
  String getBlueName() {
    MatchConfigurationEntry matchConfigurationEntry = (MatchConfigurationEntry)this.appStatusWorker.getMatchConfigurationEntry();
    return (matchConfigurationEntry != null && matchConfigurationEntry.getBlueAthlete() != null) ? matchConfigurationEntry
      .getBlueAthlete().getScoreboardName() : "BLUE";
  }
  
  String getRedName() {
    MatchConfigurationEntry matchConfigurationEntry = (MatchConfigurationEntry)this.appStatusWorker.getMatchConfigurationEntry();
    return (matchConfigurationEntry != null && matchConfigurationEntry.getRedAthlete() != null) ? matchConfigurationEntry
      .getRedAthlete().getScoreboardName() : "RED";
  }
  
  void _internalConfirmFinalResult(MatchLogDto matchLog, MatchWinner winner, FinalDecision finalDecision) {
    _sendFinalDecisionToVenueManagement(matchLog);
  }
  
  Integer _getMaxGamJeomsAllowed() {
    MatchConfigurationEntry matchConfigurationEntry = (MatchConfigurationEntry)this.appStatusWorker.getMatchConfigurationEntry();
    if (matchConfigurationEntry.getMaxAllowedGamJeoms().intValue() > 0)
      return matchConfigurationEntry.getMaxAllowedGamJeoms(); 
    return this.maxGamJeomsAllowed;
  }
  
  Integer _getGoldenPointFinishPoints() {
    return this.overtimePoints;
  }
  
  Integer getDividePenaltiesInGoldenPoint() {
    return Integer.valueOf(1);
  }
  
  Integer getBlueCurrentPenalties4EventsListeners() {
    return Integer.valueOf(this.goldenPointWorking ? this.blueGoldenPointPenalties.get() : this.bluePenalties.get());
  }
  
  Integer getRedCurrentPenalties4EventsListeners() {
    return Integer.valueOf(this.goldenPointWorking ? this.redGoldenPointPenalties.get() : this.redPenalties.get());
  }
  
  public boolean getBlueRequestVideoScoreboard() {
    return this.blueRequestVideoScoreboard.get();
  }
  
  public SimpleBooleanProperty blueRequestVideoScoreboardProperty() {
    return this.blueRequestVideoScoreboard;
  }
  
  public boolean getRedRequestVideoScoreboard() {
    return this.redRequestVideoScoreboard.get();
  }
  
  public SimpleBooleanProperty redRequestVideoScoreboardProperty() {
    return this.redRequestVideoScoreboard;
  }
  
  public int getMinBodyLevel() {
    return this.minBodyLevel.get();
  }
  
  public ReadOnlyIntegerProperty minBodyLevelProperty() {
    return (ReadOnlyIntegerProperty)this.minBodyLevel;
  }
  
  public void setMinBodyLevel(int minBodyLevel) {
    if (minBodyLevel >= 0) {
      this.minBodyLevel.set(minBodyLevel);
      _updateMatchLogMinBodyLevel(Integer.valueOf(minBodyLevel));
    } 
  }
  
  public int getMinHeadLevel() {
    return this.minHeadLevel.get();
  }
  
  public ReadOnlyIntegerProperty minHeadLevelProperty() {
    return (ReadOnlyIntegerProperty)this.minHeadLevel;
  }
  
  public void setMinHeadLevel(int minHeadLevel) {
    if (minHeadLevel >= 0) {
      this.minHeadLevel.set(minHeadLevel);
      _updateMatchLogMinHeadLevel(Integer.valueOf(minHeadLevel));
    } 
  }
  
  public int getBlueVideoQuota() {
    return this.blueVideoQuota.get();
  }
  
  public SimpleIntegerProperty blueVideoQuotaProperty() {
    return this.blueVideoQuota;
  }
  
  public int getRedVideoQuota() {
    return this.redVideoQuota.get();
  }
  
  public SimpleIntegerProperty redVideoQuotaProperty() {
    return this.redVideoQuota;
  }
  
  public SimpleObjectProperty<MatchWorker.VideoRequestResult> blueVideoRequestResult() {
    return this.blueVideoRequestResult;
  }
  
  public SimpleObjectProperty<MatchWorker.VideoRequestResult> redVideoRequestResult() {
    return this.redVideoRequestResult;
  }
  
  public void hasNewStatusEvent(StatusEvent statusEvent) {}
  
  public void hasNewGlobalStatusEvent(StatusEvent statusEvent) {}
  
  public void hasChangeNetworkStatusEvent(ChangeNetworkStatusEvent changeNetworkStatusEvent) {}
  
  public void hasChangeNetworkConfigurationEvent(ChangeNetworkConfigurationEvent changeNetworkConfigurationEvent) {}
  
  public ReadOnlyObjectProperty<SensorsGroup> sensorsGroupSelectedProperty() {
    return (ReadOnlyObjectProperty<SensorsGroup>)this.sensorsGroupSelected;
  }
  
  public SensorsGroup getSensorsGroupSelected() {
    return (SensorsGroup)this.sensorsGroupSelected.get();
  }
  
  public void doChangeSensorsGroupSelection(SensorsGroup newSensorsGroupSelection) {
    if (matchWorkerLogger.isDebugEnabled())
      matchWorkerLogger.debug("DoChangeSensorsGroupSelection - " + newSensorsGroupSelection.toString()); 
    if (newSensorsGroupSelection != null) {
      this.sensorsGroupSelected.set(newSensorsGroupSelection);
      this.globalNetworkStatusController.doChangeSensorsGroupSelection(newSensorsGroupSelection);
    } 
  }
  
  private SensorsGroup getSensorGroupByNumber(Integer groupNumber) {
    switch (groupNumber.intValue()) {
      case 1:
        return SensorsGroup.GROUP1;
      case 2:
        return SensorsGroup.GROUP2;
      case 3:
        return SensorsGroup.GROUP3;
      case 4:
        return SensorsGroup.GROUP4;
      case 5:
        return SensorsGroup.GROUP5;
      case 6:
        return SensorsGroup.GROUP6;
    } 
    return SensorsGroup.GROUP1;
  }
  
  public ReadOnlyBooleanProperty togglesColorGroupSelectionVisible() {
    return (ReadOnlyBooleanProperty)this.togglesColorGroupSelectionVisible;
  }
  
  public void doChangeTogglesColorGroupSelectionVisible() {
    this.togglesColorGroupSelectionVisible.set(!this.togglesColorGroupSelectionVisible.get());
  }
  
  public ReadOnlyIntegerProperty blueGroupSelectedProperty() {
    return (ReadOnlyIntegerProperty)this.blueGroupSelected;
  }
  
  public void doChangeBlueGroupSelected(Integer newGroupSelected) {
    if (newGroupSelected.intValue() >= 1 && newGroupSelected
      .intValue() <= 6 && 
      !newGroupSelected.equals(this.blueGroupSelected.getValue()) && 
      !getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) && 
      !getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT) && 
      _doChangeBlueGroupSelected(newGroupSelected))
      this.globalNetworkStatusController.doChangeBlueGroupSelected(newGroupSelected); 
  }
  
  private boolean _doChangeBlueGroupSelected(Integer newGroupSelected) {
    matchWorkerLogger.info("Change BLUE selection to group " + newGroupSelected);
    boolean someChange = false;
    NetworkAthletesGroupConfigEntry networkAthletesGroupConfig = this.currNetworkConfigurationEntry.getGroupConfig(newGroupSelected);
    if (networkAthletesGroupConfig != null) {
      if (networkAthletesGroupConfig.getBodySensorsEnabled().booleanValue()) {
        if (networkAthletesGroupConfig.getBodyBlueNodeId() != null) {
          this.bodyBlueNode = new AthleteNode(newGroupSelected, networkAthletesGroupConfig.getBodyBlueNodeId());
          someChange = true;
        } 
      } else {
        this.bodyBlueNode = null;
      } 
      if (networkAthletesGroupConfig.getHeadSensorsEnabled().booleanValue()) {
        if (networkAthletesGroupConfig.getHeadBlueNodeId() != null) {
          this.headBlueNode = new AthleteNode(newGroupSelected, networkAthletesGroupConfig.getHeadBlueNodeId());
          someChange = true;
        } 
      } else {
        this.headBlueNode = null;
      } 
      if (someChange)
        this.blueGroupSelected.setValue(newGroupSelected); 
    } 
    return someChange;
  }
  
  public ReadOnlyIntegerProperty redGroupSelectedProperty() {
    return (ReadOnlyIntegerProperty)this.redGroupSelected;
  }
  
  public void doChangeRedGroupSelected(Integer newGroupSelected) {
    if (newGroupSelected.intValue() >= 1 && newGroupSelected
      .intValue() <= 6 && 
      !newGroupSelected.equals(this.redGroupSelected.getValue()) && 
      !getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) && 
      !getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT) && 
      _doChangeRedGroupSelected(newGroupSelected))
      this.globalNetworkStatusController.doChangeRedGroupSelected(newGroupSelected); 
  }
  
  private boolean _doChangeRedGroupSelected(Integer newGroupSelected) {
    boolean someChange = false;
    matchWorkerLogger.info("Change RED selection to group " + newGroupSelected);
    NetworkAthletesGroupConfigEntry networkAthletesGroupConfig = this.currNetworkConfigurationEntry.getGroupConfig(newGroupSelected);
    if (networkAthletesGroupConfig != null) {
      if (networkAthletesGroupConfig.getBodySensorsEnabled().booleanValue()) {
        if (networkAthletesGroupConfig.getBodyRedNodeId() != null) {
          this.bodyRedNode = new AthleteNode(newGroupSelected, networkAthletesGroupConfig.getBodyRedNodeId());
          someChange = true;
        } 
      } else {
        this.bodyRedNode = null;
      } 
      if (networkAthletesGroupConfig.getHeadSensorsEnabled().booleanValue()) {
        if (networkAthletesGroupConfig.getHeadRedNodeId() != null) {
          this.headRedNode = new AthleteNode(newGroupSelected, networkAthletesGroupConfig.getHeadRedNodeId());
          someChange = true;
        } 
      } else {
        this.headRedNode = null;
      } 
      if (someChange)
        this.redGroupSelected.setValue(newGroupSelected); 
    } 
    return someChange;
  }
  
  void _afterPropertiesSet() throws Exception {
    Assert.notNull(this.maxGamJeomsAllowed, "Property tkStrike.maxGamJeomsAllowed is not defined!!!!");
    this.blueVideoQuota.addListener(new ChangeListener<Number>() {
          public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newVideoQuota) {
            if (!MatchWorkerImpl.this.isLock().booleanValue() && MatchWorkerImpl.this.getCurrentMatchStatus().equals(MatchStatusId.ROUND_PAUSED))
              MatchWorkerImpl.this._addMatchLogItem(Long.valueOf(System.currentTimeMillis()), 
                  Long.valueOf(MatchWorkerImpl.this.getCurrentRoundCountdownAsMillis()), 
                  Long.valueOf(System.currentTimeMillis()), MatchWorkerImpl.this
                  .getRound4MatchLog(false), MatchWorkerImpl.this
                  .getRoundStr4MatchLog(false), MatchLogItemType.BLUE_VIDEO_QUOTA_CHANGED, 
                  
                  Integer.valueOf(0), 
                  Integer.valueOf(0), newVideoQuota
                  .toString(), true, Integer.valueOf(0), false); 
          }
        });
    this.redVideoQuota.addListener(new ChangeListener<Number>() {
          public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newVideoQuota) {
            if (!MatchWorkerImpl.this.isLock().booleanValue() && MatchWorkerImpl.this.getCurrentMatchStatus().equals(MatchStatusId.ROUND_PAUSED))
              MatchWorkerImpl.this._addMatchLogItem(Long.valueOf(System.currentTimeMillis()), 
                  Long.valueOf(MatchWorkerImpl.this.getCurrentRoundCountdownAsMillis()), 
                  Long.valueOf(System.currentTimeMillis()), MatchWorkerImpl.this
                  .getRound4MatchLog(false), MatchWorkerImpl.this
                  .getRoundStr4MatchLog(false), MatchLogItemType.RED_VIDEO_QUOTA_CHANGED, 
                  
                  Integer.valueOf(0), 
                  Integer.valueOf(0), newVideoQuota
                  .toString(), true, Integer.valueOf(0), false); 
          }
        });
    this.blueRequestVideoScoreboard.addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean value) {
            if (value.booleanValue())
              MatchWorkerImpl.this._addMatchLogItem(Long.valueOf(System.currentTimeMillis()), 
                  Long.valueOf(MatchWorkerImpl.this.getCurrentRoundCountdownAsMillis()), 
                  Long.valueOf(System.currentTimeMillis()), MatchWorkerImpl.this
                  .getRound4MatchLog(false), MatchWorkerImpl.this
                  .getRoundStr4MatchLog(false), MatchLogItemType.BLUE_VIDEO_REQUEST, 
                  
                  Integer.valueOf(0), 
                  Integer.valueOf(0), "", true, 
                  Integer.valueOf(0), false); 
          }
        });
    this.redRequestVideoScoreboard.addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean value) {
            if (value.booleanValue())
              MatchWorkerImpl.this._addMatchLogItem(Long.valueOf(System.currentTimeMillis()), 
                  Long.valueOf(MatchWorkerImpl.this.getCurrentRoundCountdownAsMillis()), 
                  Long.valueOf(System.currentTimeMillis()), MatchWorkerImpl.this
                  .getRound4MatchLog(false), MatchWorkerImpl.this
                  .getRoundStr4MatchLog(false), MatchLogItemType.RED_VIDEO_REQUEST, 
                  
                  Integer.valueOf(0), 
                  Integer.valueOf(0), "", true, 
                  Integer.valueOf(0), false); 
          }
        });
    this.blueVideoRequestResult.addListener(new ChangeListener<MatchWorker.VideoRequestResult>() {
          public void changed(ObservableValue<? extends MatchWorker.VideoRequestResult> observable, MatchWorker.VideoRequestResult oldValue, final MatchWorker.VideoRequestResult newValue) {
            if (MatchWorker.VideoRequestResult.ACCEPTED.equals(newValue))
              MatchWorkerImpl.this._addMatchLogItem(Long.valueOf(System.currentTimeMillis()), 
                  Long.valueOf(MatchWorkerImpl.this.getCurrentRoundCountdownAsMillis()), 
                  Long.valueOf(System.currentTimeMillis()), MatchWorkerImpl.this
                  .getRound4MatchLog(false), MatchWorkerImpl.this
                  .getRoundStr4MatchLog(false), MatchLogItemType.BLUE_VIDEO_QUOTA_ACCEPTED, 
                  
                  Integer.valueOf(0), 
                  Integer.valueOf(0), "", true, 
                  Integer.valueOf(0), false); 
            if (MatchWorkerImpl.this.wtUDPService.isConnected())
              TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {
                    public Void call() throws Exception {
                      MatchWorkerImpl.this.wtUDPService.sendVideoReplay(true, WtUDPService.VideoRequestResult.valueOf(newValue.toString()));
                      return null;
                    }
                  }); 
          }
        });
    this.redVideoRequestResult.addListener(new ChangeListener<MatchWorker.VideoRequestResult>() {
          public void changed(ObservableValue<? extends MatchWorker.VideoRequestResult> observable, MatchWorker.VideoRequestResult oldValue, final MatchWorker.VideoRequestResult newValue) {
            if (MatchWorker.VideoRequestResult.ACCEPTED.equals(newValue))
              MatchWorkerImpl.this._addMatchLogItem(Long.valueOf(System.currentTimeMillis()), 
                  Long.valueOf(MatchWorkerImpl.this.getCurrentRoundCountdownAsMillis()), 
                  Long.valueOf(System.currentTimeMillis()), MatchWorkerImpl.this
                  .getRound4MatchLog(false), MatchWorkerImpl.this
                  .getRoundStr4MatchLog(false), MatchLogItemType.RED_VIDEO_QUOTA_ACCEPTED, 
                  
                  Integer.valueOf(0), 
                  Integer.valueOf(0), "", true, 
                  Integer.valueOf(0), false); 
            if (MatchWorkerImpl.this.wtUDPService.isConnected())
              TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {
                    public Void call() throws Exception {
                      MatchWorkerImpl.this.wtUDPService.sendVideoReplay(false, WtUDPService.VideoRequestResult.valueOf(newValue.toString()));
                      return null;
                    }
                  }); 
          }
        });
    this.minBodyLevel.addListener(new ChangeListener<Number>() {
          public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            MatchWorkerImpl.this.minBodyLevelVar = newValue.intValue();
          }
        });
    this.minHeadLevel.addListener(new ChangeListener<Number>() {
          public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            MatchWorkerImpl.this.minHeadLevelVar = newValue.intValue();
          }
        });
    this.sensorsGroupSelected.addListener(new ChangeListener<SensorsGroup>() {
          public void changed(ObservableValue<? extends SensorsGroup> observable, SensorsGroup oldValue, SensorsGroup newValue) {
            if (BaseCommonMatchWorker.matchWorkerLogger.isDebugEnabled())
              BaseCommonMatchWorker.matchWorkerLogger.debug("sensorsGroupSelected has changed newValue " + newValue); 
            if (newValue != null)
              MatchWorkerImpl.this.updateNetworkConfiguration(); 
          }
        });
  }
  
  void _cleanMatchInfo() {
    this.blueVideoQuota.set(0);
    this.redVideoQuota.set(0);
    this.blueVideoRequestResult.setValue(null);
    this.redVideoRequestResult.setValue(null);
  }
  
  Integer getMinBodyLevel4AthleteNodeId(String athleteNodeId) {
    return Integer.valueOf(this.minBodyLevelVar);
  }
  
  Integer getMinHeadLevel4AthleteNodeId(String athleteNodeId) {
    return Integer.valueOf(this.minHeadLevelVar);
  }
  
  void _initializeRulesConfiguration(IRulesEntry rulesEntry) {}
  
  void _initializeSoundsConfiguration(ISoundConfigurationEntry soundConfigurationEntry) {}
  
  Integer _updateNetworkConfiguration(INetworkConfigurationEntry networkConfigurationEntry) {
    if (networkConfigurationEntry != null) {
      this.currNetworkConfigurationEntry = (NetworkConfigurationEntry)networkConfigurationEntry;
      if (this.allowGroupSelectionByColor.booleanValue()) {
        _doChangeBlueGroupSelected(Integer.valueOf(this.blueGroupSelected.get()));
        _doChangeRedGroupSelected(Integer.valueOf(this.redGroupSelected.get()));
      } else {
        if (SensorsGroup.GROUP2.equals(this.sensorsGroupSelected.get()) && this.currNetworkConfigurationEntry.getGroup2Enabled().booleanValue()) {
          if (matchWorkerLogger.isDebugEnabled())
            matchWorkerLogger.debug("Initialize By Group2 config" + ToStringBuilder.reflectionToString(networkConfigurationEntry.getGroup2Config())); 
          return initializeByGroupConfig(networkConfigurationEntry.getGroup2Config(), Integer.valueOf(2));
        } 
        if (matchWorkerLogger.isDebugEnabled())
          matchWorkerLogger.debug("Initialize By Group1 config " + ToStringBuilder.reflectionToString(networkConfigurationEntry.getGroup1Config())); 
        return initializeByGroupConfig(networkConfigurationEntry.getGroup1Config(), Integer.valueOf(1));
      } 
    } 
    return Integer.valueOf(0);
  }
  
  boolean isBodySensorsEnabled4Config(INetworkConfigurationEntry networkConfigurationEntry) {
    if (networkConfigurationEntry != null) {
      NetworkConfigurationEntry currNetworkConfigurationEntry = (NetworkConfigurationEntry)networkConfigurationEntry;
      if (SensorsGroup.GROUP2.equals(this.sensorsGroupSelected.get()) && currNetworkConfigurationEntry.getGroup2Enabled().booleanValue())
        return networkConfigurationEntry.getGroup2Config().getBodySensorsEnabled().booleanValue(); 
      return networkConfigurationEntry.getGroup1Config().getBodySensorsEnabled().booleanValue();
    } 
    return false;
  }
  
  boolean isHeadSensorsEnabled4Config(INetworkConfigurationEntry networkConfigurationEntry) {
    if (networkConfigurationEntry != null) {
      NetworkConfigurationEntry currNetworkConfigurationEntry = (NetworkConfigurationEntry)networkConfigurationEntry;
      if (SensorsGroup.GROUP2.equals(this.sensorsGroupSelected.get()) && currNetworkConfigurationEntry.getGroup2Enabled().booleanValue())
        return networkConfigurationEntry.getGroup2Config().getHeadSensorsEnabled().booleanValue(); 
      return networkConfigurationEntry.getGroup1Config().getHeadSensorsEnabled().booleanValue();
    } 
    return false;
  }
  
  private Integer initializeByGroupConfig(NetworkAthletesGroupConfigEntry networkAthletesGroupConfig, Integer groupNumber) {
    Integer res = Integer.valueOf(0);
    if (networkAthletesGroupConfig != null) {
      if (networkAthletesGroupConfig.getBodySensorsEnabled().booleanValue()) {
        this.bodyBlueNode = new AthleteNode(groupNumber, networkAthletesGroupConfig.getBodyBlueNodeId());
        Integer integer1 = res, integer2 = res = Integer.valueOf(res.intValue() + 1);
        this.bodyRedNode = new AthleteNode(groupNumber, networkAthletesGroupConfig.getBodyRedNodeId());
        integer1 = res;
        integer2 = res = Integer.valueOf(res.intValue() + 1);
      } else {
        this.bodyBlueNode = null;
        this.bodyRedNode = null;
      } 
      if (networkAthletesGroupConfig.getHeadSensorsEnabled().booleanValue()) {
        this.headBlueNode = new AthleteNode(groupNumber, networkAthletesGroupConfig.getHeadBlueNodeId());
        Integer integer1 = res, integer2 = res = Integer.valueOf(res.intValue() + 1);
        this.headRedNode = new AthleteNode(groupNumber, networkAthletesGroupConfig.getHeadRedNodeId());
        integer1 = res;
        integer2 = res = Integer.valueOf(res.intValue() + 1);
      } else {
        this.headBlueNode = null;
        this.headRedNode = null;
      } 
      if (matchWorkerLogger.isDebugEnabled()) {
        matchWorkerLogger.debug("Athletes config ================================================");
        matchWorkerLogger.debug("  bodyBlue = " + ((this.bodyBlueNode != null) ? this.bodyBlueNode.getNodeId() : ""));
        matchWorkerLogger.debug("  headBlue = " + ((this.headBlueNode != null) ? this.headBlueNode.getNodeId() : ""));
        matchWorkerLogger.debug("  bodyRed = " + ((this.bodyRedNode != null) ? this.bodyRedNode.getNodeId() : ""));
        matchWorkerLogger.debug("  headRed = " + ((this.headRedNode != null) ? this.headRedNode.getNodeId() : ""));
        matchWorkerLogger.debug("================================================ Athletes config");
      } 
    } 
    return res;
  }
  
  void _updateMatchConfiguration(IMatchConfigurationEntry matchConfigurationEntry) {
    if (matchConfigurationEntry != null) {
      this.currentMatchCategoryName = null;
      this.currentMatchCategoryGender = null;
      this.currentMatchSubCategoryName = null;
      MatchConfigurationEntry currMatchConfigurationEntry = (MatchConfigurationEntry)matchConfigurationEntry;
      if (currMatchConfigurationEntry.getCategory() != null) {
        this.minBodyLevel.set(currMatchConfigurationEntry.getCategory().getBodyLevel());
        this.minHeadLevel.set(currMatchConfigurationEntry.getCategory().getHeadLevel());
        if (currMatchConfigurationEntry.getCategory() != null) {
          this.currentMatchCategoryName = currMatchConfigurationEntry.getCategory().getName();
          if (currMatchConfigurationEntry.getCategory().getGender() != null)
            this.currentMatchCategoryGender = currMatchConfigurationEntry.getCategory().getGender().toString(); 
          if (currMatchConfigurationEntry.getCategory().getSubCategory() != null)
            this.currentMatchSubCategoryName = currMatchConfigurationEntry.getCategory().getSubCategory().getName(); 
        } 
      } else {
        this.minBodyLevel.set(0);
        this.minHeadLevel.set(0);
      } 
      this.blueVideoQuota.set(currMatchConfigurationEntry.getBlueAthleteVideoQuota());
      this.redVideoQuota.set(currMatchConfigurationEntry.getRedAthleteVideoQuota());
      this.blueVideoRequestResult.setValue(null);
      this.redVideoRequestResult.setValue(null);
      this.isParaTkdMatch = Boolean.valueOf(currMatchConfigurationEntry.isParaTkdMatch());
      if (this.isParaTkdMatch.booleanValue()) {
        this.paraTimeOutCountdown.clean(0, 30);
        this.bluePARATimeOutQuota.set(true);
        this.redPARATimeOutQuota.set(true);
        this.bluePARATimeOutQuotaValue.set(1);
        this.redPARATimeOutQuotaValue.set(1);
        this.athleteWithPARATimeOutCountdownWorking = 0;
      } else {
        this.bluePARATimeOutQuota.set(false);
        this.redPARATimeOutQuota.set(false);
        this.bluePARATimeOutQuotaValue.set(0);
        this.redPARATimeOutQuotaValue.set(0);
      } 
    } 
  }
  
  public boolean isParaTkdMatch() {
    return this.isParaTkdMatch.booleanValue();
  }
  
  private void _sendFinalDecisionToVenueManagement(MatchLogDto matchLog) {
    if (getCurrentMatchStatus().equals(MatchStatusId.MATCH_FINISHED) && matchLog != null) {
      final MatchConfigurationEntry matchConfigurationEntry = (MatchConfigurationEntry)this.appStatusWorker.getMatchConfigurationEntry();
      if (matchConfigurationEntry != null) {
        FinalDecision finalDecision = getMatchFinalDecision();
        MatchWinner winnerColor = getMatchWinner();
        AthleteDto winnerTemp = null;
        if (MatchWinner.BLUE.equals(getMatchWinner())) {
          winnerTemp = matchConfigurationEntry.getBlueAthlete().getAthleteDto();
        } else if (MatchWinner.RED.equals(getMatchWinner())) {
          winnerTemp = matchConfigurationEntry.getRedAthlete().getAthleteDto();
        } 
        AthleteDto winner = winnerTemp;
        Integer bluePoints = getBlueMatchPoints();
        Integer fiBlueVideoQuota = Integer.valueOf(this.blueVideoQuota.get());
        Integer blueRoundsWins = getBlueRoundsWins();
        Integer redPoints = getRedMatchPoints();
        Integer fiRedVideoQuota = Integer.valueOf(this.redVideoQuota.get());
        Integer redRoundsWins = getRedRoundsWins();
        final MatchResultDto matchResultDto = new MatchResultDto();
        matchResultDto.setVmMatchInternalId(matchConfigurationEntry.getVmMatchInternalId());
        matchResultDto.setVmRingNumber(this.currentMatchVMRingNumber);
        matchResultDto.setCategoryName(this.currentMatchCategoryName);
        matchResultDto.setCategoryGender(this.currentMatchCategoryGender);
        matchResultDto.setSubCategoryName(this.currentMatchSubCategoryName);
        matchResultDto.setPhaseName(matchConfigurationEntry.getPhase().getName());
        matchResultDto.setMatchStartTime(matchLog.getMatchStartTime());
        matchResultDto.setMatchEndTime(matchLog.getMatchEndTime());
        matchResultDto.setGoldenPointTieBreakerHaveTieBreaker(Boolean.valueOf(false));
        if (matchLog.getGoldenPointTieBreakerInfo() != null && matchLog
          .getGoldenPointTieBreakerInfo().getHaveTieBreaker().booleanValue()) {
          matchResultDto.setGoldenPointTieBreakerHaveTieBreaker(matchLog.getGoldenPointTieBreakerInfo().getHaveTieBreaker());
          matchResultDto.setGoldenPointTieBreakerBluePunches(matchLog.getGoldenPointTieBreakerInfo().getBluePunches());
          matchResultDto.setGoldenPointTieBreakerBlueRoundWins(matchLog.getGoldenPointTieBreakerInfo().getBlueRoundWins());
          matchResultDto.setGoldenPointTieBreakerBlueHits(matchLog.getGoldenPointTieBreakerInfo().getBlueHits());
          matchResultDto.setGoldenPointTieBreakerBluePenalties(matchLog.getGoldenPointTieBreakerInfo().getBluePenalties());
          matchResultDto.setGoldenPointTieBreakerRedPunches(matchLog.getGoldenPointTieBreakerInfo().getRedPunches());
          matchResultDto.setGoldenPointTieBreakerRedRoundWins(matchLog.getGoldenPointTieBreakerInfo().getRedRoundWins());
          matchResultDto.setGoldenPointTieBreakerRedHits(matchLog.getGoldenPointTieBreakerInfo().getRedHits());
          matchResultDto.setGoldenPointTieBreakerRedPenalties(matchLog.getGoldenPointTieBreakerInfo().getRedPenalties());
          matchResultDto.setGoldenPointTieBreakerBluePARATechPoints(matchLog.getGoldenPointTieBreakerInfo().getBluePARATechPoints());
          matchResultDto.setGoldenPointTieBreakerRedPARATechPoints(matchLog.getGoldenPointTieBreakerInfo().getRedPARATechPoints());
        } 
        matchResultDto.setParaTkdMatch(matchLog.getParaTkdMatch());
        matchResultDto.setMatchVictoryCriteria((matchLog.getMatchVictoryCriteria() != null) ? matchLog.getMatchVictoryCriteria().toString() : null);
        matchResultDto.setMatchNumber(matchConfigurationEntry.getMatchNumber());
        matchResultDto.setMatchFinalDecision(finalDecision.toString());
        matchResultDto.setMatchWinnerColor(winnerColor.toString());
        matchResultDto.setMatchWinner(winner);
        matchResultDto.setRoundFinish(Integer.valueOf(this.currentRound.get()));
        matchResultDto.setBluePoints(bluePoints);
        matchResultDto.setBlueVideoQuota(fiBlueVideoQuota);
        matchResultDto.setBlueRoundWins(blueRoundsWins);
        matchResultDto.setBluePenalties(Integer.valueOf(getBlueTotalPenalties()));
        matchResultDto.setRedPoints(redPoints);
        matchResultDto.setRedVideoQuota(fiRedVideoQuota);
        matchResultDto.setRedRoundWins(redRoundsWins);
        matchResultDto.setRedPenalties(Integer.valueOf(getRedTotalPenalties()));
        if (MatchVictoryCriteria.BESTOF3.equals(this.matchVictoryCriteria)) {
          matchResultDto.setBluePoints(getBlueRoundsWins());
          matchResultDto.setRedPoints(getRedRoundsWins());
          matchResultDto.setBluePenalties(Integer.valueOf(getBluePenalties()));
          matchResultDto.setRedPenalties(Integer.valueOf(getRedPenalties()));
        } 
        TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {
              public Void call() throws Exception {
                if (StringUtils.isNotBlank(matchConfigurationEntry.getVmMatchInternalId()))
                  try {
                    MatchWorkerImpl.this.venueManagementClient.doSendMatchResult(matchResultDto);
                  } catch (TkStrikeServiceException e) {
                    BaseCommonMatchWorker.matchWorkerLogger.warn("Sending Match Result", e);
                  }  
                List<Callable<Void>> tasks = null;
                ExternalConfigEntry externalConfigEntry = MatchWorkerImpl.this.externalConfigService.getExternalConfigEntry();
                if (externalConfigEntry.getListenersURLs() != null) {
                  tasks = new ArrayList<>(externalConfigEntry.getListenersURLs().size());
                  for (String listenerURL : externalConfigEntry.getListenersURLs()) {
                    tasks.add(new Callable<Void>() {
                          public Void call() throws Exception {
                            MatchWorkerImpl.this.tkStrikeEventsListenerClient.doSendMatchResult(listenerURL, matchResultDto);
                            return null;
                          }
                        });
                  } 
                } 
                if (tasks != null)
                  TkStrikeExecutors.executeInParallel(tasks); 
                if (StringUtils.isNotBlank(matchConfigurationEntry.getVmMatchInternalId()) && 
                  StringUtils.isNotBlank(externalConfigEntry.getWtOvrUrl()))
                  MatchWorkerImpl.this.wtOvrClientService.sendMatchResult(externalConfigEntry.getWtOvrUrl(), externalConfigEntry
                      .getWtOvrXApiKey(), matchConfigurationEntry
                      .getVmMatchInternalId(), matchResultDto, ResultStatus.UNCONFIRMED, MatchWorkerImpl.this.currentMatchBlueAthleteOvrInternalId, MatchWorkerImpl.this.currentMatchRedAthleteOvrInternalId); 
                if (MatchWorkerImpl.this.wtUDPService.isConnected()) {
                  MatchWorkerImpl.this.wtUDPService.sendMatchResult(matchResultDto);
                  MatchWorkerImpl.this.wtUDPService.sendMatchWinner((MatchWinner)MatchWorkerImpl.this.matchWinner.getValue());
                } 
                return null;
              }
            });
      } 
    } 
  }
  
  private void _updateMatchLogMinBodyLevel(final Integer minBodyLevel) {
    TkStrikeExecutors.executeInThreadPool(new Runnable() {
          public void run() {
            try {
              if (MatchWorkerImpl.this.matchLogId != null)
                MatchWorkerImpl.this.matchLogService.updateMatchLogMinBodyLevel(MatchWorkerImpl.this.matchLogId, minBodyLevel); 
            } catch (TkStrikeServiceException e) {
              BaseCommonMatchWorker.matchWorkerLogger.error("Update MatchLog Min Body Level", e);
            } 
          }
        });
  }
  
  private void _updateMatchLogMinHeadLevel(final Integer minHeadLevel) {
    TkStrikeExecutors.executeInThreadPool(new Runnable() {
          public void run() {
            try {
              if (MatchWorkerImpl.this.matchLogId != null)
                MatchWorkerImpl.this.matchLogService.updateMatchLogMinHeadLevel(MatchWorkerImpl.this.matchLogId, minHeadLevel); 
            } catch (TkStrikeServiceException e) {
              BaseCommonMatchWorker.matchWorkerLogger.error("Update MatchLog Min Head Level", e);
            } 
          }
        });
  }
}
