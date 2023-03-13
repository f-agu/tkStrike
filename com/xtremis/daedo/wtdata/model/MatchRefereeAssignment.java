package com.xtremis.daedo.wtdata.model;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import java.io.Serializable;

@JsonApiResource(type = "match-referee-assignments")
public class MatchRefereeAssignment implements Serializable {
  @JsonApiId
  private String id;
  
  @JsonApiRelation(serialize = SerializeType.ONLY_ID)
  private Match match;
  
  @JsonApiRelation(serialize = SerializeType.ONLY_ID)
  private Participant refJ1;
  
  @JsonApiRelation(serialize = SerializeType.ONLY_ID)
  private Participant refJ2;
  
  @JsonApiRelation(serialize = SerializeType.ONLY_ID)
  private Participant refJ3;
  
  @JsonApiRelation(serialize = SerializeType.ONLY_ID)
  private Participant refCR;
  
  @JsonApiRelation(serialize = SerializeType.ONLY_ID)
  private Participant refRJ;
  
  @JsonApiRelation(serialize = SerializeType.ONLY_ID)
  private Participant refTA;
  
  public String getId() {
    return this.id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public Match getMatch() {
    return this.match;
  }
  
  public void setMatch(Match match) {
    this.match = match;
  }
  
  public Participant getRefJ1() {
    return this.refJ1;
  }
  
  public void setRefJ1(Participant refJ1) {
    this.refJ1 = refJ1;
  }
  
  public Participant getRefJ2() {
    return this.refJ2;
  }
  
  public void setRefJ2(Participant refJ2) {
    this.refJ2 = refJ2;
  }
  
  public Participant getRefJ3() {
    return this.refJ3;
  }
  
  public void setRefJ3(Participant refJ3) {
    this.refJ3 = refJ3;
  }
  
  public Participant getRefCR() {
    return this.refCR;
  }
  
  public void setRefCR(Participant refCR) {
    this.refCR = refCR;
  }
  
  public Participant getRefRJ() {
    return this.refRJ;
  }
  
  public void setRefRJ(Participant refRJ) {
    this.refRJ = refRJ;
  }
  
  public Participant getRefTA() {
    return this.refTA;
  }
  
  public void setRefTA(Participant refTA) {
    this.refTA = refTA;
  }
  
  public String toString() {
    return "MatchRefereeAssignment{id='" + this.id + '\'' + ", refJ1=" + this.refJ1 + ", refJ2=" + this.refJ2 + ", refJ3=" + this.refJ3 + ", refCR=" + this.refCR + ", refRJ=" + this.refRJ + ", refTA=" + this.refTA + '}';
  }
}
