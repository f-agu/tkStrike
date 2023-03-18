package org.crm.tkstrike.testnetwork;

import java.io.Serializable;

public interface TkStrikeCommunicationEvent extends Serializable {
  Long getEventTimestamp();
  
  NetworkStatus getNetworkStatus();
}
