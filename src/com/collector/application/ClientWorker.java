package com.collector.application;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import com.collector.dao.RealTimeDAO;
import com.collector.model.Record;
import com.collector.model.type.RealTimeRecordStatus;
import com.collector.model.type.RecordType;
import com.collector.service.Decoder;

public class ClientWorker implements Runnable {

	int DURATION_OF_NON_VALIDITY = 0;
	int DURATION_MAX_OF_NON_VALIDITY = 0;
	int DURATION_OF_TECHNICAL_ISSUE = 0;
	int DURATION_MAX_OF_TECHNICAL_ISSUE = 0;
	int MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE = 0;
	int MAX_FRAMES_LATENCY_IN_STOP_CASE = 0;
	int MAX_SEPEED = 0;
	int FREQUENCY_EXECUTION = 0;

	int deviceIds[] = { 201310, 200304, 200203, 200206, 200204, 208209, 200208, 200201, 200202, 200207, 200310, 200313,
			200204 };

	public ClientWorker() {
		super();
		this.loadConfiguration();
	}

	public void loadConfiguration() {
		System.out.println("********************* START LOAD FONCTIONNEL CONFIGURATION ********************");
		Properties prop = new Properties();
		InputStream input = null;
		try {
			// input = new FileInputStream("config.properties");
			input = getClass().getClassLoader().getResource("config.properties").openStream();
			prop.load(input);
			DURATION_OF_NON_VALIDITY = Integer.parseInt(prop.getProperty("DURATION_OF_NON_VALIDITY"));
			DURATION_OF_TECHNICAL_ISSUE = Integer.parseInt(prop.getProperty("DURATION_OF_TECHNICAL_ISSUE"));
			MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE = Integer
					.parseInt(prop.getProperty("MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE"));
			MAX_FRAMES_LATENCY_IN_STOP_CASE = Integer.parseInt(prop.getProperty("MAX_FRAMES_LATENCY_IN_STOP_CASE"));
			FREQUENCY_EXECUTION = Integer.parseInt(prop.getProperty("FREQUENCY_EXECUTION"));
			MAX_SEPEED = Integer.parseInt(prop.getProperty("MAX_SEPEED"));
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("********************* END LOAD FONCTIONNEL CONFIGURATION ********************");
	}

	public void checkValidity(int deviceId) throws IOException, SQLException {
		System.out.println("********************* START CHECK VALIDITY ********************");
		RealTimeDAO realTimeDAO = new RealTimeDAO();
		ResultSet lastRealTimeRecord = realTimeDAO.getLastRealTimeRecord(deviceId);
		while (lastRealTimeRecord.next()) {
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				Date parsedDate = dateFormat.parse(lastRealTimeRecord.getString("record_time"));
				Timestamp timestampOfLastRealTimeRecord = new java.sql.Timestamp(parsedDate.getTime());
				int speedOfLastRealTimeRecord = lastRealTimeRecord.getInt("speed");
				String stateOfLastRealTimeRecord = lastRealTimeRecord.getString("status");

				System.out.println("SPEED OF LAST REAL TRAME : " + speedOfLastRealTimeRecord);
				System.out.println("STATUS OF LAST REAL TRAME : " + stateOfLastRealTimeRecord);
				System.out.println("TIME OF LAST REAL TRAME : " + timestampOfLastRealTimeRecord.getTime());

				float numberOfSecondBetweenNowAndTimeOfLastTrame = new Date().getTime()
						- timestampOfLastRealTimeRecord.getTime() - 3600000;

				System.out.println("NUMBER OF MINUTES BETWEEN NOW AND LAST TRAME IS => "
						+ numberOfSecondBetweenNowAndTimeOfLastTrame / 60000 + " min");

				DURATION_MAX_OF_NON_VALIDITY = ((speedOfLastRealTimeRecord > 5) ? MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE
						: MAX_FRAMES_LATENCY_IN_STOP_CASE) + DURATION_OF_NON_VALIDITY;
				DURATION_MAX_OF_TECHNICAL_ISSUE = ((speedOfLastRealTimeRecord > 5)
						? MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE : MAX_FRAMES_LATENCY_IN_STOP_CASE)
						+ DURATION_OF_TECHNICAL_ISSUE;

				System.out.println("DURATION_MAX_OF_NON_VALIDITY : " + DURATION_MAX_OF_NON_VALIDITY / 60000 + " min");
				System.out.println(
						"DURATION_MAX_OF_TECHNICAL_ISSUE : " + DURATION_MAX_OF_TECHNICAL_ISSUE / 60000 + " min");

				if ((numberOfSecondBetweenNowAndTimeOfLastTrame >= DURATION_MAX_OF_NON_VALIDITY)
						&& (numberOfSecondBetweenNowAndTimeOfLastTrame < DURATION_OF_TECHNICAL_ISSUE)
						&& !(stateOfLastRealTimeRecord.equals("NON_VALID"))) {
					System.out.println("THERE IS A PROBLEM HERE MAYBE THE CAR IN TUNNEL OR GARAGE !");
					realTimeDAO.updateRealTimeRecordStatus(deviceId, RealTimeRecordStatus.NON_VALID);
				}

				if (numberOfSecondBetweenNowAndTimeOfLastTrame >= DURATION_MAX_OF_TECHNICAL_ISSUE
						&& !stateOfLastRealTimeRecord.equals("TECHNICAL_ISSUE")) {
					System.out.println("THERE IS TECHNICAL ISSUE HERE !");
					realTimeDAO.updateRealTimeRecordStatus(deviceId, RealTimeRecordStatus.TECHNICAL_ISSUE);
				}

			} catch (Exception e) {

			}
		}
		System.out.println("********************* END CHECK VALIDITY ********************");
	}

	@Override
	public void run() {

		try {
			RealTimeDAO realTimeDAO = new RealTimeDAO();
			for (;;) {
				for (int i = 0; i < this.deviceIds.length; i++) {

					System.out.println("<=====================================================" + this.deviceIds[i]
							+ "=============================================================>");

					ResultSet lastTrame = realTimeDAO.getLastBruteTrame(this.deviceIds[i]);
					Record realTimeRecord = null;
					if (!lastTrame.isBeforeFirst()) {
						checkValidity(deviceIds[i]);
					} else {
						while (lastTrame.next()) {
							String line = lastTrame.getString("last_trame");
							System.out.println(lastTrame.getString("last_time") + " | " + line + " | " + line.length());
							if (line.substring(0, 2).equals("AA")) {
								if (Decoder.isValidAATrame(line)) {
									realTimeRecord = Decoder.decodeAALine(line);
									realTimeRecord.setRecordType(RecordType.AA);
								}
							}
							if (line.substring(0, 6).equals("$GPRMC")) {
								if (Decoder.isValidGPRMCTrame(line)) {
									realTimeRecord = Decoder.decodeGPRMCLine(line);
									realTimeRecord.setRecordType(RecordType.GPRMC);
								}
							}
						}
						if (realTimeRecord != null) {
							boolean coordinateRequirement = realTimeRecord.getCoordinate().getLatitude() != 0
									&& realTimeRecord.getCoordinate().getLatitude() != 0;
							boolean speedRequirement = realTimeRecord.getSpeed() < MAX_SEPEED;
							if (realTimeRecord.isValidity() && speedRequirement && coordinateRequirement) {
								realTimeRecord.setDeviceId(this.deviceIds[i]);
								realTimeRecord.setRealTimeRecordStatus(RealTimeRecordStatus.VALID);
								System.out.println("WE HAVE HERE A GOOD TRAME ! ==> [UPDATE OLD RECORD]");
								if (!realTimeDAO.updateRealTimeRecord(realTimeRecord))
									realTimeDAO.addRealTimeRecord(realTimeRecord);
							}
						} else {
							System.out.println("WE HAVE HERE A NOT VALID TRAME ! ==> [CHECK VALIDITY]");
							checkValidity(deviceIds[i]);
						}
					}
					System.out.println(
							"<==================================================================================================================>");
					realTimeDAO.closeConnecions();
				}
				Thread.sleep(FREQUENCY_EXECUTION * 1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
