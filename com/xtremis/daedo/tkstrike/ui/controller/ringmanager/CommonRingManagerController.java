package com.xtremis.daedo.tkstrike.ui.controller.ringmanager;

import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeController;

public interface CommonRingManagerController extends TkStrikeController {
  RingManagerOpenType getRingManagerOpenType();
  
  void setRingManagerOpenType(RingManagerOpenType paramRingManagerOpenType);
  
  void requestNextMatch();
  
  void requestPrevMatch();
  
  public enum RingManagerOpenType {
    DEFAULT, REQUEST_NEXT_MATCH, REQUEST_PREV_MATCH;
  }
}
