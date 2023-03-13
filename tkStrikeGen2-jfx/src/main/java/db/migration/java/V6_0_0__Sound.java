package db.migration.java;

import com.xtremis.daedo.tkstrike.orm.model.SoundVolume;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

public class V6_0_0__Sound implements SpringJdbcMigration {
  public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
    jdbcTemplate.execute("TRUNCATE TABLE TKS_SOUND_CONFIG");
    jdbcTemplate.execute("INSERT INTO TKS_SOUND_CONFIG (ID,VERSION,BET_ENABLED,BET_VOLUME,BET_SOUND,BET_BEFORE_SECONDS,BSR_ENABLED,BSR_VOLUME,BSR_SOUND,BSR_BEFORE_SECONDS,EOT_ENABLED,EOT_VOLUME,EOT_SOUND,WBH_ENABLED,WBH_VOLUME,WBH_SOUND,WHH_ENABLED,WHH_VOLUME,WHH_SOUND,WKG_ENABLED,WKG_VOLUME,WKG_SOUND,WGJ_ENABLED,WGJ_VOLUME,WGJ_SOUND, TM_ENABLED,TM_VOLUME,TM_SOUND) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new PreparedStatementCallback<Object>() {
          public Object doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException {
            preparedStatement.setString(1, "1");
            preparedStatement.setInt(2, 0);
            preparedStatement.setBoolean(3, true);
            preparedStatement.setString(4, SoundVolume.MEDIUM.toString());
            preparedStatement.setString(5, "/sounds/Time Passing.mp3");
            preparedStatement.setInt(6, 5);
            preparedStatement.setBoolean(7, false);
            preparedStatement.setString(8, SoundVolume.MEDIUM.toString());
            preparedStatement.setString(9, "/sounds/Bell Buoy.mp3");
            preparedStatement.setInt(10, 3);
            preparedStatement.setBoolean(11, true);
            preparedStatement.setString(12, SoundVolume.MEDIUM.toString());
            preparedStatement.setString(13, "/sounds/Alarm.mp3");
            preparedStatement.setBoolean(14, true);
            preparedStatement.setString(15, SoundVolume.MEDIUM.toString());
            preparedStatement.setString(16, "/sounds/Disparo.wav");
            preparedStatement.setBoolean(17, true);
            preparedStatement.setString(18, SoundVolume.MEDIUM.toString());
            preparedStatement.setString(19, "/sounds/Disparo.wav");
            preparedStatement.setBoolean(20, true);
            preparedStatement.setString(21, SoundVolume.MEDIUM.toString());
            preparedStatement.setString(22, "/sounds/Marca.wav");
            preparedStatement.setBoolean(23, true);
            preparedStatement.setString(24, SoundVolume.MEDIUM.toString());
            preparedStatement.setString(25, "/sounds/Laser.wav");
            preparedStatement.setBoolean(26, true);
            preparedStatement.setString(27, SoundVolume.MEDIUM.toString());
            preparedStatement.setString(28, "/sounds/NewMeeting.wav");
            preparedStatement.execute();
            return null;
          }
        });
  }
}
