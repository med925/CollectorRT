package com.collector.model.type;

/**
 * @author Amine HANANE
 *
 */
public enum StateMachineType {

	NON_VALID(0), VALID(1), TECHNICAL_ISSUE(2);

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
			return NON_VALID;
		case 1:
			return VALID;
		case 2:
			return TECHNICAL_ISSUE;
		}
		return null;
	}

}
