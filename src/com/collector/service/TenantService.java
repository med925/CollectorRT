package com.collector.service;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.collector.dao.RealTimeDAO;
import com.collector.model.DbProperties;
import com.collector.model.Device;
import com.collector.model.Tenant;
import com.collector.utils.DBInteraction;

public class TenantService {

	public DbProperties dbProperties;
	public RealTimeDAO realTimeDAO;

	public TenantService(DbProperties dbProperties) throws IOException {
		this.dbProperties = dbProperties;
		this.realTimeDAO = new RealTimeDAO(dbProperties);
	}

	public List<Tenant> loadTenants() {
		try {
			ResultSet result = realTimeDAO.findTenants();
			List<Tenant> tenants = new ArrayList<>();
			while (result.next()) {
				Tenant tenant = new Tenant(result.getInt("compte_web_id"));
				tenant.setUsername(result.getString("login"));
				tenant.setPassword(result.getString("password"));
				//this.loadDevices(tenant);
				tenant.setDevices(this.loadDevices(tenant.getId()));
				tenants.add(tenant);
			}
			return tenants;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Vector<Device> loadDevices(int tenantId) {
		Vector<Device> devices = new Vector<>();
		realTimeDAO.newClientConnexion(this.dbProperties.getUserDbName() + tenantId, this.dbProperties.getUserDbUrl(),
				this.dbProperties.getUserDbUsername(), this.dbProperties.getUserDbPassword());
		try {
			ResultSet result = realTimeDAO.findDevices(tenantId);
			while (result.next()) {
				Device device = new Device(result.getInt("id_device"));
				devices.add(device);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return devices;
	}

	public void loadDevices(Tenant tenant) {
		Vector<Device> devices = new Vector<>();
		realTimeDAO.newClientConnexion(this.dbProperties.getUserDbName() + tenant.getId(),
				this.dbProperties.getUserDbUrl(), this.dbProperties.getUserDbUsername(),
				this.dbProperties.getUserDbPassword());
		try {
			ResultSet result = realTimeDAO.findDevices(tenant.getId());
			while (result.next()) {
				Device device = new Device(result.getInt("id_device"));
				devices.add(device);
			}
			tenant.setDevices(devices);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}