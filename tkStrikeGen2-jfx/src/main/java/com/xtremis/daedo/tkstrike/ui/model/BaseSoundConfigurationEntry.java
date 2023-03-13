package com.xtremis.daedo.tkstrike.ui.model;

import com.xtremis.daedo.tkstrike.orm.model.Entity;
import com.xtremis.daedo.tkstrike.orm.model.SoundConfigurationEntity;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import org.springframework.beans.BeanUtils;

public abstract class BaseSoundConfigurationEntry<E extends SoundConfigurationEntity> implements ISoundConfigurationEntry<E> {
  private SimpleStringProperty id = new SimpleStringProperty(this, "id", "1");
  
  private SimpleObjectProperty<SoundConfigurationItemEntry> soundBeforeEndTime = new SimpleObjectProperty(this, "soundBeforeEndTime");
  
  private SimpleIntegerProperty soundBeforeEndTimeSecondsBefore = new SimpleIntegerProperty(this, "soundBeforeEndTimeSecondsBefore", -1);
  
  private SimpleObjectProperty<SoundConfigurationItemEntry> soundBeforeStartRound = new SimpleObjectProperty(this, "soundBeforeEndTime");
  
  private SimpleIntegerProperty soundBeforeStartRoundSecondsBefore = new SimpleIntegerProperty(this, "soundBeforeStartRoundSecondsBefore", -1);
  
  private SimpleObjectProperty<SoundConfigurationItemEntry> soundEndOfTime = new SimpleObjectProperty(this, "soundEndOfTime");
  
  private SimpleObjectProperty<SoundConfigurationItemEntry> soundWhenBodyHit = new SimpleObjectProperty(this, "soundWhenBodyHit");
  
  private SimpleObjectProperty<SoundConfigurationItemEntry> soundWhenHeadHit = new SimpleObjectProperty(this, "soundWhenHeadHit");
  
  private SimpleObjectProperty<SoundConfigurationItemEntry> soundWhenKyongGo = new SimpleObjectProperty(this, "soundWhenKyongGo");
  
  private SimpleObjectProperty<SoundConfigurationItemEntry> soundWhenGameJeon = new SimpleObjectProperty(this, "soundWhenGameJeon");
  
  private SimpleObjectProperty<SoundConfigurationItemEntry> soundWhenTechMeeting = new SimpleObjectProperty(this, "soundWhenTechMeeting");
  
  public void fillByEntity(E entity) {
    if (entity != null) {
      this.id.set(entity.getId());
      SoundConfigurationItemEntry bet = new SoundConfigurationItemEntry();
      bet.fillByEntity(entity.getSoundBeforeEndTime());
      this.soundBeforeEndTime.set(bet);
      this.soundBeforeEndTimeSecondsBefore.set(entity.getSoundBeforeEndTimeSecondsBefore().intValue());
      SoundConfigurationItemEntry bsr = new SoundConfigurationItemEntry();
      bsr.fillByEntity(entity.getSoundBeforeStartRound());
      this.soundBeforeStartRound.set(bsr);
      this.soundBeforeStartRoundSecondsBefore.set(entity.getSoundBeforeStartRoundSecondsBefore().intValue());
      SoundConfigurationItemEntry eot = new SoundConfigurationItemEntry();
      eot.fillByEntity(entity.getSoundEndOfTime());
      this.soundEndOfTime.set(eot);
      SoundConfigurationItemEntry wbh = new SoundConfigurationItemEntry();
      wbh.fillByEntity(entity.getSoundWhenBodyHit());
      this.soundWhenBodyHit.set(wbh);
      SoundConfigurationItemEntry whh = new SoundConfigurationItemEntry();
      whh.fillByEntity(entity.getSoundWhenHeadHit());
      this.soundWhenHeadHit.set(whh);
      SoundConfigurationItemEntry wkg = new SoundConfigurationItemEntry();
      wkg.fillByEntity(entity.getSoundWhenKyongGo());
      this.soundWhenKyongGo.set(wkg);
      SoundConfigurationItemEntry wgj = new SoundConfigurationItemEntry();
      wgj.fillByEntity(entity.getSoundWhenGameJeon());
      this.soundWhenGameJeon.set(wgj);
      SoundConfigurationItemEntry tm = new SoundConfigurationItemEntry();
      tm.fillByEntity(entity.getSoundWhenTechMeeting());
      this.soundWhenTechMeeting.set(tm);
    } 
  }
  
  public E createSoundConfiguration() {
    E res = newEntityInstance();
    BeanUtils.copyProperties(this, res, new String[] { "version", "id", "soundBeforeEndTime", "soundBeforeStartRound", "soundEndOfTime", "soundWhenBodyHit", "soundWhenHeadHit", "soundWhenKyongGo", "soundWhenGameJeon", "soundWhenTechMeeting" });
    res.setSoundBeforeEndTime(getSoundBeforeEndTime().createSoundConfigurationItem());
    res.setSoundBeforeStartRound(getSoundBeforeStartRound().createSoundConfigurationItem());
    res.setSoundEndOfTime(getSoundEndOfTime().createSoundConfigurationItem());
    res.setSoundWhenBodyHit(getSoundWhenBodyHit().createSoundConfigurationItem());
    res.setSoundWhenHeadHit(getSoundWhenHeadHit().createSoundConfigurationItem());
    res.setSoundWhenKyongGo(getSoundWhenKyongGo().createSoundConfigurationItem());
    res.setSoundWhenGameJeon(getSoundWhenGameJeon().createSoundConfigurationItem());
    res.setSoundWhenTechMeeting(getSoundWhenTechMeeting().createSoundConfigurationItem());
    return res;
  }
  
  public String getId() {
    return this.id.get();
  }
  
  public SimpleStringProperty idProperty() {
    return this.id;
  }
  
  public SoundConfigurationItemEntry getSoundBeforeEndTime() {
    return (SoundConfigurationItemEntry)this.soundBeforeEndTime.get();
  }
  
  public SimpleObjectProperty<SoundConfigurationItemEntry> soundBeforeEndTimeProperty() {
    return this.soundBeforeEndTime;
  }
  
  public int getSoundBeforeEndTimeSecondsBefore() {
    return this.soundBeforeEndTimeSecondsBefore.get();
  }
  
  public Property soundBeforeEndTimeSecondsBeforeProperty() {
    return (Property)this.soundBeforeEndTimeSecondsBefore;
  }
  
  public SoundConfigurationItemEntry getSoundBeforeStartRound() {
    return (SoundConfigurationItemEntry)this.soundBeforeStartRound.get();
  }
  
  public SimpleObjectProperty<SoundConfigurationItemEntry> soundBeforeStartRoundProperty() {
    return this.soundBeforeStartRound;
  }
  
  public int getSoundBeforeStartRoundSecondsBefore() {
    return this.soundBeforeStartRoundSecondsBefore.get();
  }
  
  public Property soundBeforeStartRoundSecondsBeforeProperty() {
    return (Property)this.soundBeforeStartRoundSecondsBefore;
  }
  
  public SoundConfigurationItemEntry getSoundEndOfTime() {
    return (SoundConfigurationItemEntry)this.soundEndOfTime.get();
  }
  
  public SimpleObjectProperty<SoundConfigurationItemEntry> soundEndOfTimeProperty() {
    return this.soundEndOfTime;
  }
  
  public SoundConfigurationItemEntry getSoundWhenBodyHit() {
    return (SoundConfigurationItemEntry)this.soundWhenBodyHit.get();
  }
  
  public SimpleObjectProperty<SoundConfigurationItemEntry> soundWhenBodyHitProperty() {
    return this.soundWhenBodyHit;
  }
  
  public SoundConfigurationItemEntry getSoundWhenHeadHit() {
    return (SoundConfigurationItemEntry)this.soundWhenHeadHit.get();
  }
  
  public SimpleObjectProperty<SoundConfigurationItemEntry> soundWhenHeadHitProperty() {
    return this.soundWhenHeadHit;
  }
  
  public SoundConfigurationItemEntry getSoundWhenKyongGo() {
    return (SoundConfigurationItemEntry)this.soundWhenKyongGo.get();
  }
  
  public SimpleObjectProperty<SoundConfigurationItemEntry> soundWhenKyongGoProperty() {
    return this.soundWhenKyongGo;
  }
  
  public SoundConfigurationItemEntry getSoundWhenGameJeon() {
    return (SoundConfigurationItemEntry)this.soundWhenGameJeon.get();
  }
  
  public SimpleObjectProperty<SoundConfigurationItemEntry> soundWhenGameJeonProperty() {
    return this.soundWhenGameJeon;
  }
  
  public SoundConfigurationItemEntry getSoundWhenTechMeeting() {
    return (SoundConfigurationItemEntry)this.soundWhenTechMeeting.get();
  }
  
  public SimpleObjectProperty<SoundConfigurationItemEntry> soundWhenTechMeetingProperty() {
    return this.soundWhenTechMeeting;
  }
  
  abstract E newEntityInstance();
}
