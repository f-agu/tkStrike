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

	private ConcurrentHashMap<String, Long> headNodeLastHitTimestamp = new ConcurrentHashMap<>();

	private ConcurrentHashMap<String, Long> judgeNodeLastClickTimestamp = new ConcurrentHashMap<>(18);

	private SimpleIntegerProperty judge1NodeBadTimes = new SimpleIntegerProperty(this, "judge1NodeBadTimes", 0);

	private SimpleIntegerProperty judge2NodeBadTimes = new SimpleIntegerProperty(this, "judge2NodeBadTimes", 0);

	private SimpleIntegerProperty judge3NodeBadTimes = new SimpleIntegerProperty(this, "judge3NodeBadTimes", 0);

	private CopyOnWriteArrayList<TkStrikeCommunicationListener> listeners = new CopyOnWriteArrayList<>();

	private SerialPort serialPort = null;

	private CustomSerialListener customSerialListener = new CustomSerialListener();

	private boolean getDataEnabled = false;

	private boolean hasValidateDataEnabled = false;

	private boolean hasSendShowID = false;

	private NetworkConfigurationDto networkConfiguration;

	private NetworkStatus networkStatus = NetworkStatus.NOT_CONNECTED;

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

	@Override
	public synchronized void startComm() throws TkStrikeCommunicationException {
		if (logger.isDebugEnabled())
			logger.debug("Start Port Gen2");
		if (this.serialPort == null) {
			String portName = null;
			try {
				String[] portNames = SerialPortList.getPortNames();
				for (int i = 0; i < portNames.length; i++) {
					String tempPortName = portNames[i];
					if (logger.isDebugEnabled())
						logger.debug("Port Name = " + tempPortName);
					if (Pattern.compile(this.serialNamePattern).matcher(tempPortName).lookingAt())
						new SerialPort(tempPortName);
				}
			} catch (Exception e) {
				logger.error("startComm", e);
			}
			if (portName != null) {
				if (logger.isDebugEnabled())
					logger.debug("Open port " + portName);
				this.serialPort = new SerialPort(portName);
				try {
					this.serialPort.openPort();
					this.serialPort.setParams(38400, 8, 1, 0);
					_validateIfIsGetDataEnabled();
					this.serialPort.addEventListener(this.customSerialListener);
					NetworkStatus last = this.networkStatus;
					this.networkStatus = NetworkStatus.NOT_STARTED;
					fireNewChangeNetworkStatusEvent(Long.valueOf(System.currentTimeMillis()), this.networkStatus, last);
					return;
				} catch (SerialPortException e) {
					e.printStackTrace();
					logger.error("startComm", e);
					this.serialPort = null;
					throw new TkStrikeCommunicationException(e);
				}
			}
		}
		if (this.serialPort == null)
			throw new TkStrikeCommunicationException("");
	}

	@Override
	public void stopComm() throws TkStrikeCommunicationException {
		if (this.serialPort != null)
			try {
				_stopGetData();
				this.serialPort.removeEventListener();
				this.serialPort.closePort();
			} catch (SerialPortException e) {
				logger.error("stopComm", e);
				throw new TkStrikeCommunicationException(e);
			} finally {
				this.serialPort = null;
				NetworkStatus last = this.networkStatus;
				this.networkStatus = NetworkStatus.NOT_CONNECTED;
				fireNewChangeNetworkStatusEvent(Long.valueOf(System.currentTimeMillis()), this.networkStatus, last);
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
		_cleanAllNodes();
		try {
			_sendShowID();
		} catch (SerialPortException e) {
			e.printStackTrace();
			throw new TkStrikeCommunicationException(e);
		}
	}

	@Override
	public void startNetwork(NetworkConfigurationDto networkConfiguration) throws TkStrikeCommunicationException {
		if (networkConfiguration != null) {
			stopComm();
			if (logger.isDebugEnabled())
				logger.debug("Start netvork with " + ToStringBuilder.reflectionToString(networkConfiguration));
			try {
				_stopGetData();
				networkConfiguration.calculateNumberOfGroups();
				logger.info("------ NETWORK GROUPS = " + networkConfiguration.getGroupsNumber());
				doInitializeGroupsNumber(networkConfiguration.getGroupsNumber().intValue());
				NetworkStatus last = this.networkStatus;
				this.networkStatus = NetworkStatus.NOT_STARTED;
				fireNewChangeNetworkStatusEvent(Long.valueOf(System.currentTimeMillis()), this.networkStatus, last);
				_cleanAllNodes();
				int currNodeCount = 1;
				for (int i = 1; i <= this.maxNetworkAthleteGroupsAllowed.intValue(); i++) {
					NetworkAthletesGroupConfigDto groupConfig = networkConfiguration
							.getNetworkAthletesGroupConfig(Integer.valueOf(i));
					if (groupConfig != null) {
						boolean bodyEnabled = groupConfig.getBodySensorsEnabled().booleanValue();
						boolean headEnabled = groupConfig.getHeadSensorsEnabled().booleanValue();
						boolean groupEnabled = (networkConfiguration.getGroupsNumber().intValue() >= i);
						logger.debug("GROUP " + i + " enabled?" + groupEnabled);
						logger.debug("BODY " + i + " enabled?" + bodyEnabled);
						logger.debug("HEAD " + i + " enabled?" + headEnabled);
						String blueBody = (groupEnabled && bodyEnabled && groupConfig.getBodyBlueNodeId() != null)
								? groupConfig.getBodyBlueNodeId()
								: "0";
						String blueHead = (groupEnabled && headEnabled && groupConfig.getHeadBlueNodeId() != null)
								? groupConfig.getHeadBlueNodeId()
								: "0";
						String redBody = (groupEnabled && bodyEnabled && groupConfig.getBodyRedNodeId() != null)
								? groupConfig.getBodyRedNodeId()
								: "0";
						String redHead = (groupEnabled && headEnabled && groupConfig.getHeadRedNodeId() != null)
								? groupConfig.getHeadRedNodeId()
								: "0";
						logger.debug("TO SEND n" + currNodeCount + "=" + blueBody);
						_sendString2Serial("n" + currNodeCount + "=" + blueBody);
						currNodeCount++;
						logger.debug("TO SEND n" + currNodeCount + "=" + blueHead);
						_sendString2Serial("n" + currNodeCount + "=" + blueHead);
						currNodeCount++;
						logger.debug("TO SEND n" + currNodeCount + "=" + redBody);
						_sendString2Serial("n" + currNodeCount + "=" + redBody);
						currNodeCount++;
						logger.debug("TO SEND n" + currNodeCount + "=" + redHead);
						_sendString2Serial("n" + currNodeCount + "=" + redHead);
						currNodeCount++;
					}
				}
				logger.debug("TO SEND j1=" + ((networkConfiguration.getJudgesNumber().intValue() >= 1)
						? networkConfiguration.getJudge1NodeId()
						: "0"));
				_sendString2Serial("j1=" + ((networkConfiguration.getJudgesNumber().intValue() >= 1)
						? networkConfiguration.getJudge1NodeId()
						: "0"));
				logger.debug("TO SEND j2=" + ((networkConfiguration.getJudgesNumber().intValue() >= 2)
						? networkConfiguration.getJudge2NodeId()
						: "0"));
				_sendString2Serial("j2=" + ((networkConfiguration.getJudgesNumber().intValue() >= 2)
						? networkConfiguration.getJudge2NodeId()
						: "0"));
				logger.debug("TO SEND j3=" + ((networkConfiguration.getJudgesNumber().intValue() == 3)
						? networkConfiguration.getJudge3NodeId()
						: "0"));
				_sendString2Serial("j3=" + ((networkConfiguration.getJudgesNumber().intValue() == 3)
						? networkConfiguration.getJudge3NodeId()
						: "0"));
				tryToRecognizeWithConfig(networkConfiguration, false);
			} catch (SerialPortException e) {
				throw new TkStrikeCommunicationException(e);
			}
		}
	}

	private void doInitializeGroupsNumber(int groupsNumber) throws SerialPortException {
		_sendString2Serial("group?");
		try {
			TimeUnit.SECONDS.sleep(1L);
		} catch (InterruptedException interruptedException) {
		}
		String readed = this.serialPort.readString(this.serialPort.getInputBufferBytesCount());
		if (readed != null && !readed.toLowerCase().contains("invalid command")) {
			logger.info("Is TkStrike with Groups Setting Conf");
			_sendString2Serial("group=" + groupsNumber);
			try {
				TimeUnit.SECONDS.sleep(1L);
			} catch (InterruptedException interruptedException) {
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
		if (!this.listeners.contains(tkStrikeCommunicationListener))
			this.listeners.add(tkStrikeCommunicationListener);
	}

	@Override
	public void removeListener(TkStrikeCommunicationListener tkStrikeCommunicationListener) {
		this.listeners.remove(tkStrikeCommunicationListener);
	}

	private void validateSerial() throws TkStrikeCommunicationException {
		if (this.serialPort == null)
			startComm();
	}

	private void _validateIfIsGetDataEnabled() {
		if (!this.hasValidateDataEnabled) {
			logger.debug("Validate if GetData Is Enabled");
			validateSerial();
			try {
				logger.debug("Sleep 3 seconds");
				try {
					Thread.sleep(3000L);
				} catch (InterruptedException interruptedException) {
				}
				int r = this.serialPort.getInputBufferBytesCount();
				String strReaded = this.serialPort.readString(r);
				if (StringUtils.isNotBlank(strReaded)) {
					strReaded = _cleanReadedFromSerialString(strReaded);
					logger.debug("READED after Wait " + strReaded);
					this.getDataEnabled = isStatusEventPattern.matcher(strReaded).lookingAt();
				} else {
					this.getDataEnabled = false;
				}
				this.hasValidateDataEnabled = true;
			} catch (SerialPortException e) {
				e.printStackTrace();
			}
		}
		logger.debug("Validate if Data is Enabled - GetDataEnabled = " + this.getDataEnabled);
	}

	private void _sendString2Serial(String string) throws SerialPortException {
		if (StringUtils.isNotBlank(string)) {
			validateSerial();
			if (logger.isDebugEnabled())
				logger.debug("Send to serial " + string);
			try {
				this.serialPort.writeString(string, StandardCharsets.US_ASCII.name());
			} catch (UnsupportedEncodingException unsupportedEncodingException) {
			}
			this.serialPort.writeByte((byte) 13);
			try {
				Thread.sleep(100L);
			} catch (InterruptedException interruptedException) {
			}
		}
	}

	private void _stopGetData() throws SerialPortException {
		validateSerial();
		logger.debug("_stopGetData() method");
		if (this.getDataEnabled) {
			logger.debug("SEND STOP DATA ************************************************");
			_sendString2Serial("***");
		}
		this.getDataEnabled = false;
	}

	private void _startGetData() throws SerialPortException {
		validateSerial();
		logger.debug("_startGetData() method");
		if (!this.getDataEnabled) {
			logger.debug("SEND START DATA ************************************************");
			_sendString2Serial("***");
		}
		this.getDataEnabled = true;
	}

	private void _sendShowID() throws SerialPortException {
		validateSerial();
		logger.debug("_sendShowID() method");
		if (this.getDataEnabled)
			_stopGetData();
		if (!this.getDataEnabled)
			_sendString2Serial("showID?");
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
		this.judge1NodeBadTimes.setValue(Integer.valueOf(0));
		this.judge2NodeBadTimes.setValue(Integer.valueOf(0));
		this.judge3NodeBadTimes.setValue(Integer.valueOf(0));
		this.headNodeLastHitTimestamp = new ConcurrentHashMap<>();
		this.judgeNodeLastClickTimestamp = new ConcurrentHashMap<>(18);
	}

	private void fireNewDataEvent(DataEvent newDataEvent) {
		ArrayList<Callable<Void>> tasks = new ArrayList<>(this.listeners.size());
		this.listeners.forEach(lis -> tasks.add(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				lis.hasNewDataEvent(newDataEvent);
				return null;
			}
		}));
		try {
			TkStrikeExecutors.executeInParallel(tasks);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void fireNewStatusEvent(StatusEvent newStatusEvent) {
		ArrayList<Callable<Void>> tasks = new ArrayList<>(this.listeners.size());
		if (loggerStatusEvent.isDebugEnabled())
			loggerStatusEvent.debug("Fire New Status Event ->" + newStatusEvent.toString());
		this.listeners.forEach(lis -> tasks.add(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				lis.hasNewStatusEvent(newStatusEvent);
				return null;
			}
		}));
		try {
			TkStrikeExecutors.executeInParallel(tasks);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void fireNewChangeNetworkStatusEvent(Long timestamp, NetworkStatus prevStatus, NetworkStatus newStatus) {
		ChangeNetworkStatusEvent changeNetworkStatusEvent = new ChangeNetworkStatusEvent(timestamp, prevStatus,
				newStatus);
		ArrayList<Callable<Void>> tasks = new ArrayList<>(this.listeners.size());
		this.listeners.forEach(lis -> tasks.add(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				lis.hasChangeNetworkStatusEvent(changeNetworkStatusEvent);
				return null;
			}
		}));
		try {
			TkStrikeExecutors.executeInParallel(tasks);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void fireNewChangeNetworkConfigurationEvent(Long timestamp, NetworkStatus prevStatus,
			NetworkConfigurationDto networkConfiguration) {
		ChangeNetworkConfigurationEvent changeNetworkConfigurationEvent = new ChangeNetworkConfigurationEvent(timestamp,
				prevStatus, networkConfiguration);
		ArrayList<Callable<Void>> tasks = new ArrayList<>(this.listeners.size());
		this.listeners.forEach(lis -> tasks.add(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				lis.hasChangeNetworkConfigurationEvent(changeNetworkConfigurationEvent);
				return null;
			}
		}));
		try {
			TkStrikeExecutors.executeInParallel(tasks);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
	}

	class CustomSerialListener implements SerialPortEventListener {

		@Override
		public void serialEvent(SerialPortEvent serialPortEvent) {
			try {
				if (serialPortEvent != null && 1 == serialPortEvent.getEventType()) {
					int r = serialPort.getInputBufferBytesCount();
					String readed = serialPort.readString(r);
					String readed2 = StringUtils.trimToNull(readed);
					if (readed2 != null) {
						readed2 = readed2.replaceAll(new String(new byte[] { 13 }), "");
						readed2 = readed2.replaceAll("\n", "");
						readed2 = readed2.replaceAll("\\n", "");
						readed2 = readed2.replaceAll(TkStrikeCommunicationServiceImpl.STAUTS_PACKET_COM_SEPARATOR, "-");
						readed2 = readed2.replaceAll(TkStrikeCommunicationServiceImpl.STAUTS_PACKET_COM_HEADER, "#");
						if (TkStrikeCommunicationServiceImpl.loggerCommEvent.isDebugEnabled())
							TkStrikeCommunicationServiceImpl.loggerCommEvent
									.debug("READED -" + readed2 + " length=" + readed2.length());
						String workingReaded = readed2;
						do {
							String resta = "";
							String temp = workingReaded;
							if (workingReaded.contains("#")) {
								workingReaded = StringUtils.substringBefore(temp, "#");
								resta = StringUtils.substringAfter(temp, "#");
							}
							if (TkStrikeCommunicationServiceImpl.isHitEventPattern.matcher(workingReaded).lookingAt()) {
								if (TkStrikeCommunicationServiceImpl.loggerDataEvent.isDebugEnabled())
									TkStrikeCommunicationServiceImpl.loggerDataEvent
											.debug("Is HIT Event " + workingReaded);
								long eventTimestamp = System.currentTimeMillis();
								if (workingReaded.length() > 2) {
									String hitSource = workingReaded.substring(0, 2);
									if (hitSource.startsWith("j")) {
										if (!" ".equals(workingReaded.substring(2, 3))) {
											TkStrikeCommunicationServiceImpl.logger
													.debug("Is not a valid JUDGE HIT EVENT!");
											return;
										}
										String hitValue = StringUtils.substringBetween(workingReaded, " ");
										if (TkStrikeCommunicationServiceImpl.loggerDataEvent.isDebugEnabled())
											TkStrikeCommunicationServiceImpl.loggerDataEvent
													.debug("JUDGE Source " + hitSource + " Value = " + hitValue);
										String nodeId = networkConfiguration.getJudge1NodeId();
										if (hitSource.startsWith("j2")) {
											nodeId = networkConfiguration.getJudge2NodeId();
										} else if (hitSource.startsWith("j3")) {
											nodeId = networkConfiguration.getJudge3NodeId();
										}
										int intHitValue = 0;
										switch (hitValue) {
										case "100000":
											intHitValue = 43521;
											break;
										case "010000":
											intHitValue = 43522;
											break;
										case "001000":
											intHitValue = 43552;
											break;
										case "000100":
											intHitValue = 43524;
											break;
										case "000010":
											intHitValue = 43528;
											break;
										case "000001":
											intHitValue = 43536;
											break;
										}
										if (StringUtils.isNotBlank(nodeId) && !"0".equals(nodeId)) {
											Long lastHit4Node = judgeNodeLastClickTimestamp
													.get(nodeId + "-" + intHitValue);
											boolean throwEvent = (lastHit4Node == null || eventTimestamp
													- lastHit4Node.longValue() >= judgeClickTimeValidation.longValue());
											if (TkStrikeCommunicationServiceImpl.loggerDataEvent.isDebugEnabled())
												TkStrikeCommunicationServiceImpl.loggerDataEvent.debug(
														"JUDGE VALIDATION " + nodeId + "-" + intHitValue + " Last "
																+ lastHit4Node + " Current " + eventTimestamp + " Diff "
																+ (eventTimestamp
																		- ((lastHit4Node != null) ? lastHit4Node

																				.longValue() : 0L))
																+ ". ThrowEvent?" + throwEvent);
											if (throwEvent) {
												DataEvent newDataEvent = new DataEvent(Long.valueOf(eventTimestamp),
														networkStatus, nodeId, Integer.valueOf(intHitValue),
														DataEvent.DataEventHitType.BODY, workingReaded);
												fireNewDataEvent(newDataEvent);
												judgeNodeLastClickTimestamp.put(nodeId + "-" + intHitValue,
														Long.valueOf(eventTimestamp));
											}
										}
									} else {
										String hitGroup = workingReaded.substring(2, 3);
										String[] hitValueParts = StringUtils
												.split(workingReaded.substring(3, workingReaded.length()), " ");
										String nodeId = null;
										if (TkStrikeCommunicationServiceImpl.loggerDataEvent.isDebugEnabled())
											TkStrikeCommunicationServiceImpl.loggerDataEvent.debug("SENSOR Source "
													+ hitSource + " Group " + hitGroup + " ValueParts = "
													+ ToStringBuilder.reflectionToString(hitValueParts));
										int hitValue = -1;
										if (hitValueParts.length > 0)
											try {
												hitValue = Integer.parseInt(hitValueParts[0]);
											} catch (NumberFormatException nfe) {
												hitValue = -1;
											}
										boolean isHeadHit = false;
										switch (hitSource) {
										case "bb":
											nodeId = networkConfiguration
													.getNetworkAthletesGroupConfig(
															Integer.valueOf(Integer.parseInt(hitGroup)))
													.getBodyBlueNodeId();
											break;
										case "bh":
											isHeadHit = true;
											nodeId = networkConfiguration
													.getNetworkAthletesGroupConfig(
															Integer.valueOf(Integer.parseInt(hitGroup)))
													.getHeadBlueNodeId();
											break;
										case "rb":
											nodeId = networkConfiguration
													.getNetworkAthletesGroupConfig(
															Integer.valueOf(Integer.parseInt(hitGroup)))
													.getBodyRedNodeId();
											break;
										case "rh":
											isHeadHit = true;
											nodeId = networkConfiguration
													.getNetworkAthletesGroupConfig(
															Integer.valueOf(Integer.parseInt(hitGroup)))
													.getHeadRedNodeId();
											break;
										}
										if (StringUtils.isNotBlank(nodeId) && !"0".equals(nodeId) && hitValue >= 0) {
											boolean throwEvent = true;
											if (isHeadHit) {
												Long lastHit4Node = headNodeLastHitTimestamp.get(nodeId);
												throwEvent = (lastHit4Node == null || eventTimestamp - lastHit4Node
														.longValue() >= headHitTimeValidation.longValue());
												if (TkStrikeCommunicationServiceImpl.loggerDataEvent.isDebugEnabled())
													TkStrikeCommunicationServiceImpl.loggerDataEvent
															.debug("HIT VALIDATION " + nodeId + " Last " + lastHit4Node
																	+ " Current " + eventTimestamp + " ThrowEvent?"
																	+ throwEvent);
											}
											if (throwEvent) {
												DataEvent newDataEvent = new DataEvent(Long.valueOf(eventTimestamp),
														networkStatus, nodeId, Integer.valueOf(hitValue),
														DataEvent.DataEventHitType.BODY, workingReaded);
												fireNewDataEvent(newDataEvent);
												if (isHeadHit)
													headNodeLastHitTimestamp.put(nodeId, Long.valueOf(eventTimestamp));
											}
										}
									}
								}
							} else if (TkStrikeCommunicationServiceImpl.isStatusEventPattern.matcher(workingReaded)
									.lookingAt()) {
								if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled()) {
									TkStrikeCommunicationServiceImpl.loggerStatusEvent.debug("Is Status Event ");
									TkStrikeCommunicationServiceImpl.loggerStatusEvent.debug(
											"STATUS Before ->" + StringUtils.substringBefore(workingReaded, "-"));
									TkStrikeCommunicationServiceImpl.loggerStatusEvent
											.debug("STATUS After ->" + StringUtils.substringAfter(workingReaded, "-"));
								}
								try {
									String statusType = StringUtils.substringBefore(workingReaded, "-");
									String statusValue = StringUtils.substringAfter(workingReaded, "-");
									TkStrikeCommunicationServiceImpl.loggerStatusEvent
											.debug("STATUS VALUE " + statusValue + " length:" + statusValue.length());
									if (StringUtils.isNotBlank(statusValue)) {
										long eventTimestamp = System.currentTimeMillis();
										switch (statusType) {
										case "cj":
											if (statusValue.length() == 3) {
												workWithJudgeStatusEvent(Long.valueOf(eventTimestamp),
														statusValue.substring(0, 1),
														networkConfiguration.getJudge1NodeId(), judge1NodeBadTimes,
														workingReaded);
												workWithJudgeStatusEvent(Long.valueOf(eventTimestamp),
														statusValue.substring(1, 2),
														networkConfiguration.getJudge2NodeId(), judge2NodeBadTimes,
														workingReaded);
												workWithJudgeStatusEvent(Long.valueOf(eventTimestamp),
														statusValue.substring(2, 3),
														networkConfiguration.getJudge3NodeId(), judge3NodeBadTimes,
														workingReaded);
												break;
											}
											if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled())
												TkStrikeCommunicationServiceImpl.loggerStatusEvent
														.debug("INCORRECT JUDGES'S PACKET " + statusValue);
											break;
										case "cb":
										case "cr":
											if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled())
												TkStrikeCommunicationServiceImpl.loggerStatusEvent
														.debug("ATHLETE SENSOR'S PACKET");
											if (statusValue.contains("-") || statusValue.length() == 5) {
												String[] groupInfo = statusValue.split("-");
												if (groupInfo.length > 0) {
													for (String strGroup : groupInfo) {
														if (strGroup.length() == 5) {
															Integer member = Integer.valueOf(Integer.parseInt(
																	StringUtils.substringAfter(strGroup, ",")));
															workWithAthleteMemberStatusEvent(
																	Long.valueOf(eventTimestamp), statusType, strGroup,
																	member, workingReaded);
														} else if (TkStrikeCommunicationServiceImpl.loggerStatusEvent
																.isDebugEnabled()) {
															TkStrikeCommunicationServiceImpl.loggerStatusEvent.debug(
																	"INCORRECT GROUP 1 ATHLETE'S PACKET " + strGroup);
														}
													}
													break;
												}
												if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled())
													TkStrikeCommunicationServiceImpl.loggerStatusEvent
															.debug("INCORRECT ATHLETE'S PACKET " + statusValue);
											}
											break;
										}
									} else if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled()) {
										TkStrikeCommunicationServiceImpl.loggerStatusEvent
												.debug("INCORRECT SENSOR'S PACKET " + statusValue);
									}
								} catch (Exception e) {
									if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled())
										TkStrikeCommunicationServiceImpl.loggerStatusEvent
												.error("EXCEPTION PROCESSING Status Packet " + workingReaded, e);
								}
							} else if ("***".equals(workingReaded)) {
								TkStrikeCommunicationServiceImpl.loggerCommEvent.debug("Hem llegit VALOR DEL TOGGLE");
							} else if (StringUtils.isNumeric(workingReaded)) {
								TkStrikeCommunicationServiceImpl.loggerCommEvent.debug("Hem llegit valor numèric");
							} else {
								boolean someNode = false;
								if (TkStrikeCommunicationServiceImpl.isShowIDResponsePattern.matcher(workingReaded)
										.lookingAt())
									TkStrikeCommunicationServiceImpl.loggerCommEvent
											.info("IS A SHOW ID RESPONSE PATTERN");
								if (workingReaded.startsWith("showID?") && workingReaded.length() > 7) {
									workingReaded = StringUtils.substringAfter(workingReaded, "showID?");
									if (TkStrikeCommunicationServiceImpl.loggerCommEvent.isDebugEnabled())
										TkStrikeCommunicationServiceImpl.loggerCommEvent
												.debug("Starts with showID after substring " + workingReaded);
								}
								String nodesWorking = StringUtils.substringBefore(workingReaded, "Judge");
								String judgesWorking = "Judge" + StringUtils.substringAfter(workingReaded, "Judge");
								if (StringUtils.isNotBlank(nodesWorking) && workWithReadNodesString(nodesWorking, "n"))
									someNode = true;
								if (StringUtils.isNotBlank(judgesWorking) && !"Judge".equals(judgesWorking)
										&& workWithReadNodesString(judgesWorking, "Judge"))
									someNode = true;
								if (someNode)
									if (networkConfiguration.areAllNodesInitialized().booleanValue()) {
										TkStrikeCommunicationServiceImpl.logger
												.debug("S'ha determinat tota la xarxa...");
										hasSendShowID = false;
										networkConfiguration.setNetworkWasStarted(Boolean.TRUE);
										networkConfiguration.setChannelNumber(Integer.valueOf(14));
										networkConfiguration.calculateNumberOfJudges();
										networkConfiguration.calculateNumberOfGroups();
										fireNewChangeNetworkConfigurationEvent(Long.valueOf(System.currentTimeMillis()),
												networkStatus, networkConfiguration);
										NetworkStatus last = networkStatus;
										networkStatus = NetworkStatus.OK;
										fireNewChangeNetworkStatusEvent(Long.valueOf(System.currentTimeMillis()),
												networkStatus, last);
										try {
											_startGetData();
										} catch (SerialPortException e) {
											e.printStackTrace();
										}
									}
							}
							workingReaded = resta;
						} while (StringUtils.isNotBlank(workingReaded));
					}
				}
			} catch (Exception e) {
				TkStrikeCommunicationServiceImpl.logger.error("Error on Serial", e);
			}
		}

		private void workWithJudgeStatusEvent(Long eventTimestamp, String value, String judgeNodeId,
				SimpleIntegerProperty prevBadTimes, String nativePacket) {
			if ("0".equals(value)) {
				prevBadTimes.set(0);
				StatusEvent newStatusEvent = new StatusEvent(eventTimestamp, networkStatus, judgeNodeId, Boolean.FALSE,
						"0".equals(value) ? Boolean.TRUE : Boolean.FALSE,
						Double.valueOf("2".equals(value) ? 0.0D : 4.0D),
						Double.valueOf("2".equals(value) ? 0.0D : 90.0D), nativePacket);
				fireNewStatusEvent(newStatusEvent);
			} else if ("1".equals(value)) {
				prevBadTimes.set(prevBadTimes.get() + 1);
				if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled())
					TkStrikeCommunicationServiceImpl.loggerStatusEvent
							.debug("Judge " + judgeNodeId + " is OffLine.. times? " + prevBadTimes.get());
				if (prevBadTimes.get() >= nodeConnBadTimesAllowed.intValue()) {
					if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled())
						TkStrikeCommunicationServiceImpl.loggerStatusEvent
								.debug("Judge " + judgeNodeId + " has exceeded number of offline allowed times");
					StatusEvent newStatusEvent = new StatusEvent(eventTimestamp, networkStatus, judgeNodeId,
							Boolean.TRUE, Boolean.FALSE, Double.valueOf(0.0D), Double.valueOf(0.0D), nativePacket);
					fireNewStatusEvent(newStatusEvent);
				}
			}
		}

		private String _getBodyNodeId4Athlete(String prefix, Integer member) {
			NetworkAthletesGroupConfigDto groupConfig = networkConfiguration.getNetworkAthletesGroupConfig(member);
			if (groupConfig != null) {
				if ("cb".equals(prefix))
					return groupConfig.getBodyBlueNodeId();
				if ("cr".equals(prefix))
					return groupConfig.getBodyRedNodeId();
			}
			return null;
		}

		private String _getHeadNodeId4Athlete(String prefix, Integer member) {
			NetworkAthletesGroupConfigDto groupConfig = networkConfiguration.getNetworkAthletesGroupConfig(member);
			if (groupConfig != null) {
				if ("cb".equals(prefix))
					return groupConfig.getHeadBlueNodeId();
				if ("cr".equals(prefix))
					return groupConfig.getHeadRedNodeId();
			}
			return null;
		}

		private SimpleIntegerProperty _getBodyNodeBadTimes4Athlete(String prefix, Integer member) {
			NetworkAthletesGroupConfigDto groupConfig = networkConfiguration.getNetworkAthletesGroupConfig(member);
			if (groupConfig != null) {
				if ("cb".equals(prefix))
					return groupConfig.bodyBlueNodeBadTimesProperty();
				if ("cr".equals(prefix))
					return groupConfig.bodyRedNodeBadTimesProperty();
			}
			return null;
		}

		private SimpleIntegerProperty _getHeadNodeBadTimes4Athlete(String prefix, Integer member) {
			NetworkAthletesGroupConfigDto groupConfig = networkConfiguration.getNetworkAthletesGroupConfig(member);
			if (groupConfig != null) {
				if ("cb".equals(prefix))
					return groupConfig.headBlueNodeBadTimesProperty();
				if ("cr".equals(prefix))
					return groupConfig.headRedNodeBadTimesProperty();
			}
			return null;
		}

		private SimpleBooleanProperty _getBodyNodeInitialized4Athlete(String prefix, Integer member) {
			NetworkAthletesGroupConfigDto groupConfig = networkConfiguration.getNetworkAthletesGroupConfig(member);
			if (groupConfig != null) {
				if ("cb".equals(prefix))
					return groupConfig.bodyBlueNodeInitializedProperty();
				if ("cr".equals(prefix))
					return groupConfig.bodyRedNodeInitializedProperty();
			}
			return null;
		}

		private SimpleBooleanProperty _getHeadNodeInitialized4Athlete(String prefix, Integer member) {
			NetworkAthletesGroupConfigDto groupConfig = networkConfiguration.getNetworkAthletesGroupConfig(member);
			if (groupConfig != null) {
				if ("cb".equals(prefix))
					return groupConfig.headBlueNodeInitializedProperty();
				if ("cr".equals(prefix))
					return groupConfig.headRedNodeInitializedProperty();
			}
			return null;
		}

		private void workWithAthleteMemberStatusEvent(Long eventTimestamp, String prefix, String value, Integer member,
				String nativePacket) {
			String comm = value.substring(0, 1);
			String conn = value.substring(1, 2);
			String battery = value.substring(2, 3);
			if (member.intValue() <= networkConfiguration.getGroupsNumber().intValue()) {
				SimpleBooleanProperty bodyInitialized = _getBodyNodeInitialized4Athlete(prefix, member);
				SimpleBooleanProperty headInitialized = _getHeadNodeInitialized4Athlete(prefix, member);
				SimpleIntegerProperty bodyBadTimes = _getBodyNodeBadTimes4Athlete(prefix, member);
				SimpleIntegerProperty headBadTimes = _getHeadNodeBadTimes4Athlete(prefix, member);
				String bodyNodeId = _getBodyNodeId4Athlete(prefix, member);
				String headNodeId = _getHeadNodeId4Athlete(prefix, member);
				if (bodyInitialized != null && headInitialized != null && bodyBadTimes != null
						&& headBadTimes != null) {
					boolean bodyOffline = false;
					boolean headOffline = false;
					boolean bodySensorOk = (("0".equals(conn) || "2".equals(conn)) ? Boolean.TRUE : Boolean.FALSE)
							.booleanValue();
					boolean headSensorOk = ("0".equals(conn) ? Boolean.TRUE : Boolean.FALSE).booleanValue();
					double bodyBattery = ("0".equals(battery) || "2".equals(battery)) ? 4.0D : 0.0D;
					double bodyBatteryPct = ("0".equals(battery) || "2".equals(battery)) ? 100.0D : 0.0D;
					double headBattery = "0".equals(battery) ? 4.0D : 0.0D;
					double headBatteryPct = "0".equals(battery) ? 100.0D : 0.0D;
					if ("0".equals(comm)) {
						bodyBadTimes.set(0);
						headBadTimes.set(0);
						bodyInitialized.set(true);
						headInitialized.set(true);
						if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled())
							TkStrikeCommunicationServiceImpl.loggerStatusEvent.debug(prefix + " are OK BOTH!!!");
						bodyOffline = false;
						headOffline = false;
					} else if ("1".equals(comm)) {
						bodyBadTimes.set(bodyBadTimes.get() + 1);
						headBadTimes.set(headBadTimes.get() + 1);
						if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled())
							TkStrikeCommunicationServiceImpl.loggerStatusEvent
									.debug("Body " + bodyNodeId + " is OffLine.. times? " + bodyBadTimes.get());
						if (bodyBadTimes.get() >= nodeConnBadTimesAllowed.intValue() || !bodyInitialized.get()) {
							if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled())
								TkStrikeCommunicationServiceImpl.loggerStatusEvent
										.debug("Body " + bodyNodeId + " has exceeded number of offline allowed times"
												+ bodyBadTimes.get() + " Or initialized? " + bodyInitialized.get());
							bodyOffline = Boolean.TRUE.booleanValue();
						}
						if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled())
							TkStrikeCommunicationServiceImpl.loggerStatusEvent
									.debug("Head " + headNodeId + " is OffLine.. times? " + headBadTimes.get());
						if (headBadTimes.get() >= nodeConnBadTimesAllowed.intValue() || !headInitialized.get()) {
							if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled())
								TkStrikeCommunicationServiceImpl.loggerStatusEvent
										.debug("Head " + headNodeId + " has exceeded number of offline allowed times: "
												+ headBadTimes.get() + " Or initialized? " + headInitialized.get());
							headOffline = Boolean.TRUE.booleanValue();
						}
					} else if ("2".equals(comm)) {
						bodyBadTimes.set(0);
						bodyInitialized.set(true);
						headBadTimes.set(headBadTimes.get() + 1);
						if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled())
							TkStrikeCommunicationServiceImpl.loggerStatusEvent
									.debug("Head " + headNodeId + " is OffLine.. times? " + headBadTimes.get());
						if (headBadTimes.get() >= nodeConnBadTimesAllowed.intValue() || !headInitialized.get()) {
							if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled())
								TkStrikeCommunicationServiceImpl.loggerStatusEvent
										.debug("Head " + headNodeId + " has exceeded number of offline allowed times:"
												+ headBadTimes.get() + " Or initialized? " + headInitialized.get());
							headOffline = Boolean.TRUE.booleanValue();
						}
					}
					fireNewStatusEvent(new StatusEvent(eventTimestamp, networkStatus, bodyNodeId,

							Boolean.valueOf(bodyOffline), Boolean.valueOf(bodySensorOk), Double.valueOf(bodyBattery),
							Double.valueOf(bodyBatteryPct), nativePacket));
					fireNewStatusEvent(new StatusEvent(eventTimestamp, networkStatus, headNodeId,

							Boolean.valueOf(headOffline), Boolean.valueOf(headSensorOk), Double.valueOf(headBattery),
							Double.valueOf(headBatteryPct), nativePacket));
				} else if (TkStrikeCommunicationServiceImpl.loggerStatusEvent.isDebugEnabled()) {
					TkStrikeCommunicationServiceImpl.loggerStatusEvent
							.debug("Can't get body or head bad times for string:" + value);
				}
			}
		}
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
			if (arrNodeIds.length == arrNodes.length)
				for (int i = 0; i < arrNodes.length; i++) {
					if (StringUtils.isNotBlank(arrNodes[i]) && StringUtils.isNotBlank(arrNodeIds[i])) {
						String node = StringUtils.substringBefore(arrNodes[i], ":");
						String nodeId = arrNodeIds[i];
						if (workWithNode(prefix, nodeId, node))
							someNode = true;
					}
				}
		} else if (countMatches == 1) {
			return workWithNode(prefix, StringUtils.substringAfter(nodesString, ":"),
					StringUtils.substringBefore(StringUtils.substringAfter(nodesString, prefix), ":"));
		}
		return someNode;
	}

	private boolean workWithNode(String prefix, String nodeId, String node) {
		boolean someNode = false;
		Integer nodeNumber = Integer.valueOf(Integer.parseInt(node));
		if ("n".equals(prefix)) {
			int member = 1;
			while (nodeNumber.intValue() > 4) {
				member++;
				nodeNumber = Integer.valueOf(nodeNumber.intValue() - 4);
			}
			NetworkAthletesGroupConfigDto groupConfig = this.networkConfiguration
					.getNetworkAthletesGroupConfig(Integer.valueOf(member));
			switch (nodeNumber.intValue()) {
			case 1:
				groupConfig.setBodyBlueNodeId(nodeId);
				groupConfig.setBodyBlueNodeBadTimes(0);
				groupConfig.setBodyBlueNodeInitialized(false);
				someNode = true;
				if (loggerCommEvent.isDebugEnabled())
					loggerCommEvent.debug("Member " + member + " blueBodyNodeId OK");
				break;
			case 2:
				groupConfig.setHeadBlueNodeId(nodeId);
				groupConfig.setHeadBlueNodeBadTimes(0);
				groupConfig.setHeadBlueNodeInitialized(false);
				someNode = true;
				if (loggerCommEvent.isDebugEnabled())
					loggerCommEvent.debug("Member " + member + " blueHeadNodeId OK");
				break;
			case 3:
				groupConfig.setBodyRedNodeId(nodeId);
				groupConfig.setBodyRedNodeBadTimes(0);
				groupConfig.setBodyRedNodeInitialized(false);
				someNode = true;
				if (loggerCommEvent.isDebugEnabled())
					loggerCommEvent.debug("Member " + member + " redBodyNodeId OK");
				break;
			case 4:
				groupConfig.setHeadRedNodeId(nodeId);
				groupConfig.setHeadRedNodeBadTimes(0);
				groupConfig.setHeadRedNodeInitialized(false);
				someNode = true;
				if (loggerCommEvent.isDebugEnabled())
					loggerCommEvent.debug("Member " + member + " redHeadGNodeId OK");
				break;
			}
		} else {
			switch (nodeNumber.intValue()) {
			case 1:
				this.networkConfiguration.setJudge1NodeId(nodeId);
				someNode = true;
				if (loggerCommEvent.isDebugEnabled())
					loggerCommEvent.debug("judge1NodeId OK");
				break;
			case 2:
				this.networkConfiguration.setJudge2NodeId(nodeId);
				someNode = true;
				if (loggerCommEvent.isDebugEnabled())
					loggerCommEvent.debug("judge2NodeId OK");
				break;
			case 3:
				this.networkConfiguration.setJudge3NodeId(nodeId);
				someNode = true;
				if (loggerCommEvent.isDebugEnabled())
					loggerCommEvent.debug("judge3NodeId OK");
				break;
			}
		}
		return someNode;
	}
}
