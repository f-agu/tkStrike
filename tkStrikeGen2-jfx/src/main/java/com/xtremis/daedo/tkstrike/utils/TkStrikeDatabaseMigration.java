package com.xtremis.daedo.tkstrike.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;

public class TkStrikeDatabaseMigration {
  private final String jdbcUrl;
  
  private final String jdbcUser;
  
  private final String jdbcPwd;
  
  private final String dbMigrationDir;
  
  private final String tkStrikeGenVersion;
  
  public TkStrikeDatabaseMigration() {
    Properties properties = new Properties();
    InputStream is = TkStrikeDatabaseMigration.class.getResourceAsStream("/META-INF/app.properties");
    try {
      properties.load(is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } 
    this.jdbcUrl = properties.getProperty("jdbc.url");
    this.jdbcUser = properties.getProperty("jdbc.username");
    this.jdbcPwd = properties.getProperty("jdbc.password");
    this.dbMigrationDir = properties.getProperty("flyway.db.migration.dir");
    this.tkStrikeGenVersion = properties.getProperty("tkStrike.genVersion");
  }
  
  public TkStrikeDatabaseMigration(String jdbcUrl, String jdbcUser, String jdbcPwd, String dbMigrationDir, String tkStrikeGenVersion) {
    this.jdbcUrl = jdbcUrl;
    this.jdbcUser = jdbcUser;
    this.jdbcPwd = jdbcPwd;
    this.dbMigrationDir = dbMigrationDir;
    this.tkStrikeGenVersion = tkStrikeGenVersion;
  }
  
  public boolean databaseMigration() {
    boolean res = false;
    Flyway flyway = new Flyway();
    flyway.setDataSource(this.jdbcUrl, this.jdbcUser, this.jdbcPwd, new String[0]);
    flyway.setLocations(new String[] { "/db/migration/java", this.dbMigrationDir, this.dbMigrationDir + this.tkStrikeGenVersion });
    flyway.setBaselineVersionAsString("0.0.0");
    MigrationVersion migrationVersion = flyway.getBaselineVersion();
    if (migrationVersion == null) {
      flyway.baseline();
      res = true;
    } 
    try {
      flyway.migrate();
    } catch (RuntimeException e) {
      e.printStackTrace();
      flyway.repair();
      try {
        flyway.migrate();
      } catch (RuntimeException re) {
        flyway.clean();
        flyway.baseline();
        flyway.migrate();
        res = true;
      } 
    } 
    return res;
  }
  
  public void forceCleanAndMigrate() {
    Flyway flyway = new Flyway();
    flyway.setDataSource(this.jdbcUrl, this.jdbcUser, this.jdbcPwd, new String[0]);
    flyway.setLocations(new String[] { "/db/migration/java", this.dbMigrationDir });
    flyway.setBaselineVersionAsString("0.0.0");
    flyway.clean();
    flyway.baseline();
    flyway.migrate();
  }
}
