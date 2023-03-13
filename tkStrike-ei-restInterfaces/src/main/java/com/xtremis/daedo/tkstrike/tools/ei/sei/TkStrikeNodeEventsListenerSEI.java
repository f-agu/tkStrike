package com.xtremis.daedo.tkstrike.tools.ei.sei;

import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeChangeNetworkStatusEventDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeNetworkConfigurationEventDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeNodeDataEvent;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeNodeStatusEvent;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/node-events-listener")
@Produces({"application/json;charset=utf-8"})
@Consumes({"application/json;charset=utf-8"})
public interface TkStrikeNodeEventsListenerSEI {
  @GET
  @Path("/ping")
  Response doPing();
  
  @POST
  @Path("/new-network-status-event")
  Response sendNewNetworkStatusEvent(TkStrikeChangeNetworkStatusEventDto paramTkStrikeChangeNetworkStatusEventDto);
  
  @POST
  @Path("/new-network-configuration-event")
  Response sendNewNetworkConfigurationEvent(TkStrikeNetworkConfigurationEventDto paramTkStrikeNetworkConfigurationEventDto);
  
  @POST
  @Path("/new-status-event")
  Response sendNewStatusEvent(TkStrikeNodeStatusEvent paramTkStrikeNodeStatusEvent);
  
  @POST
  @Path("/new-data-event")
  Response sendNewDataEvent(TkStrikeNodeDataEvent paramTkStrikeNodeDataEvent);
}
