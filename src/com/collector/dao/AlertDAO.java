package com.collector.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.collector.model.Notification;
import com.collector.utils.DBInteraction;

public class AlertDAO {

	private DBInteraction rimtrackClient;
	// private DBInteraction rimtrackArchive;
	private DBInteraction rimtrackRaw;

	int MAX_FRAMES_LATENCY;

	public AlertDAO() throws IOException {

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

	public ResultSet getAlerts(long deviceId) throws SQLException {
		String selectRequest = "select alert.alert_id,information.symbol as 'information_symbol',operator.symbol as 'operator_symbol',configuration.value,information.information_type from alert alert,configuration configuration, information information,operator operator where (alert.device_id = "
				+ deviceId
				+ ") AND (alert.alert_id = configuration.alert_id) AND (configuration.information_id = information.information_id) AND (configuration.operator_id = operator.operator_id);";
		this.rimtrackClient.connect();
		ResultSet bruteTrames = this.rimtrackClient.select(selectRequest);
		return bruteTrames;
	}

	public boolean addNotification(Notification notification) {
		String insertRequest = "insert into notification(label,isReaded,createdAt,alert_id) values('" + notification.getLabel()
				+ "'," + notification.isReaded() + ",'" + notification.getCreatedAt()
				+ "'," + notification.getIdAlert() + ")";
		this.rimtrackClient.connect();
		boolean isPersisted = this.rimtrackClient.MAJ(insertRequest) != 0 ? true : false;
		return isPersisted;
	}

	public void closeConnecions() {
		this.rimtrackClient.disconnect();
		this.rimtrackRaw.disconnect();
	}

}
