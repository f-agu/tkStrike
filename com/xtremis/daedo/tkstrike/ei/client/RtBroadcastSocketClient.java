package com.xtremis.daedo.tkstrike.ei.client;

import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchConfigurationDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeEventDto;
import javafx.beans.property.ReadOnlyBooleanProperty;

public interface RtBroadcastSocketClient {
  boolean isConnected();
  
  ReadOnlyBooleanProperty connectedProperty();
  
  boolean connect(String paramString1, Long paramLong, String paramString2) throws TkStrikeServiceException;
  
  void closeConnection();
  
  String getServerIp();
  
  Long getServerPort();
  
  String getRingNumber();
  
  void sendNewMatchEvent(TkStrikeEventDto paramTkStrikeEventDto, Boolean paramBoolean, Integer paramInteger1, Integer paramInteger2, Integer paramInteger3, String paramString1, String paramString2);
  
  void sendHasNewMatchConfigured(MatchConfigurationDto paramMatchConfigurationDto);
}
