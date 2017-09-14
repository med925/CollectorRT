package com.collector.model;

import java.sql.Timestamp;

import com.collector.model.type.Point;
import com.collector.model.type.PointCardinal;
import com.collector.model.type.RealTimeRecordStatus;
import com.collector.model.type.RecordType;

public class Record {

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

	private float rotationAngle;
	
	private RealTimeRecordStatus RealTimeRecordStatus;

	// FMS values
	private int fmsFuel;
	private int fmsTemp;
	private int fmsRpm;
	private int fmsConso;
	private double fmsOdo;
	private double fmsTfu;

	// TO ADD
	private int power;
	private int mems_x, mems_y, mems_z;
	private int sendFlag;
	private int satInView;
	private int signal;

	private RecordType recordType;

	public Record() {
		super();
	}

	// GPRMC RECORD
	public Record(Timestamp recordTime, Point coordinate, PointCardinal vertical, PointCardinal horizontal, int speed,
			String temperature, int fuel, boolean validity, boolean ignition) {
		super();
		this.speed = speed;
		this.fuel = fuel;
		this.temperature = temperature;
		this.ignition = ignition;
		this.validity = validity;
		this.recordTime = recordTime;
		this.vertical = vertical;
		this.horizontal = horizontal;
		this.coordinate = coordinate;
		this.recordTime = recordTime;
	}

	// AA RECORD
	public Record(Timestamp recordTime, Point coordinate, int speed, int power, boolean ignition, int mems_x,
			int mems_y, int mems_z, int sendFlag,float rotationAngle) {
		super();
		this.recordTime = recordTime;
		this.coordinate = coordinate;
		this.speed = speed;
		this.power = power;
		this.ignition = ignition;
		this.mems_x = mems_x;
		this.mems_y = mems_y;
		this.mems_z = mems_z;
		this.sendFlag = sendFlag;
		this.rotationAngle = rotationAngle;
	}

	// AA RECORD
	public Record(Timestamp recordTime, Point coordinate, int speed, int power, boolean ignition, int mems_x,
			int mems_y, int mems_z, int sendFlag, int satInView, int signal, boolean validity,float rotationAngle) {
		super();
		this.recordTime = recordTime;
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
		this.rotationAngle = rotationAngle;
	}

	// AA FMS RECORD
	public Record(Timestamp recordTime, Point coordinate, int speed, int power, boolean ignition, int mems_x,
			int mems_y, int mems_z, int sendFlag, int satInView, int signal, boolean validity, int fmsFUel, int fmsTemp,
			double fmsOdo, int fmsCoso, int fmsRpm, double fmsTfu,float rotationAngle) {
		super();
		this.recordTime = recordTime;
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
		this.fmsFuel = fmsFUel;
		this.fmsTemp = fmsTemp;
		this.fmsOdo = fmsOdo;
		this.fmsRpm = fmsRpm;
		this.fmsTfu = fmsTfu;
		this.fmsConso = fmsConso;
		this.rotationAngle = rotationAngle;
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

	public int getFuel() {
		return fuel;
	}

	public void setFuel(int fuel) {
		this.fuel = fuel;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
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

	public RealTimeRecordStatus getRealTimeRecordStatus() {
		return RealTimeRecordStatus;
	}

	public void setRealTimeRecordStatus(RealTimeRecordStatus realTimeRecordStatus) {
		RealTimeRecordStatus = realTimeRecordStatus;
	}

	public RecordType getRecordType() {
		return recordType;
	}

	public void setRecordType(RecordType recordType) {
		this.recordType = recordType;
	}

	public float getRotationAngle() {
		return rotationAngle;
	}

	public void setRotationAngle(float rotationAngle) {
		this.rotationAngle = rotationAngle;
	}

	public int getFmsFuel() {
		return fmsFuel;
	}

	public void setFmsFuel(int fmsFuel) {
		this.fmsFuel = fmsFuel;
	}

	public int getFmsTemp() {
		return fmsTemp;
	}

	public void setFmsTemp(int fmsTemp) {
		this.fmsTemp = fmsTemp;
	}

	public int getFmsRpm() {
		return fmsRpm;
	}

	public void setFmsRpm(int fmsRpm) {
		this.fmsRpm = fmsRpm;
	}

	public int getFmsConso() {
		return fmsConso;
	}

	public void setFmsConso(int fmsConso) {
		this.fmsConso = fmsConso;
	}

	public double getFmsOdo() {
		return fmsOdo;
	}

	public void setFmsOdo(double fmsOdo) {
		this.fmsOdo = fmsOdo;
	}

	public double getFmsTfu() {
		return fmsTfu;
	}

	public void setFmsTfu(double fmsTfu) {
		this.fmsTfu = fmsTfu;
	}

	@Override
	public String toString() {
		return "Record{" + "deviceId=" + deviceId + ", recordTime=" + recordTime + ", coordinate=" + coordinate
				+ ", vertical=" + vertical + ", horizontal=" + horizontal + ", speed=" + speed + ", fuel=" + fuel
				+ ", temperature=" + temperature + ", validity=" + validity + ", ignition=" + ignition
				+ ", RealTimeRecordStatus=" + RealTimeRecordStatus + ", fmsFuel=" + fmsFuel + ", fmsTemp=" + fmsTemp
				+ ", fmsRpm=" + fmsRpm + ", fmsConso=" + fmsConso + ", fmsOdo=" + fmsOdo + ", fmsTfu=" + fmsTfu
				+ ", power=" + power + ", mems_x=" + mems_x + ", mems_y=" + mems_y + ", mems_z=" + mems_z
				+ ", sendFlag=" + sendFlag + ", satInView=" + satInView + ", signal=" + signal + ", recordType="
				+ recordType + "rotation angle="+rotationAngle+"}";
	}

}
