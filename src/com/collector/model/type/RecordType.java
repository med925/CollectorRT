package com.collector.model.type;

public enum RecordType {

	GPRMC(0), AA(1);

	private int value;

	private RecordType(int state) {
		this.value = state;
	}

	public int getValue() {
		return value;
	}
}
