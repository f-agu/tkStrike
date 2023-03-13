package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.om.MatchLogDto;
import com.xtremis.daedo.tkstrike.om.MatchLogItemDto;
import com.xtremis.daedo.tkstrike.orm.model.Gender;
import java.util.Date;
import java.util.List;

public interface MatchLogHistoricalService extends CommonMatchLogHistoricalService<MatchLogDto, MatchLogItemDto> {
  void generateAggregateCSVReport2DefaultDirectory(String paramString1, String paramString2, Gender paramGender, String paramString3, List<String> paramList, Date paramDate1, Date paramDate2) throws TkStrikeServiceException;
  
  List<MatchLogDto> find(String paramString1, String paramString2, Gender paramGender, String paramString3, List<String> paramList, Date paramDate1, Date paramDate2) throws TkStrikeServiceException;
}
