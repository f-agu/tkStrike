package com.xtremis.daedo.tkstrike.orm.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

@NoRepositoryBean
public interface DefaultMatchLogRepository<E extends com.xtremis.daedo.tkstrike.orm.model.MatchLogEntity> extends JpaRepository<E, String> {
  E getLastStarted();
  
  List<E> getAllExceptLast();
  
  void deleteByMatchConfigurationId(@Param("matchConfigurationId") String paramString);
}
