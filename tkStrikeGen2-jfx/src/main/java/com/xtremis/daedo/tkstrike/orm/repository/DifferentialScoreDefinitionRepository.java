package com.xtremis.daedo.tkstrike.orm.repository;

import com.xtremis.daedo.tkstrike.orm.model.DifferentialScoreDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DifferentialScoreDefinitionRepository extends JpaRepository<DifferentialScoreDefinition, String> {
  DifferentialScoreDefinition getByPhaseIdAndSubCategoryId(String paramString1, String paramString2);
}
