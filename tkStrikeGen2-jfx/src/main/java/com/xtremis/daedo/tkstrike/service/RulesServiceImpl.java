package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.Rules;
import com.xtremis.daedo.tkstrike.orm.model.RulesEntity;
import com.xtremis.daedo.tkstrike.orm.repository.RulesRepository;
import com.xtremis.daedo.tkstrike.ui.model.IRulesEntry;
import com.xtremis.daedo.tkstrike.ui.model.RulesEntry;
import javax.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RulesServiceImpl extends BaseTkStrikeService<Rules, RulesEntry> implements RulesService<Rules, RulesEntry> {
  @Autowired
  private AppStatusWorker appStatusWorker;
  
  @Resource
  private RulesRepository rulesRepository;
  
  protected JpaRepository<Rules, String> getRepository() {
    return (JpaRepository<Rules, String>)this.rulesRepository;
  }
  
  protected Sort getDefaultSort() {
    return null;
  }
  
  protected void deleteAllChild() throws TkStrikeServiceException {}
  
  public Boolean canDelete(String id) {
    return Boolean.FALSE;
  }
  
  public Rules getRules() throws TkStrikeServiceException {
    try {
      return (Rules)this.rulesRepository.getOne("1");
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  public RulesEntry getRulesEntry() throws TkStrikeServiceException {
    RulesEntry res = null;
    Rules rules = getRules();
    if (rules != null) {
      res = new RulesEntry();
      res.fillByEntity(rules);
    } 
    return res;
  }
  
  @Transactional(readOnly = false)
  public void update(Rules rules) throws TkStrikeServiceException {
    if (rules != null)
      try {
        Rules current = getRules();
        BeanUtils.copyProperties(rules, current, new String[] { "id", "version" });
        this.rulesRepository.saveAndFlush(current);
        this.appStatusWorker.setRulesEntry(getRulesEntry());
      } catch (Exception e) {
        throw new TkStrikeServiceException(e);
      }  
  }
  
  @Transactional(readOnly = false)
  public void update(RulesEntry rulesEntry) throws TkStrikeServiceException {
    if (rulesEntry != null)
      update((Rules)rulesEntry.createRules()); 
  }
  
  public void delete(String id) throws TkStrikeServiceException {}
}
