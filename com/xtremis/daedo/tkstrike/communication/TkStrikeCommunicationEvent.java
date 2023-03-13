package com.xtremis.daedo.tkstrike.communication;

import java.io.Serializable;

public interface TkStrikeCommunicationEvent extends Serializable {
  Long getEventTimestamp();
  
  NetworkStatus getNetworkStatus();
}
