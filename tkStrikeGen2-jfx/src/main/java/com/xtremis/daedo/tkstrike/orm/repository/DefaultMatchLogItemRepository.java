package com.xtremis.daedo.tkstrike.orm.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@NoRepositoryBean
public interface DefaultMatchLogItemRepository<E extends com.xtremis.daedo.tkstrike.orm.model.MatchLogItemEntity> extends JpaRepository<E, String> {
  @Transactional(readOnly = false)
  void deleteAllNative();
  
  @Transactional(readOnly = false)
  void deleteNotOfMatchLog(@Param("matchLogId") String paramString);
  
  @Transactional(readOnly = false)
  Long deleteByMatchLogId(String paramString);
  
  @Transactional(readOnly = false)
  Long deleteBySystemTimeLessThan(Long paramLong);
  
  List<E> findByMatchLogId(String paramString);
}
