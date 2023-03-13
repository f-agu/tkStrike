package com.xtremis.daedo.wtdata.model;

import com.xtremis.daedo.wtdata.model.constants.Action;
import io.crnk.client.CrnkClient;
import io.crnk.client.ResponseBodyException;
import io.crnk.client.http.HttpAdapterListener;
import io.crnk.client.http.HttpAdapterRequest;
import io.crnk.client.http.HttpAdapterResponse;
import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.FilterSpec;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class MatchTesting {
  private static void doTestGetMatches() {
    CrnkClient client = new CrnkClient("https://wt.uptkd.com/wt/13");
    client.getHttpAdapter().addListener(new HttpAdapterListener() {
          public void onRequest(HttpAdapterRequest httpAdapterRequest) {
            httpAdapterRequest.header("x-api-key", "8292c366-bc4f-4316-8c81-a49e73b8fe19");
          }
          
          public void onResponse(HttpAdapterRequest httpAdapterRequest, HttpAdapterResponse httpAdapterResponse) {}
        });
    ResourceRepository<Match, String> matchRepo = client.getRepositoryForType(Match.class);
    FilterSpec filterByMat = PathSpec.of("mat").filter(FilterOperator.EQ, Integer.valueOf(1));
    FilterSpec filterByStatus = PathSpec.of("status").filter(FilterOperator.EQ, "available");
    QuerySpec querySpec = new QuerySpec(Match.class);
    querySpec.addFilter(filterByMat);
    querySpec.addFilter(filterByStatus);
    ResourceList resourceList = matchRepo.findAll(querySpec);
    int n = 0;
    for (Match match : resourceList) {
      System.out.println(ReflectionToStringBuilder.reflectionToString(match));
      n++;
    } 
    System.out.println("TOTAL ->" + n);
  }
  
  private static void doTestPostMatchAction() {
    CrnkClient client = new CrnkClient("https://wt.uptkd.com/wt/13/matches/377");
    client.getHttpAdapter().addListener(new HttpAdapterListener() {
          public void onRequest(HttpAdapterRequest httpAdapterRequest) {
            httpAdapterRequest.header("x-api-key", "8292c366-bc4f-4316-8c81-a49e73b8fe19");
            System.out.println(httpAdapterRequest.getBody());
          }
          
          public void onResponse(HttpAdapterRequest httpAdapterRequest, HttpAdapterResponse httpAdapterResponse) {
            System.out.println(httpAdapterRequest.getBody());
          }
        });
    ResourceRepository<MatchAction, String> matchActionRepo = client.getRepositoryForType(MatchAction.class);
    MatchAction matchAction = new MatchAction();
    matchAction.setAction(Action.MATCH_LOADED);
    matchAction.setRound(Integer.valueOf(1));
    matchAction.setRoundTime("02:00");
    matchAction.setPosition(Integer.valueOf(1));
    matchAction.setScore(new MatchScore(Integer.valueOf(0), Integer.valueOf(0)));
    matchAction.setPenalties(new MatchScore(Integer.valueOf(0), Integer.valueOf(0)));
    matchAction.setDescription("..");
    matchAction.setTimestamp("2019-09-01T09:00:02+03:00");
    Competitor home = new Competitor();
    home.setId("c703");
    matchAction.setHomeCompetitor(home);
    Competitor away = new Competitor();
    away.setId("c669");
    matchAction.setAwayCompetitor(away);
    try {
      matchActionRepo.create(matchAction);
    } catch (ResponseBodyException rbe) {
      System.out.println("No passa res");
    } 
  }
  
  public static void main(String[] args) {
    doTestPostMatchAction();
  }
  
  private static void doTestListAll(CrnkClient client, Class theClass) {
    System.out.println("=============================================");
    ResourceRepository<?, String> theRepo = client.getRepositoryForType(theClass);
    ResourceList resourceList = theRepo.findAll(new QuerySpec(theClass));
    int n = 0;
    for (Object session : resourceList) {
      System.out.println(n + " ->" + session.toString());
      n++;
    } 
  }
}
