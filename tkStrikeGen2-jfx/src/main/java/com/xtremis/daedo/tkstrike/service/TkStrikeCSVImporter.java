package com.xtremis.daedo.tkstrike.service;

public interface TkStrikeCSVImporter {
  Boolean isImportFromCSVEnabled();
  
  Boolean isDeletePhases();
  
  Boolean isDeleteContests();
  
  Boolean isDeleteWeightDivisions();
  
  Boolean isDeleteAthletes();
  
  void tryToImportPhases() throws TkStrikeServiceException;
  
  void tryToImportContests() throws TkStrikeServiceException;
  
  void tryToImportWeightDivisions() throws TkStrikeServiceException;
  
  void tryToImportAthletes() throws TkStrikeServiceException;
}
