package com.xtremis.daedo.tkstrike.orm.repository;

import com.xtremis.daedo.tkstrike.orm.model.Athlete;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AthleteRepository extends JpaRepository<Athlete, String> {
  Athlete getByWfId(String paramString);
  
  Athlete getByOvrInternalId(String paramString);
  
  List<Athlete> findByFlagId(String paramString);
  
  List<Athlete> findByFlagName(String paramString);
  
  List<Athlete> findByFlagAbbreviation(String paramString);
  
  Long countByFlagId(String paramString);
}
