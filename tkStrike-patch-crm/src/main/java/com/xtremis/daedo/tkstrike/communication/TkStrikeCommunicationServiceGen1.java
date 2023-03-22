package com.xtremis.daedo.tkstrike.communication;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import com.xtremis.daedo.tkstrike.communication.DataEvent.DataEventHitType;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.utils.NodeConversionUtils;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class TkStrikeCommunicationServiceGen1 implements TkStrikeCommunicationService, Runnable {
	private static final Logger logger = Logger.getLogger(TkStrikeCommunicationServiceGen1.class);
	private static final Logger loggerCommEvent = Logger.getLogger("COMM_EVENT");
	private static final Logger loggerStatusEvent = Logger.getLogger("STATUS_EVENT");
	private static final Logger loggerDataEvent = Logger.getLogger("DATA_EVENT");
	private CopyOnWriteArrayList<TkStrikeCommunicationListener> listeners = new CopyOnWriteArrayList();
	private SerialPort serialPort;
	private TkStrikeCommunicationServiceGen1.CustomSerialListener customSerialListener = new TkStrikeCommunicationServiceGen1.CustomSerialListener();
	private NetworkConfigurationDto networkConfiguration;
	private NetworkStatus networkStatus;
	private Future<?> networkRecognizerFuture;
	private final SimpleBooleanProperty recognitionFinishedProperty;
	@Value("${tkStrike.serialNamePattern}")
	private String serialNamePattern;
	private HashMap<String, String> nodesConversion;
	private int lastRecognizeRemain;

	public TkStrikeCommunicationServiceGen1() {
		logger.info("Start Gen1 communication");
		this.networkStatus = NetworkStatus.NOT_CONNECTED;
		this.networkRecognizerFuture = null;
		this.recognitionFinishedProperty = new SimpleBooleanProperty();
		this.nodesConversion = new HashMap();
		this.lastRecognizeRemain = -1;
	}

	@Override
	public void startComm() throws TkStrikeCommunicationException {
		if (logger.isDebugEnabled()) {
			logger.debug("Start Port Gen1");
		}

		if (this.serialPort == null) {
			String[] portNames = SerialPortList.getPortNames();
			String portName = null;

			for (int i = 0; i < portNames.length; ++i) {
				String tempPortName = portNames[i];
				if (Pattern.compile(this.serialNamePattern).matcher(tempPortName).lookingAt()) {
					SerialPort toValidateSerialPort = new SerialPort(tempPortName);

					try {
						toValidateSerialPort.openPort();
						toValidateSerialPort.setParams(38400, 8, 1, 0);
						toValidateSerialPort.writeBytes("A".getBytes(StandardCharsets.US_ASCII));

						try {
							Thread.sleep(100L);
						} catch (InterruptedException var19) {
						}

						int r = toValidateSerialPort.getInputBufferBytesCount();
						String validatedReaded = new String(toValidateSerialPort.readBytes(r));
						if (logger.isDebugEnabled()) {
							logger.debug("ValidatedReaded = " + validatedReaded);
						}

						if (validatedReaded.endsWith("a")) {
							if (logger.isDebugEnabled()) {
								logger.info("Port " + tempPortName + " is TkStrike OK");
							}

							portName = tempPortName;
						}
					} catch (SerialPortException var20) {
						var20.printStackTrace();
						logger.error("startComm", var20);
					} finally {
						try {
							toValidateSerialPort.closePort();
						} catch (SerialPortException var17) {
						}

					}
				}
			}

			if (portName != null) {
				this.serialPort = new SerialPort(portName);

				try {
					this.serialPort.openPort();
					this.serialPort.setParams(38400, 8, 1, 0);
					this.serialPort.addEventListener(this.customSerialListener);
					NetworkStatus last = this.networkStatus;
					this.networkStatus = NetworkStatus.NOT_STARTED;
					this.fireNewChangeNetworkStatusEvent(System.currentTimeMillis(), this.networkStatus, last);
					return;
				} catch (SerialPortException var18) {
					var18.printStackTrace();
					logger.error("startComm", var18);
					this.serialPort = null;
					throw new TkStrikeCommunicationException(var18);
				}
			}

			if (this.serialPort == null) {
				throw new TkStrikeCommunicationException("");
			}
		}

	}

	@Override
	public void stopComm() throws TkStrikeCommunicationException {
		if (this.serialPort != null) {
			boolean var6 = false;

			try {
				var6 = true;
				this.serialPort.removeEventListener();
				this.serialPort.closePort();
				var6 = false;
			} catch (SerialPortException var7) {
				logger.error("stopComm", var7);
				throw new TkStrikeCommunicationException(var7);
			} finally {
				if (var6) {
					this.serialPort = null;
					this.customSerialListener.reset();
					NetworkStatus last = this.networkStatus;
					this.networkStatus = NetworkStatus.NOT_CONNECTED;
					this.fireNewChangeNetworkStatusEvent(System.currentTimeMillis(), this.networkStatus, last);
				}
			}

			this.serialPort = null;
			this.customSerialListener.reset();
			NetworkStatus last = this.networkStatus;
			this.networkStatus = NetworkStatus.NOT_CONNECTED;
			this.fireNewChangeNetworkStatusEvent(System.currentTimeMillis(), this.networkStatus, last);
		}

	}

	@Override
	public NetworkStatus getCurrentNetworkStatus() throws TkStrikeCommunicationException {
		return this.networkStatus;
	}

	@Override
	public void tryToRecognizeWithConfig(NetworkConfigurationDto networkConfigurationDto,
			boolean forceInitializerSerial) throws TkStrikeCommunicationException {
		if (networkConfigurationDto.getNetworkWasStarted()) {
			if (forceInitializerSerial) {
				this.stopComm();
				this.startComm();
			} else if (this.serialPort == null) {
				return;
			}

			this.startNetwork(networkConfigurationDto);
		}

	}

	protected void startNetwork(LinkedHashSet<String> networkComponents, Integer channel)
			throws TkStrikeCommunicationException {
		this.validateSerial();
		this.doSendA();
		this.sendNetworkComposition(networkComponents);
		this.sendChannel(channel);
		this.sendStartNet();
		NetworkStatus last = this.networkStatus;
		this.networkStatus = NetworkStatus.OK;
		this.fireNewChangeNetworkStatusEvent(System.currentTimeMillis(), this.networkStatus, last);
	}

	@Override
	public void startNetwork(NetworkConfigurationDto networkConfiguration) throws TkStrikeCommunicationException {
		this.networkConfiguration = networkConfiguration;
		LinkedHashSet<String> networkComponents = new LinkedHashSet();
		if (networkConfiguration.getJudgesNumber() > 0
				&& StringUtils.isNotBlank(networkConfiguration.getJudge1NodeId())) {
			networkComponents.add(networkConfiguration.getJudge1NodeId());
		}

		if (networkConfiguration.getJudgesNumber() > 1
				&& StringUtils.isNotBlank(networkConfiguration.getJudge2NodeId())) {
			networkComponents.add(networkConfiguration.getJudge2NodeId());
		}

		if (networkConfiguration.getJudgesNumber() > 2
				&& StringUtils.isNotBlank(networkConfiguration.getJudge3NodeId())) {
			networkComponents.add(networkConfiguration.getJudge3NodeId());
		}

		NetworkAthletesGroupConfigDto group1 = networkConfiguration.getGroup1Config();
		if (group1 != null) {
			if (group1.getBodySensorsEnabled() && StringUtils.isNotBlank(group1.getBodyBlueNodeId())) {
				networkComponents.add(group1.getBodyBlueNodeId());
			}

			if (group1.getHeadSensorsEnabled() && StringUtils.isNotBlank(group1.getHeadBlueNodeId())) {
				networkComponents.add(group1.getHeadBlueNodeId());
			}

			if (group1.getBodySensorsEnabled() && StringUtils.isNotBlank(group1.getBodyRedNodeId())) {
				networkComponents.add(group1.getBodyRedNodeId());
			}

			if (group1.getHeadSensorsEnabled() && StringUtils.isNotBlank(group1.getHeadRedNodeId())) {
				networkComponents.add(group1.getHeadRedNodeId());
			}
		}

		this.startNetwork(networkComponents, networkConfiguration.getChannelNumber());
		this.fireNewChangeNetworkConfigurationEvent(System.currentTimeMillis(), this.networkStatus,
				networkConfiguration);
	}

	@Override
	public NetworkConfigurationDto getCurrentNetworkConfiguration() {
		return this.networkConfiguration;
	}

	private void validateSerial() throws TkStrikeCommunicationException {
		if (this.serialPort == null) {
			this.startComm();
		}

	}

	private void doSendA() throws TkStrikeCommunicationException {
		if (this.serialPort != null) {
			try {
				this.serialPort.writeBytes("A".getBytes(StandardCharsets.US_ASCII));
			} catch (SerialPortException var2) {
				logger.error("sendA", var2);
				throw new TkStrikeCommunicationException(var2);
			}
		}

	}

	private void sendNetworkComposition(LinkedHashSet<String> networkComponents) throws TkStrikeCommunicationException {
		if (this.serialPort != null && networkComponents != null && networkComponents.size() > 0) {
			try {
				byte[] bytesToSend = new byte[0];
				byte b = 1;
				bytesToSend = ArrayUtils.add(bytesToSend, (byte) 1);
				bytesToSend = ArrayUtils.addAll(bytesToSend, new byte[] { 0, 1 });
				switch (networkComponents.size()) {
				case 1:
					b = 1;
					break;
				case 2:
					b = 2;
					break;
				case 3:
					b = 3;
					break;
				case 4:
					b = 4;
					break;
				case 5:
					b = 5;
					break;
				case 6:
					b = 6;
					break;
				case 7:
					b = 7;
					break;
				case 8:
					b = 8;
					break;
				case 9:
					b = 9;
					break;
				case 10:
					b = 16;
					break;
				case 11:
					b = 17;
					break;
				case 12:
					b = 18;
				}

				bytesToSend = ArrayUtils.addAll(bytesToSend, new byte[] { b });
				this.nodesConversion.clear();
				String nodesHex = "";

				String nodeHex;
				for (Iterator var5 = networkComponents.iterator(); var5.hasNext(); nodesHex = nodesHex + nodeHex) {
					String networkComponent = (String) var5.next();
					nodeHex = NodeConversionUtils.getHexFromNodeIdString(networkComponent);
					this.nodesConversion.put(nodeHex, networkComponent);
					loggerStatusEvent.debug("PUT NODES CONVERSION :" + nodeHex + " : " + networkComponent);
				}

				byte[] nodes = Hex.decodeHex(nodesHex.toCharArray());
				bytesToSend = ArrayUtils.addAll(bytesToSend, nodes);
				logger.debug(
						"<------------> sendNetworkComposition()" + ToStringBuilder.reflectionToString(bytesToSend));
				this.serialPort.writeBytes(bytesToSend);
			} catch (Exception var8) {
				logger.error("sendNetworkComposition", var8);
				throw new TkStrikeCommunicationException(var8);
			}
		}

	}

	private void sendChannel(Integer channel) {
		if (this.serialPort != null) {
			try {
				logger.debug("<------------> sendChannel");
				this.serialPort.writeBytes(("*" + channel).getBytes(StandardCharsets.US_ASCII));
			} catch (Exception var3) {
				logger.error("sendChannel", var3);
				throw new TkStrikeCommunicationException(var3);
			}
		}

	}

	private void sendStartNet() {
		if (this.serialPort != null) {
			try {
				logger.debug("<------------> sendStartNet()");
				this.serialPort.writeBytes("B".getBytes(StandardCharsets.US_ASCII));
			} catch (Exception var2) {
				logger.error("sendStartNet", var2);
				throw new TkStrikeCommunicationException(var2);
			}
		}

	}

	@Override
	public void addListener(TkStrikeCommunicationListener tkStrikeCommunicationListener) {
		if (!this.listeners.contains(tkStrikeCommunicationListener)) {
			this.listeners.add(tkStrikeCommunicationListener);
		}

	}

	@Override
	public void removeListener(TkStrikeCommunicationListener tkStrikeCommunicationListener) {
		this.listeners.remove(tkStrikeCommunicationListener);
	}

	@Override
	public void run() {
	}

	private void fireNewDataEvent(final DataEvent newDataEvent) {
		Iterator var2 = this.listeners.iterator();

		while (var2.hasNext()) {
			final TkStrikeCommunicationListener listener = (TkStrikeCommunicationListener) var2.next();
			TkStrikeExecutors.executeInThreadPool(new Runnable() {
				@Override
				public void run() {
					listener.hasNewDataEvent(newDataEvent);
				}
			});
		}

	}

	private void fireNewStatusEvent(final StatusEvent newStatusEvent) {
		Iterator var2 = this.listeners.iterator();

		while (var2.hasNext()) {
			final TkStrikeCommunicationListener listener = (TkStrikeCommunicationListener) var2.next();
			TkStrikeExecutors.executeInThreadPool(new Runnable() {
				@Override
				public void run() {
					listener.hasNewStatusEvent(newStatusEvent);
				}
			});
		}

	}

	private void fireNewChangeNetworkStatusEvent(Long timestamp, NetworkStatus prevStatus, NetworkStatus newStatus) {
		final ChangeNetworkStatusEvent changeNetworkStatusEvent = new ChangeNetworkStatusEvent(timestamp, prevStatus,
				newStatus);
		Iterator var5 = this.listeners.iterator();

		while (var5.hasNext()) {
			final TkStrikeCommunicationListener listener = (TkStrikeCommunicationListener) var5.next();
			TkStrikeExecutors.executeInThreadPool(new Runnable() {
				@Override
				public void run() {
					listener.hasChangeNetworkStatusEvent(changeNetworkStatusEvent);
				}
			});
		}

	}

	private void fireNewChangeNetworkConfigurationEvent(Long timestamp, NetworkStatus prevStatus,
			NetworkConfigurationDto networkConfiguration) {
		final ChangeNetworkConfigurationEvent changeNetworkConfigurationEvent = new ChangeNetworkConfigurationEvent(
				timestamp, prevStatus, networkConfiguration);
		Iterator var5 = this.listeners.iterator();

		while (var5.hasNext()) {
			final TkStrikeCommunicationListener listener = (TkStrikeCommunicationListener) var5.next();
			TkStrikeExecutors.executeInThreadPool(new Runnable() {
				@Override
				public void run() {
					listener.hasChangeNetworkConfigurationEvent(changeNetworkConfigurationEvent);
				}
			});
		}

	}

	class CustomSerialListener implements SerialPortEventListener {
		private SimpleStringProperty statusEventProperty = new SimpleStringProperty("");
		private SimpleStringProperty dataEventProperty = new SimpleStringProperty("");
		private String previusPack = null;
		private ConcurrentHashMap<String, Long> sourceLastDataPackedTimestamp = new ConcurrentHashMap();
		private ConcurrentHashMap<String, String> sourceLastDataPacked = new ConcurrentHashMap();

		public ObservableStringValue getStatusEvent() {
			return this.statusEventProperty;
		}

		public ObservableStringValue getDataEvent() {
			return this.dataEventProperty;
		}

		public void reset() {
			this.previusPack = null;
		}

		@Override
		public void serialEvent(SerialPortEvent serialPortEvent) {
			try {
				if (serialPortEvent != null && 1 == serialPortEvent.getEventType()) {
					int r = TkStrikeCommunicationServiceGen1.this.serialPort.getInputBufferBytesCount();
					String hexReaded = TkStrikeCommunicationServiceGen1.this.serialPort.readHexString(r, "-");
					hexReaded = hexReaded.toLowerCase().replaceAll("-", "");
					TkStrikeCommunicationServiceGen1.loggerCommEvent
							.debug("buffer = " + r + " eventValue =" + serialPortEvent.getEventValue());
					TkStrikeCommunicationServiceGen1.loggerCommEvent
							.debug("Get COMM event " + serialPortEvent.getEventType() + " "
									+ serialPortEvent.getEventValue() + " HEX - " + hexReaded);
					TkStrikeCommunicationServiceGen1.logger.debug("HEX READED ->" + hexReaded);
					if (hexReaded.equals("ff")) {
						TkStrikeCommunicationServiceGen1.logger.debug("Lectura de error...");
					} else if ("a".equals(hexReaded)) {
						TkStrikeCommunicationServiceGen1.logger.debug("Llegim A");
					} else if ("312a3133".equals(hexReaded)) {
						TkStrikeCommunicationServiceGen1.logger.debug("Llegim Inicialització de xarxa?");
					} else {
						if (this.previusPack != null) {
							hexReaded = this.previusPack + hexReaded;
							this.previusPack = null;
						}

						String strTemp = hexReaded;
						TkStrikeCommunicationServiceGen1.logger.debug("STR TEMP = " + hexReaded);

						for (int i = 0; strTemp.startsWith("fe01433d") || strTemp.indexOf("fe01433d") > 0
								|| strTemp.startsWith("fe01443d") || strTemp.indexOf("fe01443d") > 0; ++i) {
							int posControlPack = strTemp.indexOf("fe01433d");
							int posDataPack = strTemp.indexOf("fe01443d");
							if (posControlPack > 0 && (posDataPack <= 0 || posDataPack >= posControlPack)) {
								TkStrikeCommunicationServiceGen1.logger
										.debug("CONTROL - netejem la part que no ens interessa");
								strTemp = strTemp.substring(strTemp.indexOf("fe01433d"), strTemp.length());
							}

							if (posDataPack > 0) {
								TkStrikeCommunicationServiceGen1.logger
										.debug("EVENT netejem la part que no ens interessa");
								strTemp = strTemp.substring(strTemp.indexOf("fe01443d"), strTemp.length());
							}

							String paquetBase26;
							String paquetBase28;
							if (strTemp.startsWith("fe01433d") || strTemp.indexOf("fe01433d") > 0) {
								if (strTemp.length() >= 26) {
									paquetBase26 = strTemp.substring(0, 26);
									if (paquetBase26.endsWith("0d")) {
										TkStrikeCommunicationServiceGen1.logger
												.debug("Paquet llegit BASE " + i + " -> " + paquetBase26);
										this.workWithStatusEvent(paquetBase26);
										strTemp = strTemp.substring(26, strTemp.length());
									} else if (paquetBase26.endsWith("fe") && strTemp.length() >= 28
											&& strTemp.substring(26, 28).equals("01")) {
										TkStrikeCommunicationServiceGen1.logger.debug("Paquet llegit INCOMPLERT " + i
												+ " -> " + paquetBase26.substring(0, 24));
										this.workWithStatusEvent(paquetBase26.substring(0, 24));
										strTemp = strTemp.substring(24, strTemp.length());
									} else if (paquetBase26.endsWith("ff") && strTemp.length() >= 28
											&& strTemp.substring(26, 28).equals("0d")) {
										TkStrikeCommunicationServiceGen1.logger.debug(
												"Paquet llegit MES LLARG " + i + " -> " + strTemp.substring(0, 28));
										this.workWithStatusEvent(strTemp.substring(0, 28));
										strTemp = strTemp.substring(28, strTemp.length());
									} else {
										TkStrikeCommunicationServiceGen1.logger
												.debug("ESTEM EN UN CAS QUE NO SABEM QUE FER!!" + strTemp);
										paquetBase28 = strTemp.substring(0, 26);
										this.workWithStatusEvent(paquetBase28);
										if (strTemp.length() > 26) {
											strTemp = strTemp.substring(26, strTemp.length());
										} else {
											this.previusPack = null;
											strTemp = "";
										}
									}
								} else if (strTemp.length() < 26) {
									if (strTemp.endsWith("0d")) {
										TkStrikeCommunicationServiceGen1.logger
												.debug("Paquet llegit MÉS PETIT " + i + " -> " + strTemp);
										this.workWithStatusEvent(strTemp);
										strTemp = "";
									} else {
										this.previusPack = strTemp;
										strTemp = "";
									}
								}
							}

							if (strTemp.startsWith("fe01443d") || strTemp.indexOf("fe01443d") > 0) {
								if (strTemp.length() >= 26) {
									paquetBase26 = strTemp.substring(0, 26);
									paquetBase28 = "";
									if (strTemp.length() >= 28) {
										paquetBase28 = strTemp.substring(0, 28);
									}

									if (paquetBase26.endsWith("0d")) {
										TkStrikeCommunicationServiceGen1.logger
												.debug("Paquet EVENT llegit BASE " + i + " -> " + paquetBase26);
										this.workWithDataEvent(paquetBase26);
										strTemp = strTemp.substring(26, strTemp.length());
									} else if (paquetBase28.endsWith("0d")) {
										TkStrikeCommunicationServiceGen1.logger
												.debug("Paquet EVENT llegit BASE de 28 " + i + " -> " + paquetBase28);
										this.workWithDataEvent(paquetBase28);
										strTemp = strTemp.substring(28, strTemp.length());
									} else if (!paquetBase28.startsWith("fe") && strTemp.indexOf("fe014") < 26) {
										TkStrikeCommunicationServiceGen1.logger
												.debug(" EVENT ESTEM EN UN CAS QUE NO SABEM QUE FER!!" + strTemp);
										this.previusPack = strTemp;
										strTemp = "";
									} else {
										this.workWithDataEvent(paquetBase26);
										strTemp = strTemp.substring(26, strTemp.length());
									}
								} else if (strTemp.length() < 26) {
									if (strTemp.endsWith("0d")) {
										TkStrikeCommunicationServiceGen1.logger
												.debug("EVENT Paquet llegit MÉS PETIT " + i + " -> " + strTemp);
										this.workWithDataEvent(strTemp);
										strTemp = "";
									} else {
										this.previusPack = strTemp;
										strTemp = "";
									}
								}
							}
						}
					}

					TkStrikeCommunicationServiceGen1.logger
							.debug("=====================================================================");
					TkStrikeCommunicationServiceGen1.logger.debug("RX event hex = " + hexReaded);
					TkStrikeCommunicationServiceGen1.logger
							.debug("=====================================================================");
				}
			} catch (Exception var10) {
				TkStrikeCommunicationServiceGen1.logger.error("on serialEvent", var10);
			}

		}

		private void workWithStatusEvent(String statusEvent) {
			TkStrikeCommunicationServiceGen1.loggerStatusEvent.debug(statusEvent);
			if (statusEvent.length() == 26 && statusEvent.startsWith("fe01433d") && statusEvent.endsWith("0d")) {
				Long eventTimestamp = System.currentTimeMillis();
				Boolean sensorOk = Boolean.TRUE;
				String statusSensor = statusEvent.substring(8, 10);
				sensorOk = Integer.parseInt(statusSensor, 16) == 0 || Integer.parseInt(statusSensor, 16) != 1;
				String batteryStr = statusEvent.substring(10, 12);
				String strNodeBlockHex = statusEvent.substring(12, 16);
				String strNodeBlock = TkStrikeCommunicationServiceGen1.this.nodesConversion.get(strNodeBlockHex);
				TkStrikeCommunicationServiceGen1.loggerStatusEvent
						.debug("NodeHex " + strNodeBlockHex + " Node " + strNodeBlock);
				TkStrikeCommunicationServiceGen1.loggerStatusEvent.debug("NodeConversion KeySet.size = "
						+ TkStrikeCommunicationServiceGen1.this.nodesConversion.keySet().size());
				Double battery = Integer.parseInt(batteryStr, 16) * 0.0235D;
				Double batteryPct = battery <= 3.59D ? 0.0D : (battery >= 4.2D ? 100.0D : battery * 100.0D / 4.2D);
				TkStrikeCommunicationServiceGen1.loggerStatusEvent
						.debug(statusEvent + " - Node " + strNodeBlock + " battery " + battery);
				StatusEvent newStatusEvent = new StatusEvent(eventTimestamp,
						TkStrikeCommunicationServiceGen1.this.networkStatus, strNodeBlock, Boolean.FALSE, sensorOk,
						battery, batteryPct);
				TkStrikeCommunicationServiceGen1.this.fireNewStatusEvent(newStatusEvent);
			}

		}

		private void workWithDataEvent(String dataEvent) {
			if (dataEvent.startsWith("fe01443d")) {
				TkStrikeCommunicationServiceGen1.logger.debug("DataEvent --> " + dataEvent);
				Long eventTimestamp = System.currentTimeMillis();
				String strNodeBlockHex = dataEvent.substring(12, 16);
				String strNodeBlock = TkStrikeCommunicationServiceGen1.this.nodesConversion.get(strNodeBlockHex);
				if (StringUtils.isNotBlank(strNodeBlock)) {
					String lastPacked4Source = this.sourceLastDataPacked.get(strNodeBlock);
					Long lastEvent4Source = this.sourceLastDataPackedTimestamp.get(strNodeBlock);
					TkStrikeCommunicationServiceGen1.logger.debug(
							"*** - Source " + strNodeBlock + " last " + lastPacked4Source + " current " + dataEvent);
					TkStrikeCommunicationServiceGen1.logger.debug(
							"Source " + strNodeBlock + " last " + lastEvent4Source + " current " + eventTimestamp);
					if (!dataEvent.equals(lastPacked4Source)) {
						if (lastEvent4Source != null && eventTimestamp - lastEvent4Source < 500L) {
							TkStrikeCommunicationServiceGen1.logger.debug("No throw event of source " + strNodeBlock
									+ " last " + lastEvent4Source + " current " + eventTimestamp);
						} else {
							TkStrikeCommunicationServiceGen1.logger.debug("Throws new Data Event");
							this.sourceLastDataPacked.put(strNodeBlock, dataEvent);
							this.sourceLastDataPackedTimestamp.put(strNodeBlock, eventTimestamp);
							String hitValue = dataEvent.substring(8, 12);
							TkStrikeCommunicationServiceGen1.loggerDataEvent
									.debug(dataEvent + " - Event node ->" + strNodeBlock + "  = " + hitValue);
							Integer baseHit = Integer.valueOf(hitValue, 16);
							if (baseHit != 43521 && baseHit != 43522 && baseHit != 43552 && baseHit != 43524
									&& baseHit != 43528 && baseHit != 43536) {
								baseHit = baseHit / 16;
							}

							DataEvent newDataEvent = new DataEvent(eventTimestamp,
									TkStrikeCommunicationServiceGen1.this.networkStatus, strNodeBlock, baseHit,
									DataEventHitType.BODY);
							TkStrikeCommunicationServiceGen1.this.fireNewDataEvent(newDataEvent);
						}
					} else {
						TkStrikeCommunicationServiceGen1.logger.debug("Data packet " + dataEvent + " repeated!");
					}
				} else {
					TkStrikeCommunicationServiceGen1.logger
							.debug("Data packet of NodeHex " + strNodeBlockHex + " not found!");
				}
			}

		}
	}
}
