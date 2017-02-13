package com.collector.application;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
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

	int cursor;

	int deviceIds[] = { 200203, 200208, 203508, 200201, 200310, 200313, 200204 };

	public ClientWorker() {
		super();
		this.cursor = 0;
	}

	@Override
	public void run() {

		/*
		 * LOAD CONFIGUTATION
		 */

		try {
			RealTimeDAO realTimeDAO = new RealTimeDAO();
			Properties prop = new Properties();
			InputStream input = null;
			int MAX_SEPEED = 0;
			int FREQUENCY_EXECUTION = 0;
			int DURATION_OF_NON_VALIDITY = 0;
			int DURATION_OF_TECHNICAL_ISSUE = 0;
			int MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE = 0;
			int MAX_FRAMES_LATENCY_IN_STOP_CASE = 0;
			try {
				input = new FileInputStream("config.properties");
				prop.load(input);
				FREQUENCY_EXECUTION = Integer.parseInt(prop.getProperty("FREQUENCY_EXECUTION"));
				MAX_SEPEED = Integer.parseInt(prop.getProperty("MAX_SEPEED"));
				DURATION_OF_NON_VALIDITY = Integer.parseInt(prop.getProperty("DURATION_OF_NON_VALIDITY"));
				DURATION_OF_TECHNICAL_ISSUE = Integer.parseInt(prop.getProperty("DURATION_OF_TECHNICAL_ISSUE"));
				MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE = Integer
						.parseInt(prop.getProperty("MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE"));
				MAX_FRAMES_LATENCY_IN_STOP_CASE = Integer.parseInt(prop.getProperty("MAX_FRAMES_LATENCY_IN_STOP_CASE"));
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

			for (;;) {

				for (int i = 0; i < this.deviceIds.length; i++) {
					// deviceId = this.deviceIds[i];
					System.out.println("<============ THE TASK WAS STARTED AT " + new Date() + " for the device : "
							+ this.deviceIds[i] + "==============>");

					/*
					 * LOAD LAST TRAME
					 */

					ResultSet lastTrame = realTimeDAO.getLastBruteTrame(this.deviceIds[i]);
					Record realTimeRecord = null;

					if (!lastTrame.isBeforeFirst()) {

						/*
						 * IF WE GET NO DATA
						 */

						System.out.println("NO DATA");
						System.out.println("GET OLD TRAME AND VERIFY !");

						ResultSet lastRealTimeRecord = realTimeDAO.getLastRealTimeRecord(this.deviceIds[i]);

						while (lastRealTimeRecord.next()) {
							try {
								SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
								Date parsedDate = dateFormat.parse(lastRealTimeRecord.getString("record_time"));
								Timestamp timestampOfLastRealTimeRecord = new java.sql.Timestamp(parsedDate.getTime());
								int speedOfLastRealTimeRecord = lastRealTimeRecord.getInt("speed");

								System.out.println("Speed of last trame ====> " + speedOfLastRealTimeRecord);

								System.out.println(new Timestamp(new Date().getTime()));
								System.out.println(timestampOfLastRealTimeRecord);

								System.out.println(new Date().getTime());
								System.out.println(timestampOfLastRealTimeRecord.getTime());

								float numberOfSecondBetweenNowAndTimeOfLastTrame = new Date().getTime()
										- timestampOfLastRealTimeRecord.getTime() - 3600000;

								System.out.println("NUMBER OF MINUTES BETWEEN NOW AND LAST TRAME IS => "
										+ numberOfSecondBetweenNowAndTimeOfLastTrame / 60000 + " min");

								DURATION_OF_NON_VALIDITY += (speedOfLastRealTimeRecord > 5)
										? MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE : MAX_FRAMES_LATENCY_IN_STOP_CASE;

								if ((numberOfSecondBetweenNowAndTimeOfLastTrame >= DURATION_OF_NON_VALIDITY)
										&& (numberOfSecondBetweenNowAndTimeOfLastTrame < DURATION_OF_TECHNICAL_ISSUE)) {
									System.out.println("THERE IS A PROBLEM HERE MAYBE THE CAR IN TUNNEL OR GARAGE !");
									realTimeDAO.updateRealTimeRecordStatus(this.deviceIds[i],
											RealTimeRecordStatus.NON_VALID);
								} else if (numberOfSecondBetweenNowAndTimeOfLastTrame >= DURATION_OF_TECHNICAL_ISSUE) {
									System.out.println("THERE IS TECHNICAL ISSUE HERE !");
									realTimeDAO.updateRealTimeRecordStatus(this.deviceIds[i],
											RealTimeRecordStatus.TECHNICAL_ISSUE);
								}
							} catch (Exception e) {

							}
						}
					} else {

						while (lastTrame.next()) {
							String line = lastTrame.getString("last_trame");
							System.out.println(lastTrame.getString("last_time") + " | " + line + " | " + line.length());

							if (line.substring(0, 2).equals("AA")) {
								realTimeRecord = Decoder.decodeAALine(line);
								System.out.println("Decode of AA");
								realTimeRecord.setRecordType(RecordType.AA);
							}
							if (line.substring(0, 6).equals("$GPRMC")) {
								realTimeRecord = Decoder.decodeGPRMCLine(line);
								System.out.println("Decode of $GPRMC");
								realTimeRecord.setRecordType(RecordType.GPRMC);
							}

						}

						boolean coordinateRequirement = realTimeRecord.getCoordinate().getLatitude() != 0
								&& realTimeRecord.getCoordinate().getLatitude() != 0;
						boolean speedRequirement = realTimeRecord.getSpeed() < MAX_SEPEED;

						if (realTimeRecord != null && realTimeRecord.isValidity() && speedRequirement
								&& coordinateRequirement) {

							realTimeRecord.setDeviceId(this.deviceIds[i]);
							realTimeRecord.setRealTimeRecordStatus(RealTimeRecordStatus.VALID);

							System.out.println("WE HAVE HERE A GOOD TRAME ! ==> [UPDATE OLD RECORD]");
							System.out.println(realTimeRecord);

							// WE ADD NEW RECORD IF NOT EXIST
							// UPDATE THE RECORD IF EXIST

							if (!realTimeDAO.updateRealTimeRecord(realTimeRecord))
								realTimeDAO.addRealTimeRecord(realTimeRecord);

						} else {
							System.out.println("WE HAVE HERE A NOT VALID TRAME !");
							System.out.println("GET OLD TRAME AND VERIFY !");
							// RE USE THE SAME PRED LOGIQUE
						}
					}

					System.out.println("<============ THE TASK WAS ENDED AT " + new Date() + " ==============>");
					realTimeDAO.closeConnecions();
				}
				Thread.sleep(FREQUENCY_EXECUTION * 1000);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
