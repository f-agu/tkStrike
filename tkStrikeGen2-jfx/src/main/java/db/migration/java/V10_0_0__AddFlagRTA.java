package db.migration.java;

import com.xtremis.daedo.tkstrike.utils.TkStrikeBaseDirectoriesUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

public class V10_0_0__AddFlagRTA implements SpringJdbcMigration {
  private static final Logger logger = Logger.getLogger(V10_0_0__AddFlagRTA.class);
  
  private static final String[] flags = new String[] { "RTA", "Refugee Taekwondo Athletes" };
  
  public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
    final String pathFlagsDirectory = TkStrikeBaseDirectoriesUtil.getInstance().getWorkBaseDir() + "flags/";
    File dirFlagsDirectory = new File(pathFlagsDirectory);
    if (!dirFlagsDirectory.exists())
      FileUtils.forceMkdir(dirFlagsDirectory); 
    jdbcTemplate.execute("INSERT INTO TKS_FLAG (ID,VERSION,NAME,ABBREVIATION,FLAG_IMAGE_PATH) VALUES (?,?,?,?,?)", new PreparedStatementCallback<Object>() {
          public Object doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException {
            String abbreviation = "RTA";
            String flag = "WT.png";
            String country = "Refugee Taekwondo Athletes";
            try {
              File flagImageFile = new File(pathFlagsDirectory + country + "-" + abbreviation + ".png");
              System.out.println("flag:" + flag + " " + abbreviation);
              IOUtils.copy(getClass().getResourceAsStream("/db/utils/flagsWTCompetitionDataProtocol/" + flag), new FileOutputStream(flagImageFile));
              preparedStatement.setString(1, UUID.randomUUID().toString().replaceAll("-", ""));
              preparedStatement.setInt(2, 0);
              preparedStatement.setString(3, country.trim());
              preparedStatement.setString(4, abbreviation);
              preparedStatement.setString(5, flagImageFile.getAbsolutePath());
              preparedStatement.execute();
            } catch (Exception e) {
              e.printStackTrace();
            } 
            return null;
          }
        });
  }
}
