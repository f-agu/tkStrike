package com.xtremis.daedo.tkstrike.configuration;

import com.xtremis.daedo.tkstrike.utils.TkStrikeBaseDirectoriesUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class TkStrikeExtraCommunicationConditional implements Condition {
  private static final String propertyKey = "tkStrike.extraCommunicationDisabledGroups.enabled";
  
  private final Boolean activate;
  
  public TkStrikeExtraCommunicationConditional() {
    Properties properties = new Properties();
    InputStream is = TkStrikeBaseDirectoriesUtil.class.getResourceAsStream("/META-INF/app.properties");
    try {
      properties.load(is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } 
    File fCommunicationType = new File(TkStrikeBaseDirectoriesUtil.getInstance().getWorkBaseDir() + "tkStrike-ext.properties");
    if (fCommunicationType.exists())
      try {
        properties.load(new FileInputStream(fCommunicationType));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }  
    if (properties.containsKey("tkStrike.extraCommunicationDisabledGroups.enabled")) {
      this.activate = Boolean.valueOf(properties.getProperty("tkStrike.extraCommunicationDisabledGroups.enabled"));
    } else {
      this.activate = Boolean.FALSE;
    } 
  }
  
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    return this.activate.booleanValue();
  }
}
