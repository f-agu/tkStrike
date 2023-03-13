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

public class V5_0_0__FlagsWTCompetitionDataProtocol implements SpringJdbcMigration {
  private static final Logger logger = Logger.getLogger(V5_0_0__FlagsWTCompetitionDataProtocol.class);
  
  private static final String[] flags = new String[] { 
      "AFG", "Afghanistan", "ALB", "Albania", "ALG", "Algeria", "ASA", "American Samoa", "AND", "Andorra", 
      "ANG", "Angola", "ANT", "Antigua and Barbuda", "ARG", "Argentina", "ARM", "Armenia", "ARU", "Aruba", 
      "AUS", "Australia", "AUT", "Austria", "AZE", "Azerbaijan", "BAH", "Bahamas", "BRN", "Bahrain", 
      "BAN", "Bangladesh", "BAR", "Barbados", "BLR", "Belarus", "BEL", "Belgium", "BIZ", "Belize", 
      "BEN", "Benin", "BER", "Bermuda", "BHU", "Bhutan", "BOL", "Bolivia", "BIH", "Bosnia and Herzegovina", 
      "BOT", "Botswana", "BRA", "Brazil", "BRU", "Brunei Darussalam", "BUL", "Bulgaria", "BUR", "Burundi", 
      "BDI", "Burkina Faso", "CAM", "Cambodia", "CMR", "Cameroon", "CAN", "Canada", "CPV", "Cape Verde", 
      "CAY", "Cayman Islands", "CAF", "Central African Republic", "CHA", "Chad", "CHI", "Chile", "TPE", "Chinese Taipei", 
      "COL", "Colombia", "COM", "Comoros", "CGO", "Congo", "COK", "Cook Islands", "CRC", "Costa Rica", 
      "CIV", "Côte D'Ivoire", "CRO", "Croatia", "CUB", "Cuba", "CYP", "Cyprus", "CZE", "Czech Republic", 
      "PRK", "Democratic People's Republic of Korea", "COD", "Democratic Republic of the Congo", "DEN", "Denmark", "DJI", "Djibouti", "DOM", "Dominican Republic", 
      "DMA", "Dominique", "ECU", "Ecuador", "EGY", "Egypt", "ESA", "El Salvador", "GEQ", "Equatorial Guinea", 
      "ERI", "Eritrea", "EST", "Estonia", "ETH", "Ethiopia", "FSM", "Federated States of Micronesia", "FIJ", "Fiji", 
      "FIN", "Finland", "FRA", "France", "FGT", "French Guiana", "FPO", "French Polynesia", "GAB", "Gabon", 
      "GAM", "Gambia", "GEO", "Georgia", "GER", "Germany", "GHA", "Ghana", "GBR", "Great Britain", 
      "GRE", "Greece", "GRN", "Grenada", "GDL", "Guadeloupe", "GUM", "Guam", "GUA", "Guatemala", 
      "GUI", "Guinea", "GBS", "Guinea-Bissau", "GUY", "Guyana", "HAI", "Haiti", "HON", "Honduras", 
      "HKG", "Hong-Kong, China", "HUN", "Hungary", "ISL", "Iceland", "IMN", "Isle of Man", "IND", "India", 
      "INA", "Indonesia", "IRQ", "Iraq", "IRL", "Ireland", "IRI", "Islamic Republic of Iran", "ISR", "Israel", 
      "ITA", "Italy", "JAM", "Jamaica", "JPN", "Japan", "JOR", "Jordan", "KAZ", "Kazakhstan", 
      "KEN", "Kenya", "KIR", "Kiribati", "KOS", "Kosovo", "KUW", "Kuwait", "KGZ", "Kyrgyzstan", 
      "LAO", "Lao People's Democratic Republic", "LAT", "Latvia", "LBN", "Lebanon", "LES", "Lesotho", "LBR", "Liberia", 
      "LBA", "Libya", "LIE", "Liechtenstein", "LTU", "Lithuania", "LUX", "Luxembourg", "MAD", "Madagascar", 
      "MAW", "Malawi", "MAS", "Malaysia", "MDV", "Maldives", "MLI", "Mali", "MLT", "Malta", 
      "MHL", "Marshall Islands", "MRN", "Martinique", "MTN", "Mauritania", "MRI", "Mauritius", "MEX", "Mexico", 
      "MON", "Monaco", "MGL", "Mongolia", "MNE", "Montenegro", "MAR", "Morocco", "MOZ", "Mozambique", 
      "MYA", "Myanmar", "NAM", "Namibia", "NRU", "Nauru", "NEP", "Nepal", "NED", "Netherlands", 
      "NCD", "New Caledonia", "NZL", "New Zealand", "NCA", "Nicaragua", "NIG", "Niger", "NGR", "Nigeria", 
      "NOR", "Norway", "OMA", "Oman", "PAK", "Pakistan", "PLW", "Palau", "PLE", "Palestine", 
      "PAN", "Panama", "PNG", "Papua New Guinea", "PAR", "Paraguay", "CHN", "People's Republic of China", "MAC", "Macau", 
      "PER", "Peru", "PHI", "Philippines", "POL", "Poland", "POR", "Portugal", "PUR", "Puerto Rico", 
      "QAT", "Qatar", "KOR", "Republic of Korea", "MDA", "Republic of Moldova", "ROU", "Romania", "RUS", "Russian Federation", 
      "RWA", "Rwanda", "SKN", "Saint Kitts and Nevis", "LCA", "Saint Lucia", "SAM", "Samoa", "SMR", "San Marino", 
      "STP", "Sao Tome and Principe", "KSA", "Saudi Arabia", "SEN", "Senegal", "SRB", "Serbia", "SEY", "Seychelles", 
      "SLE", "Sierra Leone", "SGP", "Singapore", "SVK", "Slovakia", "SLO", "Slovenia", "SOL", "Solomon Islands", 
      "SOM", "Somalia", "RSA", "South Africa", "SSD", "South Sudan", "ESP", "Spain", "SRI", "Sri Lanka", 
      "VIN", "Saint Vincent and the Grenadines", "SUD", "Sudan", "SUR", "Suriname", "SWZ", "Eswatini", "SWE", "Sweden", 
      "SUI", "Switzerland", "SYR", "Syrian Arab Republic", "TJK", "Tajikistan", "THA", "Thailand", "MKD", "The Former Yugoslav Republic of Macedonia", 
      "TLS", "Timor-Leste", "TOG", "Togo", "TGA", "Tonga", "TTO", "Trinidad and Tobago", "TUN", "Tunisia", 
      "TUR", "Turkey", "TKM", "Turkmenistan", "TUV", "Tuvalu", "UGA", "Uganda", "UKR", "Ukraine", 
      "UAE", "United Arab Emirates", "TAN", "United Republic of Tanzania", "USA", "United States of America", "URU", "Uruguay", "UZB", "Uzbekistan", 
      "VAN", "Vanuatu", "VEN", "Venezuela", "VIE", "Vietnam", "IVB", "Virgin Islands (British)", "ISV", "Virgin Islands (Us)", 
      "YEM", "Yemen", "ZAM", "Zambia", "ZIM", "Zimbabwe", "WT", "WORLD TAEKWONDO" };
  
  public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
    jdbcTemplate.execute("DELETE FROM TKS_FLAG");
    final String pathFlagsDirectory = TkStrikeBaseDirectoriesUtil.getInstance().getWorkBaseDir() + "flags/";
    File dirFlagsDirectory = new File(pathFlagsDirectory);
    FileUtils.deleteQuietly(dirFlagsDirectory);
    if (!dirFlagsDirectory.exists())
      FileUtils.forceMkdir(dirFlagsDirectory); 
    jdbcTemplate.execute("INSERT INTO TKS_FLAG (ID,VERSION,NAME,ABBREVIATION,FLAG_IMAGE_PATH) VALUES (?,?,?,?,?)", new PreparedStatementCallback<Object>() {
          public Object doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException {
            int cont = 1;
            for (int i = 0; i < V5_0_0__FlagsWTCompetitionDataProtocol.flags.length; i += 2) {
              try {
                String abbreviation = V5_0_0__FlagsWTCompetitionDataProtocol.flags[i];
                String flag = abbreviation + ".png";
                String country = V5_0_0__FlagsWTCompetitionDataProtocol.flags[i + 1];
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
                cont++;
              } catch (Exception e) {
                V5_0_0__FlagsWTCompetitionDataProtocol.logger.error("Exception processing " + V5_0_0__FlagsWTCompetitionDataProtocol.flags[i], e);
              } 
            } 
            return null;
          }
        });
  }
}
