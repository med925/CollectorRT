package com.collector.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DbProperties {

	private String clientDbName, clientDbUrl, clientDbUsername, clientDbPassword, archiveDbName, archiveDbUrl,
			archiveDbUsername, archiveDbPassword, rawDbName, rawDbUrl, rawDbUsername, rawDbPassword, tenantDbName,
			tenantDbUrl, tenantDbUsername, tenantDbPassword, userDbName, userDbUrl, userDbPassword, userDbUsername;

	public DbProperties() {
		super();
	}

	public String getClientDbName() {
		return clientDbName;
	}

	public String getClientDbUrl() {
		return clientDbUrl;
	}

	public String getClientDbUsername() {
		return clientDbUsername;
	}

	public String getClientDbPassword() {
		return clientDbPassword;
	}

	public String getArchiveDbName() {
		return archiveDbName;
	}

	public String getArchiveDbUrl() {
		return archiveDbUrl;
	}

	public String getArchiveDbUsername() {
		return archiveDbUsername;
	}

	public String getArchiveDbPassword() {
		return archiveDbPassword;
	}

	public String getRawDbName() {
		return rawDbName;
	}

	public String getRawDbUrl() {
		return rawDbUrl;
	}

	public String getRawDbUsername() {
		return rawDbUsername;
	}

	public String getRawDbPassword() {
		return rawDbPassword;
	}

	public String getTenantDbName() {
		return tenantDbName;
	}

	public String getTenantDbUrl() {
		return tenantDbUrl;
	}

	public String getTenantDbUsername() {
		return tenantDbUsername;
	}

	public String getTenantDbPassword() {
		return tenantDbPassword;
	}

	public String getUserDbName() {
		return userDbName;
	}

	public String getUserDbUrl() {
		return userDbUrl;
	}

	public String getUserDbPassword() {
		return userDbPassword;
	}

	public String getUserDbUsername() {
		return userDbUsername;
	}

	public void load() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			// input =
			// getClass().getClassLoader().getResource("config.properties").openStream();
			input = new FileInputStream("config.properties");// .getClassLoader().getResource("config.properties").openStream();
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					this.clientDbName = prop.getProperty("RIM_TRACK_CLIENT_DB_NAME");
					this.clientDbUrl = prop.getProperty("RIM_TRACK_CLIENT_URL");
					this.clientDbUsername = prop.getProperty("RIM_TRACK_CLIENT_USERNAME");
					this.clientDbPassword = prop.getProperty("RIM_TRACK_CLIENT_PASSWORD");
					this.archiveDbName = prop.getProperty("RIM_TRACK_ARCHIVE_DB_NAME");
					this.archiveDbUrl = prop.getProperty("RIM_TRACK_ARCHIVE_URL");
					this.archiveDbUsername = prop.getProperty("RIM_TRACK_ARCHIVE_USERNAME");
					this.archiveDbPassword = prop.getProperty("RIM_TRACK_ARCHIVE_PASSWORD");
					this.rawDbName = prop.getProperty("RIM_TRACK_RAW_DB_NAME");
					this.rawDbUrl = prop.getProperty("RIM_TRACK_RAW_URL");
					this.rawDbUsername = prop.getProperty("RIM_TRACK_RAW_USERNAME");
					this.rawDbPassword = prop.getProperty("RIM_TRACK_RAW_PASSWORD");
					this.tenantDbName = prop.getProperty("RIM_TRACK_TENANT_DB_NAME");
					this.tenantDbUrl = prop.getProperty("RIM_TRACK_TENANT_URL");
					this.tenantDbUsername = prop.getProperty("RIM_TRACK_TENANT_USERNAME");
					this.tenantDbPassword = prop.getProperty("RIM_TRACK_TENANT_PASSWORD");
					this.userDbName = prop.getProperty("RIM_TRACK_USER_DB_NAME");
					this.userDbUrl = prop.getProperty("RIM_TRACK_USER_DB_URL");
					this.userDbPassword = prop.getProperty("RIM_TRACK_USER_DB_PASSWORD");
					this.userDbUsername = prop.getProperty("RIM_TRACK_USER_DB_USERNAME");
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.gc();
	}
}