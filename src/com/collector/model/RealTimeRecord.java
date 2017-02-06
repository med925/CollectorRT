package com.collector.model;

import java.sql.Timestamp;

import com.collector.model.type.Point;
import com.collector.model.type.PointCardinal;
import com.collector.model.type.RealTimeRecordStatus;

public class RealTimeRecord {

	private long deviceId;
	private Timestamp recordTime;

	private Point coordinate;

	private PointCardinal vertical;// N OU S
	private PointCardinal horizontal;// O OU W

	private int speed;
	private int fuel;

	private String temperature;

	private boolean validity;
	private boolean ignition;

	private RealTimeRecordStatus RealTimeRecordStatus;

	public RealTimeRecord() {

	}

	public RealTimeRecord(Timestamp recordTime, Point coordinate, PointCardinal vertical, PointCardinal horizontal,
			int speed, String temperature, int fuel, boolean validity, boolean ignition) {
		super();
		this.recordTime = recordTime;
		this.coordinate = coordinate;
		this.vertical = vertical;
		this.horizontal = horizontal;
		this.speed = speed;
		this.temperature = temperature;
		this.fuel = fuel;
		this.validity = validity;
		this.ignition = ignition;
	}

	public long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}

	public Timestamp getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(Timestamp recordTime) {
		this.recordTime = recordTime;
	}

	public boolean isValidity() {
		return validity;
	}

	public void setValidity(boolean validity) {
		this.validity = validity;
	}

	public Point getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Point coordinate) {
		this.coordinate = coordinate;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public int getFuel() {
		return fuel;
	}

	public void setFuel(int fuel) {
		this.fuel = fuel;
	}

	public boolean isIgnition() {
		return ignition;
	}

	public void setIgnition(boolean ignition) {
		this.ignition = ignition;
	}

	public PointCardinal getVertical() {
		return vertical;
	}

	public void setVertical(PointCardinal vertical) {
		this.vertical = vertical;
	}

	public PointCardinal getHorizontal() {
		return horizontal;
	}

	public void setHorizontal(PointCardinal horizontal) {
		this.horizontal = horizontal;
	}

	public RealTimeRecordStatus getRealTimeRecordStatus() {
		return RealTimeRecordStatus;
	}

	public void setRealTimeRecordStatus(RealTimeRecordStatus realTimeRecordStatus) {
		RealTimeRecordStatus = realTimeRecordStatus;
	}

	@Override
	public String toString() {
		return "RealTimeRecord [deviceId=" + deviceId + ", recordTime=" + recordTime + ", coordinate=" + coordinate
				+ ", vertical=" + vertical + ", horizontal=" + horizontal + ", speed=" + speed + ", fuel=" + fuel
				+ ", temperature=" + temperature + ", validity=" + validity + ", ignition=" + ignition
				+ ", RealTimeRecordStatus=" + RealTimeRecordStatus + "]";
	}

}
