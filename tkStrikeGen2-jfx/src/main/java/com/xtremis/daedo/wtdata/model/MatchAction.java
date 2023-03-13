package com.xtremis.daedo.wtdata.model;

import com.xtremis.daedo.wtdata.model.constants.Action;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import java.io.Serializable;

@JsonApiResource(type = "match-actions", resourcePath = "actions")
public class MatchAction implements Serializable {
  @JsonApiId
  private String id;
  
  private Action action;
  
  private Integer hitlevel;
  
  private Integer round;
  
  private String roundTime;
  
  private Integer position;
  
  private MatchScore score;
  
  private MatchScore penalties;
  
  private String description;
  
  private String timestamp;
  
  @JsonApiRelation(serialize = SerializeType.ONLY_ID)
  private Match match;
  
  @JsonApiRelation(serialize = SerializeType.ONLY_ID)
  private Competitor homeCompetitor;
  
  @JsonApiRelation(serialize = SerializeType.ONLY_ID)
  private Competitor awayCompetitor;
  
  public String getId() {
    return this.id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public Action getAction() {
    return this.action;
  }
  
  public void setAction(Action action) {
    this.action = action;
  }
  
  public Integer getHitlevel() {
    return this.hitlevel;
  }
  
  public void setHitlevel(Integer hitlevel) {
    this.hitlevel = hitlevel;
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
  
  public Integer getPosition() {
    return this.position;
  }
  
  public void setPosition(Integer position) {
    this.position = position;
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
  
  public String getDescription() {
    return this.description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public String getTimestamp() {
    return this.timestamp;
  }
  
  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }
  
  public Match getMatch() {
    return this.match;
  }
  
  public void setMatch(Match match) {
    this.match = match;
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
  
  public String toString() {
    return "MatchAction{id='" + this.id + '\'' + ", action=" + this.action + ", round=" + this.round + ", hitlevel=" + this.hitlevel + ", roundTime='" + this.roundTime + '\'' + ", position=" + this.position + ", score=" + this.score + ", penalties=" + this.penalties + ", description='" + this.description + '\'' + ", timestamp='" + this.timestamp + '\'' + '}';
  }
}
