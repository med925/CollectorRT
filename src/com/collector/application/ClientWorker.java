package com.collector.application;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import com.collector.dao.RealTimeDAO;
import com.collector.model.Record;
import com.collector.model.type.RealTimeRecordStatus;
import com.collector.process.AlertProcess;
import com.collector.service.Decoder;
import com.collector.validation.CheckIntegity;

public class ClientWorker implements Runnable {

	int DURATION_OF_NON_VALIDITY = 0;
	int DURATION_MAX_OF_NON_VALIDITY = 0;
	int DURATION_OF_TECHNICAL_ISSUE = 0;
	int DURATION_MAX_OF_TECHNICAL_ISSUE = 0;
	int MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE = 0;
	int MAX_FRAMES_LATENCY_IN_STOP_CASE = 0;
	int MAX_SEPEED = 0;
	int FREQUENCY_EXECUTION = 0;
	int MAX_FRAMES_LATENCY = 0;

	public ClientWorker() {
		super();
		this.loadConfiguration();
	}

	@Override
	public void run() {

		try {

			RealTimeDAO realTimeDAO = new RealTimeDAO();
			String bruteTrame = null;
			Record newRecord = null;
			Record oldRecord = null;
			int deviceId = 200320;
			boolean isValidTrame = true;

			for (;;) {

				System.out
						.println("======= task start at " + new Date() + " for the boitier " + deviceId + " ========");

				oldRecord = realTimeDAO.getLastRealTimeRecords(deviceId);
				ResultSet rs = realTimeDAO.getLastBruteTrame(deviceId);

				if (rs.next()) {
					bruteTrame = rs.getString("last_trame");
				}

				if (bruteTrame.length() > 6) {

					if (bruteTrame.substring(0, 2).equals("AA")) {
						if (Decoder.isValidAATrame(bruteTrame)) {
							newRecord = Decoder.decodeAALine(bruteTrame);
							newRecord.setDeviceId(deviceId);
						}
					}

					if (bruteTrame.substring(0, 6).equals("$GPRMC")) {
						if (Decoder.isValidGPRMCTrame(bruteTrame)) {
							newRecord = Decoder.decodeGPRMCLine(bruteTrame);
							newRecord.setDeviceId(deviceId);
						}
					}

				}

				if (newRecord != null) {

					if (CheckIntegity.isValidePoint(newRecord.getCoordinate())
							&& CheckIntegity.isValidSpeed(newRecord.getSpeed(), MAX_SEPEED, 0)
							&& CheckIntegity.isValidRealDate(newRecord.getRecordTime(), MAX_FRAMES_LATENCY)) {
						newRecord.setRealTimeRecordStatus(RealTimeRecordStatus.VALID);
						isValidTrame = true;
						System.out.println("GOOD TRAME ! ==> [UPDATE OLD RECORD]");
						if (!realTimeDAO.updateRealTimeRecord(newRecord))
							realTimeDAO.addRealTimeRecord(newRecord);
					} else {
						isValidTrame = false;
						System.out.println("NO DATA ! ==> [CHECK VALIDITY]");
						int state = CheckIntegity.validityOfState(oldRecord, MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE,
								MAX_FRAMES_LATENCY_IN_STOP_CASE, DURATION_OF_NON_VALIDITY, DURATION_OF_TECHNICAL_ISSUE);
						if (state == 0) {
							realTimeDAO.updateRealTimeRecordStatus(deviceId, RealTimeRecordStatus.NON_VALID);
						}
						if (state == -1) {
							realTimeDAO.updateRealTimeRecordStatus(deviceId, RealTimeRecordStatus.TECHNICAL_ISSUE);
						}
					}

				} else {
					isValidTrame = false;
					System.out.println("NOT VALID TRAME ! ==> [CHECK VALIDITY]");
					int state = CheckIntegity.validityOfState(oldRecord, MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE,
							MAX_FRAMES_LATENCY_IN_STOP_CASE, DURATION_OF_NON_VALIDITY, DURATION_OF_TECHNICAL_ISSUE);
					if (state == 0) {
						realTimeDAO.updateRealTimeRecordStatus(deviceId, RealTimeRecordStatus.NON_VALID);
					}
					if (state == -1) {
						realTimeDAO.updateRealTimeRecordStatus(deviceId, RealTimeRecordStatus.TECHNICAL_ISSUE);
					}
				}

				System.out.println("OLD RECORD : " + oldRecord);
				System.out.println("NEW RECORD : " + newRecord);

				AlertProcess.alertProcess(newRecord);

				oldRecord = null;
				newRecord = null;

				System.gc();
				realTimeDAO.closeConnecions();
				Thread.sleep(FREQUENCY_EXECUTION * 1000);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void loadConfiguration() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			// input =
			// getClass().getClassLoader().getResource("config.properties").openStream();
			input = new FileInputStream("config.properties");// .getClassLoader().getResource("config.properties").openStream();
			prop.load(input);
			DURATION_OF_NON_VALIDITY = Integer.parseInt(prop.getProperty("DURATION_OF_NON_VALIDITY"));
			DURATION_OF_TECHNICAL_ISSUE = Integer.parseInt(prop.getProperty("DURATION_OF_TECHNICAL_ISSUE"));
			MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE = Integer
					.parseInt(prop.getProperty("MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE"));
			MAX_FRAMES_LATENCY_IN_STOP_CASE = Integer.parseInt(prop.getProperty("MAX_FRAMES_LATENCY_IN_STOP_CASE"));
			FREQUENCY_EXECUTION = Integer.parseInt(prop.getProperty("FREQUENCY_EXECUTION"));
			MAX_SEPEED = Integer.parseInt(prop.getProperty("MAX_SEPEED"));
			MAX_FRAMES_LATENCY = Integer.parseInt(prop.getProperty("MAX_FRAMES_LATENCY"));
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
		System.gc();
	}

}
