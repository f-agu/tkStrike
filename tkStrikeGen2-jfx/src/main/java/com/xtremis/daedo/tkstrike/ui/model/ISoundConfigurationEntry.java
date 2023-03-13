package com.xtremis.daedo.tkstrike.ui.model;

import com.xtremis.daedo.tkstrike.orm.model.Entity;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public interface ISoundConfigurationEntry<E extends com.xtremis.daedo.tkstrike.orm.model.SoundConfigurationEntity> extends Entry<E> {
  void fillByEntity(E paramE);
  
  E createSoundConfiguration();
  
  SimpleStringProperty idProperty();
  
  SoundConfigurationItemEntry getSoundBeforeEndTime();
  
  SimpleObjectProperty<SoundConfigurationItemEntry> soundBeforeEndTimeProperty();
  
  int getSoundBeforeEndTimeSecondsBefore();
  
  Property soundBeforeEndTimeSecondsBeforeProperty();
  
  SoundConfigurationItemEntry getSoundBeforeStartRound();
  
  SimpleObjectProperty<SoundConfigurationItemEntry> soundBeforeStartRoundProperty();
  
  int getSoundBeforeStartRoundSecondsBefore();
  
  Property soundBeforeStartRoundSecondsBeforeProperty();
  
  SoundConfigurationItemEntry getSoundEndOfTime();
  
  SimpleObjectProperty<SoundConfigurationItemEntry> soundEndOfTimeProperty();
  
  SoundConfigurationItemEntry getSoundWhenBodyHit();
  
  SimpleObjectProperty<SoundConfigurationItemEntry> soundWhenBodyHitProperty();
  
  SoundConfigurationItemEntry getSoundWhenHeadHit();
  
  SimpleObjectProperty<SoundConfigurationItemEntry> soundWhenHeadHitProperty();
  
  SoundConfigurationItemEntry getSoundWhenKyongGo();
  
  SimpleObjectProperty<SoundConfigurationItemEntry> soundWhenKyongGoProperty();
  
  SoundConfigurationItemEntry getSoundWhenGameJeon();
  
  SimpleObjectProperty<SoundConfigurationItemEntry> soundWhenGameJeonProperty();
  
  SoundConfigurationItemEntry getSoundWhenTechMeeting();
  
  SimpleObjectProperty<SoundConfigurationItemEntry> soundWhenTechMeetingProperty();
}
