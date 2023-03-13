package com.xtremis.daedo.tkstrike.ei.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.xtremis.daedo.tkstrike.om.combat.MatchWinner;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchConfigurationDto;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchResultDto;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.wtdata.model.constants.Gender;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Duration;


@Service
@Scope("singleton")
public class WtUDPServiceImpl extends Thread implements WtUDPService, InitializingBean {

	private static final String BOF3_SCORE_CHANGE_PATTERN = "s11;%s;s21;%s;s12;%s;s22;%s;s13;%s;s23;%s";

	private static final Logger logger = Logger.getLogger("EXTERNAL_INTEGRATION");

	private static final String UDP_PACKET_SEPARATOR = "\r\n";

	private static final String UDP_REQUEST_CLOCK = "clock";

	private static final String UDP_REQUEST_REST = "break";

	private static final String UDP_REQUEST_ROUND = "period";

	private static final String UDP_REQUEST_SCORES = "scores";

	private static final String UDP_REQUEST_PENALTIES = "gam-jeom";

	private static final String UDP_REQUEST_MATCH = "informations";

	private static final String UDP_REQUEST_ATHLETES = "athletes";

	private static final String UDP_REQUEST_WINNER = "winmatch";

	private static final String UDP_REQUEST_REFEREES = "referees";

	private static final String UDP_REQUEST_PARA_TIMEOUT = "to%s;";

	private static final String UDP_REQUEST_WINNER_PERIODS = "winperiods;";

	private static final String UDP_MESSAGE_HELLO = "hel;";

	private static final String UDP_MESSAGE_BYE = "bye;";

	private boolean working = true;

	private SimpleBooleanProperty connected = new SimpleBooleanProperty(this, "connected", Boolean.FALSE.booleanValue());

	private Integer udpSocketListenPort;

	private Integer udpSocketWritePort;

	private InetAddress udpSocketBroadcastIp;

	private DatagramSocket broadcastListenSocket;

	private DatagramSocket broadcastWriteSocket;

	private byte[] readBuf = new byte[256];

	private static String lastRoundCountdownPacket = "clk;";

	private static String lastRestCountdownPacket = "brk;";

	private static String lastParaRestCountdownPacket = "to;";

	private static String lastKyeShiCountdownPacket;

	private static String lastRoundPacket = "rnd;";

	private static String lastMatchLoadedPacket = "mch;";

	private static String lastAthletesPacket = "at1;at2;";

	private static String lastMatchResultPacket = "wmh;";

	private static String lastScorePacket = "sc1;sc2;";

	private static String lastPenaltiesPacket = "wg1;wg2;";

	private static String lastHitLevelPacket = "hl1;hl2;";

	private static String lastRefereesPacket = "ref;";

	private static String lastWinnerPeriodsPacket = "wrd;";

	@Value("${tkStrike.arduino.ledBrightness}")
	private Integer ledBrightness;

	@Value("${tkStrike.arduino.showLast10Seconds}")
	private Boolean showLast10Seconds;

	@Value("${tkStrike.arduino.timeShowingHit}")
	private Long timeShowingHit;

	@Override
	public boolean isConnected() {
		return this.connected.get();
	}

	@Override
	public ReadOnlyBooleanProperty connectedProperty() {
		return this.connected;
	}

	@Override
	public boolean connect(String broadcastIp, Integer listenPort, Integer writePort) throws TkStrikeServiceException {
		if(StringUtils.isNotBlank(broadcastIp) && listenPort != null) {
			logger.info("WtUDPService :: connect to ip " + broadcastIp + " listenPort " + listenPort + " writePort " + writePort);
			try {
				this.udpSocketListenPort = listenPort;
				this.udpSocketWritePort = writePort;
				try {
					this.udpSocketBroadcastIp = InetAddress.getByName(broadcastIp);
				} catch(UnknownHostException uhe) {
					logger.error("Can't instantiate WT Broadcast to ip " + broadcastIp);
					throw new TkStrikeServiceException("Can't instantiate WT Broadcast to ip " + broadcastIp, uhe);
				}
				this.broadcastWriteSocket = new DatagramSocket();
				this.broadcastWriteSocket.setBroadcast(true);
				send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, "hel;");
				send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, "inlb;" + this.ledBrightness + ":");
				send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, "insl;" + this.showLast10Seconds + ":");
				send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, "inth;" + this.timeShowingHit + ":");
				this.broadcastListenSocket = new DatagramSocket(listenPort.intValue());
				this.broadcastListenSocket.setBroadcast(true);
				this.connected.set(true);
				return true;
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public void closeConnection(boolean forceFinish) {
		if(forceFinish)
			this.working = false;
		this.connected.set(false);
		logger.info("WtUDPService :: closeConnection");
		if(this.udpSocketBroadcastIp != null && this.udpSocketWritePort != null)
			send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, "bye;");
		try {
			if(this.broadcastWriteSocket != null)
				this.broadcastWriteSocket.close();
		} catch(Exception exception) {}
		try {
			if(this.broadcastListenSocket != null)
				this.broadcastListenSocket.close();
		} catch(Exception exception) {}
	}

	private void send2Socket(InetAddress address, Integer port, String baseMessage) {
		if(this.broadcastWriteSocket != null && address != null && port != null &&

				StringUtils.isNotBlank(baseMessage))
			try {
				logger.info("**** SEND TO SOCKET " + baseMessage);
				this.broadcastWriteSocket.send(new DatagramPacket((baseMessage + "\r\n").getBytes("UTF-8"), (baseMessage + "\r\n").length(), address,
						port.intValue()));
			} catch(IOException e) {
				if(logger.isDebugEnabled())
					logger.error("Send 2 Socket Exception", e);
			}
	}

	@Override
	public void sendMatchLoaded(MatchConfigurationDto matchConfiguration) {
		if(matchConfiguration != null) {
			String strPacket = "mch;";
			strPacket = strPacket + matchConfiguration.getMatchNumber();
			strPacket = strPacket + ";" + matchConfiguration.getPhase();
			strPacket = strPacket + ";" + getGender4WTUdp(matchConfiguration.getCategory().getGender()) + " " + matchConfiguration.getCategory()
					.getName();
			strPacket = strPacket + ";";
			strPacket = strPacket + ";#0000ff";
			strPacket = strPacket + ";#FFFFFF";
			strPacket = strPacket + ";#ff0000";
			strPacket = strPacket + ";#FFFFFF";
			strPacket = strPacket + ";" + matchConfiguration.getInternalId();
			strPacket = strPacket + ";" + matchConfiguration.getCategory().getSubCategory();
			strPacket = strPacket + ";" + matchConfiguration.getRoundsConfig().getRounds();
			strPacket = strPacket + ";" + Double.valueOf(Duration.minutes(matchConfiguration.getRoundsConfig().getRoundTimeMinutes().intValue()).add(
					Duration.seconds(matchConfiguration.getRoundsConfig().getRoundTimeSeconds().intValue())).toSeconds()).intValue();
			strPacket = strPacket + ";cntDown";
			strPacket = strPacket + ";" + matchConfiguration.getCategory().getBodyLevel();
			strPacket = strPacket + ";" + matchConfiguration.getCategory().getHeadLevel();
			lastMatchLoadedPacket = strPacket;
			send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, strPacket);
		}
	}

	private String getGender4WTUdp(String gender) {
		return Gender.FEMALE.toString().equals(gender) ? "WOMEN"
				: (Gender.MALE.toString().equals(gender) ? "MEN" : ((gender != null) ? gender : " "));
	}

	@Override
	public void sendAthletes(MatchConfigurationDto matchConfiguration) {
		if(matchConfiguration != null) {
			String strPacket = "at1;";
			strPacket = strPacket + matchConfiguration.getBlueAthlete().getScoreboardName();
			strPacket = strPacket + ";";
			strPacket = strPacket + matchConfiguration.getBlueAthlete().getScoreboardName();
			strPacket = strPacket + ";";
			strPacket = strPacket + matchConfiguration.getBlueAthlete().getFlagAbbreviation();
			strPacket = strPacket + ";at2;";
			strPacket = strPacket + matchConfiguration.getRedAthlete().getScoreboardName();
			strPacket = strPacket + ";";
			strPacket = strPacket + matchConfiguration.getRedAthlete().getScoreboardName();
			strPacket = strPacket + ";";
			strPacket = strPacket + matchConfiguration.getRedAthlete().getFlagAbbreviation();
			lastAthletesPacket = strPacket;
			send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, strPacket);
		}
	}

	@Override
	public void sendReferees(MatchConfigurationDto matchConfiguration) {
		if(matchConfiguration != null) {
			String strPacket = "ref;";
			if(matchConfiguration.getRefereeCR() != null && matchConfiguration.getRefereeCR().getScoreboardName() != null)
				strPacket = strPacket + matchConfiguration.getRefereeCR().getScoreboardName() + ";";
			if(matchConfiguration.getRefereeCR() != null && matchConfiguration.getRefereeCR().getCountry() != null)
				strPacket = strPacket + matchConfiguration.getRefereeCR().getCountry() + ";";
			strPacket = strPacket + "ju1;";
			if(matchConfiguration.getRefereeJ1() != null && matchConfiguration.getRefereeJ1().getScoreboardName() != null)
				strPacket = strPacket + matchConfiguration.getRefereeJ1().getScoreboardName() + ";";
			if(matchConfiguration.getRefereeJ1() != null && matchConfiguration.getRefereeJ1().getCountry() != null)
				strPacket = strPacket + matchConfiguration.getRefereeJ1().getCountry() + ";";
			strPacket = strPacket + "ju2;";
			if(matchConfiguration.getRefereeJ2() != null && matchConfiguration.getRefereeJ2().getScoreboardName() != null)
				strPacket = strPacket + matchConfiguration.getRefereeJ2().getScoreboardName() + ";";
			if(matchConfiguration.getRefereeJ2() != null && matchConfiguration.getRefereeJ2().getCountry() != null)
				strPacket = strPacket + matchConfiguration.getRefereeJ2().getCountry() + ";";
			strPacket = strPacket + "ju3;";
			if(matchConfiguration.getRefereeJ3() != null && matchConfiguration.getRefereeJ3().getScoreboardName() != null)
				strPacket = strPacket + matchConfiguration.getRefereeJ3().getScoreboardName() + ";";
			if(matchConfiguration.getRefereeJ3() != null && matchConfiguration.getRefereeJ3().getCountry() != null)
				strPacket = strPacket + matchConfiguration.getRefereeJ3().getCountry() + ";";
			lastRefereesPacket = strPacket;
			send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, lastRefereesPacket);
		}
	}

	@Override
	public void sendMatchPreLoaded() {
		send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, "pre;FightLoaded");
	}

	@Override
	public void sendMatchReady() {
		send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, "rdy;FightReady");
	}

	@Override
	public void sendRoundCountdownChange(String roundCountdownTime, WtUDPService.ClockAction clockAction) {
		String strPacket = "clk;" + roundCountdownTime;
		if(clockAction != null)
			switch(clockAction) {
				case START:
					strPacket = strPacket + ";start";
					break;
				case STOP:
					strPacket = strPacket + ";stop";
					break;
				case CORRECTION:
					strPacket = strPacket + ";corr";
					break;
				case END:
					strPacket = strPacket + ";stopEnd";
					break;
			}
		lastRoundCountdownPacket = strPacket;
		send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, strPacket);
	}

	@Override
	public void sendRestCountdownChange(String restCountdownTime, WtUDPService.ClockAction clockAction) {
		String strPacket = "brk;" + restCountdownTime;
		if(clockAction != null)
			switch(clockAction) {
				case START:
					strPacket = strPacket + ";start";
					break;
				case STOP:
					strPacket = strPacket + ";stop";
					break;
				case CORRECTION:
					strPacket = strPacket + ";corr";
					break;
				case END:
					strPacket = strPacket + ";stopEnd";
					break;
			}
		lastRestCountdownPacket = strPacket;
		send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, strPacket);
	}

	@Override
	public void sendKyeShi(String kyeShiCountdownTime, WtUDPService.ClockAction clockAction) {
		String strPacket = "ij0;" + kyeShiCountdownTime;
		if(clockAction != null)
			switch(clockAction) {
				case START:
					strPacket = strPacket + ";show";
					break;
				case STOP:
					strPacket = strPacket + ";hide";
					break;
				case CORRECTION:
					strPacket = strPacket + ";hide";
					break;
			}
		lastKyeShiCountdownPacket = strPacket;
		send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, strPacket);
	}

	@Override
	public void sendPARATimeOutCountdownChange(boolean blue, String paraTimeOutCountdownTime, WtUDPService.ClockAction clockAction) {
		String strPacket = String.format("to%s;", new Object[] {blue ? "1" : "2"}) + paraTimeOutCountdownTime;
		if(clockAction != null)
			switch(clockAction) {
				case START:
					strPacket = strPacket + ";start";
					break;
				case STOP:
					strPacket = strPacket + ";stop";
					break;
				case CORRECTION:
					strPacket = strPacket + ";corr";
					break;
				case END:
					strPacket = strPacket + ";stopEnd";
					break;
			}
		lastParaRestCountdownPacket = strPacket;
		send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, strPacket);
	}

	@Override
	public void sendRoundNumber(Integer roundNumber) {
		lastRoundPacket = "rnd;" + roundNumber;
		send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, lastRoundPacket);
	}

	@Override
	public void sendPointsChange(Integer bluePoints, Integer redPoints) {
		if(bluePoints != null && bluePoints.intValue() > 0)
			send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, "pt1;" + bluePoints);
		if(redPoints != null && redPoints.intValue() > 0)
			send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, "pt2;" + redPoints);
	}

	@Override
	public void sendBestOf3ScoreChange(BestOf3PointsChange bestOf3PointsChange) {
		String scoreChange = String.format("s11;%s;s21;%s;s12;%s;s22;%s;s13;%s;s23;%s", new Object[] {bestOf3PointsChange
				.getBlueR1(), bestOf3PointsChange.getRedR1(), bestOf3PointsChange
						.getBlueR2(), bestOf3PointsChange.getRedR2(), bestOf3PointsChange
								.getBlueR3(), bestOf3PointsChange.getRedR3()});
		send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, scoreChange);
	}

	@Override
	public void sendScoreChange(Integer blueScore, Integer redScore) {
		lastScorePacket = "sc1;" + blueScore + ";sc2;" + redScore;
		send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, lastScorePacket);
	}

	@Override
	public void sendPenaltiesChange(Integer bluePenalties, Integer redPenalties) {
		lastPenaltiesPacket = "wg1;" + bluePenalties + ";wg2;" + redPenalties;
		send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, lastPenaltiesPacket);
	}

	@Override
	public void sendHitLevel(Integer blueHitLevel, Integer redHitLevel) {
		lastHitLevelPacket = ((blueHitLevel != null) ? ("hl1;" + blueHitLevel) : "") + ((redHitLevel != null) ? ("hl2;" + redHitLevel) : "");
		send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, lastHitLevelPacket);
	}

	@Override
	public void sendWinnerPeriods(Map<Integer, MatchWinner> roundsWinner) {
		lastWinnerPeriodsPacket = "wrd;";
		StringBuilder sb = new StringBuilder("wrd;");
		for(Map.Entry<Integer, MatchWinner> entry : roundsWinner.entrySet())
			sb.append("rd")
					.append(entry.getKey())
					.append(";")
					.append(getWinnerPeriod(entry.getValue()))
					.append(";");
		lastWinnerPeriodsPacket = StringUtils.substringBeforeLast(sb.toString(), ";");
		send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, lastWinnerPeriodsPacket);
	}

	private String getWinnerPeriod(MatchWinner winner) {
		return MatchWinner.BLUE.equals(winner) ? "1" : (MatchWinner.RED.equals(winner) ? "2" : "0");
	}

	@Override
	public void sendMatchResult(MatchResultDto matchResult) {
		if(matchResult != null) {
			String basePacket = "wmh;";
			if(matchResult.getMatchWinner() != null)
				basePacket = basePacket + matchResult.getMatchWinner().getScoreboardName();
			basePacket = basePacket + ";" + matchResult.getBluePoints() + "-" + matchResult.getRedPoints();
			basePacket = basePacket + " " + matchResult.getMatchFinalDecision();
			lastMatchResultPacket = basePacket;
			send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, basePacket);
		}
	}

	@Override
	public void sendMatchWinner(MatchWinner matchWinner) {
		if(matchWinner != null) {
			String basePacket = "win;" + matchWinner.toString();
			send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, basePacket);
		}
	}

	@Override
	public void sendVideoReplay(boolean forBlue, WtUDPService.VideoRequestResult result) {
		String basePacket = forBlue ? "ch1;" : "ch2;";
		if(WtUDPService.VideoRequestResult.ACCEPTED.equals(result)) {
			basePacket = basePacket + "1";
		} else if(WtUDPService.VideoRequestResult.REJECTED.equals(result)) {
			basePacket = basePacket + "0";
		} else if(WtUDPService.VideoRequestResult.CLOSED.equals(result)) {
			basePacket = basePacket + "-1";
		}
		send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, basePacket);
	}

	@Override
	public void sendHardwareTestOpened() {
		send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, "hwt;1");
	}

	@Override
	public void sendHardwareTestClosed() {
		send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, "hwt;0");
	}

	@Override
	public void sendHardwareTestHit(WtUDPService.HardwareTestHit hardwareTestHit) {
		String basePacket = "hwt;";
		switch(hardwareTestHit) {
			case BLUE_BODY:
				basePacket = basePacket + "2";
				break;
			case BLUE_HEAD:
				basePacket = basePacket + "3";
				break;
			case RED_BODY:
				basePacket = basePacket + "4";
				break;
			case RED_HEAD:
				basePacket = basePacket + "5";
				break;
		}
		send2Socket(this.udpSocketBroadcastIp, this.udpSocketWritePort, basePacket);
	}

	@Override
	public void run() {
		while(this.working) {
			if(this.broadcastListenSocket != null) {
				try {
					DatagramPacket packet = new DatagramPacket(this.readBuf, this.readBuf.length);
					this.broadcastListenSocket.receive(packet);
					InetAddress address = packet.getAddress();
					if( ! InetAddress.getLocalHost().equals(address)) {
						int port = packet.getPort();
						logger.info("Received UDP packet from " + address.toString() + " over port " + port);
						String received = new String(packet.getData(), 0, packet.getLength());
						logger.info("***** PACKET ->" + received);
						TkStrikeExecutors.executeInThreadPool(new UdpPacketReceivedProcess(address, Integer.valueOf(port), received));
					}
				} catch(IOException e) {
					logger.warn("IOException when read from socket");
				}
				continue;
			}
			try {
				TimeUnit.SECONDS.sleep(1L);
			} catch(InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		logger.info("WtUDPService goes down.");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info("WtUDPService properties set. Go to start Thread");
		start();
	}

	class UdpPacketReceivedProcess implements Callable<Void> {

		private final InetAddress callerIpAddress;

		private final Integer callerPort;

		private final String packetReceived;

		public UdpPacketReceivedProcess(InetAddress callerIpAddress, Integer callerPort, String packetReceived) {
			this.callerIpAddress = callerIpAddress;
			this.callerPort = callerPort;
			this.packetReceived = packetReceived;
		}

		@Override
		public Void call() throws Exception {
			if(StringUtils.isNotBlank(this.packetReceived))
				if(StringUtils.contains(this.packetReceived, "\r\n")) {
					String[] requests = StringUtils.split(this.packetReceived, "\r\n");
					for(String request : requests)
						doProcessRequest(request);
				} else {
					doProcessRequest(this.packetReceived);
				}
			return null;
		}

		private void doProcessRequest(String request) {
			WtUDPServiceImpl.logger.debug("Process request ->" + request);
			if("clock".equals(request)) {
				WtUDPServiceImpl.logger.info("Requested for CLOCK");
				WtUDPServiceImpl.this.send2Socket(this.callerIpAddress, this.callerPort, WtUDPServiceImpl.lastRoundCountdownPacket);
			} else if("break".equals(request)) {
				WtUDPServiceImpl.logger.info("Requested for REST");
				WtUDPServiceImpl.this.send2Socket(this.callerIpAddress, this.callerPort, WtUDPServiceImpl.lastRestCountdownPacket);
			} else if("period".equals(request)) {
				WtUDPServiceImpl.logger.info("Requested for ROUND");
				WtUDPServiceImpl.this.send2Socket(this.callerIpAddress, this.callerPort, WtUDPServiceImpl.lastRoundPacket);
			} else if("scores".equals(request)) {
				WtUDPServiceImpl.logger.info("Requested for SCORES");
				WtUDPServiceImpl.this.send2Socket(this.callerIpAddress, this.callerPort, WtUDPServiceImpl.lastScorePacket);
			} else if("gam-jeom".equals(request)) {
				WtUDPServiceImpl.logger.info("Requested for PENALTIES");
				WtUDPServiceImpl.this.send2Socket(this.callerIpAddress, this.callerPort, WtUDPServiceImpl.lastPenaltiesPacket);
			} else if("informations".equals(request)) {
				WtUDPServiceImpl.logger.info("Requested for MATCH");
				WtUDPServiceImpl.this.send2Socket(this.callerIpAddress, this.callerPort, WtUDPServiceImpl.lastMatchLoadedPacket);
			} else if("athletes".equals(request)) {
				WtUDPServiceImpl.logger.info("Requested for ATHLETES");
				WtUDPServiceImpl.this.send2Socket(this.callerIpAddress, this.callerPort, WtUDPServiceImpl.lastAthletesPacket);
			} else if("winmatch".equals(request)) {
				WtUDPServiceImpl.logger.info("Requested for WINNER");
				WtUDPServiceImpl.this.send2Socket(this.callerIpAddress, this.callerPort, WtUDPServiceImpl.lastMatchResultPacket);
			} else if("referees".equals(request)) {
				WtUDPServiceImpl.logger.info("Requested for REFEREES");
				WtUDPServiceImpl.this.send2Socket(this.callerIpAddress, this.callerPort, WtUDPServiceImpl.lastRefereesPacket);
			} else if("winperiods;".equals(request)) {
				WtUDPServiceImpl.logger.info("Requested for WINNER_PERIODS");
				WtUDPServiceImpl.this.send2Socket(this.callerIpAddress, this.callerPort, WtUDPServiceImpl.lastWinnerPeriodsPacket);
			}
		}
	}
}
