package com.collector.application;

import com.collector.model.RealTimeRecord;
import com.collector.model.type.StateMachineType;

public class StateMachine {

	private StateMachineType stateMachineType;
	private RealTimeRecord realTimeRecord;

	// ===========> ALGO VARIABLES
	private boolean reportValidRecord;
	private boolean reportNonValidRecord;
	private boolean reportTechnicalIssue;

	public StateMachine(StateMachineType stateMachineType) {
		super();
		this.stateMachineType = stateMachineType;
	}

	public StateMachineType getStateMachineType() {
		return stateMachineType;
	}

	public void setStateMachineType(StateMachineType stateMachineType) {
		this.stateMachineType = stateMachineType;
	}

	public RealTimeRecord getRealTimeRecord() {
		return realTimeRecord;
	}

	public void setRealTimeRecord(RealTimeRecord realTimeRecord) {
		this.realTimeRecord = realTimeRecord;
	}

	public boolean isReportValidRecord() {
		return reportValidRecord;
	}

	public void setReportValidRecord(boolean reportValidRecord) {
		this.reportValidRecord = reportValidRecord;
	}

	public boolean isReportNonValidRecord() {
		return reportNonValidRecord;
	}

	public void setReportNonValidRecord(boolean reportNonValidRecord) {
		this.reportNonValidRecord = reportNonValidRecord;
	}

	public boolean isReportTechnicalIssue() {
		return reportTechnicalIssue;
	}

	public void setReportTechnicalIssue(boolean reportTechnicalIssue) {
		this.reportTechnicalIssue = reportTechnicalIssue;
	}

	public void routine(RealTimeRecord record) {
		switch (this.stateMachineType) {
		case VALID:

			break;
		case NON_VALID:

			break;
		case TECHNICAL_ISSUE:

			break;
		default:
			break;
		}
	}

}
