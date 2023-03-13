package com.xtremis.daedo.tkstrike.service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.xtremis.daedo.tkstrike.communication.CommonGlobalNetworkStatusController;
import com.xtremis.daedo.tkstrike.communication.DataEvent;
import com.xtremis.daedo.tkstrike.communication.GlobalNetworkStatusControllerListener;
import com.xtremis.daedo.tkstrike.communication.JudgeNode;
import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationListener;
import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationService;
import com.xtremis.daedo.tkstrike.configuration.TkStrikeScoreboardGraphicDetailType;
import com.xtremis.daedo.tkstrike.ei.client.BestOf3PointsChange;
import com.xtremis.daedo.tkstrike.ei.client.RtBroadcastSocketClient;
import com.xtremis.daedo.tkstrike.ei.client.TkStrikeEventsListenerClient;
import com.xtremis.daedo.tkstrike.ei.client.TkStrikeUDPFacadeEventsListenerClient;
import com.xtremis.daedo.tkstrike.ei.client.WtOvrClientService;
import com.xtremis.daedo.tkstrike.ei.client.WtUDPService;
import com.xtremis.daedo.tkstrike.om.ActionSource;
import com.xtremis.daedo.tkstrike.om.AppStatusId;
import com.xtremis.daedo.tkstrike.om.CommonMatchLogDto;
import com.xtremis.daedo.tkstrike.om.CommonMatchLogItemDto;
import com.xtremis.daedo.tkstrike.om.GoldenPointTieBreakerInfoDto;
import com.xtremis.daedo.tkstrike.om.MatchStatusId;
import com.xtremis.daedo.tkstrike.om.NetworkErrorCause;
import com.xtremis.daedo.tkstrike.om.combat.BestOf3RoundSuperiority;
import com.xtremis.daedo.tkstrike.om.combat.FinalDecision;
import com.xtremis.daedo.tkstrike.om.combat.HitEventType;
import com.xtremis.daedo.tkstrike.om.combat.HitEventValidator;
import com.xtremis.daedo.tkstrike.om.combat.HitJudgeStatus;
import com.xtremis.daedo.tkstrike.om.combat.MatchWinner;
import com.xtremis.daedo.tkstrike.orm.model.MatchConfigurationEntity;
import com.xtremis.daedo.tkstrike.orm.model.MatchLogItemType;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchConfigurationDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchResultDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchVictoryCriteria;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeEventDto;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.model.ExternalConfigEntry;
import com.xtremis.daedo.tkstrike.ui.model.IMatchConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.INetworkConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.IRulesEntry;
import com.xtremis.daedo.tkstrike.ui.model.ISoundConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.ScoreboardEditAction;
import com.xtremis.daedo.tkstrike.ui.service.GenerateMatchLogHistoricalService;
import com.xtremis.daedo.tkstrike.utils.CountdownMillisRefreshType;
import com.xtremis.daedo.tkstrike.utils.TkStrikeCountdownNoUI;
import com.xtremis.daedo.wtdata.model.constants.ResultStatus;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.text.Text;


public abstract class BaseCommonMatchWorker<GNSC extends CommonGlobalNetworkStatusController, MLS extends CommonMatchLogService<MLD, MLID>, MLHS extends CommonMatchLogHistoricalService, SPS extends SoundPlayerService, MLD extends CommonMatchLogDto, MLID extends CommonMatchLogItemDto, MCS extends CommonMatchConfigurationService<E, ME>, E extends MatchConfigurationEntity, ME extends IMatchConfigurationEntry>
		implements GlobalNetworkStatusControllerListener, TkStrikeCommunicationListener, InitializingBean, CommonMatchWorker {

	static final Logger matchWorkerLogger = Logger.getLogger("MATCH_WORKER");

	private final SimpleBooleanProperty lock = new SimpleBooleanProperty(this, "lock", Boolean.FALSE.booleanValue());

	final SimpleDateFormat sdfRoundTime = new SimpleDateFormat("mm:ss.SSS");

	final SimpleDateFormat sdfRoundTimeNoMillis = new SimpleDateFormat("mm:ss");

	static final DecimalFormat dfMinutes = new DecimalFormat("00");

	final SimpleDateFormat sdfSystemTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");

	SimpleBooleanProperty allowNetworkError = new SimpleBooleanProperty(this, "allowNetworkError", Boolean.FALSE.booleanValue());

	@Value("${match.minHeadLevelValidHit}")
	Integer defaultMinHeadLevelValidHit;

	@Value("${match.minBodyLevelValidHit}")
	Integer defaultMinBodyLevelValidHit;

	@Value("${tkStrike.paraTimeOut.minutes:0}")
	Integer paraTimeOutMinutes;

	@Value("${tkStrike.paraTimeOut.seconds:30}")
	Integer paraTimeOutSeconds;

	@Value("${tkStrikeEventsListeners.matchLogItemTypes.allowed}")
	private String[] tkStrikeEventsListenersMatchLogItemTypesAllowed;

	@Value("${tkStrikeRtBroadcast.matchLogItemTypes.allowed}")
	private String[] tkStrikeRtBroadcastMatchLogItemTypesAllowed;

	@Value("${tkStrike.goldenPointMatchFinish.sendTotalPoints}")
	private Boolean goldenPointMatchFinishSendTotalPoints;

	@Value("${tkStrike.subtractGoldenPointPunchPointsIfLooserFromTotal}")
	private Boolean subtractGoldenPointPunchPointsIfLooserFromTotal;

	@Value("${tkStrike.showHitsNotValidOnLeft}")
	private Boolean showHitsNotValidOnLeft;

	@Value("${tkStrike.showGoldenPointTieBreakerAuto}")
	private Boolean showGoldenPointTieBreakerAuto;

	@Value("${tkStrike.isTkStrikeKTAVersion}")
	private Boolean isTkStrikeKTAVersion;

	@Value("${tkStrike.pointGapWithGamJeomNeedsConfirmation}")
	private Boolean pointGapWithGamJeomNeedsConfirmation;

	String matchLogId = null;

	Boolean matchStarted = Boolean.valueOf(false);

	private SimpleBooleanProperty scoreboardEditorOpen = new SimpleBooleanProperty(this, "scoreboardEditorOpen", false);

	private SimpleBooleanProperty finalResultOpen = new SimpleBooleanProperty(this, "finalResultOpen", false);

	private SimpleBooleanProperty roundFinishedOpen = new SimpleBooleanProperty(this, "roundFinishedOpen", false);

	SimpleObjectProperty<MatchStatusId> currentMatchStatus = new SimpleObjectProperty(this, "currentMatchStatus", MatchStatusId.NOT_READY);

	SimpleObjectProperty<MatchStatusId> prevMatchStatus = new SimpleObjectProperty(this, "currentMatchStatus", MatchStatusId.NOT_READY);

	private boolean changeMatchStatusToTimeoutOnScoreboardChanges = true;

	private MatchStatusId matchStatusBeforeKyeShi = MatchStatusId.WAITING_4_MATCH;

	private NetworkErrorCause networkErrorCause = new NetworkErrorCause(NetworkErrorCause.NetworkErrorCauseType.LOST_CONNECTION, false, false, false);

	private MatchStatusId statusBeforeNetworkError = null;

	private SimpleBooleanProperty doctorInRound = new SimpleBooleanProperty(this, "doctorInRound", false);

	String currentMatchId = null;

	String currentMatchNumber = null;

	String currentMatchVMInternalId = null;

	String currentMatchVMRingNumber = null;

	String currentMatchBlueAthleteOvrInternalId = null;

	String currentMatchRedAthleteOvrInternalId = null;

	private SimpleIntegerProperty matchRounds = new SimpleIntegerProperty(this, "matchRounds", 0);

	SimpleIntegerProperty currentRound = new SimpleIntegerProperty(this, "currentRound", 0);

	SimpleStringProperty currentRoundStr = new SimpleStringProperty(this, "currentRoundStr", "1");

	SimpleObjectProperty<FinalDecision> matchFinalDecission = new SimpleObjectProperty(this, "matchFinalDecission", FinalDecision.RSC);

	SimpleObjectProperty<MatchWinner> matchWinner = new SimpleObjectProperty(this, "matchWinner", MatchWinner.TIE);

	SimpleBooleanProperty matchWinnerChanges = new SimpleBooleanProperty(this, "matchWinnerChanges");

	SimpleBooleanProperty matchWinnerByPointGapNeedsConfirmation = new SimpleBooleanProperty(this, "matchWinnerByPointGapNeedsConfirmation", false);

	MatchVictoryCriteria matchVictoryCriteria = MatchVictoryCriteria.CONVENTIONAL;

	private final SimpleBooleanProperty bestOf3RoundWithPointGap = new SimpleBooleanProperty(this, "bestOf3RoundWithPointGap", false);

	private MatchWinner bestOf3RoundWinnerWithPointGap = null;

	private Boolean bestOf3UpdatingFromScoreboardEditor = Boolean.valueOf(false);

	private final SimpleBooleanProperty bestOf3RoundWithSuperiority = new SimpleBooleanProperty(this, "bestOf3RoundWithSuperiority", false);

	private final SimpleBooleanProperty bestOf3RoundSuperiorityOnScoreboard = new SimpleBooleanProperty(this, "bestOf3RoundSuperiorityOnScoreboard",
			false);

	private BestOf3RoundSuperiority bestOf3RoundSuperiority = null;

	private final SimpleBooleanProperty showSuperiorityByRounds = new SimpleBooleanProperty(this, "showSuperiorityByRounds", false);

	private final SimpleBooleanProperty superiorityByRounds = new SimpleBooleanProperty(this, "superiorityByRounds", false);

	private MatchWinner bestOf3WinnerLastRoundWithSuperiority = MatchWinner.TIE;

	private boolean bestOf3WinnerLastRoundByPUN = false;

	private MatchWinner bestOf3WinnerLastRoundByPUNWinner = MatchWinner.TIE;

	SimpleIntegerProperty blueLastImpactValue = new SimpleIntegerProperty(this, "blueLastImpactValue", 0);

	SimpleIntegerProperty bluePoints = new SimpleIntegerProperty(this, "bluePoints", 0);

	SimpleIntegerProperty blueTotalPoints = new SimpleIntegerProperty(this, "blueTotalPoints", 0);

	SimpleIntegerProperty blueGeneralPoints = new SimpleIntegerProperty(this, "blueGeneralPoints", 0);

	SimpleIntegerProperty bluePenalties = new SimpleIntegerProperty(this, "bluePenalties", 0);

	SimpleIntegerProperty blueTotalPenalties = new SimpleIntegerProperty(this, "blueTotalPenalties", 0);

	Integer bluePenaltiesNextRound = Integer.valueOf(0);

	SimpleIntegerProperty blueTechPoints = new SimpleIntegerProperty(this, "blueTechPoints", 0);

	SimpleIntegerProperty bluePARATechPoints = new SimpleIntegerProperty(this, "bluePARATechPoints", 0);

	SimpleIntegerProperty blueRoundWins = new SimpleIntegerProperty(this, "blueRoundWins", 0);

	SimpleIntegerProperty blueR1Points = new SimpleIntegerProperty(this, "blueR1Points", 0);

	SimpleIntegerProperty blueR2Points = new SimpleIntegerProperty(this, "blueR2Points", 0);

	SimpleIntegerProperty blueR3Points = new SimpleIntegerProperty(this, "blueR3Points", 0);

	SimpleIntegerProperty bluePARATotalTechPoints = new SimpleIntegerProperty(this, "bluePARATotalTechPoints", 0);

	SimpleIntegerProperty blueGoldenPointPenalties = new SimpleIntegerProperty(this, "blueGoldenPointPenalties", 0);

	SimpleIntegerProperty blueGoldenPointImpacts = new SimpleIntegerProperty(this, "blueGoldenPointImpacts", 0);

	Integer blueLastGoldenPointPoints = Integer.valueOf(0);

	SimpleIntegerProperty blueGoldenPointPunches = new SimpleIntegerProperty(this, "blueGoldenPointPunches", 0);

	boolean blueLastPointIsPenalty = false;

	SimpleBooleanProperty bluePARATimeOutQuota = new SimpleBooleanProperty(this, "bluePARARestQuota", false);

	SimpleIntegerProperty bluePARATimeOutQuotaValue = new SimpleIntegerProperty(this, "bluePARARestQuotaValue", 0);

	private final SimpleObjectProperty<HitEventValidator> blueLastBodyHitValidator = new SimpleObjectProperty(this, "blueLastBodyHitValidator",
			new HitEventValidator(true, - 1L, HitEventType.BODY, false));

	private final SimpleObjectProperty<HitEventValidator> blueLastHeadHitValidator = new SimpleObjectProperty(this, "blueLastHeadHitValidator",
			new HitEventValidator(true, - 1L, HitEventType.HEAD, false));

	private final SimpleObjectProperty<HitEventValidator> blueLastSpecialHeadHitValidator = new SimpleObjectProperty(this,
			"blueLastSpecialHeadHitValidator", new HitEventValidator(true, - 1L, HitEventType.SPECIAL_HEAD, false));

	private final SimpleObjectProperty<HitEventValidator> blueLastSpecialBodyHitValidator = new SimpleObjectProperty(this,
			"blueLastSpecialBodyHitValidator", new HitEventValidator(true, - 1L, HitEventType.SPECIAL_BODY, false));

	private final SimpleObjectProperty<HitEventValidator> blueLastPunchHitValidator = new SimpleObjectProperty(this, "blueLastPunchHitValidator",
			new HitEventValidator(true, - 1L, HitEventType.PUNCH, false));

	SimpleIntegerProperty redLastImpactValue = new SimpleIntegerProperty(this, "blueLastImpactValue", 0);

	SimpleIntegerProperty redPoints = new SimpleIntegerProperty(this, "redPoints", 0);

	SimpleIntegerProperty redTotalPoints = new SimpleIntegerProperty(this, "redTotalPoints", 0);

	SimpleIntegerProperty redGeneralPoints = new SimpleIntegerProperty(this, "redGeneralPoints", 0);

	SimpleIntegerProperty redPenalties = new SimpleIntegerProperty(this, "redPenalties", 0);

	SimpleIntegerProperty redTotalPenalties = new SimpleIntegerProperty(this, "redTotalPenalties", 0);

	Integer redPenaltiesNextRound = Integer.valueOf(0);

	SimpleIntegerProperty redTechPoints = new SimpleIntegerProperty(this, "redTechPoints", 0);

	SimpleIntegerProperty redPARATechPoints = new SimpleIntegerProperty(this, "redPARATechPoints", 0);

	SimpleIntegerProperty redRoundWins = new SimpleIntegerProperty(this, "redRoundWins", 0);

	SimpleIntegerProperty redR1Points = new SimpleIntegerProperty(this, "redR1Points", 0);

	SimpleIntegerProperty redR2Points = new SimpleIntegerProperty(this, "redR2Points", 0);

	SimpleIntegerProperty redR3Points = new SimpleIntegerProperty(this, "redR3Points", 0);

	SimpleIntegerProperty redPARATotalTechPoints = new SimpleIntegerProperty(this, "redPARATotalTechPoints", 0);

	SimpleIntegerProperty redGoldenPointPenalties = new SimpleIntegerProperty(this, "redGoldenPointPenalties", 0);

	SimpleIntegerProperty redGoldenPointImpacts = new SimpleIntegerProperty(this, "redGoldenPointImpacts", 0);

	Integer redLastGoldenPointPoints = Integer.valueOf(0);

	SimpleIntegerProperty redGoldenPointPunches = new SimpleIntegerProperty(this, "redGoldenPointPunches", 0);

	boolean redLastPointIsPenalty = false;

	SimpleBooleanProperty redPARATimeOutQuota = new SimpleBooleanProperty(this, "redPARARestQuota", false);

	SimpleIntegerProperty redPARATimeOutQuotaValue = new SimpleIntegerProperty(this, "redPARARestQuotaValue", 0);

	private final SimpleObjectProperty<HitEventValidator> redLastBodyHitValidator = new SimpleObjectProperty(this, "blueLastBodyHitValidator",
			new HitEventValidator(false, - 1L, HitEventType.BODY, false));

	private final SimpleObjectProperty<HitEventValidator> redLastHeadHitValidator = new SimpleObjectProperty(this, "redLastHeadHitValidator",
			new HitEventValidator(false, - 1L, HitEventType.HEAD, false));

	private final SimpleObjectProperty<HitEventValidator> redLastSpecialHeadHitValidator = new SimpleObjectProperty(this,
			"redLastSpecialHeadHitValidator", new HitEventValidator(false, - 1L, HitEventType.SPECIAL_HEAD, false));

	private final SimpleObjectProperty<HitEventValidator> redLastSpecialBodyHitValidator = new SimpleObjectProperty(this,
			"redLastSpecialBodyHitValidator", new HitEventValidator(true, - 1L, HitEventType.SPECIAL_BODY, false));

	private final SimpleObjectProperty<HitEventValidator> redLastPunchHitValidator = new SimpleObjectProperty(this, "redLastPunchHitValidator",
			new HitEventValidator(false, - 1L, HitEventType.PUNCH, false));

	private CopyOnWriteArrayList<CommonMatchWorker.HitEventValidatorListener> hitEventValidatorListeners = new CopyOnWriteArrayList<>();

	ConcurrentHashMap<Integer, MatchWinner> roundsWinner = new ConcurrentHashMap<>(4);

	SimpleObjectProperty<MatchWinner> bestOf3CurrentRoundPartialWinner = new SimpleObjectProperty(this, "bestOf3CurrentRoundPartialWinner",
			MatchWinner.TIE);

	ConcurrentHashMap<Integer, Integer> blueRoundsPoints = new ConcurrentHashMap<>(4);

	ConcurrentHashMap<Integer, Integer> blueRoundsPenalties = new ConcurrentHashMap<>(4);

	ConcurrentHashMap<Integer, Integer> blueRoundsTechPoints = new ConcurrentHashMap<>(4);

	ConcurrentHashMap<Integer, Integer> blueRoundsHeadPoints = new ConcurrentHashMap<>(4);

	ConcurrentHashMap<Integer, Integer> blueRoundsBodyPoints = new ConcurrentHashMap<>(4);

	ConcurrentHashMap<Integer, Integer> blueRoundsPunchPoints = new ConcurrentHashMap<>(4);

	ConcurrentHashMap<Integer, Integer> blueRoundsHits = new ConcurrentHashMap<>(4);

	SimpleIntegerProperty blueRoundsHitsProperty = new SimpleIntegerProperty(this, "blueRoundsHitsProperty", 0);

	ConcurrentHashMap<Integer, Integer> redRoundsPoints = new ConcurrentHashMap<>(4);

	ConcurrentHashMap<Integer, Integer> redRoundsPenalties = new ConcurrentHashMap<>(4);

	ConcurrentHashMap<Integer, Integer> redRoundsTechPoints = new ConcurrentHashMap<>(4);

	ConcurrentHashMap<Integer, Integer> redRoundsHeadPoints = new ConcurrentHashMap<>(4);

	ConcurrentHashMap<Integer, Integer> redRoundsBodyPoints = new ConcurrentHashMap<>(4);

	ConcurrentHashMap<Integer, Integer> redRoundsPunchPoints = new ConcurrentHashMap<>(4);

	ConcurrentHashMap<Integer, Integer> redRoundsHits = new ConcurrentHashMap<>(4);

	SimpleIntegerProperty redRoundsHitsProperty = new SimpleIntegerProperty(this, "redRoundsHitsProperty", 0);

	SimpleBooleanProperty roundsWinnerChanges = new SimpleBooleanProperty(this, "roundsWinnerChanges");

	private SimpleBooleanProperty backupSystemEnabled = new SimpleBooleanProperty(this, "backupSystemEnabled", Boolean.FALSE.booleanValue());

	private int nodesInNetwork = 0;

	private boolean judgeLetsTechHeadPoints = false;

	private boolean judgeLetsBodyPoints = false;

	private int numberOfJudges = 0;

	private JudgeNode judge1Node = null;

	private JudgeNode judge2Node = null;

	private JudgeNode judge3Node = null;

	private int bodyPoints = 0;

	private int bodyTechPoints = 0;

	private int headPoints = 0;

	private int headTechPoints = 0;

	private int punchPoints = 0;

	private int paraSpinningKickPoints = 0;

	private int paraTurningKickPoints = 0;

	private int differencialScore = 0;

	private boolean pointGapAllRounds = false;

	private int cellingScore = 0;

	private int paraCellingScore = 0;

	private int nearMissLevel = 0;

	private boolean bonusPointsEnabled = false;

	private int bonusPointsMinLevel = 0;

	private int bonusPointsPoints2Add = 0;

	Integer overtimePoints = Integer.valueOf(1);

	Boolean gamJeomShowPointsOnGoldenPoint = Boolean.FALSE;

	private SimpleBooleanProperty networkOkByGlobalController = new SimpleBooleanProperty(this, "networkOkByGlobalController", Boolean.FALSE
			.booleanValue());

	private int playSoundBeforeStartRound = 0;

	private String playSoundBeforeStartRoundString = null;

	protected int roundTimeMinutes = 2;

	protected int roundTimeSeconds = 0;

	@Value("${tkStrike.countdown.milliseconds.refreshType}")
	private CountdownMillisRefreshType countdownMillisRefreshType;

	@Value("${tkStrike.scoreboard.graphicDetailType}")
	private TkStrikeScoreboardGraphicDetailType scoreboardGraphicDetailType;

	TkStrikeCountdownNoUI roundCountdown;

	private int kyeShiTimeMinutes = 1;

	private int kyeShiTimeSeconds = 0;

	private TkStrikeCountdownNoUI kyeShiCountdown;

	private int restTimeMinutes = 1;

	private int restTimeSeconds = 0;

	private TkStrikeCountdownNoUI restTimeCountdown;

	private int goldenPointTimeMinutes = 2;

	private int goldenPointTimeSeconds = 0;

	boolean goldenPointEnabled = false;

	boolean goldenPointWorking = false;

	boolean goldenPointTieBreaker = false;

	GoldenPointTieBreakerInfoDto goldenPointTieBreakerInfo = null;

	boolean goldenPointPointByPenalty = false;

	boolean goldenPointPointByPunch = false;

	int goldenPointPointByPenaltyValue = 0;

	private SimpleBooleanProperty showGoldenPointTieBreakerOnScoreboard = new SimpleBooleanProperty(this, "showGoldenPointTieBreakerOnScoreboard",
			false);

	TkStrikeCountdownNoUI paraTimeOutCountdown;

	int athleteWithPARATimeOutCountdownWorking = 0;

	final TkStrikeCommunicationService tkStrikeCommunicationService;

	final AppStatusWorker appStatusWorker;

	final RulesService rulesService;

	final SoundConfigurationService soundConfigurationService;

	final ExternalConfigService externalConfigService;

	final TkStrikeEventsListenerClient tkStrikeEventsListenerClient;

	final TkStrikeUDPFacadeEventsListenerClient tkStrikeUDPFacadeEventsListenerClient;

	final RtBroadcastSocketClient rtBroadcastSocketClient;

	@Autowired
	WtOvrClientService wtOvrClientService;

	@Autowired
	WtUDPService wtUDPService;

	@Autowired
	public BaseCommonMatchWorker(TkStrikeCommunicationService tkStrikeCommunicationService, AppStatusWorker appStatusWorker,
			RulesService rulesService, SoundConfigurationService soundConfigurationService, ExternalConfigService externalConfigService,
			TkStrikeEventsListenerClient tkStrikeEventsListenerClient, TkStrikeUDPFacadeEventsListenerClient tkStrikeUDPFacadeEventsListenerClient,
			RtBroadcastSocketClient rtBroadcastSocketClient) {
		this.tkStrikeCommunicationService = tkStrikeCommunicationService;
		this.appStatusWorker = appStatusWorker;
		this.rulesService = rulesService;
		this.soundConfigurationService = soundConfigurationService;
		this.externalConfigService = externalConfigService;
		this.tkStrikeEventsListenerClient = tkStrikeEventsListenerClient;
		this.tkStrikeUDPFacadeEventsListenerClient = tkStrikeUDPFacadeEventsListenerClient;
		this.rtBroadcastSocketClient = rtBroadcastSocketClient;
	}

	@Override
	public SimpleBooleanProperty allowNetworkErrorProperty() {
		return this.allowNetworkError;
	}

	@Override
	public Boolean isLock() {
		return Boolean.valueOf(this.lock.get());
	}

	@Override
	public final void hasNewNodeNetworkErrorEvent(GlobalNetworkStatusControllerListener.NodeNetworkErrorEvent nodeNetworkErrorEvent) {
		if( ! this.backupSystemEnabled.get() &&
				! this.allowNetworkError.get()) {
			this.networkOkByGlobalController.set(false);
			if( ! MatchStatusId.ROUND_FINISHED.equals(getCurrentMatchStatus()) &&
					! MatchStatusId.ROUND_GOLDENPOINT_FINISHED.equals(getCurrentMatchStatus()) &&
					! MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION.equals(getCurrentMatchStatus()) &&
					! MatchStatusId.MATCH_FINISHED.equals(getCurrentMatchStatus()) &&
					reviseEvents())
				if(nodeNetworkErrorEvent.getNetworkNode() instanceof JudgeNode) {
					if(matchWorkerLogger.isDebugEnabled())
						matchWorkerLogger.debug("Node Judge " + nodeNetworkErrorEvent.getNetworkNode().getNodeId()
								+ " network error but not throw error. " + "(LastTime =" + nodeNetworkErrorEvent

										.getNetworkNode().getLastTimestampStatusOk() + " CurrentTime =" + nodeNetworkErrorEvent
												.getEventTimestamp() + " Diff = " + "" + (nodeNetworkErrorEvent
														.getEventTimestamp().longValue() - nodeNetworkErrorEvent.getNetworkNode()
																.getLastTimestampStatusOk()));
				} else {
					matchWorkerLogger.debug("hasNewNodeNetworkErrorEvent " + nodeNetworkErrorEvent.getNetworkErrorCause().toString());
					if(getCurrentMatchStatus().equals(MatchStatusId.ROUND_FINISHED)) {
						if(matchWorkerLogger.isDebugEnabled())
							matchWorkerLogger.debug("Network error... but... REST Time");
					} else {
						boolean isBody = (isBlueBodyNodeId(nodeNetworkErrorEvent.getNetworkNode().getNodeId()) || isRedBodyNodeId(
								nodeNetworkErrorEvent.getNetworkNode().getNodeId()));
						boolean isHead = (isBlueHeadNodeId(nodeNetworkErrorEvent.getNetworkNode().getNodeId()) || isRedHeadNodeId(
								nodeNetworkErrorEvent.getNetworkNode().getNodeId()));
						boolean isBlue = (isBlueHeadNodeId(nodeNetworkErrorEvent.getNetworkNode().getNodeId()) || isBlueBodyNodeId(
								nodeNetworkErrorEvent.getNetworkNode().getNodeId()));
						this.networkErrorCause = new NetworkErrorCause(nodeNetworkErrorEvent.getNetworkErrorCause(), isBody, isHead, isBlue);
						if( ! MatchStatusId.NETWORK_ERROR.equals(getCurrentMatchStatus()))
							this.statusBeforeNetworkError = getCurrentMatchStatus();
						this.currentMatchStatus.set(MatchStatusId.NETWORK_ERROR);
						_addMatchLogItem(Long.valueOf(System.currentTimeMillis()),
								Long.valueOf(getCurrentRoundCountdownAsMillis()),
								Long.valueOf(System.currentTimeMillis()),
								getRound4MatchLog(true),
								getRoundStr4MatchLog(true), MatchLogItemType.NETWORK_ERROR,

								Integer.valueOf(0),
								Integer.valueOf(0), "", true,
								Integer.valueOf(0), false);
						matchWorkerLogger.debug("BEFORE CALL DoPauseRound " + this.matchLogId);
						doPauseRound();
						this.appStatusWorker.addAppStatusOk(AppStatusId.NETWORK_ERROR);
					}
				}
		}
	}

	@Override
	public final void hasNetworkOkEvent(GlobalNetworkStatusControllerListener.NetworkOkEvent networkOkEvent) {
		this.networkOkByGlobalController.set(true);
		if( ! MatchStatusId.ROUND_FINISHED.equals(getCurrentMatchStatus()) &&
				! MatchStatusId.ROUND_GOLDENPOINT_FINISHED.equals(getCurrentMatchStatus()) &&
				! MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION.equals(getCurrentMatchStatus()) &&
				! MatchStatusId.MATCH_FINISHED.equals(getCurrentMatchStatus()))
			if(this.currentMatchStatus.get().equals(MatchStatusId.NETWORK_ERROR) || this.currentMatchStatus
					.get().equals(MatchStatusId.NOT_READY)) {
				if(this.statusBeforeNetworkError != null) {
					if(matchWorkerLogger.isDebugEnabled())
						matchWorkerLogger.debug("Prev status ->" + this.statusBeforeNetworkError);
					if(this.statusBeforeNetworkError.equals(MatchStatusId.ROUND_WORKING) || this.statusBeforeNetworkError
							.equals(MatchStatusId.ROUND_IN_GOLDENPOINT) || this.statusBeforeNetworkError
									.equals(MatchStatusId.ROUND_KYESHI)) {
						this.currentMatchStatus.set(MatchStatusId.ROUND_PAUSED);
					} else if(this.statusBeforeNetworkError.equals(MatchStatusId.ROUND_FINISHED)) {
						this.currentMatchStatus.set(this.statusBeforeNetworkError);
						this.restTimeCountdown.play();
					} else {
						this.currentMatchStatus.set(this.statusBeforeNetworkError);
					}
				}
				this.appStatusWorker.addAppStatusOk(AppStatusId.NETWORK_RECOVERED);
			}
	}

	@Override
	public final void hasNewDataEvent(final DataEvent dataEvent) {
		TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				if( ! BaseCommonMatchWorker.this.backupSystemEnabled.get() && BaseCommonMatchWorker.this.isBlueBodyNodeId(dataEvent.getNodeId())) {
					BaseCommonMatchWorker.this.workWithAthleteSensorHit(dataEvent, false, HitEventType.BODY,
							BaseCommonMatchWorker.this.redGoldenPointImpacts, BaseCommonMatchWorker.this.redLastBodyHitValidator,
							BaseCommonMatchWorker.this.redLastImpactValue);
				} else if( ! BaseCommonMatchWorker.this.backupSystemEnabled.get() && BaseCommonMatchWorker.this.isRedBodyNodeId(dataEvent
						.getNodeId())) {
					BaseCommonMatchWorker.this.workWithAthleteSensorHit(dataEvent, true, HitEventType.BODY,
							BaseCommonMatchWorker.this.blueGoldenPointImpacts, BaseCommonMatchWorker.this.blueLastBodyHitValidator,
							BaseCommonMatchWorker.this.blueLastImpactValue);
				} else if( ! BaseCommonMatchWorker.this.backupSystemEnabled.get() && BaseCommonMatchWorker.this.isBlueHeadNodeId(dataEvent
						.getNodeId())) {
					BaseCommonMatchWorker.this.workWithAthleteSensorHit(dataEvent, false, HitEventType.HEAD,
							BaseCommonMatchWorker.this.redGoldenPointImpacts, BaseCommonMatchWorker.this.redLastHeadHitValidator,
							BaseCommonMatchWorker.this.redLastImpactValue);
				} else if( ! BaseCommonMatchWorker.this.backupSystemEnabled.get() && BaseCommonMatchWorker.this.isRedHeadNodeId(dataEvent
						.getNodeId())) {
					BaseCommonMatchWorker.this.workWithAthleteSensorHit(dataEvent, true, HitEventType.HEAD,
							BaseCommonMatchWorker.this.blueGoldenPointImpacts, BaseCommonMatchWorker.this.blueLastHeadHitValidator,
							BaseCommonMatchWorker.this.blueLastImpactValue);
				} else if(43521 == dataEvent.getHitValue().intValue()) {
					if(BaseCommonMatchWorker.this.judge1Node != null && dataEvent.getNodeId().equals(BaseCommonMatchWorker.this.judge1Node
							.getNodeId())) {
						BaseCommonMatchWorker.this.workWithJudgeHeadTech(dataEvent, 1, true);
					} else if(BaseCommonMatchWorker.this.judge2Node != null && dataEvent.getNodeId().equals(BaseCommonMatchWorker.this.judge2Node
							.getNodeId())) {
						BaseCommonMatchWorker.this.workWithJudgeHeadTech(dataEvent, 2, true);
					} else if(BaseCommonMatchWorker.this.judge3Node != null && dataEvent.getNodeId().equals(BaseCommonMatchWorker.this.judge3Node
							.getNodeId())) {
						BaseCommonMatchWorker.this.workWithJudgeHeadTech(dataEvent, 3, true);
					}
				} else if(43524 == dataEvent.getHitValue().intValue()) {
					if(BaseCommonMatchWorker.this.judge1Node != null && dataEvent.getNodeId().equals(BaseCommonMatchWorker.this.judge1Node
							.getNodeId())) {
						BaseCommonMatchWorker.this.workWithJudgeHeadTech(dataEvent, 1, false);
					} else if(BaseCommonMatchWorker.this.judge2Node != null && dataEvent.getNodeId().equals(BaseCommonMatchWorker.this.judge2Node
							.getNodeId())) {
						BaseCommonMatchWorker.this.workWithJudgeHeadTech(dataEvent, 2, false);
					} else if(BaseCommonMatchWorker.this.judge3Node != null && dataEvent.getNodeId().equals(BaseCommonMatchWorker.this.judge3Node
							.getNodeId())) {
						BaseCommonMatchWorker.this.workWithJudgeHeadTech(dataEvent, 3, false);
					}
				} else if(43522 == dataEvent.getHitValue().intValue()) {
					if(BaseCommonMatchWorker.this.judge1Node != null && dataEvent.getNodeId().equals(BaseCommonMatchWorker.this.judge1Node
							.getNodeId())) {
						BaseCommonMatchWorker.this.workWithJudgeBodyTech(dataEvent, 1, true);
					} else if(BaseCommonMatchWorker.this.judge2Node != null && dataEvent.getNodeId().equals(BaseCommonMatchWorker.this.judge2Node
							.getNodeId())) {
						BaseCommonMatchWorker.this.workWithJudgeBodyTech(dataEvent, 2, true);
					} else if(BaseCommonMatchWorker.this.judge3Node != null && dataEvent.getNodeId().equals(BaseCommonMatchWorker.this.judge3Node
							.getNodeId())) {
						BaseCommonMatchWorker.this.workWithJudgeBodyTech(dataEvent, 3, true);
					}
				} else if(43528 == dataEvent.getHitValue().intValue()) {
					if(BaseCommonMatchWorker.this.judge1Node != null && dataEvent.getNodeId().equals(BaseCommonMatchWorker.this.judge1Node
							.getNodeId())) {
						BaseCommonMatchWorker.this.workWithJudgeBodyTech(dataEvent, 1, false);
					} else if(BaseCommonMatchWorker.this.judge2Node != null && dataEvent.getNodeId().equals(BaseCommonMatchWorker.this.judge2Node
							.getNodeId())) {
						BaseCommonMatchWorker.this.workWithJudgeBodyTech(dataEvent, 2, false);
					} else if(BaseCommonMatchWorker.this.judge3Node != null && dataEvent.getNodeId().equals(BaseCommonMatchWorker.this.judge3Node
							.getNodeId())) {
						BaseCommonMatchWorker.this.workWithJudgeBodyTech(dataEvent, 3, false);
					}
				} else if(43552 == dataEvent.getHitValue().intValue()) {
					if(BaseCommonMatchWorker.this.judge1Node != null && dataEvent.getNodeId().equals(BaseCommonMatchWorker.this.judge1Node
							.getNodeId())) {
						BaseCommonMatchWorker.this.workWithJudgePunch(dataEvent, 1, true);
					} else if(BaseCommonMatchWorker.this.judge2Node != null && dataEvent.getNodeId().equals(BaseCommonMatchWorker.this.judge2Node
							.getNodeId())) {
						BaseCommonMatchWorker.this.workWithJudgePunch(dataEvent, 2, true);
					} else if(BaseCommonMatchWorker.this.judge3Node != null && dataEvent.getNodeId().equals(BaseCommonMatchWorker.this.judge3Node
							.getNodeId())) {
						BaseCommonMatchWorker.this.workWithJudgePunch(dataEvent, 3, true);
					}
				} else if(43536 == dataEvent.getHitValue().intValue()) {
					if(BaseCommonMatchWorker.this.judge1Node != null && dataEvent.getNodeId().equals(BaseCommonMatchWorker.this.judge1Node
							.getNodeId())) {
						BaseCommonMatchWorker.this.workWithJudgePunch(dataEvent, 1, false);
					} else if(BaseCommonMatchWorker.this.judge2Node != null && dataEvent.getNodeId().equals(BaseCommonMatchWorker.this.judge2Node
							.getNodeId())) {
						BaseCommonMatchWorker.this.workWithJudgePunch(dataEvent, 2, false);
					} else if(BaseCommonMatchWorker.this.judge3Node != null && dataEvent.getNodeId().equals(BaseCommonMatchWorker.this.judge3Node
							.getNodeId())) {
						BaseCommonMatchWorker.this.workWithJudgePunch(dataEvent, 3, false);
					}
				}
				return null;
			}
		});
	}

	@Override
	public final void addListener(CommonMatchWorker.HitEventValidatorListener hitEventValidatorListener) {
		if( ! this.hitEventValidatorListeners.contains(hitEventValidatorListener))
			this.hitEventValidatorListeners.add(hitEventValidatorListener);
	}

	@Override
	public final String getMatchLogId() {
		return this.matchLogId;
	}

	private void restartMatchByMatchLog(String newMatchLogId) {
		if(this.matchLogId != null)
			try {
				getMatchLogService().delete(this.matchLogId);
			} catch(TkStrikeServiceException e) {
				e.printStackTrace();
			}
		MLD matchLog = null;
		try {
			matchLog = getMatchLogService().getById(newMatchLogId);
		} catch(TkStrikeServiceException e) {
			e.printStackTrace();
		}
		if(matchLog != null) {
			this.matchLogId = matchLog.getId();
			try {
				List<MLID> matchLogItems = getMatchLogService().findByMatchLogId(this.matchLogId);
				if(matchLogItems != null && matchLogItems.size() > 0) {
					matchLogItems.sort(getMatchLogService().getComparator4Items());
					CommonMatchLogItemDto commonMatchLogItemDto = matchLogItems.get(matchLogItems.size() - 1);
					try {
						matchWorkerLogger.info(" LAST ITEM ->" + commonMatchLogItemDto.toJSON().toString());
					} catch(JSONException e) {
						e.printStackTrace();
					}
					this.currentMatchStatus.set(MatchStatusId.ROUND_PAUSED);
					Calendar roundTime = Calendar.getInstance();
					roundTime.setTimeInMillis(commonMatchLogItemDto.getRoundTime().longValue());
					_changeRoundAndTime(matchLog, (MLID)commonMatchLogItemDto);
					this.roundsWinner.clear();
					this.blueRoundWins.set(0);
					this.redRoundWins.set(0);
					String strRoundsWinner = matchLog.getRoundsWinners();
					if(strRoundsWinner != null) {
						String[] strRoundWinner = strRoundsWinner.split(";");
						for(int i = 0; i < strRoundWinner.length; i++) {
							String roundWinner = strRoundWinner[i];
							String[] info = roundWinner.split(":");
							if(info.length == 2 && StringUtils.isNumeric(info[0]))
								try {
									this.roundsWinner.put(Integer.valueOf(Integer.parseInt(info[0])), MatchWinner.valueOf(info[1]));
								} catch(Exception exception) {}
						}
					}
					this.roundsWinnerChanges.set(true);
					this.roundsWinnerChanges.set(false);
					this.matchStarted = Boolean.valueOf(true);
					this.bluePoints.set(commonMatchLogItemDto.getBluePoints().intValue());
					this.redPoints.set(commonMatchLogItemDto.getRedPoints().intValue());
					this.bluePenalties.setValue(null);
					this.bluePenalties.set(commonMatchLogItemDto.getBluePenalties().intValue());
					this.redPenalties.setValue(null);
					this.redPenalties.set(commonMatchLogItemDto.getRedPenalties().intValue());
					if(MatchVictoryCriteria.BESTOF3.equals(this.matchVictoryCriteria)) {
						this.blueTotalPenalties.setValue(Integer.valueOf( - 1));
						this.blueTotalPenalties.set(commonMatchLogItemDto.getBlueTotalPenalties().intValue());
						this.redTotalPenalties.setValue(Integer.valueOf( - 1));
						this.redTotalPenalties.set(commonMatchLogItemDto.getRedTotalPenalties().intValue());
					} else {
						this.blueTotalPenalties.setValue(Integer.valueOf( - 1));
						this.blueTotalPenalties.set(commonMatchLogItemDto.getBluePenalties().intValue());
						this.redTotalPenalties.setValue(Integer.valueOf( - 1));
						this.redTotalPenalties.set(commonMatchLogItemDto.getRedPenalties().intValue());
					}
					if(commonMatchLogItemDto.getGoldenPointRound().booleanValue()) {
						this.goldenPointWorking = true;
						this.blueGoldenPointImpacts.set(commonMatchLogItemDto.getBlueGoldenPointHits().intValue());
						this.redGoldenPointImpacts.set(commonMatchLogItemDto.getRedGoldenPointHits().intValue());
						this.blueGoldenPointPenalties.set(commonMatchLogItemDto.getBlueGoldenPointPenalties().intValue());
						this.redGoldenPointPenalties.set(commonMatchLogItemDto.getRedGoldenPointPenalties().intValue());
					}
					if(commonMatchLogItemDto.getGoldenPointRound().booleanValue()) {
						this.currentMatchStatus.set(MatchStatusId.WAITING_4_START_GOLDENPOINT);
						this.currentMatchStatus.set(MatchStatusId.ROUND_PAUSED);
					}
					_restartMatchByMatchLog(matchLog, matchLogItems);
					Collections.reverse(matchLogItems);
					LinkedHashSet<HitEventValidator> blueEvents = new LinkedHashSet<>();
					LinkedHashSet<HitEventValidator> redEvents = new LinkedHashSet<>();
					int blueHits = 0;
					int redHits = 0;
					boolean hasBlueJudgeTech = false;
					boolean hasRedJudgeTech = false;
					for(CommonMatchLogItemDto commonMatchLogItemDto1 : matchLogItems) {
						if(this.currentRoundStr.get().equals(commonMatchLogItemDto1.getRoundNumberStr())) {
							if(MatchLogItemType.BLUE_BODY_HIT.equals(commonMatchLogItemDto1.getMatchLogItemType()) || MatchLogItemType.BLUE_HEAD_HIT
									.equals(commonMatchLogItemDto1.getMatchLogItemType())) {
								if(blueHits < 2) {
									int multi = MatchLogItemType.BLUE_HEAD_HIT.equals(commonMatchLogItemDto1.getMatchLogItemType()) ? - 1 : 1;
									this.blueLastImpactValue.setValue(Integer.valueOf(Integer.valueOf(commonMatchLogItemDto1.getEntryValue())
											.intValue() * multi));
									blueHits++;
								}
								if(blueEvents.size() > 0) {
									HitEventValidator hitEventValidator = getRelated(blueEvents,
											MatchLogItemType.BLUE_HEAD_HIT.equals(commonMatchLogItemDto1.getMatchLogItemType()) ? HitEventType.HEAD
													: HitEventType.BODY, true);
									if(hitEventValidator != null)
										hitEventValidator.setHitValue(Integer.valueOf(commonMatchLogItemDto1.getEntryValue()));
								}
								continue;
							}
							if(MatchLogItemType.RED_BODY_HIT.equals(commonMatchLogItemDto1.getMatchLogItemType()) || MatchLogItemType.RED_HEAD_HIT
									.equals(commonMatchLogItemDto1.getMatchLogItemType())) {
								if(redHits < 2) {
									int multi = MatchLogItemType.RED_HEAD_HIT.equals(commonMatchLogItemDto1.getMatchLogItemType()) ? - 1 : 1;
									this.redLastImpactValue.setValue(Integer.valueOf(Integer.valueOf(commonMatchLogItemDto1.getEntryValue())
											.intValue() * multi));
									redHits++;
								}
								if(redEvents.size() > 0) {
									HitEventValidator hitEventValidator = getRelated(redEvents,
											MatchLogItemType.RED_HEAD_HIT.equals(commonMatchLogItemDto1.getMatchLogItemType()) ? HitEventType.HEAD
													: HitEventType.BODY, false);
									if(hitEventValidator != null)
										hitEventValidator.setHitValue(Integer.valueOf(commonMatchLogItemDto1.getEntryValue()));
								}
								continue;
							}
							if(MatchLogItemType.BLUE_BODY_POINT.equals(commonMatchLogItemDto1.getMatchLogItemType())
									|| MatchLogItemType.RED_BODY_POINT
											.equals(commonMatchLogItemDto1.getMatchLogItemType())) {
								boolean blue = MatchLogItemType.BLUE_BODY_POINT.equals(commonMatchLogItemDto1.getMatchLogItemType());
								HitEventValidator hitEventValidator = blue ? getRelated(blueEvents, HitEventType.BODY, true)
										: getRelated(redEvents, HitEventType.BODY, false);
								if(hitEventValidator != null)
									if(Math.abs(hitEventValidator.getHitTimestamp() - commonMatchLogItemDto1.getEventTime().longValue()) <= 1000L) {
										hitEventValidator.setHitTimestamp(commonMatchLogItemDto1.getEventTime().longValue());
									} else {
										hitEventValidator = null;
									}
								if(hitEventValidator == null)
									hitEventValidator = new HitEventValidator(blue, commonMatchLogItemDto1.getEventTime().longValue(),
											HitEventType.BODY, Integer.valueOf(0), this.numberOfJudges, HitJudgeStatus.NOT_VALIDATED,
											HitJudgeStatus.NOT_VALIDATED, HitJudgeStatus.NOT_VALIDATED, this.backupSystemEnabled.get());
								if(blue) {
									blueEvents.add(hitEventValidator);
									hasBlueJudgeTech = false;
									continue;
								}
								redEvents.add(hitEventValidator);
								hasRedJudgeTech = false;
								continue;
							}
							if(MatchLogItemType.BLUE_HEAD_POINT.equals(commonMatchLogItemDto1.getMatchLogItemType())
									|| MatchLogItemType.RED_HEAD_POINT
											.equals(commonMatchLogItemDto1.getMatchLogItemType())) {
								boolean blue = MatchLogItemType.BLUE_HEAD_POINT.equals(commonMatchLogItemDto1.getMatchLogItemType());
								HitEventValidator hitEventValidator = blue ? getRelated(blueEvents, HitEventType.HEAD, true)
										: getRelated(redEvents, HitEventType.HEAD, false);
								if(hitEventValidator != null)
									if(Math.abs(hitEventValidator.getHitTimestamp() - commonMatchLogItemDto1.getEventTime().longValue()) <= 1000L) {
										hitEventValidator.setHitTimestamp(commonMatchLogItemDto1.getEventTime().longValue());
									} else {
										hitEventValidator = null;
									}
								if(hitEventValidator == null)
									hitEventValidator = new HitEventValidator(blue, commonMatchLogItemDto1.getEventTime().longValue(),
											HitEventType.HEAD, Integer.valueOf(0), this.numberOfJudges, HitJudgeStatus.NOT_VALIDATED,
											HitJudgeStatus.NOT_VALIDATED, HitJudgeStatus.NOT_VALIDATED, this.backupSystemEnabled.get());
								if(blue) {
									blueEvents.add(hitEventValidator);
									hasBlueJudgeTech = false;
									continue;
								}
								redEvents.add(hitEventValidator);
								hasRedJudgeTech = false;
								continue;
							}
							if(MatchLogItemType.BLUE_BODY_TECH_POINT.equals(commonMatchLogItemDto1.getMatchLogItemType())
									|| MatchLogItemType.BLUE_HEAD_TECH_POINT
											.equals(commonMatchLogItemDto1.getMatchLogItemType())) {
								HitEventValidator hitEventValidator = getRelated(blueEvents,
										MatchLogItemType.BLUE_BODY_TECH_POINT.equals(commonMatchLogItemDto1.getMatchLogItemType()) ? HitEventType.BODY
												: HitEventType.HEAD, true);
								if(hitEventValidator == null || (hitEventValidator != null &&
										Math.abs(hitEventValidator.getHitTimestamp() - commonMatchLogItemDto1.getEventTime().longValue()) > 1000L)) {
									hitEventValidator = new HitEventValidator(true, commonMatchLogItemDto1.getEventTime().longValue(),
											MatchLogItemType.BLUE_BODY_TECH_POINT.equals(commonMatchLogItemDto1.getMatchLogItemType())
													? HitEventType.BODY
													: HitEventType.HEAD, Integer.valueOf(0), this.numberOfJudges, HitJudgeStatus.NOT_VALIDATED,
											HitJudgeStatus.NOT_VALIDATED, HitJudgeStatus.NOT_VALIDATED, this.backupSystemEnabled.get());
									blueEvents.add(hitEventValidator);
									hasBlueJudgeTech = true;
								}
								continue;
							}
							if(MatchLogItemType.RED_BODY_TECH_POINT.equals(commonMatchLogItemDto1.getMatchLogItemType())
									|| MatchLogItemType.RED_HEAD_TECH_POINT
											.equals(commonMatchLogItemDto1.getMatchLogItemType())) {
								HitEventValidator hitEventValidator = getRelated(blueEvents,
										MatchLogItemType.RED_BODY_TECH_POINT.equals(commonMatchLogItemDto1.getMatchLogItemType()) ? HitEventType.BODY
												: HitEventType.HEAD, true);
								if(hitEventValidator == null || (hitEventValidator != null &&
										Math.abs(hitEventValidator.getHitTimestamp() - commonMatchLogItemDto1.getEventTime().longValue()) > 1000L)) {
									hitEventValidator = new HitEventValidator(false, commonMatchLogItemDto1.getEventTime().longValue(),
											MatchLogItemType.RED_BODY_TECH_POINT.equals(commonMatchLogItemDto1.getMatchLogItemType())
													? HitEventType.BODY
													: HitEventType.HEAD, Integer.valueOf(0), this.numberOfJudges, HitJudgeStatus.NOT_VALIDATED,
											HitJudgeStatus.NOT_VALIDATED, HitJudgeStatus.NOT_VALIDATED, this.backupSystemEnabled.get());
									redEvents.add(hitEventValidator);
									hasRedJudgeTech = true;
								}
								continue;
							}
							if(MatchLogItemType.BLUE_JUDGE_BODY_TECH.equals(commonMatchLogItemDto1.getMatchLogItemType())
									|| MatchLogItemType.BLUE_JUDGE_HEAD_TECH
											.equals(commonMatchLogItemDto1.getMatchLogItemType())) {
								if(hasBlueJudgeTech) {
									HitEventValidator hitEventValidator = getRelated(blueEvents,
											MatchLogItemType.BLUE_JUDGE_BODY_TECH.equals(commonMatchLogItemDto1.getMatchLogItemType())
													? HitEventType.BODY
													: HitEventType.HEAD, true);
									if(hitEventValidator != null && Math.abs(hitEventValidator.getHitTimestamp() - commonMatchLogItemDto1
											.getEventTime().longValue()) <= 1000L) {
										int judge = Integer.valueOf(commonMatchLogItemDto1.getEntryValue()).intValue();
										switch(judge) {
											case 1:
												hitEventValidator.setJudge1HitStatus(HitJudgeStatus.VALIDATED);
												continue;
											case 2:
												hitEventValidator.setJudge2HitStatus(HitJudgeStatus.VALIDATED);
												continue;
											case 3:
												hitEventValidator.setJudge3HitStatus(HitJudgeStatus.VALIDATED);
												continue;
										}
									}
								}
								continue;
							}
							if(MatchLogItemType.RED_JUDGE_BODY_TECH.equals(commonMatchLogItemDto1.getMatchLogItemType())
									|| MatchLogItemType.RED_JUDGE_HEAD_TECH
											.equals(commonMatchLogItemDto1.getMatchLogItemType())) {
								if(hasRedJudgeTech) {
									HitEventValidator hitEventValidator = getRelated(redEvents,
											MatchLogItemType.RED_JUDGE_BODY_TECH.equals(commonMatchLogItemDto1.getMatchLogItemType())
													? HitEventType.BODY
													: HitEventType.HEAD, false);
									if(hitEventValidator != null && Math.abs(hitEventValidator.getHitTimestamp() - commonMatchLogItemDto1
											.getEventTime().longValue()) <= 1000L) {
										int judge = Integer.valueOf(commonMatchLogItemDto1.getEntryValue()).intValue();
										switch(judge) {
											case 1:
												hitEventValidator.setJudge1HitStatus(HitJudgeStatus.VALIDATED);
												continue;
											case 2:
												hitEventValidator.setJudge2HitStatus(HitJudgeStatus.VALIDATED);
												continue;
											case 3:
												hitEventValidator.setJudge3HitStatus(HitJudgeStatus.VALIDATED);
												continue;
										}
									}
								}
								continue;
							}
							if(MatchLogItemType.BLUE_PUNCH_POINT.equals(commonMatchLogItemDto1.getMatchLogItemType())
									|| MatchLogItemType.RED_PUNCH_POINT
											.equals(commonMatchLogItemDto1.getMatchLogItemType())) {
								boolean blue = MatchLogItemType.BLUE_PUNCH_POINT.equals(commonMatchLogItemDto1.getMatchLogItemType());
								HitEventValidator hitEventValidator = new HitEventValidator(blue, commonMatchLogItemDto1.getEventTime().longValue(),
										HitEventType.PUNCH, Integer.valueOf(0), this.numberOfJudges, HitJudgeStatus.NOT_VALIDATED,
										HitJudgeStatus.NOT_VALIDATED, HitJudgeStatus.NOT_VALIDATED, this.backupSystemEnabled.get());
								if(blue) {
									blueEvents.add(hitEventValidator);
									hasBlueJudgeTech = false;
									continue;
								}
								redEvents.add(hitEventValidator);
								hasRedJudgeTech = false;
								continue;
							}
							if(MatchLogItemType.BLUE_JUDGE_PUNCH.equals(commonMatchLogItemDto1.getMatchLogItemType())
									|| MatchLogItemType.RED_JUDGE_PUNCH
											.equals(commonMatchLogItemDto1.getMatchLogItemType())) {
								HitEventValidator hitEventValidator = MatchLogItemType.BLUE_JUDGE_PUNCH.equals(commonMatchLogItemDto1
										.getMatchLogItemType()) ? getRelated(blueEvents, HitEventType.PUNCH, true)
												: getRelated(redEvents, HitEventType.PUNCH, false);
								if(hitEventValidator != null && HitEventType.PUNCH
										.equals(hitEventValidator.getHitEventType())) {
									int judge = Integer.valueOf(commonMatchLogItemDto1.getEntryValue()).intValue();
									switch(judge) {
										case 1:
											hitEventValidator.setJudge1HitStatus(HitJudgeStatus.VALIDATED);
										case 2:
											hitEventValidator.setJudge2HitStatus(HitJudgeStatus.VALIDATED);
										case 3:
											hitEventValidator.setJudge3HitStatus(HitJudgeStatus.VALIDATED);
									}
								}
							}
						}
					}
					blueEvents.stream().sorted(Collections.reverseOrder()).forEach(e -> fireForceAddHitEventValidator(e));
					redEvents.stream().sorted(Collections.reverseOrder()).forEach(e -> fireForceAddHitEventValidator(e));
					_addMatchLogItem(Long.valueOf(System.currentTimeMillis()),
							Long.valueOf(roundTime.getTimeInMillis()),
							Long.valueOf(System.currentTimeMillis()),
							getRound4MatchLog(true), getRoundStr4MatchLog(true), MatchLogItemType.RESET_MATCH,
							Integer.valueOf(0),
							Integer.valueOf(0), " ", false,

							Integer.valueOf(0), false);
				} else {
					changeCurrentRoundAndTime(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(0), (Integer)null);
				}
			} catch(TkStrikeServiceException e) {
				e.printStackTrace();
			}
		}
	}

	private HitEventValidator getLast(Collection<HitEventValidator> collection) {
		long count = collection.stream().count();
		Stream<HitEventValidator> stream = collection.stream();
		return stream.skip(count - 1L).findFirst().orElse(null);
	}

	private HitEventValidator getRelated(Collection<HitEventValidator> collection, HitEventType typeToRelate, boolean blue) {
		HitEventValidator res = null;
		LinkedList<HitEventValidator> aList = new LinkedList<>(collection);
		Iterator<HitEventValidator> iterator = aList.descendingIterator();
		while(iterator.hasNext()) {
			HitEventValidator hitEventValidator = iterator.next();
			if(hitEventValidator.getHitEventType().equals(typeToRelate) && (hitEventValidator
					.isBlue() == blue || ( ! blue && ! hitEventValidator.isBlue())))
				return hitEventValidator;
		}
		return null;
	}

	@Override
	public final SimpleBooleanProperty scoreboardEditorOpenProperty() {
		return this.scoreboardEditorOpen;
	}

	@Override
	public final SimpleBooleanProperty finalResultOpenProperty() {
		return this.finalResultOpen;
	}

	@Override
	public SimpleBooleanProperty roundFinishedOpenProperty() {
		return this.roundFinishedOpen;
	}

	@Override
	public final MatchStatusId getCurrentMatchStatus() {
		return this.currentMatchStatus.get();
	}

	@Override
	public final ReadOnlyObjectProperty<MatchStatusId> currentMatchStatusProperty() {
		return this.currentMatchStatus;
	}

	@Override
	public final NetworkErrorCause getNetworkErrorCause() {
		return this.networkErrorCause;
	}

	@Override
	public final ReadOnlyBooleanProperty showGoldenPointTieBreakerOnScoreboard() {
		return this.showGoldenPointTieBreakerOnScoreboard;
	}

	@Override
	public final void doShowGoldenPointTieBreakerOnScoreboard(boolean show) {
		if(this.showGoldenPointTieBreakerOnScoreboard.get() == show)
			this.showGoldenPointTieBreakerOnScoreboard.set( ! show);
		this.showGoldenPointTieBreakerOnScoreboard.set(show);
	}

	@Override
	public final int getMatchRounds() {
		return this.matchRounds.get();
	}

	@Override
	public final ReadOnlyIntegerProperty matchRoundsProperty() {
		return this.matchRounds;
	}

	@Override
	public final boolean isGoldenPointEnabled() {
		return this.goldenPointEnabled;
	}

	@Override
	public final boolean isGoldenPointWorking() {
		return this.goldenPointWorking;
	}

	@Override
	public boolean showNearMissHitsOnScoreboardEditor() {
		return (this.goldenPointWorking || MatchVictoryCriteria.BESTOF3.equals(this.matchVictoryCriteria));
	}

	@Override
	public final boolean isGoldenPointTieBreaker() {
		return this.goldenPointTieBreaker;
	}

	@Override
	public final boolean isGoldenPointPointByPenalty() {
		return this.goldenPointPointByPenalty;
	}

	@Override
	public final int getCurrentRound() {
		return this.currentRound.get();
	}

	@Override
	public final ReadOnlyIntegerProperty currentRoundProperty() {
		return this.currentRound;
	}

	@Override
	public final String getCurrentRoundStr() {
		return this.currentRoundStr.get();
	}

	@Override
	public final long getCurrentRoundCountdownAsMillis() {
		return getRoundCountdownCurrentTimeMillis();
	}

	@Override
	public String getCurrentRoundCountdownString() {
		return this.roundCountdown.getCurrentTimeInternalAsString();
	}

	@Override
	public final ReadOnlyStringProperty currentRoundStrProperty() {
		return this.currentRoundStr;
	}

	@Override
	public final MatchWinner getRoundWinner(Integer roundNumber) {
		return this.roundsWinner.get(roundNumber);
	}

	@Override
	public Integer getBlueRoundPoints(Integer roundNumber) {
		return this.blueRoundsPoints.get(roundNumber);
	}

	@Override
	public Integer getRedRoundPoints(Integer roundNumber) {
		return this.redRoundsPoints.get(roundNumber);
	}

	@Override
	public final FinalDecision getMatchFinalDecision() {
		return this.matchFinalDecission.get();
	}

	@Override
	public final void confirmFinalResult(MatchWinner winner, FinalDecision finalDecision) {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("Final result of match!");
		if(this.restTimeCountdown.getWorking())
			this.restTimeCountdown.stop();
		this.restTimeCountdown.clean();
		if(this.kyeShiCountdown.getWorking())
			this.kyeShiCountdown.stop();
		this.kyeShiCountdown.clean();
		this.matchWinner.set(winner);
		this.matchFinalDecission.set(finalDecision);
		this.currentMatchStatus.set(MatchStatusId.MATCH_FINISHED);
		this.appStatusWorker.addAppStatusOk(AppStatusId.MATCH_FINISHED);
		this.matchWinnerChanges.set(Boolean.TRUE.booleanValue());
		this.matchWinnerChanges.set(Boolean.FALSE.booleanValue());
		_addMatchLogItem(Long.valueOf(System.currentTimeMillis()),
				Long.valueOf(getCurrentRoundCountdownAsMillis()),
				Long.valueOf(System.currentTimeMillis()),
				getRound4MatchLog(true), getRoundStr4MatchLog(true), MatchLogItemType.MATCH_FINISHED,
				Integer.valueOf(0),
				Integer.valueOf(0), winner
						.toString() + " - " + finalDecision.toString(), true,

				Integer.valueOf(0), false);
		MLD matchLog = updateMatchLogMatchFinish(Long.valueOf(System.currentTimeMillis()), winner, finalDecision,

				getMatchResult(), this.goldenPointTieBreakerInfo);
		_internalConfirmFinalResult(matchLog, winner, finalDecision);
	}

	@Override
	public void undoFinalResult() {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("Undo Final Result of match!");
		enableChangeMatchStatusToTimeoutOnScoreboardChanges();
		this.matchWinner.set(null);
		this.matchWinner.set(MatchWinner.TIE);
		this.matchFinalDecission.set(null);
		this.matchFinalDecission.set(FinalDecision.WDR);
		this.matchWinnerByPointGapNeedsConfirmation.set(false);
	}

	private String getMatchResult() {
		return getBlueMatchPoints() + "-" + getRedMatchPoints();
	}

	@Override
	public Integer getBlueMatchPoints() {
		if(MatchVictoryCriteria.BESTOF3.equals(this.matchVictoryCriteria))
			return Integer.valueOf(this.blueTotalPoints.get());
		if(this.blueTotalPoints.get() > 0 || this.redTotalPoints.get() > 0) {
			if(this.goldenPointEnabled && this.goldenPointWorking && ! this.goldenPointMatchFinishSendTotalPoints.booleanValue())
				return Integer.valueOf(this.blueGeneralPoints.get());
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.info("** BLUE GENERAL POINTS: " + this.blueGeneralPoints.get() + " TOTAL:" + this.blueTotalPoints.get()
						+ " GP-PUNCHES:" + this.blueGoldenPointPunches.get());
			return Integer.valueOf(this.blueGeneralPoints.get() + this.blueTotalPoints.get() - (subtractGoldenPointPunchPoints(true)
					? this.blueGoldenPointPunches.get()
					: 0));
		}
		return Integer.valueOf(this.blueGeneralPoints.get());
	}

	@Override
	public Integer getRedMatchPoints() {
		if(MatchVictoryCriteria.BESTOF3.equals(this.matchVictoryCriteria))
			return Integer.valueOf(this.redTotalPoints.get());
		if(this.blueTotalPoints.get() >= 0 || this.redTotalPoints.get() >= 0) {
			if(this.goldenPointEnabled && this.goldenPointWorking && ! this.goldenPointMatchFinishSendTotalPoints.booleanValue())
				return Integer.valueOf(this.redGeneralPoints.get());
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.info("** RED GENERAL POINTS: " + this.redGeneralPoints.get() + " TOTAL:" + this.redTotalPoints.get()
						+ " GP-PUNCHES:" + this.redGoldenPointPunches.get());
			return Integer.valueOf(this.redGeneralPoints.get() + this.redTotalPoints.get() - (subtractGoldenPointPunchPoints(false)
					? this.redGoldenPointPunches.get()
					: 0));
		}
		return Integer.valueOf(this.redGeneralPoints.get());
	}

	private boolean subtractGoldenPointPunchPoints(boolean isBlue) {
		return (this.subtractGoldenPointPunchPointsIfLooserFromTotal.booleanValue() && ((isBlue ? MatchWinner.RED
				.equals(getMatchWinner()) : MatchWinner.BLUE.equals(getMatchWinner())) || FinalDecision.SUP
						.equals(getMatchFinalDecision())));
	}

	@Override
	public Integer getBlueRoundsWins() {
		return this.blueRoundWins.getValue();
	}

	@Override
	public ReadOnlyIntegerProperty blueRoundsWinProperty() {
		return this.blueRoundWins;
	}

	@Override
	public ReadOnlyIntegerProperty blueR1PointsProperty() {
		return this.blueR1Points;
	}

	@Override
	public ReadOnlyIntegerProperty blueR2PointsProperty() {
		return this.blueR2Points;
	}

	@Override
	public ReadOnlyIntegerProperty blueR3PointsProperty() {
		return this.blueR3Points;
	}

	@Override
	public ReadOnlyIntegerProperty redRoundsWinProperty() {
		return this.redRoundWins;
	}

	@Override
	public ReadOnlyIntegerProperty redR1PointsProperty() {
		return this.redR1Points;
	}

	@Override
	public ReadOnlyIntegerProperty redR2PointsProperty() {
		return this.redR2Points;
	}

	@Override
	public ReadOnlyIntegerProperty redR3PointsProperty() {
		return this.redR3Points;
	}

	@Override
	public int getBlueNearMissHits() {
		return this.blueRoundsHits.get(Integer.valueOf(getCurrentRound())).intValue();
	}

	@Override
	public ReadOnlyIntegerProperty blueNearMissHitsProperty() {
		return this.blueRoundsHitsProperty;
	}

	@Override
	public int getRedNearMissHits() {
		return this.redRoundsHits.get(Integer.valueOf(getCurrentRound())).intValue();
	}

	@Override
	public ReadOnlyIntegerProperty redNearMissHitsProperty() {
		return this.redRoundsHitsProperty;
	}

	@Override
	public Integer getBlueTechPoints() {
		return Integer.valueOf(this.blueTechPoints.get());
	}

	@Override
	public Integer getRedRoundsWins() {
		return this.redRoundWins.getValue();
	}

	@Override
	public Integer getRedTechPoints() {
		return Integer.valueOf(this.redTechPoints.get());
	}

	@Override
	public final ReadOnlyObjectProperty<FinalDecision> matchFinalDecisionProperty() {
		return this.matchFinalDecission;
	}

	@Override
	public final MatchWinner getMatchWinner() {
		return this.matchWinner.get();
	}

	@Override
	public final ReadOnlyObjectProperty<MatchWinner> matchWinnerProperty() {
		return this.matchWinner;
	}

	@Override
	public final ReadOnlyBooleanProperty matchWinnerChangesProperty() {
		return this.matchWinnerChanges;
	}

	@Override
	public ReadOnlyBooleanProperty matchWinnerByPointGapNeedsConfirmationProperty() {
		return this.matchWinnerByPointGapNeedsConfirmation;
	}

	@Override
	public void applyPointGapConfirmation(boolean accepted) {
		if(this.pointGapWithGamJeomNeedsConfirmation.booleanValue()) {
			this.matchWinnerByPointGapNeedsConfirmation.set(false);
			if( ! accepted) {
				cancelVictoryByPointGap();
			} else {
				this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
				this.matchFinalDecission.set(FinalDecision.PTG);
			}
		}
	}

	@Override
	public MatchVictoryCriteria getMatchVictoryCriteria() {
		return this.matchVictoryCriteria;
	}

	@Override
	public ReadOnlyObjectProperty<MatchWinner> bestOf3CurrentRoundPartialWinner() {
		return this.bestOf3CurrentRoundPartialWinner;
	}

	@Override
	public BooleanProperty bestOf3RoundWithPointGap() {
		return this.bestOf3RoundWithPointGap;
	}

	@Override
	public Boolean isBestOf3WinnerLastRoundByPUN() {
		return Boolean.valueOf(this.bestOf3WinnerLastRoundByPUN);
	}

	@Override
	public MatchWinner getBestOf3RoundWinnerWithPointGap() {
		return this.bestOf3RoundWinnerWithPointGap;
	}

	@Override
	public BooleanProperty bestOf3RoundWithSuperiority() {
		return this.bestOf3RoundWithSuperiority;
	}

	@Override
	public BooleanProperty bestOf3RoundSuperiorityOnScoreboard() {
		return this.bestOf3RoundSuperiorityOnScoreboard;
	}

	@Override
	public void bestOf3HideRoundSuperiorityOnScoreboard() {
		this.bestOf3RoundSuperiorityOnScoreboard.set(false);
	}

	@Override
	public void bestOf3ShowRoundSuperiorityOnScoreboard() {
		this.bestOf3RoundSuperiorityOnScoreboard.set(true);
	}

	@Override
	public BestOf3RoundSuperiority getCurrentBestOf3RoundSuperiority() {
		return this.bestOf3RoundSuperiority;
	}

	@Override
	public void enableOnBestOf3ChangesFromScoreboardEditor() {
		matchWorkerLogger.info("enableOnBestOf3ChangesFromScoreboardEditor");
		this.bestOf3UpdatingFromScoreboardEditor = Boolean.valueOf(true);
	}

	@Override
	public void disableOnBestOf3ChangesFromScoreboardEditor() {
		if(this.bestOf3UpdatingFromScoreboardEditor.booleanValue()) {
			matchWorkerLogger.info("disableOnBestOf3ChangesFromScoreboardEditor");
			this.bestOf3UpdatingFromScoreboardEditor = Boolean.valueOf(false);
		}
	}

	@Override
	public BooleanProperty superiorityByRoundsProperty() {
		return this.superiorityByRounds;
	}

	@Override
	public BooleanProperty showSuperiorityByRoundsProperty() {
		return this.showSuperiorityByRounds;
	}

	@Override
	public void hideSuperiorityByRoundsProperty() {
		this.showSuperiorityByRounds.set(false);
	}

	@Override
	public MatchWinner getBestOf3WinnerLastRoundWithSuperiority() {
		return this.bestOf3WinnerLastRoundWithSuperiority;
	}

	@Override
	public ReadOnlyBooleanProperty roundsWinnerChangesProperty() {
		return this.roundsWinnerChanges;
	}

	@Override
	public Integer getMaxGamJeomsAllowed() {
		return _getMaxGamJeomsAllowed();
	}

	@Override
	public final SimpleIntegerProperty blueLastImpactValueProperty() {
		return this.blueLastImpactValue;
	}

	@Override
	public final int getBlueGeneralPoints() {
		return this.blueGeneralPoints.get();
	}

	@Override
	public final ReadOnlyIntegerProperty blueGeneralPointsProperty() {
		return this.blueGeneralPoints;
	}

	@Override
	public final int getBluePenalties() {
		return this.bluePenalties.get();
	}

	@Override
	public final ReadOnlyIntegerProperty bluePenaltiesProperty() {
		return this.bluePenalties;
	}

	@Override
	public final int getBlueGoldenPointImpacts() {
		return this.blueGoldenPointImpacts.get();
	}

	@Override
	public final SimpleIntegerProperty blueGoldenPointImpactsProperty() {
		return this.blueGoldenPointImpacts;
	}

	@Override
	public final int getBlueGoldenPointPenalties() {
		return this.blueGoldenPointPenalties.get();
	}

	@Override
	public final ReadOnlyIntegerProperty blueGoldenPointPenaltiesProperty() {
		return this.blueGoldenPointPenalties;
	}

	@Override
	public final void removeBlueNearMissHit(ActionSource actionSource, long eventTimestamp, long roundTimestamp) {
		if(this.goldenPointWorking) {
			if(this.blueGoldenPointImpacts.get() > 0) {
				this.blueGoldenPointImpacts.set(this.blueGoldenPointImpacts.get() - 1);
				_addMatchLogItem(Long.valueOf(eventTimestamp),
						Long.valueOf(roundTimestamp),
						Long.valueOf(System.currentTimeMillis()),
						getRound4MatchLog(true), getRoundStr4MatchLog(true), MatchLogItemType.BLUE_REMOVE_NEAR_MISS_HIT,
						Integer.valueOf(0),
						Integer.valueOf(0), actionSource
								.toString(), true,

						Integer.valueOf(0), false);
				fireRemoveGoldenPointNearMissHit(true);
			}
		} else if(MatchVictoryCriteria.BESTOF3.equals(this.matchVictoryCriteria)) {
			workWithBestOf3NearMisHitChange(actionSource, true, eventTimestamp, roundTimestamp, - 1);
		}
	}

	@Override
	public void addBlueNearMissHit(ActionSource actionSource, long eventTimestamp, long roundTimestamp) {
		if(this.goldenPointWorking) {
			this.blueGoldenPointImpacts.set(this.blueGoldenPointImpacts.get() + 1);
			_addMatchLogItem(Long.valueOf(eventTimestamp),
					Long.valueOf(roundTimestamp),
					Long.valueOf(System.currentTimeMillis()),
					getRound4MatchLog(true),
					getRoundStr4MatchLog(true), MatchLogItemType.BLUE_ADD_NEAR_MISS_HIT,

					Integer.valueOf(0),
					Integer.valueOf(0), actionSource
							.toString(), true,

					Integer.valueOf(0), false);
			fireAddGoldenPointNearMissHit(true);
		} else if(MatchVictoryCriteria.BESTOF3.equals(this.matchVictoryCriteria)) {
			workWithBestOf3NearMisHitChange(actionSource, true, eventTimestamp, roundTimestamp, 1);
		}
	}

	@Override
	public int getBlueTotalPenalties() {
		return Math.max(this.bluePenalties.get(), this.blueTotalPenalties.get()) + this.blueGoldenPointPenalties.get();
	}

	@Override
	public ReadOnlyIntegerProperty blueGoldenPointPunchesProperty() {
		return this.blueGoldenPointPunches;
	}

	@Override
	public ReadOnlyIntegerProperty bluePARATechPointsProperty() {
		return this.bluePARATechPoints;
	}

	@Override
	public boolean isBlueLastPointIsPenalty() {
		return this.blueLastPointIsPenalty;
	}

	@Override
	public ReadOnlyBooleanProperty bluePARATimeOutQuotaProperty() {
		return this.bluePARATimeOutQuota;
	}

	@Override
	public ReadOnlyIntegerProperty bluePARATimeOutQuotaValueProperty() {
		return this.bluePARATimeOutQuotaValue;
	}

	@Override
	public void doPARATimeOutByBlue() {
		if(isParaTkdMatch() && this.bluePARATimeOutQuota.getValue().booleanValue() && this.bluePARATimeOutQuotaValue.getValue().intValue() > 0) {
			this.paraTimeOutCountdown.clean(this.paraTimeOutMinutes.intValue(), this.paraTimeOutSeconds.intValue());
			this.bluePARATimeOutQuotaValue.setValue(Integer.valueOf(this.bluePARATimeOutQuotaValue.getValue().intValue() - 1));
			this.bluePARATimeOutQuota.set(false);
			this.currentMatchStatus.set(MatchStatusId.ROUND_PARA_TIMEOUT_WORKING);
			this.athleteWithPARATimeOutCountdownWorking = 1;
			this.paraTimeOutCountdown.play();
			_addMatchLogItem(Long.valueOf(System.currentTimeMillis()),
					Long.valueOf(this.roundCountdown.getCurrentTimeMillis()),
					Long.valueOf(System.currentTimeMillis()),
					getRound4MatchLog(true),
					getRoundStr4MatchLog(true), MatchLogItemType.BLUE_PARA_TIMEOUT,

					Integer.valueOf(0),
					Integer.valueOf(0), MatchLogItemType.BLUE_PARA_TIMEOUT
							.name(), true,

					Integer.valueOf(0), false);
		}
	}

	@Override
	public void doResetPARATimeOutForBlue() {
		this.bluePARATimeOutQuotaValue.setValue(Integer.valueOf(1));
		this.bluePARATimeOutQuota.set(true);
		_addMatchLogItem(Long.valueOf(System.currentTimeMillis()),
				Long.valueOf(this.roundCountdown.getCurrentTimeMillis()),
				Long.valueOf(System.currentTimeMillis()),
				getRound4MatchLog(true),
				getRoundStr4MatchLog(true), MatchLogItemType.RESET_BLUE_PARA_TIMEOUT,

				Integer.valueOf(0),
				Integer.valueOf(0), MatchLogItemType.RESET_BLUE_PARA_TIMEOUT
						.name(), true,

				Integer.valueOf(0), true);
	}

	@Override
	public final SimpleIntegerProperty redLastImpactValueProperty() {
		return this.redLastImpactValue;
	}

	@Override
	public final int getRedGeneralPoints() {
		return this.redGeneralPoints.get();
	}

	@Override
	public final ReadOnlyIntegerProperty redGeneralPointsProperty() {
		return this.redGeneralPoints;
	}

	@Override
	public final int getRedPenalties() {
		return this.redPenalties.get();
	}

	@Override
	public final ReadOnlyIntegerProperty redPenaltiesProperty() {
		return this.redPenalties;
	}

	@Override
	public final int getRedGoldenPointImpacts() {
		return this.redGoldenPointImpacts.get();
	}

	@Override
	public final SimpleIntegerProperty redGoldenPointImpactsProperty() {
		return this.redGoldenPointImpacts;
	}

	@Override
	public final int getRedGoldenPointPenalties() {
		return this.redGoldenPointPenalties.get();
	}

	@Override
	public final ReadOnlyIntegerProperty redGoldenPointPenaltiesProperty() {
		return this.redGoldenPointPenalties;
	}

	@Override
	public ReadOnlyIntegerProperty redPARATechPointsProperty() {
		return this.redPARATechPoints;
	}

	@Override
	public boolean isRedLastPointIsPenalty() {
		return this.redLastPointIsPenalty;
	}

	@Override
	public ReadOnlyBooleanProperty redPARATimeOutQuotaProperty() {
		return this.redPARATimeOutQuota;
	}

	@Override
	public ReadOnlyIntegerProperty redPARATimeOutQuotaValueProperty() {
		return this.redPARATimeOutQuotaValue;
	}

	@Override
	public void doPARATimeOutByRed() {
		this.paraTimeOutCountdown.clean(this.paraTimeOutMinutes.intValue(), this.paraTimeOutSeconds.intValue());
		this.redPARATimeOutQuotaValue.setValue(Integer.valueOf(this.redPARATimeOutQuotaValue.getValue().intValue() - 1));
		this.redPARATimeOutQuota.set(false);
		this.currentMatchStatus.set(MatchStatusId.ROUND_PARA_TIMEOUT_WORKING);
		this.athleteWithPARATimeOutCountdownWorking = 2;
		this.paraTimeOutCountdown.play();
		_addMatchLogItem(Long.valueOf(System.currentTimeMillis()),
				Long.valueOf(this.roundCountdown.getCurrentTimeMillis()),
				Long.valueOf(System.currentTimeMillis()),
				getRound4MatchLog(true),
				getRoundStr4MatchLog(true), MatchLogItemType.RED_PARA_TIMEOUT,

				Integer.valueOf(0),
				Integer.valueOf(0), MatchLogItemType.RED_PARA_TIMEOUT
						.name(), true,

				Integer.valueOf(0), false);
	}

	@Override
	public void doResetPARATimeOutForRed() {
		this.redPARATimeOutQuotaValue.setValue(Integer.valueOf(1));
		this.redPARATimeOutQuota.set(true);
		_addMatchLogItem(Long.valueOf(System.currentTimeMillis()),
				Long.valueOf(this.roundCountdown.getCurrentTimeMillis()),
				Long.valueOf(System.currentTimeMillis()),
				getRound4MatchLog(true),
				getRoundStr4MatchLog(true), MatchLogItemType.RESET_RED_PARA_TIMEOUT,

				Integer.valueOf(0),
				Integer.valueOf(0), MatchLogItemType.RESET_RED_PARA_TIMEOUT
						.name(), true,

				Integer.valueOf(0), true);
	}

	@Override
	public final void removeRedNearMissHit(ActionSource actionSource, long eventTimestamp, long roundTimestamp) {
		if(this.goldenPointWorking) {
			if(this.redGoldenPointImpacts.get() > 0) {
				this.redGoldenPointImpacts.set(this.redGoldenPointImpacts.get() - 1);
				_addMatchLogItem(Long.valueOf(eventTimestamp),
						Long.valueOf(roundTimestamp),
						Long.valueOf(System.currentTimeMillis()),
						getRound4MatchLog(true), getRoundStr4MatchLog(true), MatchLogItemType.RED_REMOVE_NEAR_MISS_HIT,
						Integer.valueOf(0),
						Integer.valueOf(0), actionSource
								.toString(), true,

						Integer.valueOf(0), false);
				fireRemoveGoldenPointNearMissHit(false);
			}
		} else if(MatchVictoryCriteria.BESTOF3.equals(this.matchVictoryCriteria)) {
			workWithBestOf3NearMisHitChange(actionSource, false, eventTimestamp, roundTimestamp, - 1);
		}
	}

	@Override
	public void addRedNearMissHit(ActionSource actionSource, long eventTimestamp, long roundTimestamp) {
		if(this.goldenPointWorking) {
			this.redGoldenPointImpacts.set(this.redGoldenPointImpacts.get() + 1);
			_addMatchLogItem(Long.valueOf(eventTimestamp),
					Long.valueOf(roundTimestamp),
					Long.valueOf(System.currentTimeMillis()),
					getRound4MatchLog(true), getRoundStr4MatchLog(true), MatchLogItemType.RED_ADD_NEAR_MISS_HIT,
					Integer.valueOf(0),
					Integer.valueOf(0), actionSource
							.toString(), true,

					Integer.valueOf(0), false);
			fireAddGoldenPointNearMissHit(false);
		} else if(MatchVictoryCriteria.BESTOF3.equals(this.matchVictoryCriteria)) {
			workWithBestOf3NearMisHitChange(actionSource, false, eventTimestamp, roundTimestamp, 1);
		}
	}

	@Override
	public int getRedTotalPenalties() {
		return Math.max(this.redTotalPenalties.get(), this.redPenalties.get()) + this.redGoldenPointPenalties.get();
	}

	@Override
	public ReadOnlyIntegerProperty redGoldenPointPunchesProperty() {
		return this.redGoldenPointPunches;
	}

	private void workWithBestOf3NearMisHitChange(ActionSource actionSource, boolean blue, long eventTimestamp, long roundTimestamp, int value) {
		appendToCounter(blue ? "BLUE_HITS" : "RED_HITS", blue ? this.blueRoundsHits : this.redRoundsHits, Integer.valueOf(getCurrentRound()), Integer
				.valueOf(value));
		_addMatchLogItem(Long.valueOf(eventTimestamp),
				Long.valueOf(roundTimestamp),
				Long.valueOf(System.currentTimeMillis()),
				getRound4MatchLog(true),
				getRoundStr4MatchLog(true), blue ? MatchLogItemType.BLUE_ADD_NEAR_MISS_HIT : MatchLogItemType.RED_ADD_NEAR_MISS_HIT,

				Integer.valueOf(0),
				Integer.valueOf(0), actionSource
						.toString(), true,

				Integer.valueOf(0), false);
		if(this.bestOf3RoundWithSuperiority.get())
			calculateCurrentRoundWinner(true, false);
	}

	@Override
	public void setTextCountdown(Text text) {
		this.roundCountdown.setTextElement(text);
	}

	@Override
	public Integer getRoundCountdownMinutes() {
		return Integer.valueOf(this.roundCountdown.getMinutes());
	}

	@Override
	public Integer getRoundCountdownSeconds() {
		return Integer.valueOf(this.roundCountdown.getSeconds());
	}

	@Override
	public Integer getRoundCountdownCentiseconds() {
		return Integer.valueOf(this.roundCountdown.getCentiseconds());
	}

	@Override
	public ReadOnlyStringProperty roundCountdownCurrentTimeAsStringProperty() {
		return this.roundCountdown.currentTimeAsStringProperty();
	}

	@Override
	public long getRoundCountdownCurrentTimeMillis() {
		return (MatchStatusId.WAITING_4_START_ROUND.equals(getCurrentMatchStatus()) && this.currentRound.get() > 1) ? 0L
				: this.roundCountdown.getCurrentTimeMillis();
	}

	@Override
	public ReadOnlyBooleanProperty roundCountdownFinishedProperty() {
		return this.roundCountdown.finishedProperty();
	}

	@Override
	public ReadOnlyStringProperty kyeShiCountdownCurrentTimeAsStringProperty() {
		return this.kyeShiCountdown.currentTimeAsStringProperty();
	}

	@Override
	public ReadOnlyBooleanProperty restTimeCountdownFinishedProperty() {
		return this.restTimeCountdown.finishedProperty();
	}

	@Override
	public ReadOnlyStringProperty restTimeCountdownCurrentTimeAsStringProperty() {
		return this.restTimeCountdown.currentTimeAsStringProperty();
	}

	@Override
	public ReadOnlyBooleanProperty paraTimeOutCountdownFinishedProperty() {
		return this.paraTimeOutCountdown.finishedProperty();
	}

	@Override
	public ReadOnlyStringProperty paraTimeOutCountdownCurrentTimeAsStringProperty() {
		return this.paraTimeOutCountdown.currentTimeAsStringProperty();
	}

	@Override
	public void cancelParaTimeOutCountdown() {
		if(this.paraTimeOutCountdown.getWorking()) {
			this.paraTimeOutCountdown.stop();
			this.paraTimeOutCountdown.clean(0, 0);
			paraTimeoutCountdownStopOrFinished();
		}
	}

	private void paraTimeoutCountdownStopOrFinished() {
		final boolean isBlue = (this.athleteWithPARATimeOutCountdownWorking == 1);
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("PARA TimeOut Countdown Finished or canceled!");
		TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				BaseCommonMatchWorker.this.getSoundPlayerService().playSoundEndOfTime();
				return null;
			}
		});
		if(this.wtUDPService.isConnected())
			TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					BaseCommonMatchWorker.this.wtUDPService.sendPARATimeOutCountdownChange(isBlue, "0:00", WtUDPService.ClockAction.END);
					return null;
				}
			});
		this.athleteWithPARATimeOutCountdownWorking = 0;
		doPauseRound();
	}

	@Override
	public void changeCurrentRoundAndTime(Integer changeNewRound, Integer changeNewMinutesTime, Integer changeNewSecondsTime,
			Integer changeNewCentisecondsTime) {
		if(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION.equals(getCurrentMatchStatus()))
			this.currentMatchStatus.set(MatchStatusId.ROUND_PAUSED);
		if(getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_MATCH) &&
				this.currentRound.get() == 1) {
			updateMatchLogStartTime(Long.valueOf(System.currentTimeMillis()));
			this.bluePoints.set(0);
			this.blueGeneralPoints.set(0);
			this.blueTechPoints.set(0);
			this.blueRoundWins.set(0);
			this.bluePenaltiesNextRound = Integer.valueOf(0);
			this.redPoints.set(0);
			this.redGeneralPoints.set(0);
			this.redRoundWins.set(0);
			this.redTechPoints.set(0);
			this.redPenaltiesNextRound = Integer.valueOf(0);
			initializeRoundInternalCounters();
			_addMatchLogItem(Long.valueOf(System.currentTimeMillis()),
					Long.valueOf(this.roundCountdown.getCurrentTimeMillis()),
					Long.valueOf(System.currentTimeMillis()),
					getRound4MatchLog(true), getRoundStr4MatchLog(true), MatchLogItemType.START_MATCH,
					Integer.valueOf(0),
					Integer.valueOf(0), "", true,

					Integer.valueOf(0), true);
		}
		if(getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_MATCH) ||
				getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_START_GOLDENPOINT) ||
				getCurrentMatchStatus().equals(MatchStatusId.ROUND_PAUSED) ||
				getCurrentMatchStatus().equals(MatchStatusId.ROUND_KYESHI) ||
				getCurrentMatchStatus().equals(MatchStatusId.ROUND_FINISHED) ||
				getCurrentMatchStatus().equals(MatchStatusId.ROUND_GOLDENPOINT_FINISHED) || (

				getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) && this.roundFinishedOpen.get())) {
			boolean someChange = false;
			if(changeNewRound != null) {
				if(changeNewRound.intValue() == - 1) {
					this.currentRound.set(this.matchRounds.get() + 1);
					this.currentRoundStr.set("" + (this.matchRounds.get() + 1));
					this.goldenPointWorking = true;
					this.goldenPointTieBreaker = false;
					this.goldenPointTieBreakerInfo = null;
					this.goldenPointPointByPenalty = false;
					this.goldenPointPointByPunch = false;
					this.goldenPointPointByPenaltyValue = 0;
					this.blueGoldenPointImpacts.set(0);
					this.redGoldenPointImpacts.set(0);
					this.roundCountdown.clean(this.goldenPointTimeMinutes, this.goldenPointTimeSeconds);
				}
				if(changeNewRound.intValue() > 0 && changeNewRound.intValue() <= this.matchRounds.get()) {
					this.currentRound.set(changeNewRound.intValue());
					this.currentRoundStr.set("" + changeNewRound);
					for(int i = this.currentRound.get(); i <= this.matchRounds.get(); i++)
						initializeRoundInternalCounters(i);
					if(this.goldenPointEnabled && this.goldenPointWorking)
						this.goldenPointWorking = false;
				}
				someChange = true;
			}
			if(changeNewMinutesTime != null && changeNewSecondsTime != null) {
				long newTimeInMillis = 0L;
				if(this.goldenPointEnabled && this.goldenPointWorking) {
					if(changeNewCentisecondsTime != null) {
						this.roundCountdown.clean(changeNewMinutesTime.intValue(), changeNewSecondsTime.intValue(), changeNewCentisecondsTime
								.intValue());
					} else {
						this.roundCountdown.clean(changeNewMinutesTime.intValue(), changeNewSecondsTime.intValue());
					}
					newTimeInMillis = this.roundCountdown.getCurrentTimeMillis();
				} else {
					if(changeNewCentisecondsTime != null) {
						this.roundCountdown.clean(changeNewMinutesTime.intValue(), changeNewSecondsTime.intValue(), changeNewCentisecondsTime
								.intValue());
					} else {
						this.roundCountdown.clean(changeNewMinutesTime.intValue(), changeNewSecondsTime.intValue());
					}
					newTimeInMillis = this.roundCountdown.getCurrentTimeMillis();
				}
				if(this.wtUDPService.isConnected()) {
					final String roundCountdown = this.sdfRoundTimeNoMillis.format(new Date(newTimeInMillis));
					TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

						@Override
						public Void call() throws Exception {
							BaseCommonMatchWorker.this.wtUDPService.sendRoundCountdownChange(roundCountdown, WtUDPService.ClockAction.CORRECTION);
							return null;
						}
					});
				}
				if(this.rtBroadcastSocketClient.isConnected())
					sendEventToRtBroadcast("TIME_ADJUST", this.currentMatchNumber,

							Integer.valueOf(this.currentRound.get()), this.currentRoundStr
									.get(),
							Boolean.valueOf(this.goldenPointWorking), Long.valueOf(newTimeInMillis),
							Long.valueOf(System.currentTimeMillis()), false, false,
							Integer.valueOf(this.blueGeneralPoints.get()),
							Integer.valueOf(this.bluePenalties.get()),
							Integer.valueOf(0),
							getBlueRoundsWins(), Integer.valueOf(this.redGeneralPoints.get()),
							Integer.valueOf(this.redPenalties.get()),
							Integer.valueOf(0),
							getRedRoundsWins(), (Integer)null, (String)null, (String)null, (String)null, (String)null);
				someChange = true;
			}
			if(someChange && ! this.matchStarted.booleanValue())
				this.matchStarted = Boolean.valueOf(true);
			if(someChange && this.restTimeCountdown.getWorking()) {
				this.restTimeCountdown.stop();
				this.restTimeCountdown.clean();
			}
			if(someChange) {
				if(this.roundFinishedOpen.get()) {
					this.roundFinishedOpen.set(false);
					this.bestOf3RoundWithSuperiority.set(false);
					this.bestOf3RoundSuperiorityOnScoreboard.set(false);
				}
				this.showGoldenPointTieBreakerOnScoreboard.set(false);
				this.showSuperiorityByRounds.set(false);
			}
			if(someChange && (getCurrentMatchStatus().equals(MatchStatusId.ROUND_FINISHED) ||
					getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_MATCH) ||
					getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_START_GOLDENPOINT) ||
					getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) ||
					getCurrentMatchStatus().equals(MatchStatusId.ROUND_GOLDENPOINT_FINISHED)))
				this.currentMatchStatus.set(MatchStatusId.ROUND_PAUSED);
		}
	}

	@Override
	public final void startRound() {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("Start Round currentStatus:" + getCurrentMatchStatus().toString());
		if(getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_MATCH) ||
				getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_START_ROUND) ||
				getCurrentMatchStatus().equals(MatchStatusId.ROUND_KYESHI) ||
				getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_START_GOLDENPOINT) ||
				getCurrentMatchStatus().equals(MatchStatusId.ROUND_PAUSED)) {
			if(this.kyeShiCountdown.getWorking()) {
				this.kyeShiCountdown.stop();
				this.kyeShiCountdown.clean();
			}
			_startRound();
		}
	}

	void _initializeFirstRound() {
		if(this.currentRound.get() == 1 && ! this.matchStarted.booleanValue()) {
			sendEventToExternalEventsListeners("START_MATCH", this.currentMatchNumber,

					Integer.valueOf(this.currentRound.get()), this.currentRoundStr
							.get(),
					Long.valueOf(this.roundCountdown.getCurrentTimeMillis()),
					Long.valueOf(System.currentTimeMillis()), false, false,

					Integer.valueOf(0),
					Integer.valueOf(0),
					Integer.valueOf(0),
					Integer.valueOf(0),
					Integer.valueOf(0),
					Integer.valueOf(0), (String)null, (String)null, "", false, (Integer)null);
			updateMatchLogStartTime(Long.valueOf(System.currentTimeMillis()));
			this.matchStarted = Boolean.valueOf(true);
			this.bluePoints.set(0);
			this.blueGeneralPoints.set(0);
			this.blueTechPoints.set(0);
			this.blueRoundWins.set(0);
			this.bluePenaltiesNextRound = Integer.valueOf(0);
			this.redPoints.set(0);
			this.redGeneralPoints.set(0);
			this.redRoundWins.set(0);
			this.redTechPoints.set(0);
			this.redPenaltiesNextRound = Integer.valueOf(0);
			this.bluePARATechPoints.set(0);
			this.bluePARATotalTechPoints.set(0);
			this.redPARATechPoints.set(0);
			this.redPARATotalTechPoints.set(0);
			initializeRoundInternalCounters();
			this.showSuperiorityByRounds.set(false);
			_addMatchLogItem(Long.valueOf(System.currentTimeMillis()),
					Long.valueOf(this.roundCountdown.getCurrentTimeMillis()),
					Long.valueOf(System.currentTimeMillis()),
					getRound4MatchLog(true), getRoundStr4MatchLog(true), MatchLogItemType.START_MATCH,
					Integer.valueOf(0),
					Integer.valueOf(0), "", true,
					Integer.valueOf(0), false);
		}
	}

	void _startRound() {
		_initializeFirstRound();
		if(MatchVictoryCriteria.CONVENTIONAL.equals(this.matchVictoryCriteria))
			initializeRoundInternalCounters(this.currentRound.getValue().intValue());
		this.bestOf3WinnerLastRoundByPUN = false;
		this.roundsWinner.put(this.currentRound.getValue(), MatchWinner.TIE);
		if(this.currentRound.get() <= this.matchRounds.get()) {
			sendEventToExternalEventsListeners("START_ROUND", this.currentMatchNumber,

					Integer.valueOf(this.currentRound.get()), this.currentRoundStr
							.get(),
					Long.valueOf(this.roundCountdown.getCurrentTimeMillis()),
					Long.valueOf(System.currentTimeMillis()), false, false,

					Integer.valueOf(MatchVictoryCriteria.BESTOF3.equals(this.matchVictoryCriteria) ? 0 : this.blueGeneralPoints.get()),
					getBlueCurrentPenalties4EventsListeners(),
					Integer.valueOf(MatchVictoryCriteria.BESTOF3.equals(this.matchVictoryCriteria) ? 0 : this.redGeneralPoints.get()),
					getRedCurrentPenalties4EventsListeners(), getBlueRoundsWins(),
					getRedRoundsWins(), (String)null, (String)null, "", false, (Integer)null);
			_addMatchLogItem(Long.valueOf(System.currentTimeMillis()),
					Long.valueOf(this.roundCountdown.getCurrentTimeMillis()),
					Long.valueOf(System.currentTimeMillis()),
					getRound4MatchLog(true), getRoundStr4MatchLog(true), MatchLogItemType.START_ROUND,
					Integer.valueOf(0),
					Integer.valueOf(0), "", true,
					Integer.valueOf(0), false);
			this.currentMatchStatus.set(MatchStatusId.ROUND_WORKING);
			this.roundCountdown.play();
		} else if(this.goldenPointEnabled && ! this.goldenPointWorking) {
			this.goldenPointWorking = true;
			this.goldenPointTieBreaker = false;
			this.goldenPointTieBreakerInfo = null;
			this.goldenPointPointByPenalty = false;
			this.goldenPointPointByPunch = false;
			this.goldenPointPointByPenaltyValue = 0;
			this.bluePoints.set( - 1);
			this.bluePoints.set(0);
			this.blueLastGoldenPointPoints = Integer.valueOf(0);
			this.blueGoldenPointPunches.set(0);
			this.blueLastPointIsPenalty = false;
			this.blueGoldenPointImpacts.set(0);
			this.redPoints.set( - 1);
			this.redPoints.set(0);
			this.redLastGoldenPointPoints = Integer.valueOf(0);
			this.redGoldenPointPunches.set(0);
			this.redLastPointIsPenalty = false;
			this.redGoldenPointImpacts.set(0);
			_addMatchLogItem(Long.valueOf(System.currentTimeMillis()),
					Long.valueOf(this.roundCountdown.getCurrentTimeMillis()),
					Long.valueOf(System.currentTimeMillis()),
					getRound4MatchLog(true),
					getRoundStr4MatchLog(true), MatchLogItemType.START_ROUND,

					Integer.valueOf(0),
					Integer.valueOf(0), "", true,

					Integer.valueOf(0), false);
			TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					BaseCommonMatchWorker.this.sendEventToExternalEventsListeners(MatchLogItemType.BLUE_ADD_GAME_JEON.toString(),
							BaseCommonMatchWorker.this.currentMatchNumber,

							Integer.valueOf(BaseCommonMatchWorker.this.currentRound.get()), BaseCommonMatchWorker.this.currentRoundStr
									.get(),
							Long.valueOf(BaseCommonMatchWorker.this.roundCountdown.getCurrentTimeMillis()),
							Long.valueOf(System.currentTimeMillis()), false, false,

							Integer.valueOf(0),
							Integer.valueOf(0),
							Integer.valueOf(0),
							Integer.valueOf(0), BaseCommonMatchWorker.this
									.getBlueRoundsWins(), BaseCommonMatchWorker.this
											.getRedRoundsWins(), (String)null, (String)null, "0", false, (Integer)null);
					TimeUnit.MILLISECONDS.sleep(1L);
					BaseCommonMatchWorker.this.sendEventToExternalEventsListeners(MatchLogItemType.RED_ADD_GAME_JEON.toString(),
							BaseCommonMatchWorker.this.currentMatchNumber,

							Integer.valueOf(BaseCommonMatchWorker.this.currentRound.get()), BaseCommonMatchWorker.this.currentRoundStr
									.get(),
							Long.valueOf(BaseCommonMatchWorker.this.roundCountdown.getCurrentTimeMillis()),
							Long.valueOf(System.currentTimeMillis()), false, false,

							Integer.valueOf(0),
							Integer.valueOf(0),
							Integer.valueOf(0),
							Integer.valueOf(0), BaseCommonMatchWorker.this
									.getBlueRoundsWins(), BaseCommonMatchWorker.this
											.getRedRoundsWins(), (String)null, (String)null, "0", false, (Integer)null);
					TimeUnit.MILLISECONDS.sleep(1L);
					BaseCommonMatchWorker.this.sendEventToExternalEventsListeners("START_ROUND", BaseCommonMatchWorker.this.currentMatchNumber,

							Integer.valueOf(BaseCommonMatchWorker.this.currentRound.get()), BaseCommonMatchWorker.this.currentRoundStr
									.get(),
							Long.valueOf(BaseCommonMatchWorker.this.roundCountdown.getCurrentTimeMillis()),
							Long.valueOf(System.currentTimeMillis()), false, false,

							Integer.valueOf(MatchVictoryCriteria.BESTOF3.equals(BaseCommonMatchWorker.this.matchVictoryCriteria) ? 0
									: BaseCommonMatchWorker.this.blueGeneralPoints.get()), BaseCommonMatchWorker.this
											.getBlueCurrentPenalties4EventsListeners(),
							Integer.valueOf(MatchVictoryCriteria.BESTOF3.equals(BaseCommonMatchWorker.this.matchVictoryCriteria) ? 0
									: BaseCommonMatchWorker.this.redGeneralPoints.get()), BaseCommonMatchWorker.this
											.getRedCurrentPenalties4EventsListeners(), BaseCommonMatchWorker.this.getBlueRoundsWins(),
							BaseCommonMatchWorker.this
									.getRedRoundsWins(), (String)null, (String)null, "", false, (Integer)null);
					if(BaseCommonMatchWorker.this.rtBroadcastSocketClient.isConnected()) {
						BaseCommonMatchWorker.this.sendEventToRtBroadcast(MatchLogItemType.BLUE_ADD_GAME_JEON.toString(),
								BaseCommonMatchWorker.this.currentMatchNumber,

								Integer.valueOf(BaseCommonMatchWorker.this.currentRound.get()), BaseCommonMatchWorker.this.currentRoundStr
										.get(),
								Boolean.valueOf(BaseCommonMatchWorker.this.goldenPointWorking),
								Long.valueOf(BaseCommonMatchWorker.this.roundCountdown.getCurrentTimeMillis()),
								Long.valueOf(System.currentTimeMillis()), false, false,
								Integer.valueOf(0),
								Integer.valueOf(0),
								Integer.valueOf(0), BaseCommonMatchWorker.this
										.getBlueRoundsWins(), Integer.valueOf(0),
								Integer.valueOf(0),
								Integer.valueOf(0), BaseCommonMatchWorker.this
										.getRedRoundsWins(), Integer.valueOf(0), (String)null, (String)null, "0", (String)null);
						BaseCommonMatchWorker.this.sendEventToRtBroadcast(MatchLogItemType.RED_ADD_GAME_JEON.toString(),
								BaseCommonMatchWorker.this.currentMatchNumber,

								Integer.valueOf(BaseCommonMatchWorker.this.currentRound.get()), BaseCommonMatchWorker.this.currentRoundStr
										.get(),
								Boolean.valueOf(BaseCommonMatchWorker.this.goldenPointWorking),
								Long.valueOf(BaseCommonMatchWorker.this.roundCountdown.getCurrentTimeMillis()),
								Long.valueOf(System.currentTimeMillis()), false, false,
								Integer.valueOf(0),
								Integer.valueOf(0),
								Integer.valueOf(0), BaseCommonMatchWorker.this
										.getBlueRoundsWins(), Integer.valueOf(0),
								Integer.valueOf(0),
								Integer.valueOf(0), BaseCommonMatchWorker.this
										.getRedRoundsWins(), Integer.valueOf(0), (String)null, (String)null, "0", (String)null);
					}
					return null;
				}
			});
			this.roundCountdown.clean(this.goldenPointTimeMinutes, this.goldenPointTimeSeconds);
			this.currentMatchStatus.set(MatchStatusId.ROUND_IN_GOLDENPOINT);
			this.roundCountdown.play();
		} else {
			this.goldenPointWorking = false;
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("Match Finished!!!");
			this.currentMatchStatus.set(MatchStatusId.MATCH_FINISHED);
		}
	}

	void initializeRoundInternalCounters() {
		for(int i = 1; i <= this.matchRounds.get(); i++)
			initializeRoundInternalCounters(i);
		if(this.goldenPointEnabled)
			initializeRoundInternalCounters(this.matchRounds.get() + 1);
	}

	void initializeRoundInternalCounters(int roundNumber) {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.info("initializeRoundInternalCounters for round " + roundNumber);
		this.roundsWinner.put(Integer.valueOf(roundNumber), MatchWinner.TIE);
		this.bestOf3CurrentRoundPartialWinner.set(MatchWinner.TIE);
		this.blueRoundsPoints.put(Integer.valueOf(roundNumber), Integer.valueOf(0));
		this.blueRoundsPenalties.put(Integer.valueOf(roundNumber), Integer.valueOf(0));
		this.blueRoundsTechPoints.put(Integer.valueOf(roundNumber), Integer.valueOf(0));
		this.blueRoundsBodyPoints.put(Integer.valueOf(roundNumber), Integer.valueOf(0));
		this.blueRoundsHeadPoints.put(Integer.valueOf(roundNumber), Integer.valueOf(0));
		this.blueRoundsPunchPoints.put(Integer.valueOf(roundNumber), Integer.valueOf(0));
		this.blueRoundsHits.put(Integer.valueOf(roundNumber), Integer.valueOf(0));
		this.blueRoundsHitsProperty.set(0);
		this.redRoundsPoints.put(Integer.valueOf(roundNumber), Integer.valueOf(0));
		this.redRoundsPenalties.put(Integer.valueOf(roundNumber), Integer.valueOf(0));
		this.redRoundsTechPoints.put(Integer.valueOf(roundNumber), Integer.valueOf(0));
		this.redRoundsBodyPoints.put(Integer.valueOf(roundNumber), Integer.valueOf(0));
		this.redRoundsHeadPoints.put(Integer.valueOf(roundNumber), Integer.valueOf(0));
		this.redRoundsPunchPoints.put(Integer.valueOf(roundNumber), Integer.valueOf(0));
		this.redRoundsHits.put(Integer.valueOf(roundNumber), Integer.valueOf(0));
		this.redRoundsHitsProperty.set(0);
	}

	void clearRoundInternalCounters() {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.info("clearRoundInternalCounters");
		this.roundsWinner.clear();
		this.bestOf3CurrentRoundPartialWinner.set(MatchWinner.TIE);
		this.blueRoundsPoints.clear();
		this.blueRoundsPenalties.clear();
		this.blueRoundsTechPoints.clear();
		this.blueRoundsBodyPoints.clear();
		this.blueRoundsHeadPoints.clear();
		this.blueRoundsPunchPoints.clear();
		this.blueRoundsHits.clear();
		this.blueRoundsHitsProperty.set(0);
		this.blueR1Points.set(0);
		this.blueR2Points.set(0);
		this.blueR3Points.set(0);
		this.redRoundsPoints.clear();
		this.redRoundsPenalties.clear();
		this.redRoundsTechPoints.clear();
		this.redRoundsBodyPoints.clear();
		this.redRoundsHeadPoints.clear();
		this.redRoundsPunchPoints.clear();
		this.redRoundsHits.clear();
		this.redRoundsHitsProperty.set(0);
		this.redR1Points.set(0);
		this.redR2Points.set(0);
		this.redR3Points.set(0);
	}

	void appendToCounter(String counterName, ConcurrentHashMap<Integer, Integer> counter, Integer roundNumber, Integer value2Add) {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.info("Append " + value2Add + " to counter " + counterName + " on round " + roundNumber);
		counter.put(roundNumber, Integer.valueOf(counter.get(roundNumber).intValue() + value2Add.intValue()));
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.info("After append to counter " + counterName + " new value " + counter.get(roundNumber));
		if("BLUE_HITS".equals(counterName)) {
			this.blueRoundsHitsProperty.set(counter.get(roundNumber).intValue());
			calculateBestOf3PartialRoundWinner();
		} else if("RED_HITS".equals(counterName)) {
			this.redRoundsHitsProperty.set(counter.get(roundNumber).intValue());
			calculateBestOf3PartialRoundWinner();
		}
	}

	void moveToNextRound() {
		long newRoundTime = 0L;
		boolean resetRoundInternal = true;
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("move to next round");
		if(this.currentRound.get() < this.matchRounds.get()) {
			this.currentRound.set(this.currentRound.get() + 1);
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("moveToNextRound: New Round " + this.currentRound.get());
			this.roundCountdown.clean(this.roundTimeMinutes, this.roundTimeSeconds);
			newRoundTime = this.roundCountdown.getCurrentTimeMillis();
			if(this.currentRound.get() > 1)
				this.currentMatchStatus.set(MatchStatusId.WAITING_4_START_ROUND);
			if(MatchVictoryCriteria.BESTOF3.equals(this.matchVictoryCriteria)) {
				matchWorkerLogger.info("moveToNextRound on bestOf3... RESET POINTS round " + this.currentRound.get());
				this.blueGeneralPoints.set(0);
				this.redGeneralPoints.set(0);
				this.bluePenalties.setValue(Integer.valueOf(0));
				this.redPenalties.setValue(Integer.valueOf(0));
				this.bluePoints.set( - 1);
				this.bluePoints.set(0);
				this.redPoints.set( - 1);
				this.redPoints.set(0);
				matchWorkerLogger.info("moveToNextRound:blue and red points reset to 0");
				if(this.blueRoundsPenalties.get(Integer.valueOf(this.currentRound.get())).intValue() > 0) {
					resetRoundInternal = false;
					int newPenalties = this.blueRoundsPenalties.get(Integer.valueOf(this.currentRound.get())).intValue();
					matchWorkerLogger.info("moveToNextRound:Blue penalties " + newPenalties + " for next round ");
					this.blueRoundsPenalties.put(Integer.valueOf(this.currentRound.get()), Integer.valueOf(0));
					for(int i = 0; i < newPenalties; i++)
						addBlueGamJeom(ActionSource.MAIN_CONTROL, System.currentTimeMillis(), newRoundTime);
				}
				if(this.redRoundsPenalties.get(Integer.valueOf(this.currentRound.get())).intValue() > 0) {
					resetRoundInternal = false;
					int newPenalties = this.redRoundsPenalties.get(Integer.valueOf(this.currentRound.get())).intValue();
					matchWorkerLogger.info("moveToNextRound:Red penalties " + newPenalties + " for next round ");
					this.redRoundsPenalties.put(Integer.valueOf(this.currentRound.get()), Integer.valueOf(0));
					for(int i = 0; i < newPenalties; i++)
						addRedGamJeom(ActionSource.MAIN_CONTROL, System.currentTimeMillis(), newRoundTime);
				}
			}
			if(this.currentRound.get() > 1)
				this.currentMatchStatus.set(MatchStatusId.WAITING_4_START_ROUND);
		} else if(this.goldenPointEnabled && ! this.goldenPointWorking) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("Next Round is GoldenPoint Round");
			if(MatchVictoryCriteria.CONVENTIONAL.equals(this.matchVictoryCriteria)) {
				this.blueTotalPoints.set(this.blueGeneralPoints.get());
				this.redTotalPoints.set(this.redGeneralPoints.get());
			}
			this.currentMatchStatus.set(MatchStatusId.WAITING_4_START_GOLDENPOINT);
			this.bluePoints.set( - 1);
			this.bluePoints.set(0);
			this.blueLastGoldenPointPoints = Integer.valueOf(0);
			this.blueGoldenPointPunches.set(0);
			this.blueLastPointIsPenalty = false;
			this.redPoints.set( - 1);
			this.redPoints.set(0);
			this.redLastGoldenPointPoints = Integer.valueOf(0);
			this.redGoldenPointPunches.set(0);
			this.redLastPointIsPenalty = false;
			this.blueTotalPenalties.setValue(Integer.valueOf(this.blueTotalPenalties.get() + this.bluePenalties.get()));
			this.redTotalPenalties.setValue(Integer.valueOf(this.redTotalPenalties.get() + this.redPenalties.get()));
			this.bluePenalties.setValue(Integer.valueOf(0));
			this.redPenalties.setValue(Integer.valueOf(0));
			if(matchWorkerLogger.isDebugEnabled()) {
				matchWorkerLogger.debug("Points was resets to 0!!");
				matchWorkerLogger.debug("BlueGeneralPoints " + this.blueGeneralPoints.get() + " RedGeneralPoints " + this.redGeneralPoints.get());
			}
			this.currentRound.set(this.matchRounds.get() + 1);
			this.currentRoundStr.set("" + (this.matchRounds.get() + 1));
			this.currentMatchStatus.set(MatchStatusId.WAITING_4_START_GOLDENPOINT);
			this.roundCountdown.clean(this.goldenPointTimeMinutes, this.goldenPointTimeSeconds);
			newRoundTime = this.roundCountdown.getCurrentTimeMillis();
		}
		if(resetRoundInternal) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("CurrentRound " + this.currentRound.get() + " initialize internal round counters");
			initializeRoundInternalCounters(this.currentRound.getValue().intValue());
		} else if(matchWorkerLogger.isDebugEnabled()) {
			matchWorkerLogger.debug("Some change has made during move to next round " + this.currentRound.get()
					+ " don't initialize internal round counters");
		}
		this.doctorInRound.set(false);
		sendEventToExternalEventsListeners("MOVE_TO_NEXT_ROUND", this.currentMatchNumber,

				Integer.valueOf(this.currentRound.get()), this.currentRoundStr
						.get(),
				Long.valueOf(newRoundTime),
				Long.valueOf(System.currentTimeMillis()), false, false,

				Integer.valueOf(MatchVictoryCriteria.BESTOF3.equals(this.matchVictoryCriteria) ? 0 : this.blueGeneralPoints.get()),
				getBlueCurrentPenalties4EventsListeners(),
				Integer.valueOf(MatchVictoryCriteria.BESTOF3.equals(this.matchVictoryCriteria) ? 0 : this.redGeneralPoints.get()),
				getRedCurrentPenalties4EventsListeners(), getBlueRoundsWins(),
				getRedRoundsWins(), (String)null, (String)null, "", false, (Integer)null);
	}

	@Override
	public final void endRound() {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("do End Round step1 MatchStatus " + getCurrentMatchStatus() + " currentRound " + this.currentRound.get());
		if(getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) ||
				getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT) ||
				getCurrentMatchStatus().equals(MatchStatusId.ROUND_KYESHI) ||
				getCurrentMatchStatus().equals(MatchStatusId.ROUND_PAUSED)) {
			matchWorkerLogger.debug("End Round - roundCountdown is finished?" + this.roundCountdown.getFinished());
			if( ! this.roundCountdown.getFinished()) {
				final Map<Integer, MatchWinner> fiRoundsWinner = new HashMap<>(this.roundsWinner);
				TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

					@Override
					public Void call() throws Exception {
						TimeUnit.MILLISECONDS.sleep(10L);
						BaseCommonMatchWorker.this._addMatchLogItem(Long.valueOf(System.currentTimeMillis() + 5L),
								Long.valueOf(BaseCommonMatchWorker.this.getCurrentRoundCountdownAsMillis()),
								Long.valueOf(System.currentTimeMillis() + 5L), BaseCommonMatchWorker.this
										.getRound4MatchLog(true), BaseCommonMatchWorker.this
												.getRoundStr4MatchLog(true), MatchLogItemType.TIMEOUT,

								Integer.valueOf(0),
								Integer.valueOf(0), "", true,

								Integer.valueOf(0), false);
						if(BaseCommonMatchWorker.this.wtUDPService.isConnected()) {
							BaseCommonMatchWorker.this.wtUDPService.sendRoundCountdownChange("0:00", WtUDPService.ClockAction.END);
							BaseCommonMatchWorker.this.wtUDPService.sendWinnerPeriods(fiRoundsWinner);
						}
						return null;
					}
				});
				this.roundCountdown.stop();
				this.roundCountdown.clean(0, 0, 0);
			}
			this.roundCountdown.clean();
		}
		if( ! this.goldenPointEnabled || ! this.goldenPointWorking) {
			this.roundFinishedOpen.set(false);
			this.roundFinishedOpen.set(true);
		}
		if(MatchVictoryCriteria.BESTOF3.equals(this.matchVictoryCriteria) && this.bestOf3WinnerLastRoundByPUN
				&& this.bestOf3WinnerLastRoundByPUNWinner != null) {
			matchWorkerLogger.info("BestOf3 finailtizat per PUN guanya el " + this.bestOf3WinnerLastRoundByPUNWinner);
			setRoundPoints(this.currentRound.get(), this.blueGeneralPoints.get(), this.redGeneralPoints.get());
		} else {
			calculateCurrentRoundWinner(true, false);
		}
	}

	@Override
	public void confirmRoundEnds() {
		matchWorkerLogger.info("confirmRoundEnds");
		internalEndRound(true);
	}

	@Override
	public void confirmRoundEndsWithWinner(MatchWinner roundWinner) {
		matchWorkerLogger.info("confirmRoundEndsWithWinner roundWinner = " + roundWinner);
		setRoundWinner(this.currentRound.get(), roundWinner, false);
		internalEndRound(true);
	}

	private void internalEndRound(boolean setRoundWinner) {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("do End Round setRoundWinner = " + setRoundWinner);
		this.roundFinishedOpen.set(false);
		_addMatchLogItem(Long.valueOf(System.currentTimeMillis()),
				Long.valueOf(getCurrentRoundCountdownAsMillis()),
				Long.valueOf(System.currentTimeMillis()),
				getRound4MatchLog(true),
				getRoundStr4MatchLog(true), MatchLogItemType.END_ROUND,

				Integer.valueOf(0),
				Integer.valueOf(0), "", true,
				Integer.valueOf(0), false);
		if(setRoundWinner)
			;
		this.bestOf3RoundWithSuperiority.set(false);
		this.bestOf3RoundWithPointGap.set(false);
		this.bestOf3RoundWinnerWithPointGap = null;
		this.bestOf3UpdatingFromScoreboardEditor = Boolean.valueOf(false);
		if(getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) ||
				getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT) ||
				getCurrentMatchStatus().equals(MatchStatusId.ROUND_KYESHI) ||
				getCurrentMatchStatus().equals(MatchStatusId.ROUND_PAUSED) ||

				getCurrentMatchStatus().equals(MatchStatusId.ROUND_FINISHED)) {
			if(this.goldenPointWorking) {
				this.currentMatchStatus.set(MatchStatusId.ROUND_GOLDENPOINT_FINISHED);
			} else if( ! MatchStatusId.ROUND_FINISHED.equals(getCurrentMatchStatus())) {
				this.currentMatchStatus.set(MatchStatusId.ROUND_FINISHED);
			}
			if(MatchVictoryCriteria.BESTOF3.equals(this.matchVictoryCriteria)) {
				this.bestOf3RoundSuperiorityOnScoreboard.setValue(null);
				this.bestOf3RoundSuperiorityOnScoreboard.setValue(Boolean.valueOf(false));
				this.bestOf3RoundWithSuperiority.setValue(null);
				this.bestOf3RoundWithSuperiority.setValue(Boolean.valueOf(false));
			}
			if(MatchVictoryCriteria.CONVENTIONAL.equals(this.matchVictoryCriteria) && (this.currentRound.get() == 2 || this.pointGapAllRounds))
				if(finishMatchByDifferentialScore(false)) {
					doPauseRound();
					if( ! this.matchWinnerByPointGapNeedsConfirmation.get())
						this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
					return;
				}
			if(_hasNextRound()) {
				this.bestOf3RoundWithPointGap.set(false);
				this.restTimeCountdown.clean(this.restTimeMinutes, this.restTimeSeconds);
				this.restTimeCountdown.play();
			} else if(MatchVictoryCriteria.BESTOF3.equals(getMatchVictoryCriteria()) && MatchWinner.TIE.equals(getMatchWinner())) {
				if( ! validateIfSomeoneWinsByRound())
					validateSuperiorityInBestOf3();
			} else {
				this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
			}
		}
	}

	private BestOf3RoundSuperiority calculateBestOf3RoundWinnerSuperiority(Integer round) {
		BestOf3RoundSuperiority workingSuperiority = new BestOf3RoundSuperiority();
		int blueTurning = this.blueRoundsTechPoints.getOrDefault(round, Integer.valueOf(0)).intValue();
		int redTurning = this.redRoundsTechPoints.getOrDefault(round, Integer.valueOf(0)).intValue();
		int blueHead = this.blueRoundsHeadPoints.getOrDefault(round, Integer.valueOf(0)).intValue();
		int redHead = this.redRoundsHeadPoints.getOrDefault(round, Integer.valueOf(0)).intValue();
		int blueBody = this.blueRoundsBodyPoints.getOrDefault(round, Integer.valueOf(0)).intValue();
		int redBody = this.redRoundsBodyPoints.getOrDefault(round, Integer.valueOf(0)).intValue();
		int bluePunch = this.blueRoundsPunchPoints.getOrDefault(round, Integer.valueOf(0)).intValue();
		int redPunch = this.redRoundsPunchPoints.getOrDefault(round, Integer.valueOf(0)).intValue();
		int bluePenalties = this.blueRoundsPenalties.getOrDefault(round, Integer.valueOf(0)).intValue();
		int redPenalties = this.redRoundsPenalties.getOrDefault(round, Integer.valueOf(0)).intValue();
		int blueHits = this.blueRoundsHits.getOrDefault(round, Integer.valueOf(0)).intValue();
		int redHits = this.redRoundsHits.getOrDefault(round, Integer.valueOf(0)).intValue();
		workingSuperiority.setBlueTurningSpinning(Integer.valueOf(blueTurning));
		workingSuperiority.setRedTurningSpinning(Integer.valueOf(redTurning));
		workingSuperiority.setBlueHits(Integer.valueOf(blueHits));
		workingSuperiority.setRedHits(Integer.valueOf(redHits));
		workingSuperiority.setBlueTech(" - ");
		workingSuperiority.setRedTech(" - ");
		if(matchWorkerLogger.isDebugEnabled()) {
			matchWorkerLogger.debug("BestOf3: Round " + round + " superiority params:");
			matchWorkerLogger.debug("\t\tBLUE TurningSpinning:" + blueTurning + ":Head:" + blueHead + ":Body:" + blueBody + ":Punch:" + bluePunch
					+ ":GJ:" + bluePenalties + ":Hits:" + blueHits);
			matchWorkerLogger.debug("\t\tRED TurningSpinning:" + redTurning + ":Head:" + redHead + ":Body:" + redBody + ":Punch:" + redPunch + ":GJ:"
					+ redPenalties + ":Hits:" + redHits);
		}
		if(blueTurning > redTurning) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("BestOf3: Round " + round + " Winner BLUE by TURNING SPINNING");
			workingSuperiority.setRoundWinner(MatchWinner.BLUE);
		} else if(redTurning > blueTurning) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("Round " + round + " Winner RED by TURNING SPINNING");
			workingSuperiority.setRoundWinner(MatchWinner.RED);
		} else if(blueHead > redHead) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("Round " + round + " Winner BLUE by TECH HEAD");
			workingSuperiority.setRoundWinner(MatchWinner.BLUE);
			workingSuperiority.setBlueTech(blueHead + " - H");
			workingSuperiority.setRedTech(redHead + " - H");
		} else if(redHead > blueHead) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("Round " + round + " Winner RED by TECH HEAD");
			workingSuperiority.setRoundWinner(MatchWinner.RED);
			workingSuperiority.setBlueTech(blueHead + " - H");
			workingSuperiority.setRedTech(redHead + " - H");
		} else if(blueBody > redBody) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("Round " + round + " Winner BLUE by TECH BODY");
			workingSuperiority.setRoundWinner(MatchWinner.BLUE);
			workingSuperiority.setBlueTech(blueBody + " - B");
			workingSuperiority.setRedTech(redBody + " - B");
		} else if(redBody > blueBody) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("Round " + round + " Winner RED by TECH BODY");
			workingSuperiority.setRoundWinner(MatchWinner.RED);
			workingSuperiority.setBlueTech(blueBody + " - B");
			workingSuperiority.setRedTech(redBody + " - B");
		} else if(bluePunch > redPunch) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("Round " + round + " Winner BLUE by TECH PUNCH");
			workingSuperiority.setRoundWinner(MatchWinner.BLUE);
			workingSuperiority.setBlueTech(bluePunch + " - P");
			workingSuperiority.setRedTech(redPunch + " - P");
		} else if(redPunch > bluePunch) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("Round " + round + " Winner RED by TECH PUNCH");
			workingSuperiority.setRoundWinner(MatchWinner.RED);
			workingSuperiority.setBlueTech(bluePunch + " - P");
			workingSuperiority.setRedTech(redPunch + " - P");
		} else if(redPenalties > bluePenalties) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("Round " + round + " Winner BLUE by TECH GAMJEOM");
			workingSuperiority.setRoundWinner(MatchWinner.BLUE);
			workingSuperiority.setBlueTech(redPenalties + " - GJ");
			workingSuperiority.setRedTech(bluePenalties + " - GJ");
		} else if(bluePenalties > redPenalties) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("Round " + round + " Winner RED by TECH GAMJEOM");
			workingSuperiority.setRoundWinner(MatchWinner.RED);
			workingSuperiority.setBlueTech(redPenalties + " - GJ");
			workingSuperiority.setRedTech(bluePenalties + " - GJ");
		} else if(blueHits > redHits) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("Round " + round + " Winner BLUE by HITS");
			workingSuperiority.setRoundWinner(MatchWinner.BLUE);
		} else if(redHits > blueHits) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("Round " + round + " Winner RED by HITS");
			workingSuperiority.setRoundWinner(MatchWinner.RED);
		}
		if(MatchWinner.TIE.equals(workingSuperiority.getRoundWinner()))
			if(blueHead > 1 || redHead > 1) {
				workingSuperiority.setBlueTech(blueHead + " - H");
				workingSuperiority.setRedTech(redHead + " - H");
			} else if(blueBody > 1 || redBody > 1) {
				workingSuperiority.setBlueTech(blueBody + " - B");
				workingSuperiority.setRedTech(redBody + " - B");
			} else if(bluePunch > 1 || redPunch > 1) {
				workingSuperiority.setBlueTech(bluePunch + " - P");
				workingSuperiority.setRedTech(redPunch + " - P");
			} else if(bluePenalties > 1 || redPenalties > 1) {
				workingSuperiority.setBlueTech(bluePenalties + " - GJ");
				workingSuperiority.setRedTech(redPenalties + " - GJ");
			}
		return workingSuperiority;
	}

	private void calculateCurrentRoundWinner(boolean updateRoundWinner, boolean sendToExternals) {
		matchWorkerLogger.info("calculateCurrentRoundWinner on " + this.matchVictoryCriteria + " currentRound " + getCurrentRound());
		if(MatchVictoryCriteria.BESTOF3.equals(this.matchVictoryCriteria)) {
			matchWorkerLogger.info("calculateCurrentRoundWinner: Set TOTAL POINTS and PENALTIES");
			this.blueTotalPoints.set(this.blueTotalPoints.get() + this.blueGeneralPoints.get());
			this.redTotalPoints.set(this.redTotalPoints.get() + this.redGeneralPoints.get());
			this.blueTotalPenalties.setValue(Integer.valueOf(this.blueTotalPenalties.get() + this.bluePenalties.get()));
			this.redTotalPenalties.setValue(Integer.valueOf(this.redTotalPenalties.get() + this.redPenalties.get()));
			matchWorkerLogger.info("calculateCurrentRoundWinner: After ADD TOTAL POINTS BLUE " + this.blueTotalPoints.get() + " ("
					+ this.blueTotalPenalties.get() + ") RED " + this.redTotalPoints.get() + " (" + this.redTotalPenalties.get() + ")");
		}
		int blueRoundPoints = 0;
		if(this.blueRoundsPoints.containsKey(Integer.valueOf(this.currentRound.get())))
			blueRoundPoints = this.blueRoundsPoints.get(Integer.valueOf(this.currentRound.get())).intValue();
		int redRoundPoints = 0;
		if(this.redRoundsPoints.containsKey(Integer.valueOf(this.currentRound.get())))
			redRoundPoints = this.redRoundsPoints.get(Integer.valueOf(this.currentRound.get())).intValue();
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("In Round " + this.currentRound
					.get() + " Points. Blue " + blueRoundPoints + " Red " + redRoundPoints);
		if(updateRoundWinner) {
			setCurrentRoundWinner(blueRoundPoints, redRoundPoints, sendToExternals);
		} else {
			matchWorkerLogger.debug("Round winner set previously. Not updated");
		}
		if(MatchVictoryCriteria.BESTOF3.equals(getMatchVictoryCriteria())) {
			MatchWinner currentRoundWinner = this.roundsWinner.get(Integer.valueOf(this.currentRound.get()));
			matchWorkerLogger.info("BestOf3-Match end round " + this.currentRound.get() + " winner " + currentRoundWinner);
			this.bestOf3RoundWithSuperiority.set(false);
			this.bestOf3RoundSuperiorityOnScoreboard.set(false);
			if(MatchWinner.TIE.equals(currentRoundWinner)) {
				matchWorkerLogger.info("BestOf3-Match round with TIE go to validate superiority!");
				this.bestOf3RoundSuperiority = calculateBestOf3RoundWinnerSuperiority(Integer.valueOf(this.currentRound.get()));
				setRoundWinner(this.currentRound.get(), this.bestOf3RoundSuperiority.getRoundWinner(), sendToExternals);
				this.bestOf3RoundWithSuperiority.set(true);
				this.bestOf3RoundSuperiorityOnScoreboard.set(false);
			}
		}
		refreshMatchLogWinners();
	}

	private void setCurrentRoundWinner(int blueRoundPoints, int redRoundPoints, boolean sendToExternals) {
		matchWorkerLogger.info("setCurrentRoundWinner: round " + this.currentRound.get() + " blue:" + blueRoundPoints + " - red:" + redRoundPoints);
		setRoundWinner(this.currentRound.get(), blueRoundPoints, redRoundPoints, sendToExternals);
	}

	private void changeRoundWinner(int round2Change) {
		int blueRoundPoints = 0;
		if(this.blueRoundsPoints.containsKey(Integer.valueOf(round2Change)))
			blueRoundPoints = this.blueRoundsPoints.get(Integer.valueOf(round2Change)).intValue();
		int redRoundPoints = 0;
		if(this.redRoundsPoints.containsKey(Integer.valueOf(round2Change)))
			redRoundPoints = this.redRoundsPoints.get(Integer.valueOf(round2Change)).intValue();
		setRoundWinner(round2Change, blueRoundPoints, redRoundPoints, false);
		this.roundsWinnerChanges.setValue(null);
		this.roundsWinnerChanges.setValue(Boolean.valueOf(true));
		if(this.restTimeCountdown.getWorking()) {
			this.restTimeCountdown.stop();
			this.restTimeCountdown.clean(this.restTimeMinutes, this.restTimeSeconds);
			endRound();
		}
	}

	private void setRoundWinner(int round, int blueRoundPoints, int redRoundPoints, boolean sendToExternals) {
		matchWorkerLogger.info("setRoundWinner round: " + round + ";blue:" + blueRoundPoints + ";red: " + redRoundPoints + ";sendToExternals?"
				+ sendToExternals);
		this.roundsWinner.put(Integer.valueOf(round), MatchWinner.TIE);
		setRoundPoints(round, blueRoundPoints, redRoundPoints);
		if(redRoundPoints > blueRoundPoints) {
			setRoundWinner(round, MatchWinner.RED, sendToExternals);
		} else if(blueRoundPoints > redRoundPoints) {
			setRoundWinner(round, MatchWinner.BLUE, sendToExternals);
		}
		matchWorkerLogger.info("SET ROUND WINNER " + this.roundsWinner.get(Integer.valueOf(round)) + " TO ROUND " + round + "(" + blueRoundPoints
				+ "-" + redRoundPoints + ")");
	}

	private void setRoundPoints(int round, int blueRoundPoints, int redRoundPoints) {
		if(MatchVictoryCriteria.BESTOF3.equals(this.matchVictoryCriteria))
			switch(round) {
				case 1:
					this.blueR1Points.set(blueRoundPoints);
					this.redR1Points.set(redRoundPoints);
					break;
				case 2:
					this.blueR2Points.set(blueRoundPoints);
					this.redR2Points.set(redRoundPoints);
					break;
				case 3:
					this.blueR3Points.set(blueRoundPoints);
					this.redR3Points.set(redRoundPoints);
					break;
			}
	}

	private void setRoundWinner(int round, MatchWinner roundWinner, boolean sendToExternals) {
		matchWorkerLogger.info("setRoundWinner:WINNER " + roundWinner + " TO ROUND " + round);
		this.roundsWinner.put(Integer.valueOf(round), roundWinner);
		this.blueRoundWins.set(0);
		this.redRoundWins.set(0);
		for(Map.Entry<Integer, MatchWinner> entry : this.roundsWinner.entrySet()) {
			if(entry.getKey().intValue() <= round) {
				if(MatchWinner.BLUE.equals(entry.getValue())) {
					this.blueRoundWins.set(this.blueRoundWins.get() + 1);
					continue;
				}
				if(MatchWinner.RED.equals(entry.getValue()))
					this.redRoundWins.set(this.redRoundWins.get() + 1);
			}
		}
		this.roundsWinnerChanges.set(true);
		this.roundsWinnerChanges.set(false);
		if(sendToExternals)
			;
	}

	private void sendRoundWinnerToWtUdp() {
		if(this.wtUDPService.isConnected()) {
			matchWorkerLogger.info("sendRoundWinnerToWtUdp -- ");
			final Map<Integer, MatchWinner> fiRoundsWinner = new HashMap<>(this.roundsWinner);
			TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					BaseCommonMatchWorker.this.wtUDPService.sendWinnerPeriods(fiRoundsWinner);
					return null;
				}
			});
		}
	}

	@Override
	public final void goNextRound() {
		matchWorkerLogger.info("Go next round");
		_addMatchLogItem(Long.valueOf(System.currentTimeMillis()),
				Long.valueOf(getCurrentRoundCountdownAsMillis()),
				Long.valueOf(System.currentTimeMillis()),
				getRound4MatchLog(true),
				getRoundStr4MatchLog(true), MatchLogItemType.NEXT_ROUND,

				Integer.valueOf(0),
				Integer.valueOf(0), "", true,

				Integer.valueOf(0), false);
		if(this.restTimeCountdown.getWorking())
			this.restTimeCountdown.stop();
		this.restTimeCountdown.clean();
		if(_hasNextRound())
			moveToNextRound();
	}

	@Override
	public final void doPauseRound() {
		if(getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) ||
				getCurrentMatchStatus().equals(MatchStatusId.NETWORK_ERROR) ||
				getCurrentMatchStatus().equals(MatchStatusId.ROUND_KYESHI) ||

				getCurrentMatchStatus().equals(MatchStatusId.ROUND_PARA_TIMEOUT_WORKING) ||
				getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT)) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("do Pause Round");
			if(getCurrentMatchStatus().equals(MatchStatusId.ROUND_KYESHI) && MatchStatusId.NETWORK_ERROR
					.equals(this.matchStatusBeforeKyeShi))
				this.currentMatchStatus.set(MatchStatusId.NETWORK_ERROR);
			if(getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) ||
					getCurrentMatchStatus().equals(MatchStatusId.ROUND_KYESHI) ||

					getCurrentMatchStatus().equals(MatchStatusId.ROUND_PARA_TIMEOUT_WORKING) ||
					getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT))
				this.currentMatchStatus.set(MatchStatusId.ROUND_PAUSED);
			if(this.matchStarted.booleanValue())
				_addMatchLogItem(Long.valueOf(System.currentTimeMillis() + 10L),
						Long.valueOf(getCurrentRoundCountdownAsMillis()),
						Long.valueOf(System.currentTimeMillis() + 10L),
						getRound4MatchLog(true),
						getRoundStr4MatchLog(true), MatchLogItemType.TIMEOUT,

						Integer.valueOf(0),
						Integer.valueOf(0), "", true,

						Integer.valueOf(0), false);
			this.roundCountdown.stop();
			this.kyeShiCountdown.stop();
			this.restTimeCountdown.stop();
		}
	}

	@Override
	public void doResumeRound() {
		if(getCurrentMatchStatus().equals(MatchStatusId.ROUND_PAUSED) ||
				getCurrentMatchStatus().equals(MatchStatusId.ROUND_KYESHI) ||

				getCurrentMatchStatus().equals(MatchStatusId.ROUND_PARA_TIMEOUT_WORKING) ||
				getCurrentMatchStatus().equals(MatchStatusId.NETWORK_ERROR)) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("Resume round");
			if(isDoctorInRound())
				this.doctorInRound.set(false);
			if( ! this.goldenPointWorking) {
				this.currentMatchStatus.set(MatchStatusId.ROUND_WORKING);
			} else {
				this.currentMatchStatus.set(MatchStatusId.ROUND_IN_GOLDENPOINT);
			}
			if(this.kyeShiCountdown.getWorking())
				this.kyeShiCountdown.stop();
			_addMatchLogItem(Long.valueOf(System.currentTimeMillis()),
					Long.valueOf(getCurrentRoundCountdownAsMillis()),
					Long.valueOf(System.currentTimeMillis()),
					getRound4MatchLog(true),
					getRoundStr4MatchLog(true), MatchLogItemType.RESUME,

					Integer.valueOf(0),
					Integer.valueOf(0), "", true,
					Integer.valueOf(0), false);
			this.kyeShiCountdown.clean();
			this.roundCountdown.play();
		}
	}

	@Override
	public final void doKyeShiInRound() {
		if(getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) ||
				getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT) ||
				getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_MATCH) ||
				getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_START_ROUND) ||

				getCurrentMatchStatus().equals(MatchStatusId.ROUND_PAUSED) ||
				getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_START_GOLDENPOINT) ||

				getCurrentMatchStatus().equals(MatchStatusId.NETWORK_ERROR)) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("Kye Shi");
			if(MatchStatusId.WAITING_4_MATCH.equals(getCurrentMatchStatus()))
				_initializeFirstRound();
			this.kyeShiCountdown.clean(this.kyeShiTimeMinutes, this.kyeShiTimeSeconds);
			this.matchStatusBeforeKyeShi = getCurrentMatchStatus();
			this.currentMatchStatus.set(MatchStatusId.ROUND_KYESHI);
			this.kyeShiCountdown.play();
			_addMatchLogItem(Long.valueOf(System.currentTimeMillis()),
					Long.valueOf(getCurrentRoundCountdownAsMillis()),
					Long.valueOf(System.currentTimeMillis()),
					getRound4MatchLog(true),
					getRoundStr4MatchLog(true), MatchLogItemType.KYE_SHI,

					Integer.valueOf(0),
					Integer.valueOf(0), "", true,

					Integer.valueOf(0), false);
			this.roundCountdown.stop();
		}
	}

	@Override
	public void doEndKyeShiIndRound() {
		if(getCurrentMatchStatus().equals(MatchStatusId.ROUND_KYESHI)) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("End Kye Shi");
			this.kyeShiCountdown.stop();
			this.kyeShiCountdown.clean(this.kyeShiTimeMinutes, this.kyeShiTimeSeconds);
			this.currentMatchStatus.set(this.matchStatusBeforeKyeShi);
		}
	}

	@Override
	public void callDoctorInRound() {
		if(MatchStatusId.ROUND_WORKING.equals(getCurrentMatchStatus()) || MatchStatusId.ROUND_IN_GOLDENPOINT
				.equals(getCurrentMatchStatus()))
			doPauseRound();
		if((getCurrentMatchStatus().equals(MatchStatusId.ROUND_KYESHI) ||
				getCurrentMatchStatus().equals(MatchStatusId.ROUND_PAUSED) ||
				getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_MATCH) ||
				getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_START_ROUND) ||
				getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_START_GOLDENPOINT)) &&
				! this.doctorInRound.get()) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("Call Doctor");
			this.doctorInRound.set(Boolean.TRUE.booleanValue());
			_addMatchLogItem(Long.valueOf(System.currentTimeMillis()),
					Long.valueOf(getCurrentRoundCountdownAsMillis()),
					Long.valueOf(System.currentTimeMillis()),
					getRound4MatchLog(true),
					getRoundStr4MatchLog(true), MatchLogItemType.DOCTOR,

					Integer.valueOf(0),
					Integer.valueOf(0), "", true,
					Integer.valueOf(0), false);
		}
	}

	@Override
	public void doctorQuitInRound() {
		if((getCurrentMatchStatus().equals(MatchStatusId.ROUND_KYESHI) ||
				getCurrentMatchStatus().equals(MatchStatusId.ROUND_PAUSED) ||
				getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_MATCH) ||
				getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_START_ROUND) ||
				getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_START_GOLDENPOINT)) && this.doctorInRound
						.get()) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("Doctor Quit");
			this.doctorInRound.set(Boolean.FALSE.booleanValue());
			_addMatchLogItem(Long.valueOf(System.currentTimeMillis()),
					Long.valueOf(getCurrentRoundCountdownAsMillis()),
					Long.valueOf(System.currentTimeMillis()),
					getRound4MatchLog(true),
					getRoundStr4MatchLog(true), MatchLogItemType.DOCTOR_QUIT,

					Integer.valueOf(0),
					Integer.valueOf(0), "", true,
					Integer.valueOf(0), false);
		}
	}

	@Override
	public ReadOnlyBooleanProperty doctorInRoundProperty() {
		return this.doctorInRound;
	}

	@Override
	public boolean isDoctorInRound() {
		return this.doctorInRound.get();
	}

	@Override
	public BooleanProperty backupSystemEnabled() {
		return this.backupSystemEnabled;
	}

	@Override
	public boolean isBackupSystemEnabled() {
		return this.backupSystemEnabled.get();
	}

	@Override
	public boolean simulateIfApplyingChangesMatchHasNextRound(Collection<ScoreboardEditAction> scoreboardEditActions) {
		boolean matchFinished = true;
		Integer blueGeneralPoints = Integer.valueOf(this.blueGeneralPoints.get());
		Integer bluePoints = Integer.valueOf(this.bluePoints.get());
		Integer bluePenalties = Integer.valueOf(this.bluePenalties.get());
		Integer blueGoldenPointPenalties = Integer.valueOf(this.blueGoldenPointPenalties.get());
		Integer redGeneralPoints = Integer.valueOf(this.redGeneralPoints.get());
		Integer redPoints = Integer.valueOf(this.redPoints.get());
		Integer redPenalties = Integer.valueOf(this.redPenalties.get());
		Integer redGoldenPointPenalties = Integer.valueOf(this.redGoldenPointPenalties.get());
		for(ScoreboardEditAction scoreboardEditAction : scoreboardEditActions) {
			Integer integer1;
			Integer integer2;
			switch(scoreboardEditAction.getAction()) {
				case BODY_POINT:
				case BODY_TECH_POINT:
				case HEAD_POINT:
				case HEAD_TECH_POINT: // fagu pas certain
				case PUNCH_POINT: // fagu pas certain
					if(scoreboardEditAction.isBlue()) {
						blueGeneralPoints = Integer.valueOf(blueGeneralPoints.intValue() + scoreboardEditAction.getValue());
						bluePoints = Integer.valueOf(bluePoints.intValue() + scoreboardEditAction.getValue());
						continue;
					}
					redGeneralPoints = Integer.valueOf(redGeneralPoints.intValue() + scoreboardEditAction.getValue());
					redPoints = Integer.valueOf(redPoints.intValue() + scoreboardEditAction.getValue());
				case KYONG_GO: // fagu pas certain
					if(scoreboardEditAction.isBlue()) {
						if(isGoldenPointWorking()) {
							if(scoreboardEditAction.getValue() > 0) {
								Integer integer7 = blueGoldenPointPenalties, integer8 = blueGoldenPointPenalties = Integer.valueOf(
										blueGoldenPointPenalties.intValue() + 1);
								integer7 = redGeneralPoints;
								integer8 = redGeneralPoints = Integer.valueOf(redGeneralPoints.intValue() + 1);
								continue;
							}
							Integer integer5 = blueGoldenPointPenalties, integer6 = blueGoldenPointPenalties = Integer.valueOf(
									blueGoldenPointPenalties.intValue() - 1);
							integer5 = redGeneralPoints;
							integer6 = redGeneralPoints = Integer.valueOf(redGeneralPoints.intValue() - 1);
							continue;
						}
						if(scoreboardEditAction.getValue() > 0) {
							Integer integer5 = bluePenalties, integer6 = bluePenalties = Integer.valueOf(bluePenalties.intValue() + 1);
							integer5 = redGeneralPoints;
							integer6 = redGeneralPoints = Integer.valueOf(redGeneralPoints.intValue() + 1);
							continue;
						}
						Integer integer3 = bluePenalties, integer4 = bluePenalties = Integer.valueOf(bluePenalties.intValue() - 1);
						integer3 = redGeneralPoints;
						integer4 = redGeneralPoints = Integer.valueOf(redGeneralPoints.intValue() - 1);
						continue;
					}
					if(isGoldenPointWorking()) {
						if(scoreboardEditAction.getValue() > 0) {
							Integer integer5 = redGoldenPointPenalties, integer6 = redGoldenPointPenalties = Integer.valueOf(redGoldenPointPenalties
									.intValue() + 1);
							integer5 = blueGeneralPoints;
							integer6 = blueGeneralPoints = Integer.valueOf(blueGeneralPoints.intValue() + 1);
							continue;
						}
						Integer integer3 = redGoldenPointPenalties, integer4 = redGoldenPointPenalties = Integer.valueOf(redGoldenPointPenalties
								.intValue() - 1);
						integer3 = blueGeneralPoints;
						integer4 = blueGeneralPoints = Integer.valueOf(blueGeneralPoints.intValue() - 1);
						continue;
					}
					if(scoreboardEditAction.getValue() > 0) {
						Integer integer3 = redPenalties, integer4 = redPenalties = Integer.valueOf(redPenalties.intValue() + 1);
						integer3 = blueGeneralPoints;
						integer4 = blueGeneralPoints = Integer.valueOf(blueGeneralPoints.intValue() + 1);
						continue;
					}
					integer1 = redPenalties;
					integer2 = redPenalties = Integer.valueOf(redPenalties.intValue() - 1);
					integer1 = blueGeneralPoints;
					integer2 = blueGeneralPoints = Integer.valueOf(blueGeneralPoints.intValue() - 1);
			}
		}
		if(matchWorkerLogger.isDebugEnabled()) {
			matchWorkerLogger.info("Simulate Applying Changes:");
			matchWorkerLogger.info("BLUE " + blueGeneralPoints + " generalPoints, " + bluePoints + " points," + bluePenalties + " penalties, "
					+ blueGoldenPointPenalties + " goldenPointPenalties");
			matchWorkerLogger.info("RED " + redGeneralPoints + " generalPoints, " + redPoints + " points," + redPenalties + " penalties, "
					+ redGoldenPointPenalties + " goldenPointPenalties");
		}
		matchFinished = validateIfMatchFinish(blueGeneralPoints,
				isGoldenPointWorking() ? blueGoldenPointPenalties : bluePenalties, bluePoints, redGeneralPoints,
				isGoldenPointWorking() ? redGoldenPointPenalties : redPenalties, redPoints, new MutableBoolean(Boolean.FALSE),
				new SimpleObjectProperty(), new SimpleObjectProperty());
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.info("Result of validateIfMatchFinish " + matchFinished);
		boolean matchHasNextRound = false;
		if(MatchVictoryCriteria.CONVENTIONAL.equals(this.matchVictoryCriteria))
			if(this.currentRound.get() < this.matchRounds.get()) {
				matchHasNextRound = true;
			} else if(this.currentRound.get() == this.matchRounds.get() && blueGeneralPoints
					.equals(redGeneralPoints) && this.goldenPointEnabled && ! this.goldenPointWorking) {
				matchHasNextRound = true;
			}
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.info("Result of matchHasNextRound " + matchHasNextRound);
		return ( ! matchFinished && matchHasNextRound);
	}

	@Override
	public void enableChangeMatchStatusToTimeoutOnScoreboardChanges() {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("Enable Change MatchStatus To Timeout On ScoreboardChanges");
		this.changeMatchStatusToTimeoutOnScoreboardChanges = true;
	}

	@Override
	public void disableChangeMatchStatusToTimeoutOnScoreboardChanges() {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("Disable Change MatchStatus To Timeout On ScoreboardChanges");
		this.changeMatchStatusToTimeoutOnScoreboardChanges = false;
	}

	@Override
	public boolean isChangeMatchStatusToTimeoutOnScoreboardChanges() {
		return this.changeMatchStatusToTimeoutOnScoreboardChanges;
	}

	boolean _hasNextRound() {
		matchWorkerLogger.info("_hasNextRound - matchVictoryCriteria: " + this.matchVictoryCriteria.toString());
		if(MatchVictoryCriteria.CONVENTIONAL.equals(this.matchVictoryCriteria)) {
			if(this.currentRound.get() < this.matchRounds.get())
				return true;
			if(this.currentRound.get() == this.matchRounds.get() && this.blueGeneralPoints
					.get() == this.redGeneralPoints.get() && this.goldenPointEnabled && ! this.goldenPointWorking)
				return true;
			if(this.blueGeneralPoints.get() > this.redGeneralPoints.get()) {
				this.matchFinalDecission.set(FinalDecision.PTF);
				this.matchWinner.set(MatchWinner.BLUE);
				this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
			} else if(this.redGeneralPoints.get() > this.blueGeneralPoints.get()) {
				this.matchFinalDecission.set(FinalDecision.PTF);
				this.matchWinner.set(MatchWinner.RED);
				this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
			}
		} else {
			if(this.currentRound.get() < this.matchRounds.get())
				return ! validateIfSomeoneWinsByRound();
			if(this.goldenPointEnabled && ! this.goldenPointWorking)
				return ! validateIfSomeoneWinsByRound();
		}
		return false;
	}

	boolean validateIfSomeoneWinsByRound() {
		int midRounds = this.matchRounds.get() / 2;
		matchWorkerLogger.info("Mid rounds ->" + midRounds + " rounds->" + this.matchRounds.get());
		int blueWins = 0;
		int redWins = 0;
		for(int round = 1; round <= this.matchRounds.get(); round++) {
			MatchWinner rWinner = getRoundWinner(Integer.valueOf(round));
			if(MatchWinner.BLUE.equals(rWinner)) {
				blueWins++;
			} else if(MatchWinner.RED.equals(rWinner)) {
				redWins++;
			}
		}
		matchWorkerLogger.info("Validate if someone wons more rounds");
		if(blueWins > midRounds || (this.currentRound
				.get() == this.matchRounds.get() && blueWins > redWins)) {
			this.matchFinalDecission.set(FinalDecision.PTF);
			this.matchWinner.set(MatchWinner.BLUE);
			this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
			return true;
		}
		if(redWins > midRounds || (this.currentRound
				.get() == this.matchRounds.get() && redWins > blueWins)) {
			this.matchFinalDecission.set(FinalDecision.PTF);
			this.matchWinner.set(MatchWinner.RED);
			this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
			return true;
		}
		return false;
	}

	@Override
	public void cancelGoldenPointHit() {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("Do cancel GoldenPoint Hit");
		_addMatchLogItem(Long.valueOf(System.currentTimeMillis()),
				Long.valueOf(getCurrentRoundCountdownAsMillis()),
				Long.valueOf(System.currentTimeMillis()),
				getRound4MatchLog(true),
				getRoundStr4MatchLog(true), MatchLogItemType.CANCEL_GOLDENPOINT_POINT,

				Integer.valueOf(0),
				Integer.valueOf(0), "", true,

				Integer.valueOf(0), true);
		if(this.goldenPointPointByPenalty) {
			if(MatchWinner.BLUE.equals(this.matchWinner.get())) {
				this.redGoldenPointPenalties.set(this.redGoldenPointPenalties.get() - this.goldenPointPointByPenaltyValue);
			} else {
				this.blueGoldenPointPenalties.set(this.blueGoldenPointPenalties.get() - this.goldenPointPointByPenaltyValue);
			}
			this.blueGeneralPoints.set(this.bluePoints.get() + ((this.gamJeomShowPointsOnGoldenPoint.booleanValue() || this.redGoldenPointPenalties
					.get() > 1) ? this.redGoldenPointPenalties.get() : 0));
			this.redGeneralPoints.set(this.redPoints.get() + ((this.gamJeomShowPointsOnGoldenPoint.booleanValue() || this.blueGoldenPointPenalties
					.get() > 1) ? this.blueGoldenPointPenalties.get() : 0));
		} else {
			fireRemoveGoldenPointNearMissHit(MatchWinner.BLUE.equals(this.matchWinner.get()));
			if(this.goldenPointPointByPunch)
				if(MatchWinner.BLUE.equals(this.matchWinner.get())) {
					this.blueGoldenPointPunches.set(this.blueGoldenPointPunches.get() - 1);
				} else {
					this.redGoldenPointPunches.set(this.redGoldenPointPunches.get() - 1);
				}
			if(MatchWinner.BLUE.equals(this.matchWinner.get())) {
				this.bluePoints.set(this.bluePoints.getValue().intValue() - this.blueLastGoldenPointPoints.intValue());
				this.blueLastGoldenPointPoints = Integer.valueOf(0);
			} else if(MatchWinner.RED.equals(this.matchWinner.get())) {
				this.redPoints.set(this.redPoints.getValue().intValue() - this.redLastGoldenPointPoints.intValue());
				this.redLastGoldenPointPoints = Integer.valueOf(0);
			}
			this.blueGeneralPoints.set(this.bluePoints.get() + ((this.gamJeomShowPointsOnGoldenPoint.booleanValue() || this.redGoldenPointPenalties
					.get() > 1) ? this.redGoldenPointPenalties.get() : 0));
			this.redGeneralPoints.set(this.redPoints.get() + ((this.gamJeomShowPointsOnGoldenPoint.booleanValue() || this.blueGoldenPointPenalties
					.get() > 1) ? this.blueGoldenPointPenalties.get() : 0));
		}
		this.goldenPointPointByPenalty = false;
		this.goldenPointPointByPunch = false;
		this.goldenPointPointByPenaltyValue = 0;
		this.currentMatchStatus.set(MatchStatusId.ROUND_PAUSED);
	}

	@Override
	public void addBlueHeadPoint(int points2Add, ActionSource actionSource, long eventTimestamp, long roundTimestamp, int prevSensorHitValue) {
		if(this.bluePoints.get() + points2Add < 0)
			return;
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("add BLUE HEAD points " + points2Add + " from " + actionSource.toString());
		if(actionSource.equals(ActionSource.SCOREBOARD_EDITOR) && MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION
				.equals(getCurrentMatchStatus()) && this.changeMatchStatusToTimeoutOnScoreboardChanges)
			this.currentMatchStatus.set(MatchStatusId.ROUND_PAUSED);
		TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				BaseCommonMatchWorker.this.getSoundPlayerService().playSoundWhenHeadHit();
				return null;
			}
		});
		appendToCounter("BLUE_HEAD", this.blueRoundsHeadPoints, this.currentRound.getValue(), Integer.valueOf((points2Add > 0) ? 1 : - 1));
		this.bluePoints.set(this.bluePoints.get() + points2Add);
		if(this.goldenPointWorking && points2Add > 0)
			this.blueLastGoldenPointPoints = Integer.valueOf(points2Add);
		_addMatchLogItem(Long.valueOf(eventTimestamp),
				Long.valueOf(roundTimestamp),
				Long.valueOf(System.currentTimeMillis()),
				getRound4MatchLog(false),
				getRoundStr4MatchLog(false), MatchLogItemType.BLUE_HEAD_POINT,

				Integer.valueOf(points2Add),
				Integer.valueOf(0), actionSource
						.toString(), true,
				Integer.valueOf(prevSensorHitValue), actionSource
						.equals(ActionSource.SCOREBOARD_EDITOR));
		workWithScoreboardEditorAction(actionSource, eventTimestamp, points2Add, true, HitEventType.HEAD, false);
	}

	@Override
	public void addBlueHeadTechPoint(int points2Add, ActionSource actionSource, long eventTimestamp, long roundTimestamp) {
		if(this.bluePoints.get() + points2Add < 0)
			return;
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("add BLUE HEAD TECH points " + points2Add + " from " + actionSource.toString());
		if(actionSource.equals(ActionSource.SCOREBOARD_EDITOR) && MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION
				.equals(getCurrentMatchStatus()) && this.changeMatchStatusToTimeoutOnScoreboardChanges)
			this.currentMatchStatus.set(MatchStatusId.ROUND_PAUSED);
		TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				BaseCommonMatchWorker.this.getSoundPlayerService().playSoundWhenHeadHit();
				return null;
			}
		});
		int points4Work = (points2Add > 0) ? (points2Add + this.headPoints) : (points2Add - this.headPoints);
		appendToCounter("BLUE_HEAD_TECH", this.blueRoundsTechPoints, this.currentRound.getValue(), Integer.valueOf(points4Work));
		appendToCounter("BLUE_HEAD", this.blueRoundsHeadPoints, this.currentRound.getValue(), Integer.valueOf( - 1));
		this.blueTechPoints.set(this.blueTechPoints.get() + points4Work);
		this.bluePoints.set(this.bluePoints.get() + points2Add);
		if(this.goldenPointWorking && points2Add > 0)
			this.blueLastGoldenPointPoints = Integer.valueOf(points2Add);
		_addMatchLogItem(Long.valueOf(eventTimestamp),
				Long.valueOf(roundTimestamp),
				Long.valueOf(System.currentTimeMillis()),
				getRound4MatchLog(false),
				getRoundStr4MatchLog(false), MatchLogItemType.BLUE_HEAD_TECH_POINT,

				Integer.valueOf(points2Add),
				Integer.valueOf(0), actionSource
						.toString(), true, Integer.valueOf(0), actionSource
								.equals(ActionSource.SCOREBOARD_EDITOR));
		workWithScoreboardEditorAction(actionSource, eventTimestamp, points2Add, true, HitEventType.HEAD, true);
	}

	@Override
	public void addBlueBodyPoint(int points2Add, ActionSource actionSource, long eventTimestamp, long roundTimestamp, int prevSensorHitValue) {
		if(this.bluePoints.get() + points2Add < 0)
			return;
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("add BLUE BODY points " + points2Add + " from " + actionSource.toString());
		if(actionSource.equals(ActionSource.SCOREBOARD_EDITOR) && MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION
				.equals(getCurrentMatchStatus()) && this.changeMatchStatusToTimeoutOnScoreboardChanges)
			this.currentMatchStatus.set(MatchStatusId.ROUND_PAUSED);
		TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				BaseCommonMatchWorker.this.getSoundPlayerService().playSoundWhenBodyHit();
				return null;
			}
		});
		appendToCounter("BLUE_BODY", this.blueRoundsBodyPoints, this.currentRound.getValue(), Integer.valueOf((points2Add > 0) ? 1 : - 1));
		this.bluePoints.set(this.bluePoints.get() + points2Add);
		if(this.goldenPointWorking && points2Add > 0)
			this.blueLastGoldenPointPoints = Integer.valueOf(points2Add);
		_addMatchLogItem(Long.valueOf(eventTimestamp),
				Long.valueOf(roundTimestamp),
				Long.valueOf(System.currentTimeMillis()),
				getRound4MatchLog(false),
				getRoundStr4MatchLog(false), MatchLogItemType.BLUE_BODY_POINT,

				Integer.valueOf(points2Add),
				Integer.valueOf(0), actionSource
						.toString(), true, Integer.valueOf(prevSensorHitValue), actionSource
								.equals(ActionSource.SCOREBOARD_EDITOR));
		workWithScoreboardEditorAction(actionSource, eventTimestamp, points2Add, true, HitEventType.BODY, false);
	}

	@Override
	public void addBlueBodyTechPoint(int points2Add, ActionSource actionSource, long eventTimestamp, long roundTimestamp) {
		if(this.bluePoints.get() + points2Add < 0)
			return;
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("add BLUE BODY points " + points2Add + " from " + actionSource.toString());
		if(actionSource.equals(ActionSource.SCOREBOARD_EDITOR) && MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION
				.equals(getCurrentMatchStatus()) && this.changeMatchStatusToTimeoutOnScoreboardChanges)
			this.currentMatchStatus.set(MatchStatusId.ROUND_PAUSED);
		TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				BaseCommonMatchWorker.this.getSoundPlayerService().playSoundWhenBodyHit();
				return null;
			}
		});
		int points4Work = (points2Add > 0) ? (points2Add + this.bodyPoints) : (points2Add - this.bodyPoints);
		appendToCounter("BLUE_BODY_TECH", this.blueRoundsTechPoints, this.currentRound.getValue(), Integer.valueOf(points4Work));
		appendToCounter("BLUE_BODY", this.blueRoundsBodyPoints, this.currentRound.getValue(), Integer.valueOf( - 1));
		this.blueTechPoints.set(this.blueTechPoints.get() + points4Work);
		this.bluePoints.set(this.bluePoints.get() + points2Add);
		if(this.goldenPointWorking && points2Add > 0)
			this.blueLastGoldenPointPoints = Integer.valueOf(points2Add);
		if(isParaTkdMatch()) {
			this.bluePARATechPoints.set((points2Add > 0) ? (this.bluePARATechPoints.get() + 1) : (this.bluePARATechPoints.get() - 1));
			this.bluePARATotalTechPoints.set(this.bluePARATotalTechPoints.get() + points2Add);
		}
		_addMatchLogItem(Long.valueOf(eventTimestamp),
				Long.valueOf(roundTimestamp),
				Long.valueOf(System.currentTimeMillis()),
				getRound4MatchLog(false),
				getRoundStr4MatchLog(false), MatchLogItemType.BLUE_BODY_TECH_POINT,

				Integer.valueOf(points2Add),
				Integer.valueOf(0), actionSource
						.toString(), true, Integer.valueOf(0), actionSource
								.equals(ActionSource.SCOREBOARD_EDITOR));
		workWithScoreboardEditorAction(actionSource, eventTimestamp, points2Add, true, HitEventType.BODY, true);
	}

	@Override
	public void addBluePunchPoint(int points2Add, ActionSource actionSource, long eventTimestamp, long roundTimestamp) {
		if(this.bluePoints.get() + points2Add < 0)
			return;
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("add BLUE PUNCH points " + points2Add + " from " + actionSource.toString());
		if(actionSource.equals(ActionSource.SCOREBOARD_EDITOR) && MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION
				.equals(getCurrentMatchStatus()) && this.changeMatchStatusToTimeoutOnScoreboardChanges)
			this.currentMatchStatus.set(MatchStatusId.ROUND_PAUSED);
		TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				BaseCommonMatchWorker.this.getSoundPlayerService().playSoundWhenBodyHit();
				return null;
			}
		});
		appendToCounter("BLUE_PUNCH", this.blueRoundsPunchPoints, this.currentRound.getValue(), Integer.valueOf((points2Add > 0) ? 1 : - 1));
		this.bluePoints.set(this.bluePoints.get() + points2Add);
		if(this.goldenPointWorking && points2Add > 0)
			this.blueLastGoldenPointPoints = Integer.valueOf(points2Add);
		if(this.goldenPointEnabled && this.goldenPointWorking) {
			this.goldenPointPointByPenalty = false;
			this.goldenPointPointByPunch = true;
			this.blueGoldenPointPunches.set(this.blueGoldenPointPunches.getValue().intValue() + ((points2Add > 0) ? 1 : - 1));
		}
		_addMatchLogItem(Long.valueOf(eventTimestamp),
				Long.valueOf(roundTimestamp),
				Long.valueOf(System.currentTimeMillis()),
				getRound4MatchLog(false),
				getRoundStr4MatchLog(false), MatchLogItemType.BLUE_PUNCH_POINT,

				Integer.valueOf(points2Add),
				Integer.valueOf(0), actionSource
						.toString(), true, Integer.valueOf(0), actionSource
								.equals(ActionSource.SCOREBOARD_EDITOR));
		workWithScoreboardEditorAction(actionSource, eventTimestamp, points2Add, true, HitEventType.PUNCH, false);
	}

	@Override
	public void addBlueGamJeom(ActionSource actionSource, long eventTimestamp, long roundTimestamp) {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("add BLUE GAM JEOM from " + actionSource.toString());
		if(actionSource.equals(ActionSource.SCOREBOARD_EDITOR) && MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION
				.equals(getCurrentMatchStatus()) && this.changeMatchStatusToTimeoutOnScoreboardChanges)
			this.currentMatchStatus.set(MatchStatusId.ROUND_PAUSED);
		if((this.goldenPointEnabled && this.goldenPointWorking) || MatchStatusId.WAITING_4_START_GOLDENPOINT.equals(getCurrentMatchStatus())) {
			this.goldenPointPointByPenalty = true;
			this.goldenPointPointByPunch = false;
			this.goldenPointPointByPenaltyValue = 1;
			addGamJeom(this.blueGoldenPointPenalties, eventTimestamp, roundTimestamp, actionSource, true);
		} else {
			addGamJeom(this.bluePenalties, eventTimestamp, roundTimestamp, actionSource, true);
		}
	}

	@Override
	public void addBlueGamJeomToNextRound(ActionSource actionSource, long eventTimestamp, long roundTimestamp) {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("add BLUE GAM JEOM to Next Round from " + actionSource.toString());
		appendToCounter("BLUE_GAMJEOM", this.blueRoundsPenalties, Integer.valueOf(this.currentRound.getValue().intValue() + 1), Integer.valueOf(1));
	}

	@Override
	public void removeBlueGamJeom(ActionSource actionSource, long eventTimestamp, long roundTimestamp) {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("remove BLUE GAM JEOM from " + actionSource.toString());
		if(actionSource.equals(ActionSource.SCOREBOARD_EDITOR) && MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION
				.equals(getCurrentMatchStatus()) && this.changeMatchStatusToTimeoutOnScoreboardChanges)
			this.currentMatchStatus.set(MatchStatusId.ROUND_PAUSED);
		if((this.goldenPointEnabled && this.goldenPointWorking) || MatchStatusId.WAITING_4_START_GOLDENPOINT.equals(getCurrentMatchStatus())) {
			removeGamJeom(this.blueGoldenPointPenalties, eventTimestamp, roundTimestamp, actionSource, true);
		} else {
			removeGamJeom(this.bluePenalties, eventTimestamp, roundTimestamp, actionSource, true);
		}
	}

	@Override
	public void addRedHeadPoint(int points2Add, ActionSource actionSource, long eventTimestamp, long roundTimestamp, int prevSensorHitValue) {
		if(this.redPoints.get() + points2Add < 0)
			return;
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("add RED HEAD points " + points2Add + " from " + actionSource.toString());
		if(actionSource.equals(ActionSource.SCOREBOARD_EDITOR) && MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION
				.equals(getCurrentMatchStatus()) && this.changeMatchStatusToTimeoutOnScoreboardChanges)
			this.currentMatchStatus.set(MatchStatusId.ROUND_PAUSED);
		TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				BaseCommonMatchWorker.this.getSoundPlayerService().playSoundWhenHeadHit();
				return null;
			}
		});
		appendToCounter("RED_HEAD", this.redRoundsHeadPoints, this.currentRound.getValue(), Integer.valueOf((points2Add > 0) ? 1 : - 1));
		this.redPoints.set(this.redPoints.get() + points2Add);
		if(this.goldenPointWorking && points2Add > 0)
			this.redLastGoldenPointPoints = Integer.valueOf(points2Add);
		_addMatchLogItem(Long.valueOf(eventTimestamp),
				Long.valueOf(roundTimestamp),
				Long.valueOf(System.currentTimeMillis()),
				getRound4MatchLog(false),
				getRoundStr4MatchLog(false), MatchLogItemType.RED_HEAD_POINT,

				Integer.valueOf(0),
				Integer.valueOf(points2Add), actionSource
						.toString(), true, Integer.valueOf(prevSensorHitValue), actionSource
								.equals(ActionSource.SCOREBOARD_EDITOR));
		workWithScoreboardEditorAction(actionSource, eventTimestamp, points2Add, false, HitEventType.HEAD, false);
	}

	@Override
	public void addRedHeadTechPoint(int points2Add, ActionSource actionSource, long eventTimestamp, long roundTimestamp) {
		if(this.redPoints.get() + points2Add < 0)
			return;
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("add RED HEAD TECH points " + points2Add + " from " + actionSource.toString());
		if(actionSource.equals(ActionSource.SCOREBOARD_EDITOR) && MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION
				.equals(getCurrentMatchStatus()) && this.changeMatchStatusToTimeoutOnScoreboardChanges)
			this.currentMatchStatus.set(MatchStatusId.ROUND_PAUSED);
		TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				BaseCommonMatchWorker.this.getSoundPlayerService().playSoundWhenHeadHit();
				return null;
			}
		});
		int points4Work = (points2Add > 0) ? (points2Add + this.headPoints) : (points2Add - this.headPoints);
		appendToCounter("RED_HEAD_TECH", this.redRoundsTechPoints, this.currentRound.getValue(), Integer.valueOf(points4Work));
		appendToCounter("RED_HEAD", this.redRoundsHeadPoints, this.currentRound.getValue(), Integer.valueOf( - 1));
		this.redTechPoints.set(this.redTechPoints.get() + points4Work);
		this.redPoints.set(this.redPoints.get() + points2Add);
		if(this.goldenPointWorking && points2Add > 0)
			this.redLastGoldenPointPoints = Integer.valueOf(points2Add);
		_addMatchLogItem(Long.valueOf(eventTimestamp),
				Long.valueOf(roundTimestamp),
				Long.valueOf(System.currentTimeMillis()),
				getRound4MatchLog(false),
				getRoundStr4MatchLog(false), MatchLogItemType.RED_HEAD_TECH_POINT,

				Integer.valueOf(0),
				Integer.valueOf(points2Add), actionSource
						.toString(), true, Integer.valueOf(0), actionSource
								.equals(ActionSource.SCOREBOARD_EDITOR));
		workWithScoreboardEditorAction(actionSource, eventTimestamp, points2Add, false, HitEventType.HEAD, true);
	}

	@Override
	public void addRedBodyPoint(int points2Add, ActionSource actionSource, long eventTimestamp, long roundTimestamp, int prevSensorHitValue) {
		if(this.redPoints.get() + points2Add < 0)
			return;
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("add RED BODY points " + points2Add + " from " + actionSource.toString());
		if(actionSource.equals(ActionSource.SCOREBOARD_EDITOR) && MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION
				.equals(getCurrentMatchStatus()) && this.changeMatchStatusToTimeoutOnScoreboardChanges)
			this.currentMatchStatus.set(MatchStatusId.ROUND_PAUSED);
		TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				BaseCommonMatchWorker.this.getSoundPlayerService().playSoundWhenBodyHit();
				return null;
			}
		});
		appendToCounter("RED_BODY", this.redRoundsBodyPoints, this.currentRound.getValue(), Integer.valueOf((points2Add > 0) ? 1 : - 1));
		this.redPoints.set(this.redPoints.get() + points2Add);
		if(this.goldenPointWorking && points2Add > 0)
			this.redLastGoldenPointPoints = Integer.valueOf(points2Add);
		_addMatchLogItem(Long.valueOf(eventTimestamp),
				Long.valueOf(roundTimestamp),
				Long.valueOf(System.currentTimeMillis()),
				getRound4MatchLog(false),
				getRoundStr4MatchLog(false), MatchLogItemType.RED_BODY_POINT,

				Integer.valueOf(0),
				Integer.valueOf(points2Add), actionSource
						.toString(), true, Integer.valueOf(prevSensorHitValue), actionSource
								.equals(ActionSource.SCOREBOARD_EDITOR));
		workWithScoreboardEditorAction(actionSource, eventTimestamp, points2Add, false, HitEventType.BODY, false);
	}

	@Override
	public void addRedBodyTechPoint(int points2Add, ActionSource actionSource, long eventTimestamp, long roundTimestamp) {
		if(this.redPoints.get() + points2Add < 0)
			return;
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("add RED BODY TECH points " + points2Add + " from " + actionSource.toString());
		if(actionSource.equals(ActionSource.SCOREBOARD_EDITOR) && MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION
				.equals(getCurrentMatchStatus()) && this.changeMatchStatusToTimeoutOnScoreboardChanges)
			this.currentMatchStatus.set(MatchStatusId.ROUND_PAUSED);
		TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				BaseCommonMatchWorker.this.getSoundPlayerService().playSoundWhenBodyHit();
				return null;
			}
		});
		int points4Work = (points2Add > 0) ? (points2Add + this.bodyPoints) : (points2Add - this.bodyPoints);
		appendToCounter("RED_BODY_TECH", this.redRoundsTechPoints, this.currentRound.getValue(), Integer.valueOf(points4Work));
		appendToCounter("RED_BODY", this.redRoundsBodyPoints, this.currentRound.getValue(), Integer.valueOf( - 1));
		this.redTechPoints.set(this.redTechPoints.get() + points4Work);
		this.redPoints.set(this.redPoints.get() + points2Add);
		if(this.goldenPointWorking && points2Add > 0)
			this.redLastGoldenPointPoints = Integer.valueOf(points2Add);
		if(isParaTkdMatch()) {
			this.redPARATechPoints.set((points2Add > 0) ? (this.redPARATechPoints.get() + 1) : (this.redPARATechPoints.get() - 1));
			this.redPARATotalTechPoints.set(this.redPARATotalTechPoints.get() + points2Add);
		}
		_addMatchLogItem(Long.valueOf(eventTimestamp),
				Long.valueOf(roundTimestamp),
				Long.valueOf(System.currentTimeMillis()),
				getRound4MatchLog(false),
				getRoundStr4MatchLog(false), MatchLogItemType.RED_BODY_TECH_POINT,

				Integer.valueOf(0),
				Integer.valueOf(points2Add), actionSource
						.toString(), true, Integer.valueOf(0), actionSource
								.equals(ActionSource.SCOREBOARD_EDITOR));
		workWithScoreboardEditorAction(actionSource, eventTimestamp, points2Add, false, HitEventType.BODY, true);
	}

	@Override
	public void addRedPunchPoint(int points2Add, ActionSource actionSource, long eventTimestamp, long roundTimestamp) {
		if(this.redPoints.get() + points2Add < 0)
			return;
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("add RED PUNCH points " + points2Add + " from " + actionSource.toString());
		if(actionSource.equals(ActionSource.SCOREBOARD_EDITOR) && MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION
				.equals(getCurrentMatchStatus()) && this.changeMatchStatusToTimeoutOnScoreboardChanges)
			this.currentMatchStatus.set(MatchStatusId.ROUND_PAUSED);
		TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				BaseCommonMatchWorker.this.getSoundPlayerService().playSoundWhenBodyHit();
				return null;
			}
		});
		appendToCounter("RED_PUNCH", this.redRoundsPunchPoints, this.currentRound.getValue(), Integer.valueOf((points2Add > 0) ? 1 : - 1));
		this.redPoints.set(this.redPoints.get() + points2Add);
		if(this.goldenPointWorking && points2Add > 0)
			this.redLastGoldenPointPoints = Integer.valueOf(points2Add);
		if(this.goldenPointEnabled && this.goldenPointWorking) {
			this.goldenPointPointByPenalty = true;
			this.goldenPointPointByPunch = false;
			this.redGoldenPointPunches.set(this.redGoldenPointPunches.getValue().intValue() + ((points2Add > 0) ? 1 : - 1));
		}
		_addMatchLogItem(Long.valueOf(eventTimestamp),
				Long.valueOf(roundTimestamp),
				Long.valueOf(System.currentTimeMillis()),
				getRound4MatchLog(false),
				getRoundStr4MatchLog(false), MatchLogItemType.RED_PUNCH_POINT,

				Integer.valueOf(0),
				Integer.valueOf(points2Add), actionSource
						.toString(), true, Integer.valueOf(0), actionSource
								.equals(ActionSource.SCOREBOARD_EDITOR));
		workWithScoreboardEditorAction(actionSource, eventTimestamp, points2Add, false, HitEventType.PUNCH, false);
	}

	@Override
	public void addRedGamJeom(ActionSource actionSource, long eventTimestamp, long roundTimestamp) {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("add RED GAME JEOM from " + actionSource.toString());
		if(actionSource.equals(ActionSource.SCOREBOARD_EDITOR) && MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION
				.equals(getCurrentMatchStatus()) && this.changeMatchStatusToTimeoutOnScoreboardChanges)
			this.currentMatchStatus.set(MatchStatusId.ROUND_PAUSED);
		if((this.goldenPointEnabled && this.goldenPointWorking) || MatchStatusId.WAITING_4_START_GOLDENPOINT.equals(getCurrentMatchStatus())) {
			this.goldenPointPointByPenalty = true;
			this.goldenPointPointByPunch = false;
			this.goldenPointPointByPenaltyValue = 1;
			addGamJeom(this.redGoldenPointPenalties, eventTimestamp, roundTimestamp, actionSource, false);
		} else {
			addGamJeom(this.redPenalties, eventTimestamp, roundTimestamp, actionSource, false);
		}
	}

	@Override
	public void addRedGamJeomToNextRound(ActionSource actionSource, long eventTimestamp, long roundTimestamp) {
		appendToCounter("RED_GAMJEOM", this.redRoundsPenalties, Integer.valueOf(this.currentRound.getValue().intValue() + 1), Integer.valueOf(1));
	}

	@Override
	public void removeRedGamJeom(ActionSource actionSource, long eventTimestamp, long roundTimestamp) {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("remove RED GAM JEOM from " + actionSource.toString());
		if(actionSource.equals(ActionSource.SCOREBOARD_EDITOR) && MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION
				.equals(getCurrentMatchStatus()) && this.changeMatchStatusToTimeoutOnScoreboardChanges)
			this.currentMatchStatus.set(MatchStatusId.ROUND_PAUSED);
		if((this.goldenPointEnabled && this.goldenPointWorking) || MatchStatusId.WAITING_4_START_GOLDENPOINT.equals(getCurrentMatchStatus())) {
			removeGamJeom(this.redGoldenPointPenalties, eventTimestamp, roundTimestamp, actionSource, false);
		} else {
			removeGamJeom(this.redPenalties, eventTimestamp, roundTimestamp, actionSource, false);
		}
	}

	@Override
	public int getDifferentialScore() {
		return this.differencialScore;
	}

	@Override
	public void disableDifferentialScore() {
		this.differencialScore = 99;
	}

	@Override
	public void cancelVictoryByPointGap() {
		matchWorkerLogger.info("PrevMatchStatus:" + this.prevMatchStatus.get() + " - currentStatus:" + this.currentMatchStatus.get());
		this.matchWinner.set(MatchWinner.TIE);
		this.matchFinalDecission.set(FinalDecision.RSC);
		this.matchWinnerByPointGapNeedsConfirmation.set(false);
		disableDifferentialScore();
		if((MatchStatusId.ROUND_FINISHED.equals(this.currentMatchStatus.get()) || MatchStatusId.ROUND_FINISHED.equals(this.prevMatchStatus.get()))
				&& MatchVictoryCriteria.CONVENTIONAL
						.equals(this.matchVictoryCriteria) && this.currentRound
								.get() == 2) {
			matchWorkerLogger.info("Estem entre els dos?");
			if(_hasNextRound()) {
				this.restTimeCountdown.clean(this.restTimeMinutes, this.restTimeSeconds);
				this.restTimeCountdown.play();
			}
		} else {
			this.currentMatchStatus.set(MatchStatusId.ROUND_PAUSED);
		}
	}

	private void workWithScoreboardEditorAction(ActionSource actionSource, long eventTimestamp, long points2Add, boolean blue,
			HitEventType hitEventType, boolean isTech) {
		if(actionSource.equals(ActionSource.SCOREBOARD_EDITOR))
			if( ! isTech) {
				if(points2Add < 0L) {
					fireTryToRemoveHitEvent(blue, hitEventType);
				} else if(points2Add > 0L) {
					HitJudgeStatus hitJudgeStatus = HitEventType.PUNCH.equals(hitEventType) ? HitJudgeStatus.VALIDATED : HitJudgeStatus.NOT_VALIDATED;
					fireForceAddHitEventValidator(new HitEventValidator(blue, eventTimestamp, hitEventType,

							Integer.valueOf(99), this.numberOfJudges, hitJudgeStatus, (this.numberOfJudges >= 2) ? hitJudgeStatus
									: HitJudgeStatus.NOT_ENABLED, (this.numberOfJudges >= 3) ? hitJudgeStatus : HitJudgeStatus.NOT_ENABLED, false,
							this.backupSystemEnabled

									.get()));
				}
			} else {
				fireTryToChangeHitTechEvent(blue, hitEventType, (points2Add > 0L));
			}
	}

	private void addGamJeom(SimpleIntegerProperty penalties, long eventTimestamp, long roundTimestamp, ActionSource actionSource, boolean blue) {
		if(penalties.get() < _getMaxGamJeomsAllowed().intValue()) {
			appendToCounter(blue ? "BLUE_GAMJEOM" : "RED_GAMJEOM", blue ? this.blueRoundsPenalties : this.redRoundsPenalties, this.currentRound
					.getValue(), Integer.valueOf(1));
			penalties.set(penalties.get() + 1);
			TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					BaseCommonMatchWorker.this.getSoundPlayerService().playSoundWhenGameJeon();
					return null;
				}
			});
			_addMatchLogItem(Long.valueOf(eventTimestamp),
					Long.valueOf(roundTimestamp),
					Long.valueOf(System.currentTimeMillis()),
					getRound4MatchLog(false),
					getRoundStr4MatchLog(false), blue ? MatchLogItemType.BLUE_ADD_GAME_JEON : MatchLogItemType.RED_ADD_GAME_JEON,

					Integer.valueOf(0),
					Integer.valueOf(0), actionSource
							.toString(), true,

					Integer.valueOf(0), actionSource
							.equals(ActionSource.SCOREBOARD_EDITOR));
			if(this.wtUDPService.isConnected())
				TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

					@Override
					public Void call() throws Exception {
						BaseCommonMatchWorker.this.wtUDPService.sendPenaltiesChange(BaseCommonMatchWorker.this
								.getBlueCurrentPenalties4EventsListeners(), BaseCommonMatchWorker.this.getRedCurrentPenalties4EventsListeners());
						return null;
					}
				});
		}
	}

	private void removeGamJeom(SimpleIntegerProperty penalties, long eventTimestamp, long roundTimestamp, ActionSource actionSource, boolean blue) {
		if(penalties.get() > 0) {
			appendToCounter(blue ? "BLUE_GAMJEOM" : "RED_GAMJEOM", blue ? this.blueRoundsPenalties : this.redRoundsPenalties, this.currentRound
					.getValue(), Integer.valueOf( - 1));
			penalties.set(penalties.get() - 1);
			TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					BaseCommonMatchWorker.this.getSoundPlayerService().playSoundWhenGameJeon();
					return null;
				}
			});
			_addMatchLogItem(Long.valueOf(eventTimestamp),
					Long.valueOf(roundTimestamp),
					Long.valueOf(System.currentTimeMillis()),
					getRound4MatchLog(false),
					getRoundStr4MatchLog(false), blue ? MatchLogItemType.BLUE_REMOVE_GAME_JEON : MatchLogItemType.RED_REMOVE_GAME_JEON,

					Integer.valueOf(0),
					Integer.valueOf(0), actionSource
							.toString(), true, Integer.valueOf(0), actionSource
									.equals(ActionSource.SCOREBOARD_EDITOR));
		}
	}

	final void validateCanStartMatch() {
		if(this.networkOkByGlobalController.get()) {
			if(getCurrentMatchStatus().equals(MatchStatusId.NOT_READY) ||
					getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_MATCH)) {
				matchWorkerLogger.debug("Current Match Status was NOT READY");
				this.currentMatchStatus.set(MatchStatusId.WAITING_4_MATCH);
				this.currentRound.set(0);
				this.currentRoundStr.set("0");
			}
			if(this.currentRound.get() == 0) {
				matchWorkerLogger.debug("Current Round = 0 --> MOVE TO NEXT ROUND!");
				moveToNextRound();
			}
		} else {
			matchWorkerLogger.debug("Validate Can Start Match - NetworkOkByGlobalController False!!!");
		}
	}

	final boolean finishMatchByDifferentialScore(boolean matchIsBestOf3) {
		boolean res = false;
		this.bestOf3RoundWithPointGap.set(false);
		this.bestOf3RoundWinnerWithPointGap = null;
		if(this.differencialScore > 0) {
			int bgp = this.blueGeneralPoints.get();
			int rgp = this.redGeneralPoints.get();
			if(bgp > 0 && bgp - rgp >= this.differencialScore) {
				if( ! matchIsBestOf3) {
					this.matchWinner.set(MatchWinner.BLUE);
					if(this.pointGapWithGamJeomNeedsConfirmation.booleanValue() && this.blueLastPointIsPenalty) {
						doPauseRound();
						this.matchWinnerByPointGapNeedsConfirmation.set(true);
					} else {
						this.matchFinalDecission.set(FinalDecision.PTG);
					}
				} else {
					this.bestOf3RoundWithPointGap.set(false);
					this.bestOf3RoundWithPointGap.set(true);
					this.bestOf3RoundWinnerWithPointGap = MatchWinner.BLUE;
				}
				res = true;
			}
			if(rgp > 0 && rgp - bgp >= this.differencialScore) {
				if( ! matchIsBestOf3) {
					this.matchWinner.set(MatchWinner.RED);
					if(this.pointGapWithGamJeomNeedsConfirmation.booleanValue() && this.redLastPointIsPenalty) {
						doPauseRound();
						this.matchWinnerByPointGapNeedsConfirmation.set(true);
					} else {
						this.matchFinalDecission.set(FinalDecision.PTG);
					}
				} else {
					this.bestOf3RoundWithPointGap.set(false);
					this.bestOf3RoundWithPointGap.set(true);
					this.bestOf3RoundWinnerWithPointGap = MatchWinner.RED;
				}
				res = true;
			}
		}
		return res;
	}

	void validateGoldenPointTieBreaker() {
		this.goldenPointTieBreaker = true;
		this.goldenPointTieBreakerInfo = new GoldenPointTieBreakerInfoDto();
		this.goldenPointTieBreakerInfo.setHaveTieBreaker(Boolean.TRUE);
		this.goldenPointTieBreakerInfo.setBluePunches(Integer.valueOf(this.blueGoldenPointPunches.get()));
		this.goldenPointTieBreakerInfo.setRedPunches(Integer.valueOf(this.redGoldenPointPunches.get()));
		this.showGoldenPointTieBreakerOnScoreboard.set(this.showGoldenPointTieBreakerAuto.booleanValue());
		int internalBlueRoundWins = 0;
		int internalRedRoundWins = 0;
		for(int round = 1; round <= this.matchRounds.get(); round++) {
			MatchWinner rWinner = getRoundWinner(Integer.valueOf(round));
			if(MatchWinner.BLUE.equals(rWinner)) {
				internalBlueRoundWins++;
			} else if(MatchWinner.RED.equals(rWinner)) {
				internalRedRoundWins++;
			}
		}
		this.goldenPointTieBreakerInfo.setBlueRoundWins(Integer.valueOf(internalBlueRoundWins));
		this.goldenPointTieBreakerInfo.setRedRoundWins(Integer.valueOf(internalRedRoundWins));
		this.goldenPointTieBreakerInfo.setBlueHits(Integer.valueOf(this.blueGoldenPointImpacts.get()));
		this.goldenPointTieBreakerInfo.setRedHits(Integer.valueOf(this.redGoldenPointImpacts.get()));
		int totalBluePenalties = this.blueTotalPenalties.get() + this.blueGoldenPointPenalties.get();
		int totalRedPenalties = this.redTotalPenalties.get() + this.redGoldenPointPenalties.get();
		this.goldenPointTieBreakerInfo.setBluePenalties(Integer.valueOf(totalBluePenalties));
		this.goldenPointTieBreakerInfo.setRedPenalties(Integer.valueOf(totalRedPenalties));
		this.goldenPointTieBreakerInfo.setBluePARATechPoints(Integer.valueOf(this.bluePARATechPoints.get()));
		this.goldenPointTieBreakerInfo.setRedPARATechPoints(Integer.valueOf(this.redPARATechPoints.get()));
		if(matchWorkerLogger.isDebugEnabled()) {
			matchWorkerLogger.debug("validateGoldenPointTieBreaker - ");
			if(isParaTkdMatch()) {
				matchWorkerLogger.debug("validateGoldenPointTieBreaker - PARA blue impacts " + this.blueGoldenPointImpacts.get());
				matchWorkerLogger.debug("validateGoldenPointTieBreaker - PARA blue roundWins " + internalBlueRoundWins);
				matchWorkerLogger.debug("validateGoldenPointTieBreaker - PARA blue 3/4 Points " + this.bluePARATechPoints.get());
				matchWorkerLogger.debug("validateGoldenPointTieBreaker - PARA blue 3/4 Total Points " + this.bluePARATotalTechPoints.get());
				matchWorkerLogger.debug("validateGoldenPointTieBreaker - PARA blue penalties " + totalBluePenalties);
				matchWorkerLogger.debug("validateGoldenPointTieBreaker - PARA red impacts " + this.redGoldenPointImpacts.get());
				matchWorkerLogger.debug("validateGoldenPointTieBreaker - PARA red roundWins " + internalRedRoundWins);
				matchWorkerLogger.debug("validateGoldenPointTieBreaker - PARA red 3/4 Points " + this.redPARATechPoints.get());
				matchWorkerLogger.debug("validateGoldenPointTieBreaker - PARA red 3/4 Total Points " + this.redPARATotalTechPoints.get());
				matchWorkerLogger.debug("validateGoldenPointTieBreaker - PARA red penalties " + totalRedPenalties);
			} else {
				matchWorkerLogger.debug("validateGoldenPointTieBreaker - blue punches " + this.blueGoldenPointPunches.get());
				matchWorkerLogger.debug("validateGoldenPointTieBreaker - blue impacts " + this.blueGoldenPointImpacts.get());
				matchWorkerLogger.debug("validateGoldenPointTieBreaker - blue roundWins " + internalBlueRoundWins);
				matchWorkerLogger.debug("validateGoldenPointTieBreaker - blue penalties " + totalBluePenalties);
				matchWorkerLogger.debug("validateGoldenPointTieBreaker - red punches " + this.redGoldenPointPunches.get());
				matchWorkerLogger.debug("validateGoldenPointTieBreaker - red impacts " + this.redGoldenPointImpacts.get());
				matchWorkerLogger.debug("validateGoldenPointTieBreaker - red roundWins " + internalRedRoundWins);
				matchWorkerLogger.debug("validateGoldenPointTieBreaker - red penalties " + totalRedPenalties);
			}
		}
		if(isParaTkdMatch()) {
			if(this.blueGoldenPointImpacts.get() > this.redGoldenPointImpacts.get()) {
				if(matchWorkerLogger.isDebugEnabled())
					matchWorkerLogger.debug("PARA-GDP SUP Winner BLUE by impacts");
				this.matchFinalDecission.set(FinalDecision.SUP);
				this.matchWinner.set(MatchWinner.BLUE);
				this.currentMatchStatus.set(null);
				this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
			} else if(this.redGoldenPointImpacts.get() > this.blueGoldenPointImpacts.get()) {
				if(matchWorkerLogger.isDebugEnabled())
					matchWorkerLogger.debug("PARA-GDP SUP Winner RED by impacts");
				this.matchFinalDecission.set(FinalDecision.SUP);
				this.matchWinner.set(MatchWinner.RED);
				this.currentMatchStatus.set(null);
				this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
			} else if(this.bluePARATechPoints.get() > this.redPARATechPoints.get()) {
				if(matchWorkerLogger.isDebugEnabled())
					matchWorkerLogger.debug("PARA-GDP SUP Winner BLUE by 3/4 Tech");
				this.matchFinalDecission.set(FinalDecision.SUP);
				this.matchWinner.set(MatchWinner.BLUE);
				this.currentMatchStatus.set(null);
				this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
			} else if(this.redPARATechPoints.get() > this.bluePARATechPoints.get()) {
				if(matchWorkerLogger.isDebugEnabled())
					matchWorkerLogger.debug("PARA-GDP SUP Winner RED by 3/4 Tech");
				this.matchFinalDecission.set(FinalDecision.SUP);
				this.matchWinner.set(MatchWinner.RED);
				this.currentMatchStatus.set(null);
				this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
			} else if(this.bluePARATechPoints.get() == this.redPARATechPoints.get() && this.bluePARATotalTechPoints
					.get() > this.redPARATotalTechPoints.get()) {
				if(matchWorkerLogger.isDebugEnabled())
					matchWorkerLogger.debug("PARA-GDP SUP Winner BLUE by 3/4 Tech Points");
				this.matchFinalDecission.set(FinalDecision.SUP);
				this.matchWinner.set(MatchWinner.BLUE);
				this.currentMatchStatus.set(null);
				this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
			} else if(this.bluePARATechPoints.get() == this.redPARATechPoints.get() && this.redPARATotalTechPoints
					.get() > this.bluePARATotalTechPoints.get()) {
				if(matchWorkerLogger.isDebugEnabled())
					matchWorkerLogger.debug("PARA-GDP SUP Winner RED by 3/4 Tech Points");
				this.matchFinalDecission.set(FinalDecision.SUP);
				this.matchWinner.set(MatchWinner.RED);
				this.currentMatchStatus.set(null);
				this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
			} else if(totalBluePenalties > totalRedPenalties) {
				if(matchWorkerLogger.isDebugEnabled())
					matchWorkerLogger.debug("PARA-GDP SUP Winner RED by penalties");
				this.matchFinalDecission.set(FinalDecision.SUP);
				this.matchWinner.set(MatchWinner.RED);
				this.currentMatchStatus.set(null);
				this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
			} else if(totalRedPenalties > totalBluePenalties) {
				if(matchWorkerLogger.isDebugEnabled())
					matchWorkerLogger.debug("PARA-GDP SUP Winner BLUE by penalties");
				this.matchFinalDecission.set(FinalDecision.SUP);
				this.matchWinner.set(MatchWinner.BLUE);
				this.currentMatchStatus.set(null);
				this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
			} else {
				if(matchWorkerLogger.isDebugEnabled())
					matchWorkerLogger.debug("PARA-GDP SUP TIE");
				this.matchFinalDecission.set(FinalDecision.SUP);
				this.matchWinner.set(MatchWinner.TIE);
				this.currentMatchStatus.set(null);
				this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
			}
		} else if(this.blueGoldenPointPunches.get() > this.redGoldenPointPunches.get()) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("GDP SUP Winner BLUE by punches");
			this.matchFinalDecission.set(FinalDecision.SUP);
			this.matchWinner.set(MatchWinner.BLUE);
			this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
		} else if(this.redGoldenPointPunches.get() > this.blueGoldenPointPunches.get()) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("GDP SUP Winner RED by punches");
			this.matchFinalDecission.set(FinalDecision.SUP);
			this.matchWinner.set(MatchWinner.RED);
			this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
		} else if(this.blueGoldenPointImpacts.get() > this.redGoldenPointImpacts.get()) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("GDP SUP Winner BLUE by impacts");
			this.matchFinalDecission.set(FinalDecision.SUP);
			this.matchWinner.set(MatchWinner.BLUE);
			this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
		} else if(this.redGoldenPointImpacts.get() > this.blueGoldenPointImpacts.get()) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("GDP SUP Winner RED by impacts");
			this.matchFinalDecission.set(FinalDecision.SUP);
			this.matchWinner.set(MatchWinner.RED);
			this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
		} else if(internalBlueRoundWins > internalRedRoundWins) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("GDP SUP Winner BLUE by Rounds Wins");
			this.matchFinalDecission.set(FinalDecision.SUP);
			this.matchWinner.set(MatchWinner.BLUE);
			this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
		} else if(internalRedRoundWins > internalBlueRoundWins) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("GDP SUP Winner RED by Rounds Wins");
			this.matchFinalDecission.set(FinalDecision.SUP);
			this.matchWinner.set(MatchWinner.RED);
			this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
		} else if(totalBluePenalties > totalRedPenalties) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("GDP SUP Winner RED by penalties");
			this.matchFinalDecission.set(FinalDecision.SUP);
			this.matchWinner.set(MatchWinner.RED);
			this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
		} else if(totalRedPenalties > totalBluePenalties) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("GDP SUP Winner BLUE by penalties");
			this.matchFinalDecission.set(FinalDecision.SUP);
			this.matchWinner.set(MatchWinner.BLUE);
			this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
		} else {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("GDP SUP TIE");
			this.matchFinalDecission.set(FinalDecision.SUP);
			this.matchWinner.set(MatchWinner.TIE);
			this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
		}
	}

	private void validateSuperiorityInBestOf3() {
		matchWorkerLogger.info("Match BestOf3 finished with TIE, go to validate Superiority.");
		int totalBluePoints = 0;
		int totalRedPoints = 0;
		for(Integer points : this.blueRoundsPoints.values())
			totalBluePoints += points.intValue();
		for(Integer points : this.redRoundsPoints.values())
			totalRedPoints += points.intValue();
		int totalBluePenalties = this.blueTotalPenalties.get();
		int totalRedPenalties = this.redTotalPenalties.get();
		this.matchWinner.set(MatchWinner.TIE);
		this.bestOf3WinnerLastRoundWithSuperiority = MatchWinner.TIE;
		if(this.bestOf3WinnerLastRoundByPUN) {
			this.matchFinalDecission.set(FinalDecision.SUP);
			this.matchWinner.set(this.bestOf3WinnerLastRoundByPUNWinner);
			this.bestOf3WinnerLastRoundWithSuperiority = this.bestOf3WinnerLastRoundByPUNWinner;
			this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
		} else if(totalBluePoints > totalRedPoints) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("ByRounds SUP Winner BLUE by total points");
			this.matchFinalDecission.set(FinalDecision.SUP);
			this.matchWinner.set(MatchWinner.BLUE);
			this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
		} else if(totalRedPoints > totalBluePoints) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("ByRounds SUP Winner RED by total points");
			this.matchFinalDecission.set(FinalDecision.SUP);
			this.matchWinner.set(MatchWinner.RED);
			this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
		} else if(this.blueTechPoints.getValue().intValue() > this.redTechPoints.getValue().intValue()) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("ByRounds SUP Winner BLUE by TURNING");
			this.matchFinalDecission.set(FinalDecision.SUP);
			this.matchWinner.set(MatchWinner.BLUE);
			this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
		} else if(this.redTechPoints.getValue().intValue() > this.blueTechPoints.getValue().intValue()) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("ByRounds SUP Winner RED by TURNING");
			this.matchFinalDecission.set(FinalDecision.SUP);
			this.matchWinner.set(MatchWinner.RED);
			this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
		} else if(totalRedPenalties > totalBluePenalties) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("ByRounds SUP Winner BLUE by PENALTIES");
			this.matchFinalDecission.set(FinalDecision.SUP);
			this.matchWinner.set(MatchWinner.BLUE);
			this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
		} else if(totalBluePenalties > totalRedPenalties) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("ByRounds SUP Winner RED by PENALTIES");
			this.matchFinalDecission.set(FinalDecision.SUP);
			this.matchWinner.set(MatchWinner.RED);
			this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
		} else {
			this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
		}
		this.superiorityByRounds.set(true);
	}

	boolean reviseEvents() {
		return (this.appStatusWorker.getMatchAllowed() && ! this.backupSystemEnabled.get());
	}

	@Override
	public final void afterPropertiesSet() throws Exception {
		this.redGoldenPointImpacts.addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				BaseCommonMatchWorker.matchWorkerLogger.info("New RED Impact -> " + newValue);
			}
		});
		this.blueGoldenPointImpacts.addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				BaseCommonMatchWorker.matchWorkerLogger.info("New BLUE Impact -> " + newValue);
			}
		});
		this.allowNetworkError.addListener((observable, oldValue, newValue) -> {
			if(newValue.booleanValue())
				hasNetworkOkEvent(new GlobalNetworkStatusControllerListener.NetworkOkEvent(Long.valueOf(System.currentTimeMillis())));
		});
		this.roundCountdown = new TkStrikeCountdownNoUI("roundCountdown", TkStrikeScoreboardGraphicDetailType.HIGH_GRAPHIC_DETAIL.equals(
				this.scoreboardGraphicDetailType) ? this.countdownMillisRefreshType : CountdownMillisRefreshType.NONE);
		this.kyeShiCountdown = new TkStrikeCountdownNoUI("kyeShiCountdown", CountdownMillisRefreshType.MEDIUM);
		this.restTimeCountdown = new TkStrikeCountdownNoUI("restTimeCountdown", CountdownMillisRefreshType.MEDIUM);
		this.paraTimeOutCountdown = new TkStrikeCountdownNoUI("paraTimeOutCountdown", CountdownMillisRefreshType.MEDIUM);
		getGlobalNetworkStatusController().addListener(this);
		this.appStatusWorker.networkConfigurationChanges().addListener((observableValue, aBoolean, t1) -> {
			if( ! this.lock.get() && t1.booleanValue()) {
				if(matchWorkerLogger.isDebugEnabled())
					matchWorkerLogger.debug("Changes on Network Configuration...");
				updateNetworkConfiguration();
			}
		});
		this.appStatusWorker.matchConfigurationChanges().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
				if( ! BaseCommonMatchWorker.this.lock.get() &&
						t1.booleanValue()) {
					if(BaseCommonMatchWorker.matchWorkerLogger.isDebugEnabled())
						BaseCommonMatchWorker.matchWorkerLogger.debug("Changes on Match Configuration...");
					BaseCommonMatchWorker.this.updateInternalNodes();
				}
			}
		});
		this.appStatusWorker.resetMatchWithMatchLogId().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue != null) {
					if(BaseCommonMatchWorker.matchWorkerLogger.isDebugEnabled())
						BaseCommonMatchWorker.matchWorkerLogger.debug("Call to resetMatchWithMatchLogId...");
					BaseCommonMatchWorker.this.lock.set(true);
					BaseCommonMatchWorker.this.updateInternalNodes();
					BaseCommonMatchWorker.this.restartMatchByMatchLog(newValue);
					if(BaseCommonMatchWorker.matchWorkerLogger.isDebugEnabled()) {
						BaseCommonMatchWorker.matchWorkerLogger.info("Current Match Status ->" + BaseCommonMatchWorker.this.currentMatchStatus.get());
						BaseCommonMatchWorker.matchWorkerLogger.info("App Last Status " + BaseCommonMatchWorker.this.appStatusWorker
								.getLastAppStatusId());
						BaseCommonMatchWorker.matchWorkerLogger.info("Contains NETWORK_ERROR?" + ArrayUtils.contains(
								BaseCommonMatchWorker.this.appStatusWorker.getCurrentAppStatuses(), AppStatusId.NETWORK_ERROR));
						BaseCommonMatchWorker.matchWorkerLogger.info("================================================");
					}
					boolean prevNetworkError = ArrayUtils.contains(BaseCommonMatchWorker.this.appStatusWorker.getCurrentAppStatuses(),
							AppStatusId.NETWORK_ERROR);
					BaseCommonMatchWorker.this.appStatusWorker.setMatchConfigurationChanges(Boolean.TRUE);
					BaseCommonMatchWorker.this.appStatusWorker.setMatchConfigurationChanges(Boolean.FALSE);
					if(prevNetworkError) {
						BaseCommonMatchWorker.this.appStatusWorker.addAppStatusOk(AppStatusId.NETWORK_ERROR);
						BaseCommonMatchWorker.this.currentMatchStatus.setValue(MatchStatusId.NETWORK_ERROR);
					}
					BaseCommonMatchWorker.this.lock.set(false);
				}
			}
		});
		this.appStatusWorker.soundConfigurationChanges().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
				if(t1.booleanValue()) {
					if(BaseCommonMatchWorker.matchWorkerLogger.isDebugEnabled())
						BaseCommonMatchWorker.matchWorkerLogger.debug("Changes on Sounds Configuration...");
					BaseCommonMatchWorker.this.initializeSoundsConfiguration();
				}
			}
		});
		this.appStatusWorker.rulesChanges().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
				if(t1.booleanValue()) {
					if(BaseCommonMatchWorker.matchWorkerLogger.isDebugEnabled())
						BaseCommonMatchWorker.matchWorkerLogger.debug("Changes on Rules Configuration...");
					BaseCommonMatchWorker.this.initializeRulesConfiguration();
				}
			}
		});
		final TkStrikeCommunicationListener tkStrikeCommunicationListener = this;
		this.appStatusWorker.matchAllowed().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
				if(newValue.booleanValue()) {
					if(BaseCommonMatchWorker.matchWorkerLogger.isDebugEnabled())
						BaseCommonMatchWorker.matchWorkerLogger.debug("match allowed!!! CurrentMatch " + BaseCommonMatchWorker.this
								.getCurrentMatchStatus() + " AppStatus " + BaseCommonMatchWorker.this.appStatusWorker
										.getLastAppStatusId());
					BaseCommonMatchWorker.this.validateCanStartMatch();
					BaseCommonMatchWorker.this.tkStrikeCommunicationService.removeListener(tkStrikeCommunicationListener);
					BaseCommonMatchWorker.this.tkStrikeCommunicationService.addListener(tkStrikeCommunicationListener);
				}
			}
		});
		this.currentMatchStatus.addListener(new ChangeListener<MatchStatusId>() {

			@Override
			public void changed(ObservableValue<? extends MatchStatusId> observable, MatchStatusId oldValue, MatchStatusId newValue) {
				BaseCommonMatchWorker.this.prevMatchStatus.set(oldValue);
			}
		});
		this.bluePoints.addListener(getBluePointsChangeListener());
		this.bluePenalties.addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newPenalties) {
				BaseCommonMatchWorker.this.redLastPointIsPenalty = true;
				BaseCommonMatchWorker.matchWorkerLogger.debug("Blue Penalties change newPenalties " + newPenalties);
				BaseCommonMatchWorker.this._calculateRedGeneralPoints();
			}
		});
		this.blueGoldenPointPenalties.addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newValue) {
				if(newValue.intValue() >= 0)
					BaseCommonMatchWorker.this._calculateRedGeneralPoints();
			}
		});
		this.blueGeneralPoints.addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if(MatchVictoryCriteria.BESTOF3.equals(BaseCommonMatchWorker.this.getMatchVictoryCriteria())
						&& BaseCommonMatchWorker.this.bestOf3UpdatingFromScoreboardEditor.booleanValue()) {
					BaseCommonMatchWorker.matchWorkerLogger.info(
							"blueGeneralPoints changed - on BESTOF3 while Updating from scoreboard, do nothing extra!");
				} else {
					int bluePrevRoundsPoints = 0;
					int roundToChange = BaseCommonMatchWorker.this.currentRound.get();
					boolean reCalculateWinner = false;
					if(roundToChange > 1 && MatchVictoryCriteria.CONVENTIONAL
							.equals(BaseCommonMatchWorker.this.matchVictoryCriteria) && MatchStatusId.WAITING_4_START_ROUND
									.equals(BaseCommonMatchWorker.this.getCurrentMatchStatus())) {
						if(BaseCommonMatchWorker.matchWorkerLogger.isDebugEnabled())
							BaseCommonMatchWorker.matchWorkerLogger.info(
									"*** WAITING 4 START A NEW ROUND ON CONVENTIONAL CHANGE MUST BE ON PREV ROUND ***");
						roundToChange--;
						reCalculateWinner = true;
					}
					if(roundToChange >= 1 && MatchStatusId.ROUND_FINISHED
							.equals(BaseCommonMatchWorker.this.getCurrentMatchStatus()) && MatchVictoryCriteria.BESTOF3
									.equals(BaseCommonMatchWorker.this.matchVictoryCriteria)) {
						BaseCommonMatchWorker.matchWorkerLogger.info(
								"*** ROUND FINISHED AND CHANGES IN POINTS ON BEST OF 3, GO TO CHANGE WINNER OF ROUND ***");
						reCalculateWinner = true;
					}
					if(roundToChange > 1 && MatchVictoryCriteria.CONVENTIONAL.equals(BaseCommonMatchWorker.this.matchVictoryCriteria))
						for(int i = roundToChange - 1; i > 0; i--) {
							if(BaseCommonMatchWorker.this.blueRoundsPoints.containsKey(Integer.valueOf(i))
									&& BaseCommonMatchWorker.this.blueRoundsPoints.get(Integer.valueOf(i)).intValue() > 0)
								bluePrevRoundsPoints += BaseCommonMatchWorker.this.blueRoundsPoints.get(Integer.valueOf(i)).intValue();
						}
					BaseCommonMatchWorker.this.blueRoundsPoints.put(Integer.valueOf(roundToChange), Integer.valueOf(newValue.intValue()
							- bluePrevRoundsPoints));
					if(reCalculateWinner)
						BaseCommonMatchWorker.this.changeRoundWinner(roundToChange);
				}
			}
		});
		this.redPoints.addListener(getRedPointsChangeListener());
		this.redPenalties.addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newPenalties) {
				BaseCommonMatchWorker.this.blueLastPointIsPenalty = true;
				BaseCommonMatchWorker.matchWorkerLogger.debug("Red Penalties change newPenalties " + newPenalties);
				BaseCommonMatchWorker.this._calculateBlueGeneralPoints();
			}
		});
		this.redGoldenPointPenalties.addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newValue) {
				if(newValue.intValue() >= 0)
					BaseCommonMatchWorker.this._calculateBlueGeneralPoints();
			}
		});
		this.redGeneralPoints.addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if(MatchVictoryCriteria.BESTOF3.equals(BaseCommonMatchWorker.this.getMatchVictoryCriteria())
						&& BaseCommonMatchWorker.this.bestOf3UpdatingFromScoreboardEditor.booleanValue()) {
					BaseCommonMatchWorker.matchWorkerLogger.info(
							"redGeneralPoints changed - on BESTOF3 while Updating from scoreboard, do nothing extra!");
				} else {
					int redPrevRoundsPoints = 0;
					int roundToChange = BaseCommonMatchWorker.this.currentRound.get();
					boolean reCalculateWinner = false;
					if(roundToChange > 1 && MatchVictoryCriteria.CONVENTIONAL
							.equals(BaseCommonMatchWorker.this.matchVictoryCriteria) && MatchStatusId.WAITING_4_START_ROUND
									.equals(BaseCommonMatchWorker.this.getCurrentMatchStatus())) {
						if(BaseCommonMatchWorker.matchWorkerLogger.isDebugEnabled())
							BaseCommonMatchWorker.matchWorkerLogger.info(
									"*** WAITING 4 START A NEW ROUND ON CONVENTIONAL CHANGE MUST BE ON PREV ROUND ***");
						roundToChange--;
						reCalculateWinner = true;
					}
					if(roundToChange >= 1 && MatchStatusId.ROUND_FINISHED
							.equals(BaseCommonMatchWorker.this.getCurrentMatchStatus()) && MatchVictoryCriteria.BESTOF3
									.equals(BaseCommonMatchWorker.this.matchVictoryCriteria)) {
						BaseCommonMatchWorker.matchWorkerLogger.info(
								"*** ROUND FINISHED AND CHANGES IN POINTS ON BEST OF 3, GO TO CHANGE WINNER OF ROUND ***");
						reCalculateWinner = true;
					}
					if(roundToChange > 1 && MatchVictoryCriteria.CONVENTIONAL.equals(BaseCommonMatchWorker.this.matchVictoryCriteria))
						for(int i = roundToChange - 1; i > 0; i--) {
							if(BaseCommonMatchWorker.this.redRoundsPoints.containsKey(Integer.valueOf(i))
									&& BaseCommonMatchWorker.this.redRoundsPoints.get(Integer.valueOf(i)).intValue() > 0)
								redPrevRoundsPoints += BaseCommonMatchWorker.this.redRoundsPoints.get(Integer.valueOf(i)).intValue();
						}
					BaseCommonMatchWorker.this.redRoundsPoints.put(Integer.valueOf(roundToChange), Integer.valueOf(newValue.intValue()
							- redPrevRoundsPoints));
					if(reCalculateWinner)
						BaseCommonMatchWorker.this.changeRoundWinner(roundToChange);
				}
			}
		});
		initializeSoundsConfiguration();
		initializeRulesConfiguration();
		this.roundCountdown.workingProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if(BaseCommonMatchWorker.this.wtUDPService.isConnected()) {
					String roundCountdownStr = BaseCommonMatchWorker.this.sdfRoundTimeNoMillis.format(new Date(
							BaseCommonMatchWorker.this.roundCountdown.getCurrentTimeMillis()));
					BaseCommonMatchWorker.this.wtUDPService.sendRoundCountdownChange(roundCountdownStr, newValue.booleanValue()
							? WtUDPService.ClockAction.START
							: WtUDPService.ClockAction.STOP);
				}
			}
		});
		this.roundCountdown.finishedProperty().addListener(getRoundCountdownFinishedPropertyChangeListener());
		this.roundCountdown.currentTimeMillisProperty().addListener(new CountdownListenerAndExternalEventsListenerNotification("ROUNDCOUNTDOWN",
				MatchStatusId.ROUND_WORKING));
		this.roundCountdown.currentTimeMillisProperty().addListener(new ChangeListener<Number>() {

			private final Calendar calendar = Calendar.getInstance();

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				this.calendar.setTimeInMillis(newValue.longValue());
				if(this.calendar.get(14) == 999L &&
						BaseCommonMatchWorker.this.wtUDPService.isConnected()) {
					String roundCountdown = BaseCommonMatchWorker.this.sdfRoundTimeNoMillis.format(new Date(newValue.longValue()));
					BaseCommonMatchWorker.this.wtUDPService.sendRoundCountdownChange(roundCountdown, WtUDPService.ClockAction.CHANGE);
				}
			}
		});
		this.kyeShiCountdown.currentTimeMillisProperty().addListener(new CountdownListenerAndExternalEventsListenerNotification("KYESHI",
				MatchStatusId.ROUND_KYESHI));
		this.kyeShiCountdown.currentTimeMillisProperty().addListener(new ChangeListener<Number>() {

			private final Calendar calendar = Calendar.getInstance();

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				this.calendar.setTimeInMillis(newValue.longValue());
				if(this.calendar.get(14) == 999L &&
						BaseCommonMatchWorker.this.wtUDPService.isConnected()) {
					final String roundCountdown = BaseCommonMatchWorker.this.sdfRoundTimeNoMillis.format(new Date(newValue.longValue()));
					TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

						@Override
						public Void call() throws Exception {
							BaseCommonMatchWorker.matchWorkerLogger.info("To SEND RoundCountdown to UDP " + roundCountdown);
							BaseCommonMatchWorker.this.wtUDPService.sendKyeShi(roundCountdown, WtUDPService.ClockAction.CHANGE);
							return null;
						}
					});
				}
			}
		});
		this.roundCountdown.currentTimeMillisProperty().addListener(new CountdownListenerAndExternalEventsListenerNotification("GOLDENPOINTCOUNTDOWN",
				MatchStatusId.ROUND_IN_GOLDENPOINT));
		this.restTimeCountdown.currentTimeMillisProperty().addListener(new ChangeListener<Number>() {

			private Calendar calendar = Calendar.getInstance();

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number prevTimeMillis, Number newTimeMillis) {
				this.calendar.setTimeInMillis(newTimeMillis.longValue());
				if(this.calendar.get(14) == 999L &&
						BaseCommonMatchWorker.this.wtUDPService.isConnected()) {
					final String roundCountdown = BaseCommonMatchWorker.this.sdfRoundTimeNoMillis.format(new Date(newTimeMillis.longValue()));
					TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

						@Override
						public Void call() throws Exception {
							BaseCommonMatchWorker.this.wtUDPService.sendRestCountdownChange(roundCountdown, WtUDPService.ClockAction.CHANGE);
							return null;
						}
					});
				}
			}
		});
		this.restTimeCountdown.currentTimeAsStringProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observableValue, String s, String currentTimeAsString) {
				if(BaseCommonMatchWorker.this.playSoundBeforeStartRound > 0 && BaseCommonMatchWorker.this.playSoundBeforeStartRoundString != null &&
						BaseCommonMatchWorker.this.playSoundBeforeStartRoundString.equals(currentTimeAsString))
					TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

						@Override
						public Void call() throws Exception {
							BaseCommonMatchWorker.this.getSoundPlayerService().playSoundBeforeStartRound();
							return null;
						}
					});
			}
		});
		this.kyeShiCountdown.workingProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, final Boolean newValue) {
				if(BaseCommonMatchWorker.this.wtUDPService.isConnected()) {
					final String kyeShiCountdown = BaseCommonMatchWorker.this.sdfRoundTimeNoMillis.format(new Date(
							BaseCommonMatchWorker.this.kyeShiCountdown.getCurrentTimeMillis()));
					TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

						@Override
						public Void call() throws Exception {
							BaseCommonMatchWorker.this.wtUDPService.sendKyeShi(kyeShiCountdown, newValue.booleanValue()
									? WtUDPService.ClockAction.START
									: WtUDPService.ClockAction.STOP);
							return null;
						}
					});
				}
			}
		});
		this.kyeShiCountdown.finishedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
				if(t1.booleanValue()) {
					if(BaseCommonMatchWorker.matchWorkerLogger.isDebugEnabled())
						BaseCommonMatchWorker.matchWorkerLogger.debug("Kye-shi Countdown Finished!");
					if(MatchStatusId.NETWORK_ERROR.equals(BaseCommonMatchWorker.this.matchStatusBeforeKyeShi)) {
						BaseCommonMatchWorker.this.currentMatchStatus.set(MatchStatusId.NETWORK_ERROR);
					} else {
						BaseCommonMatchWorker.this.doPauseRound();
					}
					if(BaseCommonMatchWorker.this.wtUDPService.isConnected())
						TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

							@Override
							public Void call() throws Exception {
								BaseCommonMatchWorker.this.wtUDPService.sendKyeShi("0:00", WtUDPService.ClockAction.END);
								return null;
							}
						});
				}
			}
		});
		this.restTimeCountdown.finishedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
				if(t1.booleanValue()) {
					if(BaseCommonMatchWorker.matchWorkerLogger.isDebugEnabled())
						BaseCommonMatchWorker.matchWorkerLogger.debug("Rest time Coutndown Finished!");
					TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

						@Override
						public Void call() throws Exception {
							BaseCommonMatchWorker.this.getSoundPlayerService().playSoundEndOfTime();
							return null;
						}
					});
					if(BaseCommonMatchWorker.this.wtUDPService.isConnected())
						TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

							@Override
							public Void call() throws Exception {
								BaseCommonMatchWorker.this.wtUDPService.sendRestCountdownChange("0:00", WtUDPService.ClockAction.END);
								return null;
							}
						});
					if(BaseCommonMatchWorker.this._hasNextRound())
						BaseCommonMatchWorker.this.moveToNextRound();
				}
			}
		});
		this.paraTimeOutCountdown.workingProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if(BaseCommonMatchWorker.this.wtUDPService.isConnected()) {
					String roundCountdownStr = BaseCommonMatchWorker.this.sdfRoundTimeNoMillis.format(new Date(
							BaseCommonMatchWorker.this.paraTimeOutCountdown
									.getCurrentTimeMillis()));
					boolean isBlue = (BaseCommonMatchWorker.this.athleteWithPARATimeOutCountdownWorking == 1);
					BaseCommonMatchWorker.this.wtUDPService.sendPARATimeOutCountdownChange(isBlue, roundCountdownStr, newValue.booleanValue()
							? WtUDPService.ClockAction.START
							: WtUDPService.ClockAction.STOP);
				}
			}
		});
		this.paraTimeOutCountdown.currentTimeMillisProperty().addListener(new ChangeListener<Number>() {

			private Calendar calendar = Calendar.getInstance();

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number prevTimeMillis, Number newTimeMillis) {
				this.calendar.setTimeInMillis(newTimeMillis.longValue());
				if(this.calendar.get(14) == 999L &&
						BaseCommonMatchWorker.this.wtUDPService.isConnected()) {
					final boolean isBlue = (BaseCommonMatchWorker.this.athleteWithPARATimeOutCountdownWorking == 1);
					final String roundCountdown4Send = BaseCommonMatchWorker.this.sdfRoundTimeNoMillis.format(new Date(newTimeMillis.longValue()));
					TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

						@Override
						public Void call() throws Exception {
							BaseCommonMatchWorker.this.wtUDPService.sendPARATimeOutCountdownChange(isBlue, roundCountdown4Send,
									WtUDPService.ClockAction.CHANGE);
							return null;
						}
					});
				}
			}
		});
		this.paraTimeOutCountdown.finishedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
				if(t1.booleanValue()) {
					if(BaseCommonMatchWorker.matchWorkerLogger.isDebugEnabled())
						BaseCommonMatchWorker.matchWorkerLogger.debug("PARA TimeOut Countdown Finished!");
					BaseCommonMatchWorker.this.paraTimeoutCountdownStopOrFinished();
				}
			}
		});
		this.currentRound.addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, final Number t1) {
				BaseCommonMatchWorker.this.currentRoundStr.set("" + t1);
				if(BaseCommonMatchWorker.this.wtUDPService.isConnected())
					TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

						@Override
						public Void call() throws Exception {
							if(t1.intValue() >= 1 &&
									! MatchStatusId.WAITING_4_MATCH.equals(BaseCommonMatchWorker.this.currentMatchStatus.get()))
								BaseCommonMatchWorker.this.wtUDPService.sendRoundNumber(Integer.valueOf(t1.intValue()));
							return null;
						}
					});
			}
		});
		this.scoreboardEditorOpen.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean value) {
				if(value.booleanValue()) {
					BaseCommonMatchWorker.this._addMatchLogItem(Long.valueOf(System.currentTimeMillis()),
							Long.valueOf(BaseCommonMatchWorker.this.getCurrentRoundCountdownAsMillis()),
							Long.valueOf(System.currentTimeMillis()), BaseCommonMatchWorker.this

									.getRound4MatchLog(false), BaseCommonMatchWorker.this
											.getRoundStr4MatchLog(false), MatchLogItemType.SCOREBOARD,

							Integer.valueOf(0),
							Integer.valueOf(0), "", true,
							Integer.valueOf(0), false);
					BaseCommonMatchWorker.this.scoreboardEditorOpen.set(false);
				}
			}
		});
		this.finalResultOpen.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean value) {
				if(value.booleanValue()) {
					BaseCommonMatchWorker.this._addMatchLogItem(Long.valueOf(System.currentTimeMillis()),
							Long.valueOf(BaseCommonMatchWorker.this.getCurrentRoundCountdownAsMillis()),
							Long.valueOf(System.currentTimeMillis()), BaseCommonMatchWorker.this
									.getRound4MatchLog(true), BaseCommonMatchWorker.this.getRoundStr4MatchLog(true),
							MatchLogItemType.MATCH_FINAL_NEEDS_CONFIRM_DECISION,
							Integer.valueOf(0),
							Integer.valueOf(0), "", true,
							Integer.valueOf(0), false);
					BaseCommonMatchWorker.this.finalResultOpen.set(false);
				}
			}
		});
		this.backupSystemEnabled.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if(MatchStatusId.NETWORK_ERROR.equals(BaseCommonMatchWorker.this.getCurrentMatchStatus())) {
					if(MatchStatusId.NOT_READY.equals(BaseCommonMatchWorker.this.statusBeforeNetworkError)) {
						BaseCommonMatchWorker.this.networkOkByGlobalController.set(true);
						BaseCommonMatchWorker.this.currentMatchStatus.setValue(MatchStatusId.WAITING_4_MATCH);
					} else {
						BaseCommonMatchWorker.this.currentMatchStatus.setValue(BaseCommonMatchWorker.this.statusBeforeNetworkError);
					}
					BaseCommonMatchWorker.this.appStatusWorker.addAppStatusOk(AppStatusId.NETWORK_RECOVERED);
				}
				BaseCommonMatchWorker.this._addMatchLogItem(Long.valueOf(System.currentTimeMillis()),
						Long.valueOf(BaseCommonMatchWorker.this.getCurrentRoundCountdownAsMillis()),
						Long.valueOf(System.currentTimeMillis()), BaseCommonMatchWorker.this
								.getRound4MatchLog(true), BaseCommonMatchWorker.this.getRoundStr4MatchLog(true), newValue.booleanValue()
										? MatchLogItemType.BACKUP_SYSTEM_ENABLED
										: MatchLogItemType.BACKUP_SYSTEM_DISABLED,
						Integer.valueOf(0),
						Integer.valueOf(0), "", false,

						Integer.valueOf(0), false);
			}
		});
		_afterPropertiesSet();
	}

	ChangeListener<Boolean> getRoundCountdownFinishedPropertyChangeListener() {
		return new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
				if(t1.booleanValue()) {
					if(BaseCommonMatchWorker.matchWorkerLogger.isDebugEnabled())
						BaseCommonMatchWorker.matchWorkerLogger.debug("Round Countdown Finished!");
					TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

						@Override
						public Void call() throws Exception {
							BaseCommonMatchWorker.this.getSoundPlayerService().playSoundEndOfTime();
							return null;
						}
					});
					if(BaseCommonMatchWorker.this.wtUDPService.isConnected()) {
						final Map<Integer, MatchWinner> fiRoundsWinner = new HashMap<>(BaseCommonMatchWorker.this.roundsWinner);
						TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

							@Override
							public Void call() throws Exception {
								BaseCommonMatchWorker.this.wtUDPService.sendRoundCountdownChange("0:00", WtUDPService.ClockAction.END);
								TimeUnit.MILLISECONDS.sleep(5L);
								BaseCommonMatchWorker.this.wtUDPService.sendWinnerPeriods(fiRoundsWinner);
								return null;
							}
						});
					}
					BaseCommonMatchWorker.this.endRound();
					if(BaseCommonMatchWorker.this.goldenPointEnabled && BaseCommonMatchWorker.this.goldenPointWorking) {
						BaseCommonMatchWorker.matchWorkerLogger.info("GoldenPoint Finished go to validate Superiority!");
						BaseCommonMatchWorker.this.validateGoldenPointTieBreaker();
					}
				}
			}
		};
	}

	ChangeListener<Number> getBluePointsChangeListener() {
		return new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newPoints) {
				if(newPoints.intValue() >= 0) {
					if((BaseCommonMatchWorker.this.goldenPointEnabled && BaseCommonMatchWorker.this.goldenPointWorking)
							|| MatchStatusId.WAITING_4_START_GOLDENPOINT
									.equals(BaseCommonMatchWorker.this.getCurrentMatchStatus())) {
						BaseCommonMatchWorker.this.goldenPointPointByPenalty = false;
						BaseCommonMatchWorker.this.goldenPointPointByPunch = false;
						BaseCommonMatchWorker.this.goldenPointPointByPenaltyValue = 0;
					}
					BaseCommonMatchWorker.this._calculateBlueGeneralPoints();
					BaseCommonMatchWorker.this.blueLastPointIsPenalty = false;
				}
			}
		};
	}

	ChangeListener<Number> getRedPointsChangeListener() {
		return new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newPoints) {
				if(newPoints.intValue() >= 0) {
					if((BaseCommonMatchWorker.this.goldenPointEnabled && BaseCommonMatchWorker.this.goldenPointWorking)
							|| MatchStatusId.WAITING_4_START_GOLDENPOINT
									.equals(BaseCommonMatchWorker.this.getCurrentMatchStatus())) {
						BaseCommonMatchWorker.this.goldenPointPointByPenalty = false;
						BaseCommonMatchWorker.this.goldenPointPointByPunch = false;
						BaseCommonMatchWorker.this.goldenPointPointByPenaltyValue = 0;
					}
					BaseCommonMatchWorker.this._calculateRedGeneralPoints();
					BaseCommonMatchWorker.this.redLastPointIsPenalty = false;
				}
			}
		};
	}

	void _calculateBlueGeneralPoints() {
		calculateGeneralPoints(true);
	}

	void _calculateRedGeneralPoints() {
		calculateGeneralPoints(false);
	}

	private void calculateGeneralPoints(boolean blue) {
		int penalties;
		if(blue) {
			penalties = this.goldenPointWorking ? (this.redGoldenPointPenalties.get() / getDividePenaltiesInGoldenPoint().intValue())
					: this.redPenalties.get();
		} else {
			penalties = this.goldenPointWorking ? (this.blueGoldenPointPenalties.get() / getDividePenaltiesInGoldenPoint().intValue())
					: this.bluePenalties.get();
		}
		calculateGeneralPoints(blue ? this.blueGeneralPoints : this.redGeneralPoints, blue ? this.bluePoints
				.get() : this.redPoints.get(), penalties);
		sendScoreChangeToWTUdpServiceIfNeeded();
		calculateBestOf3PartialRoundWinner();
	}

	private void calculateBestOf3PartialRoundWinner() {
		if(MatchVictoryCriteria.BESTOF3.equals(this.matchVictoryCriteria)) {
			matchWorkerLogger.debug("calculateBestOf3PartialRoundWinner");
			if(this.blueGeneralPoints.get() > this.redGeneralPoints.get()) {
				matchWorkerLogger.debug("calculateBestOf3PartialRoundWinner BLUE by points");
				this.bestOf3CurrentRoundPartialWinner.set(MatchWinner.BLUE);
			} else if(this.redGeneralPoints.get() > this.blueGeneralPoints.get()) {
				matchWorkerLogger.debug("calculateBestOf3PartialRoundWinner RED by points");
				this.bestOf3CurrentRoundPartialWinner.set(MatchWinner.RED);
			} else if(this.redGeneralPoints.get() == this.blueGeneralPoints.get()) {
				matchWorkerLogger.debug("calculateBestOf3PartialRoundWinner TIE by points, go to supperiority");
				this.bestOf3CurrentRoundPartialWinner.set(MatchWinner.TIE);
				BestOf3RoundSuperiority bestOf3RoundSuperiority1 = calculateBestOf3RoundWinnerSuperiority(
						Integer.valueOf(this.currentRound.get()));
				if(bestOf3RoundSuperiority1.getRoundWinner() != null)
					this.bestOf3CurrentRoundPartialWinner.set(bestOf3RoundSuperiority1.getRoundWinner());
			}
		}
	}

	private void sendScoreChangeToWTUdpServiceIfNeeded() {
		if(this.wtUDPService.isConnected() &&
				! MatchStatusId.NETWORK_ERROR.equals(getCurrentMatchStatus()) &&
				! MatchStatusId.NOT_READY.equals(getCurrentMatchStatus()) &&
				! MatchStatusId.WAITING_4_MATCH.equals(getCurrentMatchStatus()) &&
				! MatchStatusId.WAITING_4_START_ROUND.equals(getCurrentMatchStatus()) &&
				! MatchStatusId.WAITING_4_START_GOLDENPOINT.equals(getCurrentMatchStatus())) {
			final BestOf3PointsChange bestOf3PointsChange = genearetBestOf3PointsChange();
			final Integer bluePoints2Send = Integer.valueOf(this.blueGeneralPoints.get());
			final Integer redPoints2Send = Integer.valueOf(this.redGeneralPoints.get());
			TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					BaseCommonMatchWorker.this.wtUDPService.sendScoreChange(bluePoints2Send, redPoints2Send);
					if(MatchVictoryCriteria.BESTOF3.equals(BaseCommonMatchWorker.this.matchVictoryCriteria)) {
						BaseCommonMatchWorker.matchWorkerLogger.debug("send to WT udp service bestof3 score change " + bestOf3PointsChange);
						BaseCommonMatchWorker.this.wtUDPService.sendBestOf3ScoreChange(bestOf3PointsChange);
					}
					return null;
				}
			});
		}
	}

	private BestOf3PointsChange genearetBestOf3PointsChange() {
		return new BestOf3PointsChange(
				Integer.valueOf((this.currentRound.get() == 1) ? this.blueGeneralPoints.get()
						: this.blueRoundsPoints.get(Integer.valueOf(1)).intValue()),
				Integer.valueOf((this.currentRound.get() == 2) ? this.blueGeneralPoints.get()
						: this.blueRoundsPoints.get(Integer.valueOf(2)).intValue()),
				Integer.valueOf((this.currentRound.get() == 3) ? this.blueGeneralPoints.get()
						: this.blueRoundsPoints.get(Integer.valueOf(3)).intValue()),
				Integer.valueOf((this.currentRound.get() == 1) ? this.redGeneralPoints.get()
						: this.redRoundsPoints.get(Integer.valueOf(1)).intValue()),
				Integer.valueOf((this.currentRound.get() == 2) ? this.redGeneralPoints.get()
						: this.redRoundsPoints.get(Integer.valueOf(2)).intValue()),
				Integer.valueOf((this.currentRound.get() == 3) ? this.redGeneralPoints.get()
						: this.redRoundsPoints.get(Integer.valueOf(3)).intValue()));
	}

	void calculateGeneralPoints(SimpleIntegerProperty generalPoints, int points, int penalties) {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("calculateGeneralPoints goldenPointEnabled->" + this.goldenPointEnabled + " " + "goldenPointWorking->"
					+ this.goldenPointWorking + " " + "gamJeomShowPointsOnGoldenPoint->" + this.gamJeomShowPointsOnGoldenPoint + " "
					+ "getCurrentMatchStatus->" +

					getCurrentMatchStatus() + " " + "generalPoints->" + generalPoints
							.get() + " " + "points->" + points + " " + "penalties->" + penalties);
		if((this.goldenPointEnabled && this.goldenPointWorking) || MatchStatusId.WAITING_4_START_GOLDENPOINT
				.equals(getCurrentMatchStatus())) {
			generalPoints.setValue(Integer.valueOf(points + ((penalties <= 1) ? 0 : penalties)));
			matchWorkerLogger.info("**** " + ((penalties <= 1) ? 0 : penalties) + " -- NEW POINTS:" + generalPoints.get());
		} else {
			generalPoints.set(points + penalties);
		}
		if(MatchVictoryCriteria.BESTOF3.equals(getMatchVictoryCriteria()) && this.bestOf3UpdatingFromScoreboardEditor.booleanValue()) {
			matchWorkerLogger.info("calculateGeneralPoints - on BESTOF3 while Updating from scoreboard, do nothing extra!");
		} else if(MatchVictoryCriteria.BESTOF3.equals(getMatchVictoryCriteria()) && MatchStatusId.ROUND_FINISHED.equals(getCurrentMatchStatus())) {
			matchWorkerLogger.debug("calculateGeneralPoints - on BESTOF3 ROUND FINNISHED. no need to validate match");
		} else {
			validateMatch();
		}
		if(MatchStatusId.ROUND_FINISHED.equals(getCurrentMatchStatus()) && MatchVictoryCriteria.CONVENTIONAL
				.equals(this.matchVictoryCriteria))
			setCurrentRoundWinner(this.blueRoundsPoints.get(this.currentRound.getValue()).intValue(), this.redRoundsPoints
					.get(this.currentRound.getValue()).intValue(), true);
	}

	final void validateMatch() {
		matchWorkerLogger.info("validateMatch:MatchStatus:" + getCurrentMatchStatus() + ":Round:" + this.currentRound.get());
		matchWorkerLogger.info("validateMatch:BlueGeneralPoints:" + this.blueGeneralPoints.get() + " - BluePenalties: " + ( ! this.goldenPointWorking
				? this.bluePenalties
						.get()
				: this.blueGoldenPointPenalties.get()) + " - BluePoints:" + this.bluePoints
						.get());
		matchWorkerLogger.info("validateMatch:RedGeneralPoints:" + this.redGeneralPoints.get() + " - RedPenalties: " + ( ! this.goldenPointWorking
				? this.redPenalties
						.get()
				: this.redGoldenPointPenalties.get()) + " - RedPoints:" + this.redPoints
						.get());
		if(getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) ||
				getCurrentMatchStatus().equals(MatchStatusId.ROUND_PAUSED) ||

				getCurrentMatchStatus().equals(MatchStatusId.ROUND_FINISHED) ||
				getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT)) {
			MutableBoolean setRoundWinner = new MutableBoolean(Boolean.FALSE);
			boolean bestOf3Match = MatchVictoryCriteria.BESTOF3.equals(this.matchVictoryCriteria);
			boolean end = validateIfMatchFinish(Integer.valueOf(this.blueGeneralPoints.get()),
					Integer.valueOf( ! this.goldenPointWorking ? this.bluePenalties.get() : this.blueGoldenPointPenalties.get()),
					Integer.valueOf(this.bluePoints.get()),
					Integer.valueOf(this.redGeneralPoints.get()),
					Integer.valueOf( ! this.goldenPointWorking ? this.redPenalties.get() : this.redGoldenPointPenalties.get()),
					Integer.valueOf(this.redPoints.get()), setRoundWinner, this.matchFinalDecission, this.matchWinner);
			if(end && isGoldenPointWorking()) {
				Double bluePenalties = Double.valueOf(this.blueGoldenPointPenalties.get() / getDividePenaltiesInGoldenPoint().intValue());
				Double redPenalties = Double.valueOf(this.redGoldenPointPenalties.get() / getDividePenaltiesInGoldenPoint().intValue());
				if(matchWorkerLogger.isDebugEnabled())
					matchWorkerLogger.debug("Finish golden point?" + end);
				if( ! this.isTkStrikeKTAVersion.booleanValue()) {
					if(matchWorkerLogger.isDebugEnabled()) {
						matchWorkerLogger.debug("Match finished by GoldenPoint, update generalPoints");
						matchWorkerLogger.debug("Update \n\t blue->" + (this.bluePoints
								.get() + this.redGoldenPointPenalties.get()) + "\n\t red->" + (this.redPoints
										.get() + this.blueGoldenPointPenalties.get()));
					}
					this.blueGeneralPoints.setValue(Integer.valueOf(this.bluePoints.get() + this.redGoldenPointPenalties.get()));
					this.redGeneralPoints.setValue(Integer.valueOf(this.redPoints.get() + this.blueGoldenPointPenalties.get()));
				} else {
					matchWorkerLogger.info("Current version is KTA no update generalPoints");
					if(MatchWinner.BLUE.equals(getMatchWinner())) {
						if((this.blueGoldenPointPunches.get() * this.punchPoints) + redPenalties.doubleValue() >= _getGoldenPointFinishPoints()
								.intValue()) {
							matchWorkerLogger.info("Victory BLUE based on Punch + penalties. Add Penalties to points.");
							this.blueGeneralPoints.setValue(Double.valueOf((this.blueGoldenPointPunches.get() * this.punchPoints) + redPenalties
									.doubleValue()));
						}
					} else if(MatchWinner.RED.equals(getMatchWinner()) && (this.redGoldenPointPunches.get() * this.punchPoints) + bluePenalties
							.doubleValue() >= _getGoldenPointFinishPoints().intValue()) {
						matchWorkerLogger.info("Victory RED based on Punch + penalties. Add Penalties to points");
						this.redGeneralPoints.setValue(Double.valueOf((this.redGoldenPointPunches.get() * this.punchPoints) + bluePenalties
								.doubleValue()));
					}
				}
			}
			if(end) {
				if( ! bestOf3Match) {
					matchWorkerLogger.debug("S'HA INDICAT END ANEM A FER DOPAUSEROUND()!!!!");
					doPauseRound();
					if( ! this.matchWinnerByPointGapNeedsConfirmation.get())
						this.currentMatchStatus.set(MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION);
				} else {
					matchWorkerLogger.debug("S'HA INDICAT END ESTEM EN BESTOF3 FINALITZEM ROUND!!!!");
					TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

						@Override
						public Void call() throws Exception {
							BaseCommonMatchWorker.this.getSoundPlayerService().playSoundEndOfTime();
							return null;
						}
					});
					endRound();
				}
			} else if(this.roundFinishedOpen.get()) {
				matchWorkerLogger.info("validateMatch:roundFinishedOpen is opened roundCountDown=" + this.roundCountdown.getMinutes() + ":"
						+ this.roundCountdown.getSeconds() + "." + this.roundCountdown.getMilliseconds());
				if(this.roundCountdown.getMinutes() == 0 && this.roundCountdown.getSeconds() == 0 && this.roundCountdown.getMilliseconds() == 0L) {
					matchWorkerLogger.info("validateMatch:callTo:EndRound!");
					calculateCurrentRoundWinner(true, false);
				} else {
					matchWorkerLogger.info("validateMatch:callTo:PauseRound!");
					doPauseRound();
					this.roundFinishedOpen.set(false);
				}
			}
		}
	}

	private boolean validateIfMatchFinish(Integer blueGeneralPoints, Integer bluePenalties, Integer bluePoints, Integer redGeneralPoints,
			Integer redPenalties, Integer redPoints, MutableBoolean setRoundWinner, SimpleObjectProperty<FinalDecision> matchFinalDecission,
			SimpleObjectProperty<MatchWinner> matchWinner) {
		boolean end = false;
		boolean isBestOf3Match = MatchVictoryCriteria.BESTOF3.equals(this.matchVictoryCriteria);
		setRoundWinner.setValue(true);
		this.bestOf3WinnerLastRoundByPUN = false;
		this.bestOf3WinnerLastRoundByPUNWinner = MatchWinner.TIE;
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("validate match round " + getCurrentRound());
		if( ! this.goldenPointWorking) {
			matchWorkerLogger.debug("--- BLUE PENALTIES ->" + bluePenalties);
			matchWorkerLogger.debug("--- RED PENALTIES ->" + redPenalties);
			matchWorkerLogger.debug("--- MAX GAM JEOMS ALLOWED ->" + _getMaxGamJeomsAllowed());
			if(redPenalties.intValue() >= _getMaxGamJeomsAllowed().intValue()) {
				if( ! isBestOf3Match) {
					if(matchWorkerLogger.isDebugEnabled())
						matchWorkerLogger.debug("HA DE GUANYAR EL BLUE PER PUN!!!!");
					matchFinalDecission.set(FinalDecision.PUN);
					matchWinner.set(MatchWinner.BLUE);
				} else {
					if(matchWorkerLogger.isDebugEnabled())
						matchWorkerLogger.debug("EL BLUE GUANYA EL ROUND " + getCurrentRound() + " PER PUN!!!!");
					setRoundWinner(this.currentRound.get(), MatchWinner.BLUE, false);
					setRoundWinner.setValue(false);
					matchFinalDecission.set(FinalDecision.PUN);
					this.bestOf3WinnerLastRoundByPUN = true;
					this.bestOf3WinnerLastRoundByPUNWinner = MatchWinner.BLUE;
					this.bestOf3RoundWithPointGap.set(false);
					this.bestOf3RoundWithPointGap.set(true);
				}
				end = true;
			}
			if(bluePenalties.intValue() >= _getMaxGamJeomsAllowed().intValue()) {
				if( ! isBestOf3Match) {
					if(matchWorkerLogger.isDebugEnabled())
						matchWorkerLogger.debug("HA DE GUANYAR EL RED PER PUN!!!!");
					matchFinalDecission.set(FinalDecision.PUN);
					matchWinner.set(MatchWinner.RED);
				} else {
					if(matchWorkerLogger.isDebugEnabled())
						matchWorkerLogger.debug("EL RED GUANYA EL ROUND " + getCurrentRound() + " PER PUN!!!!");
					setRoundWinner(this.currentRound.get(), MatchWinner.RED, false);
					setRoundWinner.setValue(false);
					matchFinalDecission.set(FinalDecision.PUN);
					this.bestOf3WinnerLastRoundByPUN = true;
					this.bestOf3WinnerLastRoundByPUNWinner = MatchWinner.RED;
					this.bestOf3RoundWithPointGap.set(false);
					this.bestOf3RoundWithPointGap.set(true);
				}
				end = true;
			}
			if( ! end) {
				int bgp = blueGeneralPoints.intValue();
				int rgp = redGeneralPoints.intValue();
				if(isBestOf3Match || this.currentRound.get() > 2 || this.pointGapAllRounds) {
					if(matchWorkerLogger.isDebugEnabled())
						matchWorkerLogger.debug("Current round >2 validate differentialScore");
					end = finishMatchByDifferentialScore(isBestOf3Match);
					if(end)
						matchFinalDecission.set(FinalDecision.PTG);
				}
				if( ! end && getMatchCellingScore() > 0)
					if(bgp >= getMatchCellingScore()) {
						matchFinalDecission.set(FinalDecision.PTF);
						if( ! isBestOf3Match)
							matchWinner.set(MatchWinner.BLUE);
						end = true;
					} else if(rgp >= getMatchCellingScore()) {
						matchFinalDecission.set(FinalDecision.PTF);
						if( ! isBestOf3Match)
							matchWinner.set(MatchWinner.RED);
						end = true;
					}
			}
		} else {
			double dblBluePenalties = bluePenalties.intValue() / getDividePenaltiesInGoldenPoint().intValue();
			double dblRedPenalties = redPenalties.intValue() / getDividePenaltiesInGoldenPoint().intValue();
			if(matchWorkerLogger.isDebugEnabled()) {
				matchWorkerLogger.debug("GoldenPoint validate match ");
				matchWorkerLogger.debug("    BLUE Points =" + bluePoints + " Penalties =" + bluePenalties + " GeneralPoints=" + blueGeneralPoints);
				matchWorkerLogger.debug("    RED  Points =" + redPoints + " Penalties =" + redPenalties + " GeneralPoints=" + redGeneralPoints);
			}
			if(blueGeneralPoints.intValue() + ((bluePoints
					.intValue() > 0 && (int)dblRedPenalties == 1) ? 1 : 0) >= _getGoldenPointFinishPoints().intValue()) {
				matchFinalDecission.set(FinalDecision.GDP);
				matchWinner.set(MatchWinner.BLUE);
				end = true;
			} else if(redGeneralPoints.intValue() + ((redPoints
					.intValue() > 0 && (int)dblBluePenalties == 1) ? 1 : 0) >= _getGoldenPointFinishPoints().intValue()) {
				matchFinalDecission.set(FinalDecision.GDP);
				matchWinner.set(MatchWinner.RED);
				end = true;
			}
		}
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.info("ValidateMatch return:" + end);
		return end;
	}

	private int getMatchCellingScore() {
		return isParaTkdMatch() ? this.paraCellingScore : this.cellingScore;
	}

	final void updateNetworkConfiguration() {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("updateNetworkConfiguration() ");
		try {
			INetworkConfigurationEntry networkConfigurationEntry = this.appStatusWorker.getNetworkConfigurationEntry();
			this.nodesInNetwork = 0;
			if(networkConfigurationEntry != null) {
				this.numberOfJudges = 0;
				if(networkConfigurationEntry.getJudgesNumber().intValue() >= 1) {
					this.judge1Node = new JudgeNode(networkConfigurationEntry.getJudge1NodeId(), 1);
					this.nodesInNetwork++;
					this.numberOfJudges++;
				} else {
					this.judge1Node = null;
				}
				if(networkConfigurationEntry.getJudgesNumber().intValue() >= 2) {
					this.judge2Node = new JudgeNode(networkConfigurationEntry.getJudge2NodeId(), 2);
					this.nodesInNetwork++;
					this.numberOfJudges++;
				} else {
					this.judge2Node = null;
				}
				if(networkConfigurationEntry.getJudgesNumber().intValue() >= 3) {
					this.judge3Node = new JudgeNode(networkConfigurationEntry.getJudge3NodeId(), 3);
					this.nodesInNetwork++;
					this.numberOfJudges++;
				} else {
					this.judge3Node = null;
				}
				if(this.matchLogId != null)
					updateMatchLogNetworkInfo(networkConfigurationEntry);
				this.nodesInNetwork += _updateNetworkConfiguration(networkConfigurationEntry).intValue();
				if(isBodySensorsEnabled4Config(networkConfigurationEntry)) {
					HitEventValidator blue = this.blueLastBodyHitValidator.get();
					blue.setBlue(true);
					blue.setHitTimestamp( - 1L);
					this.blueLastBodyHitValidator.set(blue);
					HitEventValidator red = this.redLastBodyHitValidator.get();
					red.setBlue(false);
					red.setHitTimestamp( - 1L);
					this.redLastBodyHitValidator.set(red);
					this.judgeLetsBodyPoints = false;
				} else {
					this.judgeLetsBodyPoints = true;
				}
				if(isHeadSensorsEnabled4Config(networkConfigurationEntry)) {
					this.judgeLetsTechHeadPoints = true;
					HitEventValidator blue = this.blueLastHeadHitValidator.get();
					blue.setBlue(true);
					blue.setHitTimestamp( - 1L);
					this.blueLastHeadHitValidator.set(blue);
					HitEventValidator red = this.redLastHeadHitValidator.get();
					red.setBlue(false);
					red.setHitTimestamp( - 1L);
					this.redLastHeadHitValidator.set(red);
				} else {
					this.judgeLetsTechHeadPoints = false;
				}
			}
		} catch(TkStrikeServiceException e) {
			matchWorkerLogger.error("Update Network Configuration", e);
		}
	}

	final void updateInternalNodes() {
		try {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("Update Internal Nodes");
			this.currentMatchStatus.set(MatchStatusId.NOT_READY);
			this.statusBeforeNetworkError = MatchStatusId.NOT_READY;
			cleanMatchInfo();
			updateNetworkConfiguration();
			INetworkConfigurationEntry networkConfigurationEntry = this.appStatusWorker.getNetworkConfigurationEntry();
			IMatchConfigurationEntry matchConfigurationEntry = this.appStatusWorker.getMatchConfigurationEntry();
			if(matchConfigurationEntry != null && networkConfigurationEntry != null) {
				this.matchLogId = null;
				this.matchStarted = Boolean.FALSE;
				this.matchVictoryCriteria = matchConfigurationEntry.getMatchVictoryCriteria();
				if(matchConfigurationEntry.getRoundsConfig() != null) {
					this.currentMatchNumber = matchConfigurationEntry.getMatchNumber();
					this.matchRounds.set(matchConfigurationEntry.getRoundsConfig().getRounds());
					initializeRoundInternalCounters();
					this.currentRound.set(0);
					this.currentRoundStr.set("0");
					this.roundTimeMinutes = Integer.parseInt(matchConfigurationEntry.getRoundsConfig().getRoundTimeMinutes());
					this.roundTimeSeconds = Integer.parseInt(matchConfigurationEntry.getRoundsConfig().getRoundTimeSeconds());
					this.roundCountdown.clean(this.roundTimeMinutes, this.roundTimeSeconds);
					this.kyeShiTimeMinutes = Integer.parseInt(matchConfigurationEntry.getRoundsConfig().getKyeShiTimeMinutes());
					this.kyeShiTimeSeconds = Integer.parseInt(matchConfigurationEntry.getRoundsConfig().getKyeShiTimeSeconds());
					this.kyeShiCountdown.clean(this.kyeShiTimeMinutes, this.kyeShiTimeSeconds);
					this.restTimeMinutes = Integer.parseInt(matchConfigurationEntry.getRoundsConfig().getRestTimeMinutes());
					this.restTimeSeconds = Integer.parseInt(matchConfigurationEntry.getRoundsConfig().getRestTimeSeconds());
					this.restTimeCountdown.clean(this.restTimeMinutes, this.restTimeSeconds);
					if(matchConfigurationEntry.getRoundsConfig().getGoldenPointEnabled()) {
						this.goldenPointEnabled = true;
						this.goldenPointTimeMinutes = Integer.parseInt(matchConfigurationEntry.getRoundsConfig().getGoldenPointTimeMinutes());
						this.goldenPointTimeSeconds = Integer.parseInt(matchConfigurationEntry.getRoundsConfig().getGoldenPointTimeSeconds());
					} else {
						this.goldenPointEnabled = false;
						this.goldenPointTimeMinutes = 0;
						this.goldenPointTimeSeconds = 0;
					}
					_updateMatchConfiguration(matchConfigurationEntry);
				}
				this.currentMatchId = matchConfigurationEntry.getId();
				this.currentMatchVMInternalId = matchConfigurationEntry.getVmMatchInternalId();
				this.currentMatchVMRingNumber = this.externalConfigService.getExternalConfigEntry().getVenueManagementRingNumber();
				this.currentMatchBlueAthleteOvrInternalId = matchConfigurationEntry.getBlueAthleteOvrInternalId();
				this.currentMatchRedAthleteOvrInternalId = matchConfigurationEntry.getRedAthleteOvrInternalId();
				this.wtOvrClientService.resetCounter();
				this.differencialScore = matchConfigurationEntry.getDifferencialScore().intValue();
				createMatchLog(matchConfigurationEntry, this.rulesService.getRulesEntry(), networkConfigurationEntry);
				MatchConfigurationDto matchConfiguration4Send = matchConfigurationEntry.getMatchConfigurationDto();
				matchConfiguration4Send.setRingNumber(this.currentMatchVMRingNumber);
				_sendNewMatchConfiguredToExternalEventsListeners(matchConfiguration4Send);
			}
		} catch(TkStrikeServiceException e) {
			matchWorkerLogger.error("Update Internal Nodes", e);
		}
	}

	private void initializeSoundsConfiguration() {
		try {
			ISoundConfigurationEntry soundConfigurationEntry = this.soundConfigurationService.getSoundConfigurationEntry();
			if(soundConfigurationEntry != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
				SimpleDateFormat sdfMilis = new SimpleDateFormat("s.SSS");
				if(soundConfigurationEntry.getSoundBeforeStartRound().getEnabled()) {
					this.playSoundBeforeStartRound = soundConfigurationEntry.getSoundBeforeStartRoundSecondsBefore();
					Calendar calendar = Calendar.getInstance();
					calendar.set(12, 0);
					calendar.set(13, this.playSoundBeforeStartRound);
					if(this.playSoundBeforeStartRound < 10) {
						this.playSoundBeforeStartRoundString = sdfMilis.format(calendar.getTime());
						this.playSoundBeforeStartRoundString = this.playSoundBeforeStartRoundString.substring(0, this.playSoundBeforeStartRoundString
								.length() - 1);
					} else {
						this.playSoundBeforeStartRoundString = sdf.format(calendar.getTime());
					}
				} else {
					this.playSoundBeforeStartRound = 0;
					this.playSoundBeforeStartRoundString = null;
				}
				_initializeSoundsConfiguration(soundConfigurationEntry);
				getSoundPlayerService().refreshAllSounds();
			}
		} catch(TkStrikeServiceException e) {
			matchWorkerLogger.error("InitializeSounds", e);
		}
	}

	private void initializeRulesConfiguration() {
		try {
			IRulesEntry rulesEntry = this.rulesService.getRulesEntry();
			if(rulesEntry != null) {
				this.bodyPoints = rulesEntry.getBodyPoints();
				this.headPoints = rulesEntry.getHeadPoints();
				this.bodyTechPoints = rulesEntry.getBodyTechPoints();
				this.headTechPoints = rulesEntry.getHeadTechPoints();
				this.punchPoints = rulesEntry.getPunchPoints();
				this.paraSpinningKickPoints = rulesEntry.getParaSpinningKickPoints();
				this.paraTurningKickPoints = rulesEntry.getParaTurningKickPoints();
				this.differencialScore = rulesEntry.getDifferencialScore();
				this.pointGapAllRounds = rulesEntry.getPointGapAllRounds().booleanValue();
				this.cellingScore = rulesEntry.getCellingScore();
				this.paraCellingScore = rulesEntry.getParaCellingScore();
				this.nearMissLevel = rulesEntry.getNearMissLevel();
				if(rulesEntry.getOvertimePoints() > 0)
					this.overtimePoints = Integer.valueOf(rulesEntry.getOvertimePoints());
				this.matchVictoryCriteria = rulesEntry.getMatchVictoryCriteria();
				this.gamJeomShowPointsOnGoldenPoint = Boolean.valueOf(rulesEntry.gamJeomShowPointsOnGoldenPointProperty().get());
				this.bonusPointsEnabled = rulesEntry.getBonusPointsEnabled().booleanValue();
				this.bonusPointsMinLevel = rulesEntry.getBonusPointsMinLevel().intValue();
				this.bonusPointsPoints2Add = rulesEntry.getBonusPointsPoints2Add().intValue();
				_initializeRulesConfiguration(rulesEntry);
			}
		} catch(TkStrikeServiceException e) {
			matchWorkerLogger.error("Initialize Rules", e);
		}
	}

	private void cleanMatchInfo() {
		this.matchLogId = null;
		this.currentMatchNumber = null;
		clearRoundInternalCounters();
		this.bluePoints.set( - 1);
		this.bluePoints.set(0);
		this.blueTotalPoints.set(0);
		this.blueTechPoints.set(0);
		this.bluePenaltiesNextRound = Integer.valueOf(0);
		this.blueRoundWins.set(0);
		this.redRoundWins.set(0);
		this.redPoints.set( - 1);
		this.redPoints.set(0);
		this.redTotalPoints.set(0);
		this.redTechPoints.set(0);
		this.redPenaltiesNextRound = Integer.valueOf(0);
		this.bluePARATechPoints.set(0);
		this.bluePARATotalTechPoints.set(0);
		this.redPARATechPoints.set(0);
		this.redPARATotalTechPoints.set(0);
		this.bluePARATimeOutQuota.set(false);
		this.redPARATimeOutQuota.set(false);
		this.bluePARATimeOutQuotaValue.set(0);
		this.redPARATimeOutQuotaValue.set(0);
		this.bluePenalties.set( - 1);
		this.bluePenalties.set(0);
		this.blueTotalPenalties.set( - 1);
		this.blueTotalPenalties.set(0);
		this.blueGoldenPointImpacts.set(0);
		this.blueGoldenPointPenalties.set(0);
		this.blueLastGoldenPointPoints = Integer.valueOf(0);
		this.blueGoldenPointPunches.set(0);
		this.blueLastPointIsPenalty = false;
		this.redPenalties.set( - 1);
		this.redPenalties.set(0);
		this.redTotalPenalties.set( - 1);
		this.redTotalPenalties.set(0);
		this.redGoldenPointImpacts.set(0);
		this.redGoldenPointPenalties.set(0);
		this.redLastGoldenPointPoints = Integer.valueOf(0);
		this.redGoldenPointPunches.set(0);
		this.redLastPointIsPenalty = false;
		this.blueLastBodyHitValidator.set(new HitEventValidator(true, - 1L, HitEventType.BODY, this.backupSystemEnabled.get()));
		this.blueLastHeadHitValidator.set(new HitEventValidator(true, - 1L, HitEventType.HEAD, this.backupSystemEnabled.get()));
		this.blueLastSpecialHeadHitValidator.set(new HitEventValidator(true, - 1L, HitEventType.SPECIAL_HEAD, this.backupSystemEnabled.get()));
		this.blueLastSpecialBodyHitValidator.set(new HitEventValidator(true, - 1L, HitEventType.SPECIAL_BODY, this.backupSystemEnabled.get()));
		this.blueLastPunchHitValidator.set(new HitEventValidator(true, - 1L, HitEventType.PUNCH, this.backupSystemEnabled.get()));
		this.redLastBodyHitValidator.set(new HitEventValidator(false, - 1L, HitEventType.BODY, this.backupSystemEnabled.get()));
		this.redLastHeadHitValidator.set(new HitEventValidator(false, - 1L, HitEventType.HEAD, this.backupSystemEnabled.get()));
		this.redLastSpecialHeadHitValidator.set(new HitEventValidator(false, - 1L, HitEventType.SPECIAL_HEAD, this.backupSystemEnabled.get()));
		this.redLastSpecialBodyHitValidator.set(new HitEventValidator(false, - 1L, HitEventType.SPECIAL_BODY, this.backupSystemEnabled.get()));
		this.redLastPunchHitValidator.set(new HitEventValidator(false, - 1L, HitEventType.PUNCH, this.backupSystemEnabled.get()));
		this.goldenPointEnabled = false;
		this.goldenPointWorking = false;
		this.goldenPointTieBreaker = false;
		this.goldenPointTieBreakerInfo = null;
		this.goldenPointPointByPenalty = false;
		this.goldenPointPointByPunch = false;
		this.showGoldenPointTieBreakerOnScoreboard.set(false);
		this.superiorityByRounds.set(false);
		this.showSuperiorityByRounds.set(false);
		this.bestOf3WinnerLastRoundWithSuperiority = MatchWinner.TIE;
		this.bestOf3WinnerLastRoundByPUN = false;
		this.bestOf3WinnerLastRoundByPUNWinner = MatchWinner.TIE;
		this.bestOf3RoundSuperiorityOnScoreboard.set(false);
		this.bestOf3RoundWithSuperiority.set(false);
		this.bestOf3RoundWithPointGap.set(false);
		this.bestOf3RoundWinnerWithPointGap = null;
		this.bestOf3UpdatingFromScoreboardEditor = Boolean.valueOf(false);
		this.doctorInRound.set(false);
		this.matchFinalDecission.set(FinalDecision.WDR);
		this.matchWinner.set(null);
		this.matchWinner.set(MatchWinner.TIE);
		this.matchWinnerByPointGapNeedsConfirmation.set(false);
		this.changeMatchStatusToTimeoutOnScoreboardChanges = true;
		_cleanMatchInfo();
	}

	private boolean currentMatchStatusAllowToAddPoints() {
		return ((getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) ||
				getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT)) && this.roundCountdown.getWorking());
	}

	private void workWithAthleteSensorHit(DataEvent dataEvent, final boolean blue, HitEventType hitEventType,
			SimpleIntegerProperty goldenPointImpacts, SimpleObjectProperty<HitEventValidator> lastAthleteEventValidator,
			SimpleIntegerProperty lastImpactValue) {
		if(dataEvent.getHitValue().intValue() > this.defaultMinBodyLevelValidHit.intValue()) {
			_addMatchLogItem(dataEvent.getEventTimestamp(),
					Long.valueOf(getCurrentRoundCountdownAsMillis()),
					Long.valueOf(System.currentTimeMillis()),
					getRound4MatchLog(true), getRoundStr4MatchLog(true), HitEventType.BODY.equals(hitEventType) ? (blue ? getOTMatchLogItemTypeIfNeed(
							MatchLogItemType.BLUE_BODY_HIT) : getOTMatchLogItemTypeIfNeed(MatchLogItemType.RED_BODY_HIT)) : (blue ?

									getOTMatchLogItemTypeIfNeed(MatchLogItemType.BLUE_HEAD_HIT)
									: getOTMatchLogItemTypeIfNeed(MatchLogItemType.RED_HEAD_HIT)),
					Integer.valueOf(0),
					Integer.valueOf(0), "" + dataEvent
							.getHitValue(), false,
					Integer.valueOf(0), false);
			if(currentMatchStatusAllowToAddPoints()) {
				if(this.wtUDPService.isConnected()) {
					final int finalHitValue = dataEvent.getHitValue().intValue();
					TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

						@Override
						public Void call() throws Exception {
							BaseCommonMatchWorker.this.wtUDPService.sendHitLevel(blue ? Integer.valueOf(finalHitValue) : null, blue ? null
									: Integer.valueOf(finalHitValue));
							return null;
						}
					});
				}
				boolean validImpact = HitEventType.BODY.equals(hitEventType) ? isValidBodyImpact(dataEvent.getNodeId(), dataEvent.getHitValue(),
						goldenPointImpacts, blue ? "BLUE_HITS" : "RED_HITS", blue ? this.blueRoundsHits : this.redRoundsHits)
						: isValidHeadImpact(dataEvent.getNodeId(), dataEvent.getHitValue(), goldenPointImpacts, blue ? "BLUE_HITS" : "RED_HITS", blue
								? this.blueRoundsHits
								: this.redRoundsHits);
				if( ! validImpact)
					sendEventToRtBroadcast((HitEventType.BODY.equals(hitEventType) ? (blue ? getOTMatchLogItemTypeIfNeed(
							MatchLogItemType.BLUE_BODY_HIT) : getOTMatchLogItemTypeIfNeed(MatchLogItemType.RED_BODY_HIT)) : (blue ?

									getOTMatchLogItemTypeIfNeed(MatchLogItemType.BLUE_HEAD_HIT)
									: getOTMatchLogItemTypeIfNeed(MatchLogItemType.RED_HEAD_HIT))).toString(), this.currentMatchNumber,

							Integer.valueOf(this.currentRound.get()), this.currentRoundStr
									.get(),
							Boolean.valueOf(this.goldenPointWorking),
							Long.valueOf(getCurrentRoundCountdownAsMillis()), dataEvent
									.getEventTimestamp(), false, false,
							Integer.valueOf(this.blueGeneralPoints.get()),
							Integer.valueOf(this.bluePenalties.get()),
							Integer.valueOf(0),
							getBlueRoundsWins(), Integer.valueOf(this.redGeneralPoints.get()),
							Integer.valueOf(this.redPenalties.get()),
							Integer.valueOf(0),
							getRedRoundsWins(), (Integer)null, (String)null, (this.matchWinner != null) ? this.matchWinner
									.toString() : null, "" + dataEvent.getHitValue(), (String)null);
				if(validImpact) {
					if(HitEventType.BODY.equals(hitEventType)) {
						if(blue) {
							addBlueBodyPoint(getFinalPointsWithBonus(dataEvent.getHitValue().intValue(), getMinBodyLevel4AthleteNodeId(dataEvent
									.getNodeId()).intValue()), ActionSource.SENSOR, dataEvent.getEventTimestamp().longValue(),
									getCurrentRoundCountdownAsMillis(), dataEvent.getHitValue().intValue());
						} else {
							addRedBodyPoint(getFinalPointsWithBonus(dataEvent.getHitValue().intValue(), getMinBodyLevel4AthleteNodeId(dataEvent
									.getNodeId()).intValue()), ActionSource.SENSOR, dataEvent.getEventTimestamp().longValue(),
									getCurrentRoundCountdownAsMillis(), dataEvent.getHitValue().intValue());
						}
					} else if(blue) {
						addBlueHeadPoint(this.headPoints, ActionSource.SENSOR, dataEvent.getEventTimestamp().longValue(),
								getCurrentRoundCountdownAsMillis(), dataEvent.getHitValue().intValue());
					} else {
						addRedHeadPoint(this.headPoints, ActionSource.SENSOR, dataEvent.getEventTimestamp().longValue(),
								getCurrentRoundCountdownAsMillis(), dataEvent.getHitValue().intValue());
					}
					startDataEventHitValidation(dataEvent, hitEventType, blue, lastAthleteEventValidator, - 1);
				} else if( ! this.goldenPointWorking && dataEvent.getHitValue().intValue() >= this.nearMissLevel) {
					startDataEventHitValidation4NotValidInGoldenPoint(dataEvent, hitEventType, blue, true);
				} else if(this.goldenPointEnabled && this.goldenPointWorking && dataEvent.getHitValue().intValue() >= this.nearMissLevel) {
					startDataEventHitValidation4NotValidInGoldenPoint(dataEvent, hitEventType, blue, false);
				}
				lastImpactValue.set(HitEventType.HEAD.equals(hitEventType) ? (dataEvent.getHitValue().intValue() * - 1)
						: dataEvent.getHitValue().intValue());
				lastImpactValue.set(0);
			}
		}
	}

	private int getFinalPointsWithBonus(int hitLevelValue, int minHitLevel) {
		if(this.bonusPointsEnabled) {
			int vInc = this.bonusPointsMinLevel * minHitLevel / 100;
			int diff = hitLevelValue - minHitLevel;
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug(String.format(
						"BonusPoints enabled bonusPointsMinLevel = %s hitLevel = %s configuredMinHitLevel = %s vInc = %s diff = %s ", new Object[] {""
								+ this.bonusPointsMinLevel,

								Integer.valueOf(hitLevelValue),
								Integer.valueOf(minHitLevel),
								Integer.valueOf(vInc),
								Integer.valueOf(diff)}));
			if(diff >= vInc)
				return this.bodyPoints + this.bonusPointsPoints2Add;
		}
		return this.bodyPoints;
	}

	private void workWithJudgeHeadTech(DataEvent dataEvent, int judgeNumber, boolean blue) {
		if(this.backupSystemEnabled.get()) {
			if(isParaTkdMatch()) {
				if(currentMatchStatusAllowToAddPoints())
					if(blue) {
						if(this.blueLastSpecialBodyHitValidator.get().getHitTimestamp() == - 1L) {
							startDataEventHitValidation(dataEvent, HitEventType.PARA_SPINNING, true, this.blueLastSpecialBodyHitValidator,
									judgeNumber);
						} else {
							matchWorkerLogger.debug("blueLastSpecialBodyHitValidatorTimestamp->" + this.blueLastSpecialBodyHitValidator.get()
									.getHitTimestamp());
						}
					} else if(this.redLastSpecialBodyHitValidator.get().getHitTimestamp() == - 1L) {
						startDataEventHitValidation(dataEvent, HitEventType.PARA_SPINNING, false, this.redLastSpecialBodyHitValidator, judgeNumber);
					} else {
						matchWorkerLogger.debug("redLastSpecialBodyHitValidatorTimestamp->" + this.redLastSpecialBodyHitValidator.get()
								.getHitTimestamp());
					}
				_addMatchLogItem(dataEvent.getEventTimestamp(),
						Long.valueOf(getCurrentRoundCountdownAsMillis()),
						Long.valueOf(System.currentTimeMillis()),
						getRound4MatchLog(true), getRoundStr4MatchLog(true), blue ? getOTMatchLogItemTypeIfNeed(
								MatchLogItemType.BLUE_PARA_SPINNING_KICK) :

								getOTMatchLogItemTypeIfNeed(MatchLogItemType.RED_PARA_SPINNING_KICK),
						Integer.valueOf(0),
						Integer.valueOf(0), "" + judgeNumber, true,
						Integer.valueOf(0), false);
				if(currentMatchStatusAllowToAddPoints() &&
						canAddJudgePoint(dataEvent, judgeNumber, blue ? this.blueLastSpecialBodyHitValidator : this.redLastSpecialBodyHitValidator,
								HitEventValidator.ParaTkdTechEvent.SPINNING_KICK_TECH)) {
					if(matchWorkerLogger.isDebugEnabled())
						matchWorkerLogger.debug("To add BODY HIT AND SPINNING POINT by Judges Decission");
					sendEventToRtBroadcast((blue ? getOTMatchLogItemTypeIfNeed(MatchLogItemType.BLUE_BODY_HIT)
							: getOTMatchLogItemTypeIfNeed(MatchLogItemType.RED_BODY_HIT)).toString(), this.currentMatchNumber,

							Integer.valueOf(this.currentRound.get()), this.currentRoundStr
									.get(),
							Boolean.valueOf(this.goldenPointWorking),
							Long.valueOf(getCurrentRoundCountdownAsMillis()), dataEvent
									.getEventTimestamp(), false, false,
							Integer.valueOf(this.blueGeneralPoints.get()),
							Integer.valueOf(this.bluePenalties.get()),
							Integer.valueOf(0),
							getBlueRoundsWins(), Integer.valueOf(this.redGeneralPoints.get()),
							Integer.valueOf(this.redPenalties.get()),
							Integer.valueOf(0),
							getRedRoundsWins(), (Integer)null, (String)null, (this.matchWinner != null) ? this.matchWinner
									.toString() : null, "" + dataEvent.getHitValue(), (String)null);
					if(blue) {
						addBlueBodyPoint(this.bodyPoints, ActionSource.SENSOR, dataEvent.getEventTimestamp().longValue(),
								getCurrentRoundCountdownAsMillis(), 99);
						addBlueBodyTechPoint(this.paraSpinningKickPoints, ActionSource.JUDGE, dataEvent.getEventTimestamp().longValue(),
								getCurrentRoundCountdownAsMillis());
						this.blueLastImpactValue.set(99);
						this.blueLastImpactValue.set(0);
					} else {
						addRedBodyPoint(this.bodyPoints, ActionSource.SENSOR, dataEvent.getEventTimestamp().longValue(),
								getCurrentRoundCountdownAsMillis(), 99);
						addRedBodyTechPoint(this.paraSpinningKickPoints, ActionSource.JUDGE, dataEvent.getEventTimestamp().longValue(),
								getCurrentRoundCountdownAsMillis());
						this.redLastImpactValue.set(99);
						this.redLastImpactValue.set(0);
					}
				}
			} else {
				if(currentMatchStatusAllowToAddPoints())
					if(blue) {
						if(this.blueLastSpecialHeadHitValidator.get().getHitTimestamp() == - 1L) {
							startDataEventHitValidation(dataEvent, HitEventType.SPECIAL_HEAD, true, this.blueLastSpecialHeadHitValidator,
									judgeNumber);
						} else {
							matchWorkerLogger.debug("blueLastSpecialHeadHitValidatorTimestamp->" + this.blueLastSpecialHeadHitValidator.get()
									.getHitTimestamp());
						}
					} else if(this.redLastSpecialHeadHitValidator.get().getHitTimestamp() == - 1L) {
						startDataEventHitValidation(dataEvent, HitEventType.SPECIAL_HEAD, false, this.redLastSpecialHeadHitValidator, judgeNumber);
					} else {
						matchWorkerLogger.debug("redLastSpecialHeadHitValidatorTimestamp->" + this.redLastSpecialHeadHitValidator.get()
								.getHitTimestamp());
					}
				_addMatchLogItem(dataEvent.getEventTimestamp(),
						Long.valueOf(getCurrentRoundCountdownAsMillis()),
						Long.valueOf(System.currentTimeMillis()),
						getRound4MatchLog(true), getRoundStr4MatchLog(true), blue ? getOTMatchLogItemTypeIfNeed(
								MatchLogItemType.BLUE_JUDGE_SPECIAL_HEAD_HIT)
								: getOTMatchLogItemTypeIfNeed(MatchLogItemType.RED_JUDGE_SPECIAL_HEAD_HIT),
						Integer.valueOf(0),
						Integer.valueOf(0), "" + judgeNumber, true,
						Integer.valueOf(0), false);
				if(currentMatchStatusAllowToAddPoints() &&
						canAddJudgePoint(dataEvent, judgeNumber, blue ? this.blueLastSpecialHeadHitValidator : this.redLastSpecialHeadHitValidator,
								HitEventValidator.ParaTkdTechEvent.NONE)) {
					if(matchWorkerLogger.isDebugEnabled())
						matchWorkerLogger.debug("To add HEAD POINT by Judges Decission");
					workWithAthleteSensorHit(new DataEvent(Long.valueOf(blue ? this.blueLastSpecialHeadHitValidator.get().getHitTimestamp()
							: this.redLastSpecialHeadHitValidator.get().getHitTimestamp()), dataEvent.getNetworkStatus(), dataEvent.getNodeId(),
							Integer.valueOf(99), dataEvent.getDataEventHitType(), dataEvent.getNativePacket()), blue, HitEventType.HEAD, blue
									? this.blueGoldenPointImpacts
									: this.redGoldenPointImpacts, blue ? this.blueLastHeadHitValidator : this.redLastHeadHitValidator, blue
											? this.blueLastImpactValue
											: this.redLastImpactValue);
				}
			}
		} else if(this.judgeLetsTechHeadPoints || isParaTkdMatch()) {
			if(getCurrentMatchStatus().equals(MatchStatusId.ROUND_WORKING) ||
					getCurrentMatchStatus().equals(MatchStatusId.ROUND_PAUSED) ||
					getCurrentMatchStatus().equals(MatchStatusId.ROUND_KYESHI) ||
					getCurrentMatchStatus().equals(MatchStatusId.NETWORK_ERROR) ||
					getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_MATCH) ||
					getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_START_ROUND) ||
					getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_START_GOLDENPOINT) ||
					getCurrentMatchStatus().equals(MatchStatusId.ROUND_IN_GOLDENPOINT)) {
				_addMatchLogItem(dataEvent.getEventTimestamp(),
						Long.valueOf(getCurrentRoundCountdownAsMillis()),
						Long.valueOf(System.currentTimeMillis()),
						getRound4MatchLog(true), getRoundStr4MatchLog(true), MatchLogItemType.MEETING,
						Integer.valueOf(0),
						Integer.valueOf(0), "" + judgeNumber, true,
						Integer.valueOf(0), false);
				TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

					@Override
					public Void call() throws Exception {
						BaseCommonMatchWorker.this.getSoundPlayerService().playSoundWhenTechMeeting();
						return null;
					}
				});
			}
		} else {
			if(currentMatchStatusAllowToAddPoints())
				if(blue) {
					if(this.blueLastSpecialHeadHitValidator.get().getHitTimestamp() == - 1L) {
						startDataEventHitValidation(dataEvent, HitEventType.SPECIAL_HEAD, true, this.blueLastSpecialHeadHitValidator, judgeNumber);
					} else {
						matchWorkerLogger.debug("blueLastSpecialHeadHitValidatorTimestamp->" + this.blueLastSpecialHeadHitValidator.get()
								.getHitTimestamp());
					}
				} else if(this.redLastSpecialHeadHitValidator.get().getHitTimestamp() == - 1L) {
					startDataEventHitValidation(dataEvent, HitEventType.SPECIAL_HEAD, false, this.redLastSpecialHeadHitValidator, judgeNumber);
				} else {
					matchWorkerLogger.debug("redLastSpecialHeadHitValidatorTimestamp->" + this.redLastSpecialHeadHitValidator.get()
							.getHitTimestamp());
				}
			_addMatchLogItem(dataEvent.getEventTimestamp(),
					Long.valueOf(getCurrentRoundCountdownAsMillis()),
					Long.valueOf(System.currentTimeMillis()),
					getRound4MatchLog(true), getRoundStr4MatchLog(true), blue ? getOTMatchLogItemTypeIfNeed(
							MatchLogItemType.BLUE_JUDGE_SPECIAL_HEAD_HIT) : getOTMatchLogItemTypeIfNeed(MatchLogItemType.RED_JUDGE_SPECIAL_HEAD_HIT),
					Integer.valueOf(0),
					Integer.valueOf(0), "" + judgeNumber, true,
					Integer.valueOf(0), false);
			if(currentMatchStatusAllowToAddPoints() &&
					canAddJudgePoint(dataEvent, judgeNumber, blue ? this.blueLastSpecialHeadHitValidator : this.redLastSpecialHeadHitValidator,
							HitEventValidator.ParaTkdTechEvent.NONE)) {
				if(matchWorkerLogger.isDebugEnabled())
					matchWorkerLogger.debug("To add HEAD POINT by Judges Decission");
				workWithAthleteSensorHit(new DataEvent(Long.valueOf(blue ? this.blueLastSpecialHeadHitValidator.get().getHitTimestamp()
						: this.redLastSpecialHeadHitValidator.get().getHitTimestamp()), dataEvent.getNetworkStatus(), dataEvent.getNodeId(), Integer
								.valueOf(99), dataEvent.getDataEventHitType(), dataEvent.getNativePacket()), blue, HitEventType.HEAD, blue
										? this.blueGoldenPointImpacts
										: this.redGoldenPointImpacts, blue ? this.blueLastHeadHitValidator : this.redLastHeadHitValidator, blue
												? this.blueLastImpactValue
												: this.redLastImpactValue);
			}
		}
	}

	final void workWithJudgeBodyTech(DataEvent dataEvent, int judgeNumber, boolean blue) {
		if(this.backupSystemEnabled.get()) {
			if(isParaTkdMatch()) {
				matchWorkerLogger.debug("Is PARA Tkd Match work with TURNING KICK TECH POINT");
				if(currentMatchStatusAllowToAddPoints())
					if(blue) {
						if(this.blueLastSpecialBodyHitValidator.get().getHitTimestamp() == - 1L) {
							startDataEventHitValidation(dataEvent, HitEventType.PARA_TURNING, true, this.blueLastSpecialBodyHitValidator,
									judgeNumber);
						} else {
							matchWorkerLogger.debug("blueLastSpecialBodyHitValidatorTimestamp->" + this.blueLastSpecialBodyHitValidator.get()
									.getHitTimestamp());
						}
					} else if(this.redLastSpecialBodyHitValidator.get().getHitTimestamp() == - 1L) {
						startDataEventHitValidation(dataEvent, HitEventType.PARA_TURNING, false, this.redLastSpecialBodyHitValidator, judgeNumber);
					} else {
						matchWorkerLogger.debug("redLastSpecialBodyHitValidatorTimestamp->" + this.redLastSpecialBodyHitValidator.get()
								.getHitTimestamp());
					}
				_addMatchLogItem(dataEvent.getEventTimestamp(),
						Long.valueOf(getCurrentRoundCountdownAsMillis()),
						Long.valueOf(System.currentTimeMillis()),
						getRound4MatchLog(true), getRoundStr4MatchLog(true), blue ? getOTMatchLogItemTypeIfNeed(
								MatchLogItemType.BLUE_PARA_TURNING_KICK) :

								getOTMatchLogItemTypeIfNeed(MatchLogItemType.RED_PARA_TURNING_KICK),
						Integer.valueOf(0),
						Integer.valueOf(0), "" + judgeNumber, true,
						Integer.valueOf(0), false);
				if(currentMatchStatusAllowToAddPoints() &&
						canAddJudgePoint(dataEvent, judgeNumber, blue ? this.blueLastSpecialBodyHitValidator : this.redLastSpecialBodyHitValidator,
								HitEventValidator.ParaTkdTechEvent.TURNING_KICK_TECH)) {
					if(matchWorkerLogger.isDebugEnabled())
						matchWorkerLogger.debug("To add BODY HIT AND TURNING POINT by Judges Decission");
					sendEventToRtBroadcast((blue ? getOTMatchLogItemTypeIfNeed(MatchLogItemType.BLUE_BODY_HIT)
							: getOTMatchLogItemTypeIfNeed(MatchLogItemType.RED_BODY_HIT)).toString(), this.currentMatchNumber,

							Integer.valueOf(this.currentRound.get()), this.currentRoundStr
									.get(),
							Boolean.valueOf(this.goldenPointWorking),
							Long.valueOf(getCurrentRoundCountdownAsMillis()), dataEvent
									.getEventTimestamp(), false, false,
							Integer.valueOf(this.blueGeneralPoints.get()),
							Integer.valueOf(this.bluePenalties.get()),
							Integer.valueOf(0),
							getBlueRoundsWins(), Integer.valueOf(this.redGeneralPoints.get()),
							Integer.valueOf(this.redPenalties.get()),
							Integer.valueOf(0),
							getRedRoundsWins(), (Integer)null, (String)null, (this.matchWinner != null) ? this.matchWinner
									.toString() : null, "" + dataEvent.getHitValue(), (String)null);
					if(blue) {
						addBlueBodyPoint(this.bodyPoints, ActionSource.SENSOR, dataEvent.getEventTimestamp().longValue(),
								getCurrentRoundCountdownAsMillis(), 99);
						addBlueBodyTechPoint(this.paraTurningKickPoints, ActionSource.JUDGE, dataEvent.getEventTimestamp().longValue(),
								getCurrentRoundCountdownAsMillis());
						this.blueLastImpactValue.set(99);
						this.blueLastImpactValue.set(0);
					} else {
						addRedBodyPoint(this.bodyPoints, ActionSource.SENSOR, dataEvent.getEventTimestamp().longValue(),
								getCurrentRoundCountdownAsMillis(), 99);
						addRedBodyTechPoint(this.paraTurningKickPoints, ActionSource.JUDGE, dataEvent.getEventTimestamp().longValue(),
								getCurrentRoundCountdownAsMillis());
						this.redLastImpactValue.set(99);
						this.redLastImpactValue.set(0);
					}
				}
			} else {
				if(currentMatchStatusAllowToAddPoints())
					if(blue) {
						if(this.blueLastSpecialBodyHitValidator.get().getHitTimestamp() == - 1L) {
							startDataEventHitValidation(dataEvent, HitEventType.SPECIAL_BODY, true, this.blueLastSpecialBodyHitValidator,
									judgeNumber);
						} else {
							matchWorkerLogger.debug("blueLastSpecialBodyHitValidatorTimestamp->" + this.blueLastSpecialBodyHitValidator.get()
									.getHitTimestamp());
						}
					} else if(this.redLastSpecialBodyHitValidator.get().getHitTimestamp() == - 1L) {
						startDataEventHitValidation(dataEvent, HitEventType.SPECIAL_BODY, false, this.redLastSpecialBodyHitValidator, judgeNumber);
					} else {
						matchWorkerLogger.debug("redLastSpecialBodyHitValidatorTimestamp->" + this.redLastSpecialBodyHitValidator.get()
								.getHitTimestamp());
					}
				_addMatchLogItem(dataEvent.getEventTimestamp(),
						Long.valueOf(getCurrentRoundCountdownAsMillis()),
						Long.valueOf(System.currentTimeMillis()),
						getRound4MatchLog(true), getRoundStr4MatchLog(true), blue ? getOTMatchLogItemTypeIfNeed(
								MatchLogItemType.BLUE_JUDGE_SPECIAL_BODY_HIT)
								: getOTMatchLogItemTypeIfNeed(MatchLogItemType.RED_JUDGE_SPECIAL_BODY_HIT),
						Integer.valueOf(0),
						Integer.valueOf(0), "" + judgeNumber, true,
						Integer.valueOf(0), false);
				if(currentMatchStatusAllowToAddPoints() &&
						canAddJudgePoint(dataEvent, judgeNumber, blue ? this.blueLastSpecialBodyHitValidator : this.redLastSpecialBodyHitValidator,
								HitEventValidator.ParaTkdTechEvent.NONE)) {
					if(matchWorkerLogger.isDebugEnabled())
						matchWorkerLogger.debug("To add BODY POINT by Judges Decission");
					workWithAthleteSensorHit(new DataEvent(Long.valueOf(blue ? this.blueLastSpecialBodyHitValidator.get().getHitTimestamp()
							: this.redLastSpecialBodyHitValidator.get().getHitTimestamp()), dataEvent.getNetworkStatus(), dataEvent.getNodeId(),
							Integer.valueOf(99), dataEvent.getDataEventHitType(), dataEvent.getNativePacket()), blue, HitEventType.BODY, blue
									? this.blueGoldenPointImpacts
									: this.redGoldenPointImpacts, blue ? this.blueLastBodyHitValidator : this.redLastBodyHitValidator, blue
											? this.blueLastImpactValue
											: this.redLastImpactValue);
				}
			}
		} else if(this.judgeLetsTechHeadPoints && ! isParaTkdMatch() && ((blue && this.blueLastHeadHitValidator
				.get().getHitTimestamp() > this.blueLastBodyHitValidator.get().getHitTimestamp()) || ( ! blue && this.redLastHeadHitValidator
						.get().getHitTimestamp() > this.redLastBodyHitValidator.get().getHitTimestamp()))) {
			_addMatchLogItem(dataEvent.getEventTimestamp(),
					Long.valueOf(getCurrentRoundCountdownAsMillis()),
					Long.valueOf(System.currentTimeMillis()),
					getRound4MatchLog(true), getRoundStr4MatchLog(true), blue ? getOTMatchLogItemTypeIfNeed(MatchLogItemType.BLUE_JUDGE_HEAD_TECH)
							: getOTMatchLogItemTypeIfNeed(MatchLogItemType.RED_JUDGE_HEAD_TECH),
					Integer.valueOf(0),
					Integer.valueOf(0), "" + judgeNumber, true,

					Integer.valueOf(0), false);
			if(currentMatchStatusAllowToAddPoints() &&
					canAddJudgePoint(dataEvent, judgeNumber, blue ? this.blueLastHeadHitValidator : this.redLastHeadHitValidator,
							HitEventValidator.ParaTkdTechEvent.NONE)) {
				if(matchWorkerLogger.isDebugEnabled())
					matchWorkerLogger.debug("To add HEAD TECH POINT by Judges Decission");
				if(blue) {
					addBlueHeadTechPoint(this.headTechPoints, ActionSource.JUDGE, dataEvent.getEventTimestamp().longValue(),
							getCurrentRoundCountdownAsMillis());
				} else {
					addRedHeadTechPoint(this.headTechPoints, ActionSource.JUDGE, dataEvent.getEventTimestamp().longValue(),
							getCurrentRoundCountdownAsMillis());
				}
			}
		} else if( ! this.judgeLetsTechHeadPoints && ((blue && this.blueLastHeadHitValidator

				.get().getHitTimestamp() > this.blueLastBodyHitValidator.get().getHitTimestamp()) || ( ! blue && this.redLastHeadHitValidator
						.get().getHitTimestamp() > this.redLastBodyHitValidator.get().getHitTimestamp()))) {
			_addMatchLogItem(dataEvent.getEventTimestamp(),
					Long.valueOf(getCurrentRoundCountdownAsMillis()),
					Long.valueOf(System.currentTimeMillis()),
					getRound4MatchLog(true), getRoundStr4MatchLog(true), blue ? getOTMatchLogItemTypeIfNeed(MatchLogItemType.BLUE_JUDGE_HEAD_TECH)
							: getOTMatchLogItemTypeIfNeed(MatchLogItemType.RED_JUDGE_HEAD_TECH),
					Integer.valueOf(0),
					Integer.valueOf(0), "" + judgeNumber, true,
					Integer.valueOf(0), false);
			if(currentMatchStatusAllowToAddPoints() &&
					canAddJudgePoint(dataEvent, judgeNumber, blue ? this.blueLastHeadHitValidator : this.redLastHeadHitValidator,
							HitEventValidator.ParaTkdTechEvent.NONE)) {
				if(matchWorkerLogger.isDebugEnabled())
					matchWorkerLogger.debug("To add HEAD TECH POINT by Judges Decission");
				if(blue) {
					addBlueHeadTechPoint(this.headTechPoints, ActionSource.JUDGE, dataEvent.getEventTimestamp().longValue(),
							getCurrentRoundCountdownAsMillis());
				} else {
					addRedHeadTechPoint(this.headTechPoints, ActionSource.JUDGE, dataEvent.getEventTimestamp().longValue(),
							getCurrentRoundCountdownAsMillis());
				}
			}
		} else if(this.judgeLetsBodyPoints) {
			if(currentMatchStatusAllowToAddPoints())
				if(blue) {
					if(this.blueLastBodyHitValidator.get().getHitTimestamp() == - 1L)
						startDataEventHitValidation(dataEvent, HitEventType.BODY, true, this.blueLastBodyHitValidator, judgeNumber);
				} else if(this.redLastBodyHitValidator.get().getHitTimestamp() == - 1L) {
					startDataEventHitValidation(dataEvent, HitEventType.BODY, false, this.redLastBodyHitValidator, judgeNumber);
				}
			_addMatchLogItem(dataEvent.getEventTimestamp(),
					Long.valueOf(getCurrentRoundCountdownAsMillis()),
					Long.valueOf(System.currentTimeMillis()),
					getRound4MatchLog(true), getRoundStr4MatchLog(true), blue ? getOTMatchLogItemTypeIfNeed(MatchLogItemType.BLUE_JUDGE_BODY_TECH) :

							getOTMatchLogItemTypeIfNeed(MatchLogItemType.RED_JUDGE_BODY_TECH),
					Integer.valueOf(0),
					Integer.valueOf(0), "" + judgeNumber, true,
					Integer.valueOf(0), false);
			if(currentMatchStatusAllowToAddPoints() &&
					canAddJudgePoint(dataEvent, judgeNumber, blue ? this.blueLastBodyHitValidator : this.redLastBodyHitValidator,
							HitEventValidator.ParaTkdTechEvent.NONE)) {
				if(matchWorkerLogger.isDebugEnabled())
					matchWorkerLogger.debug("To add HEAD POINT by Judges Decission");
				if(blue) {
					addBlueBodyPoint(this.bodyPoints, ActionSource.JUDGE, dataEvent.getEventTimestamp().longValue(),
							getCurrentRoundCountdownAsMillis(), 0);
				} else {
					addRedBodyPoint(this.bodyPoints, ActionSource.JUDGE, dataEvent.getEventTimestamp().longValue(),
							getCurrentRoundCountdownAsMillis(), 0);
				}
			}
		} else {
			_addMatchLogItem(dataEvent.getEventTimestamp(),
					Long.valueOf(getCurrentRoundCountdownAsMillis()),
					Long.valueOf(System.currentTimeMillis()),
					getRound4MatchLog(true), getRoundStr4MatchLog(true), blue ? getOTMatchLogItemTypeIfNeed(isParaTkdMatch()
							? MatchLogItemType.BLUE_PARA_SPINNING_KICK
							: MatchLogItemType.BLUE_JUDGE_BODY_TECH) :

							getOTMatchLogItemTypeIfNeed(isParaTkdMatch() ? MatchLogItemType.RED_PARA_SPINNING_KICK
									: MatchLogItemType.RED_JUDGE_BODY_TECH),
					Integer.valueOf(0),
					Integer.valueOf(0), "" + judgeNumber, true,
					Integer.valueOf(0), false);
			if(currentMatchStatusAllowToAddPoints())
				if(canAddJudgePoint(dataEvent, judgeNumber, blue ? this.blueLastBodyHitValidator : this.redLastBodyHitValidator, isParaTkdMatch()
						? HitEventValidator.ParaTkdTechEvent.SPINNING_KICK_TECH
						: HitEventValidator.ParaTkdTechEvent.NONE)) {
					if(matchWorkerLogger.isDebugEnabled())
						matchWorkerLogger.debug("To add BODY TECH POINT by Judges Decission");
					if(blue) {
						addBlueBodyTechPoint(isParaTkdMatch() ? this.paraSpinningKickPoints : this.bodyTechPoints, ActionSource.JUDGE, dataEvent
								.getEventTimestamp().longValue(), getCurrentRoundCountdownAsMillis());
					} else {
						addRedBodyTechPoint(isParaTkdMatch() ? this.paraSpinningKickPoints : this.bodyTechPoints, ActionSource.JUDGE, dataEvent
								.getEventTimestamp().longValue(), getCurrentRoundCountdownAsMillis());
					}
				}
		}
	}

	final void workWithJudgePunch(DataEvent dataEvent, int judgeNumber, boolean blue) {
		if(this.backupSystemEnabled.get()) {
			if(isParaTkdMatch()) {
				if(currentMatchStatusAllowToAddPoints())
					if(blue) {
						if(this.blueLastSpecialBodyHitValidator.get().getHitTimestamp() == - 1L) {
							startDataEventHitValidation(dataEvent, HitEventType.SPECIAL_BODY, true, this.blueLastSpecialBodyHitValidator,
									judgeNumber);
						} else {
							matchWorkerLogger.debug("blueLastSpecialBodyHitValidatorTimestamp->" + this.blueLastSpecialBodyHitValidator.get()
									.getHitTimestamp());
						}
					} else if(this.redLastSpecialBodyHitValidator.get().getHitTimestamp() == - 1L) {
						startDataEventHitValidation(dataEvent, HitEventType.SPECIAL_BODY, false, this.redLastSpecialBodyHitValidator, judgeNumber);
					} else {
						matchWorkerLogger.debug("redLastSpecialBodyHitValidatorTimestamp->" + this.redLastSpecialBodyHitValidator.get()
								.getHitTimestamp());
					}
				_addMatchLogItem(dataEvent.getEventTimestamp(),
						Long.valueOf(getCurrentRoundCountdownAsMillis()),
						Long.valueOf(System.currentTimeMillis()),
						getRound4MatchLog(true), getRoundStr4MatchLog(true), blue ? getOTMatchLogItemTypeIfNeed(
								MatchLogItemType.BLUE_JUDGE_SPECIAL_BODY_HIT)
								: getOTMatchLogItemTypeIfNeed(MatchLogItemType.RED_JUDGE_SPECIAL_BODY_HIT),
						Integer.valueOf(0),
						Integer.valueOf(0), "" + judgeNumber, true,
						Integer.valueOf(0), false);
				if(currentMatchStatusAllowToAddPoints() &&
						canAddJudgePoint(dataEvent, judgeNumber, blue ? this.blueLastSpecialBodyHitValidator : this.redLastSpecialBodyHitValidator,
								HitEventValidator.ParaTkdTechEvent.NONE)) {
					if(matchWorkerLogger.isDebugEnabled())
						matchWorkerLogger.debug("To add BODY POINT by Judges Decission");
					workWithAthleteSensorHit(new DataEvent(Long.valueOf(blue ? this.blueLastSpecialBodyHitValidator.get().getHitTimestamp()
							: this.redLastSpecialBodyHitValidator.get().getHitTimestamp()), dataEvent.getNetworkStatus(), dataEvent.getNodeId(),
							Integer.valueOf(99), dataEvent.getDataEventHitType(), dataEvent.getNativePacket()), blue, HitEventType.BODY, blue
									? this.blueGoldenPointImpacts
									: this.redGoldenPointImpacts, blue ? this.blueLastBodyHitValidator : this.redLastBodyHitValidator, blue
											? this.blueLastImpactValue
											: this.redLastImpactValue);
				}
			} else if((blue && this.blueLastHeadHitValidator.get().getHitTimestamp() > this.blueLastBodyHitValidator.get().getHitTimestamp())
					|| ( ! blue && this.redLastHeadHitValidator
							.get().getHitTimestamp() > this.redLastBodyHitValidator.get().getHitTimestamp())) {
				_addMatchLogItem(dataEvent.getEventTimestamp(),
						Long.valueOf(getCurrentRoundCountdownAsMillis()),
						Long.valueOf(System.currentTimeMillis()),
						getRound4MatchLog(true), getRoundStr4MatchLog(true), blue ? getOTMatchLogItemTypeIfNeed(MatchLogItemType.BLUE_JUDGE_HEAD_TECH)
								: getOTMatchLogItemTypeIfNeed(MatchLogItemType.RED_JUDGE_HEAD_TECH),
						Integer.valueOf(0),
						Integer.valueOf(0), "" + judgeNumber, true,
						Integer.valueOf(0), false);
				if(currentMatchStatusAllowToAddPoints() &&
						canAddJudgePoint(dataEvent, judgeNumber, blue ? this.blueLastHeadHitValidator : this.redLastHeadHitValidator,
								HitEventValidator.ParaTkdTechEvent.NONE)) {
					if(matchWorkerLogger.isDebugEnabled())
						matchWorkerLogger.debug("To add HEAD TECH POINT by Judges Decission");
					if(blue) {
						addBlueHeadTechPoint(this.headTechPoints, ActionSource.JUDGE, dataEvent.getEventTimestamp().longValue(),
								getCurrentRoundCountdownAsMillis());
					} else {
						addRedHeadTechPoint(this.headTechPoints, ActionSource.JUDGE, dataEvent.getEventTimestamp().longValue(),
								getCurrentRoundCountdownAsMillis());
					}
				}
			} else if((blue && this.blueLastBodyHitValidator.get().getHitTimestamp() > this.blueLastHeadHitValidator.get().getHitTimestamp())
					|| ( ! blue && this.redLastBodyHitValidator
							.get().getHitTimestamp() > this.redLastHeadHitValidator.get().getHitTimestamp())) {
				_addMatchLogItem(dataEvent.getEventTimestamp(),
						Long.valueOf(getCurrentRoundCountdownAsMillis()),
						Long.valueOf(System.currentTimeMillis()),
						getRound4MatchLog(true), getRoundStr4MatchLog(true), blue ? getOTMatchLogItemTypeIfNeed(MatchLogItemType.BLUE_JUDGE_BODY_TECH)
								:

								getOTMatchLogItemTypeIfNeed(MatchLogItemType.RED_JUDGE_BODY_TECH),
						Integer.valueOf(0),
						Integer.valueOf(0), "" + judgeNumber, true,
						Integer.valueOf(0), false);
				if(currentMatchStatusAllowToAddPoints())
					if(canAddJudgePoint(dataEvent, judgeNumber, blue ? this.blueLastBodyHitValidator : this.redLastBodyHitValidator, isParaTkdMatch()
							? HitEventValidator.ParaTkdTechEvent.SPINNING_KICK_TECH
							: HitEventValidator.ParaTkdTechEvent.NONE)) {
						if(matchWorkerLogger.isDebugEnabled())
							matchWorkerLogger.debug("To add BODY TECH POINT by Judges Decission");
						if(blue) {
							addBlueBodyTechPoint(this.bodyTechPoints, ActionSource.JUDGE, dataEvent.getEventTimestamp().longValue(),
									getCurrentRoundCountdownAsMillis());
						} else {
							addRedBodyTechPoint(this.bodyTechPoints, ActionSource.JUDGE, dataEvent.getEventTimestamp().longValue(),
									getCurrentRoundCountdownAsMillis());
						}
					}
			} else {
				if(currentMatchStatusAllowToAddPoints())
					if(blue) {
						if(this.blueLastPunchHitValidator.get().getHitTimestamp() == - 1L)
							startDataEventHitValidation(dataEvent, HitEventType.PUNCH, true, this.blueLastPunchHitValidator, judgeNumber);
					} else if(this.redLastPunchHitValidator.get().getHitTimestamp() == - 1L) {
						startDataEventHitValidation(dataEvent, HitEventType.PUNCH, false, this.redLastPunchHitValidator, judgeNumber);
					}
				_addMatchLogItem(dataEvent.getEventTimestamp(),
						Long.valueOf(getCurrentRoundCountdownAsMillis()),
						Long.valueOf(System.currentTimeMillis()),
						getRound4MatchLog(true), getRoundStr4MatchLog(true), blue ? getOTMatchLogItemTypeIfNeed(MatchLogItemType.BLUE_JUDGE_PUNCH)
								: getOTMatchLogItemTypeIfNeed(MatchLogItemType.RED_JUDGE_PUNCH),
						Integer.valueOf(0),
						Integer.valueOf(0), "" + judgeNumber, true,
						Integer.valueOf(0), false);
				if(currentMatchStatusAllowToAddPoints() &&
						canAddJudgePoint(dataEvent, judgeNumber, blue ? this.blueLastPunchHitValidator : this.redLastPunchHitValidator,
								HitEventValidator.ParaTkdTechEvent.NONE)) {
					if(matchWorkerLogger.isDebugEnabled())
						matchWorkerLogger.debug("To add PUNCH POINT by Judges Decission");
					if(blue) {
						addBluePunchPoint(this.punchPoints, ActionSource.JUDGE, dataEvent.getEventTimestamp().longValue(),
								getCurrentRoundCountdownAsMillis());
					} else {
						addRedPunchPoint(this.punchPoints, ActionSource.JUDGE, dataEvent.getEventTimestamp().longValue(),
								getCurrentRoundCountdownAsMillis());
					}
				}
			}
		} else if(isParaTkdMatch()) {
			matchWorkerLogger.debug("Is PARA Tkd Match work with TURNING KICK TECH POINT");
			_addMatchLogItem(dataEvent.getEventTimestamp(),
					Long.valueOf(getCurrentRoundCountdownAsMillis()),
					Long.valueOf(System.currentTimeMillis()),
					getRound4MatchLog(true), getRoundStr4MatchLog(true), blue ? getOTMatchLogItemTypeIfNeed(MatchLogItemType.BLUE_PARA_TURNING_KICK) :

							getOTMatchLogItemTypeIfNeed(MatchLogItemType.RED_PARA_TURNING_KICK),
					Integer.valueOf(0),
					Integer.valueOf(0), "" + judgeNumber, true,
					Integer.valueOf(0), false);
			if(currentMatchStatusAllowToAddPoints() &&
					canAddJudgePoint(dataEvent, judgeNumber, blue ? this.blueLastBodyHitValidator : this.redLastBodyHitValidator,
							HitEventValidator.ParaTkdTechEvent.TURNING_KICK_TECH)) {
				if(matchWorkerLogger.isDebugEnabled())
					matchWorkerLogger.debug("To add BODY TURNING TECH POINT by Judges Decission");
				if(blue) {
					addBlueBodyTechPoint(this.paraTurningKickPoints, ActionSource.JUDGE, dataEvent.getEventTimestamp().longValue(),
							getCurrentRoundCountdownAsMillis());
				} else {
					addRedBodyTechPoint(this.paraTurningKickPoints, ActionSource.JUDGE, dataEvent.getEventTimestamp().longValue(),
							getCurrentRoundCountdownAsMillis());
				}
			}
		} else {
			if(currentMatchStatusAllowToAddPoints())
				if(blue) {
					if(this.blueLastPunchHitValidator.get().getHitTimestamp() == - 1L)
						startDataEventHitValidation(dataEvent, HitEventType.PUNCH, true, this.blueLastPunchHitValidator, judgeNumber);
				} else if(this.redLastPunchHitValidator.get().getHitTimestamp() == - 1L) {
					startDataEventHitValidation(dataEvent, HitEventType.PUNCH, false, this.redLastPunchHitValidator, judgeNumber);
				}
			_addMatchLogItem(dataEvent.getEventTimestamp(),
					Long.valueOf(getCurrentRoundCountdownAsMillis()),
					Long.valueOf(System.currentTimeMillis()),
					getRound4MatchLog(true), getRoundStr4MatchLog(true), blue ? getOTMatchLogItemTypeIfNeed(MatchLogItemType.BLUE_JUDGE_PUNCH)
							: getOTMatchLogItemTypeIfNeed(MatchLogItemType.RED_JUDGE_PUNCH),
					Integer.valueOf(0),
					Integer.valueOf(0), "" + judgeNumber, true,
					Integer.valueOf(0), false);
			if(currentMatchStatusAllowToAddPoints() &&
					canAddJudgePoint(dataEvent, judgeNumber, blue ? this.blueLastPunchHitValidator : this.redLastPunchHitValidator,
							HitEventValidator.ParaTkdTechEvent.NONE)) {
				if(matchWorkerLogger.isDebugEnabled())
					matchWorkerLogger.debug("To add PUNCH POINT by Judges Decission");
				if(blue) {
					addBluePunchPoint(this.punchPoints, ActionSource.JUDGE, dataEvent.getEventTimestamp().longValue(),
							getCurrentRoundCountdownAsMillis());
				} else {
					addRedPunchPoint(this.punchPoints, ActionSource.JUDGE, dataEvent.getEventTimestamp().longValue(),
							getCurrentRoundCountdownAsMillis());
				}
			}
		}
	}

	private void startDataEventHitValidation(DataEvent dataEvent, HitEventType hitEventType, boolean blue,
			SimpleObjectProperty<HitEventValidator> hitEventValidatorProperty, int judgeStart) {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("startDataEventHitValidation()..." + hitEventType + "  judge =" + judgeStart);
		HitEventValidator hitEventValidator = new HitEventValidator(blue, (this.numberOfJudges == 1 && (hitEventType.equals(HitEventType.SPECIAL_HEAD)
				|| hitEventType.equals(HitEventType.SPECIAL_BODY))) ? (dataEvent.getEventTimestamp().longValue() + 1L)
						: dataEvent.getEventTimestamp().longValue(), hitEventType, dataEvent.getHitValue(), this.numberOfJudges,
				HitJudgeStatus.NOT_VALIDATED, (this.numberOfJudges >= 2) ? HitJudgeStatus.NOT_VALIDATED : HitJudgeStatus.NOT_ENABLED,
				(this.numberOfJudges >= 3) ? HitJudgeStatus.NOT_VALIDATED : HitJudgeStatus.NOT_ENABLED, this.backupSystemEnabled.get());
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("(judgeLetsBodyPoints && (hitEventType.equals(HitEventType.BODY))) = " + ((this.judgeLetsBodyPoints
					&& hitEventType.equals(HitEventType.BODY)) ? 1 : 0));
		if((hitEventType.equals(HitEventType.PUNCH) || ( ! this.judgeLetsTechHeadPoints && hitEventType
				.equals(HitEventType.HEAD)) || (this.judgeLetsBodyPoints && hitEventType
						.equals(HitEventType.BODY))) && judgeStart > 0 && judgeStart <= 3) {
			if(matchWorkerLogger.isDebugEnabled())
				matchWorkerLogger.debug("WILL CHANGE HITEVENTVALIDATOR TO VALIDTED FOR JUDGE " + judgeStart);
			if(judgeStart == 1) {
				hitEventValidator.setJudge1HitStatus(HitJudgeStatus.VALIDATED);
			} else if(judgeStart == 2) {
				hitEventValidator.setJudge2HitStatus(HitJudgeStatus.VALIDATED);
			} else if(judgeStart == 3) {
				hitEventValidator.setJudge3HitStatus(HitJudgeStatus.VALIDATED);
			}
		}
		hitEventValidatorProperty.set(hitEventValidator);
		fireHitEventValidator(hitEventValidator);
		try {
			TkStrikeExecutors.schedule(new HitEventValidatorTask(hitEventValidatorProperty), 1000L, TimeUnit.MILLISECONDS);
		} catch(RuntimeException re) {
			matchWorkerLogger.error("Schedule HitEventValidator", re);
		}
	}

	private void startDataEventHitValidation4NotValidInGoldenPoint(DataEvent dataEvent, HitEventType hitEventType, boolean blue, boolean autoRemove) {
		HitEventValidator hitEventValidator = new HitEventValidator(blue, (this.numberOfJudges == 1 && (hitEventType.equals(HitEventType.SPECIAL_HEAD)
				|| hitEventType.equals(HitEventType.SPECIAL_BODY))) ? (dataEvent.getEventTimestamp().longValue() + 1L)
						: dataEvent.getEventTimestamp().longValue(), hitEventType, dataEvent.getHitValue(), this.numberOfJudges,
				HitJudgeStatus.NOT_VALIDATED, (this.numberOfJudges >= 2) ? HitJudgeStatus.NOT_VALIDATED : HitJudgeStatus.NOT_ENABLED,
				(this.numberOfJudges >= 3) ? HitJudgeStatus.NOT_VALIDATED : HitJudgeStatus.NOT_ENABLED, autoRemove, this.backupSystemEnabled.get());
		fireHitEventValidator(hitEventValidator);
	}

	private boolean canFireEvent(HitEventValidator hitEventValidator) {
		if( ! this.showHitsNotValidOnLeft.booleanValue() && hitEventValidator.isAutoRemove())
			return false;
		if( ! this.showHitsNotValidOnLeft.booleanValue() && ! hitEventValidator.isGivenPoint() && hitEventValidator.getHitValue().intValue() == - 1)
			return false;
		return true;
	}

	private void fireHitEventValidator(HitEventValidator hitEventValidator) {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("Fire hasHitEventValidatorChange " + hitEventValidator
					.toString() + " to " + this.hitEventValidatorListeners
							.size() + " listeners");
		if(canFireEvent(hitEventValidator)) {
			HitEventValidator newEvent = new HitEventValidator(hitEventValidator.isBlue(), hitEventValidator.getHitTimestamp(), hitEventValidator
					.getHitEventType(), hitEventValidator.getHitValue(), hitEventValidator.getJudgesEnabled(), hitEventValidator.getJudge1HitStatus(),
					hitEventValidator.getJudge2HitStatus(), hitEventValidator.getJudge3HitStatus(), hitEventValidator.getParaTkdTechEvent(),
					hitEventValidator.isAutoRemove(), this.backupSystemEnabled.get());
			ArrayList<Callable<Void>> tasks = new ArrayList<>(this.hitEventValidatorListeners.size());
			this.hitEventValidatorListeners.forEach(lis -> tasks.add(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					lis.hasHitEventValidatorChange(hitEventValidator);
					return null;
				}
			}));
			try {
				TkStrikeExecutors.executeInParallel(tasks);
			} catch(InterruptedException ignore) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private void fireForceAddHitEventValidator(HitEventValidator hitEventValidator) {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("fireForceAddHitEventValidator " + hitEventValidator.toString());
		HitEventValidator newEvent = new HitEventValidator(hitEventValidator.isBlue(), hitEventValidator.getHitTimestamp(), hitEventValidator
				.getHitEventType(), hitEventValidator.getHitValue(), hitEventValidator.getJudgesEnabled(), hitEventValidator.getJudge1HitStatus(),
				hitEventValidator.getJudge2HitStatus(), hitEventValidator.getJudge3HitStatus(), this.backupSystemEnabled.get(), hitEventValidator
						.getParaTkdTechEvent());
		ArrayList<Callable<Void>> tasks = new ArrayList<>(this.hitEventValidatorListeners.size());
		this.hitEventValidatorListeners.forEach(lis -> tasks.add(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				lis.forceAddNewHitEvent(hitEventValidator);
				return null;
			}
		}));
		try {
			TkStrikeExecutors.executeInParallel(tasks);
		} catch(InterruptedException ignore) {
			Thread.currentThread().interrupt();
		}
	}

	void fireRemoveGoldenPointNearMissHit(boolean blue) {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("Fire fireRemoveGoldenPointNearMissHit: blue?" + blue);
		ArrayList<Callable<Void>> tasks = new ArrayList<>(this.hitEventValidatorListeners.size());
		this.hitEventValidatorListeners.forEach(lis -> tasks.add(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				lis.removeGoldenPointNearMissHit(blue);
				return null;
			}
		}));
		try {
			TkStrikeExecutors.executeInParallel(tasks);
		} catch(InterruptedException ignore) {
			Thread.currentThread().interrupt();
		}
	}

	void fireAddGoldenPointNearMissHit(boolean blue) {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("Fire fireAddGoldenPointNearMissHit: blue?" + blue);
		ArrayList<Callable<Void>> tasks = new ArrayList<>(this.hitEventValidatorListeners.size());
		this.hitEventValidatorListeners.forEach(lis -> tasks.add(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				lis.addGoldenPointNearMissHit(blue);
				return null;
			}
		}));
		try {
			TkStrikeExecutors.executeInParallel(tasks);
		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	final void fireTryToRemoveHitEvent(boolean blue, HitEventType hitEventType) {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("Fire fireTryToRemoveHitEvent: blue?" + blue + " hitEventType:" + hitEventType);
		ArrayList<Callable<Void>> tasks = new ArrayList<>(this.hitEventValidatorListeners.size());
		this.hitEventValidatorListeners.forEach(lis -> tasks.add(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				lis.tryToRemoveHitEvent(blue, hitEventType);
				return null;
			}
		}));
		try {
			TkStrikeExecutors.executeInParallel(tasks);
		} catch(InterruptedException ignore) {
			Thread.currentThread().interrupt();
		}
	}

	final void fireTryToChangeHitTechEvent(boolean blue, HitEventType hitEventType, boolean validated) {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("Fire tryToChangeHitTechEvent: blue?" + blue + " hitEventType:" + hitEventType + " validated:" + validated);
		ArrayList<Callable<Void>> tasks = new ArrayList<>(this.hitEventValidatorListeners.size());
		this.hitEventValidatorListeners.forEach(lis -> tasks.add(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				lis.tryToChangeHitTechEvent(blue, hitEventType, validated);
				return null;
			}
		}));
		try {
			TkStrikeExecutors.executeInParallel(tasks);
		} catch(InterruptedException ignore) {
			Thread.currentThread().interrupt();
		}
	}

	final boolean canAddJudgePoint(DataEvent judgeDataEvent, int judgeNumber,
			SimpleObjectProperty<HitEventValidator> athleteHitEventValidatorProperty, HitEventValidator.ParaTkdTechEvent paraTkdTechEvent) {
		boolean res = false;
		if(judgeDataEvent != null && athleteHitEventValidatorProperty != null) {
			HitEventValidator athleteHit = athleteHitEventValidatorProperty.get();
			if(athleteHit != null && judgeDataEvent.getEventTimestamp().longValue() - athleteHit.getHitTimestamp() <= 1000L) {
				if(matchWorkerLogger.isDebugEnabled())
					matchWorkerLogger.debug("Judge press before a second, ok..." + paraTkdTechEvent.toString() + " currentPara?" + athleteHit
							.getParaTkdTechEvent());
				if( ! paraTkdTechEvent.equals(HitEventValidator.ParaTkdTechEvent.NONE) &&
						! athleteHit.getParaTkdTechEvent().equals(HitEventValidator.ParaTkdTechEvent.NONE) &&
						! athleteHit.getParaTkdTechEvent().equals(paraTkdTechEvent)) {
					if(matchWorkerLogger.isDebugEnabled())
						matchWorkerLogger.info("A judge press previous PARAT TkdEvent");
					return false;
				}
				athleteHit.setParaTkdTechEvent(paraTkdTechEvent);
				switch(judgeNumber) {
					case 1:
						athleteHit.setJudge1HitStatus(HitJudgeStatus.VALIDATED);
						break;
					case 2:
						athleteHit.setJudge2HitStatus(HitJudgeStatus.VALIDATED);
						break;
					case 3:
						athleteHit.setJudge3HitStatus(HitJudgeStatus.VALIDATED);
						break;
				}
				if( ! athleteHit.isGivenPoint())
					res = athleteHit.allJudgesValidated();
				if(matchWorkerLogger.isDebugEnabled())
					matchWorkerLogger.debug(athleteHit.getHitEventType() + " - j1:" + athleteHit.getJudge1HitStatus() + " j2:" + athleteHit
							.getJudge2HitStatus() + " j3:" + athleteHit.getJudge3HitStatus());
				if(res)
					athleteHit.setGivenPoint(true);
				fireHitEventValidator(athleteHit);
				athleteHitEventValidatorProperty.set(athleteHit);
			} else {
				if(matchWorkerLogger.isDebugEnabled())
					matchWorkerLogger.debug("Judge " + judgeNumber + " press after a second...");
				HitEventValidator dummyHit = new HitEventValidator((athleteHit != null && athleteHit.isBlue()), judgeDataEvent.getEventTimestamp()
						.longValue(), athleteHit.getHitEventType(), this.backupSystemEnabled.get());
				dummyHit.setJudgesEnabled(this.numberOfJudges);
				dummyHit.setHitValue(Integer.valueOf( - 1));
				dummyHit.setBlue(athleteHit.isBlue());
				dummyHit.setParaTkdTechEvent(paraTkdTechEvent);
				switch(judgeNumber) {
					case 1:
						dummyHit.setJudge1HitStatus(HitJudgeStatus.VALIDATED);
						break;
					case 2:
						dummyHit.setJudge2HitStatus(HitJudgeStatus.VALIDATED);
						break;
					case 3:
						dummyHit.setJudge3HitStatus(HitJudgeStatus.VALIDATED);
						break;
				}
				dummyHit.setGivenPoint(false);
				fireHitEventValidator(dummyHit);
			}
		}
		return res;
	}

	final boolean isValidHeadImpact(String nodeId, Integer hitLevel, SimpleIntegerProperty athleteGoldenPointImpacts, String athleteRoundsHitsName,
			ConcurrentHashMap<Integer, Integer> athleteRoundsHits) {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("isValidaHeadImpact ?" + hitLevel);
		return isValidImpact(hitLevel, getMinHeadLevel4AthleteNodeId(nodeId), athleteGoldenPointImpacts, athleteRoundsHitsName, athleteRoundsHits);
	}

	final boolean isValidBodyImpact(String nodeId, Integer hitLevel, SimpleIntegerProperty athleteGoldenPointImpacts, String athleteRoundsHitsName,
			ConcurrentHashMap<Integer, Integer> athleteRoundsHits) {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("isValidBodyImpact ?" + hitLevel);
		return isValidImpact(hitLevel, getMinBodyLevel4AthleteNodeId(nodeId), athleteGoldenPointImpacts, athleteRoundsHitsName, athleteRoundsHits);
	}

	final boolean isValidImpact(Integer hitLevel, Integer minLevel, SimpleIntegerProperty athleteGoldenPointImpacts, String athleteRoundsHitsName,
			ConcurrentHashMap<Integer, Integer> athleteRoundsHits) {
		if(matchWorkerLogger.isDebugEnabled())
			matchWorkerLogger.debug("isValidImpact ?" + hitLevel);
		if(hitLevel.intValue() < minLevel.intValue() && hitLevel.intValue() >= this.nearMissLevel)
			appendToCounter(athleteRoundsHitsName, athleteRoundsHits, this.currentRound.getValue(), Integer.valueOf(1));
		if(this.goldenPointEnabled && this.goldenPointWorking && hitLevel.intValue() < minLevel.intValue() && hitLevel
				.intValue() >= this.nearMissLevel)
			athleteGoldenPointImpacts.set(athleteGoldenPointImpacts.add(1).get());
		return (hitLevel.intValue() >= minLevel.intValue());
	}

	final void _sendNewMatchConfiguredToExternalEventsListeners(final MatchConfigurationDto matchConfigurationDto) {
		if(matchConfigurationDto != null)
			TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					try {
						List<Callable<Void>> tasks = null;
						final ExternalConfigEntry externalConfigEntry = BaseCommonMatchWorker.this.externalConfigService.getExternalConfigEntry();
						if(externalConfigEntry.getListenersURLs() != null) {
							tasks = new ArrayList<>(externalConfigEntry.getListenersURLs().size());
							for(String listenerURL : externalConfigEntry.getListenersURLs()) {
								tasks.add(new Callable<Void>() {

									@Override
									public Void call() throws Exception {
										BaseCommonMatchWorker.this.tkStrikeEventsListenerClient.sendHasNewMatchConfigured(listenerURL,
												matchConfigurationDto);
										return null;
									}
								});
							}
						}
						if(BaseCommonMatchWorker.this.rtBroadcastSocketClient.isConnected()) {
							if(tasks == null)
								tasks = new ArrayList<>(1);
							tasks.add(new Callable<Void>() {

								@Override
								public Void call() throws Exception {
									BaseCommonMatchWorker.this.rtBroadcastSocketClient.sendHasNewMatchConfigured(matchConfigurationDto);
									return null;
								}
							});
							tasks.add(new Callable<Void>() {

								@Override
								public Void call() throws Exception {
									BaseCommonMatchWorker.this.tkStrikeUDPFacadeEventsListenerClient.sendHasNewMatchConfigured(matchConfigurationDto);
									return null;
								}
							});
						}
						if(StringUtils.isNotBlank(externalConfigEntry.getWtOvrUrl())) {
							if(tasks == null)
								tasks = new ArrayList<>(1);
							tasks.add(new Callable<Void>() {

								@Override
								public Void call() throws Exception {
									if(BaseCommonMatchWorker.matchWorkerLogger.isDebugEnabled())
										BaseCommonMatchWorker.matchWorkerLogger.info("Go to Send to OVR event MatchLoadedAction");
									BaseCommonMatchWorker.this.wtOvrClientService.resetCounter();
									BaseCommonMatchWorker.this.wtOvrClientService.sendMatchLoadedAction(externalConfigEntry.getWtOvrUrl(),
											externalConfigEntry.getWtOvrXApiKey(), BaseCommonMatchWorker.this.currentMatchVMInternalId,
											BaseCommonMatchWorker.this.currentMatchBlueAthleteOvrInternalId,
											BaseCommonMatchWorker.this.currentMatchRedAthleteOvrInternalId, matchConfigurationDto);
									return null;
								}
							});
						}
						if(tasks != null)
							TkStrikeExecutors.executeInParallel(tasks);
					} catch(InterruptedException e) {
						Thread.currentThread().interrupt();
						BaseCommonMatchWorker.matchWorkerLogger.error("on sendNewMatchConfigured to Listener", e);
					} catch(Exception e) {
						BaseCommonMatchWorker.matchWorkerLogger.error("on sendNewMatchConfigured to Listener", e);
					}
					return null;
				}
			});
	}

	final MatchLogItemType getOTMatchLogItemTypeIfNeed(MatchLogItemType hitMatchLogItemType) {
		if(currentMatchStatusAllowToAddPoints())
			return hitMatchLogItemType;
		return MatchLogItemType.valueOf("OT_" + hitMatchLogItemType.toString());
	}

	private void createMatchLog(IMatchConfigurationEntry matchConfigurationEntry, IRulesEntry rulesEntry,
			INetworkConfigurationEntry networkConfigurationEntry) {
		try {
			if(matchConfigurationEntry != null) {
				CommonMatchLogDto matchLog = getMatchLogService().createNew(matchConfigurationEntry, rulesEntry,

						Integer.valueOf(this.numberOfJudges),
						Boolean.valueOf(isBodySensorsEnabled4Config(networkConfigurationEntry)),
						Boolean.valueOf(isHeadSensorsEnabled4Config(networkConfigurationEntry)));
				if(matchLog != null)
					this.matchLogId = matchLog.getId();
			}
		} catch(TkStrikeServiceException e) {
			matchWorkerLogger.warn("createMatchLog", e);
		}
	}

	private MLD updateMatchLogMatchFinish(Long newEndTime, MatchWinner matchWinner, FinalDecision finalDecision, String matchResult,
			GoldenPointTieBreakerInfoDto goldenPointTieBreakerInfo) {
		try {
			if(this.matchLogId != null) {
				MLD matchLog = getMatchLogService().getById(this.matchLogId);
				if(matchLog == null || matchLog.getMatchStartTime() == null)
					getMatchLogService().updateMatchLogStartTime(this.matchLogId, Long.valueOf(System.currentTimeMillis()));
				final MLD theMatchLog = getMatchLogService().updateMatchLogFinish(this.matchLogId, newEndTime, matchWinner, finalDecision,
						matchResult, goldenPointTieBreakerInfo);
				TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

					@Override
					public Void call() throws Exception {
						try {
							BaseCommonMatchWorker.this.getMatchLogHistoricalService().migrateToHistorical(theMatchLog);
						} catch(Exception e) {
							BaseCommonMatchWorker.matchWorkerLogger.error("Migrate to historical", e);
						}
						GenerateMatchLogHistoricalService service = new GenerateMatchLogHistoricalService(BaseCommonMatchWorker.this
								.getMatchLogHistoricalService(), BaseCommonMatchWorker.this.appStatusWorker, BaseCommonMatchWorker.this.matchLogId);
						service.start();
						System.gc();
						return null;
					}
				});
				return getMatchLogService().getById(this.matchLogId);
			}
		} catch(TkStrikeServiceException e) {
			matchWorkerLogger.error("Update MatchLog End Time", e);
		}
		return null;
	}

	void updateMatchLogStartTime(final Long newStartTime) {
		TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				try {
					if(BaseCommonMatchWorker.this.matchLogId != null)
						BaseCommonMatchWorker.this.getMatchLogService().updateMatchLogStartTime(BaseCommonMatchWorker.this.matchLogId, newStartTime);
					if(BaseCommonMatchWorker.this.currentMatchId != null)
						BaseCommonMatchWorker.this.getMatchConfigurationService().updateMatchIsStarted(BaseCommonMatchWorker.this.currentMatchId);
				} catch(TkStrikeServiceException e) {
					BaseCommonMatchWorker.matchWorkerLogger.error("Update MatchLog Start Time", e);
				}
				return null;
			}
		});
	}

	private void updateMatchLogNetworkInfo(final INetworkConfigurationEntry networkConfigurationEntry) {
		TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				try {
					if(BaseCommonMatchWorker.this.matchLogId != null)
						BaseCommonMatchWorker.this.getMatchLogService().updateNetworkInfo(BaseCommonMatchWorker.this.matchLogId,
								Integer.valueOf(BaseCommonMatchWorker.this.numberOfJudges),
								Boolean.valueOf(BaseCommonMatchWorker.this.isBodySensorsEnabled4Config(networkConfigurationEntry)),
								Boolean.valueOf(BaseCommonMatchWorker.this.isHeadSensorsEnabled4Config(networkConfigurationEntry)));
				} catch(TkStrikeServiceException e) {
					BaseCommonMatchWorker.matchWorkerLogger.error("Update MatchLog NetworkInfo", e);
				}
				return null;
			}
		});
	}

	private void refreshMatchLogWinners() {
		final String tMatchLogId = this.matchLogId;
		TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				if(tMatchLogId != null)
					BaseCommonMatchWorker.this.getMatchLogService().updateMatchLogRoundsWinners(tMatchLogId, BaseCommonMatchWorker.this.roundsWinner);
				return null;
			}
		});
	}

	Integer getRound4MatchLog(boolean useCurrentRound) {
		return Integer.valueOf(( ! useCurrentRound && usePrevRoundNumber()) ? (this.currentRound.get() - 1) : this.currentRound.get());
	}

	String getRoundStr4MatchLog(boolean useCurrentRound) {
		return ( ! useCurrentRound && usePrevRoundNumber()) ? ("" + (this.currentRound.get() - 1)) : this.currentRoundStr.get();
	}

	private boolean usePrevRoundNumber() {
		return (MatchVictoryCriteria.CONVENTIONAL.equals(getMatchVictoryCriteria()) && MatchStatusId.WAITING_4_START_ROUND.equals(
				getCurrentMatchStatus()) && this.currentRound.get() > 1);
	}

	final void _addMatchLogItem(final Long eventTime, final Long roundTime, final Long systemTime, final Integer roundNumber,
			final String roundNumberStr, final MatchLogItemType matchLogItemType, final Integer blueAddPoints, final Integer redAddPoints,
			final String entryValue, final boolean sendToRtBroadcast, final Integer prevSensorHitValue, final boolean eventFromScoreboardEditor) {
		final boolean eventAddPoints = (blueAddPoints.intValue() > 0 || redAddPoints.intValue() > 0 || MatchLogItemType.BLUE_ADD_GAME_JEON.equals(
				matchLogItemType) || MatchLogItemType.RED_ADD_GAME_JEON.equals(matchLogItemType));
		final boolean eventRemovePoints = (blueAddPoints.intValue() < 0 || redAddPoints.intValue() < 0 || MatchLogItemType.BLUE_REMOVE_GAME_JEON
				.equals(matchLogItemType) || MatchLogItemType.RED_REMOVE_GAME_JEON.equals(matchLogItemType));
		TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

			@Override
			public Void call() {
				if(MatchLogItemType.START_MATCH.equals(matchLogItemType))
					try {
						BaseCommonMatchWorker.this.getMatchLogService().deleteWhenSystemTimeLessThan(systemTime);
					} catch(TkStrikeServiceException e) {
						BaseCommonMatchWorker.matchWorkerLogger.warn("Deleting MatchLog Items", e);
					}
				Integer intBluePoints = Integer.valueOf(MatchVictoryCriteria.CONVENTIONAL.equals(BaseCommonMatchWorker.this.matchVictoryCriteria)
						? BaseCommonMatchWorker.this.getBlueMatchPoints().intValue()
						: BaseCommonMatchWorker.this.blueGeneralPoints.intValue());
				Integer intRedPoints = Integer.valueOf(MatchVictoryCriteria.CONVENTIONAL.equals(BaseCommonMatchWorker.this.matchVictoryCriteria)
						? BaseCommonMatchWorker.this.getRedMatchPoints().intValue()
						: BaseCommonMatchWorker.this.redGeneralPoints.intValue());
				if(BaseCommonMatchWorker.this.wtUDPService.isConnected()) {
					if(blueAddPoints.intValue() > 0 && (MatchLogItemType.BLUE_BODY_POINT
							.equals(matchLogItemType) || MatchLogItemType.BLUE_HEAD_POINT
									.equals(matchLogItemType) || MatchLogItemType.BLUE_PUNCH_POINT
											.equals(matchLogItemType)))
						BaseCommonMatchWorker.this.wtUDPService.sendPointsChange(blueAddPoints, null);
					if(redAddPoints.intValue() > 0 && (MatchLogItemType.RED_BODY_POINT
							.equals(matchLogItemType) || MatchLogItemType.RED_HEAD_POINT
									.equals(matchLogItemType) || MatchLogItemType.RED_PUNCH_POINT
											.equals(matchLogItemType)))
						BaseCommonMatchWorker.this.wtUDPService.sendPointsChange(null, redAddPoints);
				}
				try {
					MLID matchLogItem = BaseCommonMatchWorker.this.newMatchLogItemDtoInstance();
					matchLogItem.setMatchLogId(BaseCommonMatchWorker.this.matchLogId);
					matchLogItem.setRoundNumberStr(roundNumberStr);
					matchLogItem.setRoundNumber(roundNumber);
					matchLogItem.setEventTime(eventTime);
					matchLogItem.setRoundTime(roundTime);
					matchLogItem.setSystemTime(systemTime);
					matchLogItem.setMatchLogItemType(matchLogItemType);
					matchLogItem.setBlueGeneralPoints(intBluePoints);
					matchLogItem.setRedGeneralPoints(intRedPoints);
					matchLogItem.setBluePoints(Integer.valueOf(BaseCommonMatchWorker.this.bluePoints.get()));
					matchLogItem.setRedPoints(Integer.valueOf(BaseCommonMatchWorker.this.redPoints.get()));
					matchLogItem.setBluePenalties(Integer.valueOf(BaseCommonMatchWorker.this.bluePenalties.get()));
					matchLogItem.setRedPenalties(Integer.valueOf(BaseCommonMatchWorker.this.redPenalties.get()));
					matchLogItem.setBlueTotalPenalties(Integer.valueOf(BaseCommonMatchWorker.this.blueTotalPenalties.get()));
					matchLogItem.setRedTotalPenalties(Integer.valueOf(BaseCommonMatchWorker.this.redTotalPenalties.get()));
					matchLogItem.setBlueAddPoints(blueAddPoints);
					matchLogItem.setRedAddPoints(redAddPoints);
					matchLogItem.setBlueGoldenPointHits(Integer.valueOf(BaseCommonMatchWorker.this.blueGoldenPointImpacts.get()));
					matchLogItem.setRedGoldenPointHits(Integer.valueOf(BaseCommonMatchWorker.this.redGoldenPointImpacts.get()));
					matchLogItem.setBlueGoldenPointPenalties(Integer.valueOf(BaseCommonMatchWorker.this.blueGoldenPointPenalties.get()));
					matchLogItem.setRedGoldenPointPenalties(Integer.valueOf(BaseCommonMatchWorker.this.redGoldenPointPenalties.get()));
					matchLogItem.setGoldenPointRound(Boolean.valueOf((BaseCommonMatchWorker.this.goldenPointEnabled
							&& BaseCommonMatchWorker.this.goldenPointWorking)));
					matchLogItem.setEntryValue(entryValue);
					BaseCommonMatchWorker.this.fillExtraInfo2MatchLogItem(matchLogItem);
					BaseCommonMatchWorker.this.getMatchLogService().addMatchLogItem(BaseCommonMatchWorker.this.matchLogId, matchLogItem);
					if(ArrayUtils.contains(BaseCommonMatchWorker.this.tkStrikeEventsListenersMatchLogItemTypesAllowed, matchLogItemType.toString()))
						BaseCommonMatchWorker.this.sendEventToExternalEventsListeners(matchLogItemType.toString(),
								BaseCommonMatchWorker.this.currentMatchNumber, roundNumber, roundNumberStr, roundTime, systemTime, eventAddPoints,
								eventRemovePoints, intBluePoints, BaseCommonMatchWorker.this

										.getBlueCurrentPenalties4EventsListeners(), intRedPoints, BaseCommonMatchWorker.this

												.getRedCurrentPenalties4EventsListeners(), BaseCommonMatchWorker.this
														.getBlueRoundsWins(), BaseCommonMatchWorker.this
																.getRedRoundsWins(),
								MatchLogItemType.MATCH_FINISHED.equals(matchLogItemType) ? BaseCommonMatchWorker.this.getMatchWinner().toString()
										: null, MatchLogItemType.MATCH_FINISHED.equals(matchLogItemType) ? BaseCommonMatchWorker.this
												.getMatchFinalDecision().toString() : null, entryValue, eventFromScoreboardEditor,
								prevSensorHitValue);
				} catch(TkStrikeServiceException e) {
					BaseCommonMatchWorker.matchWorkerLogger.error("Add MatchLogItem", e);
				}
				if(sendToRtBroadcast && BaseCommonMatchWorker.this.rtBroadcastSocketClient
						.isConnected() &&
						ArrayUtils.contains(BaseCommonMatchWorker.this.tkStrikeRtBroadcastMatchLogItemTypesAllowed, matchLogItemType.toString())) {
					String matchWinner = null;
					if(MatchLogItemType.MATCH_FINISHED.equals(matchLogItemType))
						switch(BaseCommonMatchWorker.this.getMatchWinner()) {
							case TIE:
								matchWinner = "TIE";
								break;
							case BLUE:
								matchWinner = BaseCommonMatchWorker.this.getBlueName();
								break;
							case RED:
								matchWinner = BaseCommonMatchWorker.this.getRedName();
								break;
						}
					BaseCommonMatchWorker.this.sendEventToRtBroadcast(matchLogItemType.toString(), BaseCommonMatchWorker.this.currentMatchNumber,
							roundNumber, roundNumberStr,

							Boolean.valueOf(BaseCommonMatchWorker.this.goldenPointWorking), roundTime, eventTime, eventAddPoints, eventRemovePoints,
							intBluePoints,

							Integer.valueOf(BaseCommonMatchWorker.this.bluePenalties.get()), blueAddPoints, BaseCommonMatchWorker.this

									.getBlueRoundsWins(), intRedPoints,
							Integer.valueOf(BaseCommonMatchWorker.this.redPenalties.get()), redAddPoints, BaseCommonMatchWorker.this

									.getRedRoundsWins(), prevSensorHitValue,
							MatchLogItemType.MATCH_FINISHED.equals(matchLogItemType) ? BaseCommonMatchWorker.this.getMatchFinalDecision().toString()
									: null, matchWinner, entryValue, MatchLogItemType.MATCH_FINISHED.equals(matchLogItemType)
											? BaseCommonMatchWorker.this.getMatchWinner().toString()
											: null);
				}
				return null;
			}
		});
	}

	final void sendEventToExternalEventsListeners(final String matchLogItemType, final String matchNumber, final Integer roundNumber,
			final String roundNumberStr, final Long roundTimestamp, final Long eventTimestamp, final boolean eventAddPoints,
			final boolean eventRemovePoints, final Integer bluePoints, final Integer bluePenalties, final Integer redPoints,
			final Integer redPenalties, final Integer blueRoundWins, final Integer redRoundWins, final String matchWinner,
			final String matchFinalDecission, final String hitValue, final boolean eventFromScoreboardEditor, final Integer hitlevel) {
		TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

			@Override
			public Void call() {
				String roundTimeStr = BaseCommonMatchWorker.this.sdfRoundTime.format(new Date(roundTimestamp.longValue()));
				String eventTimeStr = BaseCommonMatchWorker.this.sdfSystemTime.format(new Date(eventTimestamp.longValue()));
				final TkStrikeEventDto tkStrikeEventDto = new TkStrikeEventDto(matchNumber, BaseCommonMatchWorker.this.currentMatchVMInternalId,
						BaseCommonMatchWorker.this.currentMatchVMRingNumber, roundNumber, roundNumberStr, eventTimestamp, eventTimeStr,
						roundTimestamp, roundTimeStr, matchLogItemType, eventAddPoints, eventRemovePoints, bluePoints, bluePenalties, redPoints,
						redPenalties, blueRoundWins, redRoundWins, matchWinner, matchFinalDecission, BaseCommonMatchWorker.this.getBlueBodyHit(
								hitValue, matchLogItemType), BaseCommonMatchWorker.this.getBlueHeadHit(hitValue, matchLogItemType),
						BaseCommonMatchWorker.this.getRedBodyHit(hitValue, matchLogItemType), BaseCommonMatchWorker.this.getRedHeadHit(hitValue,
								matchLogItemType), BaseCommonMatchWorker.this.getIfNeedCalledByJudgeNumber(matchLogItemType, hitValue), hitlevel);
				BaseCommonMatchWorker.this.fillExtraInfo2TkStrikeEvent(tkStrikeEventDto);
				try {
					List<Callable<Void>> tasks = null;
					final ExternalConfigEntry externalConfigEntry = BaseCommonMatchWorker.this.externalConfigService.getExternalConfigEntry();
					if(externalConfigEntry.getListenersURLs() != null) {
						tasks = new ArrayList<>(externalConfigEntry.getListenersURLs().size());
						for(String listenerURL : externalConfigEntry.getListenersURLs()) {
							tasks.add(new Callable<Void>() {

								@Override
								public Void call() throws Exception {
									BaseCommonMatchWorker.this.tkStrikeEventsListenerClient.sendNewMatchEvent(listenerURL, tkStrikeEventDto);
									return null;
								}
							});
						}
						tasks.add(new Callable<Void>() {

							@Override
							public Void call() throws Exception {
								BaseCommonMatchWorker.this.tkStrikeUDPFacadeEventsListenerClient.sendNewMatchEvent(tkStrikeEventDto);
								return null;
							}
						});
					}
					if(externalConfigEntry.getWtOvrUrl() != null) {
						if(tasks == null)
							tasks = new ArrayList<>(1);
						tasks.add(new Callable<Void>() {

							@Override
							public Void call() throws Exception {
								if(BaseCommonMatchWorker.matchWorkerLogger.isDebugEnabled())
									BaseCommonMatchWorker.matchWorkerLogger.info("Go to Send to OVR event MatchAction");
								BaseCommonMatchWorker.this.wtOvrClientService.sendMatchAction(externalConfigEntry.getWtOvrUrl(), externalConfigEntry
										.getWtOvrXApiKey(), BaseCommonMatchWorker.this.currentMatchVMInternalId,
										BaseCommonMatchWorker.this.currentMatchBlueAthleteOvrInternalId,
										BaseCommonMatchWorker.this.currentMatchRedAthleteOvrInternalId, tkStrikeEventDto, eventFromScoreboardEditor);
								return null;
							}
						});
						if(MatchLogItemType.END_ROUND.toString().equals(matchLogItemType)) {
							BaseCommonMatchWorker.matchWorkerLogger.info("Event is END_ROUND send match result as partial");
							tasks.add(new Callable<Void>() {

								@Override
								public Void call() throws Exception {
									MatchResultDto matchResultDto = new MatchResultDto();
									SimpleObjectProperty<FinalDecision> tempFinalDecision = new SimpleObjectProperty(FinalDecision.WDR);
									SimpleObjectProperty<MatchWinner> tempMatchWinner = new SimpleObjectProperty(MatchWinner.TIE);
									BaseCommonMatchWorker.this.validateIfMatchFinish(Integer.valueOf(BaseCommonMatchWorker.this.blueGeneralPoints
											.get()), bluePenalties, bluePoints,

											Integer.valueOf(BaseCommonMatchWorker.this.redGeneralPoints.get()), redPenalties, redPoints,
											new MutableBoolean(Boolean.FALSE), tempFinalDecision, tempMatchWinner);
									matchResultDto.setRoundFinish(Integer.valueOf(BaseCommonMatchWorker.this.currentRound.get()));
									matchResultDto.setMatchWinnerColor(tempMatchWinner.getValue().toString());
									BaseCommonMatchWorker.matchWorkerLogger.debug("sendEventToExternals - END ROUND finalDecision:"
											+ tempFinalDecision.getValue() + " tempRoundWinner " + tempMatchWinner + " superiority?"
											+ BaseCommonMatchWorker.this.bestOf3RoundWithSuperiority.get());
									if(MatchVictoryCriteria.BESTOF3.equals(BaseCommonMatchWorker.this.matchVictoryCriteria)) {
										matchResultDto.setMatchFinalDecision("R-" + tempFinalDecision.getValue().toString());
									} else {
										matchResultDto.setMatchFinalDecision(tempFinalDecision.getValue().toString());
									}
									matchResultDto.setBluePoints(bluePoints);
									matchResultDto.setRedPoints(redPoints);
									matchResultDto.setBluePenalties(Integer.valueOf(MatchVictoryCriteria.BESTOF3.equals(
											BaseCommonMatchWorker.this.matchVictoryCriteria) ? BaseCommonMatchWorker.this.getBluePenalties()
													: BaseCommonMatchWorker.this.getBlueTotalPenalties()));
									matchResultDto.setRedPenalties(Integer.valueOf(MatchVictoryCriteria.BESTOF3.equals(
											BaseCommonMatchWorker.this.matchVictoryCriteria) ? BaseCommonMatchWorker.this.getRedPenalties()
													: BaseCommonMatchWorker.this.getRedTotalPenalties()));
									if(MatchVictoryCriteria.BESTOF3.equals(BaseCommonMatchWorker.this.matchVictoryCriteria) && MatchWinner.TIE.equals(
											tempMatchWinner.get())) {
										BaseCommonMatchWorker.matchWorkerLogger.debug("sendEventToExternals - END ROUND bestOf3 i TIE");
										matchResultDto.setMatchWinnerColor(BaseCommonMatchWorker.this.roundsWinner.get(Integer.valueOf(
												BaseCommonMatchWorker.this.currentRound.get())).toString());
										if( ! MatchWinner.TIE.toString().equals(matchResultDto.getMatchWinnerColor()) && "R-WDR"
												.equals(matchResultDto.getMatchFinalDecision()))
											if(bluePoints.equals(redPoints)) {
												matchResultDto.setMatchFinalDecision("R-" + FinalDecision.SUP);
											} else {
												matchResultDto.setMatchFinalDecision("R-" + FinalDecision.PTF);
											}
									}
									BaseCommonMatchWorker.this.wtOvrClientService.sendMatchResult(externalConfigEntry.getWtOvrUrl(),
											externalConfigEntry
													.getWtOvrXApiKey(), BaseCommonMatchWorker.this.currentMatchVMInternalId, matchResultDto,
											ResultStatus.INTERMEDIATE, BaseCommonMatchWorker.this.currentMatchBlueAthleteOvrInternalId,
											BaseCommonMatchWorker.this.currentMatchRedAthleteOvrInternalId);
									BaseCommonMatchWorker.this.sendRoundWinnerToWtUdp();
									return null;
								}
							});
						}
					}
					if(tasks != null)
						TkStrikeExecutors.executeInParallel(tasks);
				} catch(InterruptedException e) {
					Thread.currentThread().interrupt();
					BaseCommonMatchWorker.matchWorkerLogger.warn("Sending to EventListener", e);
				} catch(Exception e) {
					BaseCommonMatchWorker.matchWorkerLogger.warn("Sending to EventListener", e);
				}
				return null;
			}
		});
	}

	private Integer getIfNeedCalledByJudgeNumber(String matchLogItemType, String hitValue) {
		return (matchLogItemType != null && (matchLogItemType
				.contains("_JUDGE_") || "MEETING".equals(matchLogItemType))) ? Integer.valueOf(Integer.parseInt(hitValue)) : null;
	}

	final void sendEventToRtBroadcast(final String matchLogItemType, final String matchNumber, final Integer roundNumber, final String roundNumberStr,
			final Boolean goldenPointWorking, final Long roundTimestamp, final Long eventTimestamp, final boolean eventAddPoints,
			final boolean eventRemovePoints, final Integer bluePoints, final Integer bluePenalties, final Integer blueAddPoints,
			final Integer blueRoundWins, final Integer redPoints, final Integer redPenalties, final Integer redAddPoints, final Integer redRoundWins,
			final Integer prevSensorHitValue, final String matchFinalDecission, final String winnerName, final String matchLogEntryValue,
			final String matchWinner) {
		TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

			@Override
			public Void call() {
				TkStrikeEventDto tkStrikeEventDto = new TkStrikeEventDto(matchNumber, BaseCommonMatchWorker.this.currentMatchVMInternalId,
						BaseCommonMatchWorker.this.currentMatchVMRingNumber, roundNumber, roundNumberStr, eventTimestamp, roundTimestamp,
						matchLogItemType, eventAddPoints, eventRemovePoints, bluePoints, bluePenalties, redPoints, redPenalties, blueRoundWins,
						redRoundWins, matchWinner, matchFinalDecission);
				BaseCommonMatchWorker.this.fillExtraInfo2TkStrikeEvent(tkStrikeEventDto);
				BaseCommonMatchWorker.this.rtBroadcastSocketClient.sendNewMatchEvent(tkStrikeEventDto, goldenPointWorking, blueAddPoints,
						redAddPoints, prevSensorHitValue, matchLogEntryValue, winnerName);
				return null;
			}
		});
	}

	private Integer getBlueBodyHit(String hitValue, String matchLogItemType) {
		if(StringUtils.isNumeric(hitValue))
			return (MatchLogItemType.BLUE_BODY_HIT.toString().equals(matchLogItemType) || MatchLogItemType.OT_BLUE_BODY_HIT
					.toString().equals(matchLogItemType)) ? Integer.valueOf(Integer.parseInt(hitValue)) : null;
		return null;
	}

	private Integer getBlueHeadHit(String hitValue, String matchLogItemType) {
		if(StringUtils.isNumeric(hitValue))
			return (MatchLogItemType.BLUE_HEAD_HIT.toString().equals(matchLogItemType) || MatchLogItemType.OT_BLUE_HEAD_HIT
					.toString().equals(matchLogItemType)) ? Integer.valueOf(Integer.parseInt(hitValue)) : null;
		return null;
	}

	private Integer getRedBodyHit(String hitValue, String matchLogItemType) {
		if(StringUtils.isNumeric(hitValue))
			return (MatchLogItemType.RED_BODY_HIT.toString().equals(matchLogItemType) || MatchLogItemType.OT_RED_BODY_HIT
					.toString().equals(matchLogItemType)) ? Integer.valueOf(Integer.parseInt(hitValue)) : null;
		return null;
	}

	private Integer getRedHeadHit(String hitValue, String matchLogItemType) {
		if(StringUtils.isNumeric(hitValue))
			return (MatchLogItemType.RED_HEAD_HIT.toString().equals(matchLogItemType) || MatchLogItemType.OT_RED_HEAD_HIT
					.toString().equals(matchLogItemType)) ? Integer.valueOf(Integer.parseInt(hitValue)) : null;
		return null;
	}

	abstract MCS getMatchConfigurationService();

	abstract MLS getMatchLogService();

	abstract MLHS getMatchLogHistoricalService();

	abstract GNSC getGlobalNetworkStatusController();

	abstract SPS getSoundPlayerService();

	abstract String getBlueName();

	abstract String getRedName();

	abstract void _internalConfirmFinalResult(MLD paramMLD, MatchWinner paramMatchWinner, FinalDecision paramFinalDecision);

	abstract Integer getMinBodyLevel4AthleteNodeId(String paramString);

	abstract Integer getMinHeadLevel4AthleteNodeId(String paramString);

	abstract void _cleanMatchInfo();

	abstract void _afterPropertiesSet() throws Exception;

	abstract void _initializeRulesConfiguration(IRulesEntry paramIRulesEntry);

	abstract void _initializeSoundsConfiguration(ISoundConfigurationEntry paramISoundConfigurationEntry);

	abstract Integer _updateNetworkConfiguration(INetworkConfigurationEntry paramINetworkConfigurationEntry);

	abstract boolean isBodySensorsEnabled4Config(INetworkConfigurationEntry paramINetworkConfigurationEntry);

	abstract boolean isHeadSensorsEnabled4Config(INetworkConfigurationEntry paramINetworkConfigurationEntry);

	abstract void _updateMatchConfiguration(IMatchConfigurationEntry paramIMatchConfigurationEntry);

	abstract Integer _getMaxGamJeomsAllowed();

	abstract Integer _getGoldenPointFinishPoints();

	abstract Integer getDividePenaltiesInGoldenPoint();

	abstract Integer getBlueCurrentPenalties4EventsListeners();

	abstract Integer getRedCurrentPenalties4EventsListeners();

	abstract MLID newMatchLogItemDtoInstance();

	abstract void fillExtraInfo2MatchLogItem(MLID paramMLID);

	abstract void fillExtraInfo2TkStrikeEvent(TkStrikeEventDto paramTkStrikeEventDto);

	abstract void _changeRoundAndTime(MLD paramMLD, MLID paramMLID);

	abstract void _restartMatchByMatchLog(MLD paramMLD, List<MLID> paramList);

	class HitEventValidatorTask implements Runnable {

		private SimpleObjectProperty<HitEventValidator> theProperty;

		public HitEventValidatorTask(SimpleObjectProperty<HitEventValidator> theProperty) {
			this.theProperty = theProperty;
		}

		@Override
		public void run() {
			HitEventValidator hitEventValidator1 = this.theProperty.get();
			if(BaseCommonMatchWorker.matchWorkerLogger.isDebugEnabled())
				BaseCommonMatchWorker.matchWorkerLogger.debug("Validate HitEventTask " + hitEventValidator1.getHitEventType().toString());
			if( ! hitEventValidator1.allJudgesValidated()) {
				if(BaseCommonMatchWorker.matchWorkerLogger.isDebugEnabled())
					BaseCommonMatchWorker.matchWorkerLogger.debug("All aren't validated...");
				hitEventValidator1.setJudge1HitStatus(HitJudgeStatus.NOT_VALIDATED);
				if(hitEventValidator1.getJudgesEnabled() >= 2) {
					hitEventValidator1.setJudge2HitStatus(HitJudgeStatus.NOT_VALIDATED);
				} else {
					hitEventValidator1.setJudge2HitStatus(HitJudgeStatus.NOT_ENABLED);
				}
				if(hitEventValidator1.getJudgesEnabled() >= 3) {
					hitEventValidator1.setJudge3HitStatus(HitJudgeStatus.NOT_VALIDATED);
				} else {
					hitEventValidator1.setJudge3HitStatus(HitJudgeStatus.NOT_ENABLED);
				}
				BaseCommonMatchWorker.this.fireHitEventValidator(hitEventValidator1);
			}
			if(hitEventValidator1.getHitEventType().equals(HitEventType.PUNCH)) {
				hitEventValidator1.setHitTimestamp( - 1L);
			} else if(( ! BaseCommonMatchWorker.this.judgeLetsTechHeadPoints || BaseCommonMatchWorker.this.backupSystemEnabled.get())
					&& hitEventValidator1.getHitEventType().equals(HitEventType.HEAD)) {
				hitEventValidator1.setHitTimestamp( - 1L);
			} else if((BaseCommonMatchWorker.this.judgeLetsBodyPoints || BaseCommonMatchWorker.this.backupSystemEnabled.get()) && hitEventValidator1
					.getHitEventType().equals(HitEventType.BODY)) {
				hitEventValidator1.setHitTimestamp( - 1L);
			} else if(( ! BaseCommonMatchWorker.this.judgeLetsTechHeadPoints || BaseCommonMatchWorker.this.backupSystemEnabled.get())
					&& (hitEventValidator1
							.getHitEventType().equals(HitEventType.SPECIAL_HEAD) || hitEventValidator1
									.getHitEventType().equals(HitEventType.SPECIAL_BODY) || (BaseCommonMatchWorker.this.backupSystemEnabled.get()
											&& hitEventValidator1
													.getHitEventType().equals(HitEventType.PARA_SPINNING)) || hitEventValidator1
															.getHitEventType().equals(HitEventType.PARA_TURNING))) {
				hitEventValidator1.setHitTimestamp( - 1L);
			}
			this.theProperty.set(hitEventValidator1);
		}
	}

	class CountdownListenerAndExternalEventsListenerNotification implements ChangeListener<Number> {

		private final String countdownName;

		private final MatchStatusId matchStatus4ValidateChanges;

		private Calendar calendar = Calendar.getInstance();

		public CountdownListenerAndExternalEventsListenerNotification(String countdownName, MatchStatusId matchStatus4ValidateChanges) {
			this.countdownName = countdownName;
			this.matchStatus4ValidateChanges = matchStatus4ValidateChanges;
		}

		@Override
		public void changed(ObservableValue<? extends Number> observableValue, Number prevTimeMillis, final Number newTimeMillis) {
			TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

				@Override
				public Void call() {
					if(BaseCommonMatchWorker.CountdownListenerAndExternalEventsListenerNotification.this.matchStatus4ValidateChanges.equals(
							BaseCommonMatchWorker.this.getCurrentMatchStatus())) {
						BaseCommonMatchWorker.CountdownListenerAndExternalEventsListenerNotification.this.calendar.setTimeInMillis(newTimeMillis
								.longValue());
						if(BaseCommonMatchWorker.CountdownListenerAndExternalEventsListenerNotification.this.calendar.get(14) == 999L) {
							BaseCommonMatchWorker.this.sendEventToExternalEventsListeners(
									BaseCommonMatchWorker.CountdownListenerAndExternalEventsListenerNotification.this.countdownName + "_CHANGE",
									BaseCommonMatchWorker.this.currentMatchNumber,

									Integer.valueOf(BaseCommonMatchWorker.this.currentRound.get()), BaseCommonMatchWorker.this.currentRoundStr
											.get(),
									Long.valueOf(newTimeMillis.longValue()),
									Long.valueOf(System.currentTimeMillis()), false, false,

									Integer.valueOf(BaseCommonMatchWorker.this.blueGeneralPoints.get()), BaseCommonMatchWorker.this

											.getBlueCurrentPenalties4EventsListeners(),
									Integer.valueOf(BaseCommonMatchWorker.this.redGeneralPoints.get()), BaseCommonMatchWorker.this
											.getRedCurrentPenalties4EventsListeners(), BaseCommonMatchWorker.this
													.getBlueRoundsWins(), BaseCommonMatchWorker.this
															.getRedRoundsWins(), (String)null, (String)null, (String)null, false, (Integer)null);
							if(BaseCommonMatchWorker.this.rtBroadcastSocketClient.isConnected())
								BaseCommonMatchWorker.this.sendEventToRtBroadcast(
										BaseCommonMatchWorker.CountdownListenerAndExternalEventsListenerNotification.this.countdownName + "_CHANGE",
										BaseCommonMatchWorker.this.currentMatchNumber,

										Integer.valueOf(BaseCommonMatchWorker.this.currentRound.get()), BaseCommonMatchWorker.this.currentRoundStr
												.get(),
										Boolean.valueOf(BaseCommonMatchWorker.this.goldenPointWorking),
										Long.valueOf(newTimeMillis.longValue()),
										Long.valueOf(System.currentTimeMillis()), false, false,
										Integer.valueOf(BaseCommonMatchWorker.this.blueGeneralPoints.get()),
										Integer.valueOf(BaseCommonMatchWorker.this.bluePenalties.get()),
										Integer.valueOf(0), BaseCommonMatchWorker.this
												.getBlueRoundsWins(), Integer.valueOf(BaseCommonMatchWorker.this.redGeneralPoints.get()),
										Integer.valueOf(BaseCommonMatchWorker.this.redPenalties.get()),
										Integer.valueOf(0), BaseCommonMatchWorker.this
												.getRedRoundsWins(), (Integer)null, (String)null, (String)null, (String)null, (String)null);
						}
					}
					return null;
				}
			});
		}
	}
}
