package com.xtremis.daedo.tkstrike.ui.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public interface IRoundsConfigEntry<E extends com.xtremis.daedo.tkstrike.orm.model.RoundsConfigEntity, RD extends com.xtremis.daedo.tkstrike.tools.ei.om.RoundsConfigDto> {
  void fillByEntity(E paramE);
  
  void fillByDto(RD paramRD);
  
  RD getRoundsConfigDto();
  
  int getRounds();
  
  SimpleIntegerProperty roundsProperty();
  
  String getRoundTimeMinutes();
  
  SimpleStringProperty roundTimeMinutesProperty();
  
  String getRoundTimeSeconds();
  
  SimpleStringProperty roundTimeSecondsProperty();
  
  String getKyeShiTimeMinutes();
  
  SimpleStringProperty kyeShiTimeMinutesProperty();
  
  String getKyeShiTimeSeconds();
  
  SimpleStringProperty kyeShiTimeSecondsProperty();
  
  String getRestTimeMinutes();
  
  SimpleStringProperty restTimeMinutesProperty();
  
  String getRestTimeSeconds();
  
  SimpleStringProperty restTimeSecondsProperty();
  
  boolean getGoldenPointEnabled();
  
  SimpleBooleanProperty goldenPointEnabledProperty();
  
  String getGoldenPointTimeMinutes();
  
  SimpleStringProperty goldenPointTimeMinutesProperty();
  
  String getGoldenPointTimeSeconds();
  
  SimpleStringProperty goldenPointTimeSecondsProperty();
  
  String getRoundTimeStr();
  
  String getKyeShiTimeStr();
  
  String getRestTimeStr();
  
  String getGoldenPointTimeStr();
  
  void setRounds(int paramInt);
  
  void setRoundTimeMinutes(String paramString);
  
  void setRoundTimeSeconds(String paramString);
  
  void setKyeShiTimeMinutes(String paramString);
  
  void setKyeShiTimeSeconds(String paramString);
  
  void setRestTimeMinutes(String paramString);
  
  void setRestTimeSeconds(String paramString);
  
  void setGoldenPointEnabled(boolean paramBoolean);
  
  void setGoldenPointTimeMinutes(String paramString);
  
  void setGoldenPointTimeSeconds(String paramString);
}
