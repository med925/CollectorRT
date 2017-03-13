package com.collector.process;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.collector.dao.AlertDAO;
import com.collector.model.AlertHash;
import com.collector.model.Notification;
import com.collector.model.Record;
import com.collector.utils.ModelManipulation;

public class AlertProcess {

	public static void alertProcess(Record record) throws IOException, SQLException {

		System.out.println("---------------- start alert process ----------------");

		AlertDAO alertDAO = new AlertDAO();
		ResultSet alerts = alertDAO.getAlerts(record.getDeviceId());
		List<AlertHash> alertHashs = new ArrayList<AlertHash>();
		AlertHash alertHash = new AlertHash();

		boolean allConfigurationsAreValid = false;

		while (alerts.next()) {
			alertHash = new AlertHash(alerts.getLong("alert_id"), alerts.getString("information_symbol"),
					alerts.getString("operator_symbol"), alerts.getString("value"),
					alerts.getString("information_type"));
			alertHashs.add(alertHash);
		}

		Map<Object, List<AlertHash>> alertHashsGrouped = alertHashs.stream()
				.collect(Collectors.groupingBy(alert -> alert.getIdAlert()));

		// set of alerts of device
		for (Entry<Object, List<AlertHash>> e : alertHashsGrouped.entrySet()) {
			System.out.println("** ----------- alert key : " + e.getKey() + "----------- **");
			// set of configuration of a single alert
			outerloop: for (int i = 0; i < e.getValue().size(); i++) {

				Object trameValue = null;
				Object userValue = null;

				try {
					trameValue = ModelManipulation
							.getValueOfPropertyOfInstance(e.getValue().get(i).getInformationSymbol(), record);
					userValue = ModelManipulation.getTypeFromStringValue(e.getValue().get(i).getInformationType(),
							e.getValue().get(i).getInformationValue());
				} catch (Exception e2) {
					e2.printStackTrace();
				}

				switch (e.getValue().get(i).getOperatorSymbol()) {

				case ">": {

					System.out.println("WE ARE IN '>' SECTION !");

					if (trameValue instanceof Integer && userValue instanceof Integer) {
						System.out.println("[" + e.getValue().get(i).getInformationSymbol() + "] RECORD VALUE "
								+ (Integer) trameValue + " > " + (Integer) userValue + " USER VALUE");
						if ((Integer) trameValue > (Integer) userValue) {
							allConfigurationsAreValid = true;
							System.out.println("Configuration[" + i + "] of alert x is valid !");
						} else {
							System.out.println("Configuration[" + i + "] of alert x is not valid !");
							allConfigurationsAreValid = false;
							break outerloop;
						}
					}

					if (trameValue instanceof Timestamp && userValue instanceof Timestamp) {
						System.out.println("[" + e.getValue().get(i).getInformationSymbol() + "] RECORD VALUE "
								+ (Timestamp) trameValue + " > " + (Timestamp) userValue + " USER VALUE");

						if (((Timestamp) trameValue).getTime() > ((Timestamp) userValue).getTime()) {
							allConfigurationsAreValid = true;
							System.out.println("Configuration[" + i + "] of alert x is valid !");
						} else {
							System.out.println("Configuration[" + i + "] of alert x is not valid !");
							allConfigurationsAreValid = false;
							break outerloop;
						}
					}

				}
					break;
				case ">=": {
					System.out.println("WE ARE IN '=>' SECTION !");
					if (trameValue instanceof Integer && userValue instanceof Integer) {
						System.out.println("[" + e.getValue().get(i).getInformationSymbol() + "] RECORD VALUE "
								+ (Integer) trameValue + " >= " + (Integer) userValue + " USER VALUE");
						if ((Integer) trameValue >= (Integer) userValue) {
							allConfigurationsAreValid = true;
							System.out.println("Configuration[" + i + "] of alert x is valid !");
						} else {
							allConfigurationsAreValid = false;
							System.out.println("Configuration[" + i + "] of alert x is not valid !");
							break outerloop;
						}
					}

					if (trameValue instanceof Timestamp && userValue instanceof Timestamp) {
						System.out.println("[" + e.getValue().get(i).getInformationSymbol() + "] RECORD VALUE "
								+ (Timestamp) trameValue + " > " + (Timestamp) userValue + " USER VALUE");

						if (((Timestamp) trameValue).getTime() >= ((Timestamp) userValue).getTime()) {
							allConfigurationsAreValid = true;
							System.out.println("Configuration[" + i + "] of alert x is valid !");
						} else {
							System.out.println("Configuration[" + i + "] of alert x is not valid !");
							allConfigurationsAreValid = false;
							break outerloop;
						}
					}
				}
					break;
				case "<": {
					System.out.println("WE ARE IN '<' SECTION !");
					if (trameValue instanceof Integer && userValue instanceof Integer) {
						System.out.println("[" + e.getValue().get(i).getInformationSymbol() + "] RECORD VALUE "
								+ (Integer) trameValue + " 	< " + (Integer) userValue + " USER VALUE");
						if ((Integer) trameValue < (Integer) userValue) {
							allConfigurationsAreValid = true;
							System.out.println("Configuration[" + i + "] of alert x is valid !");
						} else {
							allConfigurationsAreValid = false;
							System.out.println("Configuration[" + i + "] of alert x is not valid !");
							break outerloop;
						}
					}

					if (trameValue instanceof Timestamp && userValue instanceof Timestamp) {
						System.out.println("[" + e.getValue().get(i).getInformationSymbol() + "] RECORD VALUE "
								+ (Timestamp) trameValue + " < " + (Timestamp) userValue + " USER VALUE");

						if (((Timestamp) trameValue).getTime() < ((Timestamp) userValue).getTime()) {
							allConfigurationsAreValid = true;
							System.out.println("Configuration[" + i + "] of alert x is valid !");
						} else {
							System.out.println("Configuration[" + i + "] of alert x is not valid !");
							allConfigurationsAreValid = false;
							break outerloop;
						}
					}
				}
					break;
				case "<=": {
					System.out.println("WE ARE IN '=<' SECTION !");
					if (trameValue instanceof Integer && userValue instanceof Integer) {
						System.out.println("[" + e.getValue().get(i).getInformationSymbol() + "] RECORD VALUE "
								+ (Integer) trameValue + " <= " + (Integer) userValue + " USER VALUE");
						if ((Integer) trameValue <= (Integer) userValue) {
							allConfigurationsAreValid = true;
							System.out.println("Configuration[" + i + "] of alert x is valid !");
						} else {
							allConfigurationsAreValid = false;
							System.out.println("Configuration[" + i + "] of alert x is not valid !");
							break outerloop;
						}
					}

					if (trameValue instanceof Timestamp && userValue instanceof Timestamp) {
						System.out.println("[" + e.getValue().get(i).getInformationSymbol() + "] RECORD VALUE "
								+ (Timestamp) trameValue + " <= " + (Timestamp) userValue + " USER VALUE");

						if (((Timestamp) trameValue).getTime() <= ((Timestamp) userValue).getTime()) {
							allConfigurationsAreValid = true;
							System.out.println("Configuration[" + i + "] of alert x is valid !");
						} else {
							System.out.println("Configuration[" + i + "] of alert x is not valid !");
							allConfigurationsAreValid = false;
							break outerloop;
						}
					}
				}
					break;
				case "==": {
					System.out.println("WE ARE IN '==' SECTION !");
					if (trameValue instanceof Integer && userValue instanceof Integer) {
						System.out.println("[" + e.getValue().get(i).getInformationSymbol() + "] RECORD VALUE "
								+ (Integer) trameValue + " == " + (Integer) userValue + " USER VALUE");
						if ((Integer) trameValue == (Integer) userValue) {
							allConfigurationsAreValid = true;
							System.out.println("Configuration[" + i + "] of alert x is valid !");
						} else {
							allConfigurationsAreValid = false;
							System.out.println("Configuration[" + i + "] of alert x is not valid !");
							break outerloop;
						}
					}

					if (trameValue instanceof Boolean && userValue instanceof Boolean) {
						System.out.println("[" + e.getValue().get(i).getInformationSymbol() + "] RECORD VALUE "
								+ (Boolean) trameValue + " == " + (Boolean) userValue + " USER VALUE");
						if (((Boolean) trameValue) == ((Boolean) userValue)) {
							allConfigurationsAreValid = true;
							System.out.println("Configuration[" + i + "] of alert x is valid !");
						} else {
							allConfigurationsAreValid = false;
							System.out.println("Configuration[" + i + "] of alert x is not valid !");
							break outerloop;
						}
					}

					if (trameValue instanceof Timestamp && userValue instanceof Timestamp) {
						System.out.println("[" + e.getValue().get(i).getInformationSymbol() + "] RECORD VALUE "
								+ (Timestamp) trameValue + " == " + (Timestamp) userValue + " USER VALUE");

						if (((Timestamp) trameValue).getTime() == ((Timestamp) userValue).getTime()) {
							allConfigurationsAreValid = true;
							System.out.println("Configuration[" + i + "] of alert x is valid !");
						} else {
							System.out.println("Configuration[" + i + "] of alert x is not valid !");
							allConfigurationsAreValid = false;
							break outerloop;
						}
					}
				}
					break;
				case "!=": {
					System.out.println("WE ARE IN '!=' SECTION !");
					if (trameValue instanceof Integer && userValue instanceof Integer) {
						System.out.println("[" + e.getValue().get(i).getInformationSymbol() + "] RECORD VALUE "
								+ (Integer) trameValue + " != " + (Integer) userValue + " USER VALUE");
						if ((Integer) trameValue != (Integer) userValue) {
							allConfigurationsAreValid = true;
							System.out.println("Configuration[" + i + "] of alert x is valid !");
						} else {
							allConfigurationsAreValid = false;
							System.out.println("Configuration[" + i + "] of alert x is not valid !");
							break outerloop;
						}
					}

					if (trameValue instanceof Boolean && userValue instanceof Boolean) {
						System.out.println("[" + e.getValue().get(i).getInformationSymbol() + "] RECORD VALUE "
								+ (Boolean) trameValue + " != " + (Boolean) userValue + " USER VALUE");
						if ((Boolean) trameValue != (Boolean) userValue) {
							allConfigurationsAreValid = true;
							System.out.println("Configuration[" + i + "] of alert x is valid !");
						} else {
							allConfigurationsAreValid = false;
							System.out.println("Configuration[" + i + "] of alert x is not valid !");
							break outerloop;
						}
					}

					if (trameValue instanceof Timestamp && userValue instanceof Timestamp) {
						System.out.println("[" + e.getValue().get(i).getInformationSymbol() + "] RECORD VALUE "
								+ (Timestamp) trameValue + " != " + (Timestamp) userValue + " USER VALUE");

						if (((Timestamp) trameValue).getTime() != ((Timestamp) userValue).getTime()) {
							allConfigurationsAreValid = true;
							System.out.println("Configuration[" + i + "] of alert x is valid !");
						} else {
							System.out.println("Configuration[" + i + "] of alert x is not valid !");
							allConfigurationsAreValid = false;
							break outerloop;
						}
					}
				}
				case "IN": {
					System.out.println("WE ARE IN 'IN' SECTION !");
				}
					break;
				default: {
					System.out.println("undefined !");
				}
				}
			}

			if (allConfigurationsAreValid) {
				alertDAO.addNotification(new Notification("TEXT TO SHOW !", record.getDeviceId()));
				System.out.println("SEND NOTIFCATION !");
			} else {
				System.out.println("DONT SEND NOTIFCATION !");
			}
		}

		System.out.println("---------------- end alert logique ----------------");
	}
}
