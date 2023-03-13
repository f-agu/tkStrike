package com.xtremis.daedo.tkstrike.tools.ei.om;

import com.xtremis.daedo.tkstrike.tools.om.BaseJSONDto;

public class MatchConfigurationDto extends BaseJSONDto {
  private static final long serialVersionUID = 791526066782791924L;
  
  private String internalId;
  
  private String ringNumber;
  
  private String matchNumber;
  
  private String phase;
  
  private CategoryDto category;
  
  private AthleteDto blueAthlete;
  
  private Integer blueAthleteVideoQuota;
  
  private AthleteDto redAthlete;
  
  private Integer redAthleteVideoQuota;
  
  private RoundsConfigDto roundsConfig;
  
  private Boolean isParaTkdMatch;
  
  private Integer differencialScore;
  
  private Integer maxAllowedGamJeoms;
  
  private Boolean isWtCompetitionDataProtocol = Boolean.FALSE;
  
  private MatchVictoryCriteria matchVictoryCriteria = MatchVictoryCriteria.CONVENTIONAL;
  
  private RefereeDto refereeCR;
  
  private RefereeDto refereeJ1;
  
  private RefereeDto refereeJ2;
  
  private RefereeDto refereeJ3;
  
  private RefereeDto refereeTA;
  
  private RefereeDto refereeRJ;
  
  public String getInternalId() {
    return this.internalId;
  }
  
  public void setInternalId(String internalId) {
    this.internalId = internalId;
  }
  
  public String getRingNumber() {
    return this.ringNumber;
  }
  
  public void setRingNumber(String ringNumber) {
    this.ringNumber = ringNumber;
  }
  
  public String getMatchNumber() {
    return this.matchNumber;
  }
  
  public void setMatchNumber(String matchNumber) {
    this.matchNumber = matchNumber;
  }
  
  public String getPhase() {
    return this.phase;
  }
  
  public void setPhase(String phase) {
    this.phase = phase;
  }
  
  public CategoryDto getCategory() {
    return this.category;
  }
  
  public void setCategory(CategoryDto category) {
    this.category = category;
  }
  
  public AthleteDto getBlueAthlete() {
    return this.blueAthlete;
  }
  
  public void setBlueAthlete(AthleteDto blueAthlete) {
    this.blueAthlete = blueAthlete;
  }
  
  public Integer getBlueAthleteVideoQuota() {
    return this.blueAthleteVideoQuota;
  }
  
  public void setBlueAthleteVideoQuota(Integer blueAthleteVideoQuota) {
    this.blueAthleteVideoQuota = blueAthleteVideoQuota;
  }
  
  public AthleteDto getRedAthlete() {
    return this.redAthlete;
  }
  
  public void setRedAthlete(AthleteDto redAthlete) {
    this.redAthlete = redAthlete;
  }
  
  public Integer getRedAthleteVideoQuota() {
    return this.redAthleteVideoQuota;
  }
  
  public void setRedAthleteVideoQuota(Integer redAthleteVideoQuota) {
    this.redAthleteVideoQuota = redAthleteVideoQuota;
  }
  
  public RoundsConfigDto getRoundsConfig() {
    return this.roundsConfig;
  }
  
  public void setRoundsConfig(RoundsConfigDto roundsConfig) {
    this.roundsConfig = roundsConfig;
  }
  
  public Boolean getParaTkdMatch() {
    return this.isParaTkdMatch;
  }
  
  public void setParaTkdMatch(Boolean paraTkdMatch) {
    this.isParaTkdMatch = paraTkdMatch;
  }
  
  public Integer getDifferencialScore() {
    return this.differencialScore;
  }
  
  public void setDifferencialScore(Integer differencialScore) {
    this.differencialScore = differencialScore;
  }
  
  public Integer getMaxAllowedGamJeoms() {
    return this.maxAllowedGamJeoms;
  }
  
  public void setMaxAllowedGamJeoms(Integer maxAllowedGamJeoms) {
    this.maxAllowedGamJeoms = maxAllowedGamJeoms;
  }
  
  public Boolean getWtCompetitionDataProtocol() {
    return this.isWtCompetitionDataProtocol;
  }
  
  public void setWtCompetitionDataProtocol(Boolean wtCompetitionDataProtocol) {
    this.isWtCompetitionDataProtocol = wtCompetitionDataProtocol;
  }
  
  public RefereeDto getRefereeCR() {
    return this.refereeCR;
  }
  
  public void setRefereeCR(RefereeDto refereeCR) {
    this.refereeCR = refereeCR;
  }
  
  public RefereeDto getRefereeJ1() {
    return this.refereeJ1;
  }
  
  public void setRefereeJ1(RefereeDto refereeJ1) {
    this.refereeJ1 = refereeJ1;
  }
  
  public RefereeDto getRefereeJ2() {
    return this.refereeJ2;
  }
  
  public void setRefereeJ2(RefereeDto refereeJ2) {
    this.refereeJ2 = refereeJ2;
  }
  
  public RefereeDto getRefereeJ3() {
    return this.refereeJ3;
  }
  
  public void setRefereeJ3(RefereeDto refereeJ3) {
    this.refereeJ3 = refereeJ3;
  }
  
  public RefereeDto getRefereeTA() {
    return this.refereeTA;
  }
  
  public void setRefereeTA(RefereeDto refereeTA) {
    this.refereeTA = refereeTA;
  }
  
  public RefereeDto getRefereeRJ() {
    return this.refereeRJ;
  }
  
  public void setRefereeRJ(RefereeDto refereeRJ) {
    this.refereeRJ = refereeRJ;
  }
  
  public MatchVictoryCriteria getMatchVictoryCriteria() {
    return this.matchVictoryCriteria;
  }
  
  public void setMatchVictoryCriteria(MatchVictoryCriteria matchVictoryCriteria) {
    this.matchVictoryCriteria = matchVictoryCriteria;
  }
}
