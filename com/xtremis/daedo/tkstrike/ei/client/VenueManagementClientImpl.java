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

import com.xtremis.daedo.tkstrike.service.ExternalConfigService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchConfigurationDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchResultDto;
import com.xtremis.daedo.tkstrike.tools.ei.sei.VenueManagementSEI;
import com.xtremis.daedo.tkstrike.tools.utils.provider.BaseJSONProvider;
import com.xtremis.daedo.tkstrike.ui.model.ExternalConfigEntry;


@Service
public class VenueManagementClientImpl implements VenueManagementClient {

	private static final Logger loggerEI = Logger.getLogger("EXTERNAL_INTEGRATION");

	@Autowired
	private ExternalConfigService externalConfigService;

	@Override
	public Boolean doPing() throws TkStrikeServiceException {
		ExternalConfigEntry externalConfigEntry = this.externalConfigService.getExternalConfigEntry();
		return doPing((externalConfigEntry != null) ? externalConfigEntry.getVenueManagementURL() : null);
	}

	@Override
	public Boolean doPing(String vmURL) throws TkStrikeServiceException {
		if(StringUtils.isNotBlank(vmURL))
			try {
				Response response = getProxy(vmURL).doPing();
				loggerEI.info("VM ping ok!");
				if(response != null) {
					loggerEI.info("VM ping on " + vmURL + " " + response.getStatus());
					return (Response.Status.OK.getStatusCode() == response.getStatus()) ? Boolean.TRUE : Boolean.FALSE;
				}
			} catch(RuntimeException e) {
				loggerEI.error("Error doPing", e);
			}
		return Boolean.FALSE;
	}

	@Override
	public MatchConfigurationDto getNextMatch() throws TkStrikeServiceException {
		ExternalConfigEntry externalConfigEntry = this.externalConfigService.getExternalConfigEntry();
		if(externalConfigEntry != null && StringUtils.isNotBlank(externalConfigEntry.getVenueManagementURL()))
			try {
				MatchConfigurationDto nextMatch = getProxy(externalConfigEntry.getVenueManagementURL()).getNextMatch(externalConfigEntry
						.getVenueManagementRingNumber());
				try {
					loggerEI.info("NextMatch getted ->" + ((nextMatch != null) ? nextMatch.toJSON().toString() : "null"));
				} catch(JSONException jSONException) {}
				return nextMatch;
			} catch(RuntimeException e) {
				loggerEI.error("Error getNextMatch", e);
				throw new TkStrikeServiceException(e);
			}
		return null;
	}

	@Override
	public MatchConfigurationDto getPrevMatch() throws TkStrikeServiceException {
		ExternalConfigEntry externalConfigEntry = this.externalConfigService.getExternalConfigEntry();
		if(externalConfigEntry != null && StringUtils.isNotBlank(externalConfigEntry.getVenueManagementURL()))
			try {
				MatchConfigurationDto prevMatch = getProxy(externalConfigEntry.getVenueManagementURL()).getPrevMatch(externalConfigEntry
						.getVenueManagementRingNumber());
				try {
					loggerEI.info("PrevMatch getted ->" + ((prevMatch != null) ? prevMatch.toJSON().toString() : "null"));
				} catch(JSONException jSONException) {}
				return prevMatch;
			} catch(RuntimeException e) {
				loggerEI.error("Error getPrevMatch", e);
				throw new TkStrikeServiceException(e);
			}
		return null;
	}

	@Override
	public Boolean doSendMatchResult(MatchResultDto matchResultDto) throws TkStrikeServiceException {
		ExternalConfigEntry externalConfigEntry = this.externalConfigService.getExternalConfigEntry();
		if(externalConfigEntry != null && StringUtils.isNotBlank(externalConfigEntry.getVenueManagementURL()))
			try {
				try {
					loggerEI.info("Send MatchResult ->" + matchResultDto.toJSON().toString());
				} catch(JSONException jSONException) {}
				getProxy(externalConfigEntry.getVenueManagementURL()).doSendMatchResult(externalConfigEntry.getVenueManagementRingNumber(),
						matchResultDto);
				return Boolean.TRUE;
			} catch(RuntimeException e) {
				loggerEI.error("Error getPrevMatch", e);
			}
		return null;
	}

	private VenueManagementSEI getProxy(String proxyURL) throws TkStrikeServiceException {
		loggerEI.info("VM url ->" + proxyURL);
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
		return JAXRSClientFactory.create(proxyURL, VenueManagementSEI.class, providers);
	}
}
