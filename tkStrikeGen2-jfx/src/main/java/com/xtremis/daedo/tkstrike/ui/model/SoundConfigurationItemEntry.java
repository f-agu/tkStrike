package com.xtremis.daedo.tkstrike.ui.model;

import com.xtremis.daedo.tkstrike.orm.model.SoundConfigurationItem;
import com.xtremis.daedo.tkstrike.orm.model.SoundVolume;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.media.AudioClip;
import org.springframework.beans.BeanUtils;

public class SoundConfigurationItemEntry {
  private SimpleBooleanProperty enabled = new SimpleBooleanProperty(this, "enabled", false);
  
  private SimpleStringProperty soundVolume = new SimpleStringProperty(this, "soundVolume", SoundVolume.MEDIUM.toString());
  
  private SimpleObjectProperty<AudioClip> sound = new SimpleObjectProperty(this, "sound");
  
  private SimpleStringProperty name = new SimpleStringProperty(this, "name");
  
  public void fillByEntity(SoundConfigurationItem entity) {
    if (entity != null) {
      this.enabled.set(entity.getEnabled().booleanValue());
      this.soundVolume.set(entity.getSoundVolume().toString());
      if (entity.getSoundFilePath() != null) {
        this.name.set(entity.getSoundFilePath());
        URL soundURL = SoundConfigurationItemEntry.class.getResource(entity.getSoundFilePath());
        if (soundURL == null) {
          File soundFile = new File(entity.getSoundFilePath());
          try {
            soundURL = soundFile.toURI().toURL();
          } catch (MalformedURLException e) {
            soundURL = null;
          } 
        } 
        if (soundURL == null)
          throw new RuntimeException("Bad URL !!"); 
        this.sound.set(new AudioClip(soundURL.toString()));
      } 
    } 
  }
  
  public SoundConfigurationItem createSoundConfigurationItem() {
    SoundConfigurationItem res = new SoundConfigurationItem();
    BeanUtils.copyProperties(this, res, new String[] { "sound", "name", "soundVolume" });
    res.setSoundVolume(SoundVolume.valueOf(getSoundVolume()));
    res.setSoundFilePath(getName());
    return res;
  }
  
  public boolean getEnabled() {
    return this.enabled.get();
  }
  
  public SimpleBooleanProperty enabledProperty() {
    return this.enabled;
  }
  
  public String getSoundVolume() {
    return this.soundVolume.get();
  }
  
  public SimpleStringProperty soundVolumeProperty() {
    return this.soundVolume;
  }
  
  public AudioClip getSound() {
    return (AudioClip)this.sound.get();
  }
  
  public SimpleObjectProperty<AudioClip> soundProperty() {
    return this.sound;
  }
  
  public String getName() {
    return this.name.get();
  }
  
  public SimpleStringProperty nameProperty() {
    return this.name;
  }
}
