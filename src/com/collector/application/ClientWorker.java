package com.collector.application;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import com.collector.dao.RealTimeDAO;
import com.collector.model.DbProperties;
import com.collector.model.Device;
import com.collector.model.Record;
import com.collector.model.Tenant;
import com.collector.model.type.RealTimeRecordStatus;
import com.collector.model.type.RecordType;
import com.collector.service.Decoder;
import com.collector.service.TenantService;
import com.collector.validation.CheckIntegity;

public class ClientWorker implements Runnable {

	int DURATION_OF_NON_VALIDITY = 0;
	int DURATION_MAX_OF_NON_VALIDITY = 0;
	int DURATION_OF_TECHNICAL_ISSUE = 0;
	int DURATION_MAX_OF_TECHNICAL_ISSUE = 0;
	int MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE = 0;
	int MAX_FRAMES_LATENCY_IN_STOP_CASE = 0;
	int MAX_SPEED = 0;
	int FREQUENCY_EXECUTION = 0;
	int MAX_FRAMES_LATENCY = 0;

	public ClientWorker() {
		super();
	}

	@Override
	public void run() {

		try {

			DbProperties dbProperties = new DbProperties();
			dbProperties.load();
			loadRtConfig();
			RealTimeDAO realTimeDAO = new RealTimeDAO(dbProperties);
			TenantService tenantService = new TenantService(dbProperties);

			String bruteTrame = null;
			Record newRecord = null;
			Record oldRecord = null;
			int deviceId = 0;
			boolean isValidTrame = true;

			int numberOfAttempts = 0;
			List<Tenant> tenants = null;

			for (;;) {

				if (numberOfAttempts == 0) {
					tenants = tenantService.loadTenants();
					numberOfAttempts++;
					if (numberOfAttempts == 5)
						numberOfAttempts = 0;
				}

				for (Tenant tenant : tenants) {

					System.out.println("processing tenant: "+tenant.getUsername());

					realTimeDAO.newClientConnexion(dbProperties.getUserDbName() + tenant.getId(),
							dbProperties.getUserDbUrl(), dbProperties.getUserDbUsername(),
							dbProperties.getUserDbPassword());

					for (Device device : tenant.getDevices()) {

						System.out.println("device: "+device.getId());

						deviceId = device.getId();
						//System.out.println("=== task start at " + new Date() + " for the boitier " + deviceId + " ===");

						oldRecord = realTimeDAO.getLastRealTimeRecords(deviceId);
						ResultSet rs = realTimeDAO.getLastBruteTrame(deviceId);
						if(oldRecord!=null)System.out.println("old tram time : " + oldRecord.getRecordTime());
						else System.out.println("old tram is null !");
						if (rs.next()) {
							bruteTrame = rs.getString("last_trame");
							if (bruteTrame!= null && bruteTrame.length() > 6) {

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
								// load old and new record
								if (newRecord != null) {

									if (CheckIntegity.isValidePoint(newRecord.getCoordinate())
											&& CheckIntegity.isValidSpeed(newRecord.getSpeed(), MAX_SPEED, 0)
											&& CheckIntegity.isValidRealDate(newRecord.getRecordTime(),
													MAX_FRAMES_LATENCY)) {
										
										newRecord.setRealTimeRecordStatus(RealTimeRecordStatus.VALID);
										isValidTrame = true;
										//System.out.println("GOOD TRAME ! ==> [UPDATE OLD RECORD]");
										System.out.println("tram time is valid: "+newRecord.getRecordTime());
										if (!realTimeDAO.updateRealTimeRecord(newRecord))
											realTimeDAO.addRealTimeRecord(newRecord);	
									} else {
										
										isValidTrame = false;
										//System.out.println("NO DATA ! ==> [CHECK VALIDITY]");
										System.out.println("tram time invalid: "+newRecord.getRecordTime());
										int state = CheckIntegity.validityOfState(oldRecord,
												MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE, MAX_FRAMES_LATENCY_IN_STOP_CASE,
												DURATION_OF_NON_VALIDITY, DURATION_OF_TECHNICAL_ISSUE);
										if (state == 0) {
											realTimeDAO.updateRealTimeRecordStatus(deviceId,
													RealTimeRecordStatus.NON_VALID);
										}
										if (state == -1) {
											realTimeDAO.updateRealTimeRecordStatus(deviceId,
													RealTimeRecordStatus.TECHNICAL_ISSUE);
										}
									}

								} else {
									
									if(oldRecord == null){
										Record emptyRecord = new Record();
										emptyRecord.setDeviceId(deviceId);
										emptyRecord.setRecordType(RecordType.AA);
										realTimeDAO.addRealTimeRecord(emptyRecord);
									}
									
									isValidTrame = false;
									//System.out.println("NOT VALID TRAME ! ==> [CHECK VALIDITY]");
									System.out.println("invalid tram: ");
									int state = CheckIntegity.validityOfState(oldRecord,
											MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE, MAX_FRAMES_LATENCY_IN_STOP_CASE,
											DURATION_OF_NON_VALIDITY, DURATION_OF_TECHNICAL_ISSUE);
									if (state == 0) {
										realTimeDAO.updateRealTimeRecordStatus(deviceId,
												RealTimeRecordStatus.NON_VALID);
									}
									if (state == -1) {
										realTimeDAO.updateRealTimeRecordStatus(deviceId,
												RealTimeRecordStatus.TECHNICAL_ISSUE);
									}
								}
							}
						}

						oldRecord = null;
						newRecord = null;
						bruteTrame = null;

						System.gc();
					}
				}
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
	
	public void loadRtConfig() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			// input =
			// getClass().getClassLoader().getResource("config.properties").openStream();
			input = new FileInputStream("config.properties");// .getClassLoader().getResource("config.properties").openStream();
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					this.DURATION_OF_NON_VALIDITY = Integer.parseInt(prop.getProperty("DURATION_OF_NON_VALIDITY"));
					this.DURATION_MAX_OF_NON_VALIDITY = Integer.parseInt(prop.getProperty("DURATION_MAX_OF_NON_VALIDITY"));
					this.DURATION_OF_TECHNICAL_ISSUE = Integer.parseInt(prop.getProperty("DURATION_OF_TECHNICAL_ISSUE"));
					this.DURATION_MAX_OF_TECHNICAL_ISSUE = Integer.parseInt(prop.getProperty("DURATION_MAX_OF_TECHNICAL_ISSUE"));
					this.MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE = Integer.parseInt(prop.getProperty("MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE"));
					this.MAX_FRAMES_LATENCY_IN_STOP_CASE = Integer.parseInt(prop.getProperty("MAX_FRAMES_LATENCY_IN_STOP_CASE"));
					this.MAX_SPEED = Integer.parseInt(prop.getProperty("MAX_SPEED"));
					this.FREQUENCY_EXECUTION = Integer.parseInt(prop.getProperty("FREQUENCY_EXECUTION"));
					this.MAX_FRAMES_LATENCY = Integer.parseInt(prop.getProperty("MAX_FRAMES_LATENCY"));
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.gc();
	}

}
