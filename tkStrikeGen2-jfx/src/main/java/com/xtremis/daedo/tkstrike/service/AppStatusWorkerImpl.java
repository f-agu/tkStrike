package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.ui.model.IMatchConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.INetworkConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.MatchConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.NetworkConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.RulesEntry;
import com.xtremis.daedo.tkstrike.ui.model.SoundConfigurationEntry;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class AppStatusWorkerImpl extends BaseAppStatusWorker<NetworkConfigurationEntry, MatchConfigurationEntry, RulesEntry, SoundConfigurationEntry> {
  MatchConfigurationEntry newMatchConfigurationEntryInstance() {
    return new MatchConfigurationEntry();
  }
  
  NetworkConfigurationEntry newNetworkConfigurationEntryInstance() {
    return new NetworkConfigurationEntry();
  }
}
