package com.xtremis.daedo.tkstrike.configuration;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class SimulatorCommunicationConditional implements Condition {
  private final TkStrikeCommunicationTypeValue tkStrikeCommunicationType = TkStrikeCommunicationTypeUtil.getInstance().getTkStrikeCommunicationType();
  
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    return TkStrikeCommunicationTypeValue.SIMULATOR.equals(this.tkStrikeCommunicationType);
  }
}
