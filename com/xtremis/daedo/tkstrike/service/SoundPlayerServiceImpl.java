package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.SoundVolume;
import com.xtremis.daedo.tkstrike.ui.model.ISoundConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.SoundConfigurationItemEntry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SoundPlayerServiceImpl implements SoundPlayerService, InitializingBean {
  private final SoundConfigurationService soundConfigurationService;
  
  static ISoundConfigurationEntry currentSoundConfigurationEntry;
  
  @Autowired
  public SoundPlayerServiceImpl(SoundConfigurationService soundConfigurationService) {
    this.soundConfigurationService = soundConfigurationService;
  }
  
  public void afterPropertiesSet() throws Exception {
    refreshAllSounds();
  }
  
  public void refreshAllSounds() throws TkStrikeServiceException {
    ISoundConfigurationEntry soundConfigurationEntry = (ISoundConfigurationEntry)this.soundConfigurationService.getSoundConfigurationEntry();
    if (soundConfigurationEntry != null)
      currentSoundConfigurationEntry = soundConfigurationEntry; 
  }
  
  public void playSoundItem(SoundConfigurationItemEntry soundConfigurationItemEntry) {
    if (soundConfigurationItemEntry.getEnabled())
      playSound(soundConfigurationItemEntry); 
  }
  
  public void playSoundBeforeEndTime() {
    playSound(currentSoundConfigurationEntry.getSoundBeforeEndTime());
  }
  
  public void playSoundBeforeStartRound() {
    playSound(currentSoundConfigurationEntry.getSoundBeforeStartRound());
  }
  
  public void playSoundEndOfTime() {
    playSound(currentSoundConfigurationEntry.getSoundEndOfTime());
  }
  
  public void playSoundWhenBodyHit() {
    playSound(currentSoundConfigurationEntry.getSoundWhenBodyHit());
  }
  
  public void playSoundWhenHeadHit() {
    playSound(currentSoundConfigurationEntry.getSoundWhenHeadHit());
  }
  
  public void playSoundWhenKyongGo() {
    playSound(currentSoundConfigurationEntry.getSoundWhenKyongGo());
  }
  
  public void playSoundWhenGameJeon() {
    playSound(currentSoundConfigurationEntry.getSoundWhenGameJeon());
  }
  
  public void playSoundWhenTechMeeting() {
    playSound(currentSoundConfigurationEntry.getSoundWhenTechMeeting());
  }
  
  void playSound(SoundConfigurationItemEntry soundConfigurationItemEntry) {
    if (soundConfigurationItemEntry != null && soundConfigurationItemEntry.getEnabled())
      soundConfigurationItemEntry.getSound().play(getVolume(soundConfigurationItemEntry.getSoundVolume())); 
  }
  
  private double getVolume(String soundVolume) {
    double res = 0.75D;
    if (SoundVolume.HIGH.toString().equals(soundVolume)) {
      res = 1.0D;
    } else if (SoundVolume.LOW.toString().equals(soundVolume)) {
      res = 0.25D;
    } 
    return res;
  }
}
