package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.om.MatchLogItemDto;
import com.xtremis.daedo.tkstrike.orm.model.MatchLogItem;
import org.springframework.stereotype.Component;

@Component
public class MatchLogItemTransformerImpl extends DefaultTransformer<MatchLogItemDto, MatchLogItem> implements MatchLogItemTransformer<MatchLogItemDto, MatchLogItem> {
  MatchLogItemDto newDto() {
    return new MatchLogItemDto();
  }
  
  MatchLogItem newBean() {
    return new MatchLogItem();
  }
}
