package com.xtremis.daedo.tkstrike.ui.model;

import com.xtremis.daedo.tkstrike.orm.model.Athlete;
import com.xtremis.daedo.tkstrike.orm.model.Category;
import com.xtremis.daedo.tkstrike.orm.model.Entity;
import com.xtremis.daedo.tkstrike.orm.model.Gender;
import com.xtremis.daedo.tkstrike.orm.model.MatchConfiguration;
import com.xtremis.daedo.tkstrike.orm.model.MatchConfigurationEntity;
import com.xtremis.daedo.tkstrike.orm.model.Phase;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchConfigurationDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchVictoryCriteria;
import com.xtremis.daedo.tkstrike.ui.scene.RefereeEntry;
import java.util.Date;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class MatchConfigurationEntry implements IMatchConfigurationEntry<MatchConfiguration, RoundsConfigEntry, MatchConfigurationDto> {
  private SimpleStringProperty id = new SimpleStringProperty();
  
  private SimpleStringProperty sensorsGroup = new SimpleStringProperty();
  
  private SimpleStringProperty matchNumber = new SimpleStringProperty();
  
  private SimpleStringProperty vmMatchInternalId = new SimpleStringProperty(this, "vmMatchInternalId", null);
  
  private SimpleObjectProperty<PhaseEntry> phase = new SimpleObjectProperty();
  
  private SimpleObjectProperty<SubCategoryEntry> subCategory = new SimpleObjectProperty(this, "subCategory", new SubCategoryEntry());
  
  private SimpleObjectProperty<Gender> gender = new SimpleObjectProperty(this, "gender", Gender.MALE);
  
  private SimpleObjectProperty<CategoryEntry> category = new SimpleObjectProperty();
  
  private SimpleObjectProperty<AthleteEntry> blueAthlete = new SimpleObjectProperty();
  
  private SimpleIntegerProperty blueAthleteVideoQuota = new SimpleIntegerProperty(this, "blueAthleteVideoQuota", 0);
  
  private SimpleBooleanProperty blueAthleteVideoEnabled = new SimpleBooleanProperty();
  
  private SimpleObjectProperty<AthleteEntry> redAthlete = new SimpleObjectProperty();
  
  private SimpleIntegerProperty redAthleteVideoQuota = new SimpleIntegerProperty(this, "redAthleteVideoQuota", 0);
  
  private SimpleBooleanProperty redAthleteVideoEnabled = new SimpleBooleanProperty();
  
  private SimpleObjectProperty<RoundsConfigEntry> roundsConfig = new SimpleObjectProperty(this, "roundsConfig", new RoundsConfigEntry());
  
  private SimpleBooleanProperty isParaTkdMatch = new SimpleBooleanProperty(this, "isParaTkdMatch", Boolean.FALSE.booleanValue());
  
  private SimpleIntegerProperty differencialScore = new SimpleIntegerProperty(this, "differencialScore");
  
  private SimpleIntegerProperty maxAllowedGamJeoms = new SimpleIntegerProperty(this, "maxAllowedGamJeoms", 0);
  
  private SimpleBooleanProperty isWtCompetitionDataProtocol = new SimpleBooleanProperty(this, "isWtCompetitionDataProtocol", Boolean.FALSE.booleanValue());
  
  private SimpleObjectProperty<RefereeEntry> refereeCR = new SimpleObjectProperty(this, "refereeCR", null);
  
  private SimpleObjectProperty<RefereeEntry> refereeJ1 = new SimpleObjectProperty(this, "refereeJ1", null);
  
  private SimpleObjectProperty<RefereeEntry> refereeJ2 = new SimpleObjectProperty(this, "refereeJ2", null);
  
  private SimpleObjectProperty<RefereeEntry> refereeJ3 = new SimpleObjectProperty(this, "refereeJ3", null);
  
  private SimpleObjectProperty<RefereeEntry> refereeTA = new SimpleObjectProperty(this, "refereeTA", null);
  
  private SimpleObjectProperty<RefereeEntry> refereeRJ = new SimpleObjectProperty(this, "refereeRJ", null);
  
  private SimpleObjectProperty<Date> createdDatetime = new SimpleObjectProperty(this, "createdDatetime");
  
  private SimpleObjectProperty<Date> lastUpdateDatetime = new SimpleObjectProperty(this, "lastUpdateDatetime");
  
  private SimpleBooleanProperty matchStarted = new SimpleBooleanProperty(this, "matchStarted");
  
  private SimpleObjectProperty<MatchVictoryCriteria> matchVictoryCriteria = new SimpleObjectProperty(this, "matchVictoryCriteria", MatchVictoryCriteria.CONVENTIONAL);
  
  public MatchConfigurationEntry() {
    this.phase.set(new PhaseEntry());
    this.category.set(new CategoryEntry());
    this.blueAthlete.set(new AthleteEntry());
    this.redAthlete.set(new AthleteEntry());
    this.roundsConfig.set(new RoundsConfigEntry());
    this.blueAthleteVideoQuota.addListener(new ChangeListener<Number>() {
          public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newQuota) {
            if (newQuota.intValue() > 0) {
              MatchConfigurationEntry.this.blueAthleteVideoEnabled.set(Boolean.TRUE.booleanValue());
            } else {
              MatchConfigurationEntry.this.blueAthleteVideoEnabled.set(Boolean.FALSE.booleanValue());
            } 
          }
        });
    this.redAthleteVideoQuota.addListener(new ChangeListener<Number>() {
          public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newQuota) {
            if (newQuota.intValue() > 0) {
              MatchConfigurationEntry.this.redAthleteVideoEnabled.set(Boolean.TRUE.booleanValue());
            } else {
              MatchConfigurationEntry.this.redAthleteVideoEnabled.set(Boolean.FALSE.booleanValue());
            } 
          }
        });
  }
  
  public boolean isReadyForStart() {
    return (this.blueAthlete != null && this.blueAthlete.get() != null && ((AthleteEntry)this.blueAthlete.get()).getId() != null && this.redAthlete != null && this.redAthlete
      .get() != null && ((AthleteEntry)this.redAthlete.get()).getId() != null);
  }
  
  public void fillByEntity(MatchConfiguration entity) {
    if (entity != null) {
      this.id.set(entity.getId());
      if (entity.getSensorsGroup() != null)
        this.sensorsGroup.set(entity.getSensorsGroup().toString()); 
      if (entity.getMatchNumber() != null)
        this.matchNumber.set(entity.getMatchNumber()); 
      if (entity.getVmMatchInternalId() != null)
        this.vmMatchInternalId.set(entity.getVmMatchInternalId()); 
      Phase phase = entity.getPhase();
      if (phase != null) {
        PhaseEntry phaseEntry = new PhaseEntry();
        phaseEntry.fillByEntity(phase);
        this.phase.set(phaseEntry);
      } else {
        this.phase = new SimpleObjectProperty();
      } 
      Category category = entity.getCategory();
      if (category != null) {
        CategoryEntry categoryEntry = new CategoryEntry();
        categoryEntry.fillByEntity(category);
        this.category.set(categoryEntry);
        this.subCategory.set(categoryEntry.getSubCategory());
        this.gender.set(categoryEntry.getGender());
      } else {
        this.category = new SimpleObjectProperty();
      } 
      Athlete blueAthlete = entity.getBlueAthlete();
      AthleteEntry blueAthleteEntry = new AthleteEntry();
      if (blueAthlete != null)
        blueAthleteEntry.fillByEntity(blueAthlete); 
      this.blueAthlete.set(blueAthleteEntry);
      this.blueAthleteVideoQuota.set(entity.getBlueAthleteVideoQuota().intValue());
      Athlete redAthlete = entity.getRedAthlete();
      if (redAthlete != null) {
        AthleteEntry athleteEntry = new AthleteEntry();
        athleteEntry.fillByEntity(redAthlete);
        this.redAthlete.set(athleteEntry);
      } else {
        this.redAthlete = new SimpleObjectProperty();
      } 
      this.redAthleteVideoQuota.set(entity.getRedAthleteVideoQuota().intValue());
      RoundsConfigEntry roundsConfigEntry = new RoundsConfigEntry();
      if (entity.getRoundsConfig() != null)
        roundsConfigEntry.fillByEntity(entity.getRoundsConfig()); 
      this.roundsConfig.set(roundsConfigEntry);
      this.isParaTkdMatch.set(entity.getParaTkdMatch().booleanValue());
      this.differencialScore.set(entity.getDifferencialScore().intValue());
      this.maxAllowedGamJeoms.set(entity.getMaxAllowedGamJeoms().intValue());
      this.isWtCompetitionDataProtocol.set(entity.getWtCompetitionDataProtocol().booleanValue());
      if (entity.getRefereeCR() != null) {
        RefereeEntry refereeEntry = new RefereeEntry();
        refereeEntry.fillByEntity(entity.getRefereeCR());
        this.refereeCR.set(refereeEntry);
      } else {
        this.refereeCR.set(null);
      } 
      if (entity.getRefereeJ1() != null) {
        RefereeEntry refereeEntry = new RefereeEntry();
        refereeEntry.fillByEntity(entity.getRefereeJ1());
        this.refereeJ1.set(refereeEntry);
      } else {
        this.refereeJ1.set(null);
      } 
      if (entity.getRefereeJ2() != null) {
        RefereeEntry refereeEntry = new RefereeEntry();
        refereeEntry.fillByEntity(entity.getRefereeJ2());
        this.refereeJ2.set(refereeEntry);
      } else {
        this.refereeJ2.set(null);
      } 
      if (entity.getRefereeJ3() != null) {
        RefereeEntry refereeEntry = new RefereeEntry();
        refereeEntry.fillByEntity(entity.getRefereeJ3());
        this.refereeJ3.set(refereeEntry);
      } else {
        this.refereeJ3.set(null);
      } 
      if (entity.getRefereeTA() != null) {
        RefereeEntry refereeEntry = new RefereeEntry();
        refereeEntry.fillByEntity(entity.getRefereeTA());
        this.refereeTA.set(refereeEntry);
      } else {
        this.refereeTA.set(null);
      } 
      if (entity.getRefereeRJ() != null) {
        RefereeEntry refereeEntry = new RefereeEntry();
        refereeEntry.fillByEntity(entity.getRefereeRJ());
        this.refereeRJ.set(refereeEntry);
      } else {
        this.refereeRJ.set(null);
      } 
      this.createdDatetime.set(entity.getCreatedDatetime());
      this.lastUpdateDatetime.set(entity.getLastUpdateDatetime());
      this.matchStarted.set(entity.getMatchStarted().booleanValue());
      this.matchVictoryCriteria.set(entity.getMatchVictoryCriteria());
    } 
  }
  
  public MatchConfigurationDto getMatchConfigurationDto() {
    if (getId() != null && getMatchNumber() != null) {
      MatchConfigurationDto res = new MatchConfigurationDto();
      res.setInternalId(getVmMatchInternalId());
      res.setPhase(getPhase().getName());
      res.setMatchNumber(getMatchNumber());
      res.setCategory((getCategory() != null) ? getCategory().getCategoryDto() : null);
      res.setBlueAthlete((getBlueAthlete() != null) ? getBlueAthlete().getAthleteDto() : null);
      res.setBlueAthleteVideoQuota(Integer.valueOf(getBlueAthleteVideoQuota()));
      res.setRedAthlete((getRedAthlete() != null) ? getRedAthlete().getAthleteDto() : null);
      res.setRedAthleteVideoQuota(Integer.valueOf(getRedAthleteVideoQuota()));
      res.setRoundsConfig((getRoundsConfig() != null) ? getRoundsConfig().getRoundsConfigDto() : null);
      res.setParaTkdMatch(Boolean.valueOf(isParaTkdMatch()));
      res.setDifferencialScore(getDifferencialScore());
      res.setMaxAllowedGamJeoms(getMaxAllowedGamJeoms());
      res.setWtCompetitionDataProtocol(getWtCompetitionDataProtocol());
      res.setRefereeRJ((this.refereeRJ.get() != null) ? ((RefereeEntry)this.refereeRJ.getValue()).getRefereeDto() : null);
      res.setRefereeJ1((this.refereeJ1.get() != null) ? ((RefereeEntry)this.refereeJ1.getValue()).getRefereeDto() : null);
      res.setRefereeJ2((this.refereeJ2.get() != null) ? ((RefereeEntry)this.refereeJ2.getValue()).getRefereeDto() : null);
      res.setRefereeJ3((this.refereeJ3.get() != null) ? ((RefereeEntry)this.refereeJ3.getValue()).getRefereeDto() : null);
      res.setRefereeTA((this.refereeTA.get() != null) ? ((RefereeEntry)this.refereeTA.getValue()).getRefereeDto() : null);
      res.setRefereeCR((this.refereeCR.get() != null) ? ((RefereeEntry)this.refereeCR.getValue()).getRefereeDto() : null);
      res.setMatchVictoryCriteria(getMatchVictoryCriteria());
      return res;
    } 
    return null;
  }
  
  public String getId() {
    return this.id.get();
  }
  
  public SimpleStringProperty idProperty() {
    return this.id;
  }
  
  public String getSensorsGroup() {
    return this.sensorsGroup.get();
  }
  
  public SimpleStringProperty sensorsGroupProperty() {
    return this.sensorsGroup;
  }
  
  public String getMatchNumber() {
    return this.matchNumber.get();
  }
  
  public SimpleStringProperty matchNumberProperty() {
    return this.matchNumber;
  }
  
  public String getVmMatchInternalId() {
    return this.vmMatchInternalId.get();
  }
  
  public SimpleStringProperty vmMatchInternalIdProperty() {
    return this.vmMatchInternalId;
  }
  
  public String getBlueAthleteOvrInternalId() {
    return (this.blueAthlete != null) ? ((AthleteEntry)this.blueAthlete.get()).getOvrInternalId() : null;
  }
  
  public SimpleStringProperty blueAthleteOvrInternalIdProperty() {
    return (this.blueAthlete != null) ? ((AthleteEntry)this.blueAthlete.get()).ovrInternalId : null;
  }
  
  public String getRedAthleteOvrInternalId() {
    return (this.redAthlete != null) ? ((AthleteEntry)this.redAthlete.get()).getOvrInternalId() : null;
  }
  
  public SimpleStringProperty redAthleteOvrInternalIdProperty() {
    return (this.redAthlete != null) ? ((AthleteEntry)this.redAthlete.get()).ovrInternalId : null;
  }
  
  public PhaseEntry getPhase() {
    return (PhaseEntry)this.phase.get();
  }
  
  public SimpleObjectProperty<PhaseEntry> phaseProperty() {
    return this.phase;
  }
  
  public CategoryEntry getCategory() {
    return (CategoryEntry)this.category.get();
  }
  
  public SimpleObjectProperty<CategoryEntry> categoryProperty() {
    return this.category;
  }
  
  public AthleteEntry getBlueAthlete() {
    return (AthleteEntry)this.blueAthlete.get();
  }
  
  public SimpleObjectProperty<AthleteEntry> blueAthleteProperty() {
    return this.blueAthlete;
  }
  
  public boolean getBlueAthleteVideoEnabled() {
    return this.blueAthleteVideoEnabled.get();
  }
  
  public ReadOnlyBooleanProperty blueAthleteVideoEnabledProperty() {
    return (ReadOnlyBooleanProperty)this.blueAthleteVideoEnabled;
  }
  
  public int getBlueAthleteVideoQuota() {
    return this.blueAthleteVideoQuota.get();
  }
  
  public SimpleIntegerProperty blueAthleteVideoQuotaProperty() {
    return this.blueAthleteVideoQuota;
  }
  
  public AthleteEntry getRedAthlete() {
    return (AthleteEntry)this.redAthlete.get();
  }
  
  public SimpleObjectProperty<AthleteEntry> redAthleteProperty() {
    return this.redAthlete;
  }
  
  public boolean getRedAthleteVideoEnabled() {
    return this.redAthleteVideoEnabled.get();
  }
  
  public ReadOnlyBooleanProperty redAthleteVideoEnabledProperty() {
    return (ReadOnlyBooleanProperty)this.redAthleteVideoEnabled;
  }
  
  public int getRedAthleteVideoQuota() {
    return this.redAthleteVideoQuota.get();
  }
  
  public SimpleIntegerProperty redAthleteVideoQuotaProperty() {
    return this.redAthleteVideoQuota;
  }
  
  public RoundsConfigEntry getRoundsConfig() {
    return (RoundsConfigEntry)this.roundsConfig.get();
  }
  
  public SimpleObjectProperty<RoundsConfigEntry> roundsConfigProperty() {
    return this.roundsConfig;
  }
  
  public SubCategoryEntry getSubCategory() {
    return (SubCategoryEntry)this.subCategory.get();
  }
  
  public SimpleObjectProperty<SubCategoryEntry> subCategoryProperty() {
    return this.subCategory;
  }
  
  public Gender getGender() {
    return (Gender)this.gender.get();
  }
  
  public SimpleObjectProperty<Gender> genderProperty() {
    return this.gender;
  }
  
  public boolean isParaTkdMatch() {
    return this.isParaTkdMatch.get();
  }
  
  public SimpleBooleanProperty isParaTkdMatchProperty() {
    return this.isParaTkdMatch;
  }
  
  public void setIsParaTkdMatch(boolean isParaTkdMatch) {
    this.isParaTkdMatch.set(isParaTkdMatch);
  }
  
  public Integer getMaxAllowedGamJeoms() {
    return Integer.valueOf(this.maxAllowedGamJeoms.get());
  }
  
  public SimpleIntegerProperty maxAllowedGamJeomsProperty() {
    return this.maxAllowedGamJeoms;
  }
  
  public void setMaxAllowedGamJeoms(int maxAllowedGamJeoms) {
    this.maxAllowedGamJeoms.set(maxAllowedGamJeoms);
  }
  
  public SimpleIntegerProperty differencialScoreProperty() {
    return this.differencialScore;
  }
  
  public Integer getDifferencialScore() {
    return Integer.valueOf(this.differencialScore.get());
  }
  
  public void setWtCompetitionDataProtocol(boolean wtCompetitionDataProtocol) {
    this.isWtCompetitionDataProtocol.set(wtCompetitionDataProtocol);
  }
  
  public Boolean getWtCompetitionDataProtocol() {
    return Boolean.valueOf(this.isWtCompetitionDataProtocol.get());
  }
  
  public SimpleBooleanProperty wtCompetitionDataProtocolProperty() {
    return this.isWtCompetitionDataProtocol;
  }
  
  public RefereeEntry getRefereeCR() {
    return (RefereeEntry)this.refereeCR.get();
  }
  
  public SimpleObjectProperty<RefereeEntry> refereeCRProperty() {
    return this.refereeCR;
  }
  
  public void setRefereeCR(RefereeEntry refereeCR) {
    this.refereeCR.set(refereeCR);
  }
  
  public RefereeEntry getRefereeJ1() {
    return (RefereeEntry)this.refereeJ1.get();
  }
  
  public SimpleObjectProperty<RefereeEntry> refereeJ1Property() {
    return this.refereeJ1;
  }
  
  public void setRefereeJ1(RefereeEntry refereeJ1) {
    this.refereeJ1.set(refereeJ1);
  }
  
  public RefereeEntry getRefereeJ2() {
    return (RefereeEntry)this.refereeJ2.get();
  }
  
  public SimpleObjectProperty<RefereeEntry> refereeJ2Property() {
    return this.refereeJ2;
  }
  
  public void setRefereeJ2(RefereeEntry refereeJ2) {
    this.refereeJ2.set(refereeJ2);
  }
  
  public RefereeEntry getRefereeJ3() {
    return (RefereeEntry)this.refereeJ3.get();
  }
  
  public SimpleObjectProperty<RefereeEntry> refereeJ3Property() {
    return this.refereeJ3;
  }
  
  public void setRefereeJ3(RefereeEntry refereeJ3) {
    this.refereeJ3.set(refereeJ3);
  }
  
  public RefereeEntry getRefereeTA() {
    return (RefereeEntry)this.refereeTA.get();
  }
  
  public SimpleObjectProperty<RefereeEntry> refereeTAProperty() {
    return this.refereeTA;
  }
  
  public void setRefereeTA(RefereeEntry refereeTA) {
    this.refereeTA.set(refereeTA);
  }
  
  public RefereeEntry getRefereeRJ() {
    return (RefereeEntry)this.refereeRJ.get();
  }
  
  public SimpleObjectProperty<RefereeEntry> refereeRJProperty() {
    return this.refereeRJ;
  }
  
  public void setRefereeRJ(RefereeEntry refereeRJ) {
    this.refereeRJ.set(refereeRJ);
  }
  
  public Date getCreatedDatetime() {
    return (Date)this.createdDatetime.get();
  }
  
  public SimpleObjectProperty<Date> createdDatetimeProperty() {
    return this.createdDatetime;
  }
  
  public void setCreatedDatetime(Date createdDatetime) {
    this.createdDatetime.set(createdDatetime);
  }
  
  public Date getLastUpdateDatetime() {
    return (Date)this.lastUpdateDatetime.get();
  }
  
  public SimpleObjectProperty<Date> lastUpdateDatetimeProperty() {
    return this.lastUpdateDatetime;
  }
  
  public void setLastUpdateDatetime(Date lastUpdateDatetime) {
    this.lastUpdateDatetime.set(lastUpdateDatetime);
  }
  
  public boolean isMatchStarted() {
    return this.matchStarted.get();
  }
  
  public SimpleBooleanProperty matchStartedProperty() {
    return this.matchStarted;
  }
  
  public void setMatchStarted(boolean matchStarted) {
    this.matchStarted.set(matchStarted);
  }
  
  public MatchVictoryCriteria getMatchVictoryCriteria() {
    return (MatchVictoryCriteria)this.matchVictoryCriteria.get();
  }
  
  public SimpleObjectProperty<MatchVictoryCriteria> matchVictoryCriteriaProperty() {
    return this.matchVictoryCriteria;
  }
  
  public void setMatchVictoryCriteria(MatchVictoryCriteria matchVictoryCriteria) {
    this.matchVictoryCriteria.set(matchVictoryCriteria);
  }
}
