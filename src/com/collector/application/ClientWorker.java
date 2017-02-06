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
import com.collector.model.RealTimeRecord;
import com.collector.model.type.RealTimeRecordStatus;
import com.collector.service.Decoder;

public class ClientWorker implements Runnable {

	int cursor;

	public ClientWorker() {
		super();
		this.cursor = 0;
	}

	@Override
	public void run() {

		try {
			RealTimeDAO realTimeDAO = new RealTimeDAO();
			Properties prop = new Properties();
			InputStream input = null;
			int MAX_SEPEED = 0;
			int FREQUENCY_EXECUTION = 0;

			try {
				input = new FileInputStream("config.properties");
				prop.load(input);
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

			for (;;) {

				System.out.println("<============ THE TASK WAS STARTED AT " + new Date() + " ==============>");

				ResultSet lastTrame = realTimeDAO.getLastBruteTrame(200204);
				RealTimeRecord realTimeRecord = null;

				if (!lastTrame.isBeforeFirst()) {

					System.out.println("NO DATA");
					System.out.println("GET OLD TRAME AND VERIFY !");

					ResultSet lastRealTimeRecord = realTimeDAO.getLastRealTimeRecord(200204);

					while (lastRealTimeRecord.next()) {
						try {
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
							Date parsedDate = dateFormat.parse(lastRealTimeRecord.getString("recordTime"));
							Timestamp timestampOfLastRealTimeRecord = new java.sql.Timestamp(parsedDate.getTime());

							System.out.println(new Date());
							System.out.println(timestampOfLastRealTimeRecord);

							System.out.println(new Date().getTime());
							System.out.println(timestampOfLastRealTimeRecord.getTime());

							float numberOfSecondBetweenNowAndTimeOfLastTrame = new Date().getTime()
									- timestampOfLastRealTimeRecord.getTime();

							System.out.println("NUMBER OF MINUTES BETWEEN NOW AND LAST TRAME IS => "
									+ numberOfSecondBetweenNowAndTimeOfLastTrame / 60000 + " min");

							if ((numberOfSecondBetweenNowAndTimeOfLastTrame) >= (1800000 + 3600000)) {
								System.out.println("THERE IS A PROBLEM HERE MAYBE THE CAR IN TUNNEL OR GARAGE !");
							}
						} catch (Exception e) {

						}
					}
				} else {

					while (lastTrame.next()) {
						String line = lastTrame.getString("last_trame");
						System.out.println(lastTrame.getString("last_time") + " | " + line + " | " + line.length());
						System.out.println(Decoder.decodeLine(line));
						realTimeRecord = Decoder.decodeLine(line);
					}

					boolean coordinateRequirement = realTimeRecord.getCoordinate().getLatitude() != 0
							&& realTimeRecord.getCoordinate().getLatitude() != 0;
					boolean speedRequirement = realTimeRecord.getSpeed() < MAX_SEPEED;

					if (realTimeRecord != null && realTimeRecord.isValidity() && speedRequirement
							&& coordinateRequirement) {

						realTimeRecord.setDeviceId(200204);
						realTimeRecord.setRealTimeRecordStatus(RealTimeRecordStatus.VALID);

						System.out.println("WE HAVE HERE A GOOD TRAME ! ==> [UPDATE OLD RECORD]");

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
				Thread.sleep(FREQUENCY_EXECUTION * 1000);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
