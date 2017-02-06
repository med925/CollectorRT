package com.collector.model.type;

public enum RealTimeRecordStatus {

	NON_VALID(0), VALID(1), TECHNICAL_ISSUE(2);

	private int value;

	private RealTimeRecordStatus(int state) {
		this.value = state;
	}

	public int getValue() {
		return value;
	}

}
