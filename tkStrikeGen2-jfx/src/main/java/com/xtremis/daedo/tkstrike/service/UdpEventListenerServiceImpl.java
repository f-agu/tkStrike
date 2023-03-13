package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.UdpEventListener;
import com.xtremis.daedo.tkstrike.orm.repository.UdpEventListenerRepository;
import com.xtremis.daedo.tkstrike.ui.model.UdpEventListenerEntry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UdpEventListenerServiceImpl extends BaseTkStrikeService<UdpEventListener, UdpEventListenerEntry> implements UdpEventListenerService {
  private final UdpEventListenerRepository udpEventListenerRepository;
  
  @Autowired
  public UdpEventListenerServiceImpl(UdpEventListenerRepository udpEventListenerRepository) {
    this.udpEventListenerRepository = udpEventListenerRepository;
  }
  
  @Transactional(readOnly = false)
  public UdpEventListenerEntry createEntry(String udpEventListenerIp, Integer udpEventListenerPort) throws TkStrikeServiceException {
    UdpEventListener entity = create(udpEventListenerIp, udpEventListenerPort);
    if (entity != null && StringUtils.isNotBlank(entity.getId())) {
      UdpEventListenerEntry entry = new UdpEventListenerEntry();
      entry.fillByEntity(entity);
      return entry;
    } 
    return null;
  }
  
  @Transactional(readOnly = false)
  public UdpEventListener create(String udpEventListenerIp, Integer udpEventListenerPort) throws TkStrikeServiceException {
    if (StringUtils.isNotBlank(udpEventListenerIp) && udpEventListenerPort != null) {
      UdpEventListener udpEventListener = new UdpEventListener();
      udpEventListener.setUdpEventListenerIp(udpEventListenerIp);
      udpEventListener.setUdpEventListenerPort(udpEventListenerPort);
      return (UdpEventListener)this.udpEventListenerRepository.saveAndFlush(udpEventListener);
    } 
    return null;
  }
  
  protected JpaRepository<UdpEventListener, String> getRepository() {
    return (JpaRepository<UdpEventListener, String>)this.udpEventListenerRepository;
  }
  
  protected Sort getDefaultSort() {
    return new Sort(Sort.Direction.ASC, new String[] { "id" });
  }
  
  protected void deleteAllChild() throws TkStrikeServiceException {}
  
  public Boolean canDelete(String id) {
    return Boolean.TRUE;
  }
}
