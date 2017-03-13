package com.collector.model;

import java.sql.Timestamp;
import java.util.Date;

public class Notification {

	private long idNotification;

	private String label;

	private Timestamp createdAt;

	private boolean isReaded;

	private long idAlert;

	public Notification() {
		this.isReaded = false;
		this.createdAt = new Timestamp(new Date().getTime());
	}

	public Notification(String label, long idAlert) {
		super();
		this.label = label;
		this.createdAt = new Timestamp(new Date().getTime());
		this.isReaded = false;
		this.idAlert = idAlert;
	}

	public long getIdNotification() {
		return idNotification;
	}

	public void setIdNotification(long idNotification) {
		this.idNotification = idNotification;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public boolean isReaded() {
		return isReaded;
	}

	public void setReaded(boolean isReaded) {
		this.isReaded = isReaded;
	}

	public long getIdAlert() {
		return idAlert;
	}

	public void setIdAlert(long idAlert) {
		this.idAlert = idAlert;
	}

	@Override
	public String toString() {
		return "Notification [idNotification=" + idNotification + ", label=" + label + ", createdAt=" + createdAt
				+ ", isReaded=" + isReaded + ", idAlert=" + idAlert + "]";
	}

}
