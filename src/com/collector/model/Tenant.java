package com.collector.model;

import java.util.List;

public class Tenant {
	private long id;
	private List<Long> devices;

	public Tenant(long id) {
		super();
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<Long> getDevices() {
		return devices;
	}

	public void setDevices(List<Long> devices) {
		this.devices = devices;
	}

	@Override
	public String toString() {
		String deviceInterval = "empty";
		if (devices.size() > 0) {
			deviceInterval = "[" + devices.get(0) + " - " + devices.get(devices.size() - 1) + "]";
		}
		return "Tenant : " + id + ", devices : " + deviceInterval;
	}

}