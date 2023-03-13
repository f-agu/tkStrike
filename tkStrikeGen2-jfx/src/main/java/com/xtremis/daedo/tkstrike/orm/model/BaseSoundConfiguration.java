package com.xtremis.daedo.tkstrike.orm.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseSoundConfiguration extends BaseEntity implements SoundConfigurationEntity {
  @Embedded
  @AttributeOverrides({@AttributeOverride(name = "enabled", column = @Column(name = "BET_ENABLED")), @AttributeOverride(name = "soundVolume", column = @Column(name = "BET_VOLUME")), @AttributeOverride(name = "soundFilePath", column = @Column(name = "BET_SOUND"))})
  private SoundConfigurationItem soundBeforeEndTime;
  
  @Column(name = "BET_BEFORE_SECONDS")
  private Integer soundBeforeEndTimeSecondsBefore;
  
  @Embedded
  @AttributeOverrides({@AttributeOverride(name = "enabled", column = @Column(name = "BSR_ENABLED")), @AttributeOverride(name = "soundVolume", column = @Column(name = "BSR_VOLUME")), @AttributeOverride(name = "soundFilePath", column = @Column(name = "BSR_SOUND"))})
  private SoundConfigurationItem soundBeforeStartRound;
  
  @Column(name = "BSR_BEFORE_SECONDS")
  private Integer soundBeforeStartRoundSecondsBefore;
  
  @Embedded
  @AttributeOverrides({@AttributeOverride(name = "enabled", column = @Column(name = "EOT_ENABLED")), @AttributeOverride(name = "soundVolume", column = @Column(name = "EOT_VOLUME")), @AttributeOverride(name = "soundFilePath", column = @Column(name = "EOT_SOUND"))})
  private SoundConfigurationItem soundEndOfTime;
  
  @Embedded
  @AttributeOverrides({@AttributeOverride(name = "enabled", column = @Column(name = "WBH_ENABLED")), @AttributeOverride(name = "soundVolume", column = @Column(name = "WBH_VOLUME")), @AttributeOverride(name = "soundFilePath", column = @Column(name = "WBH_SOUND"))})
  private SoundConfigurationItem soundWhenBodyHit;
  
  @Embedded
  @AttributeOverrides({@AttributeOverride(name = "enabled", column = @Column(name = "WHH_ENABLED")), @AttributeOverride(name = "soundVolume", column = @Column(name = "WHH_VOLUME")), @AttributeOverride(name = "soundFilePath", column = @Column(name = "WHH_SOUND"))})
  private SoundConfigurationItem soundWhenHeadHit;
  
  @Embedded
  @AttributeOverrides({@AttributeOverride(name = "enabled", column = @Column(name = "WKG_ENABLED")), @AttributeOverride(name = "soundVolume", column = @Column(name = "WKG_VOLUME")), @AttributeOverride(name = "soundFilePath", column = @Column(name = "WKG_SOUND"))})
  private SoundConfigurationItem soundWhenKyongGo;
  
  @Embedded
  @AttributeOverrides({@AttributeOverride(name = "enabled", column = @Column(name = "WGJ_ENABLED")), @AttributeOverride(name = "soundVolume", column = @Column(name = "WGJ_VOLUME")), @AttributeOverride(name = "soundFilePath", column = @Column(name = "WGJ_SOUND"))})
  private SoundConfigurationItem soundWhenGameJeon;
  
  @Embedded
  @AttributeOverrides({@AttributeOverride(name = "enabled", column = @Column(name = "TM_ENABLED")), @AttributeOverride(name = "soundVolume", column = @Column(name = "TM_VOLUME")), @AttributeOverride(name = "soundFilePath", column = @Column(name = "TM_SOUND"))})
  private SoundConfigurationItem soundWhenTechMeeting;
  
  public SoundConfigurationItem getSoundBeforeEndTime() {
    return this.soundBeforeEndTime;
  }
  
  public void setSoundBeforeEndTime(SoundConfigurationItem soundBeforeEndTime) {
    this.soundBeforeEndTime = soundBeforeEndTime;
  }
  
  public Integer getSoundBeforeEndTimeSecondsBefore() {
    return this.soundBeforeEndTimeSecondsBefore;
  }
  
  public void setSoundBeforeEndTimeSecondsBefore(Integer soundBeforeEndTimeSecondsBefore) {
    this.soundBeforeEndTimeSecondsBefore = soundBeforeEndTimeSecondsBefore;
  }
  
  public SoundConfigurationItem getSoundBeforeStartRound() {
    return this.soundBeforeStartRound;
  }
  
  public void setSoundBeforeStartRound(SoundConfigurationItem soundBeforeStartRound) {
    this.soundBeforeStartRound = soundBeforeStartRound;
  }
  
  public Integer getSoundBeforeStartRoundSecondsBefore() {
    return this.soundBeforeStartRoundSecondsBefore;
  }
  
  public void setSoundBeforeStartRoundSecondsBefore(Integer soundBeforeStartRoundSecondsBefore) {
    this.soundBeforeStartRoundSecondsBefore = soundBeforeStartRoundSecondsBefore;
  }
  
  public SoundConfigurationItem getSoundEndOfTime() {
    return this.soundEndOfTime;
  }
  
  public void setSoundEndOfTime(SoundConfigurationItem soundEndOfTime) {
    this.soundEndOfTime = soundEndOfTime;
  }
  
  public SoundConfigurationItem getSoundWhenBodyHit() {
    return this.soundWhenBodyHit;
  }
  
  public void setSoundWhenBodyHit(SoundConfigurationItem soundWhenBodyHit) {
    this.soundWhenBodyHit = soundWhenBodyHit;
  }
  
  public SoundConfigurationItem getSoundWhenHeadHit() {
    return this.soundWhenHeadHit;
  }
  
  public void setSoundWhenHeadHit(SoundConfigurationItem soundWhenHeadHit) {
    this.soundWhenHeadHit = soundWhenHeadHit;
  }
  
  public SoundConfigurationItem getSoundWhenKyongGo() {
    return this.soundWhenKyongGo;
  }
  
  public void setSoundWhenKyongGo(SoundConfigurationItem soundWhenKyongGo) {
    this.soundWhenKyongGo = soundWhenKyongGo;
  }
  
  public SoundConfigurationItem getSoundWhenGameJeon() {
    return this.soundWhenGameJeon;
  }
  
  public void setSoundWhenGameJeon(SoundConfigurationItem soundWhenGameJeon) {
    this.soundWhenGameJeon = soundWhenGameJeon;
  }
  
  public SoundConfigurationItem getSoundWhenTechMeeting() {
    return this.soundWhenTechMeeting;
  }
  
  public void setSoundWhenTechMeeting(SoundConfigurationItem soundWhenTechMeeting) {
    this.soundWhenTechMeeting = soundWhenTechMeeting;
  }
}
