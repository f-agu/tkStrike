package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.RoundsConfigEntity;
import com.xtremis.daedo.tkstrike.tools.ei.om.RoundsConfigDto;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public abstract class CommonRoundsConfigTransformerImpl<D extends RoundsConfigDto, E extends RoundsConfigEntity> extends DefaultTransformer<D, E> implements CommonRoundsConfigTransformer<D, E> {
  static final SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
  
  static final DecimalFormat df = new DecimalFormat("00");
  
  public void transferToDto(E item, D dto) {
    super.transferToDto(item, dto);
    if (dto != null && item != null) {
      dto.setRounds(item.getRounds());
      try {
        if (item.getRoundTimeStr() != null) {
          Calendar calRound = Calendar.getInstance();
          calRound.setTime(sdf.parse(item.getRoundTimeStr()));
          dto.setRoundTimeMinutes(Integer.valueOf(calRound.get(12)));
          dto.setRoundTimeSeconds(Integer.valueOf(calRound.get(13)));
        } 
        if (item.getKyeShiTimeStr() != null) {
          Calendar calKyeShi = Calendar.getInstance();
          calKyeShi.setTime(sdf.parse(item.getKyeShiTimeStr()));
          dto.setKyeShiTimeMinutes(Integer.valueOf(calKyeShi.get(12)));
          dto.setKyeShiTimeSeconds(Integer.valueOf(calKyeShi.get(13)));
        } 
        if (item.getRestTimeStr() != null) {
          Calendar calRest = Calendar.getInstance();
          calRest.setTime(sdf.parse(item.getRestTimeStr()));
          dto.setRestTimeMinutes(Integer.valueOf(calRest.get(12)));
          dto.setRestTimeSeconds(Integer.valueOf(calRest.get(13)));
        } 
        if (item.getGoldenPointTimeStr() != null) {
          Calendar calExtra = Calendar.getInstance();
          calExtra.setTime(sdf.parse(item.getGoldenPointTimeStr()));
          dto.setGoldenPointTimeMinutes(Integer.valueOf(calExtra.get(12)));
          dto.setGoldenPointTimeSeconds(Integer.valueOf(calExtra.get(13)));
        } 
      } catch (ParseException parseException) {}
      dto.setGoldenPointEnabled(item.getGoldenPointEnabled());
    } 
  }
  
  public void transferToBean(D dto, E bean) {
    super.transferToBean(dto, bean);
    if (dto == null || bean != null);
  }
}
