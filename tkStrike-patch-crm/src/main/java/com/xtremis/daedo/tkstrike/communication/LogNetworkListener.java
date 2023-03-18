package com.xtremis.daedo.tkstrike.communication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

import com.xtremis.daedo.tkstrike.utils.TkStrikeBaseDirectoriesUtil;

/**
 * @author f.agu
 */
public class LogNetworkListener implements GlobalNetworkStatusControllerListener, TkStrikeCommunicationListener {

	private final PrintStream printStream;

	public LogNetworkListener() {
		try {
			printStream = new PrintStream(
					new File(TkStrikeBaseDirectoriesUtil.getInstance().getLogBaseDir(), "crm.log"));
		} catch (FileNotFoundException e) {
			throw new UncheckedIOException(e);
		}
	}

	// ==== GlobalNetworkStatusControllerListener ====

	@Override
	public void hasNewGlobalStatusEvent(StatusEvent e) {
		StringJoiner joiner = new StringJoiner(", ") //
				.add("hasNewGlobalStatusEvent with nodeId " + e.getNodeId()) //
				.add("battery:" + e.getNodeBatteryPct() + "%") //
				.add("networkStatus: " + e.getNetworkStatus());
		log(joiner.toString());
	}

	@Override
	public void hasNewNodeNetworkErrorEvent(NodeNetworkErrorEvent e) {
		StringJoiner joiner = new StringJoiner(", ") //
				.add("hasNewNodeNetworkErrorEvent with nodeId " + e.getNetworkNode().getNodeId()) //
				.add("cause:" + e.getNetworkErrorCause());
		log(joiner.toString());
	}

	@Override
	public void hasNetworkOkEvent(NetworkOkEvent e) {
		log("hasNetworkOkEvent");
	}

	// ==== TkStrikeCommunicationListener ====

	@Override
	public void hasNewDataEvent(DataEvent e) {
		log("hasNewDataEvent(" + e.getNodeId() + " => " + e.getDataEventHitType() + ")");
	}

	@Override
	public void hasNewStatusEvent(StatusEvent e) {
		log("hasNewStatusEvent(" + e.getNodeId() + " => " + e.getNetworkStatus() + ")");
	}

	@Override
	public void hasChangeNetworkStatusEvent(ChangeNetworkStatusEvent e) {
		log("hasChangeNetworkStatusEvent(" + e.getNetworkStatus() + ")");
	}

	@Override
	public void hasChangeNetworkConfigurationEvent(ChangeNetworkConfigurationEvent e) {
		log("hasChangeNetworkConfigurationEvent(" + e.getNetworkStatus() + ")");
	}

	// ******************************************************

	private void log(String message) {
		printStream.println(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()) + " " + message);
		printStream.flush();
	}

}
