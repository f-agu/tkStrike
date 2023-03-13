package com.xtremis.daedo.tkstrike.ui.model;

import com.xtremis.daedo.tkstrike.orm.model.Athlete;
import com.xtremis.daedo.tkstrike.orm.model.Entity;
import com.xtremis.daedo.tkstrike.tools.ei.om.AthleteDto;
import com.xtremis.daedo.tkstrike.ui.scene.FlagEntry;
import com.xtremis.daedo.wtdata.model.constants.CompetitorType;
import com.xtremis.daedo.wtdata.model.constants.Gender;
import java.util.Date;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import org.springframework.beans.BeanUtils;

public class AthleteEntry implements Entry<Athlete> {
  public SimpleStringProperty id = new SimpleStringProperty(this, "id");
  
  public SimpleStringProperty scoreboardName = new SimpleStringProperty(this, "scoreboardName");
  
  public SimpleStringProperty wfId = new SimpleStringProperty(this, "wfId");
  
  public SimpleStringProperty ovrInternalId = new SimpleStringProperty(this, "ovrInternalId");
  
  public SimpleObjectProperty<FlagEntry> flag = new SimpleObjectProperty(this, "flag", new FlagEntry());
  
  public SimpleStringProperty givenName = new SimpleStringProperty(this, "givenName");
  
  public SimpleStringProperty familyName = new SimpleStringProperty(this, "familyName");
  
  public SimpleStringProperty passportGivenName = new SimpleStringProperty(this, "passportGivenName");
  
  public SimpleStringProperty passportFamilyName = new SimpleStringProperty(this, "passportFamilyName");
  
  public SimpleStringProperty preferredGivenName = new SimpleStringProperty(this, "preferredGivenName");
  
  public SimpleStringProperty preferredFamilyName = new SimpleStringProperty(this, "preferredFamilyName");
  
  public SimpleStringProperty printName = new SimpleStringProperty(this, "printName");
  
  public SimpleStringProperty printInitialName = new SimpleStringProperty(this, "printInitialName");
  
  public SimpleStringProperty tvName = new SimpleStringProperty(this, "tvName");
  
  public SimpleStringProperty tvInitialName = new SimpleStringProperty(this, "tvInitialName");
  
  public SimpleObjectProperty<Gender> gender = new SimpleObjectProperty(this, "gender");
  
  public SimpleObjectProperty<Date> birthDate = new SimpleObjectProperty(this, "birthDate");
  
  public SimpleObjectProperty<CompetitorType> competitorType = new SimpleObjectProperty(this, "competitorType");
  
  public SimpleIntegerProperty rank = new SimpleIntegerProperty(this, "rank");
  
  public SimpleIntegerProperty seed = new SimpleIntegerProperty(this, "seed");
  
  public void fillByEntity(Athlete athlete) {
    if (athlete != null) {
      this.id.set(athlete.getId());
      this.scoreboardName.set(athlete.getScoreboardName());
      this.wfId.set(athlete.getWfId());
      this.ovrInternalId.set(athlete.getOvrInternalId());
      if (athlete.getFlag() != null) {
        FlagEntry flagEntry = new FlagEntry();
        flagEntry.fillByEntity(athlete.getFlag());
        this.flag.set(flagEntry);
      } 
      BeanUtils.copyProperties(athlete, this, new String[] { "id", "scoreboardName", "wfId", "ovrInternalId", "flag", "rank", "seed" });
      if (athlete.getRank() != null)
        this.rank.setValue(athlete.getRank()); 
      if (athlete.getSeed() != null)
        this.seed.setValue(athlete.getSeed()); 
    } 
  }
  
  public AthleteDto getAthleteDto() {
    AthleteDto res = new AthleteDto();
    res.setScoreboardName(getScoreboardName());
    res.setWfId(getWfId());
    res.setOvrInternalId(getOvrInternalId());
    if (getFlag() != null)
      res.setFlagAbbreviation(getFlag().getAbbreviation()); 
    BeanUtils.copyProperties(this, res, new String[] { "id", "scoreboardName", "wfId", "ovrInternalId", "flag", "gender", "competitorType" });
    if (getGender() != null)
      res.setGender(getGender().toString()); 
    if (getCompetitorType() != null)
      res.setCompetitorType(((CompetitorType)this.competitorType.get()).toString()); 
    return res;
  }
  
  public String getId() {
    return this.id.get();
  }
  
  public String getScoreboardName() {
    return this.scoreboardName.get();
  }
  
  public String getWfId() {
    return this.wfId.get();
  }
  
  public String getOvrInternalId() {
    return this.ovrInternalId.get();
  }
  
  public FlagEntry getFlag() {
    return (FlagEntry)this.flag.get();
  }
  
  public SimpleStringProperty idProperty() {
    return this.id;
  }
  
  public void setId(String id) {
    this.id.set(id);
  }
  
  public SimpleStringProperty scoreboardNameProperty() {
    return this.scoreboardName;
  }
  
  public void setScoreboardName(String scoreboardName) {
    this.scoreboardName.set(scoreboardName);
  }
  
  public SimpleStringProperty wfIdProperty() {
    return this.wfId;
  }
  
  public void setWfId(String wfId) {
    this.wfId.set(wfId);
  }
  
  public SimpleStringProperty ovrInternalIdProperty() {
    return this.ovrInternalId;
  }
  
  public void setOvrInternalId(String ovrInternalId) {
    this.ovrInternalId.set(ovrInternalId);
  }
  
  public SimpleObjectProperty<FlagEntry> flagProperty() {
    return this.flag;
  }
  
  public void setFlag(FlagEntry flag) {
    this.flag.set(flag);
  }
  
  public String getGivenName() {
    return this.givenName.get();
  }
  
  public SimpleStringProperty givenNameProperty() {
    return this.givenName;
  }
  
  public void setGivenName(String givenName) {
    this.givenName.set(givenName);
  }
  
  public String getFamilyName() {
    return this.familyName.get();
  }
  
  public SimpleStringProperty familyNameProperty() {
    return this.familyName;
  }
  
  public void setFamilyName(String familyName) {
    this.familyName.set(familyName);
  }
  
  public String getPassportGivenName() {
    return this.passportGivenName.get();
  }
  
  public SimpleStringProperty passportGivenNameProperty() {
    return this.passportGivenName;
  }
  
  public void setPassportGivenName(String passportGivenName) {
    this.passportGivenName.set(passportGivenName);
  }
  
  public String getPassportFamilyName() {
    return this.passportFamilyName.get();
  }
  
  public SimpleStringProperty passportFamilyNameProperty() {
    return this.passportFamilyName;
  }
  
  public void setPassportFamilyName(String passportFamilyName) {
    this.passportFamilyName.set(passportFamilyName);
  }
  
  public String getPreferredGivenName() {
    return this.preferredGivenName.get();
  }
  
  public SimpleStringProperty preferredGivenNameProperty() {
    return this.preferredGivenName;
  }
  
  public void setPreferredGivenName(String preferredGivenName) {
    this.preferredGivenName.set(preferredGivenName);
  }
  
  public String getPreferredFamilyName() {
    return this.preferredFamilyName.get();
  }
  
  public SimpleStringProperty preferredFamilyNameProperty() {
    return this.preferredFamilyName;
  }
  
  public void setPreferredFamilyName(String preferredFamilyName) {
    this.preferredFamilyName.set(preferredFamilyName);
  }
  
  public String getPrintName() {
    return this.printName.get();
  }
  
  public SimpleStringProperty printNameProperty() {
    return this.printName;
  }
  
  public void setPrintName(String printName) {
    this.printName.set(printName);
  }
  
  public String getPrintInitialName() {
    return this.printInitialName.get();
  }
  
  public SimpleStringProperty printInitialNameProperty() {
    return this.printInitialName;
  }
  
  public void setPrintInitialName(String printInitialName) {
    this.printInitialName.set(printInitialName);
  }
  
  public String getTvName() {
    return this.tvName.get();
  }
  
  public SimpleStringProperty tvNameProperty() {
    return this.tvName;
  }
  
  public void setTvName(String tvName) {
    this.tvName.set(tvName);
  }
  
  public String getTvInitialName() {
    return this.tvInitialName.get();
  }
  
  public SimpleStringProperty tvInitialNameProperty() {
    return this.tvInitialName;
  }
  
  public void setTvInitialName(String tvInitialName) {
    this.tvInitialName.set(tvInitialName);
  }
  
  public Gender getGender() {
    return (Gender)this.gender.get();
  }
  
  public SimpleObjectProperty<Gender> genderProperty() {
    return this.gender;
  }
  
  public void setGender(Gender gender) {
    this.gender.set(gender);
  }
  
  public Date getBirthDate() {
    return (Date)this.birthDate.get();
  }
  
  public SimpleObjectProperty<Date> birthDateProperty() {
    return this.birthDate;
  }
  
  public void setBirthDate(Date birthDate) {
    this.birthDate.set(birthDate);
  }
  
  public CompetitorType getCompetitorType() {
    return (CompetitorType)this.competitorType.get();
  }
  
  public SimpleObjectProperty<CompetitorType> competitorTypeProperty() {
    return this.competitorType;
  }
  
  public void setCompetitorType(CompetitorType competitorType) {
    this.competitorType.set(competitorType);
  }
  
  public int getRank() {
    return this.rank.get();
  }
  
  public SimpleIntegerProperty rankProperty() {
    return this.rank;
  }
  
  public void setRank(int rank) {
    this.rank.set(rank);
  }
  
  public int getSeed() {
    return this.seed.get();
  }
  
  public SimpleIntegerProperty seedProperty() {
    return this.seed;
  }
  
  public void setSeed(int seed) {
    this.seed.set(seed);
  }
  
  public String toString() {
    return getScoreboardName();
  }
}
