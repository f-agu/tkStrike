package com.xtremis.daedo.tkstrike.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.text.Text;

public class TkStrikeCountdownNoUI implements Runnable {
  private String cdName;
  
  private IntegerProperty minutesProperty = (IntegerProperty)new SimpleIntegerProperty(this, "minutes", 2);
  
  private int minutes = 2;
  
  private IntegerProperty secondsProperty = (IntegerProperty)new SimpleIntegerProperty(this, "seconds", 0);
  
  private int seconds = 0;
  
  private long miliseconds = 0L;
  
  private int microSeconds = 100;
  
  private Calendar calendar = Calendar.getInstance();
  
  private static final SimpleDateFormat sdf = new SimpleDateFormat("m:ss");
  
  private static final SimpleDateFormat sdfMilis = new SimpleDateFormat("s.SSS");
  
  private ScheduledExecutorService scheduler = null;
  
  private ScheduledFuture<?> scheduleAtFixedRate = null;
  
  private SimpleBooleanProperty working = new SimpleBooleanProperty(this, "working", false);
  
  private SimpleBooleanProperty finished = new SimpleBooleanProperty(this, "finished", false);
  
  private SimpleStringProperty currentTimeAsString = new SimpleStringProperty(this, "currentTimeAsString");
  
  private SimpleStringProperty currentTimeInternalAsString = new SimpleStringProperty(this, "currentTimeInternalAsString");
  
  private SimpleLongProperty currentTimeMillis = new SimpleLongProperty(this, "currentTimeMillis");
  
  private Text textElement;
  
  private final CountdownMillisRefreshType countdownMillisRefreshType;
  
  public TkStrikeCountdownNoUI(String cdName, CountdownMillisRefreshType countdownMillisRefreshType) {
    this.cdName = cdName;
    this.countdownMillisRefreshType = countdownMillisRefreshType;
    _initComponents();
  }
  
  public void setTextElement(final Text textElement) {
    this.textElement = textElement;
    this.currentTimeAsString.addListener(new ChangeListener<String>() {
          public void changed(ObservableValue<? extends String> observable, String oldValue, final String newValue) {
            Platform.runLater(new Runnable() {
                  public void run() {
                    textElement.setText(newValue);
                  }
                });
          }
        });
  }
  
  private void updateInternalTime() {
    this.currentTimeAsString.set(this.currentTimeInternalAsString.getValue());
  }
  
  public void run() {}
  
  private void _initComponents() {
    this.calendar.set(12, this.minutes);
    this.calendar.set(13, this.seconds);
    this.calendar.set(14, 0);
    this.scheduler = Executors.newSingleThreadScheduledExecutor();
    updateClocks(true);
  }
  
  private void resetTimeAndUpdateClock() {
    this.calendar.set(12, this.minutes);
    this.calendar.set(13, this.seconds);
    this.calendar.set(14, 0);
    updateClocks(true);
  }
  
  private void updateClocks(boolean forceRefresh) {
    this.currentTimeMillis.set(this.calendar.getTimeInMillis());
    int newMinute = this.calendar.get(12);
    int prevMinute = this.minutes;
    int newSecond = this.calendar.get(13);
    int prevSecond = this.seconds;
    this.minutes = newMinute;
    this.seconds = newSecond;
    this.miliseconds = this.calendar.get(14);
    if (this.minutes == 0 && this.seconds < 10) {
      if (CountdownMillisRefreshType.NONE.equals(this.countdownMillisRefreshType)) {
        if (this.miliseconds == 999L) {
          this.currentTimeAsString.set(sdf.format(this.calendar.getTime()));
          this.currentTimeInternalAsString.set(sdf.format(this.calendar.getTime()));
        } else if (this.seconds == 0 && (this.miliseconds == 0L || this.miliseconds == 250L || this.miliseconds == 500L || this.miliseconds == 750L)) {
          this.currentTimeAsString.set(sdfMilis.format(this.calendar.getTime()).substring(0, 4));
          this.currentTimeInternalAsString.set(sdfMilis.format(this.calendar.getTime()).substring(0, 4));
        } 
      } else {
        if (this.seconds == 9 && this.miliseconds == 999L)
          this.currentTimeAsString.set(sdf.format(this.calendar.getTime())); 
        if (showMillis(this.miliseconds))
          this.currentTimeAsString.set(sdfMilis.format(this.calendar.getTime()).substring(0, 4)); 
        this.currentTimeInternalAsString.set(sdfMilis.format(this.calendar.getTime()).substring(0, 4));
      } 
    } else if (forceRefresh || prevMinute != newMinute || prevSecond != newSecond) {
      this.currentTimeAsString.set(sdf.format(this.calendar.getTime()));
      this.currentTimeInternalAsString.set(sdf.format(this.calendar.getTime()));
    } 
  }
  
  private boolean showMillis(long milliseconds) {
    switch (this.countdownMillisRefreshType) {
      case SHORTEST:
        return (milliseconds % 10L == 0L);
      case SHORT:
        return (milliseconds % 50L == 0L);
      case MEDIUM:
        return (milliseconds % 100L == 0L);
      case LONG:
        return (milliseconds == 0L || milliseconds == 250L || milliseconds == 500L || milliseconds == 750L);
    } 
    return false;
  }
  
  private void refreshClocks() {
    this.calendar.add(14, -1);
    updateClocks(false);
    if (this.minutes == 0 && this.seconds == 0 && this.miliseconds == 0L) {
      this.finished.set(true);
      this.finished.set(false);
      stop();
    } 
  }
  
  public void play() {
    if (!this.working.get())
      if (this.minutes == 0 && this.seconds == 0 && this.miliseconds == 0L) {
        this.finished.set(true);
        this.finished.set(false);
        stop();
      } else {
        this.working.set(true);
        this.finished.set(false);
        refreshClocks();
        this.scheduleAtFixedRate = this.scheduler.scheduleAtFixedRate(new Runnable() {
              private int cont = 0;
              
              public void run() {
                if ((TkStrikeCountdownNoUI.this.minutes == 0 && TkStrikeCountdownNoUI.this.seconds < 10) || TkStrikeCountdownNoUI.this.miliseconds > 0L)
                  this.cont = 1000; 
                if (this.cont < 1000) {
                  this.cont++;
                } else {
                  TkStrikeCountdownNoUI.this.refreshClocks();
                } 
              }
            },  0L, 1L, TimeUnit.MILLISECONDS);
      }  
  }
  
  public void stop() {
    if (this.working.get()) {
      this.working.set(false);
      updateInternalTime();
      this.scheduleAtFixedRate.cancel(true);
    } 
  }
  
  public void clean() {
    if (!this.working.get())
      resetTimeAndUpdateClock(); 
  }
  
  public void clean(int minutes, int seconds) {
    if (!this.working.get()) {
      if (minutes >= 0)
        this.minutes = minutes; 
      if (seconds >= 0 && seconds < 60)
        this.seconds = seconds; 
      clean();
    } 
  }
  
  public void clean(int minutes, int seconds, int centiseconds) {
    if (!this.working.get()) {
      if (minutes >= 0)
        this.minutes = minutes; 
      if (seconds >= 0 && seconds < 60)
        this.seconds = seconds; 
      if (centiseconds >= 0)
        this.miliseconds = (centiseconds * 10); 
      this.calendar.set(12, this.minutes);
      this.calendar.set(13, this.seconds);
      this.calendar.set(14, Long.valueOf(this.miliseconds).intValue());
      updateClocks(true);
    } 
  }
  
  public void cleanAndRestart() {
    if (this.working.get())
      stop(); 
    clean();
    play();
  }
  
  public void cleanAndRestart(int minutes, int seconds) {
    if (!this.working.get()) {
      clean(minutes, seconds);
      play();
    } 
  }
  
  public boolean getWorking() {
    return this.working.get();
  }
  
  public ReadOnlyBooleanProperty workingProperty() {
    return (ReadOnlyBooleanProperty)this.working;
  }
  
  public String getCurrentTimeAsString() {
    return this.currentTimeAsString.get();
  }
  
  public String getCurrentTimeInternalAsString() {
    return this.currentTimeInternalAsString.getValue();
  }
  
  public ReadOnlyStringProperty currentTimeAsStringProperty() {
    return (ReadOnlyStringProperty)this.currentTimeAsString;
  }
  
  public long getCurrentTimeMillis() {
    return this.currentTimeMillis.get();
  }
  
  public ReadOnlyLongProperty currentTimeMillisProperty() {
    return (ReadOnlyLongProperty)this.currentTimeMillis;
  }
  
  public boolean getFinished() {
    return this.finished.get();
  }
  
  public ReadOnlyBooleanProperty finishedProperty() {
    return (ReadOnlyBooleanProperty)this.finished;
  }
  
  public int getMinutes() {
    return this.minutes;
  }
  
  public void setMinutes(int minutes) {
    this.minutes = minutes;
    if (!this.working.get())
      resetTimeAndUpdateClock(); 
  }
  
  public int getSeconds() {
    return this.seconds;
  }
  
  public long getMilliseconds() {
    return this.miliseconds;
  }
  
  public void setSeconds(int seconds) {
    this.seconds = seconds;
    if (!this.working.get())
      resetTimeAndUpdateClock(); 
  }
  
  public int getCentiseconds() {
    return (this.miliseconds > 0L) ? Long.valueOf(this.miliseconds / 10L).intValue() : 0;
  }
}
