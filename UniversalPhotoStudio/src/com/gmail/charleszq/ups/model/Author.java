/**
 * 
 */
package com.gmail.charleszq.ups.model;

import java.io.Serializable;

/**
 * @author charles(charleszq@gmail.com)
 *
 */
public class Author implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1250227853225988732L;
	
	private String userId;
	private String userName;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	

}
