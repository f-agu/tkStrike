package com.xtremis.daedo.tkstrike.configuration;

import com.xtremis.daedo.tkstrike.utils.TkStrikeBaseDirectoriesUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;

public final class TkStrikeCommunicationTypeUtil {
  private static final Logger _log = Logger.getLogger(TkStrikeCommunicationTypeUtil.class);
  
  private static final TkStrikeCommunicationTypeUtil INSTANCE = new TkStrikeCommunicationTypeUtil();
  
  public static TkStrikeCommunicationTypeUtil getInstance() {
    return INSTANCE;
  }
  
  public TkStrikeCommunicationTypeValue getTkStrikeCommunicationType() {
    TkStrikeCommunicationTypeValue tkStrikeCommunicationType = TkStrikeCommunicationTypeValue.NORMAL;
    TkStrikeBaseDirectoriesUtil tkStrikeBaseDirectoriesUtil = TkStrikeBaseDirectoriesUtil.getInstance();
    File fCommunicationType = new File(tkStrikeBaseDirectoriesUtil.getWorkBaseDir() + "tkStrikeWork.tks");
    _log.info("getTkStrikeCommunicationType File CommunicationType = " + ((fCommunicationType != null) ? fCommunicationType.getAbsolutePath() : "null"));
    if (fCommunicationType != null && fCommunicationType.exists()) {
      Properties properties = new Properties();
      try {
        properties.load(new FileInputStream(fCommunicationType));
      } catch (IOException e) {
        throw new RuntimeException(e);
      } 
      tkStrikeCommunicationType = TkStrikeCommunicationTypeValue.valueOf(properties.getProperty("tkStrike.communication.type", "NORMAL"));
    } 
    return tkStrikeCommunicationType;
  }
  
  public void setTkStrikeCommunicationType(TkStrikeCommunicationTypeValue newValue) throws Exception {
    if (newValue != null) {
      TkStrikeBaseDirectoriesUtil tkStrikeBaseDirectoriesUtil = TkStrikeBaseDirectoriesUtil.getInstance();
      File fCommunicationType = new File(tkStrikeBaseDirectoriesUtil.getWorkBaseDir() + "tkStrikeWork.tks");
      if (!fCommunicationType.exists())
        fCommunicationType.createNewFile(); 
      _log.info("setTkStrikeCommunicationType File CommunicationType = " + ((fCommunicationType != null) ? fCommunicationType.getAbsolutePath() : "null"));
      Properties properties = new Properties();
      try {
        properties.load(new FileInputStream(fCommunicationType));
      } catch (IOException e) {
        throw new RuntimeException(e);
      } 
      properties.setProperty("tkStrike.communication.type", newValue.toString());
      FileOutputStream fis = new FileOutputStream(fCommunicationType);
      properties.store(fis, (String)null);
      fis.flush();
      fis.close();
    } 
  }
}
