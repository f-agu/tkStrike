package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.Referee;
import com.xtremis.daedo.tkstrike.orm.repository.RefereeRepository;
import com.xtremis.daedo.tkstrike.tools.ei.om.RefereeDto;
import com.xtremis.daedo.tkstrike.ui.scene.RefereeEntry;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RefereeServiceImpl extends BaseTkStrikeService<Referee, RefereeEntry> implements RefereeService {
  private final RefereeRepository refereeRepository;
  
  @Autowired
  public RefereeServiceImpl(RefereeRepository refereeRepository) {
    this.refereeRepository = refereeRepository;
  }
  
  protected JpaRepository<Referee, String> getRepository() {
    return (JpaRepository<Referee, String>)this.refereeRepository;
  }
  
  protected Sort getDefaultSort() {
    return new Sort(Sort.Direction.ASC, new String[] { "scoreboardName" });
  }
  
  protected void deleteAllChild() throws TkStrikeServiceException {}
  
  public Boolean canDelete(String id) {
    return Boolean.TRUE;
  }
  
  @Transactional(readOnly = false)
  public Referee doGetCreateOrUpdateEntity(RefereeDto refereeDto) throws TkStrikeServiceException {
    if (refereeDto != null && refereeDto.getId() != null) {
      Referee referee = (Referee)this.refereeRepository.findOne(refereeDto.getId());
      if (referee == null)
        return createNew(refereeDto); 
      return update(refereeDto);
    } 
    return null;
  }
  
  @Transactional(readOnly = false)
  public RefereeEntry doGetCreateOrUpdateEntry(RefereeDto refereeDto) throws TkStrikeServiceException {
    Referee referee = doGetCreateOrUpdateEntity(refereeDto);
    if (referee != null) {
      RefereeEntry res = new RefereeEntry();
      res.fillByEntity(referee);
      return res;
    } 
    return null;
  }
  
  @Transactional(readOnly = false)
  public Referee createNew(RefereeDto refereeDto) throws TkStrikeServiceException {
    if (refereeDto != null && refereeDto.getId() != null) {
      Referee other = (Referee)this.refereeRepository.findOne(refereeDto.getId());
      if (other == null) {
        Referee theNew = new Referee();
        BeanUtils.copyProperties(refereeDto, theNew, new String[] { "version" });
        return (Referee)this.refereeRepository.saveAndFlush(theNew);
      } 
    } 
    return null;
  }
  
  @Transactional(readOnly = false)
  public Referee update(RefereeDto refereeDto) throws TkStrikeServiceException {
    if (refereeDto != null && refereeDto.getId() != null) {
      Referee other = (Referee)this.refereeRepository.findOne(refereeDto.getId());
      if (other != null) {
        BeanUtils.copyProperties(refereeDto, other, new String[] { "id", "version" });
        return (Referee)this.refereeRepository.saveAndFlush(other);
      } 
    } 
    return null;
  }
}
