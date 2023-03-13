package com.xtremis.daedo.tkstrike.service;

import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class TkStrikeStyleSheetsHelper implements InitializingBean {
  private final String tkStrikeStyleSheets;
  
  private static String[] styleSheets = null;
  
  public TkStrikeStyleSheetsHelper(@Qualifier("tkStrikeStyleSheets") String tkStrikeStyleSheets) {
    this.tkStrikeStyleSheets = tkStrikeStyleSheets;
  }
  
  public String[] getTkStrikeStyleSheets() {
    return styleSheets;
  }
  
  public void afterPropertiesSet() throws Exception {
    String[] base = StringUtils.split(this.tkStrikeStyleSheets, ",");
    Arrays.<String>asList(base).forEach(i -> styleSheets = (String[])ArrayUtils.add((Object[])styleSheets, TkStrikeStyleSheetsHelper.class.getResource(i).toExternalForm()));
  }
}
