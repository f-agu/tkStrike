package com.xtremis.daedo.tkstrike.tools.ei.sei;

import com.xtremis.daedo.tkstrike.tools.ei.om.MatchConfigurationDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchResultDto;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/venue-management")
@Produces({"application/json;charset=utf-8"})
public interface VenueManagementSEI {
  @GET
  @Path("/ping")
  Response doPing();
  
  @GET
  @Path("/{ringNumber}/next-match")
  MatchConfigurationDto getNextMatch(@PathParam("ringNumber") String paramString);
  
  @GET
  @Path("/{ringNumber}/prev-match")
  MatchConfigurationDto getPrevMatch(@PathParam("ringNumber") String paramString);
  
  @POST
  @Path("/{ringNumber}/match-result")
  @Consumes({"application/json;charset=utf-8"})
  Response doSendMatchResult(@PathParam("ringNumber") String paramString, MatchResultDto paramMatchResultDto);
}
