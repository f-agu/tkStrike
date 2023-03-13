package com.xtremis.daedo.tkstrike.orm.repository;

import com.xtremis.daedo.tkstrike.orm.model.Phase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhaseRepository extends JpaRepository<Phase, String> {
  Phase getByName(String paramString);
  
  Phase getByAbbreviation(String paramString);
}
