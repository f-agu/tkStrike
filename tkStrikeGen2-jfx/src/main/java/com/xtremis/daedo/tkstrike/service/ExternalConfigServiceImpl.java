package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.ExternalConfig;
import com.xtremis.daedo.tkstrike.orm.repository.ExternalConfigRepository;
import com.xtremis.daedo.tkstrike.ui.model.ExternalConfigEntry;
import javax.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ExternalConfigServiceImpl extends BaseTkStrikeService<ExternalConfig, ExternalConfigEntry> implements ExternalConfigService {
  @Resource
  private ExternalConfigRepository externalConfigRepository;
  
  @Value("${tkStrike.wtCompetitionDataProtocol.enabled}")
  private Boolean wtCompetitionDataProtocol;
  
  public Boolean getWtCompetitionDataProtocol() {
    return this.wtCompetitionDataProtocol;
  }
  
  protected JpaRepository<ExternalConfig, String> getRepository() {
    return (JpaRepository<ExternalConfig, String>)this.externalConfigRepository;
  }
  
  protected Sort getDefaultSort() {
    return null;
  }
  
  protected void deleteAllChild() throws TkStrikeServiceException {}
  
  public Boolean canDelete(String id) {
    return Boolean.FALSE;
  }
  
  public ExternalConfig getExternalConfig() throws TkStrikeServiceException {
    try {
      return (ExternalConfig)this.externalConfigRepository.getOne("1");
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  public ExternalConfigEntry getExternalConfigEntry() throws TkStrikeServiceException {
    ExternalConfigEntry res = null;
    ExternalConfig externalConfig = getExternalConfig();
    if (externalConfig != null) {
      res = new ExternalConfigEntry();
      res.fillByEntity(externalConfig);
    } 
    return res;
  }
  
  @Transactional(readOnly = false)
  public void update(ExternalConfig externalConfig) throws TkStrikeServiceException {
    if (externalConfig != null)
      try {
        ExternalConfig current = getExternalConfig();
        BeanUtils.copyProperties(externalConfig, current, new String[] { "id", "version" });
        this.externalConfigRepository.saveAndFlush(current);
      } catch (Exception e) {
        throw new TkStrikeServiceException(e);
      }  
  }
  
  @Transactional(readOnly = false)
  public void update(ExternalConfigEntry externalConfigEntry) throws TkStrikeServiceException {
    if (externalConfigEntry != null)
      update(externalConfigEntry.createExternalConfig()); 
  }
  
  public void delete(String id) throws TkStrikeServiceException {}
}
