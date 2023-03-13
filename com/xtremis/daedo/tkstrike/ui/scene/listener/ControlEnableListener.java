package com.xtremis.daedo.tkstrike.ui.scene.listener;

import com.xtremis.daedo.tkstrike.om.MatchStatusId;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import org.apache.commons.lang3.ArrayUtils;

public class ControlEnableListener implements ChangeListener<MatchStatusId> {
  private final Node node;
  
  private final MatchStatusId[] enabledStatuses;
  
  public ControlEnableListener(Node node, MatchStatusId[] enabledStatuses) {
    this.node = node;
    this.enabledStatuses = enabledStatuses;
  }
  
  public void changed(ObservableValue<? extends MatchStatusId> observableValue, MatchStatusId matchStatusId, final MatchStatusId newMatchStatusId) {
    Platform.runLater(new Runnable() {
          public void run() {
            if (ArrayUtils.contains((Object[])ControlEnableListener.this.enabledStatuses, newMatchStatusId)) {
              ControlEnableListener.this.node.setDisable(false);
            } else {
              ControlEnableListener.this.node.setDisable(true);
            } 
          }
        });
  }
}
