package db.migration.java;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

public class V8_0_0__ChangeMatchLogOutputDirectory implements SpringJdbcMigration {
  public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
    final String strNewPath = System.getProperty("user.home") + "/Desktop/TkStrikeLogs";
    jdbcTemplate.execute("UPDATE TKS_EXTERNAL_CONFIG SET MATCH_LOG_OUTPUT_DIRECTORY = ? ", new PreparedStatementCallback<Object>() {
          public Object doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException {
            preparedStatement.setString(1, strNewPath);
            preparedStatement.execute();
            return null;
          }
        });
  }
}
