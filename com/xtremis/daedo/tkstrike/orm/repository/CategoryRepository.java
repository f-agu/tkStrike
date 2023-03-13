package com.xtremis.daedo.tkstrike.orm.repository;

import com.xtremis.daedo.tkstrike.orm.model.Category;
import com.xtremis.daedo.tkstrike.orm.model.Gender;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {
  Category getBySC_G_N(String paramString1, Gender paramGender, String paramString2);
  
  List<Category> findByGender(Gender paramGender);
  
  List<Category> findBySC_G(String paramString, Gender paramGender);
  
  List<Category> findBySubCategoryName(String paramString);
  
  List<Category> findBySubCategoryId(String paramString);
}
