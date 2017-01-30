package com.collector.model.type;

/**
 * @author Amine HANANE
 *
 */
public enum StateMachineType {

	INIT(0), STOP(1), DOUBT_START(2), START(3), DOUBT_STOP(4);

	private int value;

	private StateMachineType(int state) {
		this.value = state;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public static StateMachineType fromInteger(int x) {
		switch (x) {
		case 0:
			return INIT;
		case 1:
			return STOP;
		case 2:
			return DOUBT_START;
		case 3:
			return START;
		case 4:
			return DOUBT_STOP;
		}
		return null;
	}

}
