package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.om.CommonMatchLogDto;
import com.xtremis.daedo.tkstrike.om.GoldenPointTieBreakerInfoDto;
import com.xtremis.daedo.tkstrike.om.MatchLogDto;
import com.xtremis.daedo.tkstrike.om.MatchLogItemDto;
import com.xtremis.daedo.tkstrike.orm.model.Category;
import com.xtremis.daedo.tkstrike.orm.model.GoldenPointTieBreakerInfo;
import com.xtremis.daedo.tkstrike.orm.model.MatchLog;
import com.xtremis.daedo.tkstrike.orm.model.MatchLogEntity;
import com.xtremis.daedo.tkstrike.orm.model.RoundsConfig;
import com.xtremis.daedo.tkstrike.tools.ei.om.RoundsConfigDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MatchLogTransformerImpl extends BaseMatchLogTransformer<MatchLogDto, MatchLogItemDto, MatchLog, RoundsConfigTransformer, RoundsConfigDto, RoundsConfig> {
  private final CategoryService categoryService;
  
  private final AthleteService athleteService;
  
  private final RoundsConfigTransformer roundsConfigTransformer;
  
  @Autowired
  public MatchLogTransformerImpl(PhaseService phaseService, CategoryService categoryService, AthleteService athleteService, RoundsConfigTransformer roundsConfigTransformer) {
    super(phaseService);
    this.categoryService = categoryService;
    this.athleteService = athleteService;
    this.roundsConfigTransformer = roundsConfigTransformer;
  }
  
  protected RoundsConfigTransformer getRoundsConfigTransformer() {
    return this.roundsConfigTransformer;
  }
  
  public void transferToDto(MatchLog item, MatchLogDto dto) {
    super.transferToDto(item, dto);
    if (item != null && dto != null) {
      if (item.getCategory() != null) {
        Category category = item.getCategory();
        dto.setCategoryId(category.getId());
        dto.setCategoryName(category.getName());
        dto.setCategoryBodyLevel(category.getBodyLevel());
        dto.setCategoryHeadLevel(category.getHeadLevel());
        dto.setCategoryGender(category.getGender());
        dto.setGender(category.getGender());
        if (category.getSubCategory() != null) {
          dto.setSubCategoryId(category.getSubCategory().getId());
          dto.setSubCategoryName(category.getSubCategory().getName());
        } 
      } 
      if (item.getBlueAthlete() != null) {
        dto.setBlueAthleteId(item.getBlueAthlete().getId());
        dto.setBlueAthleteName(item.getBlueAthlete().getScoreboardName());
        dto.setBlueAthleteWtfId(item.getBlueAthlete().getWfId());
        if (item.getBlueAthlete().getFlag() != null) {
          dto.setBlueAthleteFlagId(item.getBlueAthlete().getFlag().getId());
          dto.setBlueAthleteFlagName(item.getBlueAthlete().getFlag().getName());
          dto.setBlueAthleteFlagShowName(item.getBlueAthlete().getFlag().getShowName());
          dto.setBlueAthleteFlagAbbreviation(item.getBlueAthlete().getFlag().getAbbreviation());
          dto.setBlueAthleteFlagImagePath(item.getBlueAthlete().getFlag().getFlagImagePath());
        } 
      } 
      if (item.getRedAthlete() != null) {
        dto.setRedAthleteId(item.getRedAthlete().getId());
        dto.setRedAthleteName(item.getRedAthlete().getScoreboardName());
        dto.setRedAthleteWtfId(item.getRedAthlete().getWfId());
        if (item.getRedAthlete().getFlag() != null) {
          dto.setRedAthleteFlagId(item.getRedAthlete().getFlag().getId());
          dto.setRedAthleteFlagName(item.getRedAthlete().getFlag().getName());
          dto.setRedAthleteFlagShowName(item.getRedAthlete().getFlag().getShowName());
          dto.setRedAthleteFlagAbbreviation(item.getRedAthlete().getFlag().getAbbreviation());
          dto.setRedAthleteFlagImagePath(item.getRedAthlete().getFlag().getFlagImagePath());
        } 
      } 
      dto.setBlueAthleteVideoQuota(item.getBlueAthleteVideoQuota());
      dto.setRedAthleteVideoQuota(item.getRedAthleteVideoQuota());
      if (item.getGoldenPointTieBreakerInfo() != null) {
        GoldenPointTieBreakerInfoDto goldenPointTieBreakerInfoDto = new GoldenPointTieBreakerInfoDto();
        BeanUtils.copyProperties(item.getGoldenPointTieBreakerInfo(), goldenPointTieBreakerInfoDto);
        dto.setGoldenPointTieBreakerInfo(goldenPointTieBreakerInfoDto);
      } 
    } 
  }
  
  public void transferToBean(MatchLogDto dto, MatchLog bean) {
    super.transferToBean(dto, bean);
    if (dto != null && bean != null) {
      try {
        if (dto.getCategoryId() != null)
          bean.setCategory(this.categoryService.getById(dto.getCategoryId())); 
        if (dto.getBlueAthleteId() != null)
          bean.setBlueAthlete(this.athleteService.getById(dto.getBlueAthleteId())); 
        if (dto.getRedAthleteId() != null)
          bean.setRedAthlete(this.athleteService.getById(dto.getRedAthleteId())); 
      } catch (Exception exception) {}
      if (dto.getGoldenPointTieBreakerInfo() != null) {
        GoldenPointTieBreakerInfo goldenPointTieBreakerInfo = new GoldenPointTieBreakerInfo();
        BeanUtils.copyProperties(dto.getGoldenPointTieBreakerInfo(), goldenPointTieBreakerInfo);
        bean.setGoldenPointTieBreakerInfo(goldenPointTieBreakerInfo);
      } 
    } 
  }
  
  MatchLogDto newDto() {
    return new MatchLogDto();
  }
  
  MatchLog newBean() {
    return new MatchLog();
  }
}
