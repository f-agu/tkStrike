package com.xtremis.daedo.tkstrike.ui.model;

public interface Entry<E extends com.xtremis.daedo.tkstrike.orm.model.Entity> {
  void fillByEntity(E paramE);
  
  String getId();
}
