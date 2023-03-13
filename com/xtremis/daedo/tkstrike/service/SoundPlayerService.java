package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.ui.model.SoundConfigurationItemEntry;

public interface SoundPlayerService {
  void refreshAllSounds() throws TkStrikeServiceException;
  
  void playSoundItem(SoundConfigurationItemEntry paramSoundConfigurationItemEntry);
  
  void playSoundBeforeEndTime();
  
  void playSoundBeforeStartRound();
  
  void playSoundEndOfTime();
  
  void playSoundWhenBodyHit();
  
  void playSoundWhenHeadHit();
  
  void playSoundWhenKyongGo();
  
  void playSoundWhenGameJeon();
  
  void playSoundWhenTechMeeting();
}
