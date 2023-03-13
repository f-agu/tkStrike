package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.om.ActionSource;
import com.xtremis.daedo.tkstrike.om.MatchStatusId;
import com.xtremis.daedo.tkstrike.om.NetworkErrorCause;
import com.xtremis.daedo.tkstrike.om.combat.BestOf3RoundSuperiority;
import com.xtremis.daedo.tkstrike.om.combat.FinalDecision;
import com.xtremis.daedo.tkstrike.om.combat.HitEventType;
import com.xtremis.daedo.tkstrike.om.combat.HitEventValidator;
import com.xtremis.daedo.tkstrike.om.combat.MatchWinner;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchVictoryCriteria;
import com.xtremis.daedo.tkstrike.ui.model.ScoreboardEditAction;
import java.util.Collection;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.text.Text;

public interface CommonMatchWorker {
  SimpleBooleanProperty allowNetworkErrorProperty();
  
  Boolean isLock();
  
  String getMatchLogId();
  
  SimpleBooleanProperty scoreboardEditorOpenProperty();
  
  SimpleBooleanProperty finalResultOpenProperty();
  
  SimpleBooleanProperty roundFinishedOpenProperty();
  
  FinalDecision getMatchFinalDecision();
  
  ReadOnlyObjectProperty<FinalDecision> matchFinalDecisionProperty();
  
  void confirmFinalResult(MatchWinner paramMatchWinner, FinalDecision paramFinalDecision);
  
  void undoFinalResult();
  
  MatchWinner getMatchWinner();
  
  MatchWinner getRoundWinner(Integer paramInteger);
  
  Integer getBlueRoundPoints(Integer paramInteger);
  
  Integer getRedRoundPoints(Integer paramInteger);
  
  ReadOnlyObjectProperty<MatchWinner> matchWinnerProperty();
  
  ReadOnlyBooleanProperty matchWinnerChangesProperty();
  
  ReadOnlyBooleanProperty matchWinnerByPointGapNeedsConfirmationProperty();
  
  void applyPointGapConfirmation(boolean paramBoolean);
  
  ReadOnlyBooleanProperty roundsWinnerChangesProperty();
  
  ReadOnlyBooleanProperty showGoldenPointTieBreakerOnScoreboard();
  
  void doShowGoldenPointTieBreakerOnScoreboard(boolean paramBoolean);
  
  MatchStatusId getCurrentMatchStatus();
  
  ReadOnlyObjectProperty<MatchStatusId> currentMatchStatusProperty();
  
  NetworkErrorCause getNetworkErrorCause();
  
  int getMatchRounds();
  
  ReadOnlyIntegerProperty matchRoundsProperty();
  
  boolean isGoldenPointEnabled();
  
  boolean isGoldenPointWorking();
  
  boolean showNearMissHitsOnScoreboardEditor();
  
  boolean isGoldenPointTieBreaker();
  
  boolean isGoldenPointPointByPenalty();
  
  int getCurrentRound();
  
  ReadOnlyIntegerProperty currentRoundProperty();
  
  String getCurrentRoundStr();
  
  long getCurrentRoundCountdownAsMillis();
  
  String getCurrentRoundCountdownString();
  
  ReadOnlyStringProperty currentRoundStrProperty();
  
  Integer getMaxGamJeomsAllowed();
  
  boolean isParaTkdMatch();
  
  MatchVictoryCriteria getMatchVictoryCriteria();
  
  ReadOnlyObjectProperty<MatchWinner> bestOf3CurrentRoundPartialWinner();
  
  BooleanProperty bestOf3RoundWithPointGap();
  
  Boolean isBestOf3WinnerLastRoundByPUN();
  
  MatchWinner getBestOf3RoundWinnerWithPointGap();
  
  BooleanProperty bestOf3RoundWithSuperiority();
  
  BooleanProperty bestOf3RoundSuperiorityOnScoreboard();
  
  void bestOf3HideRoundSuperiorityOnScoreboard();
  
  void bestOf3ShowRoundSuperiorityOnScoreboard();
  
  BestOf3RoundSuperiority getCurrentBestOf3RoundSuperiority();
  
  void enableOnBestOf3ChangesFromScoreboardEditor();
  
  void disableOnBestOf3ChangesFromScoreboardEditor();
  
  BooleanProperty superiorityByRoundsProperty();
  
  BooleanProperty showSuperiorityByRoundsProperty();
  
  void hideSuperiorityByRoundsProperty();
  
  MatchWinner getBestOf3WinnerLastRoundWithSuperiority();
  
  Integer getBlueMatchPoints();
  
  SimpleIntegerProperty blueLastImpactValueProperty();
  
  int getBlueGeneralPoints();
  
  ReadOnlyIntegerProperty blueGeneralPointsProperty();
  
  int getBluePenalties();
  
  ReadOnlyIntegerProperty bluePenaltiesProperty();
  
  int getBlueGoldenPointImpacts();
  
  ReadOnlyIntegerProperty blueGoldenPointImpactsProperty();
  
  int getBlueGoldenPointPenalties();
  
  ReadOnlyIntegerProperty blueGoldenPointPenaltiesProperty();
  
  void removeBlueNearMissHit(ActionSource paramActionSource, long paramLong1, long paramLong2);
  
  void addBlueNearMissHit(ActionSource paramActionSource, long paramLong1, long paramLong2);
  
  int getBlueTotalPenalties();
  
  ReadOnlyIntegerProperty blueGoldenPointPunchesProperty();
  
  ReadOnlyIntegerProperty bluePARATechPointsProperty();
  
  boolean isBlueLastPointIsPenalty();
  
  Integer getBlueRoundsWins();
  
  ReadOnlyIntegerProperty blueRoundsWinProperty();
  
  ReadOnlyIntegerProperty blueR1PointsProperty();
  
  ReadOnlyIntegerProperty blueR2PointsProperty();
  
  ReadOnlyIntegerProperty blueR3PointsProperty();
  
  Integer getBlueTechPoints();
  
  ReadOnlyIntegerProperty redRoundsWinProperty();
  
  ReadOnlyIntegerProperty redR1PointsProperty();
  
  ReadOnlyIntegerProperty redR2PointsProperty();
  
  ReadOnlyIntegerProperty redR3PointsProperty();
  
  int getBlueNearMissHits();
  
  ReadOnlyIntegerProperty blueNearMissHitsProperty();
  
  int getRedNearMissHits();
  
  ReadOnlyIntegerProperty redNearMissHitsProperty();
  
  ReadOnlyBooleanProperty bluePARATimeOutQuotaProperty();
  
  ReadOnlyIntegerProperty bluePARATimeOutQuotaValueProperty();
  
  void doPARATimeOutByBlue();
  
  void doResetPARATimeOutForBlue();
  
  Integer getRedMatchPoints();
  
  SimpleIntegerProperty redLastImpactValueProperty();
  
  int getRedGeneralPoints();
  
  ReadOnlyIntegerProperty redGeneralPointsProperty();
  
  int getRedPenalties();
  
  ReadOnlyIntegerProperty redPenaltiesProperty();
  
  int getRedGoldenPointImpacts();
  
  ReadOnlyIntegerProperty redGoldenPointImpactsProperty();
  
  int getRedGoldenPointPenalties();
  
  ReadOnlyIntegerProperty redGoldenPointPenaltiesProperty();
  
  void removeRedNearMissHit(ActionSource paramActionSource, long paramLong1, long paramLong2);
  
  void addRedNearMissHit(ActionSource paramActionSource, long paramLong1, long paramLong2);
  
  int getRedTotalPenalties();
  
  ReadOnlyIntegerProperty redGoldenPointPunchesProperty();
  
  ReadOnlyIntegerProperty redPARATechPointsProperty();
  
  boolean isRedLastPointIsPenalty();
  
  Integer getRedRoundsWins();
  
  Integer getRedTechPoints();
  
  ReadOnlyBooleanProperty redPARATimeOutQuotaProperty();
  
  ReadOnlyIntegerProperty redPARATimeOutQuotaValueProperty();
  
  void doPARATimeOutByRed();
  
  void doResetPARATimeOutForRed();
  
  void setTextCountdown(Text paramText);
  
  Integer getRoundCountdownMinutes();
  
  Integer getRoundCountdownSeconds();
  
  Integer getRoundCountdownCentiseconds();
  
  ReadOnlyStringProperty roundCountdownCurrentTimeAsStringProperty();
  
  long getRoundCountdownCurrentTimeMillis();
  
  ReadOnlyBooleanProperty roundCountdownFinishedProperty();
  
  ReadOnlyStringProperty kyeShiCountdownCurrentTimeAsStringProperty();
  
  ReadOnlyBooleanProperty restTimeCountdownFinishedProperty();
  
  ReadOnlyStringProperty restTimeCountdownCurrentTimeAsStringProperty();
  
  ReadOnlyBooleanProperty paraTimeOutCountdownFinishedProperty();
  
  ReadOnlyStringProperty paraTimeOutCountdownCurrentTimeAsStringProperty();
  
  void cancelParaTimeOutCountdown();
  
  void changeCurrentRoundAndTime(Integer paramInteger1, Integer paramInteger2, Integer paramInteger3, Integer paramInteger4);
  
  void startRound();
  
  void endRound();
  
  void confirmRoundEnds();
  
  void confirmRoundEndsWithWinner(MatchWinner paramMatchWinner);
  
  void goNextRound();
  
  void doPauseRound();
  
  void doResumeRound();
  
  void doKyeShiInRound();
  
  void doEndKyeShiIndRound();
  
  void callDoctorInRound();
  
  void doctorQuitInRound();
  
  ReadOnlyBooleanProperty doctorInRoundProperty();
  
  boolean isDoctorInRound();
  
  void cancelGoldenPointHit();
  
  void addBlueHeadPoint(int paramInt1, ActionSource paramActionSource, long paramLong1, long paramLong2, int paramInt2);
  
  void addBlueHeadTechPoint(int paramInt, ActionSource paramActionSource, long paramLong1, long paramLong2);
  
  void addBlueBodyPoint(int paramInt1, ActionSource paramActionSource, long paramLong1, long paramLong2, int paramInt2);
  
  void addBlueBodyTechPoint(int paramInt, ActionSource paramActionSource, long paramLong1, long paramLong2);
  
  void addBluePunchPoint(int paramInt, ActionSource paramActionSource, long paramLong1, long paramLong2);
  
  void addBlueGamJeom(ActionSource paramActionSource, long paramLong1, long paramLong2);
  
  void addBlueGamJeomToNextRound(ActionSource paramActionSource, long paramLong1, long paramLong2);
  
  void removeBlueGamJeom(ActionSource paramActionSource, long paramLong1, long paramLong2);
  
  void addRedHeadPoint(int paramInt1, ActionSource paramActionSource, long paramLong1, long paramLong2, int paramInt2);
  
  void addRedHeadTechPoint(int paramInt, ActionSource paramActionSource, long paramLong1, long paramLong2);
  
  void addRedBodyPoint(int paramInt1, ActionSource paramActionSource, long paramLong1, long paramLong2, int paramInt2);
  
  void addRedBodyTechPoint(int paramInt, ActionSource paramActionSource, long paramLong1, long paramLong2);
  
  void addRedPunchPoint(int paramInt, ActionSource paramActionSource, long paramLong1, long paramLong2);
  
  void addRedGamJeom(ActionSource paramActionSource, long paramLong1, long paramLong2);
  
  void addRedGamJeomToNextRound(ActionSource paramActionSource, long paramLong1, long paramLong2);
  
  void removeRedGamJeom(ActionSource paramActionSource, long paramLong1, long paramLong2);
  
  void addListener(HitEventValidatorListener paramHitEventValidatorListener);
  
  BooleanProperty backupSystemEnabled();
  
  boolean isBackupSystemEnabled();
  
  int getDifferentialScore();
  
  void disableDifferentialScore();
  
  void cancelVictoryByPointGap();
  
  ReadOnlyBooleanProperty togglesColorGroupSelectionVisible();
  
  void doChangeTogglesColorGroupSelectionVisible();
  
  ReadOnlyIntegerProperty blueGroupSelectedProperty();
  
  void doChangeBlueGroupSelected(Integer paramInteger);
  
  ReadOnlyIntegerProperty redGroupSelectedProperty();
  
  void doChangeRedGroupSelected(Integer paramInteger);
  
  boolean isBlueBodyNodeId(String paramString);
  
  boolean isBlueHeadNodeId(String paramString);
  
  boolean isRedBodyNodeId(String paramString);
  
  boolean isRedHeadNodeId(String paramString);
  
  boolean simulateIfApplyingChangesMatchHasNextRound(Collection<ScoreboardEditAction> paramCollection);
  
  void enableChangeMatchStatusToTimeoutOnScoreboardChanges();
  
  void disableChangeMatchStatusToTimeoutOnScoreboardChanges();
  
  boolean isChangeMatchStatusToTimeoutOnScoreboardChanges();
  
  public static interface HitEventValidatorListener {
    void tryToRemoveHitEvent(boolean param1Boolean, HitEventType param1HitEventType);
    
    void tryToChangeHitTechEvent(boolean param1Boolean1, HitEventType param1HitEventType, boolean param1Boolean2);
    
    void removeGoldenPointNearMissHit(boolean param1Boolean);
    
    void addGoldenPointNearMissHit(boolean param1Boolean);
    
    void forceAddNewHitEvent(HitEventValidator param1HitEventValidator);
    
    void hasHitEventValidatorChange(HitEventValidator param1HitEventValidator);
  }
}
