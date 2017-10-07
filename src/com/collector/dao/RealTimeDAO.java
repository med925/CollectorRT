package com.collector.dao;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.collector.model.DbProperties;
import com.collector.model.Record;
import com.collector.model.Tenant;
import com.collector.model.type.Point;
import com.collector.model.type.RealTimeRecordStatus;
import com.collector.model.type.RecordType;
import com.collector.utils.DBInteraction;

public class RealTimeDAO {

	private DBInteraction rimtrackClient;
	private DBInteraction rimtrackRaw;
	private DBInteraction rimtrackTenant;

	private DbProperties dbProperties;

	/**
	 * inject DbProperties
	 */
	public RealTimeDAO(DbProperties dbProperties) throws IOException {
		this.rimtrackRaw = new DBInteraction(dbProperties.getRawDbUrl() + dbProperties.getRawDbName(),
				dbProperties.getRawDbUsername(), dbProperties.getRawDbPassword());
		this.rimtrackClient = new DBInteraction(dbProperties.getClientDbUrl() + dbProperties.getClientDbName(),
				dbProperties.getClientDbUsername(), dbProperties.getClientDbPassword());
		this.rimtrackTenant = new DBInteraction(dbProperties.getTenantDbUrl() + dbProperties.getTenantDbName(),
				dbProperties.getTenantDbUsername(), dbProperties.getTenantDbPassword());
		this.dbProperties = dbProperties;
	}

	/**
	 * getAllTenants : allows you to retrieve all active tenants
	 */
	public List<Tenant> getAllTenants() throws SQLException {
		List<Tenant> tenants = new ArrayList<Tenant>();
		String selectRequest = "SELECT * FROM `user`";
		this.rimtrackTenant.connect();
		ResultSet tenantsRS = this.rimtrackTenant.select(selectRequest);
		while (tenantsRS.next()) {
			Tenant tenant = new Tenant(tenantsRS.getLong("compte_web_id"));
			tenant.setDevices(this.getAllDevicesOfTenant(tenant.getId()));
			tenants.add(tenant);
		}
		return tenants;
	}

	/**
	 * getAllDevicesOfTenant : allows you to retrieve all device(boitier) of
	 * specific tenant
	 */
	public List<Long> getAllDevicesOfTenant(long idTenant) throws SQLException {
		this.rimtrackClient.setUrl(dbProperties.getUserDbUrl() + dbProperties.getUserDbName() + idTenant
				+ "?autoReconnect=true&useSSL=false");
		List<Long> deviceIds = new ArrayList<Long>();
		String selectRequest = "SELECT * FROM `device`";
		this.rimtrackClient.connect();
		ResultSet devices = this.rimtrackClient.select(selectRequest);
		while (devices.next()) {
			deviceIds.add(devices.getLong("id_device"));
		}
		return deviceIds;
	}

	public String getLastBruteTrame(Long deviceId) throws SQLException {
		String selectRequest = "SELECT * FROM `list_last` WHERE id_boitier = " + deviceId + " LIMIT 1000";
		String bruteTrame = "";
		this.rimtrackRaw.connect();
		ResultSet bruteTrames = this.rimtrackRaw.select(selectRequest);
		if (bruteTrames.next()) {
			bruteTrame = bruteTrames.getString("last_trame");
		}
		return bruteTrame;
	}

	public boolean addRealTimeRecord(long idTenant,Record realTimeRecord) {
		String insertRequest = "";
		this.rimtrackClient.setUrl(dbProperties.getUserDbUrl() + dbProperties.getUserDbName() + idTenant
				+ "?autoReconnect=true&useSSL=false");
		if (realTimeRecord.getRecordType() == RecordType.GPRMC) {
			insertRequest = "INSERT INTO real_time_dev (deviceid,record_time,latitude,longitude,speed,fuel,temperature,validity,ignition,status,type)"
					+ "VALUES(" + realTimeRecord.getDeviceId() + ", '" + realTimeRecord.getRecordTime() + "',"
					+ realTimeRecord.getCoordinate().getLatitude() + "," + realTimeRecord.getCoordinate().getLongitude()
					+ "," + realTimeRecord.getSpeed() + "," + realTimeRecord.getFuel() + ",'"
					+ realTimeRecord.getTemperature() + "'," + realTimeRecord.isValidity() + ","
					+ realTimeRecord.isIgnition() + ",'" + realTimeRecord.getRealTimeRecordStatus() + "','"
					+ realTimeRecord.getRecordType() + "')";
		}
		if (realTimeRecord.getRecordType() == RecordType.AA) {
			insertRequest = "INSERT INTO real_time_dev (deviceid,record_time,latitude,longitude,speed,fuel,`signal`,temperature,validity,ignition,status,type,power,mems_x, mems_y, mems_z,send_flag,sat_in_view,rotation_angle)"
					+ "VALUES(" + realTimeRecord.getDeviceId() + ", '" + realTimeRecord.getRecordTime() + "',"
					+ realTimeRecord.getCoordinate().getLatitude() + "," + realTimeRecord.getCoordinate().getLongitude()
					+ "," + realTimeRecord.getSpeed() + "," + realTimeRecord.getFuel() + ","
					+ realTimeRecord.getSignal() + ",'" + realTimeRecord.getTemperature() + "',"
					+ realTimeRecord.isValidity() + "," + realTimeRecord.isIgnition() + ",'"
					+ realTimeRecord.getRealTimeRecordStatus() + "','" + realTimeRecord.getRecordType() + "',"
					+ realTimeRecord.getPower() + "," + realTimeRecord.getMems_x() + "," + realTimeRecord.getMems_y()
					+ "," + realTimeRecord.getMems_z() + "," + realTimeRecord.getSendFlag() + ","
					+ realTimeRecord.getSatInView() + "," + realTimeRecord.getRotationAngle() + ")";
		}
		this.rimtrackClient.connect();
		boolean isPersisted = this.rimtrackClient.MAJ(insertRequest) != 0 ? true : false;
		return isPersisted;
	}

	public boolean updateRealTimeRecord(long idTenant, Record realTimeRecord) {
		String updateRequest = "";
		this.rimtrackClient.setUrl(dbProperties.getUserDbUrl() + dbProperties.getUserDbName() + idTenant
				+ "?autoReconnect=true&useSSL=false");
		if (realTimeRecord.getRecordType() == RecordType.GPRMC) {
			updateRequest = "UPDATE real_time_dev SET record_time = '" + realTimeRecord.getRecordTime()
					+ "', latitude = " + realTimeRecord.getCoordinate().getLatitude() + ", longitude = "
					+ realTimeRecord.getCoordinate().getLongitude() + ", speed = " + realTimeRecord.getSpeed()
					+ ", fuel = " + realTimeRecord.getFuel() + ", temperature = '" + realTimeRecord.getTemperature()
					+ "', validity = " + realTimeRecord.isValidity() + " ,ignition = " + realTimeRecord.isIgnition()
					+ ",status = '" + realTimeRecord.getRealTimeRecordStatus() + "' where deviceid = "
					+ realTimeRecord.getDeviceId();
		}
		if (realTimeRecord.getRecordType() == RecordType.AA) {
			updateRequest = "UPDATE real_time_dev SET record_time = '" + realTimeRecord.getRecordTime()
					+ "', latitude = " + realTimeRecord.getCoordinate().getLatitude() + ", longitude = "
					+ realTimeRecord.getCoordinate().getLongitude() + ", speed = " + realTimeRecord.getSpeed()
					+ ", fuel = " + realTimeRecord.getFuel() + ", temperature = '" + realTimeRecord.getTemperature()
					+ "', validity = " + realTimeRecord.isValidity() + " ,ignition = " + realTimeRecord.isIgnition()
					+ ",status = '" + realTimeRecord.getRealTimeRecordStatus() + "' ,power = "
					+ realTimeRecord.getPower() + ",`signal` = " + realTimeRecord.getSignal() + " ,mems_x = "
					+ realTimeRecord.getMems_x() + " ,mems_y = " + realTimeRecord.getMems_y() + " ,mems_z = "
					+ realTimeRecord.getMems_z() + " ,send_flag = " + realTimeRecord.getSendFlag() + " ,sat_in_view = "
					+ realTimeRecord.getSatInView() + ",rotation_angle =" + realTimeRecord.getRotationAngle()
					+ " where deviceid = " + realTimeRecord.getDeviceId();
		}
		this.rimtrackClient.connect();
		boolean isPersisted = this.rimtrackClient.MAJ(updateRequest) != 0 ? true : false;
		return isPersisted;
	}

	public boolean updateRealTimeRecordStatus(long idTenant, long deviceId, RealTimeRecordStatus RealTimeRecordStatus) {
		String updateRequest = "UPDATE real_time_dev SET status = '" + RealTimeRecordStatus + "' where deviceid = "
				+ deviceId;
		this.rimtrackClient.setUrl(dbProperties.getUserDbUrl() + dbProperties.getUserDbName() + idTenant
				+ "?autoReconnect=true&useSSL=false");
		this.rimtrackClient.connect();
		boolean isPersisted = this.rimtrackClient.MAJ(updateRequest) != 0 ? true : false;
		return isPersisted;
	}

	/**
	 * getLastRealTimeRecord : allows you to retrieve last client Real Time
	 * record !
	 */
	public Record getLastRealTimeRecord(Long tenantId, Long deviceId) throws SQLException {
		String selectRequest = "SELECT * FROM real_time_dev WHERE deviceid = " + deviceId + " LIMIT 1";
		Record record = null;
		this.rimtrackClient.setUrl(dbProperties.getUserDbUrl() + dbProperties.getUserDbName() + tenantId
				+ "?autoReconnect=true&useSSL=false");
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
				record.setSendFlag(realTimeRecordResultSSet.getInt("send_flag"));
				record.setSatInView(realTimeRecordResultSSet.getInt("sat_in_view"));
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
		return record;
	}

	// public ResultSet getAllDevices() throws SQLException {
	// String selectRequest = "";
	// this.rimtrackClient.connect();
	// ResultSet bruteTrames = this.rimtrackRaw.select(selectRequest);
	// return bruteTrames;
	// }

	public DBInteraction newClientConnexion(String dbName, String dbUrl, String dbUsername, String dbPassword) {
		this.rimtrackClient = new DBInteraction(dbUrl + dbName, dbUsername, dbPassword);
		return this.rimtrackClient;
	}

	public ResultSet findTenants() throws SQLException {
		String selectRequest = "SELECT * FROM `user`";
		this.rimtrackTenant.connect();
		ResultSet tenants = this.rimtrackTenant.select(selectRequest);
		return tenants;
	}

	public ResultSet findDevices(int id) throws SQLException {
		String selectRequest = "SELECT * FROM `device`";
		this.rimtrackClient.connect();
		ResultSet devices = this.rimtrackClient.select(selectRequest);
		return devices;
	}

	public void closeConnecions() {
		this.rimtrackClient.disconnect();
		this.rimtrackRaw.disconnect();
		System.gc();
	}

}
