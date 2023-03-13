package com.xtremis.daedo.wtdata.model;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import java.io.Serializable;
import java.util.Set;

@JsonApiResource(type = "competitors")
public class Competitor implements Serializable {
  @JsonApiId
  private String id;
  
  private String competitorType;
  
  private String printName;
  
  private String printInitialName;
  
  private String tvName;
  
  private String tvInitialName;
  
  private String scoreboardName;
  
  private Integer rank;
  
  private Integer seed;
  
  private String country;
  
  @JsonApiRelation
  private Set<Match> matches;
  
  @JsonApiRelation
  private Organization organization;
  
  @JsonApiRelation
  private Event event;
  
  @JsonApiRelation
  private Participant participant;
  
  public String getId() {
    return this.id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getCompetitorType() {
    return this.competitorType;
  }
  
  public void setCompetitorType(String competitorType) {
    this.competitorType = competitorType;
  }
  
  public String getPrintName() {
    return this.printName;
  }
  
  public void setPrintName(String printName) {
    this.printName = printName;
  }
  
  public String getPrintInitialName() {
    return this.printInitialName;
  }
  
  public void setPrintInitialName(String printInitialName) {
    this.printInitialName = printInitialName;
  }
  
  public String getTvName() {
    return this.tvName;
  }
  
  public void setTvName(String tvName) {
    this.tvName = tvName;
  }
  
  public String getTvInitialName() {
    return this.tvInitialName;
  }
  
  public void setTvInitialName(String tvInitialName) {
    this.tvInitialName = tvInitialName;
  }
  
  public String getScoreboardName() {
    return this.scoreboardName;
  }
  
  public void setScoreboardName(String scoreboardName) {
    this.scoreboardName = scoreboardName;
  }
  
  public Integer getRank() {
    return this.rank;
  }
  
  public void setRank(Integer rank) {
    this.rank = rank;
  }
  
  public Integer getSeed() {
    return this.seed;
  }
  
  public void setSeed(Integer seed) {
    this.seed = seed;
  }
  
  public String getCountry() {
    return this.country;
  }
  
  public void setCountry(String country) {
    this.country = country;
  }
  
  public Set<Match> getMatches() {
    return this.matches;
  }
  
  public void setMatches(Set<Match> matches) {
    this.matches = matches;
  }
  
  public Organization getOrganization() {
    return this.organization;
  }
  
  public void setOrganization(Organization organization) {
    this.organization = organization;
  }
  
  public Event getEvent() {
    return this.event;
  }
  
  public void setEvent(Event event) {
    this.event = event;
  }
  
  public Participant getParticipant() {
    return this.participant;
  }
  
  public void setParticipant(Participant participant) {
    this.participant = participant;
  }
  
  public String toString() {
    return "Competitor{id='" + this.id + '\'' + ", competitorType='" + this.competitorType + '\'' + ", printName='" + this.printName + '\'' + ", printInitialName='" + this.printInitialName + '\'' + ", tvName='" + this.tvName + '\'' + ", tvInitialName='" + this.tvInitialName + '\'' + ", scoreboardName='" + this.scoreboardName + '\'' + ", rank=" + this.rank + ", seed=" + this.seed + ", country='" + this.country + '\'' + ", matches=" + this.matches + ", organization=" + this.organization + ", event=" + this.event + '}';
  }
}
