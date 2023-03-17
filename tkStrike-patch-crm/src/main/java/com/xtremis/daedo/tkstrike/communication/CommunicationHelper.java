package com.xtremis.daedo.tkstrike.communication;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

/**
 * Copy of
 * com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationServiceImpl
 * 
 * @author f.agu
 * @created 15 mars 2023 18:05:57
 */
public class CommunicationHelper {

	private static final Logger LOGGER = Logger.getLogger(CommunicationHelper.class);

	public void test() {
		test("^COM", 15);
	}

	public void test(String serialNamePattern, int initBodyGap) {
		LOGGER.info("Starting test communication...");
		Pattern pattern = Pattern.compile(serialNamePattern);
		try {
			String[] portNames = SerialPortList.getPortNames();
			LOGGER.info("Find " + portNames.length + " port(s): "
					+ Arrays.stream(portNames).collect(Collectors.joining(", ")));
			for (int i = 0; i < portNames.length; ++i) {
				String tempPortName = portNames[i];
				if (pattern.matcher(tempPortName).lookingAt()) {
					LOGGER.info("Port name '" + tempPortName + "' matches");
					final SerialPort toValidateSerialPort = new SerialPort(tempPortName);
					try {
						toValidateSerialPort.openPort();
						// baudRate: 38400
						// dataBits: 8
						// stopBits: 1
						// parity: 0
						toValidateSerialPort.setParams(38400, 8, 1, 0);
						toValidateSerialPort.writeString("v?", StandardCharsets.US_ASCII.name());
						toValidateSerialPort.writeByte((byte) 13);
						TimeUnit.SECONDS.sleep(1L);
						int r = toValidateSerialPort.getInputBufferBytesCount();
						String readed = toValidateSerialPort.readString(r);
						LOGGER.info("From port " + tempPortName + ", read: " + readed);
						if (readed.trim().contains("TS_GEN2")) {
							LOGGER.info("Port " + tempPortName + " is TkStrike GEN2 OK");
							LOGGER.info("Port " + tempPortName + " write bodygap");
							toValidateSerialPort.writeString("bodygap=" + initBodyGap,
									StandardCharsets.US_ASCII.name());
							LOGGER.info("Port " + tempPortName + " write 13 (1)");
							toValidateSerialPort.writeByte((byte) 13);
							LOGGER.info("Port " + tempPortName + " write headgap");
							toValidateSerialPort.writeString("headgap=" + initBodyGap,
									StandardCharsets.US_ASCII.name());
							LOGGER.info("Port " + tempPortName + " write 13 (2)");
							toValidateSerialPort.writeByte((byte) 13);
							LOGGER.info("Port " + tempPortName + " write gap");
							toValidateSerialPort.writeString("gap?", StandardCharsets.US_ASCII.name());
							LOGGER.info("Port " + tempPortName + " write 13 (3)");
							toValidateSerialPort.writeByte((byte) 13);
							TimeUnit.SECONDS.sleep(1L);
							LOGGER.info("Port " + tempPortName + " getInputBufferBytesCount");
							r = toValidateSerialPort.getInputBufferBytesCount();
							LOGGER.info("Port " + tempPortName + " readString(" + r + ")");
							readed = toValidateSerialPort.readString(r);
							LOGGER.info("CurrentGap? (" + readed + ")");
							readed = null;
						}
					} catch (Exception e) {
						LOGGER.error("startComm", e);
					} finally {
						try {
							LOGGER.info("Closing port " + tempPortName + "...");
							toValidateSerialPort.closePort();
						} catch (SerialPortException ex) {
						}
					}
				} else {
					LOGGER.info("Port name '" + tempPortName + "' not match");
				}
			}
		} catch (Exception e) {
			LOGGER.error("startComm", e);
		}
	}

	public static void main(String... args) {
		new CommunicationHelper().test();
	}
}
