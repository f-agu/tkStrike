package com.xtremis.daedo.tkstrike.communication;

import org.springframework.beans.factory.InitializingBean;

public interface CommonGlobalNetworkStatusController extends InitializingBean {
  void addListener(GlobalNetworkStatusControllerListener paramGlobalNetworkStatusControllerListener);
  
  void removeListener(GlobalNetworkStatusControllerListener paramGlobalNetworkStatusControllerListener);
  
  NetworkStatus getCurrentNetworkStatus() throws TkStrikeCommunicationException;
}
