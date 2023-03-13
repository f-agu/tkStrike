package com.xtremis.daedo.tkstrike.ui.model;

public class ScoreboardEditAction {
  private final boolean blue;
  
  private final ScoreboardAction action;
  
  private final int value;
  
  public ScoreboardEditAction(boolean blue, ScoreboardAction action, int value) {
    this.blue = blue;
    this.action = action;
    this.value = value;
  }
  
  public boolean isBlue() {
    return this.blue;
  }
  
  public ScoreboardAction getAction() {
    return this.action;
  }
  
  public int getValue() {
    return this.value;
  }
}
