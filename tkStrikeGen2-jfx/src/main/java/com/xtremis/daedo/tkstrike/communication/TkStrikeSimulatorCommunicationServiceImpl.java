package com.xtremis.daedo.tkstrike.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;

import javafx.beans.property.SimpleBooleanProperty;


public class TkStrikeSimulatorCommunicationServiceImpl implements TkStrikeCommunicationService {

	private static final Logger _log = Logger.getLogger(TkStrikeSimulatorCommunicationServiceImpl.class);

	private CopyOnWriteArrayList<TkStrikeCommunicationListener> listeners = new CopyOnWriteArrayList<>();

	private ServerSocket serverSocket = null;

	private SimpleBooleanProperty connected = new SimpleBooleanProperty(this, "connected", Boolean.FALSE.booleanValue());

	private NetworkConfigurationDto networkConfiguration;

	private NetworkStatus networkStatus = NetworkStatus.NOT_CONNECTED;

	private ExecutorService socketListenerES = Executors.newSingleThreadExecutor();

	@Override
	public void startComm() throws TkStrikeCommunicationException {
		if(_log.isDebugEnabled())
			_log.debug("Simulator Client start");
		if(this.serverSocket != null)
			stopComm();
		try {
			this.serverSocket = new ServerSocket(9595);
			this.socketListenerES.execute(new SocketServerListener(this.serverSocket));
		} catch(Exception e) {
			throw new TkStrikeCommunicationException(e);
		}
	}

	@Override
	public void stopComm() throws TkStrikeCommunicationException {
		if(_log.isDebugEnabled())
			_log.debug("Simulator Client stop");
		this.socketListenerES.shutdownNow();
		if(this.serverSocket != null && ! this.serverSocket.isClosed()) {
			try {
				this.serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
			this.serverSocket = null;
			System.gc();
		}
	}

	@Override
	public NetworkStatus getCurrentNetworkStatus() throws TkStrikeCommunicationException {
		return this.networkStatus;
	}

	@Override
	public void tryToRecognizeWithConfig(NetworkConfigurationDto networkConfigurationDto, boolean forceInitializerSerial)
			throws TkStrikeCommunicationException {
		startNetwork(networkConfigurationDto);
	}

	@Override
	public void startNetwork(final NetworkConfigurationDto networkConfiguration) throws TkStrikeCommunicationException {
		if(_log.isDebugEnabled())
			_log.debug("StartNetwork Groups ->" + networkConfiguration.getGroupsNumber());
		this.networkConfiguration = networkConfiguration;
		this.connected.set(Boolean.TRUE.booleanValue());
		TkStrikeExecutors.schedule(new Runnable() {

			@Override
			public void run() {
				TkStrikeSimulatorCommunicationServiceImpl.this.fireNewChangeNetworkConfigurationEvent(Long.valueOf(System.currentTimeMillis()),
						TkStrikeSimulatorCommunicationServiceImpl.this.networkStatus, networkConfiguration);
				if(TkStrikeSimulatorCommunicationServiceImpl._log.isDebugEnabled())
					TkStrikeSimulatorCommunicationServiceImpl._log.debug("Has call fireNewChangeNetworkConfigurationEvent");
				NetworkStatus last = TkStrikeSimulatorCommunicationServiceImpl.this.networkStatus;
				TkStrikeSimulatorCommunicationServiceImpl.this.networkStatus = NetworkStatus.OK;
				TkStrikeSimulatorCommunicationServiceImpl.this.fireNewChangeNetworkStatusEvent(Long.valueOf(System.currentTimeMillis()),
						TkStrikeSimulatorCommunicationServiceImpl.this.networkStatus, last);
			}
		}, 1L, TimeUnit.SECONDS);
	}

	@Override
	public NetworkConfigurationDto getCurrentNetworkConfiguration() {
		return this.networkConfiguration;
	}

	@Override
	public void addListener(TkStrikeCommunicationListener tkStrikeCommunicationListener) {
		if( ! this.listeners.contains(tkStrikeCommunicationListener))
			this.listeners.add(tkStrikeCommunicationListener);
	}

	@Override
	public void removeListener(TkStrikeCommunicationListener tkStrikeCommunicationListener) {
		this.listeners.remove(tkStrikeCommunicationListener);
	}

	private void fireNewChangeNetworkConfigurationEvent(Long timestamp, NetworkStatus prevStatus, NetworkConfigurationDto networkConfiguration) {
		final ChangeNetworkConfigurationEvent changeNetworkConfigurationEvent = new ChangeNetworkConfigurationEvent(timestamp, prevStatus,
				networkConfiguration);
		for(TkStrikeCommunicationListener listener : this.listeners) {
			TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					listener.hasChangeNetworkConfigurationEvent(changeNetworkConfigurationEvent);
					return null;
				}
			});
		}
	}

	private void fireNewDataEvent(final DataEvent newDataEvent) {
		for(TkStrikeCommunicationListener listener : this.listeners) {
			TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					listener.hasNewDataEvent(newDataEvent);
					return null;
				}
			});
		}
	}

	private void fireNewStatusEvent(final StatusEvent newStatusEvent) {
		for(TkStrikeCommunicationListener listener : this.listeners) {
			TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

				@Override
				public Void call() {
					listener.hasNewStatusEvent(newStatusEvent);
					return null;
				}
			});
		}
	}

	private void fireNewChangeNetworkStatusEvent(Long timestamp, NetworkStatus prevStatus, NetworkStatus newStatus) {
		final ChangeNetworkStatusEvent changeNetworkStatusEvent = new ChangeNetworkStatusEvent(timestamp, prevStatus, newStatus);
		for(TkStrikeCommunicationListener listener : this.listeners) {
			TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

				@Override
				public Void call() {
					listener.hasChangeNetworkStatusEvent(changeNetworkStatusEvent);
					return null;
				}
			});
		}
	}

	private class SocketServerListener extends Thread {

		private final ServerSocket serverSocket;

		private boolean running = true;

		public SocketServerListener(ServerSocket serverSocket) {
			this.serverSocket = serverSocket;
		}

		@Override
		public void run() {
			while(this.running) {
				try {
					if( ! this.serverSocket.isClosed()) {
						Socket socket = this.serverSocket.accept();
						if(socket != null) {
							BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							String inputLine = null;
							while((inputLine = socketReader.readLine()) != null) {
								if(inputLine.startsWith("STATUS$")) {
									workWithStatusEvent(inputLine.substring(7, inputLine.length()));
									continue;
								}
								if(inputLine.startsWith("STATUS-2$")) {
									workWithStatus2Event(inputLine.substring(9, inputLine.length()));
									continue;
								}
								if(inputLine.startsWith("HITEVENT$"))
									workWithDataEvent(inputLine.substring(9, inputLine.length()));
							}
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void interrupt() {
			super.interrupt();
			this.running = false;
			try {
				this.serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}

		private void workWithStatusEvent(String statusEvent) {
			TkStrikeSimulatorCommunicationServiceImpl._log.debug(statusEvent);
			if(statusEvent != null && statusEvent.contains(";")) {
				String[] eventParts = statusEvent.split(";");
				Long eventTimestamp = Long.valueOf(System.currentTimeMillis());
				if(eventParts.length >= 2) {
					String[] partNode = eventParts[0].split("=");
					String[] partBattery = eventParts[1].split("=");
					if(partNode.length == 2 && partBattery.length == 2) {
						String nodeId = eventParts[0].split("=")[1];
						Integer battery = Integer.valueOf(Integer.parseInt(eventParts[1].split("=")[1]));
						Boolean sensorOk = Boolean.TRUE;
						if(eventParts.length == 3)
							sensorOk = Boolean.valueOf(eventParts[2].split("=")[1]);
						if(TkStrikeSimulatorCommunicationServiceImpl._log.isDebugEnabled())
							TkStrikeSimulatorCommunicationServiceImpl._log.debug(statusEvent + " - Node " + nodeId + " battery " + battery);
						StatusEvent newStatusEvent = new StatusEvent(eventTimestamp, TkStrikeSimulatorCommunicationServiceImpl.this.networkStatus,
								nodeId, Boolean.FALSE, sensorOk, Double.valueOf(battery.doubleValue()), Double.valueOf(battery.doubleValue()));
						TkStrikeSimulatorCommunicationServiceImpl.this.fireNewStatusEvent(newStatusEvent);
					}
				}
			}
		}

		private void workWithStatus2Event(String statusEvent) {
			TkStrikeSimulatorCommunicationServiceImpl._log.debug(statusEvent);
			if(statusEvent != null && statusEvent.contains(";")) {
				String[] eventParts = statusEvent.split(";");
				Long eventTimestamp = Long.valueOf(System.currentTimeMillis());
				if(eventParts.length >= 3) {
					String[] partOffline = eventParts[0].split("=");
					String[] partNode = eventParts[1].split("=");
					String[] partBattery = eventParts[2].split("=");
					if(partOffline.length == 2 && partNode.length == 2 && partBattery.length == 2) {
						Boolean offline = Boolean.valueOf(partOffline[1]);
						String nodeId = partNode[1];
						Integer battery = Integer.valueOf(Integer.parseInt(partBattery[1]));
						Boolean sensorOk = Boolean.TRUE;
						if(eventParts.length == 4)
							sensorOk = Boolean.valueOf(eventParts[3].split("=")[1]);
						if(TkStrikeSimulatorCommunicationServiceImpl._log.isDebugEnabled())
							TkStrikeSimulatorCommunicationServiceImpl._log.debug(statusEvent + " - Node " + nodeId + " battery " + battery);
						StatusEvent newStatusEvent = new StatusEvent(eventTimestamp, TkStrikeSimulatorCommunicationServiceImpl.this.networkStatus,
								nodeId, offline, sensorOk, Double.valueOf(battery.doubleValue()), Double.valueOf(battery.doubleValue()));
						TkStrikeSimulatorCommunicationServiceImpl.this.fireNewStatusEvent(newStatusEvent);
					}
				}
			}
		}

		private void workWithDataEvent(String dataEvent) {
			if(TkStrikeSimulatorCommunicationServiceImpl._log.isDebugEnabled())
				TkStrikeSimulatorCommunicationServiceImpl._log.debug("DataEvent --> " + dataEvent);
			if(dataEvent != null && dataEvent.contains(";")) {
				Long eventTimestamp = Long.valueOf(System.currentTimeMillis());
				String[] eventParts = dataEvent.split(";");
				String nodeId = eventParts[0].split("=")[1];
				String hitValue = eventParts[1].split("=")[1];
				DataEvent newDataEvent = new DataEvent(eventTimestamp, TkStrikeSimulatorCommunicationServiceImpl.this.networkStatus, nodeId, Integer
						.valueOf(Integer.parseInt(hitValue)), DataEvent.DataEventHitType.BODY);
				TkStrikeSimulatorCommunicationServiceImpl.this.fireNewDataEvent(newDataEvent);
			}
		}
	}
}
