package com.collector.application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.collector.config.Configuration;
import com.collector.dao.RealTimeDAO;
import com.collector.model.Record;
import com.collector.service.Decoder;

public class Application {

	public static boolean isValidTrame(String line) {
		if (line.length() == 74 || line.length() == 104) {
			return true;
		} else {
			return false;
		}
	}

	public static void main(String[] args) throws SQLException, InterruptedException {
		RealTimeDAO realTimeDAO = new RealTimeDAO();
		while (true) {
			ResultSet lastTrame = realTimeDAO.getLastBruteTrame(200304);
			while (lastTrame.next()) {
				String line = lastTrame.getString("last_trame");
				System.out.println(line + " time : " + lastTrame.getString("last_time"));
				if (isValidTrame(line)) {

					System.out.println(line + " time : " + lastTrame.getString("last_time"));

					Record record = Decoder.decodeLine(line);

					boolean speedRequirement = record.getSpeed() < Configuration.MAX_SEPEED;
					boolean coordinateRequirement = record.getCoordinate().getLatitude() != 0
							&& record.getCoordinate().getLatitude() != 0;

					if (speedRequirement && coordinateRequirement) {
						System.out.println("WE HAVE HERE A GOOD TRAME !");
					}
				}
			}
			System.out.println("----- THE TASK WAS ENDED AT " + new Date() + " -----");
			Thread.sleep(Configuration.FREQUENCY_EXECUTION * 1000);
		}
	}
}
