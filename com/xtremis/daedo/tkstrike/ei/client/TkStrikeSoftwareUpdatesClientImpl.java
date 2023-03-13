package com.xtremis.daedo.tkstrike.ei.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.provider.json.JSONProvider;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.su.om.TkStrikeHasNewVersionResponseDto;
import com.xtremis.daedo.tkstrike.tools.su.sei.TkStrikeSoftwareUpdatesSEI;
import com.xtremis.daedo.tkstrike.tools.utils.provider.BaseJSONProvider;


@Service
public class TkStrikeSoftwareUpdatesClientImpl implements TkStrikeSoftwareUpdatesClient {

	private static final Logger loggerEI = Logger.getLogger("EXTERNAL_INTEGRATION");

	@Value("${tkStrike.softwareUpdates.url}")
	private String softwareUpdatesUrl;

	@Value("${tkStrike.current.version}")
	private String currentVersion;

	@Value("${tkStrike.current.build}")
	private String currentBuild;

	@Override
	public TkStrikeHasNewVersionResponseDto hasNewVersion() throws TkStrikeServiceException {
		try {
			return getProxy(this.softwareUpdatesUrl).hasNewVersion(this.currentVersion, this.currentBuild);
		} catch(RuntimeException e) {
			throw new TkStrikeServiceException(e);
		}
	}

	private TkStrikeSoftwareUpdatesSEI getProxy(String proxyURL) throws TkStrikeServiceException {
		loggerEI.info("SoftwareUpdates url ->" + proxyURL);
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
		return JAXRSClientFactory.create(proxyURL, TkStrikeSoftwareUpdatesSEI.class, providers, "tkStrikeApp", "tkStrikeAppPassword", null);
	}
}
