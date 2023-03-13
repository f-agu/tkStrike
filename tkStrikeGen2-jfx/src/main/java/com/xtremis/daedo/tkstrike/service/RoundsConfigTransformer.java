package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.RoundsConfig;
import com.xtremis.daedo.tkstrike.tools.ei.om.RoundsConfigDto;
import org.springframework.stereotype.Component;

@Component
public class RoundsConfigTransformer extends CommonRoundsConfigTransformerImpl<RoundsConfigDto, RoundsConfig> {
  RoundsConfigDto newDto() {
    return new RoundsConfigDto();
  }
  
  RoundsConfig newBean() {
    return new RoundsConfig();
  }
}
