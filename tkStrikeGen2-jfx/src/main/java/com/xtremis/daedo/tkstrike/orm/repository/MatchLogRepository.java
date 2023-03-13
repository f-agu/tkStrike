package com.xtremis.daedo.tkstrike.orm.repository;

import com.xtremis.daedo.tkstrike.orm.model.MatchLog;
import com.xtremis.daedo.tkstrike.orm.model.MatchLogEntity;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchLogRepository extends DefaultMatchLogRepository<MatchLog> {
  MatchLog getLastStarted();
  
  List<MatchLog> getAllExceptLast();
  
  @Modifying
  @Query(name = "DELETE FROM MatchLog m WHERE m.matchConfigurationId = :matchConfigurationId")
  void deleteByMatchConfigurationId(@Param("matchConfigurationId") String paramString);
  
  Long getCountByAthlete(String paramString);
}
