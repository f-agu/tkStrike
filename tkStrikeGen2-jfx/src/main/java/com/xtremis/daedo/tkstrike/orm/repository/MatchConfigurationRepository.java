package com.xtremis.daedo.tkstrike.orm.repository;

import com.xtremis.daedo.tkstrike.orm.model.MatchConfiguration;

public interface MatchConfigurationRepository extends CommonMatchConfigurationRepository<MatchConfiguration> {
  Long getCountByAthlete(String paramString);
  
  Long countByBlueAthleteIdOrRedAthleteId(String paramString1, String paramString2);
  
  Long countByCategoryId(String paramString);
  
  Long countByCategorySubCategoryId(String paramString);
}
