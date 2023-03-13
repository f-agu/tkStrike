package com.xtremis.daedo.tkstrike.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.io.FileUtils;

public final class TkStrikeBaseDirectoriesUtil {
  private final String logBaseDir;
  
  private final String dbBaseDir;
  
  private final String workBaseDir;
  
  private static final TkStrikeBaseDirectoriesUtil INSTANCE = new TkStrikeBaseDirectoriesUtil();
  
  public static TkStrikeBaseDirectoriesUtil getInstance() {
    return INSTANCE;
  }
  
  private TkStrikeBaseDirectoriesUtil() {
    Properties properties = new Properties();
    InputStream is = TkStrikeBaseDirectoriesUtil.class.getResourceAsStream("/META-INF/app.properties");
    try {
      properties.load(is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } 
    if ("Mac OS X".equals(System.getProperty("os.name"))) {
      String userHome = System.getProperty("user.home");
      this.logBaseDir = userHome + "/.tkStrike/log";
      this.dbBaseDir = userHome + "/.tkStrike/db";
      this.workBaseDir = userHome + "/.tkStrike/work";
    } else {
      this.logBaseDir = properties.getProperty("tkStrike.log.baseDir");
      this.dbBaseDir = properties.getProperty("tkStrike.db.baseDir");
      this.workBaseDir = properties.getProperty("tkStrike.work.baseDir");
    } 
  }
  
  public void validateOrCreate() throws IOException {
    File dirLog = new File(this.logBaseDir);
    FileUtils.forceMkdir(dirLog);
    File dirDb = new File(this.dbBaseDir);
    FileUtils.forceMkdir(dirDb);
    File dirWork = new File(this.workBaseDir);
    FileUtils.forceMkdir(dirWork);
  }
  
  public String getLogBaseDir() {
    return this.logBaseDir + (!this.logBaseDir.endsWith("/") ? "/" : "");
  }
  
  public String getDbBaseDir() {
    return this.dbBaseDir + (!this.dbBaseDir.endsWith("/") ? "/" : "");
  }
  
  public String getWorkBaseDir() {
    return this.workBaseDir + (!this.workBaseDir.endsWith("/") ? "/" : "");
  }
}
