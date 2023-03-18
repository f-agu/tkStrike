package org.crm.tkstrike.testnetwork;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class TkStrikeCommunication implements Closeable {
	private static final String SEND_4_VALIDATE = "v?";
	private static final String RX_4_VALIDATE = "TS_GEN2";
	private static final String GET_COMM_DATA_TOGGLE = "***";
	private static final String SEND_SHOWID = "showID?";
	private static final String STATUS_PACKET_COM_SEPARATOR = new String(new byte[] { 2 });
	private static final String STATUS_PACKET_COM_HEADER = new String(new byte[] { 1 });
	private static final String STAUTS_PACKET_HEADER = "#";
	private static final String STAUTS_PACKET_SEPARATOR = "-";
	private ConcurrentHashMap<String, Long> headNodeLastHitTimestamp = new ConcurrentHashMap();
	private ConcurrentHashMap<String, Long> judgeNodeLastClickTimestamp = new ConcurrentHashMap(18);
	private SerialPort serialPort = null;
	private boolean getDataEnabled = false;
	private boolean hasValidateDataEnabled = false;
	private boolean hasSendShowID = false;
	private int judge1NodeBadTimes;
	private int judge2NodeBadTimes;
	private int judge3NodeBadTimes;
	private NetworkConfigurationDto networkConfiguration;
	private NetworkStatus networkStatus;
	private Integer maxNetworkAthleteGroupsAllowed = 2;
	private Long headHitTimeValidation = 500L;
	private Long judgeClickTimeValidation = 500L;
	private Integer nodeConnBadTimesAllowed = 5;
	private String serialNamePattern = "^COM";
	private Integer initBodyGap = 15;
	private Integer initHeadGap = 25;
	private static final Pattern isStatusEventPattern = Pattern.compile("^c[b|r|j]");
	private static final Pattern isHitEventPattern = Pattern.compile("^[j[1-3]|bb|bh|rb|rh|bp|rp]");
	private static final Pattern isShowIDResponsePattern = Pattern.compile("^showID\\?|n[1-8]|Judge[1-3]:");
	private CustomSerialListener customSerialListener = new CustomSerialListener();

	public TkStrikeCommunication() {
		this.networkStatus = NetworkStatus.NOT_CONNECTED;
		this.networkConfiguration = new NetworkConfigurationDto(this.maxNetworkAthleteGroupsAllowed);
	}

	public synchronized void startComm() {
		System.out.println("Start Port Gen2");

		if (serialPort == null) {
			String portName = null;
			try {
				String[] portNames = SerialPortList.getPortNames();

				for (int i = 0; i < portNames.length; ++i) {
					String tempPortName = portNames[i];
					System.out.println("Port Name = " + tempPortName);

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
								System.out.println("Port " + tempPortName + " is TkStrike GEN2 OK");

								portName = tempPortName;
								toValidateSerialPort.writeString("bodygap=" + initBodyGap.toString(),
										StandardCharsets.US_ASCII.name());
								toValidateSerialPort.writeByte((byte) 13);
								toValidateSerialPort.writeString("headgap=" + initHeadGap.toString(),
										StandardCharsets.US_ASCII.name());
								toValidateSerialPort.writeByte((byte) 13);
								toValidateSerialPort.writeString("gap?", StandardCharsets.US_ASCII.name());
								toValidateSerialPort.writeByte((byte) 13);
								TimeUnit.SECONDS.sleep(1L);
								r = toValidateSerialPort.getInputBufferBytesCount();
								readed = toValidateSerialPort.readString(r);
								System.out.println("CurrentGap ?= [" + readed + "]");
								readed = null;
							}
						} catch (Exception var19) {
							var19.printStackTrace();
						} finally {
							try {
								toValidateSerialPort.closePort();
							} catch (SerialPortException var18) {
							}

						}
					}
				}
			} catch (Exception var21) {
				var21.printStackTrace();
			}

			if (portName != null) {
				System.out.println("Open port " + portName);
				this.serialPort = new SerialPort(portName);
				try {
					this.serialPort.openPort();
					this.serialPort.setParams(38400, 8, 1, 0);
					this._validateIfIsGetDataEnabled();
					this.serialPort.addEventListener(this.customSerialListener);
					NetworkStatus last = this.networkStatus;
					this.networkStatus = NetworkStatus.NOT_STARTED;
					// this.fireNewChangeNetworkStatusEvent(System.currentTimeMillis(),
					// this.networkStatus, last);
					return;
				} catch (SerialPortException e) {
					this.serialPort = null;
					throw new RuntimeException(e);
				}
			}
		}

		if (this.serialPort == null) {
			throw new RuntimeException("Not connected");
		}
	}

	public void stopComm() {
		if (this.serialPort != null) {
			boolean var6 = false;

			try {
				var6 = true;
				this._stopGetData();
				this.serialPort.removeEventListener();
				this.serialPort.closePort();
				var6 = false;
			} catch (SerialPortException var7) {
				throw new RuntimeException(var7);
			} finally {
				if (var6) {
					this.serialPort = null;
					NetworkStatus last = this.networkStatus;
					this.networkStatus = NetworkStatus.NOT_CONNECTED;
					// this.fireNewChangeNetworkStatusEvent(System.currentTimeMillis(),
					// this.networkStatus, last);
				}
			}

			this.serialPort = null;
			NetworkStatus last = this.networkStatus;
			this.networkStatus = NetworkStatus.NOT_CONNECTED;
			// this.fireNewChangeNetworkStatusEvent(System.currentTimeMillis(),
			// this.networkStatus, last);
		}
	}

	public NetworkStatus getCurrentNetworkStatus() {
		return this.networkStatus;
	}

	public void tryToRecognizeWithConfig(NetworkConfigurationDto networkConfigurationDto,
			boolean forceInitializerSerial) {
		System.out.println("tryToRecognizeWithConfig() method");
		_cleanAllNodes();
		try {
			_sendShowID();
		} catch (SerialPortException var4) {
			throw new RuntimeException(var4);
		}
	}

	public void startNetwork(NetworkConfigurationDto networkConfiguration) {
		if (networkConfiguration != null) {
			this.stopComm();
			System.out.println("Start network with " + ToStringBuilder.reflectionToString(networkConfiguration));

			try {
				this._stopGetData();
				networkConfiguration.calculateNumberOfGroups();
				System.out.println("    ------ NETWORK GROUPS = " + networkConfiguration.getGroupsNumber());
				this.doInitializeGroupsNumber(networkConfiguration.getGroupsNumber());
				NetworkStatus last = this.networkStatus;
				this.networkStatus = NetworkStatus.NOT_STARTED;
				// this.fireNewChangeNetworkStatusEvent(System.currentTimeMillis(),
				// this.networkStatus, last);
				this._cleanAllNodes();
				int currNodeCount = 1;

				for (int i = 1; i <= this.maxNetworkAthleteGroupsAllowed; ++i) {
					NetworkAthletesGroupConfigDto groupConfig = networkConfiguration.getNetworkAthletesGroupConfig(i);
					if (groupConfig != null) {
						boolean bodyEnabled = groupConfig.getBodySensorsEnabled();
						boolean headEnabled = groupConfig.getHeadSensorsEnabled();
						boolean groupEnabled = networkConfiguration.getGroupsNumber() >= i;
						System.out.println("    GROUP " + i + ": " + (groupEnabled ? "enabled" : "DISABLED"));
						System.out.println("    BODY  " + i + ": " + (bodyEnabled ? "enabled" : "DISABLED"));
						System.out.println("    HEAD  " + i + ": " + (headEnabled ? "enabled" : "DISABLED"));
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
						System.out.println("    TO SEND n" + currNodeCount + "=" + blueBody);
						this._sendString2Serial("n" + currNodeCount + "=" + blueBody);
						++currNodeCount;
						System.out.println("    TO SEND n" + currNodeCount + "=" + blueHead);
						this._sendString2Serial("n" + currNodeCount + "=" + blueHead);
						++currNodeCount;
						System.out.println("    TO SEND n" + currNodeCount + "=" + redBody);
						this._sendString2Serial("n" + currNodeCount + "=" + redBody);
						++currNodeCount;
						System.out.println("    TO SEND n" + currNodeCount + "=" + redHead);
						this._sendString2Serial("n" + currNodeCount + "=" + redHead);
						++currNodeCount;
					}
				}

//				System.out.println("    TO SEND j1="
//						+ (networkConfiguration.getJudgesNumber() >= 1 ? networkConfiguration.getJudge1NodeId() : "0"));
//				_sendString2Serial("j1="
//						+ (networkConfiguration.getJudgesNumber() >= 1 ? networkConfiguration.getJudge1NodeId() : "0"));
//				System.out.println("    TO SEND j2="
//						+ (networkConfiguration.getJudgesNumber() >= 2 ? networkConfiguration.getJudge2NodeId() : "0"));
//				_sendString2Serial("j2="
//						+ (networkConfiguration.getJudgesNumber() >= 2 ? networkConfiguration.getJudge2NodeId() : "0"));
//				System.out.println("    TO SEND j3="
//						+ (networkConfiguration.getJudgesNumber() == 3 ? networkConfiguration.getJudge3NodeId() : "0"));
//				_sendString2Serial("j3="
//						+ (networkConfiguration.getJudgesNumber() == 3 ? networkConfiguration.getJudge3NodeId() : "0"));
				this.tryToRecognizeWithConfig(networkConfiguration, false);
			} catch (SerialPortException var13) {
				throw new RuntimeException(var13);
			}
		}

	}

	private void doInitializeGroupsNumber(int groupsNumber) throws SerialPortException {
		_sendString2Serial("group?");
		try {
			TimeUnit.SECONDS.sleep(1L);
		} catch (InterruptedException var5) {
		}
		String readed = serialPort.readString(serialPort.getInputBufferBytesCount());
		if (readed != null && !readed.toLowerCase().contains("invalid command")) {
			System.out.println("    Is TkStrike with Groups Setting Conf");
			this._sendString2Serial("group=" + groupsNumber);
			try {
				TimeUnit.SECONDS.sleep(1L);
			} catch (InterruptedException var4) {
			}
			readed = this.serialPort.readString(this.serialPort.getInputBufferBytesCount());
			System.out.println("    Group ->" + readed);
		}
	}

	public NetworkConfigurationDto getCurrentNetworkConfiguration() {
		return this.networkConfiguration;
	}

	private void validateSerial() {
		if (this.serialPort == null) {
			this.startComm();
		}
	}

	private void _validateIfIsGetDataEnabled() {
		if (!this.hasValidateDataEnabled) {
			System.out.println("Validate if GetData is Enabled");
			this.validateSerial();
			try {
				System.out.println("    Sleep 3 seconds");
				try {
					Thread.sleep(3000L);
				} catch (InterruptedException var3) {
				}
				int r = serialPort.getInputBufferBytesCount();
				String strReaded = serialPort.readString(r);
				if (StringUtils.isNotBlank(strReaded)) {
					strReaded = this._cleanReadedFromSerialString(strReaded);
					System.out.println("    READED after Wait " + strReaded);
					this.getDataEnabled = isStatusEventPattern.matcher(strReaded).lookingAt();
				} else {
					this.getDataEnabled = false;
				}
				this.hasValidateDataEnabled = true;
			} catch (SerialPortException var4) {
				var4.printStackTrace();
			}
		}
		System.out.println("    Validate if Data is Enabled - GetDataEnabled = " + getDataEnabled);
	}

	private void _sendString2Serial(String string) throws SerialPortException {
		if (StringUtils.isNotBlank(string)) {
			this.validateSerial();
			System.out.println("Send to serial [" + string + "]");
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
		System.out.println("_stopGetData() method");
		if (this.getDataEnabled) {
			System.out.println("     SEND STOP DATA ************************************************");
			this._sendString2Serial("***");
		}

		this.getDataEnabled = false;
	}

	private void _startGetData() throws SerialPortException {
		this.validateSerial();
		System.out.println("_startGetData() method");
		if (!this.getDataEnabled) {
			System.out.println("    SEND START DATA ************************************************");
			this._sendString2Serial("***");
		}

		this.getDataEnabled = true;
	}

	private void _sendShowID() throws SerialPortException {
		this.validateSerial();
		System.out.println("_sendShowID() method");
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
			readed = readed.replaceAll(STATUS_PACKET_COM_SEPARATOR, "-");
			readed = readed.replaceAll(STATUS_PACKET_COM_HEADER, "#");
		}

		return readed;
	}

	private void _cleanAllNodes() {
		this.networkConfiguration.cleanAllNodes();
		judge1NodeBadTimes = 0;
		judge2NodeBadTimes = 0;
		judge3NodeBadTimes = 0;
		this.headNodeLastHitTimestamp = new ConcurrentHashMap();
		this.judgeNodeLastClickTimestamp = new ConcurrentHashMap(18);
	}

//	private void fireNewDataEvent(DataEvent newDataEvent) {
//		ArrayList<Callable<Void>> tasks = new ArrayList(this.listeners.size());
//		this.listeners.forEach((lis) -> {
//			tasks.add(new Callable<Void>() {
//				public Void call() throws Exception {
//					lis.hasNewDataEvent(var2);
//					return null;
//				}
//			});
//		});
//
//		try {
//			TkStrikeExecutors.executeInParallel(tasks);
//		} catch (InterruptedException var4) {
//			var4.printStackTrace();
//		}
//
//	}

//	private void fireNewStatusEvent(StatusEvent newStatusEvent) {
//		ArrayList<Callable<Void>> tasks = new ArrayList(this.listeners.size());
//		if (loggerStatusEvent.isDebugEnabled()) {
//			System.out.println("Fire New Status Event ->" + newStatusEvent.toString());
//		}
//
//		this.listeners.forEach((lis) -> {
//			tasks.add(new Callable<Void>() {
//				public Void call() throws Exception {
//					lis.hasNewStatusEvent(var2);
//					return null;
//				}
//			});
//		});
//
//		try {
//			TkStrikeExecutors.executeInParallel(tasks);
//		} catch (InterruptedException var4) {
//			var4.printStackTrace();
//		}
//
//	}

//	private void fireNewChangeNetworkStatusEvent(Long timestamp, NetworkStatus prevStatus, NetworkStatus newStatus) {
//		ChangeNetworkStatusEvent changeNetworkStatusEvent = new ChangeNetworkStatusEvent(timestamp, prevStatus,
//				newStatus);
//		ArrayList<Callable<Void>> tasks = new ArrayList(this.listeners.size());
//		this.listeners.forEach((lis) -> {
//			tasks.add(new Callable<Void>() {
//				public Void call() throws Exception {
//					lis.hasChangeNetworkStatusEvent(var2);
//					return null;
//				}
//			});
//		});
//
//		try {
//			TkStrikeExecutors.executeInParallel(tasks);
//		} catch (InterruptedException var7) {
//			var7.printStackTrace();
//		}
//
//	}

//	private void fireNewChangeNetworkConfigurationEvent(Long timestamp, NetworkStatus prevStatus,
//			NetworkConfigurationDto networkConfiguration) {
//		ChangeNetworkConfigurationEvent changeNetworkConfigurationEvent = new ChangeNetworkConfigurationEvent(timestamp,
//				prevStatus, networkConfiguration);
//		ArrayList<Callable<Void>> tasks = new ArrayList(this.listeners.size());
//		this.listeners.forEach((lis) -> {
//			tasks.add(new Callable<Void>() {
//				public Void call() throws Exception {
//					lis.hasChangeNetworkConfigurationEvent(var2);
//					return null;
//				}
//			});
//		});
//
//		try {
//			TkStrikeExecutors.executeInParallel(tasks);
//		} catch (InterruptedException var7) {
//			var7.printStackTrace();
//		}
//
//	}

	public void run() {
	}

	public void afterPropertiesSet() throws Exception {
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

			NetworkAthletesGroupConfigDto groupConfig = networkConfiguration.getNetworkAthletesGroupConfig(member);
			switch (nodeNumber) {
			case 1:
				groupConfig.setBodyBlueNodeId(nodeId);
				// groupConfig.setBodyBlueNodeBadTimes(0);
				// groupConfig.setBodyBlueNodeInitialized(false);
				someNode = true;
				System.out.println("Member " + member + " blueBodyNodeId OK");
				break;
			case 2:
				groupConfig.setHeadBlueNodeId(nodeId);
				// groupConfig.setHeadBlueNodeBadTimes(0);
				// groupConfig.setHeadBlueNodeInitialized(false);
				someNode = true;
				System.out.println("Member " + member + " blueHeadNodeId OK");
				break;
			case 3:
				groupConfig.setBodyRedNodeId(nodeId);
				// groupConfig.setBodyRedNodeBadTimes(0);
				// groupConfig.setBodyRedNodeInitialized(false);
				someNode = true;
				System.out.println("Member " + member + " redBodyNodeId OK");
				break;
			case 4:
				groupConfig.setHeadRedNodeId(nodeId);
				// groupConfig.setHeadRedNodeBadTimes(0);
				// groupConfig.setHeadRedNodeInitialized(false);
				someNode = true;
				System.out.println("Member " + member + " redHeadGNodeId OK");
			}
		} else {
			switch (nodeNumber) {
			case 1:
				this.networkConfiguration.setJudge1NodeId(nodeId);
				someNode = true;
				System.out.println("judge1NodeId OK");
				break;
			case 2:
				this.networkConfiguration.setJudge2NodeId(nodeId);
				someNode = true;
				System.out.println("judge2NodeId OK");
				break;
			case 3:
				this.networkConfiguration.setJudge3NodeId(nodeId);
				someNode = true;
				System.out.println("judge3NodeId OK");
			}
		}

		return someNode;
	}

	private static void log(SerialPortEvent serialPortEvent, String message) {
		log(serialPortEvent, message, null);
	}

	private static void log(SerialPortEvent serialPortEvent, String message, Exception e) {
		System.out.println("@" + StringUtils.rightPad(Integer.toHexString(serialPortEvent.hashCode()), 10) + message);
		if (e != null) {
			e.printStackTrace();
		}
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
						readed2 = readed2.replaceAll(STATUS_PACKET_COM_SEPARATOR, "-");
						readed2 = readed2.replaceAll(STATUS_PACKET_COM_HEADER, "#");
						log(serialPortEvent,
								"READED " + readed2.length() + ": '" + readed2 + "' <=====================");

						String workingReaded = readed2;

						String resta = null;
						do {
							resta = "";
							String temp = workingReaded;
							if (workingReaded.contains("#")) {
								workingReaded = StringUtils.substringBefore(workingReaded, "#");
								resta = StringUtils.substringAfter(temp, "#");
							}
							String judgesWorking;
							if (isHitEventPattern.matcher(workingReaded).lookingAt()) {
								log(serialPortEvent, "Is HIT Event => " + workingReaded);
								long eventTimestamp = System.currentTimeMillis();
								if (workingReaded.length() > 2) {
									judgesWorking = workingReaded.substring(0, 2);
									String hitValue;
									boolean throwEvent;
									if (judgesWorking.startsWith("j")) {
										if (!" ".equals(workingReaded.substring(2, 3))) {
											log(serialPortEvent, "Is not a valid JUDGE HIT EVENT!");
											return;
										}
										hitValue = StringUtils.substringBetween(workingReaded, " ");
										log(serialPortEvent, "JUDGE Source " + judgesWorking + " Value = " + hitValue);
										String nodeId = networkConfiguration.getJudge1NodeId();
										if (judgesWorking.startsWith("j2")) {
											nodeId = networkConfiguration.getJudge2NodeId();
										} else if (judgesWorking.startsWith("j3")) {
											nodeId = networkConfiguration.getJudge3NodeId();
										}
										int intHitValue = 0;
										byte var35 = -1;
										log(serialPortEvent, "hitValue: " + hitValue);
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

										// TODO
									} else {
										hitValue = workingReaded.substring(2, 3);
										String[] hitValueParts = StringUtils
												.split(workingReaded.substring(3, workingReaded.length()), " ");
										String nodeIdx = null;
										log(serialPortEvent, "SENSOR Source " + judgesWorking + " Group " + hitValue
												+ " ValueParts = " + ToStringBuilder.reflectionToString(hitValueParts));
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

										// TODO
									}

								}

							} else {
								String statusValue;
								if (isStatusEventPattern.matcher(workingReaded).lookingAt()) {
									log(serialPortEvent,
											"Is Status Event STATUS: " + StringUtils.substringBefore(workingReaded, "-")
													+ " -> " + StringUtils.substringAfter(workingReaded, "-"));
									try {
										String statusType = StringUtils.substringBefore(workingReaded, "-");
										statusValue = StringUtils.substringAfter(workingReaded, "-");
										log(serialPortEvent, "Status value of " + statusType + ": [" + statusValue
												+ "] length:" + statusValue.length());
										if (StringUtils.isNotBlank(statusValue)) {
											long eventTimestampx = System.currentTimeMillis();
											switch (statusType) {
											case "cj":
												if (statusValue.length() == 3) {
													log(serialPortEvent, "Correct judge's packet: " + statusValue);
//													this.workWithJudgeStatusEvent(eventTimestampx,
//															statusValue.substring(0, 1),
//															networkConfiguration.getJudge1NodeId(), judge1NodeBadTimes,
//															workingReaded);
//													this.workWithJudgeStatusEvent(eventTimestampx,
//															statusValue.substring(1, 2),
//															networkConfiguration.getJudge2NodeId(), judge2NodeBadTimes,
//															workingReaded);
//													this.workWithJudgeStatusEvent(eventTimestampx,
//															statusValue.substring(2, 3),
//															networkConfiguration.getJudge3NodeId(), judge3NodeBadTimes,
//															workingReaded);
												} else {
													log(serialPortEvent, "Incorrect judges's packet " + statusValue);
												}
												break;
											case "cb":
											case "cr":

												if (statusValue.contains("-") || statusValue.length() == 5) {
													log(serialPortEvent, "Athlete sensor's packet: " + workingReaded);
													String[] groupInfo = statusValue.split("-");
													if (groupInfo.length > 0) {
														String[] var15 = groupInfo;
														int var16 = groupInfo.length;
														for (int var17 = 0; var17 < var16; ++var17) {
															String strGroup = var15[var17];
															if (strGroup.length() == 5) {
																Integer member = Integer.parseInt(
																		StringUtils.substringAfter(strGroup, ","));
																workWithAthleteMemberStatusEvent(eventTimestampx,
																		statusType, strGroup, member, workingReaded);
															} else {
																log(serialPortEvent,
																		"INCORRECT GROUP 1 ATHLETE'S PACKET "
																				+ strGroup);
															}
														}
													} else {
														log(serialPortEvent,
																"INCORRECT ATHLETE'S PACKET " + statusValue);
													}
												} else {
													log(serialPortEvent, "Athlete sensor's packet: " + workingReaded
															+ " ; but ignored");
												}
											}
										} else {
											log(serialPortEvent, "INCORRECT SENSOR'S PACKET " + statusValue);
										}
									} catch (Exception e) {
										log(serialPortEvent, "EXCEPTION PROCESSING Status Packet " + workingReaded, e);
									}
								} else if ("***".equals(workingReaded)) {
									log(serialPortEvent, "We read TOGGLE VALUE");
								} else if (StringUtils.isNumeric(workingReaded)) {
									log(serialPortEvent, "We read numerical value");
								} else {
									boolean someNode = false;
									if (isShowIDResponsePattern.matcher(workingReaded).lookingAt()) {
										log(serialPortEvent, "IS A SHOW ID RESPONSE PATTERN");
									}
									if (workingReaded.startsWith("showID?") && workingReaded.length() > 7) {
										workingReaded = StringUtils.substringAfter(workingReaded, "showID?");
										log(serialPortEvent, "Starts with showID after substring " + workingReaded);
									}
									statusValue = StringUtils.substringBefore(workingReaded, "Judge");
									judgesWorking = "Judge" + StringUtils.substringAfter(workingReaded, "Judge");
									if (StringUtils.isNotBlank(statusValue)
											&& workWithReadNodesString(statusValue, "n")) {
										someNode = true;
									}
									if (StringUtils.isNotBlank(judgesWorking) && !"Judge".equals(judgesWorking)
											&& workWithReadNodesString(judgesWorking, "Judge")) {
										someNode = true;
									}
									if (someNode && networkConfiguration.areAllNodesInitialized()) {
										log(serialPortEvent, "The entire network has been determined...");
										hasSendShowID = false;
										networkConfiguration.setNetworkWasStarted(Boolean.TRUE);
										networkConfiguration.setChannelNumber(14);
										networkConfiguration.calculateNumberOfJudges();
										networkConfiguration.calculateNumberOfGroups();
										// fireNewChangeNetworkConfigurationEvent(System.currentTimeMillis(),
										// networkStatus, networkConfiguration);
										NetworkStatus last = networkStatus;
										networkStatus = NetworkStatus.OK;
										// fireNewChangeNetworkStatusEvent(System.currentTimeMillis(), networkStatus,
										// last);

										try {
											_startGetData();
										} catch (SerialPortException var20) {
											var20.printStackTrace();
										}
									}
								}
								// TODO
							}

							workingReaded = resta;
						} while (StringUtils.isNotBlank(resta));
					}
				}
			} catch (Exception var23) {
				var23.printStackTrace();
			}
		}

	}

//	private void workWithJudgeStatusEvent(Long eventTimestamp, String value, String judgeNodeId,
//			SimpleIntegerProperty prevBadTimes, String nativePacket) {
//		StatusEvent newStatusEvent;
//		if ("0".equals(value)) {
//			prevBadTimes.set(0);
//			newStatusEvent = new StatusEvent(eventTimestamp, this.networkStatus, judgeNodeId, Boolean.FALSE,
//					"0".equals(value) ? Boolean.TRUE : Boolean.FALSE, "2".equals(value) ? 0.0D : 4.0D,
//					"2".equals(value) ? 0.0D : 90.0D, nativePacket);
//			this.fireNewStatusEvent(newStatusEvent);
//		} else if ("1".equals(value)) {
//			prevBadTimes.set(prevBadTimes.get() + 1);
//			if (loggerStatusEvent.isDebugEnabled()) {
//				System.out.println("Judge " + judgeNodeId + " is OffLine.. times? " + prevBadTimes.get());
//			}
//
//			if (prevBadTimes.get() >= this.nodeConnBadTimesAllowed) {
//				if (loggerStatusEvent.isDebugEnabled()) {
//					System.out.println("Judge " + judgeNodeId + " has exceeded number of offline allowed times");
//				}
//
//				newStatusEvent = new StatusEvent(eventTimestamp, this.networkStatus, judgeNodeId, Boolean.TRUE,
//						Boolean.FALSE, 0.0D, 0.0D, nativePacket);
//				this.fireNewStatusEvent(newStatusEvent);
//			}
//		}
//
//	}

	private String _getBodyNodeId4Athlete(String prefix, Integer member) {
		NetworkAthletesGroupConfigDto groupConfig = networkConfiguration.getNetworkAthletesGroupConfig(member);
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
		NetworkAthletesGroupConfigDto groupConfig = networkConfiguration.getNetworkAthletesGroupConfig(member);
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

//	private SimpleIntegerProperty _getBodyNodeBadTimes4Athlete(String prefix, Integer member) {
//		NetworkAthletesGroupConfigDto groupConfig = networkConfiguration.getNetworkAthletesGroupConfig(member);
//		if (groupConfig != null) {
//			if ("cb".equals(prefix)) {
//				return groupConfig.bodyBlueNodeBadTimesProperty();
//			}
//
//			if ("cr".equals(prefix)) {
//				return groupConfig.bodyRedNodeBadTimesProperty();
//			}
//		}
//
//		return null;
//	}
//
//	private SimpleIntegerProperty _getHeadNodeBadTimes4Athlete(String prefix, Integer member) {
//		NetworkAthletesGroupConfigDto groupConfig = networkConfiguration.getNetworkAthletesGroupConfig(member);
//		if (groupConfig != null) {
//			if ("cb".equals(prefix)) {
//				return groupConfig.headBlueNodeBadTimesProperty();
//			}
//
//			if ("cr".equals(prefix)) {
//				return groupConfig.headRedNodeBadTimesProperty();
//			}
//		}
//
//		return null;
//	}
//
//	private SimpleBooleanProperty _getBodyNodeInitialized4Athlete(String prefix, Integer member) {
//		NetworkAthletesGroupConfigDto groupConfig = networkConfiguration.getNetworkAthletesGroupConfig(member);
//		if (groupConfig != null) {
//			if ("cb".equals(prefix)) {
//				return groupConfig.bodyBlueNodeInitializedProperty();
//			}
//
//			if ("cr".equals(prefix)) {
//				return groupConfig.bodyRedNodeInitializedProperty();
//			}
//		}
//
//		return null;
//	}
//
//	private SimpleBooleanProperty _getHeadNodeInitialized4Athlete(String prefix, Integer member) {
//		NetworkAthletesGroupConfigDto groupConfig = networkConfiguration.getNetworkAthletesGroupConfig(member);
//		if (groupConfig != null) {
//			if ("cb".equals(prefix)) {
//				return groupConfig.headBlueNodeInitializedProperty();
//			}
//
//			if ("cr".equals(prefix)) {
//				return groupConfig.headRedNodeInitializedProperty();
//			}
//		}
//
//		return null;
//	}

	private void workWithAthleteMemberStatusEvent(Long eventTimestamp, String prefix, String value, Integer member,
			String nativePacket) {
		String comm = value.substring(0, 1);
		String conn = value.substring(1, 2);
		String battery = value.substring(2, 3);
		System.out.println("comm: " + comm + " ; conn: " + conn + " ; battery: " + battery + " ; " + member + " <= "
				+ networkConfiguration.getGroupsNumber());
		if (member <= networkConfiguration.getGroupsNumber()) {
//			SimpleBooleanProperty bodyInitialized = this._getBodyNodeInitialized4Athlete(prefix, member);
//			SimpleBooleanProperty headInitialized = this._getHeadNodeInitialized4Athlete(prefix, member);
//			SimpleIntegerProperty bodyBadTimes = this._getBodyNodeBadTimes4Athlete(prefix, member);
//			SimpleIntegerProperty headBadTimes = this._getHeadNodeBadTimes4Athlete(prefix, member);
			String bodyNodeId = this._getBodyNodeId4Athlete(prefix, member);
			String headNodeId = this._getHeadNodeId4Athlete(prefix, member);
//			if (bodyInitialized != null && headInitialized != null && bodyBadTimes != null && headBadTimes != null) {
//				boolean bodyOffline = false;
//				boolean headOffline = false;
//				boolean bodySensorOk = !"0".equals(conn) && !"2".equals(conn) ? Boolean.FALSE : Boolean.TRUE;
//				boolean headSensorOk = "0".equals(conn) ? Boolean.TRUE : Boolean.FALSE;
//				double bodyBattery = !"0".equals(battery) && !"2".equals(battery) ? 0.0D : 4.0D;
//				double bodyBatteryPct = !"0".equals(battery) && !"2".equals(battery) ? 0.0D : 100.0D;
//				double headBattery = "0".equals(battery) ? 4.0D : 0.0D;
//				double headBatteryPct = "0".equals(battery) ? 100.0D : 0.0D;
//				if ("0".equals(comm)) {
//					// bodyBadTimes.set(0);
//					// headBadTimes.set(0);
//					// bodyInitialized.set(true);
//					// headInitialized.set(true);
//					System.out.println(prefix + " are OK BOTH!!!");
//
//					bodyOffline = false;
//					headOffline = false;
//				} else if ("1".equals(comm)) {
//					// bodyBadTimes.set(bodyBadTimes.get() + 1);
//					// headBadTimes.set(headBadTimes.get() + 1);
//					System.out.println("Body " + bodyNodeId + " is OffLine.. times? " + bodyBadTimes.get());
//
//					if (bodyBadTimes.get() >= this.nodeConnBadTimesAllowed || !bodyInitialized.get()) {
//						System.out.println("Body " + bodyNodeId + " has exceeded number of offline allowed times"
//								+ bodyBadTimes.get() + " Or initialized? " + bodyInitialized.get());
//
//						bodyOffline = Boolean.TRUE;
//					}
//
//					System.out.println("Head " + headNodeId + " is OffLine.. times? " + headBadTimes.get());
//
//					if (headBadTimes.get() >= this.nodeConnBadTimesAllowed || !headInitialized.get()) {
//						System.out.println("Head " + headNodeId + " has exceeded number of offline allowed times: "
//								+ headBadTimes.get() + " Or initialized? " + headInitialized.get());
//
//						headOffline = Boolean.TRUE;
//					}
//				} else if ("2".equals(comm)) {
//					bodyBadTimes.set(0);
//					bodyInitialized.set(true);
//					headBadTimes.set(headBadTimes.get() + 1);
//					System.out.println("Head " + headNodeId + " is OffLine.. times? " + headBadTimes.get());
//				}
//				if (headBadTimes.get() >= this.nodeConnBadTimesAllowed || !headInitialized.get()) {
//					System.out.println("Head " + headNodeId + " has exceeded number of offline allowed times:"
//							+ headBadTimes.get() + " Or initialized? " + headInitialized.get());
//
//					headOffline = Boolean.TRUE;
//				}
//			}

//			this.fireNewStatusEvent(new StatusEvent(eventTimestamp, this.networkStatus, bodyNodeId, bodyOffline,
//					bodySensorOk, bodyBattery, bodyBatteryPct, nativePacket));
//			this.fireNewStatusEvent(new StatusEvent(eventTimestamp, this.networkStatus, headNodeId, headOffline,
//					headSensorOk, headBattery, headBatteryPct, nativePacket));
		} else {
			System.out.println("Can't get body or head bad times for string:" + value);
		}
	}

	@Override
	public void close() throws IOException {
		stopComm();
	}
}
