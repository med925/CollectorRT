package com.collector.validation;

import java.sql.Timestamp;
import java.util.Date;

import com.collector.model.Record;
import com.collector.model.type.Point;
import com.collector.model.type.RealTimeRecordStatus;

public class CheckIntegity {

	public static boolean isValidePoint(Point point) {
		if (point.getLatitude() == 0 && point.getLongitude() == 0)
			return false;
		return true;
	}

	public static boolean isValidSpeed(int speed, int maxSpeed, int minSpeed) {
		if (speed < maxSpeed && speed >= minSpeed)
			return true;
		return false;
	}

	public static boolean isValidRealDate(Timestamp recordTime, int intervalInSecond) {
		float now = new Date().getTime();
		if ((recordTime.getTime() <= now) && (recordTime.getTime() >= (now - intervalInSecond * 1000)))
			return true;
		else
			return false;
	}

	public static int validityOfState(Record oldRecord, int MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE,
			int MAX_FRAMES_LATENCY_IN_STOP_CASE, int DURATION_OF_NON_VALIDITY, int DURATION_OF_TECHNICAL_ISSUE) {

		int state = 1;

		try {

			Timestamp timestampOfLastRealTimeRecord = oldRecord.getRecordTime();
			int speedOfLastRealTimeRecord = oldRecord.getSpeed();
			RealTimeRecordStatus stateOfLastRealTimeRecord = oldRecord.getRealTimeRecordStatus();

			System.out.println("SPEED OF LAST REAL TRAME : " + speedOfLastRealTimeRecord);
			System.out.println("STATUS OF LAST REAL TRAME : " + stateOfLastRealTimeRecord);
			System.out.println("TIME OF LAST REAL TRAME : " + timestampOfLastRealTimeRecord.getTime());

			float numberOfSecondBetweenNowAndTimeOfLastTrame = new Date().getTime()
					- timestampOfLastRealTimeRecord.getTime();

			System.out.println("NUMBER OF MINUTES BETWEEN NOW AND LAST TRAME IS => "
					+ numberOfSecondBetweenNowAndTimeOfLastTrame / 60000 + " min");

			int DURATION_MAX_OF_NON_VALIDITY = ((speedOfLastRealTimeRecord > 5) ? MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE
					: MAX_FRAMES_LATENCY_IN_STOP_CASE) + DURATION_OF_NON_VALIDITY;

			int DURATION_MAX_OF_TECHNICAL_ISSUE = ((speedOfLastRealTimeRecord > 5)
					? MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE : MAX_FRAMES_LATENCY_IN_STOP_CASE)
					+ DURATION_OF_TECHNICAL_ISSUE;

			System.out.println("DURATION_MAX_OF_NON_VALIDITY : " + DURATION_MAX_OF_NON_VALIDITY / 60000 + " min");
			System.out.println("DURATION_MAX_OF_TECHNICAL_ISSUE : " + DURATION_MAX_OF_TECHNICAL_ISSUE / 60000 + " min");

			if ((numberOfSecondBetweenNowAndTimeOfLastTrame >= DURATION_MAX_OF_NON_VALIDITY)
					&& (numberOfSecondBetweenNowAndTimeOfLastTrame < DURATION_OF_TECHNICAL_ISSUE)
					&& !(stateOfLastRealTimeRecord.equals(RealTimeRecordStatus.NON_VALID))) {
				System.out.println("THERE IS A PROBLEM HERE MAYBE THE CAR IN TUNNEL OR GARAGE !");
				state = 0;
			}

			if (numberOfSecondBetweenNowAndTimeOfLastTrame >= DURATION_MAX_OF_TECHNICAL_ISSUE
					&& !stateOfLastRealTimeRecord.equals(RealTimeRecordStatus.TECHNICAL_ISSUE)) {
				System.out.println("THERE IS TECHNICAL ISSUE HERE !");
				state = -1;
			}

		} catch (Exception e) {

		}
		return state;
	}

}
