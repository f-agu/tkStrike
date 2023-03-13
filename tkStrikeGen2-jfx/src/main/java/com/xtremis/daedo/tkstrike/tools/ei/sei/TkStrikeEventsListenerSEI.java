package com.xtremis.daedo.tkstrike.tools.ei.sei;

import com.xtremis.daedo.tkstrike.tools.ei.om.MatchConfigurationDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchResultDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.TeamMatchConfigurationDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeEventDto;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/events-listener")
@Produces({"application/json;charset=utf-8"})
@Consumes({"application/json;charset=utf-8"})
public interface TkStrikeEventsListenerSEI {
  @GET
  @Path("/ping")
  @Consumes({"application/json;charset=utf-8"})
  Response doPing();
  
  @POST
  @Path("/new-match-event")
  @Consumes({"application/json;charset=utf-8"})
  Response sendNewMatchEvent(TkStrikeEventDto paramTkStrikeEventDto);
  
  @POST
  @Path("/new-match-configured")
  @Consumes({"application/json;charset=utf-8"})
  Response sendHasNewMatchConfigured(MatchConfigurationDto paramMatchConfigurationDto);
  
  @POST
  @Path("/new-team-match-configured")
  @Consumes({"application/json;charset=utf-8"})
  Response sendHasNewTeamMatchConfigured(TeamMatchConfigurationDto paramTeamMatchConfigurationDto);
  
  @POST
  @Path("/match-result")
  @Consumes({"application/json;charset=utf-8"})
  Response doSendMatchResult(MatchResultDto paramMatchResultDto);
}
