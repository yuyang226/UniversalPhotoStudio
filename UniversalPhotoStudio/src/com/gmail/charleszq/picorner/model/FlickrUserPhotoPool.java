/**
 * 
 */
package com.gmail.charleszq.picorner.model;

import java.io.Serializable;

/**
 * represents the model to save flickr user photo set, group and gallery
 * information into local disk.
 * 
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FlickrUserPhotoPool implements Serializable {

	/**
	 * Constants used for JSON objects.
	 */
	public static final String ID = "id"; //$NON-NLS-1$
	public static final String TITLE = "tt"; //$NON-NLS-1$
	public static final String ICON_URL = "url"; //$NON-NLS-1$
	public static final String TYPE = "t"; //$NON-NLS-1$
	public static final String COUNT = "c"; //$NON-NLS-1$

	public static final int TYPE_PHOTO_SET = 0;
	public static final int TYPE_GROUP = 1;
	public static final int TYPE_GALLERY = 2;

	/**
	 * sid
	 */
	private static final long serialVersionUID = 4236402915967568346L;

	private String id;
	private String title;

	/**
	 * <ul>
	 * <li>for photo set, save photo set id;
	 * <li>for group, just save the url;
	 * <li>for gallery, save the primary photo id;
	 * </ul>
	 */
	private String iconUrl;

	/**
	 * 0: photo set; 1: photo group 2: photo gallery
	 */
	private int type;

	/**
	 * The current photo count.
	 */
	private int photoCount;

	public int getPhotoCount() {
		return photoCount;
	}

	public void setPhotoCount(int photoCount) {
		this.photoCount = photoCount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(type == TYPE_PHOTO_SET ? "photo set" : type == TYPE_GROUP ? "group" : "gallery"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		sb.append("\n"); //$NON-NLS-1$
		sb.append("title: " + title).append("\n"); //$NON-NLS-1$//$NON-NLS-2$
		sb.append("photo count: " + photoCount); //$NON-NLS-1$
		return sb.toString();
	}

}
