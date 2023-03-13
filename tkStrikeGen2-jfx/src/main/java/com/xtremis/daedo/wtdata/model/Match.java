package com.xtremis.daedo.wtdata.model;

import com.xtremis.daedo.wtdata.model.constants.Phase;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import java.io.Serializable;
import java.util.Set;

@JsonApiResource(type = "matches")
public class Match implements Serializable {
  @JsonApiId
  private String id;
  
  private String status;
  
  private Integer mat;
  
  private String number;
  
  private Phase phase;
  
  private MatchSchedule schedule;
  
  private MatchInternalResult result;
  
  private MatchScore score;
  
  private MatchScore penalties;
  
  private Integer round;
  
  private String roundTime;
  
  @JsonApiRelation
  private Competitor homeCompetitor;
  
  @JsonApiRelation
  private Competitor awayCompetitor;
  
  @JsonApiRelation
  private Session session;
  
  @JsonApiRelation
  private Event event;
  
  @JsonApiRelation
  private MatchRefereeAssignment refereeAssignment;
  
  @JsonApiRelation(mappedBy = "match")
  private Set<MatchResult> results;
  
  @JsonApiRelation
  private MatchConfiguration matchConfiguration;
  
  public String getId() {
    return this.id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getStatus() {
    return this.status;
  }
  
  public void setStatus(String status) {
    this.status = status;
  }
  
  public Integer getMat() {
    return this.mat;
  }
  
  public void setMat(Integer mat) {
    this.mat = mat;
  }
  
  public String getNumber() {
    return this.number;
  }
  
  public void setNumber(String number) {
    this.number = number;
  }
  
  public Phase getPhase() {
    return this.phase;
  }
  
  public void setPhase(Phase phase) {
    this.phase = phase;
  }
  
  public MatchSchedule getSchedule() {
    return this.schedule;
  }
  
  public void setSchedule(MatchSchedule schedule) {
    this.schedule = schedule;
  }
  
  public MatchInternalResult getResult() {
    return this.result;
  }
  
  public void setResult(MatchInternalResult result) {
    this.result = result;
  }
  
  public MatchScore getScore() {
    return this.score;
  }
  
  public void setScore(MatchScore score) {
    this.score = score;
  }
  
  public MatchScore getPenalties() {
    return this.penalties;
  }
  
  public void setPenalties(MatchScore penalties) {
    this.penalties = penalties;
  }
  
  public Integer getRound() {
    return this.round;
  }
  
  public void setRound(Integer round) {
    this.round = round;
  }
  
  public String getRoundTime() {
    return this.roundTime;
  }
  
  public void setRoundTime(String roundTime) {
    this.roundTime = roundTime;
  }
  
  public Competitor getHomeCompetitor() {
    return this.homeCompetitor;
  }
  
  public void setHomeCompetitor(Competitor homeCompetitor) {
    this.homeCompetitor = homeCompetitor;
  }
  
  public Competitor getAwayCompetitor() {
    return this.awayCompetitor;
  }
  
  public void setAwayCompetitor(Competitor awayCompetitor) {
    this.awayCompetitor = awayCompetitor;
  }
  
  public Session getSession() {
    return this.session;
  }
  
  public void setSession(Session session) {
    this.session = session;
  }
  
  public Event getEvent() {
    return this.event;
  }
  
  public void setEvent(Event event) {
    this.event = event;
  }
  
  public MatchRefereeAssignment getRefereeAssignment() {
    return this.refereeAssignment;
  }
  
  public void setRefereeAssignment(MatchRefereeAssignment refereeAssignment) {
    this.refereeAssignment = refereeAssignment;
  }
  
  public Set<MatchResult> getResults() {
    return this.results;
  }
  
  public void setResults(Set<MatchResult> results) {
    this.results = results;
  }
  
  public MatchConfiguration getMatchConfiguration() {
    return this.matchConfiguration;
  }
  
  public void setMatchConfiguration(MatchConfiguration matchConfiguration) {
    this.matchConfiguration = matchConfiguration;
  }
  
  public String toString() {
    return "Match{id='" + this.id + '\'' + ", status='" + this.status + '\'' + ", mat=" + this.mat + ", number='" + this.number + '\'' + ", phase=" + ((this.phase != null) ? this.phase
      
      .toString() : "null") + ", schedule=" + ((this.schedule != null) ? this.schedule
      .toString() : "null") + ", result=" + ((this.result != null) ? this.result
      .toString() : "null") + ", score=" + ((this.score != null) ? this.score
      .toString() : "null") + ", penalties=" + ((this.penalties != null) ? this.penalties
      .toString() : "null") + ", round=" + this.round + ", roundTime='" + this.roundTime + '\'' + ", homeCompetitor=" + ((this.homeCompetitor != null) ? this.homeCompetitor
      
      .toString() : "null") + ", awayCompetitor=" + ((this.awayCompetitor != null) ? this.awayCompetitor
      .toString() : "null") + ", session=" + ((this.session != null) ? this.session
      .toString() : "null") + ", event=" + ((this.event != null) ? this.event
      .toString() : "null") + ", refereeAssignment=" + ((this.refereeAssignment != null) ? this.refereeAssignment
      .toString() : "null") + ", results=" + this.results + ", matchConfiguration=" + ((this.matchConfiguration != null) ? this.matchConfiguration
      
      .toString() : "null") + '}';
  }
}
