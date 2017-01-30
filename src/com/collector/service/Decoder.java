package com.collector.service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.collector.model.Record;
import com.collector.model.type.Point;

public class Decoder {

	public static double convertNMEAToDeg(double value) {
		int division = (int) value / 100;
		double reste = value - (division * 100);
		return division + reste / 60;
	}

	public static Record decodeLine(String line) {
		int lineLength = line.length();
		String Hour = String.format("%02d", Integer.parseInt(line.substring(4, 10), 16) / 3600)
				+ String.format("%02d", Integer.parseInt(line.substring(4, 10), 16) % 3600 / 60)
				+ String.format("%02d", Integer.parseInt(line.substring(4, 10), 16) % 60);
		boolean Validity = ((Integer.parseInt(line.substring(42, 44), 16) & 64) == 0) ? false : true;

		double Lat = Integer.parseInt(line.substring(10, 18), 16) / 10000
				+ (double) (Integer.parseInt(line.substring(10, 18), 16) % 10000) / 10000;
		Lat = Lat * (((Integer.parseInt(line.substring(42, 44), 16) & 1) == 0) ? -1 : 1);
		Lat = convertNMEAToDeg(Lat);
		double Lon = Integer.parseInt(line.substring(18, 26), 16) / 10000
				+ (double) (Integer.parseInt(line.substring(18, 26), 16) % 10000) / 10000; // .deg
		Lon = Lon * (((Integer.parseInt(line.substring(42, 44), 16) & 2) == 0) ? -1 : 1);
		Lon = convertNMEAToDeg(Lon);
		String Speed = Integer.parseInt(line.substring(26, 30), 16) / 10 + ""
				+ Integer.parseInt(line.substring(26, 30), 16) % 10;
		int Power = Integer.parseInt(line.substring(32, 34), 16);
		String actualDate = line.substring(66, 70);
		String Date = String.format("%02d", (Integer.parseInt(actualDate, 16) % 31) + 1)
				+ String.format("%02d", (Integer.parseInt(actualDate, 16) % (31 * 12) / 31) + 1)
				+ String.format("%02d", Integer.parseInt(actualDate, 16) / (31 * 12));
		boolean Ignition = ((Integer.parseInt(line.substring(42, 44), 16) & 4) == 0) ? false : true; // contact
		String Mems = line.substring(36, 42);
		int Mems_x = Integer.parseInt(Mems.substring(0, 2), 16);
		Mems_x = (Mems_x > 128) ? Mems_x - 256 : Mems_x;
		int Mems_y = Integer.parseInt(Mems.substring(2, 4), 16);
		Mems_y = (Mems_y > 128) ? Mems_y - 256 : Mems_y;
		int Mems_z = Integer.parseInt(Mems.substring(4, 6), 16);
		Mems_z = (Mems_z > 128) ? Mems_z - 256 : Mems_z;
		int SendFlag = Integer.parseInt(line.substring(56, 58));
		SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy hhmmss");
		java.util.Date parsedDate = null;
		try {
			parsedDate = dateFormat.parse(Date + " " + Hour);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
		int signal = 0;
		int SatInView = 0;
		if (lineLength == 74) {
			SatInView = Integer.parseInt(line.substring(70, 72), 16);
			signal = Integer.parseInt(line.substring(72, 74), 16);
		} else if (lineLength == 104) {
			SatInView = Integer.parseInt(line.substring(100, 102));
			signal = Integer.parseInt(line.substring(102, 104));
		}
		Record record = new Record(timestamp, new Point(Lat, Lon), Integer.parseInt(Speed), Power, Ignition, Mems_x,
				Mems_y, Mems_z, SendFlag, SatInView, signal, Validity);
		return record;
	}
}
