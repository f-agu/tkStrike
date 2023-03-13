package com.xtremis.daedo.tkstrike.orm.model;

public interface SoundConfigurationEntity extends Entity {
  SoundConfigurationItem getSoundBeforeEndTime();
  
  void setSoundBeforeEndTime(SoundConfigurationItem paramSoundConfigurationItem);
  
  Integer getSoundBeforeEndTimeSecondsBefore();
  
  void setSoundBeforeEndTimeSecondsBefore(Integer paramInteger);
  
  SoundConfigurationItem getSoundBeforeStartRound();
  
  void setSoundBeforeStartRound(SoundConfigurationItem paramSoundConfigurationItem);
  
  Integer getSoundBeforeStartRoundSecondsBefore();
  
  void setSoundBeforeStartRoundSecondsBefore(Integer paramInteger);
  
  SoundConfigurationItem getSoundEndOfTime();
  
  void setSoundEndOfTime(SoundConfigurationItem paramSoundConfigurationItem);
  
  SoundConfigurationItem getSoundWhenBodyHit();
  
  void setSoundWhenBodyHit(SoundConfigurationItem paramSoundConfigurationItem);
  
  SoundConfigurationItem getSoundWhenHeadHit();
  
  void setSoundWhenHeadHit(SoundConfigurationItem paramSoundConfigurationItem);
  
  SoundConfigurationItem getSoundWhenKyongGo();
  
  void setSoundWhenKyongGo(SoundConfigurationItem paramSoundConfigurationItem);
  
  SoundConfigurationItem getSoundWhenGameJeon();
  
  void setSoundWhenGameJeon(SoundConfigurationItem paramSoundConfigurationItem);
  
  SoundConfigurationItem getSoundWhenTechMeeting();
  
  void setSoundWhenTechMeeting(SoundConfigurationItem paramSoundConfigurationItem);
}
