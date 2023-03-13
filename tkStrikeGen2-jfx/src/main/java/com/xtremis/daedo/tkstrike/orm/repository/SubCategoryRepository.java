package com.xtremis.daedo.tkstrike.orm.repository;

import com.xtremis.daedo.tkstrike.orm.model.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubCategoryRepository extends JpaRepository<SubCategory, String> {
  SubCategory getScByName(String paramString);
}
