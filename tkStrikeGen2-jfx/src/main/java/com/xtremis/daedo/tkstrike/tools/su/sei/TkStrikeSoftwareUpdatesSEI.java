package com.xtremis.daedo.tkstrike.tools.su.sei;

import com.xtremis.daedo.tkstrike.tools.su.om.TkStrikeHasNewVersionResponseDto;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/")
@Produces({"application/json;charset=utf-8"})
public interface TkStrikeSoftwareUpdatesSEI {
  @GET
  @Path("/has-new-version")
  TkStrikeHasNewVersionResponseDto hasNewVersion(@QueryParam("version") String paramString1, @QueryParam("build") String paramString2);
  
  @GET
  @Path("/download-release/{release-id}")
  @Produces({"application/octet-stream"})
  Response downloadRelease(@PathParam("release-id") String paramString);
}
