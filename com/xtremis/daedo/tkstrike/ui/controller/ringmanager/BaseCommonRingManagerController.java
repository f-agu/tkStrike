package com.xtremis.daedo.tkstrike.ui.controller.ringmanager;

import com.xtremis.daedo.tkstrike.ui.TkStrikeKeyCombinationsHelper;
import com.xtremis.daedo.tkstrike.ui.controller.CommonTkStrikeBaseController;
import com.xtremis.daedo.tkstrike.ui.model.INetworkConfigurationEntry;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

public abstract class BaseCommonRingManagerController<NE extends INetworkConfigurationEntry> extends CommonTkStrikeBaseController<NE> implements CommonRingManagerController {
  private CommonRingManagerController.RingManagerOpenType ringManagerOpenType;
  
  public final void onWindowShowEvent() {
    super.onWindowShowEvent();
    _onWindowShowEvent();
    if (CommonRingManagerController.RingManagerOpenType.REQUEST_NEXT_MATCH.equals(this.ringManagerOpenType)) {
      requestNextMatch();
    } else if (CommonRingManagerController.RingManagerOpenType.REQUEST_PREV_MATCH.equals(this.ringManagerOpenType)) {
      requestPrevMatch();
    } 
    this.ringManagerOpenType = CommonRingManagerController.RingManagerOpenType.DEFAULT;
  }
  
  abstract void _onWindowShowEvent();
  
  abstract void _afterPropertiesSet() throws Exception;
  
  public final void afterPropertiesSet() throws Exception {
    this.rootView.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
          public void handle(KeyEvent event) {
            if (TkStrikeKeyCombinationsHelper.keyCombRingManagerNextMatch.match(event)) {
              Platform.runLater(new Runnable() {
                    public void run() {
                      BaseCommonRingManagerController.this.requestNextMatch();
                    }
                  });
            } else if (TkStrikeKeyCombinationsHelper.keyCombRingManagerPrevMatch.match(event)) {
              Platform.runLater(new Runnable() {
                    public void run() {
                      BaseCommonRingManagerController.this.requestPrevMatch();
                    }
                  });
            } 
          }
        });
    _afterPropertiesSet();
  }
  
  public CommonRingManagerController.RingManagerOpenType getRingManagerOpenType() {
    return this.ringManagerOpenType;
  }
  
  public void setRingManagerOpenType(CommonRingManagerController.RingManagerOpenType ringManagerOpenType) {
    this.ringManagerOpenType = ringManagerOpenType;
  }
}
