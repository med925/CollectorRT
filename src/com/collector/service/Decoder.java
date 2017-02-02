package com.collector.service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.collector.model.RealTimeRecord;
import com.collector.model.type.Point;
import com.collector.model.type.PointCardinal;

public class Decoder {

	public static double convertNMEAToDeg(double value) {
		int division = (int) value / 100;
		double reste = value - (division * 100);
		return division + reste / 60;
	}

	public static RealTimeRecord decodeLine(String line) {
		String[] ops = line.split(",");

		if (ops.length == 13) {
			RealTimeRecord realTimeRecord = new RealTimeRecord();

			PointCardinal vertical = (ops[4].equals("N")) ? PointCardinal.NORD : PointCardinal.SUD; // S
			PointCardinal horizontal = (ops[6].equals("E")) ? PointCardinal.EST : PointCardinal.OUEST;

			boolean ignition = (ops[12].charAt(ops[12].length() - 1) == 'M') ? true : false;
			boolean validity = (ops[2].equals("A")) ? true : false;

			Point p = new Point(convertNMEAToDeg(Double.parseDouble(ops[5])),
					convertNMEAToDeg(Double.parseDouble(ops[3])));

			if (vertical == PointCardinal.SUD) {
				p.setLatitude(p.getLatitude() * -1);
			}

			if (horizontal == PointCardinal.OUEST) {
				p.setLongitude(p.getLongitude() * -1);
			}

			SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy HHmmss");
			java.util.Date parsedDate = null;
			try {
				parsedDate = dateFormat.parse(ops[9] + " " + ops[1]);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());

			realTimeRecord = new RealTimeRecord(timestamp, p, vertical, horizontal, (int) Double.parseDouble(ops[7]),
					ops[8], (int) Double.parseDouble(ops[10]), validity, ignition);
			return realTimeRecord;
		} else
			return null;

	}
}
