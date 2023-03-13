package com.xtremis.daedo.tkstrike.ui.model;

import com.xtremis.daedo.tkstrike.orm.model.RoundsConfig;
import com.xtremis.daedo.tkstrike.orm.model.RoundsConfigEntity;
import com.xtremis.daedo.tkstrike.tools.ei.om.RoundsConfigDto;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class RoundsConfigEntry implements IRoundsConfigEntry<RoundsConfig, RoundsConfigDto> {
  private SimpleIntegerProperty rounds = new SimpleIntegerProperty(this, "rounds", 3);
  
  private SimpleStringProperty roundTimeMinutes = new SimpleStringProperty(this, "roundTimeMinutes", "2");
  
  private SimpleStringProperty roundTimeSeconds = new SimpleStringProperty(this, "roundTimeSeconds", "00");
  
  private SimpleStringProperty kyeShiTimeMinutes = new SimpleStringProperty(this, "kyeShiTimeMinutes", "1");
  
  private SimpleStringProperty kyeShiTimeSeconds = new SimpleStringProperty(this, "kyeShiTimeSeconds", "00");
  
  private SimpleStringProperty restTimeMinutes = new SimpleStringProperty(this, "restTimeMinutes", "1");
  
  private SimpleStringProperty restTimeSeconds = new SimpleStringProperty(this, "restTimeSeconds", "00");
  
  private SimpleBooleanProperty goldenPointEnabled = new SimpleBooleanProperty(this, "goldenPointEnabled", true);
  
  private SimpleStringProperty goldenPointTimeMinutes = new SimpleStringProperty(this, "goldenPointTimeMinutes", "2");
  
  private SimpleStringProperty goldenPointTimeSeconds = new SimpleStringProperty(this, "goldenPointTimeSeconds", "00");
  
  public void fillByEntity(RoundsConfig entity) {
    if (entity != null) {
      if (entity.getRounds() != null)
        this.rounds.set(entity.getRounds().intValue()); 
      SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
      DecimalFormat df = new DecimalFormat("00");
      try {
        if (entity.getRoundTimeStr() != null) {
          Calendar calRound = Calendar.getInstance();
          calRound.setTime(sdf.parse(entity.getRoundTimeStr()));
          this.roundTimeMinutes.set("" + calRound.get(12));
          this.roundTimeSeconds.set(df.format(calRound.get(13)));
        } 
        if (entity.getKyeShiTimeStr() != null) {
          Calendar calKyeShi = Calendar.getInstance();
          calKyeShi.setTime(sdf.parse(entity.getKyeShiTimeStr()));
          this.kyeShiTimeMinutes.set(df.format(calKyeShi.get(12)));
          this.kyeShiTimeSeconds.set(df.format(calKyeShi.get(13)));
        } 
        if (entity.getRestTimeStr() != null) {
          Calendar calRest = Calendar.getInstance();
          calRest.setTime(sdf.parse(entity.getRestTimeStr()));
          this.restTimeMinutes.set(df.format(calRest.get(12)));
          this.restTimeSeconds.set(df.format(calRest.get(13)));
        } 
        if (entity.getGoldenPointTimeStr() != null) {
          Calendar calExtra = Calendar.getInstance();
          calExtra.setTime(sdf.parse(entity.getGoldenPointTimeStr()));
          this.goldenPointTimeMinutes.set(df.format(calExtra.get(12)));
          this.goldenPointTimeSeconds.set(df.format(calExtra.get(13)));
        } 
      } catch (ParseException parseException) {}
      this.goldenPointEnabled.set(entity.getGoldenPointEnabled().booleanValue());
    } 
  }
  
  public void fillByDto(RoundsConfigDto roundsConfigDto) {
    if (roundsConfigDto != null) {
      if (roundsConfigDto.getRounds() != null)
        this.rounds.set(roundsConfigDto.getRounds().intValue()); 
      DecimalFormat df = new DecimalFormat("00");
      if (roundsConfigDto.getRoundTimeMinutes() != null) {
        this.roundTimeMinutes.set("" + roundsConfigDto.getRoundTimeMinutes());
        this.roundTimeSeconds.set(df.format(roundsConfigDto.getRoundTimeSeconds()));
      } 
      if (roundsConfigDto.getKyeShiTimeMinutes() != null) {
        this.kyeShiTimeMinutes.set(df.format(roundsConfigDto.getKyeShiTimeMinutes()));
        this.kyeShiTimeSeconds.set(df.format(roundsConfigDto.getKyeShiTimeSeconds()));
      } 
      if (roundsConfigDto.getRestTimeMinutes() != null) {
        this.restTimeMinutes.set(df.format(roundsConfigDto.getRestTimeMinutes()));
        this.restTimeSeconds.set(df.format(roundsConfigDto.getRestTimeSeconds()));
      } 
      if (roundsConfigDto.getGoldenPointTimeMinutes() != null) {
        this.goldenPointTimeMinutes.set(df.format(roundsConfigDto.getGoldenPointTimeMinutes()));
        this.goldenPointTimeSeconds.set(df.format(roundsConfigDto.getGoldenPointTimeSeconds()));
      } 
      this.goldenPointEnabled.set(roundsConfigDto.getGoldenPointEnabled().booleanValue());
    } 
  }
  
  public RoundsConfigDto getRoundsConfigDto() {
    RoundsConfigDto res = new RoundsConfigDto();
    res.setRounds(Integer.valueOf(getRounds()));
    res.setRoundTimeMinutes(Integer.valueOf(Integer.parseInt(getRoundTimeMinutes())));
    res.setRoundTimeSeconds(Integer.valueOf(Integer.parseInt(getRoundTimeSeconds())));
    res.setRestTimeMinutes(Integer.valueOf(Integer.parseInt(getRestTimeMinutes())));
    res.setRestTimeSeconds(Integer.valueOf(Integer.parseInt(getRestTimeSeconds())));
    res.setKyeShiTimeMinutes(Integer.valueOf(Integer.parseInt(getKyeShiTimeMinutes())));
    res.setKyeShiTimeSeconds(Integer.valueOf(Integer.parseInt(getKyeShiTimeSeconds())));
    res.setGoldenPointTimeMinutes(Integer.valueOf(Integer.parseInt(getGoldenPointTimeMinutes())));
    res.setGoldenPointTimeSeconds(Integer.valueOf(Integer.parseInt(getGoldenPointTimeSeconds())));
    res.setGoldenPointEnabled(Boolean.valueOf(getGoldenPointEnabled()));
    return res;
  }
  
  public int getRounds() {
    return this.rounds.get();
  }
  
  public SimpleIntegerProperty roundsProperty() {
    return this.rounds;
  }
  
  public String getRoundTimeMinutes() {
    return this.roundTimeMinutes.get();
  }
  
  public SimpleStringProperty roundTimeMinutesProperty() {
    return this.roundTimeMinutes;
  }
  
  public String getRoundTimeSeconds() {
    return this.roundTimeSeconds.get();
  }
  
  public SimpleStringProperty roundTimeSecondsProperty() {
    return this.roundTimeSeconds;
  }
  
  public String getKyeShiTimeMinutes() {
    return this.kyeShiTimeMinutes.get();
  }
  
  public SimpleStringProperty kyeShiTimeMinutesProperty() {
    return this.kyeShiTimeMinutes;
  }
  
  public String getKyeShiTimeSeconds() {
    return this.kyeShiTimeSeconds.get();
  }
  
  public SimpleStringProperty kyeShiTimeSecondsProperty() {
    return this.kyeShiTimeSeconds;
  }
  
  public String getRestTimeMinutes() {
    return this.restTimeMinutes.get();
  }
  
  public SimpleStringProperty restTimeMinutesProperty() {
    return this.restTimeMinutes;
  }
  
  public String getRestTimeSeconds() {
    return this.restTimeSeconds.get();
  }
  
  public SimpleStringProperty restTimeSecondsProperty() {
    return this.restTimeSeconds;
  }
  
  public boolean getGoldenPointEnabled() {
    return this.goldenPointEnabled.get();
  }
  
  public SimpleBooleanProperty goldenPointEnabledProperty() {
    return this.goldenPointEnabled;
  }
  
  public String getGoldenPointTimeMinutes() {
    return this.goldenPointTimeMinutes.get();
  }
  
  public SimpleStringProperty goldenPointTimeMinutesProperty() {
    return this.goldenPointTimeMinutes;
  }
  
  public String getGoldenPointTimeSeconds() {
    return this.goldenPointTimeSeconds.get();
  }
  
  public SimpleStringProperty goldenPointTimeSecondsProperty() {
    return this.goldenPointTimeSeconds;
  }
  
  public String getRoundTimeStr() {
    return this.roundTimeMinutes.get() + ":" + this.roundTimeSeconds.get();
  }
  
  public String getKyeShiTimeStr() {
    return this.kyeShiTimeMinutes.get() + ":" + this.kyeShiTimeSeconds.get();
  }
  
  public String getRestTimeStr() {
    return this.restTimeMinutes.get() + ":" + this.restTimeSeconds.get();
  }
  
  public String getGoldenPointTimeStr() {
    return this.goldenPointTimeMinutes.get() + ":" + this.goldenPointTimeSeconds.get();
  }
  
  public void setRounds(int rounds) {
    this.rounds.set(rounds);
  }
  
  public void setRoundTimeMinutes(String roundTimeMinutes) {
    this.roundTimeMinutes.set(roundTimeMinutes);
  }
  
  public void setRoundTimeSeconds(String roundTimeSeconds) {
    this.roundTimeSeconds.set(roundTimeSeconds);
  }
  
  public void setKyeShiTimeMinutes(String kyeShiTimeMinutes) {
    this.kyeShiTimeMinutes.set(kyeShiTimeMinutes);
  }
  
  public void setKyeShiTimeSeconds(String kyeShiTimeSeconds) {
    this.kyeShiTimeSeconds.set(kyeShiTimeSeconds);
  }
  
  public void setRestTimeMinutes(String restTimeMinutes) {
    this.restTimeMinutes.set(restTimeMinutes);
  }
  
  public void setRestTimeSeconds(String restTimeSeconds) {
    this.restTimeSeconds.set(restTimeSeconds);
  }
  
  public void setGoldenPointEnabled(boolean goldenPointEnabled) {
    this.goldenPointEnabled.set(goldenPointEnabled);
  }
  
  public void setGoldenPointTimeMinutes(String goldenPointTimeMinutes) {
    this.goldenPointTimeMinutes.set(goldenPointTimeMinutes);
  }
  
  public void setGoldenPointTimeSeconds(String goldenPointTimeSeconds) {
    this.goldenPointTimeSeconds.set(goldenPointTimeSeconds);
  }
}
