package com.collector.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.collector.config.Configuration;
import com.collector.utils.DBInteraction;

public class RealTimeDAO {

	// private DBInteraction rimtrackClient;
	// private DBInteraction rimtrackArchive;
	private DBInteraction rimtrackRaw;

	public RealTimeDAO() {
		// this.rimtrackClient = new
		// DBInteraction("jdbc:mysql://localhost:3306/rimtrack_client", "root",
		// "");
		// this.rimtrackArchive = new
		// DBInteraction("jdbc:mysql://localhost:3306/rimtrack_archive", "root",
		// "");
		this.rimtrackRaw = new DBInteraction("jdbc:mysql://localhost:3307/g_server", "tracking", "tracking");
	}

	public ResultSet getLastBruteTrame(int deviceId) throws SQLException {
		String selectRequest = "SELECT * FROM `list_last` WHERE id_boitier = " + deviceId
				+ " and last_time > DATE_SUB(now(),INTERVAL " + Configuration.FREQUENCY_TRANSMITION
				+ " SECOND) and last_time <= CAST(now() as datetime) LIMIT 1000";
		this.rimtrackRaw.connect();
		ResultSet bruteTrames = this.rimtrackRaw.select(selectRequest);
		return bruteTrames;
	}
}
