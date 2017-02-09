package com.collector.application;

import java.io.IOException;
import java.sql.SQLException;

public class Application {

	public static void main(String[] args) throws SQLException, InterruptedException, IOException {
		 (new Thread(new ClientWorker())).start();
	}
}
