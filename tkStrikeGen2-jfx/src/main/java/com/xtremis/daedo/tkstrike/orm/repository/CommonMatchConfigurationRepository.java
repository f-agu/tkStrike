package com.xtremis.daedo.tkstrike.orm.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CommonMatchConfigurationRepository<MC extends com.xtremis.daedo.tkstrike.orm.model.MatchConfigurationEntity> extends JpaRepository<MC, String> {
  MC getByMatchNumber(String paramString);
  
  Long countByPhaseId(String paramString);
  
  List<MC> findByDates(Date paramDate1, Date paramDate2);
  
  List<MC> findNotStarted();
  
  List<MC> findStarted();
}
