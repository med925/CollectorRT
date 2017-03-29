package com.collector.process;

public class RTProcess {

//	public static void alertProcess(Record oldRecord, Record newRecord) {
//		if (newRecord != null) {
//
//			if (CheckIntegity.isValidePoint(newRecord.getCoordinate())
//					&& CheckIntegity.isValidSpeed(newRecord.getSpeed(), MAX_SEPEED, 0)
//					&& CheckIntegity.isValidRealDate(newRecord.getRecordTime(), MAX_FRAMES_LATENCY)) {
//				newRecord.setRealTimeRecordStatus(RealTimeRecordStatus.VALID);
//				isValidTrame = true;
//				System.out.println("GOOD TRAME ! ==> [UPDATE OLD RECORD]");
//				if (!realTimeDAO.updateRealTimeRecord(newRecord))
//					realTimeDAO.addRealTimeRecord(newRecord);
//			} else {
//				isValidTrame = false;
//				System.out.println("NO DATA ! ==> [CHECK VALIDITY]");
//				int state = CheckIntegity.validityOfState(oldRecord, MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE,
//						MAX_FRAMES_LATENCY_IN_STOP_CASE, DURATION_OF_NON_VALIDITY, DURATION_OF_TECHNICAL_ISSUE);
//				if (state == 0) {
//					realTimeDAO.updateRealTimeRecordStatus(deviceId, RealTimeRecordStatus.NON_VALID);
//				}
//				if (state == -1) {
//					realTimeDAO.updateRealTimeRecordStatus(deviceId, RealTimeRecordStatus.TECHNICAL_ISSUE);
//				}
//			}
//
//		} else {
//			isValidTrame = false;
//			System.out.println("NOT VALID TRAME ! ==> [CHECK VALIDITY]");
//			int state = CheckIntegity.validityOfState(oldRecord, MAX_FRAMES_LATENCY_IN_MOUVEMENT_CASE,
//					MAX_FRAMES_LATENCY_IN_STOP_CASE, DURATION_OF_NON_VALIDITY, DURATION_OF_TECHNICAL_ISSUE);
//			if (state == 0) {
//				realTimeDAO.updateRealTimeRecordStatus(deviceId, RealTimeRecordStatus.NON_VALID);
//			}
//			if (state == -1) {
//				realTimeDAO.updateRealTimeRecordStatus(deviceId, RealTimeRecordStatus.TECHNICAL_ISSUE);
//			}
//		}
//	}

}
