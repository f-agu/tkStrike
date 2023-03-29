package com.xtremis.daedo.tkstrike.communication;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class TkStrikeCommunicationServiceImpl implements TkStrikeCommunicationService, Runnable, InitializingBean {
	private static final Logger logger = Logger.getLogger(TkStrikeCommunicationServiceImpl.class);
	private static final Logger loggerCommEvent = Logger.getLogger("COMM_EVENT");
	private static final Logger loggerStatusEvent = Logger.getLogger("STATUS_EVENT");
	private static final Logger loggerDataEvent = Logger.getLogger("DATA_EVENT");
	private static final String SEND_4_VALIDATE = "v?";
	private static final String RX_4_VALIDATE = "TS_GEN2";
	private static final String GET_COMM_DATA_TOGGLE = "***";
	private static final String SEND_SHOWID = "showID?";
	private static final String STAUTS_PACKET_COM_SEPARATOR = new String(new byte[] { 2 });
	private static final String STAUTS_PACKET_COM_HEADER = new String(new byte[] { 1 });
	private static final String STAUTS_PACKET_HEADER = "#";
	private static final String STAUTS_PACKET_SEPARATOR = "-";
	private ConcurrentHashMap<String, Long> headNodeLastHitTimestamp = new ConcurrentHashMap();
	private ConcurrentHashMap<String, Long> judgeNodeLastClickTimestamp = new ConcurrentHashMap(18);
	private SimpleIntegerProperty judge1NodeBadTimes = new SimpleIntegerProperty(this, "judge1NodeBadTimes", 0);
	private SimpleIntegerProperty judge2NodeBadTimes = new SimpleIntegerProperty(this, "judge2NodeBadTimes", 0);
	private SimpleIntegerProperty judge3NodeBadTimes = new SimpleIntegerProperty(this, "judge3NodeBadTimes", 0);
	private CopyOnWriteArrayList<TkStrikeCommunicationListener> listeners = new CopyOnWriteArrayList();
	private SerialPort serialPort = null;
	private TkStrikeCommunicationServiceImpl.CustomSerialListener customSerialListener = new TkStrikeCommunicationServiceImpl.CustomSerialListener();
	private boolean getDataEnabled = false;
	private boolean hasValidateDataEnabled = false;
	private boolean hasSendShowID = false;
	private NetworkConfigurationDto networkConfiguration;
	private NetworkStatus networkStatus;
	@Value("${tkStrike.gen2.maxNetworkAthleteGroupsAllowed}")
	private Integer maxNetworkAthleteGroupsAllowed;
	@Value("${tkStrike.gen2.headHitTimeValidation}")
	private Long headHitTimeValidation;
	@Value("${tkStrike.gen2.judgeClickTimeValidation}")
	private Long judgeClickTimeValidation;
	@Value("${tkStrike.gen2.nodeConnBadTimesAllowed}")
	private Integer nodeConnBadTimesAllowed;
	@Value("${tkStrike.serialNamePattern}")
	private String serialNamePattern;
	@Value("${tkStrike.comm.bodyGap}")
	private Integer initBodyGap;
	@Value("${tkStrike.comm.headGap}")
	private Integer initHeadGap;
	private static final Pattern isStatusEventPattern = Pattern.compile("^c[b|r|j]");
	private static final Pattern isHitEventPattern = Pattern.compile("^[j[1-3]|bb|bh|rb|rh|bp|rp]");
	private static final Pattern isShowIDResponsePattern = Pattern.compile("^showID\\?|n[1-8]|Judge[1-3]:");

	public TkStrikeCommunicationServiceImpl() {
		this.networkStatus = NetworkStatus.NOT_CONNECTED;
	}

	@Override
	public synchronized void startComm() throws TkStrikeCommunicationException {
		if (logger.isDebugEnabled()) {
			logger.debug("Start Port Gen2");
		}

		if (this.serialPort == null) {
			String portName = null;

			try {
				String[] portNames = SerialPortList.getPortNames();

				for (int i = 0; i < portNames.length; ++i) {
					String tempPortName = portNames[i];
					if (logger.isDebugEnabled()) {
						logger.debug("Port Name = " + tempPortName);
					}

					if (Pattern.compile(this.serialNamePattern).matcher(tempPortName).lookingAt()) {
						SerialPort toValidateSerialPort = new SerialPort(tempPortName);

						try {
							toValidateSerialPort.openPort();
							toValidateSerialPort.setParams(38400, 8, 1, 0);
							toValidateSerialPort.writeString("v?", StandardCharsets.US_ASCII.name());
							toValidateSerialPort.writeByte((byte) 13);
							TimeUnit.SECONDS.sleep(1L);
							int r = toValidateSerialPort.getInputBufferBytesCount();
							String readed = toValidateSerialPort.readString(r);
							if (readed.trim().contains("TS_GEN2")) {
								if (logger.isDebugEnabled()) {
									logger.info("Port " + tempPortName + " is TkStrike GEN2 OK");
								}

								portName = tempPortName;
								toValidateSerialPort.writeString("bodygap=" + this.initBodyGap.toString(),
										StandardCharsets.US_ASCII.name());
								toValidateSerialPort.writeByte((byte) 13);
								toValidateSerialPort.writeString("headgap=" + this.initHeadGap.toString(),
										StandardCharsets.US_ASCII.name());
								toValidateSerialPort.writeByte((byte) 13);
								toValidateSerialPort.writeString("gap?", StandardCharsets.US_ASCII.name());
								toValidateSerialPort.writeByte((byte) 13);
								TimeUnit.SECONDS.sleep(1L);
								r = toValidateSerialPort.getInputBufferBytesCount();
								readed = toValidateSerialPort.readString(r);
								logger.info("CurrentGap? " + readed);
								readed = null;
							}
						} catch (Exception var19) {
							logger.error("startComm", var19);
						} finally {
							try {
								toValidateSerialPort.closePort();
							} catch (SerialPortException var18) {
							}

						}
					}
				}
			} catch (Exception var21) {
				logger.error("startComm", var21);
			}

			if (portName != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Open port " + portName);
				}

				this.serialPort = new SerialPort(portName);

				try {
					this.serialPort.openPort();
					this.serialPort.setParams(38400, 8, 1, 0);
					this._validateIfIsGetDataEnabled();
					this.serialPort.addEventListener(this.customSerialListener);
					NetworkStatus last = this.networkStatus;
					this.networkStatus = NetworkStatus.NOT_STARTED;
					this.fireNewChangeNetworkStatusEvent(System.currentTimeMillis(), this.networkStatus, last);
					return;
				} catch (SerialPortException var17) {
					var17.printStackTrace();
					logger.error("startComm", var17);
					this.serialPort = null;
					throw new TkStrikeCommunicationException(var17);
				}
			}
		}

		if (this.serialPort == null) {
			throw new TkStrikeCommunicationException("");
		}
	}

	@Override
	public void stopComm() throws TkStrikeCommunicationException {
		if (this.serialPort != null) {
			boolean var6 = false;

			try {
				var6 = true;
				this._stopGetData();
				this.serialPort.removeEventListener();
				this.serialPort.closePort();
				var6 = false;
			} catch (SerialPortException var7) {
				logger.error("stopComm", var7);
				throw new TkStrikeCommunicationException(var7);
			} finally {
				if (var6) {
					this.serialPort = null;
					NetworkStatus last = this.networkStatus;
					this.networkStatus = NetworkStatus.NOT_CONNECTED;
					this.fireNewChangeNetworkStatusEvent(System.currentTimeMillis(), this.networkStatus, last);
				}
			}

			this.serialPort = null;
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
		logger.debug("tryToRecognizeWithConfig() method");
		this._cleanAllNodes();

		try {
			this._sendShowID();
		} catch (SerialPortException var4) {
			var4.printStackTrace();
			throw new TkStrikeCommunicationException(var4);
		}
	}

	@Override
	public void startNetwork(NetworkConfigurationDto networkConfiguration) throws TkStrikeCommunicationException {
		if (networkConfiguration != null) {
			this.stopComm();
			if (logger.isDebugEnabled()) {
				logger.debug("Start netvork with " + ToStringBuilder.reflectionToString(networkConfiguration));
			}

			try {
				this._stopGetData();
				networkConfiguration.calculateNumberOfGroups();
				logger.info("------ NETWORK GROUPS = " + networkConfiguration.getGroupsNumber());
				this.doInitializeGroupsNumber(networkConfiguration.getGroupsNumber());
				NetworkStatus last = this.networkStatus;
				this.networkStatus = NetworkStatus.NOT_STARTED;
				this.fireNewChangeNetworkStatusEvent(System.currentTimeMillis(), this.networkStatus, last);
				this._cleanAllNodes();
				int currNodeCount = 1;

				for (int i = 1; i <= this.maxNetworkAthleteGroupsAllowed; ++i) {
					NetworkAthletesGroupConfigDto groupConfig = networkConfiguration.getNetworkAthletesGroupConfig(i);
					if (groupConfig != null) {
						boolean bodyEnabled = groupConfig.getBodySensorsEnabled();
						boolean headEnabled = groupConfig.getHeadSensorsEnabled();
						boolean groupEnabled = networkConfiguration.getGroupsNumber() >= i;
						logger.debug("GROUP " + i + " enabled?" + groupEnabled);
						logger.debug("BODY " + i + " enabled?" + bodyEnabled);
						logger.debug("HEAD " + i + " enabled?" + headEnabled);
						String blueBody = groupEnabled && bodyEnabled && groupConfig.getBodyBlueNodeId() != null
								? groupConfig.getBodyBlueNodeId()
								: "0";
						String blueHead = groupEnabled && headEnabled && groupConfig.getHeadBlueNodeId() != null
								? groupConfig.getHeadBlueNodeId()
								: "0";
						String redBody = groupEnabled && bodyEnabled && groupConfig.getBodyRedNodeId() != null
								? groupConfig.getBodyRedNodeId()
								: "0";
						String redHead = groupEnabled && headEnabled && groupConfig.getHeadRedNodeId() != null
								? groupConfig.getHeadRedNodeId()
								: "0";
						logger.debug("TO SEND n" + currNodeCount + "=" + blueBody);
						this._sendString2Serial("n" + currNodeCount + "=" + blueBody);
						++currNodeCount;
						logger.debug("TO SEND n" + currNodeCount + "=" + blueHead);
						this._sendString2Serial("n" + currNodeCount + "=" + blueHead);
						++currNodeCount;
						logger.debug("TO SEND n" + currNodeCount + "=" + redBody);
						this._sendString2Serial("n" + currNodeCount + "=" + redBody);
						++currNodeCount;
						logger.debug("TO SEND n" + currNodeCount + "=" + redHead);
						this._sendString2Serial("n" + currNodeCount + "=" + redHead);
						++currNodeCount;
					}
				}

				logger.debug("TO SEND j1="
						+ (networkConfiguration.getJudgesNumber() >= 1 ? networkConfiguration.getJudge1NodeId() : "0"));
				this._sendString2Serial("j1="
						+ (networkConfiguration.getJudgesNumber() >= 1 ? networkConfiguration.getJudge1NodeId() : "0"));
				logger.debug("TO SEND j2="
						+ (networkConfiguration.getJudgesNumber() >= 2 ? networkConfiguration.getJudge2NodeId() : "0"));
				this._sendString2Serial("j2="
						+ (networkConfiguration.getJudgesNumber() >= 2 ? networkConfiguration.getJudge2NodeId() : "0"));
				logger.debug("TO SEND j3="
						+ (networkConfiguration.getJudgesNumber() == 3 ? networkConfiguration.getJudge3NodeId() : "0"));
				this._sendString2Serial("j3="
						+ (networkConfiguration.getJudgesNumber() == 3 ? networkConfiguration.getJudge3NodeId() : "0"));
				this.tryToRecognizeWithConfig(networkConfiguration, false);
			} catch (SerialPortException var13) {
				throw new TkStrikeCommunicationException(var13);
			}
		}

	}

	private void doInitializeGroupsNumber(int groupsNumber) throws SerialPortException {
		this._sendString2Serial("group?");

		try {
			TimeUnit.SECONDS.sleep(1L);
		} catch (InterruptedException var5) {
		}

		String readed = this.serialPort.readString(this.serialPort.getInputBufferBytesCount());
		if (readed != null && !readed.toLowerCase().contains("invalid command")) {
			logger.info("Is TkStrike with Groups Setting Conf");
			this._sendString2Serial("group=" + groupsNumber);

			try {
				TimeUnit.SECONDS.sleep(1L);
			} catch (InterruptedException var4) {
			}

			readed = this.serialPort.readString(this.serialPort.getInputBufferBytesCount());
			logger.info("Group ->" + readed);
		}

	}

	@Override
	public NetworkConfigurationDto getCurrentNetworkConfiguration() {
		return this.networkConfiguration;
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

	private void validateSerial() throws TkStrikeCommunicationException {
		if (this.serialPort == null) {
			this.startComm();
		}

	}

	private void _validateIfIsGetDataEnabled() {
		if (!this.hasValidateDataEnabled) {
			logger.debug("Validate if GetData Is Enabled");
			this.validateSerial();

			try {
				logger.debug("Sleep 3 seconds");

				try {
					Thread.sleep(3000L);
				} catch (InterruptedException var3) {
				}

				int r = this.serialPort.getInputBufferBytesCount();
				String strReaded = this.serialPort.readString(r);
				if (StringUtils.isNotBlank(strReaded)) {
					strReaded = this._cleanReadedFromSerialString(strReaded);
					logger.debug("READED after Wait " + strReaded);
					this.getDataEnabled = isStatusEventPattern.matcher(strReaded).lookingAt();
				} else {
					this.getDataEnabled = false;
				}

				this.hasValidateDataEnabled = true;
			} catch (SerialPortException var4) {
				var4.printStackTrace();
			}
		}

		logger.debug("Validate if Data is Enabled - GetDataEnabled = " + this.getDataEnabled);
	}

	private void _sendString2Serial(String string) throws SerialPortException {
		if (StringUtils.isNotBlank(string)) {
			this.validateSerial();
			if (logger.isDebugEnabled()) {
				logger.debug("Send to serial " + string);
			}

			try {
				this.serialPort.writeString(string, StandardCharsets.US_ASCII.name());
			} catch (UnsupportedEncodingException var4) {
			}

			this.serialPort.writeByte((byte) 13);

			try {
				Thread.sleep(100L);
			} catch (InterruptedException var3) {
			}
		}

	}

	private void _stopGetData() throws SerialPortException {
		this.validateSerial();
		logger.debug("_stopGetData() method");
		if (this.getDataEnabled) {
			logger.debug("SEND STOP DATA ************************************************");
			this._sendString2Serial("***");
		}

		this.getDataEnabled = false;
	}

	private void _startGetData() throws SerialPortException {
		this.validateSerial();
		logger.debug("_startGetData() method");
		if (!this.getDataEnabled) {
			logger.debug("SEND START DATA ************************************************");
			this._sendString2Serial("***");
		}

		this.getDataEnabled = true;
	}

	private void _sendShowID() throws SerialPortException {
		this.validateSerial();
		logger.debug("_sendShowID() method");
		if (this.getDataEnabled) {
			this._stopGetData();
		}

		if (!this.getDataEnabled) {
			this._sendString2Serial("showID?");
		}

		this.hasSendShowID = true;
	}

	private String _cleanReadedFromSerialString(String readed) {
		readed = StringUtils.trimToNull(readed);
		if (readed != null) {
			readed = readed.replaceAll(new String(new byte[] { 13 }), "");
			readed = readed.replaceAll("\n", "");
			readed = readed.replaceAll("\\n", "");
			readed = readed.replaceAll(STAUTS_PACKET_COM_SEPARATOR, "-");
			readed = readed.replaceAll(STAUTS_PACKET_COM_HEADER, "#");
		}

		return readed;
	}

	private void _cleanAllNodes() {
		this.networkConfiguration.cleanAllNodes();
		this.judge1NodeBadTimes.setValue(0);
		this.judge2NodeBadTimes.setValue(0);
		this.judge3NodeBadTimes.setValue(0);
		this.headNodeLastHitTimestamp = new ConcurrentHashMap();
		this.judgeNodeLastClickTimestamp = new ConcurrentHashMap(18);
	}

	private void fireNewDataEvent(DataEvent newDataEvent) {
		ArrayList<Callable<Void>> tasks = new ArrayList(this.listeners.size());
		this.listeners.forEach((lis) -> {
			tasks.add(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					lis.hasNewDataEvent(newDataEvent);
					return null;
				}
			});
		});

		try {
			TkStrikeExecutors.executeInParallel(tasks);
		} catch (InterruptedException var4) {
			var4.printStackTrace();
		}

	}

	private void fireNewStatusEvent(StatusEvent newStatusEvent) {
		ArrayList<Callable<Void>> tasks = new ArrayList(this.listeners.size());
		if (loggerStatusEvent.isDebugEnabled()) {
			loggerStatusEvent.debug("Fire New Status Event ->" + newStatusEvent.toString());
		}

		this.listeners.forEach((lis) -> {
			tasks.add(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					lis.hasNewStatusEvent(newStatusEvent);
					return null;
				}
			});
		});

		try {
			TkStrikeExecutors.executeInParallel(tasks);
		} catch (InterruptedException var4) {
			var4.printStackTrace();
		}

	}

	private void fireNewChangeNetworkStatusEvent(Long timestamp, NetworkStatus prevStatus, NetworkStatus newStatus) {
		ChangeNetworkStatusEvent changeNetworkStatusEvent = new ChangeNetworkStatusEvent(timestamp, prevStatus,
				newStatus);
		ArrayList<Callable<Void>> tasks = new ArrayList(this.listeners.size());
		this.listeners.forEach((lis) -> {
			tasks.add(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					lis.hasChangeNetworkStatusEvent(changeNetworkStatusEvent);
					return null;
				}
			});
		});

		try {
			TkStrikeExecutors.executeInParallel(tasks);
		} catch (InterruptedException var7) {
			var7.printStackTrace();
		}

	}

	private void fireNewChangeNetworkConfigurationEvent(Long timestamp, NetworkStatus prevStatus,
			NetworkConfigurationDto networkConfiguration) {
		ChangeNetworkConfigurationEvent changeNetworkConfigurationEvent = new ChangeNetworkConfigurationEvent(timestamp,
				prevStatus, networkConfiguration);
		ArrayList<Callable<Void>> tasks = new ArrayList(this.listeners.size());
		this.listeners.forEach((lis) -> {
			tasks.add(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					lis.hasChangeNetworkConfigurationEvent(changeNetworkConfigurationEvent);
					return null;
				}
			});
		});

		try {
			TkStrikeExecutors.executeInParallel(tasks);
		} catch (InterruptedException var7) {
			var7.printStackTrace();
		}

	}

	@Override
	public void run() {
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.maxNetworkAthleteGroupsAllowed);
		this.networkConfiguration = new NetworkConfigurationDto(this.maxNetworkAthleteGroupsAllowed);
	}

	private boolean workWithReadNodesString(String nodesString, String prefix) {
		boolean someNode = false;
		int countMatches = StringUtils.countMatches(nodesString, ":");
		if (countMatches > 1) {
			String[] arrNodeIds = nodesString.split(prefix + "(\\d+):");
			String[] arrNodes = nodesString.split(prefix);
			if (arrNodeIds.length == arrNodes.length) {
				for (int i = 0; i < arrNodes.length; ++i) {
					if (StringUtils.isNotBlank(arrNodes[i]) && StringUtils.isNotBlank(arrNodeIds[i])) {
						String node = StringUtils.substringBefore(arrNodes[i], ":");
						String nodeId = arrNodeIds[i];
						if (this.workWithNode(prefix, nodeId, node)) {
							someNode = true;
						}
					}
				}
			}
		} else if (countMatches == 1) {
			return this.workWithNode(prefix, StringUtils.substringAfter(nodesString, ":"),
					StringUtils.substringBefore(StringUtils.substringAfter(nodesString, prefix), ":"));
		}

		return someNode;
	}

	private boolean workWithNode(String prefix, String nodeId, String node) {
		boolean someNode = false;
		Integer nodeNumber = Integer.parseInt(node);
		if ("n".equals(prefix)) {
			int member;
			for (member = 1; nodeNumber > 4; nodeNumber = nodeNumber - 4) {
				++member;
			}

			NetworkAthletesGroupConfigDto groupConfig = this.networkConfiguration.getNetworkAthletesGroupConfig(member);
			switch (nodeNumber) {
			case 1:
				groupConfig.setBodyBlueNodeId(nodeId);
				groupConfig.setBodyBlueNodeBadTimes(0);
				groupConfig.setBodyBlueNodeInitialized(false);
				someNode = true;
				if (loggerCommEvent.isDebugEnabled()) {
					loggerCommEvent.debug("Member " + member + " blueBodyNodeId OK");
				}
				break;
			case 2:
				groupConfig.setHeadBlueNodeId(nodeId);
				groupConfig.setHeadBlueNodeBadTimes(0);
				groupConfig.setHeadBlueNodeInitialized(false);
				someNode = true;
				if (loggerCommEvent.isDebugEnabled()) {
					loggerCommEvent.debug("Member " + member + " blueHeadNodeId OK");
				}
				break;
			case 3:
				groupConfig.setBodyRedNodeId(nodeId);
				groupConfig.setBodyRedNodeBadTimes(0);
				groupConfig.setBodyRedNodeInitialized(false);
				someNode = true;
				if (loggerCommEvent.isDebugEnabled()) {
					loggerCommEvent.debug("Member " + member + " redBodyNodeId OK");
				}
				break;
			case 4:
				groupConfig.setHeadRedNodeId(nodeId);
				groupConfig.setHeadRedNodeBadTimes(0);
				groupConfig.setHeadRedNodeInitialized(false);
				someNode = true;
				if (loggerCommEvent.isDebugEnabled()) {
					loggerCommEvent.debug("Member " + member + " redHeadGNodeId OK");
				}
			}
		} else {
			switch (nodeNumber) {
			case 1:
				this.networkConfiguration.setJudge1NodeId(nodeId);
				someNode = true;
				if (loggerCommEvent.isDebugEnabled()) {
					loggerCommEvent.debug("judge1NodeId OK");
				}
				break;
			case 2:
				this.networkConfiguration.setJudge2NodeId(nodeId);
				someNode = true;
				if (loggerCommEvent.isDebugEnabled()) {
					loggerCommEvent.debug("judge2NodeId OK");
				}
				break;
			case 3:
				this.networkConfiguration.setJudge3NodeId(nodeId);
				someNode = true;
				if (loggerCommEvent.isDebugEnabled()) {
					loggerCommEvent.debug("judge3NodeId OK");
				}
			}
		}

		return someNode;
	}

	class CustomSerialListener implements SerialPortEventListener {
		@Override
		public void serialEvent(SerialPortEvent serialPortEvent) {
			try {
				if (serialPortEvent != null && 1 == serialPortEvent.getEventType()) {
					int r = TkStrikeCommunicationServiceImpl.this.serialPort.getInputBufferBytesCount();
					String readed = TkStrikeCommunicationServiceImpl.this.serialPort.readString(r);
					String readed2 = StringUtils.trimToNull(readed);
					if (readed2 != null) {
						readed2 = readed2.replaceAll(new String(new byte[] { 13 }), "");
						readed2 = readed2.replaceAll("\n", "");
						readed2 = readed2.replaceAll("\\n", "");
						readed2 = readed2.replaceAll(TkStrikeCommunicationServiceImpl.STAUTS_PACKET_COM_SEPARATOR, "-");
						readed2 = readed2.replaceAll(TkStrikeCommunicationServiceImpl.STAUTS_PACKET_COM_HEADER, "#");
						if (TkStrikeCommunicationServiceImpl.loggerCommEvent.isDebugEnabled()) {
							TkStrikeCommunicationServiceImpl.loggerCommEvent
									.debug("READED -" + readed2 + " length=" + readed2.length());
						}

						String workingReaded = readed2;

						String resta;
						do {
							resta = "";
							String temp = workingReaded;
							if (workingReaded.contains("#")) {
								workingReaded = StringUtils.substringBefore(workingReaded, "#");
								resta = StringUtils.substringAfter(temp, "#");
							}

							String judgesWorking;
							if (TkStrikeCommunicationServiceImpl.isHitEventPattern.matcher(workingReaded).lookingAt()) {
								if (TkStrikeCommunicationServiceImpl.loggerDataEvent.isDebugEnabled()) {
									TkStrikeCommunicationServiceImpl.loggerDataEvent
											.debug("Is HIT Event " + workingReaded);
								}

								long eventTimestamp = System.currentTimeMillis();
								if (workingReaded.length() > 2) {
									judgesWorking = workingReaded.substring(0, 2);
									String hitValue;
									boolean throwEvent;
									if (judgesWorking.startsWith("j")) {
										if (!" ".equals(workingReaded.substring(2, 3))) {
											TkStrikeCommunicationServiceImpl.logger
													.debug("Is not a valid JUDGE HIT EVENT!");
											return;
										}

										hitValue = StringUtils.substringBetween(workingReaded, " ");
										if (TkStrikeCommunicationServiceImpl.loggerDataEvent.isDebugEnabled()) {
											TkStrikeCommunicationServiceImpl.loggerDataEvent
													.debug("JUDGE Source " + judgesWorking + " Value = " + hitValue);
										}

										String nodeId = TkStrikeCommunicationServiceImpl.this.networkConfiguration
												.getJudge1NodeId();
										if (judgesWorking.startsWith("j2")) {
											nodeId = TkStrikeCommunicationServiceImpl.this.networkConfiguration
													.getJudge2NodeId();
										} else if (judgesWorking.startsWith("j3")) {
											nodeId = TkStrikeCommunicationServiceImpl.this.networkConfiguration
													.getJudge3NodeId();
										}

										int intHitValue = 0;
										byte var35 = -1;
										switch (hitValue.hashCode()) {
										case 1420005889:
											if (hitValue.equals("000001")) {
												var35 = 5;
											}
											break;
										case 1420005919:
											if (hitValue.equals("000010")) {
												var35 = 4;
											}
											break;
										case 1420006849:
											if (hitValue.equals("000100")) {
												var35 = 3;
											}
											break;
										case 1420035679:
											if (hitValue.equals("001000")) {
												var35 = 2;
											}
											break;
										case 1420929409:
											if (hitValue.equals("010000")) {
												var35 = 1;
											}
											break;
										case 1448635039:
											if (hitValue.equals("100000")) {
												var35 = 0;
											}
										}

										switch (var35) {
										case 0:
											intHitValue = 'ꨁ';
											break;
										case 1:
											intHitValue = 'ꨂ';
											break;
										case 2:
											intHitValue = 'ꨠ';
											break;
										case 3:
											intHitValue = 'ꨄ';
											break;
										case 4:
											intHitValue = 'ꨈ';
											break;
										case 5:
											intHitValue = 'ꨐ';
										}

										if (StringUtils.isNotBlank(nodeId) && !"0".equals(nodeId)) {
											Long lastHit4Node = TkStrikeCommunicationServiceImpl.this.judgeNodeLastClickTimestamp
													.get(nodeId + "-" + intHitValue);
											throwEvent = lastHit4Node == null || eventTimestamp
													- lastHit4Node >= TkStrikeCommunicationServiceImpl.this.judgeClickTimeValidation;
											if (TkStrikeCommunicationServiceImpl.loggerDataEvent.isDebugEnabled()) {
												TkStrikeCommunicationServiceImpl.loggerDataEvent.debug(
														"JUDGE VALIDATION " + nodeId + "-" + intHitValue + " Last "
																+ lastHit4Node + " Current " + eventTimestamp + " Diff "
																+ (eventTimestamp
																		- (lastHit4Node != null ? lastHit4Node : 0L))
																+ ". ThrowEvent?" + throwEvent);
											}

											if (throwEvent) {
												DataEvent newDataEvent = new DataEvent(eventTimestamp,
														TkStrikeCommunicationServiceImpl.this.networkStatus, nodeId,
														Integer.valueOf(intHitValue), DataEvent.DataEventHitType.BODY,
														workingReaded);
												TkStrikeCommunicationServiceImpl.this.fireNewDataEvent(newDataEvent);
												TkStrikeCommunicationServiceImpl.this.judgeNodeLastClickTimestamp
														.put(nodeId + "-" + intHitValue, eventTimestamp);
											}
										}
									} else {
										hitValue = workingReaded.substring(2, 3);
										String[] hitValueParts = StringUtils
												.split(workingReaded.substring(3, workingReaded.length()), " ");
										String nodeIdx = null;
										if (TkStrikeCommunicationServiceImpl.loggerDataEvent.isDebugEnabled()) {
											TkStrikeCommunicationServiceImpl.loggerDataEvent.debug("SENSOR Source "
													+ judgesWorking + " Group " + hitValue + " ValueParts = "
													+ ToStringBuilder.reflectionToString(hitValueParts));
										}

										int hitValuex = -1;
										if (hitValueParts.length > 0) {
											try {
												hitValuex = Integer.parseInt(hitValueParts[0]);
											} catch (NumberFormatException var21) {
												hitValuex = -1;
											}
										}

										throwEvent = false;
										byte var37 = -1;
										switch (judgesWorking.hashCode()) {
										case 3136:
											if (judgesWorking.equals("bb")) {
												var37 = 0;
											}
											break;
										case 3142:
											if (judgesWorking.equals("bh")) {
												var37 = 1;
											}
											break;
										case 3632:
											if (judgesWorking.equals("rb")) {
												var37 = 2;
											}
											break;
										case 3638:
											if (judgesWorking.equals("rh")) {
												var37 = 3;
											}
										}

										switch (var37) {
										case 0:
											nodeIdx = TkStrikeCommunicationServiceImpl.this.networkConfiguration
													.getNetworkAthletesGroupConfig(Integer.parseInt(hitValue))
													.getBodyBlueNodeId();
											break;
										case 1:
											throwEvent = true;
											nodeIdx = TkStrikeCommunicationServiceImpl.this.networkConfiguration
													.getNetworkAthletesGroupConfig(Integer.parseInt(hitValue))
													.getHeadBlueNodeId();
											break;
										case 2:
											nodeIdx = TkStrikeCommunicationServiceImpl.this.networkConfiguration
													.getNetworkAthletesGroupConfig(Integer.parseInt(hitValue))
													.getBodyRedNodeId();
											break;
										case 3:
											throwEvent = true;
											nodeIdx = TkStrikeCommunicationServiceImpl.this.networkConfiguration
													.getNetworkAthletesGroupConfig(Integer.parseInt(hitValue))
													.getHeadRedNodeId();
										}

										if (StringUtils.isNotBlank(nodeIdx) && !"0".equals(nodeIdx) && hitValuex >= 0) {
											boolean throwEventx = true;
											if (throwEvent) {
												Long lastHit4Nodex = TkStrikeCommunicationServiceImpl.this.headNodeLastHitTimestamp
														.get(nodeIdx);
												throwEventx = lastHit4Nodex == null || eventTimestamp
														- lastHit4Nodex >= TkStrikeCommunicationServiceImpl.this.headHitTimeValidation;
												if (TkStrikeCommunicationServiceImpl.loggerDataEvent.isDebugEnabled()) {
													TkStrikeCommunicationServiceImpl.loggerDataEvent
															.debug("HIT VALIDATION " + nodeIdx + " Last "
																	+ lastHit4Nodex + " Current " + eventTimestamp
																	+ " ThrowEvent?" + throwEventx);
												}
											}

											if (throwEventx) {
												DataEvent newDataEventx = new DataEvent(eventTimestamp,
														TkStrikeCommunicationServiceImpl.this.networkStatus, nodeIdx,
														hitValuex, DataEvent.DataEventHitType.BODY, workingReaded);
												TkStrikeCommunicationServiceImpl.this.fireNewDataEvent(newDataEventx);
												if (throwEvent) {
													TkStrikeCommunicationServiceImpl.this.headNodeLastHitTimestamp
															.put(nodeIdx, eventTimestamp);
												}
											}
										}
									}
								}
							} else {
								String statusValue;
								if (TkStrikeCommunicationServiceImpl.isStatusEventPattern.matcher(workingReaded)
										.lookingAt()) {
									if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled()) {
										TkStrikeCommunicationServiceImpl.loggerStatusEvent.debug("Is Status Event ");
										TkStrikeCommunicationServiceImpl.loggerStatusEvent.debug(
												"STATUS Before ->" + StringUtils.substringBefore(workingReaded, "-"));
										TkStrikeCommunicationServiceImpl.loggerStatusEvent.debug(
												"STATUS After ->" + StringUtils.substringAfter(workingReaded, "-"));
									}

									try {
										String statusType = StringUtils.substringBefore(workingReaded, "-");
										statusValue = StringUtils.substringAfter(workingReaded, "-");
										TkStrikeCommunicationServiceImpl.loggerStatusEvent.debug(
												"STATUS VALUE " + statusValue + " length:" + statusValue.length());
										if (StringUtils.isNotBlank(statusValue)) {
											long eventTimestampx = System.currentTimeMillis();
											byte var13 = -1;
											switch (statusType.hashCode()) {
											case 3167:
												if (statusType.equals("cb")) {
													var13 = 1;
												}
												break;
											case 3175:
												if (statusType.equals("cj")) {
													var13 = 0;
												}
												break;
											case 3183:
												if (statusType.equals("cr")) {
													var13 = 2;
												}
											}

											switch (var13) {
											case 0:
												if (statusValue.length() == 3) {
													this.workWithJudgeStatusEvent(eventTimestampx,
															statusValue.substring(0, 1),
															TkStrikeCommunicationServiceImpl.this.networkConfiguration
																	.getJudge1NodeId(),
															TkStrikeCommunicationServiceImpl.this.judge1NodeBadTimes,
															workingReaded);
													this.workWithJudgeStatusEvent(eventTimestampx,
															statusValue.substring(1, 2),
															TkStrikeCommunicationServiceImpl.this.networkConfiguration
																	.getJudge2NodeId(),
															TkStrikeCommunicationServiceImpl.this.judge2NodeBadTimes,
															workingReaded);
													this.workWithJudgeStatusEvent(eventTimestampx,
															statusValue.substring(2, 3),
															TkStrikeCommunicationServiceImpl.this.networkConfiguration
																	.getJudge3NodeId(),
															TkStrikeCommunicationServiceImpl.this.judge3NodeBadTimes,
															workingReaded);
												} else if (TkStrikeCommunicationServiceImpl.loggerStatusEvent
														.isDebugEnabled()) {
													TkStrikeCommunicationServiceImpl.loggerStatusEvent
															.debug("INCORRECT JUDGES'S PACKET " + statusValue);
												}
												break;
											case 1:
											case 2:
												if (TkStrikeCommunicationServiceImpl.loggerStatusEvent
														.isDebugEnabled()) {
													TkStrikeCommunicationServiceImpl.loggerStatusEvent
															.debug("ATHLETE SENSOR'S PACKET");
												}

												if (statusValue.contains("-") || statusValue.length() == 5) {
													String[] groupInfo = statusValue.split("-");
													if (groupInfo.length > 0) {
														String[] var15 = groupInfo;
														int var16 = groupInfo.length;

														for (int var17 = 0; var17 < var16; ++var17) {
															String strGroup = var15[var17];
															if (strGroup.length() == 5) {
																Integer member = Integer.parseInt(
																		StringUtils.substringAfter(strGroup, ","));
																this.workWithAthleteMemberStatusEvent(eventTimestampx,
																		statusType, strGroup, member, workingReaded);
															} else if (TkStrikeCommunicationServiceImpl.loggerStatusEvent
																	.isDebugEnabled()) {
																TkStrikeCommunicationServiceImpl.loggerStatusEvent
																		.debug("INCORRECT GROUP 1 ATHLETE'S PACKET "
																				+ strGroup);
															}
														}
													} else if (TkStrikeCommunicationServiceImpl.loggerStatusEvent
															.isDebugEnabled()) {
														TkStrikeCommunicationServiceImpl.loggerStatusEvent
																.debug("INCORRECT ATHLETE'S PACKET " + statusValue);
													}
												}
											}
										} else if (TkStrikeCommunicationServiceImpl.loggerStatusEvent
												.isDebugEnabled()) {
											TkStrikeCommunicationServiceImpl.loggerStatusEvent
													.debug("INCORRECT SENSOR'S PACKET " + statusValue);
										}
									} catch (Exception var22) {
										if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled()) {
											TkStrikeCommunicationServiceImpl.loggerStatusEvent.error(
													"EXCEPTION PROCESSING Status Packet " + workingReaded, var22);
										}
									}
								} else if ("***".equals(workingReaded)) {
									TkStrikeCommunicationServiceImpl.loggerCommEvent
											.debug("Hem llegit VALOR DEL TOGGLE");
								} else if (StringUtils.isNumeric(workingReaded)) {
									TkStrikeCommunicationServiceImpl.loggerCommEvent.debug("Hem llegit valor numèric");
								} else {
									boolean someNode = false;
									if (TkStrikeCommunicationServiceImpl.isShowIDResponsePattern.matcher(workingReaded)
											.lookingAt()) {
										TkStrikeCommunicationServiceImpl.loggerCommEvent
												.info("IS A SHOW ID RESPONSE PATTERN");
									}

									if (workingReaded.startsWith("showID?") && workingReaded.length() > 7) {
										workingReaded = StringUtils.substringAfter(workingReaded, "showID?");
										if (TkStrikeCommunicationServiceImpl.loggerCommEvent.isDebugEnabled()) {
											TkStrikeCommunicationServiceImpl.loggerCommEvent
													.debug("Starts with showID after substring " + workingReaded);
										}
									}

									statusValue = StringUtils.substringBefore(workingReaded, "Judge");
									judgesWorking = "Judge" + StringUtils.substringAfter(workingReaded, "Judge");
									if (StringUtils.isNotBlank(statusValue) && TkStrikeCommunicationServiceImpl.this
											.workWithReadNodesString(statusValue, "n")) {
										someNode = true;
									}

									if (StringUtils.isNotBlank(judgesWorking) && !"Judge".equals(judgesWorking)
											&& TkStrikeCommunicationServiceImpl.this
													.workWithReadNodesString(judgesWorking, "Judge")) {
										someNode = true;
									}

									if (someNode && TkStrikeCommunicationServiceImpl.this.networkConfiguration
											.areAllNodesInitialized()) {
										TkStrikeCommunicationServiceImpl.logger
												.debug("S'ha determinat tota la xarxa...");
										TkStrikeCommunicationServiceImpl.this.hasSendShowID = false;
										TkStrikeCommunicationServiceImpl.this.networkConfiguration
												.setNetworkWasStarted(Boolean.TRUE);
										TkStrikeCommunicationServiceImpl.this.networkConfiguration.setChannelNumber(14);
										TkStrikeCommunicationServiceImpl.this.networkConfiguration
												.calculateNumberOfJudges();
										TkStrikeCommunicationServiceImpl.this.networkConfiguration
												.calculateNumberOfGroups();
										TkStrikeCommunicationServiceImpl.this.fireNewChangeNetworkConfigurationEvent(
												System.currentTimeMillis(),
												TkStrikeCommunicationServiceImpl.this.networkStatus,
												TkStrikeCommunicationServiceImpl.this.networkConfiguration);
										NetworkStatus last = TkStrikeCommunicationServiceImpl.this.networkStatus;
										TkStrikeCommunicationServiceImpl.this.networkStatus = NetworkStatus.OK;
										TkStrikeCommunicationServiceImpl.this.fireNewChangeNetworkStatusEvent(
												System.currentTimeMillis(),
												TkStrikeCommunicationServiceImpl.this.networkStatus, last);

										try {
											TkStrikeCommunicationServiceImpl.this._startGetData();
										} catch (SerialPortException var20) {
											var20.printStackTrace();
										}
									}
								}
							}

							workingReaded = resta;
						} while (StringUtils.isNotBlank(resta));
					}
				}
			} catch (Exception var23) {
				TkStrikeCommunicationServiceImpl.logger.error("Error on Serial", var23);
			}

		}

		private void workWithJudgeStatusEvent(Long eventTimestamp, String value, String judgeNodeId,
				SimpleIntegerProperty prevBadTimes, String nativePacket) {
			StatusEvent newStatusEvent;
			if ("0".equals(value)) {
				prevBadTimes.set(0);
				newStatusEvent = new StatusEvent(eventTimestamp, TkStrikeCommunicationServiceImpl.this.networkStatus,
						judgeNodeId, Boolean.FALSE, "0".equals(value) ? Boolean.TRUE : Boolean.FALSE,
						"2".equals(value) ? 0.0D : 4.0D, "2".equals(value) ? 0.0D : 90.0D, nativePacket);
				TkStrikeCommunicationServiceImpl.this.fireNewStatusEvent(newStatusEvent);
			} else if ("1".equals(value)) {
				prevBadTimes.set(prevBadTimes.get() + 1);
				if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled()) {
					TkStrikeCommunicationServiceImpl.loggerStatusEvent
							.debug("Judge " + judgeNodeId + " is OffLine.. times? " + prevBadTimes.get());
				}

				if (prevBadTimes.get() >= TkStrikeCommunicationServiceImpl.this.nodeConnBadTimesAllowed) {
					if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled()) {
						TkStrikeCommunicationServiceImpl.loggerStatusEvent
								.debug("Judge " + judgeNodeId + " has exceeded number of offline allowed times");
					}

					newStatusEvent = new StatusEvent(eventTimestamp,
							TkStrikeCommunicationServiceImpl.this.networkStatus, judgeNodeId, Boolean.TRUE,
							Boolean.FALSE, 0.0D, 0.0D, nativePacket);
					TkStrikeCommunicationServiceImpl.this.fireNewStatusEvent(newStatusEvent);
				}
			}

		}

		private String _getBodyNodeId4Athlete(String prefix, Integer member) {
			NetworkAthletesGroupConfigDto groupConfig = TkStrikeCommunicationServiceImpl.this.networkConfiguration
					.getNetworkAthletesGroupConfig(member);
			if (groupConfig != null) {
				if ("cb".equals(prefix)) {
					return groupConfig.getBodyBlueNodeId();
				}

				if ("cr".equals(prefix)) {
					return groupConfig.getBodyRedNodeId();
				}
			}

			return null;
		}

		private String _getHeadNodeId4Athlete(String prefix, Integer member) {
			NetworkAthletesGroupConfigDto groupConfig = TkStrikeCommunicationServiceImpl.this.networkConfiguration
					.getNetworkAthletesGroupConfig(member);
			if (groupConfig != null) {
				if ("cb".equals(prefix)) {
					return groupConfig.getHeadBlueNodeId();
				}

				if ("cr".equals(prefix)) {
					return groupConfig.getHeadRedNodeId();
				}
			}

			return null;
		}

		private SimpleIntegerProperty _getBodyNodeBadTimes4Athlete(String prefix, Integer member) {
			NetworkAthletesGroupConfigDto groupConfig = TkStrikeCommunicationServiceImpl.this.networkConfiguration
					.getNetworkAthletesGroupConfig(member);
			if (groupConfig != null) {
				if ("cb".equals(prefix)) {
					return groupConfig.bodyBlueNodeBadTimesProperty();
				}

				if ("cr".equals(prefix)) {
					return groupConfig.bodyRedNodeBadTimesProperty();
				}
			}

			return null;
		}

		private SimpleIntegerProperty _getHeadNodeBadTimes4Athlete(String prefix, Integer member) {
			NetworkAthletesGroupConfigDto groupConfig = TkStrikeCommunicationServiceImpl.this.networkConfiguration
					.getNetworkAthletesGroupConfig(member);
			if (groupConfig != null) {
				if ("cb".equals(prefix)) {
					return groupConfig.headBlueNodeBadTimesProperty();
				}

				if ("cr".equals(prefix)) {
					return groupConfig.headRedNodeBadTimesProperty();
				}
			}

			return null;
		}

		private SimpleBooleanProperty _getBodyNodeInitialized4Athlete(String prefix, Integer member) {
			NetworkAthletesGroupConfigDto groupConfig = TkStrikeCommunicationServiceImpl.this.networkConfiguration
					.getNetworkAthletesGroupConfig(member);
			if (groupConfig != null) {
				if ("cb".equals(prefix)) {
					return groupConfig.bodyBlueNodeInitializedProperty();
				}

				if ("cr".equals(prefix)) {
					return groupConfig.bodyRedNodeInitializedProperty();
				}
			}

			return null;
		}

		private SimpleBooleanProperty _getHeadNodeInitialized4Athlete(String prefix, Integer member) {
			NetworkAthletesGroupConfigDto groupConfig = TkStrikeCommunicationServiceImpl.this.networkConfiguration
					.getNetworkAthletesGroupConfig(member);
			if (groupConfig != null) {
				if ("cb".equals(prefix)) {
					return groupConfig.headBlueNodeInitializedProperty();
				}

				if ("cr".equals(prefix)) {
					return groupConfig.headRedNodeInitializedProperty();
				}
			}

			return null;
		}

		private void workWithAthleteMemberStatusEvent(Long eventTimestamp, String prefix, String value, Integer member,
				String nativePacket) {
			String comm = value.substring(0, 1);
			String conn = value.substring(1, 2);
			String battery = value.substring(2, 3);
			if (member <= TkStrikeCommunicationServiceImpl.this.networkConfiguration.getGroupsNumber()) {
				SimpleBooleanProperty bodyInitialized = this._getBodyNodeInitialized4Athlete(prefix, member);
				SimpleBooleanProperty headInitialized = this._getHeadNodeInitialized4Athlete(prefix, member);
				SimpleIntegerProperty bodyBadTimes = this._getBodyNodeBadTimes4Athlete(prefix, member);
				SimpleIntegerProperty headBadTimes = this._getHeadNodeBadTimes4Athlete(prefix, member);
				String bodyNodeId = this._getBodyNodeId4Athlete(prefix, member);
				String headNodeId = this._getHeadNodeId4Athlete(prefix, member);
				if (bodyInitialized != null && headInitialized != null && bodyBadTimes != null
						&& headBadTimes != null) {
					boolean bodyOffline = false;
					boolean headOffline = false;
					boolean bodySensorOk = !"0".equals(conn) && !"2".equals(conn) ? Boolean.FALSE : Boolean.TRUE;
					boolean headSensorOk = "0".equals(conn) ? Boolean.TRUE : Boolean.FALSE;
					double bodyBattery = !"0".equals(battery) && !"2".equals(battery) ? 0.0D : 4.0D;
					double bodyBatteryPct = !"0".equals(battery) && !"2".equals(battery) ? 0.0D : 100.0D;
					double headBattery = "0".equals(battery) ? 4.0D : 0.0D;
					double headBatteryPct = "0".equals(battery) ? 100.0D : 0.0D;
					if ("0".equals(comm)) {
						bodyBadTimes.set(0);
						headBadTimes.set(0);
						bodyInitialized.set(true);
						headInitialized.set(true);
						if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled()) {
							TkStrikeCommunicationServiceImpl.loggerStatusEvent.debug(prefix + " are OK BOTH!!!");
						}

						bodyOffline = false;
						headOffline = false;
					} else if ("1".equals(comm)) {
						bodyBadTimes.set(bodyBadTimes.get() + 1);
						headBadTimes.set(headBadTimes.get() + 1);
						if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled()) {
							TkStrikeCommunicationServiceImpl.loggerStatusEvent
									.debug("Body " + bodyNodeId + " is OffLine.. times? " + bodyBadTimes.get());
						}

						if (bodyBadTimes.get() >= TkStrikeCommunicationServiceImpl.this.nodeConnBadTimesAllowed
								|| !bodyInitialized.get()) {
							if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled()) {
								TkStrikeCommunicationServiceImpl.loggerStatusEvent
										.debug("Body " + bodyNodeId + " has exceeded number of offline allowed times"
												+ bodyBadTimes.get() + " Or initialized? " + bodyInitialized.get());
							}

							bodyOffline = Boolean.TRUE;
						}

						if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled()) {
							TkStrikeCommunicationServiceImpl.loggerStatusEvent
									.debug("Head " + headNodeId + " is OffLine.. times? " + headBadTimes.get());
						}

						if (headBadTimes.get() >= TkStrikeCommunicationServiceImpl.this.nodeConnBadTimesAllowed
								|| !headInitialized.get()) {
							if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled()) {
								TkStrikeCommunicationServiceImpl.loggerStatusEvent
										.debug("Head " + headNodeId + " has exceeded number of offline allowed times: "
												+ headBadTimes.get() + " Or initialized? " + headInitialized.get());
							}

							headOffline = Boolean.TRUE;
						}
					} else if ("2".equals(comm)) {
						bodyBadTimes.set(0);
						bodyInitialized.set(true);
						headBadTimes.set(headBadTimes.get() + 1);
						if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled()) {
							TkStrikeCommunicationServiceImpl.loggerStatusEvent
									.debug("Head " + headNodeId + " is OffLine.. times? " + headBadTimes.get());
						}

						if (headBadTimes.get() >= TkStrikeCommunicationServiceImpl.this.nodeConnBadTimesAllowed
								|| !headInitialized.get()) {
							if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled()) {
								TkStrikeCommunicationServiceImpl.loggerStatusEvent
										.debug("Head " + headNodeId + " has exceeded number of offline allowed times:"
												+ headBadTimes.get() + " Or initialized? " + headInitialized.get());
							}

							headOffline = Boolean.TRUE;
						}
					}

					TkStrikeCommunicationServiceImpl.this.fireNewStatusEvent(
							new StatusEvent(eventTimestamp, TkStrikeCommunicationServiceImpl.this.networkStatus,
									bodyNodeId, bodyOffline, bodySensorOk, bodyBattery, bodyBatteryPct, nativePacket));
					TkStrikeCommunicationServiceImpl.this.fireNewStatusEvent(
							new StatusEvent(eventTimestamp, TkStrikeCommunicationServiceImpl.this.networkStatus,
									headNodeId, headOffline, headSensorOk, headBattery, headBatteryPct, nativePacket));
				} else if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled()) {
					TkStrikeCommunicationServiceImpl.loggerStatusEvent
							.debug("Can't get body or head bad times for string:" + value);
				}
			}

		}
	}
}
