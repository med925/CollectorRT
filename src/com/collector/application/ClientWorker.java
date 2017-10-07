package com.collector.application;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.collector.dao.RealTimeDAO;
import com.collector.model.DbProperties;
import com.collector.model.Record;
import com.collector.model.Tenant;
import com.collector.model.type.RealTimeRecordStatus;
import com.collector.model.type.RecordType;
import com.collector.service.Decoder;
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

			String bruteTrame = null;
			Record newRecord = null;
			Record oldRecord = null;

			int numberOfAttempts = 0;
			List<Tenant> tenants = null;

			for (;;) {

				try {
					/**
					 * load tenant after every 15 exe !
					 */
					if (numberOfAttempts == 0) {
						System.out.println("===========================================================");
						System.out.println("======= load tenant ==========");
						System.out.println("===========================================================");
						tenants = realTimeDAO.getAllTenants();

						if (tenants != null)
							System.out.println("number of tenants id " + tenants.size());

					} else if (numberOfAttempts == 15)
						numberOfAttempts = -1;

					numberOfAttempts++;

					for (Tenant tenant : tenants) {
						try {

							System.out.println("=================================");
							System.out.println("processing " + tenant + " at " + new Date());

							for (Long device : tenant.getDevices()) {
								System.out.println(" * device: " + device);

								/**
								 * ref the last RT record of the current client
								 * !
								 */
								oldRecord = realTimeDAO.getLastRealTimeRecord(tenant.getId(), device);

								/** ref to the newest brute trame ! */
								bruteTrame = realTimeDAO.getLastBruteTrame(device);

								if (bruteTrame != null && bruteTrame.length() > 6) {
									// ===========================================
									// brute trame => Record
									// ===========================================
									/**
									 * Decode AA trame
									 */
									if (bruteTrame.substring(0, 2).equals("AA")) {
										if (Decoder.isValidAATrame(bruteTrame)) {
											newRecord = Decoder.decodeAALine(bruteTrame);
											newRecord.setDeviceId(device);
										}
									}
									/**
									 * Decode GPRMC trame
									 */
									if (bruteTrame.substring(0, 6).equals("$GPRMC")) {
										if (Decoder.isValidGPRMCTrame(bruteTrame)) {
											newRecord = Decoder.decodeGPRMCLine(bruteTrame);
											newRecord.setDeviceId(device);
										}
									}
									// =============================================

									/** Start RT process (old,new Record) ! */

									/** if new record is not null ! */
									if (newRecord != null) {

										/** if newRecord is valid ! */
										if (CheckIntegity.isValidePoint(newRecord.getCoordinate())
												&& CheckIntegity.isValidSpeed(newRecord.getSpeed(), MAX_SPEED, 0)
												&& CheckIntegity.isValidRealDate(newRecord.getRecordTime(),
														MAX_FRAMES_LATENCY)) {
											newRecord.setRealTimeRecordStatus(RealTimeRecordStatus.VALID);
											System.out.println(
													"valid trame (update/add record):" + newRecord.getRecordTime());
											/** add or update last record ! */
											if (!realTimeDAO.updateRealTimeRecord(tenant.getId(), newRecord))
												realTimeDAO.addRealTimeRecord(tenant.getId(), newRecord);
										} else {

											System.out.println("tram invalid !");
											int state = CheckIntegity.validityOfState(oldRecord,
													MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE,
													MAX_FRAMES_LATENCY_IN_STOP_CASE, DURATION_OF_NON_VALIDITY,
													DURATION_OF_TECHNICAL_ISSUE);
											if (state == 0) {
												realTimeDAO.updateRealTimeRecordStatus(tenant.getId(), device,
														RealTimeRecordStatus.NON_VALID);
											}
											if (state == -1) {
												realTimeDAO.updateRealTimeRecordStatus(tenant.getId(), device,
														RealTimeRecordStatus.TECHNICAL_ISSUE);
											}
										}

									}
									/** if new record is null ! */
									else {

										/** if old record is null ! */
										if (oldRecord == null) {
											Record emptyRecord = new Record();
											emptyRecord.setDeviceId(device);
											emptyRecord.setRecordType(RecordType.AA);
											realTimeDAO.addRealTimeRecord(tenant.getId(), emptyRecord);
										}

										System.out.println("invalid tram !");
										int state = CheckIntegity.validityOfState(oldRecord,
												MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE, MAX_FRAMES_LATENCY_IN_STOP_CASE,
												DURATION_OF_NON_VALIDITY, DURATION_OF_TECHNICAL_ISSUE);
										if (state == 0) {
											realTimeDAO.updateRealTimeRecordStatus(tenant.getId(), device,
													RealTimeRecordStatus.NON_VALID);
										}
										if (state == -1) {
											realTimeDAO.updateRealTimeRecordStatus(tenant.getId(), device,
													RealTimeRecordStatus.TECHNICAL_ISSUE);
										}
									}
								}
							}
							oldRecord = null;
							newRecord = null;
							bruteTrame = null;

						} catch (Exception e) {
							continue;
						}

					}
					realTimeDAO.closeConnecions();
					Thread.sleep(FREQUENCY_EXECUTION * 1000);
				} catch (Exception e) {
					continue;
				} finally {
					System.gc();
				}
			}
		} catch (IOException e) {
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
					this.DURATION_MAX_OF_NON_VALIDITY = Integer
							.parseInt(prop.getProperty("DURATION_MAX_OF_NON_VALIDITY"));
					this.DURATION_OF_TECHNICAL_ISSUE = Integer
							.parseInt(prop.getProperty("DURATION_OF_TECHNICAL_ISSUE"));
					this.DURATION_MAX_OF_TECHNICAL_ISSUE = Integer
							.parseInt(prop.getProperty("DURATION_MAX_OF_TECHNICAL_ISSUE"));
					this.MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE = Integer
							.parseInt(prop.getProperty("MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE"));
					this.MAX_FRAMES_LATENCY_IN_STOP_CASE = Integer
							.parseInt(prop.getProperty("MAX_FRAMES_LATENCY_IN_STOP_CASE"));
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
