package com.xtremis.daedo.tkstrike.ei.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.provider.json.JSONProvider;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xtremis.daedo.tkstrike.service.ExternalConfigService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeChangeNetworkStatusEventDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeNetworkConfigurationEventDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeNodeDataEvent;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeNodeStatusEvent;
import com.xtremis.daedo.tkstrike.tools.ei.sei.TkStrikeNodeEventsListenerSEI;
import com.xtremis.daedo.tkstrike.tools.utils.provider.BaseJSONProvider;


@Service
public class TkStrikeNodeEventsListenerClientImpl implements TkStrikeNodeEventsListenerClient {

	private static final Logger logger = Logger.getLogger("EXTERNAL_NODE_EVENTS");

	protected static final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	private final ExternalConfigService externalConfigService;

	@Autowired
	public TkStrikeNodeEventsListenerClientImpl(ExternalConfigService externalConfigService) {
		this.externalConfigService = externalConfigService;
	}

	@Override
	public Future<Boolean> doFuturePing(String eventsListenerURL) throws TkStrikeServiceException {
		final TkStrikeNodeEventsListenerSEI service = getProxy(eventsListenerURL);
		return threadPool.submit(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				try {
					Response response = service.doPing();
					return (Response.Status.OK.getStatusCode() == response.getStatus()) ? Boolean.TRUE : Boolean.FALSE;
				} catch(RuntimeException e) {
					throw e;
				}
			}
		});
	}

	@Override
	public Future<Boolean> sendNewNetworkStatusEvent(String eventsListenerURL, final TkStrikeChangeNetworkStatusEventDto changeNetworkStatusEvent)
			throws TkStrikeServiceException {
		final TkStrikeNodeEventsListenerSEI service = getProxy(eventsListenerURL);
		return threadPool.submit(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				try {
					Response response = service.sendNewNetworkStatusEvent(changeNetworkStatusEvent);
					return (Response.Status.OK.getStatusCode() == response.getStatus()) ? Boolean.TRUE : Boolean.FALSE;
				} catch(RuntimeException e) {
					throw e;
				}
			}
		});
	}

	@Override
	public Future<Boolean> sendNewNetworkConfigurationEvent(String eventsListenerURL,
			final TkStrikeNetworkConfigurationEventDto networkConfigurationEvent) throws TkStrikeServiceException {
		final TkStrikeNodeEventsListenerSEI service = getProxy(eventsListenerURL);
		return threadPool.submit(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				try {
					Response response = service.sendNewNetworkConfigurationEvent(networkConfigurationEvent);
					return (Response.Status.OK.getStatusCode() == response.getStatus()) ? Boolean.TRUE : Boolean.FALSE;
				} catch(RuntimeException e) {
					throw e;
				}
			}
		});
	}

	@Override
	public Boolean doPing(String eventsListenerURL) throws TkStrikeServiceException {
		try {
			return doFuturePing(eventsListenerURL).get();
		} catch(Exception e) {
			throw new TkStrikeServiceException(e);
		}
	}

	@Override
	public Future<Boolean> sendNewStatusEvent(String eventsListenerURL, final TkStrikeNodeStatusEvent statusEvent) throws TkStrikeServiceException {
		final TkStrikeNodeEventsListenerSEI service = getProxy(eventsListenerURL);
		return threadPool.submit(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				try {
					Response response = service.sendNewStatusEvent(statusEvent);
					return (Response.Status.OK.getStatusCode() == response.getStatus()) ? Boolean.TRUE : Boolean.FALSE;
				} catch(RuntimeException e) {
					throw e;
				}
			}
		});
	}

	@Override
	public Future<Boolean> sendNewDataEvent(String eventsListenerURL, final TkStrikeNodeDataEvent dataEvent) throws TkStrikeServiceException {
		final TkStrikeNodeEventsListenerSEI service = getProxy(eventsListenerURL);
		return threadPool.submit(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				try {
					Response response = service.sendNewDataEvent(dataEvent);
					return (Response.Status.OK.getStatusCode() == response.getStatus()) ? Boolean.TRUE : Boolean.FALSE;
				} catch(RuntimeException e) {
					throw e;
				}
			}
		});
	}

	private TkStrikeNodeEventsListenerSEI getProxy(String proxyURL) throws TkStrikeServiceException {
		logger.info("NodeEventsListener url ->" + proxyURL);
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
		return JAXRSClientFactory.create(proxyURL, TkStrikeNodeEventsListenerSEI.class, providers);
	}
}
