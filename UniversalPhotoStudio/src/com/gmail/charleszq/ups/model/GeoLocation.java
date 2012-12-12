/**
 * 
 */
package com.gmail.charleszq.ups.model;

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
	private String longitude;
	private String latitude;
	private String accuracy;

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(String accuracy) {
		this.accuracy = accuracy;
	}

}
