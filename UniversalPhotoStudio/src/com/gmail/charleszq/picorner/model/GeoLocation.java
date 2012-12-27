/**
 * 
 */
package com.gmail.charleszq.picorner.model;

import java.io.Serializable;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class GeoLocation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6259542352108432358L;
	private double longitude;
	private double latitude;
	private double accuracy;

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

	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

}
