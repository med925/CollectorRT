package com.collector.utils;

import java.sql.*;

public class DBInteraction {

	static Connection con;
	static Statement st;
	ResultSet res;
	private String url;
	private String username;
	private String password;

	public DBInteraction(String url, String username, String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.url = url;
			this.username = username;
			this.password = password;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void connect() {
		try {
			con = DriverManager.getConnection(url, username, password);
			st = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void disconnect() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ResultSet select(String sql) {
		try {
			res = st.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	public int MAJ(String sql) {
		int nb = 0;
		try {
			nb = st.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return nb;
	}

	public boolean next() {
		try {
			return res.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
