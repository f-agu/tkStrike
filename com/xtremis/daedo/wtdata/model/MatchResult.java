package com.xtremis.daedo.wtdata.model;

import com.xtremis.daedo.wtdata.model.constants.ResultStatus;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import java.io.Serializable;

@JsonApiResource(type = "match-results", resourcePath = "results")
public class MatchResult implements Serializable {
  @JsonApiId
  private String id;
  
  private ResultStatus status;
  
  private Integer round;
  
  private Integer position;
  
  private MatchInternalResult result;
  
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
  
  public ResultStatus getStatus() {
    return this.status;
  }
  
  public void setStatus(ResultStatus status) {
    this.status = status;
  }
  
  public Integer getRound() {
    return this.round;
  }
  
  public void setRound(Integer round) {
    this.round = round;
  }
  
  public Integer getPosition() {
    return this.position;
  }
  
  public void setPosition(Integer position) {
    this.position = position;
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
    return "MatchResult{id='" + this.id + '\'' + ", status=" + this.status + ", round=" + this.round + ", result=" + this.result + ", score=" + this.score + ", penalties=" + this.penalties + ", description='" + this.description + '\'' + ", timestamp='" + this.timestamp + '\'' + '}';
  }
}
