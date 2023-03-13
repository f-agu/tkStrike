package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationService;
import com.xtremis.daedo.tkstrike.ui.model.INetworkConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.NetworkConfigurationEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class JudgesHardwareTestWorkerImpl extends BaseJudgesHardwareTestWorkerImpl<NetworkConfigurationEntry> {
  private final NetworkConfigurationService networkConfigurationService;
  
  private NetworkConfigurationEntry networkConfigurationEntry = new NetworkConfigurationEntry();
  
  @Autowired
  public JudgesHardwareTestWorkerImpl(TkStrikeCommunicationService tkStrikeCommunicationService, NetworkConfigurationService networkConfigurationService) {
    super(tkStrikeCommunicationService);
    this.networkConfigurationService = networkConfigurationService;
  }
  
  NetworkConfigurationService getNetworkConfigurationService() {
    return this.networkConfigurationService;
  }
  
  NetworkConfigurationEntry getCurrentNetworkConfiguration() {
    return this.networkConfigurationEntry;
  }
  
  void setCurrentNetworkConfiguration(NetworkConfigurationEntry networkConfiguration) {
    this.networkConfigurationEntry = this.networkConfigurationEntry;
  }
}
