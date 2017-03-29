package com.collector.application;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.collector.dao.RealTimeDAO;
import com.collector.model.DbProperties;
import com.collector.model.Record;
import com.collector.model.type.RealTimeRecordStatus;
import com.collector.service.Decoder;
import com.collector.service.TenantService;
import com.collector.validation.CheckIntegity;

public class ClientWorker implements Runnable {

	int DURATION_OF_NON_VALIDITY = 60000;
	int DURATION_MAX_OF_NON_VALIDITY = 0;
	int DURATION_OF_TECHNICAL_ISSUE = 3600000;
	int DURATION_MAX_OF_TECHNICAL_ISSUE = 0;
	int MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE = 60000;
	int MAX_FRAMES_LATENCY_IN_STOP_CASE = 1800000;
	int MAX_SEPEED = 120;
	int FREQUENCY_EXECUTION = 30;
	int MAX_FRAMES_LATENCY = 40;

	public ClientWorker() {
		super();
	}

	@Override
	public void run() {

		try {

			DbProperties dbProperties = new DbProperties();
			dbProperties.load();
			RealTimeDAO realTimeDAO = new RealTimeDAO(dbProperties);
			TenantService tenantService = new TenantService(dbProperties);
			String bruteTrame = null;
			Record newRecord = null;
			Record oldRecord = null;
			int deviceId = 200320;
			boolean isValidTrame = true;

			for (;;) {

				tenantService.loadTenants().forEach(tenant -> {
					System.out.println(tenant.getUsername());
					tenant.getDevices().forEach(device -> {
						System.out.println(device.getId());
					});
				});

				// System.out.println("=== task start at " + new Date() + " for
				// the boitier " + deviceId + " ===");
				//
				// oldRecord = realTimeDAO.getLastRealTimeRecords(deviceId);
				// ResultSet rs = realTimeDAO.getLastBruteTrame(deviceId);
				//
				// if (rs.next()) {
				// bruteTrame = rs.getString("last_trame");
				// }
				//
				// if (bruteTrame.length() > 6) {
				//
				// if (bruteTrame.substring(0, 2).equals("AA")) {
				// if (Decoder.isValidAATrame(bruteTrame)) {
				// newRecord = Decoder.decodeAALine(bruteTrame);
				// newRecord.setDeviceId(deviceId);
				// }
				// }
				//
				// if (bruteTrame.substring(0, 6).equals("$GPRMC")) {
				// if (Decoder.isValidGPRMCTrame(bruteTrame)) {
				// newRecord = Decoder.decodeGPRMCLine(bruteTrame);
				// newRecord.setDeviceId(deviceId);
				// }
				// }
				//
				// }
				//
				// // load old and new record
				//
				// if (newRecord != null) {
				//
				// if (CheckIntegity.isValidePoint(newRecord.getCoordinate())
				// && CheckIntegity.isValidSpeed(newRecord.getSpeed(),
				// MAX_SEPEED, 0)
				// && CheckIntegity.isValidRealDate(newRecord.getRecordTime(),
				// MAX_FRAMES_LATENCY)) {
				// newRecord.setRealTimeRecordStatus(RealTimeRecordStatus.VALID);
				// isValidTrame = true;
				// System.out.println("GOOD TRAME ! ==> [UPDATE OLD RECORD]");
				// if (!realTimeDAO.updateRealTimeRecord(newRecord))
				// realTimeDAO.addRealTimeRecord(newRecord);
				// } else {
				// isValidTrame = false;
				// System.out.println("NO DATA ! ==> [CHECK VALIDITY]");
				// int state = CheckIntegity.validityOfState(oldRecord,
				// MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE,
				// MAX_FRAMES_LATENCY_IN_STOP_CASE, DURATION_OF_NON_VALIDITY,
				// DURATION_OF_TECHNICAL_ISSUE);
				// if (state == 0) {
				// realTimeDAO.updateRealTimeRecordStatus(deviceId,
				// RealTimeRecordStatus.NON_VALID);
				// }
				// if (state == -1) {
				// realTimeDAO.updateRealTimeRecordStatus(deviceId,
				// RealTimeRecordStatus.TECHNICAL_ISSUE);
				// }
				// }
				//
				// } else {
				// isValidTrame = false;
				// System.out.println("NOT VALID TRAME ! ==> [CHECK VALIDITY]");
				// int state = CheckIntegity.validityOfState(oldRecord,
				// MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE,
				// MAX_FRAMES_LATENCY_IN_STOP_CASE, DURATION_OF_NON_VALIDITY,
				// DURATION_OF_TECHNICAL_ISSUE);
				// if (state == 0) {
				// realTimeDAO.updateRealTimeRecordStatus(deviceId,
				// RealTimeRecordStatus.NON_VALID);
				// }
				// if (state == -1) {
				// realTimeDAO.updateRealTimeRecordStatus(deviceId,
				// RealTimeRecordStatus.TECHNICAL_ISSUE);
				// }
				// }
				//
				// System.out.println("OLD RECORD : " + oldRecord);
				// System.out.println("NEW RECORD : " + newRecord);
				//
				// // AlertProcess.alertProcess(newRecord);
				//
				// oldRecord = null;
				// newRecord = null;
				//
				// System.gc();
				// realTimeDAO.closeConnecions();
				Thread.sleep(FREQUENCY_EXECUTION * 1000);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		// catch (SQLException e1) {
		// e1.printStackTrace();
		// }
		catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
