package com.xtremis.daedo.wtdata.client;

import com.xtremis.daedo.wtdata.model.Match;
import com.xtremis.daedo.wtdata.model.MatchAction;
import com.xtremis.daedo.wtdata.model.MatchResult;
import com.xtremis.daedo.wtdata.model.Participant;
import java.util.List;
import java.util.concurrent.Future;

public interface MatchClient {
  Future<Boolean> doPing(String paramString1, String paramString2);
  
  Future<Match> getMatch(String paramString1, String paramString2, String paramString3);
  
  Future<List<Match>> getMatches(String paramString1, String paramString2, Integer paramInteger);
  
  Future<Boolean> sendMatchAction(String paramString1, String paramString2, String paramString3, MatchAction paramMatchAction);
  
  Future<Boolean> sendMatchResult(String paramString1, String paramString2, String paramString3, MatchResult paramMatchResult);
  
  Future<Participant> getParticipant(String paramString1, String paramString2, String paramString3);
}
