package com.xtremis.daedo.tkstrike.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.xtremis.daedo.tkstrike.communication.ChangeNetworkConfigurationEvent;
import com.xtremis.daedo.tkstrike.communication.ChangeNetworkStatusEvent;
import com.xtremis.daedo.tkstrike.communication.DataEvent;
import com.xtremis.daedo.tkstrike.communication.StatusEvent;
import com.xtremis.daedo.tkstrike.ei.client.BestOf3PointsChange;
import com.xtremis.daedo.tkstrike.ei.client.WtUDPService;
import com.xtremis.daedo.tkstrike.om.AppStatusId;
import com.xtremis.daedo.tkstrike.om.ExternalScreenViewId;
import com.xtremis.daedo.tkstrike.om.combat.MatchWinner;
import com.xtremis.daedo.tkstrike.orm.model.ScreenResolution;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchConfigurationDto;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.model.ExternalConfigEntry;
import com.xtremis.daedo.tkstrike.ui.model.IMatchConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.INetworkConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.IRulesEntry;
import com.xtremis.daedo.tkstrike.ui.model.ISoundConfigurationEntry;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;


public abstract class BaseAppStatusWorker<NE extends INetworkConfigurationEntry, ME extends IMatchConfigurationEntry, RE extends IRulesEntry, SE extends ISoundConfigurationEntry>
		implements AppStatusWorker<NE, ME, RE, SE>, InitializingBean {

	protected static final Logger logger = Logger.getLogger(BaseAppStatusWorker.class);

	private static final Collection<AppStatusId> req4MatchAllowed = Arrays.asList(new AppStatusId[] {AppStatusId.NETWORK_CONFIGURED,
			AppStatusId.MATCH_CONFIGURED});

	private SimpleObjectProperty<AppStatusId> lastAppStatusId = new SimpleObjectProperty(this, "lastAppStatusId", AppStatusId.NETWORK_NOT_CONFIGURED);

	private SimpleBooleanProperty appStatusChanged = new SimpleBooleanProperty(this, "appStatusChanged", Boolean.FALSE.booleanValue());

	private ArrayList<AppStatusId> appStatuses = new ArrayList<>();

	private SimpleBooleanProperty matchAllowed = new SimpleBooleanProperty(this, "matchAllowed", false);

	private SimpleBooleanProperty networkConfigurationChanges = new SimpleBooleanProperty(this, "networkConfigurationChanges", false);

	private SimpleBooleanProperty matchConfigurationChanges = new SimpleBooleanProperty(this, "matchConfigurationChanges", false);

	private SimpleBooleanProperty rulesChanges = new SimpleBooleanProperty(this, "rulesChanges", false);

	private SimpleBooleanProperty soundConfigurationChanges = new SimpleBooleanProperty(this, "soundConfigurationChanges", false);

	private SimpleObjectProperty<ExternalScreenViewId> externalScreenView = new SimpleObjectProperty(this, "externalScreenView",
			ExternalScreenViewId.SCOREBOARD);

	private SimpleObjectProperty<ScreenResolution> externalScreenResolution = new SimpleObjectProperty(this, "externalScreenResolution",
			ScreenResolution.HD);

	private SimpleBooleanProperty matchLogCSVGenerated = new SimpleBooleanProperty(this, "matchLogCSVGenerated", false);

	private SimpleBooleanProperty matchLogXLSGenerated = new SimpleBooleanProperty(this, "matchLogXLSGenerated", false);

	private SimpleBooleanProperty matchLogPDFGenerated = new SimpleBooleanProperty(this, "matchLogPDFGenerated", false);

	private IMatchConfigurationEntry matchConfigurationEntry = null;

	private IRulesEntry rulesEntry = null;

	private ISoundConfigurationEntry soundConfigurationEntry = null;

	private SimpleBooleanProperty dialogWindowClose = new SimpleBooleanProperty(this, "dialogWindowClose", false);

	private SimpleBooleanProperty tryToExitTkStrike = new SimpleBooleanProperty(this, "tryToExitTkStrike", false);

	private SimpleBooleanProperty forceExitTkStrike = new SimpleBooleanProperty(this, "forceExitTkStrike", false);

	private Boolean networkConfigurationAutosave = Boolean.TRUE;

	private SimpleBooleanProperty dialogWindowOpen = new SimpleBooleanProperty(this, "dialogWindowOpen", false);

	private SimpleStringProperty resetMatchWithMatchLogId = new SimpleStringProperty(this, "resetMatchWithMatchLogId", null);

	private SimpleBooleanProperty errorWithExternalService = new SimpleBooleanProperty(this, "errorWithExternalService", false);

	private SimpleObjectProperty<MatchWinner> bestOf3SuperiorityRoundWinner = new SimpleObjectProperty(this, "bestOf3SuperiorityRoundWinner",
			MatchWinner.TIE);

	@Autowired
	private NetworkConfigurationService networkConfigurationService;

	@Autowired
	private RulesService rulesService;

	@Autowired
	private SoundConfigurationService soundConfigurationService;

	@Autowired
	private ExternalConfigService externalConfigService;

	@Autowired
	private WtUDPService wtUDPService;

	@Override
	public ReadOnlyBooleanProperty appStatusChanged() {
		return this.appStatusChanged;
	}

	@Override
	public AppStatusId[] getCurrentAppStatuses() {
		return this.appStatuses.<AppStatusId>toArray(new AppStatusId[0]);
	}

	@Override
	public AppStatusId getLastAppStatusId() {
		return this.lastAppStatusId.get();
	}

	@Override
	public ReadOnlyObjectProperty<AppStatusId> lastAppStatusIdProperty() {
		return this.lastAppStatusId;
	}

	@Override
	public synchronized void addAppStatusOk(AppStatusId appStatusId) {
		if(logger.isDebugEnabled())
			logger.debug("AddAppStatusOk --> " + appStatusId.toString());
		if(AppStatusId.NETWORK_ERROR.equals(appStatusId))
			this.matchAllowed.set(false);
		AppStatusId exists = this.appStatuses.stream().filter(s -> appStatusId.equals(s)).findAny().orElse(null);
		if(exists == null)
			this.appStatuses.add(appStatusId);
		if((AppStatusId.NETWORK_CONFIGURED.equals(appStatusId) || AppStatusId.NETWORK_RECOVERED.equals(appStatusId)) && this.appStatuses
				.contains(AppStatusId.NETWORK_ERROR)) {
			if(logger.isDebugEnabled())
				logger.debug("Go to remove NETWORK_ERROR");
			this.appStatuses.remove(AppStatusId.NETWORK_ERROR);
		}
		this.lastAppStatusId.set(appStatusId);
		if(logger.isDebugEnabled())
			logger.debug("AddAppStatusOk --> Contains NETWORK_ERROR? " + this.appStatuses.contains(AppStatusId.NETWORK_ERROR));
		if(logger.isDebugEnabled())
			logger.debug("AddAppStatusOk --> Contains ALL REQ? " + this.appStatuses.containsAll(req4MatchAllowed));
		if( ! this.appStatuses.contains(AppStatusId.NETWORK_ERROR) && this.appStatuses
				.containsAll(req4MatchAllowed)) {
			if(logger.isDebugEnabled())
				logger.debug("AddAppStatusOk --> GO CHANGE MATCH ALLOWED TO TRUE!");
			this.matchAllowed.set(false);
			this.matchAllowed.set(true);
			if( ! this.appStatuses.contains(AppStatusId.READY_FOR_MATCH))
				this.appStatuses.add(AppStatusId.READY_FOR_MATCH);
		}
		this.appStatusChanged.set(true);
		this.appStatusChanged.set(false);
	}

	@Override
	public ReadOnlyBooleanProperty matchAllowed() {
		return this.matchAllowed;
	}

	@Override
	public boolean getMatchAllowed() {
		return this.matchAllowed.get();
	}

	@Override
	public ReadOnlyBooleanProperty networkConfigurationChanges() {
		return this.networkConfigurationChanges;
	}

	@Override
	public ReadOnlyBooleanProperty matchConfigurationChanges() {
		return this.matchConfigurationChanges;
	}

	@Override
	public void setMatchConfigurationChanges(Boolean newValue) {
		this.matchConfigurationChanges.set(newValue.booleanValue());
	}

	@Override
	public ReadOnlyBooleanProperty rulesChanges() {
		return this.rulesChanges;
	}

	@Override
	public ReadOnlyBooleanProperty soundConfigurationChanges() {
		return this.soundConfigurationChanges;
	}

	@Override
	public NE getNetworkConfigurationEntry() throws TkStrikeServiceException {
		return (NE)this.networkConfigurationService.getNetworkConfigurationEntry();
	}

	@Override
	public void setNetworkConfigurationEntry(NE networkConfigurationEntry) {
		this.networkConfigurationChanges.set(true);
		this.networkConfigurationChanges.set(false);
		addAppStatusOk(AppStatusId.NETWORK_CONFIGURED);
	}

	@Override
	public ME getMatchConfigurationEntry() {
		if(this.matchConfigurationEntry == null)
			this.matchConfigurationEntry = newMatchConfigurationEntryInstance();
		return (ME)this.matchConfigurationEntry;
	}

	@Override
	public void setMatchConfigurationEntry(ME matchConfigurationEntry) {
		if(matchConfigurationEntry != null) {
			if(getRulesEntry().getForceMaxGamJomAllowed().booleanValue())
				matchConfigurationEntry.setMaxAllowedGamJeoms(getRulesEntry().getMaxGamJomAllowed().intValue());
			if(matchConfigurationEntry.isReadyForStart()) {
				this.matchConfigurationEntry = matchConfigurationEntry;
				this.matchConfigurationChanges.set(true);
				this.matchConfigurationChanges.set(false);
				if(this.appStatuses.contains(AppStatusId.NETWORK_ERROR))
					this.appStatuses.remove(AppStatusId.NETWORK_ERROR);
				addAppStatusOk(AppStatusId.MATCH_CONFIGURED);
				if(this.wtUDPService.isConnected()) {
					final MatchConfigurationDto matchConfigurationDto = matchConfigurationEntry.getMatchConfigurationDto();
					TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

						@Override
						public Void call() throws Exception {
							BaseAppStatusWorker.this.wtUDPService.sendMatchPreLoaded();
							BaseAppStatusWorker.this.wtUDPService.sendRoundNumber(Integer.valueOf(1));
							BaseAppStatusWorker.this.wtUDPService.sendRoundCountdownChange(matchConfigurationDto.getRoundsConfig().getRoundTimeStr(),
									null);
							BaseAppStatusWorker.this.wtUDPService.sendScoreChange(Integer.valueOf(0), Integer.valueOf(0));
							BaseAppStatusWorker.this.wtUDPService.sendBestOf3ScoreChange(new BestOf3PointsChange());
							BaseAppStatusWorker.this.wtUDPService.sendPenaltiesChange(Integer.valueOf(0), Integer.valueOf(0));
							BaseAppStatusWorker.this.wtUDPService.sendMatchLoaded(matchConfigurationDto);
							BaseAppStatusWorker.this.wtUDPService.sendAthletes(matchConfigurationDto);
							BaseAppStatusWorker.this.wtUDPService.sendReferees(matchConfigurationDto);
							BaseAppStatusWorker.this.wtUDPService.sendMatchReady();
							return null;
						}
					});
				}
			}
		}
	}

	@Override
	public RE getRulesEntry() {
		return (RE)this.rulesEntry;
	}

	@Override
	public void setRulesEntry(RE rulesEntry) {
		this.rulesEntry = rulesEntry;
		this.rulesChanges.set(true);
		this.rulesChanges.set(false);
	}

	@Override
	public SE getSoundConfigurationEntry() {
		return (SE)this.soundConfigurationEntry;
	}

	@Override
	public void setSoundConfigurationEntry(SE soundConfigurationEntry) {
		this.soundConfigurationEntry = soundConfigurationEntry;
		this.soundConfigurationChanges.set(true);
		this.soundConfigurationChanges.set(false);
	}

	@Override
	public ExternalScreenViewId getExternalScreenView() {
		return this.externalScreenView.get();
	}

	@Override
	public void doChangeExternalScreenView(ExternalScreenViewId newExternalScreenView) {
		this.externalScreenView.set(newExternalScreenView);
	}

	@Override
	public ReadOnlyObjectProperty<ExternalScreenViewId> externalScreenViewProperty() {
		return this.externalScreenView;
	}

	@Override
	public ReadOnlyBooleanProperty dialogWindowClose() {
		return this.dialogWindowClose;
	}

	@Override
	public synchronized void doDialogWindowCloses() {
		this.dialogWindowClose.set(true);
		this.dialogWindowClose.set(false);
	}

	@Override
	public ReadOnlyBooleanProperty tryToExitTkStrike() {
		return this.tryToExitTkStrike;
	}

	@Override
	public void doTryToExitTkStrike() {
		this.tryToExitTkStrike.set(true);
		this.tryToExitTkStrike.set(false);
	}

	@Override
	public ReadOnlyBooleanProperty forceExitTkStrike() {
		return this.forceExitTkStrike;
	}

	@Override
	public void doForceExitTkStrike() {
		this.forceExitTkStrike.set(true);
	}

	@Override
	public ScreenResolution getExternalScreenResolution() {
		return this.externalScreenResolution.get();
	}

	@Override
	public SimpleObjectProperty<ScreenResolution> externalScreenResolutionProperty() {
		return this.externalScreenResolution;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.rulesEntry = this.rulesService.getRulesEntry();
		this.soundConfigurationEntry = this.soundConfigurationService.getSoundConfigurationEntry();
		ExternalConfigEntry externalConfigEntry = this.externalConfigService.getExternalConfigEntry();
		if(externalConfigEntry != null)
			this.externalScreenResolution.set(ScreenResolution.valueOf(externalConfigEntry.getExtScoreboardResolution()));
	}

	@Override
	public void hasNewDataEvent(DataEvent dataEvent) {}

	@Override
	public void hasNewStatusEvent(StatusEvent statusEvent) {}

	@Override
	public void hasChangeNetworkStatusEvent(ChangeNetworkStatusEvent changeNetworkStatusEvent) {}

	@Override
	public void lockNetworkConfigurationAutosave() {
		this.networkConfigurationAutosave = Boolean.FALSE;
	}

	@Override
	public void unlockNetworkConfigurationAutosave() {
		this.networkConfigurationAutosave = Boolean.TRUE;
	}

	@Override
	public void hasChangeNetworkConfigurationEvent(ChangeNetworkConfigurationEvent changeNetworkConfigurationEvent) {
		logger.debug("Has Change NetworkConfiguration " + changeNetworkConfigurationEvent);
		if(this.networkConfigurationAutosave.booleanValue()) {
			NE networkConfigurationEntry = newNetworkConfigurationEntryInstance();
			networkConfigurationEntry.fillByDto(changeNetworkConfigurationEvent.getNewNetworkConfigurationDto());
			setNetworkConfigurationEntry(networkConfigurationEntry);
			try {
				this.networkConfigurationService.update(networkConfigurationEntry.getNetworkConfiguration());
			} catch(TkStrikeServiceException e) {
				e.printStackTrace();
			}
		}
		this.networkConfigurationChanges.set(true);
	}

	@Override
	public ReadOnlyBooleanProperty matchLogCSVGeneratedProperty() {
		return this.matchLogCSVGenerated;
	}

	@Override
	public void setNewMatchLogCSVGenerated() {
		this.matchLogCSVGenerated.set(true);
		this.matchLogCSVGenerated.set(false);
	}

	@Override
	public ReadOnlyBooleanProperty matchLogXLSGeneratedProperty() {
		return this.matchLogXLSGenerated;
	}

	@Override
	public void setNewMatchLogXLSGenerated() {
		this.matchLogXLSGenerated.set(true);
		this.matchLogXLSGenerated.set(false);
	}

	@Override
	public ReadOnlyBooleanProperty matchLogPDFGeneratedProperty() {
		return this.matchLogPDFGenerated;
	}

	@Override
	public void setNewMatchLogPDFGenerated() {
		this.matchLogPDFGenerated.set(true);
		this.matchLogPDFGenerated.set(false);
	}

	@Override
	public ReadOnlyStringProperty resetMatchWithMatchLogId() {
		return this.resetMatchWithMatchLogId;
	}

	@Override
	public void doResetMatchWithMatchLogId(ME matchConfigurationEntry, String matchLogId) {
		if(matchConfigurationEntry != null && StringUtils.isNotBlank(matchLogId)) {
			this.matchConfigurationEntry = matchConfigurationEntry;
			addAppStatusOk(AppStatusId.MATCH_CONFIGURED);
			this.resetMatchWithMatchLogId.set(matchLogId);
			this.resetMatchWithMatchLogId.set(null);
		}
	}

	@Override
	public void informErrorWithExternalService() {
		this.errorWithExternalService.set(true);
	}

	@Override
	public void informNoErrorWithExternalService() {
		this.errorWithExternalService.set(false);
	}

	@Override
	public ReadOnlyBooleanProperty errorWithExternalServiceProperty() {
		return this.errorWithExternalService;
	}

	@Override
	public SimpleObjectProperty<MatchWinner> bestOf3SuperiorityRoundWinnerProperty() {
		return this.bestOf3SuperiorityRoundWinner;
	}

	abstract ME newMatchConfigurationEntryInstance();

	abstract NE newNetworkConfigurationEntryInstance();
}
