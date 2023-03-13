package com.xtremis.daedo.tkstrike.ui.model;

import com.xtremis.daedo.tkstrike.orm.model.Entity;
import com.xtremis.daedo.tkstrike.orm.model.ExternalConfig;
import com.xtremis.daedo.tkstrike.orm.model.ScreenResolution;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import org.springframework.beans.BeanUtils;

public class ExternalConfigEntry implements Entry<ExternalConfig> {
  private SimpleStringProperty id = new SimpleStringProperty(this, "id");
  
  private List<String> listenersURLs = new ArrayList<>();
  
  private List<String> nodeListenersURLs = new ArrayList<>();
  
  private SimpleStringProperty venueManagementURL = new SimpleStringProperty(this, "venueManagementURL");
  
  private SimpleStringProperty venueManagementRingNumber = new SimpleStringProperty(this, "venueManagementRingNumber");
  
  private SimpleBooleanProperty extScoreboardBlueOnLeft = new SimpleBooleanProperty(this, "extScoreboardBlueOnLeft");
  
  private SimpleStringProperty extScoreboardResolution = new SimpleStringProperty(this, "extScoreboardResolution");
  
  private SimpleStringProperty rtBroadcastIp = new SimpleStringProperty(this, "rtBroadcastIp");
  
  private SimpleLongProperty rtBroadcastPort = new SimpleLongProperty(this, "rtBroadcastPort");
  
  private SimpleStringProperty rtBroadcastRingNumber = new SimpleStringProperty(this, "rtBroadcastRingNumber");
  
  private SimpleStringProperty matchLogOutputDirectory = new SimpleStringProperty(this, "matchLogOutputDirectory");
  
  private SimpleStringProperty wtOvrUrl = new SimpleStringProperty(this, "wtOvrUrl");
  
  private SimpleStringProperty wtOvrXApiKey = new SimpleStringProperty(this, "wtOvrXApiKey");
  
  private SimpleIntegerProperty wtOvrMat = new SimpleIntegerProperty(this, "wtOvrMat");
  
  private SimpleStringProperty wtOvrUdpIp = new SimpleStringProperty(this, "wtOvrUdpIp");
  
  private SimpleIntegerProperty wtOvrUdpListenPort = new SimpleIntegerProperty(this, "wtOvrUdpListenPort");
  
  private SimpleIntegerProperty wtOvrUdpWritePort = new SimpleIntegerProperty(this, "wtOvrUdpWritePort");
  
  public void fillByEntity(ExternalConfig entity) {
    if (entity != null) {
      this.id.set(entity.getId());
      if (entity.getListenersURLs() != null) {
        this.listenersURLs = new ArrayList<>(entity.getListenersURLs());
      } else {
        this.listenersURLs = new ArrayList<>();
      } 
      if (entity.getNodeListenersURLs() != null) {
        this.nodeListenersURLs = new ArrayList<>(entity.getNodeListenersURLs());
      } else {
        this.nodeListenersURLs = new ArrayList<>();
      } 
      this.venueManagementURL.set(entity.getVenueManagementURL());
      this.venueManagementRingNumber.set(entity.getVenueManagementRingNumber());
      this.extScoreboardBlueOnLeft.set(entity.getExtScoreboardBlueOnLeft().booleanValue());
      this.extScoreboardResolution.set(entity.getExtScoreboardResolution().toString());
      if (entity.getRtBroadcastIp() != null)
        this.rtBroadcastIp.set(entity.getRtBroadcastIp()); 
      if (entity.getRtBroadcastPort() != null && entity.getRtBroadcastPort().longValue() > 0L)
        this.rtBroadcastPort.set(entity.getRtBroadcastPort().longValue()); 
      if (entity.getRtBroadcastRingNumber() != null)
        this.rtBroadcastRingNumber.set(entity.getRtBroadcastRingNumber()); 
      if (entity.getMatchLogOutputDirectory() != null)
        this.matchLogOutputDirectory.set(entity.getMatchLogOutputDirectory()); 
      if (entity.getWtOvrUrl() != null)
        this.wtOvrUrl.set(entity.getWtOvrUrl()); 
      if (entity.getWtOvrXApiKey() != null)
        this.wtOvrXApiKey.set(entity.getWtOvrXApiKey()); 
      if (entity.getWtOvrMat() != null)
        this.wtOvrMat.set(entity.getWtOvrMat().intValue()); 
      if (entity.getWtOvrUdpIp() != null)
        this.wtOvrUdpIp.set(entity.getWtOvrUdpIp()); 
      if (entity.getWtOvrUdpListenPort() != null)
        this.wtOvrUdpListenPort.set(entity.getWtOvrUdpListenPort().intValue()); 
      if (entity.getWtOvrUdpWritePort() != null)
        this.wtOvrUdpWritePort.set(entity.getWtOvrUdpWritePort().intValue()); 
    } 
  }
  
  public ExternalConfig createExternalConfig() {
    ExternalConfig res = new ExternalConfig();
    BeanUtils.copyProperties(this, res, new String[] { "id", "version", "extScoreboardResolution", "listenersURLs", "nodeListenersURLs", "" });
    res.setExtScoreboardResolution(ScreenResolution.valueOf(getExtScoreboardResolution()));
    if (this.listenersURLs != null)
      res.setListenersURLs(new ArrayList<>(this.listenersURLs)); 
    if (this.nodeListenersURLs != null)
      res.setNodeListenersURLs(new ArrayList<>(this.nodeListenersURLs)); 
    return res;
  }
  
  public String getId() {
    return this.id.get();
  }
  
  public SimpleStringProperty idProperty() {
    return this.id;
  }
  
  public List<String> getListenersURLs() {
    return this.listenersURLs;
  }
  
  public void setListenersURLs(List<String> listenersURLs) {
    this.listenersURLs = listenersURLs;
  }
  
  public List<String> getNodeListenersURLs() {
    return this.nodeListenersURLs;
  }
  
  public void setNodeListenersURLs(List<String> nodeListenersURLs) {
    this.nodeListenersURLs = nodeListenersURLs;
  }
  
  public String getVenueManagementURL() {
    return this.venueManagementURL.get();
  }
  
  public SimpleStringProperty venueManagementURLProperty() {
    return this.venueManagementURL;
  }
  
  public String getVenueManagementRingNumber() {
    return this.venueManagementRingNumber.get();
  }
  
  public SimpleStringProperty venueManagementRingNumberProperty() {
    return this.venueManagementRingNumber;
  }
  
  public boolean getExtScoreboardBlueOnLeft() {
    return this.extScoreboardBlueOnLeft.get();
  }
  
  public SimpleBooleanProperty extScoreboardBlueOnLeftProperty() {
    return this.extScoreboardBlueOnLeft;
  }
  
  public String getExtScoreboardResolution() {
    return this.extScoreboardResolution.get();
  }
  
  public SimpleStringProperty extScoreboardResolutionProperty() {
    return this.extScoreboardResolution;
  }
  
  public String getRtBroadcastIp() {
    return this.rtBroadcastIp.get();
  }
  
  public SimpleStringProperty rtBroadcastIpProperty() {
    return this.rtBroadcastIp;
  }
  
  public long getRtBroadcastPort() {
    return this.rtBroadcastPort.get();
  }
  
  public SimpleLongProperty rtBroadcastPortProperty() {
    return this.rtBroadcastPort;
  }
  
  public String getRtBroadcastRingNumber() {
    return this.rtBroadcastRingNumber.get();
  }
  
  public SimpleStringProperty rtBroadcastRingNumberProperty() {
    return this.rtBroadcastRingNumber;
  }
  
  public String getMatchLogOutputDirectory() {
    return this.matchLogOutputDirectory.get();
  }
  
  public SimpleStringProperty matchLogOutputDirectoryProperty() {
    return this.matchLogOutputDirectory;
  }
  
  public String getWtOvrUrl() {
    return this.wtOvrUrl.get();
  }
  
  public SimpleStringProperty wtOvrUrlProperty() {
    return this.wtOvrUrl;
  }
  
  public void setWtOvrUrl(String wtOvrUrl) {
    this.wtOvrUrl.set(wtOvrUrl);
  }
  
  public String getWtOvrXApiKey() {
    return this.wtOvrXApiKey.get();
  }
  
  public SimpleStringProperty wtOvrXApiKeyProperty() {
    return this.wtOvrXApiKey;
  }
  
  public void setWtOvrXApiKey(String wtOvrXApiKey) {
    this.wtOvrXApiKey.set(wtOvrXApiKey);
  }
  
  public int getWtOvrMat() {
    return this.wtOvrMat.get();
  }
  
  public SimpleIntegerProperty wtOvrMatProperty() {
    return this.wtOvrMat;
  }
  
  public void setWtOvrMat(int wtOvrMat) {
    this.wtOvrMat.set(wtOvrMat);
  }
  
  public String getWtOvrUdpIp() {
    return this.wtOvrUdpIp.get();
  }
  
  public SimpleStringProperty wtOvrUdpIpProperty() {
    return this.wtOvrUdpIp;
  }
  
  public void setWtOvrUdpIp(String wtOvrUdpIp) {
    this.wtOvrUdpIp.set(wtOvrUdpIp);
  }
  
  public int getWtOvrUdpListenPort() {
    return this.wtOvrUdpListenPort.get();
  }
  
  public SimpleIntegerProperty wtOvrUdpListenPortProperty() {
    return this.wtOvrUdpListenPort;
  }
  
  public void setWtOvrUdpListenPort(int wtOvrUdpListenPort) {
    this.wtOvrUdpListenPort.set(wtOvrUdpListenPort);
  }
  
  public int getWtOvrUdpWritePort() {
    return this.wtOvrUdpWritePort.get();
  }
  
  public SimpleIntegerProperty wtOvrUdpWritePortProperty() {
    return this.wtOvrUdpWritePort;
  }
  
  public void setWtOvrUdpWritePort(int wtOvrUdpWritePort) {
    this.wtOvrUdpWritePort.set(wtOvrUdpWritePort);
  }
}
