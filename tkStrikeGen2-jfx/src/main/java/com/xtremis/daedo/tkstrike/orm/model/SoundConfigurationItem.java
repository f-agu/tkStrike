package com.xtremis.daedo.tkstrike.orm.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;

@Embeddable
public class SoundConfigurationItem {
  @Column(name = "SOUND_ENABLED")
  private Boolean enabled;
  
  @Enumerated(EnumType.STRING)
  @Column(name = "SOUND_VOLME")
  private SoundVolume soundVolume;
  
  @Lob
  @Column(name = "SOUND_FILE_PATH")
  private String soundFilePath;
  
  public Boolean getEnabled() {
    return this.enabled;
  }
  
  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }
  
  public SoundVolume getSoundVolume() {
    return this.soundVolume;
  }
  
  public void setSoundVolume(SoundVolume soundVolume) {
    this.soundVolume = soundVolume;
  }
  
  public String getSoundFilePath() {
    return this.soundFilePath;
  }
  
  public void setSoundFilePath(String soundBytes) {
    this.soundFilePath = soundBytes;
  }
}
