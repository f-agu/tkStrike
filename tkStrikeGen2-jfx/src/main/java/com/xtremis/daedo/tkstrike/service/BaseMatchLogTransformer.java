package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.om.CommonMatchLogDto;
import com.xtremis.daedo.tkstrike.om.CommonMatchLogItemDto;
import com.xtremis.daedo.tkstrike.orm.model.MatchLogEntity;
import com.xtremis.daedo.tkstrike.orm.model.RoundsConfigEntity;
import com.xtremis.daedo.tkstrike.tools.ei.om.RoundsConfigDto;

public abstract class BaseMatchLogTransformer<D extends CommonMatchLogDto<RD>, ID extends CommonMatchLogItemDto, DE extends MatchLogEntity<RE>, RCT extends CommonRoundsConfigTransformer<RD, RE>, RD extends RoundsConfigDto, RE extends RoundsConfigEntity> extends DefaultTransformer<D, DE> implements MatchLogTransformer<D, ID, DE> {
  private final PhaseService phaseService;
  
  public BaseMatchLogTransformer(PhaseService phaseService) {
    this.phaseService = phaseService;
  }
  
  public void transferToDto(DE item, D dto) {
    super.transferToDto(item, dto);
    if (item != null && dto != null) {
      if (item.getPhase() != null) {
        dto.setPhaseId(item.getPhase().getId());
        dto.setPhaseName(item.getPhase().getName());
      } 
      if (item.getRoundsConfig() != null)
        dto.setRoundsConfig(getRoundsConfigTransformer().transferToDto(item.getRoundsConfig())); 
    } 
  }
  
  public void transferToBean(D dto, DE bean) {
    super.transferToBean(dto, bean);
    if (dto != null && bean != null) {
      if (dto.getPhaseId() != null)
        try {
          bean.setPhase(this.phaseService.getById(dto.getPhaseId()));
        } catch (TkStrikeServiceException e) {
          e.printStackTrace();
        }  
      if (dto.getRoundsConfig() != null)
        bean.setRoundsConfig(getRoundsConfigTransformer().transferToBean(dto.getRoundsConfig())); 
    } 
  }
  
  public String[] getIgnoredProperties4Copy() {
    return new String[] { "phase", "roundsConfig" };
  }
  
  protected abstract RCT getRoundsConfigTransformer();
}
