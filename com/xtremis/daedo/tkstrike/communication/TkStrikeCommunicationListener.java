package com.xtremis.daedo.tkstrike.communication;

public interface TkStrikeCommunicationListener {
  void hasNewDataEvent(DataEvent paramDataEvent);
  
  void hasNewStatusEvent(StatusEvent paramStatusEvent);
  
  void hasChangeNetworkStatusEvent(ChangeNetworkStatusEvent paramChangeNetworkStatusEvent);
  
  void hasChangeNetworkConfigurationEvent(ChangeNetworkConfigurationEvent paramChangeNetworkConfigurationEvent);
}
