package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.communication.ChangeNetworkConfigurationEvent;
import com.xtremis.daedo.tkstrike.communication.ChangeNetworkStatusEvent;
import com.xtremis.daedo.tkstrike.communication.DataEvent;
import com.xtremis.daedo.tkstrike.communication.StatusEvent;
import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationListener;
import com.xtremis.daedo.tkstrike.om.AppStatusId;
import com.xtremis.daedo.tkstrike.om.ExternalScreenViewId;
import com.xtremis.daedo.tkstrike.om.combat.MatchWinner;
import com.xtremis.daedo.tkstrike.orm.model.ScreenResolution;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;

public interface AppStatusWorker<NE extends com.xtremis.daedo.tkstrike.ui.model.INetworkConfigurationEntry, ME extends com.xtremis.daedo.tkstrike.ui.model.IMatchConfigurationEntry, RE extends com.xtremis.daedo.tkstrike.ui.model.IRulesEntry, SE extends com.xtremis.daedo.tkstrike.ui.model.ISoundConfigurationEntry> extends TkStrikeCommunicationListener {
  ReadOnlyBooleanProperty appStatusChanged();
  
  AppStatusId[] getCurrentAppStatuses();
  
  ReadOnlyObjectProperty<AppStatusId> lastAppStatusIdProperty();
  
  AppStatusId getLastAppStatusId();
  
  ReadOnlyBooleanProperty dialogWindowClose();
  
  void doDialogWindowCloses();
  
  ReadOnlyBooleanProperty tryToExitTkStrike();
  
  void doTryToExitTkStrike();
  
  ReadOnlyBooleanProperty forceExitTkStrike();
  
  void doForceExitTkStrike();
  
  ScreenResolution getExternalScreenResolution();
  
  SimpleObjectProperty<ScreenResolution> externalScreenResolutionProperty();
  
  void addAppStatusOk(AppStatusId paramAppStatusId);
  
  ReadOnlyBooleanProperty matchAllowed();
  
  boolean getMatchAllowed();
  
  ReadOnlyBooleanProperty networkConfigurationChanges();
  
  ReadOnlyBooleanProperty matchConfigurationChanges();
  
  void setMatchConfigurationChanges(Boolean paramBoolean);
  
  ReadOnlyBooleanProperty rulesChanges();
  
  ReadOnlyBooleanProperty soundConfigurationChanges();
  
  ReadOnlyStringProperty resetMatchWithMatchLogId();
  
  void doResetMatchWithMatchLogId(ME paramME, String paramString);
  
  NE getNetworkConfigurationEntry() throws TkStrikeServiceException;
  
  void setNetworkConfigurationEntry(NE paramNE);
  
  ME getMatchConfigurationEntry();
  
  void setMatchConfigurationEntry(ME paramME);
  
  RE getRulesEntry();
  
  void setRulesEntry(RE paramRE);
  
  SE getSoundConfigurationEntry();
  
  void setSoundConfigurationEntry(SE paramSE);
  
  ExternalScreenViewId getExternalScreenView();
  
  void doChangeExternalScreenView(ExternalScreenViewId paramExternalScreenViewId);
  
  ReadOnlyObjectProperty<ExternalScreenViewId> externalScreenViewProperty();
  
  void hasNewDataEvent(DataEvent paramDataEvent);
  
  void hasNewStatusEvent(StatusEvent paramStatusEvent);
  
  void hasChangeNetworkStatusEvent(ChangeNetworkStatusEvent paramChangeNetworkStatusEvent);
  
  void lockNetworkConfigurationAutosave();
  
  void unlockNetworkConfigurationAutosave();
  
  void hasChangeNetworkConfigurationEvent(ChangeNetworkConfigurationEvent paramChangeNetworkConfigurationEvent);
  
  ReadOnlyBooleanProperty matchLogCSVGeneratedProperty();
  
  void setNewMatchLogCSVGenerated();
  
  ReadOnlyBooleanProperty matchLogXLSGeneratedProperty();
  
  void setNewMatchLogXLSGenerated();
  
  ReadOnlyBooleanProperty matchLogPDFGeneratedProperty();
  
  void setNewMatchLogPDFGenerated();
  
  void informErrorWithExternalService();
  
  void informNoErrorWithExternalService();
  
  ReadOnlyBooleanProperty errorWithExternalServiceProperty();
  
  SimpleObjectProperty<MatchWinner> bestOf3SuperiorityRoundWinnerProperty();
}
