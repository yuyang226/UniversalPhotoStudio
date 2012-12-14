/**
 * 
 */
package com.gmail.charleszq.ups.model;

import java.io.Serializable;

/**
 * @author charles(charleszq@gmail.com)
 *
 */
public class MediaObjectComment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8640171537704175360L;
	
	private String id;
	private String text;
	private Author author;
	private long creationTime; //in ms
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Author getAuthor() {
		return author;
	}
	public void setAuthor(Author author) {
		this.author = author;
	}
	public long getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}
	
	

}
