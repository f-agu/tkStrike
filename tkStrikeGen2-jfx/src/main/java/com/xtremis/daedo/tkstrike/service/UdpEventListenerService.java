package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.UdpEventListener;
import com.xtremis.daedo.tkstrike.ui.model.UdpEventListenerEntry;

public interface UdpEventListenerService extends TkStrikeService<UdpEventListener, UdpEventListenerEntry> {
  UdpEventListenerEntry createEntry(String paramString, Integer paramInteger) throws TkStrikeServiceException;
  
  UdpEventListener create(String paramString, Integer paramInteger) throws TkStrikeServiceException;
}
