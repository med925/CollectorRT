package com.collector.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.collector.utils.DBInteraction;

public class RealTimeDAO {

	// private DBInteraction rimtrackClient;
	// private DBInteraction rimtrackArchive;
	private DBInteraction rimtrackRaw;

	int MAX_FRAMES_LATENCY;

	public RealTimeDAO() throws IOException {
		// this.rimtrackClient = new
		// DBInteraction("jdbc:mysql://localhost:3306/rimtrack_client", "root",
		// "");
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
}
