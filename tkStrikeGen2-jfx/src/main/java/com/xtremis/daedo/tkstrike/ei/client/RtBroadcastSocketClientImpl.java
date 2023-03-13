package com.xtremis.daedo.tkstrike.ei.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.xtremis.daedo.tkstrike.om.ActionSource;
import com.xtremis.daedo.tkstrike.orm.model.MatchLogItemType;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchConfigurationDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.TkStrikeEventDto;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;


@Service
@Scope("singleton")
public class RtBroadcastSocketClientImpl implements RtBroadcastSocketClient {

	private static final Logger loggerEI = Logger.getLogger("EXTERNAL_INTEGRATION");

	private static final String SCORE_2SEND = "Score:";

	private static final String MATCHSTART_2SEND = "MatchStart:";

	private static final String ROUNDSTART_2SEND = "RoundStart:";

	private static final String TIMEOUT_2SEND = "Timeout:";

	private static final String RESUME_2SEND = "Resume:";

	private static final String ROUNDEND_2SEND = "RoundEnd:";

	private static final String MATCHEND_2SEND = "MatchEnd:";

	private static final String WINNER_2SEND = "Winner:";

	private static final String ROUNDTIME_2SEND = "RoundTime:";

	private static final String ADJUSTTIME_2SEND = "AdjustTime:";

	private static final String ADJUSTSCORE_2SEND = "AdjustScore:";

	private static final String PENALTY_2SEND = "Penalty:";

	private static final String ADJUSTPENALTY_2SEND = "AdjustPenalty:";

	private static final String SENSOR_2SEND = "Sensor:";

	private static final String ROUNDCOUNTDOWN_CHANGE = "ROUNDCOUNTDOWN_CHANGE";

	private static final String GOLDENPOINTCOUNTDOWN_CHANGE = "GOLDENPOINTCOUNTDOWN_CHANGE";

	private static final String TIME_ADJUST = "TIME_ADJUST";

	private static Socket socketClient = new Socket();

	private OutputStream socketOutputStream = null;

	private String serverIp;

	private Long serverPort;

	private String ringNumber;

	private SimpleBooleanProperty connected = new SimpleBooleanProperty(this, "connected", Boolean.FALSE.booleanValue());

	private ScheduledExecutorService schedulerReconnectSocket = null;

	private ScheduledFuture<?> schedulerReconnectSocketAtFixedRate = null;

	@Override
	public boolean isConnected() {
		return this.connected.get();
	}

	@Override
	public ReadOnlyBooleanProperty connectedProperty() {
		return this.connected;
	}

	@Override
	public boolean connect(String serverIp, Long serverPort, String ringNumber) throws TkStrikeServiceException {
		try {
			loggerEI.info(String.format("Try to connect RT-Broadcast on %s %s", new Object[] {serverIp, serverPort.toString()}));
			socketClient = new Socket();
			socketClient.connect(new InetSocketAddress(InetAddress.getByName(serverIp), serverPort.intValue()), 5000);
			this.socketOutputStream = socketClient.getOutputStream();
			this.connected.set(Boolean.TRUE.booleanValue());
			this.serverIp = serverIp;
			this.serverPort = serverPort;
			this.ringNumber = ringNumber;
			return true;
		} catch(IOException e) {
			loggerEI.error("Error connecting", e);
			this.connected.set(true);
			this.connected.set(false);
			throw new TkStrikeServiceException(e);
		}
	}

	@Override
	public void closeConnection() {
		sendToSocket("END_SERVER");
		_closeConnection();
	}

	private void _closeConnection() {
		if(socketClient != null)
			try {
				try {
					this.socketOutputStream.close();
				} catch(Exception exception) {}
				try {
					socketClient.close();
				} catch(Exception exception) {}
			} catch(Exception exception) {

			} finally {
				socketClient = null;
				this.connected.set(Boolean.FALSE.booleanValue());
			}
	}

	@Override
	public String getServerIp() {
		return this.serverIp;
	}

	@Override
	public Long getServerPort() {
		return this.serverPort;
	}

	@Override
	public String getRingNumber() {
		return this.ringNumber;
	}

	@Override
	public void sendNewMatchEvent(TkStrikeEventDto tkStrikeEventDto, Boolean goldenPointWorking, Integer blueAddPoints, Integer redAddPoints,
			Integer prevSensorHitValue, String matchLogEntryValue, String winnerName) {
		if(isConnected() && tkStrikeEventDto != null) {
			String eventType = tkStrikeEventDto.getEventType();
			if(MatchLogItemType.START_MATCH.toString().equals(eventType)) {
				sendToSocket(getRingNumberFormatted() + "MatchStart:" + tkStrikeEventDto.getMatchNumber());
			} else if(MatchLogItemType.START_ROUND.toString().equals(eventType)) {
				sendToSocket(getRingNumberFormatted() + "RoundStart:" + tkStrikeEventDto.getMatchNumber() + "," + _getRoundNumberAsString(
						tkStrikeEventDto.getRoundNumber(), goldenPointWorking));
				sendToSocket(getRingNumberFormatted() + "RoundTime:" + tkStrikeEventDto.getMatchNumber() + "," + _getRoundNumberAsString(
						tkStrikeEventDto.getRoundNumber(), goldenPointWorking) + "," + getRoundNumberInSeconds(tkStrikeEventDto.getRoundTimestamp()
								.longValue()));
			} else if(MatchLogItemType.TIMEOUT.toString().equals(eventType)) {
				sendToSocket(getRingNumberFormatted() + "Timeout:" + tkStrikeEventDto.getMatchNumber() + "," + _getRoundNumberAsString(
						tkStrikeEventDto.getRoundNumber(), goldenPointWorking) + "," + getRoundNumberInSeconds(tkStrikeEventDto.getRoundTimestamp()
								.longValue()));
			} else if(MatchLogItemType.RESUME.toString().equals(eventType)) {
				sendToSocket(getRingNumberFormatted() + "Resume:" + tkStrikeEventDto.getMatchNumber());
			} else if(MatchLogItemType.END_ROUND.toString().equals(eventType)) {
				sendToSocket(getRingNumberFormatted() + "RoundEnd:" + tkStrikeEventDto.getMatchNumber() + "," + _getRoundNumberAsString(
						tkStrikeEventDto.getRoundNumber(), goldenPointWorking));
			} else if(MatchLogItemType.MATCH_FINISHED.toString().equals(eventType)) {
				sendToSocket(getRingNumberFormatted() + "MatchEnd:" + tkStrikeEventDto.getMatchNumber());
				sendToSocket(getRingNumberFormatted() + "Winner:" + tkStrikeEventDto.getMatchNumber() + "," + tkStrikeEventDto
						.getMatchWinner() + "," + winnerName + "," + tkStrikeEventDto

								.getMatchFinalDecision() + "," + tkStrikeEventDto
										.getBluePoints() + "," + tkStrikeEventDto
												.getRedPoints());
			} else if("ROUNDCOUNTDOWN_CHANGE".equals(eventType) || "GOLDENPOINTCOUNTDOWN_CHANGE".equals(eventType)) {
				sendToSocket(getRingNumberFormatted() + "RoundTime:" + tkStrikeEventDto.getMatchNumber() + "," + _getRoundNumberAsString(
						tkStrikeEventDto.getRoundNumber(), goldenPointWorking) + "," + getRoundNumberInSeconds(tkStrikeEventDto.getRoundTimestamp()
								.longValue()));
			} else if("TIME_ADJUST".equals(eventType)) {
				sendToSocket(getRingNumberFormatted() + "AdjustTime:" + tkStrikeEventDto.getMatchNumber() + "," + _getRoundNumberAsString(
						tkStrikeEventDto.getRoundNumber(), goldenPointWorking) + "," + getRoundNumberInSeconds(tkStrikeEventDto.getRoundTimestamp()
								.longValue()));
			} else if(MatchLogItemType.BLUE_BODY_POINT.toString().equals(eventType) || MatchLogItemType.BLUE_HEAD_POINT
					.toString().equals(eventType) || MatchLogItemType.BLUE_PUNCH_POINT
							.toString().equals(eventType) || MatchLogItemType.BLUE_BODY_TECH_POINT
									.toString().equals(eventType) || MatchLogItemType.BLUE_HEAD_TECH_POINT
											.toString().equals(eventType) || "BLUE_PENALTY_POINT"
													.equals(eventType)) {
				if(ActionSource.SCOREBOARD_EDITOR.toString().equals(matchLogEntryValue)) {
					sendToSocket(getRingNumberFormatted() + "AdjustScore:" + tkStrikeEventDto

							.getMatchNumber() + "," +
							_getRoundNumberAsString(tkStrikeEventDto.getRoundNumber(), goldenPointWorking) + "," +
							getRoundNumberInSeconds(tkStrikeEventDto.getRoundTimestamp().longValue()) + ",Blue," + blueAddPoints + ","
							+ tkStrikeEventDto

									.getBluePoints() + "," + tkStrikeEventDto
											.getRedPoints());
				} else {
					sendToSocket(getRingNumberFormatted() + "Score:" + tkStrikeEventDto.getMatchNumber() + "," +
							_getRoundNumberAsString(tkStrikeEventDto.getRoundNumber(), goldenPointWorking) + "," +
							getRoundNumberInSeconds(tkStrikeEventDto.getRoundTimestamp().longValue()) + ",Blue," + (

							MatchLogItemType.BLUE_BODY_POINT.toString().equals(eventType) ? "Body"
									: (MatchLogItemType.BLUE_HEAD_POINT.toString().equals(eventType) ? "Head"
											: (MatchLogItemType.BLUE_PUNCH_POINT.toString().equals(eventType) ? "Punch"
													: ("BLUE_PENALTY_POINT".equals(eventType) ? "Penalty"
															: ((MatchLogItemType.BLUE_BODY_TECH_POINT
																	.toString().equals(eventType) || MatchLogItemType.BLUE_HEAD_TECH_POINT.toString()
																			.equals(eventType)) ? "Tech Point" : ""))))) + "," + tkStrikeEventDto

																					.getBluePoints() + "," + tkStrikeEventDto
																							.getRedPoints() + ((prevSensorHitValue != null) ? (","
																									+ prevSensorHitValue) : ""));
				}
			} else if(MatchLogItemType.RED_BODY_POINT.toString().equals(eventType) || MatchLogItemType.RED_HEAD_POINT
					.toString().equals(eventType) || MatchLogItemType.RED_PUNCH_POINT
							.toString().equals(eventType) || MatchLogItemType.RED_BODY_TECH_POINT
									.toString().equals(eventType) || MatchLogItemType.RED_HEAD_TECH_POINT
											.toString().equals(eventType) || "RED_PENALTY_POINT"
													.equals(eventType)) {
				if(ActionSource.SCOREBOARD_EDITOR.toString().equals(matchLogEntryValue)) {
					sendToSocket(getRingNumberFormatted() + "AdjustScore:" + tkStrikeEventDto

							.getMatchNumber() + "," +
							_getRoundNumberAsString(tkStrikeEventDto.getRoundNumber(), goldenPointWorking) + "," +
							getRoundNumberInSeconds(tkStrikeEventDto.getRoundTimestamp().longValue()) + ",Red," + redAddPoints + ","
							+ tkStrikeEventDto

									.getBluePoints() + "," + tkStrikeEventDto
											.getRedPoints());
				} else {
					sendToSocket(getRingNumberFormatted() + "Score:" + tkStrikeEventDto.getMatchNumber() + "," +
							_getRoundNumberAsString(tkStrikeEventDto.getRoundNumber(), goldenPointWorking) + "," +
							getRoundNumberInSeconds(tkStrikeEventDto.getRoundTimestamp().longValue()) + ",Red," + (

							MatchLogItemType.RED_BODY_POINT.toString().equals(eventType) ? "Body"
									: (MatchLogItemType.RED_HEAD_POINT.toString().equals(eventType) ? "Head"
											: (MatchLogItemType.RED_PUNCH_POINT.toString().equals(eventType) ? "Punch"
													: ("RED_PENALTY_POINT".equals(eventType) ? "Penalty"
															: ((MatchLogItemType.RED_BODY_TECH_POINT
																	.toString().equals(eventType) || MatchLogItemType.RED_HEAD_TECH_POINT.toString()
																			.equals(eventType)) ? "Tech Point" : ""))))) + "," + tkStrikeEventDto

																					.getBluePoints() + "," + tkStrikeEventDto
																							.getRedPoints() + ((prevSensorHitValue != null) ? (","
																									+ prevSensorHitValue) : ""));
				}
			} else if(MatchLogItemType.BLUE_ADD_KYONG_GO.toString().equals(eventType) || MatchLogItemType.BLUE_ADD_GAME_JEON
					.toString().equals(eventType) || MatchLogItemType.BLUE_REMOVE_KYONG_GO
							.toString().equals(eventType) || MatchLogItemType.BLUE_REMOVE_GAME_JEON
									.toString().equals(eventType)) {
				String base = "Penalty:";
				if(ActionSource.SCOREBOARD_EDITOR.toString().equals(matchLogEntryValue))
					base = "AdjustPenalty:";
				sendToSocket(getRingNumberFormatted() + base + tkStrikeEventDto.getMatchNumber() + "," +

						_getRoundNumberAsString(tkStrikeEventDto.getRoundNumber(), goldenPointWorking) + "," +
						getRoundNumberInSeconds(tkStrikeEventDto.getRoundTimestamp().longValue()) + ",Blue," + ((MatchLogItemType.BLUE_ADD_KYONG_GO
								.toString().equals(eventType) || MatchLogItemType.BLUE_REMOVE_KYONG_GO.toString().equals(eventType)) ? "KG" : "GJ")
						+ (ActionSource.SCOREBOARD_EDITOR.toString().equals(matchLogEntryValue) ? ((MatchLogItemType.BLUE_ADD_KYONG_GO
								.toString().equals(eventType) || MatchLogItemType.BLUE_ADD_GAME_JEON.toString().equals(eventType)) ? ",Add"
										: ",Remove") : "") + "," + tkStrikeEventDto
												.getBluePoints() + "," + tkStrikeEventDto
														.getRedPoints() + "," + tkStrikeEventDto
																.getBluePenalties() + "," + tkStrikeEventDto
																		.getRedPenalties());
			} else if(MatchLogItemType.RED_ADD_KYONG_GO.toString().equals(eventType) || MatchLogItemType.RED_ADD_GAME_JEON
					.toString().equals(eventType) || MatchLogItemType.RED_REMOVE_KYONG_GO
							.toString().equals(eventType) || MatchLogItemType.RED_REMOVE_GAME_JEON
									.toString().equals(eventType)) {
				String base = "Penalty:";
				if(ActionSource.SCOREBOARD_EDITOR.toString().equals(matchLogEntryValue))
					base = "AdjustPenalty:";
				sendToSocket(getRingNumberFormatted() + base + tkStrikeEventDto.getMatchNumber() + "," +

						_getRoundNumberAsString(tkStrikeEventDto.getRoundNumber(), goldenPointWorking) + "," +
						getRoundNumberInSeconds(tkStrikeEventDto.getRoundTimestamp().longValue()) + ",Red," + ((MatchLogItemType.RED_ADD_KYONG_GO
								.toString().equals(eventType) || MatchLogItemType.RED_REMOVE_KYONG_GO.toString().equals(eventType)) ? "KG" : "GJ")
						+ (ActionSource.SCOREBOARD_EDITOR.toString().equals(matchLogEntryValue) ? ((MatchLogItemType.RED_ADD_KYONG_GO
								.toString().equals(eventType) || MatchLogItemType.RED_ADD_GAME_JEON.toString().equals(eventType)) ? ",Add"
										: ",Remove") : "") + "," + tkStrikeEventDto
												.getBluePoints() + "," + tkStrikeEventDto
														.getRedPoints() + "," + tkStrikeEventDto
																.getBluePenalties() + "," + tkStrikeEventDto
																		.getRedPenalties());
			} else if(MatchLogItemType.BLUE_BODY_HIT.toString().equals(eventType) || MatchLogItemType.BLUE_HEAD_HIT
					.toString().equals(eventType) || MatchLogItemType.RED_BODY_HIT
							.toString().equals(eventType) || MatchLogItemType.RED_HEAD_HIT
									.toString().equals(eventType)) {
				sendToSocket(getRingNumberFormatted() + "Sensor:" + tkStrikeEventDto.getMatchNumber() + "," +

						_getRoundNumberAsString(tkStrikeEventDto.getRoundNumber(), goldenPointWorking) + "," +
						getRoundNumberInSeconds(tkStrikeEventDto.getRoundTimestamp().longValue()) + "," + ((MatchLogItemType.BLUE_BODY_HIT
								.toString().equals(eventType) || MatchLogItemType.BLUE_HEAD_HIT.toString().equals(eventType)) ? "Blue" : "Red") + ","
						+ ((MatchLogItemType.BLUE_BODY_HIT
								.toString().equals(eventType) || MatchLogItemType.RED_BODY_HIT.toString().equals(eventType)) ? "Body" : "Head") + ","
						+ matchLogEntryValue);
			}
		}
	}

	private String _getRoundNumberAsString(Integer roundNumber, Boolean goldenPointWorking) {
		return goldenPointWorking.booleanValue() ? "OT" : ("R" + roundNumber);
	}

	@Override
	public void sendHasNewMatchConfigured(MatchConfigurationDto matchConfigurationDto) {
		if(isConnected() && matchConfigurationDto != null) {
			String toSend = getRingNumberFormatted() + "NewMatch:" + matchConfigurationDto.getMatchNumber() + "," + matchConfigurationDto
					.getBlueAthlete().getScoreboardName() + "," + matchConfigurationDto.getBlueAthlete().getFlagAbbreviation() + ","
					+ matchConfigurationDto.getRedAthlete().getScoreboardName() + "," + matchConfigurationDto.getRedAthlete().getFlagAbbreviation()
					+ "," + matchConfigurationDto.getCategory().getGender() + "," + matchConfigurationDto.getCategory().getName();
			sendToSocket(toSend);
		}
	}

	private void tryToReconnect() {
		if(this.schedulerReconnectSocket != null) {
			this.schedulerReconnectSocket.shutdownNow();
			this.schedulerReconnectSocket = null;
		}
		this.schedulerReconnectSocket = Executors.newSingleThreadScheduledExecutor();
		this.schedulerReconnectSocketAtFixedRate = this.schedulerReconnectSocket.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					RtBroadcastSocketClientImpl.loggerEI.debug("Try to reconnect RT Broadcast");
					RtBroadcastSocketClientImpl.socketClient = new Socket();
					RtBroadcastSocketClientImpl.socketClient.connect(new InetSocketAddress(InetAddress.getByName(
							RtBroadcastSocketClientImpl.this.serverIp), RtBroadcastSocketClientImpl.this.serverPort.intValue()), 500);
					RtBroadcastSocketClientImpl.this.socketOutputStream = RtBroadcastSocketClientImpl.socketClient.getOutputStream();
					RtBroadcastSocketClientImpl.this.connected.set(Boolean.TRUE.booleanValue());
					RtBroadcastSocketClientImpl.loggerEI.debug("Shutdown scheduler....");
					RtBroadcastSocketClientImpl.this.schedulerReconnectSocket.shutdownNow();
				} catch(IOException e) {
					RtBroadcastSocketClientImpl.loggerEI.debug("IOExcetion when validating InputStream", e);
				}
			}
		}, 0L, 1000L, TimeUnit.MILLISECONDS);
	}

	private void sendToSocket(String baseMessage) {
		if(isConnected() && baseMessage != null && socketClient != null)
			try {
				loggerEI.debug("Send to RTBroadcast ->" + baseMessage);
				socketClient.getOutputStream().write(createRtBroadcastMessageBytes(baseMessage));
				socketClient.getOutputStream().flush();
			} catch(Exception e) {
				loggerEI.warn("Exception sending to Broadcast. Try to re-connect.", e);
				_closeConnection();
				tryToReconnect();
			}
	}

	private String createRtBroadcastMessage(String baseMessage) {
		loggerEI.debug("ToSendBroadcast->" + baseMessage);
		return (baseMessage.length() + 1) + baseMessage;
	}

	private byte[] createRtBroadcastMessageBytes(String message) throws DecoderException {
		byte[] res = Hex.decodeHex("023030".toCharArray());
		res = ArrayUtils.addAll(res, createRtBroadcastMessage(message).getBytes(Charset.forName("US-ASCII")));
		res = ArrayUtils.addAll(res, Hex.decodeHex("03".toCharArray()));
		return res;
	}

	private String getRingNumberFormatted() {
		return df.format(Integer.parseInt(this.ringNumber));
	}

	private static final DecimalFormat df = new DecimalFormat("00");

	private int getRoundNumberInSeconds(long timestamp) {
		int res = 0;
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);
		int minutes = calendar.get(12);
		int seconds = calendar.get(13);
		res = seconds + minutes * 60;
		return res;
	}
}
