package com.xtremis.daedo.tkstrike.ui.controller.externalscreen;

import com.xtremis.daedo.tkstrike.service.MatchWorker;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.scene.text.Text;

public class ExternalScoreboardHDController extends ExternalScoreboardBaseController {
  private boolean blueOnLeft = true;
  
  protected double getFlagImageHeight() {
    return 63.75D;
  }
  
  protected double getVideoImageHeight() {
    return 46.5D;
  }
  
  protected Double getImpactImageHeight() {
    return Double.valueOf(57.5D);
  }
  
  protected int getHitsControlDefaultFontSize() {
    return 15;
  }
  
  protected double getHitsControlHeight() {
    return 25.0D;
  }
  
  protected double getHitsControlSpacing() {
    return 5.0D;
  }
  
  protected double getHitsControlJudgesWidth() {
    return 25.0D;
  }
  
  protected double getHitsControlHitWidth() {
    return 25.0D;
  }
  
  protected boolean isBlueOnLeft() {
    return this.blueOnLeft;
  }
  
  protected void setBlueOnLeft(boolean blueOnLeft) {
    this.blueOnLeft = blueOnLeft;
  }
  
  protected void _internalOnWindowShowEvent() {
    Platform.runLater(new Runnable() {
          public void run() {
            ExternalScoreboardHDController.this.txtBodyLevel.setText("" + ExternalScoreboardHDController.this.matchWorker.minBodyLevelProperty().intValue());
            ExternalScoreboardHDController.this.txtHeadLevel.setText("" + ExternalScoreboardHDController.this.matchWorker.minHeadLevelProperty().intValue());
          }
        });
  }
  
  protected void _internalBindUIControls() {}
  
  protected void _internalUnbindUIControls() {}
  
  protected void _internalInitialize(URL url, ResourceBundle resourceBundle) {}
  
  protected boolean showNoVideoImage() {
    return false;
  }
}
