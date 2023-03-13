package com.xtremis.daedo.tkstrike.ui.model;

import com.xtremis.daedo.tkstrike.orm.model.Entity;
import com.xtremis.daedo.tkstrike.orm.model.RoundsConfig;
import com.xtremis.daedo.tkstrike.orm.model.RoundsConfigEntity;
import com.xtremis.daedo.tkstrike.orm.model.Rules;
import com.xtremis.daedo.tkstrike.orm.model.RulesEntity;
import javafx.beans.property.SimpleBooleanProperty;

public class RulesEntry extends BaseRulesEntry<Rules, RoundsConfigEntry, RoundsConfig> {
  private SimpleBooleanProperty allMatchPARA = new SimpleBooleanProperty(this, "allMatchPARA");
  
  Rules newRulesInstance() {
    return new Rules();
  }
  
  RoundsConfigEntry newRoundsConfigEntry() {
    return new RoundsConfigEntry();
  }
  
  RoundsConfig newRoundsConfigEntity() {
    return new RoundsConfig();
  }
  
  public void fillByEntity(Rules entity) {
    super.fillByEntity(entity);
    if (entity != null)
      this.allMatchPARA.set(entity.getAllMatchPARA().booleanValue()); 
  }
  
  public boolean isAllMatchPARA() {
    return this.allMatchPARA.get();
  }
  
  public SimpleBooleanProperty allMatchPARAProperty() {
    return this.allMatchPARA;
  }
  
  public void setAllMatchPARA(boolean allMatchPARA) {
    this.allMatchPARA.set(allMatchPARA);
  }
}
