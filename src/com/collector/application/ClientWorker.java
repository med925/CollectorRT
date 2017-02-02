package com.collector.application;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Properties;

import com.collector.dao.RealTimeDAO;
import com.collector.model.RealTimeRecord;
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
				ResultSet lastTrame = realTimeDAO.getLastBruteTrame(200205);
				while (lastTrame.next()) {
					String line = lastTrame.getString("last_trame");
					System.out.println(lastTrame.getString("last_time") + " | " + line + " | " + line.length());
					System.out.println(Decoder.decodeLine(line));
					RealTimeRecord realTimeRecord = Decoder.decodeLine(line);
					boolean coordinateRequirement = realTimeRecord.getCoordinate().getLatitude() != 0
							&& realTimeRecord.getCoordinate().getLatitude() != 0;
					boolean speedRequirement = realTimeRecord.getSpeed() < MAX_SEPEED;
					if (realTimeRecord.isValidity() && speedRequirement && coordinateRequirement) {
						System.out.println("WE HAVE HERE A GOOD TRAME !");
					}
				}
				System.out.println("----- THE TASK WAS ENDED AT " + new Date() + " -----");
				Thread.sleep(FREQUENCY_EXECUTION * 1000);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
