package com.collector.model;

import java.util.Vector;

public class Tenant {
	private int id;
	private String password, username;
	Vector<Device> devices;

	public Tenant(int id) {
		super();
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Vector<Device> getDevices() {
		return devices;
	}

	public void setDevices(Vector<Device> devices) {
		this.devices = devices;
	}

	public void addDevice(Device d) {
		this.devices.add(d);
	}

}