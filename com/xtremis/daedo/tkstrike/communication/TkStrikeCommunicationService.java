package com.xtremis.daedo.tkstrike.communication;

public interface TkStrikeCommunicationService {
  void startComm() throws TkStrikeCommunicationException;
  
  void stopComm() throws TkStrikeCommunicationException;
  
  NetworkStatus getCurrentNetworkStatus() throws TkStrikeCommunicationException;
  
  void tryToRecognizeWithConfig(NetworkConfigurationDto paramNetworkConfigurationDto, boolean paramBoolean) throws TkStrikeCommunicationException;
  
  void startNetwork(NetworkConfigurationDto paramNetworkConfigurationDto) throws TkStrikeCommunicationException;
  
  NetworkConfigurationDto getCurrentNetworkConfiguration();
  
  void addListener(TkStrikeCommunicationListener paramTkStrikeCommunicationListener);
  
  void removeListener(TkStrikeCommunicationListener paramTkStrikeCommunicationListener);
}
