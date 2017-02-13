/**
 * 
 */
package com.collector.model.type;

/**
 * @author Amine HANANE
 *
 */
public class Point {

	private double longitude;
	private double latitude;

	public Point(double latitude, double longitude) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@Override
	public String toString() {
		return "Point [longitude=" + longitude + ", latitude=" + latitude + "]";
	}

}