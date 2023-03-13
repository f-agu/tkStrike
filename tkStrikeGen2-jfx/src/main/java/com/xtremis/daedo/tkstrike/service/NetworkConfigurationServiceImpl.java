package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.NetworkConfiguration;
import com.xtremis.daedo.tkstrike.orm.model.NetworkConfigurationEntity;
import com.xtremis.daedo.tkstrike.orm.repository.NetworkConfigurationRepository;
import com.xtremis.daedo.tkstrike.ui.model.INetworkConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.NetworkConfigurationEntry;
import javax.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class NetworkConfigurationServiceImpl extends BaseTkStrikeService<NetworkConfiguration, NetworkConfigurationEntry> implements NetworkConfigurationService<NetworkConfiguration, NetworkConfigurationEntry> {
  @Resource
  private NetworkConfigurationRepository networkConfigurationRepository;
  
  protected JpaRepository<NetworkConfiguration, String> getRepository() {
    return (JpaRepository<NetworkConfiguration, String>)this.networkConfigurationRepository;
  }
  
  protected Sort getDefaultSort() {
    return null;
  }
  
  protected void deleteAllChild() throws TkStrikeServiceException {}
  
  public Boolean canDelete(String id) {
    return Boolean.FALSE;
  }
  
  public NetworkConfiguration getNetworkConfiguration() throws TkStrikeServiceException {
    try {
      return (NetworkConfiguration)this.networkConfigurationRepository.getOne("1");
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  public NetworkConfigurationEntry getNetworkConfigurationEntry() throws TkStrikeServiceException {
    NetworkConfiguration networkConfiguration = getNetworkConfiguration();
    NetworkConfigurationEntry res = new NetworkConfigurationEntry();
    res.fillByEntity(networkConfiguration);
    return res;
  }
  
  @Transactional(readOnly = false)
  public void update(NetworkConfiguration networkConfiguration) throws TkStrikeServiceException {
    if (networkConfiguration != null)
      try {
        NetworkConfiguration current = getNetworkConfiguration();
        BeanUtils.copyProperties(networkConfiguration, current, new String[] { "id", "version" });
        this.networkConfigurationRepository.saveAndFlush(current);
      } catch (Exception e) {
        throw new TkStrikeServiceException(e);
      }  
  }
  
  @Transactional(readOnly = false)
  public void update(NetworkConfigurationEntry networkConfigurationEntry) throws TkStrikeServiceException {}
  
  public void delete(String id) throws TkStrikeServiceException {}
}
