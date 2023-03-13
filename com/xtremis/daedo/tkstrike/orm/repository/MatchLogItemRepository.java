package com.xtremis.daedo.tkstrike.orm.repository;

import com.xtremis.daedo.tkstrike.orm.model.MatchLogItem;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface MatchLogItemRepository extends DefaultMatchLogItemRepository<MatchLogItem> {
  @Query(value = "DELETE FROM TKS_MATCH_LOG_ITEM", nativeQuery = true)
  @Modifying
  @Transactional(readOnly = false)
  void deleteAllNative();
  
  @Query(value = "DELETE FROM TKS_MATCH_LOG_ITEM WHERE MATCH_LOG_ID!=:matchLogId ", nativeQuery = true)
  @Modifying
  @Transactional(readOnly = false)
  void deleteNotOfMatchLog(@Param("matchLogId") String paramString);
  
  @Transactional(readOnly = false)
  Long deleteByMatchLogId(String paramString);
  
  @Transactional(readOnly = false)
  Long deleteBySystemTimeLessThan(Long paramLong);
  
  List<MatchLogItem> findByMatchLogId(String paramString);
}
