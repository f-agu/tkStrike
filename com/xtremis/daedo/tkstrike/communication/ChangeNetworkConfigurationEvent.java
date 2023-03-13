package com.xtremis.daedo.tkstrike.communication;

public class ChangeNetworkConfigurationEvent extends TkStrikeCommunicationBaseEvent {
  private static final long serialVersionUID = -1765288928875212547L;
  
  private NetworkConfigurationDto newNetworkConfigurationDto;
  
  public ChangeNetworkConfigurationEvent(Long eventTimestamp, NetworkStatus networkStatus, NetworkConfigurationDto newNetworkConfigurationDto) {
    super(eventTimestamp, networkStatus);
    this.newNetworkConfigurationDto = newNetworkConfigurationDto;
  }
  
  public NetworkConfigurationDto getNewNetworkConfigurationDto() {
    return this.newNetworkConfigurationDto;
  }
}
