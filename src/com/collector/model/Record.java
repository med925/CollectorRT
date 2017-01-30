package com.collector.model;

import java.sql.Timestamp;

import com.collector.model.type.Point;

/**
 * @author Amine HANANE
 *
 */
public class Record {

	private Timestamp time;
	private Point coordinate;
	private int speed;
	private int power;
	private boolean ignition;
	private int mems_x, mems_y, mems_z;
	private int sendFlag;
	private int satInView;
	private int signal;
	private boolean validity;

	public Record() {
		super();
	}

	public Record(Timestamp time, Point coordinate, int speed, int power, boolean ignition, int mems_x, int mems_y,
			int mems_z, int sendFlag) {
		super();
		this.time = time;
		this.coordinate = coordinate;
		this.speed = speed;
		this.power = power;
		this.ignition = ignition;
		this.mems_x = mems_x;
		this.mems_y = mems_y;
		this.mems_z = mems_z;
		this.sendFlag = sendFlag;
	}

	public Record(Timestamp time, Point coordinate, int speed, int power, boolean ignition, int mems_x, int mems_y,
			int mems_z, int sendFlag, int satInView, int signal, boolean validity) {
		super();
		this.time = time;
		this.coordinate = coordinate;
		this.speed = speed;
		this.power = power;
		this.ignition = ignition;
		this.mems_x = mems_x;
		this.mems_y = mems_y;
		this.mems_z = mems_z;
		this.sendFlag = sendFlag;
		this.satInView = satInView;
		this.signal = signal;
		this.validity = validity;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
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

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public boolean isIgnition() {
		return ignition;
	}

	public void setIgnition(boolean ignition) {
		this.ignition = ignition;
	}

	public int getMems_x() {
		return mems_x;
	}

	public void setMems_x(int mems_x) {
		this.mems_x = mems_x;
	}

	public int getMems_y() {
		return mems_y;
	}

	public void setMems_y(int mems_y) {
		this.mems_y = mems_y;
	}

	public int getMems_z() {
		return mems_z;
	}

	public void setMems_z(int mems_z) {
		this.mems_z = mems_z;
	}

	public int getSendFlag() {
		return sendFlag;
	}

	public void setSendFlag(int sendFlag) {
		this.sendFlag = sendFlag;
	}

	public int getSatInView() {
		return satInView;
	}

	public void setSatInView(int satInView) {
		this.satInView = satInView;
	}

	public int getSignal() {
		return signal;
	}

	public void setSignal(int signal) {
		this.signal = signal;
	}

	public boolean isValidity() {
		return validity;
	}

	public void setValidity(boolean validity) {
		this.validity = validity;
	}

	@Override
	public String toString() {
		return "--------------------- RECORD DEFINITION ---------------------\n" +

				"record [time=" + time + ",\ncoordinate=" + coordinate + ",\nspeed=" + speed + ",\npower="
				+ ((power > 128) ? "Battery" : "Sector") + power % 128 + ",\nignition=" + (ignition ? "true" : "false")
				+ ",\nmems_x=" + mems_x + ",\nmems_y=" + mems_y + ",\nmems_z=" + mems_z + ",\nsendFlag=" + sendFlag
				+ "]"

				+ "\n--------------------- ------ -------- ---------------------";
	}

}