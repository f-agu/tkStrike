package com.xtremis.daedo.wtdata.client;

import com.xtremis.daedo.wtdata.model.Match;
import com.xtremis.daedo.wtdata.model.MatchAction;
import com.xtremis.daedo.wtdata.model.MatchResult;
import com.xtremis.daedo.wtdata.model.Participant;
import io.crnk.client.CrnkClient;
import io.crnk.client.ResponseBodyException;
import io.crnk.client.http.HttpAdapterListener;
import io.crnk.client.http.HttpAdapterRequest;
import io.crnk.client.http.HttpAdapterResponse;
import io.crnk.client.http.apache.HttpClientAdapter;
import io.crnk.client.http.apache.HttpClientAdapterListener;
import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.FilterSpec;
import io.crnk.core.queryspec.IncludeRelationSpec;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class MatchClientImpl implements MatchClient {
  private static final Logger loggerWT = Logger.getLogger(MatchClientImpl.class);
  
  private static final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
  
  private static final String X_API_KEY = "x-api-key";
  
  @Value("${tkStrike.wtCompetitionDataProtocol.clientReceiveTimeoutInSeconds}")
  private Integer clientReceiveTimeout;
  
  private String ovrMatchUrl;
  
  private ResourceRepository<Match, String> matchRepository;
  
  private String ovrMatchActionUrl;
  
  private ResourceRepository<MatchAction, String> matchActionRepository;
  
  private String ovrMatchResultUrl;
  
  private ResourceRepository<MatchResult, String> matchResultRepository;
  
  private String ovrParticipantUrl;
  
  private ResourceRepository<Participant, String> participantRepository;
  
  private static SSLContext sslContext;
  
  private SSLContext getSslContext() {
    if (sslContext == null)
      try {
        sslContext = SSLContext.getInstance("TLS");
        TrustManager[] trustManagers = new TrustManager[0];
        trustManagers = (TrustManager[])ArrayUtils.addAll((Object[])trustManagers, (Object[])new TrustManager[] { new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}
                
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}
                
                public X509Certificate[] getAcceptedIssuers() {
                  return new X509Certificate[0];
                }
              } });
        KeyManager[] keyManagers = new KeyManager[0];
        keyManagers = (KeyManager[])ArrayUtils.addAll((Object[])keyManagers, (Object[])new KeyManager[] { new X509KeyManager() {
                public String[] getClientAliases(String s, Principal[] principals) {
                  return new String[0];
                }
                
                public String chooseClientAlias(String[] strings, Principal[] principals, Socket socket) {
                  return null;
                }
                
                public String[] getServerAliases(String s, Principal[] principals) {
                  return new String[0];
                }
                
                public String chooseServerAlias(String s, Principal[] principals, Socket socket) {
                  return null;
                }
                
                public X509Certificate[] getCertificateChain(String s) {
                  return new X509Certificate[0];
                }
                
                public PrivateKey getPrivateKey(String s) {
                  return null;
                }
              } });
        SecureRandom secureRandom = new SecureRandom();
        sslContext.init(keyManagers, trustManagers, secureRandom);
        return sslContext;
      } catch (Exception e) {
        e.printStackTrace();
      }  
    return sslContext;
  }
  
  private ResourceRepository<Match, String> getMatchRepository(String ovrUrl, String xApiKey) {
    if (this.matchRepository == null || 
      !this.ovrMatchUrl.equals(ovrUrl)) {
      this.ovrMatchUrl = ovrUrl;
      CrnkClient client = new CrnkClient(this.ovrMatchUrl);
      generalInitializationClient(client, xApiKey);
      this.matchRepository = client.getRepositoryForType(Match.class);
    } 
    return this.matchRepository;
  }
  
  private ResourceRepository<MatchAction, String> getMatchActionRepository(String ovrUrl, String xApiKey) {
    if (this.matchActionRepository == null || 
      !this.ovrMatchActionUrl.equals(ovrUrl)) {
      this.ovrMatchActionUrl = ovrUrl;
      CrnkClient client = new CrnkClient(this.ovrMatchActionUrl);
      generalInitializationClient(client, xApiKey);
      this.matchActionRepository = client.getRepositoryForType(MatchAction.class);
    } 
    return this.matchActionRepository;
  }
  
  private ResourceRepository<MatchResult, String> getMatchResultRepository(String ovrUrl, String xApiKey) {
    if (this.matchResultRepository == null || 
      !this.ovrMatchResultUrl.equals(ovrUrl)) {
      this.ovrMatchResultUrl = ovrUrl;
      CrnkClient client = new CrnkClient(this.ovrMatchResultUrl);
      generalInitializationClient(client, xApiKey);
      this.matchResultRepository = client.getRepositoryForType(MatchResult.class);
    } 
    return this.matchResultRepository;
  }
  
  private ResourceRepository<Participant, String> getParticipantRepository(String ovrUrl, String xApiKey) {
    if (this.participantRepository == null || 
      !this.ovrParticipantUrl.equals(ovrUrl)) {
      this.ovrParticipantUrl = ovrUrl;
      CrnkClient client = new CrnkClient(this.ovrParticipantUrl);
      generalInitializationClient(client, xApiKey);
      this.participantRepository = client.getRepositoryForType(Participant.class);
    } 
    return this.participantRepository;
  }
  
  private void generalInitializationClient(CrnkClient client, final String xApiKey) {
    client.getHttpAdapter().setReceiveTimeout(this.clientReceiveTimeout.intValue(), TimeUnit.SECONDS);
    ((HttpClientAdapter)client.getHttpAdapter()).addListener(new HttpClientAdapterListener() {
          public void onBuild(HttpClientBuilder httpClientBuilder) {
            httpClientBuilder.setSSLContext(MatchClientImpl.this.getSslContext());
          }
        });
    client.getHttpAdapter().addListener(new HttpAdapterListener() {
          public void onRequest(HttpAdapterRequest httpAdapterRequest) {
            if (xApiKey != null) {
              if (MatchClientImpl.loggerWT.isDebugEnabled())
                MatchClientImpl.loggerWT.info("Setting X-API-KEY header to HTTP request"); 
              httpAdapterRequest.header("x-api-key", xApiKey);
            } 
            httpAdapterRequest.header("Content-Type", "application/vnd.api+json");
            MatchClientImpl.loggerWT.debug(httpAdapterRequest.getBody());
          }
          
          public void onResponse(HttpAdapterRequest httpAdapterRequest, HttpAdapterResponse httpAdapterResponse) {}
        });
  }
  
  public Future<Boolean> doPing(final String ovrUrl, final String xApiKey) {
    return threadPool.submit(new Callable<Boolean>() {
          public Boolean call() throws Exception {
            MatchClientImpl.loggerWT.info("Call ping/status to " + ovrUrl);
            CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().setSSLContext(MatchClientImpl.this.getSslContext()).build();
            HttpGet request = new HttpGet(ovrUrl + "/status");
            if (StringUtils.isNotBlank(xApiKey))
              request.addHeader("x-api-key", xApiKey); 
            HttpResponse response = closeableHttpClient.execute((HttpUriRequest)request);
            return Boolean.valueOf((response != null && response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 204));
          }
        });
  }
  
  public Future<Match> getMatch(String ovrUrl, String xApiKey, final String matchId) {
    final ResourceRepository<Match, String> matchRepository4ThreadPool = getMatchRepository(ovrUrl, xApiKey);
    return threadPool.submit(new Callable<Match>() {
          public Match call() throws Exception {
            MatchClientImpl.loggerWT.info("Call getMatch with id " + matchId);
            try {
              return (Match)matchRepository4ThreadPool.findOne(matchId, new QuerySpec(Match.class));
            } catch (RuntimeException e) {
              MatchClientImpl.loggerWT.error("Exception getting Match", e);
              throw e;
            } 
          }
        });
  }
  
  public Future<List<Match>> getMatches(String ovrUrl, String xApiKey, final Integer matNumber) {
    final ResourceRepository<Match, String> matchRepository4ThreadPool = getMatchRepository(ovrUrl, xApiKey);
    return threadPool.submit(new Callable<List<Match>>() {
          public List<Match> call() throws Exception {
            try {
              MatchClientImpl.loggerWT.info("Call getMatches of mat " + matNumber);
              FilterSpec filterByMat = PathSpec.of("mat").filter(FilterOperator.EQ, matNumber);
              FilterSpec filterByStatus = PathSpec.of("status").filter(FilterOperator.EQ, "available");
              QuerySpec querySpec = new QuerySpec(Match.class);
              querySpec.addFilter(filterByMat);
              querySpec.addFilter(filterByStatus);
              querySpec.setIncludedRelations(Arrays.asList(new IncludeRelationSpec[] { new IncludeRelationSpec(PathSpec.of("homeCompetitor")), new IncludeRelationSpec(
                        PathSpec.of("awayCompetitor")), new IncludeRelationSpec(
                        PathSpec.of("matchConfiguration")), new IncludeRelationSpec(
                        PathSpec.of("refereeAssignment")), new IncludeRelationSpec(
                        PathSpec.of("event")) }));
              return (List<Match>)matchRepository4ThreadPool.findAll(querySpec);
            } catch (RuntimeException e) {
              MatchClientImpl.loggerWT.error("Exception getting Matches", e);
              throw e;
            } 
          }
        });
  }
  
  public Future<Boolean> sendMatchAction(String ovrUrl, String xApiKey, final String matchId, final MatchAction matchAction) {
    final ResourceRepository<MatchAction, String> matchActionRepository4ThreadPool = getMatchActionRepository(ovrUrl + "/matches/" + matchId, xApiKey);
    return threadPool.submit(new Callable<Boolean>() {
          public Boolean call() throws Exception {
            try {
              MatchClientImpl.loggerWT.info("To Send new ACTION event " + matchAction.getPosition() + " type: " + matchAction.getAction() + " for Match " + matchId + " (" + matchAction.toString() + ")");
              matchActionRepository4ThreadPool.create(matchAction);
              return Boolean.TRUE;
            } catch (ResponseBodyException e) {
              return Boolean.TRUE;
            } catch (RuntimeException e) {
              throw e;
            } 
          }
        });
  }
  
  public Future<Boolean> sendMatchResult(String ovrUrl, String xApiKey, final String matchId, final MatchResult matchResult) {
    final ResourceRepository<MatchResult, String> matchResultRepository4ThreadPool = getMatchResultRepository(ovrUrl + "/matches/" + matchId, xApiKey);
    return threadPool.submit(new Callable<Boolean>() {
          public Boolean call() throws Exception {
            try {
              MatchClientImpl.loggerWT.info("To send new Match Result of match " + matchId + " " + matchResult.toString());
              matchResultRepository4ThreadPool.create(matchResult);
              return Boolean.TRUE;
            } catch (ResponseBodyException e) {
              return Boolean.TRUE;
            } catch (RuntimeException e) {
              throw e;
            } 
          }
        });
  }
  
  public Future<Participant> getParticipant(String ovrUrl, String xApiKey, final String participantId) {
    final ResourceRepository<Participant, String> participantRepository4ThreadPool = getParticipantRepository(ovrUrl, xApiKey);
    return threadPool.submit(new Callable<Participant>() {
          public Participant call() throws Exception {
            MatchClientImpl.loggerWT.info("Call getParticipant with id " + participantId);
            try {
              return (Participant)participantRepository4ThreadPool.findOne(participantId, new QuerySpec(Participant.class));
            } catch (RuntimeException e) {
              MatchClientImpl.loggerWT.error("Exception calling getParticipant", e);
              throw e;
            } 
          }
        });
  }
}
