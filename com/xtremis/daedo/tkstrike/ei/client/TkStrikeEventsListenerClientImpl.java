package com.xtremis.daedo.tkstrike.ei.client;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.provider.json.JSONProvider;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xtremis.daedo.tkstrike.service.AppStatusWorker;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchConfigurationDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchResultDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.TeamMatchConfigurationDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeEventDto;
import com.xtremis.daedo.tkstrike.tools.ei.sei.TkStrikeEventsListenerSEI;
import com.xtremis.daedo.tkstrike.tools.utils.provider.BaseJSONProvider;


@Service
public class TkStrikeEventsListenerClientImpl implements TkStrikeEventsListenerClient {

	private static final Logger loggerEI = Logger.getLogger("EXTERNAL_INTEGRATION");

	private final AppStatusWorker appStatusWorker;

	@Autowired
	public TkStrikeEventsListenerClientImpl(AppStatusWorker appStatusWorker) {
		this.appStatusWorker = appStatusWorker;
	}

	@Override
	public Boolean doPing(String eventsListenerURL) throws TkStrikeServiceException {
		if(StringUtils.isNotBlank(eventsListenerURL))
			try {
				Response response = getProxy(eventsListenerURL).doPing();
				if(response != null) {
					loggerEI.info("EventsListener ping on " + eventsListenerURL + " " + response.getStatus());
					return (Response.Status.OK.getStatusCode() == response.getStatus()) ? Boolean.TRUE : Boolean.FALSE;
				}
			} catch(RuntimeException e) {
				loggerEI.error("Error doPing", e);
				this.appStatusWorker.informErrorWithExternalService();
			}
		return Boolean.FALSE;
	}

	@Override
	public Boolean sendNewMatchEvent(String eventsListenerURL, TkStrikeEventDto tkStrikeEventDto) throws TkStrikeServiceException {
		if(StringUtils.isNotBlank(eventsListenerURL) && tkStrikeEventDto != null)
			try {
				Response response = getProxy(eventsListenerURL).sendNewMatchEvent(tkStrikeEventDto);
				if(response != null) {
					try {
						loggerEI.info("EventsListener sendNewMatchEvent " + tkStrikeEventDto.toJSON().toString() + " on " + eventsListenerURL + " "
								+ response.getStatus());
					} catch(JSONException jSONException) {}
					return (Response.Status.OK.getStatusCode() == response.getStatus()) ? Boolean.TRUE : Boolean.FALSE;
				}
			} catch(RuntimeException e) {
				loggerEI.error("Error sendNewMatchEvent", e);
				this.appStatusWorker.informErrorWithExternalService();
			}
		return Boolean.FALSE;
	}

	@Override
	public Boolean sendHasNewMatchConfigured(String eventsListenerURL, MatchConfigurationDto matchConfigurationDto) throws TkStrikeServiceException {
		if(StringUtils.isNotBlank(eventsListenerURL) && matchConfigurationDto != null)
			try {
				try {
					loggerEI.info("SEND NewMatchConfigured ->" + matchConfigurationDto.toJSON().toString());
				} catch(JSONException jSONException) {}
				Response response = null;
				if(matchConfigurationDto instanceof TeamMatchConfigurationDto) {
					response = getProxy(eventsListenerURL).sendHasNewTeamMatchConfigured((TeamMatchConfigurationDto)matchConfigurationDto);
				} else {
					response = getProxy(eventsListenerURL).sendHasNewMatchConfigured(matchConfigurationDto);
				}
				if(response != null) {
					loggerEI.info("EventsListener sendHasNewMatchConfigured on " + eventsListenerURL + " " + response.getStatus());
					return (Response.Status.OK.getStatusCode() == response.getStatus()) ? Boolean.TRUE : Boolean.FALSE;
				}
			} catch(RuntimeException e) {
				loggerEI.error("Error sendHasNewMatchConfigured", e);
				this.appStatusWorker.informErrorWithExternalService();
			}
		return Boolean.FALSE;
	}

	@Override
	public Boolean doSendMatchResult(String eventsListenerURL, MatchResultDto matchResultDto) throws TkStrikeServiceException {
		if(StringUtils.isNotBlank(eventsListenerURL) && matchResultDto != null)
			try {
				try {
					loggerEI.info("SEND NewMatchResult ->" + matchResultDto.toJSON().toString());
				} catch(JSONException jSONException) {}
				Response response = null;
				response = getProxy(eventsListenerURL).doSendMatchResult(matchResultDto);
				if(response != null) {
					loggerEI.info("EventsListener doSendMatchResult on " + eventsListenerURL + " " + response.getStatus());
					return (Response.Status.OK.getStatusCode() == response.getStatus()) ? Boolean.TRUE : Boolean.FALSE;
				}
			} catch(RuntimeException e) {
				loggerEI.error("Error doSendMatchResult", e);
				this.appStatusWorker.informErrorWithExternalService();
			}
		return Boolean.FALSE;
	}

	private TkStrikeEventsListenerSEI getProxy(String proxyURL) throws TkStrikeServiceException {
		loggerEI.info("EI url ->" + proxyURL);
		BaseJSONProvider baseJSONProvider = new BaseJSONProvider();
		JSONProvider jsonProvider = new JSONProvider();
		jsonProvider.setDropRootElement(false);
		jsonProvider.setDropCollectionWrapperElement(false);
		jsonProvider.setAttributesToElements(true);
		jsonProvider.setSupportUnwrapped(false);
		jsonProvider.setSerializeAsArray(true);
		List<Object> providers = new ArrayList();
		providers.add(baseJSONProvider);
		providers.add(jsonProvider);
		return JAXRSClientFactory.create(proxyURL, TkStrikeEventsListenerSEI.class, providers);
	}
}
