package com.collector.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.collector.model.Record;
import com.collector.model.type.Point;
import com.collector.model.type.RealTimeRecordStatus;
import com.collector.model.type.RecordType;
import com.collector.utils.DBInteraction;

public class RealTimeDAO {

	private DBInteraction rimtrackClient;
	// private DBInteraction rimtrackArchive;
	private DBInteraction rimtrackRaw;

	int MAX_FRAMES_LATENCY;

	public RealTimeDAO() throws IOException {
		// this.rimtrackArchive = new
		// DBInteraction("jdbc:mysql://localhost:3306/rimtrack_archive", "root",
		// "");

		Properties prop = new Properties();
		InputStream input = null;
		// input = new FileInputStream("config.properties");
//		input = getClass().getClassLoader().getResource("config.properties").openStream();
		input = new FileInputStream("config.properties");
		prop.load(input);

		MAX_FRAMES_LATENCY = Integer.parseInt(prop.getProperty("MAX_FRAMES_LATENCY"));
		this.rimtrackRaw = new DBInteraction(prop.getProperty("RIM_TRACK_RAW_URL"),
				prop.getProperty("RIM_TRACK_RAW_USERNAME"), prop.getProperty("RIM_TRACK_RAW_PASSWORD"));
		this.rimtrackClient = new DBInteraction(prop.getProperty("RIM_TRACK_CLIENT_URL"),
				prop.getProperty("RIM_TRACK_CLIENT_USERNAME"), prop.getProperty("RIM_TRACK_CLIENT_PASSWORD"));

		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public ResultSet getLastBruteTrame(int deviceId) throws SQLException {
//		String selectRequest = "SELECT * FROM `list_last` WHERE id_boitier = " + deviceId
//				+ " and last_time > DATE_SUB(now(),INTERVAL " + MAX_FRAMES_LATENCY
//				+ " SECOND) and last_time <= CAST(now() as datetime) LIMIT 1000";
		String selectRequest = "SELECT * FROM `list_last` WHERE id_boitier = " + deviceId
				+ " LIMIT 1000";
		this.rimtrackRaw.connect();
		ResultSet bruteTrames = this.rimtrackRaw.select(selectRequest);
		return bruteTrames;
	}

	public boolean addRealTimeRecord(Record realTimeRecord) {

		String insertRequest = "";

		if (realTimeRecord.getRecordType() == RecordType.GPRMC) {
			insertRequest = "INSERT INTO real_time_dev (deviceid,record_time,latitude,longitude,vertical,horizontal,speed,fuel,temperature,validity,ignition,status,type)"
					+ "VALUES(" + realTimeRecord.getDeviceId() + ", '" + realTimeRecord.getRecordTime() + "',"
					+ realTimeRecord.getCoordinate().getLatitude() + "," + realTimeRecord.getCoordinate().getLongitude()
					+ ",'" + realTimeRecord.getVertical() + "','" + realTimeRecord.getHorizontal() + "',"
					+ realTimeRecord.getSpeed() + "," + realTimeRecord.getFuel() + ",'"
					+ realTimeRecord.getTemperature() + "'," + realTimeRecord.isValidity() + ","
					+ realTimeRecord.isIgnition() + ",'" + realTimeRecord.getRealTimeRecordStatus() + "','"
					+ realTimeRecord.getRecordType() + "')";
		}

		if (realTimeRecord.getRecordType() == RecordType.AA) {
			insertRequest = "INSERT INTO real_time_dev (deviceid,record_time,latitude,longitude,vertical,horizontal,speed,fuel,temperature,validity,ignition,status,type,power,mems_x, mems_y, mems_z,sendFlag,satInView)"
					+ "VALUES(" + realTimeRecord.getDeviceId() + ", '" + realTimeRecord.getRecordTime() + "',"
					+ realTimeRecord.getCoordinate().getLatitude() + "," + realTimeRecord.getCoordinate().getLongitude()
					+ ",'" + realTimeRecord.getVertical() + "','" + realTimeRecord.getHorizontal() + "',"
					+ realTimeRecord.getSpeed() + "," + realTimeRecord.getFuel() + ",'"
					+ realTimeRecord.getTemperature() + "'," + realTimeRecord.isValidity() + ","
					+ realTimeRecord.isIgnition() + ",'" + realTimeRecord.getRealTimeRecordStatus() + "','"
					+ realTimeRecord.getRecordType() + "'," + realTimeRecord.getPower() + ","
					+ realTimeRecord.getMems_x() + "," + realTimeRecord.getMems_y() + "," + realTimeRecord.getMems_z()
					+ "," + realTimeRecord.getSendFlag() + "," + realTimeRecord.getSatInView() + ")";
		}

		this.rimtrackClient.connect();
		System.out.println(insertRequest);
		boolean isPersisted = this.rimtrackClient.MAJ(insertRequest) != 0 ? true : false;
		return isPersisted;
	}

	public boolean updateRealTimeRecord(Record realTimeRecord) {
		String updateRequest = "";
		if (realTimeRecord.getRecordType() == RecordType.GPRMC) {
			updateRequest = "UPDATE real_time_dev SET record_time = '" + realTimeRecord.getRecordTime()
					+ "', latitude = " + realTimeRecord.getCoordinate().getLatitude() + ", longitude = "
					+ realTimeRecord.getCoordinate().getLongitude() + ", vertical = '" + realTimeRecord.getVertical()
					+ "', horizontal = '" + realTimeRecord.getHorizontal() + "', speed = " + realTimeRecord.getSpeed()
					+ ", fuel = " + realTimeRecord.getFuel() + ", temperature = '" + realTimeRecord.getTemperature()
					+ "', validity = " + realTimeRecord.isValidity() + " ,ignition = " + realTimeRecord.isIgnition()
					+ ",status = '" + realTimeRecord.getRealTimeRecordStatus() + "' where deviceid = "
					+ realTimeRecord.getDeviceId();
		}

		if (realTimeRecord.getRecordType() == RecordType.AA) {
			updateRequest = "UPDATE real_time_dev SET record_time = '" + realTimeRecord.getRecordTime()
					+ "', latitude = " + realTimeRecord.getCoordinate().getLatitude() + ", longitude = "
					+ realTimeRecord.getCoordinate().getLongitude() + ", vertical = '" + realTimeRecord.getVertical()
					+ "', horizontal = '" + realTimeRecord.getHorizontal() + "', speed = " + realTimeRecord.getSpeed()
					+ ", fuel = " + realTimeRecord.getFuel() + ", temperature = '" + realTimeRecord.getTemperature()
					+ "', validity = " + realTimeRecord.isValidity() + " ,ignition = " + realTimeRecord.isIgnition()
					+ ",status = '" + realTimeRecord.getRealTimeRecordStatus() + "' ,power = "
					+ realTimeRecord.getPower() + " ,mems_x = " + realTimeRecord.getMems_x() + " ,mems_y = "
					+ realTimeRecord.getMems_y() + " ,mems_z = " + realTimeRecord.getMems_z() + " ,sendFlag = "
					+ realTimeRecord.getSendFlag() + " ,satInView = " + realTimeRecord.getSatInView()
					+ " where deviceid = " + realTimeRecord.getDeviceId();
		}

		System.out.println(updateRequest);
		this.rimtrackClient.connect();
		boolean isPersisted = this.rimtrackClient.MAJ(updateRequest) != 0 ? true : false;
		return isPersisted;
	}

	public boolean updateRealTimeRecordStatus(long deviceId, RealTimeRecordStatus RealTimeRecordStatus) {
		String updateRequest = "UPDATE real_time_dev SET status = '" + RealTimeRecordStatus + "' where deviceid = "
				+ deviceId;
		System.out.println(updateRequest);
		this.rimtrackClient.connect();
		boolean isPersisted = this.rimtrackClient.MAJ(updateRequest) != 0 ? true : false;
		return isPersisted;
	}

	public ResultSet getLastRealTimeRecord(int deviceId) throws SQLException {
		String selectRequest = "SELECT * FROM real_time_dev WHERE deviceid = " + deviceId + " LIMIT 1";
		this.rimtrackClient.connect();
		ResultSet bruteTrames = this.rimtrackClient.select(selectRequest);
		System.out.println(selectRequest);
		return bruteTrames;
	}

	public Record getLastRealTimeRecords(int deviceId) throws SQLException {
		String selectRequest = "SELECT * FROM real_time_dev WHERE deviceid = " + deviceId + " LIMIT 1";
		Record record = null;
		this.rimtrackClient.connect();
		ResultSet realTimeRecordResultSSet = this.rimtrackClient.select(selectRequest);
		if (realTimeRecordResultSSet.next()) {
			if (realTimeRecordResultSSet.getString("type").equals("GPRMC")) {

				record = new Record();

				record.setRecordTime(realTimeRecordResultSSet.getTimestamp("record_time"));
				record.setCoordinate(new Point(realTimeRecordResultSSet.getDouble("latitude"),
						realTimeRecordResultSSet.getDouble("longitude")));
				record.setDeviceId(realTimeRecordResultSSet.getLong("deviceid"));
				record.setTemperature(realTimeRecordResultSSet.getString("temperature"));
				record.setSpeed(realTimeRecordResultSSet.getInt("speed"));
				record.setFuel(realTimeRecordResultSSet.getInt("fuel"));
				record.setValidity(realTimeRecordResultSSet.getBoolean("validity"));
				record.setIgnition(realTimeRecordResultSSet.getBoolean("ignition"));
				RealTimeRecordStatus status = null;

				if (realTimeRecordResultSSet.getString("status").equals("VALID")) {
					status = RealTimeRecordStatus.VALID;
				}
				if (realTimeRecordResultSSet.getString("status").equals("NON_VALID")) {
					status = RealTimeRecordStatus.NON_VALID;
				}
				if (realTimeRecordResultSSet.getString("status").equals("TECHNICAL_ISSUE")) {
					status = RealTimeRecordStatus.TECHNICAL_ISSUE;
				}
				record.setRealTimeRecordStatus(status);
				record.setRecordType(RecordType.GPRMC);
			}
			if (realTimeRecordResultSSet.getString("type").equals("AA")) {

				record = new Record();
				record.setRecordTime(realTimeRecordResultSSet.getTimestamp("record_time"));
				record.setCoordinate(new Point(realTimeRecordResultSSet.getDouble("latitude"),
						realTimeRecordResultSSet.getDouble("longitude")));
				record.setDeviceId(realTimeRecordResultSSet.getLong("deviceid"));
				record.setTemperature(realTimeRecordResultSSet.getString("temperature"));
				record.setSpeed(realTimeRecordResultSSet.getInt("speed"));
				record.setFuel(realTimeRecordResultSSet.getInt("fuel"));
				record.setValidity(realTimeRecordResultSSet.getBoolean("validity"));
				record.setIgnition(realTimeRecordResultSSet.getBoolean("ignition"));

				record.setPower(realTimeRecordResultSSet.getInt("power"));
				record.setMems_x(realTimeRecordResultSSet.getInt("mems_x"));
				record.setMems_y(realTimeRecordResultSSet.getInt("mems_y"));
				record.setMems_z(realTimeRecordResultSSet.getInt("mems_z"));
				record.setSendFlag(realTimeRecordResultSSet.getInt("sendFlag"));
				record.setSatInView(realTimeRecordResultSSet.getInt("satInView"));
				record.setSignal(realTimeRecordResultSSet.getInt("signal"));

				RealTimeRecordStatus status = null;

				if (realTimeRecordResultSSet.getString("status").equals("VALID")) {
					status = RealTimeRecordStatus.VALID;
				}
				if (realTimeRecordResultSSet.getString("status").equals("NON_VALID")) {
					status = RealTimeRecordStatus.NON_VALID;
				}
				if (realTimeRecordResultSSet.getString("status").equals("TECHNICAL_ISSUE")) {
					status = RealTimeRecordStatus.TECHNICAL_ISSUE;
				}
				record.setRealTimeRecordStatus(status);
				record.setRecordType(RecordType.AA);

			}
		}
		this.rimtrackClient.disconnect();
		// System.out.println(selectRequest);
		return record;
	}

	public ResultSet getAllDevices() throws SQLException {
		String selectRequest = "";
		this.rimtrackClient.connect();
		ResultSet bruteTrames = this.rimtrackRaw.select(selectRequest);
		System.out.println(selectRequest);
		return bruteTrames;
	}

	public void closeConnecions() {
		this.rimtrackClient.disconnect();
		this.rimtrackRaw.disconnect();
		System.gc();
	}

}
