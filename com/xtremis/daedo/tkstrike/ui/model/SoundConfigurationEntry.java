package com.xtremis.daedo.tkstrike.ui.model;

import com.xtremis.daedo.tkstrike.orm.model.SoundConfiguration;
import com.xtremis.daedo.tkstrike.orm.model.SoundConfigurationEntity;

public class SoundConfigurationEntry extends BaseSoundConfigurationEntry<SoundConfiguration> {
  SoundConfiguration newEntityInstance() {
    return new SoundConfiguration();
  }
}
