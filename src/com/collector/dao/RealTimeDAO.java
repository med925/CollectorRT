package com.collector.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.collector.model.RealTimeRecord;
import com.collector.model.type.RealTimeRecordStatus;
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
		String selectRequest = "SELECT * FROM `list_last` WHERE id_boitier = " + deviceId
				+ " and last_time > DATE_SUB(now(),INTERVAL " + MAX_FRAMES_LATENCY
				+ " SECOND) and last_time <= CAST(now() as datetime) LIMIT 1000";
		this.rimtrackRaw.connect();
		ResultSet bruteTrames = this.rimtrackRaw.select(selectRequest);
		return bruteTrames;
	}

	public boolean addRealTimeRecord(RealTimeRecord realTimeRecord) {
		String insertRequest = "INSERT INTO real_time (device_id,recordTime,coordinate,vertical,horizontal,speed,fuel,temperature,validity,ignition,status)"
				+ "VALUES(" + realTimeRecord.getDeviceId() + ", '" + realTimeRecord.getRecordTime() + "',"
				+ "ST_GeomFromText('POINT(" + realTimeRecord.getCoordinate().getLatitude() + " "
				+ realTimeRecord.getCoordinate().getLongitude() + ")'),'" + realTimeRecord.getVertical() + "','"
				+ realTimeRecord.getHorizontal() + "'," + realTimeRecord.getSpeed() + "," + realTimeRecord.getFuel()
				+ ",'" + realTimeRecord.getTemperature() + "'," + realTimeRecord.isValidity() + ","
				+ realTimeRecord.isIgnition() + ",'" + realTimeRecord.getRealTimeRecordStatus() + "')";
		this.rimtrackClient.connect();
		System.out.println(insertRequest);
		boolean isPersisted = this.rimtrackClient.MAJ(insertRequest) != 0 ? true : false;
		return isPersisted;
	}

	public boolean updateRealTimeRecord(RealTimeRecord realTimeRecord) {
		String updateRequest = "UPDATE real_time SET recordTime = '" + realTimeRecord.getRecordTime()
				+ "', coordinate = ST_GeomFromText('POINT(" + realTimeRecord.getCoordinate().getLatitude() + " "
				+ realTimeRecord.getCoordinate().getLongitude() + ")'), vertical = '" + realTimeRecord.getVertical()
				+ "', horizontal = '" + realTimeRecord.getHorizontal() + "', speed = " + realTimeRecord.getSpeed()
				+ ", fuel = " + realTimeRecord.getFuel() + ", temperature = '" + realTimeRecord.getTemperature()
				+ "', validity = " + realTimeRecord.isValidity() + " ,ignition = " + realTimeRecord.isIgnition()
				+ ",status = '" + realTimeRecord.getRealTimeRecordStatus() + "' where device_id = "
				+ realTimeRecord.getDeviceId();
		System.out.println(updateRequest);
		this.rimtrackClient.connect();
		boolean isPersisted = this.rimtrackClient.MAJ(updateRequest) != 0 ? true : false;
		return isPersisted;
	}

	public boolean updateRealTimeRecordStatus(long deviceId, RealTimeRecordStatus RealTimeRecordStatus) {
		String updateRequest = "UPDATE real_time SET status = '" + RealTimeRecordStatus + "' where device_id = "
				+ deviceId;
		System.out.println(updateRequest);
		this.rimtrackClient.connect();
		boolean isPersisted = this.rimtrackClient.MAJ(updateRequest) != 0 ? true : false;
		return isPersisted;
	}

	public ResultSet getLastRealTimeRecord(int deviceId) throws SQLException {
		String selectRequest = "SELECT * FROM `real_time` WHERE device_id = " + deviceId + " LIMIT 1";
		this.rimtrackClient.connect();
		ResultSet bruteTrames = this.rimtrackClient.select(selectRequest);
		return bruteTrames;
	}

}
