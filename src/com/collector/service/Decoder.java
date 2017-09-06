package com.collector.service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.collector.model.Record;
import com.collector.model.type.Point;
import com.collector.model.type.PointCardinal;
import com.collector.model.type.RecordType;
import java.math.BigInteger;

public class Decoder {

	public static double convertNMEAToDeg(double value) {
		int division = (int) value / 100;
		double reste = value - (division * 100);
		return division + reste / 60;
	}

	public static Record decodeGPRMCLine(String line) {

		String[] ops = line.split(",");

		// $GPRMC,164351.00,A,3217.7658,N,00625.4544,W,000.0,251�C,230217,00.0,W,064CB,FE0A0A0066

		if (ops.length == 13) {

			PointCardinal vertical = (ops[4].equals("N")) ? PointCardinal.NORD : PointCardinal.SUD; // S
			PointCardinal horizontal = (ops[6].equals("E")) ? PointCardinal.EST : PointCardinal.OUEST;

			boolean ignition = (ops[12].charAt(ops[12].length() - 1) == 'M') ? true : false;
			boolean validity = (ops[2].equals("A")) ? true : false;

			Point p = new Point(convertNMEAToDeg(Double.parseDouble(ops[3])),
					convertNMEAToDeg(Double.parseDouble(ops[5])));

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
			Record record = new Record(timestamp, p, vertical, horizontal, (int) Double.parseDouble(ops[7]), ops[8],
					(int) Double.parseDouble(ops[10]), validity, ignition);
			record.setRecordType(RecordType.GPRMC);
			System.gc();
			return record;
		} else
			return null;
	}

	public static boolean isValidAATrame(String line) {
		if (line.length() == 74  || line.length() == 104 /**/) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isValidGPRMCTrame(String line) {
		if (line.length() == 75/* || line.length() == 86 */) {
			return true;
		} else {
			return false;
		}
	}

	public static Record decodeAALine(String line) {
		int lineLength = line.length();
		String Hour = String.format("%02d", Integer.parseInt(line.substring(4, 10), 16) / 3600) + // hour
				String.format("%02d", Integer.parseInt(line.substring(4, 10), 16) % 3600 / 60) + // minute
				String.format("%02d", Integer.parseInt(line.substring(4, 10), 16) % 60); // second
		boolean Validity = ((Integer.parseInt(line.substring(42, 44), 16) & 64) == 0) ? false : true; // validity
		double Lat = Integer.parseInt(line.substring(10, 18), 16) / 10000 + // deg
																			// lat
				(double) (Integer.parseInt(line.substring(10, 18), 16) % 10000) / 10000;
		Lat = Lat * (((Integer.parseInt(line.substring(42, 44), 16) & 1) == 0) ? -1 : 1);
		Lat = convertNMEAToDeg(Lat);
		double Lon = Integer.parseInt(line.substring(18, 26), 16) / 10000 + // deg
				(double) (Integer.parseInt(line.substring(18, 26), 16) % 10000) / 10000; // .deg
		Lon = Lon * (((Integer.parseInt(line.substring(42, 44), 16) & 2) == 0) ? -1 : 1);
		Lon = convertNMEAToDeg(Lon);
		String Speed = Integer.parseInt(line.substring(26, 30), 16) / 10 + "" + // vit
				Integer.parseInt(line.substring(26, 30), 16) % 10; // .vit
		int Power = Integer.parseInt(line.substring(32, 34), 16); // pwr
		String actualDate = line.substring(66, 70);
		String Date = String.format("%02d", (Integer.parseInt(actualDate, 16) % 31) + 1) + // day
				String.format("%02d", (Integer.parseInt(actualDate, 16) % (31 * 12) / 31) + 1) + // month
				String.format("%02d", Integer.parseInt(actualDate, 16) / (31 * 12)); // year
		// int Fuel = Integer.parseInt(line.substring(34, 36), 16); // carburant
		boolean Ignition = ((Integer.parseInt(line.substring(42, 44), 16) & 4) == 0) ? false : true; // contact
		String Mems = line.substring(36, 42); // ac�l�rometre XXYYZZ
		int Mems_x = Integer.parseInt(Mems.substring(0, 2), 16);
		Mems_x = (Mems_x > 128) ? Mems_x - 256 : Mems_x;
		int Mems_y = Integer.parseInt(Mems.substring(2, 4), 16);
		Mems_y = (Mems_y > 128) ? Mems_y - 256 : Mems_y;
		int Mems_z = Integer.parseInt(Mems.substring(4, 6), 16);
		Mems_z = (Mems_z > 128) ? Mems_z - 256 : Mems_z;
		// String Temp = line.substring(44, 48); // temp�rature
		// int Odo = Integer.parseInt(line.substring(48, 56), 16); // odo
		// int Heading = Integer.parseInt(line.substring(30, 32), 16); // cap
		int SendFlag = Integer.parseInt(line.substring(56, 58)); // added flags
		// int addedinfo = Integer.parseInt(line.substring(58, 66)); //
		// addedinfo

		SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy HHmmss");
		java.util.Date parsedDate = null;
		try {
			parsedDate = dateFormat.parse(Date + " " + Hour);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
		int signal = 0;
		int SatInView = 0;
                // FMS valus
                int fmsFuel=0;
                int fmsTemp=0;
                int fmsRpm=0;
                int fmsConso=0;
                double fmsOdo=0;
                double fmsTfu=0;
                Record record=null;
		if (lineLength == 74) {
			SatInView = Integer.parseInt(line.substring(70, 72), 16);
			signal = Integer.parseInt(line.substring(72, 74), 16);
                        record = new Record(timestamp, new Point(Lat, Lon), (int) Math.round(Integer.parseInt(Speed) * 0.1852),
				Power, Ignition, Mems_x, Mems_y, Mems_z, SendFlag, SatInView, signal, Validity);
		
		} else if (lineLength == 104) {
                        fmsFuel=Integer.parseInt(line.substring(70, 72), 16);
                        fmsTemp=Integer.parseInt(line.substring(72, 74), 16)-40;
                        fmsOdo=new BigInteger(line.substring(74, 82), 16).longValue();
                        fmsRpm=Integer.parseInt(line.substring(84, 88), 16);
                        fmsConso=Integer.parseInt(line.substring(88, 92), 16);
                        fmsTfu=new BigInteger(line.substring(92, 100), 16).longValue();
                        SatInView = Integer.parseInt(line.substring(100, 102),16);
			signal = Integer.parseInt(line.substring(102, 104),16);
                        record = new Record(timestamp, new Point(Lat, Lon), (int) Math.round(Integer.parseInt(Speed) * 0.1852),
				Power, Ignition, Mems_x, Mems_y, Mems_z, SendFlag, SatInView, signal, Validity,fmsFuel,fmsTemp,fmsOdo,fmsRpm, fmsConso,fmsTfu);
		
		}
		record.setRecordType(RecordType.AA);
		System.gc();
		return record;
		
	}
        
        public static void main(String[] args) {
            System.out.println(decodeAALine("AA1300BF10022AC80A009B78FE00002CA900FDFDFD6380FB000000000400000000177B5C49000A2E1E000000FAFF0000C6490B18").toString());
        }
}

/*package com.collector.service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.collector.model.Record;
import com.collector.model.type.Point;
import com.collector.model.type.PointCardinal;
import com.collector.model.type.RecordType;

public class Decoder {

	public static double convertNMEAToDeg(double value) {
		int division = (int) value / 100;
		double reste = value - (division * 100);
		return division + reste / 60;
	}

	public static Record decodeGPRMCLine(String line) {

		String[] ops = line.split(",");

		// $GPRMC,164351.00,A,3217.7658,N,00625.4544,W,000.0,251°C,230217,00.0,W,064CB,FE0A0A0066

		if (ops.length == 13) {

			PointCardinal vertical = (ops[4].equals("N")) ? PointCardinal.NORD : PointCardinal.SUD; // S
			PointCardinal horizontal = (ops[6].equals("E")) ? PointCardinal.EST : PointCardinal.OUEST;

			boolean ignition = (ops[12].charAt(ops[12].length() - 1) == 'M') ? true : false;
			boolean validity = (ops[2].equals("A")) ? true : false;

			Point p = new Point(convertNMEAToDeg(Double.parseDouble(ops[3])),
					convertNMEAToDeg(Double.parseDouble(ops[5])));

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
			Record record = new Record(timestamp, p, vertical, horizontal, (int) Double.parseDouble(ops[7]), ops[8],
					(int) Double.parseDouble(ops[10]), validity, ignition);
			record.setRecordType(RecordType.GPRMC);
			System.gc();
			return record;
		} else
			return null;
	}

	public static boolean isValidAATrame(String line) {
		if (line.length() == 74) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isValidGPRMCTrame(String line) {
		if (line.length() == 75/* || line.length() == 86 ) {
			return true;
		} else {
			return false;
		}
	}

	public static Record decodeAALine(String line) {
		int lineLength = line.length();
		String Hour = String.format("%02d", Integer.parseInt(line.substring(4, 10), 16) / 3600) + // hour
				String.format("%02d", Integer.parseInt(line.substring(4, 10), 16) % 3600 / 60) + // minute
				String.format("%02d", Integer.parseInt(line.substring(4, 10), 16) % 60); // second
		boolean Validity = ((Integer.parseInt(line.substring(42, 44), 16) & 64) == 0) ? false : true; // validity
		double Lat = Integer.parseInt(line.substring(10, 18), 16) / 10000 + // deg
																			// lat
				(double) (Integer.parseInt(line.substring(10, 18), 16) % 10000) / 10000;
		Lat = Lat * (((Integer.parseInt(line.substring(42, 44), 16) & 1) == 0) ? -1 : 1);
		Lat = convertNMEAToDeg(Lat);
		double Lon = Integer.parseInt(line.substring(18, 26), 16) / 10000 + // deg
				(double) (Integer.parseInt(line.substring(18, 26), 16) % 10000) / 10000; // .deg
		Lon = Lon * (((Integer.parseInt(line.substring(42, 44), 16) & 2) == 0) ? -1 : 1);
		Lon = convertNMEAToDeg(Lon);
		String Speed = Integer.parseInt(line.substring(26, 30), 16) / 10 + "" + // vit
				Integer.parseInt(line.substring(26, 30), 16) % 10; // .vit
		int Power = Integer.parseInt(line.substring(32, 34), 16); // pwr
		String actualDate = line.substring(66, 70);
		String Date = String.format("%02d", (Integer.parseInt(actualDate, 16) % 31) + 1) + // day
				String.format("%02d", (Integer.parseInt(actualDate, 16) % (31 * 12) / 31) + 1) + // month
				String.format("%02d", Integer.parseInt(actualDate, 16) / (31 * 12)); // year
		// int Fuel = Integer.parseInt(line.substring(34, 36), 16); // carburant
		boolean Ignition = ((Integer.parseInt(line.substring(42, 44), 16) & 4) == 0) ? false : true; // contact
		String Mems = line.substring(36, 42); // acélérometre XXYYZZ
		int Mems_x = Integer.parseInt(Mems.substring(0, 2), 16);
		Mems_x = (Mems_x > 128) ? Mems_x - 256 : Mems_x;
		int Mems_y = Integer.parseInt(Mems.substring(2, 4), 16);
		Mems_y = (Mems_y > 128) ? Mems_y - 256 : Mems_y;
		int Mems_z = Integer.parseInt(Mems.substring(4, 6), 16);
		Mems_z = (Mems_z > 128) ? Mems_z - 256 : Mems_z;
		// String Temp = line.substring(44, 48); // température
		// int Odo = Integer.parseInt(line.substring(48, 56), 16); // odo
		// int Heading = Integer.parseInt(line.substring(30, 32), 16); // cap
		int SendFlag = Integer.parseInt(line.substring(56, 58)); // added flags
		// int addedinfo = Integer.parseInt(line.substring(58, 66)); //
		// addedinfo

		SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy HHmmss");
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
		Record record = new Record(timestamp, new Point(Lat, Lon), (int) Math.round(Integer.parseInt(Speed) * 0.1852),
				Power, Ignition, Mems_x, Mems_y, Mems_z, SendFlag, SatInView, signal, Validity);
		record.setRecordType(RecordType.AA);
		System.gc();
		return record;
		
	}
}*/
