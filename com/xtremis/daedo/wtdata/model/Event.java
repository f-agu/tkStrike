package com.xtremis.daedo.wtdata.model;

import com.xtremis.daedo.wtdata.model.constants.Gender;
import com.xtremis.daedo.wtdata.model.constants.Role;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import java.io.Serializable;
import java.util.Set;

@JsonApiResource(type = "events")
public class Event implements Serializable {
  @JsonApiId
  private String id;
  
  private String discipline;
  
  private String division;
  
  private Gender gender;
  
  private String name;
  
  private String weightCategory;
  
  private String sportClass;
  
  private String category;
  
  private Role role;
  
  @JsonApiRelation
  private Set<MedalWinner> medalWinners;
  
  @JsonApiRelation
  private Set<Match> matches;
  
  public String getId() {
    return this.id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getDiscipline() {
    return this.discipline;
  }
  
  public void setDiscipline(String discipline) {
    this.discipline = discipline;
  }
  
  public String getDivision() {
    return this.division;
  }
  
  public void setDivision(String division) {
    this.division = division;
  }
  
  public Gender getGender() {
    return this.gender;
  }
  
  public void setGender(Gender gender) {
    this.gender = gender;
  }
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getWeightCategory() {
    return this.weightCategory;
  }
  
  public void setWeightCategory(String weightCategory) {
    this.weightCategory = weightCategory;
  }
  
  public String getSportClass() {
    return this.sportClass;
  }
  
  public void setSportClass(String sportClass) {
    this.sportClass = sportClass;
  }
  
  public String getCategory() {
    return this.category;
  }
  
  public void setCategory(String category) {
    this.category = category;
  }
  
  public Role getRole() {
    return this.role;
  }
  
  public void setRole(Role role) {
    this.role = role;
  }
  
  public Set<MedalWinner> getMedalWinners() {
    return this.medalWinners;
  }
  
  public void setMedalWinners(Set<MedalWinner> medalWinners) {
    this.medalWinners = medalWinners;
  }
  
  public Set<Match> getMatches() {
    return this.matches;
  }
  
  public void setMatches(Set<Match> matches) {
    this.matches = matches;
  }
  
  public String toString() {
    return "Event{id='" + this.id + '\'' + ", discipline='" + this.discipline + '\'' + ", division='" + this.division + '\'' + ", gender='" + this.gender + '\'' + ", name='" + this.name + '\'' + ", weightCategory='" + this.weightCategory + '\'' + ", sportClass='" + this.sportClass + '\'' + ", category='" + this.category + '\'' + ", role='" + this.role + '\'' + ", medalWinners=" + this.medalWinners + '}';
  }
}
