package com.xtremis.daedo.tkstrike.ei.client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.service.UdpEventListenerService;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchConfigurationDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeEventDto;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.model.UdpEventListenerEntry;


@Service
@Scope("singleton")
public class TkStrikeUDPFacadeEventsListenerClientImpl implements TkStrikeUDPFacadeEventsListenerClient, InitializingBean {

	private static final Logger loggerEI = Logger.getLogger("EXTERNAL_INTEGRATION");

	private final UdpEventListenerService udpEventListenerService;

	private static final List<Callable<Void>> tasks = new ArrayList<>();

	@Autowired
	public TkStrikeUDPFacadeEventsListenerClientImpl(UdpEventListenerService udpEventListenerService) {
		this.udpEventListenerService = udpEventListenerService;
	}

	@Override
	public void updateUDPSockets() {
		if(loggerEI.isDebugEnabled())
			loggerEI.debug("Call to updateUDPSockets");
		tasks.clear();
		try {
			List<UdpEventListenerEntry> udpEventListenerEntries = this.udpEventListenerService.findAllEntries();
			if(udpEventListenerEntries != null)
				for(UdpEventListenerEntry entry : udpEventListenerEntries) {
					if(loggerEI.isDebugEnabled())
						loggerEI.debug("Create new UDP Socket to " + entry.toString());
					tasks.add(new SendToUdpCallable(entry));
				}
		} catch(Exception e) {
			loggerEI.error("Error updating UDP Entries", e);
		}
	}

	@Override
	public void sendNewMatchEvent(TkStrikeEventDto tkStrikeEventDto) throws TkStrikeServiceException {
		if(tkStrikeEventDto != null)
			try {
				String strTotal = tkStrikeEventDto.toJSON().toString();
				if(loggerEI.isDebugEnabled())
					loggerEI.debug("To sendNewMatchEvent -> " + strTotal);
				_sendJSON(strTotal);
			} catch(Exception e) {
				loggerEI.error("Exception", e);
			}
	}

	@Override
	public void sendHasNewMatchConfigured(MatchConfigurationDto matchConfigurationDto) throws TkStrikeServiceException {
		if(matchConfigurationDto != null)
			try {
				String strTotal = matchConfigurationDto.toJSON().toString();
				if(loggerEI.isDebugEnabled())
					loggerEI.debug("To sendHasNewMatchConfigured -> " + strTotal);
				_sendJSON(strTotal);
			} catch(Exception e) {
				loggerEI.error("Exception", e);
			}
	}

	private void _sendJSON(String str) {
		try {
			if(str != null) {
				byte[] bytesToSend = str.getBytes("UTF-8");
				tasks.forEach(callable -> ((SendToUdpCallable)callable).setBytesToSend(bytesToSend));
				TkStrikeExecutors.executeInParallel(tasks);
			}
		} catch(Exception e) {
			loggerEI.error("Exception", e);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		updateUDPSockets();
	}

	class SendToUdpCallable implements Callable<Void> {

		private final DatagramSocket datagramSocket;

		private final InetAddress targetAddress;

		private final int targetPort;

		private byte[] bytesToSend = null;

		public SendToUdpCallable(UdpEventListenerEntry entry) throws Exception {
			this.datagramSocket = new DatagramSocket();
			this.targetAddress = InetAddress.getByName(entry.getUdpEventListenerIp());
			this.targetPort = entry.getUdpEventListenerPort();
		}

		public void setBytesToSend(byte[] bytesToSend) {
			this.bytesToSend = bytesToSend;
		}

		@Override
		public Void call() throws Exception {
			if(TkStrikeUDPFacadeEventsListenerClientImpl.loggerEI.isDebugEnabled())
				TkStrikeUDPFacadeEventsListenerClientImpl.loggerEI.debug("To Send on " + this.targetAddress.toString() + " : " + this.targetPort);
			if(this.bytesToSend != null)
				this.datagramSocket.send(new DatagramPacket(this.bytesToSend, this.bytesToSend.length, this.targetAddress, this.targetPort));
			return null;
		}
	}
}
