package com.xtremis.daedo.tkstrike.service;

import au.com.bytecode.opencsv.CSVReader;
import com.xtremis.daedo.tkstrike.orm.model.Category;
import com.xtremis.daedo.tkstrike.orm.model.Gender;
import com.xtremis.daedo.tkstrike.orm.model.SubCategory;
import com.xtremis.daedo.tkstrike.tools.ei.om.AthleteDto;
import com.xtremis.daedo.tkstrike.utils.TkStrikeBaseDirectoriesUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TkStrikeCSVImporterImpl implements TkStrikeCSVImporter {
  private static final Logger importerLogger = Logger.getLogger("CSV_IMPORTER");
  
  @Value("${tkStrike.importFromCSVFiles}")
  private Boolean importFromCSVFiles;
  
  @Value("${tkStrike.importFromCSVFiles.deletePhases}")
  private Boolean deletePhases;
  
  @Value("${tkStrike.importFromCSVFiles.deleteContests}")
  private Boolean deleteContests;
  
  @Value("${tkStrike.importFromCSVFiles.deleteWeightDivisions}")
  private Boolean deleteWeightDivisions;
  
  @Value("${tkStrike.importFromCSVFiles.deleteAthletes}")
  private Boolean deleteAthletes;
  
  @Autowired
  private PhaseService phaseService;
  
  @Autowired
  private SubCategoryService subCategoryService;
  
  @Autowired
  private CategoryService categoryService;
  
  @Autowired
  private AthleteService athleteService;
  
  public Boolean isImportFromCSVEnabled() {
    return this.importFromCSVFiles;
  }
  
  public Boolean isDeletePhases() {
    return this.deletePhases;
  }
  
  public Boolean isDeleteContests() {
    return this.deleteContests;
  }
  
  public Boolean isDeleteWeightDivisions() {
    return this.deleteWeightDivisions;
  }
  
  public Boolean isDeleteAthletes() {
    return this.deleteAthletes;
  }
  
  public void tryToImportPhases() throws TkStrikeServiceException {
    if (isImportFromCSVEnabled().booleanValue()) {
      File phasesFile = new File(TkStrikeBaseDirectoriesUtil.getInstance().getWorkBaseDir() + "phases.csv");
      if (phasesFile.exists()) {
        if (this.deletePhases.booleanValue())
          this.phaseService.deleteAll(); 
        try {
          InputStreamReader isr = new InputStreamReader(new FileInputStream(phasesFile));
          CSVReader csvReader = new CSVReader(isr, ',', '"', '\\', 0, false, true);
          String[] line;
          while (null != (line = csvReader.readNext())) {
            if (line.length >= 1) {
              String phaseName = line[0];
              try {
                if (this.phaseService.getByName(phaseName) == null)
                  this.phaseService.createNew(phaseName, phaseName); 
              } catch (Exception e) {
                importerLogger.error("PHASES -- Error on line " + Arrays.toString((Object[])line) + "#" + e.getMessage());
              } 
            } 
          } 
        } catch (Exception e) {
          throw new TkStrikeServiceException(e);
        } finally {
          FileUtils.deleteQuietly(phasesFile);
        } 
      } 
    } 
  }
  
  public void tryToImportContests() throws TkStrikeServiceException {
    if (isImportFromCSVEnabled().booleanValue()) {
      File contestsFile = new File(TkStrikeBaseDirectoriesUtil.getInstance().getWorkBaseDir() + "contests.csv");
      if (contestsFile.exists()) {
        if (this.deleteContests.booleanValue()) {
          this.athleteService.deleteAll();
          this.categoryService.deleteAll();
          this.subCategoryService.deleteAll();
        } 
        try {
          InputStreamReader isr = new InputStreamReader(new FileInputStream(contestsFile));
          CSVReader csvReader = new CSVReader(isr, ',', '"', '\\', 0, false, true);
          String[] line;
          while (null != (line = csvReader.readNext())) {
            if (line.length >= 1)
              try {
                String subCategoryName = line[0];
                if (this.subCategoryService.getByName(subCategoryName) == null)
                  this.subCategoryService.createNew(subCategoryName); 
              } catch (Exception e) {
                importerLogger.error("CONTESTS -- Error on line " + Arrays.toString((Object[])line) + "#" + e.getMessage());
              }  
          } 
        } catch (Exception e) {
          throw new TkStrikeServiceException(e);
        } finally {
          FileUtils.deleteQuietly(contestsFile);
        } 
      } 
    } 
  }
  
  public void tryToImportWeightDivisions() throws TkStrikeServiceException {
    if (isImportFromCSVEnabled().booleanValue()) {
      File weightDivisionsFile = new File(TkStrikeBaseDirectoriesUtil.getInstance().getWorkBaseDir() + "weightDivisions.csv");
      if (weightDivisionsFile.exists()) {
        if (this.deleteWeightDivisions.booleanValue()) {
          this.athleteService.deleteAll();
          this.categoryService.deleteAll();
        } 
        try {
          InputStreamReader isr = new InputStreamReader(new FileInputStream(weightDivisionsFile));
          CSVReader csvReader = new CSVReader(isr, ',', '"', '\\', 0, false, true);
          String[] line;
          while (null != (line = csvReader.readNext())) {
            if (line.length >= 5)
              try {
                String categoryName = line[0];
                Gender gender = Gender.valueOf(line[1]);
                String subCategoryName = line[2];
                Integer bodyLevel = Integer.valueOf(Integer.parseInt(line[3]));
                Integer headLevel = Integer.valueOf(Integer.parseInt(line[4]));
                Category category = this.categoryService.getBySC_G_N(subCategoryName, gender, categoryName);
                if (category != null) {
                  category.setBodyLevel(bodyLevel);
                  category.setHeadLevel(headLevel);
                  this.categoryService.update(category.getId(), categoryName, category.getSubCategory(), category.getGender(), bodyLevel, headLevel);
                  continue;
                } 
                SubCategory subCategory = this.subCategoryService.getByName(subCategoryName);
                if (subCategory != null)
                  this.categoryService.createNew(categoryName, subCategory, gender, bodyLevel, headLevel); 
              } catch (Exception e) {
                importerLogger.error("WEIGHT DIVISION -- Error on line " + Arrays.toString((Object[])line) + "#" + e.getMessage());
              }  
          } 
        } catch (Exception e) {
          throw new TkStrikeServiceException(e);
        } finally {
          FileUtils.deleteQuietly(weightDivisionsFile);
        } 
      } 
    } 
  }
  
  public void tryToImportAthletes() throws TkStrikeServiceException {
    if (isImportFromCSVEnabled().booleanValue()) {
      File athletesFile = new File(TkStrikeBaseDirectoriesUtil.getInstance().getWorkBaseDir() + "athletes.csv");
      if (athletesFile.exists()) {
        if (this.deleteAthletes.booleanValue())
          this.athleteService.deleteAll(); 
        try {
          InputStreamReader isr = new InputStreamReader(new FileInputStream(athletesFile));
          CSVReader csvReader = new CSVReader(isr, ',', '"', '\\', 0, false, true);
          String[] line;
          while (null != (line = csvReader.readNext())) {
            if (line.length >= 3)
              try {
                String athleteName = line[0];
                String wfId = line[1];
                String flagAbbreviation = line[2];
                AthleteDto dto = new AthleteDto();
                dto.setScoreboardName(athleteName);
                dto.setWfId(wfId);
                dto.setFlagAbbreviation(flagAbbreviation);
                this.athleteService.doGetCreateOrUpdateEntity(dto);
              } catch (Exception e) {
                importerLogger.error("ATHLETE -- Error on line " + Arrays.toString((Object[])line) + "#" + e.getMessage());
              }  
          } 
        } catch (Exception e) {
          throw new TkStrikeServiceException(e);
        } finally {
          FileUtils.deleteQuietly(athletesFile);
        } 
      } 
    } 
  }
}
