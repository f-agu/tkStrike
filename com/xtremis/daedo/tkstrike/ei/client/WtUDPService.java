package com.xtremis.daedo.tkstrike.ei.client;

import com.xtremis.daedo.tkstrike.om.combat.MatchWinner;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchConfigurationDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchResultDto;
import java.util.Map;
import javafx.beans.property.ReadOnlyBooleanProperty;

public interface WtUDPService {
  boolean isConnected();
  
  ReadOnlyBooleanProperty connectedProperty();
  
  boolean connect(String paramString, Integer paramInteger1, Integer paramInteger2) throws TkStrikeServiceException;
  
  void closeConnection(boolean paramBoolean);
  
  void sendMatchLoaded(MatchConfigurationDto paramMatchConfigurationDto);
  
  void sendAthletes(MatchConfigurationDto paramMatchConfigurationDto);
  
  void sendReferees(MatchConfigurationDto paramMatchConfigurationDto);
  
  void sendMatchPreLoaded();
  
  void sendMatchReady();
  
  void sendRoundCountdownChange(String paramString, ClockAction paramClockAction);
  
  void sendRestCountdownChange(String paramString, ClockAction paramClockAction);
  
  void sendKyeShi(String paramString, ClockAction paramClockAction);
  
  void sendPARATimeOutCountdownChange(boolean paramBoolean, String paramString, ClockAction paramClockAction);
  
  void sendRoundNumber(Integer paramInteger);
  
  void sendPointsChange(Integer paramInteger1, Integer paramInteger2);
  
  void sendBestOf3ScoreChange(BestOf3PointsChange paramBestOf3PointsChange);
  
  void sendScoreChange(Integer paramInteger1, Integer paramInteger2);
  
  void sendPenaltiesChange(Integer paramInteger1, Integer paramInteger2);
  
  void sendHitLevel(Integer paramInteger1, Integer paramInteger2);
  
  void sendWinnerPeriods(Map<Integer, MatchWinner> paramMap);
  
  void sendMatchResult(MatchResultDto paramMatchResultDto);
  
  void sendMatchWinner(MatchWinner paramMatchWinner);
  
  void sendVideoReplay(boolean paramBoolean, VideoRequestResult paramVideoRequestResult);
  
  void sendHardwareTestOpened();
  
  void sendHardwareTestClosed();
  
  void sendHardwareTestHit(HardwareTestHit paramHardwareTestHit);
  
  public enum ClockAction {
    START, STOP, CHANGE, CORRECTION, END;
  }
  
  public enum VideoRequestResult {
    ACCEPTED, REJECTED, CLOSED;
  }
  
  public enum HardwareTestHit {
    BLUE_BODY, BLUE_HEAD, RED_BODY, RED_HEAD;
  }
}
