package com.collector.application;

import java.io.IOException;
import java.sql.SQLException;

public class Application {

	public static boolean isValidTrame(String line) {
		if (line.length() == 74 || line.length() == 104) {
			return true;
		} else {
			return false;
		}
	}

	public static void main(String[] args) throws SQLException, InterruptedException, IOException {
		(new Thread(new ClientWorker())).start();
	}
}
