package com.xtremis.daedo.tkstrike.orm.repository;

import com.xtremis.daedo.tkstrike.orm.model.Flag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlagRepository extends JpaRepository<Flag, String> {
  List<Flag> getByAbbreviation(String paramString);
}
