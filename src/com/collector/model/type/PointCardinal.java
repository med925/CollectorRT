package com.collector.model.type;

public enum PointCardinal {

	NORD(0), EST(1), SUD(2), OUEST(3);

	private int value;

	private PointCardinal(int state) {
		this.value = state;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
