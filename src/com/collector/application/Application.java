package com.collector.application;

import java.io.IOException;
import java.sql.SQLException;

public class Application {

	public static void main(String[] args) throws SQLException, InterruptedException, IOException {
		 (new Thread(new ClientWorker())).start();
//		RealTimeDAO realTimeDAO = new RealTimeDAO();
//		RealTimeRecord realTimeRecord = new RealTimeRecord();
//		realTimeRecord.setRecordTime(new Timestamp(1486120571));
//		realTimeRecord.setCoordinate(new Point(758441, 251));
//		realTimeRecord.setHorizontal(PointCardinal.OUEST);
//		realTimeRecord.setVertical(PointCardinal.SUD);
//		realTimeRecord.setSpeed(60);
//		realTimeRecord.setFuel(60);
//		realTimeRecord.setTemperature("200C°");
//		realTimeRecord.setValidity(true);
//		realTimeRecord.setIgnition(false);
//		realTimeRecord.setDeviceId(6);
//		realTimeRecord.setRealTimeRecordStatus(RealTimeRecordStatus.VALID);
//		System.out.println(realTimeRecord);
//		// realTimeDAO.addRealTimeRecord(realTimeRecord);
//		// realTimeDAO.updateRealTimeRecord(realTimeRecord);
//		realTimeDAO.updateRealTimeRecordStatus(6, RealTimeRecordStatus.NON_VALID);
	}
}
